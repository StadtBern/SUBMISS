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
    .module("submiss.document")
    .controller("SelectSignOrDepController", SelectSignOrDepController);

  /** @ngInject */
  function SelectSignOrDepController($scope, QFormJSRValidation, AppService,
    $uibModalInstance, DocumentService, $stateParams, signatures, template, $state, $uibModal, createAndPrintDocument, userDepartments, isBeko) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var DOCUMENTLIST = 'documentView.documentList';
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.template = template;
    vm.template.id = $stateParams.id;
    vm.signatures = [];
    vm.userDepartments = [];
    vm.isBeko = isBeko;
    vm.userDepartments = userDepartments;
    vm.signatures = signatures;
    vm.selectedDepartment = {};
    vm.signature = {};
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.generateDocument = generateDocument;
    vm.makeFormValid = makeFormValid;
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
    function generateDocument() {
      vm.template.firstSignature = vm.signature.firstSignature;
      vm.template.secondSignature = vm.signature.secondSignature;
      if (!vm.isBeko && !vm.template.firstSignature) {
        DocumentService.emptyFirstSignature()
          .error(function (response, status) {
            if (status === 400) {
              QFormJSRValidation
                .markErrors($scope, $scope.selectSignOrDepCtrl.signatureForm, response);
            }
          });
        return 1;
      }
      if (vm.isBeko && !vm.template.department) {
        DocumentService.emptyFirstSignature()
          .error(function (response, status) {
            if (status === 400) {
              QFormJSRValidation
                .markErrors($scope, $scope.selectSignOrDepCtrl.signatureForm, response);
            }
          });
        return 2;
      }
      if (template.createVersion) {
        AppService.setPaneShown(true);
        DocumentService.createDocument(vm.template)
          .success(function (data) {
            $uibModalInstance.close();
            $state.go(DOCUMENTLIST, {}, {
              reload: true
            });
            AppService.setPaneShown(false);
            if (createAndPrintDocument) {
              printDocuments(data);
            }
          }).error(function (response, status) {
            if (status === 400) { // Validation
              // errors.
              AppService.setPaneShown(false);
              QFormJSRValidation
                .markErrors(
                  $scope,
                  $scope.selectSignOrDepCtrl.signatureForm,
                  response);
            } else {
              AppService.setPaneShown(true);
            }
          });
      } else {
        DocumentService
          .documentVersionExists(template)
          .success(
            function (data) {
              if (data === 'versionExists') {
                overwriteDocumentConfirmation();
              } else {
                AppService.setPaneShown(true);
                DocumentService.createDocument(
                  template).success(
                  function (data) {
                    $uibModalInstance.close();
                    $state.go(DOCUMENTLIST, {}, {
                      reload: true
                    });
                    AppService.setPaneShown(false);
                    if (createAndPrintDocument) {
                      printDocuments(data);
                    }
                  }).error(function (response, status) {
                  if (status === 400) { // Validation
                    // errors.
                    AppService.setPaneShown(false);
                    QFormJSRValidation
                      .markErrors(
                        $scope,
                        $scope.selectSignOrDepCtrl.signatureForm,
                        response);
                  } else {
                    AppService.setPaneShown(true);
                  }
                });
              }
            }).error(function (response, status) {
            if (status === 400) { // Validation
              // errors.
              AppService.setPaneShown(false);
              QFormJSRValidation
                .markErrors(
                  $scope,
                  $scope.selectSignOrDepCtrl.signatureForm,
                  response);
            } else {
              AppService.setPaneShown(true);
            }
          });
      }
      return 3;
    }

    function printDocuments(documentIds) {
      for (var i = 0; i < documentIds.length; i++) {
        DocumentService.printDocument(documentIds[i])
          .success(function (response, status, headers) {
            if (headers()["content-disposition"] !== undefined) {
              var fileName = headers()["content-disposition"]
                .split(';')[1].trim().split('=')[1];
              // Remove quotes added from the DocumentResource in order to enable filenames with special characters like comma
              fileName = fileName.substring(1, fileName.length - 1);
              var blob = new Blob([response], {
                type: "application/pdf"
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

    function overwriteDocumentConfirmation() {
      var confirmationWindowInstance = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="selectSignatureCtrl.closeConfirmationWindow(\'nein\');"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">Dokument erstellen</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie das bereits existierende Dokument überschreiben?</p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="selectSignatureCtrl.closeConfirmationWindow(\'ja\');">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="selectSignatureCtrl.closeConfirmationWindow(\'nein\');">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'selectSignatureCtrl',
          backdrop: 'static',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeConfirmationWindow = function (reason) {
              confirmationWindowInstance.dismiss(reason);
            };
          }
        });

      return confirmationWindowInstance.result
        .then(
          function () {
            // Modal Success Handler
          },
          function (response) { // Modal Dismiss Handler
            if (response === 'ja') {
              AppService.setPaneShown(true);
              DocumentService.createDocument(template)
                .success(function (data) {
                  $state.go(DOCUMENTLIST, {}, {
                    reload: true
                  });
                  $uibModalInstance.close();
                  AppService.setPaneShown(false);
                  if (createAndPrintDocument) {
                    printDocuments(data);
                  }
                }).error(function (response, status) {
                  if (status === 400) { // Validation
                    // errors.
                    AppService.setPaneShown(false);
                    QFormJSRValidation
                      .markErrors(
                        $scope,
                        $scope.selectSignOrDepCtrl.signatureForm,
                        response);
                  } else {
                    AppService.setPaneShown(true);
                  }
                });
            } else {
              return false;
            }
            return null;
          });
    }

    /** Function to make invalid form valid */
    function makeFormValid() {
      if ($scope.selectSignOrDepCtrl.signatureForm.$invalid) {
        $scope.selectSignOrDepCtrl.signatureForm.$invalid = false;
      }
    }
  }
})();
