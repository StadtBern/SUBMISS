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
    .module("submiss.project").controller("ProjectViewController",
      ProjectViewController);

  /** @ngInject */
  function ProjectViewController($rootScope, $scope, $state, $stateParams,
    ProjectService, $uibModal, QFormJSRValidation, AppService, AppConstants) {
    var vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.name = $stateParams.name;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.readProject = readProject;
    vm.deleteProject = deleteProject;
    vm.deleteModal = deleteModal;
    vm.editProject = editProject;
    vm.createSubmission = createSubmission;
    vm.navigateToProjectSubmissions = navigateToProjectSubmissions;
    vm.getObjectInfo = getObjectInfo;
    vm.secProjectEdit = false;
    vm.secProjectDelete = false;
    vm.secTenderCreate = false;
    vm.secTenderListView = false;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      ProjectService.loadProjectDetails($stateParams.id)
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            if ($rootScope.$previousState === 'project.search' || $rootScope.$previousState === undefined || $rootScope.$previousState === 'project.create' ||
              $rootScope.$previousState === 'project.view' || $rootScope.$previousState === 'project.view.from.Offers' ||
              $rootScope.$previousState === 'company.search' || $rootScope.$previousState === '' || $rootScope.$previousState === 'projectSubmissionsView') {
              vm.readProject($stateParams.id);
            } else {
              vm.readProject($rootScope.selectedProjectId);
            }
            vm.secProjectEdit = AppService.isOperationPermitted(AppConstants.OPERATION.PROJECT_EDIT, null);
            vm.secProjectDelete = AppService.isOperationPermitted(AppConstants.OPERATION.PROJECT_DELETE, null);
            vm.secTenderCreate = AppService.isOperationPermitted(AppConstants.OPERATION.TENDER_CREATE, null);
            vm.secTenderListView = AppService.isOperationPermitted(AppConstants.OPERATION.TENDER_LIST_VIEW, null);
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
        $rootScope.projectName = vm.data.project.projectName;
      }).error(function (response, status) {});
    }
    /**Create a function to show a modal for deleting a Projekt */
    function deleteModal() {
      var confirmationWindowInstance = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="projectViewCtr.closeConfirmationWindow(\'nein\');"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">Projekt löschen</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie das Projekt wirklich löschen? </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="projectViewCtr.closeConfirmationWindow(\'ja\');">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="projectViewCtr.closeConfirmationWindow(\'nein\');">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'projectViewCtr',
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
          deleteProject();
        } else {
          return false;
        }
        return null;
      });
    }
    /**Create a function to delete the Projekt */
    function deleteProject() {
      if (vm.data.project) {
        ProjectService.deleteProject(vm.data.project.id)
          .success(function (data, status) {
            if (status === 403) { // Security checks.
              return;
            }
            $state.go('project.search', {});
          }).error(function (response, status) {
            if (status === 400) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.projectViewCtr.projectForm, response);
            }
          });
      } else {
        //Das System zeigt dem Benutzer die Startseite an.
        $state.go('project.search', {});
      }
    }

    function editProject() {
      $uibModal.open({
        template: '<div class="modal-header">' +
          '<button type="button" class="close" aria-label="Close" ' +
          'ng-click="projectUpdateCtr.closeWindow(projectUpdateCtr.projectForm.$dirty)"><span aria-hidden="true">&times;</span></button>' +
          '<h4 class="modal-title">Projekt bearbeiten</h4>' +
          '</div>' +
          '<div class="modal-body">' +
          '<div ng-include src="' +
          '\'' +
          'app/project/update/projectUpdate.html' +
          '\'">' +
          '<div>' +
          '<div>',
        controller: 'ProjectUpdateController',
        controllerAs: 'projectUpdateCtr',
        size: 'lg',
        backdrop: 'static',
        keyboard: false,
        resolve: {
          project: function () {
            return vm.data.project;
          },
          editProject: function () {
            return true;
          }
        }
      });
    }

    function navigateToProjectSubmissions(projectId, projectName) {
      ProjectService.projectExists(projectId).success(function (data) {
        $state.go('projectSubmissionsView', {
          id: projectId,
          name: projectName,
          stateFrom: 'project.view'
        });
      }).error(function (response, status) {
        if (status === 400) { // Validation errors.
          QFormJSRValidation.markErrors($scope,
            $scope.projectViewCtr.projectForm, response);
        }
      });
    }

    function createSubmission() {
      $uibModal.open({
        template: '<div class="modal-header">' +
          '<button type="button" class="close" data-dismiss="modal" ' +
          'aria-label="Close" ng-click="submissionCreateCtr.closeWindow(submissionCreateCtr.submissionForm.$dirty)"><span aria-hidden="true">&times;</span></button>' +
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
    /** Function to display object information */
    function getObjectInfo(object) {
      return AppService.getObjectInfo(object);
    }
  }
})();
