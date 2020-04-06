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
public class Template {
  
  /**
   * The Enum TEMPLATE_NAMES.
   */
  public enum TEMPLATE_NAMES {
    
    /** The angebotsdeckblatt. */
    ANGEBOTSDECKBLATT("Angebotsdeckblatt"), 
 /** The beko antrag. */
 BEKO_ANTRAG("BeKo Antrag"), 
 /** The verfahrensabbruch. */
 VERFAHRENSABBRUCH(
        "Verfahrensabbruch"), 
 /** The rechtliches gehor ab. */
 RECHTLICHES_GEHOR_AB(
            "Rechtliches Gehör (Abbruch)_"), 
 /** The rechtliches gehor aus. */
 RECHTLICHES_GEHOR_AUS(
                "Rechtliches Gehör (Ausschluss)_"), 
 /** The ausschluss. */
 AUSSCHLUSS("Ausschluss"), 
 /** The selektiv 1 stufe. */
 SELEKTIV_1_STUFE(
                    "Verfügungen S-Verfahren 1. Stufe "), 
 /** The beko beschluss. */
 BEKO_BESCHLUSS("BeKo Beschluss"), 
 /** The BENUTZERÜBERSICHT. */
 BENUTZERUBERSICHT("Benutzerverrechnung pro Mandant");

    /** The value. */
    private String value;

    /**
     * Instantiates a new template names.
     *
     * @param value the value
     */
    TEMPLATE_NAMES(String value) {
      this.value = value;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    /* Returns the value associated with the enum */
    public String getValue() {
      return value;
    }

    /**
     * From value.
     *
     * @param value the value
     * @return the template names
     */
    /* Perform a reverse lookup on the enum */
    public static TEMPLATE_NAMES fromValue(String value) {
      for (TEMPLATE_NAMES i : values()) {
        if (i.value.equals(value)) {
          return i;
        }
      }
      return null;
    }
  }
  
  /** The Constant ANGEBOTSDECKBLATT. */
  public static final String ANGEBOTSDECKBLATT = "PT01";

  /** The Constant BEKO_ANTRAG. */
  public static final String BEKO_ANTRAG = "PT02";

  /** The Constant NACHWEISBRIEF_PT. */
  public static final String NACHWEISBRIEF_PT = "PT03";

  /** The Constant NACHWEISBRIEF_SUB. */
  public static final String NACHWEISBRIEF_SUB = "PT04";

  /** The Constant NACHWEISBRIEF_FT. */
  public static final String NACHWEISBRIEF_FT = "FT01";

  /** The Constant ZERTIFIKAT. */
  public static final String ZERTIFIKAT = "FT02";
  
  /** The Constant BEKO_BESCHLUSS. */
  public static final String BEKO_BESCHLUSS = "PT05";
  
  /** The Constant VERFAHRENSABBRUCH. */
  public static final String VERFAHRENSABBRUCH = "PT06";
  
  /** The Constant SUBMITTENTENLISTE. */
  public static final String SUBMITTENTENLISTE = "PT15";
  
  /** The Constant LISTE_ARBEITSGATTUNG. */
  public static final String LISTE_ARBEITSGATTUNG = "FT05";
  
  /** The Constant FIRMENBLATT_EINFACH. */
  public static final String FIRMENBLATT_EINFACH = "FT03";
  
  /** The Constant FIRMENBLATT_EINFACH. */
  public static final String FIRMENBLATT_ERWEITERT = "FT04";
  
  /** The Constant SUBMITTENTENLISTE_POSTLISTE. */
  public static final String SUBMITTENTENLISTE_POSTLISTE = "PT17";
  
  /** The Constant ANONYMISIERTES OFFERTOFNUNGSPROTOKOLL. */
  public static final String A_OFFERRTOFFNUNGSPROTOKOLL = "PT09";
  
  /** The Constant OFFERTOFNUNGSPROTOKOLL. */
  public static final String OFFERRTOFFNUNGSPROTOKOLL = "PT08";
  
  /** The Constant OFFERTVERGLEICH_FREIHANDIG. */
  public static final String OFFERTVERGLEICH_FREIHANDIG = "PT12";
  
  /** The Constant OFFERRTOFFNUNGSPROTOKOLL_DL_WW. */
  public static final String OFFERRTOFFNUNGSPROTOKOLL_DL_WW = "PT10";
  
  /** The Constant RECHTLICHES_GEHOR. */
  public static final String RECHTLICHES_GEHOR = "PT19";
  
  /** The Constant ANONYMISIERTES OFFERRTOFFNUNGSPROTOKOLL_DL_WW. */
  public static final String A_OFFERRTOFFNUNGSPROTOKOLL_DL_WW = "PT11";
  
