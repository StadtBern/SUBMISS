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
 * @name legalHearing.service.js
 * @description LegalHearingService service
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document")
    .factory("LegalHearingService", LegalHearingService);

  /** @ngInject */
  function LegalHearingService($http, AppConstants) {
    return {
      createLegalHearingTerminate: function (terminate, submissionId) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/legal/hearing/terminate/' + submissionId, terminate);
      },
      updateLegalHearingTerminate: function (terminate, submissionId) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/legal/hearing/terminate/' + submissionId, terminate);
      },
      getSubmissionLegalHearingTermination: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/legal/hearing/terminate/' + submissionId);
      },
      getExcludedSubmittents: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/legal/exclude/' + submissionId);
      },
      addExcludedSubmittent: function (submittentId) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/legal/exclude/submittent/' + submittentId);
      },
      updateExcludedSubmittent: function (exclusion) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/legal/exclude/update/', exclusion);
      },
      getExclusionDeadline: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/legal/deadline/' + submissionId);
      },
      addExcludedApplicant: function (applicantId) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/legal/exclude/applicant/' + applicantId);
      }
    };
  }
})();
