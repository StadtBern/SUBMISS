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
 * @name selectiveFirstStage.controller.js
 * @description SelectiveFirstStageController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss").controller("SelectiveFirstStageController",
      SelectiveFirstStageController);

  /** @ngInject */
  function SelectiveFirstStageController($scope, $rootScope, $state, AppService,
    $stateParams, SubmissionService, AppConstants) {
    const vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.submission = null;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    // Activating the controller.
    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      readSubmission($stateParams.id);
      readStatusOfSubmission($stateParams.id);
      navigateToDefaultState();
    }

    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});

    /***********************************************************************
     * Functions.
     **********************************************************************/

    /** Function to read submission data */
    function readSubmission(submissionId) {
      SubmissionService.readSubmission(submissionId).success(function (data) {
        vm.submission = data;
        $rootScope.projectName = vm.submission.project.projectName;
        $rootScope.selectedProjectId = vm.submission.project.id;
      });
    }

    /** Function to read status of submission */
    function readStatusOfSubmission(submissionId) {
      SubmissionService.getCurrentStatusOfSubmission(submissionId).success(function (data) {
        vm.currentStatus = data;
      }).error(function (response, status) {

      });
    }

    /** Function to navigate to default state */
    function navigateToDefaultState() {
      if ($state.current.name === "selectiveFirstStage" ||
        $state.current.name === "applicants") {
        $state.go("applicants", {
          id: $stateParams.id
        }, {});
      }
    }
  }
})();
