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
    .module("submiss.submission").controller("SubmissionViewController",
      SubmissionViewController);

  /** @ngInject */
  function SubmissionViewController($rootScope, $scope, $state, $stateParams,
    SubmissionService, $uibModal, NgTableParams,
    $filter, QFormJSRValidation, AppService, AppConstants, SubmissionCancelService) {
    const
      vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.secFormalAuditView = false;
    vm.secTenderDelete = false;
    vm.secTenderEdit = false;
    vm.secTenderMove = false;
    vm.secSentEmail = false;
    vm.secGekoSubmissionFieldView = false;
    vm.secLockedSubmissionFieldView = false;
    vm.status = AppConstants.STATUS;
    vm.group = AppConstants.GROUP;
    vm.availableDateOfCancelledSubmission = null;
    vm.cancelledMessageStart = AppConstants.CANCELLED_MESSAGE_START;

    /***********************************************************************
     * Exported functions.
     **********************************************************************/

    vm.deleteSubmission = deleteSubmission;
    vm.deleteModal = deleteModal;
    vm.readSubmission = readSubmission;
    vm.navigateToProjectSubmissions = navigateToProjectSubmissions;
    vm.navigateToSubmittentList = navigateToSubmittentList;
    vm.navigateToExamination = navigateToExamination;
    vm.navigateToExaminationNegotiatedProcedure = navigateToExaminationNegotiatedProcedure;
    vm.editSubmission = editSubmission;
    vm.readStatusOfSubmission = readStatusOfSubmission;
    vm.getAvailableDateOfCancelledSubmission = getAvailableDateOfCancelledSubmission;
    vm.tableParams = new NgTableParams({}, {
      dataset: vm.submissions
    });
    vm.formatAmount = formatAmount;
    vm.getObjectInfo = getObjectInfo;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      vm.readSubmission($stateParams.id);
      vm.readStatusOfSubmission($stateParams.id);
      vm.getAvailableDateOfCancelledSubmission($stateParams.id);
      vm.secTenderDelete = AppService.isOperationPermitted(AppConstants.OPERATION.TENDER_DELETE, null);
      vm.secTenderEdit = AppService.isOperationPermitted(AppConstants.OPERATION.TENDER_EDIT, null);
      vm.secTenderMove = AppService.isOperationPermitted(AppConstants.OPERATION.TENDER_MOVE, null);
      vm.secSentEmail = AppService.isOperationPermitted(AppConstants.OPERATION.SENT_EMAIL, null);
      vm.secGekoSubmissionFieldView = AppService.isOperationPermitted(AppConstants.OPERATION.GEKO_SUBMISSION_FIELD_VIEW, null);
      vm.secLockedSubmissionFieldView = AppService.isOperationPermitted(AppConstants.OPERATION.LOCKED_SUBMISSION_FIELD_VIEW, null);
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

    function readSubmission(id) {
      SubmissionService.readSubmission(id).success(function (data) {
        vm.data.submission = data;
        vm.secFormalAuditView = AppService.isOperationPermitted(AppConstants.OPERATION.FORMAL_AUDIT_VIEW, vm.data.submission.process);
      }).error(function (response, status) {});
    }

    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id).success(function (data) {
        vm.currentStatus = data;
      }).error(function (response, status) {

      });
    }

    /* Finds the available date of the submission cancel entity given a submissionId */
    function getAvailableDateOfCancelledSubmission(id) {
      SubmissionCancelService.getAvailableDateBySubmissionId(id).success(function (data) {
        vm.availableDateOfCancelledSubmission = data;
      }).error(function (response, status) {

      });
    }

    function navigateToProjectSubmissions(project) {
      $state.go('projectSubmissionsView', {
        project: project
      });
    }

    function editSubmission() {
      var submissionModal = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ' +
            'ng-click="submissionCreateCtr.closeWindow(submissionCreateCtr.submissionForm.$dirty)">' +
            '<span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">Submission bearbeiten</h4>' +
            '</div>' + '<div class="modal-body">' +
            '<div ng-include src="' + '\'' +
            'app/submission/create/submissionCreate.html' +
            '\'">' + '<div>' + '<div>',
          controller: 'SubmissionCreateController',
          controllerAs: 'submissionCreateCtr',
          size: 'lg',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            submission: function () {
              return vm.data.submission;
            },
            project: function () {
              return vm.data.submission.project;
            },
            editSubmission: function () {
              return true;
            }
          }

        });
      return submissionModal.result.then(function () {}, function (response) {
        if (response !== 'Speichern') {
          return false;
        }
        return null;
      });
    }
    /** Create a function to show a modal for deleting a Submission */
    function deleteModal() {
      var confirmationWindowInstance = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="submissionViewCtr.closeConfirmationWindow(\'nein\');"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">Submission löschen</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie diese Submission wirklich löschen? </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="submissionViewCtr.closeConfirmationWindow(\'ja\');">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="submissionViewCtr.closeConfirmationWindow(\'nein\');">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'submissionViewCtr',
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
          deleteSubmission(vm.data.submission.id);
        } else {
          return false;
        }
        return null;
      });
    }

    /** Create a function to delete the Submission */
    function deleteSubmission(id) {
      SubmissionService.deleteSubmission(id).success(function (data) {
        $state.go('project.search');
      }).error(function (response, status) {
        if (status === 400) { // Validation errors.
          QFormJSRValidation.markErrors($scope,
            $scope.submissionViewCtr.submissionForm, response);
        }
      });

    }

    function navigateToSubmittentList(submissionId) {
      $state.go('offerListView', {
        id: submissionId
      });
    }

    function navigateToExamination(submissionId) {
      $state.go('examination.view', {
        id: submissionId
      });
    }

    function navigateToExaminationNegotiatedProcedure(submissionId) {
      $state.go('examination.formal', {
        id: submissionId
      });
    }

    /**Format number according to specifications */
    function formatAmount(num) {
      return AppService.formatAmount(num);
    }

    /** Function to display object information */
    function getObjectInfo(object) {
      return AppService.getObjectInfo(object);
    }
  }
})();
