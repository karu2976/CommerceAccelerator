/**
 * Renderer for the AccountMenuNavigation cartridge. Displays a list of menu options for a logged in
 * user or a link to the login page for an anonymous user.
 *
 * @module csa/plugins/account/application/components/account-menu-navigation
 * @requires module:content-item
 * @requires module:text!./account-menu-navigation.html
 * @requires module:profile-services
 */
define([
  'content-item',
  'text!./account-menu-navigation.html',
  'profile',
  'profile-services'
], function (ContentItem, template, profile, profileServices) {
  'use strict';

  function AccountMenuNavigation() {
    ContentItem.apply(this, arguments);
  }

  // Define class prototype
  AccountMenuNavigation.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends AccountMenuNavigation
     */
    constructor: {
      value: AccountMenuNavigation
    },

    /**
     * @property
     * @lends AccountMenuNavigation
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'authenticatedMenuOptions',
        'unauthenticatedMenuOptions'
      ])
    },

    /**
     * Log the user out and redirect the user.
     */
    logout: {
      value: function logout () {
        return profileServices.logout();
      }
    },

    /**
     * @property
     * @lends AccountMenuNavigation
     */
    profile: {
      value: profile,
      enumerable: true
    }
  });

  // The component defintion.
  return {

    // The component HTML template.
    template: template,

    // The component view model factory.
    viewModel: {
      createViewModel: function createViewModel (params) {
        var viewModel = new AccountMenuNavigation().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    AccountMenuNavigation: AccountMenuNavigation
  };

});
