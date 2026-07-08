import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environments';
import { CustomerService } from '../../../../services/customer.service';
import { Customer } from '../../../../models/customer.model';

@Component({
  selector: 'app-customer-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customer-register.html',
  styleUrls: ['./customer-register.css']
})
export class CustomerRegisterComponent {


   customer: Customer = {
    id: 0, // Remove if your model doesn't have id
    name: '',
    customerName: '',
    email: '',
    phone: '',
    password: '',
    address: '',
    gender: '',
    dateOfBirth: '',
    image: ''
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

      alert("Passwords do not match");

      return;
    }

    this.customerService.register(this.customer, this.selectedImage)
      .subscribe({

        next: (res) => {

          console.log(res);

          alert("Registration Successful");

          this.reset();

        },

        error: (err) => {

          console.error(err);

          alert("Registration Failed");

        }

      });

  }

  reset(): void {

    this.customer = {
      id: 0, // Remove if your model doesn't have id
      name: '',
      customerName: '',
      email: '',
      phone: '',
      password: '',
      address: '',
      gender: '',
      dateOfBirth: '',
      image: ''
    };

    this.confirmPassword = '';

    this.selectedImage = undefined;

    this.preview = null;
  }

} 