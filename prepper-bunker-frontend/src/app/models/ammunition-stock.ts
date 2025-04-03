export interface AmmunitionStock {
  id: number;
  ammunitionTypeId: number;
  quantity: number;
  storageLocationId: number;
}

export interface AmmunitionStockCreate {
  ammunitionTypeId: number;
  quantity: number;
  storageLocationId: number;
}
