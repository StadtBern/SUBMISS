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
 * @name projectCreate.controller.js
 * @description ProjectCreateController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.project")
    .controller("ProjectCreateController", ProjectCreateController);

  /** @ngInject */
  function ProjectCreateController($scope, $state, $stateParams, ProjectService,
    StammdatenService, QFormJSRValidation, $uibModal, $transitions, AppService,
    $filter) {
    const vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.project = {};
    vm.objects = [];
    vm.procedures = [];
    vm.departments = [];
    vm.company = {};
    vm.pmExternal = $stateParams.pmExternal;

    vm.options = [{
        value: true,
        label: 'Ja'
      },
      {
        value: false,
        label: 'Nein'
      }
    ];

    vm.constructionPermitOptions = [{
        value: "PENDING",
        label: 'ausstehend'
      },
      {
        value: "NON_EXISTING",
        label: 'nicht relevant'
      },
      {
        value: "EXISTING",
        label: 'vorhanden'
      }
    ];

    vm.loanApprovalOptions = [{
        value: "PENDING",
        label: 'ausstehend'
      },
      {
        value: "EXISTING",
        label: 'vorhanden'
      }
    ];
    vm.moreThanOneError = false;

    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.save = save;
    vm.loadObjects = loadObjects;
    vm.loadProcedures = loadProcedures;
    vm.loadDepartments = loadDepartments;
    vm.addCompany = addCompany;
    vm.resetPage = resetPage;
    // Activating the controller.
    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      ProjectService.loadProjectCreate()
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            vm.loadObjects();
            vm.loadProcedures();
            vm.loadDepartments();
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
    $transitions.onBefore({}, function (transition) {
      if (transition.to() !== transition.from() && vm.dirtyFlag === true) {

        var closeProjectEditModal = $uibModal.open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title"></h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie den Vorgang wirklich abbrechen? Nicht gespeicherte Informationen gehen dabei verloren </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="projectCreateCtr.closeWindowModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="projectCreateCtr.closeWindowModal(\'nein\')">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'projectCreateCtr',
          backdrop: 'static',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeWindowModal = function (reason) {
              closeProjectEditModal.dismiss(reason);
            };
          }

        });
        return closeProjectEditModal.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            vm.dirtyFlag = false;
            $state.go(transition.to(), {}, {
              reload: true
            });
          } else {
            // Do something else here
            return false;
          }
          return null;
        });

      }
      return null;
    });



    function save() {
      vm.dirtyFlag = false;
      if (vm.pmExternal !== null) {
        vm.project.pmExternal = vm.pmExternal;
      }
      ProjectService.createProject(vm.project)
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          }
          $state.go('project.view', {
            id: data.id
          });
        })
        .error(function (response, status) {
          if (status === 400) { // Validation errors.
            $scope.projectCreateCtr.saved = false ;
            QFormJSRValidation.markErrors($scope,
              $scope.projectCreateCtr.projectForm, response);
          }
        });
    }

    // Function that opens a modal to add a company for a project
    function addCompany() {
      if ((vm.project.pmExternal !== null) && (!angular.isUndefined(vm.project.pmExternal))) {
        // Externe Projektleitung already exists, display error message
        vm.moreThanOneError = true;
      } else {
        var addCompany = AppService.addCompany(true, null);
        return addCompany.result.then(function (response) {
          vm.companyList = [];
          vm.companyList = response;
          if (vm.companyList !== null) {
            for (var i = 0; i < vm.companyList.length; i++) {
              vm.project.pmExternal = {};
              vm.project.pmExternal = vm.companyList[i];
            }
          }
        });
      }
      return null;
    }

    // Function that reset the fields for create project
    function resetPage(dirtyflag) {
      if (!dirtyflag) {
        $state.go('project.create', {}, {
          reload: true
        });
      } else {
        var cancelModal = AppService.cancelModal();
        return cancelModal.result.then(function (response) {
          if (response) {
            $state.reload();
            dirtyflag = false;
            vm.dirtyFlag = false;
            return true;
          } else {
            return false;
          }
        });
      }
      return null;
    }

    //Function that returns all Objects
    function loadObjects() {
      StammdatenService.readAllObjects()
        .success(function (data) {
          vm.objects = AppService.showOnlyActive(data);
        })
        .error(function (response, status) {});
    }

    //Function that returns all Procedures
    function loadProcedures() {
      StammdatenService.readAllProcedures()
        .success(function (data) {
          vm.procedures = $filter('filter')(data, {
            'active': true
          });
        })
        .error(function (response, status) {});
    }

    //Function that returns all Abteilungen
    function loadDepartments() {
      StammdatenService.readDepartmentsByUserAndRole()
        .success(function (data) {
          vm.departments = AppService.showOnlyActive(data);
        })
        .error(function (response, status) {});
    }

  }
})();
