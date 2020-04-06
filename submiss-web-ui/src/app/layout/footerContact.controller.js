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
 * @name footerContact.controller.js
 * @description FooterContactController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module('submiss')
    .controller('FooterContactController', FooterContactController);

  /** @ngInject */
  function FooterContactController($scope, AppService, $uibModal, UsersService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var unregisteredUser = {
      tenantId: null,
      groupName: null
    };
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    /** Activating the controller. */
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      getUserSettings();
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    function getUserSettings() {
      // If a tenant has been assigned to the user, get the tenant id.
      if (AppService.getUnregisteredUserTenant()) {
        unregisteredUser.tenantId = AppService.getUnregisteredUserTenant().id;
      }
      // If a group has been assigned to the user, get the group name.
      if (AppService.getUnregisteredUserGroup()) {
        unregisteredUser.groupName = AppService.getUnregisteredUserGroup().name;
      }
      UsersService.getUserSettings(unregisteredUser).success(function (data) {
        vm.userSettings = data;
      });
    }
  }
})();
