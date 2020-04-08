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
 * @name awardView.controller.js
 * @description AwardViewController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.award")
    .controller("AwardViewController", AwardViewController);

  /** @ngInject */
  function AwardViewController($rootScope, $scope, $state, $stateParams,
    SubmissionService, ExaminationService, AwardService, $uibModal,
    $filter, QFormJSRValidation, NgTableParams, AppService,
    AppConstants, $location, $anchorScroll, $transitions) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var i;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.criteria = [];
    vm.subcriteria = null;
    vm.status = AppConstants.STATUS;
    vm.group = AppConstants.GROUP;
    vm.reopen = AppConstants.STATUS_TO_REOPEN;
    vm.reopenQuestion = AppConstants.STATUS_REOPEN_QUESTION;
    vm.reopenTitle = AppConstants.STATUS_REOPEN_TITLE;
    vm.customFormulaPriceTitle = AppConstants.CUSTOM_FORMULA_PRICE_TITLE;
    vm.customFormulaOperatingCostsTitle = AppConstants.CUSTOM_FORMULA_OPERATING_COSTS_TITLE;
    vm.currentStatus = null;
    vm.criterion = [];
    vm.award = {};
    vm.award.criterion = [];
    vm.canAddOperatingCostAwardCriterion = null;
    vm.secAddOperatingCostAwardCriterion = false;
    vm.secAddCriterion = false;
    vm.secDeleteCriterion = false;
    vm.secUpdateCriterion = false;
    vm.secAddSubcriterion = false;
    vm.secDeleteSubcriterion = false;
    vm.secUpdateSubcriterion = false;
    vm.secAwardEvaluationEdit = false;
    vm.secAwardEvaluationClose = false;
    vm.secTenderMove = false;
    vm.secSentEmail = false;
    vm.secAwardEvaluationReopen = false;
    vm.calculationFormulas = [];
    vm.offers = [];
    vm.tableAccordionShow = true;
    vm.displayedCriterion = {};
    /** field indicating whether the form has been saved in the past,
     * so that the function Zuschlagskriterien bewerten can be executed
     */
    vm.saved = false;
    vm.displayedOffers = AppConstants.AWARD_DISPLAYED_OFFERS;
    vm.validateNumberInputForMinus = validateNumberInputForMinus;
    vm.process = AppConstants.PROCESS;
    /** field indicating whether the error message indicating
     * that the function Zuschlagskriterien bewerten can not be executed
     * should be displayed
     */
    vm.notPermittedToEnterCriteriaAssess = false;
    vm.mandatoryFieldsEmpty = false;
    vm.isCompletedOrCancelled = false;
    vm.showCustomFormulaPrice = false;
    vm.showCustomFormulaOperatingCosts = false;
    vm.tableStyle = AppService.customTableStyle();
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.save = save;
    vm.cancelButton = cancelButton;
    vm.readCriteriaOfSubmission = readCriteriaOfSubmission;
    vm.deleteCriterion = deleteCriterion;
    vm.openCriteriaAccordion = openCriteriaAccordion;
    vm.addCriterion = addCriterion;
    vm.addSubcriterion = addSubcriterion;
    vm.readSubcriteriaOfCriterion = readSubcriteriaOfCriterion;
    vm.deleteSubcriterion = deleteSubcriterion;
    vm.operatingCostAwardCriterionExists = operatingCostAwardCriterionExists;
    vm.canOperatingCostAwardCriterionBeAdded = canOperatingCostAwardCriterionBeAdded;
    vm.buttonOrFieldDisabledForAwardCriterion = buttonOrFieldDisabledForAwardCriterion;
    vm.buttonOrFieldDisabledForSubcriterion = buttonOrFieldDisabledForSubcriterion;
    vm.getCalculationFormulas = getCalculationFormulas;
    vm.buttonOrFieldDisabledForAwardEvaluationEdit = buttonOrFieldDisabledForAwardEvaluationEdit;
    vm.customDisplayCalculationFormulas = customDisplayCalculationFormulas;
    vm.customMatchCalculationFormulas = customMatchCalculationFormulas;
    vm.readOfferCriteriaOfSubmission = readOfferCriteriaOfSubmission;
    vm.getOfferCriterionGrade = getOfferCriterionGrade;
    vm.getOfferCriterionScore = getOfferCriterionScore;
    vm.getOfferSubcriterionGrade = getOfferSubcriterionGrade;
    vm.getOfferSubcriterionScore = getOfferSubcriterionScore;
    vm.showSubmittentJointVenturesSubcontractors = showSubmittentJointVenturesSubcontractors;
    vm.showSubmittentAndLocation = showSubmittentAndLocation;
    vm.showJointVenturesSubcontractors = showJointVenturesSubcontractors;
    vm.closeAwardEvaluationModal = closeAwardEvaluationModal;
    vm.openTableAccordion = openTableAccordion;
    vm.filterLessThan = filterLessThan;
    vm.awardEvaluationCloseButtonDisabled = awardEvaluationCloseButtonDisabled;
    vm.roundScoreToString = roundScoreToString;
    vm.greyedOut = greyedOut;
    vm.reopenAwardEvaluation = reopenAwardEvaluation;
    vm.formatAmount = formatAmount;
    vm.customRoundNumber = customRoundNumber;
    vm.saveAndCancelButtonsDisabled = saveAndCancelButtonsDisabled;
    vm.checkForChanges = checkForChanges;
    vm.getObjectInfo = getObjectInfo;
    vm.criteriaAssess = criteriaAssess;
    vm.openCustomFormulaPrice = openCustomFormulaPrice;
    vm.openCustomFormulaOperatingCosts = openCustomFormulaOperatingCosts;
    vm.exportCriteria = exportCriteria;
    vm.exportEMLCriteria = exportEMLCriteria;
    vm.lockAward = lockAward;
    vm.unlockAward = unlockAward;
    vm.uploaded = uploaded;
    vm.uploadFile = uploadFile;
    vm.uploadError = uploadError;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      AwardService.loadAwardEvaluation($stateParams.id)
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            readSubmission($stateParams.id);
            readStatusOfSubmission($stateParams.id);
            readCriteriaOfSubmission($stateParams.id);
            canOperatingCostAwardCriterionBeAdded($stateParams.id);
            readOfferCriteriaOfSubmission($stateParams.id);
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
    function readSubmission(id) {
      SubmissionService.readSubmission(id)
        .success(function (data) {
          vm.data.submission = data;
          vm.submittents = vm.data.submission.submittents;
          hasOfferOpeningBeenClosedBefore(vm.data.submission.id);
          vm.secAddOperatingCostAwardCriterion = AppService.isOperationPermitted(AppConstants.OPERATION.ADD_OPERATING_COST_AWARD_CRITERION, vm.data.submission.process);
          vm.secAddCriterion = AppService.isOperationPermitted(AppConstants.OPERATION.ADD_CRITERION, vm.data.submission.process);
          vm.secDeleteCriterion = AppService.isOperationPermitted(AppConstants.OPERATION.DELETE_CRITERION, vm.data.submission.process);
          vm.secUpdateCriterion = AppService.isOperationPermitted(AppConstants.OPERATION.UPDATE_CRITERION, vm.data.submission.process);
          vm.secAddSubcriterion = AppService.isOperationPermitted(AppConstants.OPERATION.ADD_SUBCRITERION, vm.data.submission.process);
          vm.secDeleteSubcriterion = AppService.isOperationPermitted(AppConstants.OPERATION.DELETE_SUBCRITERION, vm.data.submission.process);
          vm.secUpdateSubcriterion = AppService.isOperationPermitted(AppConstants.OPERATION.UPDATE_SUBCRITERION, vm.data.submission.process);
          vm.secAwardEvaluationEdit = AppService.isOperationPermitted(AppConstants.OPERATION.AWARD_EVALUATION_EDIT, vm.data.submission.process);
          vm.secAwardEvaluationEdit = vm.secAwardEvaluationEdit &&
            !(vm.data.submission.isServiceTender === true && (vm.data.submission.process === 'SELECTIVE' || vm.data.submission.process === 'OPEN'));
          vm.secAwardEvaluationClose = AppService.isOperationPermitted(AppConstants.OPERATION.AWARD_EVALUATION_CLOSE, vm.data.submission.process);
          vm.secTenderMove = AppService.isOperationPermitted(AppConstants.OPERATION.TENDER_MOVE, null);
          vm.secSentEmail = AppService.isOperationPermitted(AppConstants.OPERATION.SENT_EMAIL, null);
          vm.secAwardEvaluationReopen = AppService.isOperationPermitted(AppConstants.OPERATION.AWARD_EVALUATION_REOPEN, null);

          if (vm.data.submission.priceFormula) {
            vm.award.priceFormula = vm.data.submission.priceFormula;
          }

          if (vm.data.submission.operatingCostFormula) {
            vm.award.operatingCostFormula = vm.data.submission.operatingCostFormula;
          }

          if (vm.data.submission.awardMinGrade !== null) {
            vm.award.awardMinGrade = vm.data.submission.awardMinGrade;
          } else {
            vm.award.awardMinGrade = -999;
          }

          if (vm.data.submission.awardMaxGrade !== null) {
            vm.award.awardMaxGrade = vm.data.submission.awardMaxGrade;
          } else {
            vm.award.awardMaxGrade = 5;
          }

          if (vm.data.submission.addedAwardRecipients !== null) {
            vm.award.addedAwardRecipients = vm.data.submission.addedAwardRecipients;
          } else {
            vm.award.addedAwardRecipients = 1;
          }

          if (vm.data.submission.evaluationThrough !== null) {
            vm.award.evaluationThrough = vm.data.submission.evaluationThrough;
            /* if this required field is returned, then this means that the form has been saved in the past */
            vm.saved = true;
          }

          if (vm.data.submission.customPriceFormula) {
            vm.award.customPriceFormula = vm.data.submission.customPriceFormula;
          }

          if (vm.data.submission.customOperatingCostFormula) {
            vm.award.customOperatingCostFormula = vm.data.submission.customOperatingCostFormula;
          }

          vm.getCalculationFormulas(vm.data.submission.project.tenant.id);
          // If a command was given to close the award evaluation before the page
          // reloaded, proceed with the command.
          if ($stateParams.proceedToClose) {
            closeAwardEvaluationModal();
          }
        }).error(function (response, status) {});
    }

    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id)
        .success(function (data) {
          if (data === vm.status.PROCEDURE_COMPLETED ||
            data === vm.status.PROCEDURE_CANCELED) {
            vm.isCompletedOrCancelled = true;
            AppService.getSubmissionStatuses(id).success(function (data) {
              vm.statusHistory = data;
              vm.currentStatus = AppService.assignCurrentStatus(vm.statusHistory);
            });
          } else {
            vm.currentStatus = data;
          }
        }).error(function (response, status) {});
    }

    function customRoundNumber(num) {
      return AppService.customRoundNumber(num);
    }

    function readCriteriaOfSubmission(id) {
      ExaminationService.readCriteriaOfSubmission(id)
        .success(function (data) {
          /**
           * will be removed when separate functions
           * are created for separate criterion types
           */
          for (var i = 0; i < data.length; i++) {
            if (data[i].criterionType === 'awardCriterion' ||
              data[i].criterionType === 'priceAwardCriterion' ||
              data[i].criterionType === 'operatingCostAwardCriterion') {
              // If criterion accordion was open before, keep it open after reloading the page.
              if ($stateParams.displayedCriterionId !== null &&
                $stateParams.displayedCriterionId === data[i].id) {
                data[i].show = true;
                vm.displayedCriterion = data[i];
              }
              vm.criteria.push(data[i]);
            }
          }
        }).error(function (response, status) {});
    }

    function lockAward() {
      SubmissionService.lockSubmission($stateParams.id, AppConstants.CRITERION_TYPE.AWARD)
        .success(function (data) {
          defaultReload()
        });
    }

    function unlockAward() {
      SubmissionService.unlockSubmission($stateParams.id, AppConstants.CRITERION_TYPE.AWARD)
        .success(function (data) {
          defaultReload()
        });
    }

    function readOfferCriteriaOfSubmission(id) {
      ExaminationService.readOfferCriteria(id, AppConstants.CRITERION_TYPE.AWARD)
        .success(function (data) {
          vm.offers = data;
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
    /**Function to handle the table accordion functionality */
    function openTableAccordion() {
      vm.tableAccordionShow = !vm.tableAccordionShow;
    }
    /**Function to handle the criteria accordion functionality */
    function openCriteriaAccordion(criterion) {
      criterion.show = !criterion.show;
      if (criterion.show) {
        if (vm.displayedCriterion !== null) {
          vm.displayedCriterion.show = false;
        }
        vm.displayedCriterion = criterion;
      } else {
        vm.displayedCriterion = null;
      }
    }
    /**Retrieve subcriteria of criterion */
    function readSubcriteriaOfCriterion(criterionId) {
      ExaminationService.readSubcriteriaOfCriterion(criterionId)
        .success(function (data) {
          vm.subcriteria = data;
        }).error(function (response, status) {});
    }
    /**Create a function to show a modal for adding a criterion*/
    function addCriterion(isOperatingCost) {
      if (!vm.dirtyFlag) {
        $uibModal.open({
          templateUrl: 'app/awardEvaluation/awardCriterion/awardCriterionAdd.html',
          controller: 'AwardCriterionAddController',
          controllerAs: 'awardCriterionAddCtrl',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            submission: function () {
              return vm.data.submission.id;
            },
            isOperatingCost: function () {
              return isOperatingCost;
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
    function deleteCriterion(id) {
      ExaminationService.awardLockedByAnotherUser($stateParams.id)
        .success(function (data) {
          proceedWithDeletingCriterion(id);
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            // remove this error message, if already exists
            $anchorScroll('page-title');
            QFormJSRValidation
              .markErrors($scope, $scope.awardViewCtrl.awardForm, response);
          }
        });
    }

    function proceedWithDeletingCriterion(id) {
      if (!vm.dirtyFlag) {
        if (!vm.award.evaluationThrough || !vm.award.awardMinGrade ||
          !vm.award.awardMaxGrade || !vm.award.priceFormula ||
          !vm.award.addedAwardRecipients || !vm.award.operatingCostFormula ||
          (!vm.award.customPriceFormula && vm.showCustomFormulaPrice) ||
          (!vm.award.customOperatingCostFormula && vm.showCustomFormulaOperatingCosts)) {
          vm.mandatoryFieldsEmpty = true;
          $anchorScroll('page-title');
        } else {
          if (vm.mandatoryFieldsEmpty) {
            vm.mandatoryFieldsEmpty = false;
          }
          $uibModal.open({
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
              '<button type="button" class="btn btn-primary" ng-click="awardViewCtrl.criterionDelete(); $close()">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'awardViewCtrl',
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
                ExaminationService.deleteCriterion(id, pageRequestedOn).success(function (data) {
                  proceedWithSaving();
                }).error(function (response, status) {
                  if (status === 400) { // Validation errors.
                    // remove this error message, if already exists
                    $anchorScroll('page-title');
                    QFormJSRValidation
                      .markErrors($scope, $scope.awardViewCtrl.awardForm, response);
                  }
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
        $uibModal.open({
          templateUrl: 'app/awardEvaluation/awardCriterion/awardSubcriterionAdd.html',
          controller: 'AwardSubcriterionAddController',
          controllerAs: 'awardSubcriterionAddCtrl',
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
    /**Create a function to delete a subcriterion */
    function deleteSubcriterion(id) {
      ExaminationService.awardLockedByAnotherUser($stateParams.id)
        .success(function (data) {
          proceedWithDeletingSubCriterion(id);
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            // remove this error message, if already exists
            $anchorScroll('page-title');
            QFormJSRValidation
              .markErrors($scope, $scope.awardViewCtrl.awardForm, response);
          }
        });
    }

    function proceedWithDeletingSubCriterion(id) {
      if (!vm.dirtyFlag) {
        if (!vm.award.evaluationThrough || !vm.award.awardMinGrade ||
          !vm.award.awardMaxGrade || !vm.award.priceFormula ||
          !vm.award.addedAwardRecipients || !vm.award.operatingCostFormula ||
          (!vm.award.customPriceFormula && vm.showCustomFormulaPrice) ||
          (!vm.award.customOperatingCostFormula && vm.showCustomFormulaOperatingCosts)) {
          vm.mandatoryFieldsEmpty = true;
          $anchorScroll('page-title');
        } else {
          if (vm.mandatoryFieldsEmpty) {
            vm.mandatoryFieldsEmpty = false;
          }
          $uibModal.open({
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
              '<button type="button" class="btn btn-primary" ng-click="awardViewCtrl.subcriterionDelete(); $close()">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'awardViewCtrl',
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
                ExaminationService.deleteSubcriterion(id, pageRequestedOn).success(function (data) {
                  proceedWithSaving();
                }).error(function (response, status) {
                  if (status === 400) { // Validation errors.
                    // remove this error message, if already exists
                    $anchorScroll('page-title');
                    QFormJSRValidation
                      .markErrors($scope, $scope.awardViewCtrl.awardForm, response);
                  }
                });
              };
            }
          });
        }
      } else {
        AppService.informAboutUnsavedChanges();
      }
    }

    /**Create a function to close the award evaluation */
    function closeAwardEvaluationModal() {
      AwardService.awardEvaluationCloseNoErrors(vm.criteria, vm.data.submission.id, vm.data.submission.awardMinGrade, vm.data.submission.awardMaxGrade)
        .success(function (data) {
          $uibModal.open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close"' +
              'ng-click="closeAwardEvaluationModalCtrl.closeAwardEvaluation(false); $close()"><span aria-hidden="true">&times;</span></button>' +
              '<h4 class="modal-title">Zuschlagsbewertung abschliessen</h4>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Die {{closeAwardEvaluationModalCtrl.showSubmittents()}} wird/werden als Zuschlag/Zuschläge vorgeschlagen. ' +
              'Möchten Sie die Zuschlagsbewertung abschliessen und den Zuschlag/die Zuschläge hiermit bestätigen? </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="closeAwardEvaluationModalCtrl.closeAwardEvaluation(true); $close()">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="closeAwardEvaluationModalCtrl.closeAwardEvaluation(false); $close()">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'closeAwardEvaluationModalCtrl',
            backdrop: 'static',
            size: 'lg',
            keyboard: false,
            controller: function () {
              var vm2 = this;
              // Display submittents who are going to receive an award.
              vm2.showSubmittents = function () {
                var text = "";
                var awardRecipientCounter = 0;
                vm2.offers = $filter('orderBy')(vm.offers, 'awardRank');
                vm2.awardedOfferIds = [];
                for (i = 0; i < vm2.offers.length; i++) {
                  if (vm2.offers[i] && !vm2.offers[i].isExcludedFromProcess &&
                    awardRecipientCounter < vm.award.addedAwardRecipients) {
                    text += vm2.offers[i].submittent.companyId.companyName;
                    vm2.awardedOfferIds.push(vm2.offers[i].id);
                    awardRecipientCounter++;
                    if (awardRecipientCounter < vm.award.addedAwardRecipients) {
                      text += "/"
                    }
                  }
                }
                return text;
              };
              // Give award to submittents and close the award evaluation.
              vm2.closeAwardEvaluation = function (response) {
                if (response) {
                  AwardService.closeAwardEvaluation(vm2.awardedOfferIds, vm.data.submission.id, vm.data.submission.version)
                    .success(function (data) {
                      vm.dirtyFlag = false;
                      $state.go($state.current, {
                        displayedCriterionId: null,
                        proceedToClose: null
                      }, {
                        reload: true
                      });
                    }).error(function (response, status) {
                      if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
                        // remove this error message, if already exists
                        vm.notPermittedToEnterCriteriaAssess = false;
                        $anchorScroll('page-title');
                        QFormJSRValidation.markErrors($scope,
                          $scope.awardViewCtrl.awardForm, response);
                      }
                    });
                } else {
                  if ($stateParams.proceedToClose) {
                    defaultReload();
                  }
                  return false;
                }
                return null;
              }
            }
          });
        }).error(function (response, status) {
          if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
            // remove this error message, if already exists
            vm.notPermittedToEnterCriteriaAssess = false;
            $anchorScroll('page-title');
            QFormJSRValidation.markErrors($scope, $scope.awardViewCtrl.awardForm, response);
          }
        });
    }

    function save() {
      ExaminationService.awardLockedByAnotherUser(vm.data.submission.id)
        .success(function (data) {
          proceedWithSaving();
        }).error(function (response, status) {
          if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
            $anchorScroll('page-title');
            QFormJSRValidation
              .markErrors($scope, $scope.awardViewCtrl.awardForm, response);
          }
        });
    }

    function proceedWithSaving() {
      createEmptyCriterion();
      vm.award.criterion = vm.criteria;
      vm.award.submissionId = $stateParams.id;
      vm.award.offers = vm.offers;
      vm.award.pageRequestedOn = vm.data.submission.pageRequestedOn;
      if (vm.award.priceFormula) {
        if (vm.award.priceFormula.value1 === AppConstants.USER_DEFINED) {
          // empty price formula, custom price formula is sent
          vm.award.priceFormula = null;
        } else {
          // empty custom price formula, price formula is sent
          vm.award.customPriceFormula = null;
        }
      }
      if (vm.award.operatingCostFormula) {
        if (vm.award.operatingCostFormula.value1 === AppConstants.USER_DEFINED) {
          // empty operating cost formula, custom operating cost formula is sent
          vm.award.operatingCostFormula = null;
        } else {
          // empty custom operating cost formula, operating cost formula is sent
          vm.award.customOperatingCostFormula = null;
        }
      }
      AppService.setPaneShown(true);
      AwardService.updateAward(vm.award).success(function (data, status) {
        AppService.setPaneShown(false);
        if (status === 403) { // Security checks.
          return;
        }
        vm.saved = true;
        defaultReload();
      }).error(function (response, status) {
        if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
          // remove this error message, if already exists
          AppService.setPaneShown(false);
          vm.notPermittedToEnterCriteriaAssess = false;
          $anchorScroll('page-title');
          QFormJSRValidation
            .markErrors($scope, $scope.awardViewCtrl.awardForm, response);
        }
      });
    }

    function cancelButton(dirtyflag) {
      createEmptyCriterion();
      if (!dirtyflag) {
        defaultReload();
      } else {
        var closeCancelModal = $uibModal.open({
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
            '<button type="button" class="btn btn-primary" ng-click="awardViewCtrl.closeWindowModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'awardViewCtrl',
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
    /**Function to reopen the award evaluation */
    function reopenAwardEvaluation() {
      var reopenTenderStatusModal = AppService
        .reopenTenderStatusModal(vm.reopenTitle.AWARD_EVALUATION, vm.reopenQuestion.AWARD_EVALUATION, vm.reopen.AWARD_EVALUATION);

      return reopenTenderStatusModal.result
        .then(function (response) {
          if (!angular.isUndefined(response)) {
            var reopenForm = {
              reopenReason: response
            };
            AwardService.reopenAwardEvaluation(reopenForm, vm.data.submission.id, vm.data.submission.version)
              .success(function (data) {
                $state.reload();
              }).error(function (response, status) {
                if (status === AppConstants.HTTP_RESPONSES.BAD_REQUEST || status === AppConstants.HTTP_RESPONSES.CONFLICT) { // Validation errors.
                  // remove this error message, if already exists
                  AppService.setPaneShown(false);
                  vm.notPermittedToEnterCriteriaAssess = false;
                  $anchorScroll('page-title');
                  QFormJSRValidation
                    .markErrors($scope, $scope.awardViewCtrl.awardForm, response);
                }
              });
          }
        });
    }
    /**Check if an operating cost award criterion already exists */
    function operatingCostAwardCriterionExists() {
      if (vm.criteria !== null) {
        for (i = 0; i < vm.criteria.length; i++) {
          if (vm.criteria[i].criterionType === "operatingCostAwardCriterion") {
            return true;
          }
        }
      }
      return false;
    }
    /**Check if it is possible to add an operating cost award criterion */
    function canOperatingCostAwardCriterionBeAdded(submissionId) {
      AwardService.canOperatingCostAwardCriterionBeAdded(submissionId)
        .success(function (data) {
          vm.canAddOperatingCostAwardCriterion = data;
        }).error(function (response, status) {});
    }
    /**Function to determine when to add, delete or update an award criterion */
    function buttonOrFieldDisabledForAwardCriterion(process, currentStatus) {
      return !((currentStatus >= vm.status.OFFER_OPENING_CLOSED &&
            (process === 'SELECTIVE' || process === 'OPEN' || process === 'INVITATION') && currentStatus < vm.status.AWARD_EVALUATION_CLOSED) ||
          (process === 'SELECTIVE' && currentStatus >= vm.status.APPLICATION_OPENING_CLOSED &&
            currentStatus < vm.status.SUITABILITY_AUDIT_COMPLETED_S)) ||
        vm.isCompletedOrCancelled;
    }
    /**Function to determine when to add, delete or update a subcriterion */
    function buttonOrFieldDisabledForSubcriterion(process, currentStatus) {
      return !((currentStatus >= vm.status.OFFER_OPENING_CLOSED &&
            (process === 'SELECTIVE' || process === 'OPEN' || process === 'INVITATION') && currentStatus < vm.status.AWARD_EVALUATION_CLOSED) ||
          (process === 'SELECTIVE' && currentStatus >= vm.status.APPLICATION_OPENING_CLOSED &&
            currentStatus < vm.status.SUITABILITY_AUDIT_COMPLETED_S)) ||
        vm.isCompletedOrCancelled;
    }
    /**Function to get the calculation formulas */
    function getCalculationFormulas(tenantId) {
      AwardService.getCalculationFormulas(tenantId)
        .success(function (data) {
          vm.calculationFormulas = $filter('filter')(data, {
            'active': true
          });
          vm.calculationFormulas.push({
            value1: AppConstants.USER_DEFINED
          });
          // set value of the drop down for the formulas,
          // if custom then to custom
          // if none then default
          if (!vm.award.priceFormula.value1) {
            if (vm.award.customPriceFormula) {
              vm.award.priceFormula = vm.calculationFormulas[vm.calculationFormulas.length - 1];
            } else {
              vm.award.priceFormula = vm.calculationFormulas[1];
            }
          }
          if (!vm.award.operatingCostFormula.value1) {
            if (vm.award.customOperatingCostFormula) {
              vm.award.operatingCostFormula = vm.calculationFormulas[vm.calculationFormulas.length - 1];
            } else {
              vm.award.operatingCostFormula = vm.calculationFormulas[1];
            }
          }
        }).error(function (response, status) {});
    }
    /**Function to determine when fields and buttons are disabled for edit */
    function buttonOrFieldDisabledForAwardEvaluationEdit(currentStatus) {
      return !(currentStatus >= vm.status.OFFER_OPENING_CLOSED && currentStatus < vm.status.AWARD_EVALUATION_CLOSED) ||
        vm.isCompletedOrCancelled;
    }
    /**Custom display of calculation formulas according to specifications */
    function customDisplayCalculationFormulas(calculationFormulaValue1, calculationFormulaValue2, isPrice) {
      if (calculationFormulaValue1 === AppConstants.USER_DEFINED) {
        if (isPrice) {
          if (vm.award.customPriceFormula) {
            calculationFormulaValue2 = vm.award.customPriceFormula;
          } else {
            return calculationFormulaValue1;
          }
        } else {
          if (vm.award.customOperatingCostFormula) {
            calculationFormulaValue2 = vm.award.customOperatingCostFormula;
          } else {
            return calculationFormulaValue1;
          }
        }
      }
      return calculationFormulaValue1 + ", " + calculationFormulaValue2;
    }
    /**Display calculation formulas according to specifications and open editor in case of custom formula */
    function customMatchCalculationFormulas(calculationFormulaValue1, calculationFormulaValue2, isPrice) {
      // in case custom is selected, then show the button for the custom editor
      // otherwise hide
      if (isPrice) {
        if (calculationFormulaValue1 === AppConstants.USER_DEFINED) {
          vm.showCustomFormulaPrice = true;
        } else {
          vm.showCustomFormulaPrice = false;
        }
      } else {
        if (calculationFormulaValue1 === AppConstants.USER_DEFINED) {
          vm.showCustomFormulaOperatingCosts = true;
        } else {
          vm.showCustomFormulaOperatingCosts = false;
        }
      }
      return customDisplayCalculationFormulas(calculationFormulaValue1, calculationFormulaValue2, isPrice);
    }
    /**
     * Function to show Submittent (Tenderer) with its Joint Ventures and
     * Subcontractors
     */
    function showSubmittentJointVenturesSubcontractors(offer) {
      return AppService.showSubmittentAndLocation(offer) +
        AppService.showJointVenturesSubcontractors(offer);
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
     * function that returns the offerCriterion grade given an offer
     * and criterionIndex
     */
    function getOfferCriterionGrade(offer, criterionIndex) {
      var offerCriterion = getOfferCriterion(offer, criterionIndex);
      if (offerCriterion) {
        return roundGradeToString(offerCriterion.grade);
      } else {
        return null;
      }
    }
    /**
     * function that returns the offerCriterion score given an offer
     * and criterionIndex
     */
    function getOfferCriterionScore(offer, criterionIndex) {
      var offerCriterion = getOfferCriterion(offer, criterionIndex);
      if (offerCriterion) {
        return roundScoreToString(offerCriterion.score);
      } else {
        return null;
      }
    }
    /**
     * function that returns the offerCriterion given an offer and
     * criterionIndex
     */
    function getOfferCriterion(offer, criterionIndex) {
      var offerCriterion = null;
      for (var i = 0; i < offer.offerCriteria.length; i++) {
        if (offer.offerCriteria[i].criterion.id === vm.criteria[criterionIndex].id) {
          offerCriterion = offer.offerCriteria[i];
        }
      }
      return offerCriterion;
    }
    /**
     * function that returns the offerSubcriterion grade given an
     * offer, criterionIndex and subcriterionIndex
     */
    function getOfferSubcriterionGrade(offer, criterionIndex, subcriterionIndex) {
      var offerSubcriterion = getOfferSubcriterion(offer, criterionIndex, subcriterionIndex);
      if (offerSubcriterion) {
        return roundGradeToString(offerSubcriterion.grade);
      } else {
        return null;
      }
    }
    /**
     * function that returns the offerSubcriterion score given an
     * offer, criterionIndex and subcriterionIndex
     */
    function getOfferSubcriterionScore(offer, criterionIndex, subcriterionIndex) {
      var offerSubcriterion = getOfferSubcriterion(offer, criterionIndex, subcriterionIndex);
      if (offerSubcriterion) {
        return roundScoreToString(offerSubcriterion.score);
      } else {
        return null;
      }
    }
    /**
     * function that returns the offerSubcriterion given an offer,
     * criterionIndex and subcriterionIndex
     */
    function getOfferSubcriterion(offer, criterionIndex, subcriterionIndex) {
      var offerSubcriterion = null;
      for (var i = 0; i < offer.offerSubcriteria.length; i++) {
        if (offer.offerSubcriteria[i].subcriterion.id === vm.criteria[criterionIndex].subcriterion[subcriterionIndex].id) {
          offerSubcriterion = offer.offerSubcriteria[i];
        }
      }
      return offerSubcriterion;
    }

    function filterLessThan(prop, val, index) {
      return function (item, index) {
        if (item[prop] !== null) {
          return item[prop] <= val;
        } else {
          return index < val;
        }
      }
    }
    /**
     * function that rounds the given numbers according to the requirement
     * for score and returns a string so that the trailing zeros are
     * displayed
     */
    function roundGradeToString(input) {
      return (input === null) ? null : input.toFixed(AppConstants.ROUND_DECIMALS.GRADE);
    }
    /**
     * function that rounds the given numbers according to the requirement
     * for score and returns a string so that the trailing zeros are
     * displayed
     */
    function roundScoreToString(input) {
      return (input === null) ? null : input.toFixed(AppConstants.ROUND_DECIMALS.SCORE);
    }
    /** Check for unsaved changes when trying to transition to different state */
    $transitions.onBefore({}, function (transition) {
      if (transition.to() !== transition.from() && vm.dirtyFlag === true) {
        var transitionModal = AppService.transitionModal();
        return transitionModal.result
          .then(function (response) {
            if (response) {
              vm.dirtyFlag = false;
              return true;
            }
            return false;
          });
      }
      return null;
    });
    /**Function to determine when the award evaluation close button is disabled */
    function awardEvaluationCloseButtonDisabled(process, currentStatus) {
      return !((process === "SELECTIVE" && currentStatus >= vm.status.FORMAL_AUDIT_COMPLETED) ||
          ((process === "OPEN" || process === "INVITATION") && currentStatus >= vm.status.SUITABILITY_AUDIT_COMPLETED_AND_AWARD_EVALUATION_STARTED)) ||
        !vm.data.submission.evaluationThrough || vm.isCompletedOrCancelled;
    }
    /**
     * function that returns the style to apply, greyedOut in case of
     * exclusion, none otherwise
     */
    function greyedOut(offer) {
      if (offer.isExcludedFromAwardProcess) {
        return {
          'background-color': '#d3d3d3'
        }; // light grey
      }
      return null;
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
    /** Function to determine when save and cancel buttons are disabled */
    function saveAndCancelButtonsDisabled() {
      return (!(vm.secUpdateCriterion || vm.secUpdateSubcriterion || vm.secAwardEvaluationEdit) ||
          (vm.buttonOrFieldDisabledForAwardCriterion(vm.data.submission.process, vm.currentStatus) &&
            vm.buttonOrFieldDisabledForSubcriterion(vm.data.submission.process, vm.currentStatus) &&
            vm.buttonOrFieldDisabledForAwardEvaluationEdit(vm.currentStatus))) ||
        vm.isCompletedOrCancelled;
    }
    /** Function to ignore error in case "minus" is entered first in fields with type number*/
    function validateNumberInputForMinus(form) {
      if (!form.awardMinGrade.$viewValue || !form.addedAwardRecipients.$viewValue ||
        !form.awardMaxGrade.$viewValue) {
        $scope.awardViewCtrl.awardForm.$dirty = false;
        $scope.awardViewCtrl.awardForm.$invalid = false;
      }
    }
    /** Function to create an empty criterion if needed */
    function createEmptyCriterion() {
      // If no criterion accordion is open, create an empty criterion with null id
      // so that there is no error when reloading the page.
      if (vm.displayedCriterion === null) {
        vm.displayedCriterion = {};
        vm.displayedCriterion.id = null;
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
              displayedCriterionId: vm.displayedCriterion.id,
              proceedToClose: response
            }, {
              reload: true
            });
          }
        });
      } else {
        closeAwardEvaluationModal();
        return null;
      }
    }
    /** Function to navigate to current state with default parameters */
    function defaultReload() {
      if (vm.dirtyFlag) {
        vm.dirtyFlag = false;
      }
      $state.go($state.current, {
        displayedCriterionId: vm.displayedCriterion.id,
        proceedToClose: null
      }, {
        reload: true
      });
    }
    /** Function to display object information */
    function getObjectInfo(object) {
      return AppService.getObjectInfo(object);
    }
    /** Function to check if it is allowed to navigate to Zuschlagskriterien bewerten.
     *  It is allowed to enter this function only if the data are of this form are already saved */
    function criteriaAssess() {
      if (vm.saved) {
        $state.go('award.criteriaAssess', {
          id: vm.data.submission.id,
          submission: vm.data.submission,
          criteria: vm.criteria
        }, {
          reload: true
        });
      } else {
        vm.notPermittedToEnterCriteriaAssess = true;
      }
    }
    /** Function to display a modal window, where the user can insert
     *  his custom calculation formula for the price */
    function openCustomFormulaPrice() {
      openCustomFormula(vm.award.customPriceFormula, true);
    }
    /** Function to display a modal window, where the user can insert
     *  his custom calculation formula for the operating costs */
    function openCustomFormulaOperatingCosts() {
      openCustomFormula(vm.award.customOperatingCostFormula, false);
    }
    /** Function to display a modal window, where the user can insert
     *  his custom calculation formula */
    function openCustomFormula(formula, isPrice) {
      if (!vm.data.submission.evaluationThrough) {
        // If the form has not been saved yet, show modal informing the user to save the form before adding a custom formula.
        return showUnsavedFormModal();
      } else {
        var title = (isPrice) ? AppConstants.CUSTOM_FORMULA_PRICE_TITLE : AppConstants.CUSTOM_FORMULA_OPERATING_COSTS_TITLE;
        var customFormulaModal = $uibModal.open({
          templateUrl: 'app/awardEvaluation/customFormula.html',
          controller: 'CustomFormulaController',
          controllerAs: 'customFormulaCtrl',
          backdrop: 'static',
          keyboard: false,
          resolve: {
            title: function () {
              return title;
            },
            formula: function () {
              return formula;
            },
            isPrice: function () {
              return isPrice;
            },
            submissionId: function () {
              return vm.data.submission.id;
            },
            displayedCriterionId: function () {
              return vm.displayedCriterion.id;
            }
          }
        });
        return customFormulaModal.result
          .then(function (response) {
            // update the according field with the custom calculation formula
            if (!angular.isUndefined(response)) {
              if (isPrice) {
                vm.award.customPriceFormula = response;
              } else {
                vm.award.customOperatingCostFormula = response;
              }
              if (vm.dirtyFlag) {
                vm.dirtyFlag = false;
              }
            }
          });
      }
    }
    /** Function to show modal informing the user to save the form before adding a custom formula */
    function showUnsavedFormModal() {
      return $uibModal.open({
        template: '<div class="modal-header">' +
          '<button type="button" class="close" aria-label="Close"' +
          'ng-click="unsavedFormCtrl.closeUnsavedFormModal(); $close()"><span aria-hidden="true">&times;</span></button>' +
          '<h4 class="modal-title"></h4>' +
          '</div>' +
          '<div class="modal-body">' +
          '<div class="row">' +
          '<div class="col-md-12">' +
          '<p> Sie müssen mindestens einmal die Maske speichern, um eine benutzerdefinierte Formel eingeben zu können. </p>' +
          '</div>' +
          '</div>' +
          '</div>' +
          '<div class="modal-footer">' +
          '<button type="button" class="btn btn-primary" ng-click="unsavedFormCtrl.closeUnsavedFormModal(); $close()">OK</button>' +
          '</div>' + '</div>',
        controllerAs: 'unsavedFormCtrl',
        backdrop: 'static',
        keyboard: false,
        controller: function () {
          var vm = this;
          vm.closeUnsavedFormModal = function () {
            return null;
          };
        }
      });
    }
    /**
     * Function to export criteria to xlsx
     *
     */
    function exportCriteria() {
      ExaminationService.awardLockedByAnotherUser($stateParams.id)
        .success(function (data) {
          proceedWithExportCriteria();
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            $anchorScroll('page-title');
            QFormJSRValidation
              .markErrors($scope, $scope.awardViewCtrl.awardForm, response);
          }
        });
    }

    function proceedWithExportCriteria() {
      ExaminationService.exportOfferCriteria($stateParams.id, AppConstants.CRITERION_TYPE.AWARD)
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

    function exportEMLCriteria() {
      ExaminationService.awardLockedByAnotherUser($stateParams.id)
        .success(function (data) {
          proceedWithexportEMLCriteria();
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            $anchorScroll('page-title');
            QFormJSRValidation
              .markErrors($scope, $scope.awardViewCtrl.awardForm, response);
          }
        });
    }

    function proceedWithexportEMLCriteria() {
      ExaminationService.exportEMLOfferCriteria($stateParams.id, AppConstants.CRITERION_TYPE.AWARD)
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

    /** Function to import award */
    function uploadFile(flow) {
      AwardService.checkAwardOptimisticLock(vm.data.submission.id, vm.data.submission.pageRequestedOn)
        .success(function (response, status, headers) {
          flow.upload();
        }).error(function (response, status) {
          if (status === 409) { // Validation errors.
            $anchorScroll('page-title');
            QFormJSRValidation
              .markErrors($scope, $scope.awardViewCtrl.awardForm, response);
          }
        });
    }

    function uploaded(flow) {
      var file = flow.files[0];
      ExaminationService.checkforchangesCriteria($stateParams.id, file.uniqueIdentifier, AppConstants.CRITERION_TYPE.AWARD)
        .success(function (changed) {
          var hasBeenChanged = changed.message;
          if (changed.path === "true") {
            var modalResult = AppService
              .openGenericModal("Bestätigungsdialog", hasBeenChanged, "Möchten Sie die vorhandenen Daten wirklich überschreiben?");
            return modalResult.result
              .then(function (response) {
                if (response) {
                  ExaminationService.importOfferCriteria($stateParams.id, file.uniqueIdentifier, AppConstants.CRITERION_TYPE.AWARD)
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
      return $uibModal.open({
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
  }
})();
