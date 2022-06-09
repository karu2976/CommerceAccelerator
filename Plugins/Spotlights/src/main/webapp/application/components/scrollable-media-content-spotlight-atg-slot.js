
/**
 * @module csa/plugins/spotlights/application/components/scrollable-media-content-spotlight-atg-slot
 * @alias module:scrollable-media-content-spotlight-atg-slot
 * @requires module:content-item
 * @requires module:text!./scrollable-media-content-spotlight-atg-slot.html
 */
define([
  'content-item',
  'text!./scrollable-media-content-spotlight-atg-slot.html'
], function (ContentItem, template) {
  'use strict';

  /**
   * @class
   * @extends ContentItem
   */
  function ScrollableMediaContentSpotlightATGSlot () {
    ContentItem.apply(this, arguments);
  }

  ScrollableMediaContentSpotlightATGSlot.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends ScrollableMediaContentSpotlightATGSlot
     */
    constructor: {
      value: ScrollableMediaContentSpotlightATGSlot
    },

    /**
     * @property
     * @lends ScrollableMediaContentSpotlightATGSlot
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'items'
      ])
    },

    /**
     * @property
     * @lends ScrollableMediaContentSpotlightATGSlot
     */
    missingImagePath: {
      value: '',
      writable: true
    }
  });

  return {

    // The component HTML template.
    template: template,

    // The component view model factory.
    viewModel: {
      createViewModel: function createViewModel (params) {
        var viewModel = new ScrollableMediaContentSpotlightATGSlot().init(params);

        return viewModel;
      }
    },

    // The component view model classes.
    ScrollableMediaContentSpotlightATGSlot: ScrollableMediaContentSpotlightATGSlot
  };
});
