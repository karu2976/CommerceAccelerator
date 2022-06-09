/**
 * The OrderSummary component provides the UI to notify the user if the cart is empty. Different messages
 * are displayed for authenticated and anonymous users.
 *
 * @module csa/plugins/shopping-cart/application/components/order-summary
 * @requires module:libs
 * @requires module:content-item
 * @requires module:text!./order-summary.html
 * @requires module:order
 */
define([
  'libs',
  'content-item',
  'text!./order-summary.html',
  'order'
], function (libs, ContentItem, template, order) {
  'use strict';

  var _ = libs._;

  /**
   * @class
   * @extends ContentItem
   */
  function OrderSummary() {
    ContentItem.apply(this, arguments);
  }

  OrderSummary.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends OrderSummary
     */
    constructor: {
      value: OrderSummary
    },

    /**
     * @property
     * @lends OrderSummary
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'hasAdjustments'
      ])
    },

    /**
     * @property
     * @lends OrderSummary
     */
    order: {
      value: order,
      enumerable: true
    },

    /**
     * @function
     * @lends OrderSummary
     */
    hasAdjustments: {
      value: function () {
        var hasAdjustments = this.order.priceInfo.adjustments().length > 0 || !!_.find(this.order.shippingGroups(), function (shippingGroup) {
          return shippingGroup.priceInfo.adjustments().length > 0;
        });

        return hasAdjustments;
      },
      writable: true
    }
  });

  // The component defintion.
  return {

    // The component HTML template.
    template: template,

    // The component view model factory.
    viewModel: {
      createViewModel: function createViewModel (params) {
        var viewModel = new OrderSummary().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    OrderSummary: OrderSummary
  };
});
