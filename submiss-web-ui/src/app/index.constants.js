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

(function () {
  "use strict";

  angular
    .module("submiss")
    .constant("AppConstants", {
      URLS: {
        /** The prefix of the REST/CXF server */
        RESOURCE_PROVIDER: "/api",
        AUDIT_LOGS: "/audit/logs/"
      },

      SECURITY: {
        /** The name of the Cookie set by SSO Filters and used by RACS */
        SSO_COOKIE_NAME: "org.apache.cxf.websso.context",

        /** The name of the Cookie holding the URL to redirect after auth */
        REDIRECT_TO_COOKIE_NAME: "redirectTo",

        /** The endpoint to trigger authentication. As this is a browser
         *  redirect (and not simply a state change) we need the full URL here. */
        SSO_AUTH_ENDPOINT: "/api/auth/login"
      },

      USER_STATUS: {
        DOES_NOT_EXIST: -1,
        DISABLED: 0,
        DISABLED_APPROVED: 1,
        ENABLED_NOT_APPROVED: 2,
        ENABLED_APPROVED: 3
      },

      STATUS: {},

      PROCESS_MAP: {
        SELECTIVE: "SelectiveTender",
        OPEN: "OpenTender",
        INVITATION: "InvitedTender",
        NEGOTIATED_PROCEDURE: "NegotiatedTender",
        NEGOTIATED_PROCEDURE_WITH_COMPETITION: "NegotiatedCompetitionTender"
      },

      PROCESS: {
        SELECTIVE: "SELECTIVE",
        OPEN: "OPEN",
        INVITATION: "INVITATION",
        NEGOTIATED_PROCEDURE: "NEGOTIATED_PROCEDURE",
        NEGOTIATED_PROCEDURE_WITH_COMPETITION: "NEGOTIATED_PROCEDURE_WITH_COMPETITION"
      },

      OPERATION: {
        PROJECT_SEARCH: "ProjectSearch",
        TENDER_VIEW: "TenderView",
        TENDERER_ADD: "TendererAdd",
        TENDER_AUDIT: "TenderAudit",
        TENDER_CHECKED: "TenderChecked",
        OFFER_OPENING_CLOSE: "OfferOpeningClose",
        OFFER_OPENING_RESTART: "OfferOpeningRestart",
        OFFER_DELETE: "OfferDelete",
        TENDERER_REMOVE: "TendererRemove",
        COMPANY_VIEW: "CompanyView",
        COMPANY_PROOFS_VIEW: "CompanyProofsView",
        AWARDED: "Awarded",
        AWARD_REMOVED: "AwardRemoved",
        RESOURCE_TENANT: "ResourceTenant",
        RESOURCE_PROJECT_VIEW: "ResourceProjectView",
        RESOURCE_USER_VIEW: "ResourceUserView",
        RESOURCE_ROLE_TYPE_USER: "ResourceRoleTypeUser",
        FORMAL_AUDIT_EXECUTE: "FormalAuditExecute",
        FORMAL_AUDIT_VIEW: "FormalAuditView",
        FORMAL_AUDIT_EDIT: "FormalAuditEdit",
        ADD_CRITERION: "AddCriterion",
        UPDATE_CRITERION: "UpdateCriterion",
        DELETE_CRITERION: "DeleteCriterion",
        ADD_SUBCRITERION: "AddSubcriterion",
        UPDATE_SUBCRITERION: "UpdateSubcriterion",
        DELETE_SUBCRITERION: "DeleteSubcriterion",
        PROJECT_EDIT: "ProjectEdit",
        TENDER_CREATE: "TenderCreate",
        DL_WETTBEWERB_FIELD_TENDER_CREATE: "DLWettbewerbFieldTenderCreate",
        GEKO_EINTRAG_FIELD_TENDER_CREATE: "GeKoEintragFieldTenderCreate",
        TENDER_EDIT: "TenderEdit",
        TENDER_DELETE: "TenderDelete",
        USER_OPERATION_USER_VIEW: "UserOperationUserView",
        ADD_JOINT_VENTURE: "AddJointVenture",
        DELETE_JOINT_VENTURE: "DeleteJointVenture",
        OFFER_DETAILS_EDIT: "OfferDetailsEdit",
        ANCILLIARY_COST_DETAILS_EDIT: "AncilliaryCostDetailsEdit",
        ADMIN_NOTES_VIEW: "AdminNotesView",
        NOTES_VIEW: "NotesView",
        COMPANY_UPDATE: "CompanyUpdate",
        COMPANY_DELETE: "CompanyDelete",
        OPERATING_COST_DETAILS_EDIT: "OperationCostDetailsEdit",
        PROOF_VERIFICATION_REQUEST: "ProofVerificationRequest",
        COMPANY_OFFERS_VIEW: "CompanyOffersView",
        COMPANY_CREATE: "CompanyCreate",
        COMPANY_SEARCH: "CompanySearch",
        ADD_SUBCONTRACTOR: "AddSubcontractor",
        DELETE_SUBCONTRACTOR: "DeleteSubcontractor",
        PROJECT_CREATE: "ProjectCreate",
        PROJECT_DELETE: "ProjectDelete",
        TENDER_LIST_VIEW: "TenderListView",
        MAIN_TENANT_BEMERKUNG_FABE_EDIT: "MainTenantBemerkungFabeEdit",
        MAIN_TENANT_BEMERKUNG_FABE_VIEW: "MainTenantBemerkungFabeView",
        MAIN_TENANT_BESCHAFFUNGSWESEN_EDIT: "MainTenantBeschaffungswesenEdit",
        MAIN_TENANT_BESCHAFFUNGSWESEN_VIEW: "MainTenantBeschaffungswesenView",
        EXTENDED_ACCESS: "ExtendedAccess",
        PENDING: "Pending",
        PROJECT: "Project",
        AUDIT: "Audit",
        EINGABETERMIN_2_FIELD_TENDER_EDIT: "Eingabetermin2FieldTenderEdit",
        FORMAL_AUDIT_CLOSE: "FormalAuditClose",
        EXAMINATION_CLOSE: "ExaminationClose",
        ADD_OPERATING_COST_AWARD_CRITERION: "AddOperatingCostAwardCriterion",
        AWARD_EVALUATION_EDIT: "AwardEvaluationEdit",
        SUITABILITY_EXECUTE: "SuitabilityExecute",
        AWARD_EVALUATION_CLOSE: "AwardEvaluationClose",
        TENDER_MOVE: "TenderMove",
        SENT_EMAIL: "SentEmail",
        AWARD_EVALUATION_REOPEN: "AwardEvaluationReopen",
        EXAMINATION_REOPEN: "ExaminationReopen",
        FORMAL_AUDIT_REOPEN: "FormalAuditReopen",
        COMMISSION_PROCUREMENT_PROPOSAL_VIEW: "CommissionProcurementProposalView",
        COMMISSION_PROCUREMENT_PROPOSAL_EDIT: "CommissionProcurementProposalEdit",
        GEKO_SUBMISSION_FIELD_VIEW: "GekoSubmissionFieldView",
        LOCKED_SUBMISSION_FIELD_VIEW: "LockedSubmissionFieldView",
        PROJECT_DOCUMENTVIEW: "ProjectDocumentView",
        PROJECT_DOCUMENT_UPLOAD: "ProjectDocumentUpload",
        PROJECT_DOCUMENT_DOWNLOAD: "ProjectDocumentDownload",
        PROJECT_DOCUMENT_PRINT: "ProjectDocumentPrint",
        PROJECT_DOCUMENT_DELETE: "ProjectDocumentDelete",
        PROJECT_DOCUMENT_EDIT_PROPERTIES: "ProjectDocumentEditProperties",
        COMPANY_DOCUMENT_UPLOAD: "CompanyDocumentUpload",
        COMPANY_DOCUMENT_DOWNLOAD: "CompanyDocumentDownload",
        COMPANY_DOCUMENT_PRINT: "CompanyDocumentPrint",
        COMPANY_DOCUMENT_DELETE: "CompanyDocumentDelete",
        COMPANY_DOCUMENT_EDIT_PROPERTIES: "CompanyDocumentEditProperties",
        COMMISSION_PROCUREMENT_PROPOSAL_CLOSE: "CommissionProcurementProposalClose",
        COMMISSION_PROCUREMENT_PROPOSAL_REOPEN: "CommissionProcurementProposalReopen",
        COMMISSION_PROCUREMENT_DECISION_VIEW: "CommissionProcurementDecisionView",
        COMMISSION_PROCUREMENT_DECISION_EDIT: "CommissionProcurementDecisionEdit",
        COMMISSION_PROCUREMENT_DECISION_CLOSE: "CommissionProcurementDecisionClose",
        COMMISSION_PROCUREMENT_DECISION_REOPEN: "CommissionProcurementDecisionReopen",
        PROJECT_MULTIPLE_DOCUMENTS_UPLOAD: "ProjectMultipleDocumentsUpload",
        COMPANY_MULTIPLE_DOCUMENTS_UPLOAD: "CompanyMultipleDocumentsUpload",
        PROJECT_MULTIPLE_DOCUMENTS_DOWNLOAD: "ProjectMultipleDocumentsDownload",
        COMPANY_MULTIPLE_DOCUMENTS_DOWNLOAD: "CompanyMultipleDocumentsDownload",
        TENDER_CANCEL_VIEW: "TenderCancelView",
        TENDER_CANCEL_EDIT: "TenderCancelEdit",
        TENDER_CANCEL: "TenderCancel",
        APPLICANTS_VIEW: "ApplicantsView",
        APPLICANTS_ADD: "ApplicantsAdd",
        APPLICANT_DELETE: "ApplicantDelete",
        TENDER_CLOSE: "TenderClose",
        TENDER_REOPEN: "TenderReopen",
        GENERATE_REPORT: "GenerateReport",
        APPLICATION_VIEW: "ApplicationView",
        APPLICATION_EDIT: "ApplicationEdit",
        LEGAL_VIEW: "LegalView",
        APPLICATION_DELETE: "ApplicationDelete",
        APPLICATION_OPENING_CLOSE: "ApplicationOpeningClose",
        AWARD_INFO_EDIT: "AwardInfoEdit",
        AWARD_INFO_VIEW: "AwardInfoView",
        APPLICATION_OPENING_REOPEN: "ApplicationOpeningReopen",
        AWARD_INFO_FIRST_LEVEL_EDIT: "AwardInfoFirstLevelEdit",
        AWARD_INFO_FIRST_LEVEL_VIEW: "AwardInfoFirstLevelView",
        GENERATE_OPERATIONS_REPORT: "GenerateOperationsReport",
        GENERATE_USERS_EXPORT_REPORT: "GenerateUsersExportReport"
      },

      GROUP: {
        PL: "PL",
        ADMIN: "Admin",
        SB: "SB",
        DIR: "Dir"
      },

      CRITERION_TYPE: {
        SUITABILITY: "suitability",
        AWARD: "award",
        AWARD_CRITERION_TYPE: "awardCriterion",
        MUST_CRITERION_TYPE: "mustCriterion",
        EVALUATED_CRITERION_TYPE: "evaluatedCriterion",
        PRICE_AWARD_CRITERION_TYPE: "priceAwardCriterion",
        OPERATING_COST_AWARD_CRITERION_TYPE: "operatingCostAwardCriterion"
      },

      ROUND_DECIMALS: {
        GRADE: 2,
        SCORE: 3
      },

      STATUS_TO_REOPEN: {
        APPLICATION_OPENING: 1,
        OFFER_OPENING: 2,
        SUITABILITY_AUDIT: 3,
        FORMAL_AUDIT: 4,
        AWARD_EVALUATION: 5,
        COMMISSION_PROCUREMENT_PROPOSAL: 6,
        COMMISSION_PROCUREMENT_DECISION: 7,
        PROCESS: 8,
        MANUAL_AWARD: 9
      },

      STATUS_REOPEN_QUESTION: {
        APPLICATION_OPENING: "Möchten Sie die Bewerbungsöffnung wirklich wiederaufnehmen?",
        OFFER_OPENING: "Möchten Sie die Offertöffnung wiederaufnehmen?",
        SUITABILITY_AUDIT: "Möchten Sie die Eignungsprüfung wiederaufnehmen?",
        FORMAL_AUDIT: "Möchten Sie die formelle Prüfung wiederaufnehmen?",
        AWARD_EVALUATION: "Möchten Sie die Zuschlagsbewertung wiederaufnehmen?",
        COMMISSION_PROCUREMENT_PROPOSAL: "Möchten Sie den Beschaffungsantrag wirklich wiederaufnehmen?",
        COMMISSION_PROCUREMENT_DECISION: "Möchten Sie den BeKo Beschluss wirklich wiederaufnehmen?",
        PROCESS: "Möchten Sie diese Submission wirklich wiederaufnehmen?",
        MANUAL_AWARD: "Möchten Sie die Zuschlagserteilung wirklich aufheben und die Zuschlagserteilung wiederaufnehmen?"
      },

      STATUS_REOPEN_TITLE: {
        APPLICATION_OPENING: "Bewerbungsöffnung wiederaufnehmen",
        OFFER_OPENING: "Offertöffnung wiederaufnehmen",
        SUITABILITY_AUDIT: "Eignungsprüfung wiederaufnehmen",
        FORMAL_AUDIT: "Formelle Prüfung wiederaufnehmen",
        AWARD_EVALUATION: "Zuschlagsbewertung wiederaufnehmen",
        COMMISSION_PROCUREMENT_PROPOSAL: "Beschaffungsantrag wiederaufnehmen",
        COMMISSION_PROCUREMENT_DECISION: "BeKo Beschluss wiederaufnehmen",
        PROCESS: "Verfahren wiederaufnehmen",
        MANUAL_AWARD: "Manuelle Zuschlagserteilung aufheben"
      },

      AWARD_DISPLAYED_OFFERS: 5,

      PROOF_REQUEST_FT: "Nachweisbrief",
      CERTIFICATE: "Zertifikat",
      WORKTYPE_LIST: "Liste Arbeitsgattung",
      COMPANYFILE_SIMPLE: "Firmenblatt einfach",
      COMPANYFILE_EXTENDED: "Firmenblatt erweitert",
      SUBMITTENTENLISTE_POSTLISTE: "Submittentenliste (Postliste)",
      SUBMITTENTENLISTE_ALS_ETIKETTEN: "Submittentenliste als Etiketten (3652)",
      RECHTLICHES_GEHOER: "PT19",
      VERFAHRENSABBRUCH: "PT06",
      BEKO_ANTRAG: "PT02",
      BEKO_BESCHLUSS: "PT05",
      COMPANIES_CREATED_PER_MANDANT: "FT06",
      FIRMENLISTE_KOMPLETT: "FT07",
      FIRMENLISTE_NACH_SUCHRESULTAT: "FT08",
      LISTE_WINBAU: "FT09",
      LISTE_ARBEITSGATTUNG: "FT05",
      VERTRAG: "PT21",
      OFFERRTOFFNUNGSPROTOKOLL: "PT08",
      A_OFFERRTOFFNUNGSPROTOKOLL: "PT09",
      OFFERRTOFFNUNGSPROTOKOLL_DL_WW: "PT10",
      A_OFFERRTOFFNUNGSPROTOKOLL_DL_WW: "PT11",
      MASTER_LIST_TYPE: {
        EXCLUSION_REASON: "Ausschlussgrund",
        CANCELATION_REASON: "Abbruchgrund"
      },

      locales: {
        de: {
          "DATETIME_FORMATS": {
            "AMPMS": ["vorm.", "nachm."],
            "DAY": ["Sonntag", "Montag", "Dienstag", "Mittwoch",
              "Donnerstag", "Freitag", "Samstag"
            ],
            "ERANAMES": ["v. Chr.", "n. Chr."],
            "ERAS": ["v. Chr.", "n. Chr."],
            "FIRSTDAYOFWEEK": 0,
            "MONTH": ["Januar", "Februar", "M\u00e4rz", "April",
              "Mai", "Juni", "Juli", "August", "September",
              "Oktober", "November", "Dezember"
            ],
            "SHORTDAY": ["So.", "Mo.", "Di.", "Mi.", "Do.", "Fr.",
              "Sa."
            ],
            "SHORTMONTH": ["Jan.", "Feb.", "M\u00e4rz", "Apr.",
              "Mai", "Juni", "Juli", "Aug.", "Sep.", "Okt.",
              "Nov.", "Dez."
            ],
            "STANDALONEMONTH": ["Januar", "Februar", "M\u00e4rz",
              "April", "Mai", "Juni", "Juli", "August",
              "September", "Oktober", "November", "Dezember"
            ],
            "WEEKENDRANGE": [5, 6],
            "fullDate": "EEEE, d. MMMM y",
            "longDate": "d. MMMM y",
            "medium": "dd.MM.y HH:mm:ss",
            "mediumDate": "dd.MM.y",
            "mediumTime": "HH:mm:ss",
            "short": "dd.MM.yy HH:mm",
            "shortDate": "dd.MM.yy",
            "shortTime": "HH:mm"
          },
          "NUMBER_FORMATS": {
            "CURRENCY_SYM": "CHF",
            "DECIMAL_SEP": ".",
            "GROUP_SEP": "\u2019",
            "PATTERNS": [{
              "gSize": 3,
              "lgSize": 3,
              "maxFrac": 3,
              "minFrac": 0,
              "minInt": 1,
              "negPre": "-",
              "negSuf": "",
              "posPre": "",
              "posSuf": ""
            }, {
              "gSize": 3,
              "lgSize": 3,
              "maxFrac": 2,
              "minFrac": 2,
              "minInt": 1,
              "negPre": "\u00a4-",
              "negSuf": "",
              "posPre": "\u00a4\u00a0",
              "posSuf": ""
            }]
          },
          "id": "de-ch",
          "localeID": "de_CH",
          "pluralCat": function (n) {
            if (n >= 0 && n <= 2 && n !== 2) {
              return PLURAL_CATEGORY.ONE;
            }
            return PLURAL_CATEGORY.OTHER;
          }
        }
      },

      HTTP_RESPONSES: {
        BAD_REQUEST: 400,
        NO_CONTENT: 204
      },

      DAYS_TO_PERMIT_SUBMISSION_CLOSE: 20,

      UNDEFINED: "undefined",
      IMPORT_ERROR: "Import Fehlermeldung",
      REOPEN_MESSAGE_START: "Wiederaufnahme zum ",
      AND: " und ",
      REOPEN_MESSAGE_END_CANCEL: " nach Verfahrensabbruch",
      REOPEN_MESSAGE_END_CLOSE: " nach Verfahrensabschluss",

      CANCELLED_MESSAGE_START: "Verfahren abgebrochen am ",

      EXCLUSION_REASON_B: "Lit. b",
      EXCLUSION_REASON_C: "Lit. c",

      TASK_TYPES: {},

      USER_DEFINED: "Benutzerdefiniert",
      CUSTOM_FORMULA_PRICE_TITLE: "Benutzerdefinierte Preisformel",
      CUSTOM_FORMULA_OPERATING_COSTS_TITLE: "Benutzerdefinierte Betriebskostenformel"

    });
})();
