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
  function FooterController($scope, AppService, $uibModal, UsersService, $interval) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    let backEndPinged = 0;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.showContactModal = showContactModal;
    vm.showImpressumModal = showImpressumModal;
    /** Activating the controller. */
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/

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

    // After 30 minutes of idle time the Web Application Firewall will log out the user
    // So we will ping the back end, every 15 minutes (900000 milis)
    // After 8 hours ( 32 quarters ) stop pinging.
    // Submiss redirects to the login and back to requested page where the interval will start over again
    let stopTime = $interval( function pingBackEnd() {
      AppService.pingBackEnd().then(function (response) {
        backEndPinged ++;
        if ( backEndPinged === 33 ){
          stopTimer();
        }
      })
    }, 900000);


     function stopTimer() {
      if (angular.isDefined(stopTime)) {
        $interval.cancel(stopTime);
        stopTime = undefined;
        backEndPinged = 0;
      }
    }

  }
})();
