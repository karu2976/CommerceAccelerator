/**
 * @module csa/plugins/search-and-navigation/application/services/web/oc-rest/get-selected-sku
 * @alias oc-rest-get-selected-sku
 * @requires module:catalog
 */
define([
  'catalog'
], function (catalog) {
  'use strict';

  /**
   * getSelectedSku:
   *   Gets the SKU properties for the given product and sku picker values.
   *
   * Input Parameters:
   *   type - The SKU type of the SKU that details are being requested for as defined in the
   *     application.
   *   productIdPropertyName - The repository id of the current product.
   *   selectedWoodFinish (optional) - The value currently selected in the wood finish picker. For
   *     use with SKUs of type furniture-sku.
   *   selectedColor (optional) - The value currently selected in the color picker.  Used when
   *     working with SKUs of type clothing-sku.
   *   selectedSize (optional) - The value currently selected in the size picker.  Used when
   *     working with SKUs of type clothing-sku.
   *
   * Output: (example)
   *  {
   *    "selectedSku": {
   *      "color": "Black"
   *      "displayName": "Belted Rugged Shorts"
   *      "id": "sku40299"
   *      "inStock": true
   *      "isAvailable": true
   *      "listPrice": 45.5
   *      "repositoryId": "sku40299"
   *      "showSalePrice": false
   *      "size": "30"
   *      "type": "clothing-sku"
   *    }
   *  }
   */
  return function getSelectedSku (skuOptions) {

    // Define service options.
    var options = {
      serviceId: 'ocRestGetSelectedSku',
      url: '/rest/model/atg/commerce/product/detail/ProductDetailsSkuPickerActor/getSelectedSku',
      type: 'GET',
      data: skuOptions
    };

    // Make service call.
    return catalog.datasource.execute(options).done(function (data) {
      if (data.selectedSku) {
        catalog.set(data);
      }
      else {
        catalog.set({selectedSku: null});
      }
    });
  };
});
