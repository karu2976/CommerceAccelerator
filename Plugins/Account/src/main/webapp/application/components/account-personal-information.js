/**
 * Render for the AccountPersonalInformation cartridge. Lets a user update their profile
 * information.
 *
 * @module csa/plugins/account/application/components/account-personal-information
 * @requires module:content-item
 * @requires module:text!./account-personal-information.html
* @requires module:profile
 * @requires module:profile-services
 * @requires module:libs
 */
define([
  'content-item',
  'text!./account-personal-information.html',
  'profile',
  'profile-services',
  'libs'
], function (ContentItem, template, profile, profileServices, libs) {
  'use strict';

  var $ = libs.$;
  var i18next = libs.i18next;
  var toastr = libs.toastr;

  function AccountPersonalInformation() {
    ContentItem.apply(this, arguments);
  }

  // Define class prototype
  AccountPersonalInformation.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends AccountPersonalInformation
     */
    constructor: {
      value: AccountPersonalInformation
    },

    /**
     * @property
     * @lends AccountMenuNavigation
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'placeholderText'
      ])
    },

    /**
     * Updates a user account on the server then redirect the user.
     */
    updateProfile: {
      value: function updateProfile () {

        // Fields to update
        var account = {
          login: this.profile.login(),
          email: this.profile.login(),
          firstName: this.profile.firstName(),
          lastName: this.profile.lastName(),
          postalCode: this.profile.postalCode(),
          gender: this.profile.gender(),
          birthday: this.profile.dateOfBirth()
        };

        profileServices.updateProfile(account).then(function (response) {
          if (!response.formError) {
            toastr.success(i18next.t('account.alert.editProfileSuccess'));
          }
          else {
            response.formExceptions.forEach(function (ex) {
              switch (ex.errorCode){
                case 'invalidDateFormat':
                  var $dateOfBirth = $('.csa-account-dob');
                  $dateOfBirth[0].setCustomValidity(ex.localizedMessage);
                  $dateOfBirth[0].checkValidity();

                  $dateOfBirth[0].oninput = function () {
                    this.setCustomValidity('');
                  };

                  break;
              }
            });
          }
        });
      }
    },

    /**
     * Don't update the profile, reload the original values. The original values are retrieved from
     * the server.
     */
    viewProfile: {
      value: function viewProfile (form) {
        return profileServices.viewProfile().done(function (response) {
          if (!response.formError) {
            $(form).removeClass('dirty');
          }
        });
      }
    },

    /**
     * @property
     * @lends AccountPersonalInformation
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
        var viewModel = new AccountPersonalInformation().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    AccountPersonalInformation: AccountPersonalInformation
  };
});
