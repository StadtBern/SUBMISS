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
 * @name selective.service.js
 * @description SelectiveService service
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss")
    .factory("SelectiveService", SelectiveService);

  /** @ngInject */
  function SelectiveService($http, AppConstants) {

    return {
      updateApplication: function (application) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/updateApplication', application);
      },
      deleteApplicant: function (id) {
        return $http.delete(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/deleteApplicant/' + id);
      },
      deleteApplication: function (applicationId, applicationVersion) {
        return $http.delete(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/deleteApplication/' + applicationId + '/' + applicationVersion);
      },
      closeApplicationOpening: function (submissionId, submissionVersion) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/closeApplicationOpening/' + submissionId + '/' + submissionVersion);
      },
      hasApplicationOpeningBeenClosedBefore: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/hasApplicationOpeningBeenClosedBefore/' + submissionId);
      },
      reopenApplicationOpening: function (reopenForm, submissionId, submissionVersion) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/reopenApplicationOpening/' + submissionId + '/' + submissionVersion, reopenForm);
      },
      getApplicantsForFormalAudit: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/getApplicantsForFormalAudit/' + submissionId);
      },
      updateSelectiveFormalAudit: function (submittents) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/updateSelectiveFormalAudit', submittents);
      }
    };
  }
})();
