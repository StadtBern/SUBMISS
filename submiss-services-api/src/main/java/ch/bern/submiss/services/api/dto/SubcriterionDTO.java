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

/**
 * The Class SubcriterionDTO.
 */
public class SubcriterionDTO {

  /** The id. */
  private String id;

  /** The criterion. */
  private String criterion;

  /** The subcriterion text. */
  private String subcriterionText;

  /** The weighting. */
  private Double weighting;

  /**
   * Gets the id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Sets the id.
   *
   * @param id the new id
   */
  public void setId(String id) {
    this.id = id;
  }

  /**
   * Gets the criterion.
   *
   * @return the criterion
   */
  public String getCriterion() {
    return criterion;
  }

  /**
   * Sets the criterion.
   *
   * @param criterion the new criterion
   */
  public void setCriterion(String criterion) {
    this.criterion = criterion;
  }

  /**
   * Gets the subcriterion text.
   *
   * @return the subcriterion text
   */
  public String getSubcriterionText() {
    return subcriterionText;
  }

  /**
   * Sets the subcriterion text.
   *
   * @param subcriterionText the new subcriterion text
   */
  public void setSubcriterionText(String subcriterionText) {
    this.subcriterionText = subcriterionText;
  }

  /**
   * Gets the weighting.
   *
   * @return the weighting
   */
  public Double getWeighting() {
    return weighting;
  }

  /**
   * Sets the weighting.
   *
   * @param weighting the new weighting
   */
  public void setWeighting(Double weighting) {
    this.weighting = weighting;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "SubcriterionDTO [id=" + id + ", criterion=" + criterion + ", subcriterionText="
        + subcriterionText + ", weighting=" + weighting + "]";
  }



}
