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
 * @name typeDataEdit.controller.js
 * @description TypeDataEditController controller
 */
(function () {
  "use strict";
  angular // eslint-disable-line no-undef
    .module("submiss").controller("TypeDataEditController", TypeDataEditController);

  /** @ngInject */
  function TypeDataEditController($rootScope, $scope, $state, StammdatenService,
    $stateParams, $filter, AppConstants, entryId, type, AppService,
    $uibModal, $uibModalInstance, QFormJSRValidation) {
    const
      vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var i;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.entryId = entryId;
    vm.type = type;
    vm.entry = {};
    vm.originalEntry = {};
    vm.dirtyFlag = false;
    vm.internalOrExternal = [true, false];
    vm.isLogib = false;
    vm.isProcessType = false;
    vm.isDepartment = false;
    vm.isCancelReason = false;
    vm.isWorkType = false;
    vm.isExclusionCriterion = false;
    vm.isCalculationFormula = false;
    vm.isDirectorate = false;
    vm.isEmailTemplate = false;
    vm.isCountry = false;
    vm.isILO = false;
    vm.isVatRate = false;
    vm.isProofs = false;
    vm.isObject = false;
    vm.isProcessPM = false;
    vm.isSettlementType = false;
    vm.isNegotiationReason = false;
    vm.notResizable = {
      "resize": "none"
    };
    vm.errorFieldsVisible = true;
    vm.isTenantKantonBern = false;
    vm.invalidImageMessageVisible = false;
    vm.oldValue1 = null;
    vm.oldValue2 = null;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.activeCheckboxVisible = activeCheckboxVisible;
    vm.descriptionVisible = descriptionVisible;
    vm.shortNameVisible = shortNameVisible;
    vm.articleVisible = articleVisible;
    vm.toInternalOrExternal = toInternalOrExternal;
    vm.setAvailablePartValue = setAvailablePartValue;
    vm.cancelButton = cancelButton;
    vm.save = save;
    vm.uploadSelectedImage = uploadSelectedImage;
    vm.uploaded = uploaded;
    vm.settingsInputValueVisible = settingsInputValueVisible;
    vm.isFrenchTextMandatory = isFrenchTextMandatory;
    vm.translateSendType = translateSendType;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      StammdatenService.loadSD()
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            $uibModalInstance.close();
            return;
          } else {
            defineModalTitle();
            checkType();
            initializeValues();
            getTypeDataEntryById();
            getDirectorates();
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
    /** Function to check which type is present */
    function checkType() {
      if (vm.type === AppConstants.CategorySD.LOGIB) {
        vm.errorFieldsVisible = false;
        vm.isLogib = true;
      } else if (vm.type === AppConstants.CategorySD.PROCESS_TYPE) {
        vm.isProcessType = true;
      } else if (vm.type === AppConstants.CategorySD.CANCEL_REASON) {
        vm.isCancelReason = true;
      } else if (vm.type === AppConstants.CategorySD.DEPARTMENT) {
        vm.isDepartment = true;
        vm.errorFieldsVisible = false;
      } else if (vm.type === AppConstants.CategorySD.WORKTYPE) {
        vm.isWorkType = true;
      } else if (vm.type === AppConstants.CategorySD.EXCLUSION_CRITERION) {
        vm.isExclusionCriterion = true;
      } else if (vm.type === AppConstants.CategorySD.CALCULATION_FORMULA) {
        vm.isCalculationFormula = true;
      } else if (vm.type === AppConstants.CategorySD.DIRECTORATE) {
        vm.isDirectorate = true;
      } else if (vm.type === AppConstants.CategorySD.COUNTRY) {
        vm.isCountry = true;
      } else if (vm.type === AppConstants.CategorySD.ILO) {
        vm.isILO = true;
      } else if (vm.type === AppConstants.CategorySD.VAT_RATE) {
        vm.isVatRate = true;
        vm.errorFieldsVisible = false;
      } else if (vm.type === AppConstants.CategorySD.PROOFS) {
        vm.isProofs = true;
        vm.errorFieldsVisible = false;
        getCountries();
      } else if (vm.type === AppConstants.CategorySD.EMAIL_TEMPLATE) {
        vm.isEmailTemplate = true;
        if (!vm.entryId) {
          vm.entry.attributes = [];
          // Adding attributes to email entry.
          vm.entry.attributes[0] = {};
          vm.entry.attributes[0].sendType = "TO";
          vm.entry.attributes[1] = {};
          vm.entry.attributes[1].sendType = "CC";
          vm.entry.attributes[2] = {};
          vm.entry.attributes[2].sendType = "BCC";
        }
        // Call function to determine if the current tenant is Kanton Bern.
        StammdatenService.isTenantKantonBern().success(function (data) {
          vm.isTenantKantonBern = data;
        });
      } else if (vm.type === AppConstants.CategorySD.OBJECT) {
        vm.isObject = true;
      } else if (vm.type === AppConstants.CategorySD.PROCESS_PM) {
        vm.isProcessPM = true;
      } else if (vm.type === AppConstants.CategorySD.SETTLEMENT_TYPE) {
        vm.isSettlementType = true;
      } else if (vm.type === AppConstants.CategorySD.NEGOTIATION_REASON) {
        vm.isNegotiationReason = true;
      } else if (vm.type === AppConstants.CategorySD.SETTINGS) {
        vm.errorFieldsVisible = false;
        vm.serverSideTypes = ["LPR", "RAW"];
        vm.isSettings = true;
      }
    }

    /** Function to get type data entry by id */
    function getTypeDataEntryById() {
      if (vm.entryId) {
        StammdatenService.getTypeDataEntryById(vm.entryId, vm.type).success(function (data) {
          vm.entry = data;
          //Using angular.copy because that value should not be changed.
          angular.copy(vm.entry, vm.originalEntry);

          if (vm.isProofs) {
            vm.oldValue1 = vm.entry.name;
            vm.oldValue2 = vm.entry.country.id;
            // Translate active value to true or false.
            if (vm.entry.active === 1) {
              vm.entry.active = true;
            } else if (vm.entry.active === 0) {
              vm.entry.active = false;
            }
            getCountries();
          } else if (vm.isVatRate) {
            vm.oldValue1 = vm.entry.value1;
            // Convert "Wert in %" value to number.
            vm.vatRatePercentage = Number(vm.entry.value2);
          } else if (vm.isSettings) {
            if (vm.entry.shortCode === "S6" || vm.entry.shortCode === "S12") {
              vm.entry.value2 = Number(vm.entry.value2);
            }
          } else if (vm.isDepartment) {
            vm.oldValue1 = vm.entry.name;
            vm.oldValue2 = vm.entry.shortName;
          } else if (vm.isDirectorate) {
            vm.oldValue1 = vm.entry.name;
          } else if (vm.isCancelReason || vm.isWorkType || vm.isExclusionCriterion ||
            vm.isNegotiationReason || vm.isCalculationFormula || vm.isILO ||
            vm.isObject || vm.isProcessPM || vm.isSettlementType) {
            vm.oldValue1 = vm.entry.value2;
          } else if (vm.isCountry) {
            vm.oldValue1 = vm.entry.countryName;
          }
        });
      }
    }

    /** Function to get the country information */
    function getCountries() {
      StammdatenService.readAllCountries().success(function (data) {
        vm.countries = data;
        // Get the id of Switzerland, which is the first country in the list.
        vm.switzerlandId = vm.countries[0].countryId.id;
        if (!vm.entryId) {
          // Select the first country as default (case of creating a new entry).
          vm.entryCountry = vm.countries[0];
        } else {
          // Get the country information by the entry country id (case of updating an entry).
          if (!angular.isUndefined(vm.entry.country)) {
            for (i in vm.countries) {
              if (vm.entry.country.id === vm.countries[i].countryId.id) {
                vm.entryCountry = vm.countries[i];
                break;
              }
            }
          }
        }
        isFrenchTextMandatory();
      });
    }

    /** Function to get all directorates if necessary */
    function getDirectorates() {
      if (vm.isDepartment) {
        StammdatenService.readAllDirectorates().success(function (data) {
          vm.directorates = data;
          // In case of creating a new entry, select the first directorate as default choice.
          if (!vm.entryId) {
            vm.entry.directorate = vm.directorates[0];
          }
        });
      }
    }

    /** Function to determine if the active checkbox is visible */
    function activeCheckboxVisible() {
      return vm.isLogib || vm.isCancelReason || vm.isDepartment || vm.isWorkType ||
        vm.isExclusionCriterion || vm.isCalculationFormula || vm.isDirectorate ||
        vm.isEmailTemplate || vm.isILO || vm.isVatRate || vm.isProofs ||
        vm.isObject || vm.isProcessPM || vm.isSettlementType ||
        vm.isNegotiationReason || vm.isCountry;
    }

    /** Function to determine if the description field is visible */
    function descriptionVisible() {
      return vm.isLogib || vm.isProcessType || vm.isCancelReason || vm.isDepartment ||
        vm.isWorkType || vm.isExclusionCriterion || vm.isCalculationFormula ||
        vm.isDirectorate || vm.isEmailTemplate || vm.isCountry || vm.isILO ||
        vm.isVatRate || vm.isProofs || vm.isObject || vm.isProcessPM ||
        vm.isSettlementType || vm.isNegotiationReason;
    }

    /** Function to determine if the article field is visible */
    function articleVisible() {
      if (vm.isCancelReason || vm.isNegotiationReason) {
        vm.articleTitle = "Artikel und Litera";
      } else if (vm.isExclusionCriterion) {
        vm.articleTitle = "Litera";
      }
      return vm.isCancelReason || vm.isExclusionCriterion || vm.isNegotiationReason;
    }

    /** Function to determine if the short name field is visible */
    function shortNameVisible() {
      return vm.isDepartment;
    }

    /** Translate true/false to internal/external */
    function toInternalOrExternal(value) {
      if (value) {
        return "Intern";
      } else {
        return "Extern";
      }
    }

    function setAvailablePartValue(projectAvailable) {
      if (projectAvailable) {
        if (vm.entry.availablePart !== 'PROJECT_PART') {
          vm.entry.availablePart = 'PROJECT_PART';
        } else {
          vm.entry.availablePart = null;
        }
      } else {
        if (vm.entry.availablePart !== 'COMPANY_PART') {
          vm.entry.availablePart = 'COMPANY_PART';
        } else {
          vm.entry.availablePart = null;
        }
      }
    }

    /** Implementing the cancellation button */
    function cancelButton() {
      if (vm.dirtyFlag) {
        var closeTypeEditModal = $uibModal
          .open({
            template: '<div class="modal-header">' +
              '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
              '</div>' +
              '<div class="modal-body">' +
              '<div class="row">' +
              '<div class="col-md-12">' +
              '<p> Möchten Sie den Vorgang wirklich abbrechen? Nicht gespeicherte Informationen gehen dabei verloren. </p>' +
              '</div>' +
              '</div>' +
              '</div>' +
              '<div class="modal-footer">' +
              '<button type="button" class="btn btn-primary" ng-click="closeEditCtrl.closeWindowModal()">Ja</button>' +
              '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
              '</div>' + '</div>',
            controllerAs: 'closeEditCtrl',
            backdrop: 'static',
            keyboard: false,
            controller: function () {
              var vm = this;
              vm.closeWindowModal = function () {
                closeTypeEditModal.dismiss();
              };
            }
          });
        return closeTypeEditModal.result.then(function () {}, function () { // Modal Dismiss Handler
          $uibModalInstance.close();
          $state.reload();
        });
      } else {
        $uibModalInstance.dismiss();
        return null;
      }
    }

    /** Saving functionality */
    function save() {
      // Convert "Wert in %" value back to String, if present.
      if (vm.isVatRate && vm.vatRatePercentage != null && !angular.isUndefined(vm.vatRatePercentage)) {
        vm.entry.value2 = vm.vatRatePercentage.toString();
      }
      //do not save if the updated Object is equal with the Original
      if (angular.equals(JSON.stringify(vm.entry), JSON.stringify(vm.originalEntry))) {
        $uibModalInstance.close();
        return;
      }

      if (vm.isDepartment) {
        if (angular.isUndefined(vm.entry.internal)) {
          vm.entry.booleanInternalValue = null;
        } else {
          vm.entry.booleanInternalValue = vm.entry.internal;
        }
        // check if the combination (name, shortName) has changed
        var isNameOrShortNameChanged = vm.oldValue1 !== vm.entry.name || vm.oldValue2 !== vm.entry.shortName;
        StammdatenService.saveDepartmentEntry(vm.entry, isNameOrShortNameChanged).success(function (data, status) {
          $uibModalInstance.close();
          if (status === 403) { // Security checks.
            return;
          } else {
            $state.reload();
          }
        }).error(function (response, status) {
          if (status === 400) {
            vm.errorFieldsVisible = true;
            QFormJSRValidation.markErrors($scope,
              $scope.editForm, response);
          }
        });
      } else if (vm.isDirectorate) {
        // check if name has changed
        var isNameChanged = vm.oldValue1 !== vm.entry.name;
        StammdatenService.saveDirectorateEntry(vm.entry, isNameChanged).success(function (data, status) {
          $uibModalInstance.close();
          if (status === 403) { // Security checks.
            return;
          } else {
            $state.reload();
          }
        }).error(function (response, status) {
          if (status === 400) {
            QFormJSRValidation.markErrors($scope,
              $scope.editForm, response);
          }
        });
      } else if (vm.isCountry) {
        // check if name has changed
        var isNameChanged = vm.oldValue1 !== vm.entry.countryName;
        StammdatenService.saveCountryEntry(vm.entry, isNameChanged).success(function (data, status) {
          $uibModalInstance.close();
          if (status === 403) { // Security checks.
            return;
          } else {
            $state.reload();
          }
        }).error(function (response, status) {
          if (status === 400) {
            QFormJSRValidation.markErrors($scope,
              $scope.editForm, response);
          }
        });
      } else if (vm.isLogib) {
        StammdatenService.saveLogibEntry(vm.entry).success(function (data, status) {
          $uibModalInstance.close();
          if (status === 403) { // Security checks.
            return;
          } else {
            $state.reload();
          }
        }).error(function (response, status) {
          if (status === 400) {
            vm.errorFieldsVisible = true;
            QFormJSRValidation.markErrors($scope,
              $scope.editForm, response);
          }
        });
      } else if (vm.isProofs) {
        saveProof();
      } else if (vm.isEmailTemplate) {
        StammdatenService.saveEmailTemplateEntry(vm.entry).success(function (data, status) {
          $uibModalInstance.close();
          if (status === 403) { // Security checks.
            return;
          } else {
            $state.reload();
          }
        }).error(function (response, status) {
          if (status === 400) {
            QFormJSRValidation.markErrors($scope,
              $scope.editForm, response);
          }
        });
      } else {
        var isValueChanged;
        if (vm.isVatRate) {
          // check if value1 has changed
          isValueChanged = vm.oldValue1 !== vm.entry.value1;
          if (!(vm.vatRatePercentage != null && !angular.isUndefined(vm.vatRatePercentage))) {
            vm.entry.value2 = null;
          }
        } else {
          // check if value2 has changed
          isValueChanged = vm.oldValue1 !== vm.entry.value2;
        }
        StammdatenService.saveSDEntry(vm.entry, vm.type, isValueChanged).success(function (data, status) {
          $uibModalInstance.close();
          if (status === 403) { // Security checks.
            return;
          } else {
            $state.reload();
          }
        }).error(function (response, status) {
          if (status === 400) {
            vm.errorFieldsVisible = true;
            QFormJSRValidation.markErrors($scope,
              $scope.editForm, response);
          }
        });
      }
    }

    /** Function to initialize values in case of creating a new entry */
    function initializeValues() {
      if (!vm.entryId) {
        if (vm.isEmailTemplate || vm.isLogib) {
          vm.entry.isActive = true;
        } else {
          vm.entry.active = true;
        }
        if (vm.isDepartment) {
          vm.entry.internal = true;
        }
        if (vm.isDepartment || vm.isDirectorate) {
          vm.entry.website = "http://";
        }
        if (vm.isProofs) {
          vm.entry.required = true;
        }
      }
    }

    /** Check if the settings input value field is visible */
    function settingsInputValueVisible() {
      return vm.entry.shortCode === 'S7' ||
        vm.entry.shortCode === 'S1' ||
        vm.entry.shortCode === 'S10' ||
        vm.entry.shortCode === 'S3' ||
        vm.entry.shortCode === 'S2' ||
        vm.entry.shortCode === 'S11' ||
        vm.entry.shortCode === 'S4';
    }

    function uploadSelectedImage(flow) {
      flow.upload();
    }

    function uploaded(flow) {
      var image = flow.files[0];
      if (image && !image.error) {
        StammdatenService.uploadSelectedImage(image.uniqueIdentifier, vm.entry.id).success(function (data) {
          if (vm.isSettings) {
            vm.entry.tenantLogo = data[0];
          } else if (vm.isDepartment) {
            vm.entry.logo = data[0];
          }
          vm.dirtyFlag = true;
          vm.invalidImageMessageVisible = false;
        }).error(function (response, status) {
          if (status === 400) {
            vm.invalidImageMessageVisible = true;
            QFormJSRValidation.markErrors($scope,
              $scope.editForm, response);
          }
        });
      }
      flow.cancel();
    }

    /** Function to define the modal title */
    function defineModalTitle() {
      if (vm.entryId) {
        vm.modalTitle = "Bearbeiten";
      } else {
        vm.modalTitle = "Neuen Eintrag erstellen";
      }
    }

    /** Function to check if the French text field is mandatory */
    function isFrenchTextMandatory() {
      if (vm.entryCountry && vm.entryCountry.countryId.id === vm.switzerlandId) {
        // If the selected country is Switzerland, the French text field is mandatory.
        vm.frenchTextMandatory = true;
      } else {
        vm.frenchTextMandatory = false;
      }
    }

    /** Function to translate the send type */
    function translateSendType(input) {
      if (input === "TO") {
        input = "An";
      }
      return input;
    }

    /** Function to implement the proof saving/update functionality */
    function saveProof() {
      // Translate active value to integer.
      if (vm.entry.active) {
        vm.entry.active = 1;
      } else {
        vm.entry.active = 0;
      }
      // Get the country id and set it to the entry.
      vm.entry.country = {};
      if (vm.entryCountry) {
        vm.entry.country.id = vm.entryCountry.countryId.id;
      }
      // Modal to inform the user about possible delays during saving.
      var possibleDelaysModal = $uibModal
        .open({
          template: '<div class="modal-header">' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Die Aktualisierung bzw. Speicherung der Nachweisart kann sich etwas verzögern. ' +
            'Bitte brechen Sie den Vorgang nicht ab. Klicken Sie auf "OK", um weiter fortzufahren. </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="delaysCtrl.possibleDelays()">OK</button>' +
            '</div>' + '</div>',
          controllerAs: 'delaysCtrl',
          backdrop: 'static',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.possibleDelays = function () {
              possibleDelaysModal.dismiss();
            };
          }
        });
      return possibleDelaysModal.result.then(function () {}, function () { // Modal Dismiss Handler
        StammdatenService.isFirstCountryProof(vm.entry.country.id).success(function (data) {
          if (data) {
            var changedCountryModal = AppService.openGenericModal(vm.modalTitle, 'Das Land wurde geändert. Möchten Sie wirklich fortfahren? Alle Nachweise gehen dabei verloren.', '');
            return changedCountryModal.result.then(function (response) {
              if (response) {
                saveProofEntry();
              }
            });
          } else {
            saveProofEntry();
          }
          return null;
        })
      });
    }

    function saveProofEntry() {
      AppService.setPaneShown(true);
      // check if the combination (name, country) is changed
      var isNameOrCountryChanged = vm.oldValue1 !== vm.entry.name || vm.oldValue2 !== vm.entry.country.id;
      StammdatenService.saveProofsEntry(vm.entry, isNameOrCountryChanged).success(function (data, status) {
        AppService.setPaneShown(false);
        $uibModalInstance.close();
        if (status === 403) { // Security checks.
          return;
        } else {
          $state.reload();
        }
      }).error(function (response, status) {
        AppService.setPaneShown(false);
        // Translate active value back to true or false.
        if (vm.entry.active === 1) {
          vm.entry.active = true;
        } else if (vm.entry.active === 0) {
          vm.entry.active = false;
        }
        if (status === 400) {
          vm.errorFieldsVisible = true;
          QFormJSRValidation.markErrors($scope,
            $scope.editForm, response);
        }
      });
    }
  }
})();
