import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Medication, MedicationCreate } from '../models/medication';

@Injectable({
  providedIn: 'root'
})
export class MedicationService extends ApiService<Medication, MedicationCreate> {
  protected override endpoint = 'medications';

  getExpiredMedications(): Observable<Medication[]> {
    return this.http.get<Medication[]>(`${this.apiUrl}/${this.endpoint}/expired`);
  }
}
