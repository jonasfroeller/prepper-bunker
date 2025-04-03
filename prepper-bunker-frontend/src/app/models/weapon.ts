export interface Weapon {
  id: number;
  type: string;
  model: string;
  quantity: number;
  ammunitionTypeId: number;
  storageLocationId: number;
}

export interface WeaponCreate {
  type: string;
  model: string;
  quantity: number;
  ammunitionTypeId: number;
  storageLocationId: number;
}
