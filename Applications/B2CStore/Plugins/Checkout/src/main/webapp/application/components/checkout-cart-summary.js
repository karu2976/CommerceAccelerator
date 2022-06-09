/**
* This extended component overrides the template property so that it can reference some
* more application specific properties.
*
* @module csa/applications/b2cstore/plugins/checkout/application/components/checkout-cart-summary
* @requires module:csa/plugins/checkout/application/components/checkout-cart-summary
* @requires module:text!./checkout-cart-summary.html
*/
define([
  'csa/plugins/checkout/application/components/checkout-cart-summary',
  'text!./checkout-cart-summary.html'
], function (checkoutCartSummary, template) {
  'use strict';

  // Override the component template.
  checkoutCartSummary.template = template;

  // The component defintion.
  return checkoutCartSummary;
});
