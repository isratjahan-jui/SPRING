import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CustomerService } from '../../../../services/customer.service';
import { Customer } from '../../../../models/customer.model';

@Component({
  selector: 'app-customer-register',
  imports: [CommonModule, FormsModule],
  templateUrl: './customer-register.html',
  styleUrl: './customer-register.css',
})
export class CustomerRegisterComponent {
  customer: Customer = {
    name: '',
    customerName: '',
    email: '',
    phone: '',
    password: '',
    address: '',
    gender: '',
    dateOfBirth: '',
    image: '',
  };

  confirmPassword = '';
  selectedImage?: File;
  preview: string | ArrayBuffer | null = null;

  constructor(private customerService: CustomerService) {}

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

  register(): void {
    if (this.customer.password !== this.confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    this.customerService.register(this.customer, this.selectedImage).subscribe({
      next: () => {
        alert('Registration Successful');
        this.reset();
      },
      error: (err) => {
        console.error(err);
        alert('Registration Failed');
      },
    });
  }

  reset(): void {
    this.customer = {
      name: '',
      customerName: '',
      email: '',
      phone: '',
      password: '',
      address: '',
      gender: '',
      dateOfBirth: '',
      image: '',
    };
    this.confirmPassword = '';
    this.selectedImage = undefined;
    this.preview = null;
  }
}
