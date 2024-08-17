package com.infinitynet.server.dtos.validates;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final String PHONE_NUMBER_PATTERN = "^[+]?[0-9]{10,13}$";

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext context) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false; // hoặc trả về true nếu bạn muốn cho phép số điện thoại rỗng
        }
        return Pattern.matches(PHONE_NUMBER_PATTERN, phoneNumber);
    }
}
