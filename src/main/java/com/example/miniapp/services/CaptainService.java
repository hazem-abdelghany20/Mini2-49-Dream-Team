package com.example.miniapp.services;

import com.example.miniapp.models.Captain;
import com.example.miniapp.repositories.CaptainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional // Good practice for service layer methods involving DB operations
public class CaptainService {

    private final CaptainRepository captainRepository;

    @Autowired
    public CaptainService(CaptainRepository captainRepository) {
        this.captainRepository = captainRepository;
    }

    public List<Captain> getAllCaptains() {
        return captainRepository.findAll();
    }

    public Optional<Captain> getCaptainById(Long id) {
        return captainRepository.findById(id);
    }

    public Captain createCaptain(Captain captain) {
        // Add validation or business logic if needed before saving
        return captainRepository.save(captain);
    }

    public Optional<Captain> updateCaptain(Long id, Captain captainDetails) {
        return captainRepository.findById(id)
            .map(existingCaptain -> {
                existingCaptain.setName(captainDetails.getName());
                existingCaptain.setEmail(captainDetails.getEmail());
                existingCaptain.setPhoneNumber(captainDetails.getPhoneNumber());
                existingCaptain.setVehicleDetails(captainDetails.getVehicleDetails());
                // Note: Handling the 'trips' list update might require more specific logic
                return captainRepository.save(existingCaptain);
            });
    }

    public boolean deleteCaptain(Long id) {
        return captainRepository.findById(id)
            .map(captain -> {
                captainRepository.delete(captain);
                return true;
            }).orElse(false);
    }

    // Example using custom repository method
    public Optional<Captain> findCaptainByEmail(String email) {
        return captainRepository.findByEmail(email);
    }
}
