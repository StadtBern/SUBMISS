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
 * @name examination.service.js
 * @description ExaminationService service
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.examination")
    .factory("ExaminationService", ExaminationService);

  /** @ngInject */
  function ExaminationService($http, AppConstants) {
    return {
      addCriterionToSubmission: function (criterion) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/criterion/add', criterion);
      },
      readCriteriaOfSubmission: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/submission/' + submissionId);
      },
      updateFormalAuditExamination: function (submittents) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/examination/update', submittents, {});
      },
      updateSubmissionFormalAuditExaminationStatus: function (submissionId) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/examination/formal/' + submissionId);
      },
      deleteCriterion: function (id, pageRequestedOn) {
        return $http.delete(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/criterion/' + id + '/' + pageRequestedOn);
      },
      updateExamination: function (examination) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/criterion/update', examination);
      },
      readSubcriteriaOfCriterion: function (criterionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/subcriterion/' + criterionId);
      },
      deleteSubcriterion: function (id, pageRequestedOn) {
        return $http.delete(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/subcriterion/' + id + '/' + pageRequestedOn);
      },
      addSubcriterionToCriterion: function (submissionId, submissionVersion, subcriterion) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/' + submissionId + '/' + submissionVersion + '/subcriterion/add', subcriterion);
      },
      closeFormalAuditValidations: function (submissionId, submissionVersion) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/formal/closeValidations/' + submissionId + '/' + submissionVersion);
      },
      closeFormalAudit: function (createVersion, submissionId, submissionVersion) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/formal/close/' + createVersion + '/' + submissionId + '/' + submissionVersion);
      },
      closeExaminationValidations: function (examinationForm) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/examination/closeValidations', examinationForm);
      },
      closeExamination: function (examinationForm, createVersion) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/examination/close/' + createVersion, examinationForm);
      },
      readOfferCriteria: function (submissionId, type) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/qualification/' + submissionId + '/' + type + '/offerCriteria');
      },
      exportOfferCriteria: function (submissionId, type) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/export/' + submissionId + '/' + type, {
          responseType: 'arraybuffer',
          headers: {
            'Accept': 'application/octet-stream'
          }
        });
      },
      exportEMLOfferCriteria: function (submissionId, type) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/exporteml/' + submissionId + '/' + type, {
          responseType: 'arraybuffer',
          headers: {
            'Accept': 'application/octet-stream'
          }
        });
      },
      importOfferCriteria: function (submissionId, fileId, type) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/import/' + submissionId + '/' + fileId + '/' + type);
      },
      checkforchangesCriteria: function (submissionId, fileId, type) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/checkforchanges/' + submissionId + '/' + fileId + '/' + type);

      },
      updateOfferCriteria: function (suitabilities, submissionId, pageRequestedOn) {
        return $http.post(
          AppConstants.URLS.RESOURCE_PROVIDER + '/examination/qualification/' + submissionId + '/' + pageRequestedOn + '/offerCriteria', suitabilities);
      },
      reopenFormalAudit: function (reopen, id, version) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/reopen/formal/' + id + '/' + version, reopen);
      },
      reopenExamination: function (reopen, id, version) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/reopen/examination/' + id + '/' + version, reopen);
      },
      getExaminationSubmittentListWithCriteria: function (submissionId, type, all) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/qualification/' + submissionId + '/' + type + '/view/' + all);
      },
      generateProofDocNegotiatedProcedure: function (submittents) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/generate/proofs', submittents, {});
      },
      examinationLockedByAnotherUser: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/examinationLockedByAnotherUser/' + submissionId);
      },
      awardLockedByAnotherUser: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/awardLockedByAnotherUser/' + submissionId);
      },
      checkExaminationOptimisticLock: function (submissionId, pageRequestedOn, submissionVersion) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/examination/optimisticLock/' + submissionId + '/' + pageRequestedOn + '/' + submissionVersion);
      }
    };
  }
})();
