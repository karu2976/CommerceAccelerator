/**
 * This script defines custom bindings for bcc preview.
 *  preview:
 *    This binding provides edit capability on node containing product, catalog or promotion details.
 *    It takes an object that has item descriptor name property with value as its repository id.
 *
 * @module csa/plugins/preview/application/preview/custom-bcc-preview-bindings
 * @requires module:libs
 */
define([
  'libs'
], function (libs) {
  'use strict';

  var ko = libs.ko;

  return function bccPreviewCustomBindings (ocpreview) {

    // Define the preview binding
    ko.bindingHandlers.preview = {
      init: function (element, valueAccessor) {

        // Provide edit capability on products
        if (valueAccessor().product) {
          var productId = ko.isObservable(valueAccessor().product) ?
            valueAccessor().product() : valueAccessor().product;
          ocpreview.addPreviewableElement(element, 'ProductCatalog', 'product', productId);
        }

        // Provide edit capability on categories
        if (valueAccessor().category) {
          var categoryId = ko.isObservable(valueAccessor().category) ?
            valueAccessor().category() : valueAccessor().category;
          ocpreview.addPreviewableElement(element, 'ProductCatalog', 'category', categoryId);
        }

        //Provide edit capability on promotions
        if (valueAccessor().promotion) {
          var promotionId = ko.isObservable(valueAccessor().promotion) ?
            valueAccessor().promotion() : valueAccessor().promotion;
          ocpreview.addPreviewableElement(element, 'ProductCatalog', 'promotion', promotionId);
        }

        //Provide edit capability on mediaContent
        if (valueAccessor().mediaContent) {
          var mediaContentId = ko.isObservable(valueAccessor().mediaContent) ?
            valueAccessor().mediaContent() : valueAccessor().mediaContent;
          ocpreview.addPreviewableElement(element, 'ContentManagementRepository', 'mediaContent', mediaContentId);
        }

        //Provide edit capability on section with all details available in itemPreviewUrl property
        if (valueAccessor().itemPreviewUrl) {
          var previewUrlTextArray = ko.isObservable(valueAccessor().itemPreviewUrl) ?
            valueAccessor().itemPreviewUrl().split('/') : valueAccessor().itemPreviewUrl.split('/');
          ocpreview.addPreviewableElement(element, previewUrlTextArray[0], previewUrlTextArray[1], previewUrlTextArray[2]);
        }
      }
    };

  };
});
