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
 * @name StammdatenService.factory.js
 * @description StammdatenServiceService factory
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.stammdaten")
    .factory("StammdatenService", StammdatenService);

  /** @ngInject */
  function StammdatenService($http, AppConstants) {
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
      readAllObjects: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/object');
      },
      readAllProcedures: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/procedure');
      },
      readAllDepartments: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/department');
      },
      readAllDirectorates: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/directorate');
      },
      readAllCountries: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/country');
      },
      readAllWorkTypes: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/worktype');
      },
      readAllILO: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/ilo');
      },
      readAllProcessTypes: function (projectId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/processtype/' + projectId);
      },
      readAllVats: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/vat');
      },
      readAllNegotiatedProcedures: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/negotiatedProcedure');
      },
      readDirectoratesByDepartmentsIDs: function (departmentIDs) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/directorate/department', departmentIDs);
      },
      readDirectoratesByIDs: function (directoratesIDs) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/directorate/id', directoratesIDs);
      },
      readDepartmentsByDirectoratesIDs: function (directoratesIDs) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/department/directorate', directoratesIDs);
      },
      readLogib: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/logib/logib');
      },
      readLogibArgib: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/logib/logibArgib');
      },
      readDepartmentsByTenant: function (tenantId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/department/tenant/' + tenantId);
      },
      readTenantByDepartment: function (departmentId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/tenant/department/' + departmentId);
      },
      readDirectoratesByTenant: function (tenantId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/directorate/tenant/' + tenantId);
      },
      readTenantByDirectorate: function (directorateId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/tenant/directorate/' + directorateId);
      },
      readObjectsByProjectsIDs: function (projectsIDs) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/object/projects', projectsIDs);
      },
      getMasterListHistoryByType: function (type) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/masterList/' + type);
      },
      getMasterListTypes: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/getMasterListTypes');
      },
      getMasterListTypeData: function (type) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/getMasterListTypeData/' + type);
      },
      getTypeDataEntryById: function (entryId, type) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/getTypeDataEntryById/' + entryId + "/" + type);
      },
      saveDepartmentEntry: function (entry, isNameOrShortNameChanged) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/department/saveDepartmentEntry/' + isNameOrShortNameChanged, entry);
      },
      saveDirectorateEntry: function (entry, saveDirectorateEntry) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/directorate/saveDirectorateEntry/' + saveDirectorateEntry, entry);
      },
      saveCountryEntry: function (entry, isNameChanged) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/country/saveCountryEntry/' + isNameChanged, entry);
      },
      saveLogibEntry: function (entry) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/logib/saveLogibEntry', entry);
      },
      saveProofsEntry: function (entry, isNameOrCountryChanged) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/proof/saveProofsEntry/' + isNameOrCountryChanged, entry);
      },
      saveEmailTemplateEntry: function (entry) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/email/saveEmailTemplateEntry', entry);
      },
      saveSDEntry: function (entry, type, isValueChanged) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/saveSDEntry/' + type + '/' + isValueChanged, entry);
      },
      getSignaturesByDirectorateId: function (directorateId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/getSignaturesByDirectorateId/' + directorateId);
      },
      getReasonFreeAwards: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/negotiatedProcedure/all');
      },
      getCancelationreasons: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/cancelReason/all');
      },
      getProccessDataEntryById: function (entryId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/getProccessDataEntryById/' + entryId);
      },
      updateProcedureValue: function (procedure) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/updateProcedureValue', procedure);
      },
      getSignatureCopiesById: function (signatureId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/getSignaturesCopiesBySignatureId/' + signatureId);
      },
      getSignatureById: function (signatureId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/getSignatureById/' + signatureId);
      },
      getAllActiveDepartemntsByUserTenant: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/department/getAllActiveDepartemntsByUserTenant/');
      },
      getAllDirectoratesByUserTenant: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/directorate/getAllDirectoratesByUserTenant/');
      },
      updateSignature: function (signature) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/updateSignature', signature);
      },
      updateSignatureCopies: function (signature) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/updateSignatureCopies', signature);
      },
      uploadSelectedImage: function (uploadedImageId) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/uploadSelectedImage/' + uploadedImageId);
      },
      getMasterListValueHistoryDataBySubmission: function (submissionId, type) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/getMasterListValueHistoryDataBySubmission/' + submissionId + '/' + type);
      },
      getVatsBySubmission: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/vat/getVatsBySubmission/' + submissionId);
      },
      isTenantKantonBern: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/tenant/isTenantKantonBern');
      },
      isFirstCountryProof: function (countryId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/proof/first/' + countryId);
      },
      readDepartmentsByUserAndRole: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/department/role');
      },
      getCurrentMLVHEntriesForSubmission: function (submissionId, typeName) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/getCurrentMLVHEntriesForSubmission/' + submissionId + '/' + typeName);
      },
      loadSD: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/loadSD');
      }
    };
  }
})();
