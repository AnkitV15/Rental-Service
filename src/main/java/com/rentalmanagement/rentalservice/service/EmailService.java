package com.rentalmanagement.rentalservice.service;

import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import com.rentalmanagement.rentalservice.model.Invoice;
import org.springframework.scheduling.annotation.Async;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Async
    public void sendTenantAccessEmail(String to, String magicLink) {
        log.info("Sending tenant access email to {} with magic link: {}", to, magicLink);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your Tenant Access Link");
        message.setText("Please use the following link to access your tenant portal: " + magicLink);
        message.setFrom(fromEmail);
        mailSender.send(message);
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        log.info("Inside Email Service - sendHtmlEmail(): {} {} {}", to, subject, htmlContent);
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
        mimeMessageHelper.setFrom(fromEmail);
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Async
    public void sendInvoiceCreatedEmail(String to, Invoice invoice) {
        try {
            String subject = "New Invoice Generated - " + invoice.getPeriodStart() + " to " + invoice.getPeriodEnd();
            String magicLink = frontendUrl + "/tenant/login?token=" + invoice.getLease().getAccessToken();

            String htmlContent = String.format(
                    """
                            <h1>New Invoice Generated</h1>
                            <p>Dear Tenant,</p>
                            <p>A new invoice has been generated for your unit.</p>
                            <ul>
                                <li><strong>Amount:</strong> $%.2f</li>
                                <li><strong>Due Date:</strong> %s</li>
                                <li><strong>Period:</strong> %s to %s</li>
                            </ul>
                            <p>
                                <a href="%s" style="padding: 10px 20px; background-color: #007bff; color: white; text-decoration: none; border-radius: 5px;">
                                    View & Pay Invoice
                                </a>
                            </p>
                            <p>Or click this link: <a href="%s">%s</a></p>
                            """,
                    invoice.getTotalAmount(), invoice.getDueDate(), invoice.getPeriodStart(),
                    invoice.getPeriodEnd(), magicLink, magicLink, magicLink);
            sendHtmlEmail(to, subject, htmlContent);
        } catch (MessagingException e) {
            log.error("Failed to send invoice email to {}", to, e);
        }
    }
}
