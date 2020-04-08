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

package ch.bern.submiss.services.impl.util;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;

/**
 * Contains utility methods for database operations.
 */
public class DBUtils {

  // match function template
  private static final String MATCH = "match({0},{1})";

  // The wildcard, indicating zero or more characters. It can only appear at the end of a word.
  private static final char STAR_WILDCARD = '*';
  private static final char PLUS_WILDCARD = '+';

  /**
   * Generates a begins with search expression.
   *
   * @param query the query to search for
   * @param path the column to search in
   * @return the expression
   */
  public static BooleanExpression getFullTextSearchExpression(String query, StringPath path) {
    return Expressions.stringTemplate(MATCH, path,
        PLUS_WILDCARD + query.replace("+", " ").replace("*", " ").replace("-", " ")
            .replace("(", " ").replace(")", " ").replace("()", " ").replace("<", " ").replace(">", " ").replace("~", " ")
            + STAR_WILDCARD)
        .gt("0");
  }
}
