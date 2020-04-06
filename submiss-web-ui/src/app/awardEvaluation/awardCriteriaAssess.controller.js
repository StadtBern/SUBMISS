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
 * @name awardCriteriaAssess.controller.js
 * @description AwardCriteriaAssessController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.award")
    .controller("AwardCriteriaAssessController", AwardCriteriaAssessController);

  /** @ngInject */
  function AwardCriteriaAssessController($rootScope, $scope, $state,
    $stateParams, SubmissionService, ExaminationService, AwardService,
    AppConstants, $filter, AppService, $uibModal) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.criteria = [];
    vm.offers = [];
    vm.notPermittedGrade = false;
    vm.notRoundedGrade = false;
    vm.criterionGrade = [];
    vm.subcriterionGrade = [];
    vm.group = AppConstants.GROUP;
    vm.criterionType = AppConstants.CRITERION_TYPE;
    vm.status = AppConstants.STATUS;
    vm.tableStyle = AppService.customTableStyle();
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.readSubmission = readSubmission;
    vm.readCriteriaOfSubmission = readCriteriaOfSubmission;
    vm.readOfferCriteriaOfSubmission = readOfferCriteriaOfSubmission;
    vm.showSubmittentJointVenturesSubcontractors = showSubmittentJointVenturesSubcontractors;
    vm.showSubmittentAndLocation = showSubmittentAndLocation;
    vm.showJointVenturesSubcontractors = showJointVenturesSubcontractors;
    vm.getOfferCriterionGrade = getOfferCriterionGrade;
    vm.getOfferCriterionScore = getOfferCriterionScore;
    vm.getOfferCriterion = getOfferCriterion;
    vm.proofAndCalculateScoreCriterion = proofAndCalculateScoreCriterion;
    vm.getOfferSubcriterionGrade = getOfferSubcriterionGrade;
    vm.getOfferSubcriterionScore = getOfferSubcriterionScore;
    vm.getOfferSubcriterion = getOfferSubcriterion;
    vm.proofAndCalculateScoreSubcriterion = proofAndCalculateScoreSubcriterion;
    vm.calculateTotalScore = calculateTotalScore;
    vm.showInputField = showInputField;
    vm.rankOffers = rankOffers;
    vm.save = save;
    vm.greyedOut = greyedOut;
    vm.roundScoreToString = roundScoreToString;
    vm.formatAmount = formatAmount;
    vm.customRoundNumber = customRoundNumber;
    vm.criteriaAssessFormDisabled = criteriaAssessFormDisabled;
    vm.getObjectInfo = getObjectInfo;
    vm.cancelButton = cancelButton;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      readSubmission($stateParams.id);
      readCriteriaOfSubmission($stateParams.id);
      readOfferCriteriaOfSubmission($stateParams.id);
      readStatusOfSubmission($stateParams.id);
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
    /** Function to get the current status of the submission */
    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id)
        .success(function (data) {
          vm.currentStatus = data;
        }).error(function (response, status) {});
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
              vm.criteria.push(data[i]);
            }
          }
        }).error(function (response, status) {});
    }

    function readOfferCriteriaOfSubmission(id) {
      AppService.setPaneShown(true);
      ExaminationService
        .readOfferCriteria(id, AppConstants.CRITERION_TYPE.AWARD)
        .success(function (data) {
          vm.offers = data;
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
        }).error(function (response, status) {});
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
      }
      return null;
    }
    /**
     * function that returns the offerCriterion score given an offer
     * and criterionIndex
     */
    function getOfferCriterionScore(offer, criterionIndex) {
      var offerCriterion = getOfferCriterion(offer, criterionIndex);
      if (offerCriterion) {
        return roundScoreToString(offerCriterion.score);
      }
      return null;
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
     * function that checks if the entry of the grade is permitted and does
     * the calculation of the score and the assignment of grade, score to
     * the according variables of criterion
     */
    function proofAndCalculateScoreCriterion(offer, offerIndex, criterionIndex, criterionGrade) {
      // in case the field has the value (-) or (.) it is saved as undefined,
      // so show error and set score to null
      var offerCriterion = getOfferCriterion(offer, criterionIndex);
      if (typeof criterionGrade === AppConstants.UNDEFINED) {
        offerCriterion.score = null;
      } else {
        if (criterionGrade < vm.data.submission.awardMinGrade ||
          criterionGrade > vm.data.submission.awardMaxGrade) {
          vm.notPermittedGrade = true;
          criterionGrade = null;
        } else {
          vm.notPermittedGrade = false;
        }
        /* grade and score need to be rounded */
        var roundedCriterionGrade = roundGrade(criterionGrade);
        if (criterionGrade !== roundedCriterionGrade) {
          vm.notRoundedGrade = true;
        } else {
          vm.notRoundedGrade = false;
        }
        offerCriterion.grade = roundedCriterionGrade;
        if (criterionGrade !== null) {
          /* inform the form in real time */
          vm.criterionGrade[offerIndex][criterionIndex] = roundedCriterionGrade;
          var score = roundedCriterionGrade * vm.criteria[criterionIndex].weighting / 100;
          offerCriterion.score = roundScore(score);
        } else {
          /* set also score to null */
          offerCriterion.score = null;
        }
      }
      calculateTotalScore(offerIndex);
    }
    /**
     * function that returns the offerSubcriterion grade given an
     * offer, criterionIndex and subcriterionIndex
     */
    function getOfferSubcriterionGrade(offer, criterionIndex,
      subcriterionIndex) {
      var offerSubcriterion = getOfferSubcriterion(offer, criterionIndex, subcriterionIndex);
      return roundGradeToString(offerSubcriterion.grade);
    }
    /**
     * function that returns the offerSubcriterion score given an
     * offer, criterionIndex and subcriterionIndex
     */
    function getOfferSubcriterionScore(offer, criterionIndex, subcriterionIndex) {
      var offerSubcriterion = getOfferSubcriterion(offer,
        criterionIndex, subcriterionIndex);
      return roundScoreToString(offerSubcriterion.score);
    }
    /*
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
    /**
     * function that checks if the entry of the grade is permitted and does
     * the calculation of the score and the assignment of grade, score to
     * the according variables of criterion
     */
    function proofAndCalculateScoreSubcriterion(offer, offerIndex, criterionIndex,
      subcriterionIndex, subcriterionGrade) {
      // in case the field has the value (-) or (.) it is saved as undefined,
      // so show error and set score to null
      var offerSubriterion = getOfferSubcriterion(offer, criterionIndex,
        subcriterionIndex);
      if (typeof subcriterionGrade === AppConstants.UNDEFINED) {
        offerSubriterion.score = null;
      } else {
        if (subcriterionGrade < vm.data.submission.awardMinGrade ||
          subcriterionGrade > vm.data.submission.awardMaxGrade) {
          vm.notPermittedGrade = true;
          subcriterionGrade = null;
        } else {
          vm.notPermittedGrade = false;
        }
        /* grade and score need to be rounded */
        var roundedSubcriterionGrade = roundGrade(subcriterionGrade);
        if (subcriterionGrade !== roundedSubcriterionGrade) {
          vm.notRoundedGrade = true;
        } else {
          vm.notRoundedGrade = false;
        }
        offerSubriterion.grade = roundedSubcriterionGrade;
        if (subcriterionGrade !== null) {
          /* inform the form in real time */
          vm.subcriterionGrade[offerIndex][criterionIndex][subcriterionIndex] = roundedSubcriterionGrade;
          var scoreSubriterion = roundedSubcriterionGrade *
            vm.criteria[criterionIndex].subcriterion[subcriterionIndex].weighting / 100;
          offerSubriterion.score = roundScore(scoreSubriterion);
        } else {
          /* set also score to null */
          offerSubriterion.score = null;
        }
      }
      /**
       * calculate grade and score of criterion the subcriterion belongs
       * to get the criterion
       */
      var offerCriterion = getOfferCriterion(offer, criterionIndex);
      /**
       * get all subcriteria of the given criterion and add their score.
       * The output of this will be the grade of the criterion
       */
      var sumScore = 0;
      /* if all scores are null set also the grade to null */
      var countNulls = 0;
      for (var i = 0; i < offerCriterion.criterion.subcriterion.length; i++) {
        var offerSubcriterion = getOfferSubcriterion(offer, criterionIndex, i);
        if (offerSubcriterion.score === null) {
          countNulls++;
        } else {
          sumScore = sumScore + offerSubcriterion.score;
        }
      }
      /* inform criterion grade and score of the criterion */
      if (countNulls === vm.criteria[criterionIndex].subcriterion.length) {
        /* all null, return null */
        offerCriterion.grade = null;
        offerCriterion.score = null;
      } else {
        offerCriterion.grade = roundGrade(sumScore);
        var criterionScore = offerCriterion.grade *
          vm.criteria[criterionIndex].weighting / 100;
        offerCriterion.score = roundScore(criterionScore);
      }
      calculateTotalScore(offerIndex);
    }

    function calculateTotalScore(offerIndex) {
      var curOffer = vm.offers[offerIndex];
      var totalScore = 0;
      for (var i = 0; i < curOffer.offerCriteria.length; i++) {
        //Add score to total score if the criterion type is one of the three award criteria.
        if (curOffer.offerCriteria[i].criterion.criterionType === AppConstants.CRITERION_TYPE.AWARD_CRITERION_TYPE ||
          curOffer.offerCriteria[i].criterion.criterionType === AppConstants.CRITERION_TYPE.PRICE_AWARD_CRITERION_TYPE ||
          curOffer.offerCriteria[i].criterion.criterionType === AppConstants.CRITERION_TYPE.OPERATING_COST_AWARD_CRITERION_TYPE) {
          totalScore = totalScore + curOffer.offerCriteria[i].score;
        }
      }
      /* round */
      curOffer.awardTotalScore = roundScore(totalScore);
      rankOffers();
    }
    /**
     * function that sorts offers according to their total score and if they
     * have the same total score according to the company name
     */
    function rankOffers() {
      var sortedOffers = vm.offers.slice();
      /**
       * add company name because the sorting function can not see deeper
       * as the first level
       * and remove excluded offers, those do not need to be sorted
       */
      for (var k = 0; k < sortedOffers.length; k++) {
        sortedOffers[k].companyName = sortedOffers[k].submittent.companyId.companyName;
        if (sortedOffers[k].isExcludedFromAwardProcess) {
          delete sortedOffers[k];
        }
      }
      sortedOffers.sort(function (a, b) {
        /* sort with grade */
        var awardTotalScore1 = a['awardTotalScore'];
        var awardTotalScore2 = b['awardTotalScore'];
        if (awardTotalScore1 === null ||
          awardTotalScore1 < awardTotalScore2) {
          return 1;
        } else if (awardTotalScore2 === null ||
          awardTotalScore1 > awardTotalScore2) {
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
          if (sortedOffers[i] && vm.offers[j].id === sortedOffers[i].id) {
            vm.offers[j].awardRank = i + 1;
          }
        }
      }
    }
    /* function that rounds the given numbers according to the requirement for grade */
    function roundGrade(input) {
      return (input == null) ? null : parseFloat(input.toFixed(AppConstants.ROUND_DECIMALS.GRADE));
    }
    /* function that rounds the given numbers according to the requirement for score */
    function roundScore(input) {
      return (input == null) ? null : parseFloat(input.toFixed(AppConstants.ROUND_DECIMALS.SCORE));
    }
    /** function that rounds the given numbers according to the requirement for score
     * and returns a string so that the trailing zeros are displayed
     */
    function roundGradeToString(input) {
      return (input == null) ? null : input.toFixed(AppConstants.ROUND_DECIMALS.GRADE);
    }
    /** function that rounds the given numbers according to the requirement for score
     * and returns a string so that the trailing zeros are displayed
     */
    function roundScoreToString(input) {
      return (input == null) ? null : input.toFixed(AppConstants.ROUND_DECIMALS.SCORE);
    }
    /**
     * function that returns a boolean stating whether to display the given
     * field as input field or read only
     */
    function showInputField(criterion, offer) {
      return criterion.subcriterion.length === 0 &&
        criterion.criterionType !== AppConstants.CRITERION_TYPE.PRICE_AWARD_CRITERION_TYPE &&
        criterion.criterionType !== AppConstants.CRITERION_TYPE.OPERATING_COST_AWARD_CRITERION_TYPE &&
        !offer.isExcludedFromAwardProcess;
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
      } else {
        return null;
      }
    }

    function save(form) {
      var awardAssessList = [];
      for (var i = 0; i < vm.offers.length; i++) {
        var awardAssess = {
          offerId: vm.offers[i].id,
          rank: vm.offers[i].awardRank,
          totalScore: vm.offers[i].awardTotalScore,
          offerCriteria: [],
          offerSubcriteria: []
        };
        var awardCriteriaIds = [];
        for (var j = 0; j < vm.offers[i].offerCriteria.length; j++) {
          var offerCriterion = {
            offerCriterionId: vm.offers[i].offerCriteria[j].id
          };
          // Take into consideration only award-criteria.
          if (vm.offers[i].offerCriteria[j].criterion.criterionType === vm.criterionType.AWARD_CRITERION_TYPE) {
            // Check criterion grades for invalid values only if criterion has no sub-criteria.
            if ((vm.offers[i].offerCriteria[j].grade < vm.data.submission.awardMinGrade ||
                vm.offers[i].offerCriteria[j].grade > vm.data.submission.awardMaxGrade) &&
              !vm.offers[i].offerCriteria[j].criterion.subcriterion.length) {
              offerCriterion.grade = null;
              offerCriterion.score = null;
            } else {
              offerCriterion.grade = vm.offers[i].offerCriteria[j].grade;
              offerCriterion.score = vm.offers[i].offerCriteria[j].score;
            }
            awardAssess.offerCriteria.push(offerCriterion);
            // Keep ids of award-criteria which include sub-criteria.
            if (vm.offers[i].offerCriteria[j].criterion.subcriterion.length) {
              awardCriteriaIds.push(vm.offers[i].offerCriteria[j].criterion.id);
            }
          }
        }
        for (j = 0; j < awardCriteriaIds.length; j++) {
          for (var k = 0; k < vm.offers[i].offerSubcriteria.length; k++) {
            var offerSubcriterion = {
              offerSubcriterionId: vm.offers[i].offerSubcriteria[k].id
            };
            // Choose only sub-criteria of award-criteria.
            if (vm.offers[i].offerSubcriteria[k].subcriterion.criterion === awardCriteriaIds[j]) {
              // Check sub-criterion grades for invalid values.
              if (vm.offers[i].offerSubcriteria[k].grade < vm.data.submission.awardMinGrade ||
                vm.offers[i].offerSubcriteria[k].grade > vm.data.submission.awardMaxGrade) {
                offerSubcriterion.grade = null;
                offerSubcriterion.score = null;
              } else {
                offerSubcriterion.grade = vm.offers[i].offerSubcriteria[k].grade;
                offerSubcriterion.score = vm.offers[i].offerSubcriteria[k].score;
              }
              awardAssess.offerSubcriteria.push(offerSubcriterion);
            }
          }
        }
        awardAssessList.push(awardAssess);
      }
      AwardService.updateOfferCriteriaAward(awardAssessList)
        .success(function (data) {
          $state.reload();
        }).error(function (response, status) {});
    }
    /** Format number according to specifications */
    function formatAmount(num) {
      return AppService.formatAmount(num);
    }
    /** Function to implement the swiss number rounding standard */
    function customRoundNumber(num) {
      return AppService.customRoundNumber(num);
    }
    /** Function to disable the criteria assess form */
    function criteriaAssessFormDisabled(currentStatus) {
      return !(currentStatus >= vm.status.OFFER_OPENING_CLOSED && currentStatus < vm.status.AWARD_EVALUATION_CLOSED && (vm.data.submission && !vm.data.submission.awardIsLocked));
    }
    /** Function to display object information */
    function getObjectInfo(object) {
      return AppService.getObjectInfo(object);
    }
    /** Function to implement the cancellation functionality */
    function cancelButton(dirtyflag, goBack) {
      if (!dirtyflag) {
        if (goBack) {
          $state.go('award.view', {
            id: vm.data.submission.id
          }, {
            reload: true
          });
        } else {
          $state.reload();
        }
      } else {
        var cancelModal = $uibModal.open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title"></h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie den Vorgang wirklich abbrechen? Nicht gespeicherte Informationen gehen dabei verloren. </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="cancelCtrl.closeWindowModal(true)">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'cancelCtrl',
          backdrop: 'static',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeWindowModal = function () {
              cancelModal.dismiss();
            };
          }
        });
        return cancelModal.result.then(function () {}, function () {
          if (goBack) {
            $state.go('award.view', {
              id: vm.data.submission.id
            }, {
              reload: true
            });
          } else {
            $state.reload();
          }
          return null;
        });
      }
      return null;
    }
  }
})();
