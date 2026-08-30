import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SetupLayoutComponent } from './setup-layout.component';

describe('SetupLayoutComponent', () => {
  let component: SetupLayoutComponent;
  let fixture: ComponentFixture<SetupLayoutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SetupLayoutComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SetupLayoutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
