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
 * @name login.controller.js
 * @description LoginController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.auth")
    .controller("LoginController", LoginController);

  /** @ngInject */
  function LoginController($scope, AppService, AppConstants, $state, $timeout) {
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      $timeout(function () {
        var group = AppService.getLoggedUser().userGroup.name;
        var extendedAccess = AppService.isOperationPermitted(AppConstants.OPERATION.EXTENDED_ACCESS, null);
        // The check if the user role is Administrator must always be first, in order
        // to make sure that the Administrator is always redirected by default to the tasks page.
        if (group === AppConstants.GROUP.ADMIN) {
          $state.go("tasksView");
        } else if (!extendedAccess || group === AppConstants.GROUP.SB) {
          $state.go("company.search");
        } else {
          $state.go("project.search");
        }
      }, 100);
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});

    /***********************************************************************
     * Functions.
     **********************************************************************/
  }
})();
