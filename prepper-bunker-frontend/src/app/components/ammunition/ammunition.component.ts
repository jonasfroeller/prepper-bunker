import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, computed, effect, inject, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatBadgeModule } from '@angular/material/badge';
import { AmmunitionStock, AmmunitionStockCreate } from '../../models/ammunition-stock';
import { AmmunitionType } from '../../models/ammunition-type';
import { AmmunitionService } from '../../services/ammunition.service';
import { StorageLocationService } from '../../services/storage-location.service';
import { WebsocketService, WebSocketMessage } from '../../services/websocket.service';
import { StorageLocation } from '../../models/storage-location';
import { Subject } from 'rxjs';
import { ResourceFormDialogComponent, ResourceFormField } from '../shared/resource-form-dialog/resource-form-dialog.component';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-ammunition',
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
    MatBadgeModule
  ],
  templateUrl: './ammunition.component.html',
  styleUrl: './ammunition.component.scss'
})
export class AmmunitionComponent implements OnInit, OnDestroy {
  private ammunitionService = inject(AmmunitionService);
  private storageLocationService = inject(StorageLocationService);
  private websocketService = inject(WebsocketService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private destroy$ = new Subject<void>();

  ammunitionStocks = signal<AmmunitionStock[]>([]);
  ammunitionTypes = signal<AmmunitionType[]>([]);
  storageLocations = signal<StorageLocation[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  typeTotals = signal<Map<number, number>>(new Map());

  displayedColumns = ['id', 'type', 'quantity', 'storageLocation', 'actions'];

  storageLocationMap = computed(() => {
    const map = new Map<number, string>();
    this.storageLocations().forEach(location => {
      map.set(location.id, location.name);
    });
    return map;
  });

  ammoTypeMap = computed(() => {
    const map = new Map<number, string>();
    this.ammunitionTypes().forEach(type => {
      map.set(type.id, `${type.caliber} ${type.type}`);
    });
    return map;
  });

  constructor() {
    effect(() => {
      const message = this.websocketService.lastMessage();
      if (!message) return;
      
      if (message.resourceType === 'ammunition') {
        this.handleAmmunitionUpdate(message);
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
    
    this.ammunitionService.getAll().subscribe({
      next: (data) => {
        this.ammunitionStocks.set(data);
        this.updateTypeTotals(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load ammunition');
        this.loading.set(false);
        console.error('Error loading ammunition:', err);
      }
    });

    this.storageLocationService.getAll().subscribe({
      next: (data) => this.storageLocations.set(data),
      error: (err) => console.error('Error loading storage locations:', err)
    });

    this.ammunitionService.getAllTypes().subscribe({
      next: (data) => this.ammunitionTypes.set(data),
      error: (err) => console.error('Error loading ammunition types:', err)
    });
  }

  updateTypeTotals(stocks: AmmunitionStock[]): void {
    const totals = new Map<number, number>();
    
    stocks.forEach(stock => {
      const currentTotal = totals.get(stock.ammunitionTypeId) || 0;
      totals.set(stock.ammunitionTypeId, currentTotal + stock.quantity);
    });
    
    this.typeTotals.set(totals);
  }

  setupWebSocket(): void {
    this.websocketService.connect();
  }

  private handleAmmunitionUpdate(message: WebSocketMessage): void {
    this.loadData();
    
    const actionMap: { [key: string]: string } = {
      'CREATE': 'added',
      'UPDATE': 'updated',
      'DELETE': 'removed'
    };
    
    const action = actionMap[message.type] || 'changed';
    this.snackBar.open(`Ammunition stock ${action}`, 'Dismiss', {
      duration: 3000
    });
  }

  openAddDialog(): void {
    const formFields: ResourceFormField[] = [
      { 
        name: 'ammunitionTypeId', 
        label: 'Ammunition Type', 
        type: 'select', 
        required: true, 
        options: this.ammunitionTypes(), 
        valueField: 'id', 
        displayField: 'caliber' 
      },
      { name: 'quantity', label: 'Quantity', type: 'number', required: true },
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
        title: 'Add Ammunition Stock',
        fields: formFields,
        additionalData: {
          ammunitionTypes: this.ammunitionTypes()
        }
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.ammunitionService.create(result as AmmunitionStockCreate).subscribe({
          next: () => {
            this.snackBar.open('Ammunition stock added successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
          },
          error: (err) => {
            this.snackBar.open('Error adding ammunition stock', 'Dismiss', { duration: 3000 });
            console.error('Error adding ammunition stock:', err);
          }
        });
      }
    });
  }

  openEditDialog(stock: AmmunitionStock): void {
    const formFields: ResourceFormField[] = [
      { 
        name: 'ammunitionTypeId', 
        label: 'Ammunition Type', 
        type: 'select', 
        required: true, 
        options: this.ammunitionTypes(), 
        valueField: 'id', 
        displayField: 'caliber' 
      },
      { name: 'quantity', label: 'Quantity', type: 'number', required: true },
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
        title: 'Edit Ammunition Stock',
        fields: formFields,
        resource: stock,
        additionalData: {
          ammunitionTypes: this.ammunitionTypes()
        }
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.ammunitionService.update(stock.id, result).subscribe({
          next: () => {
            this.snackBar.open('Ammunition stock updated successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
          },
          error: (err) => {
            this.snackBar.open('Error updating ammunition stock', 'Dismiss', { duration: 3000 });
            console.error('Error updating ammunition stock:', err);
          }
        });
      }
    });
  }

  deleteAmmunitionStock(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Confirm Deletion',
        message: 'Are you sure you want to delete this ammunition stock?',
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.ammunitionService.delete(id).subscribe({
          next: () => {
            this.ammunitionStocks.update(stocks => {
              const updatedStocks = stocks.filter(stock => stock.id !== id);
              this.updateTypeTotals(updatedStocks);
              return updatedStocks;
            });
            this.snackBar.open('Ammunition stock deleted successfully', 'Dismiss', { duration: 3000 });
          },
          error: (err) => {
            this.snackBar.open('Error deleting ammunition stock', 'Dismiss', { duration: 3000 });
            console.error('Error deleting ammunition stock:', err);
          }
        });
      }
    });
  }
}
