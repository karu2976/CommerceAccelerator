 /**
  * Render for the Product Detail Page.  Controls the product Image and
  * prices.
  *
  * @module csa/plugins/search-and-navigation/application/components/product-detail
  * @alias module:product-detail
  * @requires module:content-item
  * @requires module:content-item-array
  * @requires module:text!./product-detail.html
  * @requires module:catalog
  */
define([
  'content-item',
  'content-item-array',
  'text!./product-detail.html',
  'catalog',
  'metadata'
], function (ContentItem, contentItemArray, template, catalog, metadata) {
  'use strict';

  /**
   * @class
   * @extends ContentItem
   */
  function ProductDetail () {
    ContentItem.apply(this, arguments);
  }

  ProductDetail.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends ProductDetail
     */
    constructor: {
      value: ProductDetail
    },

    /**
     * @property
     * @lends ProductDetail
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'product',
        'selectedSku'
      ])
    },

    /**
     * @property
     * @lends ProductDetail
     */
    associations: {
      value: ContentItem.prototype.associations.concat([
        {
          name: 'ProductInformation',
          factory: contentItemArray
        }
      ])
    },

    /**
     * @property
     * @lends ProductDetail
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'listPrice',
        'salePrice',
        'showSalePrice'
      ])
    },

    /**
     * When a media content image can't be found, this value will contain the path
     * of a default image to display instead.
     *
     * @property
     * @lends ProductDetail
     */
    missingImagePath: {
      value: '',
      writable: true
    },

    /**
     * The catalog domain model
     *
     * @property
     * @lends ProductDetail
     */
    catalog: {
      value: catalog,
      enumerable: true
    },

    /**
     * The metadata domain model.
     *
     * @property
     * @lends ProductDetail
     */
    metadata: {
      value: metadata,
      enumerable: true
    },

    /**
     * @property
     * @lends ProductDetail
     */
    set: {
      value: function set (newData) {
        ContentItem.prototype.set.call(this, newData);

        // Populate the catalog model.
        catalog.set({
          product: newData.product,
          selectedSku: newData.selectedSku || null
        });

        return this;
      }
    },

    /**
     * The list price of the currently selected SKU
     *
     * If no SKU has been selected the listPrice of the product will be returned.
     *
     * If a SKU has been selected the listPrice for the currently selected SKU
     * is returned unless it is undefined or null.
     *
     * @function
     * @lends ProductDetail
     */
    listPrice: {
      value: function () {
        var listPrice = '';

        if (catalog.selectedSku()) {
          listPrice = catalog.selectedSku().listPrice || this.product().listPrice;
        }
        else {
          listPrice = this.product().listPrice;
        }

        return listPrice;
      },
      writable: true
    },

    /**
     * The sale price of the currently selected SKU
     *
     * If no SKU has been selected the salePrice of the product will be returned.
     *
     * If a SKU has been selected the salePrice for the currently selected SKU
     * is returned unless it is undefined or null.
     *
     * @function
     * @lends ProductDetail
     */
    salePrice: {
      value: function () {
        var salePrice = '';

        if (this.showSalePrice()) {
          if (catalog.selectedSku()) {
            salePrice = catalog.selectedSku().salePrice || this.product().salePrice;
          }
          else {
            salePrice = this.product().salePrice;
          }
        }

        return salePrice;
      },
      writable: true
    },

    /**
     * Boolean flag to determine whether or not the sale price of a product should
     * be shown.
     *
     * @function
     * @lends ProductDetail
     */
    showSalePrice: {
      value: function () {
        var showSalePrice = '';

        if (catalog.selectedSku()) {
          showSalePrice = catalog.selectedSku().showSalePrice || this.product().showSalePrice;
        }
        else {
          showSalePrice = this.product().showSalePrice;
        }

        return showSalePrice;
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
        var viewModel = new ProductDetail().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    ProductDetail: ProductDetail
  };
});
