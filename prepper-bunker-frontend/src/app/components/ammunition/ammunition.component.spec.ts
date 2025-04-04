import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AmmunitionComponent } from './ammunition.component';

describe('AmmunitionComponent', () => {
  let component: AmmunitionComponent;
  let fixture: ComponentFixture<AmmunitionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AmmunitionComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AmmunitionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
