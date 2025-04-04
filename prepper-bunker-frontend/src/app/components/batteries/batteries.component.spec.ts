import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BatteriesComponent } from './batteries.component';

describe('BatteriesComponent', () => {
  let component: BatteriesComponent;
  let fixture: ComponentFixture<BatteriesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BatteriesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BatteriesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
