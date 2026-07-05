package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.WishlistMapper;
import com.MHM.MultiHotelManagement.dto.request.WishlistRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.WishlistResponseDTO;
import com.MHM.MultiHotelManagement.entity.Customer;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.entity.Wishlist;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.CustomerRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import com.MHM.MultiHotelManagement.repository.WishlistRepository;
import com.MHM.MultiHotelManagement.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepo;
    private final UserRepository userRepo;
    private final CustomerRepository customerRepo;
    private final HotelRepository hotelRepo;

    @Override
    @Transactional
    public WishlistResponseDTO addToWishlist(WishlistRequestDTO dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Customer customer = customerRepo.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        Hotel hotel = hotelRepo.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        Wishlist wishlist = new Wishlist();
        wishlist.setUser(user);
        wishlist.setCustomer(customer);
        wishlist.setHotel(hotel);
        wishlist.setNotes(dto.getNotes());
        wishlist.setIsActive(true);

        Wishlist saved = wishlistRepo.save(wishlist);
        return WishlistMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long id) {
        Wishlist wishlist = wishlistRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist not found"));
        wishlistRepo.delete(wishlist);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistResponseDTO> getAll() {
        return wishlistRepo.findAll()
                .stream()
                .map(WishlistMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistResponseDTO> getByUserId(Long userId) {
        return wishlistRepo.findByUser_Id(userId)
                .stream()
                .map(WishlistMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistResponseDTO> getByCustomerId(Long customerId) {
        return wishlistRepo.findByCustomer_Id(customerId)
                .stream()
                .map(WishlistMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistResponseDTO> getByHotelId(Long hotelId) {
        return wishlistRepo.findByHotel_Id(hotelId)
                .stream()
                .map(WishlistMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCustomerAndHotel(Long customerId, Long hotelId) {
        return wishlistRepo.existsByCustomer_IdAndHotel_Id(customerId, hotelId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserAndHotel(Long userId, Long hotelId) {
        return wishlistRepo.existsByUser_IdAndHotel_Id(userId, hotelId);
    }
}
