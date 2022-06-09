/**
 * Render for the AddAddress cartridge. Allows a user to add an address to their list of
 * profile addresses.
 *
 * @module csa/plugins/account/application/components/add-address
 * @requires module:content-item
 * @requires module:text!./add-address.html
 * @requires module:location-services
 * @requires module:profile-services
 * @requires module:libs
 */
define([
  'content-item',
  'text!./add-address.html',
  'location-services',
  'profile-services',
  'libs'
], function (ContentItem, template, locationServices, profileServices, libs) {
  'use strict';

  var i18next = libs.i18next;
  var toastr = libs.toastr;

  function AddAddress() {
    ContentItem.apply(this, arguments);

    // defaults
    this.useAsDefaultShippingAddress(false);
  }

  // Define class prototype
  AddAddress.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends AddAddress
     */
    constructor: {
      value: AddAddress
    },

    /**
     * @property
     * @lends AddAddress
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'addressNickname',
        'lastName',
        'useAsDefaultShippingAddress',
        'state',
        'address1',
        'address2',
        'country',
        'city',
        'postalCode',
        'phoneNumber',
        'firstName'
      ])
    },

    /**
     * @property
     * @lends AddAddress
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'states',
        'countries'
      ])
    },

    /**
     * getStatesForCountry makes a request to the server for the states belonging to the selected
     * country. It then populates the address viewmodel (passed in as data) states observable with
     * the returned list of states.
     */
    getStatesForCountry: {
      value: function getStatesForCountry (data) {
        var selectedCountry = data.country();
        locationServices.getStatesForCountry(selectedCountry).then(function (response) {
          this.states(response.states);
          this.state('');
        }.bind(this));
      }
    },

    /**
     * Display the localized options caption for the select state dropdown.
     */
    selectStateOptionsText: {
      value: function selectStateOptionsText () {
        return i18next.t('account.text.selectState');
      }
    },

    /**
     * Display the localized options caption for the select state dropdown.
     */
    selectCountryOptionsText: {
      value: function selectStateOptionsText () {
        return i18next.t('account.text.selectCountry');
      }
    },

    /**
     * Adds an address to the profile then redirect the user.
     */
    addAddress: {
      value: function addAddress () {

        var address = {
          addressNickname: this.addressNickname(),
          firstName: this.firstName(),
          lastName: this.lastName(),
          address1: this.address1(),
          address2: this.address2(),
          city: this.city(),
          state: this.state(),
          country: this.country(),
          postalCode: this.postalCode(),
          phoneNumber: this.phoneNumber(),
          useAsDefaultShippingAddress: this.useAsDefaultShippingAddress()
        };

        return profileServices.addAddress(address).then(function (response) {
          if (!response.formError) {
            window.history.pushState(null, null, 'account/addressbook', true);
            toastr.success(i18next.t('account.alert.addAddressSuccess'));
          }
        });
      }
    },

    /**
     * Cancel adding an address.
     */
    cancel: {
      value: function cancel () {
        window.history.pushState(null, null, 'account/addressbook', true);
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
        var viewModel = new AddAddress().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    AddAddress: AddAddress
  };
});
