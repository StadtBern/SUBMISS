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
    .module("submiss").controller("AuthorisedSignatoryCopyEditController",
      AuthorisedSignatoryCopyEditController);

  /** @ngInject */
  function AuthorisedSignatoryCopyEditController($rootScope, $scope, $state,
    StammdatenService, $uibModalInstance, $stateParams, $filter,
    AppConstants, entryId, directorate) {
    const vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.entryId = entryId;
    vm.typeDataEntry = {};
    vm.signatureCopies = {};
    vm.addNewRow = addNewRow;
    vm.removeRow = removeRow;
    vm.setDepartmentForSignatureCopy = setDepartmentForSignatureCopy;
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
      getSignatureDataEntry();
      readAllDepartments();

    }

    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});

    /***********************************************************************
     * Functions.
     **********************************************************************/

    function readAllDepartments() {
      StammdatenService.getAllActiveDepartemntsByUserTenant().success(
        function (data) {
          vm.departments = data;

        });
    }

    function getSignatureDataEntry() {
      StammdatenService.getSignatureById(entryId).success(function (data) {
        vm.signature = data;
        vm.signatureCopies = data.signatureCopies;
        if (vm.signatureCopies.length === 0) {
          var newRow = {};
          vm.signatureCopies.splice(0, 0, newRow);
        }
        readAllDepartments();

      });
    }

    function addNewRow(index) {
      var newRow = {};
      vm.signatureCopies.splice(index + 1, 0, newRow);
    }

    function removeRow(index) {
      vm.signatureCopies.splice(index, 1);
    }

    function setDepartmentForSignatureCopy(selectedDepartment, index) {
      vm.signatureCopies[index].department = selectedDepartment;
    }

    function save() {
      vm.signature.signatureCopies = vm.signatureCopies;
      StammdatenService.updateSignatureCopies(vm.signature).success(
        function () {
          $uibModalInstance.close();
          $state.go('stammdaten.authorisedSignatory', {
            directorate: directorate
          }, {
            reload: true
          });
        }).error(function (response, status) {

      });

    }

  }
})();
