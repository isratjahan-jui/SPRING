import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { Customer } from '../../../models/customer.model';
import { StorageService } from '../../../services/storage.service';

@Component({
  selector: 'app-customer-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class CustomerProfile implements OnInit {
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private storage = inject(StorageService);
  private cdr = inject(ChangeDetectorRef);

  customer?: Customer;
  loading = true;
  saving = false;
  successMsg = '';
  errorMsg = '';

  profile = {
    customerName: '',
    email: '',
    phone: '',
    address: '',
    gender: '',
    dateOfBirth: '',
  };

  password = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  };
  passwordSaving = false;
  passwordSuccess = '';
  passwordError = '';

  ngOnInit() {
    const user = this.storage.getUser();
    if (user?.userId) {
      this.customerService.getCustomerByUserId(user.userId).subscribe({
        next: (data) => {
          this.customer = data;
          this.profile = {
            customerName: data.customerName || '',
            email: data.email || '',
            phone: data.phone || '',
            address: data.address || '',
            gender: data.gender || '',
            dateOfBirth: data.dateOfBirth || '',
          };
          this.loading = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
    } else {
      this.loading = false;
    }
  }

  saveProfile() {
    if (!this.customer?.id) return;
    this.saving = true;
    this.successMsg = '';
    this.errorMsg = '';

    const updateData: Customer = {
      id: this.customer.id,
      userId: this.customer.userId,
      name: this.profile.customerName,
      email: this.profile.email,
      phone: this.profile.phone,
      customerName: this.profile.customerName,
      address: this.profile.address,
      gender: this.profile.gender,
      dateOfBirth: this.profile.dateOfBirth,
    };

    this.customerService.updateCustomer(this.customer.id, updateData).subscribe({
      next: (data) => {
        this.customer = data;
        this.saving = false;
        this.successMsg = 'Profile updated successfully!';
        this.cdr.markForCheck();
        setTimeout(() => (this.successMsg = ''), 3000);
      },
      error: (err) => {
        this.saving = false;
        this.errorMsg = err.error?.message || 'Failed to update profile.';
        this.cdr.markForCheck();
      },
    });
  }

  changePassword() {
    if (this.password.newPassword !== this.password.confirmPassword) {
      this.passwordError = 'New passwords do not match.';
      return;
    }
    if (this.password.newPassword.length < 8) {
      this.passwordError = 'Password must be at least 8 characters.';
      return;
    }
    if (!this.customer?.userId) return;

    this.passwordSaving = true;
    this.passwordSuccess = '';
    this.passwordError = '';

    this.auth
      .changePassword(
        this.customer.userId,
        this.password.currentPassword,
        this.password.newPassword,
      )
      .subscribe({
        next: () => {
          this.passwordSaving = false;
          this.passwordSuccess = 'Password changed successfully!';
          this.password = { currentPassword: '', newPassword: '', confirmPassword: '' };
          this.cdr.markForCheck();
          setTimeout(() => (this.passwordSuccess = ''), 3000);
        },
        error: (err) => {
          this.passwordSaving = false;
          this.passwordError = err.error?.message || 'Failed to change password.';
          this.cdr.markForCheck();
        },
      });
  }
}
