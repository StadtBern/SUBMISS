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
 * @name offerDetails.controller.js
 * @description OfferDetailsController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.offer").controller("OfferDetailsController",
      OfferDetailsController);

  /** @ngInject */
  function OfferDetailsController($scope, $locale, $state, $stateParams,
    OfferService, SubmissionService, StammdatenService, QFormJSRValidation, $uibModal,
    $filter, $transitions, AppService, AppConstants) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var grossAmount = null;
    var discount = null;
    var buildingCosts = null;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.openOfferAccordion = false;
    vm.status = AppConstants.STATUS;
    vm.group = AppConstants.GROUP;
    vm.fieldSetTrue = false;
    vm.tempVat = "0.00";
    vm.currentStatus = "";
    vm.hasStatusCheck = false;
    vm.hasStatusChecked = false;
    vm.offer_exists = false;
    vm.noSubmittent = true;
    vm.moreThanOneError = false;
    vm.secTendererAdd = false;
    vm.secTenderAudit = false;
    vm.secTenderChecked = false;
    vm.secOfferOpeningClose = false;
    vm.secOfferOpeningRestart = false;
    vm.secOfferDelete = false;
    vm.secTendererRemove = false;
    vm.secCompanyView = false;
    vm.secAwarded = false;
    vm.secAwardRemoved = false;
    vm.secFormalAuditView = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.save = save;
    vm.openOfferDate = openOfferDate;
    vm.resetPage = resetPage;
    vm.resetOfferValues = resetOfferValues;
    vm.calculateNet = calculateNet;
    vm.calculateCHFDiscount = calculateCHFDiscount;
    vm.calculateCHFBuildingCosts = calculateCHFBuildingCosts;
    vm.calculateCHFDiscount2 = calculateCHFDiscount2;
    vm.calculateCHFMWST = calculateCHFMWST;
    vm.isPartOfferOrIsVariantChecked = isPartOfferOrIsVariantChecked;
    vm.customRoundNumber = customRoundNumber;
    vm.variantNotesValue = variantNotesValue;
    vm.formatAmount = formatAmount;
    vm.calculateCHFMWSTValue = calculateCHFMWSTValue;
    vm.calculateCHFDiscountValue = calculateCHFDiscountValue;
    vm.calculateCHFBuildingCostsValue = calculateCHFBuildingCostsValue;
    vm.calculateCHFDiscount2Value = calculateCHFDiscount2Value;
    vm.calculateCHFMWSTValueCalc = calculateCHFMWSTValueCalc;
    vm.secOfferDetailsEdit = false;
    var offer = {};
    offer.show = false;
    vm.fieldsetTrue = false;
    vm.disabledForm = disabledForm;
    vm.ignoreInvalidNumber = ignoreInvalidNumber;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      AppService.setIsDirty(false);
      readStatusOfSubmission($stateParams.offer.submittent.submissionId.id);
      getOffer($stateParams.offer.id);
      vm.secOfferDetailsEdit = AppService.isOperationPermitted(AppConstants.OPERATION.OFFER_DETAILS_EDIT, null);
      getSettlementTypes();
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    /** Check for unsaved changes when trying to transition to different state */
    $transitions.onBefore({}, function (transition) {
      if (transition.to() !== transition.from() && vm.dirtyFlag === true) {
        var transitionModal = AppService.transitionModal();
        return transitionModal.result
          .then(function (response) {
            if (response) {
              AppService.setIsDirty(false);
              vm.dirtyFlag = false;
              return true;
            }
            AppService.setIsDirty(false);
            return false;
          });
      }
    });

    function openOfferDate() {
      vm.openOfferDate.opened = !vm.openOfferDate.opened;
    }

    // A function that returns true or false if the form on html must be or not disabled
    function disabledForm(group) {
      var submission = $stateParams.offer.submittent.submissionId;
      return (!vm.secOfferDetailsEdit || vm.currentStatus >= vm.status.AWARD_EVALUATION_CLOSED ||
          ((submission.process !== AppConstants.PROCESS.SELECTIVE &&
            submission.process !== null) && (!submission.secondDeadline ||
            !submission.offerOpeningDate || !submission.secondLoggedBy)) ||
          (submission.process === AppConstants.PROCESS.SELECTIVE && ((!submission.firstDeadline ||
              !submission.applicationOpeningDate || !submission.firstLoggedBy) ||
            (vm.currentStatus < vm.status.AWARD_NOTICES_1_LEVEL_CREATED))) ||
          vm.currentStatus === vm.status.SUBMITTENTLIST_CHECK ||
          (vm.currentStatus < vm.status.OFFER_OPENING_CLOSED &&
            group === vm.group.PL &&
            (submission.process === AppConstants.PROCESS.SELECTIVE ||
              submission.process === AppConstants.PROCESS.OPEN ||
              submission.process === AppConstants.PROCESS.INVITATION)) ||
          vm.currentStatus >= vm.status.MANUAL_AWARD_COMPLETED) ||
        AppService.offerEditingDisabled(submission);
    }

    function getOffer(offerId) {
      OfferService.getOfferById(offerId)
        .success(function (data) {
          vm.offer = data;
          vm.fieldSetDisabled = false;

          // call the disabledForm function to define if the form now must be disabled or not.
          vm.fieldSetDisabled = vm.disabledForm();

          if (vm.offer.isCorrected) {
            grossAmount = vm.offer.grossAmountCorrected;
          } else {
            grossAmount = vm.offer.grossAmount;
          }
          vm.CHFDiscount = calculateCHFDiscount(grossAmount, vm.offer.discount);
          if (vm.offer.isDiscountPercentage) {
            discount = vm.CHFDiscount;
          } else {
            discount = vm.offer.discount;
          }
          vm.CHFBuildingCosts = calculateCHFBuildingCosts(grossAmount, discount, vm.offer.buildingCosts);
          if (vm.offer.isBuildingCostsPercentage) {
            buildingCosts = vm.CHFBuildingCosts;
          } else {
            buildingCosts = vm.offer.buildingCosts;
          }
          vm.CHFDiscount2 = calculateCHFDiscount2(grossAmount, discount,
            buildingCosts, vm.offer.discount2);
          vm.net = calculateNet(vm.offer.grossAmount, vm.offer.grossAmountCorrected, vm.offer.isCorrected, vm.offer.discount, vm.offer.isDiscountPercentage,
            vm.offer.buildingCosts, vm.offer.isBuildingCostsPercentage, vm.offer.discount2, vm.offer.isDiscount2Percentage);

          // Set the value of MWST if it is in %.
          if (vm.offer.isVatPercentage) {
            vm.tempVat = 0;
            vm.tempVatDD = String(vm.offer.vat);
            vm.CHFMWST = vm.calculateCHFMWST(vm.net, vm.offer.vat);
            vm.netInkl = vm.net + vm.CHFMWST;
          }
          StammdatenService.getVatsBySubmission($stateParams.offer.submittent.submissionId.id)
            .success(function (data) {
              var vats = data;
              vm.vatValues = [];
              for (var i in vats) {
                if (vats[i].shortCode === "VT1") {
                  vm.mainVat = vats[i];
                }
                vm.vatValues.push(vats[i].value2);
              }
              // Set the value of MWST if it is not in %.
              if (!vm.offer.isVatPercentage) {
                vm.tempVatDD = vm.mainVat.value2;
                vm.tempVat = String(vm.offer.vat);
                vm.netInkl = vm.net + vm.offer.vat;
              }
            });
          $("#offerDate").focus();
        }).error(function (response, status) {

        });
    }

    function calculateCHFBuildingCostsValue(isCorrected, grossAmountCorrected, grossAmount, discount, isDiscountPercentage, buildingCosts) {
      if (isCorrected) {
        grossAmount = grossAmountCorrected;
      }
      if (isDiscountPercentage) {
        discount = calculateCHFDiscount(grossAmount, discount);
      }
      return calculateCHFBuildingCosts(grossAmount, discount, buildingCosts);
    }

    function calculateCHFDiscountValue(isCorrected, grossAmountCorrected, grossAmount, discount) {
      if (isCorrected) {
        grossAmount = grossAmountCorrected;
      }
      return calculateCHFDiscount(grossAmount, discount);
    }

    function calculateCHFDiscount2Value(isCorrected, grossAmountCorrected, grossAmount, discount, isDiscountPercentage, buildingCosts, isBuildingCostsPercentage, discount2) {
      if (isCorrected) {
        grossAmount = grossAmountCorrected;
      }
      if (isDiscountPercentage) {
        discount = calculateCHFDiscount(grossAmount, discount);
      }
      if (isBuildingCostsPercentage) {
        buildingCosts = calculateCHFBuildingCosts(grossAmount, discount, buildingCosts);
      }
      return calculateCHFDiscount2(grossAmount, discount,
        buildingCosts, discount2);
    }

    function calculateCHFMWSTValue(net, vat, isVatPercentage) {
      var returnValue;
      if (isVatPercentage) {
        returnValue = net * vat / 100;
      } else {
        returnValue = vat;
      }
      return returnValue;
    }

    function calculateCHFMWSTValueCalc(net, vat, isVatPercentage, tempVat, tempVatDD) {
      tempVat = parseFloat(tempVat)
      if (tempVat === "0.00" || tempVat === "0.0" || tempVat === "0" || !tempVat) {
        if (tempVat == null || tempVat === "" || angular.isUndefined(tempVat)) {
          tempVat = 0;
        }
        if (!tempVatDD && vm.mainVat) {
          tempVatDD = Number(vm.mainVat.value2);
        }
        isVatPercentage = true;
        vat = tempVatDD;
      } else {
        isVatPercentage = false;
        vat = tempVat;
      }
      var returnValue;
      if (isVatPercentage) {
        returnValue = net * vat / 100;
      } else {
        returnValue = vat;
      }
      return returnValue;
    }

    function save() {
      vm.tempVat = String(vm.tempVat);
      vm.dirtyFlag = false;
      /**All default values must be set to 0 */
      if (vm.offer.grossAmount === null || angular.isUndefined(vm.offer.grossAmount) || vm.offer.grossAmount === "") {
        vm.offer.grossAmount = 0;
      }
      if (vm.offer.grossAmountCorrected === null || angular.isUndefined(vm.offer.grossAmountCorrected) || vm.offer.grossAmountCorrected === "") {
        vm.offer.grossAmountCorrected = 0;
      }
      if (vm.offer.discount === null || angular.isUndefined(vm.offer.discount) || vm.offer.discount === "") {
        vm.offer.discount = 0;
      }
      if (vm.offer.buildingCosts === null || angular.isUndefined(vm.offer.buildingCosts) || vm.offer.buildingCosts === "") {
        vm.offer.buildingCosts = 0;
      }
      if (vm.offer.discount2 === null || angular.isUndefined(vm.offer.discount2) || vm.offer.discount2 === "") {
        vm.offer.discount2 = 0;
      }
      if (vm.tempVat === "0.00" || vm.tempVat === "0.0" || vm.tempVat === "0" || vm.tempVat === null || vm.tempVat === "" || angular.isUndefined(vm.tempVat)) {
        if (vm.tempVat === null || vm.tempVat === "" || angular.isUndefined(vm.tempVat)) {
          vm.tempVat = 0;
        }
        if (vm.tempVatDD === null || angular.isUndefined(vm.tempVatDD)) {
          vm.tempVatDD = Number(vm.mainVat.value2);
        }
        vm.offer.isVatPercentage = true;
        vm.offer.vat = vm.tempVatDD;
      } else {
        vm.offer.isVatPercentage = false;
        vm.offer.vat = vm.tempVat;
      }
      vm.offer.amount = AppService.customRoundNumberWithoutSeparator(calculateNet(vm.offer.grossAmount, vm.offer.grossAmountCorrected, vm.offer.isCorrected,
        vm.offer.discount, vm.offer.isDiscountPercentage, vm.offer.buildingCosts, vm.offer.isBuildingCostsPercentage, vm.offer.discount2, vm.offer.isDiscount2Percentage));
      if (vm.offer.isEmptyOffer) {
        resetOfferValues();
      } else {
        OfferService.updateOffer(vm.offer)
          .success(function (data) {
            customReload($stateParams.offer.id);
          })
          .error(function (response, status) {
            if (status === 400) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.offerDetailsCtrl.offerDetailsForm, response);
              //When status 400 dirty flag value must remain as it was before.
              //That's the case to don't cancel the editing before confirmation modal.
              vm.dirtyFlag = true;
              AppService.setIsDirty(true);
            }
          });
      }
      return true;
    }

    function resetPage(dirtyflag) {
      if (!dirtyflag) {
        customReload($stateParams.offer.id);
      } else {
        var closeProjectEditModal = $uibModal
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
              '<button type="button" class="btn btn-primary" ng-click="offerListViewCtr.closeWindowModal(\'ja\')">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'offerListViewCtr',
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
            vm.dirtyFlag = false;
            customReload($stateParams.offer.id);
          } else {
            // Do something else here
            return false;
          }
          return null;
        });
      }
      return null;
    }

    function calculateNet(grossAmount, grossAmountCorrected, isCorrected, discount, isDiscountPercentage,
      buildingCosts, isBuildingCostsPercentage, discount2, isDiscount2Percentage) {
      var returnValue;
      if (grossAmount === null && grossAmountCorrected === null) {
        return null;
      }
      if (isCorrected) {
        grossAmount = grossAmountCorrected;
      }
      if (isDiscountPercentage) {
        discount = vm.calculateCHFDiscount(grossAmount, discount);
      }
      if (isBuildingCostsPercentage) {
        buildingCosts = vm.calculateCHFBuildingCosts(grossAmount, discount, buildingCosts);
      }
      if (isDiscount2Percentage) {
        discount2 = vm.calculateCHFDiscount2(grossAmount, discount, buildingCosts, discount2);
      }
      returnValue = grossAmount - discount - buildingCosts - discount2;
      //	vm.net=returnValue; ?? Why this?
      return returnValue;
    }

    // Berechnung: (Wert des "Bruttobetrag" * Wert "Rabatt")/100.
    function calculateCHFDiscount(grossAmount, discount) {
      var returnValue;
      returnValue = (grossAmount * discount) / 100;
      return returnValue;
    }

    // Berechnung: (Wert des "Bruttobetrag" - Wert des "Rabatt" (in CHF))* Wert "Baunebenkosten"/100
    function calculateCHFBuildingCosts(grossAmount, discount, buildingCosts) {
      var returnValue;
      returnValue = ((grossAmount - discount) * buildingCosts) / 100;
      return returnValue;
    }

    // Berechnung: [(Wert des "Bruttobetrag" - (Wert "Rabatt" + Wert "Baunebenkosten" (in CHF))) * Wert "Skonto"]/100
    function calculateCHFDiscount2(grossAmount, discount, CHFBuildingCosts, discount2) {
      discount = Number(discount);
      CHFBuildingCosts = Number(CHFBuildingCosts);
      var returnValue;
      returnValue = ((grossAmount - (discount + CHFBuildingCosts)) * discount2) / 100;
      return returnValue;
    }

    function calculateCHFMWST(net, vat) {
      return net * vat / 100;
    }

    /**Function to determine if isPartOffer or isVariant is checked */
    function isPartOfferOrIsVariantChecked(isEmptyOffer) {
      if (!isEmptyOffer) {
        return false;
      } else {
        vm.offer.isPartOffer = false;
        vm.offer.isVariant = false;
        return true;
      }
    }

    function customRoundNumber(num) {
      return AppService.customRoundNumber(num);
    }

    /**If the "Variante" checkbox is not checked, leave the "Bemerkung Variante" field empty */
    function variantNotesValue(isVariant) {
      if (isVariant) {
        return vm.offer.variantNotes;
      } else {
        return null;
      }
    }

    /**Format number according to specifications */
    function formatAmount(num) {
      if (Math.abs(num) >= 1000) {
        num = $filter('number')(num, 2).replace(/,/g, "'");
      } else {
        num = $filter('number')(num, 2);
      }
      return num;
    }

    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id)
        .success(function (data) {
          vm.currentStatus = data;
        }).error(function (response, status) {

        });
    }

    /**Reseting the form values to default keeping only the Offer Date */
    function resetOfferValues() {
      OfferService.resetOfferValues(vm.offer)
        .success(function (data) {
          customReload(data.id)
        })
        .error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.offerDetailsCtrl.offerDetailsForm, response);
            //When status 400 dirty flag value must remain as it was before.
            //That's the case to don't cancel the editing before confirmation modal.
            vm.dirtyFlag = true;
            AppService.setIsDirty(true);
          }
        });
    }

    /** Function to ignore invalid number */
    function ignoreInvalidNumber(element) {
      return AppService.ignoreInvalidNumber(element);
    }

    /** Function to get all settlement types */
    function getSettlementTypes() {

      StammdatenService.getMasterListValueHistoryDataBySubmission($stateParams.offer.submittent.submissionId.id, "SettlementType")
        .success(function (data) {
          vm.settlementOptions = $filter('filter')(data, {
            active: true
          });
        });
    }

    /** Function to implement custom reload */
    function customReload(id) {
      if (AppService.getIsDirty()) {
        AppService.setIsDirty(false);
      }
      $state.go('offerListView', {
        displayedOfferId: id,
        offerDetails: true,
        subcontractor: false,
        jointVenture: false,
        operatingCost: false,
        ancilliaryCost: false
      }, {
        reload: true
      });
    }
  }
})();
