/**
 * @module csa/base/application/models/view/content-item-array
 * @alias module:content-item-array
 * @requires module:model-array
 * @requires module:content-item
 * @requires module:cartridge-to-component-lookup
 */
define([
  'model-array',
  'content-item',
  'cartridge-to-component-lookup'
], function (modelArray, ContentItem, cartridgeToComponentLookup) {
  'use strict';

  var modelArrayPrototype = modelArray();

  var contentItemArrayMixinDescriptor = {

    /**
     *
     */
    Model: {
      value: ContentItem,
      configurable: true
    },

    /**
     *
     */
    modelId: {
      value: function modelId (model) {
        var id = modelArrayPrototype.modelId.call(this, model);
        id = cartridgeToComponentLookup[id] || id;

        return id;
      },
      configurable: true
    },
  };

  return function contentItemArray () {
    return Object.defineProperties(modelArray(), contentItemArrayMixinDescriptor);
  };
});
