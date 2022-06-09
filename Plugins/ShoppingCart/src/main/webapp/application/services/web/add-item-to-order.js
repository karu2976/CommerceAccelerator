/**
 * @module csa/plugins/shopping-cart/application/services/web/add-item-to-order
 * @alias module:add-item-to-order
 * @requires {@link module:order order}
 */
define([
  'order'
], function (order) {
  'use strict';

  /**
   * The addItemToOrder service adds a commerce item from the order.
   *
   * @function
   * @param data The addItemToOrder data payload, which specifies the item to be added to the order
   *     and must be an object with properties productId, catalogRefIds and quantity, e.g.:
   *
   *      {
   *        productId: 'prod101',
   *        catalogRefIds: 'sku102',
   *        quantity: 1,
   *      }
   * @return A {Promise} object.
   */
  return function addItemToOrder (data) {

    // Define service options.
    var options = {
      serviceId: 'addItemToOrder',
      url: '/rest/model/atg/commerce/order/purchase/CartModifierActor/addItemToOrder',
      type: 'POST',
      data: data
    };

    // Execute service call.
    return order.datasource.execute(options);
  };
});
