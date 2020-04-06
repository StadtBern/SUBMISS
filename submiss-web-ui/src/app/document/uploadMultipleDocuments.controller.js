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
 * @name uploadMultipleDocumentsController.controller.js
 * @description UploadMultipleDocumentsController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.submission").controller("UploadMultipleDocumentsController",
      UploadMultipleDocumentsController);

  /** @ngInject */
  function UploadMultipleDocumentsController($scope, QFormJSRValidation,
    $uibModalInstance, DocumentService, $locale, $stateParams, $state,
    documents, flow) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.documents = documents;
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    $scope.ok = function () {
      flow.upload();
      $uibModalInstance.close(documents);
    };

    $scope.cancel = function () {
      flow.cancel();
      $uibModalInstance.dismiss('cancel');
    };
  }
})();
