import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Fuel } from '../models/fuel';
import { environment } from '../../environments/environment';
import { FuelType } from '../models/fuel-type';

@Injectable({
  providedIn: 'root'
})
export class FuelService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  getAll(): Observable<Fuel[]> {
    return this.http.get<Fuel[]>(`${this.apiUrl}/fuel`);
  }

  getById(id: number): Observable<Fuel> {
    return this.http.get<Fuel>(`${this.apiUrl}/fuel/${id}`);
  }

  create(fuel: Partial<Fuel>): Observable<Fuel> {
    return this.http.post<Fuel>(`${this.apiUrl}/fuel`, fuel);
  }

  update(id: number, fuel: Partial<Fuel>): Observable<Fuel> {
    return this.http.put<Fuel>(`${this.apiUrl}/fuel/${id}`, fuel);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/fuel/${id}`);
  }

  getAllTypes(): Observable<FuelType[]> {
    return this.http.get<FuelType[]>(`${this.apiUrl}/fuel-types`);
  }
}
