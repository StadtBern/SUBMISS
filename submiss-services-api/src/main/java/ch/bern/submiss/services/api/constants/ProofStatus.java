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

public enum ProofStatus {
  ALL_PROOF(1), NOT_ACTIVE(2), WITH_FABE(3), WITH_KAIO(4), NULL(5);

  private int value;

  private ProofStatus(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
  
  public static ProofStatus fromValue(int value) {
    for (ProofStatus s : values()) {
      if (s.value ==  value)
        return s;
    }
    return null;
  }

}
