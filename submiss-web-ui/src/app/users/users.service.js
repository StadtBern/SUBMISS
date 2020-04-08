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
 * @name users.service.js
 * @description UserAdministrationService service
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.users")
    .factory("UsersService", UserAdministrationService);

  /** @ngInject */
  function UserAdministrationService($http, AppConstants) {
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

      createUser: function (user) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/users/create',
          user);
      },

      editUser: function (user) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/users/edit',
          user);
      },

      countUsers: function (form) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/users/count',
          form);
      },

      search: function (searchForm) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/users/search', searchForm);
      },

      exportUsers: function (exportForm) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/users/export', exportForm, {
          responseType: 'arraybuffer',
          headers: {
            'Accept': 'application/octet-stream'
          }
        });
      },

      readAllTenants: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/sd/tenant/all');
      },

      getUserTenants: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/sd/tenant/user');
      },

      readAllRoles: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/sd/group/all');
      },

      getPermittedRoles: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/sd/group/permitted');
      },
      readAllDepartments: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/department/user');
      },
      // searchForSpecificUser: function () {
      //   return $http.post(AppConstants.URLS.RESOURCE_PROVIDER
      //     + '/users/specificUserSearch');
      // },

      loadSAMLAttributes: function () {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER +
          '/users/loadSAMLAttributes');
      },

      getUser: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/users/user');
      },

      // searchForRegisteredUser: function () {
      //   return $http.post(AppConstants.URLS.RESOURCE_PROVIDER
      //     + '/users/registeredUser');
      // },

      // searchForActiveUser: function () {
      //   return $http.post(AppConstants.URLS.RESOURCE_PROVIDER
      //     + '/users/activeUser');
      // },

      declineUser: function (userId) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER +
          '/users/declineUser/' + userId);
      },

      findUserInfo: function () {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER +
          '/users/userInfo');
      },

      getUserStatus: function (userId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/users/status');
      },
      readAllUsers: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/users/all');
      },

      getTenant: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/sd/tenant/tenant');
      },
      readAllPermittedDirectorates: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/directorate');
      },
      readAllPermittedDepartments: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/department');
      },
      getUserDepartments: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/department/departments');
      },
      validateExportForm: function (exportForm) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER + '/users/validateExportForm', exportForm);
      },
      getUserSettings: function (unregisteredUser) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER + '/sd/settings', unregisteredUser);
      },
      loadUserExport: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/users/loadUserExport');
      },
      loadUserSearch: function () {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER + '/users/loadUserSearch');
      }
    };
  }
})();
