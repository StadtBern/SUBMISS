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

package ch.bern.submiss.services.api.util;

import ch.bern.submiss.services.api.constants.TenderStatus;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Static lookup values used throughout the project.
 */
public class LookupValues {

  /* The application's name. Used as jobGroup in the scheduler */
  public static final String APPLICATION_NAME = "submiss";

  /* Used as input for all operation performed not by a user, but by the system */
  public static final String SYSTEM = "SYSTEM";

  /* The lexicon groups into which KAVModern IPS translations are held */
  public static final String DEFAULT_LEXICON_GROUP = "default";

  /* The number of days to cache translations on the user's browser */
  public static final int LEXICON_CACHE_TRANSLATIONS_DURATION = 7;

  /* The types of criteria */
  public static final String MUST_CRITERION_TYPE = "mustCriterion";
  public static final String EVALUATED_CRITERION_TYPE = "evaluatedCriterion";
  public static final String AWARD_CRITERION_TYPE = "awardCriterion";
  public static final String PRICE_AWARD_CRITERION_TYPE = "priceAwardCriterion";
  public static final String OPERATING_COST_AWARD_CRITERION_TYPE = "operatingCostAwardCriterion";
  public static final String REPORTS_MAX_RESULTS = "Maximale Anzahl der Auswertungsergebnisse";

  /* Criteria max/min grade*/
  public static final String MAX_GRADE = "maxGrade";
  public static final String MIN_GRADE = "minGrade";

  /* Audit constants. Use 0,1 for internal,external user accordingly. */
  public static final String INTERNAL_LOG = "0";
  public static final String EXTERNAL_LOG = "1";

  /* Template constants. */
  public static final String CREATED_BY = "CREATED_BY";
  public static final String TEMPLATE_FOLDER_ID = "cb9d1ed6-f603-11e7-8c3f-9a214cf093ae";
  public static final String TYPE = "TYPE";
  public static final String WITH_BK = "WITH_BK";
  public static final String WITH_VORBEHALT = "WITH_VORBEHALT";
  public static final String ABOVE_THRESHOLD = "ABOVE_THRESHOLD";
  public static final String SUB_COMPANY = "SUB_COMPANY";
  public static final String TENANT_KANTON_BERN = "Kanton Bern";
  public static final String STADT_BERN_MAIN_DEPARTMENT_SHORT_NAME = "FaBe";

  /*Report Template constants. */
  public static final String REPORT_TEMPLATE_FOLDER_ID = "2b2f4778-47a0-11e8-842f-0ed5f89f718b";
  public static final String REPORT_TEMPLATE_ATTRIBUTE_NAME = "NAME";

  /* security error messages */
  public static final String CHECK_ENTITY = " - Check entity";
  public static final String CHECK_PRECONDITIONS = " - Check preconditions";

  /* audit level */
  public static final String COMPANY_LEVEL = "COMPANY_LEVEL";
  public static final String PROJECT_LEVEL = "PROJECT_LEVEL";

