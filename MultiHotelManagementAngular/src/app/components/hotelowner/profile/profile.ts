import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { HotelOwnerService } from '../../../services/hotel-owner.service';
import { StorageService } from '../../../services/storage.service';
import { LoginResponse } from '../../../models/auth.model';
import { HotelOwner } from '../../../models/hotel-owner.model';

@Component({
  selector: 'app-owner-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class OwnerProfile implements OnInit {
  private auth = inject(AuthService);
  private ownerService = inject(HotelOwnerService);
  private storage = inject(StorageService);
  private cdr = inject(ChangeDetectorRef);

  user: LoginResponse | null = null;
  owner: HotelOwner | null = null;
  loading = true;
  saving = false;
  successMsg = '';
  errorMsg = '';

  profileForm = {
    name: '',
    email: '',
    phone: '',
    gender: '',
    dateOfBirth: '',
    address: '',
  };

  passwordForm = {
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  };

  ngOnInit() {
    this.user = this.storage.getUser();
    this.loadOwner();
  }

  loadOwner() {
    if (!this.user?.userId) {
      this.loading = false;
      return;
    }
    this.ownerService.getOwnerByUserId(this.user.userId).subscribe({
      next: (data) => {
        this.owner = data;
        this.profileForm = {
          name: data.name || '',
          email: data.email || '',
          phone: data.phone || '',
          gender: data.gender || '',
          dateOfBirth: data.dateOfBirth || '',
          address: data.address || '',
        };
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  saveProfile() {
    if (!this.owner?.id) return;
    this.saving = true;
    this.successMsg = '';
    this.errorMsg = '';

    this.ownerService.updateOwner(this.owner.id, this.profileForm as any).subscribe({
      next: (updated) => {
        this.owner = updated;
        this.saving = false;
        this.successMsg = 'Profile updated successfully!';
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.saving = false;
        this.errorMsg = err.error?.message || 'Failed to update profile.';
        this.cdr.markForCheck();
      },
    });
  }

  changePassword() {
    this.successMsg = '';
    this.errorMsg = '';

    if (!this.passwordForm.currentPassword || !this.passwordForm.newPassword) {
      this.errorMsg = 'Please fill in all password fields.';
      return;
    }
    if (this.passwordForm.newPassword.length < 8) {
      this.errorMsg = 'New password must be at least 8 characters.';
      return;
    }
    if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.errorMsg = 'New passwords do not match.';
      return;
    }

    this.saving = true;
    this.auth
      .changePassword(
        this.user!.userId,
        this.passwordForm.currentPassword,
        this.passwordForm.newPassword,
      )
      .subscribe({
        next: () => {
          this.saving = false;
          this.successMsg = 'Password changed successfully!';
          this.passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };
          this.cdr.markForCheck();
        },
        error: (err) => {
          this.saving = false;
          this.errorMsg = err.error?.message || 'Failed to change password.';
          this.cdr.markForCheck();
        },
      });
  }
}
