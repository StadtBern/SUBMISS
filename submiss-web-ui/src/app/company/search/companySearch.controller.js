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
 * @name companySearch.controller.js
 * @description CompanySearchController controller
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.company").controller("CompanySearchController",
      CompanySearchController);

  /** @ngInject */
  function CompanySearchController($scope, $locale, $location, $anchorScroll,
    $state,
    CompanyService, StammdatenService, NgTableParams, $filter,
    AppService, DocumentService, AppConstants, $uibModal) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var results = 0;
    var scrollyCounter = -1;
    var scrollyResultsCounter = 1;
    var scrollyPreviousResults = [];
    var tempSelectedCompanies = [];
    var previousCount = 10;
    var lastFilteredCompanyName = null;
    var lastFilteredCompanyTel = null;
    var lastFilteredPostCode = null;
    var lastFilteredProofStatus = null;
    var lastFilteredWorkTypes = null;
    var previousSortType = 'asc';
    var previousSortColumn = 'companyName';
    var tempCount = 10;
    var tempPage = 1;
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.companies = {};
    vm.searchForm = {};
    vm.countries = [];
    vm.workTypes = [];
    vm.templates = [];
    vm.ilo = {};
    vm.logibStatuses = [{
        id: "ALL_TRUE_LOGIB",
        title: 'Alle (logibpflichtigen Firmen)'
      },
      {
        id: "LOGIB",
        title: 'Logib'
      },
      {
        id: "LOGIB_ARGIB",
        title: 'Logib ARGIB'
      }

    ];
    vm.proofStatuses = [{
        id: "NULL",
        title: 'Nachweis-Status bei der Suche nicht berücksichtigen'
      },
      {
        id: "ALL_PROOF",
        title: 'Alle Nachweise vorhanden'
      },
      {
        id: "NOT_ACTIVE",
        title: 'Nachweise nicht mehr aktuell, nicht vollständig, oder nicht vorhanden'
      },
      {
        id: "WITH_KAIO",
        title: 'Rücksprache mit KAIO ZKB / FaBe'
      }
    ];
    // Secondary filter for the table. This filter does not include the "NULL" option
    vm.proofStatusesFilter = [{
        id: "ALL_PROOF",
        title: 'Alle Nachweise vorhanden'
      },
      {
        id: "NOT_ACTIVE",
        title: 'Nachweise nicht mehr aktuell, nicht vollständig, oder nicht vorhanden'
      },
      {
        id: "WITH_KAIO",
        title: 'Rücksprache mit KAIO ZKB / FaBe'
      },
      {
        id: "WITH_FABE",
        title: 'Rücksprache mit der Fachstelle Beschaffungswesen'
      }
    ];
    vm.searchForm.proofStatus = vm.proofStatuses[0].id;
    vm.searchForm.archived = false;
    vm.divTableHeight = {};
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.loadCountries = loadCountries;
    vm.loadWorkTypes = loadWorkTypes;
    vm.loadILO = loadILO;
    vm.getUserAllowedTemplates = getUserAllowedTemplates;
    vm.search = search;
    vm.generateDocument = generateDocument;
    vm.gotoSearchResults = gotoSearchResults;
    vm.navigateToCompany = navigateToCompany;
    vm.reloadPage = reloadPage;
    vm.getCompanyNames = getCompanyNames;
    vm.getCompanyPostcodes = getCompanyPostcodes;
    vm.getCompanyLocations = getCompanyLocations;
    vm.getCompanyTelephones = getCompanyTelephones;
    vm.getCompanyNotes = getCompanyNotes;
    vm.checkLogib = checkLogib;
    vm.openCertifiedOnDateFilter = openCertifiedOnDateFilter;
    vm.addSelected = addSelected;
    vm.applyClass = applyClass;
    vm.checkForEmptyResults = checkForEmptyResults;
    vm.downloadDocument = downloadDocument;
    vm.loggedInUser = AppService.getLoggedUser();
    vm.scrollyRun = scrollyRun;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      CompanyService.loadCompanySearch()
        .success(function (data, status) {
          if (status === 403) { // Security checks.
            return;
          } else {
            vm.loadCountries();
            vm.loadWorkTypes();
            vm.loadILO();
            vm.getUserAllowedTemplates();
            vm.secSentEmail = AppService.isOperationPermitted(
              AppConstants.OPERATION.SENT_EMAIL, null);
            /* if the user has the right to view proof status Fabe, the add it to the drop down */
            var secMainTenantBeschaffungswesenView = AppService.isOperationPermitted(
              AppConstants.OPERATION.MAIN_TENANT_BESCHAFFUNGSWESEN_VIEW, null);
            if (secMainTenantBeschaffungswesenView) {
              vm.proofStatuses.push({
                id: "WITH_FABE",
                title: 'Rücksprache mit der Fachstelle Beschaffungswesen'
              });
            }
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
    // Function that returns all Countries
    function loadCountries() {
      StammdatenService.readAllCountries()
        .success(function (data) {
          vm.countries = data;
        }).error(function (response, status) {});
    }

    // Function that returns all Arbeitsgattungen/Work Types
    function loadWorkTypes() {
      StammdatenService.readAllWorkTypes()
        .success(function (data) {
          vm.workTypes = data;
        }).error(function (response, status) {});
    }

    // Function that returns all ILO
    function loadILO() {
      StammdatenService.readAllILO()
        .success(function (data) {
          vm.ilo = data;
        }).error(function (response, status) {});
    }

    // Function that returns all ILO
    function getUserAllowedTemplates() {
      CompanyService.getUserAllowedTemplates()
        .success(function (data) {
          vm.templates = data;
        }).error(function (response, status) {});
    }

    function checkLogib(logib, logibArgib) {
      if (logib === true && logibArgib === false) {
        return 'Logib';
      }
      if (logib === false && logibArgib === true) {
        return 'Logib ARGIB';
      }
      if (logib === false && logibArgib === false) {
        return '';
      }
      return null;
    }

    // Function that searches for Firmen
    function search() {
      vm.companies = [];
      tempSelectedCompanies = [];
      vm.tableParams = new NgTableParams({
        count: 10,
        sorting: {
          companyName: 'asc'
        }
      }, {
        getData: function (params) {
          vm.searchForm.filter = params.filter();
          if (vm.tableParams.page() != null && vm.tableParams.page() !== 0 &&
            vm.tableParams.count() !== null && vm.tableParams.count() !== 0) {
            /**
             * when another pagination option is chosen
             * use $anchorScroll('StartingPoint') to move to the first element of the table
             */
            if ((vm.tableParams.count() > 0 && vm.tableParams.count() !== tempCount) || vm.tableParams.count() === -1) {
              tempCount = vm.tableParams.count();
              $anchorScroll('StartingPoint');
            }
            /**
             * when changing pages
             * use $anchorScroll('StartingPoint') to move to the first element of the table
             */
            if (vm.tableParams.page() !== tempPage) {
              tempPage = vm.tableParams.page();
              $anchorScroll('StartingPoint');
            }

            var sortColumn = '';
            var sortType = '';

            for (var k in vm.tableParams.sorting()) {
              sortColumn = k;
              sortType = vm.tableParams.sorting()[k];
            }

            gotoSearchResults();
            AppService.setPaneShown(true);


            if (params.count() < 0) { // **** this part of the function is used for pagination option Alle ****

              // Reset holding previous results if sorting or filtering is requested by the user
              if (sortingRequested(sortColumn, sortType) || filterRequested(
                  vm.searchForm.filter)) {
                tempSelectedCompanies = [];
                scrollyPreviousResults = [];
                scrollyResultsCounter = 1;
                scrollyCounter = -1;
                $anchorScroll('formStart');
              }

              return CompanyService
                .search(vm.searchForm, scrollyResultsCounter, 100, sortColumn, sortType)
                .then(function (companyListResult) {
                  AppService.setPaneShown(false);
                  angular.forEach(companyListResult.data.results,
                    function (company) {
                      scrollyPreviousResults.push(company);
                    });
                  vm.companies = scrollyPreviousResults;
                  params.total(companyListResult.data.count);
                  return scrollyPreviousResults;
                });

            } else { // **** this part of the function is used for pagination options (10,50) ****

              keepSelectedCompanies(vm.companies);

              return CompanyService
                .search(vm.searchForm, vm.tableParams.page(), vm.tableParams.count(), sortColumn, sortType)
                .then(function (companyListResult) {
                  AppService.setPaneShown(false);
                  params.total(companyListResult.data.count);
                  vm.companies = companyListResult.data.results;
                  results = companyListResult.data.count;
                  if (previousCount === params.count()) {
                    setSelectedFlag(vm.companies);
                  } else {
                    tempSelectedCompanies = [];
                    previousCount = params.count();
                  }
                  // Reset all scrolly memory values in case that Alle is selected as next pagination choice.
                  scrollyPreviousResults = [];
                  scrollyResultsCounter = 1;
                  scrollyCounter = -1;
                  return companyListResult.data.results;
                });
            }
          }
        }
      });
      vm.gotoSearchResults();
    }

    function generateDocument(template) {
      if (template.shortCode === AppConstants.FIRMENLISTE_KOMPLETT ||
        template.shortCode === AppConstants.LISTE_ARBEITSGATTUNG ||
        template.shortCode === AppConstants.LISTE_WINBAU) {
        AppService.setPaneShown(true);
        DocumentService.generateCompaniesListDocument(
          template.shortCode).success(
          function (response, status, headers) {
            downloadDocument(response, status, headers);
          }).error(function (response, status) {
          AppService.setPaneShown(false);
        });
      } else if (template.shortCode ===
        AppConstants.FIRMENLISTE_NACH_SUCHRESULTAT) {
        if (vm.tableParams) {

          var sortColumn = '';
          var sortType = '';

          for (var k in vm.tableParams.sorting()) {
            sortColumn = k;
            sortType = vm.tableParams.sorting()[k];
          }

          AppService.setPaneShown(true);
          // set pageItems to -1  and page to 1 in order to bring all searched companies
          DocumentService.generateSearchedCompaniesDocument(template.shortCode,
            vm.searchForm, 1, -1,
            sortColumn, sortType).success(
            function (response, status, headers) {
              downloadDocument(response, status, headers);
            }).error(function (response, status) {
            AppService.setPaneShown(false);
          });
        }
      }
    }

    function reloadPage() {
      $state.go('company.search', {}, {
        reload: true
      });
    }

    function gotoSearchResults() {
      if (vm.tableParams.count() > 0 || scrollyResultsCounter === 1) {
        $anchorScroll('formStart');
      } else {
        $anchorScroll('company' + (scrollyPreviousResults.length));
      }
    }

    function navigateToCompany(companyId) {
      $state.go('company.view', {
        id: companyId
      });
    }

    function getCompanyNames(query) {
      return CompanyService.getCompanyNames(query, vm.searchForm.archived).then(function (response) {
        return response.data;
      });
    }

    function getCompanyPostcodes(query) {
      return CompanyService.getCompanyPostcodes(query, vm.searchForm.archived).then(
        function (response) {
          return response.data;
        });
    }

    function getCompanyLocations(query) {
      return CompanyService.getCompanyLocations(query, vm.searchForm.archived).then(
        function (response) {
          return response.data;
        });
    }

    function getCompanyTelephones(query) {
      return CompanyService.getCompanyTelephones(query, vm.searchForm.archived).then(
        function (response) {
          return response.data;
        });
    }

    function getCompanyNotes(query) {
      return CompanyService.getCompanyNotes(query, vm.searchForm.archived).then(function (response) {
        return response.data;
      });
    }

    function openCertifiedOnDateFilter() {
      vm.openCertifiedOnDateFilter.opened = !vm.openCertifiedOnDateFilter.opened;
      vm.divTableHeight = AppService.adjustTableHeight(results,
        vm.openCertifiedOnDateFilter.opened);
    }

    function addSelected() {
      vm.companyArray = [];
      vm.countSelected = 0;
      vm.notAllowedCompanySelected = false;
      vm.notSelected = false;
      angular.forEach(vm.companies, function (company) {
        if (company.selected) {
          vm.companyArray.push(company.id);
          vm.countSelected++;
        }
      });

      //already selected companies from the other pages must be inserted too
      angular.forEach(tempSelectedCompanies, function (tempSelectedCompany) {
        if (!checkIfAlreadyPut(tempSelectedCompany)) {
          vm.companyArray.push(tempSelectedCompany.id);
          vm.countSelected++;
        }
      });
      AppService.sendMailModal(null, vm.companyArray);
    }

    // Function that return the icon of the listed document
    function applyClass(template) {
      if (template.shortCode === AppConstants.FIRMENLISTE_KOMPLETT ||
        template.shortCode === AppConstants.FIRMENLISTE_NACH_SUCHRESULTAT) {
        return "fa fa-file-pdf-o";
      } else {
        return "fa fa-file-excel-o";
      }
    }

    // Function that check if no results returned
    function checkForEmptyResults(response) {
      if (response.byteLength === 0) {
        vm.noResultsReturned = true;
        $anchorScroll('errorScroll');
      }
    }

    // Function that downloads the document
    function downloadDocument(response, status, headers) {
      checkForEmptyResults(response);
      AppService.setPaneShown(false);
      if (response.byteLength > 0) {
        var fileName = headers()["content-disposition"]
          .split(';')[1].trim().split('=')[1];
        // Remove quotes added from the DocumentResource in order to enable filenames with special characters like comma
        fileName = fileName.substring(1, fileName.length - 1);
        var blob = new Blob([response], {
          type: "application/octet-stream"
        });
        // For Internet Explorer 11 only
        if (window.navigator &&
          window.navigator.msSaveOrOpenBlob) {
          window.navigator.msSaveOrOpenBlob(blob, fileName);
        } else {
          var objectUrl = URL.createObjectURL(blob);
          var a = $("<a style='display: none;'/>");
          a.attr("href", objectUrl);
          a.attr("download", fileName);
          $("body").append(a);
          a[0].click();
          window.URL.revokeObjectURL(objectUrl);
          a.remove();
        }
      }
    }

    function keepSelectedCompanies(companies) {
      angular.forEach(companies, function (company) {

        // When page is changed through pagination in options (10,50)
        // selected Companies must be kept in a temporary list

        // if a selected company already exists in the temporary list
        // and remains selected,it must not be added again when
        // navigating to the same page
        if (company.selected) {
          var companyAllreadyPut = false;
          angular.forEach(tempSelectedCompanies, function (
            tempSelectedCompany) {
            if (company.id === tempSelectedCompany.id) {
              companyAllreadyPut = true;
            }
          });
          if (!companyAllreadyPut) {
            tempSelectedCompanies.push(company);
          }
        }
        // if a company does not remain selected when navigating to a
        // page but exists in the temporary list because of previous
        // navigation to the specific page,
        // it must be removed from the temporary list
        if (!company.selected) {
          angular.forEach(tempSelectedCompanies, function (
            tempSelectedCompany) {
            if (company.id === tempSelectedCompany.id) {
              tempSelectedCompanies.splice(tempSelectedCompanies
                .indexOf(tempSelectedCompany), 1);
            }
          });
        }
      });
    }

    function setSelectedFlag(companies) {
      angular.forEach(tempSelectedCompanies, function (tempCompany) {
        angular.forEach(companies, function (company) {
          // if user returns to a page that some companies are
          // previously selected through pagination,they must appear
          // as selected.
          if (tempCompany.id === company.id) {
            company.selected = true;
          }
        });
      });
    }

    function checkIfAlreadyPut(tempSelectedCompany) {
      var exists = false;
      angular.forEach(vm.companies, function (company) {
        if (company.id === tempSelectedCompany.id) {
          exists = true;
        }
      });
      return exists;
    }

    /* this function is called every time the user reaches to the
    end of the search results through scrolling.*/
    function scrollyRun(tableParams) {
      // we want this function to be triggered only if pagination option
      // is Alle.
      if (tableParams.count() < 0) {
        // scrollyResultsCounter is the var that holds the page of the
        // new search results that will be added to the previous
        scrollyResultsCounter = scrollyResultsCounter + 1;
        // scrollyCounter is the var that holds the value of the
        // parameter,that the count function of tableparams will be called.
        // In option Alle,
        // we want this param to be negative so that we can separate the
        // functionality from the two other pagination options(10,50).
        //For this reason, it is initialized to -1 and and it is decreased by -1
        //for every call of this function with Alle pagination choice.
        scrollyCounter = scrollyCounter - 1;
        //getData of tableParms is triggered below
        //with tableparams.count containing the negative value that scrollyCounter holds.
        tableParams.count(scrollyCounter);
      }
    }

    /*This function is used to check if user has put any Sorting criteria */
    function sortingRequested(sortColumn, sortType) {
      // previousSortColumn and previousSortType vars, hold the Sorting
      // criteria of the previous Search
      // sortColumn and sortType vars,hold the Sorting criteria of the
      // running Search
      if (sortColumn !== previousSortColumn ||
        sortType !== previousSortType) {
        // if user changes something in the Sorting criteria of the
        // running search, previousSortColumn and previousSortType vars
        // are updated in order to be compared
        // to the Sorting values of the next search.
        // This way we can track if user has requested new Sorting
        // criteria
        previousSortColumn = sortColumn;
        previousSortType = sortType;
        return true;

      } else {
        return false;
      }
    }

    /*This function is used to check if user has put any new Filter criteria */
    function filterRequested(filter) {
      // lastFilteredxxxx vars hold the filter values of the previous
      // Search and filter object contains the filter values
      // of the running search
      if (filter.companyName !== lastFilteredCompanyName ||
        filter.companyTel !== lastFilteredCompanyTel ||
        filter.postCode !== lastFilteredPostCode ||
        filter.proofStatus !== lastFilteredProofStatus ||
        filter.workTypes !== lastFilteredWorkTypes) {
        // if user changes something in the filter values of the running
        // search, the lastFilteredxxxx vars are updated in order to be
        // compared
        // to the filter values of the next search.
        // This way we can track if user has requested new filter
        // criteria
        lastFilteredCompanyName = filter.companyName;
        lastFilteredCompanyTel = filter.companyTel;
        lastFilteredPostCode = filter.postCode;
        lastFilteredProofStatus = filter.proofStatus;
        lastFilteredWorkTypes = filter.workTypes;
        return true;
      }
      return false;
    }
  }
})();
