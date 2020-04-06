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
 * @name userCreate.controller.js
 * @description UserCreateController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.users")
    .controller("UserCreateController", UserCreateController);

  /** @ngInject */
  function UserCreateController($scope, $state,
    QFormJSRValidation, UsersService, StammdatenService, $uibModal,
    toastr, AppConstants, $q, AppService) {
    const vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.user = {};
    vm.departments = [];
    vm.tenants = [];
    vm.roles = [];
    vm.markAsFirstUser = false;
    var readAllUsersFunction, readAllTenantsFunction, readAllRolesFunction;

    /***********************************************************************
     * Exported functions.
     **********************************************************************/

    vm.save = save;
    vm.resetPage = resetPage;
    vm.loadDepartments = loadDepartments;
    vm.loadTenants = loadTenants;
    vm.loadRoles = loadRoles;
    vm.loadSAMLAttributes = loadSAMLAttributes;
    vm.tenantChange = tenantChange;
    vm.departmentChange = departmentChange;
    vm.createFirstUser = createFirstUser;
    vm.setUnregisteredUserGroup = setUnregisteredUserGroup;
    // Activating the controller.
    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {

      readAllTenantsFunction = vm.loadTenants();
      readAllRolesFunction = vm.loadRoles();
      vm.loadDepartments();
      vm.loadSAMLAttributes();
      readAllUsersFunction = readAllUsers();
      $q.all([readAllTenantsFunction, readAllRolesFunction, readAllUsersFunction]).then(function (resolves) {
        // the first user must have main tenant as tenant and admin as role
        if (vm.usersCount === 0 && vm.user.isFirstUser === null) {
          vm.markAsFirstUser = true;
          for (var i in vm.tenants) {
            if (vm.tenants[i].isMain) {
              vm.user.tenant = vm.tenants[i];
            }
          }
          for (var j in vm.roles) {
            if (vm.roles[j].name === AppConstants.GROUP.ADMIN) {
              vm.user.userGroup = vm.roles[j];
            }
          }
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
    function save() {
      if (vm.usersCount === 0) {
        vm.user.isFirstUser = true;
        vm.createFirstUser();
      }
      if (vm.usersCount !== 0) {
        UsersService.createUser(vm.user)
          .success(function (data) {
            $state.go('users.info-created-pending', {
              id: data.id
            });
          })
          .error(function (response, status) {
            if (status === 400) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.userCreateCtr.userForm, response);
            }
          });
      }
    }

    // Function that reset the fields for create company
    function resetPage() {
      $state.go('users.create', {}, {
        reload: true
      });
    }

    //Function that returns all Abteilungen
    function loadDepartments() {
      UsersService.readAllDepartments()
        .success(function (data) {
          vm.departments = data;
        })
        .error(function (response, status) {
          toastr.error('error', "Abteilungen konnten nicht geladen werden.");
        });
    }

    //Function that returns all Mandants
    function loadTenants() {
      return UsersService.readAllTenants()
        .success(function (data) {
          vm.tenants = data;
        })
        .error(function (response, status) {
          toastr.error('error', "Mandanten konnten nicht geladen werden.");
        });
    }

    //Function that returns all Rollen
    function loadRoles() {
      return UsersService.readAllRoles()
        .success(function (data) {
          vm.roles = data;
        })
        .error(function (response, status) {
          toastr.error('error', "Rollen konnten nicht geladen werden.");
        });
    }

    function loadSAMLAttributes() {
      UsersService.loadSAMLAttributes()
        .success(function (data) {
          vm.user = data;
        })
        .error(function (response, status) {
          toastr.error('error', "SAML Attributes konnten nicht geladen werden.");
        });
    }

    function tenantChange(tenant) {
      // clear selected departments
      // vm.searchForm.departmentsIDs = [];
      vm.user.secondaryDepartments = null;
      if (tenant !== null) {
        StammdatenService.readDepartmentsByTenant(tenant.id)
          .success(function (data) {
            vm.departments = data;
          })
          .error(function (response, status) {

          });
      } else {
        vm.loadDepartments();
      }
      // Set the unregistered user tenant.
      AppService.setUnregisteredUserTenant(tenant);
    }

    function readAllUsers() {
      return UsersService.readAllUsers()
        .success(function (data) {
          vm.usersCount = data;
        })
        .error(function (response, status) {});
    }

    function createFirstUser() {
      UsersService.createUser(vm.user)
        .success(function (data) {
          $state.go('project.search');
        })
        .error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.userCreateCtr.userForm, response);
          }
        });
    }

    // function that triggers the tenant change depending on departments
    // selected if main department is not selected
    function departmentChange(secondaryDepartments) {
      if (secondaryDepartments.length === 1 &&
        vm.user.mainDepartment === null) {
        StammdatenService.readTenantByDepartment(
          secondaryDepartments[0].departmentId.id).success(
          function (data) {
            vm.user.tenant = data;
          }).error(function (response, status) {});
      } else if (secondaryDepartments.length === 0 &&
        vm.user.mainDepartment === null) {
        vm.user.tenant = null;
        vm.loadDepartments();

      }
    }

    /** Function to set the unregistered user group */
    function setUnregisteredUserGroup(group) {
      AppService.setUnregisteredUserGroup(group);
    }

  }
})();
