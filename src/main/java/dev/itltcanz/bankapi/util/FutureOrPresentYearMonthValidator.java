package dev.itltcanz.bankapi.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.YearMonth;

/**
 * Validator for the {@link FutureOrPresentYearMonth} annotation, ensuring a YearMonth is in the
 * present or future.
 */
public class FutureOrPresentYearMonthValidator implements
    ConstraintValidator<FutureOrPresentYearMonth, YearMonth> {

  /**
   * Initializes the validator with the constraint annotation.
   *
   * @param constraintAnnotation The {@link FutureOrPresentYearMonth} annotation instance.
   */
  @Override
  public void initialize(FutureOrPresentYearMonth constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  /**
   * Validates that the provided YearMonth is in the present or future.
   *
   * @param yearMonth                  The YearMonth to validate.
   * @param constraintValidatorContext The validation context.
   * @return {@code true} if the YearMonth is null or in the present/future; {@code false}
   * otherwise.
   */
  @Override
  public boolean isValid(YearMonth yearMonth,
      ConstraintValidatorContext constraintValidatorContext) {
    if (yearMonth == null) {
      return true;
    }
    return !yearMonth.isBefore(YearMonth.now());
  }
}
