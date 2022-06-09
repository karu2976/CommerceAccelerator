/**
 * @module csa/applications/b2cstore/plugins/spotlights/application/components/scrollable-product-spotlight-atg-slot
 * @alias module:scrollable-product-spotlight-atg-slot
 * @requires module:csa/plugins/spotlights/application/components/scrollable-product-spotlight-atg-slot
 */
define([
  'csa/plugins/spotlights/application/components/scrollable-product-spotlight-atg-slot'
], function (scrollableProductSpotlightATGSlot) {
  'use strict';

  // Get the component class.
  var ScrollableProductSpotlightATGSlot = scrollableProductSpotlightATGSlot.ScrollableProductSpotlightATGSlot;

  // Set the missing image path URL.
  ScrollableProductSpotlightATGSlot.prototype.missingImagePath = '/csadocroot/content/images/products/large/MissingProduct_large.jpg';

  // The component defintion.
  return scrollableProductSpotlightATGSlot;
});
