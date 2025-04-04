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
import { Generator } from '../../models/generator';
import { GeneratorService } from '../../services/generator.service';
import { StorageLocationService } from '../../services/storage-location.service';
import { WebsocketService, WebSocketMessage } from '../../services/websocket.service';
import { Subject, takeUntil } from 'rxjs';
import { ResourceFormDialogComponent, ResourceFormField } from '../shared/resource-form-dialog/resource-form-dialog.component';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog.component';
import { StorageLocation } from '../../models/storage-location';
import { FuelService } from '../../services/fuel.service';
import { FuelType } from '../../models/fuel-type';

@Component({
  selector: 'app-generators',
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
  templateUrl: './generators.component.html',
  styleUrl: './generators.component.scss'
})
export class GeneratorsComponent implements OnInit, OnDestroy {
  private generatorService = inject(GeneratorService);
  private storageLocationService = inject(StorageLocationService);
  private fuelService = inject(FuelService);
  private websocketService = inject(WebsocketService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private destroy$ = new Subject<void>();

  generators = signal<Generator[]>([]);
  storageLocations = signal<StorageLocation[]>([]);
  fuelTypes = signal<FuelType[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  displayedColumns = ['id', 'type', 'power', 'fuelType', 'status', 'storageLocation', 'actions'];

  constructor() {
    effect(() => {
      const message = this.websocketService.lastMessage();
      if (!message) return;
      
      if (message.resourceType === 'generator') {
        this.handleGeneratorUpdate(message);
      }
    });
  }

  ngOnInit(): void {
    this.loadData();
    this.loadStorageLocations();
    this.loadFuelTypes();
    this.setupWebSocket();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadData(): void {
    this.loading.set(true);
    this.error.set(null);
    
    this.generatorService.getAll().subscribe({
      next: (data) => {
        this.generators.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load generators');
        this.loading.set(false);
        console.error('Error loading generators:', err);
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

  setupWebSocket(): void {
    this.websocketService.connect();
  }

  private handleGeneratorUpdate(message: WebSocketMessage): void {
    this.loadData();
    
    const actionMap: { [key: string]: string } = {
      'CREATE': 'added',
      'UPDATE': 'updated',
      'DELETE': 'removed'
    };
    
    const action = actionMap[message.type] || 'changed';
    this.snackBar.open(`Generator ${action}`, 'Dismiss', {
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

  formatPowerOutput(power: number): string {
    return `${power} kW`;
  }

  openAddDialog(): void {
    const formFields: ResourceFormField[] = [
      { name: 'type', label: 'Type', type: 'text', required: true },
      { name: 'power', label: 'Power Output (kW)', type: 'number', required: true },
      { 
        name: 'fuelTypeId', 
        label: 'Fuel Type', 
        type: 'select', 
        options: this.fuelTypes(),
        valueField: 'id',
        displayField: 'name',
        required: true
      },
      { name: 'status', label: 'Status', type: 'text', required: true },
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
        title: 'Add Generator',
        fields: formFields
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.generatorService.create(result).subscribe({
          next: () => {
            this.snackBar.open('Generator added successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
          },
          error: (err) => {
            this.snackBar.open('Error adding generator', 'Dismiss', { duration: 3000 });
            console.error('Error adding generator:', err);
          }
        });
      }
    });
  }

  openEditDialog(generator: Generator): void {
    const formFields: ResourceFormField[] = [
      { name: 'type', label: 'Type', type: 'text', required: true },
      { name: 'power', label: 'Power Output (kW)', type: 'number', required: true },
      { 
        name: 'fuelTypeId', 
        label: 'Fuel Type', 
        type: 'select', 
        options: this.fuelTypes(),
        valueField: 'id',
        displayField: 'name',
        required: true
      },
      { name: 'status', label: 'Status', type: 'text', required: true },
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
        title: 'Edit Generator',
        fields: formFields,
        resource: generator
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.generatorService.update(generator.id, result).subscribe({
          next: () => {
            this.snackBar.open('Generator updated successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
          },
          error: (err) => {
            this.snackBar.open('Error updating generator', 'Dismiss', { duration: 3000 });
            console.error('Error updating generator:', err);
          }
        });
      }
    });
  }

  deleteGenerator(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Confirm Deletion',
        message: 'Are you sure you want to delete this generator?',
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.generatorService.delete(id).subscribe({
          next: () => {
            this.generators.update(generators => generators.filter(g => g.id !== id));
            this.snackBar.open('Generator deleted successfully', 'Dismiss', { duration: 3000 });
          },
          error: (err) => {
            this.snackBar.open('Error deleting generator', 'Dismiss', { duration: 3000 });
            console.error('Error deleting generator:', err);
          }
        });
      }
    });
  }
}
