/**
 * The ShippingMethodSelector component provides the options for different types
 * delivery methods on UI on checkout page.
 *
 * @module csa/plugins/checkout/application/components/ShippingMethodSelector
 * @requires module:content-item
 * @requires module:checkout-details
 * @requires module:checkout-services
 * @requires module:text!./shipping-method-selector.html
 * @requires module:libs
 */
define([
  'content-item',
  'checkout-details',
  'checkout-services',
  'text!./shipping-method-selector.html',
  'libs'
], function (ContentItem, checkoutDetails, checkoutServices, template, libs) {
  'use strict';

  var $ = libs.$;
  var ko = libs.ko;

  function ShippingMethodSelector() {
    ContentItem.apply(this, arguments);

    /**
     * When any shipping method details change
     * update the value of isShippingMethodValid
     */
    this.updateShippingMethodValidation = ko.computed(function () {
      // The shipping method observable is added here so
      // this computed will be triggered if it changes
      this.checkoutDetails.shippingMethod();

      if (this.checkoutDetails.hasShippingMethodBeenValidated()) {
        // Any changes to the shipping method after validation
        // makes the shipping method section invalid.
        this.checkoutDetails.isShippingMethodValid(false);
      }
    }.bind(this));

    /**
     * Subscribe to checkoutDetails.hasShippingMethodBeenValidated observable changes
     *
     * When false clear all shipping method details and set isShippingMethodValid to false
     * Will be set to false when the checkout page is first loaded
     */
    this.checkoutDetails.hasShippingMethodBeenValidated.subscribe(function (hasShippingMethodBeenValidated) {
      if (!hasShippingMethodBeenValidated) {
        this.clearShippingMethod();
        this.checkoutDetails.isShippingMethodValid(false);
      }
    }.bind(this));

    /**
     * If the user has an account and has set a default shipping method then copy their
     * default shipping method to shipping method details
     */
    $.each(this.checkoutDetails.availableShippingMethods(), function (index, shippingMethod) {
      if (shippingMethod.useAsDefaultShippingMethod) {
        this.checkoutDetails.shippingMethod(shippingMethod.method);
      }
    }.bind(this));
  }

  // Define class prototype
  ShippingMethodSelector.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends ShippingMethodSelector
     */
    constructor: {
      value: ShippingMethodSelector
    },

    /**
     * @property
     * @lends ShippingMethodSelector
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'validationIconClass',
        'shippingMethodDisplayName'
      ])
    },

    /**
     * The CSS class to display the correct icon based on the values
     * of hasShippingMethodBeenValidated and isShippingMethodValid
     */
    validationIconClass: {
      value: function () {
        var retVal = 'glyphicon-question-sign text-info';

        if (this.checkoutDetails.hasShippingMethodBeenValidated()) {
          if (this.checkoutDetails.isShippingMethodValid()) {
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
     * Shipping method name for display.
     */
    shippingMethodDisplayName: {
      value: function () {
        for (var i = 0; i < this.checkoutDetails.availableShippingMethods().length; i++) {
          if (checkoutDetails.shippingMethod() ===
            this.checkoutDetails.availableShippingMethods()[i].method) {
            return this.checkoutDetails.availableShippingMethods()[i].displayName;
          }
        }
      },
      writable: true
    },

    /**
     * checkout details
     */
    checkoutDetails: {
      value: checkoutDetails
    },

    /**
     * Whether or not shipping method validation REST call is in progress.
     */
    isShippingMethodValidationInProgress: {
      value: false,
      writable: true
    },

    /**
     * Make the shipping method checkout step visible
     */
    showShippingMethodAccordionPanel: {
      value: function () {
        this.checkoutDetails.isShippingMethodAccordionPanelVisible(true);
      }
    },

    /**
     * This function makes a call to validate the shipping method against
     * shipping address and applies the shipping method to order if it is valid.
     */
    validateShippingMethod: {
      value: function validateShippingMethod () {

        if (this.isShippingMethodValidationInProgress) {
          return;
        }

        this.checkoutDetails.hasShippingMethodBeenValidated(true);

        if (this.checkoutDetails.shippingMethod() &&
            this.checkoutDetails.isShippingMethodValid()) {

          // a shipping method has been selected and it is
          // valid so continue to the payment method step
          this.checkoutDetails.isPaymentMethodAccordionPanelVisible(true);
        }
        else {
          var shippingMethodDetails = {
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
            'saveShippingAddressToProfile': this.checkoutDetails.saveShippingAddressToProfile(),
            'shippingMethod': this.checkoutDetails.shippingMethod()
          };

          this.isShippingMethodValidationInProgress = true;

          checkoutServices.applyShippingMethod(shippingMethodDetails).then(function (response) {
            if (response.formError) {
              this.checkoutDetails.isShippingMethodValid(false);
            }
            else {
              this.checkoutDetails.isShippingMethodValid(true);
              this.checkoutDetails.isPaymentMethodAccordionPanelVisible(true);
            }
            this.isShippingMethodValidationInProgress = false;
          }.bind(this));
        }
      }
    },

    /**
     * Clear all shipping method details
     */
    clearShippingMethod: {
      value: function () {
        this.checkoutDetails.shippingMethod('');
      }
    },

    dispose: {
      value: function dispose () {
        ContentItem.prototype.dispose.call(this);

        this.updateShippingMethodValidation.dispose();

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
        var viewModel = new ShippingMethodSelector().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    ShippingMethodSelector: ShippingMethodSelector
  };
});
