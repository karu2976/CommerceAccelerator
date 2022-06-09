/**
 * This is the base application view model object, and represents the complete application view state (as knockout observables).
 * The view model object is passed into <code>ko.applyBindings</code> and the knockout binding are initialized.  As the view model
 * state changes, the bindings are (automatically) re-evaluated and view updated.
 *
 * @module csa/base/application/models/view/page
 * @alias module:page
 * @requires module:content-slot
 * @requires module:metadata
 */
define([
  'content-slot',
  'metadata',
  'oc-xmgr-datasource'
], function (contentSlot, metadata, Datasource) {
  'use strict';

  var ContentSlot = contentSlot.ContentSlot;

  /**
   * @class
   */
  function Page () {
    ContentSlot.apply(this, arguments);

    // Override attribute '@type'
    this['@type'] = 'PageSlot';

    // Define the model datasource
    this.datasource = new Datasource(this);

    // Preset page with blank data, to ensure bindings can be initialized before content is loaded.
    this.set({contents: []});
  }

  Page.prototype = Object.create(ContentSlot.prototype, {

    /**
     * @property
     * @lends Page
     */
    constructor: {
      value: Page
    },

    // ~ to be removed
    observables: {
      value: ContentSlot.prototype.observables.concat([
        'canonical',
        'previewModuleUrl'
      ])
    },

    /**
     * @property
     * @lends Page
     */
    metadata: {
      value: metadata,
      enumerable: true
    },

    /**
     * @function
     * @lends Page
     */
    set: {
      value: function set (newContentItemData) {
        if (!newContentItemData || newContentItemData instanceof Array) {
          // No valid data, do nothing.
          return;
        }

        if (!(newContentItemData.contents instanceof Array)) {
          newContentItemData.contents = [newContentItemData];
        }

        ContentSlot.prototype.set.call(this, newContentItemData);

        return this;
      }
    }
  });

  return new Page();
});
