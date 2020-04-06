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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonView;
import ch.bern.submiss.services.api.util.View;

/**
 * The Class GeKoResultDTO.
 */
public class GeKoResultDTO extends SubmissionDTO {

  /** The first level available date from - Verfügungsdatum 1.Stufe. */
  @JsonView(View.Internal.class)
  private Date firstLevelavailableDate;

  /** The available date from - Verfügungsdatum. */
  @JsonView(View.Internal.class)
  private Date levelavailableDate;

  /** The selective open process - S/O Auftrage exkl.MWST. */
  @JsonView(View.Internal.class)
  private BigDecimal soCalculation;

  /** The InvitedTender process - E Auftrage exkl.MWST. */
  @JsonView(View.Internal.class)
  private BigDecimal eCalculation;

  /**
   * The NEGOTIATED_PROCEDURE or NEGOTIATED_PROCEDURE_WITH_COMPETITION process - F Auftrage
   * exkl.MWST.
   */
  @JsonView(View.Internal.class)
  private BigDecimal fCalculation;

  /** The submittent names list. */
  @JsonView(View.Internal.class)
  private List<String> submittentNames;

  /** The submittent name. */
  @JsonView(View.Internal.class)
  private String submittentName;

  /** The so Sum List. */
  @JsonView(View.Internal.class)
  private List<BigDecimal> soSumList;

  /** The so Sum List. */
  @JsonView(View.Internal.class)
  private List<BigDecimal> eSumList;

  /** The so Sum List. */
  @JsonView(View.Internal.class)
  private List<BigDecimal> fSumList;

  /** The soSum. */
  @JsonView(View.Internal.class)
  private String soSum;


  /** The soSum. */
  @JsonView(View.Internal.class)
  private String eSum;

  /** The soSum. */
  @JsonView(View.Internal.class)
  private String fSum;

  /** The freezeCloseSubmission. */
  @JsonView(View.Internal.class)
  private Boolean freezeCloseSubmission;

  /** The Bemerkungsfeld. */
  @JsonView(View.Internal.class)
  private String commentField;

  /**
   * Gets the first levelavailable date.
   *
   * @return the first levelavailable date
   */
  public Date getFirstLevelavailableDate() {
    return firstLevelavailableDate;
  }

  /** The submittent. */
  private SubmittentDTO submittent;

  /**
   * Sets the first levelavailable date.
   *
   * @param firstLevelavailableDate the new first levelavailable date
   */
  public void setFirstLevelavailableDate(Date firstLevelavailableDate) {
    this.firstLevelavailableDate = firstLevelavailableDate;
  }

  /**
   * Gets the levelavailable date.
   *
   * @return the levelavailable date
   */
  public Date getLevelavailableDate() {
    return levelavailableDate;
  }

  /**
   * Sets the levelavailable date.
   *
   * @param levelavailableDate the new levelavailable date
   */
  public void setLevelavailableDate(Date levelavailableDate) {
    this.levelavailableDate = levelavailableDate;
  }

  /**
   * Gets the so calculation.
   *
   * @return the so calculation
   */
  public BigDecimal getSoCalculation() {
    return soCalculation;
  }

  /**
   * Sets the so calculation.
   *
   * @param soCalculation the new so calculation
   */
  public void setSoCalculation(BigDecimal soCalculation) {
    this.soCalculation = soCalculation;
  }

  /**
   * Gets the e calculation.
   *
   * @return the e calculation
   */
  public BigDecimal geteCalculation() {
    return eCalculation;
  }

  /**
   * Sets the e calculation.
   *
   * @param eCalculation the new e calculation
   */
  public void seteCalculation(BigDecimal eCalculation) {
    this.eCalculation = eCalculation;
  }

  /**
   * Gets the f calculation.
   *
   * @return the f calculation
   */
  public BigDecimal getfCalculation() {
    return fCalculation;
  }

  /**
   * Sets the f calculation.
   *
   * @param fCalculation the new f calculation
   */
  public void setfCalculation(BigDecimal fCalculation) {
    this.fCalculation = fCalculation;
  }

  /**
   * Gets the submittent.
   *
   * @return the submittent
   */
  public SubmittentDTO getSubmittent() {
    return submittent;
  }

