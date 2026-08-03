package com.MHM.MultiHotelManagement.util;

import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.Customer;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.HotelOwner;
import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OwnershipGuard {

    private final UserRepository userRepository;

    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    public boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Not authenticated");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new AccessDeniedException("Not authenticated"));
    }

    public boolean isHotelOwnedByCurrentUser(Hotel hotel, User currentUser) {
        return hotel != null && hotel.getOwner() != null && hotel.getOwner().getUser() != null
                && hotel.getOwner().getUser().getId().equals(currentUser.getId());
    }

    // Verifies the authenticated user is ADMIN or the owner of the given hotel; throws otherwise.
    public void verifyHotelOwnership(Hotel hotel) {
        if (isAdmin()) {
            return;
        }
        User currentUser = getCurrentUser();
        if (!isHotelOwnedByCurrentUser(hotel, currentUser)) {
            throw new AccessDeniedException("You do not have permission to modify this hotel's resources");
        }
    }

    // A booking is reachable by the hotel that owns the stay, the customer who made the
    // booking, or an admin — not just any authenticated hotel owner.
    public void verifyBookingAccess(Booking booking) {
        if (isAdmin()) {
            return;
        }
        User currentUser = getCurrentUser();
        boolean isHotelOwner = isHotelOwnedByCurrentUser(booking.getHotel(), currentUser);
        boolean isBookingCustomer = booking.getCustomer() != null
                && booking.getCustomer().getUser() != null
                && booking.getCustomer().getUser().getId().equals(currentUser.getId());
        if (!isHotelOwner && !isBookingCustomer) {
            throw new AccessDeniedException("You do not have permission to access this booking");
        }
    }

    // Verifies the authenticated user is ADMIN or the given customer's own account.
    public void verifyCustomerAccess(Customer customer) {
        if (isAdmin()) {
            return;
        }
        User currentUser = getCurrentUser();
        if (customer == null || customer.getUser() == null
                || !customer.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to access this customer's data");
        }
    }

    // Verifies the authenticated user is ADMIN or the given hotel owner's own account.
    public void verifyHotelOwnerAccess(HotelOwner owner) {
        if (isAdmin()) {
            return;
        }
        User currentUser = getCurrentUser();
        if (owner == null || owner.getUser() == null
                || !owner.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You do not have permission to access this owner's data");
        }
    }
}
