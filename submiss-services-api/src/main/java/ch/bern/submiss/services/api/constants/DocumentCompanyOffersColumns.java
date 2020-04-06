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

public enum DocumentCompanyOffersColumns {
  COLUMN_1(1), COLUMN_2(2), COLUMN_3(3), COLUMN_4(4), COLUMN_5(5), COLUMN_6(6), COLUMN_7(7), COLUMN_8(8), COLUMN_9(9), COLUMN_10(10);

  private int value;

  private DocumentCompanyOffersColumns(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
  
  public static DocumentCompanyOffersColumns fromValue(int value) {
    for (DocumentCompanyOffersColumns s : values()) {
      if (s.value ==  value)
        return s;
    }
    return null;
  }

}
