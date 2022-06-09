/**
 * Render for the Sku Selector on the Product Detail Page.  Extends
 * CSA/Applications/B2CStore/Plugins/SearchAndNavigation/sku-selector to add
 * additional functionality around translations.
 *
 * @module csa/applications/b2cstore/plugins/search-and-navigation/application/components/sku-selector
 * @alias module:sku-selector
 * @requires module:csa/plugins/search-and-navigation/application/components/sku-selector
 * @requires module:i18n-loader!AppSearchAndNavigationLocales.json
 * @requires module:libs
 */
define([
  'libs',
  'content-item',
  'csa/plugins/search-and-navigation/application/components/sku-selector'
], function (libs, ContentItem, skuSelector) {
  'use strict';

  var ko = libs.ko;
  var SkuSelector = skuSelector.SkuSelector;

  /**
   * @class
   * @extends SkuSelector
   */
  function B2CStoreSkuSelector () {
    SkuSelector.apply(this, arguments);
  }

  B2CStoreSkuSelector.prototype = Object.create(SkuSelector.prototype, {

    /**
     * @property
     * @lends B2CStoreSkuSelector
     */
    constructor: {
      value: B2CStoreSkuSelector
    },

    /**
     * @property
     * @lends B2CStoreSkuSelector
     */
    set: {
      value: function set (newData) {
        SkuSelector.prototype.set.call(this, newData);

        if (newData.skuPickers) {

          /*
           * Create a knockout component binding for each sku picker that has
           * associated additional information content. See
           * http://knockoutjs.com/documentation/component-binding.html
           *
           * Use require to load the correct html file and pass a view model
           * which contains the namespace for the app specific locales
           */
          newData.skuPickers.forEach(function (skuPicker) {
            if (skuPicker.helperInformation && !ko.components.isRegistered(skuPicker.type)) {
              ko.components.register(skuPicker.type, {
                viewModel: {
                  instance: {}
                },
                template: {
                  require: 'text!' + skuPicker.helperInformation.contentUrl
                }
              });
            }
          });
        }

        return this;
      }
    }
  });

  // Override the component view model class.
  skuSelector.SkuSelector = B2CStoreSkuSelector;

  // Modify the view model factory to use the new class.
  skuSelector.viewModel.createViewModel = function createViewModel (params) {
    var viewModel = new B2CStoreSkuSelector().init(params);

    return viewModel;
  };

  // The component defintion.
  return skuSelector;
});
