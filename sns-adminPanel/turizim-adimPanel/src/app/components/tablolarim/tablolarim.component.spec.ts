import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TablolarimComponent } from './tablolarim.component';

describe('TablolarimComponent', () => {
  let component: TablolarimComponent;
  let fixture: ComponentFixture<TablolarimComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TablolarimComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TablolarimComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
