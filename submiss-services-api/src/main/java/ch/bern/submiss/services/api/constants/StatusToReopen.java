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

public enum StatusToReopen {
  APPLICATION_OPENING(1),
  OFFER_OPENING(2),
  SUITABILITY_AUDIT(3),
  FORMAL_AUDIT(4),
  AWARD_EVALUATION(5),
  COMMISSION_PROCUREMENT_PROPOSAL(6),
  COMMISSION_PROCUREMENT_DECISION(7),
  PROCESS(8),
  MANUAL_AWARD(9);
  
  private int value;
  
  private StatusToReopen(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

}
