// jshint jasmine: true
define([
  'knockout',
  'knockout-mapping'
], function (ko, komap) {
  'use strict';

  //Mock catalog domain model
  var vm = {};

  return Object.create(vm, {

    modelName: {
      value: 'catalog'
    },

    product: {
      value: ko.observable()
    },

    selectedSku: {
      value: ko.observable()
    },

    fromJS: {
      value: function (data) {
        return komap.fromJS(this.parse(data), {}, this);
      }
    },

    parse: {
      value: function (data) {
        return data;
      }
    }
  });
});
