package com.infinitynet.server.services;

import com.infinitynet.server.dtos.others.SendBrevoEmailDetails;

public interface EmailService {

    String sendEmail(SendBrevoEmailDetails details, String token, String code);

}