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
 * @name project.service.js
 * @description ProjectService service
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.project")
    .factory("ProjectService", ProjectService);

  /** @ngInject */
  function ProjectService($http, AppConstants) {
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
      createProject: function (project) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/project', project);
      },
      readProject: function (id) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/project/' + id);
      },
      deleteProject: function (id) {
        return $http.delete(AppConstants.URLS.RESOURCE_PROVIDER + '/project/' + id);
      },
      search: function (searchForm, page, pageItems, sortColumn, sortType) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/project/search/' + page + '/' + pageItems + '/' + sortColumn + '/' + sortType, searchForm);
      },
      readProjectsNames: function (excludedProject) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/project/allNames/' + excludedProject);
      },
      readAllProjectsCreditNos: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/project/allCreditNumbers');
      },
      readAllProjectManagers: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/project/allProjectManagers');
      },
      readProjects: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/project/all');
      },
      readProjectsByObject: function (objectId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/project/object/' + objectId);
      },
      readProjectsByObjectsIDs: function (objectsIDs) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/project/objects/', objectsIDs);
      },
      hasStatusAfterOfferOpeningClosed: function (projectId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/project/checkStatus/' + projectId, {});
      },
      updateProject: function (project) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/project/update', project);
      }
    };
  }
})();
