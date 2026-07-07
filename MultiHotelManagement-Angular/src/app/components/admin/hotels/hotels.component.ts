import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { API_URL } from '../../../constants';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-admin-hotels',
  standalone: true,
  templateUrl: './hotels.component.html',
  styleUrls: ['./hotels.component.css'],
})
export class AdminHotelsComponent implements OnInit {
  private http = inject(HttpClient);
  hotels: Hotel[] = [];

  ngOnInit() {
    this.http.get<Hotel[]>(`${API_URL}/hotels/approved`).subscribe(d => this.hotels = d);
  }
}
