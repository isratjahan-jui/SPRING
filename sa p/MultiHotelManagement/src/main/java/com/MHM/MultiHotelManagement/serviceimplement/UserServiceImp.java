package com.MHM.MultiHotelManagement.serviceimplement;


import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import com.MHM.MultiHotelManagement.service.UserService;
import com.MHM.MultiHotelManagement.service.AuditTrailService;
import com.MHM.MultiHotelManagement.util.OwnershipGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditTrailService auditTrailService;

    @Autowired
    private OwnershipGuard ownershipGuard;

    @Override
    @Transactional
    public User save(User u) {
        return userRepository.save(u);

    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        String deletedEmail = userRepository.findById(id).map(User::getEmail).orElse("unknown");
        userRepository.deleteById(id);

        try {
            auditTrailService.logAction("USER_DELETED", "User", id,
                    "User account '" + deletedEmail + "' deleted",
                    ownershipGuard.getCurrentUser().getEmail());
        } catch (Exception e) {
            // audit failure must not roll back the delete itself
        }
    }


}
