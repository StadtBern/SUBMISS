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
 * @name customFormula.controller.js
 * @description CustomFormula controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.award")
    .controller("CustomFormulaController", CustomFormulaController);

  /** @ngInject */
  function CustomFormulaController($scope, QFormJSRValidation,
    $uibModalInstance, AwardService, title, formula, AppConstants, isPrice,
    submissionId, displayedCriterionId, $state, $uibModal) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.title = title;
    vm.formula = formula;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.update = update;
    vm.closeWindow = closeWindow;
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
    function update() {
      if (vm.formula === null || angular.isUndefined(vm.formula)) {
        // If no formula has been given, set an empty String as its value.
        vm.formula = "";
      }
      AwardService.updateCustomCalculationFormula(vm.formula, isPrice, submissionId).success(function (data) {
        $uibModalInstance.close(vm.formula);
        $state.go('award.view', {
          displayedCriterionId: displayedCriterionId
        }, {
          reload: true
        });
      }).error(function (response, status) {
        if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST) { // Validation errors.
          QFormJSRValidation.markErrors($scope,
            $scope.customFormulaCtrl.customFormulaHTMLForm, response);
        }
      });
    }

    /** Function to check for unsaved changes before closing the modal. */
    function closeWindow(dirtyflag) {
      if (dirtyflag) {
        var closeModal = $uibModal
          .open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
              '<h4 class="modal-title"></h4>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie den Vorgang wirklich abbrechen? Nicht gespeicherte Informationen gehen dabei verloren. </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="modalCtrl.closeWindowModal(true)">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="modalCtrl.closeWindowModal(false)">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'modalCtrl',
            backdrop: 'static',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.closeWindowModal = function (reason) {
                closeModal.dismiss(reason);
              };
            }
          });
        return closeModal.result.then(function () {}, function (response) {
          if (response) {
            $uibModalInstance.close();
          }
        });
      } else {
        $uibModalInstance.dismiss();
        return null;
      }
    }
  }
})();
