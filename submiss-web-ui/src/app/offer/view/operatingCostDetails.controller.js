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
 * @name subcontractorDetails.controller.js
 * @description OperatingCostDetailsController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.offer")
    .controller("OperatingCostDetailsController", OperatingCostDetailsController);

  /** @ngInject */
  function OperatingCostDetailsController($rootScope, $scope, $location, $state, $stateParams,
    OfferService, SubmissionService, StammdatenService, QFormJSRValidation, $uibModal, NgTableParams,
    $filter, $transitions, AppConstants, AppService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var operatingCostGross = null;
    var operatingCostDiscount = null;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.offerId = $stateParams.offer.id;
    vm.save = save;
    vm.status = AppConstants.STATUS;
    vm.group = AppConstants.GROUP;
    vm.tempOperVat = "0.00";
    vm.secOperationCostDetailsEdit = false;
    vm.offerCopy = {};
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.resetPage = resetPage;
    vm.calculateOperatingNet = calculateOperatingNet;
    vm.calculateOperatingMWSTValue = calculateOperatingMWSTValue;
    vm.calculateCHFDiscountValue = calculateCHFDiscountValue;
    vm.calculateCHFDiscount2Value = calculateCHFDiscount2Value;
    vm.calculateOperatingMWSTValueCalc = calculateOperatingMWSTValueCalc;
    vm.customRoundNumber = customRoundNumber;
    vm.disabledForm = disabledForm;
    vm.resetFormValues = resetFormValues;
    vm.ignoreInvalidNumber = ignoreInvalidNumber;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      AppService.setIsDirty(false);
      getOffer(vm.offerId);
      readStatusOfSubmission($stateParams.offer.submittent.submissionId.id);
      vm.secOperationCostDetailsEdit = AppService.isOperationPermitted(AppConstants.OPERATION.OPERATING_COST_DETAILS_EDIT, null);
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    $rootScope.$on('$stateChangeSuccess', function (event, to, toParams,
      from, fromParams) {
      // save the previous state in a rootScope variable so that it's
      // accessible from everywhere
      $rootScope.previousState = from;
    });
    /** Check for unsaved changes when trying to transition to different state */
    $transitions.onBefore({}, function (transition) {
      if (transition.to() !== transition.from() && vm.dirtyFlag === true) {
        var transitionModal = AppService.transitionModal();
        return transitionModal.result.then(function (response) {
          if (response) {
            vm.dirtyFlag = false;
            AppService.setIsDirty(false);
            return true;
          }
          return false;
        });
      }
      return null;
    });

    function resetPage(dirtyflag) {
      if (!dirtyflag) {
        customReload();
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
            '<button type="button" class="btn btn-primary" ng-click="operatingCostDetailsCtrl.closeWindowModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'operatingCostDetailsCtrl',
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
            customReload();
          } else {
            // Do something else here
            return false;
          }
          return null;
        });
      }
      return null;
    }

    // A function that returns true or false if the form on html must be or not disabled
    function disabledForm(group) {
      var submission = $stateParams.offer.submittent.submissionId;
      return (!vm.secOperationCostDetailsEdit || vm.currentStatus >= vm.status.AWARD_EVALUATION_CLOSED ||
          ((submission.process !== AppConstants.PROCESS.SELECTIVE &&
            submission.process !== null) && (!submission.secondDeadline ||
            !submission.offerOpeningDate || !submission.secondLoggedBy)) ||
          (submission.process === AppConstants.PROCESS.SELECTIVE &&
            ((!submission.firstDeadline ||
              !submission.applicationOpeningDate || !submission.firstLoggedBy) || (vm.currentStatus < vm.status.AWARD_NOTICES_1_LEVEL_CREATED))) ||
          vm.currentStatus === vm.status.SUBMITTENTLIST_CHECK ||
          (vm.currentStatus < vm.status.OFFER_OPENING_CLOSED && group === vm.group.PL &&
            (submission.process === AppConstants.PROCESS.SELECTIVE || submission.process === AppConstants.PROCESS.OPEN ||
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
          if (vm.offer.isOperatingCostCorrected) {
            operatingCostGross = vm.offer.operatingCostGrossCorrected;
          } else {
            operatingCostGross = vm.offer.operatingCostGross;
          }
          // calculate CHF values
          vm.CHFRabatt = calculateCHFRabattAmount(operatingCostGross, vm.offer.operatingCostDiscount);
          if (vm.offer.isOperatingCostDiscountPercentage) {
            operatingCostDiscount = vm.CHFRabatt;
          } else {
            operatingCostDiscount = vm.offer.operatingCostDiscount;
          }
          vm.CHFSkonto = calculateCHFSkonto(operatingCostGross, operatingCostDiscount, vm.offer.operatingCostDiscount2);
          // calcumate net exkl value
          vm.operNetExkl = vm.calculateOperatingNet(vm.offer.operatingCostGross, vm.offer.operatingCostGrossCorrected, vm.offer.isOperatingCostCorrected,
            vm.offer.operatingCostDiscount, vm.offer.isOperatingCostDiscountPercentage, vm.offer.operatingCostDiscount2, vm.offer.isOperatingCostDiscount2Percentage);

          // Set the value of MWST if it is in %.
          if (vm.offer.operatingCostIsVatPercentage) {
            vm.tempOperVat = 0;
            vm.tempOperVatDD = String(vm.offer.operatingCostVat);
            vm.CHFMWST = calculateOperatingMWST(vm.operNetExkl, vm.offer.operatingCostVat);
          }
          StammdatenService.getVatsBySubmission($stateParams.offer.submittent.submissionId.id).success(function (data) {
            var vats = data;
            vm.vatValues = [];
            for (var i in vats) {
              if (vats[i].shortCode === "VT1") {
                vm.mainVat = vats[i];
              }
              vm.vatValues.push(vats[i].value2);
            }
            // Set the value of MWST if it is not in %.
            if (!vm.offer.operatingCostIsVatPercentage) {
              vm.tempOperVatDD = vm.mainVat.value2;
              vm.tempOperVat = String(vm.offer.operatingCostVat);
            }
          });
          // calculate the net inkl value
          vm.operNetInkl = calculateOperatingNetInkl(vm.operNetExkl, vm.CHFMWST, vm.offer.operatingCostVat);
        }).error(function (response, status) {});
    }

    function calculateCHFDiscountValue(isOperatingCostCorrected, operatingCostGrossCorrected, operatingCostGross, operatingCostDiscount) {
      if (isOperatingCostCorrected) {
        operatingCostGross = operatingCostGrossCorrected;
      }
      return calculateCHFRabattAmount(operatingCostGross, operatingCostDiscount);

    }

    function calculateCHFDiscount2Value(isOperatingCostCorrected,
      operatingCostGrossCorrected, operatingCostGross,
      operatingCostDiscount, isOperatingCostDiscountPercentage,
      operatingCostDiscount2) {

      if (isOperatingCostCorrected) {
        operatingCostGross = parseFloat(operatingCostGrossCorrected);
      } else {
        operatingCostGross = parseFloat(operatingCostGross);
      }
      if (isOperatingCostDiscountPercentage) {
        operatingCostDiscount = calculateCHFRabattAmount(operatingCostGross, operatingCostDiscount);
      }
      return calculateCHFSkonto(operatingCostGross, operatingCostDiscount, operatingCostDiscount2);
    }

    function calculateOperatingMWSTValueCalc(operNetExkl, operatingCostVat, operatingCostIsVatPercentage, tempOperVat, tempOperVatDD) {
      if (tempOperVat !== "0.00" && tempOperVat !== "0.0" && tempOperVat !== "0" && tempOperVat !== null && tempOperVat !== "" && !angular.isUndefined(tempOperVat)) {
        tempOperVat = parseFloat(tempOperVat);
      }
      if (tempOperVat === "0.00" || tempOperVat === "0.0" || tempOperVat === "0" || tempOperVat === null ||
        tempOperVat === "" || tempOperVat === 0 || angular.isUndefined(tempOperVat)) {
        if (tempOperVat === null || tempOperVat === "" || angular.isUndefined(tempOperVat)) {
          tempOperVat = 0;
        }
        if (!tempOperVatDD && vm.mainVat) {
          tempOperVatDD = Number(vm.mainVat.value2);
        }
        operatingCostIsVatPercentage = true;
        operatingCostVat = tempOperVatDD;
      } else {
        operatingCostIsVatPercentage = false;
        operatingCostVat = tempOperVat;
      }
      var returnNet;
      if (operatingCostIsVatPercentage) {
        returnNet = operNetExkl * operatingCostVat / 100;
      } else {
        returnNet = operatingCostVat;
      }
      return returnNet;
    }

    function calculateOperatingMWSTValue(operNetExkl, operatingCostVat, operatingCostIsVatPercentage) {
      var returnNet;
      if (operatingCostIsVatPercentage) {
        returnNet = operNetExkl * operatingCostVat / 100;
      } else {
        returnNet = operatingCostVat;
      }
      return returnNet;
    }

    function save() {
      SubmissionService.isStatusChanged($stateParams.offer.submittent.submissionId.id, vm.currentStatus)
        .success(function (data) {
          proceedWithSave();
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.operatingCostDetailsCtrl.offerOperatingCostDetailsCtrlForm, response);
          }
        });
    }

    function proceedWithSave() {
      // If the form contains invalid numerical values, prevent it from being saved and updated.
      if ($scope.operatingCostDetailsCtrl.offerOperatingCostDetailsCtrlForm.operatingCostGross.$invalid ||
        $scope.operatingCostDetailsCtrl.offerOperatingCostDetailsCtrlForm.operatingCostGrossCorrected.$invalid ||
        $scope.operatingCostDetailsCtrl.offerOperatingCostDetailsCtrlForm.operatingCostDiscount.$invalid ||
        $scope.operatingCostDetailsCtrl.offerOperatingCostDetailsCtrlForm.operatingCostDiscount2.$invalid ||
        $scope.operatingCostDetailsCtrl.offerOperatingCostDetailsCtrlForm.tempOperVat.$invalid) {
        return null;
      }
      vm.dirtyFlag = false;
      /**All default values must be set to 0 */
      if (vm.offer.operatingCostGross === null || angular.isUndefined(vm.offer.operatingCostGross) || vm.offer.operatingCostGross === "") {
        vm.offer.operatingCostGross = 0;
      }
      if (vm.offer.operatingCostGrossCorrected === null || angular.isUndefined(vm.offer.operatingCostGrossCorrected) || vm.offer.operatingCostGrossCorrected === "") {
        vm.offer.operatingCostGrossCorrected = 0;
      }
      if (vm.offer.operatingCostDiscount === null || angular.isUndefined(vm.offer.operatingCostDiscount) || vm.offer.operatingCostDiscount === "") {
        vm.offer.operatingCostDiscount = 0;
      }
      if (vm.offer.operatingCostDiscount2 === null || angular.isUndefined(vm.offer.operatingCostDiscount2) || vm.offer.operatingCostDiscount2 === "") {
        vm.offer.operatingCostDiscount2 = 0;
      }
      if (vm.tempOperVat === "0.00" || vm.tempOperVat === "0.0" || vm.tempOperVat === "0" || !vm.tempOperVat || vm.tempOperVat === "" || vm.tempOperVat === 0) {
        if (vm.tempOperVat === null || vm.tempOperVat === "" || angular.isUndefined(vm.tempOperVat)) {
          vm.tempOperVat = 0;
        }
        if (!vm.tempOperVatDD) {
          vm.tempOperVatDD = Number(vm.mainVat.value2);
        }
        vm.offer.operatingCostIsVatPercentage = true;
        vm.offer.operatingCostVat = vm.tempOperVatDD;
      } else {
        vm.offer.operatingCostIsVatPercentage = false;
        vm.offer.operatingCostVat = vm.tempOperVat;
      }
      vm.offer.operatingCostsAmount = AppService.customRoundNumberWithoutSeparator(vm.calculateOperatingNet(vm.offer.operatingCostGross, vm.offer.operatingCostGrossCorrected,
        vm.offer.isOperatingCostCorrected, vm.offer.operatingCostDiscount, vm.offer.isOperatingCostDiscountPercentage,
        vm.offer.operatingCostDiscount2, vm.offer.isOperatingCostDiscount2Percentage));
      if (vm.offer.isEmptyOffer) {
        resetOfferValues();
      } else {
        OfferService.updateOperatingCostOffer(vm.offer)
          .success(function (data) {
            vm.dirtyFlag = false;
            customReload();
          }).error(function (response, status) {
            if (status === 400) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.operatingCostDetailsCtrl.offerOperatingCostDetailsCtrlForm, response);
            }
          });
      }
    }

    /** Function to reset the offer values */
    function resetOfferValues() {
      OfferService.resetOfferValues(vm.offer)
        .success(function (data) {
          customReload(data.id);
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
    // Calculate CHF Rabatt Label
    function calculateCHFRabattAmount(operatingCostGross, operatingCostDiscount) {
      var returnValue;
      returnValue = (operatingCostGross * operatingCostDiscount) / 100;
      return returnValue;
    }

    function calculateCHFSkonto(operatingCostGross, operatingCostDiscount, operatingCostDiscount2) {
      var returnValue;
      returnValue = ((operatingCostGross - operatingCostDiscount) * operatingCostDiscount2) / 100;
      return returnValue;
    }

    function calculateOperatingNet(operatingCostGross, operatingCostGrossCorrected, isOperatingCostCorrected,
      operatingCostDiscount, isOperatingCostDiscountPercentage, operatingCostDiscount2, isOperatingCostDiscount2Percentage) {
      var returnValue;
      if (operatingCostGross === null && operatingCostGrossCorrected === null) {
        return null;
      }
      if (isOperatingCostCorrected) {
        operatingCostGross = operatingCostGrossCorrected;
      }
      if (isOperatingCostDiscountPercentage) {
        operatingCostDiscount = calculateCHFRabattAmount(operatingCostGross, operatingCostDiscount);
      }
      if (isOperatingCostDiscount2Percentage) {
        operatingCostDiscount2 = calculateCHFSkonto(operatingCostGross, operatingCostDiscount, operatingCostDiscount2);
      }
      returnValue = operatingCostGross - operatingCostDiscount - operatingCostDiscount2;
      return returnValue;
    }

    function calculateOperatingMWST(operNetExkl, operatingCostVat) {
      return operNetExkl * operatingCostVat / 100;
    }

    function calculateOperatingNetInkl(operNetExkl, CHFMWST, operatingCostVat) {
      operatingCostVat = String(operatingCostVat);
      var returnValue;
      if (operNetExkl !== null) {
        if (CHFMWST) {
          returnValue = operNetExkl + CHFMWST;
        } else {
          returnValue = operNetExkl + operatingCostVat;
        }
      }
      return returnValue;
    }

    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id)
        .success(function (data) {
          vm.currentStatus = data;
        }).error(function (response, status) {});
    }

    function customRoundNumber(num) {
      return AppService.customRoundNumber(num);
    }
    /** Function to reset the operating cost values */
    function resetFormValues() {
      OfferService.resetOperatingCostValues(vm.offer)
        .success(function () {
          customReload();
        });
    }
    /** Function to ignore invalid number */
    function ignoreInvalidNumber(element) {
      return AppService.ignoreInvalidNumber(element);
    }
    /** Function to implement custom reload */
    function customReload(newOfferId) {
      if (AppService.getIsDirty()) {
        AppService.setIsDirty(false);
      }
      var displayedOfferId;
      // Check if a new offer id has been given (in case of empty offer).
      if (newOfferId) {
        displayedOfferId = newOfferId;
      } else {
        displayedOfferId = $stateParams.offer.id;
      }
      $state.go('offerListView', {
        displayedOfferId: displayedOfferId,
        offerDetails: false,
        subcontractor: false,
        jointVenture: false,
        operatingCost: true,
        ancilliaryCost: false
      }, {
        reload: true
      });
    }
  }
})();
