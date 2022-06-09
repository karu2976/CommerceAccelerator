 /**
  * The Search component provides the UI with an input field that allows a user
  * to search for items that relate to particular keywords. The search field will
  * have a binding to keep track of whether it is empty or not. Having this binding
  * allows us to know when to enable/disable the 'Search' button. For example, when
  * no text has been entered into the search field, the 'Search' button will be disabled,
  * otherwise it will be enabled.
  *
  * @module csa/plugins/search-and-navigation/application/components/Search
  * @requires module:content-item-mixin
  * @requires module:text!./Search.html
  * @requires module:i18n-loader!SearchAndNavigationLocales.json
  */
define([
  'content-item-mixin',
  'text!./Search.html',
  'i18n-loader!SearchAndNavigationLocales.json'
], function (contentItemMixin, template, searchNavi18nNs) {
  'use strict';

  // Export
  return function searchMixin (viewModel, element) {

    // Decorate the view model with the ContentItem mixin.
    contentItemMixin(viewModel, element);

    // Decorate the view model with the Search mixin.
    return Object.defineProperties(viewModel, {

      /**
       * The HTML template for the Search component.
       */
      template: {
        value: template
      },

      /**
       * The locale resource bundle for handling translations.
       */
      localeNamespace: {
        value: searchNavi18nNs
      },

      /**
       * The value entered into the search box by the user.
       */
      searchText: {
        value: viewModel.observable()
      }
    });
  };
});
