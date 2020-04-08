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
 * @name awardCriterionAdd.controller.js
 * @description AwardCriterionAddController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.award")
    .controller("AwardCriterionAddController", AwardCriterionAddController);

  /** @ngInject */
  function AwardCriterionAddController($rootScope, $scope, $state, $stateParams, $uibModalInstance,
    submission, submissionVersion, pageRequestedOn, $uibModal, QFormJSRValidation, ExaminationService,
    isOperatingCost, AppConstants, AppService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.criterion = {};
    vm.name = $stateParams.name;
    vm.isOperatingCost = isOperatingCost;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.criterionSave = criterionSave;
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
    function criterionSave() {
      ExaminationService.awardLockedByAnotherUser($stateParams.id)
        .success(function (data) {
          proceedWithSaving();
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            vm.errorFieldsVisible = true;
            QFormJSRValidation.markErrors($scope,
              $scope.awardCriterionAddCtrl.criterionForm, response);
          }
        });
    }

    function proceedWithSaving() {
      if (vm.isOperatingCost) {
        vm.criterion.criterionType = AppConstants.CRITERION_TYPE.OPERATING_COST_AWARD_CRITERION_TYPE;
      } else {
        vm.criterion.criterionType = AppConstants.CRITERION_TYPE.AWARD_CRITERION_TYPE;
      }
      vm.criterion.submission = submission;
      vm.criterion.pageRequestedOn = pageRequestedOn;
      vm.criterion.submissionVersion = submissionVersion;
      ExaminationService.addCriterionToSubmission(vm.criterion)
        .success(function (data) {
          $uibModalInstance.close();
          $state.go('award.view', {
            displayedCriterionId: data.id
          }, {
            reload: true
          });
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            vm.errorFieldsVisible = true;
            QFormJSRValidation.markErrors($scope,
              $scope.awardCriterionAddCtrl.criterionForm, response);
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
