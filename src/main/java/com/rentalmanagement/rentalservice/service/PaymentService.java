package com.rentalmanagement.rentalservice.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.rentalmanagement.rentalservice.model.Invoice;
import com.rentalmanagement.rentalservice.repository.InvoiceRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    private RazorpayClient razorpayClient;

    private final InvoiceRepository invoiceRepository;

    @PostConstruct
    public void init() throws RazorpayException {
        // Initialize Razorpay Client
        // In production, ensure keys are valid. For dev, we might fail effectively if
        // keys are placeholders.
        if (!"YOUR_KEY_ID".equals(keyId)) {
            this.razorpayClient = new RazorpayClient(keyId, keySecret);
        }
    }

    public String createOrder(Long invoiceId) throws RazorpayException {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        Order order = createOrder(invoice.getTotalAmount(), "inv_" + invoice.getId());
        return order.toString();
    }

    public Order createOrder(Double amount, String receipt) throws RazorpayException {
        if (razorpayClient == null) {
            throw new RuntimeException("Razorpay keys are not configured properly.");
        }

        // Amount is in paisa (1 INR = 100 paisa)
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (amount * 100));
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", receipt);

        return razorpayClient.orders.create(orderRequest);
    }

    @Transactional
    public boolean verifyPayment(String orderId, String paymentId, String signature, Long invoiceId)
            throws RazorpayException {
        if (razorpayClient == null) {
            // Re-init attempt or throw
            this.razorpayClient = new RazorpayClient(keyId, keySecret);
        }

        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", orderId);
        options.put("razorpay_payment_id", paymentId);
        options.put("razorpay_signature", signature);

        boolean isValid = Utils.verifyPaymentSignature(options, keySecret);

        if (isValid) {
            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));

            invoice.setStatus("PAID");
            invoice.setPaymentDate(LocalDateTime.now());
            invoice.setPaymentId(paymentId);
            invoiceRepository.save(invoice);
            return true;
        }
        return false;
    }

    public boolean verifySignature(String orderId, String paymentId, String signature) throws RazorpayException {
        if (razorpayClient == null) {
            this.razorpayClient = new RazorpayClient(keyId, keySecret);
        }

        JSONObject options = new JSONObject();
        options.put("razorpay_order_id", orderId);
        options.put("razorpay_payment_id", paymentId);
        options.put("razorpay_signature", signature);

        return Utils.verifyPaymentSignature(options, keySecret);
    }
}
