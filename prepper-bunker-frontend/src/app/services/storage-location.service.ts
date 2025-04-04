import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { StorageLocation } from '../models/storage-location';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class StorageLocationService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/storage-locations`;

  getAll(): Observable<StorageLocation[]> {
    return this.http.get<StorageLocation[]>(this.apiUrl);
  }

  getById(id: number): Observable<StorageLocation> {
    return this.http.get<StorageLocation>(`${this.apiUrl}/${id}`);
  }

  create(location: Omit<StorageLocation, 'id'>): Observable<StorageLocation> {
    return this.http.post<StorageLocation>(this.apiUrl, location);
  }

  update(id: number, location: Partial<StorageLocation>): Observable<StorageLocation> {
    return this.http.put<StorageLocation>(`${this.apiUrl}/${id}`, location);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
