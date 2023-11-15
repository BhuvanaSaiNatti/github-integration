package com.redcarepharmacy.validator;

import com.redcarepharmacy.model.Language;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;

public class LanguageValidator implements ConstraintValidator<ValidLanguage, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid;
        if (value == null) {
            isValid = true;
        } else {
            isValid = Language.getLanguageIfValid(value.trim()) != null;
        }
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Language not supported. Accepted values for language are "
                            + Arrays.stream(Language.values()).collect(Collectors.toSet()))
                    .addConstraintViolation();
        }
        return isValid;
    }
}
