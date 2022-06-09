/**
 * @module csa/plugins/shopping-cart/application/services/web/set-order
 * @alias module:set-order
 * @requires {@link module:order order}
 */
define([
  'order'
], function (order) {
  'use strict';

  /**
   * The setOrder service updates the quatity for each commerce items in the order.
   *
   * @function
   * @return A {Promise} object.
   */
  return function setOrder () {

    // Build parameter data.
    var data = {};
    order.commerceItems().forEach(function (commerceItem) {
      data[commerceItem.catalogRefId()] = commerceItem.quantity();
    });

    // Define service options.
    var options = {
      serviceId: 'setOrder',
      url: '/rest/model/atg/commerce/order/purchase/CartModifierActor/setOrder',
      type: 'POST',
      data: data
    };

    // Execute service call.
    return order.datasource.execute(options);
  };
});
