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
 * @name projectUpdate.controller.js
 * @description ProjectCreateController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.project")
    .controller("ProjectUpdateController", ProjectUpdateController);

  /** @ngInject */
  function ProjectUpdateController($scope, $location, $anchorScroll, $state,
    $stateParams, $q, ProjectService,
    StammdatenService, QFormJSRValidation, editProject, project,
    $uibModalInstance, $uibModal, $transitions,
    AppService, AppConstants, $filter) {
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
    vm.secProjectEdit = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.save = save;
    vm.closeWindow = closeWindow;
    vm.loadObjects = loadObjects;
    vm.loadProcedures = loadProcedures;
    vm.loadDepartments = loadDepartments;
    vm.project = project;
    vm.editProject = editProject;
    vm.addCompany = addCompany;
    vm.hasStatusAfterOfferOpeningClosed = hasStatusAfterOfferOpeningClosed;
    vm.errorFieldFocus = errorFieldFocus;
    // Activating the controller.
    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      vm.loadObjects();
      vm.loadProcedures();
      vm.loadDepartments();
      vm.hasStatusAfterOfferOpeningClosed();
      findUserOperations();
    }

    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});

    /***********************************************************************
     * Functions.
     **********************************************************************/

    /** Prevent backspace from navigating back using jQuery. */
    $(document).on("keydown", function (e) {
      if (e.which === 8 && !$(e.target).is("input, textarea")) {
        e.preventDefault();
      }
    });

    function save() {
      ProjectService.updateProject(vm.project)
        .success(function (data) {
          $uibModalInstance.close();
          $state.go('project.view', {
            id: vm.project.id
          }, {
            reload: 'project.view'
          });
        })
        .error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.projectUpdateCtr.projectForm, response);
          }
        });
      vm.projectForm = $scope.projectUpdateCtr.projectForm;
      vm.errorFieldFocus(vm.projectForm);
    }

    // Function that opens a modal to add a company for a project
    function addCompany() {
      if (vm.project.pmExternal !== null) {
        // Externe Projektleitung already exists, display error message
        vm.moreThanOneError = true;
      } else {
        var addCompany = AppService.addCompany(true, null);
        return addCompany.result.then(function (response) {
          vm.companyList = [];
          vm.companyList = response;
          if (vm.companyList !== null) {
            for (var i = 0; i < vm.companyList.length; i++) {
              vm.project.pmExternal = vm.companyList[i];
            }
          }
        });
      }
    }

    function closeWindow(dirtyflag) {
      if (dirtyflag) {
        var closeProjectEditModal = $uibModal.open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">Projekt bearbeiten</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie den Vorgang wirklich abbrechen? Nicht gespeicherte Informationen gehen dabei verloren </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="projectUpdateCtr.closeWindowModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'projectUpdateCtr',
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
            $uibModalInstance.close();
            $state.go($state.current, {}, {
              reload: true
            });
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

    //Function that returns all Departments
    function loadDepartments() {
      StammdatenService.readDepartmentsByUserAndRole()
        .success(function (data) {
          vm.departments = AppService.showOnlyActive(data);
        })
        .error(function (response, status) {});
    }

    function hasStatusAfterOfferOpeningClosed() {
      ProjectService.hasStatusAfterOfferOpeningClosed(vm.project.id).success(
        function (data) {
          vm.hasStatusAfterOfferOpeningClosed = data;
        }).error(function (response, status) {

      });
    }

    function findUserOperations() {
      vm.secProjectEdit = AppService.isOperationPermitted(
        AppConstants.OPERATION.PROJECT_EDIT, null);
    }

    /** Function that navigates to the top of the modal dialog if there is an error*/
    function errorFieldFocus(projectForm) {
      if (projectForm.$dirty) {
        $anchorScroll('errorScroll');
      }
    }
  }
})();
