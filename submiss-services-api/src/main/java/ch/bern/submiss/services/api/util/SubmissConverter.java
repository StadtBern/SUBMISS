/*
 *  Submiss, eProcurement system for managing tenders
 *  Copyright (C) 2019 Stadt Bern
 *  Licensed under the EUPL, Version 1.2 or - as soon as they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence");
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at:
 *  https://joinup.ec.europa.eu/collection/eupl
 *  Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 *  distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the Licence for the specific language governing permissions and limitations
 *  under the Licence.
 */

package ch.bern.submiss.services.api.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The Class SubmissConverter.
 */
public class SubmissConverter {

  private static final String AMPERSAND_REGEX = "&(?![a-zA-Z][a-zA-Z][a-zA-Z]?[a-zA-Z]?;)";

  private static final String LT_REGEX = "<(?![a-zA-Z][a-zA-Z][a-zA-Z]?[a-zA-Z]?;)";

  private static final String GT_REGEX = ">(?![a-zA-Z][a-zA-Z][a-zA-Z]?[a-zA-Z]?;)";

  private static final String QUOT_REGEX = "\"(?![a-zA-Z][a-zA-Z][a-zA-Z]?[a-zA-Z]?;)";

  /**
   * Instantiates a new SubmissConverter.
   */
  private SubmissConverter() {
    throw new IllegalStateException(
      "Utility classes, which are collections of static members, are not meant to be instantiated.");
  }

  /**
   * This method is used in order to convert a BigDecimal to Swiss currency (CHF).
   *
   * @param value the value
   * @return the string
   */
  public static String convertToCHFCurrency(BigDecimal value) {
    Currency c = Currency.getInstance("CHF");
    DecimalFormat formatter = currencyConversion(c);
    return "Fr.".concat(" ").concat(formatter.format(value).replace(",", ".").substring(0,
      formatter.format(value).length() - 4));
  }

  /**
   * This method is used in order to convert a BigDecimal to Swiss currency (CHF) without the
   * currency symbol.
   *
   * @param value the value
   * @return the string
   */
  public static String convertToCHFCurrencyWithoutSymbol(BigDecimal value) {
    Currency c = Currency.getInstance("CHF");
    DecimalFormat formatter = currencyConversion(c);
    return formatter.format(value).replace(",", ".").substring(0,
      formatter.format(value).length() - 4);
  }

  /**
   * Currency conversion.
   *
   * @param c the c
   * @return the decimal format
   */
  private static DecimalFormat currencyConversion(Currency c) {
    DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.GERMANY);
    formatter.setCurrency(c);
    DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
    symbols.setDecimalSeparator('.');
    symbols.setGroupingSeparator('\'');
    formatter.setDecimalFormatSymbols(symbols);
    return formatter;
  }

  /**
   * This method is used in order to convert to Swiss date representation.
   *
   * @param value the value
   * @return the string
   */
  public static String convertToSwissDate(Date value) {
    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
    Date date = new Timestamp(value.getTime());
    return df.format(date);
  }


  /**
   * This method is used to replace the ampersand when a document is generated.
   *
   * @param word the word
   * @return the string
   */
  public static String replaceAmpersand(String word) {
    return word.replaceAll(AMPERSAND_REGEX, "&amp;");
  }

  /**
   * This method is used to replace the special characters when a document is generated.
   *
   * @param placeholders the placeholders
   * @return the map
   */
  public static Map<String, String> replaceSpecialCharactersInPlaceholders(
    Map<String, String> placeholders) {
    for (Entry<String, String> entry : placeholders.entrySet()) {
      if (entry.getValue() != null && entry.getValue().contains("&")) {
        placeholders.replace(entry.getKey(), entry.getValue().replaceAll(AMPERSAND_REGEX, "&amp;"));
      }
      if (entry.getValue() != null && entry.getValue().contains("<")) {
        placeholders.replace(entry.getKey(), entry.getValue().replaceAll(LT_REGEX, "&lt;"));
      }
      if (entry.getValue() != null && entry.getValue().contains(">")) {
        placeholders.replace(entry.getKey(), entry.getValue().replaceAll(GT_REGEX, "&gt;"));
      }
      if (entry.getValue() != null && entry.getValue().contains("\"")) {
        placeholders.replace(entry.getKey(), entry.getValue().replaceAll(QUOT_REGEX, "&quot;"));
      }
    }
    return placeholders;
  }

  /**
   * Function to implement the swiss number rounding standard ("Rappenrundung") for BigDecimal
   */
  public static BigDecimal customRoundNumber(BigDecimal num) {
    if (num == null || num == BigDecimal.ZERO) {
      return BigDecimal.valueOf(0.00);
    }
    double tenNum = num.doubleValue() * 10;
    double numWithOneDecimal = Double.valueOf((long) tenNum) / 10;
    double diff = num.doubleValue() - numWithOneDecimal;
    if (diff < 0.025) {
      return BigDecimal.valueOf(numWithOneDecimal);
    } else if (diff >= 0.025 && diff < 0.075) {
      return BigDecimal.valueOf(numWithOneDecimal + 0.05);
    } else {
      return BigDecimal.valueOf(numWithOneDecimal + 0.1);
    }
  }

}
