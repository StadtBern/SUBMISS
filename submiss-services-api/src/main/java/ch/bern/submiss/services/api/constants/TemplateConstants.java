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
 * The Class Template.
 */
public class TemplateConstants {

  /** The Constant DOCX_FILE_EXTENSION. */
  public static final String DOCX_FILE_EXTENSION = ".docx";

  /** The Constant XLSX_FILE_EXTENSION. */
  public static final String XLSX_FILE_EXTENSION = ".xlsx";
  
  /** The Constant XLSX_FILE_EXTENSION. */
  public static final String PDF_FILE_EXTENSION = ".pdf";
  
  /** The Constant EMPTY_STRING. */
  public static final String EMPTY_STRING = "";
  
  /** The Constant TRANSLATION_FORMAT. */
  public static final String TRANSLATION_FORMAT = "de-ch";
  
  /** The Constant PROOF_STATUS_ALL_ACTIVE. */
  public static final String PROOF_STATUS_ALL_ACTIVE = "i.o.";

  /** The Constant PROOF_STATUS_NOT_ACTIVE. */
  public static final String PROOF_STATUS_NOT_ACTIVE = "nicht aktuell";
  
  /** The Constant DEFAULT_DOUBLE. */
  public static final Double DEFAULT_DOUBLE = Double.valueOf(0);

  /** The Constant ARGE. */
  public static final String ARGE = "ARGE ";
  
  /** The Constant PERCENTAGE_SYMBOL. */
  public static final String PERCENTAGE_SYMBOL = "%";
  
  /** The Constant DEFAULT_AMOUNT_FORMAT. */
  public static final String DEFAULT_AMOUNT_FORMAT = "SFr.";
  
  /** The Constant NEW_LINE_STRING. */
  public static final String NEW_LINE_STRING = "\n";
  
  /** The Constant DOC_SPLIT_STRING. */
  public static final String DOC_SPLIT_STRING = "\\s*,\\s*";
  
  /** The Constant COPY_TO. */
  public static final String COPY_TO = "Kopie an: ";
  
  /** The Constant VARIANT. */
  public static final String VARIANT = "Variante";

  /** The Constant PART_OFFER. */
  public static final String PART_OFFER = "Teilofferte";

  /** The Constant EMPTY_OFFER. */
  public static final String EMPTY_OFFER = "Leere Offerte";
  
  /** The Constant TWO_DECIMAL_PLACES */
  public static final String TWO_DECIMAL_PLACES = "%.2f";

  /** The Constant TWO_DECIMAL_PLACES_COMMA */
  public static final String TWO_DECIMAL_PLACES_COMMA = "%,.2f";
  
  /** The Constant THREE_DECIMAL_PLACES */
  public static final String THREE_DECIMAL_PLACES = "%.3f";
  
  /** The Constant PUNKTE */
  public static final String PUNKTE = " Punkte (";

  /** The Constant COMPANY_DE. */
  public static final String COMPANY_DE = "Firma ";
  
  /** The Constant INPUTSTREAM_ERROR. */
  public static final String INPUTSTREAM_ERROR = "Inputstream error: ";

  /** The Constant CANCELATION. */
  public static final String CANCELATION = "CANCELATION";
  
  /** The Constant EXCLUSION. */
  public static final String EXCLUSION = "EXCLUSION";
  
  /** The Constant REJECTION. */
  public static final String REJECTION = "REJECTION";
  
  /** The Constant AWARD. */
  public static final String AWARD = "AWARD";
  
  /** The Constant ZUSCHLAG. */
  public static final String ZUSCHLAG = "Zuschlag";
  
  /** The Constant ABSAGE. */
  public static final String ABSAGE = "Absage";

  /** The Constant AUSSCHLUSS. */
  public static final String AUSSCHLUSS = "Ausschluss";
  
  /** The Constant FIRMEN_PRO_MANDNT. */
  public static final String FIRMEN_PRO_MANDNT = "Erstelle_Firmen_pro_Mandant.xlsx";
  
  /** The Constant SUBMITTENTENLISTE_POSTLISTE. */
  public static final String SUBMITTENTENLISTE_POSTLISTE = "Submittentenliste_Postliste.xlsx";
  
  /** The Constant LISTE_ARBEITSGATTUNG. */
  public static final String LISTE_ARBEITSGATTUNG = "Liste_Arbeitsgattungen.xlsx";
  
  /** The Constant SUBMITTENTENLISTE_ALS_ETIKETTEN. */
  public static final String  SUBMITTENTENLISTE_ALS_ETIKETTEN = "Submittentenliste_als_Etiketten.pdf";
  
  /** The Constant FIRMENLISTE_KOMPLETT. */
  public static final String  FIRMENLISTE_KOMPLETT = "Firmenliste_komplett.pdf";
  
  /** The Constant FIRMENLISTE_NACH_SUCHRESULTAT. */
  public static final String FIRMENLISTE_NACH_SUCHRESULTAT = "Firmenliste_nach_Suchresultat.pdf";
  
  /** The Constant LISTE_WINBAU. */
  public static final String LISTE_WINBAU = "Liste_Winbau.xlsx";
  
