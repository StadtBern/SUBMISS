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

public enum CommissionProcurementProposalReasonGiven {
  REASON_GIVEN_DL("Das Preisgericht empfiehlt einstimmig das beantragte Team zur Weiterbearbeitung des Projekts."),
  REASON_GIVEN_NOT_DL("Die Angebote der geeigneten Anbieterinnen und Anbieter wurden nach den publizierten Zuschlagskriterien bewertet. "
      + "Die beantragte Firma reichte das wirtschaftlich günstigste Angebot ein. "
      + "Nach Bewertungstabelle resultiert das Punktemaximum.");
  
  private String value;

  private CommissionProcurementProposalReasonGiven(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
