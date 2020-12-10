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
 * @name projectView.controller.js
 * @description ProjectViewController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.submissionDefault").controller("SubmissionDefaultController",
      SubmissionDefaultController);

  /** @ngInject */
  function SubmissionDefaultController($rootScope, $scope, $state, $stateParams,
    SubmissionService, $uibModal, NgTableParams,
    $filter, QFormJSRValidation, AppService, AppConstants, StammdatenService, SubmissionCancelService) {
    var vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var isOfferAboveThreshold = null;
    var statusAwardNoticesCreatedDate = null;
    // The arrays with status codes
    var submittentenlisteStatus = [10, 80, 90, 100];
    var stufe1Status = [10, 20, 30, 40, 50];
    var offertubersichtStatus = [110, 150, 170];
    var formelle_eignungsprufungStatus = [120, 130, 160];
    var stufe2Status = [70, 110, 150, 120, 140, 190, 160];
    var zuschlagsbewertungStatus = [160, 180];
    var formellePrufungStatus = [120, 140];
    var dokumentStatus = [60, 200, 210, 220, 230, 240, 250, 260, 270, 290];
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.secFormalAuditView = false;
    vm.secAwardEvaluationView = false;
    vm.secTenderDelete = false;
    vm.secTenderEdit = false;
    vm.secTenderMove = false;
    vm.secSentEmail = false;
    vm.secTenderClose = false;
    vm.secTenderReopen = false;
    vm.status = AppConstants.STATUS;
    vm.group = AppConstants.GROUP;
    vm.process = AppConstants.PROCESS;
    vm.offerOpeningClosedBefore = false;
    vm.suitabilityAuditCompletedSSetBefore = false;
    vm.secDocumentView = false;
    // needed for displayed message for reopen submission
    vm.reopenDate = null;
    vm.reopenMessageStart = AppConstants.REOPEN_MESSAGE_START;
    vm.cancelledMessageInfo = AppConstants.CANCELLED_MESSAGE_INFO;
    vm.reopenMessageEnd = null;
    vm.statusHistory = null;
    vm.isCompletedOrCancelled = false;
    vm.isCompleted = false;
    vm.moveButtonDisabled = true;
    vm.unsavedChangesModalOpen = false;
    vm.availableDateOfCancelledSubmission = null;
    vm.cancelDateOfSubmission = null;
    vm.isReopenedAfterVerfugungen = null;
    vm.currentStatus = null;
    vm.tabStatus = null;
    vm.isCancelledBeforeClose = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.readSubmission = readSubmission;
    vm.getStatusAwardNoticesCreatedDate = getStatusAwardNoticesCreatedDate;
    vm.checkIsOfferAboveThreshold = checkIsOfferAboveThreshold;
    vm.showSubmissionCloseButton = showSubmissionCloseButton;
    vm.showSubmissionReopenButton = showSubmissionReopenButton;
    vm.disableSubmissionCloseButton = disableSubmissionCloseButton;
    vm.disableSubmissionReopenButton = disableSubmissionReopenButton;
    vm.closeSubmission = closeSubmission;
    vm.reopenSubmission = reopenSubmission;
    vm.getDateAndPreviousReopenStatus = getDateAndPreviousReopenStatus;
    vm.tableParams = new NgTableParams({}, {
      dataset: vm.submissions
    });
    vm.isAwardEvaluationTabHidden = isAwardEvaluationTabHidden;
    vm.selectiveSecondStageTabVisible = selectiveSecondStageTabVisible;
    vm.submittentsOrOffersVisible = submittentsOrOffersVisible;
    vm.awardEvaluationVisible = awardEvaluationVisible;
    vm.formalAuditVisible = formalAuditVisible;
    vm.examinationTabVisible = examinationTabVisible;
    vm.documentTabActive = documentTabActive;
    vm.navigateToDocumentView = navigateToDocumentView;
    vm.firstStageTabActive = firstStageTabActive;
    vm.navigateToFirstStage = navigateToFirstStage;
    vm.secondStageTabActive = secondStageTabActive;
    vm.navigateToSecondStage = navigateToSecondStage;
    vm.sendMailModal = sendMailModal;
    vm.projectSearchModal = projectSearchModal;
    vm.readSubmittentsCount = readSubmittentsCount;
    vm.getAvailableDateOfCancelledSubmission = getAvailableDateOfCancelledSubmission;
    vm.getCancelDateOfSubmission = getCancelDateOfSubmission;
    vm.getStatusTab = getStatusTab;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      SubmissionService.loadSubmission($stateParams.id)
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            vm.readSubmission($stateParams.id);
            vm.getStatusAwardNoticesCreatedDate($stateParams.id);
            vm.getDateAndPreviousReopenStatus($stateParams.id);
            vm.getAvailableDateOfCancelledSubmission($stateParams.id);
            vm.getCancelDateOfSubmission($stateParams.id);
            vm.secTenderMove = AppService.isOperationPermitted(AppConstants.OPERATION.TENDER_MOVE, null);
            vm.secSentEmail = AppService.isOperationPermitted(AppConstants.OPERATION.SENT_EMAIL, null);
            vm.secDocumentView = AppService.isOperationPermitted(AppConstants.OPERATION.PROJECT_DOCUMENTVIEW, null);
            vm.secTenderClose = AppService.isOperationPermitted(AppConstants.OPERATION.TENDER_CLOSE, null);
            vm.secTenderReopen = AppService.isOperationPermitted(AppConstants.OPERATION.TENDER_REOPEN, null);
            vm.readSubmittentsCount($stateParams.id);
          }
        });
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

    function sendMailModal() {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        AppService.sendMailModal(vm.data.submission.id, null);
      }
    }

    function readSubmission(id) {
      SubmissionService.readSubmission(id).success(function (data) {
        vm.data.submission = data;
        getSubmissionStatuses(vm.data.submission.id);
        readStatusOfSubmission(vm.data.submission.id);
        vm.secFormalAuditView = AppService.isOperationPermitted(AppConstants.OPERATION.FORMAL_AUDIT_VIEW, vm.data.submission.process);
        vm.secAwardEvaluationView = AppService.isOperationPermitted(AppConstants.OPERATION.AWARD_EVALUATION_VIEW, vm.data.submission.process);
        // in case of PL and Negotiated process
        // make an extra call to determine
        // if at least one offer is above threshold
        if (AppService.getLoggedUser().userGroup.name === AppConstants.GROUP.PL &&
          (vm.data.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE ||
            vm.data.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION)) {
          vm.checkIsOfferAboveThreshold(id);
        }
      }).error(function (response, status) {
        if (status === 409) { // Validation errors.
          QFormJSRValidation.markErrors($scope,
            $scope.submissionDefaultCtr.submissionDefaultForm, response);
        }
      });
    }

    /* Function to return the status tab to highlight */
    function getStatusTab(status) {
      var statusTab = -1;
      if (vm.data.submission && (vm.data.submission.process !== vm.process.SELECTIVE) &&
        submittentenlisteStatus.includes(status)) {
        statusTab = 1;
      } else if (vm.data.submission && (vm.data.submission.process !== vm.process.SELECTIVE) &&
        offertubersichtStatus.includes(status)) {
        statusTab = 2;
      } else if (vm.data.submission && (vm.data.submission.process === vm.process.OPEN || vm.data.submission.process === vm.process.INVITATION) &&
        formelle_eignungsprufungStatus.includes(status)) {
        statusTab = 3;
      } else if (vm.data.submission && (vm.data.submission.process === vm.process.NEGOTIATED_PROCEDURE || vm.data.submission.process === vm.process.NEGOTIATED_PROCEDURE_WITH_COMPETITION) &&
        formellePrufungStatus.includes(status)) {
        statusTab = 4;
      } else if (zuschlagsbewertungStatus.includes(status)) {
        statusTab = 5;
      } else if (vm.data.submission && (vm.data.submission.process === vm.process.SELECTIVE) &&
        stufe1Status.includes(status)) {
        statusTab = 6;
      } else if (vm.data.submission && (vm.data.submission.process === vm.process.SELECTIVE) &&
        stufe2Status.includes(status)) {
        statusTab = 7;
      } else if (dokumentStatus.includes(status)) {
        statusTab = 8;
      }
      return statusTab;
    }

    /* Finds the available date of the submission cancel entity given a submissionId */
    function getAvailableDateOfCancelledSubmission(id) {
      SubmissionCancelService.getAvailableDateBySubmissionId(id).success(function (data) {
        vm.availableDateOfCancelledSubmission = data;
      }).error(function (response, status) {});
    }

    function getCancelDateOfSubmission(id) {
      SubmissionCancelService.getSubmissionCancelBySubmissionId(id)
        .success(function (data) {
          vm.cancelDateOfSubmission = data.cancelledOn;
        }).error(function (response, status) {});
    }

    function readSubmittentsCount(id) {
      SubmissionService.readSubmittentsCount(id).success(function (data) {
        vm.submittentsLength = data;
      }).error(function (response, status) {});
    }

    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id).success(function (data) {
        if (data === vm.status.PROCEDURE_COMPLETED ||
          data === vm.status.PROCEDURE_CANCELED) {
          vm.isCompletedOrCancelled = true;
          if (data === vm.status.PROCEDURE_COMPLETED) {
            vm.isCompleted = true;
          }
          AppService.getSubmissionStatuses(id).success(function (data) {
            vm.statusHistory = data;
            vm.currentStatus = AppService.assignCurrentStatus(vm.statusHistory);
          });
        } else {
          vm.currentStatus = data;
        }
        vm.tabStatus = data;
        if (AppService.getLoggedUser().userGroup.name === vm.group.ADMIN) {
          isMoveButtonDisabled();
        }
      }).error(function (response, status) {

      });
    }

    /** Get the date status Award Notices Created is set */
    function getStatusAwardNoticesCreatedDate(id) {
      SubmissionService.getAwardNoticesCreatedDateForClose(id).success(function (data) {
        statusAwardNoticesCreatedDate = data;
      }).error(function (response, status) {

      });
    }

    /** Get the date a submission has been reopened and the status before the reopen (closed or cancelled), if exists, otherwise null */
    function getDateAndPreviousReopenStatus(id) {
      SubmissionService.getDateAndPreviousReopenStatus(id).success(function (data) {
        if (data != null) {
          vm.reopenDate = data.onDate;
          var previousReopenStatus = Number(data.statusId);
          if (previousReopenStatus === vm.status.PROCEDURE_CANCELED) {
            vm.reopenMessageEnd = AppConstants.REOPEN_MESSAGE_END_CANCEL;
          } else if (previousReopenStatus === vm.status.PROCEDURE_COMPLETED) {
            vm.reopenMessageEnd = AppConstants.REOPEN_MESSAGE_END_CLOSE;
          }
        }
      }).error(function (response, status) {

      });
    }

    /** Check if the offer is above threshold */
    function checkIsOfferAboveThreshold(id) {
      SubmissionService.isOfferAboveThreshold(id).success(function (data) {
        isOfferAboveThreshold = data;
      }).error(function (response, status) {

      });
    }

    /** Function to determine if the award evaluation tab is hidden */
    function isAwardEvaluationTabHidden() {
      if (vm.data.submission) {
        return ((vm.currentStatus < vm.status.OFFER_OPENING_CLOSED &&
            !vm.offerOpeningClosedBefore) ||
          (vm.data.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE ||
            vm.data.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) ||
          ((vm.data.submission.process === AppConstants.PROCESS.OPEN ||
              vm.data.submission.process === AppConstants.PROCESS.SELECTIVE) &&
            vm.data.submission.isServiceTender));
      }
      return null
    }

    /** Function to determine if the submission close button should be displayed */
    function showSubmissionCloseButton() {
      var currentStatus = null;
      if (vm.isCompletedOrCancelled && vm.statusHistory) {
        currentStatus = vm.statusHistory[0];
      } else {
        currentStatus = vm.currentStatus;
      }
      // make sure the data are already loaded
      if (vm.data.submission && currentStatus) {
        // if the current status is PROCEDURE_COMPLETED then submission
        // reopen button is displayed instead
        if (currentStatus !== vm.status.PROCEDURE_COMPLETED) {
          if (vm.data.submission.noAwardTender) {
            return true;
          } else {
            // if the status is cancelled then display the button
            if (currentStatus === vm.status.PROCEDURE_CANCELED) {
              return true;
            }
            // for process NEGOTIATED_PROCEDURE check if status is after SUBMITTENT_LIST_CREATED
            else if (vm.data.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE ||
              vm.data.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) {
              return (currentStatus >= vm.status.SUBMITTENT_LIST_CREATED);
              // for the rest of the processes check if status AWARD_NOTICES_CREATED is set more than 20 days ago
            } else {
              // add the extra days for the check
              if (statusAwardNoticesCreatedDate) {
                var statusAwardNoticesCreatedDatePlus = new Date(statusAwardNoticesCreatedDate);
                statusAwardNoticesCreatedDatePlus.setDate(statusAwardNoticesCreatedDatePlus.getDate() +
                  AppConstants.DAYS_TO_PERMIT_SUBMISSION_CLOSE);
                return (new Date() > statusAwardNoticesCreatedDatePlus);
              } else {
                return false;
              }

            }
          }
        } else {
          return false;
        }
      } else {
        return false;
      }
    }

    /** Function to determine if the submission reopen button should be displayed */
    function showSubmissionReopenButton() {
      return (vm.isCompleted);
    }

    /** Function to determine if the submission close button should be activated */
    function disableSubmissionCloseButton() {
      if (vm.secTenderClose) {
        return disableCloseReopenSubmissionButtons();
      } else {
        return true;
      }
    }

    /** Function to determine if the submission reopen button should be activated */
    function disableSubmissionReopenButton() {
      if (vm.secTenderReopen) {
        return disableCloseReopenSubmissionButtons();
      } else {
        return true;
      }
    }

    /** Function to determine if the user, which already has permission to close/reopen submission set,
     *  can close/reopen the submission */
    function disableCloseReopenSubmissionButtons() {
      if (AppService.getLoggedUser().userGroup.name === AppConstants.GROUP.PL) {
        return (vm.data.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE &&
          isOfferAboveThreshold)
        ||(vm.data.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION &&
          vm.data.submission.aboveThreshold !== null && vm.data.submission.aboveThreshold);
      } else {
        return false;
      }
    }

    /**
     * Show a confirmation message for close submission and if the user
     * confirms close the submission (user can add a closing reason optionally)
     */
    function closeSubmission() {
      var closeSubmissionModal = AppService.closeSubmissionModal();
      return closeSubmissionModal.result.then(function (response) {
        if (!angular.isUndefined(response)) {
          var closeForm = {
            closeReason: response
          };
          SubmissionService.closeSubmission($stateParams.id, vm.data.submission.version, closeForm)
            .success(function (data) {
              $state.reload();
            }).error(function (response, status) {
              if (status === 400) { // Validation errors.
                QFormJSRValidation.markErrors($scope,
                  $scope.submissionDefaultCtr.submissionDefaultForm, response);
                vm.response = response;
              }
            });
        }
      });
    }

    /** Show a confirmation message for reopen submission and if the user confirms reopen the submission */
    function reopenSubmission() {
      var reopenTenderStatusModal = AppService.reopenTenderStatusModal(AppConstants.STATUS_REOPEN_TITLE.PROCESS,
        AppConstants.STATUS_REOPEN_QUESTION.PROCESS, AppConstants.STATUS_TO_REOPEN.PROCESS);
      return reopenTenderStatusModal.result.then(function (response) {
        if (!angular.isUndefined(response)) {
          var reopenForm = {};
          reopenForm.reopenReason = response;
          SubmissionService.reopenSubmission($stateParams.id, vm.data.submission.version, reopenForm).success(
            function (data) {
              $state.reload();
            }).error(function (response, status) {
            if (status === 409) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.submissionDefaultCtr.submissionDefaultForm, response);
              vm.response = response;
            }
          });
        }
      });
    }

    /** Function to determine if the second stage selective process tab is visible */
    function selectiveSecondStageTabVisible() {
      if (vm.data.submission) {
        return (vm.data.submission.process === AppConstants.PROCESS.SELECTIVE &&
          vm.currentStatus >= vm.status.SUITABILITY_AUDIT_COMPLETED_S &&
          !((AppService.getLoggedUser().userGroup.name === vm.group.DIR ||
              AppService.getLoggedUser().userGroup.name === vm.group.PL) &&
            vm.currentStatus >= vm.status.OFFER_OPENING_STARTED &&
            vm.currentStatus < vm.status.OFFER_OPENING_CLOSED));
      }
      return false;
    }

    /** Function to determine if the submittent/offer tab is visible */
    function submittentsOrOffersVisible() {
      if (vm.data.submission) {
        return vm.data.submission.process !== vm.process.SELECTIVE &&
          (vm.currentStatus < vm.status.OFFER_OPENING_STARTED ||
            (vm.currentStatus >= vm.status.OFFER_OPENING_STARTED &&
              !((AppService.getLoggedUser().userGroup.name === vm.group.DIR ||
                  (AppService.getLoggedUser().userGroup.name === vm.group.PL &&
                    (vm.data.submission.process === vm.process.OPEN ||
                      vm.data.submission.process === vm.process.INVITATION))) &&
                vm.currentStatus < vm.status.OFFER_OPENING_CLOSED)));
      }
      return false;
    }

    /** Function to determine if the award evaluation tab is visible */
    function awardEvaluationVisible() {
      if (vm.data.submission && vm.currentStatus) {
        return vm.secAwardEvaluationView &&
          vm.data.submission.process !== vm.process.SELECTIVE &&
          !vm.isAwardEvaluationTabHidden();
      }
      return false;
    }

    /** Function to determine if the formal audit tab is visible */
    function formalAuditVisible() {
      if (vm.currentStatus) {
        return vm.secFormalAuditView &&
          (vm.data.submission.process === vm.process.NEGOTIATED_PROCEDURE || vm.data.submission.process === vm.process.NEGOTIATED_PROCEDURE_WITH_COMPETITION) &&
          (vm.currentStatus >= vm.status.OFFER_OPENING_CLOSED || vm.offerOpeningClosedBefore);
      }
      return false;
    }

    /** Function to get the submission status history */
    function getSubmissionStatuses(submissionId) {
      AppService.getSubmissionStatuses(submissionId).success(function (data) {
        vm.statusHistory = data;
        // Check if status offer opening closed has been set before.
        vm.offerOpeningClosedBefore = statusSetBefore(vm.status.OFFER_OPENING_CLOSED, vm.statusHistory);
        // Check if status suitability audit completed (selective process) has been set before.
        vm.suitabilityAuditCompletedSSetBefore = statusSetBefore(vm.status.SUITABILITY_AUDIT_COMPLETED_S, vm.statusHistory);
        // Check if status reopened after Verfugungen erstellt
        vm.isReopenedAfterVerfugungen = reopenAfterVerfugungen(vm.statusHistory);
        // Check if status cancelled is set before status closed
        vm.isCancelledBeforeClose = cancelledBeforeClose(vm.statusHistory);
      });
    }

    function cancelledBeforeClose(statusHistory) {
      return (statusHistory[0] === vm.status.PROCEDURE_COMPLETED &&
        statusHistory[1] === vm.status.PROCEDURE_CANCELED);
    }

    /** Function to check if status reopened after Verfugungen erstellt */
    function reopenAfterVerfugungen(statusHistory) {
      var verfugungenIndex = -1;
      var lastVerfugungenHistory = [];
      // Check if Verfugungen erstellt status has been set.
      if (statusSetBefore(vm.status.AWARD_NOTICES_CREATED, statusHistory)) {
        verfugungenIndex = statusHistory.findIndex(function (element) {
          return element === vm.status.AWARD_NOTICES_CREATED;
        });
        // Take the last Verfugungen erstellt status that has been set.
        lastVerfugungenHistory = statusHistory.slice(0, verfugungenIndex + 1);
      }
      return lastVerfugungenHistory.length > 1 &&
        (statusSetBefore(vm.status.COMMISSION_PROCUREMENT_DECISION_STARTED, lastVerfugungenHistory) || statusSetBefore(vm.status.FORMAL_EXAMINATION_STARTED, lastVerfugungenHistory));
    }

    /** Function to check if given status has been set before */
    function statusSetBefore(status, array) {
      for (var i in array) {
        if (array[i] === status) {
          return true;
        }
      }
      return false;
    }

    /** Function to determine if the examination tab is visible */
    function examinationTabVisible() {
      if (vm.data.submission && vm.currentStatus) {
        return vm.secFormalAuditView && vm.data.submission.process !== vm.process.SELECTIVE &&
          vm.data.submission.process !== vm.process.NEGOTIATED_PROCEDURE && vm.data.submission.process !== vm.process.NEGOTIATED_PROCEDURE_WITH_COMPETITION &&
          (vm.currentStatus >= vm.status.OFFER_OPENING_CLOSED || vm.offerOpeningClosedBefore);
      }
      return false;
    }

    /** Function to check if document tab is active */
    function documentTabActive() {
      return $state.current.name.startsWith("documentView");
    }

    /** Navigate to document view */
    function navigateToDocumentView() {
      if (!documentTabActive()) {
        $state.go("documentView", {
          id: vm.data.submission.id
        });
      }
    }

    /** Function to check if selective first stage tab is active */
    function firstStageTabActive() {
      return ($state.current.name === "selectiveFirstStage" ||
        $state.current.name === "formalAuditSelective" ||
        $state.current.name === "examination.view" ||
        $state.current.name.startsWith("applicants"));
    }

    /** Navigate to selective first stage */
    function navigateToFirstStage() {
      if (!firstStageTabActive()) {
        $state.go("selectiveFirstStage", {
          id: vm.data.submission.id
        });
      }
    }

    /** Function to check if selective second stage tab is active */
    function secondStageTabActive() {
      return ($state.current.name === "selectiveSecondStage" ||
        $state.current.name === "examination.formal" ||
        $state.current.name === "award.view" ||
        $state.current.name.startsWith("offerListView"));
    }

    /** Navigate to selective second stage */
    function navigateToSecondStage() {
      if (!secondStageTabActive()) {
        $state.go("selectiveSecondStage", {
          id: vm.data.submission.id
        });
      }
    }

    function projectSearchModal() {
      if (AppService.getIsDirty()) {
        handleUnsavedChanges();
      } else {
        $uibModal.open({
          templateUrl: 'app/project/search/projectSearchModal.html',
          controller: 'ProjectSearchModalCtrl',
          controllerAs: 'projectSearchModalCtrl',
          backdrop: 'static',
          keyboard: false,
          windowClass: 'search-modal-window',
          resolve: {
            submission: function () {
              return vm.data.submission;
            }
          }
        });
      }
    }

    /** Function to determine if the move button is disabled. */
    function isMoveButtonDisabled() {
      if ((vm.data.submission.process !== vm.process.SELECTIVE &&
          vm.currentStatus >= vm.status.OFFER_OPENING_CLOSED) ||
        (vm.data.submission.process === vm.process.SELECTIVE &&
          vm.currentStatus >= vm.status.APPLICATION_OPENING_CLOSED) ||
        vm.isCompletedOrCancelled) {
        vm.moveButtonDisabled = true;
      } else {
        vm.moveButtonDisabled = false;
      }
    }

    /** Function to handle the unsaved changes modal */
    function handleUnsavedChanges() {
      if (!vm.unsavedChangesModalOpen) {
        vm.unsavedChangesModalOpen = true;
        var unsavedChangesModal = AppService.informAboutUnsavedChanges();
        return unsavedChangesModal.result.then(function () {
          vm.unsavedChangesModalOpen = false;
        });
      }
      return null;
    }
  }
})();
