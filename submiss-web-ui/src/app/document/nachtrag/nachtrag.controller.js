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
 * @name nachtrag.controller.js
 * @description NachtragController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.document")
    .controller("NachtragController", NachtragController);

  /** @ngInject */
  function NachtragController($scope, $rootScope, DocumentService,
    AppService, $transitions,
    $state, $stateParams, SubmissionService, AppConstants, $uibModal,
    QFormJSRValidation, UsersService, NachtragService, StammdatenService) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.nachtragSubmittents;
    vm.openSubmittentAccordion = false;
    vm.displayedSubmittent = {};
    vm.nachtragList = {};
    vm.displayedNachtrag = {};
    vm.nachtrag = {};
    vm.tempVat = "0.00";
    vm.gesamtsumme;
    vm.errorFieldsVisible = false;
    vm.currentStatusOfSubmission;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.addNachtragSubmittent = addNachtragSubmittent;
    vm.getNachtragSubmittents = getNachtragSubmittents;
    vm.formatAmount = formatAmount;
    vm.customRoundNumber = customRoundNumber;
    vm.openSubmittentDetails = openSubmittentDetails;
    vm.createNachtrag = createNachtrag;
    vm.openNachtragForm = openNachtragForm;
    vm.disableDeleteButton = disableDeleteButton;
    vm.deleteNachtrag = deleteNachtrag;
    vm.disableCloseButton = disableCloseButton;
    vm.closeNachtrag = closeNachtrag;
    vm.disableReopenButton = disableReopenButton;
    vm.reopenNachtrag = reopenNachtrag;
    vm.disableCreateButton = disableCreateButton;
    vm.setCheckedNachtragForDocCreation = setCheckedNachtragForDocCreation;
    vm.openNachtragDate = openNachtragDate;
    vm.getNachtrag = getNachtrag;
    vm.readStatusOfSubmission = readStatusOfSubmission;
    vm.ignoreInvalidNumber = ignoreInvalidNumber;
    vm.calculateCHFDiscountValue = calculateCHFDiscountValue;
    vm.calculateCHFDiscount = calculateCHFDiscount;
    vm.calculateCHFBuildingCosts = calculateCHFBuildingCosts;
    vm.calculateCHFBuildingCostsValue = calculateCHFBuildingCostsValue;
    vm.calculateGesamtsumme = calculateGesamtsumme;
    vm.save = save;
    vm.calculateCHFMWST = calculateCHFMWST;
    vm.calculateNet = calculateNet;
    vm.calculateCHFMWSTValueCalc = calculateCHFMWSTValueCalc;
    vm.resetPage = resetPage;
    vm.disableForm = disableForm;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      getNachtragSubmittents($stateParams.submissionId);
      readStatusOfSubmission($stateParams.submissionId);
    }

    function readStatusOfSubmission(id) {
      SubmissionService.getCurrentStatusOfSubmission(id)
      .success(function (data) {
        vm.currentStatusOfSubmission = data;
        // Check if AWARD_NOTICES_CREATED >= current status <= CONTRACT_CREATED
        // to enable the Nachtrag fields and functions
        if(vm.currentStatusOfSubmission >= '260'
          && vm.currentStatusOfSubmission <= '280'){
          vm.enableFields = true;
        }
      }).error(function (response, status) {});
    }

    function addNachtragSubmittent() {
      $uibModal.open({
        templateUrl: 'app/document/nachtrag/addNachtragSubmittent.html',
        controller: 'AddNachtragSubmittentController',
        controllerAs: 'addNachtragSubmittentCtrl',
        backdrop: 'static',
        keyboard: false,
      });
    }

    function getNachtragSubmittents(submissionId) {
      NachtragService.getNachtragSubmittents(submissionId)
        .success(function (data) {
          vm.nachtragSubmittents = data;
        });
    }

    // Format number according to specifications
    function formatAmount(num) {
      return AppService.formatAmount(num);
    }

    function customRoundNumber(num) {
      return AppService.customRoundNumber(num);
    }

    function openSubmittentDetails(nachtragSubmittent) {
      nachtragSubmittent.show = !nachtragSubmittent.show;
      // if the details of one nachtrag submittent are opened, then close the one that
      // was opened before, so that there is always only one nachtrag submittent open
      if (nachtragSubmittent.show) {
        if (vm.displayedSubmittent) {
          vm.displayedSubmittent.show = false;
        }
        vm.displayedSubmittent = nachtragSubmittent;
        getNachtragList(nachtragSubmittent.id);
      } else {
        vm.displayedSubmittent = {};
        vm.nachtragList = {};
        NachtragService.setCheckedNachtragForDocCreation(null);
      }
    }

    function createNachtrag(nachtragSubmittentId) {
      AppService.setPaneShown(true);
      NachtragService.createNachtrag(nachtragSubmittentId)
        .success(function (data) {
          $state.reload();
          AppService.setPaneShown(false);
        });
    }

    function getNachtragList(nachtragSubmittentId) {
      NachtragService.getNachtragList(nachtragSubmittentId)
        .success(function (data) {
          vm.nachtragList = data;
        });
    }

    function openNachtragForm(nachtrag) {
      nachtrag.show = !nachtrag.show;
      // if the details of one nachtrag are opened, then close the one that
      // was opened before, so that there is always only one nachtrag open
      if (nachtrag.show) {
        if (vm.displayedNachtrag) {
          vm.displayedNachtrag.show = false;
        }
        vm.displayedNachtrag = nachtrag;
        getNachtrag(nachtrag.id);
      } else {
        vm.displayedNachtrag = {};
      }
    }

    function getNachtrag(nachtragId) {
      NachtragService.getNachtrag(nachtragId)
        .success(function (data) {
          vm.nachtrag = data;

          vm.CHFDiscount = calculateCHFDiscount(vm.nachtrag.grossAmount, vm.nachtrag.discount);
          vm.CHFDiscount2 = calculateCHFDiscount(vm.nachtrag.grossAmount, vm.nachtrag.discount2);
          vm.net = calculateNet(vm.nachtrag.grossAmount, vm.nachtrag.discount, vm.nachtrag.isDiscountPercentage,
            vm.buildingCosts, vm.isBuildingCostsPercentage, vm.nachtrag.discount2, vm.nachtrag.isDiscount2Percentage);

          // Set the value of MWST if it is in %.
          if (vm.nachtrag.isVatPercentage) {
            vm.tempVat = 0;
            vm.tempVatDD = String(vm.nachtrag.vat);
            vm.CHFMWST = vm.calculateCHFMWST(vm.net, vm.nachtrag.vat);
            vm.netInkl = vm.net + vm.CHFMWST;
          }
          StammdatenService.getVatsBySubmission($stateParams.submissionId)
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
              if (!vm.nachtrag.isVatPercentage) {
                vm.tempVatDD = vm.mainVat.value2;
                vm.tempVat = String(vm.nachtrag.vat);
                vm.netInkl = vm.net + vm.nachtrag.vat;
              }
            });
          calculateGesamtsumme();
        });
    }

    function deleteNachtrag(nachtrag) {
      var deleteNachtragModal = AppService.openGenericModal('Nachtrag löschen', 'Möchten Sie diesen Nachtrag wirklich löschen?');
      return deleteNachtragModal.result.then(function (response) {
        if (response) {
          AppService.setPaneShown(true);
          NachtragService.deleteNachtrag(nachtrag.id, nachtrag.version)
            .success(function (data) {
              $state.reload();
              AppService.setPaneShown(false);
            }).error(function (response, status) {
            if (status === 400 || status === 409) { // Validation errors.
              AppService.setPaneShown(false);
              QFormJSRValidation.markErrors($scope,
                $scope.nachtragCtrl.nachtragErrorForm, response);
            }
          });
          return true;
        }
        return false;
      });
    }

    function closeNachtrag(nachtrag) {
      var closeNachtragModal = AppService.openGenericModal('Nachtrag abschliessen', 'Möchten Sie diesen Nachtrag wirklich abschliessen?');
      return closeNachtragModal.result.then(function (response) {
        if (response) {
          AppService.setPaneShown(true);
          NachtragService.closeNachtrag(nachtrag.id, nachtrag.version)
            .success(function (data) {
              $state.reload();
              AppService.setPaneShown(false);
            }).error(function (response, status) {
            if (status === 400 || status === 409) { // Validation errors.
              AppService.setPaneShown(false);
              QFormJSRValidation.markErrors($scope,
                $scope.nachtragCtrl.nachtragErrorForm, response);
            }
          });
          return true;
        }
        return false;
      });
    }

    function reopenNachtrag(nachtrag) {
      var reopenModal = AppService.reopenTenderStatusModal('Nachtrag wiederaufnehmen',
        'Möchten Sie den Nachtrag wirklich wiederaufnehmen?', -1);
      return reopenModal.result.then(function (response) {
        if (response) {
          var reopenForm = {
            reopenReason: response
          };
          AppService.setPaneShown(true);
          NachtragService.reopenNachtrag(nachtrag.id, nachtrag.version, reopenForm)
            .success(function (data) {
              $state.reload();
              AppService.setPaneShown(false);
            }).error(function (response, status) {
            if (status === 400 || status === 409) { // Validation errors.
              AppService.setPaneShown(false);
              QFormJSRValidation.markErrors($scope,
                $scope.nachtragCtrl.nachtragErrorForm, response);
            }
          });
        }
      });
    }

    function openNachtragDate() {
      vm.openNachtragDate.opened = !vm.openNachtragDate.opened;
    }

    /** Function to ignore invalid number */
    function ignoreInvalidNumber(element) {
      return AppService.ignoreInvalidNumber(element);
    }

    function updateNachtragValuesBeforeSaving() {
      vm.nachtrag.amount = AppService.customRoundNumberWithoutSeparator(
        calculateNet(vm.nachtrag.grossAmount, vm.nachtrag.discount,
          vm.nachtrag.isDiscountPercentage,
          vm.nachtrag.buildingCosts, vm.nachtrag.isBuildingCostsPercentage,
          vm.nachtrag.discount2, vm.nachtrag.isDiscount2Percentage));
      if (vm.nachtrag.isBuildingCostsPercentage) {
        vm.nachtrag.buildingCostsValue = AppService.customRoundNumberWithoutSeparator(
          calculateCHFBuildingCostsValue(vm.nachtrag.grossAmount,
            vm.nachtrag.discount, vm.nachtrag.isDiscountPercentage,
            vm.nachtrag.discount2, vm.nachtrag.isDiscount2Percentage,
            vm.nachtrag.buildingCosts));
      }
      if (vm.nachtrag.isDiscountPercentage) {
        vm.nachtrag.discount1Value = AppService.customRoundNumberWithoutSeparator(
          calculateCHFDiscount(vm.nachtrag.grossAmount, vm.nachtrag.discount));
      }
      if (vm.nachtrag.isDiscount2Percentage) {
        vm.nachtrag.discount2Value = AppService.customRoundNumberWithoutSeparator(
          calculateCHFDiscount(vm.nachtrag.grossAmount, vm.nachtrag.discount2));
      }
      if(!(vm.tempVat == '0.00' || vm.tempVat == '0.0'
        || vm.tempVat == '0' || vm.tempVat == '')){
        vm.nachtrag.isVatPercentage = false;
        vm.nachtrag.vat =  vm.tempVat;
        vm.nachtrag.vatValue = vm.tempVat;
      }else {
        vm.nachtrag.isVatPercentage = true;
        vm.nachtrag.vat = vm.tempVatDD;
        vm.nachtrag.vatValue = AppService.customRoundNumberWithoutSeparator(
          calculateNet(vm.nachtrag.grossAmount,
            vm.nachtrag.discount, vm.nachtrag.isDiscountPercentage,
            vm.nachtrag.buildingCosts, vm.nachtrag.isBuildingCostsPercentage,
            vm.nachtrag.discount2, vm.nachtrag.isDiscount2Percentage)
          * vm.nachtrag.vat / 100);
      }
      vm.nachtrag.amountInclusive = AppService.customRoundNumberWithoutSeparator(
        calculateNet(vm.nachtrag.grossAmount, vm.nachtrag.discount,
          vm.nachtrag.isDiscountPercentage,
          vm.nachtrag.buildingCosts, vm.nachtrag.isBuildingCostsPercentage,
          vm.nachtrag.discount2, vm.nachtrag.isDiscount2Percentage)
        + parseFloat(vm.nachtrag.vatValue));
    }

    function save() {
      updateNachtragValuesBeforeSaving();
      AppService.setPaneShown(true);
      NachtragService.updateNachtrag(vm.nachtrag)
        .success(function (data) {
          AppService.setPaneShown(false);
          if (AppService.getIsDirty()) {
            AppService.setIsDirty(false);
          }
          getNachtrag(vm.nachtrag.id);
          setCheckedNachtragForDocCreation(vm.nachtrag);
        })
        .error(function (response, status) {
          if (status === 400) { // Validation errors.
            vm.errorFieldsVisible = true;
            AppService.setPaneShown(false);
            QFormJSRValidation.markErrors($scope,
              $scope.nachtragCtrl.nachtragForm, response);
          }else if(status === 409){ // Optimistic lock error
            AppService.setPaneShown(false);
            QFormJSRValidation.markErrors($scope,
              $scope.nachtragCtrl.nachtragErrorForm, response);
          }
        });
    }

    function calculateCHFDiscountValue(grossAmount, discount) {
      return calculateCHFDiscount(grossAmount, discount);
    }

    // Berechnung: (Wert des "Bruttobetrag" * Wert "Rabatt")/100.
    function calculateCHFDiscount(grossAmount, discount) {
      var returnValue;
      returnValue = (grossAmount * discount) / 100;
      return returnValue;
    }

    function calculateCHFMWST(net, vat) {
      return net * vat / 100;
    }

    function calculateNet(grossAmount, discount, isDiscountPercentage,
      buildingCosts, isBuildingCostsPercentage, discount2,
      isDiscount2Percentage) {
      if (grossAmount === null) {
        return null;
      }
      if (isDiscountPercentage) {
        discount = vm.calculateCHFDiscount(grossAmount, discount);
      }
      if (isDiscount2Percentage) {
        discount2 = vm.calculateCHFDiscount(grossAmount, discount2);
      }
      if (isBuildingCostsPercentage) {
        buildingCosts = vm.calculateCHFBuildingCosts(grossAmount, discount,
          discount2, buildingCosts);
      }
      return grossAmount - discount - discount2 - buildingCosts;
    }

    function calculateCHFBuildingCosts(grossAmount, discount,
      discount2, buildingCosts) {
      var returnValue;
      returnValue = ((grossAmount - discount - discount2) * buildingCosts) / 100;
      return returnValue;
    }

    function calculateCHFBuildingCostsValue(grossAmount, discount,
      isDiscountPercentage, discount2, isDiscount2Percentage, buildingCosts) {
      if (isDiscountPercentage) {
        discount = vm.calculateCHFDiscount(grossAmount, discount);
      }
      if (isDiscount2Percentage) {
        discount2 = vm.calculateCHFDiscount(grossAmount, discount2);
      }
      return vm.calculateCHFBuildingCosts(grossAmount, discount,
        discount2, buildingCosts);
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


    function calculateGesamtsumme(){
      var mainOfferAmount = vm.displayedSubmittent.amount;
      var gesamtsummeString = customRoundNumber(mainOfferAmount) + ' CHF';
      var tempGesamtsumme = mainOfferAmount;
      for (var i = 0; i < vm.nachtragList.length; i++) {
        if(vm.nachtragList[i].grossAmount && vm.nachtragList[i].grossAmount !== 0){
          var nachtrag = vm.nachtragList[i];
          if(vm.nachtrag.id === vm.nachtragList[i].id){
            nachtrag = vm.nachtrag;
          }
          var tmpAmount = calculateNet(nachtrag.grossAmount,
            nachtrag.discount, nachtrag.isDiscountPercentage,
            nachtrag.buildingCosts, nachtrag.isBuildingCostsPercentage,
            nachtrag.discount2, nachtrag.isDiscount2Percentage);
          tempGesamtsumme = tempGesamtsumme + tmpAmount;
          gesamtsummeString = gesamtsummeString + ' + ' + customRoundNumber(tmpAmount) + ' CHF';
        }
      }
      gesamtsummeString = gesamtsummeString + ' (' + customRoundNumber(tempGesamtsumme) + ' CHF)';
      vm.gesamtsumme = gesamtsummeString;
    }

    function resetPage(dirtyflag) {
      if (dirtyflag) {
        var closeEditModal = $uibModal
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
                closeEditModal.dismiss(reason);
              };
            }
          });
        return closeEditModal.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            $state.reload();
          }
        });
      }
    }

    function disableForm(nachtrag) {
      return (!angular.isUndefined(nachtrag) && nachtrag.closed)
        || !vm.enableFields || !vm.displayedSubmittent.isNachtragSubmittent
        || !AppService.isOperationPermitted(
          AppConstants.OPERATION.NACHTRAG_EDIT, null);
    }

    function disableCreateButton(nachtragSubmittent) {
      return nachtragSubmittent.hasActiveNachtrag || !vm.enableFields
        || !nachtragSubmittent.isNachtragSubmittent
        || !AppService.isOperationPermitted(
          AppConstants.OPERATION.NACHTRAG_CREATE, null);
    }

    function disableReopenButton(nachtrag) {
      return !nachtrag.closed || !vm.enableFields
        || !nachtrag.offer.isNachtragSubmittent
        || !AppService.isOperationPermitted(
          AppConstants.OPERATION.NACHTRAG_REOPEN, null);
    }

    function disableCloseButton(nachtrag) {
      return !checkIfNachtragHasBeenSaved(nachtrag) || !vm.enableFields
        || !nachtrag.offer.isNachtragSubmittent || nachtrag.closed
        || !AppService.isOperationPermitted(
          AppConstants.OPERATION.NACHTRAG_CLOSE, null);
    }

    function disableDeleteButton(nachtrag) {
      return nachtrag.closed || !vm.enableFields
        || !AppService.isOperationPermitted(
          AppConstants.OPERATION.NACHTRAG_DELETE, null);
    }

    function checkIfNachtragHasBeenSaved(nachtrag) {
      return !(nachtrag.title === null
        || nachtrag.grossAmount === null || nachtrag.grossAmount === "0.00"
        || nachtrag.nachtragDate === null);
    }

    function setCheckedNachtragForDocCreation(checkedNachtrag) {
      if (!angular.isUndefined(checkedNachtrag) && checkedNachtrag !== null) {
        NachtragService.getNachtrag(checkedNachtrag.id)
        .success(function (data) {
          checkedNachtrag = data;
          NachtragService.setCheckedNachtragForDocCreation([checkedNachtrag.id,
            checkedNachtrag.offer.isNachtragSubmittent,
            checkIfNachtragHasBeenSaved(checkedNachtrag),
            checkedNachtrag.offer.submittent.id,
            checkedNachtrag.offer.submittent.companyId.id]);
        })
      } else {
        NachtragService.setCheckedNachtragForDocCreation(null);
      }
    }

  }
})();
