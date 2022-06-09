 /**
  * The GuidedNavigation component provides the UI with an area that will output
  * a 'navigation' array of RefinementMenu cartridges. This 'navigation' array will
  * be returned in the GuidedNavigation cartridge content item and made available in
  * this component's ViewModel object.
  *
  * @module csa/plugins/search-and-navigation/application/components/guided-navigation
  * @alias module:guided-navigation
  * @requires module:browser
  * @requires module:content-item
  * @requires module:content-item-array
  * @requires module:text!./guided-navigation.html
  */
define([
  'browser',
  'content-item',
  'content-item-array',
  'text!./guided-navigation.html'
], function (browser, ContentItem, contentItemArray, template) {
  'use strict';

  var location = browser.location;

  /**
   * @class
   * @extends ContentItem
   */
  function GuidedNavigation () {
    ContentItem.apply(this, arguments);
  }

  GuidedNavigation.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends GuidedNavigation
     */
    constructor: {
      value: GuidedNavigation
    },

    /**
     * @property
     * @lends GuidedNavigation
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'validNavigationPath'
      ])
    },

    /**
     * @property
     * @lends GuidedNavigation
     */
    associations: {
      value: ContentItem.prototype.associations.concat([
        {
          name: 'navigation',
          factory: contentItemArray
        }
      ])
    },

    /**
     * @property
     * @lends GuidedNavigation
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'showFilter'
      ])
    },

    /**
     * Set this computed value to the current URL's 'showFilter' query parameter
     * value (if it exists). The value should either be 'true' or 'false'. When
     * 'showFilter' doesn't exist in the URL, set as false.
     *
     * @function
     * @lends GuidedNavigation
     */
    showFilter: {
      value: function () {
        var showFilterIndex = location.search.lastIndexOf('showFilter');

        if (showFilterIndex !== -1) {
          var showFilterValue =
            location.search.substring(
              showFilterIndex, location.search.length).split('=')[1];

          if (showFilterValue === 'true' || showFilterValue === 'false') {
            return showFilterValue === 'true';
          }
          else {
            return showFilterValue.substring(0, showFilterValue.length).split('&')[0] === 'true';
          }
        }

        return false;
      },
      writable: true
    }
  });

  // The component defintion.
  return {

    // The component HTML template.
    template: template,

    // The component view model factory.
    viewModel: {
      createViewModel: function createViewModel (params) {
        var viewModel = new GuidedNavigation().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    GuidedNavigation: GuidedNavigation
  };
});