  /*Label names for search criteria in GeKO export. */
  public static final String PUBLICATION = "Publikation";
  public static final String PUBLICATION_AWARD = "Publikation Zuschlag";
  public static final String APPLICATION_OPENING = "Bewerbungsöffnung";
  public static final String OFFER_OPENING = "Offertöffnung";
  public static final String COMMISION_PROCUREMENT_PROPOSAL = "BeKo-Sitzung";
  public static final String PUBLICATION_DIRECT_AWARD = "Publikation Absicht freihändige Vergabe";
  public static final String FIRST_DEADLINE = "Eingabetermin 1";
  public static final String SECOND_DEADLINE = "Eingabetermin 2";
  public static final String FIRST_LEVEL_AVAILABLE = "Verfügungsdatum 1.Stufe";
  public static final String LEVEL_AVAILABLE = "Verfügungsdatum";
  public static final String AWARD_COMPANY = "Zuschlagsempfänger";
  public static final String NOTES = "Submissionsbemerkungen";
  public static final String DESCRIPTION = "Beschreibung";
  public static final String GATT_WTO = "GATT / WTO";
  public static final String INTERN_EXTERN = "Intern/Extern";
  public static final String CANCEL_REASON = "Verfahrensabbruch";
  public static final String FREE_REASON = "Freihandvergabe";
  public static final String YES = "Ja";
  public static final String NO = "Nein";
  public static final String INTERN = "Intern";
  public static final String EXTERN = "Extern";
  public static final String FROM = "von";
  public static final String BIS = "bis";
  public static final String DATE_FORMAT = "dd.MM.yyyy";
  public static final String SCORE = "-";
  public static final String EXCEL_FORMAT = ".xlsx";
  public static final String PDF_FORMAT = ".pdf";
  public static final String SPACE = " ";
  public static final String SEARCHEDCRITERIA = "SEARCHEDCRITERIA";
  public static final String DATASOUURCE = "DATASOUURCE";
  public static final String IS_COLUMN_SELECTED = "isColumnSelected";
  public static final String START_DATE = "START_DATE";
  public static final String END_DATE = "END_DATE";
  public static final String SO_AMOUNT = "SO_AMOUNT";
  public static final String E_AMOUNT = "E_AMOUNT";
  public static final String F_AMOUNT = "F_AMOUNT";
  public static final String TOTAL_AMOUNT = "TOTAL_AMOUNT";
  public static final String ZEITRAUM = "Zeitraum";
  public static final String FUTURE_ERROR = "any_date_in_the_future_error_message";
  public static final String END_DATE_BEFORE_START_DATE_ERROR = "date_from_after_date_to_error_message";
  public static final String NPE_EXCEPTION = "Invalid result";
  public static final String GEKO = "GeKo";
  public static final String UNDER_SCORE = "_";
  public static final String DOT = ".";
  public static final String GEKO_TEMPLATE = "GeKo_Report";
  public static final String SO_LABEL = "S/O-Aufträge exkl.MWST";
  public static final String E_LABEL = "E-Aufträge exkl.MWST";
  public static final String F_LABEL = "F-Aufträge exkl.MWST";
  public static final String SUM_AMOUNTS = "SUM_AMOUNTS";
  public static final String SO_LABEL2 = "SO-Aufträge exkl.MWST";
  public static final String SLASH = " / ";
  public static final String SEMICOLON = "; ";
  public static final String LAUFENDES_BESCHWERDENVERFAHREN = "Laufendes Beschwerdeverfahren";
  public static final String PRINT_COMMAND = "IN1";

  public static final int MAX_REASON_TEXT_LENGTH = 10000;

  /* Schedulers */
  public static final String COMPANY_PROOFS_SCHEDULER_NAME = "CompanyProofsValidityCheckScheduler";
  public static final String COMPANY_PROOFS_SCHEDULER_TRIGGER_NAME = "CompanyProofsValidityCheckSchedulerTrigger";
  public static final String COMPANY_PROOFS_SCHEDULER_DAILY_TIME = "00:10";
  public static final String COMPANY_PROOFS_SCHEDULER_UPDATE_REASON_NOT_ACTIVE =
    "At least one proof has expired, so the proof status of the company is set to not active";
  public static final String PUBLICATION_AWARD_DATE_SCHEDULER_NAME = "PublicationAwardDateCheckScheduler";
  public static final String PUBLICATION_AWARD_DATE_SCHEDULER_TRIGGER_NAME = "PublicationAwardDateCheckSchedulerTrigger";
  public static final String PUBLICATION_AWARD_DATE_SCHEDULER_DAILY_TIME = "01:10";
  public static final String NOT_CLOSED_SUBMISSIONS_SCHEDULER_NAME = "NotClosedSubmissionsCheckScheduler";
  public static final String NOT_CLOSED_SUBMISSIONS_SCHEDULER_TRIGGER_NAME = "NotClosedSubmissionsCheckSchedulerTrigger";
  public static final String NOT_CLOSED_SUBMISSIONS_SCHEDULER_DAILY_TIME = "02:10";
  public static final String AUTO_CLOSE_SUBMISSIONS_SCHEDULER_NAME = "AutomaticallyCloseSubmissionsScheduler";
  public static final String AUTO_CLOSE_SUBMISSIONS_SCHEDULER_TRIGGER_NAME = "AutomaticallyCloseSubmissionsSchedulerTrigger";
  public static final String AUTO_CLOSE_SUBMISSIONS_SCHEDULER_DAILY_TIME = "03:10";
  public static final String AUTO_DEACTIVATE_USER_SCHEDULER_NAME = "AutomaticallyDeactivateUserScheduler";
  public static final String AUTO_DEACTIVATE_USER_SCHEDULER_TRIGGER_NAME = "AutomaticallyDeactivateUserSchedulerTrigger";
  public static final String AUTO_DEACTIVATE_USER_SCHEDULER_DAILY_TIME = "04:10";
  public static final String AUTO_UPDATE_TASK_TYPE_SCHEDULER_NAME = "AutomaticallyUpdateTaskTypeScheduler";
  public static final String AUTO_UPDATE_TASK_TYPE_SCHEDULER_TRIGGER_NAME = "AutomaticallyUpdateTaskTypeSchedulerTrigger";
  public static final String AUTO_UPDATE_TASK_TYPE_SCHEDULER_DAILY_TIME = "05:10";

