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

import java.sql.Timestamp;

/**
 * The Class SubcriterionDTO.
 */
public class SubcriterionDTO extends AbstractDTO {

  /**
   * The criterion.
   */
  private String criterion;

  /**
   * The subcriterion text.
   */
  private String subcriterionText;

  /**
   * The weighting.
   */
  private Double weighting;

  /**
   * The created on.
   */
  private Timestamp createdOn;

  /**
   * The updated on.
   */
  private Timestamp updatedOn;

  /**
   * The pageRequestedOn.
   */
  private Timestamp pageRequestedOn;

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

  /**
   * Gets the created on.
   *
   * @return the createdOn
   */
  public Timestamp getCreatedOn() {
    return createdOn;
  }

  /**
   * Sets the created on.
   *
   * @param createdOn the createdOn
   */
  public void setCreatedOn(Timestamp createdOn) {
    this.createdOn = createdOn;
  }

  /**
   * Gets the updated on.
   *
   * @return the updatedOn
   */
  public Timestamp getUpdatedOn() {
    return updatedOn;
  }

  /**
   * Sets the updated on.
   *
   * @param updatedOn the updatedOn
   */
  public void setUpdatedOn(Timestamp updatedOn) {
    this.updatedOn = updatedOn;
  }

  /**
   * Gets the pageRequestedOn.
   *
   * @return the pageRequestedOn
   */
  public Timestamp getPageRequestedOn() {
    return pageRequestedOn;
  }

  /**
   * Sets the pageRequestedOn.
   *
   * @param pageRequestedOn the pageRequestedOn
   */
  public void setPageRequestedOn(Timestamp pageRequestedOn) {
    this.pageRequestedOn = pageRequestedOn;
  }

  @Override
  public String toString() {
    return "SubcriterionDTO [id=" + super.getId() + ", version=" + super.getVersion()
      + ", criterion=" + criterion + ", subcriterionText="
      + subcriterionText + ", weighting=" + weighting
      + ", createdOn=" + createdOn + ", updatedOn=" + updatedOn
      + ", createdOn=" + createdOn + ", updatedOn="
      + updatedOn  + ", pageRequestedOn=" + pageRequestedOn + "]";
  }
}
