/**
 * Render for the product description of the currently viewed product.
 *
 * @module csa/plugins/search-and-navigation/application/components/product-description
 * @requires module:content-item-mixin
 * @requires module:text!./product-description.html
 * @requires module:catalog
 */
define([
  'content-item',
  'text!./product-description.html',
  'catalog'
], function (ContentItem, template, catalog) {
  'use strict';

  /**
   * @class
   * @extends ContentItem
   */
  function ProductDescription () {
    ContentItem.apply(this, arguments);
  }

  ProductDescription.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends ProductDescription
     */
    constructor: {
      value: ProductDescription
    },

    /**
     * @property
     * @lends ProductDescription
     */
    catalog: {
      value: catalog,
      enumerable: true
    }
  });

  // The component defintion.
  return {

    // The component HTML template.
    template: template,

    // The component view model factory.
    viewModel: {
      createViewModel: function createViewModel (params) {
        var viewModel = new ProductDescription().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    ProductDescription: ProductDescription
  };
});

