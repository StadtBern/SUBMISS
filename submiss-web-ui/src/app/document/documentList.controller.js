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
 * @name offerListView.controller.js
 * @description DocumentViewController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document")
    .controller("DocumentListController", DocumentListController)
    .filter('customDocumentDateFilter', function ($filter) {
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
  function DocumentListController($scope, $rootScope, DocumentService,
    $state, $stateParams, NgTableParams, $filter, UsersService,
    $uibModal, AppService, AppConstants) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var resultsCount = 0; // Results counter.
    var CONTENT_DISPOSITION = 'content-disposition';
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.documentForm = {};
    vm.documents = [];
    vm.selectedDocumentIds = [];
    vm.documents.checkbox = null;
    vm.mainCheckbox = false;
    vm.tableParams = new NgTableParams({
      page: 1,
      count: 10,
      sorting: {
        objectName: 'asc'
      }
    });
    vm.selectedDocument = null;
    vm.replaceDocument = null;
    vm.multipleFilesDownload = false;
    vm.uploadedDocuments = [];
    vm.permittedUploadDocumentsNames = [];
    // '' values for drop downs are used in order to reset the filter when no option is selected
    vm.active = [{
        id: '!!',
        title: ''
      },
      {
        id: true,
        title: 'Ja'
      },
      {
        id: false,
        title: 'Nein'
      }
    ];
    vm.documentCreationType = [{
        id: '!!',
        title: ''
      },
      {
        id: 'GENERATED',
        title: 'generiert'
      },
      {
        id: 'UPLOADED',
        title: 'hochgeladen'
      }
    ];
    vm.secProjectMultipleDocumentUpload = true;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.showActiveOrInactive = showActiveOrInactive;
    vm.getDocumentFiles = getDocumentFiles;
    vm.mainCheckboxFunctionality = mainCheckboxFunctionality;
    vm.seperateCheckboxFunctionality = seperateCheckboxFunctionality;
    vm.seperateDocumentDownload = seperateDocumentDownload;
    vm.uploaded = uploaded;
    vm.uploadSelectedDocument = uploadSelectedDocument;
    vm.uploadError = uploadError;
    vm.deleteDocument = deleteDocument;
    vm.downloadMultipleDocuments = downloadMultipleDocuments;
    vm.editDocumentProperties = editDocumentProperties;
    vm.openCreatedOnFilter = openCreatedOnFilter;
    vm.openLastModifiedOnDateFilter = openLastModifiedOnDateFilter;
    vm.uploadMultipleDocuments = uploadMultipleDocuments;
    vm.uploadMultipleDocumentsDialog = uploadMultipleDocumentsDialog;
    vm.uploadedMultipleDocuments = uploadedMultipleDocuments;
    vm.printDocument = printDocument;
    vm.printDocumentHidden = printDocumentHidden;
    vm.getPermittedDocumentsForUpload = getPermittedDocumentsForUpload;
    vm.printAllowedFiles = printAllowedFiles;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      vm.getDocumentFiles($stateParams.id);
      vm.secProjectDocumentUpload = AppService.isOperationPermitted(
        AppConstants.OPERATION.PROJECT_DOCUMENT_UPLOAD, null);
      vm.secProjectDocumentDownload = AppService.isOperationPermitted(
        AppConstants.OPERATION.PROJECT_DOCUMENT_DOWNLOAD, null);
      vm.secProjectDocumentPrint = AppService.isOperationPermitted(
        AppConstants.OPERATION.PROJECT_DOCUMENT_PRINT, null);
      vm.secProjectDocumentDelete = AppService.isOperationPermitted(
        AppConstants.OPERATION.PROJECT_DOCUMENT_DELETE, null);
      vm.secProjectDocumentEditProperties = AppService.isOperationPermitted(
        AppConstants.OPERATION.PROJECT_DOCUMENT_EDIT_PROPERTIES, null);
      vm.secProjectMultipleDocumentsDownload = AppService.isOperationPermitted(
        AppConstants.OPERATION.PROJECT_MULTIPLE_DOCUMENTS_DOWNLOAD, null);
      if (AppService.getLoggedUser().userGroup.name ===
        AppConstants.GROUP.DIR) {
        vm.secProjectMultipleDocumentUpload = false;
      }
    }
    /***********************************************************************
     * Functions.
     **********************************************************************/
    function getDocumentFiles(id) {
      var timer = setTimeout(function () {
        AppService.setPaneShown(true);
      }, 500);
      DocumentService.getDocumentListProject(id).success(
        function (data) {
          clearTimeout(timer);
          AppService.setPaneShown(false);
          vm.documents = data;
          for (var i = 0; i < vm.documents.length; i++) {
            vm.documents[i].checkbox = false;
          }

          vm.tableParams = new NgTableParams({
            page: 1,
            count: 10,
            sorting: {
              title: 'asc' // initial sorting
            }
          }, {
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
                  filteredData = $filter('customDocumentDateFilter')(
                    vm.documents, filters.createdOn, true);
                  filteredCreatedOn = true;
                  createdOneFilterValue = filters.createdOn;
                  delete filters.createdOn;
                }
                if (filters.lastModifiedOn) {
                  filteredData = $filter('customDocumentDateFilter')(
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
              var orderedData = params.sorting() ? $filter(
                'orderBy')(filteredData,
                params.orderBy()) : filteredData;
              // when -1 value, show all data
              if (params.count() === -1) {
                $defer.resolve(orderedData);
              } else {
                $defer.resolve(
                  orderedData.slice((params.page() - 1) * params.count(),
                    params.page() * params.count()));
              }
              // count filtered results
              params.total(resultsCount);
            }
          });
        }).error(function (response, status) {});
    }

    function showActiveOrInactive(state) {
      return state ? 'Ja' : 'Nein';
    }

    function mainCheckboxFunctionality() {
      for (var i = 0; i < vm.documents.length; i++) {
        vm.documents[i].checkbox = vm.mainCheckbox;
      }
      vm.multipleFilesDownload = vm.mainCheckbox;
    }

    function seperateCheckboxFunctionality() {
      var documentsSelected = 0;
      for (var i = 0; i < vm.documents.length; i++) {
        if (vm.documents[i].checkbox) {
          documentsSelected++;
        }
      }
      vm.multipleFilesDownload = (documentsSelected >= 2);
    }

    function seperateDocumentDownload(documentId) {
      DocumentService.downloadDocument(documentId).success(
        function (response, status, headers) {
          var fileName = headers()[CONTENT_DISPOSITION]
            .split(';')[1].trim().split('=')[1];
          // Remove quotes added from the DocumentResource in order to enable filenames with special characters like comma
          fileName = fileName.substring(1, fileName.length - 1);
          var blob = new Blob([response], {
            type: "application/octet-stream"
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

    function uploadError(file, message, flow) {
      // Pass error to documentVieController
      var response = JSON.parse(message);
      flow.cancel();
      $state.go('documentView', {
        uploadError: response[0].message,
        id: $stateParams.id
      }, {
        reload: true
      });
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
              DocumentService.uploadSelectedDocument(
                  this.selectedDocument.id, uploadedFileId, true, true)
                .success(function (data) {
                  handleReload($stateParams.errorReturned);
                }).error(function (response, status) {
                  $state.go('documentView', {
                    uploadError: response[0].message,
                    id: $stateParams.id
                  }, {
                    reload: true
                  });
                });
            } else {
              DocumentService.uploadSelectedDocument(
                  this.selectedDocument.id, uploadedFileId, false, true)
                .success(function (data) {
                  handleReload($stateParams.errorReturned);
                }).error(function (response, status) {
                  $state.go('documentView', {
                    uploadError: response[0].message,
                    id: $stateParams.id
                  }, {
                    reload: true
                  });
                });
            }
          } else {
            DocumentService.uploadSelectedDocument(
                this.selectedDocument.id, uploadedFileId, true, true)
              .success(function (data) {
                handleReload($stateParams.errorReturned);
              }).error(function (response, status) {
                $state.go('documentView', {
                  uploadError: response[0].message,
                  id: $stateParams.id
                }, {
                  reload: true
                });
              });
          }
        }
      }
      flow.cancel();
    }

    /** Function to handle reload in case of possible previous errors */
    function handleReload(errorReturned) {
      if (errorReturned) {
        $state.go('documentView', {
          uploadError: null
        });
      } else {
        $state.reload();
      }
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
          handleReload($stateParams.errorReturned);
        }).error(function (response, status) {
          $state.go('documentView', {
            uploadError: response[0].message,
            id: $stateParams.id
          }, {
            reload: true
          });
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
        $state.go('documentView', {
          uploadError: 'invalid_file_type',
          id: $stateParams.id
        }, {
          reload: true
        });
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
        DocumentService
          .getDocumentVersionsByFilename(flow, $stateParams.id)
          .success(function (data) {
            for (var i = 0; i < flow.files.length; i++) {
              var documentUploadForm = {
                filename: flow.files[i].name,
                versioningAllowed: false,
                versionDocument: false,
                projectDocument: true,
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
            vm.permittedUploadDocumentsNames = getPermittedDocumentsForUpload(
              vm.uploadedDocuments, flow, $stateParams.id);
          }).error(function (response, status) {});
      } else {
        flow.cancel();
        $state.go('documentView', {
          uploadError: 'invalid_file_type',
          id: $stateParams.id
        }, {
          reload: true
        });
      }
    }

    function getPermittedDocumentsForUpload(uploadedDocuments, flow, folderId) {
      var documents = [];
      DocumentService.getPermittedDocumentsForUpload(flow, folderId,
        true).success(
        function (data) {
          for (var i = 0; i < uploadedDocuments.length; i++) {
            for (var j = 0; j < data.length; j++) {
              var filename = removeFileExtension(uploadedDocuments[i].filename);
              if (filename === data[j]) {
                documents.push(uploadedDocuments[i]);
                break;
              }
            }
          }
        }).error(function (response, status) {});
      uploadMultipleDocumentsDialog(documents, flow);
      return documents;
    }

    function replaceDocumentConfirmation(flow) {
      $uibModal
        .open({
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
            type: "application/octet-stream"
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
        }).error(function (response, status) {

        });
      vm.selectedDocumentIds = [];
    }

    // this function handles the editing/viewing of the document properties depending on the boolean value of editValue
    function editDocumentProperties(selectedDocument, editValue) {
      $uibModal
        .open({
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
            projectPart: true
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

      /* IE and Edge do not accept Blob Url in an <object> element . (Access Denied error)
       * In addition, IE 11 does not support to render and print pdfs through an IFrame and needs an <object> or <embed> element instead
       https://stackoverflow.com/questions/24007073/open-links-made-by-createobjecturl-in-ie11
       https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/8474657/
       Errors: SEC7134
       Firefox does not allow to call print
       Error: Permission denied to access property "print" on cross-origin object
       https://stackoverflow.com/questions/33254679/print-pdf-in-firefox
       https://bugzilla.mozilla.org/show_bug.cgi?id=911444
       */
      DocumentService.printDocument(documentId).success(
        function (response, status, headers) {
          if (headers()[CONTENT_DISPOSITION]) {
            var fileName = headers()[CONTENT_DISPOSITION]
              .split(';')[1].trim().split('=')[1];
            // Remove quotes added from the DocumentResource in order to enable filenames with special characters like comma
            fileName = fileName.substring(1, fileName.length - 1);
            var blob = new Blob([response], {
              type: "application/pdf"
            });
            var objectURL = URL.createObjectURL(blob);

            // TODO maintain until customer response for the browser compatibility issues
            // Works on crhome partially on firefox
            //						var objFra = document.getElementById('print-iframe');
            //						objFra.setAttribute('src', objectURL);
            //						setTimeout(function(){
            //					        objFra.contentWindow.focus();
            //					        objFra.contentWindow.print();
            //					    },2000);
            //
            //
            //						// Works on chrome
            //					    const iframe = document.createElement('iframe');
            //					    iframe.style.display = 'none';
            //					    iframe.src = objectURL;
            //					    document.body.appendChild(iframe);
            //
            //
            //					    // Works on Chrome
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

    function printDocumentHidden(documentId) {
      DocumentService.printDocument(documentId)
        .success(function (data) {})
        .error(function (response, status) {});
    }

    function removeFileExtension(filename) {
      if (filename.indexOf(".") >= 0) {
        filename = filename.substring(0, filename.lastIndexOf("."));
      }
      return filename;
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
