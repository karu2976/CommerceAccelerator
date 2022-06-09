/**
* The MainMenu component provides the UI with a menu that allows a user to
* navigate to particular top-level-categories and sub-categories. These categories and
* corresponding sub-categories will be returned in the MainMenu cartridge
* content item and made available in this component's ViewModel object.
*
* @module csa/plugins/search-and-navigation/application/components/main-menu
* @alias module:main-menu
* @requires module:content-item
* @requires module:text!./main-menu.html
*/
define([
  'content-item',
  'text!./main-menu.html'
], function (ContentItem, template) {
  'use strict';

  /**
   * @class
   * @extends ContentItem
   */
  function MainMenu() {
    ContentItem.apply(this, arguments);
  }

  MainMenu.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends MainMenu
     */
    constructor: {
      value: MainMenu
    },

    /**
     * @property
     * @lends MainMenu
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'categories'
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
        var viewModel = new MainMenu().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    MainMenu: MainMenu
  };
});
