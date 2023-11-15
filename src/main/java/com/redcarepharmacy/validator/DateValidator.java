package com.redcarepharmacy.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidator implements ConstraintValidator<ValidDate, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        value = value.trim();
        try {
            LocalDate currentDate = LocalDate.now();
            LocalDate date = LocalDate.parse(value, DateTimeFormatter.ISO_DATE_TIME);
            if (date.isAfter(currentDate)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Date provided is in future")
                        .addConstraintViolation();
                return false;
            }
            return true;
        } catch (DateTimeParseException e) {
            if (!e.getMessage().contains("index")) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Date provided is not valid - " + e.getMessage())
                        .addConstraintViolation();
            }
            return false;
        }
    }
}