  /** The Constant OFFERRTOFFNUNGSPROTOKOLL_NO_OFFERS. */
  public static final String OFFERRTOFFNUNGSPROTOKOLL_NO_OFFERS = "Offertoffnungsprotokoll_keine_Angebote";
  
  /** The Constant OFFERRTOFFNUNGSPROTOKOLL_TITLE. */
  public static final String OFFERRTOFFNUNGSPROTOKOLL_TITLE = "Offertöffnungsprotokoll";
  
  /** The Constant ANON_OFFERRTOFFNUNGSPROTOKOLL_TITLE. */
  public static final String ANON_OFFERRTOFFNUNGSPROTOKOLL_TITLE = "Anonymisiertes Offertöffnungsprotokoll";
  
  /** The Constant OFFERRTOFFNUNGSPROTOKOLL_DL_WW_TITLE. */
  public static final String OFFERRTOFFNUNGSPROTOKOLL_DL_WW_TITLE = "Offertöffnungsprotokoll Wettbewerb";
  
  /** The Constant OFFERRTOFFNUNGSPROTOKOLL_DL_WW_TITLE. */
  public static final String ANON_OFFERRTOFFNUNGSPROTOKOLL_DL_WW_TITLE = "Anonymisiertes Offertöffnungsprotokoll Wettbewerb";

  /** The Constant ZUSCHLAGSBEWERTUNG. */
  public static final String ZUSCHLAGSBEWERTUNG = "Zuschlagsbewertung";

  /** The Constant EIGNUNGSPRUFUNG. */
  public static final String EIGNUNGSPRUFUNG = "Eignungsprüfung";
  
  /** The Constant DATASOURCE. */
  public static final String DATASOURCE = "DataSource";
  
  /** The Constant STATUS_WITH_FABE. */
  public static final String STATUS_WITH_FABE = "Rücksprache mit der Fachstelle Beschaffungswesen";
  
  /** The Constant PROOF_NOT_PROVIDED. */
  public static final String PROOF_NOT_PROVIDED = "Nachweise nicht erbracht ";
  
  /** The Constant NOT_MANDATORY. */
  public static final String NOT_MANDATORY = "nicht erforderlich ";

  /** The Constant DATE_FORMAT. */
  public static final String DATE_FORMAT = "dd.MM.yy";
  
  /** The Constant SUITABILITY. */
  public static final String SUITABILITY = "suitability";
  
  /** The Constant SUITABILITY. */
  public static final String ALL_PROOF_STATUS_AVAILABLE = "Alle Nachweise vorhanden";

  /** The Constant YES. */
  public static final String YES = "Ja";
  
  /** The Constant NO. */
  public static final String NO = "Nein";
  
  /** The Constant NO. */
  public static final String MUST_CRITERION_TYPE = "muss criterion";
  
  /** The Constant NO. */
  public static final String RATED_CRITERION_TYPE = "rated criterion";
  
  /** The Constant SB_LOGO_WIDTH. */
  public static final long SB_LOGO_WIDTH = 440; 
  
  /** The Constant FABE_ICON_PATH. */
  public static final String FABE_ICON_PATH = "/icons/flagBan.png"; 
  
  /** The Constant KAIO_FABE_ICON_PATH. */
  public static final String KAIO_FABE_ICON_PATH = "/icons/ban.png"; 
  
  /** The Constant SUBMITTED_OFFERS. */
  public static final String SUBMITTED_OFFERS = "Eingereichte Offerten"; 
  
  public static final String GRAY_COLOR = "808080";

  /**
   * The Constant GERMAN_TEXT_ZERTIFIKAT.
   */
  public static final String GERMAN_TEXT_ZERTIFIKAT = "Das vollständig ausgefüllte und unterschriebene Selbstdeklarationsformular\n";

  /**
   * The Constant FRENCH_TEXT_ZERTIFIKAT.
   */
  public static final String FRENCH_TEXT_ZERTIFIKAT = "Le formulaire de déclaration spontanée dûment rempli et signé\n";

  public static final String EXCLUSION_REASON = "Ausschlussgrund";
  
  public enum TEMPLATE_TABLE_HEADER {
    HEADER_COLUMN_DATE("Datum"),
    HEADER_COLUMN_TENDERS("BewerberIn"),
    HEADER_COLUMN_RANKING("R"), 
    HEADER_COLUMN_COMPANY("Firma"), 
    HEADER_COLUMN_GROSS_AMOUNT("Brutto"), 
    HEADER_COLUMN_DISCOUNT("Rabatt"), 
    HEADER_COLUMN_DISCOUNT2("Skonto"), 
    HEADER_COLUMN_BUILDING_COSTS("BNK"), 
    HEADER_COLUMN_AMOUNT("Netto exkl. MWST"), 
    HEADER_COLUMN_VAT("MWST"), 
    HEADER_COLUMN_NOTES("Bemerkung"), 
    HEADER_COLUMN_TEAM("Team"), 
    HEADER_COLUMN_KEYWORD("Kennwort");

    private String value;

    TEMPLATE_TABLE_HEADER(String value) {
      this.value = value;
    }

    /* Returns the value associated with the enum */
    public String getValue() {
      return value;
    }
  }
}
