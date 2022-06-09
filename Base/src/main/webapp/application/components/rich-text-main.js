/**
 * @module csa/base/application/component/rich-text-main
 * @alias module:rich-text-main
 * @requires module:content-item
 * @requires module:content-item-array
 * @requires module:./rich-text-main.html
 */
define([
  'content-item',
  'content-item-array',
  'text!./rich-text-main.html'
], function (ContentItem, contentItemArray, template) {
  'use strict';

  /**
   * @class
   * @extends ContentItem
   */
  function RichTextMain () {
    ContentItem.apply(this, arguments);
  }

  RichTextMain.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends RichTextMain
     */
    constructor: {
      value: RichTextMain
    },

    /**
     * @property
     * @lends RichTextMain
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'content'
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
        var viewModel = new RichTextMain().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    RichTextMain: RichTextMain
  };
});
