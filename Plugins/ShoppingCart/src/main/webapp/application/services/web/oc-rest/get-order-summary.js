/**
 * @module csa/plugins/shopping-cart/application/services/web/order-summary
 * @alias module:order-summary
 * @requires {@link module:order order}
 */
define([
  'order'
], function (order) {
  'use strict';

  /**
   * The orderSummary service return a summary of the order.
   *
   * @function
   * @return A {Promise} object.
   */
  return function orderSummary () {

    // Define service options.
    var options = {
      serviceId: 'orderSummary',
      url: '/rest/model/atg/commerce/ShoppingCartActor/summary',
      type: 'GET'
    };

    // Execute service call.
    return order.datasource.execute(options);
  };
});
