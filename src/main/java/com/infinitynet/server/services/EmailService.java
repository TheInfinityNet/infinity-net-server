package com.infinitynet.server.services;

import com.infinitynet.server.dtos.others.SendBrevoEmailDetails;
import com.infinitynet.server.dtos.responses.EmailResponse;

public interface EmailService {

    EmailResponse sendEmail(SendBrevoEmailDetails details, String token);

}