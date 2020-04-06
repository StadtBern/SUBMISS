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
 * @description CompanyProofRequestController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module('submiss.document')
    .controller('CompanyProofRequestController', CompanyProofRequestController);

  /** @ngInject */
  function CompanyProofRequestController($scope, QFormJSRValidation, AppService,
    $uibModalInstance, StammdatenService, DocumentService, $locale, $stateParams, $state, template, createAndPrintDocument) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var i;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.template = template;
    vm.template.id = $stateParams.id;
    template.submitDate = new Date().setDate(new Date().getDate() + 7);
    vm.createAndPrintDocument = createAndPrintDocument;
    vm.errorFieldsVisible = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.requestProofs = requestProofs;
    vm.openSubmitDate = openSubmitDate;
    vm.printDocuments = printDocuments;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {}
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on('$destroy', function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    function openSubmitDate() {
      vm.openSubmitDate.opened = !vm.openSubmitDate.opened;
    }

    function requestProofs() {
      AppService.setPaneShown(true);
      DocumentService.createDocument(vm.template)
        .success(function (data) {
          $uibModalInstance.close();
          $state.reload();
          AppService.setPaneShown(false);
          if (createAndPrintDocument) {
            printDocuments(data);
          }
        }).error(function (response, status) {
          AppService.setPaneShown(false);
          if (status === 400) {
            vm.errorFieldsVisible = true;
            response = handleResponse(response);
            QFormJSRValidation.markErrors($scope,
              $scope.companyProofRequestCtrl.requestForm, response);
          }
        });
    }
    /** Function to handle the response content */
    function handleResponse(response) {
      for (i in response) {
        if (response[i].message === 'mandatory_error_message' && $scope.companyProofRequestCtrl.requestForm.submitDate.$viewValue) {
          response[i].message = 'invalid_date_error_message';
        }
      }
      return response;
    }

    function printDocuments(documentIds) {
      for (var i = 0; i < documentIds.length; i++) {
        DocumentService.printDocument(documentIds[i])
          .success(function (response, status, headers) {
            if (headers()['content-disposition']) {
              var fileName = headers()['content-disposition']
                .split(';')[1].trim().split('=')[1];
              // Remove quotes added from the DocumentResource in order to enable filenames with special characters like comma
              fileName = fileName.substring(1, fileName.length - 1);
              var blob = new Blob([response], {
                type: 'application/pdf'
              });
              var objectURL = URL.createObjectURL(blob);
              var iFrameJQueryObject = $('<iframe id="iframe" src="' + objectURL +
                '" style="display:none"></iframe>');
              $('body').append(iFrameJQueryObject);
              iFrameJQueryObject.on('load', function () {
                $(this).get(0).contentWindow.print();
              });
            }
          }).error(function (response, status) {});
      }
    }
  }
})();
