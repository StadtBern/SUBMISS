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
    .module("submiss.project").controller("SubmissionsViewController",
      SubmissionsViewController);

  /** @ngInject */
  function SubmissionsViewController($rootScope, $scope, $state, $stateParams, QFormJSRValidation,
    ProjectService, SubmissionService, $uibModal, NgTableParams, $filter, AppConstants, AppService) {
    const vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.currentStatus = [];
    vm.secTenderDelete = false;
    vm.secTenderView = false;
    vm.secTenderEdit = [];
    vm.secTenderCreate = false;
    vm.status = AppConstants.STATUS;
    vm.group = AppConstants.GROUP;

    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.readProject = readProject;
    vm.deleteSubmission = deleteSubmission;
    vm.deleteModal = deleteModal;
    vm.createSubmission = createSubmission;
    vm.readProjectSubmissions = readProjectSubmissions;
    vm.navigateToProjectSubmissions = navigateToProjectSubmissions;
    vm.navigateToSubmissionView = navigateToSubmissionView;
    vm.editSubmission = editSubmission;
    vm.navigateToProject = navigateToProject;
    vm.tableParams = new NgTableParams({}, {
      dataset: vm.submissions
    });
    vm.readStatusOfSubmission = readStatusOfSubmission;
    vm.getObjectInfo = getObjectInfo;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      ProjectService.loadSubmissionList($stateParams.id)
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            vm.readProject($stateParams.id);
            vm.readProjectSubmissions($stateParams.id);
            vm.secTenderDelete = AppService.isOperationPermitted(AppConstants.OPERATION.TENDER_DELETE, null);
            vm.secTenderView = AppService.isOperationPermitted(AppConstants.OPERATION.TENDER_VIEW, null);
            vm.secTenderCreate = AppService.isOperationPermitted(AppConstants.OPERATION.TENDER_CREATE, null);
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
    $rootScope.$on('$stateChangeSuccess', function (event, to, toParams, from, fromParams) {
      //save the previous state in a rootScope variable so that it's accessible from everywhere
      $rootScope.previousState = from;
    });


    function readProject(id) {
      ProjectService.readProject(id).success(function (data) {
        vm.data.project = data;
      }).error(function (response, status) {});
    }
    /**Create a function to show a modal for deleting a Submission */
    function deleteModal(id) {
      var confirmationWindowInstance = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="submissionsViewCtr.closeConfirmationWindow(\'nein\');"><span aria-hidden="true">&times;</span></button>' +
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
            '<button type="button" class="btn btn-primary" ng-click="submissionsViewCtr.closeConfirmationWindow(\'ja\');">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="submissionsViewCtr.closeConfirmationWindow(\'nein\');">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'submissionsViewCtr',
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
          deleteSubmission(id);
        } else {
          return false;
        }
        return null;
      });
    }

    /**Create a function to delete the Submission */
    function deleteSubmission(id) {
      SubmissionService.deleteSubmission(id).success(function (data) {
        $state.reload();
      }).error(function (response, status) {
        if (status === 400) { // Validation errors.
          QFormJSRValidation.markErrors($scope,
            $scope.submissionsViewCtr.submissionForm, response);
        }
      });

    }

    // Function that returns the list of all submissions of specific project
    function readProjectSubmissions(id) {
      SubmissionService.readProjectSubmissions(id).then(function (results) {
        vm.submissions = results.data;
        for (var i = 0; i < vm.submissions.length; i++) {
          vm.secTenderEdit[i] = AppService.isOperationPermitted(AppConstants.OPERATION.TENDER_EDIT, null);
          vm.readStatusOfSubmission(vm.submissions[i].id, i);
        }
        vm.tableParams = new NgTableParams({
          page: 1,
          count: 10,
          sorting: {
            workType: "asc"
          },
          filter: {
            description: ''
          }
        }, {
          total: vm.submissions.length,
          getData: function ($defer, params) {
            var filteredData = params.filter() ?
              $filter('filter')(vm.submissions, params.filter()) :
              tabledata;

            var orderedData = params.sorting() ?
              $filter('orderBy')(filteredData, params.orderBy()) : filteredData;
            if (params.count() === -1) {
              $defer.resolve(orderedData);
            } else {
              $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
            }
          }
        });
      });
    }

    function editSubmission(submission) {
      $uibModal.open({
        template: '<div class="modal-header">' +
          '<button type="button" class="close" aria-label="Close" ' +
          'ng-click="submissionCreateCtr.closeWindow(submissionCreateCtr.submissionForm.$dirty)">' +
          '<span aria-hidden="true">&times;</span></button>' +
          '<h4 class="modal-title">Submission bearbeiten</h4>' +
          '</div>' +
          '<div class="modal-body">' +
          '<div ng-include src="' +
          '\'' +
          'app/submission/create/submissionCreate.html' +
          '\'">' +
          '<div>' +
          '<div>',
        controller: 'SubmissionCreateController',
        controllerAs: 'submissionCreateCtr',
        size: 'lg',
        backdrop: 'static',
        keyboard: false,
        resolve: {
          submission: function () {
            return submission;
          },
          project: function () {
            return submission.project;
          },
          editSubmission: function () {
            return true;
          }
        }

      });
    }


    function navigateToProjectSubmissions(project) {
      $state.go('projectSubmissionsView', {
        project: project
      });
    }

    function navigateToSubmissionView(submissionId) {
      SubmissionService.submissionExists(submissionId).success(function (data) {
        $state.go('submissionView', {
          id: submissionId
        });
      }).error(function (response, status) {
        if (status === 400) { // Validation errors.
          QFormJSRValidation.markErrors($scope,
            $scope.submissionsViewCtr.submissionForm, response);
        }
      });
    }

    function navigateToProject(projectId) {
      ProjectService.projectExists(projectId).success(function (data) {
        $state.go('project.view', {
          id: projectId
        });
      }).error(function (response, status) {
        if (status === 400) { // Validation errors.
          QFormJSRValidation.markErrors($scope,
            $scope.submissionsViewCtr.submissionForm, response);
        }
      });
    }

    function createSubmission() {
      $uibModal.open({
        template: '<div class="modal-header">' +
          '<button type="button" class="close" data-dismiss="modal" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
          '<h4 class="modal-title">Submission eröffnen, {{submissionCreateCtr.project.projectName}}, {{submissionCreateCtr.project.objectName.value1}}</h4>' +
          '</div>' +
          '<div class="modal-body">' +
          '<div ng-include src="' +
          '\'' +
          'app/submission/create/submissionCreate.html' +
          '\'">' +
          '<div>' +
          '<div>',
        controller: 'SubmissionCreateController',
        controllerAs: 'submissionCreateCtr',
        size: 'lg',
        backdrop: 'static',
        keyboard: false,
        resolve: {
          project: function () {
            return vm.data.project;
          },
          editSubmission: function () {
            return false;
          },
          submission: function () {
            return {};
          }
        }
      });
    }

    function readStatusOfSubmission(id, index) {
      SubmissionService.getCurrentStatusOfSubmission(id).success(function (data) {
        vm.currentStatus[index] = data;
      }).error(function (response, status) {

      });
    }

    /** Function to display object information */
    function getObjectInfo(object) {
      return AppService.getObjectInfo(object);
    }
  }
})();
