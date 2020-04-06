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

package ch.bern.submiss.services.impl.administration;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Singleton;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.parsertokens.Token;

import com.eurodyn.qlack2.util.jsr.validator.util.ValidationError;

import ch.bern.submiss.services.api.util.LookupValues;
import ch.bern.submiss.services.api.util.ValidationMessages;

/**
 * The Class MathParserBean.
 */
@Singleton
public class MathParserBean {

  /**
   * The Constant LOGGER.
   */
  private static final Logger LOGGER = Logger.getLogger(MathParserBean.class.getName());


  /**
   * Calculate.
   *
   * @param argAi the arg ai
   * @param argAo the arg ao
   * @param expression the expression
   * @return the big decimal
   */
  public BigDecimal calculate(BigDecimal argAi, BigDecimal argAo, String expression) {

    LOGGER.log(Level.CONFIG, "Executing method calculate, Parameters: argAi: {0}, "
        + "argAo: {1}, expression: {2}",
      new Object[]{argAi, argAo, expression});

    Argument ai = new Argument("ai", argAi.doubleValue());
    Argument ao = new Argument("ao", argAo.doubleValue());
    Expression e = new Expression(expression, ai, ao);
    // If the result of the expression is not a numeric value (not a number) return null, else
    // return the Big Decimal value of the result.
    if (Double.isNaN(e.calculate())) {
      return null;
    } else {
      return BigDecimal.valueOf(e.calculate());
    }
  }

  /**
   * Proof if the given calculation formula is valid.
   *
   * @param calculationFormula the calculation formula
   * @return a set of the errors that have occurred in the formula, empty set if none
   */
  public Set<ValidationError> checkCalculationFormula(String calculationFormula) {

    LOGGER.log(Level.CONFIG,
      "Executing method checkCalculationFormula, Parameters: "
        + "calculationFormula: {0}",
      calculationFormula);

    Set<ValidationError> errors = new HashSet<>();
    Expression e = new Expression(calculationFormula);
    // first check if the syntax is correct
    if (!e.checkLexSyntax()) {
      // if not return error in syntax
      errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
        ValidationMessages.FORMULA_SYNTAX_ERROR_MESSAGE));
      errors.add(new ValidationError("formula", ValidationMessages.FORMULA_SYNTAX_ERROR_MESSAGE));
    }
    // then check the provided arguments
    List<Token> tokensList = e.getCopyOfInitialTokens();
    for (Token t : tokensList) {
      // for each argument (and not number or operator)
      // take its name and proof if it is one of the permitted
      if (t.tokenTypeId == Token.NOT_MATCHED && !t.tokenStr.equals("ai")
        && !t.tokenStr.equals("ao")) {
        // if not return error in provided arguments
        errors.add(new ValidationError(ValidationMessages.ERROR_FIELD,
          ValidationMessages.FORMULA_ARGUMENTS_ERROR_MESSAGE));
        errors.add(
          new ValidationError("formula", ValidationMessages.FORMULA_ARGUMENTS_ERROR_MESSAGE));
      }
    }
    return errors;
  }
}
