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
 * @name companyProofRequestCtrl.controller.js
 * @description SelectSignatureController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.submissionDefault")
    .controller("SelectEmailTemplateController", SelectEmailTemplateController);

  /** @ngInject */
  function SelectEmailTemplateController($scope, QFormJSRValidation,
    $uibModalInstance, SubmissionService, $stateParams, $state, id, $uibModal, companyIds, CompanyService) {
    const vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.templates = [];
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.openEmailClient = openEmailClient;
    vm.template = {};
    vm.errorMessage = false;

    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      loadEmailTemplates();
    }

    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});

    /***********************************************************************
     * Functions.
     **********************************************************************/

    function openEmailClient() {
      if (companyIds == null || companyIds.length === 0) {
        SubmissionService.sendMail(id, vm.template).success(function (data) {
          if (data !== "mandatory_error_message") {
            $uibModalInstance.close();
            window.location.href = data;
          } else {
            vm.errorMessage = true;
          }
        })
      } else {
        var ids = '';
        for (var i = 0; i < companyIds.length; i++) {
          ids = ids + 'id=' + companyIds[i];
          if (companyIds.length !== i + 1) {
            ids = ids + '&';
          }
        }
        CompanyService.openCompanyEmail(ids, vm.template).success(function (data) {
          if (data !== "mandatory_error_message") {
            $uibModalInstance.close();
            window.location.href = data;
          } else {
            vm.errorMessage = true;
          }
        })
      }
    }

    //Function that returns all Abteilungen
    function loadEmailTemplates() {
      SubmissionService.loadEmailTemplates(id)
        .success(function (data) {
          vm.templates = data;
          if (vm.templates[0].availablePart === "PROJECT_PART") {
            vm.template = vm.templates[vm.templates.length - 1];
          } else {
            vm.template = vm.templates[0];
          }
        })
    }
  }
})();
