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
 * @name award.service.js
 * @description AwardService service
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.award")
    .factory("AwardService", AwardService);

  /** @ngInject */
  function AwardService($http, AppConstants) {

    return {
      updateAward: function (award) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/awardCriterion', award);
      },
      canOperatingCostAwardCriterionBeAdded: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/submission/operatingCostAwardCriterion/' + submissionId);
      },
      getCalculationFormulas: function (tenantId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/submission/calculationFormulas/' + tenantId);
      },
      updateOfferCriteriaAward: function (awardAssessList) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/awardAssess/', awardAssessList);
      },
      closeAwardEvaluation: function (awardedOfferIds, submissionId) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/closeAwardEvaluation/' + submissionId, awardedOfferIds);
      },
      awardEvaluationCloseNoErrors: function (criteria, submissionId, awardMinGrade, awardMaxGrade) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/awardEvaluationCloseNoErrors/' +
          submissionId + '/' + awardMinGrade + '/' + awardMaxGrade, criteria);
      },
      reopenAwardEvaluation: function (reopen, submissionId) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/reopen/awardEvaluation/' + submissionId, reopen);
      },
      updateCustomCalculationFormula: function (calculationFormula, isPrice, submissionId) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/updateCustomCalculationFormula/' + isPrice +
          '/' + submissionId, calculationFormula);
      }
    };
  }
})();
