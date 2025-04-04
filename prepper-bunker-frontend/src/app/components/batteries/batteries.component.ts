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
import { Battery } from '../../models/battery';
import { BatteryService } from '../../services/battery.service';
import { StorageLocationService } from '../../services/storage-location.service';
import { WebsocketService, WebSocketMessage } from '../../services/websocket.service';
import { Subject, takeUntil } from 'rxjs';
import { ResourceFormDialogComponent, ResourceFormField } from '../shared/resource-form-dialog/resource-form-dialog.component';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog.component';
import { StorageLocation } from '../../models/storage-location';

@Component({
  selector: 'app-batteries',
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
  templateUrl: './batteries.component.html',
  styleUrl: './batteries.component.scss'
})
export class BatteriesComponent implements OnInit, OnDestroy {
  private batteryService = inject(BatteryService);
  private storageLocationService = inject(StorageLocationService);
  private websocketService = inject(WebsocketService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private destroy$ = new Subject<void>();

  batteries = signal<Battery[]>([]);
  storageLocations = signal<StorageLocation[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  displayedColumns = ['id', 'type', 'quantity', 'capacity', 'storageLocation', 'actions'];

  constructor() {
    effect(() => {
      const message = this.websocketService.lastMessage();
      if (!message) return;
      
      if (message.resourceType === 'battery') {
        this.handleBatteryUpdate(message);
      }
    });
  }

  ngOnInit(): void {
    this.loadData();
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
    
    this.batteryService.getAll().subscribe({
      next: (data) => {
        this.batteries.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load batteries');
        this.loading.set(false);
        console.error('Error loading batteries:', err);
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

  private handleBatteryUpdate(message: WebSocketMessage): void {
    this.loadData();
    
    const actionMap: { [key: string]: string } = {
      'CREATE': 'added',
      'UPDATE': 'updated',
      'DELETE': 'removed'
    };
    
    const action = actionMap[message.type] || 'changed';
    this.snackBar.open(`Battery ${action}`, 'Dismiss', {
      duration: 3000
    });
  }

  getStorageLocationName(locationId: number): string {
    const location = this.storageLocations().find(loc => loc.id === locationId);
    return location ? location.name : 'Unknown';
  }

  openAddDialog(): void {
    const formFields: ResourceFormField[] = [
      { name: 'type', label: 'Type', type: 'text', required: true },
      { name: 'quantity', label: 'Quantity', type: 'number', required: true },
      { name: 'capacity', label: 'Capacity (mAh)', type: 'number', required: true },
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
        title: 'Add Battery',
        fields: formFields
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.batteryService.create(result).subscribe({
          next: () => {
            this.snackBar.open('Battery added successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
          },
          error: (err) => {
            this.snackBar.open('Error adding battery', 'Dismiss', { duration: 3000 });
            console.error('Error adding battery:', err);
          }
        });
      }
    });
  }

  openEditDialog(battery: Battery): void {
    const formFields: ResourceFormField[] = [
      { name: 'type', label: 'Type', type: 'text', required: true },
      { name: 'quantity', label: 'Quantity', type: 'number', required: true },
      { name: 'capacity', label: 'Capacity (mAh)', type: 'number', required: true },
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
        title: 'Edit Battery',
        fields: formFields,
        resource: battery
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.batteryService.update(battery.id, result).subscribe({
          next: () => {
            this.snackBar.open('Battery updated successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
          },
          error: (err) => {
            this.snackBar.open('Error updating battery', 'Dismiss', { duration: 3000 });
            console.error('Error updating battery:', err);
          }
        });
      }
    });
  }

  deleteBattery(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Confirm Deletion',
        message: 'Are you sure you want to delete this battery?',
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.batteryService.delete(id).subscribe({
          next: () => {
            this.batteries.update(batteries => batteries.filter(battery => battery.id !== id));
            this.snackBar.open('Battery deleted successfully', 'Dismiss', { duration: 3000 });
          },
          error: (err) => {
            this.snackBar.open('Error deleting battery', 'Dismiss', { duration: 3000 });
            console.error('Error deleting battery:', err);
          }
        });
      }
    });
  }
}
