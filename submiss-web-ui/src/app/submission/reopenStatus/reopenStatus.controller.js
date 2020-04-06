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
 * @name ReopenStatus.controller.js
 * @description ReopenStatusController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.submission")
    .controller("ReopenStatusController", ReopenStatusController);

  /** @ngInject */
  function ReopenStatusController($scope, QFormJSRValidation,
    $uibModalInstance, SubmissionService, reopenTitle, reopenQuestion, statusToReopen) {
    const vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.reopenTitle = reopenTitle;
    vm.reopenQuestion = reopenQuestion;
    vm.reasonGiven = null;
    vm.reopenStatusForm = null;
    vm.statusToReopen = statusToReopen;
    vm.reasonGivenStyle = {
      "resize": "none"
    };
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.resumeReopenStatus = resumeReopenStatus;

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
    function resumeReopenStatus() {
      var reopen = {};
      reopen.reopenReason = vm.reasonGiven;
      SubmissionService.checkReasonGiven(reopen, vm.statusToReopen).success(function (data) {
        $uibModalInstance.close(vm.reasonGiven);
      }).error(function (response, status) {
        if (status === 400) { // Validation errors.
          QFormJSRValidation.markErrors($scope,
            $scope.reopenStatusCtrl.reopenStatusForm, response);
        }
      });
    }

  }
})();
