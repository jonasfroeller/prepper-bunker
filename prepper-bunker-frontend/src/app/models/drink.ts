export interface Drink {
  id: number;
  type: string;
  quantity: number;
  expirationDate: string;
  storageLocationId: number;
}

export interface DrinkCreate {
  type: string;
  quantity: number;
  expirationDate: string;
  storageLocationId: number;
}
