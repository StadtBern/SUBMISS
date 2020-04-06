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
    .module("submiss.company").controller("CompanyAddController",
      CompanyAddController);

  /** @ngInject */
  function CompanyAddController($scope, $location, $anchorScroll, $state,
    CompanyService, StammdatenService, NgTableParams, $filter,
    filterFilter, $uibModalInstance, onlyOneAllowed, notAllowedCompany,
    AppService, AppConstants) {
    /***********************************************************************
     * Local variables.
     **********************************************************************/
    var vm = this;
    var scrollyCounter = -1;
    var scrollyResultsCounter = 1;
    var scrollyPreviousResults = [];
    var tempSelectedCompanies = [];
    var previousCount = 10;
    var previousSortType = 'asc';
    var previousSortColumn = 'companyName';
    /***********************************************************************
     * Exported variables.
     **********************************************************************/
    vm.companies = [];
    vm.searchForm = {};
    vm.countries = [];
    vm.workTypes = [];
    vm.company = {};
    vm.companiesSelected = [];
    vm.companyList = [];
    vm.selectAll = null;
    /***********************************************************************
     * Exported functions.
     **********************************************************************/
    vm.loadCountries = loadCountries;
    vm.loadWorkTypes = loadWorkTypes;
    vm.search = search;
    vm.gotoSearchResults = gotoSearchResults;
    vm.reloadPage = reloadPage;
    vm.addSelected = addSelected;
    vm.select = select;
    vm.navigateToCompany = navigateToCompany;
    vm.getCompanyNames = getCompanyNames;
    vm.getCompanyTelephones = getCompanyTelephones;
    vm.getCompanyNotes = getCompanyNotes;
    vm.closeModal = closeModal;
    vm.checkAll = checkAll;
    vm.selectedCompanies = [];
    vm.scrollyRun = scrollyRun;
    vm.resetSearch = resetSearch;
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
    vm.searchForm.proofStatus = vm.proofStatuses[0].id;
    // Activating the controller.
    activate();
    /***********************************************************************
     * Controller activation.
     **********************************************************************/
    function activate() {
      vm.loadCountries();
      vm.loadWorkTypes();
      /* if the user has the right to view proof status Fabe, the add it to the drop down */
      var secMainTenantBeschaffungswesenView = AppService.isOperationPermitted(AppConstants.OPERATION.MAIN_TENANT_BESCHAFFUNGSWESEN_VIEW, null);
      if (secMainTenantBeschaffungswesenView) {
        vm.proofStatuses.push({
          id: "WITH_FABE",
          title: 'Rücksprache mit der Fachstelle Beschaffungswesen'
        });
      }
    }
    /***********************************************************************
     * $scope destroy.
     **********************************************************************/
    $scope.$on("$destroy", function () {});
    /***********************************************************************
     * Functions.
     **********************************************************************/
    function checkAll() {
      angular.forEach(vm.companies, function (company) {
        company.selected = vm.selectAll;
      });
    }
    // Function that returns all Countries
    function loadCountries() {
      StammdatenService.readAllCountries().success(function (data) {
        vm.countries = data;
      }).error(function (response, status) {});
    }

    // Function that returns all Arbeitsgattungen/Work Types
    function loadWorkTypes() {
      StammdatenService.readAllWorkTypes().success(function (data) {
        vm.workTypes = data;
      }).error(function (response, status) {});
    }

    /* this function is called every time the user reaches to the
     end of the search results through scrolling.*/
    function scrollyRun(tableParams) {
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
    // Function that searches for Firmen
    function search() {
      vm.tableParams = new NgTableParams({
        count: 10,
        sorting: {
          companyName: 'asc'
        }
      }, {

        getData: function (params) {
          vm.searchForm.filter = params.filter();
          vm.selectAll = false;
          vm.gotoSearchResults();
          if (vm.tableParams.page() !== null && vm.tableParams.page() !== 0 && vm.tableParams.count() !== null && vm.tableParams.count() !== 0) {
            var sortColumn = '';
            var sortType = '';

            for (var k in vm.tableParams.sorting()) {
              sortColumn = k;
              sortType = vm.tableParams.sorting()[k];
            }
            //****this part of the function is used for pagination option Alle ***
            if (params.count() < 0) {
              //Reset holding previous results if sorting or filtering is requested by the user
              if (sortingRequested(sortColumn, sortType)) {
                tempSelectedCompanies = [];
                scrollyPreviousResults = [];
                scrollyResultsCounter = 1;
                scrollyCounter = -1;
                $anchorScroll('formStart');
              }
              AppService.setPaneShown(true);
              return CompanyService
                .search(vm.searchForm, scrollyResultsCounter, 100, sortColumn, sortType)
                .then(function (companyListResult) {
                  AppService.setPaneShown(false);
                  //add the new results to the previous for better performance
                  angular.forEach(companyListResult.data.results, function (company) {
                    scrollyPreviousResults.push(company);
                  });
                  vm.companies = scrollyPreviousResults;
                  params.total(companyListResult.data.count);
                  return scrollyPreviousResults;
                });
            }
            //***this part of the function is used for pagination options (10,50) ***
            keepSelectedCompanies(vm.companies);
            return CompanyService
              .search(vm.searchForm, vm.tableParams.page(), params.count(), sortColumn, sortType)
              .then(function (companyListResult) {
                AppService.setPaneShown(false);
                vm.companies = companyListResult.data.results;
                params.total(companyListResult.data.count);
                //if pagination choice remains the same during navigation to the search results ,
                //previously selected companies must be marked as selected when returning to the page
                if (previousCount === params.count()) {
                  setSelectedFlag(vm.companies);
                } else {
                  //if pagination choice is changed by the user during navigation to the search results,
                  //the list previously selected companies must be cleared
                  tempSelectedCompanies = [];
                  previousCount = params.count();
                }
                //reset All scrolly memory values in case that Alle is selected as next pagination choice.
                scrollyPreviousResults = [];
                scrollyResultsCounter = 1;
                scrollyCounter = -1;
                return companyListResult.data.results;

              });
          }
          return null;
        }
      });
      vm.gotoSearchResults();
    }

    function select(company) {
      vm.tempPmExternal = company;
      vm.selectedCompanies.push(company);
    }

    function addSelected() {
      vm.companyArray = [];
      vm.countSelected = 0;
      vm.notAllowedCompanySelected = false;
      vm.notSelected = false;
      angular.forEach(vm.companies, function (company) {
        if (company.selected) {
          vm.companyArray.push(company);
          vm.countSelected++;
          // check if the user is selecting the same company as
          // branch, which is not allowed
          if (notAllowedCompany !== null) {
            if (company.id === notAllowedCompany) {
              vm.notAllowedCompanySelected = true;
            }
          }
        }
      });
      //put the allready selected companies from the other pages
      angular.forEach(tempSelectedCompanies, function (tempSelectedCompany) {
        if (!checkIfAlreadyPut(tempSelectedCompany)) {
          vm.companyArray.push(tempSelectedCompany);
          vm.countSelected++;
        }
      });
      if (vm.notAllowedCompanySelected) {
        vm.notAllowedCompanyError = true;
        $anchorScroll('ErrorAnchor');
      }
      // Check if any company is selected
      if (vm.countSelected < 1) {
        vm.notSelected = true;
      }
      if (vm.notSelected) {
        vm.notSelectedCompanyError = true;
        $anchorScroll('errorScroll');
      }
      // for some cases (where onlyOneAllowed=true) the user is not
      // allowed to select more than one companies
      // so an error message will be displayed
      else if (onlyOneAllowed && vm.countSelected > 1) {
        vm.moreThanOneError = true;
        $anchorScroll('ErrorAnchor');
      } else {
        $uibModalInstance.close(vm.companyArray);
      }
    }

    function reloadPage() {
      $uibModalInstance.close();
      AppService.addCompany(onlyOneAllowed, notAllowedCompany);
    }

    function closeModal() {
      $uibModalInstance.close();
    }

    function gotoSearchResults() {
      // set the location.hash to the id of
      // the element you wish to scroll to.
      if (vm.tableParams.count() > 0 || scrollyResultsCounter === 1) {
        // call $anchorScroll()
        $anchorScroll('formStart');
      } else {
        $anchorScroll('company' + (scrollyPreviousResults.length / 2));
      }
    }

    function navigateToCompany(companyId) {
      var url = $state.href('company.view', {
        id: companyId
      });
      window.open(url, '_blank');
    }

    function getCompanyNames(query) {
      return CompanyService.getCompanyNames(query)
        .then(function (response) {
          return response.data;
        });
    }

    function getCompanyTelephones(query) {
      return CompanyService.getCompanyTelephones(query)
        .then(function (response) {
          return response.data;
        });
    }

    function getCompanyNotes(query) {
      return CompanyService.getCompanyNotes(query)
        .then(function (response) {
          return response.data;
        });
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
          // if a company does not remain selected when navigating to a
          // page but exists in the temporary list because of previous
          // navigation to the specific page,
          // it must be removed from the temporary list
          if (!companyAllreadyPut) {
            tempSelectedCompanies.push(company);
          }
        }

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
      // if user returns to a page that some companies are
      // previously selected through pagination,they must appear
      // as selected.
      angular.forEach(tempSelectedCompanies, function (tempCompany) {
        angular.forEach(companies, function (company) {
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

    function resetSearch() {
      vm.tableParams = null;
    }
    /**This function is used to check if user has put any Sorting criteria */
    function sortingRequested(sortColumn, sortType) {
      // previousSortColumn and previousSortType vars, hold the Sorting
      // criteria of the previous Search
      // sortColumn and sortType vars,hold the Sorting criteria of the
      // running Search
      if (sortColumn !== previousSortColumn || sortType !== previousSortType) {
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
  }
})();
