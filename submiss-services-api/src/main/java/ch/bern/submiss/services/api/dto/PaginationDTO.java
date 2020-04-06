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

package ch.bern.submiss.services.api.dto;

import java.util.List;

/**
 * DTO for paginating all DTOs.
 *
 * @param <E> The DTO to be paginated.
 */
public class PaginationDTO<E extends Object> {

  /**
   * The count.
   */
  private long count;

  /**
   * The results.
   */
  private List<E> results;

  /**
   * Constructor for pagination DTO.
   *
   * @param count Count is number of objects to be paginated.
   * @param results Results is the list of objects which will be paginated.
   */
  public PaginationDTO(long count, List<E> results) {
    this.count = count;
    this.results = results;
  }

  /**
   * Gets the count.
   *
   * @return the count
   */
  public long getCount() {
    return count;
  }

  /**
   * Sets the count.
   *
   * @param count the new count
   */
  public void setCount(long count) {
    this.count = count;
  }

  /**
   * Gets the results.
   *
   * @return the results
   */
  public List<E> getResults() {
    return results;
  }

  /**
   * Sets the results.
   *
   * @param results the new results
   */
  public void setResults(List<E> results) {
    this.results = results;
  }

}
