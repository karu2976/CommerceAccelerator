/**
 * @module csa/base/application/models/domain/entity-array
 * @alias module:entity-array
 * @requires module:model-array
 * @requires module:entity
 */
define([
  'model-array',
  'entity'
], function (modelArray, Entity) {
  'use strict';

  var entityArrayMixinDescriptor = {
    Model: {
      value: Entity,
      configurable: true
    }
  };

  return function contentItemArray () {
    return Object.defineProperties(modelArray(), entityArrayMixinDescriptor);
  };
});
