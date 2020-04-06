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
 * @name formalAudit.controller.js
 * @description FormalAuditController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.examination")
    .controller("SuitabilityAuditController", SuitabilityAuditController);

  /** @ngInject */
  function SuitabilityAuditController($rootScope, $scope, $state, $stateParams, QFormJSRValidation,
    SubmissionService, ExaminationService, NgTableParams, $filter, $element, $uibModal, AppService, AppConstants) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var i;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.submittent = {};
    vm.offer = [];
    vm.tableParams = new NgTableParams({}, {
      dataset: vm.offers
    });
    vm.mustCriterion = [];
    vm.offer.mustCriterion = [];
    vm.evaluatedCriterion = [];
    vm.subcriterion = [];
    vm.criterionGrade = [];
    vm.subcriterionGrade = [];
    vm.notPermittedGrade = false;
    vm.differentValues = false;
    vm.criteria = [];
    // Initialize security variable
    vm.secFormalAuditExecute = null;
    vm.secFormalAuditClose = false;
    vm.secSuitabilityExecute = false;
    vm.options = [{
        value: true,
        label: 'Erfüllt'
      },
      {
        value: false,
        label: 'Nicht erfüllt'
      }
    ];
    vm.formalExaminationFulfilled = [{
        value: true,
        label: 'Ja'
      },
      {
        value: false,
        label: 'Nein'
      }
    ];
    vm.status = AppConstants.STATUS;
    vm.group = AppConstants.GROUP;
    vm.tableStyle = AppService.customTableStyle();
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    // Activating the controller.
    activate();
    vm.save = save;
    vm.closeFormalAudit = closeFormalAudit;
    vm.calculateTotalPoint = calculateTotalPoint;
    vm.calculatePoint = calculatePoint;
    vm.calculateStatus = calculateStatus;
    vm.calculateSubcriterionPoint = calculateSubcriterionPoint;
    vm.calculatePointWithSubcriteria = calculatePointWithSubcriteria;
    vm.getOfferCriterionGrade = getOfferCriterionGrade;
    vm.getOfferCriterion = getOfferCriterion;
    vm.getOfferSubcriterionGrade = getOfferSubcriterionGrade;
    vm.getOfferSubcriterion = getOfferSubcriterion;
    vm.rankOffers = rankOffers;
    vm.showSubmittentJointVenturesSubcontractors = showSubmittentJointVenturesSubcontractors;
    vm.showSubmittentAndLocation = showSubmittentAndLocation;
    vm.showJointVenturesSubcontractors = showJointVenturesSubcontractors;
    vm.setMandatoryNotes = setMandatoryNotes;
    vm.cancelButton = cancelButton;
    vm.proofAndCalculateScoreCriterion = proofAndCalculateScoreCriterion;
    vm.proofAndCalculateScoreSubcriterion = proofAndCalculateScoreSubcriterion;
    vm.formatAmount = formatAmount;
    vm.buttonOrFieldDisabled = buttonOrFieldDisabled;
    vm.getObjectInfo = getObjectInfo;
    vm.evaluatedCriteriaColspan = evaluatedCriteriaColspan;
    vm.resultsColspan = resultsColspan;
    vm.markRed = markRed;
    vm.loggedInUser = AppService.getLoggedUser();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      readStatusOfSubmission($stateParams.id);
      readSubmission($stateParams.id);
      readCriteriaOfSubmission($stateParams.id);
      findUserOperations();
      readOfferCriteria($stateParams.id);
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
        }).error(function (response, status) {

        });
    }

    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id)
        .success(function (data) {
          vm.currentStatus = data;
        }).error(function (response, status) {

        });
    }

    function markRed(offer) {
      if (offer.submittent.subcontractors.length > 0) {
        for (var i = 0; i < offer.submittent.subcontractors.length; i++) {
          if (offer.submittent.subcontractors[i].proofStatusFabe === 4 || (offer.submittent.subcontractors[i].proofStatusFabe === 3 && vm.loggedInUser.tenant.isMain)) {
            return true;
          }
        }
      }
      if (offer.submittent.jointVentures.length > 0) {
        for (var i = 0; i < offer.submittent.jointVentures.length; i++) {
          if (offer.submittent.jointVentures[i].proofStatusFabe === 4 || (offer.submittent.jointVentures[i].proofStatusFabe === 3 && vm.loggedInUser.tenant.isMain)) {
            return true;
          }
        }
      }
      return false;
    }

    function readOfferCriteria(id) {
      AppService.setPaneShown(true);
      ExaminationService.getExaminationSubmittentListWithCriteria(id, AppConstants.CRITERION_TYPE.SUITABILITY, true).success(function (data) {
        vm.offers = data;
        for (var i = 0; i < vm.offers.length; i++) {
          vm.offers[i].mustCriterion = [];
          vm.offers[i].evaluatedCriterion = [];
          // create different object for UI requirements. Add G (Gewichtung) in frond of every evaluated criteria,
          // dropdown fields for must criteria etc.
          for (var j = 0; j < vm.offers[i].offerCriteria.length; j++) {
            if (vm.offers[i].offerCriteria[j].criterion.criterionType === "mustCriterion") {
              vm.offers[i].mustCriterion.push(vm.offers[i].offerCriteria[j]);
            }
            if (vm.offers[i].offerCriteria[j].criterion.criterionType === "evaluatedCriterion") {
              vm.offers[i].evaluatedCriterion.push(vm.offers[i].offerCriteria[j]);
            }
          }
          for (var j = 0; j < vm.offers[i].offerSubcriteria.length; j++) {
            vm.subcriterion.push(vm.offers[i].offerSubcriteria[j]);
            for (var x = 0; x < vm.offers[i].evaluatedCriterion.length; x++) {
              vm.offers[i].evaluatedCriterion[x].subcriterion = [];
              for (var y = 0; y < vm.offers[i].evaluatedCriterion[x].criterion.subcriterion.length; y++) {
                if (vm.offers[i].offerSubcriteria[j].subcriterion.id === vm.offers[i].evaluatedCriterion[x].criterion.subcriterion[y].id) {
                  // set grade of subcriterion to offerSubcriterion grade that is already set (used to show the grade in the input field/
                  vm.offers[i].evaluatedCriterion[x].criterion.subcriterion[y].grade = vm.offers[i].offerSubcriteria[j].grade;
                }
              }
            }
          }
        }
        AppService.setPaneShown(false);
        // in order to freeze the first left column and two first top rows
        // we use jquery plugin tableHeadFixer.
        // it doesn't work if applied before the html form is loaded,
        // that is why we set a zero timeout
        setTimeout(function () {
          $("#fixTable").tableHeadFixer({
            "left": 1
          });
        }, 0);
      }).error(function (response, status) {

      });
    }

    function setMandatoryNotes(offers) {
      for (var i = 0; i < offers.length; i++) {
        var status = calculateStatus(offers[i].submittent.existsExclusionReasons, offers[i].submittent.formalExaminationFulfilled, offers[i].mustCriterion);
        if (offers[i].qExExaminationIsFulfilled != null && offers[i].qExExaminationIsFulfilled !== status) {
          vm.differentValues = true;
          break;
        }
        if (offers[i].qExExaminationIsFulfilled === status) {
          vm.differentValues = false;
        }
      }
    }

    function save(form) {
      var suitabilities = [];
      // create suitability in order to send it to ExaminationService.
      for (var i = 0; i < vm.offers.length; i++) {
        var suitability = {
          qExStatus: calculateStatus(vm.offers[i].submittent.existsExclusionReasons,
            vm.offers[i].submittent.formalExaminationFulfilled, vm.offers[i].mustCriterion),
          offerId: vm.offers[i].id,
          qExRank: vm.offers[i].qExRank,
          qExSuitabilityNotes: vm.offers[i].qExSuitabilityNotes,
          qExExaminationIsFulfilled: vm.offers[i].qExExaminationIsFulfilled,
          mustCriterion: [],
          evaluatedCriterion: [],
          offerSubcriteria: []
        };

        for (var j = 0; j < vm.offers[i].mustCriterion.length; j++) {
          var mustCriterion = {
            criterionId: vm.offers[i].mustCriterion[j].id,
            isFulfilled: vm.offers[i].mustCriterion[j].isFulfilled
          };
          suitability.mustCriterion.push(mustCriterion);
        }

        for (var j = 0; j < vm.offers[i].evaluatedCriterion.length; j++) {
          var evaluatedCriterion = {};
          if (vm.offers[i].evaluatedCriterion[j].criterion.subcriterion.length === 0) {
            evaluatedCriterion.criterionId = vm.offers[i].evaluatedCriterion[j].id;
            if (vm.offers[i].evaluatedCriterion[j].grade >= vm.data.submission.minGrade &&
              vm.offers[i].evaluatedCriterion[j].grade <= vm.data.submission.maxGrade) {
              evaluatedCriterion.grade = vm.offers[i].evaluatedCriterion[j].grade;
            } else {
              evaluatedCriterion.grade = null;
            }
            evaluatedCriterion.score = vm.offers[i].evaluatedCriterion[j].grade * (vm.offers[i].evaluatedCriterion[j].criterion.weighting / 100);
          } else {
            evaluatedCriterion.criterionId = vm.offers[i].evaluatedCriterion[j].id;
            evaluatedCriterion.score = calculatePointWithSubcriteria(vm.offers[i].evaluatedCriterion[j]);
            evaluatedCriterion.grade = getOfferCriterionGrade(vm.offers[i].evaluatedCriterion[j]);
          }
          suitability.evaluatedCriterion.push(evaluatedCriterion);
          for (var x = 0; x < vm.offers[i].evaluatedCriterion[j].criterion.subcriterion.length; x++) {
            var offerSubcriterion = {
              offerSubcriterionId: vm.offers[i].evaluatedCriterion[j].criterion.subcriterion[x].id,
              score: vm.offers[i].evaluatedCriterion[j].criterion.subcriterion[x].grade * (vm.offers[i].evaluatedCriterion[j].criterion.subcriterion[x].weighting / 100)
            };
            if (vm.offers[i].evaluatedCriterion[j].criterion.subcriterion[x].grade >= vm.data.submission.minGrade &&
              vm.offers[i].evaluatedCriterion[j].criterion.subcriterion[x].grade <= vm.data.submission.maxGrade) {
              offerSubcriterion.grade = vm.offers[i].evaluatedCriterion[j].criterion.subcriterion[x].grade;
            } else {
              offerSubcriterion.grade = null;
            }
            suitability.offerSubcriteria.push(offerSubcriterion);
          }
        }
        suitability.qExTotalGrade = calculateTotalPoint(vm.offers[i].evaluatedCriterion);
        suitabilities.push(suitability);
      }
      ExaminationService.updateOfferCriteria(suitabilities)
        .success(function (data) {
          ExaminationService.updateSubmissionFormalAuditExaminationStatus($stateParams.id).success(function (data) {
            $state.reload();
          });
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.suitabilityAuditCtrl.evidenceForm, response);
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

    function cancelButton(dirtyflag, goBack) {
      if (!dirtyflag) {
        if (goBack) {
          $state.go('examination.view', {
            id: vm.data.submission.id
          }, {
            reload: true
          });
        } else {
          $state.go($state.current, {}, {
            reload: true
          });
        }
      } else {
        var closeProjectEditModal = $uibModal.open({
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
            '<button type="button" class="btn btn-primary" ng-click="formalAuditCtrl.closeWindowModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'formalAuditCtrl',
          backdrop: 'static',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeWindowModal = function (reason) {
              closeProjectEditModal.dismiss(reason);
            };
          }

        });

        return closeProjectEditModal.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            if (goBack) {
              $state.go('examination.view', {
                id: vm.data.submission.id
              }, {
                reload: true
              });
            } else {
              $state.go($state.current, {}, {
                reload: true
              });
            }
          } else {
            // Do something else here
            return false;
          }
          return null;
        });
      }
      return null;
    }

    function closeFormalAudit() {
      var openCloseFormalAuditModal = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">Formelle Prüfung abschliessen</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie die Formelle Prüfung wirklich abschliessen? </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="formalAuditCtrl.closeModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'formalAuditCtrl',
          backdrop: 'static',
          size: 'lg',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeModal = function (reason) {
              openCloseFormalAuditModal.dismiss(reason);
            };
          }

        });
      return openCloseFormalAuditModal.result
        .then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            ExaminationService.closeFormalAudit(vm.data.submission.id)
              .success(function (data) {
                $state.go($state.current, {}, {
                  reload: true
                });
              })
          } else {
            // Do something else here
            return false;
          }
          return null;
        });
    }

    function readCriteriaOfSubmission(id) {
      ExaminationService.readCriteriaOfSubmission(id)
        .success(function (data) {
          vm.criteria = data;
          for (i = 0; i < vm.criteria.length; i++) {
            if (vm.criteria[i].criterionType === "mustCriterion") {
              vm.mustCriterion.push(vm.criteria[i]);
              vm.offer.mustCriterion.push(vm.criteria[i]);
              // copy object in order not to updated when field is updated.
              vm.showMustCriterion = angular.copy(vm.mustCriterion);
            }
            if (vm.criteria[i].criterionType === "evaluatedCriterion") {
              vm.evaluatedCriterion.push(vm.criteria[i]);
              // copy object in order not to updated when field is updated.
              vm.showEvaluatedCriterion = angular.copy(vm.evaluatedCriterion);
            }
          }
        }).error(function (response, status) {});
    }
    /*
     * function that sorts offers according to their total score and if they
     * have the same total score according to the company name
     */
    function rankOffers() {
      var sortedOffers = vm.offers.slice();
      /*
       * add company name because the sorting function can not see deeper
       * as the first level
       */
      for (var k = 0; k < sortedOffers.length; k++) {
        sortedOffers[k].companyName = sortedOffers[k].submittent.companyId.companyName;
      }
      sortedOffers.sort(function (a, b) {
        var qExTotalGrade1 = a['qExTotalGrade'];
        var qExTotalGrade2 = b['qExTotalGrade'];
        if (qExTotalGrade1 === null ||
          qExTotalGrade1 < qExTotalGrade2) {
          return 1;
        } else if (qExTotalGrade2 === null ||
          qExTotalGrade1 > qExTotalGrade2) {
          return -1;
        } else {
          /* if equal sort by company name alphabetically */
          var companyName1 = a['companyName'];
          var companyName2 = b['companyName'];
          return companyName1.localeCompare(companyName2);
        }
      });
      for (var i = 0; i < sortedOffers.length; i++) {
        for (var j = 0; j < vm.offers.length; j++) {
          if (vm.offers[j].id === sortedOffers[i].id) {
            vm.offers[j].qExRank = i + 1;
          }
        }
      }
    }
    /*
     * function that returns the total point (score).
     */
    function calculateTotalPoint(evaluatedCriteria) {
      var totalPoint = 0.000;
      var totalSub = 0;
      for (var i = 0; i < evaluatedCriteria.length; i++) {
        if (vm.data.submission && evaluatedCriteria[i].grade >= vm.data.submission.minGrade &&
          evaluatedCriteria[i].grade <= vm.data.submission.maxGrade) {
          if (evaluatedCriteria[i].criterion.subcriterion.length === 0) {
            totalPoint += evaluatedCriteria[i].grade * (evaluatedCriteria[i].criterion.weighting / 100);
          } else {
            totalSub += parseFloat(calculatePointWithSubcriteria(evaluatedCriteria[i]));
          }
        }
      }
      totalPoint = totalPoint + totalSub;
      if (evaluatedCriteria.length > 0) {
        rankOffers();
      }
      return AppService.formatScore(roundScore(totalPoint));
    }
    /*
     * function that returns the offerCriterion grade given a criterion
     */
    function getOfferCriterionGrade(evaluatedCriterion) {
      var totalGrade = null;
      for (var i = 0; i < evaluatedCriterion.criterion.subcriterion.length; i++) {
        if (vm.data.submission && evaluatedCriterion.criterion.subcriterion[i].grade >= vm.data.submission.minGrade &&
          evaluatedCriterion.criterion.subcriterion[i].grade <= vm.data.submission.maxGrade) {
          totalGrade += evaluatedCriterion.criterion.subcriterion[i].grade * (evaluatedCriterion.criterion.subcriterion[i].weighting);
        }
      }
      if (totalGrade) {
        return roundGradeToString(totalGrade / 100);
      } else if (!evaluatedCriterion.criterion.subcriterion.length > 0) {
        return roundGradeToString(evaluatedCriterion.grade);
      } else {
        return null;
      }
    }
    /*
     * function that returns the point (score) of a criterion when subcriteria are added.
     */
    function calculatePointWithSubcriteria(evaluatedCriterion) {
      var totalGrade = null;
      for (var i = 0; i < evaluatedCriterion.criterion.subcriterion.length; i++) {
        if (vm.data.submission && evaluatedCriterion.criterion.subcriterion[i].grade >= vm.data.submission.minGrade &&
          evaluatedCriterion.criterion.subcriterion[i].grade <= vm.data.submission.maxGrade) {
          totalGrade += evaluatedCriterion.criterion.subcriterion[i].grade * (evaluatedCriterion.criterion.subcriterion[i].weighting);
        }
      }
      if (totalGrade) {
        var returnValue = totalGrade / 100 * (evaluatedCriterion.criterion.weighting / 100);
        return AppService.formatScore(roundScore(returnValue));
      }
      return AppService.formatScore(0);
    }
    /*
     * function that returns the offerCriterion given an offerIndex and
     * criterionIndex
     */
    function getOfferCriterion(offerIndex, criterionIndex) {
      var offerCriterion = null;
      var curOffer = vm.offers[offerIndex];
      for (var i = 0; i < curOffer.offerCriteria.length; i++) {
        if (curOffer.offerCriteria[i].criterion.id === vm.criteria[criterionIndex].id) {
          offerCriterion = curOffer.offerCriteria[i];
        }
      }
      return offerCriterion;
    }
    /*
     * function that returns the offerSubcriterion grade given an offerIndex,
     * criterionIndex and subcriterionIndex
     */
    function getOfferSubcriterionGrade(offerIndex, criterionIndex,
      subcriterionIndex) {
      var subcriterion = getOfferSubcriterion(offerIndex,
        criterionIndex, subcriterionIndex);
      if (subcriterion) {
        return roundGradeToString(subcriterion.grade);
      }
      return null;
    }
    /*
     * function that returns the offerSubcriterion given an offerIndex,
     * criterionIndex and subcriterionIndex
     */
    function getOfferSubcriterion(offerIndex, criterionIndex,
      subcriterionIndex) {
      var offerSubcriterion = null;
      var curOffer = vm.offers[offerIndex];
      for (var i = 0; i < curOffer.offerSubcriteria.length; i++) {
        if (vm.criteria[criterionIndex].subcriterion[subcriterionIndex] &&
          curOffer.offerSubcriteria[i].subcriterion.id === vm.criteria[criterionIndex].subcriterion[subcriterionIndex].id) {
          offerSubcriterion = curOffer.offerSubcriteria[i];
        }
      }
      return offerSubcriterion;
    }

    /*
     * function that returns the point (score) of a criterion when subcriteria are not added.
     */
    function calculatePoint(evaluatedCriterion) {
      if (vm.data.submission && evaluatedCriterion.grade >= vm.data.submission.minGrade &&
        evaluatedCriterion.grade <= vm.data.submission.maxGrade) {
        vm.evaluatedCriterion.point = evaluatedCriterion.grade * (evaluatedCriterion.criterion.weighting / 100);
        return AppService.formatScore(roundScore(vm.evaluatedCriterion.point));
      }
      vm.evaluatedCriterion.point = null;
      return null;
    }

    function proofAndCalculateScoreCriterion(evaluatedCriterion, offerIndex, criterionIndex) {
      // in case the field has the value (-) or (.) it is saved a undefined,
      // so show error
      if (typeof vm.criterionGrade[offerIndex][criterionIndex] === AppConstants.UNDEFINED) {
        vm.notPermittedGrade = true;
      } else {
        evaluatedCriterion.grade = vm.criterionGrade[offerIndex][criterionIndex];
        calculatePoint(evaluatedCriterion);
        if (evaluatedCriterion.grade != null && (evaluatedCriterion.grade > vm.data.submission.maxGrade ||
            evaluatedCriterion.grade < vm.data.submission.minGrade)) {
          vm.notPermittedGrade = true;
        } else {
          vm.notPermittedGrade = false;
        }
        /* grade and score need to be rounded */
        var roundedCriterionGrade = roundGrade(evaluatedCriterion.grade);
        if (evaluatedCriterion.grade !== roundedCriterionGrade) {
          vm.notRoundedGrade = true;
        } else {
          vm.notRoundedGrade = false;
        }
        evaluatedCriterion.grade = roundedCriterionGrade;
      }
    }

    function proofAndCalculateScoreSubcriterion(evaluatedCriterion, offerIndex, criterionIndex, subcriterionIndex) {
      // in case the field has the value (-) or (.) it is saved a undefined,
      // so show error
      if (typeof vm.subcriterionGrade[offerIndex][criterionIndex][subcriterionIndex] === AppConstants.UNDEFINED) {
        vm.notPermittedGrade = true;
      } else {
        evaluatedCriterion.grade = vm.subcriterionGrade[offerIndex][criterionIndex][subcriterionIndex];
        calculateSubcriterionPoint(evaluatedCriterion);
        if (evaluatedCriterion.grade != null && (evaluatedCriterion.grade > vm.data.submission.maxGrade ||
            evaluatedCriterion.grade < vm.data.submission.minGrade)) {
          vm.notPermittedGrade = true;
        } else {
          vm.notPermittedGrade = false;
        }
        /* grade and score need to be rounded */
        var roundedCriterionGrade = roundGrade(evaluatedCriterion.grade);
        if (evaluatedCriterion.grade !== roundedCriterionGrade) {
          vm.notRoundedGrade = true;
        } else {
          vm.notRoundedGrade = false;
        }
        evaluatedCriterion.grade = roundedCriterionGrade;
      }
    }
    /*
     * function that returns the point (score) of a subcriterion.
     */
    function calculateSubcriterionPoint(subcriterion) {
      if (vm.data.submission && subcriterion.grade >= vm.data.submission.minGrade &&
        subcriterion.grade <= vm.data.submission.maxGrade) {
        vm.subcriterion.point = subcriterion.grade * (subcriterion.weighting / 100);
        return AppService.formatScore(roundScore(vm.subcriterion.point));
      }
      vm.subcriterion.point = null;
      return null;
    }

    function findUserOperations() {
      vm.secFormalAuditExecute = AppService.isOperationPermitted(AppConstants.OPERATION.FORMAL_AUDIT_EXECUTE, null);
      vm.secExaminationClose = AppService.isOperationPermitted(AppConstants.OPERATION.EXAMINATION_CLOSE, null);
      vm.secFormalAuditClose = AppService.isOperationPermitted(AppConstants.OPERATION.FORMAL_AUDIT_CLOSE, null);
      vm.secSuitabilityExecute = AppService.isOperationPermitted(AppConstants.OPERATION.SUITABILITY_EXECUTE, null);
    }

    /* function that rounds the given numbers according to the requirement for grade */
    function roundGrade(input) {
      if (input !== null && !angular.isUndefined(input)) {
        return parseFloat(input.toFixed(AppConstants.ROUND_DECIMALS.GRADE));
      }
      return null;
    }

    /* function that rounds the given numbers according to the requirement for score */
    function roundScore(input) {
      return parseFloat(input.toFixed(AppConstants.ROUND_DECIMALS.SCORE));
    }

    /* function that rounds the given numbers according to the requirement for score
     * and returns a string so that the trailing zeros are displayed
     */
    function roundGradeToString(input) {
      return (input == null) ? null : input.toFixed(AppConstants.ROUND_DECIMALS.GRADE);
    }

    /*
     * function that returns the status.
     */
    function calculateStatus(existsExclusionReasons, formalExaminationFulfilled, mustCriterion) {
      var returnValue;
      if (!formalExaminationFulfilled || existsExclusionReasons) {
        returnValue = false;
      } else {
        returnValue = true;
      }
      for (var i = 0; i < mustCriterion.length; i++) {
        if (mustCriterion[i].isFulfilled != null) {
          if (!mustCriterion[i].isFulfilled) {
            returnValue = false;
            break;
          } else {
            if (!formalExaminationFulfilled || existsExclusionReasons) {
              returnValue = false;
              break;
            }
          }
        }
      }
      return returnValue;
    }

    /** Format number according to specifications */
    function formatAmount(num) {
      return AppService.formatAmount(num);
    }

    /** Function to disable buttons and fields */
    function buttonOrFieldDisabled() {
      if (vm.data.submission) {
        return (!vm.secSuitabilityExecute || vm.data.submission.examinationIsLocked ||
          (vm.data.submission.process !== AppConstants.PROCESS.SELECTIVE &&
            (vm.currentStatus >= vm.status.SUITABILITY_AUDIT_COMPLETED ||
              vm.currentStatus < vm.status.OFFER_OPENING_CLOSED)) ||
          (vm.data.submission.process === AppConstants.PROCESS.SELECTIVE &&
            (vm.currentStatus < vm.status.APPLICATION_OPENING_CLOSED ||
              vm.currentStatus >= vm.status.SUITABILITY_AUDIT_COMPLETED_S)));
      }
      return null;
    }

    /** Function to display object information */
    function getObjectInfo(object) {
      return AppService.getObjectInfo(object);
    }

    /** Function to calculate colspan for evaluated criteria */
    function evaluatedCriteriaColspan(evaluatedCriteria) {
      var colspan = 0;
      if (evaluatedCriteria) {
        colspan = evaluatedCriteria.length * 3;
        for (i in evaluatedCriteria) {
          if (evaluatedCriteria[i] && evaluatedCriteria[i].subcriterion) {
            colspan += evaluatedCriteria[i].subcriterion.length * 3;
          }
        }
      }
      return colspan;
    }

    /** Function to define colspan for results */
    function resultsColspan(evaluatedCriteria) {
      if (evaluatedCriteria) {
        return 4;
      }
      return 3;
    }
  }
})();
