import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, computed, effect, inject, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Food, FoodCreate } from '../../models/food';
import { FoodService } from '../../services/food.service';
import { StorageLocationService } from '../../services/storage-location.service';
import { WebsocketService, WebSocketMessage } from '../../services/websocket.service';
import { StorageLocation } from '../../models/storage-location';
import { Subject } from 'rxjs';
import { ResourceFormDialogComponent, ResourceFormField } from '../shared/resource-form-dialog/resource-form-dialog.component';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog.component';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-food',
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
  templateUrl: './food.component.html',
  styleUrl: './food.component.scss'
})
export class FoodComponent implements OnInit, OnDestroy {
  private foodService = inject(FoodService);
  private storageLocationService = inject(StorageLocationService);
  private websocketService = inject(WebsocketService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private destroy$ = new Subject<void>();

  food = signal<Food[]>([]);
  storageLocations = signal<StorageLocation[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  expiredFood = signal<Food[]>([]);
  showExpired = signal(false);

  displayedColumns = ['id', 'type', 'quantity', 'expirationDate', 'storageLocation', 'actions'];

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
      
      if (message.resourceType === 'food') {
        this.handleFoodUpdate(message);
      }
    });
  }

  ngOnInit(): void {
    this.loadData();
    this.loadExpiredFood();
    this.setupWebSocket();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadData(): void {
    this.loading.set(true);
    this.error.set(null);
    
    this.foodService.getAll().subscribe({
      next: (data) => {
        this.food.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load food');
        this.loading.set(false);
        console.error('Error loading food:', err);
      }
    });

    this.storageLocationService.getAll().subscribe({
      next: (data) => this.storageLocations.set(data),
      error: (err) => console.error('Error loading storage locations:', err)
    });
  }

  loadExpiredFood(): void {
    this.foodService.getExpiredFood().subscribe({
      next: (data) => this.expiredFood.set(data),
      error: (err) => console.error('Error loading expired food:', err)
    });
  }

  setupWebSocket(): void {
    this.websocketService.connect();
  }

  private handleFoodUpdate(message: WebSocketMessage): void {
    this.loadData();
    if (this.showExpired()) {
      this.loadExpiredFood();
    }
    
    const actionMap: { [key: string]: string } = {
      'CREATE': 'added',
      'UPDATE': 'updated',
      'DELETE': 'removed'
    };
    
    const action = actionMap[message.type] || 'changed';
    this.snackBar.open(`Food item ${action}`, 'Dismiss', {
      duration: 3000
    });
  }

  toggleExpiredView(): void {
    this.showExpired.update(val => !val);
    if (this.showExpired()) {
      this.loadExpiredFood();
    }
  }

  openAddDialog(): void {
    const formFields: ResourceFormField[] = [
      { name: 'type', label: 'Type', type: 'text', required: true },
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
        title: 'Add Food Item',
        fields: formFields
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.foodService.create(result as FoodCreate).subscribe({
          next: () => {
            this.snackBar.open('Food item added successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
            if (this.showExpired()) {
              this.loadExpiredFood();
            }
          },
          error: (err) => {
            this.snackBar.open('Error adding food item', 'Dismiss', { duration: 3000 });
            console.error('Error adding food item:', err);
          }
        });
      }
    });
  }

  openEditDialog(food: Food): void {
    const formFields: ResourceFormField[] = [
      { name: 'type', label: 'Type', type: 'text', required: true },
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
        title: 'Edit Food Item',
        fields: formFields,
        resource: food
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.foodService.update(food.id, result).subscribe({
          next: () => {
            this.snackBar.open('Food item updated successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
            if (this.showExpired()) {
              this.loadExpiredFood();
            }
          },
          error: (err) => {
            this.snackBar.open('Error updating food item', 'Dismiss', { duration: 3000 });
            console.error('Error updating food item:', err);
          }
        });
      }
    });
  }

  deleteFood(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Confirm Deletion',
        message: 'Are you sure you want to delete this food item?',
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.foodService.delete(id).subscribe({
          next: () => {
            this.food.update(items => items.filter(item => item.id !== id));
            if (this.showExpired()) {
              this.expiredFood.update(items => items.filter(item => item.id !== id));
            }
            this.snackBar.open('Food item deleted successfully', 'Dismiss', { duration: 3000 });
          },
          error: (err) => {
            this.snackBar.open('Error deleting food item', 'Dismiss', { duration: 3000 });
            console.error('Error deleting food item:', err);
          }
        });
      }
    });
  }
}
