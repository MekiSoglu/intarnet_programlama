import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IslemlerComponent } from './islemler.component';

describe('IslemlerComponent', () => {
  let component: IslemlerComponent;
  let fixture: ComponentFixture<IslemlerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [IslemlerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IslemlerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
