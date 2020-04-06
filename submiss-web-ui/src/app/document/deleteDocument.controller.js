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
 * @name deleteDocument.controller.js
 * @description DeleteDocumentController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document")
    .controller("DeleteDocumentController", DeleteDocumentController);

  /** @ngInject */
  function DeleteDocumentController($scope, QFormJSRValidation,
    $uibModalInstance, DocumentService, $locale, $stateParams, $state,
    versionId, documentTitle, templateId) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.documentTitle = documentTitle;
    vm.reason;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.deleteDocument = deleteDocument;
    vm.reasonIsMandatory = false;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      isDeletionReasonMandatory();
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    // generate and download company certificate
    function deleteDocument() {
      DocumentService.deleteSelectedDocument(templateId, versionId, vm.reason)
        .success(function (data) {
          $uibModalInstance.close();
          $state.reload();
        }).error(function (response, status) {
          QFormJSRValidation.markErrors($scope,
            $scope.deleteDocumentController.requestForm, response);
        });
    }

    /** Check if deletion reason is mandatory */
    function isDeletionReasonMandatory() {
      DocumentService.isDeletionReasonMandatory(templateId)
        .success(function (data) {
          vm.reasonIsMandatory = data;
        });
    }
  }
})();
