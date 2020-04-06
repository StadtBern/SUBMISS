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
 * @name tasksView.controller.js
 * @description TasksViewController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.tasks")
    .controller("TasksViewController", TasksViewController).filter(
      'customUserDateFilter',
      function ($filter) {
        return function (values, dateValue, createdOn) {
          var filtered = [];
          if (typeof values != 'undefined' && typeof dateValue != 'undefined') {
            angular.forEach(values, function (value) {
              if (createdOn) {
                if (value.createdOn != null && $filter('date')(value.createdOn,
                    'dd.MM.yyyy').indexOf($filter('date')(dateValue, 'dd.MM.yyyy')) >=
                  0) {
                  filtered.push(value);
                }
              }
            });
          }
          return filtered;
        }
      }).filter("customDescriptionFilter", function ($filter) {
      return function (values, filterObj) {
        var filtered = [];
        if (typeof values != 'undefined' && typeof filterObj != 'undefined') {
          angular.forEach(values, function (value) {
            if (value.description != null) {
              var translatedTest = $filter('translate')(value.description,
                filterObj);
              translatedTest = translatedTest.replace('XY',
                value.userAutoAssigned);
              if (translatedTest.indexOf(filterObj.description) > -1) {
                filtered.push(value);
              }
            }
          });
        }
        return filtered;
      }
    });

  /** @ngInject */
  function TasksViewController($scope, $state, TasksService, NgTableParams,
    $filter,
    AppService, $uibModal, $translate, StammdatenService) {
    var vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var resultsCount = 0; // Results counter.
    var resultsUserCount = 0;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.allTasks = [];
    vm.allUserTasks = [];
    vm.controllingTasks = ['NOT_CLOSED_SUBMISSION', 'ILLEGAL_THRESHOLD'];
    vm.userTaskTableAccordionShow = true;
    vm.taskTableAccordionShow = true;
    vm.oneAtATime = true;
    vm.status = {
      isFirstOpen: true,
      isFirstDisabled: false
    };
    vm.openTasksAccordionTitle = null;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.getAllTasks = getAllTasks;
    vm.openCreatedOnFilter = openCreatedOnFilter;
    vm.openCreatedOnUserTaskFilter = openCreatedOnUserTaskFilter;
    vm.canBeSettled = canBeSettled;
    vm.gotoTaskPage = gotoTaskPage;
    vm.settleTask = settleTask;
    vm.undertakeTask = undertakeTask;
    vm.openMailModal = openMailModal;
    vm.getAllUserTasks = getAllUserTasks;
    vm.editUser = editUser;
    vm.openUserTaskTableAccordion = openUserTaskTableAccordion;
    vm.openTaskTableAccordion = openTaskTableAccordion;
    vm.defineOpenTasksAccordionTitle = defineOpenTasksAccordionTitle;
    // Activating the controller.
    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      defineOpenTasksAccordionTitle();
      vm.getAllTasks();
      vm.getAllUserTasks();
    }

    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});

    /***********************************************************************
     * Functions.
     **********************************************************************/
    function getAllTasks() {
      TasksService.getAllTasks(false)
        .success(function (data) {
          vm.allTasks = data;
          vm.tableParams = new NgTableParams({
            page: 1,
            count: 10,
            sorting: {
              createdOn: 'desc'
            }
          }, {
            total: vm.allTasks.length,
            getData: function ($defer, params) {
              var filterObj = params.filter(true);
              var filteredData;
              if (filterObj) {
                filteredData = vm.allTasks;
                var filteredCreatedOn = false;
                var createdOnFilterValue;
                var filteredDescription = false;
                var descriptionFilterValue;
                if (filterObj.createdOn) {
                  filteredData = $filter('customUserDateFilter')(vm.allTasks,
                    filterObj.createdOn, true);
                  filteredCreatedOn = true;
                  createdOnFilterValue = filterObj.createdOn;
                  delete filterObj.createdOn;
                }
                if (filterObj.description) {
                  filteredData = $filter('customDescriptionFilter')(filteredData,
                    filterObj);
                  filteredDescription = true;
                  descriptionFilterValue = filterObj.description;
                  delete filterObj.description;
                }
                filteredData = $filter('filter')(filteredData, filterObj);
                if (filteredCreatedOn) {
                  filterObj.createdOn = createdOnFilterValue;
                }
                if (filteredDescription) {
                  filterObj.description = descriptionFilterValue;
                }
              } else {
                filteredData = vm.allTasks;
              }
              resultsCount = filteredData.length;
              vm.divTableHeight = AppService.adjustTableHeight(resultsCount,
                vm.openCreatedOnFilter.opened);
              var orderedData = params.sorting() ? $filter('orderBy')(
                filteredData, params.orderBy()) : filteredData;
              if (params.count() === -1) {
                $defer.resolve(orderedData);
              } else {
                $defer.resolve(
                  orderedData.slice((params.page() - 1) * params.count(),
                    params.page() * params.count()));
              }
            }
          });
        }).error(function (response, status) {});
    }

    function getAllUserTasks() {
      TasksService.getAllTasks(true)
        .success(function (data) {
          vm.allUserTasks = data;
          vm.tableParamsUser = new NgTableParams({
            page: 1,
            count: 10,
            sorting: {
              createdOn: 'desc'
            }
          }, {
            total: vm.allUserTasks.length,
            getData: function ($defer, params) {
              var filterObj = params.filter(true);
              var filteredData;
              if (filterObj) {
                filteredData = vm.allUserTasks;
                var filteredCreatedOn = false;
                var createdOnFilterValue;
                var filteredDescription = false;
                var descriptionFilterValue;
                if (filterObj.createdOn) {
                  filteredData = $filter('customUserDateFilter')(vm.allUserTasks,
                    filterObj.createdOn, true);
                  filteredCreatedOn = true;
                  createdOnFilterValue = filterObj.createdOn;
                  delete filterObj.createdOn;
                }
                if (filterObj.description) {
                  filteredData = $filter('customDescriptionFilter')(filteredData,
                    filterObj);
                  filteredDescription = true;
                  descriptionFilterValue = filterObj.description;
                  delete filterObj.description;
                }
                filteredData = $filter('filter')(filteredData, filterObj);
                if (filteredCreatedOn) {
                  filterObj.createdOn = createdOnFilterValue;
                }
              } else {
                filteredData = vm.allUserTasks;
              }
              resultsUserCount = filteredData.length;
              vm.divTableHeight = AppService.adjustTableHeight(resultsUserCount,
                vm.openCreatedOnUserTaskFilter.opened);
              var orderedData = params.sorting() ? $filter('orderBy')(
                filteredData, params.orderBy()) : filteredData;
              if (params.count() === -1) {
                $defer.resolve(orderedData);
              } else {
                $defer.resolve(orderedData
                  .slice((params.page() - 1) * params.count(),
                    params.page() * params.count()));
              }
            }
          });
        }).error(function (response, status) {});
    }

    function openCreatedOnFilter() {
      vm.openCreatedOnFilter.opened = !vm.openCreatedOnFilter.opened;
      vm.divTableHeight = AppService.adjustTableHeight(resultsCount,
        vm.openCreatedOnFilter.opened);
    }

    function openCreatedOnUserTaskFilter() {
      vm.openCreatedOnUserTaskFilter.opened = !vm.openCreatedOnUserTaskFilter.opened;
      vm.divTableHeight = AppService.adjustTableHeight(resultsCount,
        vm.openCreatedOnUserTaskFilter.opened);
    }

    //Only Contolling Tasks can be settled
    function canBeSettled(task) {
      if (vm.controllingTasks.indexOf(task.description) !== -1 || task.type ===
        1) {
        return true;
      }
      return false;
    }

    function gotoTaskPage(task) {
      if (task.submission) {
        window.open($state.href('submissionView', {
            id: task.submission.id
          }),
          '_blank');
      } else {
        window.open($state.href('company.view', {
            id: task.company.id
          }),
          '_blank');
      }
    }

    function settleTask(task) {
      var modalResult = AppService.openGenericModal("Bestätigungsdialog",
        "Μöchten Sie die Pendenz wirklich erledigen?");
      return modalResult.result
        .then(function (response) {
          if (response) {
            TasksService.settleTask(task.id).success(
                function (data) {
                  $state.reload();
                })
              .error(function (response, status) {});
          }
          return true;
        });
    }

    function undertakeTask(task) {
      var modalResult = AppService.openGenericModal("Bestätigungsdialog",
        "Μöchten Sie die Pendenz wirklich übernehmen?");
      return modalResult.result
        .then(function (response) {
          if (response) {
            TasksService.undertakeTask(task.id)
              .success(function (data) {
                $state.reload();
              }).error(function (response, status) {});
          }
          return true;
        });
    }

    function openMailModal(task) {
      var id = '';
      if (task.submission != null) {
        id = task.submission.id;
      } else {
        id = task.company.id;
      }
      if (task.description !== "CHECK_TENDERLIST" && task.description !==
        "PROOF_REQUEST_PL_XY") {
        AppService.sendMailModal(id, null);
      } else {
        TasksService.openMailClient(task).success(
          function (data) {
            window.location.href = data;
          })
      }
    }

    function editUser(user) {
      var userModal = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="userEditCtr.closeWindow(userEditCtr.userForm.$dirty)"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">Benutzer bearbeiten</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div ng-include src="' +
            '\'' +
            'app/users/edit/userEdit.html' +
            '\'">' +
            '<div>' +
            '<div>',
          controller: 'UserEditController',
          controllerAs: 'userEditCtr',
          size: 'lg',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            user: function () {
              return user;
            },
            editUser: function () {
              return true;
            },
            fromTaskPage: function () {
              return true;
            }
          }
        });
    }

    function openUserTaskTableAccordion() {
      vm.userTaskTableAccordionShow = !vm.userTaskTableAccordionShow;
    }

    function openTaskTableAccordion() {
      vm.taskTableAccordionShow = !vm.taskTableAccordionShow;
    }

    /** Function to define the open tasks accordion title according to the tenant */
    function defineOpenTasksAccordionTitle() {
      StammdatenService.isTenantKantonBern()
        .success(function (data) {
          if (data) {
            vm.openTasksAccordionTitle = "Firmenteil";
          } else {
            vm.openTasksAccordionTitle = "Projektteil und Firmenteil";
          }
        });
    }
  }
})();
