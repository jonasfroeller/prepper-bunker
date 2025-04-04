import { Routes } from '@angular/router';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { WeaponsComponent } from './components/weapons/weapons.component';
import { FoodComponent } from './components/food/food.component';
import { MedicationsComponent } from './components/medications/medications.component';
import { StorageLocationsComponent } from './components/storage-locations/storage-locations.component';
import { AmmunitionComponent } from './components/ammunition/ammunition.component';
import { FuelComponent } from './components/fuel/fuel.component';
import { BatteriesComponent } from './components/batteries/batteries.component';
import { GeneratorsComponent } from './components/generators/generators.component';
import { APP_ROUTES } from './shared/route-constants';

const COMPONENT_MAP = {
  [APP_ROUTES.WEAPONS.path]: WeaponsComponent,
  [APP_ROUTES.AMMUNITION.path]: AmmunitionComponent,
  [APP_ROUTES.FOOD.path]: FoodComponent,
  [APP_ROUTES.MEDICATIONS.path]: MedicationsComponent,
  [APP_ROUTES.FUEL.path]: FuelComponent,
  [APP_ROUTES.BATTERIES.path]: BatteriesComponent,
  [APP_ROUTES.GENERATORS.path]: GeneratorsComponent,
  [APP_ROUTES.STORAGE_LOCATIONS.path]: StorageLocationsComponent
};

export const routes: Routes = [
  { path: '', redirectTo: APP_ROUTES.WEAPONS.path, pathMatch: 'full' },
  ...Object.values(APP_ROUTES).map(route => ({
    path: route.path,
    component: COMPONENT_MAP[route.path]
  })),
  { path: '**', component: NotFoundComponent }
];
