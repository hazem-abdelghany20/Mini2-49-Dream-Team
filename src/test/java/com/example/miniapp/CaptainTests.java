package com.example.miniapp;

import com.example.miniapp.controllers.CaptainController;
import com.example.miniapp.models.Captain;
import com.example.miniapp.repositories.CaptainRepository;
import com.example.miniapp.services.CaptainService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@SpringBootTest
@AutoConfigureTestDatabase
public class CaptainTests {

    @Autowired
    private CaptainRepository captainRepository;

    @Autowired
    private CaptainService captainService;

    // For controller tests
    private MockMvc mockMvc;

    @Mock
    private CaptainService mockService;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        // Clean DB
        captainRepository.deleteAll();
        // Setup standalone controller with mock service
        mockMvc = MockMvcBuilders.standaloneSetup(new CaptainController(mockService)).build();
    }

    // ---------- Model Layer Tests ----------
    @Test
    void testCaptainModel() {
        Captain c = new Captain();
        c.setName("Zeta");
        c.setLicenseNumber("ZINC");
        c.setAvgRatingScore(5.0);
        assertNull(c.getId());
        assertEquals("Zeta", c.getName());
        assertEquals("ZINC", c.getLicenseNumber());
        assertEquals(5.0, c.getAvgRatingScore());

        Captain c2 = new Captain(10L, "Theta", "LICT", 4.4);
        assertEquals(10L, c2.getId());
        assertEquals("Theta", c2.getName());
        assertEquals("LICT", c2.getLicenseNumber());
        assertEquals(4.4, c2.getAvgRatingScore());
    }

    // ---------- Repository Layer Tests ----------
    @Test
    void testSaveAndFindAllCaptains() {
        Captain c1 = new Captain(null, "Alpha", "LN1", 3.0);
        Captain c2 = new Captain(null, "Beta",  "LN2", 4.0);
        captainRepository.save(c1);
        captainRepository.save(c2);
        List<Captain> all = captainRepository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void testFindByAvgRatingScoreGreaterThan() {
        captainRepository.save(new Captain(null, "Low", "L1", 2.0));
        captainRepository.save(new Captain(null, "High", "L2", 4.5));
        List<Captain> result = captainRepository.findByAvgRatingScoreGreaterThan(3.0);
        assertEquals(1, result.size());
        assertEquals("High", result.get(0).getName());
    }

    @Test
    void testFindByLicenseNumberExists() {
        captainRepository.save(new Captain(null, "Gamma", "LIC100", 3.3));
        Optional<Captain> result = captainRepository.findByLicenseNumber("LIC100");
        assertTrue(result.isPresent());
        assertEquals("Gamma", result.get().getName());
    }

    @Test
    void testFindByLicenseNumberNotFound() {
        Optional<Captain> result = captainRepository.findByLicenseNumber("NOPE");
        assertTrue(result.isEmpty());
    }

    // ---------- Service Layer Tests (Integration against real DB) ----------
    @Test
    void testAddAndGetCaptainsViaService() {
        Captain c = new Captain(null, "ServiceTest", "SLIC", 2.5);
        Captain saved = captainService.addCaptain(c);
        assertNotNull(saved.getId());

        List<Captain> all = captainService.getAllCaptains();
        assertEquals(1, all.size());

        Captain found = captainService.getCaptainById(saved.getId());
        assertEquals("ServiceTest", found.getName());
    }

    @Test
    void testServiceGetCaptainsByRating() {
        captainService.addCaptain(new Captain(null, "LowRate", "LR", 1.0));
        captainService.addCaptain(new Captain(null, "HighRate", "HR", 4.9));
        List<Captain> filtered = captainService.getCaptainsByRating(3.0);
        assertEquals(1, filtered.size());
        assertEquals("HighRate", filtered.get(0).getName());
    }

    @Test
    void testServiceGetCaptainByLicenseNumber() {
        captainService.addCaptain(new Captain(null, "LicTest", "LIC999", 3.3));
        Captain byLic = captainService.getCaptainByLicenseNumber("LIC999");
        assertEquals("LicTest", byLic.getName());
    }

    @Test
    void testServiceNotFoundExceptions() {
        assertThrows(RuntimeException.class, () -> captainService.getCaptainById(999L));
        assertThrows(RuntimeException.class, () -> captainService.getCaptainByLicenseNumber("UNKNOWN"));
    }

    // ---------- Controller Layer Tests ----------
    @Test
    void controller_getAllCaptains() throws Exception {
        List<Captain> stubList = Arrays.asList(
                new Captain(1L, "Alpha", "LN1", 3.5),
                new Captain(2L, "Beta",  "LN2", 4.5)
        );
        when(mockService.getAllCaptains()).thenReturn(stubList);

        mockMvc.perform(get("/captain/allCaptains"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Alpha"));
    }

    @Test
    void controller_addCaptain() throws Exception {
        Captain input = new Captain(null, "Gamma", "LN3", 2.5);
        Captain returned = new Captain(3L, "Gamma", "LN3", 2.5);
        when(mockService.addCaptain(any(Captain.class))).thenReturn(returned);

        mockMvc.perform(post("/captain/addCaptain")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.licenseNumber").value("LN3"));
    }

    @Test
    void controller_getCaptainById() throws Exception {
        Captain c = new Captain(1L, "Delta", "LN4", 4.2);
        when(mockService.getCaptainById(1L)).thenReturn(c);

        mockMvc.perform(get("/captain/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Delta"));
    }

    @Test
    void controller_filterByRating() throws Exception {
        List<Captain> stub = Arrays.asList(
                new Captain(2L, "High", "LN2", 4.8)
        );
        when(mockService.getCaptainsByRating(4.0)).thenReturn(stub);

        mockMvc.perform(get("/captain/filterByRating")
                        .param("ratingThreshold", "4.0")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("High"));
    }

    @Test
    void controller_filterByLicenseNumber() throws Exception {
        Captain c = new Captain(1L, "Delta", "LIC123", 3.3);
        when(mockService.getCaptainByLicenseNumber("LIC123")).thenReturn(c);

        mockMvc.perform(get("/captain/filterByLicenseNumber")
                        .param("licenseNumber", "LIC123")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Delta"));
    }
}
