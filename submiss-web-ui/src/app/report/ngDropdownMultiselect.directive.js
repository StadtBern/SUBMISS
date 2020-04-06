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
 * @ngdoc object
 * @description Custom directive. Formats input value according to the given format
 */

(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.report")
    .directive('multiselectDropdown', function () {
      return {
        restrict: 'E',
        scope: {
          model: '=',
          options: '='
        },
        templateUrl: 'app/layout/multiselect.html',
        controller: function ($scope) {
          $(document).bind('click', function (event) {
            if (event.target.id === 'multiSId' || event.target.id === 'spanId' ||
              event.target.id === 'multiSCaretId' || event.target.id ===
              'spanCheckboxId') {
              $(".multiS").addClass("open");
            } else {
              $(".multiS").removeClass("open");
            }
          });

          $scope.selectAll = function () {
            $scope.model = [];
            angular.forEach($scope.options, function (item, index) {
              $scope.model.push(item);
            });

          };

          $scope.deselectAll = function () {
            $scope.model = [];
          };

          $scope.toggleSelectItem = function (option) {
            var intIndex = -1;
            angular.forEach($scope.model, function (item, index) {
              if (item.id === option.id) {
                intIndex = index;
              }
            });

            if (intIndex >= 0) {
              $scope.model.splice(intIndex, 1);
            } else {
              $scope.model.push(option);
            }
            if (option.id === 10) {
              /**
               *  $scope.model.includes(option) replaced by
               *  $scope.model.indexOf(option) > -1
               *  because IE does not support .includes()
               */
              if ($scope.model.indexOf(option) > -1) {
                $scope.selectAll();
              } else {
                $scope.deselectAll();
              }
            }
            $scope.isChecked(option);
          };
          $scope.isChecked = function (option) {
            var isChecked = false;
            angular.forEach($scope.model, function (item, index) {
              if (item.id === option.id) {
                isChecked = true;
              }
            });
            return (isChecked);
          };
        }
      }
    });
})();
