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
 * @name header.controller.js
 * @description HeaderController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss")
    .controller("HeaderController", HeaderController);

  /** @ngInject */
  function HeaderController($scope, $state, LayoutService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.expandedMenu = 0;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.navigateTo = navigateTo;
    vm.currentState = currentState;
    vm.expandMenu = expandMenu;
    vm.resetMenu = resetMenu;
    vm.projectTabActive = projectTabActive;
    vm.companyTabActive = companyTabActive;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      getTenantLogo();
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    /** Routes to the given state */
    function navigateTo(stateName, levelIdOption) {
      vm.resetMenu();
      $state.go(stateName, {
        levelIdOption: levelIdOption
      });
    }

    function currentState() {
      return $state.$current.toString();
    }

    function expandMenu(menuNo) {
      vm.expandedMenu = menuNo;
    }

    function resetMenu() {
      vm.expandMenu(0);
    }

    /** Function to get the tenant logo */
    function getTenantLogo() {
      LayoutService.getTenantLogo().success(function (data) {
        vm.tenantLogo = data[0];
        vm.logoDimensions = {
          "height": "49px",
          "width": "auto"
        };
      });
    }

    /** Function to determine if the project tab is active */
    function projectTabActive() {
      return vm.currentState().startsWith('project') || vm.currentState().startsWith('submission') ||
        vm.currentState().startsWith('offer') || vm.currentState().startsWith('examination') ||
        vm.currentState().startsWith('document') || vm.currentState().startsWith('award') ||
        vm.currentState().startsWith('applicants') || vm.currentState().startsWith('formalAuditSelective') ||
        ($state.current.ncyBreadcrumbLink && $state.current.ncyBreadcrumbLink.includes("PROJECT_LEVEL"));
    }

    /** Function to determine if the company tab is active */
    function companyTabActive() {
      return vm.currentState().startsWith('company') ||
        ($state.current.ncyBreadcrumbLink && $state.current.ncyBreadcrumbLink.includes("COMPANY_LEVEL"));
    }
  }
})();
