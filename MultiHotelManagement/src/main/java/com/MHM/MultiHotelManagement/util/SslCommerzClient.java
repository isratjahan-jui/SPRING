package com.MHM.MultiHotelManagement.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class SslCommerzClient {

    @Value("${sslcommerz.store-id:}")
    private String storeId;

    @Value("${sslcommerz.store-password:}")
    private String storePassword;

    @Value("${sslcommerz.sandbox:true}")
    private boolean sandbox;

    @Value("${sslcommerz.api-url:}")
    private String apiUrl;

    @Value("${sslcommerz.validation-url:}")
    private String validationUrl;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${app.backend-url:http://localhost:8085}")
    private String backendUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> initiateSession(String transactionId, BigDecimal amount,
                                                String currency, String customerName,
                                                String customerEmail, String customerPhone,
                                                String productName, Long bookingId) {
        String successUrl = backendUrl + "/api/payments/sslcommerz/success";
        String failUrl = backendUrl + "/api/payments/sslcommerz/fail";
        String cancelUrl = backendUrl + "/api/payments/sslcommerz/cancel";
        String ipnUrl = backendUrl + "/api/payments/sslcommerz/ipn";

        Map<String, String> body = new HashMap<>();
        body.put("store_id", storeId);
        body.put("store_passwd", storePassword);
        body.put("total_amount", amount.toPlainString());
        body.put("currency", currency);
        body.put("tran_id", transactionId);
        body.put("success_url", successUrl);
        body.put("fail_url", failUrl);
        body.put("cancel_url", cancelUrl);
        body.put("ipn_url", ipnUrl);
        body.put("product_name", productName);
        body.put("product_category", "Hotel Booking");
        body.put("product_profile", "non-express-goods");
        body.put("cus_name", customerName);
        body.put("cus_email", customerEmail);
        body.put("cus_phone", customerPhone);
        body.put("cus_add1", "");
        body.put("cus_city", "");
        body.put("cus_postcode", "");
        body.put("cus_country", "Bangladesh");
        body.put("shipping_method", "NO");
        body.put("num_of_item", "1");
        body.put("emitter", "NO");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            StringBuilder formBody = new StringBuilder();
            for (Map.Entry<String, String> entry : body.entrySet()) {
                if (formBody.length() > 0) formBody.append("&");
                formBody.append(entry.getKey()).append("=").append(entry.getValue());
            }

            HttpEntity<String> request = new HttpEntity<>(formBody.toString(), headers);

            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, request, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && "SUCCESS".equals(responseBody.get("status"))) {
                Map<String, Object> result = new HashMap<>();
                result.put("gatewayPageUrl", responseBody.get("GatewayPageURL"));
                result.put("transactionId", transactionId);
                return result;
            } else {
                log.error("SSLCommerz session creation failed: {}", responseBody);
                throw new RuntimeException("Payment gateway initialization failed");
            }
        } catch (Exception e) {
            log.error("SSLCommerz API error", e);
            throw new RuntimeException("Payment gateway error: " + e.getMessage());
        }
    }

    public Map<String, Object> validateTransaction(String valId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String formBody = "val_id=" + valId
                    + "&store_id=" + storeId
                    + "&store_passwd=" + storePassword;

            HttpEntity<String> request = new HttpEntity<>(formBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    validationUrl, HttpMethod.POST, request, Map.class);

            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null) {
                return responseBody;
            }
            throw new RuntimeException("Empty validation response");
        } catch (Exception e) {
            log.error("SSLCommerz validation error", e);
            throw new RuntimeException("Payment validation error: " + e.getMessage());
        }
    }
}
