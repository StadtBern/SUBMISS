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

public enum DocumentPlaceholders {

  S_SUM_OFFERS("s_sum_offers"),
  S_CANCEL_ART_NUMBER("s_cancel_art_number"),
  S_CANCEL_ART_DESCRIPTION("s_cancel_art_description"),
  S_MAIN_COMPANY("s_main_company"),
  S_MAIN_COMPANY_LOCATION("s_main_company_location"),
  
  F_MAIN_COMPANY("f_main_company"),
  F_COMPANY_PROOF_STATUS_COL_1("f_company_proofs1"),
  F_COMPANY_PROOF_STATUS_COL_2("f_company_proofs2"),
  F_COMPANY_PROOF_STATUS_COL_3("f_company_proofs3"),
  F_COMPANY_PROOF_STATUS_COL_4("f_company_proofs4"),
  F_COMPANY_WORKTYPES("f_company_worktypes"),
  F_COMPANY_LOCATION("f_company_location"),
  F_COMPANY_ADDRESS("f_company_address"),
  F_COMPANY_ADDRESS2("f_company_address2"),
  F_COMPANY_PROOF_STATUS("f_company_proof"),
  F_COMPANY_NAME_OR_ARGE("f_company_name_or_arge"),
  F_COMPANY_JOINT("f_company_joint"),
  F_GERMAN_PROOFS("f_german_proofs"),
  F_FRENCH_PROOFS("f_french_proofs"),
  F_COMPANY_APPRENTICE_FACTOR_VALUE("f_app_factor_value"),
  F_COMPANY_FIFTY_PLUS_FACTOR_VALUE("f_fifty_plus_factor_value"),
  
  R_DIRECTORATE_POST("r_directorate_post"),
  R_DIRECTORATE_TEL("r_directorate_tel"),
  R_DIRECTORATE_FAX("r_directorate_fax"),
  R_DIRECTORATE_ADDRESS("r_directorate_address"),
  R_DIRECTORATE_WEBSITE("r_directorate_website"),
  R_DIRECTORATE_NAME("r_directorate_name"),
  R_DIRECTORATE_EMAIL("r_directorate_email"),
  
  R_DEPARTMENT_ADDRESS("r_department_address"),
  R_DEPARTMENT_NAME("r_department_name"),
  R_DEPARTMENT_TEL("r_department_tel"),
  R_DEPARTMENT_FAX("r_department_fax"),
  R_DEPARTMENT_EMAIL("r_department_email"),
  R_DEPARTMENT_POST("r_department_post"),
  R_DEPARTMENT_SHORT("r_department_short"),
  R_DEPARTMENT_WEBSITE("r_department_website"),
  R_PERSON_NAME("r_person_name"),
  R_PERSON_FIRSTNAME("r_person_firstname"),
  R_INITIALS("r_initials"),
  
  BA_AWARD("ba_award"),
  BA_DIR_DEP("ba_dir_dep"),
  BA_RESERVATION("ba_reservation"),
  
  REFERENCE_PERSON("reference_person"),
  READ_VALUES("read_values"),
  R_CANCEL_ART_NUMBER("r_cancel_art_number"),
  R_CANCEL_ART_DESCRIPTION("r_cancel_art_description"),
  R_CANCEL_REASON("r_cancel_reason"),
  R_CANCEL_DEADLINE("r_cancel_deadline"),
  EXCLUSION_NUMBER("exclusion_number"),
  EXCLUSION_DESCRIPTION("exclusion_description"),
  AWARD_DATE("v2_intentions_award_date"),
  
  O_OFFER_DATE("o_offer_date"),
  O_PRICE_INCREASE("o_price_increase"),
  O_VAT_PERCENT("o_vat_percent"),
  O_VAT_AMOUNT("o_vat_amount"),
  O_NET_AMOUNT("o_net_amount"),
  O_GROSS_AMOUNT("o_gross_amount"),
  O_DISCOUNT("o_discount"),
  O_DISCOUNT2("o_discount2"),
  O_BUILDING_COSTS("o_building_costs"),
  O_ANCILLIARY_AMOUNT_GROSS("o_ancilliary_amount_gross"),
  O_ANCILLIARY_AMOUNT_PERCENT("o_ancilliary_amount_percent"),
  O_ANCILLIARY_AMOUNT_VAT("o_ancilliary_amount_vat"),
  O_ANCILLIARY_AMOUNT_NET("o_ancilliary_amount_net"),
  O_GROSS_AMOUNT_CORRECTED("o_gross_amount_corrected"),
  O_ANCILLIARY_AMOUNT_INCL("o_ancilliary_amount_incl"),
  O_DISCOUNT_TOTAL("o_discount_total"),
  O_DISCOUNT2_TOTAL("o_discount2_total"),
  O_AMOUNT_INCL("o_amount_incl"),
  O_DISCOUNT_PERCENT("o_discount_percent"),
  O_DISCOUNT2_PERCENT("o_discount2_percent"),
  O_VAT_AMOUNT_PERCENT("o_vat_amount_percent"),
  O_SETTLEMENT("o_settlement"),
  O_SUM_OF_APPLICANTS("o_sum_of_applicants"),
  AWARDED_NOTES("awarded_notes"),
  AWARDED_COMPANY_NAME_OR_ARGE("awarded_company_name_or_arge"),
  AWARDED_TOTAL_POINTS("award_total_points"),
  AWARDED_AMOUNT("awarded_amount"),
  AWARDED_OPER_AMOUNT("awarded_oper_amount"),
  AWARDED_OPER_NOTES("awarded_oper_notes"),
  AWARDED_CRITERION_NAME("award_criterion_name"),
  AWARDED_CRITERION_POINTS("award_criterion_points"),
  AWARD_CRITERION_POINTS("award_criterion_points"),
  FIRST_AWARD_DATE("v1_intentions_award_date"),
  FIRST_AWARD_EXCL_REASON("first_level_exclusion_reason"),
  EXCLUDED_SUB_NAME("excluded_sub_name"),
  EXCLUDED_NET_AMOUNT("excluded_net_amount"),
  AWARDED_COMPANY_NAME("awarded_company_name"),
  E_CRITERION_NAME("e_criterion_name"),
  E_CRITERION_POINTS("e_criterion_points"),
  COPY_REFERENCE("copy_reference"),
  DOCUMENT_TITLE("doc_title"),
  R_DIRECTORATE_SHORT("r_directorate_short");
  
  private String value;

  private DocumentPlaceholders(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
