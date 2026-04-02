import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CarilerimComponent } from './carilerim.component';

describe('CarilerimComponent', () => {
  let component: CarilerimComponent;
  let fixture: ComponentFixture<CarilerimComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CarilerimComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CarilerimComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
