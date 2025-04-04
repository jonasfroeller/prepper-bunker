import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Generator } from '../models/generator';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class GeneratorService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/generators`;

  getAll(): Observable<Generator[]> {
    return this.http.get<Generator[]>(this.apiUrl);
  }

  getById(id: number): Observable<Generator> {
    return this.http.get<Generator>(`${this.apiUrl}/${id}`);
  }

  create(generator: Omit<Generator, 'id'>): Observable<Generator> {
    return this.http.post<Generator>(this.apiUrl, generator);
  }

  update(id: number, generator: Partial<Generator>): Observable<Generator> {
    return this.http.put<Generator>(`${this.apiUrl}/${id}`, generator);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
