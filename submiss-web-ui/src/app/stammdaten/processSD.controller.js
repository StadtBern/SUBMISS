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
 * @name processSD.controller.js
 * @description ProcessSDController controller
 */
(function () {
  "use strict";
  angular // eslint-disable-line no-undef
    .module("submiss").controller("ProcessSDController", ProcessSDController);

  /** @ngInject */
  function ProcessSDController($rootScope, $scope, $state, ProcedureService,
    $stateParams, $filter, $uibModal, AppService) {
    const vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.processes = null;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.showProccessType = showProccessType;
    vm.showValue = showValue;
    vm.editProcessData = editProcessData;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      readProcesses();
      setTypeName();
    }

    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});

    /***********************************************************************
     * Functions.
     **********************************************************************/
    $rootScope.$on('$stateChangeSuccess', function (event, to, toParams,
      from, fromParams) {
      // save the previous state in a rootScope variable so that it's
      // accessible from everywhere
      $rootScope.previousState = from;
    });

    function readProcesses() {
      ProcedureService.readAllProcedures().success(function (data) {
        vm.processes = data;

      }).error(function (response, status) {});
    }

    function showProccessType(procedure) {
      return procedure.processTypeHistory.value1;

    }

    function showValue(procedure, procedure2) {
      var value = null;
      for (var i = 0; i < vm.processes.length; i++) {
        if (vm.processes[i].processTypeHistory.value1 === procedure.processTypeHistory.value1 &&
          vm.processes[i].process === procedure2.process) {
          value = vm.processes[i].value;
        }
      }
      return AppService.formatAmount(value);
    }

    function editProcessData(procedure, procedure2) {

      var id = null;
      for (var i = 0; i < vm.processes.length; i++) {
        if (vm.processes[i].processTypeHistory.value1 === procedure.processTypeHistory.value1 &&
          vm.processes[i].process === procedure2.process) {
          id = vm.processes[i].id;
        }
      }

      $uibModal.open({
        templateUrl: 'app/stammdaten/processSDEdit.html',
        controller: 'ProcessSDEditController',
        controllerAs: 'processSDEditCtrl',
        backdrop: 'static',
        keyboard: false,
        resolve: {
          entryId: function () {
            return id;
          }

        }
      });
    }

    /** Function to set the type name */
    function setTypeName() {
      if (!$stateParams.typeName) {
        // If the parameter typeName contains no value, get it from the type.
        AppService.getNameOfMasterListType($stateParams.type).success(function (data) {
          vm.typeName = data[0];
        });
      } else {
        vm.typeName = $stateParams.typeName;
      }
    }

  }
})();
