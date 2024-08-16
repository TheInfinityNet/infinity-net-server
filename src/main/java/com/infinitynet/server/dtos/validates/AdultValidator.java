package com.infinitynet.server.dtos.validates;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.Period;

public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {

    @Override
    public void initialize(Adult constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate dateOfBirth, ConstraintValidatorContext context) {
        if (dateOfBirth == null) {
            return false; // hoặc bạn có thể trả về true nếu không muốn bắt buộc trường này
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears() >= 18;
    }
}
