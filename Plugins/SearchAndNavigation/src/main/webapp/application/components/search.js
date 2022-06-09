 /**
  * The Search component provides the UI with an input field that allows a user
  * to search for items that relate to particular keywords. The search field will
  * have a binding to keep track of whether it is empty or not. Having this binding
  * allows us to know when to enable/disable the 'Search' button. For example, when
  * no text has been entered into the search field, the 'Search' button will be disabled,
  * otherwise it will be enabled.
  *
  * @module csa/plugins/search-and-navigation/application/components/search
  * @alias module:search
  * @requires module:content-item
  * @requires module:text!./search.html
  */
define([
  'content-item',
  'text!./search.html'
], function (ContentItem, template) {
  'use strict';

  /**
   * @class
   * @extends ContentItem
   */
  function Search () {
    ContentItem.apply(this, arguments);
  }

  Search.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends Search
     */
    constructor: {
      value: Search
    },

    /**
     * @property
     * @lends Search
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'searchText'
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
        var viewModel = new Search().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    Search: Search
  };
});
