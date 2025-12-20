package com.rentalmanagement.rentalservice.service;

import com.rentalmanagement.rentalservice.model.Owner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final EmailService emailService;

    @Value("${spring.application.url}")
    private String appbaseUrl;

    @Async
    public void sendVerificationEmail(Owner newOwner) {
        log.info("Inside NotificationService - sendVerificationEmail() for user: {} ", newOwner.getEmail());
        try {
            // Adding a small delay to simulate network latency for testing, can be removed
            Thread.sleep(4000);

            String link = appbaseUrl + "api/auth/verify-email?token=" + newOwner.getVerificationToken();
            String html = "<!DOCTYPE html>" +
                    "<html lang='en'>" +
                    "<head>" +
                    "  <meta charset='UTF-8'>" +
                    "  <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                    "  <title>Verify Your Email</title>" +
                    "</head>" +
                    "<body style='margin:0; padding:0; background-color:#f9fafb; font-family:Arial, sans-serif;'>" +
                    "  <table role='presentation' width='100%' cellspacing='0' cellpadding='0' style='background-color:#f9fafb; padding:40px 0;'>"
                    +
                    "    <tr>" +
                    "      <td align='center'>" +
                    "        <table role='presentation' width='600' cellspacing='0' cellpadding='0' style='background:#ffffff; border-radius:8px; box-shadow:0 2px 8px rgba(0,0,0,0.05);'>"
                    +
                    "          <tr>" +
                    "            <td style='padding:32px; text-align:center;'>" +
                    "              <h2 style='margin:0; font-size:24px; color:#111827;'>Verify Your Email</h2>" +
                    "              <p style='margin:16px 0; font-size:16px; color:#374151;'>Hi "
                    + newOwner.getUsername() + ",</p>" +
                    "              <p style='margin:0 0 24px 0; font-size:16px; color:#374151;'>Please confirm your email to activate your account.</p>"
                    +
                    "              <a href='" + link + "' " +
                    "                 style='display:inline-block; padding:12px 24px; background:#6366f1; color:#ffffff; font-size:16px; font-weight:bold; border-radius:6px; text-decoration:none;'>Verify Email</a>"
                    +
                    "              <p style='margin:24px 0 0 0; font-size:14px; color:#6b7280;'>Or copy this link into your browser:</p>"
                    +
                    "              <p style='margin:8px 0 0 0; font-size:14px; color:#2563eb; word-break:break-all;'>"
                    + link + "</p>" +
                    "            </td>" +
                    "          </tr>" +
                    "        </table>" +
                    "      </td>" +
                    "    </tr>" +
                    "  </table>" +
                    "</body>" +
                    "</html>";

            emailService.sendHtmlEmail(newOwner.getEmail(), "Verify your email", html);
            log.info("Verification email sent successfully to {}", newOwner.getEmail());
        } catch (Exception e) {
            log.error("Exception occurred at sendVerificationEmail(): {}", e.getMessage(), e);
            // In a real app, you might want to add retry logic or save the failed notification to a DB table
        }
    }
}
