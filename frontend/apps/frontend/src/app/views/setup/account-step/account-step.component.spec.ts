import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AccountStepComponent } from './account-step.component';

describe('AccountSetupComponent', () => {
  let component: AccountStepComponent;
  let fixture: ComponentFixture<AccountStepComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountStepComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AccountStepComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
