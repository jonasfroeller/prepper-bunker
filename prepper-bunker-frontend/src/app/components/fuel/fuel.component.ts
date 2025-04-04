import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, effect, inject, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Fuel } from '../../models/fuel';
import { FuelService } from '../../services/fuel.service';
import { StorageLocationService } from '../../services/storage-location.service';
import { WebsocketService, WebSocketMessage } from '../../services/websocket.service';
import { Subject, takeUntil } from 'rxjs';
import { ResourceFormDialogComponent, ResourceFormField } from '../shared/resource-form-dialog/resource-form-dialog.component';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog.component';
import { StorageLocation } from '../../models/storage-location';
import { FuelType } from '../../models/fuel-type';

@Component({
  selector: 'app-fuel',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatChipsModule,
    MatTooltipModule
  ],
  templateUrl: './fuel.component.html',
  styleUrl: './fuel.component.scss'
})
export class FuelComponent implements OnInit, OnDestroy {
  private fuelService = inject(FuelService);
  private storageLocationService = inject(StorageLocationService);
  private websocketService = inject(WebsocketService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private destroy$ = new Subject<void>();

  fuels = signal<Fuel[]>([]);
  fuelTypes = signal<FuelType[]>([]);
  storageLocations = signal<StorageLocation[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  displayedColumns = ['id', 'fuelType', 'quantity', 'storageLocation', 'actions'];

  constructor() {
    effect(() => {
      const message = this.websocketService.lastMessage();
      if (!message) return;
      
      if (message.resourceType === 'fuel') {
        this.handleFuelUpdate(message);
      }
    });
  }

  ngOnInit(): void {
    this.loadData();
    this.loadFuelTypes();
    this.loadStorageLocations();
    this.setupWebSocket();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadData(): void {
    this.loading.set(true);
    this.error.set(null);
    
    this.fuelService.getAll().subscribe({
      next: (data) => {
        this.fuels.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load fuel items');
        this.loading.set(false);
        console.error('Error loading fuel items:', err);
      }
    });
  }

  loadFuelTypes(): void {
    this.fuelService.getAllTypes()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (types) => {
          this.fuelTypes.set(types);
          console.log('Loaded fuel types:', types);
        },
        error: (err) => {
          console.error('Error loading fuel types:', err);
          this.fuelTypes.set([
            {id: 1, name: 'Gasoline'},
            {id: 2, name: 'Diesel'},
            {id: 3, name: 'Propane'},
            {id: 4, name: 'Kerosene'}
          ]);
        }
      });
  }

  loadStorageLocations(): void {
    this.storageLocationService.getAll()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (locations) => {
          this.storageLocations.set(locations);
        },
        error: (err) => {
          console.error('Error loading storage locations:', err);
        }
      });
  }

  setupWebSocket(): void {
    this.websocketService.connect();
  }

  private handleFuelUpdate(message: WebSocketMessage): void {
    this.loadData();
    
    const actionMap: { [key: string]: string } = {
      'CREATE': 'added',
      'UPDATE': 'updated',
      'DELETE': 'removed'
    };
    
    const action = actionMap[message.type] || 'changed';
    this.snackBar.open(`Fuel ${action}`, 'Dismiss', {
      duration: 3000
    });
  }

  getStorageLocationName(locationId: number): string {
    const location = this.storageLocations().find(loc => loc.id === locationId);
    return location ? location.name : 'Unknown';
  }

  getFuelTypeName(typeId: number): string {
    const type = this.fuelTypes().find(type => type.id === typeId);
    return type ? type.name : 'Unknown';
  }

  openAddDialog(): void {
    const formFields: ResourceFormField[] = [
      { 
        name: 'fuelTypeId', 
        label: 'Fuel Type', 
        type: 'select', 
        options: this.fuelTypes(),
        valueField: 'id',
        displayField: 'name',
        required: true
      },
      { name: 'quantity', label: 'Quantity', type: 'number', required: true },
      { 
        name: 'storageLocationId', 
        label: 'Storage Location', 
        type: 'select', 
        options: this.storageLocations(),
        valueField: 'id',
        displayField: 'name',
        required: true
      }
    ];

    const dialogRef = this.dialog.open(ResourceFormDialogComponent, {
      width: '500px',
      data: {
        title: 'Add Fuel',
        fields: formFields
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.fuelService.create(result).subscribe({
          next: () => {
            this.snackBar.open('Fuel added successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
          },
          error: (err) => {
            this.snackBar.open('Error adding fuel', 'Dismiss', { duration: 3000 });
            console.error('Error adding fuel:', err);
          }
        });
      }
    });
  }

  openEditDialog(fuel: Fuel): void {
    const formFields: ResourceFormField[] = [
      { 
        name: 'fuelTypeId', 
        label: 'Fuel Type', 
        type: 'select', 
        options: this.fuelTypes(),
        valueField: 'id',
        displayField: 'name',
        required: true
      },
      { name: 'quantity', label: 'Quantity', type: 'number', required: true },
      { 
        name: 'storageLocationId', 
        label: 'Storage Location', 
        type: 'select', 
        options: this.storageLocations(),
        valueField: 'id',
        displayField: 'name',
        required: true
      }
    ];

    const dialogRef = this.dialog.open(ResourceFormDialogComponent, {
      width: '500px',
      data: {
        title: 'Edit Fuel',
        fields: formFields,
        resource: fuel
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.fuelService.update(fuel.id, result).subscribe({
          next: () => {
            this.snackBar.open('Fuel updated successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
          },
          error: (err) => {
            this.snackBar.open('Error updating fuel', 'Dismiss', { duration: 3000 });
            console.error('Error updating fuel:', err);
          }
        });
      }
    });
  }

  deleteFuel(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Confirm Deletion',
        message: 'Are you sure you want to delete this fuel?',
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.fuelService.delete(id).subscribe({
          next: () => {
            this.fuels.update(fuels => fuels.filter(f => f.id !== id));
            this.snackBar.open('Fuel deleted successfully', 'Dismiss', { duration: 3000 });
          },
          error: (err) => {
            this.snackBar.open('Error deleting fuel', 'Dismiss', { duration: 3000 });
            console.error('Error deleting fuel:', err);
          }
        });
      }
    });
  }
}
