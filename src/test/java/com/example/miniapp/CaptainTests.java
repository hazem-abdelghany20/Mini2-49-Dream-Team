package com.example.miniapp;

import com.example.miniapp.controllers.CaptainController;
import com.example.miniapp.models.Captain;
import com.example.miniapp.repositories.CaptainRepository;
import com.example.miniapp.services.CaptainService;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CaptainTests {

    // -------------------- Model --------------------
    @Test
    void testCaptainModel() {
        Captain captain = new Captain(1L, "Ali", "LIC123", 4.8);
        assertEquals(1L, captain.getId());
        assertEquals("Ali", captain.getName());
        assertEquals("LIC123", captain.getLicenseNumber());
        assertEquals(4.8, captain.getRating());
    }

    @Test
    void testCaptainEquals() {
        Captain c1 = new Captain(1L, "Ali", "LIC123", 4.5);
        Captain c2 = new Captain(1L, "Ali", "LIC123", 4.5);
        assertEquals(c1, c2);
    }

    // -------------------- Service --------------------
    @Mock CaptainRepository captainRepository;

    @InjectMocks CaptainService captainService;

    @BeforeEach
    void setUpService() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCaptainService() {
        Captain captain = new Captain(null, "Ali", "LIC123", 4.5);
        when(captainRepository.save(captain)).thenReturn(captain);
        assertEquals(captain, captainService.addCaptain(captain));
    }

    @Test
    void testGetCaptainByIdService() {
        Captain captain = new Captain(1L, "Sara", "LIC001", 5.0);
        when(captainRepository.findById(1L)).thenReturn(Optional.of(captain));
        assertEquals("Sara", captainService.getCaptainById(1L).getName());
    }

    // -------------------- Controller --------------------
    @Test
    void testCaptainControllerGetAll() {
        CaptainService mockService = mock(CaptainService.class);
        List<Captain> sample = List.of(new Captain(1L, "Ali", "LIC123", 4.5));
        when(mockService.getAllCaptains()).thenReturn(sample);

        CaptainController controller = new CaptainController(mockService);
        ResponseEntity<List<Captain>> response = controller.getAllCaptains();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testCaptainControllerAdd() {
        CaptainService mockService = mock(CaptainService.class);
        Captain captain = new Captain(null, "Ali", "LIC123", 4.5);
        when(mockService.addCaptain(captain)).thenReturn(captain);

        CaptainController controller = new CaptainController(mockService);
        ResponseEntity<Captain> response = controller.addCaptain(captain);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Ali", response.getBody().getName());
    }
}
