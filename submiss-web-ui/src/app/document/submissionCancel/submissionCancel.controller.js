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
 * @name submissionCancel.controller.js
 * @description SubmissionCancel controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document").controller("SubmissionCancelController",
      SubmissionCancelController);

  /** @ngInject */
  function SubmissionCancelController($scope, $rootScope, $locale,
    $state, $stateParams, NgTableParams,
    $uibModal, AppService, AppConstants,
    SubmissionCancelService, StammdatenService,
    $q, $anchorScroll, QFormJSRValidation,
    SubmissionService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var getSubmissionCancelFunction;
    var getExclusionReasonsFunction;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.submissionCancel = {};
    vm.exclusionReasons = {};
    vm.availableDateOpened = false;
    vm.openAvailableDate = openAvailableDate;
    vm.save = save;
    vm.secTenderCancelEdit = false;
    vm.secTenderCancel = false;
    vm.currentStatus;
    vm.caseCreate = true;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    // Activating the controller.
    activate();
    vm.cancelSubmission = cancelSubmission;
    vm.resetPage = resetPage;
    vm.isSubmissionCancelled = isSubmissionCancelled;
    vm.isSubmissionCompleted = isSubmissionCompleted;
    vm.isFormDisabled = isFormDisabled;
    vm.reopenSubmission = reopenSubmission;
    vm.isFreezeCheckboxDisabled = isFreezeCheckboxDisabled;
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      getSubmissionCancelFunction = getSubmissionCancel($stateParams.submissionId);
      getExclusionReasonsFunction = getExclusionReasons();
      $q.all([getSubmissionCancelFunction, getExclusionReasonsFunction]).then(function (resolves) {
        assignExclusionReasons();
      });
      readStatusOfSubmission($stateParams.submissionId);
      vm.secTenderCancelEdit = AppService.isOperationPermitted(AppConstants.OPERATION.TENDER_CANCEL_EDIT, null);
      vm.secTenderCancel = AppService.isOperationPermitted(AppConstants.OPERATION.TENDER_CANCEL, null);
    }

    /** Load the submission cancel form */
    function getSubmissionCancel(submissionId) {
      return SubmissionCancelService.getSubmissionCancelBySubmissionId(submissionId)
        .success(function (data, status) {
          vm.submissionCancel = data;
          // case create, initialise
          if (vm.submissionCancel.id === null) {
            vm.caseCreate = true;
            vm.submissionCancel.submission = {
              id: submissionId
            };
            vm.submissionCancel.availableDate = new Date();
            vm.submissionCancel.objectNameRead = true;
            vm.submissionCancel.projectNameRead = true;
            vm.submissionCancel.workingClassRead = true;
            vm.submissionCancel.descriptionRead = true;
            vm.submissionCancel.freezeCloseSubmission = false;
            if (vm.submissionCancel.workTypes === null) {
              vm.submissionCancel.workTypes = [];
            }

          } else {
            vm.caseCreate = false;
          }
        }).error(function (response, status) {});
    }

    /** Load the master list value data for the exclusion reason */
    function getExclusionReasons() {
      return StammdatenService.getCurrentMLVHEntriesForSubmission($stateParams.submissionId, AppConstants.MASTER_LIST_TYPE.CANCELATION_REASON)
        .success(function (data) {
          vm.exclusionReasons = data;
        }).error(function (response, status) {

        });
    }

    /** Get the status of the submission */
    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id)
        .success(function (data) {
          vm.currentStatus = data;
        }).error(function (response, status) {

        });
    }

    /** Save the submission cancel form */
    function save() {
      vm.dirtyFlag = false;
      vm.submissionCancel.workTypes = [];
      for (var i = 0; i < vm.exclusionReasons.length; i++) {
        if (vm.exclusionReasons[i].selected) {
          var workType = {
            id: vm.exclusionReasons[i].masterListValueId.id
          };
          vm.submissionCancel.workTypes.push(workType);
        }
      }
      if (angular.isUndefined(vm.submissionCancel.id) || vm.submissionCancel.id === null) {
        SubmissionCancelService.createSubmissionCancel(vm.submissionCancel)
          .success(function (data) {
            $state.reload();
          }).error(function (response, status) {
            if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
              $anchorScroll();
              QFormJSRValidation.markErrors($scope,
                $scope.submissionCancelCtrl.submissionCancelHtmlForm, response);
            }
          });
      } else {
        SubmissionCancelService.updateSubmissionCancel(vm.submissionCancel)
          .success(function (data) {
            $state.reload();
          }).error(function (response, status) {
            if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
              $anchorScroll();
              QFormJSRValidation.markErrors($scope,
                $scope.submissionCancelCtrl.submissionCancelHtmlForm, response);
            }
          });
      }
    }

    /** Date modal functionality */
    function openAvailableDate() {
      vm.availableDateOpened = !vm.availableDateOpened;
    }

    /** Inform the exclusion reasons checkboxes about the data */
    function assignExclusionReasons() {
      for (var i = 0; i < vm.exclusionReasons.length; i++) {
        vm.exclusionReasons[i].selected = false;
        for (var j = 0; j < vm.submissionCancel.workTypes.length; j++) {
          if (vm.exclusionReasons[i].masterListValueId.id === vm.submissionCancel.workTypes[j].id) {
            vm.exclusionReasons[i].selected = true;
          }
        }
      }
    }

    /** cancel submission */
    function cancelSubmission() {
      // if the data of the form are not saved
      // then display a message whether to save the data or not
      if (vm.dirtyFlag) {
        var proceedWithoutSavingModal = AppService.proceedWithoutSaving();
        return proceedWithoutSavingModal.result.then(function (response) {
          if (response) {
            confirmAndCloseSubmission();
          }
        });
      } else {
        return confirmAndCloseSubmission();
      }
    }

    /** Show a confirmation message for SubmissionCancel and if the user confirms cancel the submission */
    function confirmAndCloseSubmission() {
      SubmissionCancelService.cancellationDocumentCreated($stateParams.submissionId)
        .success(function (data) {
          var confirmationWindowInstance = $uibModal
            .open({
              template: '<div class="modal-header">' +
                '<button type="button" class="close" aria-label="Close" ' +
                'ng-click="submissionCancelCtrl.closeConfirmationWindow(\'nein\');"><span aria-hidden="true">&times;</span></button>' +
                '<h4 class="modal-title">Verfahren abbrechen</h4>' +
                '</div>' +
                '<div class="modal-body">' +
                '<div class="row">' +
                '<div class="col-md-12">' +
                '<p> Möchten Sie diese Submission wirklich abbrechen? </p>' +
                '</div>' +
                '</div>' +
                '</div>' +
                '<div class="modal-footer">' +
                '<button type="button" class="btn btn-primary" ng-click="submissionCancelCtrl.closeConfirmationWindow(\'ja\');">Ja</button>' +
                '<button type="button" class="btn btn-default" ng-click="submissionCancelCtrl.closeConfirmationWindow(\'nein\');">Nein</button>' +
                '</div>' + '</div>',
              controllerAs: 'submissionCancelCtrl',
              backdrop: 'static',
              keyboard: false,
              controller: function () {
                var vm = this;
                vm.closeConfirmationWindow = function (reason) {
                  confirmationWindowInstance.dismiss(reason);
                };
              }
            });
          return confirmationWindowInstance.result.then(function () {
            // Modal Success Handler
          }, function (response) { // Modal Dismiss Handler
            if (response === 'ja') {
              SubmissionCancelService.cancelSubmission($stateParams.submissionId, vm.submissionCancel.id, vm.submissionCancel.version).success(function (data) {
                $state.reload();
              }).error(function (response, status) {
                if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST) { // Validation errors.
                  $anchorScroll();
                  QFormJSRValidation.markErrors($scope,
                    $scope.submissionCancelCtrl.submissionCancelHtmlForm, response);
                }
              });
              return true;
            } else {
              return false;
            }
          });
        }).error(function (response, status) {
          if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST) { // Validation errors.
            $anchorScroll();
            QFormJSRValidation.markErrors($scope,
              $scope.submissionCancelCtrl.submissionCancelHtmlForm, response);
          }
        });
    }

    /** Show a confirmation message for cancel of the process and if the user confirms reset the page to the saved values */
    function resetPage(dirtyflag) {
      if (!dirtyflag) {
        $state.go('documentView.submissionCancel', {}, {
          reload: true
        });
        return true;
      } else {
        var resetPageModal = $uibModal.open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title"></h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie den Vorgang wirklich abbrechen? Nicht gespeicherte Informationen gehen dabei verloren </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="submissionCancelCtrl.closeWindowModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'submissionCancelCtrl',
          backdrop: 'static',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeWindowModal = function (reason) {
              resetPageModal.dismiss(reason);
            };
          }

        });
        return resetPageModal.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            vm.dirtyFlag = false;
            $state.go('documentView.submissionCancel', {}, {
              reload: true
            });
            return true;
          } else {
            return false;
          }
        });
      }
    }

    /** function that checks if a submission is already cancelled, in order to show the correct buttons */
    function isSubmissionCancelled() {
      return vm.currentStatus === AppConstants.STATUS.PROCEDURE_CANCELED;
    }

    /** Function that checks if submission is completed */
    function isSubmissionCompleted() {
      return vm.currentStatus === AppConstants.STATUS.PROCEDURE_COMPLETED;
    }

    function isFormDisabled() {
      return vm.currentStatus >= AppConstants.STATUS.AWARD_NOTICES_CREATED;
    }

    /**
     * Disable Beschwerdeeingang checkbox when on Verfugungen erstellt status.
     * https://www.meistertask.com/app/task/OuVYqwZj/beschwerdeeingang-is-disabled-if-verfahren-abgebrochen
     */
    function isFreezeCheckboxDisabled() {
      return vm.currentStatus === AppConstants.STATUS.AWARD_NOTICES_CREATED;
    }

    /** Function to reopen submission */
    function reopenSubmission() {
      var reopenTenderStatusModal = AppService.reopenTenderStatusModal(AppConstants.STATUS_REOPEN_TITLE.PROCESS,
        AppConstants.STATUS_REOPEN_QUESTION.PROCESS, AppConstants.STATUS_TO_REOPEN.PROCESS);
      return reopenTenderStatusModal.result.then(function (response) {
        if (!angular.isUndefined(response)) {
          var reopenForm = {
            reopenReason: response
          };
          SubmissionService.reopenSubmission($stateParams.id, reopenForm)
            .success(function (data) {
              $state.reload();
            });
        }
      });
    }
  }
})();
