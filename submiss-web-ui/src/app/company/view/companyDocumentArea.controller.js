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
 * @name companyDocumentArea.controller.js
 * @description CompanyDocumentAreaController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.company")
    .controller("CompanyDocumentAreaController",
      CompanyDocumentAreaController)
    .filter('customUserDateFilterCompany', function ($filter) {
      return function (values, dateValue, createdOn) {
        var DATEFORM = 'dd.MM.yyyy';
        var filtered = [];

        if (typeof values != 'undefined' && typeof dateValue != 'undefined') {
          angular.forEach(values, function (value) {
            if (createdOn) {
              if (value.createdOn != null && $filter('date')(value.createdOn,
                  DATEFORM).indexOf($filter('date')(dateValue, DATEFORM)) >=
                0) {
                filtered.push(value);
              }
            } else {
              if (value.lastModifiedOn != null && value.lastModifiedOn !==
                undefined &&
                $filter('date')(value.lastModifiedOn, DATEFORM).indexOf(
                  $filter('date')(dateValue, DATEFORM)) >= 0) {
                filtered.push(value);
              }
            }
          });
        }
        return filtered;
      }
    });

  /** @ngInject */
  function CompanyDocumentAreaController($scope, $rootScope, $state,
    $stateParams,
    DocumentService, CompanyService, $uibModal, NgTableParams, $filter,
    $location, $anchorScroll, UsersService, AppService, AppConstants,
    QFormJSRValidation, TasksService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var resultsCount = 0; // Results counter.
    var KANTONBERN = 'Kanton Bern';
    var COM_PROOFREQUEST_URL = 'app/document/proofRequest/companyProofRequest.html';
    var CONTENT_DISPOSITION = 'content-disposition';
    var APPLICATION_OCTETSTREAM = 'application/octet-stream';
    var APPLICATION_PDF = 'application/pdf';
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.documentForm = {};
    vm.templateForm = {};
    vm.documents = [];
    vm.templates = [];
    vm.selectedDocumentIds = [];
    vm.name = $stateParams.name;
    vm.id = $stateParams.id;
    vm.templateForm.id = $stateParams.id;
    vm.secCompanyProofsView = false;
    vm.secProofVerificationRequest = false;
    vm.secTenderView = false;
    vm.secCompanyOffersView = false;
    vm.templateForm.createVersion = false;
    vm.disableVersion = false;
    vm.multipleFilesDownload = false;
    vm.active = [{
      id: '!!',
      title: ''
    }, {
      id: true,
      title: 'Ja'
    }, {
      id: false,
      title: 'Nein'
    }];
    vm.documentCreationType = [{
      id: '!!',
      title: ''
    }, {
      id: 'GENERATED',
      title: 'generiert'
    }, {
      id: 'UPLOADED',
      title: 'hochgeladen'
    }];
    vm.openCreatedOnFilter = openCreatedOnFilter;
    vm.openLastModifiedOnDateFilter = openLastModifiedOnDateFilter;
    vm.requestProofs = requestProofs;
    vm.uploadedDocuments = [];
    vm.loggedInUser = {};
    vm.createAndPrintDocument = false;
    vm.uploadError = null;
    vm.taskCreateForm = {};
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.showActiveOrInactive = showActiveOrInactive;
    vm.getTemplates = getTemplates;
    vm.getDocumentFiles = getDocumentFiles;
    vm.createDocument = createDocument;
    vm.mainCheckboxFunctionality = mainCheckboxFunctionality;
    vm.seperateCheckboxFunctionality = seperateCheckboxFunctionality;
    vm.seperateDocumentDownload = seperateDocumentDownload;
    vm.onTemplateSelection = onTemplateSelection;
    vm.uploadErrorFunction = uploadErrorFunction;
    vm.uploaded = uploaded;
    vm.uploadSelectedDocument = uploadSelectedDocument;
    vm.deleteDocument = deleteDocument;
    vm.downloadMultipleDocuments = downloadMultipleDocuments;
    vm.editDocumentProperties = editDocumentProperties;
    vm.uploadMultipleDocuments = uploadMultipleDocuments;
    vm.uploadMultipleDocumentsDialog = uploadMultipleDocumentsDialog;
    vm.uploadedMultipleDocuments = uploadedMultipleDocuments;
    vm.printDocument = printDocument;
    vm.printDocuments = printDocuments;
    vm.printAllowedFiles = printAllowedFiles;
    vm.getCompanyTask = getCompanyTask;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      readCompany($stateParams.id);
      getTemplates();
      getDocumentFiles($stateParams.id);
      vm.secProofVerificationRequest = AppService.isOperationPermitted(
        AppConstants.OPERATION.PROOF_VERIFICATION_REQUEST, null);
      vm.secCompanyProofsView = AppService.isOperationPermitted(
        AppConstants.OPERATION.COMPANY_PROOFS_VIEW, null);
      vm.secCompanyOffersView = AppService.isOperationPermitted(
        AppConstants.OPERATION.COMPANY_OFFERS_VIEW, null);
      vm.secCompanyDocumentUpload = AppService.isOperationPermitted(
        AppConstants.OPERATION.COMPANY_DOCUMENT_UPLOAD, null);
      vm.secCompanyDocumentDownload = AppService.isOperationPermitted(
        AppConstants.OPERATION.COMPANY_DOCUMENT_DOWNLOAD, null);
      vm.secCompanyDocumentPrint = AppService.isOperationPermitted(
        AppConstants.OPERATION.COMPANY_DOCUMENT_PRINT, null);
      vm.secCompanyDocumentDelete = AppService.isOperationPermitted(
        AppConstants.OPERATION.COMPANY_DOCUMENT_DELETE, null);
      vm.secCompanyDocumentEditProperties = AppService
        .isOperationPermitted(
          AppConstants.OPERATION.COMPANY_DOCUMENT_EDIT_PROPERTIES, null);
      vm.getCompanyTask($stateParams.id);
    }
    /***********************************************************************
     * $scope destroy
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    function onTemplateSelection(selection) {
      if (selection != null && selection.value1 ===
        AppConstants.PROOF_REQUEST_FT) {
        if (AppService.getLoggedUser().tenant.name !== KANTONBERN) {
          vm.templateForm.createVersion = true;
        }
        vm.disableVersion = true;
      } else if (selection != null && (selection.value1 ===
          AppConstants.CERTIFICATE || selection.value1 ===
          AppConstants.WORKTYPE_LIST ||
          selection.value1 === AppConstants.COMPANYFILE_SIMPLE ||
          selection.value1 === AppConstants.COMPANYFILE_EXTENDED)) {
        vm.templateForm.createVersion = false;
        vm.disableVersion = true;
      } else {
        vm.templateForm.createVersion = false;
        vm.disableVersion = false;
      }
    }

    /** Read company information */
    function readCompany(id) {
      CompanyService.readCompany(id).success(function (data) {
        vm.data.company = data;
      }).error(function (response, status) {});
    }

    function getCompanyTask(companyId) {
      TasksService.getCompanyTask(companyId).success(function (data) {
        var name = (data.firstName != null && data.lastName != null) ? data.firstName + ' ' + data.lastName : null;
        vm.data.companyTask = {
          createdOn: data.createdOn,
          createdBy: name
        };
      }).error(function (response, status) {});
    }

    /** Get documents of company */
    function getDocumentFiles(id) {
      var timer = setTimeout(function () {
        AppService.setPaneShown(true);
      }, 500);
      DocumentService.getDocumentListCompany(id)
        .success(function (data) {
          clearTimeout(timer);
          AppService.setPaneShown(false);
          vm.documents = data;
          vm.tableParams = new NgTableParams({
            page: 1,
            count: 10,
            sorting: {
              title: 'asc' // initial sorting
            }
          }, {
            // https://stackoverflow.com/questions/34963090/make-table-heading-sortable-by-clicking-heading-with-angularjs-ngtable
            total: vm.documents.length,
            getData: function ($defer, params) {
              var filters = params.filter(true);
              var filteredData;
              if (filters) {
                filteredData = vm.documents;
                var filteredCreatedOn = false;
                var filteredLastModifiedOn = false;
                var createdOneFilterValue;
                var lastModifiedOnValue;
                // Custom filter has to be used to filter date
                if (filters.createdOn) {
                  filteredData = $filter('customUserDateFilterCompany')(
                    vm.documents, filters.createdOn, true);
                  filteredCreatedOn = true;
                  createdOneFilterValue = filters.createdOn;
                  delete filters.createdOn;
                }
                if (filters.lastModifiedOn) {
                  filteredData = $filter('customUserDateFilterCompany')(
                    vm.documents, filters.lastModifiedOn, false);
                  filteredLastModifiedOn = true;
                  lastModifiedOnValue = filters.lastModifiedOn;
                  delete filters.lastModifiedOn;
                }
                filteredData = $filter('filter')(filteredData, filters);
                // Restore filter value to the filters
                if (filteredCreatedOn) {
                  filters.createdOn = createdOneFilterValue;
                }
                if (filteredLastModifiedOn) {
                  filters.lastModifiedOn = lastModifiedOnValue;
                }
              } else {
                filteredData = vm.documents;
              }
              resultsCount = filteredData.length;
              vm.divTableHeight = AppService.adjustTableHeight(resultsCount,
                vm.openLastModifiedOnDateFilter.opened ||
                vm.openCreatedOnFilter.opened);
              var orderedData = params.sorting() ? $filter('orderBy')(
                filteredData, params.orderBy()) : filteredData;
              // when -1 value, show all data
              if (params.count() === -1) {
                $defer.resolve(orderedData);
              } else {
                $defer.resolve(
                  orderedData.slice((params.page() - 1) * params.count(),
                    params.page() * params.count()));
              }
            }
          });
          return vm.tableParams;
        }).error(function (response, status) {});
    }

    function createDocument() {
      if (vm.chosenTemplate !== undefined) {
        vm.templateForm.templateId = vm.chosenTemplate.masterListValueId.id;
        vm.templateForm.tenantId = vm.chosenTemplate.tenant.id;
        vm.templateForm.templateName = vm.chosenTemplate.value1;
        // Check if the user has requested or not a new version.
        // set these values to false. It shows that the template is derived
        // from user input.
        vm.templateForm.generateProofTemplate = false;
        vm.templateForm.isCompanyCertificate = false;
        vm.templateForm.projectDocument = false;
        if (vm.chosenTemplate.value1 === AppConstants.CERTIFICATE) {
          $uibModal
            .open({
              templateUrl: 'app/document/certificate/generateCertificate.html',
              controller: 'GenerateCertificateController',
              controllerAs: 'generateCertificateCtrl',
              backdrop: 'static',
              keyboard: false,
              resolve: {
                template: function () {
                  return vm.templateForm;
                },
                createAndPrintDocument: function () {
                  return vm.createAndPrintDocument;
                }
              }
            });
        } else if (vm.chosenTemplate.value1 === AppConstants.WORKTYPE_LIST) {
          AppService.setPaneShown(true);
          vm.templateForm.templateShortCode = 'FT05';
          if (vm.createAndPrintDocument) {
            downloadAndPrintCompanyDocument(vm.templateForm);
          } else {
            downloadCompanyDocument(vm.templateForm);
          }
        } else if (vm.chosenTemplate.value1 ===
          AppConstants.COMPANYFILE_SIMPLE) {
          AppService.setPaneShown(true);
          vm.templateForm.templateShortCode = 'FT03';
          if (vm.createAndPrintDocument) {
            downloadAndPrintCompanyDocument(vm.templateForm);
          } else {
            downloadCompanyDocument(vm.templateForm);
          }
        } else if (vm.chosenTemplate.value1 ===
          AppConstants.COMPANYFILE_EXTENDED) {
          AppService.setPaneShown(true);
          vm.templateForm.templateShortCode = 'FT04';
          if (vm.createAndPrintDocument) {
            downloadAndPrintCompanyDocument(vm.templateForm);
          } else {
            downloadCompanyDocument(vm.templateForm);
          }
        } else if (vm.chosenTemplate.shortCode ===
          AppConstants.COMPANIES_CREATED_PER_MANDANT) {
          vm.templateForm.templateShortCode = AppConstants.COMPANIES_CREATED_PER_MANDANT;
          AppService.setPaneShown(true);
          if (vm.createAndPrintDocument) {
            downloadAndPrintCompanyDocument(vm.templateForm);
          } else {
            downloadCompanyDocument(vm.templateForm);
          }
        } else {
          if (vm.templateForm.createVersion) {
            if (AppService.getLoggedUser().tenant.name !== KANTONBERN) {
              if (vm.chosenTemplate.value1 === AppConstants.PROOF_REQUEST_FT) {
                $uibModal
                  .open({
                    templateUrl: COM_PROOFREQUEST_URL,
                    controller: 'CompanyProofRequestController',
                    controllerAs: 'companyProofRequestCtrl',
                    backdrop: 'static',
                    keyboard: false,
                    resolve: {
                      template: function () {
                        return vm.templateForm;
                      },
                      createAndPrintDocument: function () {
                        return vm.createAndPrintDocument;
                      }
                    }
                  });
              } else {
                AppService.setPaneShown(true);
                DocumentService.createDocument(vm.templateForm)
                  .success(function (data) {
                    AppService.setPaneShown(false);
                    if (vm.createAndPrintDocument) {
                      printDocuments(data);
                    }
                  }).error(function (response, status) {
                    AppService.setPaneShown(false);
                    handleValidationErrors(response, status);
                  });
              }
            } else {
              CompanyService.openProofKanton($stateParams.id)
                .success(function (data) {
                  if (data !== "mandatory_error_message") {
                    window.location.href = data;
                  } else {
                    vm.errorMessage = true;
                  }
                })
            }
          } else {
            /**
             * In case the user has not requested a new version and a
             * version already exists for this selection in the database,
             * ask user for confirmation to overwrite the existing version
             */
            DocumentService.documentVersionExists(vm.templateForm)
              .success(function (data) {
                if (data === 'versionExists') {
                  overwriteDocumentConfirmation();
                } else {
                  if (vm.chosenTemplate.value1 ===
                    AppConstants.PROOF_REQUEST_FT) {
                    if (AppService.getLoggedUser().tenant.name !==
                      KANTONBERN) {
                      $uibModal
                        .open({
                          templateUrl: COM_PROOFREQUEST_URL,
                          controller: 'CompanyProofRequestController',
                          controllerAs: 'companyProofRequestCtrl',
                          backdrop: 'static',
                          keyboard: false,
                          resolve: {
                            template: function () {
                              return vm.templateForm;
                            },
                            createAndPrintDocument: function () {
                              return vm.createAndPrintDocument;
                            }
                          }
                        });
                    } else {
                      CompanyService.openProofKanton($stateParams.id)
                        .success(function (data) {
                          window.location.href = data.mailTo;
                          if (!(data.message == null || data.message !== "mandatory_error_message")) {
                            $uibModal
                              .open({
                                templateUrl: 'app/document/email/emailPopUp.html',
                                controller: 'EmailPopUpController',
                                controllerAs: 'emailPopUpCtrl',
                                backdrop: 'static',
                                keyboard: false,
                                resolve: {
                                  body: function () {
                                    return data.body;
                                  },
                                  message: function () {
                                    return data.message;
                                  }
                                }
                              });
                          }
                        });
                    }
                  } else if (vm.chosenTemplate.value1 ===
                    AppConstants.CERTIFICATE) {
                    $uibModal
                      .open({
                        templateUrl: 'app/document/certificate/generateCertificate.html',
                        controller: 'GenerateCertificateController',
                        controllerAs: 'generateCertificateCtrl',
                        backdrop: 'static',
                        keyboard: false,
                        resolve: {
                          template: function () {
                            return vm.templateForm;
                          }
                        }
                      });
                  } else {
                    AppService.setPaneShown(true);
                    DocumentService.createDocument(vm.templateForm)
                      .success(function (data) {
                        AppService.setPaneShown(false);
                        if (vm.createAndPrintDocument) {
                          printDocuments(data);
                        }
                      }).error(function (response, status) {
                        AppService.setPaneShown(false);
                        handleValidationErrors(response, status);
                      });
                  }
                }
              }).error(function (response, status) {});
          }
        }
      } else {
        AppService.setPaneShown(true);
        DocumentService.createDocument(vm.templateForm)
          .success(function (data) {
            if (vm.createAndPrintDocument) {
              printDocuments(data);
            }
          }).error(function (response, status) {
            AppService.setPaneShown(false);
            handleValidationErrors(response, status);
          });
      }
    }

    function downloadCompanyDocument(templateForm) {
      DocumentService.downloadCompanyDocument(templateForm)
        .success(function (response, status, headers) {
          var fileName = headers()[CONTENT_DISPOSITION]
            .split(';')[1].trim().split('=')[1];
          // Remove quotes added from the DocumentResource in order to enable filenames with special characters like comma
          fileName = fileName.substring(1, fileName.length - 1);
          var blob = new Blob([response], {
            type: APPLICATION_OCTETSTREAM
          });
          // For Internet Explorer 11 only
          if (window.navigator && window.navigator.msSaveOrOpenBlob) {
            window.navigator.msSaveOrOpenBlob(blob, fileName);
          } else {
            var objectUrl = URL.createObjectURL(blob);
            var a = $("<a style='display: none;'/>");
            a.attr("href", objectUrl);
            a.attr("download", fileName);
            $("body").append(a);
            a[0].click();
            window.URL.revokeObjectURL(objectUrl);
            a.remove();
          }
          AppService.setPaneShown(false);
        }).error(function (response, status) {});
    }

    function downloadAndPrintCompanyDocument(templateForm) {
      DocumentService.downloadAndPrintCompanyDocument(templateForm)
        .success(function (response, status, headers) {
          // download document
          downloadCompanyDocument(templateForm);
          // print document
          if (headers()[CONTENT_DISPOSITION]) {
            var fileName = headers()[CONTENT_DISPOSITION]
              .split(';')[1].trim().split('=')[1];
            // Remove quotes added from the DocumentResource in order to enable filenames with special characters like comma
            fileName = fileName.substring(1, fileName.length - 1);
            var blob = new Blob([response], {
              type: APPLICATION_PDF
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

    function overwriteDocumentConfirmation() {
      var confirmationWindowInstance = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="documentViewCtrl.closeConfirmationWindow(\'nein\');"><span aria-hidden="true">&times;</span></button>' +
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
            '<button type="button" class="btn btn-primary" ng-click="documentViewCtrl.closeConfirmationWindow(\'ja\');">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="documentViewCtrl.closeConfirmationWindow(\'nein\');">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'documentViewCtrl',
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
          },
          function (response) { // Modal Dismiss Handler
            if (response === 'ja') {
              vm.templateForm.templateId = vm.chosenTemplate.masterListValueId.id;
              vm.templateForm.tenantId = vm.chosenTemplate.tenant.id;
              vm.templateForm.templateName = vm.chosenTemplate.value1;
              // set these values to false. It shows that the template is derived from user input.
              vm.templateForm.generateProofTemplate = false;
              vm.templateForm.isCompanyCertificate = false;
              if (vm.chosenTemplate.value1 === AppConstants.PROOF_REQUEST_FT) {
                $uibModal
                  .open({
                    templateUrl: COM_PROOFREQUEST_URL,
                    controller: 'CompanyProofRequestController',
                    controllerAs: 'companyProofRequestCtrl',
                    backdrop: 'static',
                    keyboard: false,
                    resolve: {
                      template: function () {
                        return vm.templateForm;
                      }
                    }
                  });
              } else {
                AppService.setPaneShown(true);
                DocumentService.createDocument(vm.templateForm)
                  .success(function (data) {
                    AppService.setPaneShown(false);
                    if (vm.createAndPrintDocument) {
                      printDocuments(data);
                    }
                  }).error(function (response, status) {
                    AppService.setPaneShown(false);
                    handleValidationErrors(response, status);
                  });
              }
            } else {
              return false;
            }
            return null;
          });
    }

    /** Function to show text for Aktiv column */
    function showActiveOrInactive(active) {
      if (active) {
        return "Ja";
      } else {
        return "Nein";
      }
    }

    /** Get the document templates */
    function getTemplates() {
      CompanyService.getCompanyTemplates($stateParams.id).success(
        function (data) {
          vm.templates = data;
          vm.chosenTemplate = vm.templates[0];
        }).error(function (response, status) {});
    }

    // select/unselect all checkboxes
    function mainCheckboxFunctionality() {
      for (var i = 0; i < vm.documents.length; i++) {
        vm.documents[i].checkbox = vm.mainCheckbox;
      }
      vm.multipleFilesDownload = vm.mainCheckbox;
    }

    // disable maincheckbox if not all selected
    function seperateCheckboxFunctionality() {
      var documentsSelected = 0;
      for (var i = 0; i < vm.documents.length; i++) {
        if (vm.documents[i].checkbox) {
          documentsSelected++;
        }
      }
      if (documentsSelected >= 2) {
        vm.multipleFilesDownload = true;
      } else {
        vm.multipleFilesDownload = false;
      }
    }

    // download specific document functionality
    function seperateDocumentDownload(documentId) {
      DocumentService.downloadDocument(documentId)
        .success(function (response, status, headers) {
          var fileName = headers()[CONTENT_DISPOSITION]
            .split(';')[1].trim().split('=')[1];
          fileName = fileName.substring(1, fileName.length - 1);
          var blob = new Blob([response], {
            type: APPLICATION_OCTETSTREAM
          });
          // For Internet Explorer 11 only
          if (window.navigator && window.navigator.msSaveOrOpenBlob) {
            window.navigator.msSaveOrOpenBlob(blob, fileName);
          } else {
            var objectUrl = URL.createObjectURL(blob);
            var a = $("<a style='display: none;'/>");
            a.attr("href", objectUrl);
            a.attr("download", fileName);
            $("body").append(a);
            a[0].click();
            window.URL.revokeObjectURL(objectUrl);
            a.remove();
          }
        }).error(function (response, status) {});
    }

    function handleValidationErrors(response, status) {
      if (status === 400) { // Validation errors.
        QFormJSRValidation.markErrors($scope,
          $scope.companyDocumentAreaCtrl.template, response);
      }
    }

    function uploadErrorFunction(file, message, flow) {
      var response = JSON.parse(message);
      vm.uploadError = response[0].message;
    }

    function uploaded(flow) {
      var file = flow.files[0];
      if (file !== undefined) {
        var uploadedFileId = file.uniqueIdentifier;
        // Call upload rest method only if the file uploaded does not contain any errors
        if (file.error === false) {
          if (this.selectedDocument.documentCreationType === 'GENERATED') {
            // In case of a generated file, apply the users choice to overwrite or create a new version
            if (this.replaceDocument === 'true') {
              DocumentService.uploadSelectedDocument(this.selectedDocument.id,
                  uploadedFileId, true, false)
                .success(function (data) {
                  $state.reload();
                }).error(function (response, status) {
                  vm.uploadError = response[0].message;
                });
            } else {
              DocumentService.uploadSelectedDocument(this.selectedDocument.id,
                  uploadedFileId, false, false)
                .success(function (data) {
                  $state.reload();
                }).error(function (response, status) {
                  vm.uploadError = response[0].message;
                });
            }
          } else {
            DocumentService.uploadSelectedDocument(this.selectedDocument.id,
                uploadedFileId, true, false)
              .success(function (data) {
                $state.reload();
              }).error(function (response, status) {
                vm.uploadError = response[0].message;
              });
          }
        }
      }
      flow.cancel();
    }

    function uploadedMultipleDocuments(flow) {
      for (var i = 0; i < flow.files.length; i++) {
        for (var j = 0; j < vm.uploadedDocuments.length; j++) {
          if (vm.uploadedDocuments[j].filename === flow.files[i].name) {
            vm.uploadedDocuments[j].uploadedFileId = flow.files[i].uniqueIdentifier;
            break;
          }
        }
      }
      DocumentService.uploadMultipleDocuments(vm.uploadedDocuments)
        .success(function (data) {
          $state.reload();
        }).error(function (response, status) {
          vm.uploadError = response[0].message;
        });
    }

    function uploadSelectedDocument(flow, selectedDocument) {
      var validFormat = false;
      for (var i = 0; i < flow.files.length; i++) {
        validFormat = AppService.isValidFormat(
          flow.files[i].name.split(".").pop());
        if (!validFormat) {
          break;
        }
      }
      if (validFormat) {
        vm.selectedDocument = selectedDocument;
        if (selectedDocument.documentCreationType === 'GENERATED') {
          replaceDocumentConfirmation(flow);
        } else {
          flow.upload();
        }
      } else {
        flow.cancel();
        vm.uploadError = 'invalid_file_type';
      }
    }

    function uploadMultipleDocuments(flow) {
      var validFormat = false;
      for (var i = 0; i < flow.files.length; i++) {
        validFormat = AppService.isValidFormat(
          flow.files[i].name.split(".").pop());
        if (!validFormat) {
          break;
        }
      }
      if (validFormat) {
        DocumentService.getDocumentVersionsByFilename(flow, $stateParams.id)
          .success(function (data) {
            for (var i = 0; i < flow.files.length; i++) {
              var documentUploadForm = {
                filename: flow.files[i].name,
                versioningAllowed: false,
                versionDocument: false,
                projectDocument: false,
                folderId: $stateParams.id,
                newFile: true
              };
              for (var j = 0; j < data.length; j++) {
                if (data[j].filename === flow.files[i].name) {
                  documentUploadForm.versionId = data[j].id;
                  documentUploadForm.newFile = false;

                  if (data[j].documentCreationType === 'GENERATED') {
                    documentUploadForm.versioningAllowed = true;
                    // In case of Nachweisbrief document upload,
                    // version checkbox must be checked by default.
                    if (data[j].filename.startsWith('Nachweisbrief')) {
                      documentUploadForm.versionDocument = true;
                    }
                  }
                  break;
                }
              }
              vm.uploadedDocuments.push(documentUploadForm);
            }
            uploadMultipleDocumentsDialog(vm.uploadedDocuments, flow);
          }).error(function (response, status) {});
      } else {
        flow.cancel();
        vm.uploadError = 'invalid_file_type';
      }
    }

    function replaceDocumentConfirmation(flow) {
      $uibModal.open({
        templateUrl: 'app/document/replaceDocument.html',
        controller: 'ReplaceDocumentController',
        controllerAs: 'ReplaceDocumentController',
        resolve: {
          replaceDocument: function () {
            return $scope.replaceDocument;
          },
          flow: function () {
            return flow;
          }
        }
      }).result.then(function (result) {
        vm.replaceDocument = result;
      });
    }

    function deleteDocument(templateId, versionId, documentTitle) {
      $uibModal
        .open({
          templateUrl: 'app/document/deleteDocument.html',
          controller: 'DeleteDocumentController',
          controllerAs: 'deleteDocumentController',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            versionId: function () {
              return versionId;
            },
            documentTitle: function () {
              return documentTitle;
            },
            templateId: function () {
              return templateId;
            }
          }
        });
    }

    function downloadMultipleDocuments() {
      for (var i = 0; i < vm.documents.length; i++) {
        if (vm.documents[i].checkbox) {
          vm.selectedDocumentIds.push(vm.documents[i].id);
        }
      }
      DocumentService.downloadMultipleDocuments(vm.selectedDocumentIds)
        .success(function (response, status, headers) {
          var fileName = headers()[CONTENT_DISPOSITION]
            .split(';')[1].trim().split('=')[1];
          // Remove quotes added from the DocumentResource in order to enable filenames with special characters like comma
          fileName = fileName.substring(1, fileName.length - 1);
          var blob = new Blob([response], {
            type: APPLICATION_OCTETSTREAM
          });
          // For Internet Explorer 11 only
          if (window.navigator && window.navigator.msSaveOrOpenBlob) {
            window.navigator.msSaveOrOpenBlob(blob, fileName);
          } else {
            var objectUrl = URL.createObjectURL(blob);
            var a = $("<a style='display: none;'/>");
            a.attr("href", objectUrl);
            a.attr("download", fileName);
            $("body").append(a);
            a[0].click();
            window.URL.revokeObjectURL(objectUrl);
            a.remove();
          }
        }).error(function (response, status) {});
      vm.selectedDocumentIds = [];
    }

    /** this function handles the editing of the document properties */
    function editDocumentProperties(selectedDocument, editValue) {
      $uibModal.open({
        templateUrl: 'app/document/documentEdit/documentEdit.html',
        controller: 'DocumentEditController',
        controllerAs: 'documentEditCtrl',
        size: 'lg',
        backdrop: 'static',
        keyboard: false,
        resolve: {
          document: function () {
            return selectedDocument;
          },
          editMode: editValue,
          projectPart: false
        }
      });
    }

    function openCreatedOnFilter() {
      vm.openCreatedOnFilter.opened = !vm.openCreatedOnFilter.opened;
      vm.divTableHeight = AppService.adjustTableHeight(resultsCount,
        vm.openCreatedOnFilter.opened);
    }

    function openLastModifiedOnDateFilter() {
      vm.openLastModifiedOnDateFilter.opened = !vm.openLastModifiedOnDateFilter.opened;
      vm.divTableHeight = AppService.adjustTableHeight(resultsCount,
        vm.openLastModifiedOnDateFilter.opened);
    }

    function uploadMultipleDocumentsDialog(documents, flow) {
      $uibModal
        .open({
          templateUrl: 'app/document/uploadMultipleDocuments.html',
          controller: 'UploadMultipleDocumentsController',
          controllerAs: 'uploadMultipleDocumentsController',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            documents: function () {
              return documents;
            },
            flow: function () {
              return flow;
            }
          }
        }).result.then(function (result) {
          vm.uploadedDocuments = result;
        }, function (response) { // Modal Dismiss Handler
          if (response === 'cancel') {
            vm.uploadedDocuments = [];
          }
        });
    }

    function printDocument(documentId) {
      /** IE and Edge do not accept Blob Url in an <object> element . (Access Denied error)
       * In addition, IE 11 does not support to render and print pdfs through an IFrame and needs an <object> or <embed> element instead
       https://stackoverflow.com/questions/24007073/open-links-made-by-createobjecturl-in-ie11
       https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/8474657/
       Errors: SEC7134
       Firefox does not allow to call print
       Error: Permission denied to access property "print" on cross-origin object
       https://stackoverflow.com/questions/33254679/print-pdf-in-firefox
       https://bugzilla.mozilla.org/show_bug.cgi?id=911444
       */
      DocumentService.printDocument(documentId)
        .success(function (response, status, headers) {
          if (headers()[CONTENT_DISPOSITION]) {
            var fileName = headers()[CONTENT_DISPOSITION].split(
              ';')[1].trim().split('=')[1];
            // Remove quotes added from the DocumentResource in order to enable filenames with special characters like comma
            fileName = fileName.substring(1, fileName.length - 1);
            var blob = new Blob([response], {
              type: APPLICATION_PDF
            });
            var objectURL = URL.createObjectURL(blob);
            // TODO maintain until customer response for the browser compatibility issues
            // Works on crhome partially on firefox
            //					var objFra = document.getElementById('print-iframe');
            //					objFra.setAttribute('src', objectURL);
            //					setTimeout(function(){
            //					       objFra.contentWindow.focus();
            //					       objFra.contentWindow.print();
            //					    },2000);
            //
            //						// Works on chrome
            //					    const iframe = document.createElement('iframe');
            //					    iframe.style.display = 'none';
            //					    iframe.src = objectURL;
            //					    document.body.appendChild(iframe);
            //					    iframe.contentWindow.print();
            //
            //
            //						  // Works on Chrome
            //					    // FF : SecurityError: Permission denied to access property "print" on cross-origin object
            //					    // II : Prints empty pdf
            //					    $("#print-iframe").get(0).setAttribute('src', objectURL);
            //					    setTimeout(function(){
            //					        window.frames["print-iframe"].focus();
            //					        window.frames["print-iframe"].print();
            //					    },2000);
            //
            //
            //					   // Works on Chrome
            //					   // IE shows empty pdf
            //					    var objFra = document.getElementById('print-iframe');
            //					//    objFra.setAttribute('src', objectURL);
            //					    var contentWindow = objFra.contentWindow;
            //					    var result = contentWindow.document.execCommand('print', false, null);
            //					    if (!result)
            //					    contentWindow.print();
            //
            //					    // Works on IE with absolut path to pdf
            //					    var ua = window.navigator.userAgent;
            //					    var msie = ua.indexOf('MSIE ');
            //					    var trident = ua.indexOf('Trident/');
            //					    var edge = ua.indexOf('Edge/');
            //					    var url = 'app/document/test.pdf';
            //					    var pdf ='';
            //					    var style = 'position:fixed; top:0px; left:0px; bottom:0px; right:0px; width:100%; height:100%; border:none; margin:0; padding:0; overflow:hidden;';
            //
            //					    if(msie > 0 || trident > 0 || edge > 0){
            //					        pdf = '<object data="' + objectURL + '" name="print_frame" id="print_frame"  type="application/pdf">';
            //					    }
            //					    else{
            //					        pdf ='<iframe src="' + objectURL + '" name="print_frame" id="print_frame" ></iframe>';
            //					    }
            //
            //					    $(document.body).append(pdf);
            //					//    window.navigator.msSaveOrOpenBlob(blob, "test");
            //					    setTimeout(function(){
            //					        window.frames["print_frame"].focus();
            //					        window.frames["print_frame"].print();
            //					    },2000);

            // Works on Chrome
            var iFrameJQueryObject = $('<iframe id="iframe" src="' + objectURL +
              '" style="display:none"></iframe>');
            $('body').append(iFrameJQueryObject);
            iFrameJQueryObject.on('load', function () {
              $(this).get(0).contentWindow.print();
            });
          }
        }).error(function (response, status) {

        });
    }

    function printDocuments(documentIds) {
      for (var i = 0; i < documentIds.length; i++) {
        DocumentService.printDocument(documentIds[i])
          .success(function (response, status, headers) {
            if (headers()[CONTENT_DISPOSITION]) {
              var fileName = headers()[CONTENT_DISPOSITION].split(
                ';')[1].trim().split('=')[1];
              // Remove quotes added from the DocumentResource in order to enable filenames with special characters like comma
              fileName = fileName.substring(1, fileName.length - 1);
              var blob = new Blob([response], {
                type: APPLICATION_PDF
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

    function requestProofs() {
      vm.taskCreateForm.taskType = AppConstants.TASK_TYPES[1];
      vm.taskCreateForm.submittentIDs = [];
      vm.taskCreateForm.submittentIDs.push(vm.data.company.id);
      TasksService.createTask(vm.taskCreateForm)
        .success(function (data) {
          $state.reload();
        }).error(function (response, status) {});
    }

    function printAllowedFiles(filename) {
      return filename.endsWith('xlsx') || filename.endsWith('jpg') || filename.endsWith('jpeg') ||
        filename.endsWith('gif') || filename.endsWith('rtf') || filename.endsWith('html') ||
        filename.endsWith('ppt') || filename.endsWith('pptx') || filename.endsWith('csv') ||
        filename.endsWith('mpp') || filename.endsWith('htm') || filename.endsWith('xls') ||
        filename.endsWith('msg');
    }
  }
})();
