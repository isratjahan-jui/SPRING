package com.MHM.MultiHotelManagement.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    private final RestTemplate restTemplate;

    public SslCommerzClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

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
        body.put("product_category", "travel");
        body.put("product_profile", "travel");
        body.put("cus_name", customerName);
        body.put("cus_email", customerEmail);
        body.put("cus_phone", customerPhone);
        body.put("cus_add1", "");
        body.put("cus_city", "");
        body.put("cus_postcode", "");
        body.put("cus_country", "Bangladesh");
        body.put("shipping_method", "NO");
        body.put("num_of_item", "1");

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            StringBuilder formBody = new StringBuilder();
            for (Map.Entry<String, String> entry : body.entrySet()) {
                if (formBody.length() > 0) formBody.append("&");
                formBody.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                formBody.append("=");
                formBody.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            }

            HttpEntity<String> request = new HttpEntity<>(formBody.toString(), headers);

            String url = sandbox ? "https://sandbox.sslcommerz.com/gwprocess/v4/api.php" : "https://securepay.sslcommerz.com/gwprocess/v4/api.php";
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

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
            // SSLCommerz's validation API is a GET endpoint with query params and
            // format=json — a POST returns a 500 SOAP fault.
            String base = sandbox
                    ? "https://sandbox.sslcommerz.com/validator/api/validationserverAPI.php"
                    : "https://securepay.sslcommerz.com/validator/api/validationserverAPI.php";
            String url = base
                    + "?val_id=" + java.net.URLEncoder.encode(valId, StandardCharsets.UTF_8)
                    + "&store_id=" + java.net.URLEncoder.encode(storeId, StandardCharsets.UTF_8)
                    + "&store_passwd=" + java.net.URLEncoder.encode(storePassword, StandardCharsets.UTF_8)
                    + "&v=1&format=json";

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

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

    // Verifies the SSLCommerz callback using its documented verify_sign algorithm:
    //   1. take only the params named in verify_key
    //   2. add store_passwd = md5(store_password)
    //   3. sort keys alphabetically, join as key=value&... (NOT url-encoded)
    //   4. md5 the string and compare (case-insensitive) to verify_sign
    public boolean verifyCallbackSignature(Map<String, String> params) {
        String verifySign = params.get("verify_sign");
        String verifyKey = params.get("verify_key");
        if (verifySign == null || verifyKey == null || storePassword == null || storePassword.isEmpty()) {
            log.warn("Missing verify_sign / verify_key or store password for signature verification");
            return false;
        }

        try {
            // Collect only the fields SSLCommerz used to build the signature (TreeMap = sorted)
            java.util.TreeMap<String, String> data = new java.util.TreeMap<>();
            for (String key : verifyKey.split(",")) {
                key = key.trim();
                if (key.isEmpty()) continue;
                data.put(key, params.getOrDefault(key, ""));
            }
            // Append the MD5 of the store password (per SSLCommerz spec)
            data.put("store_passwd", md5Hex(storePassword));

            StringBuilder hashString = new StringBuilder();
            for (Map.Entry<String, String> e : data.entrySet()) {
                if (hashString.length() > 0) hashString.append("&");
                hashString.append(e.getKey()).append("=").append(e.getValue());
            }

            String computedSign = md5Hex(hashString.toString());
            boolean valid = computedSign.equalsIgnoreCase(verifySign);
            if (!valid) {
                log.error("SSLCommerz verify_sign mismatch: expected={}, computed={}", verifySign, computedSign);
            }
            return valid;
        } catch (Exception e) {
            log.error("Signature verification failed: {}", e.getMessage(), e);
            return false;
        }
    }

    private String md5Hex(String input) throws java.security.NoSuchAlgorithmException {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hex = new StringBuilder();
        for (byte b : digest) {
            String h = Integer.toHexString(0xff & b);
            if (h.length() == 1) hex.append('0');
            hex.append(h);
        }
        return hex.toString();
    }
}
