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
 * @ngdoc service
 * @name award.service.js
 * @description AwardService service
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document")
    .factory("DocumentService", DocumentService);

  /** @ngInject */
  function DocumentService($http, AppConstants) {

    var APPLICATION_OCTETSTREAM = 'application/octet-stream';

    return {
      getDocumentListProject: function (id) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/getDocumentListProject/' + id);
      },
      getDocumentListCompany: function (id) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/getDocumentListCompany/' + id);
      },
      createDocument: function (templateForm) {
        return $http.post(
          AppConstants.URLS.RESOURCE_PROVIDER + '/document/createDocument',
          templateForm);

      },
      downloadDocument: function (id) {
        return $http.get(
          AppConstants.URLS.RESOURCE_PROVIDER + '/document/downloadDocument/' +
          id, {
            responseType: 'arraybuffer',
            headers: {
              'Accept': APPLICATION_OCTETSTREAM
            }
          });
      },
      downloadCompanyDocument: function (templateForm) {
        var urlParams = templateForm.templateShortCode;
        var existsExpiration = false;
        var deptAmounts = false;
        if (templateForm.id !== undefined) {
          urlParams = urlParams + '?id=' + templateForm.id;
        }
        if (templateForm.expirationDate !== undefined &&
          templateForm.expirationDate !== "") {
          urlParams = urlParams + '&expirationDate=' +
            templateForm.expirationDate;
          existsExpiration = true;
        }
        if (templateForm.deptAmounts !== undefined && templateForm.deptAmounts !==
          "" && templateForm.deptAmounts !== null) {
          if (existsExpiration) {
            urlParams = urlParams + '&';
          }
          urlParams = urlParams + '&deptAmounts=' + templateForm.deptAmounts;
          deptAmounts = true;
        }
        if (templateForm.deptAmountAction !== undefined &&
          templateForm.deptAmountAction !== "") {
          if (deptAmounts) {
            urlParams = urlParams + '&';
          }
          urlParams = urlParams + '&deptAmountAction=' +
            templateForm.deptAmountAction;
        }
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/downloadCompanyDocument/' + urlParams, {
            responseType: 'arraybuffer',
            headers: {
              'Accept': APPLICATION_OCTETSTREAM
            }
          });
      },
      downloadAndPrintCompanyDocument: function (templateForm) {
        var urlParams = templateForm.templateShortCode;
        var existsExpiration = false;
        var deptAmounts = false;
        if (templateForm.id !== undefined) {
          urlParams = urlParams + '?id=' + templateForm.id;
        }
        if (templateForm.expirationDate !== undefined &&
          templateForm.expirationDate !== "") {
          urlParams = urlParams + '&expirationDate=' +
            templateForm.expirationDate;
          existsExpiration = true;
        }
        if (templateForm.deptAmounts !== undefined && templateForm.deptAmounts !==
          "" && templateForm.deptAmounts !== null) {
          if (existsExpiration) {
            urlParams = urlParams + '&';
          }
          urlParams = urlParams + '&deptAmounts=' + templateForm.deptAmounts;
          deptAmounts = true;
        }
        if (templateForm.deptAmountAction !== undefined &&
          templateForm.deptAmountAction !== "") {
          if (deptAmounts) {
            urlParams = urlParams + '&';
          }
          urlParams = urlParams + '&deptAmountAction=' +
            templateForm.deptAmountAction;
        }
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/downloadCompanyDocument/print/' + urlParams, {
            responseType: 'arraybuffer',
            headers: {
              'Accept': APPLICATION_OCTETSTREAM
            }
          });
      },
      haveAwardStatusesBeenClosed: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/haveAwardStatusesBeenClosed/' + submissionId);
      },
      documentVersionExists: function (documentForm) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/documentVersionExists/', documentForm);
      },
      uploadSelectedDocument: function (versionId, uploadedFileId,
        replaceDocument, isProjectDocument) {
        return $http.post(
          AppConstants.URLS.RESOURCE_PROVIDER + '/document/uploadDocument/' +
          versionId +
          '/' + uploadedFileId + '/' + replaceDocument + '/' +
          isProjectDocument);
      },
      hasCommissionProcurementProposalBeenClosed: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/hasCommissionProcurementProposalBeenClosed/' +
          submissionId);
      },
      hasCommissionProcurementDecisionBeenClosed: function (submissionId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/hasCommissionProcurementDecisionBeenClosed/' +
          submissionId);
      },
      deleteSelectedDocument: function (templateId, versionId, reason) {
        if (reason === undefined) {
          return $http.post(
            AppConstants.URLS.RESOURCE_PROVIDER + '/document/deleteDocument/' +
            templateId + '/' + versionId + '/');
        } else {
          return $http.post(
            AppConstants.URLS.RESOURCE_PROVIDER + '/document/deleteDocument/' +
            templateId + '/' + versionId + '/' + reason);
        }
      },
      downloadMultipleDocuments: function (selectedDocumentIds) {
        // append list of selected documents id in the url
        var ids = '';
        for (var i = 0; i < selectedDocumentIds.length; i++) {
          ids = ids + 'id=' + selectedDocumentIds[i];
          if (selectedDocumentIds.length !== i + 1) {
            ids = ids + '&';
          }
        }
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/downloadMultipleDocuments?' + ids, {
            responseType: 'arraybuffer',
            headers: {
              'Accept': APPLICATION_OCTETSTREAM
            }
          });

      },
      updateDocumentProperties: function (documentEditForm) {
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/updateDocumentProperties/', documentEditForm);
      },
      loadDocumentDirectorate: function (documentId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/loadDocumentDirectorate/' + documentId);
      },
      downloadProjectDocument: function (templateForm) {
        var urlParams = templateForm.id + '/' + templateForm.templateId + '?';
        var existsExpiration = false;
        if (templateForm.expirationDate !== undefined &&
          templateForm.expirationDate !== "") {
          urlParams = urlParams + 'expirationDate=' +
            templateForm.expirationDate;
          existsExpiration = true;
        }
        if (templateForm.deptAmounts !== undefined && templateForm.deptAmounts !==
          "") {
          if (existsExpiration) {
            urlParams = urlParams + '&';
          }
          urlParams = urlParams + 'deptAmounts=' + templateForm.deptAmounts;
        }
        return $http.get(
          AppConstants.URLS.RESOURCE_PROVIDER + '/document/downloadProjectDocument/' +
          urlParams, {
            responseType: 'arraybuffer',
            headers: {
              'Accept': APPLICATION_OCTETSTREAM
            }
          });
      },
      downloadAndPrintProjectDocument: function (templateForm) {
        var urlParams = templateForm.id + '/' + templateForm.templateId + '?';
        var existsExpiration = false;
        if (templateForm.expirationDate !== undefined &&
          templateForm.expirationDate !== "") {
          urlParams = urlParams + 'expirationDate=' +
            templateForm.expirationDate;
          existsExpiration = true;
        }
        if (templateForm.deptAmounts !== undefined && templateForm.deptAmounts !==
          "") {
          if (existsExpiration) {
            urlParams = urlParams + '&';
          }
          urlParams = urlParams + 'deptAmounts=' + templateForm.deptAmounts;
        }
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/downloadProjectDocument/print/' + urlParams, {
            responseType: 'arraybuffer',
            headers: {
              'Accept': APPLICATION_OCTETSTREAM
            }
          });
      },
      isDeletionReasonMandatory: function (templateId) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/isDeletionReasonMandatory/' + templateId);
      },
      getDocumentVersionsByFilename: function (flow, folderId) {
        var fileNames = [];
        for (var i in flow.files) {
          fileNames.push(flow.files[i].name);
        }
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/getDocumentVersionsByFilename/' + folderId, fileNames);
      },
      uploadMultipleDocuments: function (uploadedDocuments) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/uploadMultipleDocuments', uploadedDocuments);
      },
      printDocument: function (id) {
        return $http.get(
          AppConstants.URLS.RESOURCE_PROVIDER + '/document/printDocument/' + id, {
            responseType: 'arraybuffer',
            headers: {
              'Accept': APPLICATION_OCTETSTREAM
            }
          });
      },
      downloadPDFDocument: function (id) {
        return $http.get(
          AppConstants.URLS.RESOURCE_PROVIDER + '/document/downloadPDFDocument/' +
          id, {
            responseType: 'arraybuffer',
            headers: {
              'Accept': APPLICATION_OCTETSTREAM
            }
          });
      },
      generateSearchedCompaniesDocument: function (shortCode, searchForm,
        page, pageItems, sortColumn, sortType) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/generateSearchedCompaniesDocument/' +
          shortCode + '/' + page + '/' + pageItems + '/' +
          sortColumn + '/' + sortType, searchForm, {
            responseType: 'arraybuffer',
            headers: {
              'Accept': APPLICATION_OCTETSTREAM
            }
          });
      },
      generateCompaniesListDocument: function (shortCode) {
        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/generateCompaniesListDocument/' + shortCode, {
            responseType: 'arraybuffer',
            headers: {
              'Accept': APPLICATION_OCTETSTREAM
            }
          });
      },
      emptyFirstSignature: function () {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/emptyFirstSignature');
      },
      validateCertificate: function (deptamounts, nullFromUser) {

        return $http.get(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/validateCertificate/' + deptamounts + "/" +
          nullFromUser);
      },
      getPermittedDocumentsForUpload: function (flow, folderId, isProjectPart) {
        var fileNames = [];
        for (var i in flow.files) {
          fileNames.push(flow.files[i].name);
        }
        return $http.put(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/getPermittedDocumentsForUpload/' + folderId + '/' +
          isProjectPart, fileNames);
      },
      checkContractDocument: function (submissionId) {
        return $http.post(AppConstants.URLS.RESOURCE_PROVIDER +
          '/document/contract/' + submissionId);
      }
    };
  }
})();
