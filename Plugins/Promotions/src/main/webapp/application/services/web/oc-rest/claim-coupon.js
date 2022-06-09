/**
 * @module csa/plugins/promotions/application/sevices/web/oc-rest/claim-coupon
 * alias module:claim-coupon
 * @requires module:order
 */
define([
  'order'
], function (order) {
  'use strict';

  /**
   * The claimCoupon service applies the associated promotion to the order.
   *
   * @function
   * @param couponCode Promotion coupon code to be claimed.
   */
  return function claimCoupon (couponCode) {

    // Define service arguments.
    var data = {
      couponClaimCode : couponCode
    };

    // Define service options.
    var options = {
      serviceId: 'ocRestClaimCoupon',
      url: '/rest/model/atg/commerce/promotion/CouponActor/claimCoupon',
      type: 'POST',
      data: data
    };

    // Execute serivce call.
    return order.datasource.execute(options);
  };
});
