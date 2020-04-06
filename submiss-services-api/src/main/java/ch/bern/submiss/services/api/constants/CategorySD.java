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


/**
 * Reference Data Categories
 * The Enum CategorySD.
 */
public enum CategorySD {
  /*
   * E-mail Vorlage
   */
  EMAIL_TEMPLATE("EmailTemplate"),

  /*
   * Unterschriftsberechtigter
   */
  AUTHORISED_SIGNATORY("AuthorisedSignatory"),

  /*
   * Direktion
   */
  DIRECTORATE("Dicectorate"),

  /*
   * ILO
   */
  ILO("ILO"),

  /*
   * Arbeitsgattung
   */
  WORKTYPE("Worktype"),

  /*
   * MwSt-Satz
   */
  VAT_RATE("VatRate"),

  /*
   * Abteilung
   */
  DEPARTMENT("Department"),

  /*
   * Berechnungsformel
   */
  CALCULATION_FORMULA("CalulationFormula"),

  /*
   * Einstellungen
   */
  SETTINGS("Settings"),

  /*
   * Ausschlussgrund
   */
  EXCLUSION_CRITERION("ExclusionCriterion"),


  /*
   * Abbruchgrund
   */
  CANCEL_REASON("CancelReason"),

  /*
   * Logib
   */
  LOGIB("LOGIB"),

  /*
   * Verrechngsart
   */
  SETTLEMENT_TYPE("SettlementType"),

  /*
   * Vorlagenart
   */
  TEMPLATE_TYPE("TemplateType"),

  /*
   * Begründung Freihandvergabet
   */
  NEGOTIATION_REASON("NegotiationReason"),

  /*
   * Verfahrensart
   */
  PROCESS_TYPE("ProcessType"),

  /*
   * Objekt
   */
  OBJECT("Object"),

  /*
   * Verfahrensleitung
   */
  PROCESS_PM("ProcessPM"),

  /*
   * Nachweise
   */
  PROOFS("Proofs"),

  /*
   * Länder
   */
  COUNTRY("Country"),

  /*
   * Verfahren
   */
  PROCESS("Process");

  private String value;

  private CategorySD(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static CategorySD fromValue(String value) {
    for (CategorySD s : values()) {
      if (s.value.equals(value))
        return s;
    }
    return null;
  }
  
}
