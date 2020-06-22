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
 * @name CloseSubmission.controller.js
 * @description CloseSubmissionController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.submission")
    .controller("CloseSubmissionController", CloseSubmissionController);

  /** @ngInject */
  function CloseSubmissionController($scope, QFormJSRValidation,
    $uibModalInstance, SubmissionService) {
    const vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.reasonGiven = null;
    vm.reasonGivenStyle = {
      "resize": "none"
    };
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.closingSubmission = closingSubmission;

    // Activating the controller.
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
    function closingSubmission() {
      var closeForm = {
        closeReason: vm.reasonGiven
      };
      SubmissionService.checkClosingReason(closeForm).success(function (data) {
        $uibModalInstance.close(vm.reasonGiven);
      }).error(function (response, status) {
        if (status === 400) { // Validation errors.
          QFormJSRValidation.markErrors($scope,
            $scope.closeSubmissionCtrl.closeSubmissionForm, response);
        }
      });
    }
  }
})();
