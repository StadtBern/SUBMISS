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

public class EmailTemplate {

  public enum AVAILABLE_PART {
    PROJECT_PART, COMPANY_PART, USER_PART;
  }

  public enum SEND_TYPE {
    TO, CC, BCC;
  }

  public enum RECIEVER_TYPE {
    PL("PL"), EXTERNAL_PL("EXTERNAL_PL"), SENDER("Absender"), SUBMITTENT_LIST(
        "Adressaten gemäss Submittentenliste"), RECIPIENT("Adressat"), SENDER_AFTER_REQUEST(
            "Absender gemäss Anfrage"), RECIPIENT_AFTER_SELECTION(
                "Adressaten gemäss Auswahlliste"), RECIPIENT_AFTER_REGISTRATION(
                    "Absender gemäss Registrierungsantrag");

    private String value;

    RECIEVER_TYPE(String value) {
      this.value = value;
    }

    /* Returns the value associated with the enum */
    public String getValue() {
      return value;
    }

    /* Perform a reverse lookup on the enum */
    public static RECIEVER_TYPE fromValue(String value) {
      for (RECIEVER_TYPE i : values()) {
        if (i.value.equals(value)) {
          return i;
        }
      }
      return null;
    }
  }

  public enum TEMPLATE_SHORT_CODE {
    ET01, ET02, ET03, ET04, ET05, ET06, ET07, ET08, ET09, ET10, ET11, ET12, ET13, ET14, ET15
  }

  public enum PLACEHOLDER {
    OBJECT("object"), PROJECT("project"), WORKTYPE("worktype"), SUBMISSION(
        "submission"), DIRECTORATE("directorate"), DEPARTMENT(
            "department"), PROCESS("process"), DEADLINE("deadline"), GERMAN_PROOFS(
                "german_proofs"), FRENCH_PROOFS("french_proofs"), USERNAME(
                    "username"), COMPANY_NAME("company_name"), COMPANY_LOCATION("company_location");

    private String value;

    PLACEHOLDER(String value) {
      this.value = value;
    }

    /* Returns the value associated with the enum */
    public String getValue() {
      return value;
    }
  }

}
