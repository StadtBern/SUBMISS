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
 * @name typeData.controller.js
 * @description TypeDataController controller
 */
(function () {
  "use strict";
  angular // eslint-disable-line no-undef
    .module("submiss").controller("TypeDataController", TypeDataController);

  /** @ngInject */
  function TypeDataController($rootScope, $scope, $state, StammdatenService, $stateParams,
    NgTableParams, $filter, AppConstants, $uibModal, AppService) {
    const
      vm = this;
    /***********************************************************************
     * Local variables.
     **********************************************************************/

    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.tableParams = new NgTableParams({
      page: 1,
      count: 10,
      sorting: {
        objectName: 'asc'
      }
    });
    vm.yesOrNo = [{
        id: null,
        title: ''
      },
      {
        id: true,
        title: 'Ja'
      },
      {
        id: false,
        title: 'Nein'
      }
    ];
    vm.availablePartChoices = [{
        id: null,
        title: ''
      },
      {
        id: "COMPANY_PART",
        title: 'Firmenteil'
      },
      {
        id: "PROJECT_PART",
        title: 'Projektteil'
      },
      {
        id: "USER_PART",
        title: 'Benutzerverwaltung'
      }
    ];
    vm.type = $stateParams.type;
    vm.additionalTitle = null;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.additionalInformationRequired = additionalInformationRequired;
    vm.isTypeLogib = isTypeLogib;
    vm.isTypeProofs = isTypeProofs;
    vm.isTypeEmailTemplate = isTypeEmailTemplate;
    vm.isProcessType = isProcessType;
    vm.isTemplateType = isTemplateType;
    vm.editTypeData = editTypeData;
    vm.newEntryPossible = newEntryPossible;
    vm.isEditAllowed = isEditAllowed;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      StammdatenService.loadSD()
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            getMasterListTypeData($stateParams.type);
            setTypeName();
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
    $rootScope.$on('$stateChangeSuccess', function (event, to, toParams,
      from, fromParams) {
      // save the previous state in a rootScope variable so that it's
      // accessible from everywhere
      $rootScope.previousState = from;
    });

    /** Function to retrieve data by master list type. */
    function getMasterListTypeData(type) {
      StammdatenService.getMasterListTypeData(type).success(function (data) {
        vm.typeData = data;
        if (type === AppConstants.CategorySD.SETTINGS) {
          // Find the logo setting and set its added description value to null,
          // as it has no meaning.
          for (var i in vm.typeData) {
            if (vm.typeData[i].shortCode === "S5") {
              vm.typeData[i].addedDescription = null;
              break;
            }
          }
        }
        vm.tableParams = new NgTableParams({
          page: 1,
          count: 10,
          sorting: {
            title: 'asc' // initial sorting
          }
        }, {
          total: vm.typeData.length,
          getData: function ($defer, params) {
            var filters = params.filter(true);
            var filteredData;
            if (filters) {
              filteredData = vm.typeData;
              filteredData = $filter('filter')(filteredData, filters);
            } else {
              filteredData = vm.documents;
            }
            var orderedData = params.sorting() ? $filter(
              'orderBy')(filteredData,
              params.orderBy()) : filteredData;

            $defer.resolve(orderedData.slice(
              (params.page() - 1) * params.count(),
              params.page() * params.count()));
            // count filtered results
            params.total(filteredData.length);
          }
        });
      });
    }

    function isEditAllowed(shortCode) {
      return shortCode === 'S14';
    }

    /** Function to check if additional information is required. */
    function additionalInformationRequired(type) {
      if (type === AppConstants.CategorySD.CANCEL_REASON ||
        type === AppConstants.CategorySD.EXCLUSION_CRITERION ||
        type === AppConstants.CategorySD.NEGOTIATION_REASON ||
        type === AppConstants.CategorySD.WORKTYPE ||
        type === AppConstants.CategorySD.CALCULATION_FORMULA ||
        type === AppConstants.CategorySD.VAT_RATE ||
        type === AppConstants.CategorySD.DEPARTMENT ||
        type === AppConstants.CategorySD.DIRECTORATE ||
        type === AppConstants.CategorySD.COUNTRY ||
        type === AppConstants.CategorySD.PROOFS ||
        type === AppConstants.CategorySD.SETTINGS) {
        // If additional information is required, define the additional column title.
        if (type === AppConstants.CategorySD.CANCEL_REASON ||
          type === AppConstants.CategorySD.NEGOTIATION_REASON) {
          vm.additionalTitle = "Artikel und Litera";
        } else if (type === AppConstants.CategorySD.EXCLUSION_CRITERION) {
          vm.additionalTitle = "Litera";
        } else if (type === AppConstants.CategorySD.WORKTYPE) {
          vm.additionalTitle = "ArG-Nr.";
        } else if (type === AppConstants.CategorySD.CALCULATION_FORMULA) {
          vm.additionalTitle = "Formel";
        } else if (type === AppConstants.CategorySD.VAT_RATE) {
          vm.additionalTitle = "Wert in %";
        } else if (type === AppConstants.CategorySD.DEPARTMENT) {
          vm.additionalTitle = "Kürzel";
          vm.isDepartment = true;
        } else if (type === AppConstants.CategorySD.DIRECTORATE) {
          vm.additionalTitle = "Kürzel";
          vm.isDirectorate = true;
        } else if (type === AppConstants.CategorySD.COUNTRY) {
          vm.additionalTitle = "ISO-Code";
          vm.isCountry = true;
        } else if (type === AppConstants.CategorySD.PROOFS) {
          vm.additionalTitle = "Text DE";
        } else if (type === AppConstants.CategorySD.SETTINGS) {
          vm.additionalTitle = "Wert";
          vm.isSettings = true;
        }
        return true;
      }
      return false;
    }

    /** Function to check if type is logib */
    function isTypeLogib() {
      return vm.type === AppConstants.CategorySD.LOGIB;
    }

    /** Function to check if type is proofs */
    function isTypeProofs() {
      return vm.type === AppConstants.CategorySD.PROOFS;
    }

    /** Function to check if type is email template */
    function isTypeEmailTemplate() {
      return vm.type === AppConstants.CategorySD.EMAIL_TEMPLATE;
    }

    /** Function to check if type is template type */
    function isTemplateType() {
      return vm.type === AppConstants.CategorySD.TEMPLATE_TYPE;
    }

    /** Function to check if type is process type */
    function isProcessType() {
      return vm.type === AppConstants.CategorySD.PROCESS_TYPE;
    }

    /** Function to determine if new type data entry is possible */
    function newEntryPossible() {
      return vm.type === AppConstants.CategorySD.CANCEL_REASON || vm.type === AppConstants.CategorySD.DEPARTMENT ||
        vm.type === AppConstants.CategorySD.WORKTYPE || vm.type === AppConstants.CategorySD.EXCLUSION_CRITERION ||
        vm.type === AppConstants.CategorySD.DIRECTORATE || vm.type === AppConstants.CategorySD.EMAIL_TEMPLATE ||
        vm.type === AppConstants.CategorySD.COUNTRY || vm.type === AppConstants.CategorySD.ILO ||
        vm.type === AppConstants.CategorySD.VAT_RATE || vm.type === AppConstants.CategorySD.PROOFS ||
        vm.type === AppConstants.CategorySD.OBJECT || vm.type === AppConstants.CategorySD.PROCESS_PM ||
        vm.type === AppConstants.CategorySD.SETTLEMENT_TYPE || vm.type === AppConstants.CategorySD.NEGOTIATION_REASON ||
        vm.type === AppConstants.CategorySD.CALCULATION_FORMULA;
    }

    /** Edit type data entry */
    function editTypeData(id) {
      $uibModal.open({
        templateUrl: 'app/stammdaten/typeDataEdit.html',
        controller: 'TypeDataEditController',
        controllerAs: 'typeDataEditCtrl',
        backdrop: 'static',
        keyboard: false,
        resolve: {
          entryId: function () {
            return id;
          },
          type: function () {
            return vm.type;
          }
        }
      });
    }

    /** Function to set the type name */
    function setTypeName() {
      if (!$stateParams.typeName) {
        // If the parameter typeName contains no value, get it from the type.
        AppService.getNameOfMasterListType($stateParams.type).success(function (data) {
          vm.typeName = data[0];
        });
      } else {
        vm.typeName = $stateParams.typeName;
      }
    }

  }
})();
