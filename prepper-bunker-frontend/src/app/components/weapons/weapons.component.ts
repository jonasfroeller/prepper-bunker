import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit, computed, effect, inject, signal } from '@angular/core';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Weapon, WeaponCreate } from '../../models/weapon';
import { WeaponService } from '../../services/weapon.service';
import { StorageLocationService } from '../../services/storage-location.service';
import { AmmunitionService } from '../../services/ammunition.service';
import { WebsocketService, WebSocketMessage } from '../../services/websocket.service';
import { StorageLocation } from '../../models/storage-location';
import { AmmunitionType } from '../../models/ammunition-type';
import { Subject, takeUntil } from 'rxjs';
import { ResourceFormDialogComponent, ResourceFormField } from '../shared/resource-form-dialog/resource-form-dialog.component';
import { ConfirmDialogComponent } from '../shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-weapons',
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
  templateUrl: './weapons.component.html',
  styleUrl: './weapons.component.scss'
})
export class WeaponsComponent implements OnInit, OnDestroy {
  private weaponService = inject(WeaponService);
  private storageLocationService = inject(StorageLocationService);
  private ammoService = inject(AmmunitionService);
  private websocketService = inject(WebsocketService);
  private snackBar = inject(MatSnackBar);
  private dialog = inject(MatDialog);
  private destroy$ = new Subject<void>();

  weapons = signal<Weapon[]>([]);
  storageLocations = signal<StorageLocation[]>([]);
  ammunitionTypes = signal<AmmunitionType[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  displayedColumns = ['id', 'type', 'model', 'quantity', 'ammunitionType', 'storageLocation', 'actions'];

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
      
      if (message.resourceType === 'weapon') {
        this.handleWeaponUpdate(message);
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
    
    this.weaponService.getAll().subscribe({
      next: (data) => {
        this.weapons.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load weapons');
        this.loading.set(false);
        console.error('Error loading weapons:', err);
      }
    });

    this.storageLocationService.getAll().subscribe({
      next: (data) => this.storageLocations.set(data),
      error: (err) => console.error('Error loading storage locations:', err)
    });

    this.ammoService.getAllTypes().subscribe({
      next: (data) => this.ammunitionTypes.set(data),
      error: (err) => console.error('Error loading ammunition types:', err)
    });
  }

  setupWebSocket(): void {
    this.websocketService.connect();
  }

  private handleWeaponUpdate(message: WebSocketMessage): void {
    this.loadData();
    
    const actionMap: { [key: string]: string } = {
      'CREATE': 'added',
      'UPDATE': 'updated',
      'DELETE': 'removed'
    };
    
    const action = actionMap[message.type] || 'changed';
    this.snackBar.open(`Weapon ${action}`, 'Dismiss', {
      duration: 3000
    });
  }

  openAddDialog(): void {
    const formFields: ResourceFormField[] = [
      { name: 'type', label: 'Type', type: 'text', required: true },
      { name: 'model', label: 'Model', type: 'text', required: true },
      { name: 'quantity', label: 'Quantity', type: 'number', required: true },
      { 
        name: 'ammunitionTypeId', 
        label: 'Ammunition Type', 
        type: 'select', 
        required: true, 
        options: this.ammunitionTypes(), 
        valueField: 'id', 
        displayField: 'caliber' 
      },
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
        title: 'Add Weapon',
        fields: formFields
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.weaponService.create(result as WeaponCreate).subscribe({
          next: () => {
            this.snackBar.open('Weapon added successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
          },
          error: (err) => {
            this.snackBar.open('Error adding weapon', 'Dismiss', { duration: 3000 });
            console.error('Error adding weapon:', err);
          }
        });
      }
    });
  }

  openEditDialog(weapon: Weapon): void {
    const formFields: ResourceFormField[] = [
      { name: 'type', label: 'Type', type: 'text', required: true },
      { name: 'model', label: 'Model', type: 'text', required: true },
      { name: 'quantity', label: 'Quantity', type: 'number', required: true },
      { 
        name: 'ammunitionTypeId', 
        label: 'Ammunition Type', 
        type: 'select', 
        required: true, 
        options: this.ammunitionTypes(), 
        valueField: 'id', 
        displayField: 'caliber' 
      },
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
        title: 'Edit Weapon',
        fields: formFields,
        resource: weapon
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.weaponService.update(weapon.id, result).subscribe({
          next: () => {
            this.snackBar.open('Weapon updated successfully', 'Dismiss', { duration: 3000 });
            this.loadData();
          },
          error: (err) => {
            this.snackBar.open('Error updating weapon', 'Dismiss', { duration: 3000 });
            console.error('Error updating weapon:', err);
          }
        });
      }
    });
  }

  deleteWeapon(id: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: {
        title: 'Confirm Deletion',
        message: 'Are you sure you want to delete this weapon?',
        confirmText: 'Delete',
        cancelText: 'Cancel'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.weaponService.delete(id).subscribe({
          next: () => {
            this.weapons.update(weapons => weapons.filter(w => w.id !== id));
            this.snackBar.open('Weapon deleted successfully', 'Dismiss', { duration: 3000 });
          },
          error: (err) => {
            this.snackBar.open('Error deleting weapon', 'Dismiss', { duration: 3000 });
            console.error('Error deleting weapon:', err);
          }
        });
      }
    });
  }
}