  /* Numbers as Strings */
  public static final double ZERO_DOUBLE = 0;
  public static final double TEN_THOUSAND_DOUBLE = 10000;
  public static final String ZERO_STRING = "0";
  public static final int ZERO_INT = 0;
  public static final int ONE_INT = 1;
  public static final int TWO_INT = 2;
  public static final int THREE_INT = 3;
  public static final int SIX = 6;
  public static final int TWENTY = 20;
  public static final int FORTY = 40;
  public static final int ONE_HUNDRENT = 100;
  // Langer Text (max. 150 Zeichen; min. 10 Zeichen)
  public static final int LARGE_TEXT_MIN = 10;
  public static final int LARGE_TEXT_MAX = 150;
  public static final String TABLE_ALIAS = "tableAlias";

  /* Exclusion Reason */
  public static final String EXCLUSION_REASON =
    "Das Angebot der Firma &1 entspricht den wesentlichen Formerfordernissen nicht. Gemäss der Verordnung "
      + "über das öffentliche Beschaffungswesen (ÖBV), Art. 20, sind dem Angebot die Nachweise über die Erfüllung "
      + "der Pflichten gegenüber der öffentlichen Hand, der Sozialversicherung sowie den Arbeitnehmerinnen und Arbeitnehmern "
      + "(Selbstdeklaration und weitere Bestätigungen, dass alle fälligen Prämien bezahlt sind) beizulegen. Die Nachweise wurden "
      + "der Offerte vom &2 nicht beigelegt. Mit Schreiben vom &3 wurde die Firma auf das Fehlen der Nachweise hingewiesen. Ihr "
      + "wurde eine Frist zur Nachreichung der Unterlagen bis am &4 gewährt. Die Nachweise wurden bis heute nicht vollständig eingereicht.";


  /* Permitted expressions for calculation formulas */
  public static final String AI = "ai";
  public static final String AO = "ao";

  /* Double Quotes */
  public static final String DOUBLEQUOTE = "\"";

  /**
   * The Constant submissStatuses contains all statuses in the order specified in the workflows
   */
  protected static final List<TenderStatus> submissStatuses = new LinkedList<>(Arrays.asList(
    (TenderStatus.SUBMISSION_CREATED),
    (TenderStatus.APPLICANTS_LIST_CREATED),
    (TenderStatus.APPLICATION_OPENING_STARTED),
    (TenderStatus.APPLICATION_OPENING_CLOSED),
    (TenderStatus.FORMAL_EXAMINATION_SUITABILITY_AUDIT_STARTED),
    (TenderStatus.SUITABILITY_AUDIT_COMPLETED_S),
    (TenderStatus.AWARD_NOTICES_1_LEVEL_CREATED),
    (TenderStatus.SUBMITTENT_LIST_CREATED),
    (TenderStatus.SUBMITTENTLIST_CHECK),
    (TenderStatus.SUBMITTENTLIST_CHECKED),
    (TenderStatus.OFFER_OPENING_STARTED),
    (TenderStatus.OFFER_OPENING_CLOSED),
    (TenderStatus.FORMAL_EXAMINATION_AND_QUALIFICATION_STARTED),
    (TenderStatus.FORMAL_EXAMINATION_STARTED),
    (TenderStatus.AWARD_EVALUATION_STARTED),
    (TenderStatus.FORMAL_AUDIT_COMPLETED),
    (TenderStatus.SUITABILITY_AUDIT_COMPLETED),
    (TenderStatus.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED),
    (TenderStatus.FORMAL_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED),
    (TenderStatus.AWARD_EVALUATION_CLOSED),
    (TenderStatus.MANUAL_AWARD_COMPLETED),
    (TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_STARTED),
    (TenderStatus.COMMISSION_PROCUREMENT_PROPOSAL_CLOSED),
    (TenderStatus.COMMISSION_PROCUREMENT_DECISION_STARTED),
    (TenderStatus.COMMISSION_PROCUREMENT_DECISION_CLOSED),
    (TenderStatus.AWARD_NOTICES_CREATED),
    (TenderStatus.CONTRACT_CREATED),
    (TenderStatus.PROCEDURE_CANCELED),
    (TenderStatus.PROCEDURE_COMPLETED)));

