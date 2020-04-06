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
 * @name generateCertificateCtrl.controller.js
 * @description GenerateCertificateController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document")
    .controller("GenerateCertificateController", GenerateCertificateController);

  /** @ngInject */
  function GenerateCertificateController($scope, QFormJSRValidation,
    $uibModalInstance, DocumentService, CompanyService, $locale,
    $stateParams, $state, AppService, template, createAndPrintDocument) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var CONTENT_DISPOSITION = 'content-disposition';
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.template = template;
    vm.createAndPrintDocument = createAndPrintDocument;
    // Set default value to radio button.
    vm.template.deptAmountAction = 'noActionDeptAmount';
    vm.template.deptAmounts = null;
    vm.nullString = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.requestCertificate = requestCertificate;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      calculateExpirationDate($stateParams.id);
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    // generate and download company certificate
    function requestCertificate() {
      if (vm.template.deptAmounts === "" || vm.template.deptAmounts === undefined) {
        vm.template.deptAmounts = null;
      }
      //We have to seperate text "null" from content null because in validation have the same value.
      if (vm.template.deptAmounts === "null") {
        vm.nullFromUser = true;
      }

      DocumentService.validateCertificate(vm.template.deptAmounts, vm.nullFromUser)
        .success(function (response, status, headers) {
          vm.template.templateShortCode = 'FT02';
          AppService.setPaneShown(true);
          if (vm.createAndPrintDocument) {
            downloadAndPrintCertificate(vm.template);
          } else {
            downloadCertificate(vm.template);
          }
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.generateCertificateCtrl.requestForm, response);
          }
        });
    }

    function downloadCertificate(template) {
      DocumentService.downloadCompanyDocument(template)
        .success(function (response, status, headers) {
          AppService.setPaneShown(false);
          var fileName = headers()[CONTENT_DISPOSITION]
            .split(';')[1].trim().split('=')[1];
          fileName = fileName.substring(1, fileName.length - 1);
          var blob = new Blob([response], {
            type: "application/octet-stream"
          });
          // For Internet Explorer 11 only
          if (window.navigator &&
            window.navigator.msSaveOrOpenBlob) {
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
          $uibModalInstance.close();
          $state.reload();
        }).error(function (response, status) {
          AppService.setPaneShown(false);
        });
    }

    function downloadAndPrintCertificate(template) {
      DocumentService.downloadAndPrintCompanyDocument(template)
        .success(function (response, status, headers) {
          // download document
          downloadCertificate(template);
          // print document
          if (headers()[CONTENT_DISPOSITION]) {
            var fileName = headers()[CONTENT_DISPOSITION]
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

    function calculateExpirationDate(id) {
      CompanyService.calculateExpirationDate(id).success(function (data) {
        vm.template.expirationDate = data;
      }).error(function (response, status) {});
    }
  }
})();
