import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HotelOwnerService } from '../../../../services/hotel-owner.service';
import { HotelOwner } from '../../../../models/hotel-owner.model';

@Component({
  selector: 'app-hotel-owner-register',
  imports: [CommonModule, FormsModule],
  templateUrl: './hotel-owner-register.html',
  styleUrl: './hotel-owner-register.css',
})
export class HotelOwnerRegister {
  owner: HotelOwner = {
    name: '',
    email: '',
    phone: '',
    address: '',
    gender: '',
    dateOfBirth: '',
    image: '',
    password: '',
  };

  confirmPassword: string = '';
  selectedImage?: File;
  preview: string | ArrayBuffer | null = null;

  constructor(private ownerService: HotelOwnerService) {}

  onFileSelected(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedImage = event.target.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.preview = reader.result;
      };
      reader.readAsDataURL(this.selectedImage!);
    }
  }

  saveOwner() {
    if (this.owner.password !== this.confirmPassword) {
      alert('Password and Confirm Password do not match.');
      return;
    }

    this.ownerService.createOwner(this.owner, this.selectedImage).subscribe({
      next: () => {
        alert('Hotel Owner Created Successfully');
        this.resetForm();
      },
      error: (err: any) => {
        console.error(err);
        alert(err.error?.message || 'Failed to Create Hotel Owner');
      },
    });
  }

  resetForm() {
    this.owner = {
      name: '',
      email: '',
      phone: '',
      address: '',
      gender: '',
      dateOfBirth: '',
      image: '',
      password: '',
    };
    this.confirmPassword = '';
    this.selectedImage = undefined;
    this.preview = null;
  }
}
