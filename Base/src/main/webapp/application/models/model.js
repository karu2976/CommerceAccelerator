/**
 * @module csa/base/model
 * @alias module:model
 * @requires module:libs
 */
define([
  'libs'
], function (libs) {
  'use strict';

  var $ = libs.$;
  var ko = libs.ko;

  /**
   * Base class for all models--both domain models and view models.
   *
   * @class
   * @alias module:csa/base/model
   */
  function Model () {

    // Define observables.
    this.observables.forEach(function (name) {
      this[name] = ko.observable();
    }, this);

    // Define observableArrays.
    this.observableArrays.forEach(function (name) {
      this[name] = ko.observableArray();
    }, this);

    // Define associations.
    this.associations.forEach(function (associationDescriptor) {
      var name = typeof associationDescriptor === 'string' ? associationDescriptor : associationDescriptor.name;
      var factory = typeof associationDescriptor === 'string' ? ko.observableArray : associationDescriptor.factory;

      this[name] = factory();
    }, this);

    // Define computed observables.
    this.computeds.forEach(function (name) {
      this[name] = ko.pureComputed(this[name], this);
    }, this);

    // Define private properties.
    Object.defineProperty(this, '__raw__', {
      value: ko.observable()
    });
  }

  Object.defineProperties(Model.prototype, {

    /**
     * @property
     * @lends Model
     */
    idAttribute: {
      value: 'id'
    },

    /**
     * @property
     * @lends Model
     */
    observables: {
      value: [
        'id'
      ]
    },

    /**
     * @property
     * @lends Model
     */
    observableArrays: {
      value: []
    },

    /**
     * @property
     * @lends Model
     */
    associations: {
      value: []
    },

    /**
     * @property
     * @lends Model
     */
    computeds: {
      value: []
    },

    /**
     * @property
     * @lends Model
     */
    promise: {
      value: $.when(),
      writable: true
    },

    /**
     * Set the value each model property to the corresponding data value in newData.
     *
     * @function
     * @lends Model
     * @param {Object} newData An object of new data values for the model properties.
     */
    set: {
      value: function set (newData) {
        if (!newData || newData instanceof Array) {
          // No valid data, do nothing.
          return this;
        }

        // For each enumerable property im this model, try to update the value.
        for (var key in this) {
          var newValue = newData[key];
          var property = this[key];

          if (newValue === undefined) {
            // When the new value is undefined leave the current value unchanged.
          }
          else if (typeof property.set === 'function') {
            // When the property is reference to a model object or model array, update the property
            // with the new data.
            property.set(newValue);
          }
          else if (ko.isWritableObservable(property)) {
            // When the property is an observable, update the property with the new value.
            property(newValue);
          }
        }

        // Update the __raw__ (observable) with the newData. This will notify any observers that the
        // model data has changed.
        this.__raw__(newData);

        return this;
      }
    }
  });

  return Model;
});
