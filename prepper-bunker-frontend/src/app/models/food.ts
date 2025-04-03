export interface Food {
  id: number;
  type: string;
  quantity: number;
  expirationDate: string;
  storageLocationId: number;
}

export interface FoodCreate {
  type: string;
  quantity: number;
  expirationDate: string;
  storageLocationId: number;
}
