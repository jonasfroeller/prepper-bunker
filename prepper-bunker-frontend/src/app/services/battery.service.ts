import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Battery } from '../models/battery';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class BatteryService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/batteries`;

  getAll(): Observable<Battery[]> {
    return this.http.get<Battery[]>(this.apiUrl);
  }

  getById(id: number): Observable<Battery> {
    return this.http.get<Battery>(`${this.apiUrl}/${id}`);
  }

  create(battery: Omit<Battery, 'id'>): Observable<Battery> {
    return this.http.post<Battery>(this.apiUrl, battery);
  }

  update(id: number, battery: Partial<Battery>): Observable<Battery> {
    return this.http.put<Battery>(`${this.apiUrl}/${id}`, battery);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
