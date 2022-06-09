/**
 * The static-page-navigation component provides the UI with a static page menu area. The menu will be
 * returned in a static-page-navigation cartridge content item and made available in this component's
 * ViewModel object.
 *
 * @module csa/base/application/component/static-page-navigation
 * @alias module:static-page-navigation
 * @requires module:content-item
 * @requires module:content-item-array
 * @requires module:./static-page-navigation.html
 */
define([
  'content-item',
  'content-item-array',
  'text!./static-page-navigation.html'
], function (ContentItem, contentItemArray, template) {
  'use strict';

  /**
   * @class
   * @extends ContentItem
   */
  function StaticPageNavigation () {
    ContentItem.apply(this, arguments);
  }

  StaticPageNavigation.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends StaticPageNavigation
     */
    constructor: {
      value: StaticPageNavigation
    },

    /**
     * @property
     * @lends SitePreferences
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'currentStaticPage'
      ])
    },

    /**
     * @property
     * @lends SitePreferences
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'links'
      ])
    },

    /**
     * @property
     * @lends StaticPageNavigation
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
        var viewModel = new StaticPageNavigation().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    StaticPageNavigation: StaticPageNavigation
  };
});
