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
 * @name companyUpdate.controller.js
 * @description CompanyUpdateController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.company")
    .controller("CompanyUpdateController", CompanyUpdateController);

  /** @ngInject */
  function CompanyUpdateController($scope, $location, $anchorScroll, $locale, $state, CompanyService,
    StammdatenService, QFormJSRValidation, $uibModal, $uibModalInstance, company, AppService, AppConstants) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var archivedPrevious;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.company = company;
    vm.initialCountryId = vm.company.country.id;
    vm.companyForm = {};
    vm.countries = [];
    vm.workTypes = [];
    vm.ilo = [];
    vm.logib = {};
    vm.logibArgib = {};
    vm.secMainTenantBemerkungFabeEdit = false;
    vm.secMainTenantBeschaffungswesenEdit = false;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.save = save;
    vm.loadCountries = loadCountries;
    vm.loadWorkTypes = loadWorkTypes;
    vm.addCompany = addCompany;
    vm.resetPage = resetPage;
    vm.loadILO = loadILO;
    vm.closeWindow = closeWindow;
    vm.openModificationDate = openModificationDate;
    vm.openLogibDate = openLogibDate;
    vm.openLogibKmuDate = openLogibKmuDate;
    vm.openIloDate = openIloDate;
    vm.apprenticeFactor = apprenticeFactor;
    vm.numberOfColleagues = numberOfColleagues;
    vm.onChangeTel = onChangeTel;
    vm.loadLogib = loadLogib;
    vm.loadLogibArgib = loadLogibArgib;
    vm.changeCheckboxValue = changeCheckboxValue;
    vm.errorFieldFocus = errorFieldFocus;
    vm.showOptimisticLockErrorMessage = false;
    vm.checkForCountryChangeBeforeSaving = checkForCountryChangeBeforeSaving;
    vm.fiftyPlusFactor = fiftyPlusFactor;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      CompanyService.loadCompanyUpdate()
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            $uibModalInstance.close();
            return;
          } else {
            vm.loadCountries();
            vm.loadWorkTypes();
            vm.loadILO();
            vm.apprenticeFactor();
            vm.numberOfColleagues();
            vm.loadLogib();
            vm.loadLogibArgib();
            vm.secMainTenantBemerkungFabeEdit = AppService.isOperationPermitted(AppConstants.OPERATION.MAIN_TENANT_BEMERKUNG_FABE_EDIT, null);
            vm.secMainTenantBeschaffungswesenEdit = AppService.isOperationPermitted(AppConstants.OPERATION.MAIN_TENANT_BESCHAFFUNGSWESEN_EDIT, null);
            isArchivedPrevious();
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
    // Function to declare what logib status and which checkbox it will
    // automatilly
    function changeCheckboxValue() {
      if (vm.logib.workerNumber <= vm.numberOfColleagues(
          vm.company.numberOfMen, vm.company.numberOfWomen) &&
        vm.logib.womenNumber <= vm.company.numberOfWomen &&
        vm.logib.menNumber <= vm.company.numberOfMen) {
        vm.company.logIbARGIB = false;
        vm.company.logIb = true;
      } else if (vm.logibArgib.workerNumber <= vm.numberOfColleagues(
          vm.company.numberOfMen, vm.company.numberOfWomen) &&
        vm.logibArgib.womenNumber <= vm.company.numberOfWomen &&
        vm.logibArgib.menNumber <= vm.company.numberOfMen &&
        (vm.logib.workerNumber > vm.numberOfColleagues(
            vm.company.numberOfMen, vm.company.numberOfWomen) ||
          vm.logib.womenNumber > vm.company.numberOfWomen || vm.logib.menNumber > vm.company.numberOfMen) ||
        vm.company.logIbArgib === false) {

        vm.company.logIbARGIB = true;
        vm.company.logIb = false;

      } else {
        vm.company.logIbARGIB = false;
        vm.company.logIb = false;
      }
    }

    /** Function to check if the country has been changed before saving */
    function checkForCountryChangeBeforeSaving() {
      if (vm.company.country && vm.initialCountryId && vm.initialCountryId !== vm.company.country.id) {
        var changedCountryModal = AppService.countryChangeProofDeletion();
        return changedCountryModal.result.then(function (response) {
          if (response) {
            save();
          }
        });
      } else {
        save();
      }
      return null;
    }

    function save() {
      CompanyService.findIfTelephoneUnique(vm.company)
        .success(function (data) {
          if (data === true) {
            saveCompany(vm.company)
          } else {
            openUniqueTelephoneModal(vm.company);
          }
        });
    }

    function openUniqueTelephoneModal(company) {
      var uniqueCompanyTelephone = AppService.openGenericModal('Firma bearbeiten', 'Die Telefonnummer besteht bereits. Wollen Sie die Firma wirklich speichern?');
      return uniqueCompanyTelephone.result.then(function (response) {
        if (response) {
          saveCompany(company);
          return true;
        }
        return false;
      });
    }

    // Function that saves a company
    function saveCompany(company) {
      if (company.consultKaio === null) {
        company.consultKaio = false;
      }
      if (company.consultAdmin === null) {
        company.consultAdmin = false;
      }
      if (angular.isUndefined(company.modificationDate) || angular.isUndefined(company.logibDate) ||
        angular.isUndefined(company.logibKmuDate) || angular.isUndefined(company.iloDate)) {
        vm.invalidDate = true;
        if (angular.isUndefined(company.modificationDate)) {
          vm.invalidModificationDate = true;
        }
        if (angular.isUndefined(company.logibDate)) {
          vm.invalidLogibDate = true;
        }
        if (angular.isUndefined(company.logibKmuDate)) {
          vm.invalidLogibKmuDate = true;
        }
        if (angular.isUndefined(company.iloDate)) {
          vm.invalidIloDate = true;
        }
      } else {
        vm.invalidDate = false;
        CompanyService.updateCompany(company, vm.archivedPrevious)
          .success(function (data, status) {
            $uibModalInstance.close();
            if (status === 403) { // Security checks.
              return;
            }
            $state.go('company.view', {
              id: company.id
            }, {
              reload: true
            });
          })
          .error(function (response, status) {
            if (status === 400) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.companyUpdateCtr.companyForm, response);
            }
          });
        vm.companyForm = $scope.companyUpdateCtr.companyForm;
        vm.errorFieldFocus(vm.companyForm);
      }
    }

    function closeWindow(dirtyflag) {
      if (dirtyflag) {
        var closeCompanyEditModal = $uibModal.open({
          template: '<div class="modal-header">' +
            '<button type="button" class="close" aria-label="Close" ng-click="$close()"><span aria-hidden="true">&times;</span></button>' +
            '<h4 class="modal-title">Firma bearbeiten</h4>' +
            '</div>' +
            '<div class="modal-body">' +
            '<div class="row">' +
            '<div class="col-md-12">' +
            '<p> Möchten Sie den Vorgang wirklich abbrechen? Nicht gespeicherte Informationen gehen dabei verloren </p>' +
            '</div>' +
            '</div>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button type="button" class="btn btn-primary" ng-click="companyUpdateCtr.closeWindowModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'companyUpdateCtr',
          backdrop: 'static',
          size: 'lg',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeWindowModal = function (reason) {
              closeCompanyEditModal.dismiss(reason);
            };
          }

        });
        return closeCompanyEditModal.result.then(function () {
          // Modal Success Handler
        }, function (response) { // Modal Dismiss Handler
          if (response === 'ja') {
            $uibModalInstance.close();
            $state.go($state.current, {}, {
              reload: true
            });
            return true;
          } else {
            return false;
          }
        });
      } else {
        $uibModalInstance.dismiss();
      }
      return null;
    }

    // Function that opens a modal to add a company for a company
    function addCompany() {
      var addCompany = AppService.addCompany(false, vm.company.id);
      return addCompany.result.then(function (response) {
        // take the chosen user response from modal and add it into company.branches
        for (var i = 0; i <= response.length - 1; i++) {
          vm.company.branches.push(response[i]);

          vm.companyForm.$dirty = true;
        }
      });

    }

    // Function that stores the previous archived selection
    function isArchivedPrevious() {
      vm.archivedPrevious = vm.company.archived;
    }

    // Function that returns all Countries
    function loadCountries() {
      StammdatenService.readAllCountries().success(function (data) {
        vm.countries = data;
      }).error(function (response, status) {});
    }

    // Function that return a Logib from referenceData
    function loadLogib() {
      StammdatenService.readLogib().success(function (data) {
        vm.logib = data;

      }).error(function (response, status) {});
    }

    // Function that return a LogibARGIB from referenceData
    function loadLogibArgib() {
      StammdatenService.readLogibArgib().success(function (data) {
        vm.logibArgib = data;
      }).error(function (response, status) {});
    }

    // Function that returns all Arbeitsgattungen/Work Types
    function loadWorkTypes() {
      StammdatenService.readAllWorkTypes().success(function (data) {
        vm.workTypes = AppService.showOnlyActive(data);
      }).error(function (response, status) {});
    }

    // Function that returns all ILO
    function loadILO() {
      StammdatenService.readAllILO().success(function (data) {
        vm.ilo = AppService.showOnlyActive(data);
      }).error(function (response, status) {});
    }

    // Function that reset the fields for update company
    function resetPage() {
      $state.go('companyUpdate', {}, {
        reload: true
      });
    }

    function openModificationDate() {
      vm.openModificationDate.opened = !vm.openModificationDate.opened;
    }

    function openLogibDate() {
      vm.openLogibDate.opened = !vm.openLogibDate.opened;
    }

    function openLogibKmuDate() {
      vm.openLogibKmuDate.opened = !vm.openLogibKmuDate.opened;
    }

    function openIloDate() {
      vm.openIloDate.opened = !vm.openIloDate.opened;
    }

    function numberOfColleagues(numberOfMen, numberOfWomen) {
      var colleagues = parseInt(numberOfMen) * 1 + parseInt(numberOfWomen);
      return colleagues || 0;
    }

    function apprenticeFactor(numberOfMen, numberOfWomen, numberOfTrainees) {
      var colleagues = numberOfColleagues(numberOfMen, numberOfWomen);
      if (numberOfTrainees && colleagues) {
        var apFactor = parseInt(numberOfTrainees) * 1 / (colleagues);
        return apFactor || 0;
      } else {
        return 0;
      }
    }

    function onChangeTel() {
      if (vm.company.country) {
        vm.company.companyTel = vm.company.country.telPrefix;
        vm.company.companyFax = vm.company.country.telPrefix;
      }
    }

    /** Function that navigates to the top of the modal dialog if there is an error*/
    function errorFieldFocus(companyForm) {
      if (companyForm.$dirty) {
        $anchorScroll('errorScroll');
      }
    }

    /** Function to calculate the 50+ factor */
    function fiftyPlusFactor(fiftyPlusColleagues, numberOfMen, numberOfWomen) {
      var colleagues = numberOfColleagues(numberOfMen, numberOfWomen)
      if (fiftyPlusColleagues && colleagues) {
        return fiftyPlusColleagues / colleagues;
      }
      return 0;
    }
  }
})();
