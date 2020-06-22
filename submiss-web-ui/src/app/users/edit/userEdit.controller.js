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
 * @name userEdit.controller.js
 * @description UserEditController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.users")
    .controller("UserEditController", UserEditController);

  /** @ngInject */
  function UserEditController($scope, $location, $anchorScroll, $state,
    $uibModalInstance, AppConstants,
    QFormJSRValidation, UsersService, StammdatenService, $uibModal, user,
    AppService, fromTaskPage) {

    const vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.user = user;
    /* store the values of group, userAdminRight and secondary departments, so if they are changed the back end must be informed,
     * in order to update the security resources
     */
    var group = vm.user.userGroup;
    var userAdminRight = vm.user.userAdminRight;
    var secondaryDepartments = vm.user.secondaryDepartments;
    vm.user.active = (vm.user.status ===
      AppConstants.USER_STATUS.ENABLED_APPROVED);
    var tempActive = vm.user.active;
    vm.departments = [];
    vm.tenants = [];
    vm.roles = [];
    vm.loggedInUser = {};
    vm.userStatus = AppConstants.USER_STATUS;
    vm.fromTaskPage = fromTaskPage;

    /***********************************************************************
     * Exported functions.
     **********************************************************************/

    vm.save = save;
    vm.resetPage = resetPage;
    vm.loadDepartments = loadDepartments;
    vm.loadTenants = loadTenants;
    vm.loadRoles = loadRoles;
    vm.closeWindow = closeWindow;
    vm.declineUserModal = declineUserModal;
    vm.userActivationModal = userActivationModal;
    vm.getUser = getUser;
    vm.errorFieldFocus = errorFieldFocus;
    // Activating the controller.
    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      UsersService.loadUserSearch()
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            $uibModalInstance.close();
            return;
          } else {
            vm.loadDepartments();
            vm.loadTenants();
            vm.loadRoles();
            vm.getUser();
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
    function getUser() {
      vm.loggedInUser = AppService.getLoggedUser();
    }

    function save() {
      /* check if group, userAdminRight and secondary departments are changed and update the according flags
       */
      vm.user.groupChanged = (group !== vm.user.userGroup);
      // hold the initial groupId to check for optimistic lock errors
      vm.user.oldGroupId = group.id;
      vm.user.userAdminRightChanged = (userAdminRight !==
        vm.user.userAdminRight);
      vm.user.secDeptsChanged = (secondaryDepartments !==
        vm.user.secondaryDepartments);
      // The condition (vm.user.active !== tempActive) can be
      // true in case that the user is not allready registered
      // and a validation error occurs.As a rusult,the
      // The popup "Möchten Sie diesen Benutzer wirklich
      // aktivieren/deaktivieren?"appears and this is not true, because the
      // 'Activ' checkbox is not even visible in this case.
      // So we add a second condition to prevent that situation and
      // trigger the popup only if the user is in Approved status and a change
      // occured in the checkbox 'Αctiv'.
      if ((vm.user.active !== tempActive) &&
        (vm.user.status === vm.userStatus.ENABLED_APPROVED || vm.user.status ===
          vm.userStatus.DISABLED_APPROVED)) {
        vm.userActivationModal(vm.user);
      } else {
        if (vm.user.userGroup !== null) {
          if (vm.user.userGroup.name !== 'Admin') {
            vm.user.userAdminRight = false;
          }
        }
        // in case the user is not already registered mark him to be registered and to be set to active
        if (vm.user.status == null || (vm.user.status !==
            vm.userStatus.ENABLED_APPROVED && vm.user.status !==
            vm.userStatus.DISABLED_APPROVED)) {
          vm.user.register = true;
          vm.user.active = true;
        } else {
          vm.user.register = false;
        }
        UsersService.editUser(vm.user)
          .success(function (data, status) {
            if (status === 403) { // Security checks.
              $uibModalInstance.close();
              return;
            }
            if (vm.user.register) {
              window.location.href = data.id;
              $state.go($state.current, {}, {
                reload: true
              }); // reload the list
            }
            $uibModalInstance.close();
            if (fromTaskPage) {
              $state.go('tasksView', {}, {
                reload: true
              });
            } else {
              $state.go('users.search', {}, {
                reload: true
              });
            }
          })
          .error(function (response, status) {
            if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.userEditCtr.userForm, response);
            }
          });
        vm.userForm = $scope.userEditCtr.userForm;
        vm.errorFieldFocus(vm.userForm);
      }
    }

    // Function that reset the fields for create company
    function resetPage() {
      $state.go('users.edit', {}, {
        reload: true
      });
    }

    function userActivationModal() {
      var openUserActivationModal = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">Benutzer bearbeiten</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie diesen Benutzer wirklich aktivieren/deaktivieren? </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="userEditCtr.activationModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'userEditCtr',
          backdrop: 'static',
          size: 'lg',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.activationModal = function (reason) {
              openUserActivationModal.dismiss(reason);
            };
          }

        });
      return openUserActivationModal.result.then(function () {
        // Modal Success Handler
      }, function (response) { // Modal Dismiss Handler
        if (response === 'ja') {
          if (vm.user.userGroup.name !== 'Admin') {
            vm.user.userAdminRight = false;
          }
          UsersService.editUser(vm.user)
            .success(function (data) {
              $uibModalInstance.close();
              $state.go('users.search', {}, {
                reload: true
              });
            })
            .error(function (response, status) {
              if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
                QFormJSRValidation.markErrors($scope,
                  $scope.userEditCtr.userForm, response);
              }
            });
        } else {
          // Do something else here
          return false;
        }
        return null;
      });
    }

    function declineUserModal() {
      var openDeclineUserModal = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">Benutzer bearbeiten</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie diesen Benutzer wirklich ablehnen? Mit Betätigen der Schaltfläche "Ja" wird der Benutzer aus der Fachanwendung gelöscht. </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="userEditCtr.declineModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'userEditCtr',
          backdrop: 'static',
          size: 'lg',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.declineModal = function (reason) {
              openDeclineUserModal.dismiss(reason);
            };
          }

        });
      return openDeclineUserModal.result.then(function () {
        // Modal Success Handler
      }, function (response) { // Modal Dismiss Handler
        if (response === 'ja') {
          UsersService.declineUser(vm.user.id)
            .success(function (data) {
              window.location.href = data;
              $state.go($state.current, {}, {
                reload: true
              }); // reload the list
              if (fromTaskPage) {
                $state.go('tasksView', {}, {
                  reload: true
                });
              } else {
                $state.go('users.search', {}, {
                  reload: true
                });
              }
            })
            .error(function (response, status) {
              if (status === 400) { // Validation errors.
                QFormJSRValidation.markErrors($scope,
                  $scope.userEditCtr.userForm, response);
              }
            });
          $uibModalInstance.close();
          if (fromTaskPage) {
            $state.go('tasksView', {}, {
              reload: true
            });
          } else {
            $state.go('users.search', {}, {
              reload: true
            });
          }
        } else {
          // Do something else here
          return false;
        }
        return null;
      });
    }

    function closeWindow(dirtyflag) {
      if (dirtyflag) {
        var closeUserEditModal = $uibModal
          .open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
              '<h4 class="modal-title">Benutzer bearbeiten</h4>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie den Vorgang wirklich abbrechen? Nicht gespeicherte Informationen gehen dabei verloren </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="userEditCtr.closeWindowModal(\'ja\')">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'userEditCtr',
            backdrop: 'static',
            size: 'lg',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.closeWindowModal = function (reason) {
                closeUserEditModal.dismiss(reason);
              };
            }

          });

        return closeUserEditModal.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            $uibModalInstance.close();
            if (fromTaskPage) {
              $state.go('tasksView', {}, {
                reload: true
              });
            } else {
              $state.go('users.search', {}, {
                reload: true
              });
            }
          } else {
            // Do something else here
            return false;
          }
          return null;
        });
      } else {
        $uibModalInstance.dismiss();
      }
      return null;
    }

    // Function that returns all Abteilungen
    function loadDepartments() {
      StammdatenService.readDepartmentsByTenant(vm.user.tenant.id)
        .success(function (data) {
          vm.departments = data;
        })
        .error(function (response, status) {
          NotificationsService.show('error',
            "Abteilungen konnten nicht geladen werden.");
        });
    }

    //Function that returns all Mandants
    function loadTenants() {
      UsersService.getUserTenants()
        .success(function (data) {
          vm.tenants = data;
        })
        .error(function (response, status) {
          NotificationsService.show('error',
            "Mandanten konnten nicht geladen werden.");
        });
    }

    //Function that returns all Rollen
    function loadRoles() {
      UsersService.getPermittedRoles()
        .success(function (data) {
          vm.roles = data;
        })
        .error(function (response, status) {
          NotificationsService.show('error',
            "Rollen konnten nicht geladen werden.");
        });
    }

    /** Function that navigates to the top of the modal dialog if there is an error*/
    function errorFieldFocus(userForm) {
      if (userForm.$dirty) {
        $anchorScroll('errorScroll');
      }
    }
  }
})();
