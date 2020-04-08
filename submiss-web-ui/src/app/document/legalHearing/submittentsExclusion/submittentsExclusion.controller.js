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
    .module("submiss.document").controller("SubmittentsExclusionController",
      SubmittentsExclusionController);

  /** @ngInject */
  function SubmittentsExclusionController($scope, $rootScope, $locale,
    $state, $stateParams, $transitions,
    $uibModal, AppConstants,
    StammdatenService, LegalHearingService, AppService,
    SubmissionService, $anchorScroll, QFormJSRValidation) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var submittent = {
      show: false
    };
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.exclusionDateOpened = false;
    vm.firstLevelExclusionDateOpened = false;
    vm.openExclusionDate = openExclusionDate;
    vm.openExclusionFirstLevelDate = openExclusionFirstLevelDate;
    vm.currentStatus;
    vm.openSubmittentsModal = openSubmittentsModal;
    vm.openApplicantsModal = openApplicantsModal;
    vm.openSubmittentAccordion = false;
    vm.displayedSubmittent = {};
    vm.legalExclusion = {
      submissionId: $stateParams.submissionId,
      legalExclusions: []
    };
    vm.dirtyFlag = false;
    vm.submission = {};
    vm.accordionOne = {};
    vm.accordionOne.show = true;
    vm.openAccordionOne = openAccordionOne;
    vm.accordionTwo = {};
    vm.accordionTwo.show = true;
    vm.openAccordionTwo = openAccordionTwo;
    vm.status = AppConstants.STATUS;
    vm.errorFieldsVisible = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.openSubmittentDetails = openSubmittentDetails;
    vm.addSubmittentList = addSubmittentList;
    vm.save = save;
    vm.cancelButton = cancelButton;
    vm.isFormDisabled = isFormDisabled;
    // Activating the controller.
    activate();
    vm.checkUncheckAll = checkUncheckAll;
    vm.checkUncheckAllFirst = checkUncheckAllFirst;
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      readStatusOfSubmission($stateParams.submissionId);
      readSubmittentsBySubmission($stateParams.submissionId);
      getExclusionDeadline($stateParams.submissionId);
      readSubmission($stateParams.submissionId)
    }
    /***********************************************************************
     * Functions.
     **********************************************************************/
    function openAccordionOne() {
      vm.accordionOne.show = !vm.accordionOne.show;
    }

    function openAccordionTwo() {
      vm.accordionTwo.show = !vm.accordionTwo.show;
    }

    function checkUncheckAll() {
      vm.activeButton = false;
      if (vm.checkallSub) {
        vm.checkallSub = true;
        vm.activeButton = true;
      } else {
        vm.checkallSub = false;
        vm.activeButton = false;
      }
      // if main is checked/unchecked then give the same status to the
      // others from the list.
      angular.forEach(vm.legalExclusions, function (submittent) {
        if (!submittent.disable && (submittent.level === 0 || submittent.level === 2)) {
          submittent.checked = vm.checkallSub;
          AppService.addSubmittent(submittent);
        }
      });
    }

    function checkUncheckAllFirst() {
      vm.activeButton = false;
      if (vm.checkall) {
        vm.checkall = true;
        vm.activeButton = true;
      } else {
        vm.checkall = false;
        vm.activeButton = false;
      }
      // if main is checked/unchecked then give the same status to the
      // others from the list.
      angular.forEach(vm.legalExclusions, function (submittent) {
        if (!submittent.disable && submittent.level === 1) {
          submittent.checked = vm.checkall;
          AppService.addSubmittent(submittent);
        }
      });
    }

    function getExclusionDeadline(id) {
      LegalHearingService.getExclusionDeadline(id).success(function (data) {
        vm.legalExclusion.exclusionDate = data;
      }).error(function (response, status) {

      });
    }

    function addSubmittentList(submittent) {
      if (submittent.checked) {
        AppService.addSubmittent(submittent);
      }
    }

    function openSubmittentDetails(submittent) {
      submittent.show = !submittent.show;
      // if the details of one offer are opened, then close the one that
      // was opened before, so that there is always only one offer open
      if (submittent.show) {
        if (vm.displayedSubmittent) {
          vm.displayedSubmittent.show = false;
        }
        vm.displayedSubmittent = submittent;
      } else {
        vm.displayedSubmittent = {};
      }
    }

    function openSubmittentsModal() {
      $uibModal.open({
        templateUrl: 'app/document/legalHearing/submittentsExclusion/addExcludedSubmittent.html',
        controller: 'AddExcludedSubmittentController',
        controllerAs: 'addExcludedSubmittentCtrl',
        backdrop: 'static',
        keyboard: false,
      });
    }

    function openApplicantsModal() {
      $uibModal.open({
        templateUrl: 'app/document/legalHearing/submittentsExclusion/selective/addExcludedApplicant.html',
        controller: 'AddExcludedApplicantController',
        controllerAs: 'addExcludedApplicantCtrl',
        backdrop: 'static',
        keyboard: false,
      });
    }

    function readSubmittentsBySubmission(id) {
      LegalHearingService.getExcludedSubmittents(id).success(function (data) {
        vm.legalExclusions = data;
        for (var i = 0; i < vm.legalExclusions.length; i++) {
          if ($stateParams.submittentId === vm.legalExclusions[i].submittent.id) {
            vm.legalExclusions[i].show = true;
            vm.displayedSubmittent = vm.legalExclusions[i];
            break;
          }
        }
      });
    }

    /** Get the status of the submission */
    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id).success(function (data) {
        vm.currentStatus = data;
      }).error(function (response, status) {

      });
    }

    function save() {
      vm.legalExclusion.legalExclusions = [];
      for (var i = 0; i < vm.legalExclusions.length; i++) {
        vm.legalExclusion.legalExclusions.push(vm.legalExclusions[i]);
      }
      // Pass the firstLevelExclusionDate view value from the form (if applicable).
      if ($scope.submittentsExclusionCtrl.legalExclusionForm.firstLevelExclusionDate) {
        vm.legalExclusion.fLExclusionDateViewValue =
          $scope.submittentsExclusionCtrl.legalExclusionForm.firstLevelExclusionDate.$viewValue;
      }
      // Pass the exclusionDate view value from the form (if applicable).
      if ($scope.submittentsExclusionCtrl.legalExclusionForm.exclusionDate) {
        vm.legalExclusion.exclusionDateViewValue =
          $scope.submittentsExclusionCtrl.legalExclusionForm.exclusionDate.$viewValue;
      }
      AppService.setPaneShown(true);
      vm.legalExclusion.submissionVersion = vm.submission.version;
      LegalHearingService.updateExcludedSubmittent(vm.legalExclusion).success(function (data) {
        vm.dirtyFlag = false;
        if (vm.displayedSubmittent.submittent) {
          $state.go($state.current, {
            submissionId: $stateParams.submissionId,
            submittentId: null
          }, {
            reload: true
          });
        } else {
          $state.go($state.current, {
            submissionId: $stateParams.submissionId,
            submittentId: null
          }, {
            reload: true
          });
        }
        AppService.setPaneShown(false);
      }).error(function (response, status) {
        if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status == AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
          vm.errorFieldsVisible = true;
          $anchorScroll();
          QFormJSRValidation.markErrors($scope,
            $scope.submittentsExclusionCtrl.legalExclusionForm, response);
        }
        AppService.setPaneShown(false);
      });
    }

    /** Date modal functionality */
    function openExclusionDate() {
      vm.exclusionDateOpened = !vm.exclusionDateOpened;
    }

    /** Date modal functionality */
    function openExclusionFirstLevelDate() {
      vm.firstLevelExclusionDateOpened = !vm.firstLevelExclusionDateOpened;
    }

    /** Implementing the cancellation button */
    function cancelButton(dirtyFlag) {
      if (!dirtyFlag) {
        $state.reload();
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
          vm.dirtyFlag = false;
          $state.reload();
        });
      }
      return null;
    }

    /** Check for unsaved changes when trying to transition to different state */
    $transitions.onBefore({}, function (transition) {
      if (transition.to() !== transition.from() && vm.dirtyFlag === true) {
        vm.dirtyFlag = false;
      }
      return null;
    });


    /** function that checks if the form needs to be disabled
     * It will be disabled if the status is cancelled or completed */
    function isFormDisabled() {
      return (vm.currentStatus === AppConstants.STATUS.PROCEDURE_CANCELED ||
          vm.currentStatus === AppConstants.STATUS.PROCEDURE_COMPLETED) ||
        vm.currentStatus >= AppConstants.STATUS.AWARD_NOTICES_CREATED;
    }

    function readSubmission(id) {
      SubmissionService.readSubmission(id).success(function (data) {
        vm.submission = data;
        vm.legalExclusion.firstLevelExclusionDate = vm.submission.firstLevelExclusionDate;
      })
    }
  }
})();
