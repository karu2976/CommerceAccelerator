define([
  'jquery',
], function ($) {
  'use strict';

  //Mock profile services
  var object = {};
  return Object.create(object, {
    promise: {
      value: $.when()
    },

    viewAddresses : {
      value: function () {
        return this.promise.then(function () {
          var response = {
            addresses : {
              secondaryAddresses: []
            }
          };
          return response;
        });
      }
    },

    viewCards : {
      value: function () {
        return this.promise.then(function () {
          var response = {
            paymentInfo : {
              creditCards : []
            }
          };
          return response;
        });
      }
    }
  });
});
