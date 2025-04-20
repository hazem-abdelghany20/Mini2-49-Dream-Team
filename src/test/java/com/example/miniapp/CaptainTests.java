package com.example.miniapp;

import com.example.miniapp.controllers.CaptainController;
import com.example.miniapp.models.Captain;
import com.example.miniapp.repositories.CaptainRepository;
import com.example.miniapp.services.CaptainService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import jakarta.persistence.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CaptainTests {

    /**
     * Model Tests
     */
    @Nested
    @DisplayName("Captain Model Tests")
    class CaptainModelTests {

        @Test
        @DisplayName("Test Captain Model Creation")
        void testCaptainCreation() {
            // Test default constructor (from @NoArgsConstructor)
            Captain captain1 = new Captain();
            assertNotNull(captain1);

            // Test full constructor (from @AllArgsConstructor)
            Captain captain3 = new Captain(1L, "Jane Williams", "LIC789012", 4.5);
            assertNotNull(captain3);
            assertEquals(1L, captain3.getId());
            assertEquals("Jane Williams", captain3.getName());
            assertEquals("LIC789012", captain3.getLicenseNumber());
            assertEquals(4.5, captain3.getAvgRatingScore());
        }

        @Test
        @DisplayName("Test Captain Getters and Setters")
        void testCaptainGettersSetters() {
            Captain captain = new Captain();

            captain.setId(1L);
            captain.setName("John Smith");
            captain.setLicenseNumber("LIC123456");
            captain.setAvgRatingScore(4.5);

            assertEquals(1L, captain.getId());
            assertEquals("John Smith", captain.getName());
            assertEquals("LIC123456", captain.getLicenseNumber());
            assertEquals(4.5, captain.getAvgRatingScore());
        }

        @Test
        @DisplayName("Test Captain toString Method")
        void testToString() {
            Captain captain = new Captain(1L, "John Smith", "LIC123456", 4.5);
            String toString = captain.toString();

            assertTrue(toString.contains("id=1"));
            assertTrue(toString.contains("name=John Smith"));
            assertTrue(toString.contains("licenseNumber=LIC123456"));
            assertTrue(toString.contains("avgRatingScore=4.5"));
        }

        @Test
        @DisplayName("Test Captain equals and hashCode Methods")
        void testEqualsAndHashCode() {
            Captain captain1 = new Captain(1L, "John Smith", "LIC123456", 4.5);
            Captain captain2 = new Captain(1L, "John Smith", "LIC123456", 4.5);
            Captain captain3 = new Captain(2L, "Jane Williams", "LIC789012", 3.8);

            // Test equals
            assertEquals(captain1, captain2);
            assertNotEquals(captain1, captain3);

            // Test hashCode
            assertEquals(captain1.hashCode(), captain2.hashCode());
            assertNotEquals(captain1.hashCode(), captain3.hashCode());
        }

        @Test
        @DisplayName("Test JPA Annotations")
        void testJpaAnnotations() throws NoSuchFieldException {
            // Verify @Entity annotation
            assertTrue(Captain.class.isAnnotationPresent(Entity.class));

            // Verify @Table annotation
            Table tableAnnotation = Captain.class.getAnnotation(Table.class);
            assertNotNull(tableAnnotation);
            assertEquals("captains", tableAnnotation.name());

            // Verify @Id annotation
            java.lang.reflect.Field idField = Captain.class.getDeclaredField("id");
            assertTrue(idField.isAnnotationPresent(Id.class));

            // Verify @GeneratedValue annotation
            GeneratedValue generatedValueAnnotation = idField.getAnnotation(GeneratedValue.class);
            assertNotNull(generatedValueAnnotation);
            assertEquals(GenerationType.IDENTITY, generatedValueAnnotation.strategy());

            // Verify @Column annotations
            java.lang.reflect.Field nameField = Captain.class.getDeclaredField("name");
            Column nameColumnAnnotation = nameField.getAnnotation(Column.class);
            assertNotNull(nameColumnAnnotation);
            assertTrue(nameColumnAnnotation.nullable() == false);

            // Verify other fields exist with correct capitalization (camelCase)
            assertDoesNotThrow(() -> Captain.class.getDeclaredField("licenseNumber"));
            assertDoesNotThrow(() -> Captain.class.getDeclaredField("avgRatingScore"));
        }
    }

    /**
     * Repository Tests
     */
    @Nested
    @DisplayName("Captain Repository Tests")
    @DataJpaTest
    class CaptainRepositoryTests {

        @Autowired
        private CaptainRepository captainRepository;

        private Captain captain1;
        private Captain captain2;

        @BeforeEach
        void setUp() {
            // Clear the repository
            captainRepository.deleteAll();

            // Create test captains
            captain1 = new Captain();
            captain1.setName("John Smith");
            captain1.setLicenseNumber("LIC123456");
            captain1.setAvgRatingScore(4.5);

            captain2 = new Captain();
            captain2.setName("Jane Williams");
            captain2.setLicenseNumber("LIC789012");
            captain2.setAvgRatingScore(3.8);

            // Save captains to repository
            captainRepository.save(captain1);
            captainRepository.save(captain2);
        }

        @Test
        @DisplayName("Test Find All Captains")
        void testFindAll() {
            List<Captain> captains = captainRepository.findAll();
            assertEquals(2, captains.size());
        }

        @Test
        @DisplayName("Test Find Captain By ID")
        void testFindById() {
            // Find captain by ID
            Optional<Captain> foundCaptain = captainRepository.findById(captain1.getId());
            assertTrue(foundCaptain.isPresent());
            assertEquals("John Smith", foundCaptain.get().getName());
        }

        @Test
        @DisplayName("Test Find Captains By Rating")
        void testFindByAvgRatingScoreGreaterThan() {
            List<Captain> highRatedCaptains = captainRepository.findByAvgRatingScoreGreaterThan(4.0);
            assertEquals(1, highRatedCaptains.size());
            assertEquals("John Smith", highRatedCaptains.get(0).getName());

            List<Captain> allCaptains = captainRepository.findByAvgRatingScoreGreaterThan(3.0);
            assertEquals(2, allCaptains.size());
        }

        @Test
        @DisplayName("Test Find Captain By License Number")
        void testFindByLicenseNumber() {
            Optional<Captain> foundCaptain = captainRepository.findByLicenseNumber("LIC123456");
            assertTrue(foundCaptain.isPresent());
            assertEquals("John Smith", foundCaptain.get().getName());

            Optional<Captain> notFoundCaptain = captainRepository.findByLicenseNumber("NONEXISTENT");
            assertFalse(notFoundCaptain.isPresent());
        }
    }

    /**
     * Service Tests
     */
    @Nested
    @DisplayName("Captain Service Tests")
    class CaptainServiceTests {

        @Mock
        private CaptainRepository captainRepository;

        @InjectMocks
        private CaptainService captainService;

        private Captain captain1;
        private Captain captain2;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);

            captain1 = new Captain();
            captain1.setId(1L);
            captain1.setName("John Smith");
            captain1.setLicenseNumber("LIC123456");
            captain1.setAvgRatingScore(4.5);

            captain2 = new Captain();
            captain2.setId(2L);
            captain2.setName("Jane Williams");
            captain2.setLicenseNumber("LIC789012");
            captain2.setAvgRatingScore(3.8);
        }

        @Test
        @DisplayName("Test Add Captain")
        void testAddCaptain() {
            when(captainRepository.save(any(Captain.class))).thenReturn(captain1);

            Captain result = captainService.addCaptain(captain1);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(captainRepository, times(1)).save(any(Captain.class));
        }

        @Test
        @DisplayName("Test Get All Captains")
        void testGetAllCaptains() {
            when(captainRepository.findAll()).thenReturn(Arrays.asList(captain1, captain2));

            List<Captain> result = captainService.getAllCaptains();

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(captainRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Test Get Captain By ID - Found")
        void testGetCaptainByIdFound() {
            when(captainRepository.findById(1L)).thenReturn(Optional.of(captain1));

            Captain result = captainService.getCaptainById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(captainRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Test Get Captain By ID - Not Found")
        void testGetCaptainByIdNotFound() {
            when(captainRepository.findById(3L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                captainService.getCaptainById(3L);
            });

            verify(captainRepository, times(1)).findById(3L);
        }

        @Test
        @DisplayName("Test Get Captains By Rating")
        void testGetCaptainsByRating() {
            Double ratingThreshold = 4.0;
            when(captainRepository.findByAvgRatingScoreGreaterThan(ratingThreshold))
                    .thenReturn(List.of(captain1));

            List<Captain> result = captainService.getCaptainsByRating(ratingThreshold);

            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(4.5, result.get(0).getAvgRatingScore());
            verify(captainRepository, times(1)).findByAvgRatingScoreGreaterThan(ratingThreshold);
        }

        @Test
        @DisplayName("Test Get Captain By License Number - Found")
        void testGetCaptainByLicenseNumberFound() {
            String licenseNumber = "LIC123456";
            when(captainRepository.findByLicenseNumber(licenseNumber))
                    .thenReturn(Optional.of(captain1));

            Captain result = captainService.getCaptainByLicenseNumber(licenseNumber);

            assertNotNull(result);
            assertEquals(licenseNumber, result.getLicenseNumber());
            assertEquals(captain1.getName(), result.getName());
            verify(captainRepository, times(1)).findByLicenseNumber(licenseNumber);
        }

        @Test
        @DisplayName("Test Get Captain By License Number - Not Found")
        void testGetCaptainByLicenseNumberNotFound() {
            String licenseNumber = "NONEXISTENT";
            when(captainRepository.findByLicenseNumber(licenseNumber))
                    .thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                captainService.getCaptainByLicenseNumber(licenseNumber);
            });

            verify(captainRepository, times(1)).findByLicenseNumber(licenseNumber);
        }
    }

    /**
     * Controller Tests
     */
    @Nested
    @DisplayName("Captain Controller Tests")
    @WebMvcTest(CaptainController.class)
    class CaptainControllerTests {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CaptainService captainService;

        @Autowired
        private ObjectMapper objectMapper;

        private Captain captain1;
        private Captain captain2;

        @BeforeEach
        void setUp() {
            captain1 = new Captain();
            captain1.setId(1L);
            captain1.setName("John Smith");
            captain1.setLicenseNumber("LIC123456");
            captain1.setAvgRatingScore(4.5);

            captain2 = new Captain();
            captain2.setId(2L);
            captain2.setName("Jane Williams");
            captain2.setLicenseNumber("LIC789012");
            captain2.setAvgRatingScore(3.8);
        }

        @Test
        @DisplayName("Test Add Captain Endpoint")
        void testAddCaptain() throws Exception {
            when(captainService.addCaptain(any(Captain.class))).thenReturn(captain1);

            mockMvc.perform(post("/captain/addCaptain")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(captain1)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Smith")))
                    .andExpect(jsonPath("$.licenseNumber", is("LIC123456")))
                    .andExpect(jsonPath("$.avgRatingScore", is(4.5)));

            verify(captainService, times(1)).addCaptain(any(Captain.class));
        }

        @Test
        @DisplayName("Test Get All Captains Endpoint")
        void testGetAllCaptains() throws Exception {
            when(captainService.getAllCaptains()).thenReturn(Arrays.asList(captain1, captain2));

            mockMvc.perform(get("/captain/allCaptains"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].name", is("John Smith")))
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[1].name", is("Jane Williams")));

            verify(captainService, times(1)).getAllCaptains();
        }

        @Test
        @DisplayName("Test Get Captain By ID Endpoint")
        void testGetCaptainById() throws Exception {
            when(captainService.getCaptainById(1L)).thenReturn(captain1);

            mockMvc.perform(get("/captain/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Smith")));

            verify(captainService, times(1)).getCaptainById(1L);
        }

        @Test
        @DisplayName("Test Filter Captains By Rating Endpoint")
        void testGetCaptainsByRating() throws Exception {
            Double ratingThreshold = 4.0;
            when(captainService.getCaptainsByRating(ratingThreshold)).thenReturn(List.of(captain1));

            mockMvc.perform(get("/captain/filterByRating")
                            .param("ratingThreshold", String.valueOf(ratingThreshold)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].avgRatingScore", is(4.5)));

            verify(captainService, times(1)).getCaptainsByRating(ratingThreshold);
        }

        @Test
        @DisplayName("Test Filter Captain By License Number Endpoint")
        void testGetCaptainByLicenseNumber() throws Exception {
            String licenseNumber = "LIC123456";
            when(captainService.getCaptainByLicenseNumber(licenseNumber)).thenReturn(captain1);

            mockMvc.perform(get("/captain/filterByLicenseNumber")
                            .param("licenseNumber", licenseNumber))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.licenseNumber", is(licenseNumber)));

            verify(captainService, times(1)).getCaptainByLicenseNumber(licenseNumber);
        }
    }
}