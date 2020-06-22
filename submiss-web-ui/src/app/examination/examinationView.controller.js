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
 * @name examination.controller.js
 * @description ExaminationViewController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.examination")
    .controller("ExaminationViewController", ExaminationViewController);

  /** @ngInject */
  function ExaminationViewController($rootScope, $scope, $state, $stateParams,
    SubmissionService, ExaminationService,
    $uibModal, $filter, QFormJSRValidation, NgTableParams, AppService,
    AppConstants, $location, $anchorScroll,
    $transitions, SelectiveService, DocumentService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var i;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.examination = {};
    vm.examination.criterion = [];
    vm.criteria = null;
    vm.subcriteria = null;
    vm.submittents = [];
    vm.mustCriterion = [];
    vm.evaluatedCriterion = [];
    vm.subcriterion = [];
    vm.options = [{
        value: true,
        label: 'Erfüllt'
      },
      {
        value: false,
        label: 'Nicht erfüllt'
      }
    ];
    vm.grade = {
      criterionText: 'G',
      id: null
    };
    vm.currentStatus = null;
    vm.secFormalAuditEdit = false;
    vm.secAddCriterion = false;
    vm.secDeleteCriterion = false;
    vm.secUpdateCriterion = false;
    vm.secAddSubcriterion = false;
    vm.secDeleteSubcriterion = false;
    vm.secUpdateSubcriterion = false;
    vm.secExaminationClose = false;
    vm.secTenderMove = false;
    vm.secSentEmail = false;
    vm.secExaminationReopen = false;
    vm.group = AppConstants.GROUP;
    vm.status = AppConstants.STATUS;
    vm.reopen = AppConstants.STATUS_TO_REOPEN;
    vm.process = AppConstants.PROCESS;
    vm.reopenQuestion = AppConstants.STATUS_REOPEN_QUESTION;
    vm.reopenTitle = AppConstants.STATUS_REOPEN_TITLE;
    vm.offerOpeningClosedBefore = null;
    vm.displayedMustCriterion = {};
    vm.displayedEvaluatedCriterion = {};
    vm.isProcessSelective = false;
    vm.applicationOpeningClosedBefore = false;
    vm.mandatoryFieldsEmpty = false;
    vm.isCompletedOrCancelled = false;
    vm.tableStyle = AppService.customTableStyle();
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.save = save;
    vm.cancelButton = cancelButton;
    vm.navigateToFormalAudit = navigateToFormalAudit;
    vm.readCriteriaOfSubmission = readCriteriaOfSubmission;
    vm.deleteCriterion = deleteCriterion;
    vm.openAccordion = openAccordion;
    vm.openMustCriteriaAccordion = openMustCriteriaAccordion;
    vm.accordion = {};
    vm.accordion.show = true;
    vm.openEvaluatedCriteriaAccordion = openEvaluatedCriteriaAccordion;
    vm.addCriterion = addCriterion;
    vm.addSubcriterion = addSubcriterion;
    vm.readSubcriteriaOfCriterion = readSubcriteriaOfCriterion;
    vm.deleteSubcriterion = deleteSubcriterion;
    vm.buttonOrFieldDisabledForCriterion = buttonOrFieldDisabledForCriterion;
    vm.buttonOrFieldDisabledForSubcriterion = buttonOrFieldDisabledForSubcriterion;
    vm.closeExamination = closeExamination;
    vm.reopenExamination = reopenExamination;
    vm.isMindestnoteAndMaximalnoteMandatory = isMindestnoteAndMaximalnoteMandatory;
    vm.getOfferCriterionGrade = getOfferCriterionGrade;
    vm.calculateSubcriterionPoint = calculateSubcriterionPoint;
    vm.getOfferSubcriterionGrade = getOfferSubcriterionGrade;
    vm.showSubmittentJointVenturesSubcontractors = showSubmittentJointVenturesSubcontractors;
    vm.showSubmittentAndLocation = showSubmittentAndLocation;
    vm.showJointVenturesSubcontractors = showJointVenturesSubcontractors;
    vm.getOption = getOption;
    vm.formatAmount = formatAmount;
    vm.isFormDisabled = isFormDisabled;
    vm.formatScore = formatScore;
    vm.validateNumberInputForMinus = validateNumberInputForMinus;
    vm.checkForChanges = checkForChanges;
    vm.saveAndCancelButtonsDisabled = saveAndCancelButtonsDisabled;
    vm.reopenExaminationButtonDisabled = reopenExaminationButtonDisabled;
    vm.getObjectInfo = getObjectInfo;
    vm.exportCriteria = exportCriteria;
    vm.exportEMLCriteria = exportEMLCriteria;
    vm.uploaded = uploaded;
    vm.uploadFile = uploadFile;
    vm.uploadError = uploadError;
    vm.lockExamination = lockExamination;
    vm.unlockExamination = unlockExamination;
    vm.markRed = markRed;
    vm.loggedInUser = AppService.getLoggedUser();
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      SubmissionService.loadFormalAudit($stateParams.id)
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            readSubmission($stateParams.id);
            readStatusOfSubmission($stateParams.id);
            readCriteriaOfSubmission($stateParams.id);
            readOfferCriteria($stateParams.id);
            hasApplicationOpeningBeenClosedBefore($stateParams.id);
            // If a command was given to close the examination before the page
            // reloaded, proceed with the command.
            if ($stateParams.proceedToClose) {
              closeExamination();
            }
          }
        });
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    function openAccordion() {
      vm.accordion.show = !vm.accordion.show;
    }

    function lockExamination() {
      SubmissionService
        .lockSubmission($stateParams.id, AppConstants.CRITERION_TYPE.SUITABILITY)
        .success(function (data) {
          defaultReload()
        });
    }

    function unlockExamination() {
      SubmissionService
        .unlockSubmission($stateParams.id,
          AppConstants.CRITERION_TYPE.SUITABILITY)
        .success(function (data) {
          defaultReload()
        });
    }

    /**Function to handle the must-criteria accordion functionality */
    function openMustCriteriaAccordion(mustCriterion) {
      mustCriterion.show = !mustCriterion.show;
      if (mustCriterion.show) {
        if (vm.displayedMustCriterion !== null) {
          vm.displayedMustCriterion.show = false;
        }
        vm.displayedMustCriterion = mustCriterion;
      } else {
        vm.displayedMustCriterion = null;
      }
    }

    /**Function to handle the evaluated-criteria accordion functionality */
    function openEvaluatedCriteriaAccordion(evaluatedCriterion) {
      evaluatedCriterion.show = !evaluatedCriterion.show;
      if (evaluatedCriterion.show) {
        if (vm.displayedEvaluatedCriterion !== null) {
          vm.displayedEvaluatedCriterion.show = false;
        }
        vm.displayedEvaluatedCriterion = evaluatedCriterion;
      } else {
        vm.displayedEvaluatedCriterion = null;
      }
    }

    function save() {
      ExaminationService.examinationLockedByAnotherUser(vm.data.submission.id)
        .success(function (data) {
          proceedWithSaving();
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            $anchorScroll('page-title');
            QFormJSRValidation.markErrors($scope,
              $scope.examinationViewCtrl.examinationForm, response);
          }
        });
    }

    function proceedWithSaving() {
      createEmptyCriteria();
      vm.examination.criterion = vm.criteria;
      vm.examination.submissionId = $stateParams.id;
      vm.examination.pageRequestedOn = vm.data.submission.pageRequestedOn;
      vm.examination.submissionVersion = vm.data.submission.version;
      AppService.setPaneShown(true);
      ExaminationService.updateExamination(vm.examination)
        .success(function (data) {
          AppService.setPaneShown(false);
          ExaminationService.updateSubmissionFormalAuditExaminationStatus(
              $stateParams.id)
            .success(function (data) {
              defaultReload()
            });
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            AppService.setPaneShown(false);
            $anchorScroll('page-title');
            QFormJSRValidation.markErrors($scope,
              $scope.examinationViewCtrl.examinationForm, response);
          }
        });
    }

    function readOfferCriteria(id) {
      ExaminationService.getExaminationSubmittentListWithCriteria(id,
          AppConstants.CRITERION_TYPE.SUITABILITY, false)
        .success(function (data) {
          vm.offers = data;
          for (var i = 0; i < vm.offers.length; i++) {
            vm.offers[i].mustCriterion = [];
            vm.offers[i].evaluatedCriterion = [];
            // create different object for UI requirements. Add G (Gewichtung) in frond of every evaluated criteria,
            // dropdown fields for must criteria etc.
            for (var j = 0; j < vm.offers[i].offerCriteria.length; j++) {
              if (vm.offers[i].offerCriteria[j].criterion.criterionType ===
                "mustCriterion") {
                vm.offers[i].mustCriterion.push(vm.offers[i].offerCriteria[j]);
              }
              if (vm.offers[i].offerCriteria[j].criterion.criterionType ===
                "evaluatedCriterion") {
                vm.offers[i].evaluatedCriterion.push(
                  vm.offers[i].offerCriteria[j]);
              }
            }
            for (var z = 0; z < vm.offers[i].offerSubcriteria.length; z++) {
              vm.subcriterion.push(vm.offers[i].offerSubcriteria[z]);
              for (var x = 0; x < vm.offers[i].evaluatedCriterion.length; x++) {
                vm.offers[i].evaluatedCriterion[x].subcriterion = [];
                for (var y = 0; y <
                  vm.offers[i].evaluatedCriterion[x].criterion.subcriterion.length; y++) {
                  if (vm.offers[i].offerSubcriteria[z].subcriterion.id ===
                    vm.offers[i].evaluatedCriterion[x].criterion.subcriterion[y].id) {
                    // Set offer subcriterion to subcriterion.
                    vm.offers[i].evaluatedCriterion[x].criterion.subcriterion[y] = vm.offers[i].offerSubcriteria[z];
                  }
                }
              }
            }
          }
          // in order to freeze the first left column and two first top rows
          // we use jquery plugin tableHeadFixer.
          // it doesn't work if applied before the html form is loaded,
          // that is why we set a zero timeout
          setTimeout(function () {
            $("#fixTable").tableHeadFixer({
              "left": 1
            });
          }, 0);
        }).error(function (response, status) {});
    }

    function readSubmission(id) {
      SubmissionService.readSubmission(id)
        .success(function (data) {
          vm.data.submission = data;
          hasOfferOpeningBeenClosedBefore(vm.data.submission.id);
          vm.secFormalAuditEdit = AppService.isOperationPermitted(
            AppConstants.OPERATION.FORMAL_AUDIT_EDIT, vm.data.submission.process);
          vm.secAddCriterion = AppService.isOperationPermitted(
            AppConstants.OPERATION.ADD_CRITERION, vm.data.submission.process);
          vm.secDeleteCriterion = AppService.isOperationPermitted(
            AppConstants.OPERATION.DELETE_CRITERION, vm.data.submission.process);
          vm.secUpdateCriterion = AppService.isOperationPermitted(
            AppConstants.OPERATION.UPDATE_CRITERION, vm.data.submission.process);
          vm.secAddSubcriterion = AppService.isOperationPermitted(
            AppConstants.OPERATION.ADD_SUBCRITERION, vm.data.submission.process);
          vm.secDeleteSubcriterion = AppService.isOperationPermitted(
            AppConstants.OPERATION.DELETE_SUBCRITERION,
            vm.data.submission.process);
          vm.secUpdateSubcriterion = AppService.isOperationPermitted(
            AppConstants.OPERATION.UPDATE_SUBCRITERION,
            vm.data.submission.process);
          vm.secExaminationClose = AppService.isOperationPermitted(
            AppConstants.OPERATION.EXAMINATION_CLOSE, null);
          vm.secExaminationReopen = AppService.isOperationPermitted(
            AppConstants.OPERATION.EXAMINATION_REOPEN, null);
          vm.secTenderMove = AppService.isOperationPermitted(
            AppConstants.OPERATION.TENDER_MOVE, null);
          vm.secSentEmail = AppService.isOperationPermitted(
            AppConstants.OPERATION.SENT_EMAIL, null);
          // Set the min/max grade of submission to examination object in order to be updated after save.
          if (vm.data.submission.minGrade !== null) {
            vm.examination.minGrade = vm.data.submission.minGrade;
          }

          if (vm.data.submission.maxGrade !== null) {
            vm.examination.maxGrade = vm.data.submission.maxGrade;
          }
          vm.submittents = vm.data.submission.submittents;
          // Check if the process type is selective.
          if (vm.data.submission.process === AppConstants.PROCESS.SELECTIVE) {
            vm.isProcessSelective = true;
          }
        }).error(function (response, status) {});
    }

    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id)
        .success(function (data) {
          if (data === vm.status.PROCEDURE_COMPLETED ||
            data === vm.status.PROCEDURE_CANCELED) {
            vm.isCompletedOrCancelled = true;
            AppService.getSubmissionStatuses(id)
              .success(function (data) {
                vm.statusHistory = data;
                vm.currentStatus = AppService.assignCurrentStatus(vm.statusHistory);
              });
          } else {
            vm.currentStatus = data;
          }
        }).error(function (response, status) {});
    }

    function navigateToFormalAudit(id) {
      if (vm.data.submission.process === AppConstants.PROCESS.SELECTIVE) {
        $state.go('formalAuditSelective', {
          id: id
        });
      } else {
        $state.go('examination.formal', {
          id: id
        });
      }
    }

    function readCriteriaOfSubmission(id) {
      ExaminationService.readCriteriaOfSubmission(id)
        .success(function (data) {
          vm.criteria = data;
          for (i = 0; i < vm.criteria.length; i++) {
            if (vm.criteria[i].criterionType === "mustCriterion") {
              // If must criterion accordion was open before, keep it open after reloading the page.
              if ($stateParams.displayedMustCriterionId !== null &&
                $stateParams.displayedMustCriterionId === vm.criteria[i].id) {
                vm.criteria[i].show = true;
                vm.displayedMustCriterion = vm.criteria[i];
              }
              vm.mustCriterion.push(vm.criteria[i]);
            }
            if (vm.criteria[i].criterionType === "evaluatedCriterion") {
              // If evaluated criterion accordion was open before, keep it open after reloading the page.
              if ($stateParams.displayedEvaluatedCriterionId !== null &&
                $stateParams.displayedEvaluatedCriterionId ===
                vm.criteria[i].id) {
                vm.criteria[i].show = true;
                vm.displayedEvaluatedCriterion = vm.criteria[i];
              }
              vm.evaluatedCriterion.push(vm.criteria[i]);
            }
          }
        }).error(function (response, status) {});
    }

    /**Create a function to show a modal for adding a criterion*/
    function addCriterion(isEvaluated) {
      if (!vm.dirtyFlag) {
        $uibModal.open({
          templateUrl: 'app/examination/criterion/criterionAdd.html',
          controller: 'CriterionAddController',
          controllerAs: 'criterionAddCtrl',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            submission: function () {
              return vm.data.submission.id;
            },
            isEvaluated: function () {
              return isEvaluated;
            },
            pageRequestedOn: function () {
              return vm.data.submission.pageRequestedOn;
            },
            submissionVersion: function () {
              return vm.data.submission.version;
            }
          }
        });
      } else {
        AppService.informAboutUnsavedChanges();
      }
    }

    /**Create a function to delete a criterion */
    function deleteCriterion(id, isEvaluated) {
      ExaminationService.examinationLockedByAnotherUser($stateParams.id)
        .success(function (data) {
          proceedWithDeletingCriterion(id, isEvaluated);
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            $anchorScroll('page-title');
            QFormJSRValidation.markErrors($scope,
              $scope.examinationViewCtrl.examinationForm, response);
          }
        });
    }

    function proceedWithDeletingCriterion(id, isEvaluated) {
      if (!vm.dirtyFlag) {
        if ((vm.examination.minGrade === null || angular.isUndefined(
              vm.examination.minGrade) ||
            !vm.examination.maxGrade) && isEvaluated) {
          vm.mandatoryFieldsEmpty = true;
          if ($scope.examinationViewCtrl.examinationForm.$valid) {
            $anchorScroll('mandatoryFieldsEmpty');
          } else {
            $anchorScroll('ErrorAnchor');
          }
        } else {
          if (vm.mandatoryFieldsEmpty) {
            vm.mandatoryFieldsEmpty = false;
          }
          $uibModal
            .open({
              template: '<div class="modal-header">' +
                '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
                '<h4 class="modal-title">Kriterium löschen</h4>' +
                '</div>' +
                '<div class="modal-body">' +
                '<div class="row">' +
                '<div class="col-md-12">' +
                '<p> Möchten Sie das Kriterium wirklich löschen? </p>' +
                '</div>' +
                '</div>' +
                '</div>' +
                '<div class="modal-footer">' +
                '<button type="button" class="btn btn-primary" ng-click="examinationViewCtrl.criterionDelete(); $close()">Ja</button>' +
                '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
                '</div>' + '</div>',
              controllerAs: 'examinationViewCtrl',
              backdrop: 'static',
              keyboard: false,
              resolve: {
                pageRequestedOn: function () {
                  return vm.data.submission.pageRequestedOn;
                }
              },
              controller: function (pageRequestedOn) {
                var vm = this;
                vm.criterionDelete = function () {
                  ExaminationService.deleteCriterion(id, pageRequestedOn)
                    .success(function (data) {
                      proceedWithSaving();
                    }).error(function (response, status) {
                      $anchorScroll('page-title');
                      QFormJSRValidation.markErrors($scope,
                        $scope.examinationViewCtrl.examinationForm, response);
                    });
                };
              }
            });
        }
      } else {
        AppService.informAboutUnsavedChanges();
      }
    }

    function cancelButton(dirtyflag) {
      createEmptyCriteria();
      if (!dirtyflag) {
        defaultReload();
      } else {
        var closeCancelModal = $uibModal
          .open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
              '<h4 class="modal-title"></h4>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie den Vorgang wirklich abbrechen? Nicht gespeicherte Informationen gehen dabei verloren </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="examinationViewCtrl.closeWindowModal(\'ja\')">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'examinationViewCtrl',
            backdrop: 'static',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.closeWindowModal = function (reason) {
                closeCancelModal.dismiss(reason);
              };
            }
          });
        return closeCancelModal.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            defaultReload();
          } else {
            // Do something else here
            return false;
          }
          return null;
        });
      }
      return null;
    }

    /**Retrieve subcriteria of criterion */
    function readSubcriteriaOfCriterion(criterionId) {
      ExaminationService.readSubcriteriaOfCriterion(criterionId)
        .success(function (data) {
          vm.subcriteria = data;
        }).error(function (response, status) {});
    }

    /**Create a function to delete a subcriterion */
    function deleteSubcriterion(id) {
      ExaminationService.examinationLockedByAnotherUser($stateParams.id)
        .success(function (data) {
          proceedWithDeletingSubCriterion(id);
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            $anchorScroll('page-title');
            QFormJSRValidation.markErrors($scope,
              $scope.examinationViewCtrl.examinationForm, response);
          }
        });
    }

    function proceedWithDeletingSubCriterion(id) {
      if (!vm.dirtyFlag) {
        if (vm.examination.minGrade === null || angular.isUndefined(
            vm.examination.minGrade) ||
          !vm.examination.maxGrade) {
          vm.mandatoryFieldsEmpty = true;
          if ($scope.examinationViewCtrl.examinationForm.$valid) {
            $anchorScroll('mandatoryFieldsEmpty');
          } else {
            $anchorScroll('ErrorAnchor');
          }
        } else {
          if (vm.mandatoryFieldsEmpty) {
            vm.mandatoryFieldsEmpty = false;
          }
          $uibModal
            .open({
              template: '<div class="modal-header">' +
                '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
                '<h4 class="modal-title">Unterkriterium löschen</h4>' +
                '</div>' +
                '<div class="modal-body">' +
                '<div class="row">' +
                '<div class="col-md-12">' +
                '<p> Möchten Sie das Unterkriterium wirklich löschen? </p>' +
                '</div>' +
                '</div>' +
                '</div>' +
                '<div class="modal-footer">' +
                '<button type="button" class="btn btn-primary" ng-click="examinationViewCtrl.subcriterionDelete(); $close()">Ja</button>' +
                '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
                '</div>' + '</div>',
              controllerAs: 'examinationViewCtrl',
              backdrop: 'static',
              keyboard: false,
              resolve: {
                pageRequestedOn: function () {
                  return vm.data.submission.pageRequestedOn;
                }
              },
              controller: function (pageRequestedOn) {
                var vm = this;
                vm.subcriterionDelete = function () {
                  ExaminationService.deleteSubcriterion(id, pageRequestedOn)
                    .success(function (data) {
                      proceedWithSaving();
                    }).error(function (response, status) {
                      $anchorScroll('page-title');
                      QFormJSRValidation.markErrors($scope,
                        $scope.examinationViewCtrl.examinationForm, response);
                    });
                };
              }
            });
        }
      } else {
        AppService.informAboutUnsavedChanges();
      }
    }

    /**Create a function to show a modal for adding a subcriterion */
    function addSubcriterion(criterion) {
      if (!vm.dirtyFlag) {
        $uibModal
          .open({
            templateUrl: 'app/examination/criterion/subcriterionAdd.html',
            controller: 'SubcriterionAddController',
            controllerAs: 'subcriterionAddCtrl',
            backdrop: 'static',
            keyboard: false,
            resolve: {
              criterion: function () {
                return criterion;
              },
              pageRequestedOn: function () {
                return vm.data.submission.pageRequestedOn;
              },
              submissionId: function () {
                return vm.data.submission.id;
              },
              submissionVersion: function () {
                return vm.data.submission.version;
              }
            }
          });
      } else {
        AppService.informAboutUnsavedChanges();
      }
    }

    function closeExamination() {
      var openCloseExaminationModal = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="examinationViewCtrl.closeModal(false)"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">Eignungsprüfung abschliessen</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie die Eignungsprüfung wirklich abschliessen? </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="examinationViewCtrl.closeModal(true)">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="examinationViewCtrl.closeModal(false)">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'examinationViewCtrl',
          backdrop: 'static',
          size: 'lg',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeModal = function (reason) {
              openCloseExaminationModal.dismiss(reason);
            };
          }
        });
      return openCloseExaminationModal.result.then(function () {
        // Modal Success Handler
      }, function (response) { // Modal Dismiss Handler
        if (response) {
          AppService.setPaneShown(true);
          vm.examination.submissionId = vm.data.submission.id;
          vm.examination.submissionVersion = vm.data.submission.version;
          ExaminationService.closeExamination(vm.examination)
            .success(function (data) {
              AppService.setPaneShown(false);
              vm.dirtyFlag = false;
              $state.go($state.current, {
                displayedMustCriterionId: null,
                displayedEvaluatedCriterionId: null,
                proceedToClose: null
              }, {
                reload: true
              });
            }).error(function (response, status) {
              AppService.setPaneShown(false);
              if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
                $anchorScroll('ErrorAnchor');
                QFormJSRValidation.markErrors($scope,
                  $scope.examinationViewCtrl.examinationForm, response);
              }
            });
        } else {
          if ($stateParams.proceedToClose) {
            defaultReload();
          }
          return false;
        }
        return null;
      });
    }

    /**Function to determine when to add, delete or update a criterion */
    function buttonOrFieldDisabledForCriterion(process, currentStatus) {
      return !((currentStatus >= vm.status.OFFER_OPENING_CLOSED &&
            ((process === 'SELECTIVE' && currentStatus <
                vm.status.AWARD_EVALUATION_CLOSED) ||
              ((process === 'OPEN' || process === 'INVITATION') &&
                (currentStatus < vm.status.AWARD_EVALUATION_CLOSED ||
                  currentStatus <
                  vm.status.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED)))) ||
          (process === 'SELECTIVE' && (currentStatus >=
            vm.status.APPLICATION_OPENING_CLOSED &&
            currentStatus < vm.status.SUITABILITY_AUDIT_COMPLETED_S))) ||
        vm.isCompletedOrCancelled;
    }

    /**Function to determine when to add, delete or update a subcriterion */
    function buttonOrFieldDisabledForSubcriterion(process, currentStatus) {
      return !((currentStatus >= vm.status.OFFER_OPENING_CLOSED &&
            ((process === 'OPEN' || process === 'INVITATION') &&
              (currentStatus <
                vm.status.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED ||
                currentStatus < vm.status.SUITABILITY_AUDIT_COMPLETED))) ||
          (process === 'SELECTIVE' && (currentStatus >=
            vm.status.APPLICATION_OPENING_CLOSED &&
            currentStatus < vm.status.SUITABILITY_AUDIT_COMPLETED_S))) ||
        vm.isCompletedOrCancelled;
    }

    /**Function to show mandatory sign for Mindestnote and Maximalnote fields*/
    function isMindestnoteAndMaximalnoteMandatory() {
      if (vm.criteria !== null) {
        for (i = 0; i < vm.criteria.length; i++) {
          if (vm.criteria[i].criterionType === "evaluatedCriterion") {
            return true;
          }
        }
      }
      return false;
    }

    function calculateSubcriterionPoint(subcriterion) {
      subcriterion.weighting = parseFloat(
        subcriterion.weighting.toFixed(AppConstants.ROUND_DECIMALS.SCORE));
      vm.subcriterion.point = subcriterion.grade * (subcriterion.weighting /
        100);
      return parseFloat(
        vm.subcriterion.point.toFixed(AppConstants.ROUND_DECIMALS.SCORE));
    }

    function getOfferCriterionGrade(evaluatedCriterion) {
      var totalGrade = 0;
      for (var i = 0; i < evaluatedCriterion.criterion.subcriterion.length; i++) {
        totalGrade += evaluatedCriterion.criterion.subcriterion[i].grade *
          (evaluatedCriterion.criterion.subcriterion[i].weighting);
      }
      var returnValue = totalGrade / 100;
      return parseFloat(returnValue.toFixed(AppConstants.ROUND_DECIMALS.SCORE));
    }

    /**
     * function that returns the offerSubcriterion grade given an offerIndex,
     * criterionIndex and subcriterionIndex
     */
    function getOfferSubcriterionGrade(offerIndex, criterionIndex,
      subcriterionIndex) {
      var subcriterion = getOfferSubcriterion(offerIndex, criterionIndex,
        subcriterionIndex);
      if (subcriterion) {
        return subcriterion.grade;
      }
      return null;
    }

    /**
     * function that returns the offerSubcriterion given an offerIndex,
     * criterionIndex and subcriterionIndex
     */
    function getOfferSubcriterion(offerIndex, criterionIndex,
      subcriterionIndex) {
      var offerSubcriterion = null;
      var curOffer = vm.offers[offerIndex];
      for (var i = 0; i < curOffer.offerSubcriteria.length; i++) {
        if (vm.criteria[criterionIndex].subcriterion[subcriterionIndex] &&
          curOffer.offerSubcriteria[i].subcriterion.id ===
          vm.criteria[criterionIndex].subcriterion[subcriterionIndex].id) {
          offerSubcriterion = curOffer.offerSubcriteria[i];
        }
      }
      return offerSubcriterion;
    }

    /**
     * function that change the status of submission to Formelle Prüfung & Eignungsprüfung gestartet
     */
    function reopenExamination() {
      var reopenTenderStatusModal = AppService.reopenTenderStatusModal(
        vm.reopenTitle.SUITABILITY_AUDIT,
        vm.reopenQuestion.SUITABILITY_AUDIT, vm.reopen.SUITABILITY_AUDIT);
      return reopenTenderStatusModal.result
        .then(function (response) {
          var reopenForm = {};
          if (!angular.isUndefined(response)) {
            reopenForm.reopenReason = response;
            ExaminationService.reopenExamination(reopenForm, vm.data.submission.id, vm.data.submission.version)
              .success(function (data) {
                $state.reload(); // reload the list
              }).error(function (response, status) {
                if (status === 400 || status === 409) {
                  QFormJSRValidation.markErrors($scope,
                    $scope.examinationViewCtrl.examinationForm, response);
                }
              });
          }
        });
    }

    /**
     * Function to show Submittent (Tenderer) with its Joint Ventures and
     * Subcontractors
     */
    function showSubmittentJointVenturesSubcontractors(offer) {
      return AppService.showSubmittentAndLocation(offer) +
        AppService.showJointVenturesSubcontractors(offer);
    }

    function markRed(offer) {
      var i;
      if (offer.submittent.subcontractors.length > 0) {
        for (i = 0; i < offer.submittent.subcontractors.length; i++) {
          if (offer.submittent.subcontractors[i].proofStatusFabe === 4 ||
            (offer.submittent.subcontractors[i].proofStatusFabe === 3 &&
              vm.loggedInUser.tenant.isMain)) {
            return true;
          }
        }
      }
      if (offer.submittent.jointVentures.length > 0) {
        for (i = 0; i < offer.submittent.jointVentures.length; i++) {
          if (offer.submittent.jointVentures[i].proofStatusFabe === 4 ||
            (offer.submittent.jointVentures[i].proofStatusFabe === 3 &&
              vm.loggedInUser.tenant.isMain)) {
            return true;
          }
        }
      }
      return false;
    }

    /**
     * Function to show Submittent (Tenderer) with its location
     */
    function showSubmittentAndLocation(offer) {
      return AppService.showSubmittentAndLocation(offer);
    }

    /**
     * Function to show Joint Ventures and Subcontractors
     * of tenderer
     */
    function showJointVenturesSubcontractors(offer) {
      return AppService.showJointVenturesSubcontractors(offer);
    }

    /**
     * Function to get the option('Erfüllt', 'Nicht erfüllt') according to the value of the isFulfilled boolean
     */
    function getOption(isFulfilled) {
      var label = null;
      for (var i = 0; i < vm.options.length; i++) {
        if (vm.options[i].value === isFulfilled) {
          label = vm.options[i].label;
        }
      }
      return label;
    }

    /** Check if status offer opening closed has been set before */
    function hasOfferOpeningBeenClosedBefore(submissionId) {
      SubmissionService.hasOfferOpeningBeenClosedBefore(submissionId)
        .success(function (data) {
          vm.offerOpeningClosedBefore = data;
        }).error(function (response, status) {});
    }

    /** Format number according to specifications */
    function formatAmount(num) {
      return AppService.formatAmount(num);
    }

    /**Function to determine if the form should be disabled */
    function isFormDisabled() {
      if (vm.data.submission) {
        return (vm.data.submission.process !== AppConstants.PROCESS.SELECTIVE &&
            vm.currentStatus < vm.status.OFFER_OPENING_CLOSED &&
            vm.offerOpeningClosedBefore) ||
          (vm.data.submission.process === AppConstants.PROCESS.SELECTIVE &&
            ((vm.currentStatus < vm.status.APPLICATION_OPENING_CLOSED &&
                vm.applicationOpeningClosedBefore) ||
              vm.currentStatus >= vm.status.SUITABILITY_AUDIT_COMPLETED_S)) ||
          vm.isCompletedOrCancelled;
      }
      return null;
    }

    /** Function to format scores (3 decimals) */
    function formatScore(score) {
      return AppService.formatScore(score);
    }

    /** Function to ignore error in case "minus" is entered first in fields with type number*/
    function validateNumberInputForMinus(form) {
      if (!form.minGrade.$viewValue || !form.maxGrade.$viewValue) {
        $scope.examinationViewCtrl.examinationForm.$dirty = false;
        $scope.examinationViewCtrl.examinationForm.$invalid = false;
      }
    }

    /** Function to create empty criteria if needed */
    function createEmptyCriteria() {
      // If no must criterion accordion is open, create an empty must criterion with null id
      // so that there is no error when reloading the page.
      if (vm.displayedMustCriterion === null) {
        vm.displayedMustCriterion = {};
        vm.displayedMustCriterion.id = null;
      }
      // If no evaluated criterion accordion is open, create an empty evaluated criterion with null id,
      // so that there is no error when reloading the page.
      if (vm.displayedEvaluatedCriterion === null) {
        vm.displayedEvaluatedCriterion = {};
        vm.displayedEvaluatedCriterion.id = null;
      }
    }

    /** Function to check for unsaved changes before closing the status */
    function checkForChanges(changesMade) {
      if (changesMade) {
        var proceedWithoutSavingModal = AppService.proceedWithoutSaving();
        return proceedWithoutSavingModal.result.then(function (response) {
          if (response) {
            vm.dirtyFlag = false;
            $state.go($state.current, {
              id: $stateParams.id,
              displayedMustCriterionId: vm.displayedMustCriterion.id,
              displayedEvaluatedCriterionId: vm.displayedEvaluatedCriterion.id,
              proceedToClose: response
            }, {
              reload: true
            });
          }
        });
      } else {
        closeExamination();
        return null;
      }
    }

    /** Function to export criteria */
    function exportCriteria() {
      ExaminationService.examinationLockedByAnotherUser($stateParams.id)
        .success(function (data) {
          proceedWithExportCriteria();
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            $anchorScroll('page-title');
            QFormJSRValidation.markErrors($scope,
              $scope.examinationViewCtrl.examinationForm, response);
          }
        });
    }

    function proceedWithExportCriteria() {
      ExaminationService.exportOfferCriteria($stateParams.id,
          AppConstants.CRITERION_TYPE.SUITABILITY)
        .success(function (response, status, headers) {
          if (response.byteLength > 0) {
            var fileName = headers()["content-disposition"]
              .split(';')[1].trim().split('=')[1];
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
          }
          $state.reload();
        }).error(function (response, status) {});
    }

    /** Function to import criteria */
    function uploadFile(flow) {
      let validFormat = false;
      for (let k = 0; k < flow.files.length; k++) {
        validFormat =  flow.files[k].name.split(".").pop().toLowerCase() === 'xlsx';
        if (!validFormat) {
          flow.cancel();
          break;
        }
      }
      if(validFormat) {
        ExaminationService.checkExaminationOptimisticLock(vm.data.submission.id,
          vm.data.submission.pageRequestedOn, vm.data.submission.version)
        .success(function (response, status, headers) {
          flow.upload();
        }).error(function (response, status) {
          if (status === 409) { // Validation errors.
            $anchorScroll('page-title');
            QFormJSRValidation.markErrors($scope,
              $scope.examinationViewCtrl.examinationForm, response);
          }
        });
      } else {
        openWindowError(AppConstants.IMPORT_ERROR, AppConstants.INVALID_FILE_TYPE);
      }
    }

    /** Function to export criteria */
    function exportEMLCriteria() {
      ExaminationService.examinationLockedByAnotherUser($stateParams.id)
        .success(function (data) {
          proceedWithexportEMLCriteria();
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            $anchorScroll('page-title');
            QFormJSRValidation.markErrors($scope,
              $scope.examinationViewCtrl.examinationForm, response);
          }
        });
    }

    function proceedWithexportEMLCriteria() {
      ExaminationService.exportEMLOfferCriteria($stateParams.id,
          AppConstants.CRITERION_TYPE.SUITABILITY)
        .success(function (response, status, headers) {
          if (response.byteLength > 0) {
            var fileName = headers()["content-disposition"]
              .split(';')[1].trim().split('=')[1];
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
          }
          $state.reload();
        }).error(function (response, status) {});
    }

    function uploaded(flow) {
      var file = flow.files[0];
      ExaminationService.checkforchangesCriteria($stateParams.id,
          file.uniqueIdentifier, AppConstants.CRITERION_TYPE.SUITABILITY)
        .success(function (changed) {
          var hasBeenChanged = changed.message;
          if (changed.path === "true") {
            var modalResult = AppService.openGenericModal("Bestätigungsdialog",
              hasBeenChanged,
              "Möchten Sie die vorhandenen Daten wirklich überschreiben?");
            return modalResult.result.then(function (response) {
              if (response) {
                ExaminationService.importOfferCriteria($stateParams.id,
                    file.uniqueIdentifier, AppConstants.CRITERION_TYPE.SUITABILITY)
                  .success(function (data) {
                    defaultReload();
                  }).error(function (response, status) {
                    openWindowError(response);
                    //reload to clear cache
                    defaultReload();
                  });
              }
            });
          }
        }).error(function (response, status) {
          openWindowError(AppConstants.IMPORT_ERROR, response.message);
          //reload to clear cache
          defaultReload();
        });
    }

    function uploadError(file, message, flow) {
      // Pass error to ui
      var response = JSON.parse(message);
      return {
        message: response
      };
    }

    function openWindowError(title, message) {
      return $uibModal
        .open({
          templateUrl: 'app/genericModals/errorModal.html',
          controller: 'GenericModalsController',
          controllerAs: 'genericModalsCtrl',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            title: function () {
              return title;
            },
            body: function () {
              return {
                message: message
              };
            }
          }
        });
    }

    /** Check for unsaved changes when trying to transition to different state */
    $transitions.onBefore({}, function (transition) {
      if (transition.to() !== transition.from() && vm.dirtyFlag === true) {
        var transitionModal = AppService.transitionModal();
        return transitionModal.result.then(function (response) {
          if (response) {
            vm.dirtyFlag = false;
            return true;
          }
          return false;
        });
      }
      return null;
    });

    /** Check if status application opening closed has been set before */
    function hasApplicationOpeningBeenClosedBefore(submissionId) {
      SelectiveService.hasApplicationOpeningBeenClosedBefore(
        submissionId).success(function (data) {
        vm.applicationOpeningClosedBefore = data;
      }).error(function (response, status) {});
    }

    /** Function to disable the save and cancel buttons */
    function saveAndCancelButtonsDisabled() {
      return (!(vm.secFormalAuditEdit || vm.secUpdateCriterion ||
            vm.secUpdateSubcriterion) ||
          (vm.buttonOrFieldDisabledForCriterion(vm.data.submission.process,
              vm.currentStatus) &&
            vm.buttonOrFieldDisabledForSubcriterion(vm.data.submission.process,
              vm.currentStatus)) ||
          vm.currentStatus >= vm.status.SUITABILITY_AUDIT_COMPLETED ||
          vm.isFormDisabled()) ||
        vm.isCompletedOrCancelled || vm.data.submission.examinationIsLocked;
    }

    /** Function to disable the reopen examination button */
    function reopenExaminationButtonDisabled() {
      if (vm.data.submission) {
        return !vm.secExaminationReopen ||
          (vm.data.submission.process !== AppConstants.PROCESS.SELECTIVE &&
            vm.currentStatus >= vm.status.AWARD_EVALUATION_CLOSED) ||
          (vm.data.submission.process === AppConstants.PROCESS.SELECTIVE &&
            vm.currentStatus >= vm.status.OFFER_OPENING_CLOSED) ||
          vm.isCompletedOrCancelled;
      }
      return null;
    }

    /** Function to navigate to current state with default parameters */
    function defaultReload() {
      if (vm.dirtyFlag) {
        vm.dirtyFlag = false;
      }
      $state.go($state.current, {
        id: $stateParams.id,
        displayedMustCriterionId: vm.displayedMustCriterion.id,
        displayedEvaluatedCriterionId: vm.displayedEvaluatedCriterion.id,
        proceedToClose: false
      }, {
        reload: true
      });
    }

    /** Function to display object information */
    function getObjectInfo(object) {
      return AppService.getObjectInfo(object);
    }
  }
})();
