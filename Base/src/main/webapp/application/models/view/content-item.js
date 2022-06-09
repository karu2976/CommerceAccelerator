/**
 * @module csa/base/application/models/view/content-item
 * @alias content-item
 * @requires module:libs
 * @requires module:model
 * @requires module:cartridge-to-component-lookup
 */
define([
  'libs',
  'model',
  'cartridge-to-component-lookup'
], function (libs, Model, cartridgeToComponentLookup) {
  'use strict';

  var _ = libs._;
  var ko = libs.ko;

  /**
   * @class
   */
  function ContentItem () {
    Model.apply(this, arguments);
  }

  ContentItem.prototype = Object.create(Model.prototype, {

    /**
     * @property
     * @lends ContentItem
     */
    constructor: {
      value: ContentItem
    },

    /**
     * @property
     * @lends ContentItem
     */
    idAttribute: {
      value: '@type'
    },

    /**
     * @property
     * @lends Model
     */
    observables: {
      value: Model.prototype.observables.concat([
        '@type'
      ])
    },

    /**
     * @property
     * @lends Model
     */
    computeds: {
      value: Model.prototype.computeds.concat([
        'cid',
        'classes',
        'name'
      ])
    },

    /**
     * The model client id.
     *
     * @property
     * @lends ContentItem
     */
    cid: {
      value: function () {
        var type = ko.unwrap(this['@type']);
        var cid = _.uniqueId(type + '-');

        return cid;
      },
      writable: true
    },

    /**
     * CSS semantic classes classes.
     *
     * @property
     * @lends ContentItem
     */
    classes: {
      value: function () {
        var classes = ko.unwrap(this['@type']) + ' ContentItem';

        return classes;
      },
      writable: true
    },

    /**
     * Knockout component name.
     *
     * @property
     * @lends ContentItem
     */
    name: {
      value: function () {
        var type = ko.unwrap(this['@type']);
        var name = cartridgeToComponentLookup[type] || type;

        return name;
      },
      writable: true
    },

    /**
     * Knockout component params.
     *
     * @property
     * @lends ContentItem
     */
    params: {
      get: function () {
        return this;
      }
    },

    /**
     * @function
     * @lends ContentItem
     */
    init: {
      value: function init (contentItem) {

        // Link this component with __raw__ observable, i.e. when the __raw__ changes, update the
        // corresponding properties of this component.
        this.link(contentItem.__raw__);

        return this;
      }
    },

    /**
     * @function
     * @lends ContentItem
     */
    link: {
      value: function link (observable) {
        if (ko.isObservable(observable)) {

          // A model can be linked to one data source at a time.
          // Ensure exiting links are destroyed before creating a new link.
          this.unlink();

          // Create a new link, i.e. a subscription listening for changes to the observable.
          // Save the subscription as a hidden property so it can be disposed of later.
          this.__subscription__ = observable.subscribe(function (newData) {
            // When the observable value changes, update this model with the new data values.
            this.set(newData);
          }, this);

          // Initialise the model.
          this.set(observable());
        }

        return this;
      }
    },

    /**
     * @function
     * @lends ContentItem
     */
    unlink: {
      value: function unlink () {
        if (this.__subscription__) {
          // Dispose of the subscription.
          this.__subscription__.dispose();
        }

        // Delete the hidden property.
        delete this.__subscription__;

        return this;
      }
    },

    /**
     * @function
     * @lends ContentItem
     */
    dispose: {
      value: function dispose () {
        this.unlink();

        return this;
      }
    }
  });

  return ContentItem;
});
