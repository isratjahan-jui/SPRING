import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HotelOwner } from '../../../models/hotel-owner.model';
import { HotelOwenerService } from '../../../services/hotel-owener-service';

@Component({
  selector: 'app-add-hotel-owner-component',
  imports: [CommonModule, FormsModule],
  templateUrl: './add-hotel-owner-component.html',
  styleUrl: './add-hotel-owner-component.css',
})
export class AddHotelOwnerComponent {
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

  constructor(private ownerService: HotelOwenerService) {}

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
