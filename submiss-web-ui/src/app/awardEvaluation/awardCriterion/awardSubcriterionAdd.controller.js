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
 * @name awardSubcriterionAdd.controller.js
 * @description AwardSubcriterionAddController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.award")
    .controller("AwardSubcriterionAddController", AwardSubcriterionAddController);

  /** @ngInject */
  function AwardSubcriterionAddController($rootScope, $scope, $state, $stateParams,
    criterion, pageRequestedOn, submissionId, submissionVersion, $uibModal, QFormJSRValidation,
    $uibModalInstance, ExaminationService, AppService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    const vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.subcriterion = {};
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.subcriterionSave = subcriterionSave;
    vm.closeModal = closeModal;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {}
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    function subcriterionSave() {
      ExaminationService.awardLockedByAnotherUser($stateParams.id)
        .success(function (data) {
          proceedWithSaving();
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            vm.errorFieldsVisible = true;
            QFormJSRValidation.markErrors($scope,
              $scope.awardSubcriterionAddCtrl.subcriterionForm, response);
          }
        });
    }

    function proceedWithSaving() {
      vm.subcriterion.criterion = criterion;
      vm.subcriterion.pageRequestedOn = pageRequestedOn;
      ExaminationService.addSubcriterionToCriterion(submissionId, submissionVersion, vm.subcriterion).success(function (data) {
        $uibModalInstance.close();
        $state.go('award.view', {
          displayedCriterionId: criterion
        }, {
          reload: true
        });
      }).error(function (response, status) {
        if (status === 400) { // Validation errors.
          vm.errorFieldsVisible = true;
          QFormJSRValidation.markErrors($scope,
            $scope.awardSubcriterionAddCtrl.subcriterionForm, response);
        }
      });
    }

    function closeModal() {
      if (vm.dirtyFlag || vm.errorFieldsVisible) {
        var cancelModal = AppService.cancelModal();
        return cancelModal.result.then(function (response) {
          if (response) {
            $uibModalInstance.close();
            $state.go('award.view', {}, {
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
