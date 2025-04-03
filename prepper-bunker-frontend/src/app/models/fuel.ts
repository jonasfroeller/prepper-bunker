export interface Fuel {
  id: number;
  fuelTypeId: number;
  quantity: number;
  storageLocationId: number;
}

export interface FuelCreate {
  fuelTypeId: number;
  quantity: number;
  storageLocationId: number;
}
