/**
 * Render for the AccountChangePassword cartridge. Allows a user to update their password.
 *
 * @module csa/plugins/account/application/components/account-change-password
 * @requires module:browser
 * @requires module:libs
 * @requires module:content-item
 * @requires module:text!./account-change-password.html
 * @requires module:profile-services
 */
define([
  'browser',
  'libs',
  'content-item',
  'text!./account-change-password.html',
  'profile-services'
], function (browser, libs, ContentItem, template, profileServices) {
  'use strict';

  var history = browser.history;
  var i18next = libs.i18next;
  var toastr = libs.toastr;

  function AccountChangePassword() {
    ContentItem.apply(this, arguments);
  }

  // Define class prototype
  AccountChangePassword.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends AccountChangePassword
     */
    constructor: {
      value: AccountChangePassword
    },

    /**
     * @property
     * @lends AddAddress
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'oldPassword',
        'newPassword',
        'confirmPassword'
      ])
    },

    /**
     * Dont change the password, blank the input fields.
     */
    cancel: {
      value: function cancel () {
        this.oldPassword(null);
        this.newPassword(null);
        this.confirmPassword(null);
        history.pushState(null, null, 'account', true);
      }
    },

    /**
     * Updates a user password on the server.
     */
    changePassword: {
      value: function changePassword () {
        var account = {
          oldPassword: this.oldPassword(),
          newPassword: this.newPassword(),
          confirmPassword: this.confirmPassword()
        };

        return profileServices.changePassword(account).then(function (response) {
          this.oldPassword(null);
          this.newPassword(null);
          this.confirmPassword(null);

          if (!response.formError) {
            toastr.success(i18next.t('account.alert.changePasswordSuccess'));
          }
        }.bind(this));
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
        var viewModel = new AccountChangePassword().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    AccountChangePassword: AccountChangePassword
  };
});
