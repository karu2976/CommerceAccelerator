/**
 * @module csa/plugins/promotions/application/sevices/web/oc-rest/remove-coupon
 * @alias module:oc-rest-remove-coupon
 * @requires module:order
 */
define([
  'order'
], function (order) {
  'use strict';

  /**
   * The removeCoupon service removes the associated promotion from the order.
   *
   * @function
   * @param coupon Promotion coupon code to be removed.
   */
  return function removeCoupon () {

    // Define service options.
    var options = {
      serviceId: 'ocRestRemoveCoupon',
      url: '/rest/model/atg/commerce/promotion/CouponActor/removeCoupon',
      type: 'POST'
    };

    // Execute service call.
    return order.datasource.execute(options);
  };
});
