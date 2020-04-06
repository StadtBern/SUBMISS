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
 * @name projectCreate.controller.js
 * @description ProjectCreateController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.company")
    .controller("CompanyCreateController", CompanyCreateController);

  /** @ngInject */
  function CompanyCreateController($scope, $locale, $state, CompanyService,
    StammdatenService, QFormJSRValidation, $uibModal, $transitions, AppService, AppConstants) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.company = {};
    vm.company.modificationDate = null;
    vm.company.logibDate = null;
    vm.company.logibKmuDate = null;
    vm.company.iloDate = null;
    vm.companyForm = {};
    vm.countries = [];
    vm.workTypes = [];
    vm.ilo = [];
    vm.companyNames = '';
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
    vm.areErrorsPresent = areErrorsPresent;
    vm.fiftyPlusFactor = fiftyPlusFactor;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      vm.loadCountries();
      vm.loadWorkTypes();
      vm.loadILO();
      vm.apprenticeFactor();
      vm.numberOfColleagues();
      vm.loadLogib();
      vm.loadLogibArgib();
      vm.company.branches = []; // initialise in order to be updated by the add button
      vm.secMainTenantBemerkungFabeEdit = AppService.isOperationPermitted(AppConstants.OPERATION.MAIN_TENANT_BEMERKUNG_FABE_EDIT, null);
      vm.secMainTenantBeschaffungswesenEdit = AppService.isOperationPermitted(AppConstants.OPERATION.MAIN_TENANT_BESCHAFFUNGSWESEN_EDIT, null);
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    $transitions.onBefore({}, function (transition) {
      if (transition.to() !== transition.from() && vm.dirtyFlag === true) {
        var closeEditModal = $uibModal.open({
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
            '<button type="button" class="btn btn-primary" ng-click="companyCreateCtr.closeWindowModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="companyCreateCtr.closeWindowModal(\'nein\')">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'companyCreateCtr',
          backdrop: 'static',
          keyboard: false,
          controller: function () {
            var vm = this;
            vm.closeWindowModal = function (reason) {
              closeEditModal.dismiss(reason);
            };
          }
        });
        return closeEditModal.result
          .then(function () {
            // Modal Success Handler
          }, function (response) { // Modal Dismiss Handler
            if (response === 'ja') {
              vm.dirtyFlag = false;
              $state.go(transition.to(), {}, {
                reload: true
              });
              return true;
            } else {
              return false;
            }
          });
      }
      return null;
    });

    //Function to declare what logib status and which checkbox it will automatilly
    function changeCheckboxValue() {
      if (vm.logib.workerNumber <= vm.numberOfColleagues(vm.company.numberOfMen, vm.company.numberOfWomen) &&
        vm.logib.womenNumber <= vm.company.numberOfWomen && vm.logib.menNumber <= vm.company.numberOfMen) {
        if (vm.company.logibKmuDate !== '' || vm.company.logibKmuDate == null) {
          vm.company.logibKmuDate = '';
        }
        vm.company.logIbARGIB = false;
        vm.company.logIb = true;

      } else if (vm.logibArgib.workerNumber <= vm.numberOfColleagues(vm.company.numberOfMen, vm.company.numberOfWomen) &&
        vm.logibArgib.womenNumber <= vm.company.numberOfWomen &&
        vm.logibArgib.menNumber <= vm.company.numberOfMen &&
        (vm.logib.workerNumber > vm.numberOfColleagues(vm.company.numberOfMen, vm.company.numberOfWomen) ||
          vm.logib.womenNumber > vm.company.numberOfWomen ||
          vm.logib.menNumber > vm.company.numberOfMen) || vm.company.logIbArgib === false) {
        if (vm.company.logibDate !== '') {
          vm.company.logibDate = '';
        }
        vm.company.logIbARGIB = true;
        vm.company.logIb = false;

      } else {
        vm.company.logibKmuDate = '';
        vm.company.logibDate = '';
        vm.company.logIbARGIB = false;
        vm.company.logIb = false;

      }
    }

    // Function that saves a company
    function save() {
      CompanyService.findIfTelephoneUnique(vm.company)
        .success(function (data) {
          if (data === true) {
            saveCompany(vm.company)
          } else {
            openUniqueTelephoneModal(vm.company);
          }
        })
    }

    // Function that opens a modal to add a company for a company
    function addCompany() {
      var addCompany = AppService.addCompany(false, null);
      return addCompany.result.then(function (response) {
        // take the chosen user response from modal and add it into company.branches
        for (var i = 0; i <= response.length - 1; i++) {
          vm.company.branches.push(response[i]);
        }
      });
    }

    function openUniqueTelephoneModal(company) {
      var uniqueCompanyTelephone = AppService.openGenericModal('Firma eröffnen', 'Die Telefonnummer besteht bereits. Wollen Sie die Firma wirklich speichern?');
      return uniqueCompanyTelephone.result.then(function (response) {
        if (response) {
          saveCompany(company);
          return true;
        }
        return false;
      });
    }

    function saveCompany(company) {
      vm.dirtyFlag = false;
      //Check this for cases where consultKaio is: negative,undefined,null,empty value and make the value false
      if (!company.consultKaio) {
        company.consultKaio = false;
      }
      //Check the cases if consultAdmin is negative,undefined,null,empty value and make the value false
      if (!company.consultAdmin) {
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
        CompanyService.createCompany(company)
          .success(function (data) {
            $state.go('company.view', {
              id: data.id
            });
          })
          .error(function (response, status) {
            if (status === 400) { // Validation errors.
              QFormJSRValidation.markErrors($scope,
                $scope.companyCreateCtr.companyForm, response);
            }
          });
      }
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


    // Function that reset the fields for create company
    function resetPage(dirtyflag) {
      if (!dirtyflag) {
        $state.go('company.create', {}, {
          reload: true
        });
      } else {
        var closeCompanyEditModal = $uibModal.open({
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
            '<button type="button" class="btn btn-primary" ng-click="companyCreateCtr.closeWindowModal(\'ja\')">Ja</button>' +
            '<button type="button" class="btn btn-default" ng-click="$close()">Nein</button>' +
            '</div>' + '</div>',
          controllerAs: 'companyCreateCtr',
          backdrop: 'static',
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
            $state.go('company.create', {}, {
              reload: true
            });
          } else {
            return false;
          }
          return null;
        });
      }
      return null;
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

    function openIloDate() {
      vm.openIloDate.opened = !vm.openIloDate.opened;
    }


    function onChangeTel() {
      vm.company.companyTel = vm.company.country.telPrefix;
      vm.company.companyFax = vm.company.country.telPrefix;
    }

    /** Function to check for errors */
    function areErrorsPresent() {
      return $scope.companyCreateCtr.companyForm.errorField.$invalid ||
        $scope.companyCreateCtr.companyForm.companyFaxErrorFieldMax.$invalid ||
        $scope.companyCreateCtr.companyForm.companyFaxErrorFieldInvalid.$invalid ||
        $scope.companyCreateCtr.companyForm.companyTelErrorFieldMax.$invalid ||
        $scope.companyCreateCtr.companyForm.companyAddress2ErrorFieldInvalid.$invalid ||
        $scope.companyCreateCtr.companyForm.uniqueTelephoneErrorField.$invalid ||
        $scope.companyCreateCtr.companyForm.companyTelErrorFieldInvalid.$invalid ||
        $scope.companyCreateCtr.companyForm.companyPostCodeErrorField.$invalid ||
        $scope.companyCreateCtr.companyForm.emailFormatErrorField.$invalid ||
        $scope.companyCreateCtr.companyForm.companyOriginIndicationErrorFieldInvalid.$invalid ||
        $scope.companyCreateCtr.companyForm.emailMaxSizeErrorField.$invalid ||
        $scope.companyCreateCtr.companyForm.companyNoteAdminErrorField.$invalid ||
        $scope.companyCreateCtr.companyForm.companyNameErrorFieldInvalid.$invalid ||
        $scope.companyCreateCtr.companyForm.companyWebsiteErrorFieldInvalid.$invalid ||
        $scope.companyCreateCtr.companyForm.companyNotesErrorField.$invalid ||
        $scope.companyCreateCtr.companyForm.companyAddress1ErrorFieldInvalid.$invalid ||
        $scope.companyCreateCtr.companyForm.companyNewVatErrorFieldInvalid.$invalid ||
        $scope.companyCreateCtr.companyForm.companyLocationErrorFieldInvalid.$invalid ||
        $scope.companyCreateCtr.companyForm.companyName.$invalid ||
        $scope.companyCreateCtr.companyForm.companyAddress1.$invalid ||
        $scope.companyCreateCtr.companyForm.companyPostCode.$invalid ||
        $scope.companyCreateCtr.companyForm.companyTel.$invalid ||
        $scope.companyCreateCtr.companyForm.companyEmail.$invalid ||
        $scope.companyCreateCtr.companyForm.companyLocation.$invalid ||
        $scope.companyCreateCtr.companyForm.countryName.$invalid ||
        $scope.companyCreateCtr.companyForm.companyWorkTypes.$invalid ||
        vm.invalidModificationDate ||
        $scope.companyCreateCtr.companyForm.infoOrigin.$invalid ||
        vm.invalidDate ||
        $scope.companyCreateCtr.companyForm.companyAdminNotesErrorField.$invalid;
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
