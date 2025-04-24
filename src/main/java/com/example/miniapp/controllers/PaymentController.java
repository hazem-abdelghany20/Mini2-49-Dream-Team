package com.example.miniapp.controllers;

import com.example.miniapp.models.Payment;
import com.example.miniapp.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RESTful controller for Payment-related operations
 */
@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Add a new payment
     * @param payment The payment object to be added
     * @return The saved payment with generated ID
     */
    @PostMapping("/addPayment")
    public ResponseEntity<Payment> addPayment(@RequestBody Payment payment) {
        try {
            Payment savedPayment = paymentService.addPayment(payment);
            return new ResponseEntity<>(savedPayment, HttpStatus.OK);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all payment records
     * @return List of all payment records
     */
    @GetMapping("/allPayments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }

    /**
     * Get a specific payment by ID
     * @param id The ID of the payment to retrieve
     * @return The payment with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            return new ResponseEntity<>(payment, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Update payment details
     * @param id The ID of the payment to update
     * @param payment The updated payment details
     * @return The updated payment
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment payment) {
        try {
            Payment updatedPayment = paymentService.updatePayment(id, payment);
            return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Delete a payment
     * @param id The ID of the payment to delete
     * @return A confirmation message
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deletePayment(@PathVariable Long id) {
        try {
            paymentService.deletePayment(id);
            return new ResponseEntity<>("Payment with ID: " + id + " has been deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Payment with ID: " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Find payments by trip ID
     * @param tripId The ID of the trip to find payments for
     * @return List of payments associated with the specified trip
     */
    @GetMapping("/findByTripId")
    public ResponseEntity<List<Payment>> findPaymentsByTripId(@RequestParam Long tripId) {
        try {
            List<Payment> payments = paymentService.findPaymentsByTripId(tripId);
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Find payments with an amount greater than a threshold
     * @param threshold The minimum amount threshold
     * @return List of payments with amounts exceeding the threshold
     */
    @GetMapping("/findByAmountThreshold")
    public ResponseEntity<List<Payment>> findPaymentsWithAmountGreaterThan(@RequestParam Double threshold) {
        try {
            List<Payment> payments = paymentService.findByAmountThreshold(threshold);
            return new ResponseEntity<>(payments, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}