  public static List<TenderStatus> getSubmissstatuses() {
    return submissStatuses;
  }

  public enum CRITERION_TYPE {
    SUITABILITY("suitability"), AWARD("award");

    private String value;

    CRITERION_TYPE(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  /*
   * Different types of status a user may have. The value '-1' indicates that this user does not
   * exist in the database.
   */
  public enum USER_STATUS {
    DOES_NOT_EXIST((byte) -1), DISABLED((byte) 0), DISABLED_APPROVED(
      (byte) 1), ENABLED_NOT_APPROVED((byte) 2), ENABLED_APPROVED((byte) 3);

    private byte value;

    USER_STATUS(byte value) {
      this.value = value;
    }

    /* Perform a reverse lookup on the enum */
    public static USER_STATUS fromValue(byte value) {
      for (USER_STATUS s : values()) {
        if (s.value == value) {
          return s;
        }
      }
      return null;
    }

    /* Returns the value associated with the enum */
    public byte getValue() {
      return value;
    }
  }

  public enum WEBSSO_ATTRIBUTES {
    USERNAME("uid"), USER_ID("entryUUID"), FIRST_NAME("givenName"), LAST_NAME("sn"), EMAIL(
      "mail"), DEPARTMENT("department"), COMPANY("company");

    private String value;

    WEBSSO_ATTRIBUTES(String value) {
      this.value = value;
    }

    /* Perform a reverse lookup on the enum */
    public static WEBSSO_ATTRIBUTES fromValue(String value) {
      for (WEBSSO_ATTRIBUTES i : values()) {
        if (i.value.equals(value)) {
          return i;
        }
      }
      return null;
    }

    /* Returns the value associated with the enum */
    public String getValue() {
      return value;
    }
  }

  public enum USER_ATTRIBUTES {
    EMAIL("EMAIL"), FIRSTNAME("FIRSTNAME"), LASTNAME("LASTNAME"), TENANT("TENANT"), STATUS(
      "STATUS"), USERNAME("USERNAME"), SAML_DEPARTMENT("SAML_DEPARTMENT"), SEC_DEPARTMENTS(
      "SECONDARY_DEP_IDS"), REGISTERED_DATE("REGISTERED_DATE"), FUNCTION(
      "FUNCTION"), DEACTIVATION_DATE(
      "DEACTIVATION_DATE"), LOGIN_DATE("LOGIN_DATE"), REGISTERED("REGISTERED");

    private String value;

    USER_ATTRIBUTES(String value) {
      this.value = value;
    }

    /* Perform a reverse lookup on the enum */
    public static USER_ATTRIBUTES fromValue(String value) {
      for (USER_ATTRIBUTES i : values()) {
        if (i.value.equals(value)) {
          return i;
        }
      }
      return null;
    }

    /* Returns the value associated with the enum */
    public String getValue() {
      return value;
    }
  }


  public enum SEARCH_USER_STATUS {
    NO_DEPARTMENT_NO_DIR(0), DEPARTMENT_NO_DIR(1), NO_DEPARTMENT_DIR(2), DEPARTMENT_DIR(3);

    private Integer value;

    SEARCH_USER_STATUS(Integer value) {
      this.value = value;
    }

    public Integer getValue() {
      return value;
    }
  }

  public enum Level {

    FIRST(1), SECOND(2);

    private int value;

    private Level(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }


  /**
   * The available export table types.
   */
  public enum GEKO_CRITERIA {
    PUBLICATION(LookupValues.PUBLICATION, 1, "futurePublicationDateErrorField",
      LookupValues.FUTURE_ERROR,
      "dateOrderPublicationDateErrorField", LookupValues.END_DATE_BEFORE_START_DATE_ERROR),
    PUBLICATION_AWARD(LookupValues.PUBLICATION_AWARD, 2, "futurePublicationDateAwardErrorField",
      LookupValues.FUTURE_ERROR,
      "dateOrderPublicationDateAwardErrorField", LookupValues.END_DATE_BEFORE_START_DATE_ERROR),
    APPLICATION_OPENING(LookupValues.APPLICATION_OPENING, 3,
      "futureApplicationOpeningDateErrorField", LookupValues.FUTURE_ERROR,
      "dateOrderApplicationOpeningDateErrorField", LookupValues.END_DATE_BEFORE_START_DATE_ERROR),
    OFFER_OPENING(LookupValues.OFFER_OPENING, 4, "futureOfferOpeningErrorField",
      LookupValues.FUTURE_ERROR,
      "dateOrderOfferOpeningErrorField", LookupValues.END_DATE_BEFORE_START_DATE_ERROR),
    COMMISION_PROCUREMENT_PROPOSAL(LookupValues.COMMISION_PROCUREMENT_PROPOSAL, 5,
      "futureCommissionProcurementProposalDateErrorField", LookupValues.FUTURE_ERROR,
      "dateOrderCommissionProcurementProposalDateErrorField",
      LookupValues.END_DATE_BEFORE_START_DATE_ERROR),
    PUBLICATION_DIRECT_AWARD(LookupValues.PUBLICATION_DIRECT_AWARD, 17,
      "futurePublicationDateDirectAwardErrorField", LookupValues.FUTURE_ERROR,
      "dateOrderPublicationDateDirectAwardErrorField",
      LookupValues.END_DATE_BEFORE_START_DATE_ERROR),
    FIRST_DEADLINE(LookupValues.FIRST_DEADLINE, 6, "futureFirstDeadlineErrorField",
      LookupValues.FUTURE_ERROR,
      "dateOrderFirstDeadlineErrorField", LookupValues.END_DATE_BEFORE_START_DATE_ERROR),
    SECOND_DEADLINE(LookupValues.SECOND_DEADLINE, 7, "futureSecondDeadlineErrorField",
      LookupValues.FUTURE_ERROR,
      "dateOrderSecondDeadlineErrorField", LookupValues.END_DATE_BEFORE_START_DATE_ERROR),
    FIRST_LEVEL_AVAILABLE(LookupValues.FIRST_LEVEL_AVAILABLE, 8,
      "futureFirstLevelavailableDateErrorField", LookupValues.FUTURE_ERROR,
      "dateOrderFirstLevelavailableDateErrorField", LookupValues.END_DATE_BEFORE_START_DATE_ERROR),
    LEVEL_AVAILABLE(LookupValues.LEVEL_AVAILABLE, 9, "futureLevelavailableDateErrorField",
      LookupValues.FUTURE_ERROR,
      "dateOrderLevelavailableDateErrorField", LookupValues.END_DATE_BEFORE_START_DATE_ERROR),
    AWARD_COMPANY(LookupValues.AWARD_COMPANY, 10, "", "", "", ""),
    NOTES(LookupValues.NOTES, 11, "", "", "", ""),
    DESCRIPTION(LookupValues.DESCRIPTION, 12, "", "", "", ""),
    GATT_WTO(LookupValues.GATT_WTO, 13, "", "", "", ""),
    INTERN_EXTERN(LookupValues.INTERN_EXTERN, 14, "", "", "", ""),
    CANCEL_REASON(LookupValues.CANCEL_REASON, 15, "", "", "", ""),
    FREE_REASON(LookupValues.FREE_REASON, 16, "", "", "", "");

    private final String label;
    private final int criteriaId;
    private final String futureErrorField;
    private final String futureErrorMsg;
    private final String dateOrderErrorField;
    private final String dateOrderErrorMsg;


    private GEKO_CRITERIA(final String label, final int criteriaId, final String futureErrorField,
      final String futureErrorMsg, final String dateOrderErrorField,
      final String dateOrderErrorMsg) {
      this.label = label;
      this.criteriaId = criteriaId;
      this.futureErrorField = futureErrorField;
      this.futureErrorMsg = futureErrorMsg;
      this.dateOrderErrorField = dateOrderErrorField;
      this.dateOrderErrorMsg = dateOrderErrorMsg;
    }


    public String getLabel() {
      return label;
    }

    public int getCriteriaId() {
      return criteriaId;
    }

    public String getFutureErrorField() {
      return futureErrorField;
    }

    public String getFutureErrorMsg() {
      return futureErrorMsg;
    }

    public String getDateOrderErrorField() {
      return dateOrderErrorField;
    }

    public String getDateOrderErrorMsg() {
      return dateOrderErrorMsg;
    }

  }
}
