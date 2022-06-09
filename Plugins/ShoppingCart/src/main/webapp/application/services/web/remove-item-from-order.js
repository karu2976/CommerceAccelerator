/**
 * @module csa/plugins/shopping-cart/application/services/web/remove-item-from-order
 * @alias module:remove-item-from-order
 * @requires {@link module:order order}
 */
define([
  'order'
], function (order) {
  'use strict';

  /**
   * The removeItemFromOrder service removes a commerce item from the order.
   *
   * @function
   * @param removalCommerceIds The commerce id of the item to be removed.
   * @return A {Promise} object.
   */
  return function removeItemFromOrder (removalCommerceIds) {

    // Build parameter data.
    var data = {
      removalCommerceIds: removalCommerceIds
    };

    // Define service options.
    var options = {
      serviceId: 'removeItemFromOrder',
      url: '/rest/model/atg/commerce/order/purchase/CartModifierActor/removeItemFromOrder',
      type: 'POST',
      data: data
    };

    // Execute service call.
    return order.datasource.execute(options);
  };
});
