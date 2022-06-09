/**
 * Render for the Login cartridge. Allows a user to register an account, or a previously registered
 * user to login.
 *
 * @module csa/plugins/account/application/components/Login
 * @requires module:browser
 * @requires module:libs
 * @requires module:content-item
 * @requires module:text!./login.html
 * @requires module:profile-services
 */
define([
  'browser',
  'libs',
  'content-item',
  'text!./login.html',
  'profile-services'
], function (browser, libs, ContentItem, template, profileServices) {
  'use strict';

  var history = browser.history;
  var location = browser.location;
  var $ = libs.$;

  function forgotPasswordFactory () {
    return new ContentItem().set({
      '@type': 'ForgotPassword'
    });
  }

  function Login() {
    ContentItem.apply(this, arguments);

    // Set property defaults.
    this.isRecoverPasswordModalVisible(false);
    this.isRecoverPasswordCompleteModalVisible(false);
  }

  // Define class prototype
  Login.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends Login
     */
    constructor: {
      value: Login
    },

    /**
     * @property
     * @lends Login
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'isRecoverPasswordModalVisible',
        'isRecoverPasswordCompleteModalVisible',
        'newUser',
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
     * Call the login service to log the user in, passing in the credentials entered in the form
     * fields. The credentials are passed using the state parameter of the pushState.
     *
     * @function
     * @lends Login
     */
    register: {
      value: function register () {
        var email = this.newUser();

        return profileServices.emailCheck(email).then(function (response) {
          if (!response.formError) {
            history.pushState({email: email}, null, 'account/register', true);
          }
          else {
            response.formExceptions.forEach(function (exception) {

              switch (exception.errorCode){
                case 'userAlreadyExists':
                  var $registerEmailAddress = $('[data-name="registerEmailAddress"]');
                  $registerEmailAddress.setCustomValidity(exception.localizedMessage);
                  $registerEmailAddress.trigger('updatevalidation');

                  $registerEmailAddress.on('input', function () {
                    this.setCustomValidity('');
                  });

                  break;
              }
            });
          }
        });
      }
    },

    /**
     * Call the login service to log the user in, passing in the credentials entered in the form
     * fields.
     *
     * @function
     * @lends Login
     */
    login: {
      value: function login () {

        // Create data argument for login service.
        var credentials = {
          login: this.username(),
          password: this.password()
        };

        profileServices.login(credentials).then(function (response) {

          // Remove the credentials
          this.password(null);
          this.username(null);

          if (!response.formError) {

            // Go to home on successful log in.
            location.href = 'home';
          }
        }.bind(this));
      }
    },

    /**
     * Listener for select saved address event.
     *
     * @function
     * @lends Login
     */
    showRecoverPassword: {
      value: function showRecoverPassword () {
        this.isRecoverPasswordModalVisible(true);
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
        var viewModel = new Login().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    Login: Login
  };
});
