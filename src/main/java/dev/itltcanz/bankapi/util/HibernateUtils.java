package dev.itltcanz.bankapi.util;

import dev.itltcanz.bankapi.entity.Card;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import org.hibernate.Hibernate;

/**
 * Utility class for Hibernate-related operations, such as checking initialization and extracting
 * IDs.
 */
public final class HibernateUtils {

  /**
   * Checks if an object is non-null and initialized by Hibernate.
   *
   * @param obj The object to check.
   * @return {@code true} if the object is non-null and initialized; {@code false} otherwise.
   */
  public static boolean isSafe(Object obj) {
    return obj != null && Hibernate.isInitialized(obj);
  }

  /**
   * Retrieves the ID of an object as a string by invoking its getId method.
   *
   * @param obj The object whose ID is to be retrieved.
   * @return The ID as a string, or {@code null} if the object is not safe or has no getId method.
   */
  public static String getIdAsString(Object obj) {
    if (!isSafe(obj)) {
      return null;
    }
    try {
      Method getId = obj.getClass().getMethod("getId");
      UUID id = (UUID) getId.invoke(obj);
      return id != null ? id.toString() : null;
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
      return null;
    }
  }

  /**
   * Retrieves the card number from a Card object.
   *
   * @param card The Card object.
   * @return The card number as a string, or {@code null} if the card is not safe.
   */
  public static String getCardNumber(Card card) {
    return isSafe(card) ? card.getNumber() : null;
  }
}