  /**
   * Sets the submittent.
   *
   * @param submittent the new submittent
   */
  public void setSubmittent(SubmittentDTO submittent) {
    this.submittent = submittent;
  }

  /**
   * Gets the submittent names.
   *
   * @return the submittent names
   */
  public List<String> getSubmittentNames() {
    return submittentNames;
  }

  /**
   * Sets the submittent names.
   *
   * @param submittentNames the new submittent names
   */
  public void setSubmittentNames(List<String> submittentNames) {
    this.submittentNames = submittentNames;
  }

  /**
   * Gets the submittent name.
   *
   * @return the submittent name
   */
  public String getSubmittentName() {
    return submittentName;
  }

  /**
   * Sets the submittent name.
   *
   * @param submittentName the new submittent name
   */
  public void setSubmittentName(String submittentName) {
    this.submittentName = submittentName;
  }

  /**
   * Gets the so sum list.
   *
   * @return the so sum list
   */
  public List<BigDecimal> getSoSumList() {
    return soSumList;
  }

  /**
   * Sets the so sum list.
   *
   * @param soSumList the new so sum list
   */
  public void setSoSumList(List<BigDecimal> soSumList) {
    this.soSumList = soSumList;
  }

  /**
   * Gets the e sum list.
   *
   * @return the e sum list
   */
  public List<BigDecimal> geteSumList() {
    return eSumList;
  }

  /**
   * Sets the e sum list.
   *
   * @param eSumList the new e sum list
   */
  public void seteSumList(List<BigDecimal> eSumList) {
    this.eSumList = eSumList;
  }

  /**
   * Gets the f sum list.
   *
   * @return the f sum list
   */
  public List<BigDecimal> getfSumList() {
    return fSumList;
  }

  /**
   * Sets the f sum list.
   *
   * @param fSumList the new f sum list
   */
  public void setfSumList(List<BigDecimal> fSumList) {
    this.fSumList = fSumList;
  }

  /**
   * Gets the so sum.
   *
   * @return the so sum
   */
  public String getSoSum() {
    return soSum;
  }

  /**
   * Sets the so sum.
   *
   * @param soSum the new so sum
   */
  public void setSoSum(String soSum) {
    this.soSum = soSum;
  }

  /**
   * Gets the e sum.
   *
   * @return the e sum
   */
  public String geteSum() {
    return eSum;
  }

  /**
   * Sets the e sum.
   *
   * @param eSum the new e sum
   */
  public void seteSum(String eSum) {
    this.eSum = eSum;
  }

  /**
   * Gets the f sum.
   *
   * @return the f sum
   */
  public String getfSum() {
    return fSum;
  }

  /**
   * Sets the f sum.
   *
   * @param fSum the new f sum
   */
  public void setfSum(String fSum) {
    this.fSum = fSum;
  }

  /**
   * Gets the freeze close submission.
   *
   * @return the freeze close submission
   */
  public Boolean getFreezeCloseSubmission() {
    return freezeCloseSubmission;
  }

  /**
   * Sets the freeze close submission.
   *
   * @param freezeCloseSubmission the new freeze close submission
   */
  public void setFreezeCloseSubmission(Boolean freezeCloseSubmission) {
    this.freezeCloseSubmission = freezeCloseSubmission;
  }

  /**
   * Gets the comment field.
   *
   * @return the comment field
   */
  public String getCommentField() {
    return commentField;
  }

  /**
   * Sets the comment field.
   *
   * @param commentField the new comment field
   */
  public void setCommentField(String commentField) {
    this.commentField = commentField;
  }

  @Override
  public String toString() {
    return "GeKoResultDTO [firstLevelavailableDate=" + firstLevelavailableDate
        + ", levelavailableDate=" + levelavailableDate + ", soCalculation=" + soCalculation
        + ", eCalculation=" + eCalculation + ", fCalculation=" + fCalculation + ", submittentNames="
        + submittentNames + ", submittentName=" + submittentName + ", soSumList=" + soSumList
        + ", eSumList=" + eSumList + ", fSumList=" + fSumList + ", soSum=" + soSum + ", eSum="
        + eSum + ", fSum=" + fSum + ", freezeCloseSubmission=" + freezeCloseSubmission
        + ", commentField=" + commentField + ", submittent=" + submittent + "]";
  }


}