  /** The Constant BEWERBER_UBERSICHT. */
  public static final String BEWERBER_UBERSICHT = "PT13";
  
  /** The Constant SUBMISSIONSUBERSICHT. */
  public static final String SUBMISSIONSUBERSICHT = "PT14";
  
  /** The Constant RECHTLICHES_GEHOR_AUSSCHLUSS. */
  public static final String RECHTLICHES_GEHOR_AUSSCHLUSS = "PT20";
  
  /** The Constant COMPANIES_PER_TENANT. */
  public static final String COMPANIES_PER_TENANT = "FT06";
  
  /** The Constant VERTRAG_PLANUNGS_PROJEKTIERUNGS_HERSTELLUNGSVERTRAG. */
  public static final String VERTRAG_PLANUNGS_PROJEKTIERUNGS_HERSTELLUNGSVERTRAG = "DEP_SGB06";
  
  /** The Constant VERTRAG_LIEFER_ANBAUVERTRAG. */
  public static final String VERTRAG_LIEFER_ANBAUVERTRAG = "DEP_SGB05";
  
  /** The Constant VERTRAG_KAUFVERTRAG_SGB. */
  public static final String VERTRAG_KAUFVERTRAG_SGB ="DEP_SGB04";

  /** The Constant VERTRAG_HONORARVERTRAG_SGB. */
  public static final String VERTRAG_HONORARVERTRAG_SGB = "DEP_SGB03";

  /** The Constant VERTRAG_AUFTRAGSBESTATIGUNG_FREIHANDIG_SGB. */
  public static final String VERTRAG_AUFTRAGSBESTATIGUNG_FREIHANDIG_SGB = "DEP_SGB02";

  /** The Constant VERTRAG_WERKVERTRAG_SGB. */
  public static final String VERTRAG_WERKVERTRAG_SGB = "DEP_SGB01";
  
  /** The Constant VERTRAG_KAUF_LIEFERVERTRAG_LB. */
  public static final String VERTRAG_KAUF_LIEFERVERTRAG_LB = "DEP_LB02";
  
  /** The Constant VERTRAG_DIENSTLEISTUNGSVERTRAG_LB. */
  public static final String VERTRAG_DIENSTLEISTUNGSVERTRAG_LB = "DEP_LB01";
  
  /** The Constant VERTRAG_WERKVERTRAG_ISB. */
  public static final String VERTRAG_WERKVERTRAG_ISB = "DEP_ISB02";
  
  /** The Constant VERTRAG_AUFTRAGSBESTATIGUNG_FREIHANDIG_ISB. */
  public static final String VERTRAG_AUFTRAGSBESTATIGUNG_FREIHANDIG_ISB = "DEP_ISB01";
  
  /** The Constant VERTRAG_WERKVERTRAG_HSB. */
  public static final String VERTRAG_WERKVERTRAG_HSB = "DEP_HSB01";
  
  /** The Constant AUSSCHLUSS. */
  public static final String AUSSCHLUSS = "PT25";
  
  /** The Constant ZUSCHLAG_ABSAGE. */
  public static final String VERFUGUNGEN = "PT24";

  /** The Constant SELEKTIV_1_STUFE. */
  public static final String SELEKTIV_1_STUFE = "PT22";
  
  /** The Constant VERFUGUNGEN_DL_WETTBEWERB. */
  public static final String VERFUGUNGEN_DL_WETTBEWERB = "PT23";
  
  /** The Constant RECHTLICHES_GEHOR_EMPTY. */
  public static final String RECHTLICHES_GEHOR_EMPTY = "PT19E";
  
  /** The Constant VERFAHRENSABBRUCH_EMPTY. */
  public static final String VERFAHRENSABBRUCH_EMPTY = "PT06E";

  /** The Constant ZUSCHLAGSBEWERTUNG. */
  public static final String ZUSCHLAGSBEWERTUNG = "PT18";
  
  /** The Constant SUBMITTENTENLISTE ALS ETIKETTEN. */
  public static final String SUBMITTENTENLISTE_ALS_ETIKETTEN = "PT16";
  
  /** The Constant FIRMENLISTE_KOMPLETT. */
  public static final String FIRMENLISTE_KOMPLETT = "FT07";
  
  /** The Constant FIRMENLISTE_NACH_SUCHRESULTAT. */
  public static final String FIRMENLISTE_NACH_SUCHRESULTAT = "FT08";
  
  /** The Constant LISTE_WINBAU. */
  public static final String LISTE_WINBAU = "FT09";
  
  /** The Constant EIGNUNGSPRUFUNG. */
  public static final String EIGNUNGSPRUFUNG = "PT07";
  
  public static final String LF_CR_CONSTANT = "%0D%0A";
  
  public static final String LF_CONSTANT = "\\%0A";
  
}
