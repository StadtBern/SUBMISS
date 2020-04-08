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
 * @description AncilliaryCostDetailsController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.offer").controller("AncilliaryCostDetailsController",
      AncilliaryCostDetailsController);

  /** @ngInject */
  function AncilliaryCostDetailsController($rootScope, $scope, $location, $state, $stateParams,
    OfferService, SubmissionService, StammdatenService, QFormJSRValidation, $uibModal, NgTableParams,
    $filter, $transitions, AppService, AppConstants) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var grossAmount = null;
    var ancilliaryAmountGross = null;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.data = {};
    vm.offerId = $stateParams.offer.id;
    vm.group = AppConstants.GROUP;
    vm.save = save;
    vm.status = AppConstants.STATUS;
    vm.secAncilliaryCostDetailsEdit = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.resetPage = resetPage;
    vm.calculateNetInkl = calculateNetInkl;
    vm.calculateCHFMWSTValueCalc = calculateCHFMWSTValueCalc;
    vm.disabledForm = disabledForm;
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
      loadVats();
      vm.secAncilliaryCostDetailsEdit = AppService.isOperationPermitted(AppConstants.OPERATION.ANCILLIARY_COST_DETAILS_EDIT, null);
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
              '<button type="button" class="btn btn-primary" ng-click="ancilliaryCostDetailsCtrl.closeWindowModal(\'ja\')">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'ancilliaryCostDetailsCtrl',
            backdrop: 'static',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.closeWindowModal = function (reason) {
                closeProjectEditModal.dismiss(reason);
              };
            }

          });

        return closeProjectEditModal.result
          .then(function () {
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
      return (!vm.secAncilliaryCostDetailsEdit || vm.currentStatus >= vm.status.AWARD_EVALUATION_CLOSED ||
          ((submission.process !== AppConstants.PROCESS.SELECTIVE &&
            submission.process !== null) && (!submission.secondDeadline ||
            !submission.offerOpeningDate || !submission.secondLoggedBy)) ||
          (submission.process === AppConstants.PROCESS.SELECTIVE &&
            ((!submission.firstDeadline ||
              !submission.applicationOpeningDate || !submission.firstLoggedBy) || (vm.currentStatus < vm.status.AWARD_NOTICES_1_LEVEL_CREATED))) ||
          vm.currentStatus === vm.status.SUBMITTENTLIST_CHECK ||
          (vm.currentStatus < vm.status.OFFER_OPENING_CLOSED &&
            group === vm.group.PL &&
            (submission.process === AppConstants.PROCESS.SELECTIVE ||
              submission.process === AppConstants.PROCESS.OPEN || submission.process === AppConstants.PROCESS.INVITATION)) ||
          vm.currentStatus >= vm.status.MANUAL_AWARD_COMPLETED) ||
        AppService.offerEditingDisabled(submission);
    }

    function getOffer(offerId) {
      OfferService.getOfferById(offerId)
        .success(function (data) {
          vm.offer = data;
          vm.fieldSetDisabled = false;
          vm.fieldSetDisabled = vm.disabledForm(); // call the disabledForm function to define if the form now must be disabled or not.

          if (vm.offer.isCorrected) {
            grossAmount = vm.offer.grossAmountCorrected;
          } else {
            grossAmount = vm.offer.grossAmount;
          }

          vm.amountCHF = calculateCHFAncilliaryAmount(grossAmount, vm.offer.ancilliaryAmountGross);
          if (vm.offer.isAncilliaryAmountPercentage) {
            ancilliaryAmountGross = vm.amountCHF;
          } else {
            ancilliaryAmountGross = vm.offer.ancilliaryAmountGross;
          }
          vm.mwstCHF = calculateMWST(ancilliaryAmountGross, vm.offer.ancilliaryAmountVat);

          vm.netto = ancilliaryAmountGross + vm.mwstCHF;

        }).error(function (response, status) {

        });
    }

    function calculateNetInkl(isCorrected, grossAmountCorrected, grossAmount, ancilliaryAmountGross, isAncilliaryAmountPercentage, ancilliaryAmountVat) {
      var returnValue;
      if (isCorrected) {
        grossAmount = grossAmountCorrected;
      }
      if (ancilliaryAmountGross === null || angular.isUndefined(ancilliaryAmountGross) || ancilliaryAmountGross === "") {
        ancilliaryAmountGross = 0;
      }
      if (!ancilliaryAmountVat && vm.mainVat) {
        ancilliaryAmountVat = Number(vm.mainVat.value2);
      }
      vm.amountCHF = calculateCHFAncilliaryAmount(grossAmount, ancilliaryAmountGross);
      if (isAncilliaryAmountPercentage) {
        ancilliaryAmountGross = vm.amountCHF;
        returnValue = ancilliaryAmountGross + calculateMWST(ancilliaryAmountGross, ancilliaryAmountVat);
      } else {
        ancilliaryAmountGross = parseFloat(ancilliaryAmountGross);
        returnValue = ancilliaryAmountGross + calculateMWST(ancilliaryAmountGross, ancilliaryAmountVat);
      }
      vm.mwstCHF = calculateMWST(ancilliaryAmountGross, ancilliaryAmountVat);

      return returnValue;
    }

    function calculateCHFMWSTValueCalc(isCorrected, grossAmountCorrected, grossAmount, ancilliaryAmountGross, isAncilliaryAmountPercentage, ancilliaryAmountVat) {
      if (isCorrected) {
        grossAmount = grossAmountCorrected;
      }
      if (isAncilliaryAmountPercentage) {
        ancilliaryAmountGross = calculateCHFAncilliaryAmount(grossAmount, ancilliaryAmountGross);
      }
      return calculateMWST(ancilliaryAmountGross, ancilliaryAmountVat);
    }

    function save() {
      SubmissionService.isStatusChanged($stateParams.offer.submittent.submissionId.id, vm.currentStatus)
        .success(function (data) {
          proceedWithSave();
        }).error(function (response, status) {
          if (status === 400) { // Validation errors.
            QFormJSRValidation.markErrors($scope,
              $scope.ancilliaryCostDetailsCtrl.offerAncilliaryCostDetailsForm, response);
          }
        });
    }

    function proceedWithSave() {
      // If the ancilliary amount gross contains an invalid numerical value, prevent the form from being saved and updated.
      if ($scope.ancilliaryCostDetailsCtrl.offerAncilliaryCostDetailsForm.ancilliaryAmountGross.$invalid) {
        return null;
      }
      vm.dirtyFlag = false;
      if (vm.offer.ancilliaryAmountGross === null || angular.isUndefined(vm.offer.ancilliaryAmountGross) || vm.offer.ancilliaryAmountGross === "") {
        vm.offer.ancilliaryAmountGross = 0;
      }
      if (!vm.offer.ancilliaryAmountVat) {
        vm.offer.ancilliaryAmountVat = Number(vm.mainVat.value2);
      }
      if (vm.offer.isEmptyOffer) {
        resetOfferValues();
      } else {
        OfferService.updateOffer(vm.offer)
          .success(function (data) {
            customReload();
          })
          .error(function (response, status) {
            if (status === 400) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.ancilliaryCostDetailsCtrl.offerAncilliaryCostDetailsForm, response);
            }
          });
      }
      return true;
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

    function loadVats() {
      StammdatenService.getVatsBySubmission($stateParams.offer.submittent.submissionId.id).success(function (data) {
        var vats = data;
        vm.vatValues = [];
        for (var i in vats) {
          if (vats[i].shortCode === "VT1") {
            vm.mainVat = vats[i];
          }
          vm.vatValues.push(vats[i].value2);
        }
      }).error(function (response, status) {

      });
    }

    // Berechnung: (Wert des "Bruttobetrag" * Wert "NK brutto")/100.
    function calculateCHFAncilliaryAmount(grossAmount, ancilliaryAmountGross) {
      var returnValue;
      returnValue = (grossAmount * ancilliaryAmountGross) / 100;
      return returnValue;
    }

    function calculateMWST(ancilliaryAmountGross, ancilliaryAmountVat) {
      var returnValue;
      returnValue = (ancilliaryAmountGross * ancilliaryAmountVat) / 100;
      return returnValue;
    }

    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id).success(function (data) {
        vm.currentStatus = data;
      }).error(function (response, status) {

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
        operatingCost: false,
        ancilliaryCost: true
      }, {
        reload: true
      });
    }
  }
})();
