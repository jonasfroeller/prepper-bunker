import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Weapon, WeaponCreate } from '../models/weapon';

@Injectable({
  providedIn: 'root'
})
export class WeaponService extends ApiService<Weapon, WeaponCreate> {
  protected override endpoint = 'weapons';

  getWeaponsByLocation(locationId: number): Observable<Weapon[]> {
    return this.http.get<Weapon[]>(`${this.apiUrl}/${this.endpoint}/by-location/${locationId}`);
  }
}
