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
import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SubcriterionForm extends AbstractForm {

  private String criterion;
  private String subcriterionText;
  private Double weighting;
  private Timestamp pageRequestedOn;

  public String getCriterion() {
    return criterion;
  }

  public void setCriterion(String criterion) {
    this.criterion = criterion;
  }

  public String getSubcriterionText() {
    return subcriterionText;
  }

  public void setSubcriterionText(String subcriterionText) {
    this.subcriterionText = subcriterionText;
  }

  public Double getWeighting() {
    return weighting;
  }

  public void setWeighting(Double weighting) {
    this.weighting = weighting;
  }

  public Timestamp getPageRequestedOn() {
    return pageRequestedOn;
  }

  public void setPageRequestedOn(Timestamp pageRequestedOn) {
    this.pageRequestedOn = pageRequestedOn;
  }
}
