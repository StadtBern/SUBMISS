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
 * @name projectView.controller.js
 * @description ProjectViewController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document")
    .controller("DocumentEditController", DocumentEditController);

  /** @ngInject */
  function DocumentEditController($rootScope, $scope, $locale, $state,
    $stateParams, editMode,
    SubmissionService, $uibModal, NgTableParams, $uibModalInstance,
    DocumentService, document, UsersService,
    $filter, QFormJSRValidation, AppService, AppConstants, projectPart) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.documentEditForm = {};
    vm.document = document;
    vm.editMode = editMode;
    vm.projectPart = projectPart;
    vm.privateDocumentFirstValue = document.privateDocument;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.closeWindow = closeWindow;
    vm.openCreateDate = openCreateDate;
    vm.openChangeDate = openChangeDate;
    vm.save = save;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      loadDocumentDirectorate(vm.document.id);
      getUser();
    }
    /***********************************************************************
     * Functions.
     **********************************************************************/
    /** this function handles the popUp window for the CreationDate */
    function openCreateDate() {
      vm.openCreateDate.opened = !vm.openCreateDate.opened;
    }

    /** this function handles the popUp window for the ChangeDate */
    function openChangeDate() {
      vm.openChangeDate.opened = !vm.openChangeDate.opened;
    }

    /** this function handles the warning message, if the user made some changes without saving them */
    function closeWindow(dirtyflag) {
      if (dirtyflag) {
        var closeSubmissionModal = $uibModal
          .open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie den Vorgang wirklich abbrechen? Nicht gespeicherte Informationen gehen dabei verloren </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="documentEditCtrl.closeWindowModal(\'ja\')">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'documentEditCtrl',
            backdrop: 'static',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.closeWindowModal = function (reason) {
                closeSubmissionModal.dismiss(reason);
              };
            }

          });
        return closeSubmissionModal.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            $uibModalInstance.close();
            $state.go($state.current, {}, {
              reload: true
            });
          } else {
            // Do something else here
            return false;
          }
          return null;
        });
      } else {
        $uibModalInstance.dismiss();
      }
      return null;
    }

    function getUser() {
      vm.loggedInUser = AppService.getLoggedUser();
    }

    /** this function saves the edited properties */
    function save(id, title, privateDocument, dirtyflag) {
      // we call the service below only if something has been updated,else
      // there is no need to
      if (dirtyflag) {
        vm.documentEditForm.id = id;
        vm.documentEditForm.title = title;
        vm.documentEditForm.privateDocument = privateDocument;
        vm.documentEditForm.privateDocumentSetChanged = false;
        vm.documentEditForm.privateDocumentSetChanged =
          (vm.privateDocumentFirstValue !== privateDocument &&
            privateDocument === true);
        vm.documentEditForm.lastModifiedOn = vm.document.lastModifiedOn;

        DocumentService.updateDocumentProperties(vm.documentEditForm)
          .success(function (data) {
            $uibModalInstance.close();
            $state.reload();
          }).error(function (response, status) {
            handleValidationErrors(response, status);
          });
      } else {
        $uibModalInstance.close();
      }
    }

    /** this function handles the validation errors */
    function handleValidationErrors(response, status) {
      if (status === 400 || status === 409) { // Validation errors.
        QFormJSRValidation.markErrors($scope,
          $scope.documentEditCtrl.documentEdit, response);
      }
    }

    function loadDocumentDirectorate(documentId) {
      DocumentService.loadDocumentDirectorate(document.id)
        .success(function (data) {
          vm.documentDirectorate = data;
        });
    }
  }
})();
