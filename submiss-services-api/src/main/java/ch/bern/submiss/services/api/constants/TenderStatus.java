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

package ch.bern.submiss.services.api.constants;

public enum TenderStatus {

  SUBMISSION_CREATED("10"),
  APPLICANTS_LIST_CREATED("20"),
  APPLICATION_OPENING_STARTED("30"),
  APPLICATION_OPENING_CLOSED("40"),
  FORMAL_EXAMINATION_SUITABILITY_AUDIT_STARTED("50"),
  SUITABILITY_AUDIT_COMPLETED_S("60"),
  AWARD_NOTICES_1_LEVEL_CREATED("70"),
  SUBMITTENT_LIST_CREATED("80"),
  SUBMITTENTLIST_CHECK("90"),
  SUBMITTENTLIST_CHECKED("100"),
  OFFER_OPENING_STARTED("110"),
  OFFER_OPENING_CLOSED("120"),
  FORMAL_EXAMINATION_AND_QUALIFICATION_STARTED("130"),
  FORMAL_EXAMINATION_STARTED("140"),
  FORMAL_AUDIT_COMPLETED("150"),
  AWARD_EVALUATION_STARTED("160"),
  SUITABILITY_AUDIT_COMPLETED("170"),
  SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED("180"),
  FORMAL_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED("190"),
  AWARD_EVALUATION_CLOSED("200"),
  MANUAL_AWARD_COMPLETED("210"),
  COMMISSION_PROCUREMENT_PROPOSAL_STARTED("220"),
  COMMISSION_PROCUREMENT_PROPOSAL_CLOSED("230"),
  COMMISSION_PROCUREMENT_DECISION_STARTED("240"),
  COMMISSION_PROCUREMENT_DECISION_CLOSED("250"),
  AWARD_NOTICES_CREATED("260"),
  CONTRACT_CREATED("270"),
  PROCEDURE_COMPLETED("280"),
  PROCEDURE_CANCELED("290");
  
  private String value;
  
  private TenderStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static TenderStatus fromValue(String value) {
    for (TenderStatus s : values()) {
      if (s.value.equals(value))
        return s;
    }
    return null;
  }
   
}
