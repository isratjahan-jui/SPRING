import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../../services/customer.service';
import { Customer } from '../../../models/customer.model';
import { environment } from '../../../../environments/environments';

@Component({
  selector: 'app-admin-customers',
  imports: [CommonModule, FormsModule],
  templateUrl: './customers.html',
  styleUrl: './customers.css',
})
export class AdminCustomers implements OnInit {
  private customerService = inject(CustomerService);
  private cdr = inject(ChangeDetectorRef);

  customers: Customer[] = [];
  filteredCustomers: Customer[] = [];
  loading = true;
  searchTerm = '';
  imageBaseUrl = environment.imageBaseUrl;
  deleteModalCustomer: Customer | null = null;

  ngOnInit() {
    this.loadCustomers();
  }

  loadCustomers() {
    this.loading = true;
    this.customerService.getAllCustomers().subscribe({
      next: (data) => {
        this.customers = data;
        this.filteredCustomers = data;
        this.loading = false;
        this.cdr.detectChanges();   // UI refresh after data load
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();   // UI refresh even on error
      },
    });
  }

  onSearch() {
    const term = this.searchTerm.toLowerCase();
    this.filteredCustomers = this.customers.filter(
      (c) =>
        (c.customerName && c.customerName.toLowerCase().includes(term)) ||
        c.name.toLowerCase().includes(term) ||
        c.email.toLowerCase().includes(term) ||
        c.phone.toLowerCase().includes(term) ||
        (c.address && c.address.toLowerCase().includes(term)),
    );
    this.cdr.detectChanges();   // UI refresh after search
  }

  confirmDelete(customer: Customer) {
    this.deleteModalCustomer = customer;
    this.cdr.detectChanges();
  }

  cancelDelete() {
    this.deleteModalCustomer = null;
    this.cdr.detectChanges();
  }

  deleteCustomer() {
    if (!this.deleteModalCustomer?.id) return;
    this.customerService.deleteCustomer(this.deleteModalCustomer.id).subscribe({
      next: () => {
        this.customers = this.customers.filter(
          (c) => c.id !== this.deleteModalCustomer!.id,
        );
        this.filteredCustomers = this.filteredCustomers.filter(
          (c) => c.id !== this.deleteModalCustomer!.id,
        );
        this.deleteModalCustomer = null;
        this.cdr.detectChanges();   // UI refresh after delete
      },
      error: () => {
        this.deleteModalCustomer = null;
        this.cdr.detectChanges();
      },
    });
  }

  getImageUrl(image?: string): string {
    return image ? `${this.imageBaseUrl}/customers/${image}` : '';
  }

  getDisplayName(c: Customer): string {
    return c.customerName || c.name;
  }
}
