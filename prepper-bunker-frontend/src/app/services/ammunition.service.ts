import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { AmmunitionStock, AmmunitionStockCreate } from '../models/ammunition-stock';
import { AmmunitionType } from '../models/ammunition-type';

@Injectable({
  providedIn: 'root'
})
export class AmmunitionService extends ApiService<AmmunitionStock, AmmunitionStockCreate> {
  protected override endpoint = 'ammunition-stocks';

  getTotalByType(typeId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/${this.endpoint}/total/${typeId}`);
  }

  getAllTypes(): Observable<AmmunitionType[]> {
    return this.http.get<AmmunitionType[]>(`${this.apiUrl}/ammunition-types`);
  }

  getTypeById(id: number): Observable<AmmunitionType> {
    return this.http.get<AmmunitionType>(`${this.apiUrl}/ammunition-types/${id}`);
  }
}
