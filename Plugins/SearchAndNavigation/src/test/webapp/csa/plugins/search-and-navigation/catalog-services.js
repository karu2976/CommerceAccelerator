// jshint jasmine: true
define([
  'jquery',
], function ($) {
  'use strict';

  //Mock catlog services
  var object = {};

  return Object.create(object, {

    promise: {
      value: $.when()
    },

    getSelectedSku: {
      value: function () {
        return $.when({'selectedSku': {'id': 'sku123'}});
      }
    }
  });
});
