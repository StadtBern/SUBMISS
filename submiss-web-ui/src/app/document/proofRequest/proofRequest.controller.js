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
 * @name proofRequestCtrl.controller.js
 * @description ProofRequestController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document")
    .controller("ProofRequestController", ProofRequestController);

  /** @ngInject */
  function ProofRequestController($scope, QFormJSRValidation,
    $uibModalInstance, DocumentService, SubmissionService, submittents, AppService,
    $locale, $stateParams, $state, AppConstants, ExaminationService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var i;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.template = {};
    vm.template.id = $stateParams.id;
    vm.template.submitDate = new Date().setDate(new Date().getDate() + 7);
    vm.errorFieldsVisible = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.requestProofs = requestProofs;
    vm.openSubmitDate = openSubmitDate;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      readSubmission($stateParams.id);
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    function openSubmitDate() {
      vm.openSubmitDate.opened = !vm.openSubmitDate.opened;
    }

    function readSubmission(id) {
      SubmissionService.readSubmission(id).success(function (data) {
        vm.submission = data;
      }).error(function (response, status) {});
    }

    function requestProofs() {
      vm.template.generateProofTemplate = true;
      vm.template.isCompanyCertificate = false;
      vm.template.projectDocument = true;
      vm.template.title = AppConstants.PROOF_REQUEST_FT;
      if (vm.submission.process !== AppConstants.PROCESS.NEGOTIATED_PROCEDURE && vm.submission.process !== AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) {
        AppService.setPaneShown(true);
        DocumentService.createDocument(vm.template)
          .success(function (data) {
            // we need to update also the status of the submission if not already updated
            ExaminationService.updateSubmissionFormalAuditExaminationStatus($stateParams.id).success(function (data) {
              $uibModalInstance.close(true);
              $state.reload();
              AppService.setPaneShown(false);
            });
          }).error(function (response, status) {
            AppService.setPaneShown(false);
            vm.errorFieldsVisible = true;
            response = handleResponse(response);
            QFormJSRValidation
              .markErrors(
                $scope,
                $scope.proofRequestCtrl.requestForm,
                response);
          });
      } else {
        // Select submittents to generate Nachweisbrief documents when Process is NEGOTIATED_PROCEDURE or NEGOTIATED_PROCEDURE_WITH_COMPETITION.
        var proofSubmittents = [];
        for (var i = 0; i <= submittents.length - 1; i++) {
          if (!angular.isUndefined(submittents[i].checked) && submittents[i].checked) {
            submittents[i].proofDocPending = true;
            proofSubmittents.push(submittents[i]);
          } else {
            submittents[i].proofDocPending = false;
            proofSubmittents.push(submittents[i]);
          }
        }
        // Generate Nachweisbrief only for the selected submittents.
        ExaminationService.generateProofDocNegotiatedProcedure(proofSubmittents)
          .success(function (data) {
            AppService.setPaneShown(true);
            DocumentService.createDocument(vm.template)
              .success(function (data) {
                $uibModalInstance.close(true);
                $state.reload();
                AppService.setPaneShown(false);
              }).error(function (response, status) {
                AppService.setPaneShown(false);
                vm.errorFieldsVisible = true;
                response = handleResponse(response);
                QFormJSRValidation
                  .markErrors(
                    $scope,
                    $scope.proofRequestCtrl.requestForm,
                    response);
              });
          }).error(function (response, status) {
            vm.errorFieldsVisible = true;
            QFormJSRValidation
              .markErrors(
                $scope,
                $scope.proofRequestCtrl.requestForm,
                response);
          });
      }
    }

    /** Function to handle the response content */
    function handleResponse(response) {
      for (i in response) {
        if (response[i].message === "mandatory_error_message" && $scope.proofRequestCtrl.requestForm.submitDate.$viewValue) {
          response[i].message = "invalid_date_error_message";
        }
      }
      return response;
    }
  }
})();
