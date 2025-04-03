export interface Battery {
  id: number;
  type: string;
  capacity: number;
  quantity: number;
  storageLocationId: number;
}

export interface BatteryCreate {
  type: string;
  capacity: number;
  quantity: number;
  storageLocationId: number;
}
