package com.example.miniapp.controllers;

import com.example.miniapp.models.Captain;
import com.example.miniapp.services.CaptainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/captains")
public class CaptainController {

    private final CaptainService captainService;

    @Autowired
    public CaptainController(CaptainService captainService) {
        this.captainService = captainService;
    }

    @GetMapping
    public ResponseEntity<List<Captain>> getAllCaptains() {
        return ResponseEntity.ok(captainService.getAllCaptains());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Captain> getCaptainById(@PathVariable Long id) {
        return captainService.getCaptainById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Captain> createCaptain(@RequestBody Captain captain) {
        Captain createdCaptain = captainService.createCaptain(captain);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCaptain);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Captain> updateCaptain(@PathVariable Long id, @RequestBody Captain captain) {
        return captainService.updateCaptain(id, captain)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCaptain(@PathVariable Long id) {
        if (captainService.deleteCaptain(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Captain> getCaptainByEmail(@PathVariable String email) {
        return captainService.findCaptainByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
