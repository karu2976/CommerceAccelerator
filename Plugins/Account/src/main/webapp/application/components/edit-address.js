/**
 * Render for the EditAddress cartridge. Allows a user to edit a single address associated
 * with their profile.
 *
 * @module csa/plugins/account/application/components/edit-address
 * @requires module:browser
 * @requires module:libs
 * @requires module:content-item
 * @requires module:text!./edit-address.html
 * @requires module:location-services
 * @requires module:profile-services
 */
define([
  'browser',
  'libs',
  'content-item',
  'text!./edit-address.html',
  'location-services',
  'profile-services'
], function (browser, libs, ContentItem, template, locationServices, profileServices) {
  'use strict';

  var history = browser.history;
  var i18next = libs.i18next;
  var toastr = libs.toastr;

  function EditAddress() {
    ContentItem.apply(this, arguments);
  }

  // Define class prototype
  EditAddress.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends EditAddress
     */
    constructor: {
      value: EditAddress
    },

    /**
     * @property
     * @lends EditAddress
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
     * @lends EditAddress
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'states',
        'countries'
      ])
    },

    /**
     * When updating the address we require the existing nickname so the address can be identifed
     * and the new nickname. This field represents the existing nickname.
     */
    addressDisplayName: {
      value: '',
      writable: true
    },

    /**
     * getStatesForCountry makes a request to the server for the states belonging to the selected
     * country. It then populates the address viewmodel (passed in as data) states observable with
     * the returned list of states. The currently selected state will be reset.
     */
    getStatesForCountry: {
      value: function getStatesForCountry (data) {
        var selectedCountry = data.country();
        locationServices.getStatesForCountry(selectedCountry).then(function (response) {
          data.states(response.states);
          data.state('');
        });
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
     * Updates the address on the profile then redirect the user.
     */
    updateAddress: {
      value: function updateAddress () {

        var address = {
          addressNickname: this.addressDisplayName,
          newAddressNickname: this.addressNickname(),
          firstName: this.firstName(),
          lastName: this.lastName(),
          address1: this.address1(),
          address2: this.hasOwnProperty('address2') ? this.address2() : null, // Non required
          city: this.city(),
          state: this.state(),
          country: this.country(),
          postalCode: this.postalCode(),
          phoneNumber: this.phoneNumber(),
          useAsDefaultShippingAddress: this.useAsDefaultShippingAddress()
        };

        return profileServices.updateAddress(address).then(function (response) {
          if (!response.formError) {
            history.pushState(null, null, 'account/addressbook', true);
            toastr.success(i18next.t('account.alert.editAddressSuccess'));
          }
        });
      }
    },

    /**
     * Cancel editing an address.
     */
    cancel: {
      value: function cancel () {
        history.pushState(null, null, 'account/addressbook', true);
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
        var viewModel = new EditAddress().init(params);

        //TODO: review this line...
        viewModel.addressDisplayName = viewModel.addressNickname();

        return viewModel;
      }
    },

    // The component view model class.
    EditAddress: EditAddress
  };
});
