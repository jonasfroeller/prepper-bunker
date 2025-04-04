import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, effect, inject, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { StorageLocation } from '../../models/storage-location';
import { StorageLocationService } from '../../services/storage-location.service';
import { WebsocketService, WebSocketMessage } from '../../services/websocket.service';
import { Subject } from 'rxjs';
import { ResourceFormDialogComponent, ResourceFormField } from '../shared/resource-form-dialog/resource-form-dialog.component';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-storage-locations',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './storage-locations.component.html',
  styleUrl: './storage-locations.component.scss'
})
export class StorageLocationsComponent implements OnInit, OnDestroy {
  private storageLocationService = inject(StorageLocationService);
  private websocketService = inject(WebsocketService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private destroy$ = new Subject<void>();

  storageLocations = signal<StorageLocation[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  displayedColumns = ['id', 'name', 'description', 'actions'];

  constructor() {
    effect(() => {
      const message = this.websocketService.lastMessage();
      if (!message) return;
      
      if (message.resourceType === 'storage-location') {
        this.handleStorageLocationUpdate(message);
      }
    });
  }

  ngOnInit(): void {
    this.loadData();
    this.setupWebSocket();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadData(): void {
    this.loading.set(true);
    this.error.set(null);
    
    this.storageLocationService.getAll().subscribe({
      next: (data) => {
        this.storageLocations.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load storage locations');
        this.loading.set(false);
        console.error('Error loading storage locations:', err);
      }
    });
  }

  setupWebSocket(): void {
    this.websocketService.connect();
  }

  private handleStorageLocationUpdate(message: WebSocketMessage): void {
    this.loadData();
    
    const actionMap: { [key: string]: string } = {
      'CREATE': 'added',
      'UPDATE': 'updated',
      'DELETE': 'removed'
    };
    
    const action = actionMap[message.type] || 'changed';
    this.snackBar.open(`Storage location ${action}`, 'Dismiss', {
      duration: 3000
    });
  }

  openAddDialog(): void {
    const formFields: ResourceFormField[] = [
      { name: 'name', label: 'Name', type: 'text', required: true },
      { name: 'description', label: 'Description', type: 'text' }
    ];

    const dialogRef = this.dialog.open(ResourceFormDialogComponent, {
      width: '400px',
      data: {
        title: 'Add Storage Location',
        fields: formFields
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.storageLocationService.create(result).subscribe({
          next: () => {
            this.snackBar.open('Storage location added successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
          },
          error: (err) => {
            this.snackBar.open('Error adding storage location', 'Dismiss', { duration: 3000 });
            console.error('Error adding storage location:', err);
          }
        });
      }
    });
  }

  openEditDialog(location: StorageLocation): void {
    const formFields: ResourceFormField[] = [
      { name: 'name', label: 'Name', type: 'text', required: true },
      { name: 'description', label: 'Description', type: 'text' }
    ];

    const dialogRef = this.dialog.open(ResourceFormDialogComponent, {
      width: '400px',
      data: {
        title: 'Edit Storage Location',
        fields: formFields,
        resource: location
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.storageLocationService.update(location.id, result).subscribe({
          next: () => {
            this.snackBar.open('Storage location updated successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
          },
          error: (err) => {
            this.snackBar.open('Error updating storage location', 'Dismiss', { duration: 3000 });
            console.error('Error updating storage location:', err);
          }
        });
      }
    });
  }

  deleteStorageLocation(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Confirm Deletion',
        message: 'Are you sure you want to delete this storage location? This may affect items stored in this location.',
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.storageLocationService.delete(id).subscribe({
          next: () => {
            this.storageLocations.update(locations => locations.filter(loc => loc.id !== id));
            this.snackBar.open('Storage location deleted successfully', 'Dismiss', { duration: 3000 });
          },
          error: (err) => {
            this.snackBar.open('Error deleting storage location', 'Dismiss', { duration: 3000 });
            console.error('Error deleting storage location:', err);
          }
        });
      }
    });
  }
}
