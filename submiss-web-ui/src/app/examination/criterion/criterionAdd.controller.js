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
 * @name criterionAdd.controller.js
 * @description CriterionAddController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.examination").controller("CriterionAddController",
      CriterionAddController);

  /** @ngInject */
  function CriterionAddController($rootScope, $scope, $state, $stateParams, $uibModalInstance,
    submission, isEvaluated, $uibModal, QFormJSRValidation, ExaminationService, AppConstants) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.criterion = {};
    vm.isEvaluated = isEvaluated;
    vm.name = $stateParams.name;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.criterionSave = criterionSave;
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
      if (isEvaluated) {
        vm.criterion.criterionType = AppConstants.CRITERION_TYPE.EVALUATED_CRITERION_TYPE;
      } else {
        vm.criterion.criterionType = AppConstants.CRITERION_TYPE.MUST_CRITERION_TYPE;
      }
      vm.criterion.submission = submission;
      ExaminationService.addCriterionToSubmission(vm.criterion)
        .success(function (data) {
          $uibModalInstance.close();
          if (isEvaluated) {
            $state.go('examination.view', {
              displayedEvaluatedCriterionId: data.id
            }, {
              reload: true
            });
          } else {
            $state.go('examination.view', {
              displayedMustCriterionId: data.id
            }, {
              reload: true
            });
          }
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.criterionAddCtrl.criterionForm, response);
          }
        });
    }
  }
})();
