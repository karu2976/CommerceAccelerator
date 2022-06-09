/**
* The Breadcrumbs component provides the UI with an area that will output the
* refinement selections chosen by the user. The selections will be returned in
* the Breadcrumbs cartridge content item and made available in this component's
* ViewModel object.
*
* @module csa/plugins/search-and-navigation/application/components/breadcrumbs
* @alias module: breadcrumbs
* @requires module:content-item
* @requires module:text!./breadcrumbs.html
*/
define([
  'content-item',
  'text!./breadcrumbs.html'
], function (ContentItem, template) {
  'use strict';

  /**
   * @class
   * @extends ContentItem
   */
  function Breadcrumbs () {
    ContentItem.apply(this, arguments);
  }

  Breadcrumbs.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends Breadcrumbs
     */
    constructor: {
      value: Breadcrumbs
    },

    /**
     * @property
     * @lends Breadcrumbs
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'removeAllAction',
        'validNavigationPath'
      ])
    },

    /**
     * @property
     * @lends Breadcrumbs
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'rangeFilterCrumbs',
        'refinementCrumbs',
        'searchCrumbs',
      ])
    },

    /**
     * @property
     * @lends Breadcrumbs
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'contentPath',
        'hasSkuActivePriceCrumb',
        'showRemoveAll',
        'showSelections'
      ])
    },

    /**
     * Retrieve the current contentPath used in the returned breadcrumbs and from it,
     * return a new contentPath property by removing the preceding forward slash. For
     * example, if the contentPath is '/browse', this computed function will return
     * 'browse'. This is necessary because the template's anchor href data-bind requires
     * a non-relative content path (no preceding forward slash) in order to automatically
     * prepend the base URI.
     *
     * @function
     * @lends Breadcrumbs
     */
    contentPath : {
      value: function () {
        var contentPath = this.removeAllAction().contentPath.replace(/^\//, '');

        return contentPath;
      },
      writable: true
    },

    /**
     * Determine whether or not the sku.activeActivePrice rangeFilterCrumb has
     * already been added.
     *
     * @function
     * @lends Breadcrumbs
     */
    hasSkuActivePriceCrumb: {
      value: function () {
        var hasSkuActivePriceCrumb = false;

        this.rangeFilterCrumbs().forEach(function (rangeFilterCrumb) {
          if (rangeFilterCrumb.propertyName === 'sku.activePrice') {
            hasSkuActivePriceCrumb = true;
          }
        });

        return hasSkuActivePriceCrumb;
      },
      writable: true
    },

    /**
     * This function determines whether to display the breadcrumb 'removeAll' link.
     * true will only be returned if 2 refinements have been selected.
     *
     * @function
     * @lends Breadcrumbs
     */
    showRemoveAll: {
      value: function () {
        var showRemoveAll = (this.refinementCrumbs().length > 1) ||
            (this.searchCrumbs().length > 1) ||
            (this.refinementCrumbs().length > 0 && this.searchCrumbs().length > 0) ||
            (this.refinementCrumbs().length > 0 && this.hasSkuActivePriceCrumb()) ||
            (this.searchCrumbs().length > 0 && this.hasSkuActivePriceCrumb());

        return showRemoveAll;
      },
      writable: true
    },

    /**
     * This function determines whether to display the breadcrumb selections
     * or not. It only returns true when the sku.activePrice rangeFilterCrumb,
     * refinementCrumbs or searchCrumbs are not empty.
     *
     * @function
     * @lends Breadcrumbs
     */
    showSelections: {
      value: function () {
        var showSelections = (this.refinementCrumbs().length > 0) ||
            (this.searchCrumbs().length > 0) ||
            this.hasSkuActivePriceCrumb();

        return showSelections;
      },
      writable: true
    },

    /**
     * To ensure that the (filters) refinement menu doesn't automatically open when a user clicks
     * on a breadcrumb link or a breadcrumb's remove action, the 'showFilter' query parameter value
     * should always be set to false.
     *
     * @function
     * @lends Breadcrumbs
     */
    navigationStateNoShowFilter: {
      value: function navigationStateNoShowFilter (navigationState) {
        return navigationState.replace(/showFilter=true/i, 'showFilter=false');
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
        var viewModel = new Breadcrumbs().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    Breadcrumbs: Breadcrumbs
  };
});
