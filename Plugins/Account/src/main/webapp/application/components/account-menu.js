/**
 * Renderer for the AccountMenu cartridge. Displays a list of menu options for a logged in user or
 * a link to the login page for an anonymous user.
 *
 * @module csa/plugins/account/application/components/account-menu
 * @alias module:account-menu
 * @requires module:content-item-mixin
 * @requires module:text!./account-menu.html
 * @requires module:profile-services
 * @requires module:profile
 */
define([
  'content-item',
  'text!./account-menu.html',
  'profile-services',
  'profile'
], function (ContentItem, template, profileServices, profile) {
  'use strict';

  function AccountMenu() {
    ContentItem.apply(this, arguments);
  }

  // Define class prototype
  AccountMenu.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends AccountMenu
     */
    constructor: {
      value: AccountMenu
    },

    /**
     * @property
     * @lends AccountMenu
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'authenticatedMenuOptions'
      ])
    },

    /**
     * @property
     * @lends AccountMenu
     */
    profile: {
      value: profile,
      enumerable: true
    },

    /**
     * Log the user out and redirect the user.
     *
     * @function
     * @lends AccountMenu
     */
    logout: {
      value: function logout () {
        profileServices.logout();
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
        var viewModel = new AccountMenu().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    AccountMenu: AccountMenu
  };
});
