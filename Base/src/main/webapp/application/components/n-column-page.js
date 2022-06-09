/**
 * @module csa/base/application/component/n-column-page
 * @alias module:n-column-page
 * @requires module:content-item
 * @requires module:content-item-array
 * @requires module:./n-column-page.html
 */
define([
  'content-item',
  'content-item-array',
  'text!./n-column-page.html'
], function (ContentItem, contentItemArray, template) {
  'use strict';

  /**
   * @class
   * @extends ContentItem
   */
  function NColumnPage () {
    ContentItem.apply(this, arguments);
  }

  NColumnPage.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends NColumnPage
     */
    constructor: {
      value: NColumnPage
    },

    /**
     * @property
     * @lends NColumnPage
     */
    associations: {
      value: ContentItem.prototype.associations.concat([
        {
          name: 'header',
          factory: contentItemArray
        },
        {
          name: 'footer',
          factory: contentItemArray
        },
        {
          name: 'secondaryContent',
          factory: contentItemArray
        },
        {
          name: 'mainContent',
          factory: contentItemArray
        },
        {
          name: 'mainProductContent',
          factory: contentItemArray
        }
      ])
    },

    /**
     *
     */
    set: {
      value: function set (newContentItemData) {
        if (newContentItemData) {
          newContentItemData.secondaryContent = newContentItemData.secondaryContent || [];
          newContentItemData.mainContent = newContentItemData.mainContent || [];
          newContentItemData.mainProductContent = newContentItemData.mainProductContent || [];
        }

        return ContentItem.prototype.set.call(this, newContentItemData);
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
        var viewModel = new NColumnPage().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    NColumnPage: NColumnPage
  };
});
