export interface Generator {
  id: number;
  type: string;
  power: number;
  fuelTypeId: number;
  status: string;
  storageLocationId: number;
}

export interface GeneratorCreate {
  type: string;
  power: number;
  fuelTypeId: number;
  status: string;
  storageLocationId: number;
}
