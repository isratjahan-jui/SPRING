import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

import { Hotel } from '../../../models/hotel.model';
import { DealResponse } from '../../../models/deal.model';
import { HotelService } from '../../../services/hotel.service';
import { DealService } from '../../../services/deal.service';
import { CommonModule } from '@angular/common';
import { environment } from '../../../../environments/environments';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, CommonModule, FormsModule],
  templateUrl: './home.html',
  styleUrls: ['./home.css'],
})
export class HomeComponent implements OnInit {
  private hotelService = inject(HotelService);
  private dealService = inject(DealService);
  private cdr = inject(ChangeDetectorRef);
  hotels: Hotel[] = [];
  allHotels: Hotel[] = []; // full approved list — used for real destination counts
  deals: DealResponse[] = [];
  searchKeyword = '';
  searchActive = false;
  imageBaseUrl = environment.imageBaseUrl;

  activeDivision = '';
  loadingHotels = true;

  // Hero quick filters — real, searchable Bangladesh destinations
  quickFilters = ['Dhaka', "Cox's Bazar", 'Sylhet', 'Chattogram', 'Bandarban'];

  // KPI statistics
  stats = [
    { icon: '🏨', value: '1,250+', label: 'Hotels' },
    { icon: '🛏️', value: '45,000', label: 'Rooms' },
    { icon: '😊', value: '120,000', label: 'Happy Guests' },
    { icon: '⭐', value: '48,000', label: 'Reviews' },
    { icon: '📍', value: '45', label: 'Cities' },
    { icon: '🔥', value: '150', label: 'Active Deals' },
  ];

  // Explore destinations — derived from real hotels (see realDestinations getter)

  // Featured amenities
  amenities = [
    { icon: '📶', name: 'Free WiFi' },
    { icon: '🏊', name: 'Swimming Pool' },
    { icon: '💆', name: 'Spa' },
    { icon: '🏋️', name: 'Gym' },
    { icon: '🍽️', name: 'Restaurant' },
    { icon: '🅿️', name: 'Parking' },
    { icon: '🚐', name: 'Airport Shuttle' },
    { icon: '🐾', name: 'Pet Friendly' },
  ];

  // Why choose us
  features = [
    { icon: '🏷️', title: 'Best Price Guarantee', text: 'Find a lower price and we will match it — every time.' },
    { icon: '⚡', title: 'Instant Confirmation', text: 'Book in seconds and get confirmed immediately.' },
    { icon: '✅', title: 'Verified Hotels', text: 'Every property is reviewed and approved by our team.' },
    { icon: '🔒', title: 'Secure Payments', text: 'Bank-grade encryption on every transaction.' },
    { icon: '🔄', title: 'Free Cancellation', text: 'Flexible plans with free cancellation on most stays.' },
    { icon: '🎧', title: '24/7 Customer Support', text: 'Real people ready to help, any time of day.' },
  ];

  // Guest reviews
  testimonials = [
    { name: 'Rafiq Ahmed', country: 'Dhaka, Bangladesh', rating: 5, comment: 'Booking was effortless and the hotel exceeded expectations. TripNest is now my go-to.' },
    { name: 'Sadia Karim', country: "Cox's Bazar, Bangladesh", rating: 5, comment: 'Loved the deals section — saved 20% on a beachfront resort. Highly recommended!' },
    { name: 'Tanvir Hasan', country: 'Sylhet, Bangladesh', rating: 4, comment: 'Clean interface, verified hotels, and instant confirmation. Exactly what travel should be.' },
  ];

