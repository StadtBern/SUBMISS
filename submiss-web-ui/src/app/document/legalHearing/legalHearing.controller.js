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
 * @name legalHearing.controller.js
 * @description LegalHearingController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document").controller("LegalHearingController",
      LegalHearingController);

  /** @ngInject */
  function LegalHearingController($scope, $rootScope, $locale, $state, $stateParams, NgTableParams, $uibModal, AppService, AppConstants,
    StammdatenService, $q, QFormJSRValidation, SubmissionService, LegalHearingService, $anchorScroll, $transitions) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.hearingDeadlineOpened = false;
    vm.openHearingDeadline = openHearingDeadline;
    vm.currentStatus;
    vm.submissionTerminate = {};
    vm.submissionTerminate.terminationReason = [];
    var getTerminationReasonsFunction, getSubmissionLegalHearingTerminationFunction;
    vm.errorFieldsVisible = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.cancelButton = cancelButton;
    vm.isFormDisabled = isFormDisabled;
    // Activating the controller.
    activate();
    vm.save = save;
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      getSubmissionLegalHearingTerminationFunction = getSubmissionLegalHearingTermination($stateParams.submissionId);
      readStatusOfSubmission($stateParams.submissionId);
      getTerminationReasonsFunction = getTerminationReasons();
      $q.all([getTerminationReasonsFunction, getSubmissionLegalHearingTerminationFunction]).then(function (resolves) {
        assignTerminationReasons();
      });
    }

    /** Load the submission cancel form */
    function getSubmissionLegalHearingTermination(submissionId) {
      return LegalHearingService.getSubmissionLegalHearingTermination(submissionId).success(
        function (data, status) {
          if (status === AppConstants.HTTP_RESPONSES.NO_CONTENT) {
            vm.submissionTerminate = {};
            vm.submissionTerminate.deadline = null;
          } else {
            vm.submissionTerminate = data;
          }
        }).error(function (response, status) {});
    }

    function save() {
      vm.submissionTerminate.terminationReason = [];
      for (var i = 0; i < vm.terminationReasons.length; i++) {
        if (vm.terminationReasons[i].selected) {
          var terminationReason = {
            id: vm.terminationReasons[i].masterListValueId.id
          };
          vm.submissionTerminate.terminationReason.push(terminationReason);
        }
      }
      vm.submissionTerminate.deadlineViewValue = $scope.legalHearingCtrl.legalTerminationForm.hearingDeadline.$viewValue;
      if (angular.isUndefined(vm.submissionTerminate.id)) {
        LegalHearingService.createLegalHearingTerminate(vm.submissionTerminate, $stateParams.submissionId).success(function (data) {
          reloadState();
        }).error(function (response, status) {
          if (status === 400 || status === 409) { // Validation errors.
            vm.errorFieldsVisible = true;
            $anchorScroll();
            QFormJSRValidation.markErrors($scope,
              $scope.legalHearingCtrl.legalTerminationForm, response);
          }
        });
      } else {
        LegalHearingService.updateLegalHearingTerminate(vm.submissionTerminate, $stateParams.submissionId).success(function (data) {
          reloadState();
        }).error(function (response, status) {
          if (status === 400 || status === 409) { // Validation errors.
            vm.errorFieldsVisible = true;
            $anchorScroll();
            QFormJSRValidation.markErrors($scope,
              $scope.legalHearingCtrl.legalTerminationForm, response);
          }
        });
      }

    }

    /** Load the master list value data for the termination reason */
    function getTerminationReasons() {
      return StammdatenService.getCurrentMLVHEntriesForSubmission($stateParams.submissionId, AppConstants.MASTER_LIST_TYPE.CANCELATION_REASON).success(
        function (data) {
          vm.terminationReasons = data;
        }).error(function (response, status) {

      });
    }

    /** Get the status of the submission */
    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id).success(function (data) {
        vm.currentStatus = data;
      }).error(function (response, status) {

      });
    }

    /** Date modal functionality */
    function openHearingDeadline() {
      vm.hearingDeadlineOpened = !vm.hearingDeadlineOpened;
    }

    /** Inform the termination reasons checkboxes about the data */
    function assignTerminationReasons() {
      for (var i = 0; i < vm.terminationReasons.length; i++) {
        vm.terminationReasons[i].selected = false;
        if (vm.submissionTerminate.terminationReason) {
          for (var j = 0; j < vm.submissionTerminate.terminationReason.length; j++) {
            if (vm.terminationReasons[i].masterListValueId.id === vm.submissionTerminate.terminationReason[j].id) {
              vm.terminationReasons[i].selected = true;
            }
          }
        }
      }
    }

    /** Implementing the cancellation button */
    function cancelButton(dirtyFlag) {
      if (!dirtyFlag) {
        reloadState();
      } else {
        var closeCancelModal = $uibModal.open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title"></h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie den Vorgang wirklich abbrechen? Nicht gespeicherte Informationen gehen dabei verloren. </p>' +
            '</div>' + '</div>' + '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="cancelModalCtrl.closeWindowModal()">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'cancelModalCtrl',
          backdrop: 'static',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeWindowModal = function (reason) {
              closeCancelModal.dismiss(reason);
            };
          }
        });

        return closeCancelModal.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          reloadState();
        });
      }
      return null;
    }

    /** Check for unsaved changes when trying to transition to different state */
    $transitions.onBefore({}, function (transition) {
      if (transition.to() !== transition.from() && vm.dirtyFlag === true) {
        var transitionModal = AppService.transitionModal();
        return transitionModal.result.then(function (response) {
          if (response) {
            vm.dirtyFlag = false;
            return true;
          }
          return false;
        });
      }
      return null;
    });

    /** Reload state with unsaved changes set as false */
    function reloadState() {
      if (vm.dirtyFlag) {
        vm.dirtyFlag = false;
      }
      $state.reload();
    }

    /** Function that checks if the form needs to be disabled */
    function isFormDisabled() {
      return (vm.currentStatus === AppConstants.STATUS.PROCEDURE_CANCELED ||
          vm.currentStatus === AppConstants.STATUS.PROCEDURE_COMPLETED) ||
        vm.currentStatus >= AppConstants.STATUS.AWARD_NOTICES_CREATED;
    }
  }
})();
