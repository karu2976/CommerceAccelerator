/**
 * Render for Product Detail Page.  Extends
 * csa/plugins/search-and-navigation/application/components/product-detail adding
 * additional functionality for handling missing images.
 *
 * @module csa/applications/b2cstore/plugins/search-and-navigation/application/components/product-detail
 * @alias module:product-detail
 * @requires module:csa/plugins/search-and-navigation/application/components/product-detail
 */
define([
  'csa/plugins/search-and-navigation/application/components/product-detail'
], function (productDetail) {
  'use strict';

  // Set the missing image URL.
  productDetail.ProductDetail.prototype.missingImagePath = '/csadocroot/content/images/products/large/MissingProduct_large.jpg';

  // The component defintion.
  return productDetail;
});
