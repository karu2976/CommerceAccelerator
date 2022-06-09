/**
 * The BillingAddressSelector component provides the UI on checkout page for
 * inputting billing address details.
 *
 * @module csa/plugins/checkout/application/components/billing-address-selector
 * @requires module:content-item
 * @requires module:checkout-details
 * @requires module:checkout-services
 * @requires module:text!./billing-address-selector.html
 * @requires module:libs
 */
define([
  'content-item',
  'checkout-details',
  'checkout-services',
  'location-services',
  'text!./billing-address-selector.html',
  'libs'
], function (ContentItem, checkoutDetails, checkoutServices, locationServices, template, libs) {
  'use strict';

  var $ = libs.$;
  var i18next = libs.i18next;
  var ko = libs.ko;

  function BillingAddressSelector() {
    ContentItem.apply(this, arguments);

    this.isSavedAddressModalVisible(false);

    /**
     * When any billing address details change
     * update the value of isBillingAddressValid
     */
    this.updateBillingAddressValidation = ko.computed(function () {
      // All billing address observables are added here so
      // this computed will be triggered if any of them change
      this.checkoutDetails.billingNickName();
      this.checkoutDetails.billingFirstName();
      this.checkoutDetails.billingLastName();
      this.checkoutDetails.billingAddress1();
      this.checkoutDetails.billingAddress2();
      this.checkoutDetails.billingCountry();
      this.checkoutDetails.billingCity();
      this.checkoutDetails.billingState();
      this.checkoutDetails.billingPostalCode();
      this.checkoutDetails.billingPhoneNumber();
      this.checkoutDetails.billingEmail();
      this.checkoutDetails.isBillingAddressSameAsShippingAddress();

      if (this.checkoutDetails.hasBillingAddressBeenValidated()) {
        // Any changes to the billing address after validation
        // makes the billing address section invalid.
        this.checkoutDetails.isBillingAddressValid(false);
      }
    }.bind(this));

    /**
     * If the billing address country is changed then update the
     * billing address states with values for the selected country
     */
    this.updateBillingAddressStates = ko.computed(function () {
      var billingCountry = this.checkoutDetails.billingCountry();
      if (billingCountry) {
        locationServices.getStatesForCountry(billingCountry).then(function (response) {
          this.checkoutDetails.billingStates(response.states);

          // check if the currently selected state is in the new state list and only
          // clear billingAddress().billingState if its value is not in the new list
          // peek() returns the current value of the observable without creating a dependency
          var selectedBillingAddressStateCode = this.checkoutDetails.billingState.peek();

          for (var i = 0, len = response.states.length; i < len; i++) {
            if (response.states[i].code === selectedBillingAddressStateCode) {
              return;
            }
          }

          this.checkoutDetails.billingState('');

        }.bind(this));
      }
    }.bind(this));

    /**
     * Subscribe to checkoutDetails.isCreditCardSelectedFromProfile observable changes
     *
     * If the payment method (credit card) is one that has
     * previously been saved to the the profile then the billing
     * address will the address associated with that payment method.
     * If a new payment method has been entered then clear the
     * billing address details.
     */
    this.checkoutDetails.isCreditCardSelectedFromProfile.subscribe(function (isCreditCardSelectedFromProfile) {
      if (!isCreditCardSelectedFromProfile) {
        this.clearBillingAddress();
      }
    }.bind(this));

    /**
     * Subscribe to checkoutDetails.isBillingAddressSameAsShippingAddress observable changes
     *
     * Update the billing address details when the same as shipping address value changes
     */
    this.checkoutDetails.isBillingAddressSameAsShippingAddress.subscribe(function (isBillingAddressSameAsShippingAddress) {
      if (isBillingAddressSameAsShippingAddress) {
        this.checkoutDetails.billingNickName(this.checkoutDetails.nickName());
        this.checkoutDetails.billingFirstName(this.checkoutDetails.shippingFirstName());
        this.checkoutDetails.billingLastName(this.checkoutDetails.shippingLastName());
        this.checkoutDetails.billingAddress1(this.checkoutDetails.shippingAddress1());
        this.checkoutDetails.billingAddress2(this.checkoutDetails.shippingAddress2());
        this.checkoutDetails.billingCountry(this.checkoutDetails.shippingCountry());
        this.checkoutDetails.billingCity(this.checkoutDetails.shippingCity());
        this.checkoutDetails.billingState(this.checkoutDetails.shippingState());
        this.checkoutDetails.billingPostalCode(this.checkoutDetails.shippingPostalCode());
        this.checkoutDetails.billingPhoneNumber(this.checkoutDetails.shippingPhoneNumber());
        this.checkoutDetails.billingEmail(this.checkoutDetails.shippingEmail());
        $('[data-name="billToAddress-form"]').trigger('resetvalidation');
      }
      else {
        if (!this.checkoutDetails.isBillingAddressSelectedFromProfile()) {
          this.clearBillingAddress();
        }
      }
    }.bind(this));

    /**
     * Subscribe to checkoutDetails.hasBillingAddressBeenValidated observable changes
     *
     * When false clear all billing address details and set isBillingAddressValid to false
     * Will be set to false when the checkout page is first loaded
     */
    this.checkoutDetails.hasBillingAddressBeenValidated.subscribe(function (hasBillingAddressBeenValidated) {
      if (!hasBillingAddressBeenValidated) {
        this.clearBillingAddress();
        this.checkoutDetails.isBillingAddressValid(false);
      }
    }.bind(this));

    // Set the default billing address country
    this.checkoutDetails.billingCountry(this.checkoutDetails.countryBasedOnLocale);
  }

  // Define class prototype
  BillingAddressSelector.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends BillingAddressSelector
     */
    constructor: {
      value: BillingAddressSelector
    },

    /**
     * @property
     * @lends BillingAddressSelector
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'isSavedAddressModalVisible'
      ])
    },

    /**
     * @property
     * @lends BillingAddressSelector
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'validationIconClass'
      ])
    },

    /**
     * The CSS class to display the correct icon based on the values
     * of hasBillingAddressBeenValidated and isBillingAddressValid
     */
    validationIconClass: {
      value: function () {
        var retVal = 'glyphicon-question-sign text-info';

        if (this.checkoutDetails.hasBillingAddressBeenValidated()) {
          if (this.checkoutDetails.isBillingAddressValid()) {
            retVal = 'glyphicon-ok-sign text-success';
          }
          else {
            retVal = 'glyphicon-remove-sign text-danger';
          }
        }

        return retVal;
      },
      writable: true
    },

    /**
     * Listener for select saved address event.
     */
    showSelectSavedAddress: {
      value: function showSelectSavedAddress () {
        this.isSavedAddressModalVisible(true);
      }
    },

    /**
     * Make the billing address checkout step visible
     */
    showBillingAddressAccordionPanel: {
      value: function () {
        this.checkoutDetails.isBillingAddressAccordionPanelVisible(true);
      }
    },

    /**
     *  Checkout details
     */
    checkoutDetails: {
      value: checkoutDetails
    },

    /**
     * Whether or not the a validate billing address REST call is in progress
     */
    isValidateBillingAddressInProgress: {
      value: false,
      writable: true
    },

    /**
     * This function makes a rest service call to validate the current
     * billing address.
     */
    validateBillingAddress: {
      value: function validateBillingAddress () {

        if (this.isValidateBillingAddressInProgress) {
          return;
        }

        this.checkoutDetails.hasBillingAddressBeenValidated(true);

        if (this.checkoutDetails.billingFirstName &&
            this.checkoutDetails.billingLastName &&
            this.checkoutDetails.billingAddress1 &&
            this.checkoutDetails.billingCountry &&
            this.checkoutDetails.billingCity &&
            this.checkoutDetails.billingState &&
            this.checkoutDetails.billingPostalCode &&
            this.checkoutDetails.billingPhoneNumber &&
            this.checkoutDetails.isBillingAddressValid()) {

          // all fields are completed and the billing address is
          // valid so close the billing address panel
          // (billing address is the last step in the accordion)
          this.checkoutDetails.isBillingAddressAccordionPanelVisible(false);
        }
        else {
          this.isValidateBillingAddressInProgress = true;

          var billingAddressDetails = {
            'isBillingAddressSameAsShippingAddress': this.checkoutDetails.isBillingAddressSameAsShippingAddress(),
            'billingNickName': this.checkoutDetails.billingNickName(),
            'billingFirstName': this.checkoutDetails.billingFirstName(),
            'billingLastName': this.checkoutDetails.billingLastName(),
            'billingAddress1': this.checkoutDetails.billingAddress1(),
            'billingAddress2': this.checkoutDetails.billingAddress2(),
            'billingCountry': this.checkoutDetails.billingCountry(),
            'billingCity': this.checkoutDetails.billingCity(),
            'billingState': this.checkoutDetails.billingState(),
            'billingPostalCode': this.checkoutDetails.billingPostalCode(),
            'billingPhoneNumber': this.checkoutDetails.billingPhoneNumber(),
            'billingEmail': this.checkoutDetails.billingEmail(),
            'cardNickName': this.checkoutDetails.cardNickName(),
            'creditCardNumber': this.checkoutDetails.creditCardNumber(),
            'creditCardType': this.checkoutDetails.creditCardType(),
            'creditCardVerificationNumber': this.checkoutDetails.creditCardVerificationNumber(),
            'creditCardExpirationMonth': this.checkoutDetails.creditCardExpirationMonth(),
            'creditCardExpirationYear': this.checkoutDetails.creditCardExpirationYear(),
            'saveCreditCardToProfile': this.checkoutDetails.saveCreditCardToProfile(),
            'useSavedCreditCard': this.checkoutDetails.useSavedCreditCard()
          };

          checkoutServices.applyBillingAddress(billingAddressDetails).then(function (response) {
            if (response.formError) {
              this.checkoutDetails.isBillingAddressValid(false);
            }
            else {
              this.checkoutDetails.isBillingAddressValid(true);
              this.checkoutDetails.isBillingAddressAccordionPanelVisible(false);
            }
            this.isValidateBillingAddressInProgress = false;
          }.bind(this));
        }
      }
    },

    /**
     * This property returns the translated string that will be displayed in the option caption
     * for the picker when the screen is initially rendered.
     */
    optionsCaption: {
      value: function (value) {
        var i18nDefault = i18next.t('checkout.placeholder.select');
        return i18next.t('checkout.placeholder.' + value,
          {defaultValue: i18nDefault});
      },
      writable: true
    },

    /**
     * Clear all billing address details
     */
    clearBillingAddress:{
      value: function () {
        this.checkoutDetails.billingNickName('');
        this.checkoutDetails.billingFirstName('');
        this.checkoutDetails.billingLastName('');
        this.checkoutDetails.billingAddress1('');
        this.checkoutDetails.billingAddress2('');
        this.checkoutDetails.billingCountry(this.checkoutDetails.countryBasedOnLocale);
        this.checkoutDetails.billingCity('');
        this.checkoutDetails.billingState('');
        this.checkoutDetails.billingPostalCode('');
        this.checkoutDetails.billingPhoneNumber('');
        this.checkoutDetails.isBillingAddressSameAsShippingAddress(false);
        this.checkoutDetails.isBillingAddressSelectedFromProfile(false);
      }
    },

    /**
     * Find the selected nickname in the list of available addresses
     * and update the billing address details
     */
    setSelectedBillingAddress: {
      value: function () {
        $.each(checkoutDetails.availableShippingAddress(), function (index, address) {
          if (address.addressNickname === this.checkoutDetails.currentBillingAddressSelection()) {
            this.copyToBillingAddress(address);
          }
        }.bind(this));
      }
    },

    /**
     * Copy values from the passed address to the billing address details
     */
    copyToBillingAddress: {
      value: function (address) {
        this.checkoutDetails.billingNickName(address.addressNickname);
        this.checkoutDetails.billingFirstName(address.firstName);
        this.checkoutDetails.billingLastName(address.lastName);
        this.checkoutDetails.billingAddress1(address.address1);
        this.checkoutDetails.billingAddress2(address.address2);
        this.checkoutDetails.billingCountry(address.country);
        this.checkoutDetails.billingCity(address.city);
        this.checkoutDetails.billingState(address.state);
        this.checkoutDetails.billingPostalCode(address.postalCode);
        this.checkoutDetails.billingPhoneNumber(address.phoneNumber);
        this.checkoutDetails.isBillingAddressSelectedFromProfile(true);
        $('[data-name="billToAddress-form"]').trigger('resetvalidation');
      }
    },

    dispose: {
      value: function dispose () {
        ContentItem.prototype.dispose.call(this);

        this.updateBillingAddressValidation.dispose();
        this.updateBillingAddressStates.dispose();

        return this;
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
        var viewModel = new BillingAddressSelector().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    BillingAddressSelector: BillingAddressSelector
  };
});
