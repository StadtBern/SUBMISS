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

package ch.bern.submiss.services.impl.model;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * The Class CriterionEntity.
 */
@Entity
@Table(name = "SUB_CRITERION")
public class CriterionEntity {

  /** The id. */
  @Id
  @GeneratedValue(generator = "uuid1")
  @GenericGenerator(name = "uuid1", strategy = "uuid2")
  private String id;

  /** The submission. */
  @ManyToOne
  @JoinColumn(name = "FK_TENDER")
  private SubmissionEntity submission;

  /** The criterion text. */
  @Column(name = "CRITERION_TEXT")
  private String criterionText;

  /** The weighting. */
  @Column(name = "WEIGHTING")
  private Double weighting;

  /** The criterion type. */
  @Column(name = "CRITERION_TYPE")
  private String criterionType;

  /** The subcriteria. */
  @OneToMany(mappedBy = "criterion", cascade = CascadeType.ALL)
  private List<SubcriterionEntity> subcriteria;

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
  public SubmissionEntity getSubmission() {
    return submission;
  }

  /**
   * Sets the submission.
   *
   * @param submission the new submission
   */
  public void setSubmission(SubmissionEntity submission) {
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
   * Gets the subcriteria.
   *
   * @return the subcriteria
   */
  public List<SubcriterionEntity> getSubcriteria() {
    return subcriteria;
  }

  /**
   * Sets the subcriteria.
   *
   * @param subcriteria the new subcriteria
   */
  public void setSubcriteria(List<SubcriterionEntity> subcriteria) {
    this.subcriteria = subcriteria;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "CriterionEntity [id=" + id + ", criterionText=" + criterionText + ", weighting="
        + weighting + ", criterionType=" + criterionType + "]";
  }

}
