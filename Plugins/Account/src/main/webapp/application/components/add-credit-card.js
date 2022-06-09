/**
 * Render for the AddCreditCard cartridge. Allows a user to add a credit card to their list of
 * profile credit cards.
 *
 * @module csa/plugins/account/application/components/add-credit-card
 * @requires module:content-item
 * @requires module:text!./add-credit-card.html
 * @requires module:location-services
 * @requires module:profile-services
 * @requires module:libs
 */
define([
  'content-item',
  'text!./add-credit-card.html',
  'profile',
  'location-services',
  'profile-services',
  'libs'
], function (ContentItem, template, profile, locationServices, profileServices, libs) {
  'use strict';

  var _ = libs._;
  var $ = libs.$;
  var i18next = libs.i18next;
  var toastr = libs.toastr;

  function AddCreditCard() {
    ContentItem.apply(this, arguments);

    // defaults
    this.selectedAddressIndex(0);
    this.setAsDefaultCard(false);
    this.formPopulatedFromSavedAddress(false);
    this.isSavedAddressModalVisible(false);
  }

  // Define class prototype
  AddCreditCard.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends AddCreditCard
     */
    constructor: {
      value: AddCreditCard
    },

    /**
     * @property
     * @lends AddCreditCard
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'setAsDefaultCard',
        'creditCardNumber',
        'creditCardType',
        'expirationMonth',
        'expirationYear',
        'cardNickname',
        'firstName',
        'lastName',
        'address1',
        'address2',
        'postalCode',
        'city',
        'state',
        'country',
        'phoneNumber',
        'selectedAddressIndex',
        'isSavedAddressModalVisible',
        'formPopulatedFromSavedAddress'
      ])
    },

    /**
     * @property
     * @lends AddCreditCard
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'states',
        'countries',
        'expiryYears'
      ])
    },

    /**
     * @property
     * @lends AddCreditCard
     */
    profile: {
      value: profile,
      enumerated: true
    },

    /**
     * Adds an card to the profile then redirect the user.
     *
     * @function
     * @lends AddCreditCard
     * @param {Object} newData An object of new data values for the model properties.
     */
    createCard: {
      value: function createCard () {

        var card = {
          cardNickname: this.cardNickname(),
          creditCardType: this.creditCardType(),
          creditCardNumber: this.creditCardNumber(),
          expirationMonth: this.expirationMonth(),
          expirationYear: this.expirationYear(),
          setAsDefaultCard: this.setAsDefaultCard(),
          firstName: this.firstName(),
          lastName: this.lastName(),
          address1: this.address1(),
          address2: this.hasOwnProperty('address2') ? this.address2() : null, // Non required
          city: this.city(),
          state: this.state(),
          country: this.country(),
          postalCode: this.postalCode(),
          phoneNumber: this.phoneNumber()
        };

        profileServices.addCard(card).then(function (response) {
          if (!response.formError) {
            window.history.pushState(null, null, 'account/billing', true);
            toastr.success(i18next.t('account.alert.addCardSuccess'));
          }
        });
      }
    },

    /**
     * Determine the card type from the card number. Visa starts with 4, Master card starts with
     * 51 through 55. Amex starts with 34 or 37 and Discover starts with 6011.
     *
     * @function
     * @lends AddCreditCard
     * @param {Object} newData An object of new data values for the model properties.
     */
    setCardType: {
      value: function setCardType () {
        $('.csa-cc').removeClass('mc vc ac dc');
        $('.csa-cc').next('div.form-group').removeClass('has-error has-feedback');

        var currentVal = this.creditCardNumber();
        if (!currentVal) {
          $('.csa-cc').removeClass('mc vc ac dc');
          return;
        }

        if (currentVal.match(/^5[1-5]\d{0,14}$/)) {
          //MC
          $('.csa-cc').addClass('mc');
          this.creditCardType('mastercard');
        }
        else if (currentVal.match(/^4\d{0,15}/) || currentVal.match(/^4\d{0,12}/)) {
          //Visa
          $('.csa-cc').addClass('vc');
          this.creditCardType('visa');
        }
        else if (currentVal.match(/^3[47]\d{0,13}/)) {
          //Amex
          $('.csa-cc').addClass('ac');
          this.creditCardType('amex');
        }
        else if (currentVal.match(/^6011\d{0,12}/)) {
          //Discover
          $('.csa-cc').addClass('dc');
          this.creditCardType('discover');
        }
        else if (currentVal.length > 3) {
          // at least 4 characters have been entered and
          // no match therefore number entered is invalid
          $('.csa-cc').removeClass('mc vc ac dc');
          $('.csa-cc').next('div.form-group').addClass('has-error has-feedback');
          this.creditCardType('');
        }
      }
    },

    /**
     * Display the localized options caption for the select state dropdown.
     *
     * @function
     * @lends AddCreditCard
     * @param {Object} newData An object of new data values for the model properties.
     */
    selectStateOptionsText: {
      value: function selectStateOptionsText () {
        return i18next.t('account.text.selectState');
      }
    },

    /**
     * Display the localized options caption for the select state dropdown.
     *
     * @function
     * @lends AddCreditCard
     * @param {Object} newData An object of new data values for the model properties.
     */
    selectCountryOptionsText: {
      value: function selectStateOptionsText () {
        return i18next.t('account.text.selectCountry');
      }
    },

    /**
     * Display the localized options caption for the select year dropdown.
     *
     * @function
     * @lends AddCreditCard
     * @param {Object} newData An object of new data values for the model properties.
     */
    selectYearOptionsText: {
      value: function selectYearOptionsText () {
        return i18next.t('account.text.selectYear');
      }
    },

    /**
     * Listener for select saved address event.
     *
     * @function
     * @lends AddCreditCard
     * @param {Object} newData An object of new data values for the model properties.
     */
    showSelectSavedAddress: {
      value: function showSelectSavedAddress () {
        this.isSavedAddressModalVisible(true);
      }
    },

    /**
     * Used to lazily load the addresses associated with the profile for selection as the billing
     * address. This is invoked on expansion of the 'Add' a new payment method accordion.
     *
     * @function
     * @lends AddCreditCard
     * @param {Object} newData An object of new data values for the model properties.
     */
    getProfileAddresses: {
      value: function getProfileAddresses () {
        profileServices.viewAddresses().then(function () {

          // Set default selected address to be the profile default
          _.each(this.profile.addresses().secondaryAddresses, function (address, index) {
            if (address.useAsDefaultShippingAddress === true) {
              this.selectedAddressIndex(index);
              return;
            }
          }.bind(this));

        }.bind(this));
      }
    },

    /**
     * getStatesForCountry makes a request to the server for the states belonging to the selected
     * country. It then populates the address viewmodel (passed in as data) states observable with
     * the returned list of states. Additionally reset the currently selected state.
     *
     * @function
     * @lends AddCreditCard
     * @param {Object} newData An object of new data values for the model properties.
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
     * Copies a selected address' fields into the billing address inputs.
     *
     * @function
     * @lends AddCreditCard
     * @param {Object} newData An object of new data values for the model properties.
     */
    copyAddressFields: {
      value: function copyAddressFields () {
        // Get the selected address data from the addresses array.
        var data = this.profile.addresses().secondaryAddresses[this.selectedAddressIndex()];

        if (!data) {
          return;
        }

        // Update states for a change in country. The data is also mapped in here and formPopulatedFromSavedAddress
        // so that the field validation checks wait until all the values have been mapped to their fields (after the
        // callback completes).
        if (data.country !== this.country) {
          locationServices.getStatesForCountry(data.country).then(function (response) {
            this.states(response.states);
            this.setAddress(data);
            this.formPopulatedFromSavedAddress(true);
          }.bind(this));
        }
        else {
          // Populate the address fields and set formPopulatedFromSavedAddress to true so
          // validation checks can be performed to remove any previous error messages.
          this.setAddress(data);
          this.formPopulatedFromSavedAddress(true);
        }
      }
    },

    /**
     * Sets the models address observables with the data passed to the function.
     *
     * @function
     * @lends AddCreditCard
     * @param {Object} newData An object of new data values for the model properties.
     */
    setAddress: {
      value: function setAddress (data) {
        if (data) {
          this.firstName(data.firstName);
          this.lastName(data.lastName);
          this.address1(data.address1);
          if (data.address2) {
            this.address2(data.address2);
          }
          this.postalCode(data.postalCode);
          this.city(data.city);
          this.state(data.state);
          this.country(data.country);
          this.phoneNumber(data.phoneNumber);
        }
      }
    },

    /**
     * Cancel adding a credit card.
     *
     * @function
     * @lends AddCreditCard
     * @param {Object} newData An object of new data values for the model properties.
     */
    cancel: {
      value: function cancel () {
        window.history.pushState(null, null, 'account/billing', true);
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
        var viewModel = new AddCreditCard().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    AddCreditCard: AddCreditCard
  };
});
