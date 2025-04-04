import { TestBed } from '@angular/core/testing';

import { AmmunitionService } from './ammunition.service';

describe('AmmunitionService', () => {
  let service: AmmunitionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AmmunitionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
