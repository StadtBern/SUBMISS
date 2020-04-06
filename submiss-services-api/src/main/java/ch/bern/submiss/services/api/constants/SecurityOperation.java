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

public enum SecurityOperation {

  PROJECT_SEARCH("ProjectSearch"),
  TENDER_VIEW("TenderView"),
  TENDERER_ADD("TendererAdd"),
  TENDER_AUDIT("TenderAudit"),
  TENDER_CHECKED("TenderChecked"),
  OFFER_OPENING_CLOSE("OfferOpeningClose"),
  OFFER_OPENING_RESTART("OfferOpeningRestart"),
  OFFER_DELETE("OfferDelete"),
  TENDERER_REMOVE("TendererRemove"),
  COMPANY_VIEW("CompanyView"),
  COMPANY_PROOFS_VIEW("CompanyProofsView"),
  AWARDED("Awarded"),
  AWARD_REMOVED("AwardRemoved"),
  RESOURCE_TENANT("ResourceTenant"),
  RESOURCE_PROJECT_VIEW("ResourceProjectView"),
  RESOURCE_USER_VIEW("ResourceUserView"),
  RESOURCE_ROLE_TYPE_USER("ResourceRoleTypeUser"),
  RESOURCE_SUBMISSION_LIST("ResourceSubmissionList"),
  RESOURCE_SUBMISSION_LOCKED("ResourceSubmissionLocked"),
  RESOURCE_SUBMISSION_UNLOCKED("ResourceSubmissionUnlocked"),
  RESOURCE_DOCUMENT_DELETE_DENY("ResourceDocumentDeleteDeny"),
  FORMAL_AUDIT_EXECUTE("FormalAuditExecute"),
  FORMAL_AUDIT_VIEW("FormalAuditView"),
  FORMAL_AUDIT_EDIT("FormalAuditEdit"),
  ADD_CRITERION("AddCriterion"),
  DELETE_CRITERION("DeleteCriterion"),
  UPDATE_CRITERION("UpdateCriterion"),
  ADD_SUBCRITERION("AddSubcriterion"),
  DELETE_SUBCRITERION("DeleteSubcriterion"),
  UPDATE_SUBCRITERION("UpdateSubcriterion"),
  PROJECT_EDIT("ProjectEdit"),
  TENDER_CREATE("TenderCreate"),
  DL_WETTBEWERB_FIELD_TENDER_CREATE("DLWettbewerbFieldTenderCreate"),
  GEKO_EINTRAG_FIELD_TENDER_CREATE("GeKoEintragFieldTenderCreate"),
  TENDER_EDIT("TenderEdit"),
  TENDER_DELETE("TenderDelete"),
  USER_OPERATION_USER_VIEW("UserOperationUserView"),
  ADD_JOINT_VENTURE("AddJointVenture"),
  DELETE_JOINT_VENTURE("DeleteJointVenture"),
  OFFER_DETAILS_EDIT("OfferDetailsEdit"),
  ANCILLIARY_COST_DETAILS_EDIT("AncilliaryCostDetailsEdit"),
  ADMIN_NOTES_VIEW("AdminNotesView"),
  NOTES_VIEW("NotesView"),
  COMPANY_UPDATE("CompanyUpdate"),
  COMPANY_DELETE("CompanyDelete"),
  OPERATING_COST_DETAILS_EDIT("OperationCostDetailsEdit"),
  PROOF_VERIFICATION_REQUEST("ProofVerificationRequest"),
  COMPANY_OFFERS_VIEW("CompanyOffersView"),
  COMPANY_CREATE("CompanyCreate"),
  COMPANY_SEARCH("CompanySearch"),
  ADD_SUBCONTRACTOR("AddSubcontractor"),
  DELETE_SUBCONTRACTOR("DeleteSubcontractor"),
  PROJECT_CREATE("ProjectCreate"),
  PROJECT_DELETE("ProjectDelete"),
  TENDER_LIST_VIEW("TenderListView"),
  MAIN_TENANT_BEMERKUNG_FABE_EDIT("MainTenantBemerkungFabeEdit"),
  MAIN_TENANT_BEMERKUNG_FABE_VIEW("MainTenantBemerkungFabeView"),
  MAIN_TENANT_BESCHAFFUNGSWESEN_EDIT("MainTenantBeschaffungswesenEdit"),
  MAIN_TENANT_BESCHAFFUNGSWESEN_VIEW("MainTenantBeschaffungswesenView"),
  EXTENDED_ACCESS("ExtendedAccess"),
  PENDING("Pending"),
  PROJECT("Project"),
  AUDIT("Audit"),
  EINGABETERMIN_2_FIELD_TENDER_EDIT("Eingabetermin2FieldTenderEdit"),
  FORMAL_AUDIT_CLOSE("FormalAuditClose"),
  EXAMINATION_CLOSE("ExaminationClose"),
  ADD_OPERATING_COST_AWARD_CRITERION("AddOperatingCostAwardCriterion"),
  AWARD_EVALUATION_EDIT("AwardEvaluationEdit"),
  SUITABILITY_EXECUTE("SuitabilityExecute"),
  AWARD_EVALUATION_CLOSE("AwardEvaluationClose"),
  TENDER_MOVE("TenderMove"),
  SENT_EMAIL("SentEmail"),
  AWARD_EVALUATION_REOPEN("AwardEvaluationReopen"),
  EXAMINATION_REOPEN("ExaminationReopen"),
  FORMAL_AUDIT_REOPEN("FormalAuditReopen"),
  COMMISSION_PROCUREMENT_PROPOSAL_VIEW("CommissionProcurementProposalView"),
  COMMISSION_PROCUREMENT_PROPOSAL_EDIT("CommissionProcurementProposalEdit"),
  GEKO_SUBMISSION_FIELD_VIEW("GekoSubmissionFieldView"),
  LOCKED_SUBMISSION_FIELD_VIEW("LockedSubmissionFieldView"),
  PROJECT_DOCUMENTVIEW("ProjectDocumentView"),
  PROJECT_DOCUMENT_UPLOAD("ProjectDocumentUpload"),
  PROJECT_DOCUMENT_DOWNLOAD("ProjectDocumentDownload"),
  PROJECT_DOCUMENT_PRINT("ProjectDocumentPrint"),
  PROJECT_DOCUMENT_DELETE("ProjectDocumentDelete"),
  PROJECT_DOCUMENT_EDIT_PROPERTIES("ProjectDocumentEditProperties"),
  COMPANY_DOCUMENT_UPLOAD("CompanyDocumentUpload"),
  COMPANY_DOCUMENT_DOWNLOAD("CompanyDocumentDownload"),
  COMPANY_DOCUMENT_PRINT("CompanyDocumentPrint"),
  COMPANY_DOCUMENT_DELETE("CompanyDocumentDelete"),
  COMPANY_DOCUMENT_EDIT_PROPERTIES("CompanyDocumentEditProperties"),
  COMMISSION_PROCUREMENT_PROPOSAL_CLOSE("CommissionProcurementProposalClose"),
  COMMISSION_PROCUREMENT_PROPOSAL_REOPEN("CommissionProcurementProposalReopen"),
  COMMISSION_PROCUREMENT_DECISION_VIEW("CommissionProcurementDecisionView"),
  COMMISSION_PROCUREMENT_DECISION_EDIT("CommissionProcurementDecisionEdit"),
  COMMISSION_PROCUREMENT_DECISION_CLOSE("CommissionProcurementDecisionClose"),
  COMMISSION_PROCUREMENT_DECISION_REOPEN("CommissionProcurementDecisionReopen"),
  PROJECT_MULTIPLE_DOCUMENTS_UPLOAD("ProjectMultipleDocumentsUpload"),
  COMPANY_MULTIPLE_DOCUMENTS_UPLOAD("CompanyMultipleDocumentsUpload"),
  PROJECT_MULTIPLE_DOCUMENTS_DOWNLOAD("ProjectMultipleDocumentsDownload"),
  COMPANY_MULTIPLE_DOCUMENTS_DOWNLOAD("CompanyMultipleDocumentsDownload"),
  TENDER_CANCEL_VIEW("TenderCancelView"),
  TENDER_CANCEL_EDIT("TenderCancelEdit"),
  TENDER_CANCEL("TenderCancel"),
  APPLICANTS_VIEW("ApplicantsView"),
  APPLICANTS_ADD("ApplicantsAdd"),
  APPLICANT_DELETE("ApplicantDelete"),
  TENDER_CLOSE("TenderClose"),
  TENDER_REOPEN("TenderReopen"),
  GENERATE_REPORT("GenerateReport"),
  APPLICATION_VIEW("ApplicationView"),
  APPLICATION_EDIT("ApplicationEdit"),
  APPLICATION_DELETE("ApplicationDelete"),
  APPLICATION_OPENING_CLOSE("ApplicationOpeningClose"),
  AWARD_INFO_EDIT("AwardInfoEdit"),
  AWARD_INFO_VIEW("AwardInfoView"),
  APPLICATION_OPENING_REOPEN("ApplicationOpeningReopen"),
  AWARD_INFO_FIRST_LEVEL_EDIT("AwardInfoFirstLevelEdit"),
  AWARD_INFO_FIRST_LEVEL_VIEW("AwardInfoFirstLevelView"),
  GENERATE_OPERATIONS_REPORT("GenerateOperationsReport"),
  GENERATE_USERS_EXPORT_REPORT("GenerateUsersExportReport");

  private String value;

  private SecurityOperation(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
