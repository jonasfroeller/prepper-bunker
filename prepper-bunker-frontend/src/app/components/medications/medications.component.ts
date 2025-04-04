import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, computed, effect, inject, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Medication, MedicationCreate } from '../../models/medication';
import { MedicationService } from '../../services/medication.service';
import { StorageLocationService } from '../../services/storage-location.service';
import { WebsocketService, WebSocketMessage } from '../../services/websocket.service';
import { StorageLocation } from '../../models/storage-location';
import { Subject } from 'rxjs';
import { ResourceFormDialogComponent, ResourceFormField } from '../shared/resource-form-dialog/resource-form-dialog.component';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog.component';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-medications',
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
    DatePipe
  ],
  templateUrl: './medications.component.html',
  styleUrl: './medications.component.scss'
})
export class MedicationsComponent implements OnInit, OnDestroy {
  private medicationService = inject(MedicationService);
  private storageLocationService = inject(StorageLocationService);
  private websocketService = inject(WebsocketService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private destroy$ = new Subject<void>();

  medications = signal<Medication[]>([]);
  storageLocations = signal<StorageLocation[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  expiredMedications = signal<Medication[]>([]);
  showExpired = signal(false);

  displayedColumns = ['id', 'name', 'purpose', 'quantity', 'expirationDate', 'storageLocation', 'actions'];

  storageLocationMap = computed(() => {
    const map = new Map<number, string>();
    this.storageLocations().forEach(location => {
      map.set(location.id, location.name);
    });
    return map;
  });

  constructor() {
    effect(() => {
      const message = this.websocketService.lastMessage();
      if (!message) return;
      
      if (message.resourceType === 'medication') {
        this.handleMedicationUpdate(message);
      }
    });
  }

  ngOnInit(): void {
    this.loadData();
    this.loadExpiredMedications();
    this.setupWebSocket();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadData(): void {
    this.loading.set(true);
    this.error.set(null);
    
    this.medicationService.getAll().subscribe({
      next: (data) => {
        this.medications.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load medications');
        this.loading.set(false);
        console.error('Error loading medications:', err);
      }
    });

    this.storageLocationService.getAll().subscribe({
      next: (data) => this.storageLocations.set(data),
      error: (err) => console.error('Error loading storage locations:', err)
    });
  }

  loadExpiredMedications(): void {
    this.medicationService.getExpiredMedications().subscribe({
      next: (data) => this.expiredMedications.set(data),
      error: (err) => console.error('Error loading expired medications:', err)
    });
  }

  setupWebSocket(): void {
    this.websocketService.connect();
  }

  private handleMedicationUpdate(message: WebSocketMessage): void {
    this.loadData();
    if (this.showExpired()) {
      this.loadExpiredMedications();
    }
    
    const actionMap: { [key: string]: string } = {
      'CREATE': 'added',
      'UPDATE': 'updated',
      'DELETE': 'removed'
    };
    
    const action = actionMap[message.type] || 'changed';
    this.snackBar.open(`Medication ${action}`, 'Dismiss', {
      duration: 3000
    });
  }

  toggleExpiredView(): void {
    this.showExpired.update(val => !val);
    if (this.showExpired()) {
      this.loadExpiredMedications();
    }
  }

  openAddDialog(): void {
    const formFields: ResourceFormField[] = [
      { name: 'name', label: 'Name', type: 'text', required: true },
      { name: 'purpose', label: 'Purpose', type: 'text', required: true },
      { name: 'quantity', label: 'Quantity', type: 'number', required: true },
      { name: 'expirationDate', label: 'Expiration Date', type: 'date', required: true },
      { 
        name: 'storageLocationId', 
        label: 'Storage Location', 
        type: 'select', 
        required: true, 
        options: this.storageLocations(), 
        valueField: 'id', 
        displayField: 'name' 
      }
    ];

    const dialogRef = this.dialog.open(ResourceFormDialogComponent, {
      width: '400px',
      data: {
        title: 'Add Medication',
        fields: formFields
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.medicationService.create(result as MedicationCreate).subscribe({
          next: () => {
            this.snackBar.open('Medication added successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
            if (this.showExpired()) {
              this.loadExpiredMedications();
            }
          },
          error: (err) => {
            this.snackBar.open('Error adding medication', 'Dismiss', { duration: 3000 });
            console.error('Error adding medication:', err);
          }
        });
      }
    });
  }

  openEditDialog(medication: Medication): void {
    const formFields: ResourceFormField[] = [
      { name: 'name', label: 'Name', type: 'text', required: true },
      { name: 'purpose', label: 'Purpose', type: 'text', required: true },
      { name: 'quantity', label: 'Quantity', type: 'number', required: true },
      { name: 'expirationDate', label: 'Expiration Date', type: 'date', required: true },
      { 
        name: 'storageLocationId', 
        label: 'Storage Location', 
        type: 'select', 
        required: true, 
        options: this.storageLocations(), 
        valueField: 'id', 
        displayField: 'name' 
      }
    ];

    const dialogRef = this.dialog.open(ResourceFormDialogComponent, {
      width: '400px',
      data: {
        title: 'Edit Medication',
        fields: formFields,
        resource: medication
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.medicationService.update(medication.id, result).subscribe({
          next: () => {
            this.snackBar.open('Medication updated successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
            if (this.showExpired()) {
              this.loadExpiredMedications();
            }
          },
          error: (err) => {
            this.snackBar.open('Error updating medication', 'Dismiss', { duration: 3000 });
            console.error('Error updating medication:', err);
          }
        });
      }
    });
  }

  deleteMedication(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Confirm Deletion',
        message: 'Are you sure you want to delete this medication?',
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.medicationService.delete(id).subscribe({
          next: () => {
            this.medications.update(items => items.filter(item => item.id !== id));
            if (this.showExpired()) {
              this.expiredMedications.update(items => items.filter(item => item.id !== id));
            }
            this.snackBar.open('Medication deleted successfully', 'Dismiss', { duration: 3000 });
          },
          error: (err) => {
            this.snackBar.open('Error deleting medication', 'Dismiss', { duration: 3000 });
            console.error('Error deleting medication:', err);
          }
        });
      }
    });
  }
}
