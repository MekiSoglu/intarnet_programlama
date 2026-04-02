import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CariComponent } from './cari.component';

describe('CariComponent', () => {
  let component: CariComponent;
  let fixture: ComponentFixture<CariComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CariComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CariComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
