/**
 * The ShippingAddressSelector component provides the UI on checkout page for
 * inputting shipping address details.
 *
 * @module csa/plugins/checkout/application/components/ShippingAddressSelector
 * @requires module:content-item
 * @requires module:checkout-details
 * @requires module:checkout-services
 * @requires module:text!./ShippingAddressSelector.html
 * @requires module:profile
 * @requires module:libs
 */
define([
  'content-item',
  'checkout-details',
  'checkout-services',
  'location-services',
  'text!./shipping-address-selector.html',
  'profile',
  'libs'
], function (ContentItem, checkoutDetails, checkoutServices, locationServices, template, profile, libs) {
  'use strict';

  var $ = libs.$;
  var i18next = libs.i18next;
  var ko = libs.ko;

  function ShippingAddressSelector() {
    ContentItem.apply(this, arguments);

    this.isSavedAddressModalVisible(false);

    /**
     * When any shipping address details change
     * update the value of isShippingAddressValid
     */
    this.updateShippingAddressValidation = ko.computed(function () {
      // All shipping address observables are added here so
      // this computed will be triggered if any of them change
      this.checkoutDetails.nickName();
      this.checkoutDetails.shippingFirstName();
      this.checkoutDetails.shippingLastName();
      this.checkoutDetails.shippingAddress1();
      this.checkoutDetails.shippingAddress2();
      this.checkoutDetails.shippingCountry();
      this.checkoutDetails.shippingCity();
      this.checkoutDetails.shippingState();
      this.checkoutDetails.shippingPostalCode();
      this.checkoutDetails.shippingPhoneNumber();
      this.checkoutDetails.shippingEmail();
      this.checkoutDetails.saveShippingAddressToProfile();

      if (this.checkoutDetails.hasShippingAddressBeenValidated()) {
        // Any changes to the shipping address after validation
        // makes the shipping address section invalid.
        this.checkoutDetails.isShippingAddressValid(false);

        if (this.checkoutDetails.hasShippingMethodBeenValidated) {
          // As available shipping methods are dependent
          // on the selected shipping address, shipping
          // method must be set to invalid when shipping
          // address changes
          this.checkoutDetails.isShippingMethodValid(false);
        }
      }
    }.bind(this));

    /**
     * If the shipping address country is changed then update the
     * shipping address states with values for the selected country
     */
    this.updateShippingAddressStates = ko.computed(function () {
      var shippingCountry = this.checkoutDetails.shippingCountry();
      if (shippingCountry) {
        locationServices.getStatesForCountry(shippingCountry).then(function (response) {
          this.checkoutDetails.shippingStates(response.states);

          // check if the currently selected state is in the new state list and only
          // clear shippingAddress().shippingState if its value is not in the new list
          // peek() returns the current value of the observable without creating a dependency
          var selectedShippingAddressStateCode = this.checkoutDetails.shippingState.peek();
          for (var i = 0, len = response.states.length; i < len; i++) {
            if (response.states[i].code === selectedShippingAddressStateCode) {
              return;
            }
          }

          this.checkoutDetails.shippingState('');

        }.bind(this));
      }
    }.bind(this));

    /**
     * Subscribe to checkoutDetails.hasShippingAddressBeenValidated observable changes
     *
     * When false clear all shipping address details and set isShippingAddressValid to false
     * Will be set to false when the checkout page is first loaded
     */
    this.checkoutDetails.hasShippingAddressBeenValidated.subscribe(function (hasShippingAddressBeenValidated) {
      if (!hasShippingAddressBeenValidated) {
        this.clearShippingAddress();
        this.checkoutDetails.isShippingAddressValid(false);
      }
    }.bind(this));

    // Set the default shipping address country
    this.checkoutDetails.shippingCountry(this.checkoutDetails.countryBasedOnLocale);

    /**
     * If the user has an account and has set a default shipping address then copy their
     * default shipping address to shipping address details
     */
    $.each(this.checkoutDetails.availableShippingAddress(), function (index, address) {
      if (address.useAsDefaultShippingAddress) {
        this.checkoutDetails.currentShippingAddressSelection(address.addressNickname);
        this.copyToShippingAddress(address);
      }
    }.bind(this));
  }

  // Define class prototype
  ShippingAddressSelector.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends ShippingAddressSelector
     */
    constructor: {
      value: ShippingAddressSelector
    },

    /**
     * @property
     * @lends ShippingAddressSelector
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'isSavedAddressModalVisible',
        'saveToProfile',
      ])
    },

    /**
     * @property
     * @lends ShippingAddressSelector
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'validationIconClass'
      ])
    },

    /**
     * The CSS class to display the correct icon based on the values
     * of hasShippingAddressBeenValidated and isShippingAddressValid
     */
    validationIconClass: {
      value: function () {
        var retVal = 'glyphicon-question-sign text-info';

        if (this.checkoutDetails.hasShippingAddressBeenValidated()) {
          if (this.checkoutDetails.isShippingAddressValid()) {
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
     * Make the shipping address checkout step visible
     */
    showShippingAddressAccordionPanel: {
      value: function () {
        this.checkoutDetails.isShippingAddressAccordionPanelVisible(true);
      }
    },

    /**
     * Profile object.
     */
    profile: {
      value: profile
    },

    /**
     * checkout details
     */
    checkoutDetails: {
      value: checkoutDetails
    },

    /**
     * Whether or not shipping address validation REST call is in progress.
     */
    isShippingAddressValidationInProgress: {
      value: false,
      writable: true
    },

    /**
     * This function makes a call to validate the shipping address and apply the
     * same address to the order.
     */
    validateShippingAddress: {
      value: function validateShippingAddress () {

        if (this.isShippingAddressValidationInProgress) {
          return;
        }

        this.checkoutDetails.hasShippingAddressBeenValidated(true);

        if (this.checkoutDetails.shippingFirstName() &&
          this.checkoutDetails.shippingLastName() &&
          this.checkoutDetails.shippingAddress1() &&
          this.checkoutDetails.shippingCountry() &&
          this.checkoutDetails.shippingCity() &&
          this.checkoutDetails.shippingState() &&
          this.checkoutDetails.shippingPostalCode() &&
          this.checkoutDetails.shippingPhoneNumber() &&
          this.checkoutDetails.isShippingAddressValid()) {

          // all fields are completed and the shipping address is
          // valid so continue to the shipping method step
          this.checkoutDetails.isShippingMethodAccordionPanelVisible(true);
        }
        else {

          var shippingAddress = {
            'nickName': this.checkoutDetails.nickName(),
            'shippingFirstName': this.checkoutDetails.shippingFirstName(),
            'shippingLastName': this.checkoutDetails.shippingLastName(),
            'shippingAddress1': this.checkoutDetails.shippingAddress1(),
            'shippingAddress2': this.checkoutDetails.shippingAddress2(),
            'shippingCountry': this.checkoutDetails.shippingCountry(),
            'shippingCity': this.checkoutDetails.shippingCity(),
            'shippingState': this.checkoutDetails.shippingState(),
            'shippingPostalCode': this.checkoutDetails.shippingPostalCode(),
            'shippingPhoneNumber': this.checkoutDetails.shippingPhoneNumber(),
            'shippingEmail': this.checkoutDetails.shippingEmail(),
            'saveShippingAddressToProfile': this.checkoutDetails.saveShippingAddressToProfile()
          };

          this.isShippingAddressValidationInProgress = true;

          checkoutServices.applyShippingAddress(shippingAddress).then(function (response) {
            if (response.formError) {
              this.checkoutDetails.isShippingAddressValid(false);
            }
            else {
              this.checkoutDetails.isShippingAddressValid(true);
              this.checkoutDetails.isShippingMethodAccordionPanelVisible(true);
            }
            this.isShippingAddressValidationInProgress = false;
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
     * Clear all shipping address details
     */
    clearShippingAddress: {
      value: function () {
        this.checkoutDetails.nickName('');
        this.checkoutDetails.shippingFirstName('');
        this.checkoutDetails.shippingLastName('');
        this.checkoutDetails.shippingAddress1('');
        this.checkoutDetails.shippingAddress2('');
        this.checkoutDetails.shippingCountry(this.checkoutDetails.countryBasedOnLocale);
        this.checkoutDetails.shippingCity('');
        this.checkoutDetails.shippingState('');
        this.checkoutDetails.shippingPostalCode('');
        this.checkoutDetails.shippingPhoneNumber('');
        this.checkoutDetails.shippingEmail('');
        this.checkoutDetails.saveShippingAddressToProfile(false);
        this.checkoutDetails.isShippingAddressSelectedfromProfile(false);
      }
    },

    /**
     * Find the selected nickname in the list of available addresses
     * and update the shipping address details
     */
    setSelectedShippingAddress: {
      value: function () {
        $.each(checkoutDetails.availableShippingAddress(), function (index, address) {
          if (address.addressNickname === this.checkoutDetails.currentShippingAddressSelection()) {
            this.copyToShippingAddress(address);
          }
        }.bind(this));
      }
    },

    /**
     * Copy values from the passed address to the shipping address details
     */
    copyToShippingAddress: {
      value: function (address) {
        this.checkoutDetails.nickName(address.addressNickname);
        this.checkoutDetails.shippingFirstName(address.firstName);
        this.checkoutDetails.shippingLastName(address.lastName);
        this.checkoutDetails.shippingAddress1(address.address1);
        this.checkoutDetails.shippingAddress2(address.address2);
        this.checkoutDetails.shippingCountry(address.country);
        this.checkoutDetails.shippingCity(address.city);
        this.checkoutDetails.shippingState(address.state);
        this.checkoutDetails.shippingPostalCode(address.postalCode);
        this.checkoutDetails.shippingPhoneNumber(address.phoneNumber);
        this.checkoutDetails.shippingEmail('');
        this.checkoutDetails.saveShippingAddressToProfile(false);
        this.checkoutDetails.isShippingAddressSelectedfromProfile(true);
        $('[data-name="shipToAddress-form"]').trigger('resetvalidation');
      }
    },

    dispose: {
      value: function dispose () {
        ContentItem.prototype.dispose.call(this);

        this.updateShippingAddressValidation.dispose();
        this.updateShippingAddressStates.dispose();

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
        var viewModel = new ShippingAddressSelector().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    ShippingAddressSelector: ShippingAddressSelector
  };
});
