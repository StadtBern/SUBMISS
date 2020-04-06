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
 * @name authorisedSignatory.controller.js
 * @description AuthorisedSignatoryController controller
 */
(function () {
  "use strict";
  angular // eslint-disable-line no-undef
    .module("submiss").controller("AuthorisedSignatoryController",
      AuthorisedSignatoryController);

  /** @ngInject */
  function AuthorisedSignatoryController($rootScope, $scope, $state,
    StammdatenService, $stateParams, $filter, $uibModal, AppService) {
    var vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.typeName = $stateParams.typeName;

    vm.chosenDirectorate = null;
    vm.directorates = null;
    vm.getSignaturesByDirectorate = getSignaturesByDirectorate;
    vm.showSignatureValue = showSignatureValue;
    vm.showSignatureCopiesValue = showSignatureCopiesValue;
    vm.editSignatureValue = editSignatureValue;
    vm.editSignatureCopiesValue = editSignatureCopiesValue;

    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    // Activating the controller.
    activate();

    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      getAllDirectorates();
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

    function getAllDirectorates() {
      StammdatenService
        .getAllDirectoratesByUserTenant()
        .success(
          function (data) {
            vm.directorates = data;
            if ($stateParams.directorate === null) {
              vm.chosenDirectorate = vm.directorates[0];
            } else {
              vm.chosenDirectorate = $stateParams.directorate;
            }
            getSignaturesByDirectorate(vm.chosenDirectorate.directorateId.id);
          }).error(function (response, status) {});
    }

    function getSignaturesByDirectorate(directorateId) {
      StammdatenService.getSignaturesByDirectorateId(directorateId)
        .success(function (data) {
          vm.signatures = data;
        }).error(function (response, status) {});
    }

    function showSignatureValue(processName, departmentName) {
      var value = null;
      var count = 0;
      for (var i = 0; i < vm.signatures.length; i++) {
        if (vm.signatures[i].signature.departmentHistory.name === departmentName &&
          vm.signatures[i].process === processName) {
          count++;
          value = vm.signatures[i].signatureProcessTypeEntitled;
        }
      }
      return value;
    }

    function showSignatureCopiesValue(processName, departmentName) {
      var value = null;
      var count = 0;
      for (var i = 0; i < vm.signatures.length; i++) {

        count = 0;
        if (vm.signatures[i].signature.departmentHistory.name === departmentName &&
          vm.signatures[i].process === processName) {
          count++;
          value = vm.signatures[i].signatureCopies;
        }
      }
      return value;
    }

    function editSignatureValue(processName, departmentName) {
      var id = null;
      var count = 0;
      for (var i = 0; i < vm.signatures.length; i++) {
        if (vm.signatures[i].signature.departmentHistory.name === departmentName &&
          vm.signatures[i].process === processName) {
          count++;
          id = vm.signatures[i].id;
        }
      }

      $uibModal.open({
        templateUrl: 'app/stammdaten/authorisedSignatoryEdit.html',
        controller: 'AuthorisedSignatoryEditController',
        controllerAs: 'AuthorisedSignatoryEditCtrl',
        backdrop: 'static',
        size: 'lg',
        keyboard: false,
        resolve: {
          entryId: function () {
            return id;
          },
          directorate: function () {
            return vm.chosenDirectorate;
          }
        }
      });
    }

    function editSignatureCopiesValue(processName, departmentName) {
      var value = null;
      var id = null;
      for (var i = 0; i < vm.signatures.length; i++) {

        if (vm.signatures[i].signature.departmentHistory.name === departmentName &&
          vm.signatures[i].process === processName) {
          value = vm.signatures[i].signatureCopies;
          id = vm.signatures[i].id;
        }
      }

      $uibModal
        .open({
          templateUrl: 'app/stammdaten/authorisedSignatoryCopyEdit.html',
          controller: 'AuthorisedSignatoryCopyEditController',
          controllerAs: 'AuthorisedSignatoryCopyEditCtrl',
          backdrop: 'static',

          keyboard: false,
          resolve: {
            entryId: function () {
              return id;
            },
            directorate: function () {
              return vm.chosenDirectorate;
            }
          }
        });
    }

    /** Function to set the type name */
    function setTypeName() {
      if (!$stateParams.typeName) {
        // If the parameter typeName contains no value, get it from the type.
        AppService.getNameOfMasterListType($stateParams.type).success(
          function (data) {
            vm.typeName = data[0];
          });
      } else {
        vm.typeName = $stateParams.typeName;
      }
    }
  }
})();
