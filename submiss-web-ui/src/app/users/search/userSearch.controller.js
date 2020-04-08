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
 * @name userSearch.controller.js
 * @description UserSearchController controller
 */
(function () {
  "use strict";
  angular // eslint-disable-line no-undef
    .module("submiss.users")
    .controller("UserSearchController", UserSearchController);

  /** @ngInject */
  function UserSearchController($scope, $state, $location, $anchorScroll,
    UsersService, StammdatenService, NgTableParams, $filter, $uibModal) {
    var vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.searchForm = {};
    vm.departments = [];
    vm.directorates = [];
    vm.searchForm.tenant = null;
    vm.tenants = [];
    vm.roles = [];
    vm.options = [{
      value: 0,
      label: 'Nein'
    }, {
      value: 1,
      label: 'Ja'
    }];
    vm.options = $filter('orderBy')(vm.options, 'label');
    vm.lastTenant = null;
    vm.totalPages = 0;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.changeNrOfPages = changeNrOfPages;
    vm.search = search;
    vm.resetPage = resetPage;
    vm.loadDepartments = loadDepartments;
    vm.loadDepartmentsPerTenant = loadDepartmentsPerTenant;
    vm.loadTenants = loadTenants;
    vm.loadRoles = loadRoles;
    vm.editUser = editUser;
    vm.gotoSearchResults = gotoSearchResults;
    vm.setClickedRow = setClickedRow;
    vm.tenantChange = tenantChange;
    vm.loadDirectorates = loadDirectorates;
    vm.departmentChange = departmentChange;
    vm.directorateChange = directorateChange;
    vm.loadDirectoratesByTenant = loadDirectoratesByTenant;
    vm.checkForDepartmentsAndDirectorates = checkForDepartmentsAndDirectorates;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      UsersService.loadUserSearch()
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            vm.loadTenants();
            vm.loadRoles();
            vm.loadDirectorates();
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
    function search() {
      UsersService.search(vm.searchForm)
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          }
          vm.users = data;
          vm.tableParams = new NgTableParams({
            page: 1,
            count: 10,
            sorting: {
              firstName: 'asc'
            },
            filter: {
              firstName: ''
            }
          }, {
            total: vm.users.length,
            getData: function ($defer, params) {
              var orderedData = params.sorting() ? $filter('orderBy')(vm.users, params.orderBy()) : vm.users;
              if (params.count() === -1) {
                $defer.resolve(orderedData);
              } else {
                $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
              }
            }
          });
        }).error(function (response, status) {});
    }

    function resetPage() {
      $state.go('users.search', {}, {
        reload: true
      });
    }
    //Function that returns all Abteilungen
    function loadDepartments() {
      UsersService.readAllPermittedDepartments()
        .success(function (data) {
          vm.departments = data;
        })
        .error(function () {
          NotificationsService.show('error', "Abteilungen konnten nicht geladen werden.");
        });
    }
    //Function that returns all Abteilungen per Tenant
    function loadDepartmentsPerTenant(tenantId) {
      StammdatenService.readDepartmentsByTenant(tenantId)
        .success(function (data) {
          vm.departments = data;
        })
        .error(function () {
          NotificationsService.show('error', "Abteilungen konnten nicht geladen werden.");
        });
    }
    //Function that returns all Mandants
    function loadTenants() {
      UsersService.getUserTenants()
        .success(function (data) {
          vm.tenants = data;
          /* in case only one tenant can be selected set it as the selected value in the drop down list
           * and return only his department, otherwise return all departments
           */
          if (vm.tenants.length === 1) {
            vm.searchForm.tenant = vm.tenants[0];
            vm.loadDepartmentsPerTenant(vm.searchForm.tenant.id);
          } else {
            vm.loadDepartments();
          }
        }).error(function () {
          NotificationsService.show('error', "Mandanten konnten nicht geladen werden.");
        });
    }
    //Function that returns all Rollen
    function loadRoles() {
      UsersService.readAllRoles()
        .success(function (data) {
          vm.roles = data;
        })
        .error(function () {
          NotificationsService.show('error', "Rollen konnten nicht geladen werden.");
        });
    }
    //Function that returns all Permitted Directorates
    function loadDirectorates() {
      UsersService.readAllPermittedDirectorates()
        .success(function (data) {
          vm.directorates = data;
        })
        .error(function (response, status) {});
    }

    function gotoSearchResults() {
      // set the location.hash to the id of
      // the element you wish to scroll to.
      // call $anchorScroll()
      $anchorScroll('results');
    }

    function tenantChange(tenant) {
      // if user chooses a different tenant from that
      // selected or clears
      // this fied ,departments and directorates get empty
      if (tenant === null ||
        (vm.lastTenant !== null && vm.lastTenant.name !== tenant.name)) {
        vm.searchForm.secondaryDepartments = null;
        vm.searchForm.directorates = null;
      }

      if (tenant !== null) {
        StammdatenService.readDepartmentsByTenant(tenant.id).success(
          function (data) {
            vm.departments = data;
            vm.loadDirectoratesByTenant(tenant.id);

          }).error(function (response, status) {

        });
      } else {
        vm.loadDepartments();
        vm.loadDirectorates();
      }
    }

    function editUser(user) {
      var userModal = $uibModal.open({
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
            return false;
          }
        }
      });
      return userModal.result.then(function () {}, function (response) {
        if (response === 'Speichern') {
          editUser(data);
        } else {
          return false;
        }
        return null;
      });
    }
    //Function to set the active row of the table
    //Called when the user clicks on a row
    function setClickedRow(index) {
      vm.selectedRow = index;
    }
    //Function that calculates the number of pages
    function changeNrOfPages() {
      vm.totalPages = Math.ceil(vm.tableParams.total() / vm.tableParams.count());
    }
    // this function is triggered when the user clicks on the department
    // dpodpdown
    function departmentChange(secondaryDepartments) {
      if (secondaryDepartments.length === 1) {
        StammdatenService.readTenantByDepartment(secondaryDepartments[0].departmentId.id)
          .success(function (data) {
            vm.lastTenant = data;
            vm.searchForm.tenant = data;
          }).error(function (response, status) {});
        // if user clears all the selected departments,associated tenant
        // and selected directorates get cleared
      }
    }
    // this function is triggered when the user clicks on the directorate
    // dpodpdown
    function directorateChange(directorates) {
      if (directorates.length === 1) {
        StammdatenService.readTenantByDirectorate(directorates[0].directorateId.id)
          .success(function (data) {
            vm.lastTenant = data;
            vm.searchForm.tenant = data;
          }).error(function (response, status) {});
        // if user clears all the selected directorates,associated
        // tenant and selected departments get cleared
      }
    }
    //this function gets the directories of a specific tenant
    function loadDirectoratesByTenant(tenantId) {
      StammdatenService.readDirectoratesByTenant(tenantId).success(
        function (data) {
          vm.directorates = data;
        }).error(function (response, status) {});
    }
    /**
     * Function to check if there are no selected secondary departments and directorates,
     * so as to delete the selected tenant.
     * */
    function checkForDepartmentsAndDirectorates() {
      if ((!vm.searchForm.secondaryDepartments || vm.searchForm.secondaryDepartments.length === 0) &&
        (!vm.searchForm.directorates || vm.searchForm.directorates.length === 0)) {
        vm.searchForm.tenant = null;
      }
    }
  }
})();
