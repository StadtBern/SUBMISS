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
 * @ngdoc overview
 * @name company.module.js
 * @description submiss.company module
 */
(function () {
  "use strict";

  angular // eslint-disable-line no-undef
    .module("submiss.company", ["QFormJSRValidation"])
    //Datepicker
    .directive("mydatepicker", function () {
      return {
        restrict: "E",
        scope: {
          ngModel: "=",
          dateOptions: "=",
          opened: "=",
        },
        link: function ($scope, element, attrs) {
          const vm = this;
          vm.open = function (event) {
            event.preventDefault();
            event.stopPropagation();
            vm.opened = true;
          };
          vm.clear = function () {
            vm.ngModel = null;
          };
        },
        template: '<p class="input-group">' +
          '<input type="text" class="form-control" datepicker-popup="yyyy-MM-dd"' +
          'ng-model="ngModel" is-open="opened" datepicker-options="dateOptions" date-disabled="disabled(date, mode)" close-text="Close" />' +
          '<span class="input-group-btn">' +
          '<button class="btn btn-default" ng-click="open($event)"><i class="glyphicon glyphicon-calendar"></i></button>' +
          '</span>' +
          '</p>'
      }
    })
    .controller('myCtrl', function ($scope, $http) {
      const vm = this;
      vm.formData = {};
      vm.formData.date = "";
      vm.opened = false;

      //Datepicker
      vm.dateOptions = {
        'year-format': "'yy'",
        'show-weeks': false
      };
    });
})();
