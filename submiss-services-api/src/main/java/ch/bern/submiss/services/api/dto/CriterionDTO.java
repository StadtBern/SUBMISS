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
 * The Class CriterionDTO.
 */
public class CriterionDTO {

  /** The id. */
  private String id;

  /** The submission. */
  private String submission;

  /** The criterion text. */
  private String criterionText;

  /** The weighting. */
  private Double weighting;

  /** The criterion type. */
  private String criterionType;

  /** The subcriterion. */
  private List<SubcriterionDTO> subcriterion;

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
   * Gets the submission.
   *
   * @return the submission
   */
  public String getSubmission() {
    return submission;
  }

  /**
   * Sets the submission.
   *
   * @param submission the new submission
   */
  public void setSubmission(String submission) {
    this.submission = submission;
  }

  /**
   * Gets the criterion text.
   *
   * @return the criterion text
   */
  public String getCriterionText() {
    return criterionText;
  }

  /**
   * Sets the criterion text.
   *
   * @param criterionText the new criterion text
   */
  public void setCriterionText(String criterionText) {
    this.criterionText = criterionText;
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
   * Gets the criterion type.
   *
   * @return the criterion type
   */
  public String getCriterionType() {
    return criterionType;
  }

  /**
   * Sets the criterion type.
   *
   * @param criterionType the new criterion type
   */
  public void setCriterionType(String criterionType) {
    this.criterionType = criterionType;
  }

  /**
   * Gets the subcriterion.
   *
   * @return the subcriterion
   */
  public List<SubcriterionDTO> getSubcriterion() {
    return subcriterion;
  }

  /**
   * Sets the subcriterion.
   *
   * @param subcriterion the new subcriterion
   */
  public void setSubcriterion(List<SubcriterionDTO> subcriterion) {
    this.subcriterion = subcriterion;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "CriterionDTO [id=" + id + ", submission=" + submission + ", criterionText="
        + criterionText + ", weighting=" + weighting + ", criterionType=" + criterionType + "]";
  }

}
