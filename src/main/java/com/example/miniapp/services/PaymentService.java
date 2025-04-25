package com.example.miniapp.services;

import com.example.miniapp.models.Payment;
import com.example.miniapp.models.Trip;
import com.example.miniapp.repositories.PaymentRepository;
import com.example.miniapp.repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TripRepository tripRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, TripRepository tripRepository) {
        this.paymentRepository = paymentRepository;
        this.tripRepository = tripRepository;
    }


    @Transactional
    public Payment addPayment(Payment payment) {
        if (payment == null) {
            return null;
        }

        // Check if trip exists
        if (payment.getTrip() != null) {
            Trip trip = tripRepository.findById(payment.getTrip().getId()).orElse(null);
            // Set the trip and save the payment
            payment.setTrip(trip);
        }

        return paymentRepository.save(payment);
    }


    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }





    @Transactional
    public Payment updatePayment(Long id, Payment payment) {
        if (payment == null) {
            return null;
        }


        Payment existingPayment = paymentRepository.findById(id).orElse(null);


        if (existingPayment == null) {
            return null;
        }

        // Update fields
        if(payment.getAmount() != null)
            existingPayment.setAmount(payment.getAmount());
        if(payment.getPaymentMethod() != null)
            existingPayment.setPaymentMethod(payment.getPaymentMethod());
        if(payment.getPaymentStatus() != null)
            existingPayment.setPaymentStatus(payment.getPaymentStatus());




        return paymentRepository.save(existingPayment);
    }


    @Transactional
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    public List<Payment> findByAmountThreshold(Double threshold) {
        return paymentRepository.findByAmountGreaterThan(threshold);
    }
    public List<Payment> findPaymentsByTripId(Long tripId) {
        return paymentRepository.findByTripId(tripId);
    }



}