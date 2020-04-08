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

/**
 * @ngdoc service
 * @name commissionProcurementProposal.service.js
 * @description CommissionProcurementProposalService service
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document")
    .factory("CommissionProcurementProposalService", CommissionProcurementProposalService);

  /** @ngInject */
  function CommissionProcurementProposalService($http, AppConstants) {
    return {
      updateCommissionProcurementProposal: function (commissionProcurementForm, submissionId, submissionVersion) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/commissionProcurement/updateCommissionProcurementProposal/' + submissionId + '/' + submissionVersion, commissionProcurementForm);
      },
      getOffersBySubmission: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/commissionProcurement/submissionOffers/' + submissionId);
      },
      getSubmission: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/commissionProcurement/' + submissionId);
      },
      getSuitabilityAuditDropdownChoices: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/commissionProcurement/suitabilityAuditDropdownChoices');
      },
      closeProposalNoErrors: function (submissionId) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/commissionProcurement/closeProposalNoErrors/' + submissionId);
      },
      closeCommissionProcurementProposal: function (submissionId, submissionVersion) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/commissionProcurement/closeCommissionProcurementProposal/' + submissionId + '/' + submissionVersion);
      },
      reopenCommissionProcurementProposal: function (reopen, submissionId, submissionVersion) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/commissionProcurement/reopen/commissionProcurementProposal/' + submissionId + '/' + submissionVersion, reopen);
      },
      updateCommissionProcurementDecision: function (commissionProcurementDecisionForm, submissionId, submissionVersion) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/commissionProcurement/updateCommissionProcurementDecision/' +
          submissionId + '/' + submissionVersion, commissionProcurementDecisionForm);
      },
      closeDecisionNoErrors: function (submissionId) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/commissionProcurement/closeDecisionNoErrors/' + submissionId);
      },
      closeCommissionProcurementDecision: function (submissionId, submissionVersion) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/commissionProcurement/closeCommissionProcurementDecision/' + submissionId + '/' + submissionVersion);
      },
      reopenCommissionProcurementDecision: function (reopen, submissionId, submissionVersion) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/commissionProcurement/reopen/commissionProcurementDecision/' + submissionId + '/' + submissionVersion, reopen);
      }
    };
  }
})();
