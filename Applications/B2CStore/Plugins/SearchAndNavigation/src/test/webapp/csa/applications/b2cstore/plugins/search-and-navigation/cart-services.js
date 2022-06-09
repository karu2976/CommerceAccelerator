define([
  'jquery',
], function ($) {
  'use strict';

  //Mock catalog services
  var object = {};

  return Object.create(object, {

    promise: {
      value: $.when()
    },

    addItemToOrder: {
      value: function addItemToOrder (data) {
        // Define rest options
        var options = {
          serviceId: 'addItemToOrder',
          url: '/rest/model/atg/commerce/order/purchase/CartModifierActor/addItemToOrder',
          type: 'POST',
          data: data
        };

        // Call rest service
        return options;
      }
    }
  });
});
