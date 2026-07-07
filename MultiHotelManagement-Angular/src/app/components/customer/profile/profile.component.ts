import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { Customer } from '../../../models/customer.model';

@Component({
  selector: 'app-customer-profile',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class CustomerProfileComponent implements OnInit {
  private auth = inject(AuthService);
  private custSvc = inject(CustomerService);
  profile: Customer | null = null;

  ngOnInit() {
    this.custSvc.getByUserId(this.auth.userId()!).subscribe(d => this.profile = d);
  }

  save() {
    if (!this.profile) return;
    const fd = new FormData();
    fd.append('data', new Blob([JSON.stringify(this.profile)], { type: 'application/json' }));
    this.custSvc.update(this.profile.id, fd).subscribe(() => alert('Profile updated!'));
  }
}
