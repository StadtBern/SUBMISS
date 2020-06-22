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
 * @description GenerateContractController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document")
    .controller("GenerateContractController", GenerateContractController);

  /** @ngInject */
  function GenerateContractController($scope, QFormJSRValidation, AppService,
    AppConstants, $uibModalInstance, SubmissionService, DocumentService, $stateParams,
    template, $state, submission, $uibModal, createAndPrintDocument) {
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
    vm.templates = [];
    vm.contractTemplate = {};
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.generateContract = generateContract;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      loadDepartmentTemplates();
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    function generateContract() {
      template.templateId = vm.contractTemplate.masterListValueId.id;
      template.templateName = vm.contractTemplate.value1;
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
            AppService.setPaneShown(false);
            if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST) {
              QFormJSRValidation.markErrors($scope,
                $scope.generateContractCtrl.requestForm, response);
            }
          });
      } else {
        DocumentService
          .documentVersionExists(template)
          .success(function (data) {
            if (data === 'versionExists') {
              overwriteDocumentConfirmation();
            } else {
              AppService.setPaneShown(true);
              DocumentService.createDocument(template)
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
                  AppService.setPaneShown(false);
                  if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST) {
                    QFormJSRValidation.markErrors($scope,
                      $scope.generateContractCtrl.requestForm, response);
                  }
                });
            }
          }).error(function (response, status) {
            AppService.setPaneShown(true);
          });
      }
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
            '<button type="button" class="close" aria-label="Close" ng-click="generateContractCtrl.closeConfirmationWindow(\'nein\');">' +
            '<span aria-hidden="true">&times;</span></button>' +
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
            '<button type="button" class="btn btn-primary" ng-click="generateContractCtrl.closeConfirmationWindow(\'ja\');">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="generateContractCtrl.closeConfirmationWindow(\'nein\');">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'generateContractCtrl',
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
        .then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            template.templateId = vm.contractTemplate.masterListValueId.id;
            template.templateName = vm.contractTemplate.value1;
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
                AppService.setPaneShown(false);
                if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST) {
                  QFormJSRValidation.markErrors($scope,
                    $scope.generateContractCtrl.requestForm, response);
                }
              });
          } else {
            return false;
          }
          return null;
        });
    }

    //Function that returns all Abteilungen
    function loadDepartmentTemplates() {
      SubmissionService.getTemplatesByDepartment(submission.id)
        .success(function (data) {
          vm.templates = data;
        }).error(function (response, status) {});
    }
  }
})();
