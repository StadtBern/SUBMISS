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
    .module("submiss.document").controller("DocumentViewController",
      DocumentViewController);

  /** @ngInject */
  function DocumentViewController($scope, $rootScope, DocumentService,
    AppService, $transitions,
    $state, $stateParams, SubmissionService, AppConstants, $uibModal,
    QFormJSRValidation, UsersService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var DOCUMENTLIST = 'documentView.documentList';
    var LEGALHEARING = 'documentView.legalHearing';
    var SUBMITTENTSEXCLUSION = 'documentView.submittentsExclusion';
    var CONTENT_DISPOSITION = 'content-disposition';
    var SUBMISSIONCANCEL = 'documentView.submissionCancel';
    var i = 0;
    var dokumentenlisteStatus = [260, 270];
    var verfugungenStufe1Status = [60];
    var beKoAntragStatus = [200, 210, 220];
    var beKoBeschlussStatus = [230, 240];
    var verfugungenStatus = [250];
    var zu_absageStatus = [210];
    var verfahrensabbruchStatus = [290];
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.templateForm = {};
    vm.templates = [];
    vm.submittentList = [];
    vm.chosenTemplate = {};
    vm.submission = null;
    vm.templateForm.id = $stateParams.id;
    vm.templateForm.createVersion = false;
    vm.secCommissionProcurementProposalView = false;
    vm.status = AppConstants.STATUS;
    vm.currentStatus = null;
    vm.hasStatusCancelled = false;
    vm.hasStatusComProcDecClosed = false;
    vm.hasStatusComProcDecStarted = false;
    vm.hasStatusManualAwardCompleted = false;
    vm.hasStatusStatusSuitAudComplS = false;
    vm.awardStatusesClosed = false;
    vm.commissionProcurementProposalClosed = false;
    vm.templateForm.tenantId = null;
    vm.uploadError = $stateParams.uploadError;
    vm.secTenderCancelView = false;
    vm.secIsLegalView = false;
    vm.secAwardInfoView = false;
    vm.secAwardInfoFirstLevelView = false;
    vm.status = AppConstants.STATUS;
    vm.invalidNavigation = false;
    vm.signatures = [];
    vm.departments = [];
    vm.createAndPrintDocument = false;
    vm.submissionCancelTabActive = false;
    vm.legalHearingTabActive = false;
    vm.showError = false;
    vm.legalHearingErrorMessage = false;
    vm.anonymousOfferDocTemplate = {};
    vm.group = AppConstants.GROUP;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.getTemplates = getTemplates;
    vm.createDocument = createDocument;
    vm.readSubmission = readSubmission;
    vm.isCommissionProcurementProposalTabVisible = isCommissionProcurementProposalTabVisible;
    vm.isCPDecisionTabVisible = isCPDecisionTabVisible;
    vm.isAwardInfoTabVisible = isAwardInfoTabVisible;
    vm.isAwardInfoTabNegotiatedVisible = isAwardInfoTabNegotiatedVisible;
    vm.isSubmissionCancelTabVisible = isSubmissionCancelTabVisible;
    vm.isAwardInfoFirstLevelTabVisible = isAwardInfoFirstLevelTabVisible;
    vm.readStatusOfSubmission = readStatusOfSubmission;
    vm.isLegal = isLegal;
    vm.getObjectInfo = getObjectInfo;
    vm.submissionCancelNavigation = submissionCancelNavigation;
    vm.printDocuments = printDocuments;
    vm.getStatusSubTab = getStatusSubTab;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      SubmissionService.loadDocumentArea($stateParams.id)
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            vm.getTemplates();
            vm.readSubmission($stateParams.id);
            haveAwardStatusesBeenClosed($stateParams.id);
            hasCommissionProcurementProposalBeenClosed($stateParams.id);
            vm.readStatusOfSubmission($stateParams.id);
            readUserDepartments();
            loadSignatures();
            // we need to check if the status of the submission was ever set to certain statuses
            // in order to identify if certain tabs should be visible
            hasSubmissionStatusCancelled($stateParams.id);
            hasSubmissionStatusComProcDecClosed($stateParams.id);
            hasSubmissionStatusComProcDecStarted($stateParams.id);
            hasSubmissionStatusManualAwardCompleted($stateParams.id);
            hasSubmissionStatusSuitAudComplS($stateParams.id);
            //default child state
            if ($state.current.name === "documentView" ||
              $state.current.name === DOCUMENTLIST) {
              $state.go(DOCUMENTLIST, {
                errorReturned: $stateParams.uploadError
              });
            }
            legalHearingOrSubmissionCancelActive();
          }
        });
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id).success(
        function (data) {
          vm.currentStatus = data;
        });
    }

    function getStatusSubTab(status) {
      let statusSubTab = -1;
      if (dokumentenlisteStatus.includes(status)) {
        statusSubTab = 0;
      } else if (verfugungenStufe1Status.includes(status)) {
        statusSubTab = 1;
      } else if (vm.submission && (!(vm.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE || vm.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) ||
          (vm.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE || vm.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) && vm.submission.aboveThreshold) &&
        beKoAntragStatus.includes(status)) {
        statusSubTab = 2;
      } else if (beKoBeschlussStatus.includes(status)) {
        statusSubTab = 3;
      } else if (verfugungenStatus.includes(status) && vm.submission.aboveThreshold !== true) {
        statusSubTab = 4;
      } else if (vm.submission && (vm.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE || vm.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) &&
        (zu_absageStatus.includes(status) || (verfugungenStatus.includes(status) && vm.submission.aboveThreshold === true))) {
        statusSubTab = 5;
      } else if (verfahrensabbruchStatus.includes(status)) {
        statusSubTab = 6;
      }
      return statusSubTab;
    }

    function readUserDepartments(id) {
      UsersService.getUserDepartments().success(function (data) {
        vm.departments = data;
      });
    }

    function loadSignatures() {
      SubmissionService.loadSignatures($stateParams.id)
        .success(function (data) {
          if (data !== '') {
            vm.signatures = data.signatureProcessTypeEntitled;
          }
        });
    }

    // this function gets the Document templates for the drop-down menu
    function getTemplates() {
      SubmissionService.getProjectTemplates($stateParams.id).success(
        function (data) {
          // Iterate through the returned templates to remove the anonymous offer document templates,
          // if present, as these documents are created automatically when creating the offer documents.
          // Also, keep the anonymous offer document template in a separate variable (if found).
          for (i in data) {
            if (data[i].shortCode === AppConstants.A_OFFERRTOFFNUNGSPROTOKOLL ||
              data[i].shortCode === AppConstants.A_OFFERRTOFFNUNGSPROTOKOLL_DL_WW) {
              vm.anonymousOfferDocTemplate = data[i];
              data.splice(i, 1);
            }
          }
          vm.templates = data;
          vm.chosenTemplate = vm.templates[0];
        }).error(function (response, status) {

      });
    }

    // this function creates the document selected from the drop-down menu
    function createDocument() {
      // If error messages regarding the legal hearing are visible, remove them.
      if (vm.legalHearingErrorMessage) {
        vm.legalHearingErrorMessage = false;
      }
      if (vm.showError) {
        vm.showError = false;
      }

      if (vm.chosenTemplate !== null) {
        if (AppService.getIsDirty()) {
          AppService.setIsDirty(false);
          var transitionModal = AppService.transitionModal();
          return transitionModal.result.then(function (response) {
            if (response) {
              AppService.setIsDirty(false);
            }
            return false;
          });
        } else {
          AppService.setIsDirty(false);
          vm.templateForm.templateId = vm.chosenTemplate.masterListValueId.id;
          vm.templateForm.tenantId = vm.chosenTemplate.tenant.id;
          vm.templateForm.templateName = vm.chosenTemplate.value1;
          if (vm.departments.length === 1) {
            vm.templateForm.department = vm.departments[0];
          }
          if (vm.signatures != null &&
            vm.signatures.length === 1) {
            vm.templateForm.firstSignature = vm.signatures[0];
          }
          // Check if the user has requested or not a new version.
          // set these values to false. It shows that the template is derived from user input.
          vm.templateForm.generateProofTemplate = false;
          vm.templateForm.isCompanyCertificate = false;
          vm.templateForm.projectDocument = true;

          // Check if the document being created is the OFFERRTOFFNUNGSPROTOKOLL or OFFERRTOFFNUNGSPROTOKOLL_DL_WW,
          // in order to create a template form for the anonymous offer document as well, as the two documents are
          // created together.
          if (vm.chosenTemplate.shortCode === AppConstants.OFFERRTOFFNUNGSPROTOKOLL ||
            vm.chosenTemplate.shortCode === AppConstants.OFFERRTOFFNUNGSPROTOKOLL_DL_WW) {
            createAnonymOfferTemplateForm();
          }

          if (vm.chosenTemplate.value1 ===
            AppConstants.SUBMITTENTENLISTE_POSTLISTE ||
            vm.chosenTemplate.value1 ===
            AppConstants.SUBMITTENTENLISTE_ALS_ETIKETTEN) {
            AppService.setPaneShown(true);
            if (vm.createAndPrintDocument) {
              downloadAndPrintProjectDocument(vm.templateForm);
            } else {
              downloadProjectDocument(vm.templateForm);
            }
          } else if (vm.chosenTemplate.shortCode === AppConstants.VERTRAG) {
            DocumentService.checkContractDocument(vm.submission.id)
              .success(function (data) {
                $uibModal
                  .open({
                    templateUrl: 'app/document/contract/generateContract.html',
                    controller: 'GenerateContractController',
                    controllerAs: 'generateContractCtrl',
                    backdrop: 'static',
                    keyboard: false,
                    resolve: {
                      template: function () {
                        return vm.templateForm;
                      },
                      submission: function () {
                        return vm.submission;
                      },
                      createAndPrintDocument: function () {
                        return vm.createAndPrintDocument;
                      }
                    }
                  });
              }).error(function (response, status) {
                if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST) {
                  QFormJSRValidation.markErrors($scope,
                    $scope.documentViewCtrl.template, response);
                }
              });
          } else if ((vm.chosenTemplate.shortCode === AppConstants.BEKO_ANTRAG ||
              vm.chosenTemplate.shortCode === AppConstants.BEKO_BESCHLUSS) &&
            vm.departments.length > 1) {
            $uibModal
              .open({
                templateUrl: 'app/document/signature/selectSignOrDep.html',
                controller: 'SelectSignOrDepController',
                controllerAs: 'selectSignOrDepCtrl',
                backdrop: 'static',
                keyboard: false,
                resolve: {
                  template: function () {
                    return vm.templateForm;
                  },
                  submission: function () {
                    return vm.submission;
                  },
                  userDepartments: function () {
                    return vm.departments;
                  },
                  isBeko: function () {
                    return true;
                  },
                  signatures: function () {
                    return null;
                  },
                  createAndPrintDocument: function () {
                    return vm.createAndPrintDocument;
                  }
                }
              });
          } else if (vm.chosenTemplate.shortCode ===
            AppConstants.RECHTLICHES_GEHOER && $state.current.name ===
            LEGALHEARING) {
            vm.templateForm.legalHearingType = "CANCELATION";
            if (vm.templateForm.createVersion) {
              AppService.setPaneShown(true);
              DocumentService.createDocument(vm.templateForm)
                .success(function (data) {
                  $state.go(DOCUMENTLIST, {}, {
                    reload: true
                  });
                  AppService.setPaneShown(false);
                  if (vm.createAndPrintDocument) {
                    printDocuments(data);
                  }
                }).error(function (response, status) {
                  AppService.setPaneShown(true);
                });
            } else {
              // In case the user has not requested a new version and a
              // version already exists for this selection in the database,
              // ask user for confirmation to overwrite the existing version
              DocumentService
                .documentVersionExists(vm.templateForm)
                .success(function (data) {
                  if (data === 'versionExists') {
                    overwriteDocumentConfirmation(false);
                  } else {
                    AppService.setPaneShown(true);
                    DocumentService.createDocument(
                      vm.templateForm).success(
                      function (data) {
                        $state.go(DOCUMENTLIST, {}, {
                          reload: true
                        });
                        AppService.setPaneShown(false);
                        if (vm.createAndPrintDocument) {
                          printDocuments(data);
                        }
                      }).error(
                      function (response, status) {
                        AppService.setPaneShown(false);
                        handleValidationErrors(response, status);
                      });
                  }
                }).error(function (response, status) {
                  AppService.setPaneShown(true);
                });
            }
          } else if ((vm.chosenTemplate.shortCode ===
              AppConstants.VERFAHRENSABBRUCH || vm.chosenTemplate.shortCode ===
              "PT25" || vm.chosenTemplate.shortCode === "PT24" ||
              vm.chosenTemplate.shortCode === "PT22" ||
              vm.chosenTemplate.shortCode === "PT23") && vm.signatures.length >
            1) {
            $uibModal
              .open({
                templateUrl: 'app/document/signature/selectSignOrDep.html',
                controller: 'SelectSignOrDepController',
                controllerAs: 'selectSignOrDepCtrl',
                backdrop: 'static',
                keyboard: false,
                resolve: {
                  template: function () {
                    return vm.templateForm;
                  },
                  submission: function () {
                    return vm.submission;
                  },
                  userDepartments: function () {
                    return null;
                  },
                  isBeko: function () {
                    return false;
                  },
                  signatures: function () {
                    return vm.signatures;
                  },
                  createAndPrintDocument: function () {
                    return vm.createAndPrintDocument;
                  }
                }
              });
          } else if (vm.chosenTemplate.shortCode ===
            AppConstants.RECHTLICHES_GEHOER && $state.current.name ===
            SUBMITTENTSEXCLUSION) {
            vm.submittentList = AppService.getSubmittentList();
            vm.templateForm.legalHearingType = "EXCLUSION";
            vm.templateForm.legalHearingExclusion = [];
            for (var i = 0; i <= vm.submittentList.length - 1; i++) {
              if (!angular.isUndefined(vm.submittentList[i].checked) &&
                vm.submittentList[i].checked &&
                !vm.submittentList[i].disable) {
                vm.templateForm.legalHearingExclusion.push(
                  vm.submittentList[i]);
              }
            }
            if (vm.submittentList.length >= 1) {
              if (vm.templateForm.createVersion) {
                AppService.setPaneShown(true);
                DocumentService.createDocument(vm.templateForm).success(
                  function (data) {
                    $state.go(DOCUMENTLIST, {}, {
                      reload: true
                    });
                    AppService.setPaneShown(false);
                    AppService.emptySubmittentList();
                    if (vm.createAndPrintDocument) {
                      printDocuments(data);
                    }
                  }).error(function (response, status) {
                  AppService.setPaneShown(true);
                });
              } else {
                // In case the user has not requested a new version and a
                // version already exists for this selection in the database,
                // ask user for confirmation to overwrite the existing version
                DocumentService
                  .documentVersionExists(vm.templateForm)
                  .success(function (data) {
                    if (data === 'versionExists') {
                      overwriteDocumentConfirmation(true);
                    } else {
                      AppService.setPaneShown(true);
                      DocumentService.createDocument(
                        vm.templateForm).success(
                        function (data) {
                          $state.go(DOCUMENTLIST, {}, {
                            reload: true
                          });
                          AppService.setPaneShown(false);
                          AppService.emptySubmittentList();
                          if (vm.createAndPrintDocument) {
                            printDocuments(data);
                          }
                        }).error(
                        function (response, status) {
                          handleValidationErrors(response, status);
                        });
                    }
                  }).error(function (response, status) {
                    AppService.setPaneShown(true);
                  });
              }
            } else {
              vm.showError = true;
            }
            AppService.emptySubmittentList();
          } else {
            AppService.emptySubmittentList();
            if (vm.chosenTemplate.shortCode ===
              AppConstants.RECHTLICHES_GEHOER) {
              vm.templateForm.legalHearingType = "CANCELATION";
            }
            if (vm.submission.legalHearingTerminate.length === 0 &&
              vm.chosenTemplate.shortCode ===
              AppConstants.RECHTLICHES_GEHOER) {
              vm.legalHearingErrorMessage = true;
            } else {
              if (vm.templateForm.createVersion) {
                vm.legalHearingErrorMessage = false;
                AppService.setPaneShown(true);
                DocumentService.createDocument(vm.templateForm)
                  .success(function (data) {
                    genericDocumentCreation(data);
                  }).error(function (response, status) {
                    AppService.setPaneShown(true);
                  });
              } else {
                // In case the user has not requested a new version and a
                // version already exists for this selection in the database,
                // ask user for confirmation to overwrite the existing version
                DocumentService
                  .documentVersionExists(vm.templateForm)
                  .success(function (data) {
                    if (data === 'versionExists') {
                      overwriteDocumentConfirmation(false);
                    } else {
                      AppService.setPaneShown(true);
                      DocumentService.createDocument(
                        vm.templateForm).success(
                        function (data) {
                          genericDocumentCreation(data);
                        }).error(
                        function (response, status) {
                          handleValidationErrors(response, status);
                        });
                    }
                  }).error(function (response, status) {
                    AppService.setPaneShown(true);
                  });
              }
            }
          }
        }
      } else {
        AppService.setPaneShown(true);
        vm.templateForm.department = vm.departments[1];
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

    /** Creates a template form for the anonymous offer document */
    function createAnonymOfferTemplateForm() {
      vm.anonymousOfferTemplateForm = {};
      vm.anonymousOfferTemplateForm.id = vm.templateForm.id; // where id is the submission id.
      vm.anonymousOfferTemplateForm.createVersion = vm.templateForm.createVersion;
      vm.anonymousOfferTemplateForm.templateId = vm.anonymousOfferDocTemplate.masterListValueId.id;
      vm.anonymousOfferTemplateForm.tenantId = vm.anonymousOfferDocTemplate.tenant.id;
      vm.anonymousOfferTemplateForm.templateName = vm.anonymousOfferDocTemplate.value1;
      if (vm.departments.length === 1) {
        vm.anonymousOfferTemplateForm.department = vm.departments[0];
      }
      if (vm.signatures != null &&
        vm.signatures.length === 1) {
        vm.anonymousOfferTemplateForm.firstSignature = vm.signatures[0];
      }
      vm.anonymousOfferTemplateForm.generateProofTemplate = false;
      vm.anonymousOfferTemplateForm.isCompanyCertificate = false;
      vm.anonymousOfferTemplateForm.projectDocument = true;
    }

    /**
     * Creates the anonymous offer document and handles the printing for both the anonymous offer
     * document and the offer document.
     *
     * @param {*} offerDocId the id of the created offer document
     */
    function createAnonymousOfferDocAndPrint(offerDocId) {
      DocumentService.createDocument(vm.anonymousOfferTemplateForm)
        .success(function (data) {
          AppService.setPaneShown(false);
          if (vm.legalHearingErrorMessage) {
            vm.legalHearingErrorMessage = false;
          }
          var docIds = [];
          // Add both ids to the docIds array (used for printing).
          docIds.push(offerDocId, data)
          if (vm.createAndPrintDocument) {
            printDocuments(docIds);
          }
          $state.go(DOCUMENTLIST, {}, {
            reload: true
          });
        });
    }

    /**
     * Implements a generic document creation functionality used multiple times in the code.
     *
     * @param {*} docId the id of the created document
     */
    function genericDocumentCreation(docId) {
      // Check if the anonymous offer template form has been created, which indicates the
      // need for the anonymous offer document creation.
      if (vm.anonymousOfferTemplateForm) {
        createAnonymousOfferDocAndPrint(docId);
      } else {
        AppService.setPaneShown(false);
        if (vm.legalHearingErrorMessage) {
          vm.legalHearingErrorMessage = false;
        }
        if (vm.createAndPrintDocument) {
          printDocuments(docId);
        }
        $state.go(DOCUMENTLIST, {}, {
          reload: true
        });
      }
    }

    /** Function to read submission by submission id */
    function readSubmission(id) {
      SubmissionService.readSubmission(id)
        .success(function (data) {
          vm.submission = data;
          vm.secCommissionProcurementProposalView = AppService.isOperationPermitted(
            AppConstants.OPERATION.COMMISSION_PROCUREMENT_PROPOSAL_VIEW, null);
          vm.secCommissionProcurementDecisionView = AppService.isOperationPermitted(
            AppConstants.OPERATION.COMMISSION_PROCUREMENT_DECISION_VIEW, null);
          vm.secTenderCancelView = AppService.isOperationPermitted(
            AppConstants.OPERATION.TENDER_CANCEL_VIEW, vm.submission.process);
          vm.secIsLegalView = AppService.isOperationPermitted(
            AppConstants.OPERATION.LEGAL_VIEW, null);
          vm.secAwardInfoView = AppService.isOperationPermitted(
            AppConstants.OPERATION.AWARD_INFO_VIEW, null);
          vm.secAwardInfoFirstLevelView = AppService.isOperationPermitted(
            AppConstants.OPERATION.AWARD_INFO_FIRST_LEVEL_VIEW,
            vm.submission.process);
        }).error(function (response, status) {});
    }

    /** Check if statuses award evaluation closed or manual award completed have been set before */
    function haveAwardStatusesBeenClosed(submissionId) {
      DocumentService.haveAwardStatusesBeenClosed(submissionId)
        .success(function (data) {
          vm.awardStatusesClosed = data;
        }).error(function (response, status) {});
    }

    /** Function to determine if the commission procurement proposal tab is visible */
    function isCommissionProcurementProposalTabVisible(currentStatus, process) {
      return ((process === AppConstants.PROCESS.INVITATION || process ===
        AppConstants.PROCESS.OPEN || process ===
        AppConstants.PROCESS.SELECTIVE ||
        ((process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE || process ===
            AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) &&
          vm.submission.aboveThreshold)) && (vm.awardStatusesClosed));
    }

    // check if the status of the submission was ever set to PROCEDURE_CANCELED
    function hasSubmissionStatusCancelled(id) {
      SubmissionService.hasSubmissionStatus(id,
          vm.status.PROCEDURE_CANCELED)
        .success(function (data) {
          vm.hasStatusCancelled = data;
        }).error(function (response, status) {

        });
    }

    // check if the status of the submission was ever set to COMMISSION_PROCUREMENT_DECISION_CLOSED
    function hasSubmissionStatusComProcDecClosed(id) {
      SubmissionService.hasSubmissionStatus(id,
          vm.status.COMMISSION_PROCUREMENT_DECISION_CLOSED)
        .success(function (data) {
          vm.hasStatusComProcDecClosed = data;
        }).error(function (response, status) {

        });
    }

    // check if the status of the submission was ever set to COMMISSION_PROCUREMENT_DECISION_STARTED
    function hasSubmissionStatusComProcDecStarted(id) {
      SubmissionService.hasSubmissionStatus(id,
          vm.status.COMMISSION_PROCUREMENT_DECISION_STARTED)
        .success(function (data) {
          vm.hasStatusComProcDecStarted = data;
        }).error(function (response, status) {

        });
    }

    // check if the status of the submission was ever set to MANUAL_AWARD_COMPLETED
    function hasSubmissionStatusManualAwardCompleted(id) {
      SubmissionService.hasSubmissionStatus(id,
          vm.status.MANUAL_AWARD_COMPLETED)
        .success(function (data) {
          vm.hasStatusManualAwardCompleted = data;
        }).error(function (response, status) {

        });
    }

    // check if the status of the submission was ever set to SUITABILITY_AUDIT_COMPLETED_S
    function hasSubmissionStatusSuitAudComplS(id) {
      SubmissionService.hasSubmissionStatus(id,
          vm.status.SUITABILITY_AUDIT_COMPLETED_S)
        .success(function (data) {
          vm.hasStatusStatusSuitAudComplS = data;
        }).error(function (response, status) {

        });
    }

    function overwriteDocumentConfirmation(refreshState) {
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
              AppService.setPaneShown(true);
              DocumentService.createDocument(vm.templateForm)
                .success(function (data) {
                  genericDocumentCreation(data);
                }).error(function (response, status) {});
            } else {
              if (refreshState) {
                $state.go(SUBMITTENTSEXCLUSION, {}, {
                  reload: true
                });
              }
              return false;
            }
            return null;
          });
    }

    function handleValidationErrors(response, status) {
      if (status === 400) { // Validation errors.
        QFormJSRValidation.markErrors($scope,
          $scope.documentViewCtrl.template, response);
      }
    }

    /** Check if the commission procurement proposal has been closed before */
    function hasCommissionProcurementProposalBeenClosed(submissionId) {
      DocumentService
        .hasCommissionProcurementProposalBeenClosed(submissionId)
        .success(function (data) {
          vm.commissionProcurementProposalClosed = data;
        }).error(function (response, status) {});
    }

    /** Function to determine if the commission procurement decision tab is visible */
    function isCPDecisionTabVisible(process) {
      return (vm.commissionProcurementProposalClosed && (process ===
        AppConstants.PROCESS.SELECTIVE ||
        process === AppConstants.PROCESS.OPEN || process ===
        AppConstants.PROCESS.INVITATION ||
        ((process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE ||
            AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION) &&
          vm.submission.aboveThreshold)));
    }

    /** Function to determine if the award info tab is visible */
    function isAwardInfoTabVisible() {
      // the tab is visible only for users with this right and
      // in Selective, Open, Invited Tender
      // if the status has ever been set to
      // COMMISSION_PROCUREMENT_DECISION_CLOSED
      return (vm.secAwardInfoView &&
        (vm.submission.process === AppConstants.PROCESS.SELECTIVE ||
          vm.submission.process === AppConstants.PROCESS.OPEN ||
          vm.submission.process === AppConstants.PROCESS.INVITATION) &&
        vm.hasStatusComProcDecClosed);
    }

    /** Function to determine if the award info tab for Negotiated Tender is visible */
    function isAwardInfoTabNegotiatedVisible() {
      // the tab is visible only for users with this right and
      // in Negotiated, NegotiatedCompetition Tender
      // above threshold if the status has ever been set to COMMISSION_PROCUREMENT_DECISION_STARTED
      // below threshold if the status has ever been set to MANUAL_AWARD_COMPLETED
      if (vm.secAwardInfoView &&
        (vm.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE ||
          vm.submission.process ===
          AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION)) {
        if (vm.submission.aboveThreshold) {
          return vm.hasStatusComProcDecClosed;
        } else {
          return vm.hasStatusManualAwardCompleted;
        }
      } else {
        return false;
      }
    }

    /** Function to determine if the submission cancel tab is visible */
    function isSubmissionCancelTabVisible() {
      // the tab is visible under following conditions:
      // 1) the user is Admin
      // 2) the process of the submission is not NegotiatedTender or NegotiatedCompetitionTender
      // 3) the status of the submission is after APPLICANTS_LIST_CREATED for SelectiveTender
      //    or after SUBMITTENT_LIST_CREATED for OpenTender or InvitedTender
      // 4) the status is not after AWARD_NOTICES_CREATED or was never set to PROCEDURE_CANCELED
      return (vm.secTenderCancelView &&
        ((vm.submission.process === AppConstants.PROCESS.SELECTIVE &&
            vm.currentStatus >= vm.status.APPLICANTS_LIST_CREATED) ||
          ((vm.submission.process === AppConstants.PROCESS.OPEN ||
              vm.submission.process === AppConstants.PROCESS.INVITATION) &&
            vm.currentStatus >= vm.status.SUBMITTENT_LIST_CREATED)) &&
        (vm.currentStatus < vm.status.AWARD_NOTICES_CREATED ||
          vm.hasStatusCancelled));
    }

    function isLegal() {
      if (!vm.submission) {
        return false;
      }
      return (vm.secIsLegalView && ((vm.submission.aboveThreshold &&
          (vm.submission.process === AppConstants.PROCESS.NEGOTIATED_PROCEDURE ||
            vm.submission.process ===
            AppConstants.PROCESS.NEGOTIATED_PROCEDURE_WITH_COMPETITION)) ||
        ((vm.submission.process === AppConstants.PROCESS.SELECTIVE &&
            (vm.currentStatus >= vm.status.APPLICANTS_LIST_CREATED &&
              (vm.currentStatus <= vm.status.PROCEDURE_COMPLETED ||
                vm.hasStatusCancelled))) ||
          (vm.submission.process === AppConstants.PROCESS.OPEN ||
            vm.submission.process === AppConstants.PROCESS.INVITATION) &&
          (vm.currentStatus >= vm.status.SUBMITTENT_LIST_CREATED &&
            (vm.currentStatus <= vm.status.PROCEDURE_COMPLETED ||
              vm.hasStatusCancelled)))));
    }

    function downloadProjectDocument(templateForm) {
      DocumentService.downloadProjectDocument(templateForm)
        .success(function (response, status, headers) {
          var fileName = headers()[CONTENT_DISPOSITION]
            .split(';')[1].trim().split('=')[1];
          fileName = fileName.substring(1, fileName.length - 1);
          var blob = new Blob([response], {
            type: 'application/octet-stream'
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
          AppService.setPaneShown(false);
        }).error(function (response, status) {

        });
    }

    function downloadAndPrintProjectDocument(templateForm) {
      DocumentService.downloadAndPrintProjectDocument(templateForm)
        .success(function (response, status, headers) {
          // download document
          downloadProjectDocument(templateForm);
          // print document
          if (headers()[CONTENT_DISPOSITION]) {
            var fileName = headers()[CONTENT_DISPOSITION]
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

    /** Function to determine if the award info first level tab is visible */
    function isAwardInfoFirstLevelTabVisible() {
      // the tab is visible only for users with this right and
      // if the status has ever been set to SUITABILITY_AUDIT_COMPLETED_S
      return vm.secAwardInfoFirstLevelView && vm.hasStatusStatusSuitAudComplS;
    }

    /** Function to display object information */
    function getObjectInfo(object) {
      return AppService.getObjectInfo(object);
    }

    /** Function to handle navigation to the submission canceling tab */
    function submissionCancelNavigation() {
      SubmissionService.submissionCancelNavigation($stateParams.id).success(
        function (data) {
          $state.go(SUBMISSIONCANCEL, {
            submissionId: $stateParams.id
          });
        }).error(function (response, status) {
        if (status === 400) {
          vm.invalidNavigation = true;
        }
      });
    }

    /** Set invalid navigation to false when changing state */
    $transitions.onBefore({}, function (transition) {
      if (transition.to() !== transition.from() && vm.invalidNavigation) {
        vm.invalidNavigation = false;
      }
      // Check if the submission cancel tab needs to be activated/deactivated.
      if (transition.to().name === SUBMISSIONCANCEL) {
        vm.submissionCancelTabActive = true;
      } else {
        vm.submissionCancelTabActive = false;
      }
      // Check if the legal hearing tab needs to be activated/deactivated.
      if (transition.to().name === SUBMITTENTSEXCLUSION ||
        transition.to().name === LEGALHEARING) {
        vm.legalHearingTabActive = true;
      } else {
        vm.legalHearingTabActive = false;
      }
    });

    function printDocuments(documentIds) {
      for (var i = 0; i < documentIds.length; i++) {
        DocumentService.printDocument(documentIds[i])
          .success(function (response, status, headers) {
            if (headers()[CONTENT_DISPOSITION]) {
              var fileName = headers()[CONTENT_DISPOSITION]
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

    /** Function to set the legal hearing or submission cancel tabs active */
    function legalHearingOrSubmissionCancelActive() {
      if ($state.current.name === SUBMITTENTSEXCLUSION ||
        $state.current.name === LEGALHEARING) {
        vm.legalHearingTabActive = true;
      } else if ($state.current.name === SUBMISSIONCANCEL) {
        vm.submissionCancelTabActive = true;
      }
    }
  }
})();