  // Travel inspiration
  travelPosts = [
    { title: 'Top Beaches in Bangladesh', tag: 'Beaches', img: 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600&q=80' },
    { title: 'Perfect Weekend Trips', tag: 'Weekend', img: 'https://images.unsplash.com/photo-1476514525535-07fb3b4ae5f1?w=600&q=80' },
    { title: 'Family Vacation Ideas', tag: 'Family', img: 'https://images.unsplash.com/photo-1533105079780-92b9be482077?w=600&q=80' },
  ];

  newsletterEmail = '';

  divisions = [
    { name: 'Dhaka', icon: '🏙️' },
    { name: 'Chattogram', icon: '⚓' },
    { name: 'Rajshahi', icon: '🥭' },
    { name: 'Khulna', icon: '🌿' },
    { name: 'Barishal', icon: '🚢' },
    { name: 'Sylhet', icon: '🍵' },
    { name: 'Rangpur', icon: '🌾' },
    { name: 'Mymensingh', icon: '🎓' },
  ];

  ngOnInit() {
    this.loadHotels();
    this.dealService.getAll().subscribe({
      next: (data) => {
        this.deals = data;
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  // Real destinations grouped from the actual approved hotels (city → count)
  get realDestinations(): { name: string; count: number }[] {
    const map = new Map<string, number>();
    for (const h of this.allHotels) {
      const city = (h.city || h.locationName || '').trim();
      if (!city) continue;
      map.set(city, (map.get(city) || 0) + 1);
    }
    return Array.from(map.entries())
      .map(([name, count]) => ({ name, count }))
      .sort((a, b) => b.count - a.count);
  }

  // Featured = all approved; Top rated = rating >= 4.5 (rating is stored as a string)
  get topRatedHotels(): Hotel[] {
    return [...this.hotels]
      .filter((h) => parseFloat(h.rating || '0') >= 4.5)
      .sort((a, b) => parseFloat(b.rating || '0') - parseFloat(a.rating || '0'));
  }

  loadHotels() {
    this.loadingHotels = true;
    this.cdr.markForCheck();
    this.hotelService.getAllApproved().subscribe({
      next: (data) => {
        this.hotels = data;
        this.allHotels = data; // keep the full list for real destination counts
        this.searchActive = false;
        this.loadingHotels = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.hotels = [];
        this.loadingHotels = false;
        this.cdr.markForCheck();
      },
    });
  }

  search() {
    const q = this.searchKeyword.trim();
    if (!q) {
      this.loadHotels();
      return;
    }

    // If the destination matches a known city/division (e.g. "dhaka"), use the
    // reliable city endpoint so it returns ALL hotels in that city.
    const known = [...this.divisions.map((d) => d.name), ...this.quickFilters];
    const cityMatch = known.find((name) => name.toLowerCase() === q.toLowerCase());
    if (cityMatch) {
      this.browseDestination(cityMatch);
      return;
    }

    this.activeDivision = '';
    this.loadingHotels = true;
    this.cdr.markForCheck();
    this.hotelService.search(q).subscribe({
      next: (data) => {
        this.hotels = data;
        this.searchActive = true;
        this.loadingHotels = false;
        this.cdr.markForCheck();
        this.scrollToHotels();
      },
      error: () => {
        this.hotels = [];
        this.searchActive = true;
        this.loadingHotels = false;
        this.cdr.markForCheck();
        this.scrollToHotels();
      },
    });
  }

  clearSearch() {
    this.searchKeyword = '';
    this.loadHotels();
  }

  filterByDivision(division: string) {
    if (this.activeDivision === division) {
      this.clearDivisionFilter();
      return;
    }
    this.searchKeyword = '';
    this.searchActive = false;
    this.activeDivision = division;
    this.loadingHotels = true;
    this.cdr.markForCheck();
    this.hotelService.getByCity(division).subscribe({
      next: (data) => {
        this.hotels = data;
        this.loadingHotels = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.hotels = [];
        this.loadingHotels = false;
        this.cdr.markForCheck();
      },
    });
  }

  clearDivisionFilter() {
    this.activeDivision = '';
    this.loadHotels();
  }

  searchDestination(name: string) {
    this.searchKeyword = name;
    this.search();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  // Real count of approved hotels matching a city/division name
  countForCity(name: string): number {
    const n = name.toLowerCase();
    return this.allHotels.filter(
      (h) =>
        (h.city || '').toLowerCase().includes(n) ||
        (h.locationName || '').toLowerCase().includes(n) ||
        (h.address || '').toLowerCase().includes(n),
    ).length;
  }

  // Real backend filter by city, then scroll down to the results
  browseDestination(name: string) {
    this.searchKeyword = name;
    this.searchActive = false;
    this.activeDivision = name;
    this.loadingHotels = true;
    this.cdr.markForCheck();
    this.hotelService.getByCity(name).subscribe({
      next: (data) => {
        this.hotels = data;
        this.loadingHotels = false;
        this.cdr.markForCheck();
        this.scrollToHotels();
      },
      error: () => {
        this.hotels = [];
        this.loadingHotels = false;
        this.cdr.markForCheck();
        this.scrollToHotels();
      },
    });
  }

  private scrollToHotels() {
    setTimeout(() => {
      document.getElementById('featured-hotels')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 100);
  }

  subscribeNewsletter() {
    if (this.newsletterEmail && this.newsletterEmail.includes('@')) {
      this.newsletterEmail = '';
      alert('Thanks for subscribing! You will receive our best deals.');
    }
  }
}
