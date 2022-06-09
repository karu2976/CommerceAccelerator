/**
 * Render for the EditCreditCard cartridge. Allows a user to edit a credit card in their list of
 * profile credit cards.
 *
 * @module csa/plugins/account/application/components/edit-credit-card
 * @requires module:content-item
 * @requires module:text!./edit-credit-card.html
 * @requires module:location-services
 * @requires module:profile-services
 * @requires module:libs
 */
define([
  'content-item',
  'text!./edit-credit-card.html',
  'profile',
  'location-services',
  'profile-services',
  'libs'
], function (ContentItem, template, profile, locationServices, profileServices, libs) {
  'use strict';

  var _ = libs._;
  var i18next = libs.i18next;
  var toastr = libs.toastr;

  function EditCreditCard() {
    ContentItem.apply(this, arguments);

    // defaults
    this.selectedAddressIndex(0);
    this.setAsDefaultCard(false);
    this.formPopulatedFromSavedAddress(false);
    this.isSavedAddressModalVisible(false);
  }

  // Define class prototype
  EditCreditCard.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends EditCreditCard
     */
    constructor: {
      value: EditCreditCard
    },

    /**
     * @property
     * @lends EditCreditCard
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'setAsDefaultCard',
        'creditCardNumber',
        'creditCardType',
        'expirationMonth',
        'expirationYear',
        'cardNickname',
        'cardDisplayName',
        'billingAddress',
        'selectedAddressIndex',
        'isSavedAddressModalVisible',
        'formPopulatedFromSavedAddress'
      ])
    },

    /**
     * @property
     * @lends EditCreditCard
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'countries',
        'expiryYears'
      ])
    },

    /**
     * @property
     * @lends EditCreditCard
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'setCardType'
      ])
    },

    /**
     * Override the set function to ensure that the cardDisplayName property is set
     * after the the rest of the poperties.
     *
     * @function
     * @lends EditCreditCard
     * @param {Object} newData An object of new data values for the model properties.
     */
    set: {
      value: function set (newData) {
        ContentItem.prototype.set.call(this, newData);

        this.cardDisplayName(newData.cardNickname);
        return this;
      }
    },

    /**
     * @property
     * @lends EditCreditCard
     */
    profile: {
      value: profile,
      enumerated: true
    },

    /**
     * Updates the address on the profile then redirect the user.
     *
     * @function
     * @lends EditCreditCard
     */
    updateCard: {
      value: function updateCard () {

        var card = {
          setAsDefaultCard: this.setAsDefaultCard(),
          cardNickname: this.cardDisplayName(),
          newCardNickname: this.cardNickname(),
          expirationMonth: this.expirationMonth(),
          expirationYear: this.expirationYear(),
          firstName: this.billingAddress().firstName,
          lastName: this.billingAddress().lastName,
          address1: this.billingAddress().address1,
          address2: this.billingAddress().hasOwnProperty('address2') ? this.billingAddress.address2 : null, // Non required
          city: this.billingAddress().city,
          state: this.billingAddress().state,
          country: this.billingAddress().country,
          postalCode: this.billingAddress().postalCode,
          phoneNumber: this.billingAddress().phoneNumber
        };

        return profileServices.updateCard(card).then(function (response) {
          if (!response.formError) {
            window.history.pushState(null, null, 'account/billing', true);
            toastr.success(i18next.t('account.alert.editCardSuccess'));
          }
        });
      }
    },

    /**
     * Apply the correct styles to indicate the card type.
     *
     * @function
     * @lends EditCreditCard
     */
    setCardType: {
      value: function () {

        var cardType = this.creditCardType();

        if (cardType === 'mastercard') {
          return 'mc';
        }
        else if (cardType === 'visa') {
          return 'vc';
        }
        else if (cardType === 'amex') {
          return 'ac';
        }
        else if (cardType === 'discover') {
          return 'dc';
        }
      },
      writable: true
    },

    /**
     * Display the localized options caption for the select state dropdown.
     *
     * @function
     * @lends EditCreditCard
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
     * @lends EditCreditCard
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
     * @lends EditCreditCard
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
     * @lends EditCreditCard
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
     * @lends EditCreditCard
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
     * @lends EditCreditCard
     */
    getStatesForCountry: {
      value: function getStatesForCountry (data) {
        var selectedCountry = data.billingAddress().country;
        locationServices.getStatesForCountry(selectedCountry).then(function (response) {
          var address = data.billingAddress();
          address.states = response.states;
          address.state = '';
          data.billingAddress(address);
        });
      }
    },

    /**
     * Copies a selected address' fields into the billing address inputs.
     *
     * @function
     * @lends EditCreditCard
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
        if (data.country !== this.billingAddress().country) {
          locationServices.getStatesForCountry(data.country).then(function (response) {
            data.states = response.states;
            this.billingAddress(data);
            this.formPopulatedFromSavedAddress(true);
          }.bind(this));
        }
        else {
          // Populate the address fields and set formPopulatedFromSavedAddress to true so
          // validation checks can be performed to remove any previous error messages.
          data.states = this.billingAddress().states;
          this.billingAddress(data);
          this.formPopulatedFromSavedAddress(true);
        }
      }
    },

    /**
     * Cancel adding a credit card.
     *
     * @function
     * @lends EditCreditCard
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
        var viewModel = new EditCreditCard().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    EditCreditCard: EditCreditCard
  };
});
