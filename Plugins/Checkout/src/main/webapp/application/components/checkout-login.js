/**
 * The CheckoutLogin component provides the UI for Login page during checkout for anonymous user.
 * Allows a previously registered user to login, or checkout as anonymous user.
 *
 * @module csa/plugins/checkout/application/components/checkout-login
 * @requires module:content-item
 * @requires module:checkout-services
 * @requires module:profile
 * @requires module:profile-services
 * @requires module:text!./checkout-login.html
 */
define([
  'content-item',
  'checkout-services',
  'profile',
  'profile-services',
  'text!./checkout-login.html',
], function (ContentItem, checkoutServices, profile, profileServices, template) {
  'use strict';

  function forgotPasswordFactory () {
    return new ContentItem().set({
      '@type': 'ForgotPassword'
    });
  }

  function CheckoutLogin() {
    ContentItem.apply(this, arguments);

    // Set property defaults.
    this.isRecoverPasswordModalVisible(false);
    this.isRecoverPasswordCompleteModalVisible(false);
  }

  // Define class prototype
  CheckoutLogin.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends CheckoutLogin
     */
    constructor: {
      value: CheckoutLogin
    },

    /**
     * @property
     * @lends CheckoutLogin
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'isRecoverPasswordModalVisible',
        'isRecoverPasswordCompleteModalVisible',
        'emailAddress',
        'password',
        'username'
      ])
    },

    /**
     * @property
     * @lends Login
     */
    associations: {
      value: ContentItem.prototype.associations.concat([
        {
          name: 'forgotPassword',
          factory: forgotPasswordFactory
        }
      ])
    },

    /**
     * Listener for select saved address event.
     */
    showRecoverPassword: {
      value: function showRecoverPassword () {
        this.isRecoverPasswordModalVisible(true);
      }
    },

    /**
     * This method takes email address and if email address is valid.
     * Then redirects from checkout login page to checkout page.
     */
    guestCheckout: {
      value: function guestCheckout () {
        if (!(this.emailAddress() === undefined || this.emailAddress() === '')) {
          checkoutServices.guestCheckout(this.emailAddress()).then(function (response) {
            if (!response.formError) {
              window.location.href = 'checkout';
            }
          });
        }
      }
    },

    /**
     * This method takes user name and password. Login will be successful,
     * if they are already registered and user name and password are correct.
     */
    login: {
      value: function login () {
        if (this.username() && this.password()) {

          // Create data argument for login service.
          var credentials = {
            login: this.username(),
            password: this.password()
          };

          profileServices.login(credentials).then(function (response) {

            // Remove the credentials
            this.password(null);
            this.username(null);

            if (profile.authenticated() && !response.formError) {
              window.location.href = 'checkout';
            }

          }.bind(this));
        }
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
        var viewModel = new CheckoutLogin().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    CheckoutLogin: CheckoutLogin
  };
});
