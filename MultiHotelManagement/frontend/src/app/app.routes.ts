import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { AdminInvoicesComponent } from './pages/admin-invoices/admin-invoices';
import { AdminReceiptsComponent } from './pages/admin-receipts/admin-receipts';
import { AdminPaymentsComponent } from './pages/admin-payments/admin-payments';
import { OwnerInvoicesComponent } from './pages/owner-invoices/owner-invoices';
import { OwnerReceiptsComponent } from './pages/owner-receipts/owner-receipts';
import { CustomerInvoicesComponent } from './pages/customer-invoices/customer-invoices';
import { CustomerReceiptsComponent } from './pages/customer-receipts/customer-receipts';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'admin/invoices', component: AdminInvoicesComponent },
  { path: 'admin/receipts', component: AdminReceiptsComponent },
  { path: 'admin/payments', component: AdminPaymentsComponent },
  { path: 'owner/invoices', component: OwnerInvoicesComponent },
  { path: 'owner/receipts', component: OwnerReceiptsComponent },
  { path: 'customer/invoices', component: CustomerInvoicesComponent },
  { path: 'customer/receipts', component: CustomerReceiptsComponent },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];