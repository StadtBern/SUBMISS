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
 * @name stammdatenOverview.controller.js
 * @description StammdatenOverviewController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss").controller("StammdatenOverviewController",
      StammdatenOverviewController);

  /** @ngInject */
  function StammdatenOverviewController($rootScope, $scope, $state, StammdatenService,
    AppConstants) {
    const
      vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.masterListTypes = [];
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.navigateToTypeData = navigateToTypeData;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      getMasterListTypes();
    }

    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});

    /***********************************************************************
     * Functions.
     **********************************************************************/
    $rootScope.$on('$stateChangeSuccess', function (event, to, toParams,
      from, fromParams) {
      // save the previous state in a rootScope variable so that it's
      // accessible from everywhere
      $rootScope.previousState = from;
    });

    /** Function to get the master list types. */
    function getMasterListTypes() {
      StammdatenService.getMasterListTypes().success(function (data) {
        vm.masterListTypes = data;
      });
    }

    function navigateToTypeData(name, type) {
      if (type === AppConstants.CategorySD.AUTHORISED_SIGNATORY) {
        $state.go('stammdaten.authorisedSignatory', {
          type: type,
          typeName: name
        });
      } else if (type === AppConstants.CategorySD.PROCESS) {
        $state.go('stammdaten.process', {
          type: type,
          typeName: name
        });
      } else {
        $state.go('stammdaten.typeData', {
          type: type,
          typeName: name
        });
      }
    }
  }
})();
