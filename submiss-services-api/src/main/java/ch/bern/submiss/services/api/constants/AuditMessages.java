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

import java.util.ArrayList;
import java.util.List;

/**
 * The Class AuditMessages.
 */
public enum AuditMessages {

  /**
   * The create project.
   */
  CREATE_PROJECT("Projekt erstellt"),

  /**
   * The delete project.
   */
  DELETE_PROJECT("Projekt gelöscht"),

  /**
   * The create submission.
   */
  CREATE_SUBMISSION("Submission erstellt"),

  /**
   * The delete submission.
   */
  DELETE_SUBMISSION("Submission gelöscht"),

  /**
   * The submittent added.
   */
  SUBMITTENT_ADDED("Submittent hinzugefügt nach Prüfung der Submittentenliste"),

  /**
   * The submittent removed.
   */
  SUBMITTENT_REMOVED("Submittent entfernt nach Prüfung der Submittentenliste"),

  /**
   * The arge added.
   */
  ARGE_ADDED("ARGE-Partner hinzugefügt nach Prüfung der Submittentenliste"),

  /**
   * The arge removed.
   */
  ARGE_REMOVED("ARGE-Partner entfernt nach Prüfung der Submittentenliste"),

  /**
   * The subcontractor added.
   */
  SUBCONTRACTOR_ADDED(
    "Subunternehmen hinzugefügt nach Prüfung der Submittentenliste und/oder nach Offeröffnung abgeschlossen"),

  /**
   * The subcontractor removed.
   */
  SUBCONTRACTOR_REMOVED(
    "Subunternehmen entfernt nach Prüfung der Submittentenliste und/oder nach Offertöffnung abgeschlossen"),

  /**
   * The submittentlist check.
   */
  SUBMITTENTLIST_CHECK("Submittentenliste prüfen"),

  /**
   * The submittentlist checked.
   */
  SUBMITTENTLIST_CHECKED("Submittentenliste freigegeben bzw. geprüft"),

  /**
   * The suitability audit close.
   */
  SUITABILITY_AUDIT_CLOSE("Eignungsprüfung abgeschlossen"),

  /**
   * The formal audit close.
   */
  FORMAL_AUDIT_CLOSE("Formelle Prüfung abgeschlossen"),

  /**
   * The award evaluation close.
   */
  AWARD_EVALUATION_CLOSE("Zuschlagsbewertung abgeschlossen"),

  /**
   * The commission procurement proposal close.
   */
  COMMISSION_PROCUREMENT_PROPOSAL_CLOSE("BeKo Antrag abgeschlossen"),

  /**
   * The commission procurement decision close.
   */
  COMMISSION_PROCUREMENT_DECISION_CLOSE("BeKo Beschluss abgeschlossen"),

  /**
   * The manual award completed.
   */
  MANUAL_AWARD_COMPLETED("Manuelle Zuschlagserteilung erfolgt"),

  /**
   * The offer reopen.
   */
  OFFER_REOPEN("Wiederaufnahme Offertöffnung"),

  /**
   * The formal audit reopen.
   */
  FORMAL_AUDIT_REOPEN("Wiederaufnahme Formelle Prüfung"),

  /**
   * The formal examination reopen.
   */
  FORMAL_EXAMINATION_REOPEN("Wiederaufnahme Eignungsprüfung"),

  /**
   * The manual award reopen.
   */
  MANUAL_AWARD_REOPEN("Manuelle Zuschlagserteilung aufheben"),

  /**
   * The generate proof document.
   */
  GENERATE_PROOF_DOCUMENT("Erstellung Nachweisbrief"),

  /**
   * The document deleted.
   */
  DOCUMENT_DELETED("Dokument XY gelöscht"),

  /**
   * The cancel submission.
   */
  CANCEL_SUBMISSION("Verfahren abgebrochen"),

  /**
   * The close submission.
   */
  CLOSE_SUBMISSION("Verfahren automatisch abgeschlossen"),

  /**
   * The automatically close submission.
   */
  AUTOMATICALLY_CLOSE_SUBMISSION("Verfahren automatisch abgeschlossen"),

  /**
   * The reopen submission.
   */
  REOPEN_SUBMISSION("Verfahren wiederaufgenommen"),

  /**
   * The close offer.
   */
  CLOSE_OFFER("Offertöffnung abgeschlossen"),

  /**
   * The application opening close.
   */
  APPLICATION_OPENING_CLOSE("Bewerbungsöffnung abgeschlossen"),

  /**
   * The application opening reopen.
   */
  APPLICATION_OPENING_REOPEN("Wiederaufnahme Bewerbungsöffnung"),

