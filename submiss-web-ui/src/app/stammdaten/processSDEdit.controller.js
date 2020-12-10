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
 * @name processSDEdit.controller.js
 * @description ProcessSDEditController controller
 */
(function () {
  "use strict";
  angular // eslint-disable-line no-undef
    .module("submiss").controller("ProcessSDEditController",
      ProcessSDEditController);

  /** @ngInject */
  function ProcessSDEditController($rootScope, $scope, $state,
    StammdatenService, QFormJSRValidation, $stateParams, $filter,
    AppConstants, entryId, $uibModalInstance, AppService) {
    var vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.entryId = entryId;
    vm.typeDataEntry = {};
    vm.newEntry = {};
    vm.editValue = {};
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.save = save;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      getProcessDataEntry();
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    /** Function to get type data entry by id */
    function getProcessDataEntry() {
      if (vm.entryId) {
        StammdatenService.getProccessDataEntryById(vm.entryId)
          .success(function (data) {
            vm.typeDataEntry = data;
            vm.editValue = vm.typeDataEntry.value;
          });
      }
    }

    function save() {
      vm.newEntry.id = vm.typeDataEntry.id;
      vm.newEntry.version = vm.typeDataEntry.version;
      vm.newEntry.procedure = vm.typeDataEntry.procedure;
      if (angular.isNumber(vm.editValue)) {
        vm.newEntry.value = AppService.formatAmount(vm.editValue);
      } else {
        vm.newEntry.value = vm.editValue;
      }
      vm.newEntry.tenant = vm.typeDataEntry.tenant;
      vm.newEntry.processType = vm.typeDataEntry.processType;
      vm.newEntry.process = vm.typeDataEntry.process;

      StammdatenService.updateProcedureValue(vm.newEntry)
        .success(function () {
          $uibModalInstance.close();
          $state.reload();
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.processSDEditCtrl.prEditForm,
              response);
          }
        });
    }
  }
})();
