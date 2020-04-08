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
 * @name offer.service.js
 * @description OfferService service
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.offer")
    .factory("OfferService", OfferService);

  /** @ngInject */
  function OfferService($http, AppConstants) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Local functions.
     **********************************************************************/

    /***********************************************************************
     * Exported functions and variables.
     **********************************************************************/

    return {
      deleteSubmittent: function (id) {
        return $http.delete(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/submittent/' + id);
      },
      deleteOffer: function (id) {
        return $http.delete(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/delete/' + id);
      },
      closeOffer: function (id, version) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/close/' + id + '/' + version);
      },
      getOfferById: function (id) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/' + id);
      },
      addSubcontractorToSubmittent: function (submittent) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/subcontractor', submittent);
      },
      deleteSubcontractor: function (submittentId, subcontractorId) {
        return $http.delete(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/submittent/' + submittentId + '/subcontractor/' + subcontractorId);
      },
      addJointVentureToSubmittent: function (submittent) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/jointVenture', submittent);
      },
      deleteJointVenture: function (submittentId, jointVentureId) {
        return $http.delete(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/submittent/' + submittentId + '/jointVenture/' + jointVentureId);
      },
      updateOffer: function (offer) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/update', offer);
      },
      resetOfferValues: function (offer) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/resetOffer', offer, {});
      },
      updateAwardOffers: function (checkedOffersIds, submissionId) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/update/award/' + submissionId, checkedOffersIds, {});
      },
      updateOperatingCostOffer: function (offer) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/operating/update', offer, {});
      },
      reopenOffer: function (reopen, id, version) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/reopen/offer/' + id + '/' + version, reopen, {});
      },
      readActiveSubmittentsBySubmission: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/submittent/submission/' + submissionId, {});
      },
      readAwardSubmittents: function (value) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/awardSubmittents', value);
      },
      reopenManualAward: function (reopenForm, submissionId, submissionVersion) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/reopen/manualAward/' + submissionId + '/' + submissionVersion, reopenForm);
      },
      updateAndGetThreshold: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/update/threshold/' + submissionId, {});
      },
      manualAwardReopenCheck: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/submission/manualAwardReopenCheck/' + submissionId);
      },
      resetOperatingCostValues: function (offer) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/resetOperatingCostValues', offer);
      },
      validateManualAward: function (manualAwardForm) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/validateManualAward', manualAwardForm);
      },
      loadOfferList: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/offer/loadOfferList/' + submissionId);
      }
    }
  }
})();
