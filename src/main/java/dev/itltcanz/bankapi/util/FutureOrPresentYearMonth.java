package dev.itltcanz.bankapi.util;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for validating that a YearMonth field or parameter is in the present or future.
 */
@Constraint(validatedBy = FutureOrPresentYearMonthValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureOrPresentYearMonth {

  /**
   * The error message to display when validation fails.
   *
   * @return The default error message.
   */
  String message() default "Validity period must be in the present or future";

  /**
   * The validation groups to which this constraint belongs.
   *
   * @return The validation groups.
   */
  Class<?>[] groups() default {};

  /**
   * The payload associated with this constraint.
   *
   * @return The payload classes.
   */
  Class<? extends Payload>[] payload() default {};
}