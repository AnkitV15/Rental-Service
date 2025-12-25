package com.rentalmanagement.rentalservice.controller;

import com.rentalmanagement.rentalservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order/{invoiceId}")
    public ResponseEntity<?> createOrder(@PathVariable Long invoiceId) {
        try {
            String order = paymentService.createOrder(invoiceId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, Object> data) {
        try {
            String orderId = (String) data.get("razorpay_order_id");
            String paymentId = (String) data.get("razorpay_payment_id");
            String signature = (String) data.get("razorpay_signature");

            // invoiceId should be passed from frontend or derived from receipt if needed,
            // but for simplicity let's pass it in body
            Long invoiceId = Long.valueOf(data.get("invoiceId").toString());

            boolean isValid = paymentService.verifyPayment(orderId, paymentId, signature, invoiceId);

            if (isValid) {
                return ResponseEntity.ok("Payment verified and Invoice updated.");
            } else {
                return ResponseEntity.badRequest().body("Payment verification failed.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
