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
 * @name nachtrag.service.js
 * @description NachtragService service
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document")
    .factory("NachtragService", NachtragService);

  /** @ngInject */
  function NachtragService($http, AppConstants) {
    var checkedNachtrag;
    return {
      createNachtrag: function (offerId) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/nachtrag/' + offerId);
      },
      addNachtragSubmittent: function (offerId) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/nachtrag/submittent/' + offerId);
      },
      getAwardedOffers: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/nachtrag/awardedOffers/' + submissionId);
      },
      getNachtrag: function (nachtragId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/nachtrag/' + nachtragId);
      },
      getNachtragSubmittents: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/nachtrag/submittents/' + submissionId);
      },
      getNachtragList: function (offerId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/nachtrag/submittent/' + offerId);
      },
      deleteNachtrag: function (nachtragId, version) {
        return $http.delete(AppConstants.URLS.RESOURCE_PROVIDER + '/nachtrag/' + nachtragId + '/' + version);
      },
      closeNachtrag: function (nachtragId, version) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/nachtrag/close/' + nachtragId + '/' + version);
      },
      reopenNachtrag: function (nachtragId, version, reopenForm) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/nachtrag/reopen/' + nachtragId + '/' + version, reopenForm);
      },
      updateNachtrag: function (nachtragForm) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/nachtrag', nachtragForm);
      },
      getCheckedNachtragForDocCreation: function () {
        return checkedNachtrag;
      },
      setCheckedNachtragForDocCreation: function (checkednachtrag) {
        checkedNachtrag = checkednachtrag;
      }
    };
  }
})();
