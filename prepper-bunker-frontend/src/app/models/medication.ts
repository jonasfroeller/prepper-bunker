export interface Medication {
  id: number;
  name: string;
  quantity: number;
  expirationDate: string;
  purpose: string;
  storageLocationId: number;
}

export interface MedicationCreate {
  name: string;
  quantity: number;
  expirationDate: string;
  purpose: string;
  storageLocationId: number;
}
