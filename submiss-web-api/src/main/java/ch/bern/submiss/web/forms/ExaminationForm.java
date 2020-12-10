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

package ch.bern.submiss.web.forms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExaminationForm {

  private BigDecimal minGrade;
  private BigDecimal maxGrade;
  private String submissionId;
  private Long submissionVersion;
  private List<CriterionForm> criterion;
  private Timestamp pageRequestedOn;
  private Integer passingApplicants;
  private Integer totalApplicants;

  public BigDecimal getMinGrade() {
    return minGrade;
  }

  public void setMinGrade(BigDecimal minGrade) {
    this.minGrade = minGrade;
  }

  public BigDecimal getMaxGrade() {
    return maxGrade;
  }

  public void setMaxGrade(BigDecimal maxGrade) {
    this.maxGrade = maxGrade;
  }

  public String getSubmissionId() {
    return submissionId;
  }

  public void setSubmissionId(String submissionId) {
    this.submissionId = submissionId;
  }

  public List<CriterionForm> getCriterion() {
    return criterion;
  }

  public void setCriterion(List<CriterionForm> criterion) {
    this.criterion = criterion;
  }

  public Timestamp getPageRequestedOn() {
    return pageRequestedOn;
  }

  public void setPageRequestedOn(Timestamp pageRequestedOn) {
    this.pageRequestedOn = pageRequestedOn;
  }

  public Long getSubmissionVersion() {
    return submissionVersion;
  }

  public void setSubmissionVersion(Long submissionVersion) {
    this.submissionVersion = submissionVersion;
  }

  public Integer getPassingApplicants() {
    return passingApplicants;
  }

  public void setPassingApplicants(Integer passingApplicants) {
    this.passingApplicants = passingApplicants;
  }

  public Integer getTotalApplicants() {
    return totalApplicants;
  }

  public void setTotalApplicants(Integer totalApplicants) {
    this.totalApplicants = totalApplicants;
  }
}