  /**
   * The rechtliches gehor doc generated.
   */
  RECHTLICHES_GEHOR_DOC_GENERATED("Erstellung Rechtliches Gehör"),

  /**
   * The selektiv 1 stufe doc generated.
   */
  SELEKTIV_1_STUFE_DOC_GENERATED("Erstellung Verfügungen 1. Stufe"),

  /**
   * The verfugungen docs generated.
   */
  VERFUGUNGEN_DOCS_GENERATED("Erstellung Verfügungen"),

  /**
   * The verfahrensabbruch doc generated.
   */
  VERFAHRENSABBRUCH_DOC_GENERATED("Erstellung Verfahrensabbruch Verfügungen"),

  /**
   * The commission procurement proposal reopen.
   */
  COMMISSION_PROCUREMENT_PROPOSAL_REOPEN("Wiederaufnahme BeKo Antrag"),

  /**
   * The commission procurement decision reopen.
   */
  COMMISSION_PROCUREMENT_DECISION_REOPEN("Wiederaufnahme BeKo Beschluss"),

  /**
   * The award evaluation reopen.
   */
  AWARD_EVALUATION_REOPEN("Wiederaufnahme Zuschlagsbewertung"),

  /**
   * The email created.
   */
  EMAIL_CREATED("E-Mail XY erstellt"),

  /**
   * The rechtliches gehor doc uploaded.
   */
  RECHTLICHES_GEHOR_DOC_UPLOADED("Rechtliches Gehör hochgeladen"),

  /**
   * The selektiv 1 stufe doc uploaded.
   */
  SELEKTIV_1_STUFE_DOC_UPLOADED("Verfügungen 1. Stufe hochgeladen"),

  /**
   * The verfugungen docs uploaded.
   */
  VERFUGUNGEN_DOCS_UPLOADED("Verfügungen hochgeladen"),

  /**
   * The verfahrensabbruch doc uploaded.
   */
  VERFAHRENSABBRUCH_DOC_UPLOADED("Verfahrensabbruch Verfügungen hochgeladen"),

  /**
   * The task settled.
   */
  TASK_SETTLED("Pendenz XY erledigt"),

  /**
   * The nachweisbrief doc uploaded.
   */
  NACHWEISBRIEF_DOC_UPLOADED("Nachweisbrief hochgeladen"),

  /**
   * The create company.
   */
  CREATE_COMPANY("Firmenprofil erstellt"),

  /**
   * The proof request.
   */
  PROOF_REQUEST("Nachweisaufforderung gewünscht"),

  /**
   * The email created 6.
   */
  EMAIL_CREATED_6("Beantwortung Anfrage PL XY"),

  /**
   * The email created 3.
   */
  EMAIL_CREATED_3("Erstellung E-Mail zur Nachweisaufforderung Zertifikat"),

  /**
   * The update company1.
   */
  UPDATE_COMPANY1("Der Zustand der Firma hat sich auf \"archiviert\" geändert."),

  /**
   * The update company2.
   */
  UPDATE_COMPANY2("Der Zustand der Firma hat sich auf \"aktiv\" geändert."),

  /**
   * The sumbission data moved.
   */
  SUMBISSION_DATA_MOVED("Submission verschoben"),

  /**
   * The generate certificate document.
   */
  GENERATE_CERTIFICATE_DOCUMENT("Erstellung Zertifikat"),

  /**
   * The import.
   */
  IMPORT("Bewertung importiert"),

  /**
   * The delete company.
   */
  DELETE_COMPANY("Firmenprofil gelöscht"),

  PROCEDURE_ADDED("PROCEDURE_ADDED"),

  COUNTRY_ADDED("COUNTRY_ADDED"),

  COUNTRY_UPDATED("COUNTRY_UPDATED"),

  NACHTRAG_CLOSE("Nachtrag abgeschlossen"),

  NACHTRAG_REOPEN("Wiederaufnahme Nachtrag"),

  NACHTRAG_CREATED("Nachtrag erstellt");

  /**
   * The value.
   */
  private String value;

  /**
   * Instantiates a new audit messages.
   *
   * @param value the value
   */
  private AuditMessages(String value) {
    this.value = value;
  }


  /**
   * Gets the audit enum.
   *
   * @param filterValue the filter value
   * @return the audit enum
   */
  public static List<String> getAuditEnum(String filterValue) {

    List<String> auditMessagesList = new ArrayList<>();

    for (AuditMessages audit : AuditMessages.values()) {
      if (audit.value.contains(filterValue)) {
        auditMessagesList.add(audit.name());
      }
    }

    return auditMessagesList;
  }
}
