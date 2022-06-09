/**
 * The CheckoutCartSummary component provides the UI for summary of cart page.
 * On this page user can see items added into the cart. For example image of product,
 * product display name, color, size and quantity of product.
 *
 * @module csa/plugins/checkout/application/components/checkout-cart-summary
 * @requires module:content-item-mixin
 * @requires module:order
 * @requires module:text!./checkout-cart-summary.html
 */
define([
  'content-item',
  'order',
  'text!./checkout-cart-summary.html',
], function (ContentItem, order, template) {
  'use strict';

  function CheckoutCartSummary() {
    ContentItem.apply(this, arguments);
  }

  // Define class prototype
  CheckoutCartSummary.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends CheckoutCartSummary
     */
    constructor: {
      value: CheckoutCartSummary
    },

    /**
     * Current Order in the shopping cart.
     */
    currentOrder: {
      value: order
    }
  });

  // The component defintion
  return {

    // The component HTML template.
    template: template,

    // The component view model factory.
    viewModel: {
      createViewModel: function createViewModel (params) {
        var viewModel = new CheckoutCartSummary().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    CheckoutCartSummary: CheckoutCartSummary
  };
});
