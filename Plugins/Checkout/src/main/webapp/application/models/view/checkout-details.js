/**
 * This is a helper class of checkout and works as temporary storage of checkout details.
 *
 * @module csa/plugins/checkout/application/models/view/checkout-details
 * @requires module:profile
 * @requires module:entity
 */
define([
  'profile',
  'entity'
], function (profile, Entity) {
  'use strict';

  /**
   * @class
   */
  function CheckoutDetails () {
    Entity.apply(this, arguments);
  }

  CheckoutDetails.prototype = Object.create(Entity.prototype, {

    /**
     * @property
     * @lends CheckoutDetails
     */
    constructor: {
      value: CheckoutDetails
    },

    /**
     * @property
     * @lends CheckoutDetails
     */
    observables: {
      value: Entity.prototype.observables.concat([
        'currentCountry',
        'currentShippingAddressSelection',
        'currentBillingAddressSelection',
        'currentPaymentMethodSelection',
        // Has validation been attempted for each checkout step
        'hasShippingAddressBeenValidated',
        'hasShippingMethodBeenValidated',
        'hasPaymentMethodBeenValidated',
        'hasBillingAddressBeenValidated',
        // Validation status of each checkout step
        'isShippingAddressValid',
        'isShippingMethodValid',
        'isPaymentMethodValid',
        'isBillingAddressValid',
        // The id of the parent accordion element
        'accordionId',
        // Collapsed/expanded state of each accordion panel
        'isShippingAddressAccordionPanelVisible',
        'isShippingMethodAccordionPanelVisible',
        'isPaymentMethodAccordionPanelVisible',
        'isBillingAddressAccordionPanelVisible',
        // Shipping Address details.
        'nickName',
        'shippingFirstName',
        'shippingLastName',
        'shippingAddress1',
        'shippingAddress2',
        'shippingCountry',
        'shippingCity',
        'shippingState',
        'shippingPostalCode',
        'shippingPhoneNumber',
        'shippingEmail',
        'saveShippingAddressToProfile',
        // Shipping Method details
        'shippingMethod',
        // Payment Method details
        'cardNickName',
        'creditCardNumber',
        'creditCardType',
        'creditCardVerificationNumber',
        'creditCardExpirationMonth',
        'creditCardExpirationYear',
        'saveCreditCardToProfile',
        'useSavedCreditCard',
        // Billing Address details
        'isBillingAddressSameAsShippingAddress',
        'billingNickName',
        'billingFirstName',
        'billingLastName',
        'billingAddress1',
        'billingAddress2',
        'billingCountry',
        'billingCity',
        'billingState',
        'billingPostalCode',
        'billingPhoneNumber',
        'billingEmail',
        // An existing customer can select saved details
        'isShippingAddressSelectedfromProfile',
        'isShippingMethodSelectedFromProfile',
        'isCreditCardSelectedFromProfile',
        'isBillingAddressSelectedFromProfile',
        // Shipping address states based on the selected shipping country.
        'shippingStates',
        // Billing address states based on the selected billing country.
        'billingStates'
      ]),
    },

    /**
     * @property
     * @lends CheckoutDetails
     */
    observableArrays: {
      value: Entity.prototype.observableArrays.concat([
        'availableShippingAddress',
        'availablePaymentMethods',
        'availableShippingMethods'
      ]),
    },

    /**
     * @property
     * @lends CheckoutDetails
     */
    computeds: {
      value: Entity.prototype.computeds.concat([
        'allCheckoutStepsValid'
      ])
    },

    allCheckoutStepsValid: {
      value: function () {
        return this.isShippingAddressValid() &&
          this.isShippingMethodValid() &&
          this.isPaymentMethodValid() &&
          this.isBillingAddressValid();
      },
      writable: true
    },

    profile: {
      value: profile
    },

    stateListBasedOnLocale: {
      value: [],
      writable: true
    },

    countryBasedOnLocale: {
      value: '',
      writable: true
    },

    initialize: {
      value: function (options) {
        this.availableShippingAddress(options.availableShippingAddress);
        this.availablePaymentMethods(options.availablePaymentMethods);
        this.availableShippingMethods(options.availableShippingMethods);
        this.stateListBasedOnLocale = options.states;
        this.countryBasedOnLocale = options.currentCountry;

        this.accordionId(options.parentCid + 'checkoutSteps');

        // The "collapse" state of each accordion panel
        this.isShippingAddressAccordionPanelVisible(true);
        this.isShippingMethodAccordionPanelVisible(false);
        this.isPaymentMethodAccordionPanelVisible(false);
        this.isBillingAddressAccordionPanelVisible(false);

        // whether or not a section has been validated
        this.hasShippingAddressBeenValidated(false);
        this.hasShippingMethodBeenValidated(false);
        this.hasPaymentMethodBeenValidated(false);
        this.hasBillingAddressBeenValidated(false);
      }
    }
  });

  return new CheckoutDetails();
});
