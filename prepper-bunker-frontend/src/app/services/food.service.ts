import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Food, FoodCreate } from '../models/food';

@Injectable({
  providedIn: 'root'
})
export class FoodService extends ApiService<Food, FoodCreate> {
  protected override endpoint = 'food';

  getExpiredFood(): Observable<Food[]> {
    return this.http.get<Food[]>(`${this.apiUrl}/${this.endpoint}/expired`);
  }
}
