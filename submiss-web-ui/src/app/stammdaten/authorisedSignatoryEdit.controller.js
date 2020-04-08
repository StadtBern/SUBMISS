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
    .module("submiss").controller("AuthorisedSignatoryEditController",
      AuthorisedSignatoryEditController);

  /** @ngInject */
  function AuthorisedSignatoryEditController($rootScope, $scope, $state,
    StammdatenService, $stateParams, $filter, AppConstants, AppService, entryId,
    $uibModalInstance, directorate, QFormJSRValidation) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.entryId = entryId;
    vm.departments = [];
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.addNewRow = addNewRow;
    vm.removeRow = removeRow;
    vm.save = save;
    vm.setDepartmentForSignature = setDepartmentForSignature;
    vm.closeModal = closeModal;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      readAllDepartments();
      getSignatureById(entryId);
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    /** Function to get type data entry by id */
    function readAllDepartments() {
      StammdatenService.getAllActiveDepartemntsByUserTenant()
        .success(function (data) {
          vm.departments = data;
        });
    }

    function getSignatureById(entryId) {
      StammdatenService
        .getSignatureById(entryId)
        .success(function (data) {
          vm.signature = data;
          vm.signatureProcessTypeEntitled = data.signatureProcessTypeEntitled;
          if (vm.signatureProcessTypeEntitled.length === 0) {
            var newRow = {};
            vm.signatureProcessTypeEntitled.splice(0, 0, newRow);
          }
        });
    }

    function addNewRow(index) {
      var newRow = {};
      vm.signatureProcessTypeEntitled.splice(index + 1, 0, newRow);
      vm.dirtyFlag = true;
    }

    function removeRow(index) {
      vm.signatureProcessTypeEntitled.splice(index, 1);
      vm.dirtyFlag = true;
    }

    function setDepartmentForSignature(selectedDepartment, index) {
      vm.signatureProcessTypeEntitled[index].department = selectedDepartment;
    }

    function save() {
      for (var i = 0; i < vm.signatureProcessTypeEntitled.length; i++) {
        vm.signatureProcessTypeEntitled[i].sortNumber = i;
      }
      vm.signature.signatureProcessTypeEntitled = vm.signatureProcessTypeEntitled;
      StammdatenService.updateSignature(vm.signature)
        .success(function () {
          $uibModalInstance.close();
          $state.go('stammdaten.authorisedSignatory', {
            directorate: directorate
          }, {
            reload: true
          });
        }).error(function (response, status) {
          if (status === 400) {
            vm.errorFieldsVisible = true;
            QFormJSRValidation.markErrors($scope,
              $scope.editForm, response);
          }
        });
    }

    function closeModal() {
      if (vm.dirtyFlag || vm.errorFieldsVisible) {
        var cancelModal = AppService.cancelModal();
        return cancelModal.result.then(function (response) {
          if (response) {
            $uibModalInstance.close();
            $state.go('stammdaten.authorisedSignatory', {
              directorate: directorate
            }, {
              reload: true
            });
          }
        });
      } else {
        $uibModalInstance.close();
      }
    }
  }
})();
