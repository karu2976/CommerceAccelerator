/**
 * @module csa/base/application/component/content-slot
 * @requires module:content-item
 * @requires module:content-item-array
 * @requires module:./content-slot.html
 */
define([
  'content-item',
  'content-item-array',
  'text!./content-slot.html'
], function (ContentItem, contentItemArray, template) {
  'use strict';

  /**
   * @class
   * @extends ContentItem
   */
  function ContentSlot () {
    ContentItem.apply(this, arguments);
  }

  ContentSlot.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends ContentSlot
     */
    constructor: {
      value: ContentSlot
    },

    /**
     * @property
     * @lends ContentSlot
     */
    associations: {
      value: ContentItem.prototype.associations.concat([
        {
          name: 'contents',
          factory: contentItemArray
        }
      ])
    }
  });

  // The component defintion.
  return {

    // The component HTML template.
    template: template,

    // The component view model factory.
    viewModel: {
      createViewModel: function createViewModel (params) {
        var viewModel = new ContentSlot().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    ContentSlot: ContentSlot
  };
});
