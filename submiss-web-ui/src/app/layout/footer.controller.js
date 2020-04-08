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
 * @name footer.controller.js
 * @description FooterController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module('submiss')
    .controller('FooterController', FooterController);

  /** @ngInject */
  function FooterController($scope, AppService, $uibModal, UsersService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var appVersionInfo;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.getAppVersion = getAppVersion;
    vm.getCommitId = getCommitId;
    vm.showContactModal = showContactModal;
    vm.showImpressumModal = showImpressumModal;
    /** Activating the controller. */
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      AppService.getAppVersionInfo()
        .then(function success(response) {
          appVersionInfo = response;
        })
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    function getAppVersion() {
      return appVersionInfo ? appVersionInfo['git.build.version'] : '';
    }

    function getCommitId() {
      return appVersionInfo ? appVersionInfo['git.commit.id'] : '';
    }

    function showContactModal() {
      $uibModal.open({
        templateUrl: 'app/layout/footer.contact.html',
        controller: 'FooterContactController',
        controllerAs: 'footerContactCtrl'
      });
    }

    function showImpressumModal() {
      $uibModal.open({
        templateUrl: 'app/layout/footer.impressum.html',
        controller: 'FooterImpressumController',
        controllerAs: 'footerImpressumCtrl'
      });
    }
  }
})();
