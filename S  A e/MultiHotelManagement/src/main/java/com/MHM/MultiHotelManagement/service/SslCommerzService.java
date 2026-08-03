package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.response.PaymentResponseDTO;

import java.util.Map;

public interface SslCommerzService {
    Map<String, Object> initiatePayment(Long bookingId);
    void handleSuccess(Map<String, String> params);
    void handleFail(Map<String, String> params);
    void handleCancel(Map<String, String> params);
    void handleIpn(Map<String, String> params);
}
