import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { DashboardComponent } from './components/dashboard/dashboard';
import { AnalysisComponent } from './components/analysis/analysis';
import { LoanComponent } from './components/loan/loan';
import { BillComponent } from './components/bill/bill';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'analysis', component: AnalysisComponent },
  { path: 'loan', component: LoanComponent },
  { path: 'bills', component: BillComponent }
];
