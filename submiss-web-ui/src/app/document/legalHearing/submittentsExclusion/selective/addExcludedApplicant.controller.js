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
 * @name addExcludedSubmittent.controller.js
 * @description AddExcludedSubmittentController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document").controller("AddExcludedApplicantController",
      AddExcludedApplicantController);

  /** @ngInject */
  function AddExcludedApplicantController($scope, $rootScope, $uibModalInstance,
    $state, $stateParams, AppConstants, AppService, SelectiveService, LegalHearingService, $filter) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.applicants = {};
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.addApplicant = addApplicant;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      readApplicantsBySubmission($stateParams.submissionId);
    }

    function readApplicantsBySubmission(id) {
      SelectiveService.getApplicantsForFormalAudit(id).success(function (data) {
        vm.applicants = data;
      });
    }

    function addApplicant() {
      AppService.setPaneShown(true);
      LegalHearingService.addExcludedApplicant(vm.applicant.id).success(function (data) {
        AppService.setPaneShown(false);
        $uibModalInstance.close();
        $state.reload();
      });
    }
  }
})();
