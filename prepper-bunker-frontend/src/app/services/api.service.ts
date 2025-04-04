import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export abstract class ApiService<T, C> {
    protected http = inject(HttpClient);
    protected abstract endpoint: string;
    protected apiUrl = environment.apiUrl;

    getAll(): Observable<T[]> {
        return this.http.get<T[]>(`${this.apiUrl}/${this.endpoint}`);
    }

    getById(id: number): Observable<T> {
        return this.http.get<T>(`${this.apiUrl}/${this.endpoint}/${id}`);
    }

    create(item: C): Observable<T> {
        return this.http.post<T>(`${this.apiUrl}/${this.endpoint}`, item);
    }

    update(id: number, item: Partial<T>): Observable<T> {
        return this.http.put<T>(`${this.apiUrl}/${this.endpoint}/${id}`, item);
    }

    delete(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${this.endpoint}/${id}`);
    }
}
