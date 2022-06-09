/**
 * Render for the ForgotPassword cartridge. Allows a user to reset a forgotten password.
 *
 * @module csa/plugins/account/application/components/forgot-password
 * @requires module:libs
 * @requires module:content-item
 * @requires module:text!./forgot-password.html
 * @requires module:profile-services
 */
define([
  'libs',
  'content-item',
  'text!./forgot-password.html',
  'profile-services'
], function (libs, ContentItem, template, profileServices) {
  'use strict';

  var $ = libs.$;
  var ko = libs.ko;

  function ForgotPassword() {
    ContentItem.apply(this, arguments);

    // Define instance properties
    Object.defineProperties(this, {
      emailAddress: {
        value: ko.observable(),
        enumerable: true
      }
    });
  }

  // Define class prototype
  ForgotPassword.prototype = Object.create(ContentItem.prototype, {
    /**
     * @property
     * @lends ForgotPassword
     */
    constructor: {
      value: ForgotPassword
    },

    /**
     * Recovers a user password.
     */
    recoverPassword: {
      value: function recoverPassword () {
        return profileServices.recoverPassword(this.emailAddress()).then(function (response) {
          if (!response.formError) {
            $('[data-name="recoverPassword"]').modal('hide');
            $('[data-name="recoverPasswordCompleted"]').modal('show');
            this.emailAddress('');
          }
        });
      }
    }
  });

  // The component defintion
  return {

    // The component HTML template.
    template: template,

    // The component view model factory.
    viewModel: {
      createViewModel: function createViewModel (params) {
        var viewModel = new ForgotPassword().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    ForgotPassword: ForgotPassword
  };
});
