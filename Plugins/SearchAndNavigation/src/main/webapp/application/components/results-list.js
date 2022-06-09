/**
 * The ResultsList component provides the UI with an area to display items returned in
 * the ResultsList cartridge content item and made available in this component's ViewModel
 * object. This component will enable a user to sort the returned results, click on particular
 * items to navigate to that item's detail page and provide pagination controls when the number
 * of items returned exceeds the total number of items allowed per page.
 *
 * @module csa/plugins/search-and-navigation/application/components/results-list
 * @alias module:results-list
 * @requires module:browser
 * @requires module:libs
 * @requires module:content-item
 * @requires module:content-item-array
 * @requires module:text!./results-list.html
 * @requires module:metadata
 */
define([
  'browser',
  'libs',
  'content-item',
  'content-item-array',
  'text!./results-list.html',
  'metadata'
], function (browser, libs, ContentItem, contentItemArray, template, metadata) {
  'use strict';

  var history = browser.history;
  var ko = libs.ko;
  var _ = libs._;

  /**
   * @class
   * @extends ContentItem
   */
  function ResultsList () {
    ContentItem.apply(this, arguments);

    // Default property values.
    this.recsPerPage(12);
  }

  ResultsList.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends ResultsList
     */
    constructor: {
      value: ResultsList
    },

    /**
     * @property
     * @lends ResultsList
     */
    metadata: {
      value: metadata,
      enumerable: true
    },

    /**
     * @property
     * @lends ResultsList
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'firstRecNum',
        'lastRecNum',
        'pagingActionTemplate',
        'recsPerPage',
        'selectedSortOption',
        'totalNumRecs',
        'validNavigationPath'
      ])
    },

    /**
     * @property
     * @lends ResultsList
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'records',
        'sortOptions'
      ])
    },

    /**
     * @property
     * @lends ResultsList
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'currentPageNumber',
        'paginationLinks'
      ])
    },

    /**
     * The currently chosen page number.
     *
     * @function
     * @lends ResultsList
     */
    currentPageNumber: {
      value: function () {
        if (this.totalNumRecs() !== 0) {
          var recsPerPage = this.recsPerPage();

          return Math.ceil(this.lastRecNum() / recsPerPage);
        }
      },
      writable: true
    },

    /**
     * Build a list of pagination links.
     *
     * @function
     * @lends ResultsList
     */
    paginationLinks: {
      value: function () {
        var pageLinks = [];
        var pageTemplate = '.' + this.pagingActionTemplate().contentPath + this.pagingActionTemplate().navigationState;
        var recsPerPage = this.recsPerPage();
        var numPageLinks = Math.ceil(this.totalNumRecs() / recsPerPage);

        if (this.totalNumRecs() > recsPerPage) {

          for (var i = 1; i <= numPageLinks; i++) {
            var linkAction = pageTemplate.replace('%7Boffset%7D', (i - 1) * recsPerPage);
            linkAction = linkAction.replace('%7BrecordsPerPage%7D', recsPerPage);

            pageLinks.push(linkAction);
          }
        }

        return pageLinks;
      },
      writable: true
    },

    /**
     * @function
     * @lends ResultsList
     */
    initialSortOption: {
      value: function initialSortOption () {
        var sortOptions = this.sortOptions();

        var selectedOption = _.find(sortOptions, function (item) {
          return item.selected;
        }) || sortOptions[0];

        return selectedOption ? this.sortOptionsValue(selectedOption) : undefined;
      }
    },

    /**
     * @function
     * @lends ResultsList
     */
    setSortOptionSelected: {
      value: function setOptionSelected (option, item) {
        ko.applyBindingsToNode(option, {attr: {selected: item.selected}}, item);
      }
    },

    /**
     * When the sort option is changed an event will fire, invoking this function.
     *
     * @function
     * @lends ResultsList
     */
    sort: {
      value: function sort () {
        var selectedSortOption = this.selectedSortOption();
        var initialSortOption = this.initialSortOption();

        if (selectedSortOption !== initialSortOption) {
          history.pushState(null, null, selectedSortOption, true);
        }
      }
    },

    /**
     * Return the value for the sort options, i.e. contentPath + navigationState.
     *
     * @function
     * @lends ResultsList
     */
    sortOptionsValue : {
      value: function sortOptionsValue (item) {
        return '.' + item.contentPath + item.navigationState;
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
        var viewModel = new ResultsList().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    ResultsList: ResultsList
  };
});
