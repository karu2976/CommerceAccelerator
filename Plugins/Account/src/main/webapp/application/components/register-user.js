/**
 * Render for the RegisterUser cartridge. Allows a user to register an account.
 *
 * @module csa/plugins/account/application/components/register-user
 * @requires module:browser
 * @requires module:libs
 * @requires module:content-item-mixin
 * @requires module:text!./register-user.html
 * @requires module:profile-services
 */
define([
  'browser',
  'libs',
  'content-item',
  'text!./register-user.html',
  'profile-services'
], function (browser, libs, ContentItem, template, profileServices) {
  'use strict';

  var history = browser.history;
  var _ = libs._;
  var $ = libs.$;
  var i18next = libs.i18next;
  var toastr = libs.toastr;

  function RegisterUser() {
    ContentItem.apply(this, arguments);

    this.isAccountBenefitsModalVisible(false);

    // Check if there's an email address supplied in the pushState. If there is then set it as the
    // emailAddress so the field is prepopulated. This means that this cartridge was loaded from
    // another page where a user entered their email address. See Login.js.
    if (history.state) {
      var registerEmail = history.state.email;
      if (registerEmail) {
        this.emailAddress(registerEmail);
      }
    }
  }

  // Define class prototype
  RegisterUser.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends RegisterUser
     */
    constructor: {
      value: RegisterUser
    },

    /**
     * @property
     * @lends RegisterUser
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'isAccountBenefitsModalVisible',
        'emailAddress',
        'password',
        'confirmPassword',
        'firstName',
        'lastName',
        'postalCode',
        'gender',
        'dateOfBirth',
        'placeholderText'
      ])
    },

    /**
     * Listener for select saved address event.
     */
    showAccountBenefits: {
      value: function showRecoverPassword () {
        this.isAccountBenefitsModalVisible(true);
      }
    },

    /**
     * Cancel creating an account, blank the register user form fields and redirect to the login
     * page.
     */
    cancel:{
      value: function cancel () {
        this.emailAddress(null);
        this.password(null);
        this.confirmPassword(null);
        this.firstName(null);
        this.lastName(null);
        this.postalCode(null);
        this.gender(null);
        this.dateOfBirth(null);

        history.pushState(null, null, 'login', true);
      }
    },

    /**
     * Creates a user account on the server then redirect the user.
     */
    createAccount: {
      value: function createAccount () {

        var account = {
          login: this.emailAddress(),
          email: this.emailAddress(),
          password: this.password(),
          confirmPassword: this.confirmPassword(),
          firstName: this.firstName(),
          lastName: this.lastName(),
          postalCode: this.postalCode(),
          gender: this.gender(),
          birthday: this.dateOfBirth()
        };

        return profileServices.create(account).then(function (response) {
          // Make sure there are no form errors, indicating success.
          if (!response.formError) {
            history.pushState(null, null, 'account', true);
            toastr.success(i18next.t('account.alert.accountSuccess'));
          }
          else {
            _.each(response.formExceptions, function (ex) {

              switch (ex.errorCode){
                case 'invalidDateFormat':
                  var $dateOfBirth = $('[data-name="dateOfBirth"]');
                  $dateOfBirth.setCustomValidity(ex.localizedMessage);
                  $dateOfBirth.trigger('updatevalidation');

                  $dateOfBirth.on('input', function () {
                    this.setCustomValidity('');
                  });

                  break;

                case 'userAlreadyExists':
                  var $emailAddress = $('[data-name="emailAddress"]');
                  $emailAddress.setCustomValidity(ex.localizedMessage);
                  $emailAddress.trigger('updatevalidation');

                  $emailAddress.on('input', function () {
                    this.setCustomValidity('');
                  });
              }
            });
          }
        });
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
        var viewModel = new RegisterUser().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    RegisterUser: RegisterUser
  };
});
