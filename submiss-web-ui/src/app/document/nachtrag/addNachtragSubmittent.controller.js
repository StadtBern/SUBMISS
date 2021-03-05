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
 * @name addNachtragSubmittent.controller.js
 * @description AddNachtragSubmittentController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document")
    .controller("AddNachtragSubmittentController", AddNachtragSubmittentController);

  /** @ngInject */
  function AddNachtragSubmittentController($uibModalInstance,
    $state, $stateParams, AppService, NachtragService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.awardedOffer = {};
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.addNachtragSubmittent = addNachtragSubmittent;
    vm.closeModal = closeModal;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      getAwardedOffers($stateParams.submissionId);
    }

    function getAwardedOffers(submissionId) {
      NachtragService.getAwardedOffers(submissionId)
        .success(function (data) {
          vm.awardedOffers = data;
        });
    }

    function addNachtragSubmittent() {
      AppService.setPaneShown(true);
      NachtragService.addNachtragSubmittent(vm.awardedOffer.id)
        .success(function (data) {
          AppService.setPaneShown(false);
          $uibModalInstance.close();
          $state.reload();
        });
    }

    function closeModal() {
      if (vm.dirtyFlag || vm.errorFieldsVisible) {
        var cancelModal = AppService.cancelModal();
        return cancelModal.result.then(function (response) {
          if (response) {
            $uibModalInstance.close();
            $state.go('documentView.nachtrag', {}, {
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
