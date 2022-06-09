 /**
  * The RefinementMenu component provides the UI with an area that will output a particular type
  * of refinement menu e.g. categories, brands, price ranges etc. The refinement menu will be
  * returned in a RefinementMenu cartridge content item and made available in this component's
  * ViewModel object.
  *
  * @module csa/plugins/search-and-navigation/application/components/refinement-menu
  * @alias module:refinement-menu
  * @requires module:browser
  * @requires module:libs
  * @requires module:content-item
  * @requires module:text!./refinement-menu.html
  */
define([
  'browser',
  'content-item',
  'text!./refinement-menu.html'
], function (browser, ContentItem, template) {
  'use strict';

  /**
   * @class
   * @extends ContentItem
   */
  function RefinementMenu () {
    ContentItem.apply(this, arguments);
  }

  RefinementMenu.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends RefinementMenu
     */
    constructor: {
      value: RefinementMenu
    },

    /**
     * @property
     * @lends RefinementMenu
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'currencySymbol',
        'dimensionName',
        'moreLink',
        'lessLink'
      ])
    },

    /**
     * @property
     * @lends RefinementMenu
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'refinements'
      ])
    },

    /**
     * @property
     * @lends RefinementMenu
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'contentPath',
        'showFilterQueryStringName'
      ])
    },

    /**
     * Retrieve the current contentPath used in the returned refinements and from it,
     * return a new contentPath property by removing the preceding forward slash. For
     * example, if the contentPath is '/browse', this computed function will return
     * 'browse'. This is necessary because the template's anchor href data-bind requires
     * a non-relative content path (no preceding forward slash) in order to automatically
     * prepend the base URI.
     *
     * @function
     * @lends RefinementMenu
     */
    contentPath : {
      value: function () {
        var contentPath;
        var refinements = this.refinements();

        if (refinements.length > 0) {
          contentPath = refinements[0].contentPath.replace(/^\//, '');
        }

        return contentPath;
      },
      writable: true
    },

    /**
     * This computed value constructs the format of the 'showFilter' query
     * parameter. If the current navigation state already contains a '?'
     * separator, prepend a '&' separator to the parameter name; otherwise
     * prepend a '?' separator.
     *
     * @function
     * @lends RefinementMenu
     */
    showFilterQueryStringName: {
      value: function () {
        var showFilterQueryStringName;
        var refinements = this.refinements();

        if (refinements.length > 0) {
          var navigationState = refinements[0].navigationState;

          showFilterQueryStringName = (navigationState.indexOf('?') !== -1) ? '&showFilter' : '?showFilter';
        }

        return showFilterQueryStringName;
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
        var viewModel = new RefinementMenu().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    RefinementMenu: RefinementMenu
  };
});
