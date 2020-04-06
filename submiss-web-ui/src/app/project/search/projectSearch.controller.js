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
 * @name projectSearch.controller.js
 * @description ProjectSearchController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.project")
    .controller("ProjectSearchController", ProjectSearchController);

  /** @ngInject */
  function ProjectSearchController($scope, $location, $anchorScroll, $state,
    ProjectService, StammdatenService, NgTableParams, $filter, $locale,
    AppService, AppConstants) {
    const vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var results = 0;
    var tempCount = 10;
    var tempPage = 1;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.searchForm = {};
    vm.searchForm.offerDateFrom = null;
    vm.searchForm.offerDateUntil = null;
    vm.tenders = {};
    vm.objects = [];
    vm.procedures = [];
    vm.projects = [];
    vm.departments = [];
    vm.directorates = [];
    vm.totalPages = 0;
    vm.isCollapsed = true;
    vm.secTenderView = false;
    vm.processes = [{
        id: null,
        title: ''
      },
      {
        id: 0,
        title: 'Selektiv'
      },
      {
        id: 1,
        title: 'Offen'
      },
      {
        id: 2,
        title: 'Einladungsverfahren'
      },
      {
        id: 3,
        title: 'Freihändig'
      },
      {
        id: 4,
        title: 'Freihändig mit Konkurrenz'
      }
    ];
    vm.divTableHeight = {};
    vm.startingDisplay = 0;
    vm.totalDisplay = 50;

    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.changeNrOfPages = changeNrOfPages;
    vm.setClickedRow = setClickedRow;
    vm.loadObjects = loadObjects;
    vm.loadProcedures = loadProcedures;
    vm.loadProjects = loadProjects;
    vm.loadDepartments = loadDepartments;
    vm.loadWorkTypes = loadWorkTypes;
    vm.search = search;
    vm.objectChange = objectChange;
    vm.loadDirectorates = loadDirectorates;
    vm.navigateToProject = navigateToProject;
    vm.navigateToSubmission = navigateToSubmission;
    vm.gotoSearchResults = gotoSearchResults;
    vm.directorateChange = directorateChange;
    vm.departmentChange = departmentChange;
    vm.reloadPage = reloadPage;
    vm.objectValue = objectValue;
    vm.openOfferDateFrom = openOfferDateFrom;
    vm.openOfferDateUntil = openOfferDateUntil;
    vm.openSubmissionDeadlineFilter = openSubmissionDeadlineFilter;
    vm.loadMore = loadMore;

    // Activating the controller.
    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      vm.loadObjects();
      vm.loadProcedures();
      vm.loadDepartments();
      vm.loadDirectorates();
      vm.loadWorkTypes();
      vm.secTenderView = AppService.isOperationPermitted(
        AppConstants.OPERATION.TENDER_VIEW, null);
    }

    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});

    /***********************************************************************
     * Functions.
     **********************************************************************/
    //Function that returns all Objects
    function loadObjects() {
      StammdatenService.readAllObjects()
        .success(function (data) {
          vm.objects = data;
        })
        .error(function (response, status) {});
    }

    //Function that returns all Procedures
    function loadProcedures() {
      StammdatenService.readAllProcedures()
        .success(function (data) {
          vm.procedures = data;
        })
        .error(function (response, status) {});
    }

    // Function that returns all Projects names
    function loadProjects() {
      ProjectService.readProjectsNames(null).success(function (data) {
        vm.projects = data;
      }).error(function (response, status) {});
    }

    // Function that returns all Abteilungen
    function loadDepartments() {
      StammdatenService.readAllDepartments()
        .success(function (data) {
          vm.departments = data;
        })
        .error(function (response, status) {});
    }

    //Function that returns all Directorates
    function loadDirectorates() {
      StammdatenService.readAllDirectorates()
        .success(function (data) {
          vm.directorates = data;
        })
        .error(function (response, status) {});
    }

    // Function that returns all Arbeitsgattungen/Work Types
    function loadWorkTypes() {
      StammdatenService.readAllWorkTypes().success(function (data) {
        vm.workTypes = data;
      }).error(function (response, status) {});
    }

    //Function that calculates the number of pages
    function changeNrOfPages() {
      vm.totalPages = Math.ceil(
        vm.tableParams.total() / vm.tableParams.count());
    }

    //Function to set the active row of the table
    //Called when the user clicks on a row
    function setClickedRow(index) {
      vm.selectedRow = index;
    }

    //Function that searches for Tenders
    function search() {
      if (angular.isUndefined(vm.searchForm.offerDateFrom) ||
        angular.isUndefined(vm.searchForm.offerDateUntil)) {
        vm.invalidDate = true;
      } else {
        vm.invalidDate = false;
        vm.tableParams = new NgTableParams({
          count: 10,
          sorting: {
            objectName: 'asc'
          }
        }, {
          getData: function ($defer, params) {
            vm.searchForm.filter = params.filter();
            if (vm.tableParams.page() != null && vm.tableParams.page() !== 0 &&
              vm.tableParams.count() !== null && vm.tableParams.count() !== 0) {
              /**
               * when another pagination option is chosen
               * use $anchorScroll('fromStart') to move to the top of the table,
               * $anchorScroll('StartingPoint') to move to the first element of the table
               * and initialize totalDisplay to the starting value
               */
              if (vm.tableParams.count() !== tempCount) {
                tempCount = vm.tableParams.count();
                vm.totalDisplay = 50;
                vm.gotoSearchResults('fromStart');
                vm.gotoSearchResults('StartingPoint');
              }
              /**
               * when changing pages
               * use $anchorScroll('fromStart') to move to the top of the table
               * $anchorScroll('StartingPoint') to move to the first element of the table
               * and initialize totalDisplay to the starting value
               */
              if (vm.tableParams.page() !== tempPage) {
                tempPage = vm.tableParams.page();
                vm.totalDisplay = 50;
                vm.gotoSearchResults('fromStart');
                vm.gotoSearchResults('StartingPoint');
              }
              var sortColumn = '';
              var sortType = '';
              for (var k in vm.tableParams.sorting()) {
                sortColumn = k;
                sortType = vm.tableParams.sorting()[k];
              }
              AppService.setPaneShown(true);
              return ProjectService.search(vm.searchForm, vm.tableParams.page(),
                vm.tableParams.count(), sortColumn, sortType).then(
                function (projectListResult) {
                  AppService.setPaneShown(false);
                  vm.setClickedRow(null);
                  params.total(projectListResult.data.count);
                  results = projectListResult.data.count;
                  vm.divTableHeight = AppService.adjustTableHeight(results,
                    vm.openSubmissionDeadlineFilter.opened);
                  return projectListResult.data.results;
                });
            }
          }
        });
      }
      vm.gotoSearchResults('results');
    }

    function loadMore(tableParams) {
      // this function is triggered only if pagination option
      // is Alle
      if (tableParams.count() < 0) {
        vm.totalDisplay += 50;
      }
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

    //Function that returns empty and not null when value2 is null
    function objectValue(value2) {
      if (value2 === null) {
        return "";
      } else {
        return ", " + value2;
      }
    }

    function departmentChange() {
      // clear selected directorates
      // vm.searchForm.directoratesIDs = [];
      if (vm.searchForm.departmentsIDs.length > 0) {
        StammdatenService.readDirectoratesByDepartmentsIDs(
            vm.searchForm.departmentsIDs)
          .success(function (data) {
            vm.directorates = data;
          })
          .error(function (response, status) {

          });
      } else {
        vm.loadDirectorates();
      }
    }

    function directorateChange() {
      // clear selected departments
      // vm.searchForm.departmentsIDs = [];
      if (vm.searchForm.directoratesIDs.length > 0) {
        StammdatenService.readDepartmentsByDirectoratesIDs(
            vm.searchForm.directoratesIDs)
          .success(function (data) {
            vm.departments = data;
          })
          .error(function (response, status) {

          });
      } else {
        vm.loadDepartments();
      }
    }

    function navigateToProject(projectId, projectName) {
      $state.go('project.view', {
        id: projectId,
        name: projectName
      });
    }

    function navigateToSubmission(submissionId) {
      $state.go('submissionView', {
        id: submissionId
      });
    }

    function reloadPage() {
      $state.go('project.search', {}, {
        reload: true
      });
    }

    function gotoSearchResults(id) {
      // set the location.hash to the id of
      // the element you wish to scroll to.
      $anchorScroll(id);
    }

    function openOfferDateFrom() {
      vm.openOfferDateFrom.opened = !vm.openOfferDateFrom.opened;
    }

    function openOfferDateUntil() {
      vm.openOfferDateUntil.opened = !vm.openOfferDateUntil.opened;
    }

    function openSubmissionDeadlineFilter() {
      vm.openSubmissionDeadlineFilter.opened = !vm.openSubmissionDeadlineFilter.opened;
      vm.divTableHeight = AppService.adjustTableHeight(results,
        vm.openSubmissionDeadlineFilter.opened);
    }
  }
})();
