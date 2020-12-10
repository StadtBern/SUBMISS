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
 * @name secondStageTabs.controller.js
 * @description SecondStageTabsController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss").controller("SecondStageTabsController",
      SecondStageTabsController);

  /** @ngInject */
  function SecondStageTabsController($scope, $rootScope, $state, SelectiveService,
    $stateParams, SubmissionService, AppConstants, AppService) {
    const vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var submittentenliste_offertubersichtStatus = [70, 80, 110, 150];
    var formellePrufungStatus = [120, 140];
    var zuschlagsbewertungStatus = [160, 190];
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.submission = null;
    vm.status = AppConstants.STATUS;
    vm.group = AppConstants.GROUP;
    vm.tabStatus = null;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.offerTabVisible = offerTabVisible;
    vm.isAwardEvaluationTabVisible = isAwardEvaluationTabVisible;
    vm.getStatusSubTab = getStatusSubTab;
    // Activating the controller.
    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      readStatusOfSubmission($stateParams.id);
      readSubmission($stateParams.id);
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
          });
        } else {
          vm.currentStatus = data;
        }
        vm.tabStatus = data;
      });
    }

    /** Function to read submission data */
    function readSubmission(submissionId) {
      SubmissionService.readSubmission(submissionId).success(function (data) {
        vm.submission = data;
        $rootScope.projectName = vm.submission.project.projectName;
        $rootScope.selectedProjectId = vm.submission.project.id;
        vm.secFormalAuditView = AppService.isOperationPermitted(AppConstants.OPERATION.FORMAL_AUDIT_VIEW, vm.submission.process);
        hasOfferOpeningBeenClosedBefore(vm.submission.id);
      });
    }

    function getStatusSubTab(status) {
      var statusSubTab = -1;
      if (submittentenliste_offertubersichtStatus.includes(status)) {
        statusSubTab = 0;
      } else if (formellePrufungStatus.includes(status)) {
        statusSubTab = 1;
      } else if (zuschlagsbewertungStatus.includes(status)) {
        statusSubTab = 2;
      }
      return statusSubTab;
    }

    /** Function to determine if the offer tab is visible */
    function offerTabVisible() {
      if (vm.submission) {
        return vm.currentStatus >= vm.status.OFFER_OPENING_STARTED;
      }
      return false;
    }

    /** Check if status offer opening closed has been set before */
    function hasOfferOpeningBeenClosedBefore(submissionId) {
      SubmissionService.hasOfferOpeningBeenClosedBefore(submissionId).success(function (data) {
        vm.offerOpeningClosedBefore = data;
      }).error(function (response, status) {});
    }

    /** Function to determine if the award evaluation tab is visible */
    function isAwardEvaluationTabVisible() {
      if (vm.submission) {
        return ((vm.currentStatus >= vm.status.OFFER_OPENING_CLOSED || vm.offerOpeningClosedBefore) &&
          (vm.submission.process === AppConstants.PROCESS.SELECTIVE &&
            !vm.submission.isServiceTender));
      }
      return false;
    }
  }
})();
