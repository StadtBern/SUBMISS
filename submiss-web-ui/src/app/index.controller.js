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
 * @ngdoc controller
 * @name index.controller.js
 * @description AppController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss")
    .controller("AppController", AppController);

  /** @ngInject */
  function AppController(AppService, UsersService, $rootScope, $cookies) {
    var vm = this;

    /** Exported variables */
    vm.isTenantEWB = false;

    $rootScope.$previousState = getPreviousStateBeforeReload();
    $rootScope.selectedProjectId = getProjectIdBeforeReload();
    $rootScope.projectName = getProjectNameBeforeReload();

    // As IE does not support the "startsWith" and "includes" methods,
    // a custom implementation is added using polyfills.
    if (!String.prototype.startsWith) {
      String.prototype.startsWith = function (searchString, position) {
        position = position || 0;
        return this.indexOf(searchString, position) === position;
      };
    }
    if (!String.prototype.includes) {
      String.prototype.includes = function (search, start) {
        if (typeof start !== 'number') {
          start = 0;
        }
        if (start + search.length > this.length) {
          return false;
        } else {
          return this.indexOf(search, start) !== -1;
        }
      };
    }

    getTenantStyling();

    // Exported functions.
    vm.getLoggedUser = getLoggedUser;
    vm.getPermOpsMenu = getPermOpsMenu;
    vm.getPaneShown = getPaneShown;
    vm.setDirtyFlag = setDirtyFlag;

    // Functions.
    function getLoggedUser() {
      return AppService.getLoggedUser();
    }

    function getPermOpsMenu() {
      return AppService.getPermOpsMenu();
    }

    function getPaneShown() {
      return AppService.getPaneShown();
    }

    function setDirtyFlag(dirty) {
      return AppService.setIsDirty(dirty);
    }

    /** Function to get the tenant styling */
    function getTenantStyling() {
      UsersService.getTenant().success(function (data) {
        if (data.name === "EWB") {
          vm.tenantStyling = './assets/theme_ewb.css';
        } else {
          vm.tenantStyling = './assets/theme_bern.css';
        }
      });
    }

    /** Keeping data in case of page reload */
    window.onbeforeunload = function () {
      if ($rootScope.$previousState) {
        $cookies.put('previousState', $rootScope.$previousState);
      }
      if ($rootScope.selectedProjectId) {
        $cookies.put('selectedProjectId', $rootScope.selectedProjectId);
      }
      if ($rootScope.projectName) {
        $cookies.put('projectName', $rootScope.projectName);
      }
    }

    /** Function to get the previous state name before the page reloaded (if applicable) */
    function getPreviousStateBeforeReload() {
      if ($cookies.get('previousState')) {
        return $cookies.get('previousState');
      } else {
        return '';
      }
    }

    /** Function to get the project id before the page reloaded (if applicable) */
    function getProjectIdBeforeReload() {
      if ($cookies.get('selectedProjectId')) {
        return $cookies.get('selectedProjectId');
      } else {
        return '';
      }
    }

    /** Function to get the project name before the page reloaded (if applicable) */
    function getProjectNameBeforeReload() {
      if ($cookies.get('projectName')) {
        return $cookies.get('projectName');
      } else {
        return '';
      }
    }

  }
})();
