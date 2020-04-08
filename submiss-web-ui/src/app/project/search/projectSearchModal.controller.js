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
 * @name projectSearchModalCtrl.controller.js
 * @description ProjectSearchModalCtrl controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.project")
    .controller("ProjectSearchModalCtrl", ProjectSearchModalCtrl);

  /** @ngInject */
  function ProjectSearchModalCtrl($scope, $location, $anchorScroll, $state,
    AppService, ProjectService, StammdatenService, QFormJSRValidation, SubmissionService, NgTableParams, $filter, $locale, $uibModalInstance, submission) {
    const vm = this;

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.searchForm = {};
    vm.submission = submission;
    vm.objects = [];
    vm.projects = [];
    vm.selectedProject = {};
    vm.notSelected = false;

    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.search = search;
    vm.gotoSearchResults = gotoSearchResults;
    vm.objectChange = objectChange;
    vm.loadProjects = loadProjects;
    vm.loadObjects = loadObjects;
    vm.objectValue = objectValue;
    vm.moveProjectData = moveProjectData;
    vm.closeModal = closeModal;

    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      vm.loadObjects();
    }

    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});

    function loadObjects() {
      StammdatenService.readAllObjects()
        .success(function (data) {
          vm.objects = data;
        })
        .error(function (response, status) {});
    }

    function loadProjects() {
      ProjectService.readProjectsNames(submission.project.id).success(function (data) {
        vm.projects = data;
      }).error(function (response, status) {});
    }

    //Function that searches for Tenders
    function search() {

      vm.searchForm.excludedProject = submission.project.id
      vm.tableParams = new NgTableParams({
        count: 10,
        sorting: {
          objectName: 'asc'
        }
      }, {
        getData: function ($defer, params) {
          vm.searchForm.filter = params.filter();
          if (vm.tableParams.page() != null && vm.tableParams.page() !== 0 && vm.tableParams.count() !== null && vm.tableParams.count() !== 0) {
            var sortColumn = '';
            var sortType = '';

            for (var k in vm.tableParams.sorting()) {
              sortColumn = k;
              sortType = vm.tableParams.sorting()[k];
            }

            return ProjectService.search(vm.searchForm, vm.tableParams.page(), vm.tableParams.count(), sortColumn, sortType).then(function (projectListResult) {
              params.total(projectListResult.data.count);
              return projectListResult.data.results;
            });
          }
        }
      });

      vm.gotoSearchResults();
    }

    function gotoSearchResults() {
      // set the location.hash to the id of
      // the element you wish to scroll to.
      $anchorScroll('results');
    }

    function objectChange() {
      // clear selected project names
      vm.searchForm.projectNames = [];
      if (vm.searchForm.objectId) {
        ProjectService.readProjectsByObject(vm.searchForm.objectId)
          .success(function (data) {
            vm.projects = data;
          })
          .error(function (response, status) {

          });
      } else {
        vm.loadProjects();
      }
    }

    function objectValue(value2) {
      if (value2 === null) {
        return "";
      } else {
        return ", " + value2;
      }
    }

    function moveProjectData(result) {
      if (!angular.equals({}, result)) {
        SubmissionService.moveProjectData(submission.id, submission.version, result.projectId, result.projectVersion)
          .success(function (data) {
            $uibModalInstance.close();
            $state.reload();
          }).error(function (response, status) {
            if (status === 400 || status === 409) {
              vm.errorFieldsVisible = true;
              QFormJSRValidation.markErrors($scope,
                $scope.projectSearchModalCtrl.projectSearchForm, response);
            }
          });
      } else {
        vm.notSelected = true;
      }
    }

    function closeModal() {
      if (vm.errorFieldsVisible) {
        var cancelModal = AppService.cancelModal();
        return cancelModal.result.then(function (response) {
          if (response) {
            $uibModalInstance.close();
            $state.go('submissionView', {
              id: submission.id
            }, {
              reload: true
            });
          }
        });
      } else {
        $uibModalInstance.close();
      }
    }
  }
})();
