import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IslemlerimComponent } from './islemlerim.component';

describe('IslemlerimComponent', () => {
  let component: IslemlerimComponent;
  let fixture: ComponentFixture<IslemlerimComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [IslemlerimComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IslemlerimComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
