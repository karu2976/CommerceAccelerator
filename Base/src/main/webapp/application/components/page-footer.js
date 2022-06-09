/**
 * @module csa/base/application/component/page-footer
 * @alias module:page-footer
 * @requires module:content-item
 * @requires module:content-item-array
 * @requires module:./page-footer.html
 */
define([
  'content-item',
  'content-item-array',
  'text!./page-footer.html'
], function (ContentItem, contentItemArray, template) {
  'use strict';

  /**
   * @class
   * @extends ContentItem
   */
  function PageFooter () {
    ContentItem.apply(this, arguments);
  }

  PageFooter.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends PageFooter
     */
    constructor: {
      value: PageFooter
    },

    /**
     * @property
     * @lends PageFooter
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'copyright',
        'linkMenu'
      ])
    },

    /**
     * @property
     * @lends PageFooter
     */
    hrefFromLink: {
      value: function hrefFromLink (link) {
        var href = '.' + (link.url || link.contentPath + (link.navigationState || ''));

        return href;
      }
    }
  });

  // The component defintion.
  return {

    // The component HTML template.
    template: template,

    // The component view model factory.
    viewModel: {
      createViewModel: function createViewModel (params) {
        var viewModel = new PageFooter().init(params);

        return viewModel;
      }
    },

    // The component view model classes.
    PageFooter: PageFooter
  };
});
