import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
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
    password: ''
  };

  confirmPassword: string = '';

  constructor(private ownerService: HotelOwenerService) { }

  saveOwner() {

    if (this.owner.password !== this.confirmPassword) {
      alert('Password and Confirm Password do not match.');
      return;
    }

    this.ownerService.createOwner(this.owner).subscribe({
      next: () => {
        alert('Hotel Owner Created Successfully');
        this.resetForm();
      },
      error: (err) => {
        console.error(err);
        alert('Failed to Create Hotel Owner');
      }
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
      password: ''
    };

    this.confirmPassword = '';
  }




}
