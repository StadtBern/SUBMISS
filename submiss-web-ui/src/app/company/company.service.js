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
 * @name company.service.js
 * @description CompanyService service
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.company")
    .factory("CompanyService", CompanyService);

  /** @ngInject */
  function CompanyService($http, AppConstants) {
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
      search: function (searchForm, page, pageItems, sortColumn, sortType) {
        return $http.post(
          AppConstants.URLS.RESOURCE_PROVIDER + '/company/search/' + page + '/' +
          pageItems + '/' + sortColumn + '/' + sortType, searchForm);
      },
      readCompany: function (id) {
        return $http.get(
          AppConstants.URLS.RESOURCE_PROVIDER + '/company/' + id);
      },
      deleteCompany: function (id) {
        return $http.delete(
          AppConstants.URLS.RESOURCE_PROVIDER + '/company/' + id);
      },
      getCompanyNames: function (query) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER +
          '/company/fullTextSearch/company_name/' + query);
      },
      createCompany: function (company) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/company',
          company);
      },
      updateCompany: function (company, archivedPrevious) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/company/update/' + archivedPrevious,
          company);
      },
      getOfferByCompanyId: function (id) {
        return $http.get(
          AppConstants.URLS.RESOURCE_PROVIDER + '/company/offers/' + id);
      },
      getProofsByCompanyId: function (id) {
        return $http.get(
          AppConstants.URLS.RESOURCE_PROVIDER + '/company/proofs/' + id);
      },
      calculateExpirationDate: function (id) {
        return $http.get(
          AppConstants.URLS.RESOURCE_PROVIDER + '/company/expiration/' + id);
      },
      updateProofs: function (proofs) {
        return $http.put(
          AppConstants.URLS.RESOURCE_PROVIDER + '/company/proofs/update',
          proofs);
      },
      getCompanyPostcodes: function (query) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER +
          '/company/fullTextSearch/post_code/' + query);
      },
      getCompanyLocations: function (query) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER +
          '/company/fullTextSearch/location/' + query);
      },
      getCompanyTelephones: function (query) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER +
          '/company/fullTextSearch/company_tel/' + query);
      },
      getCompanyNotes: function (query) {
        return $http.post(
          AppConstants.URLS.RESOURCE_PROVIDER + '/company/fullTextSearch/notes/' +
          query);
      },
      getCompanyTemplates: function (id) {
        return $http.get(
          AppConstants.URLS.RESOURCE_PROVIDER + '/company/getTemplates/' + id);
      },
      openProofKanton: function (id) {
        return $http.post(
          AppConstants.URLS.RESOURCE_PROVIDER + '/email/proof/' + id);
      },
      openCompanyEmail: function (companyIds, template) {
        return $http.post(
          AppConstants.URLS.RESOURCE_PROVIDER + '/email/?' + companyIds,
          template);
      },
      getUserAllowedTemplates: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/company/getUserAllowedTemplates');
      },
      findIfTelephoneUnique: function (company) {
        return $http.post(
          AppConstants.URLS.RESOURCE_PROVIDER + '/company/telephone', company);
      }
    };
  }
})();
