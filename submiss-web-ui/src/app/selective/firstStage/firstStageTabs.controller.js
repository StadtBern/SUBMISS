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
 * @name firstStageTabs.controller.js
 * @description FirstStageTabsController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss").controller("FirstStageTabsController",
      FirstStageTabsController);

  /** @ngInject */
  function FirstStageTabsController($scope, $rootScope, $state, SelectiveService,
    $stateParams, SubmissionService, AppConstants, AppService) {
    const vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.submission = null;
    vm.id = $stateParams.id;
    vm.status = AppConstants.STATUS;
    vm.secApplicantsView = false;
    vm.examinationTabVisible = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    // Activating the controller.
    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      readStatusOfSubmission($stateParams.id);
      implementingSecurity($stateParams.id);
      isExaminationTabVisible($stateParams.id);
    }

    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});

    /***********************************************************************
     * Functions.
     **********************************************************************/

    /** Function to read status of submission */
    function readStatusOfSubmission(submissionId) {
      SubmissionService.getCurrentStatusOfSubmission(submissionId).success(function (data) {
        if (data === vm.status.PROCEDURE_COMPLETED ||
          data === vm.status.PROCEDURE_CANCELED) {
          AppService.getSubmissionStatuses(submissionId).success(function (data) {
            vm.statusHistory = data;
            vm.currentStatus = AppService.assignCurrentStatus(vm.statusHistory);
            setApplicantListTitle(vm.currentStatus);
          });
        } else {
          vm.currentStatus = data;
          setApplicantListTitle(vm.currentStatus);
        }
      }).error(function (response, status) {

      });
    }

    /** Function to set the applicant list tab title according to the current status */
    function setApplicantListTitle(currentStatus) {
      if (currentStatus < AppConstants.STATUS.APPLICATION_OPENING_STARTED) {
        vm.applicantListTitle = "Bewerberliste";
      } else {
        vm.applicantListTitle = "Bewerbungsübersicht";
      }
    }

    /** Function to check for user rights when performing operations */
    function implementingSecurity(submissionId) {
      vm.secApplicantsView =
        AppService.isOperationPermitted(AppConstants.OPERATION.APPLICANTS_VIEW, AppConstants.PROCESS.SELECTIVE);
    }

    /** Function to determine if the examination tab is visible */
    function isExaminationTabVisible(submissionId) {
      SelectiveService.hasApplicationOpeningBeenClosedBefore(submissionId)
        .success(function (data) {
          vm.examinationTabVisible = data;
        });
    }
  }
})();
