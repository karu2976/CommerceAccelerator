/**
 * The PaymentMethodSelector component provides the UI on checkout page for
 * inputting credit card details.
 *
 * @module csa/plugins/checkout/application/components/payment-method-selector
 * @requires module:content-item
 * @requires module:checkout-details
 * @requires module:checkout-services
 * @requires module:text!./payment-method-selector.html
 * @requires module:libs
 */
define([
  'content-item',
  'checkout-details',
  'checkout-services',
  'text!./payment-method-selector.html',
  'libs'
], function (ContentItem, checkoutDetails, checkoutServices, template, libs) {
  'use strict';

  var $ = libs.$;
  var i18next = libs.i18next;
  var ko = libs.ko;

  function PaymentMethodSelector() {
    ContentItem.apply(this, arguments);

    this.isSelectSavedCreditCardModalVisible(false);
    this.isSecurityCodeExplanationModalVisible(false);

    /**
     * When any payment method detail changes
     * update the value of isPaymentMethodValid
     */
    this.updatePaymentMethodValidation = ko.computed(function () {
      // All payment method observables are added here so
      // this computed will be triggered if any of them change
      this.checkoutDetails.cardNickName();
      this.checkoutDetails.creditCardNumber();
      this.checkoutDetails.creditCardType();
      this.checkoutDetails.creditCardVerificationNumber();
      this.checkoutDetails.creditCardExpirationMonth();
      this.checkoutDetails.creditCardExpirationYear();

      if (this.checkoutDetails.hasPaymentMethodBeenValidated()) {
        // Any changes to the payment method after validation
        // makes the payment method section invalid.
        this.checkoutDetails.isPaymentMethodValid(false);

        if (this.checkoutDetails.hasBillingAddressBeenValidated) {
          // As the billing address is related to the selected
          // payment method, the billing address must be set to
          // invalid when payment method changes
          this.checkoutDetails.isBillingAddressValid(false);
        }
      }
    }.bind(this));

    /**
     * Subscribe to checkoutDetails.creditCardNumber observable changes
     *
     * Updates the credit card type when the credit card number changes
     */
    this.checkoutDetails.creditCardNumber.subscribe(function (creditCardNumber) {
      if (checkoutDetails.isCreditCardSelectedFromProfile()) {
        return;
      }

      // Determine the card type from the card number. Visa starts with 4, Master card
      // starts with 51 through 55. Amex starts with 34 or 37 and Discover starts with 6011.
      if (/^4\d{0,15}/.test(creditCardNumber) || /^4\d{0,12}/.test(creditCardNumber)) {
        this.checkoutDetails.creditCardType('visa');
      } else if (/^5[1-5]\d{0,14}$/.test(creditCardNumber)) {
        this.checkoutDetails.creditCardType('masterCard');
      } else if (/^3[47]\d{0,13}/.test(creditCardNumber)) {
        this.checkoutDetails.creditCardType('americanExpress');
      } else if (/^6011\d{0,12}/.test(creditCardNumber)) {
        this.checkoutDetails.creditCardType('discover');
      } else {
        this.checkoutDetails.creditCardType('');
      }
    }.bind(this));

    /**
     * Subscribe to checkoutDetails.hasPaymentMethodBeenValidated observable changes
     *
     * When false clear all payment method details and set isPaymentMethodValid to false
     * Will be set to false when the checkout page is first loaded
     */
    this.checkoutDetails.hasPaymentMethodBeenValidated.subscribe(function (hasPaymentMethodBeenValidated) {
      if (!hasPaymentMethodBeenValidated) {
        this.clearPaymentMethod();
        this.checkoutDetails.isPaymentMethodValid(false);
      }
    }.bind(this));

    /**
     * If the user has an account and has set a default payment method then copy their
     * default payment method to shipping method details (will also update the billing address)
     */
    $.each(this.checkoutDetails.availablePaymentMethods(), function (index, paymentMethod) {
      if (paymentMethod.setAsDefaultCard) {
        this.checkoutDetails.currentPaymentMethodSelection(paymentMethod.cardNickname);
        this.copyToPaymentMethod(paymentMethod);
      }
    }.bind(this));
  }

  // Define class prototype
  PaymentMethodSelector.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends PaymentMethodSelector
     */
    constructor: {
      value: PaymentMethodSelector
    },

    /**
     * @property
     * @lends PaymentMethodSelector
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'isSelectSavedCreditCardModalVisible',
        'isSecurityCodeExplanationModalVisible'
      ])
    },

    /**
     * @property
     * @lends PaymentMethodSelector
     */
    computeds: {
      value: ContentItem.prototype.computeds.concat([
        'validationIconClass',
        'creditCardNumberLast4Digits',
        'creditCardType',
        'creditCardTypeCssClass'
      ])
    },

    /**
     * The CSS class to display the correct icon based on the values
     * of hasPaymentMethodBeenValidated and isPaymentMethodValid
     */
    validationIconClass: {
      value: function () {
        var retVal = 'glyphicon-question-sign text-info';

        if (this.checkoutDetails.hasPaymentMethodBeenValidated()) {
          if (this.checkoutDetails.isPaymentMethodValid()) {
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
     * The last 4 digits of the selected credit card
     */
    creditCardNumberLast4Digits: {
      value: function () {
        if (this.checkoutDetails.creditCardNumber() !== undefined) {
          return this.checkoutDetails.creditCardNumber().slice(-4);
        }
      },
      writable: true
    },

    /**
     * Display friendly version of the credit card type
     */
    creditCardType: {
      value: function () {
        if (this.checkoutDetails.creditCardType()) {
          // find all lower case letters that are followed by an upper case
          // letter and insert a space between them.
          // NOTE: this replace will handle scenarios like 'masterCard'
          // and 'americanExpress' but not 'thisIsANewCard', really the
          // card description text should be returned with the card types
          return this.checkoutDetails.creditCardType().replace(/([a-z])([A-Z])/g, '$1 $2');
        }
      },
      writable: true
    },

    /**
     * The CSS class(es) to indicate the select card type icon
     */
    creditCardTypeCssClass: {
      value: function () {
        var retVal = '';

        switch (this.checkoutDetails.creditCardType()) {
          case 'masterCard':
            retVal = 'mc';
            break;
          case 'visa':
            retVal = 'vc';
            break;
          case 'discover':
            retVal = 'dc';
            break;
          case 'americanExpress':
            retVal = 'ac';
            break;
          default:
            retVal = 'mc vc dc ac';
            break;
        }

        return retVal;
      },
      writable: true
    },

    /**
     * Listener for select saved address event.
     */
    showSelectSavedCreditCard: {
      value: function showSelectSavedCreditCard () {
        this.isSelectSavedCreditCardModalVisible(true);
      }
    },

    /**
     * Listener for show security code explanation.
     */
    showSecurityCodeExplanation: {
      value: function showSecurityCodeExplanation () {
        this.isSecurityCodeExplanationModalVisible(true);
      }
    },

    /**
     * Make the payment method checkout step visible
     */
    showPaymentMethodAccordionPanel: {
      value: function () {
        this.checkoutDetails.isPaymentMethodAccordionPanelVisible(true);
      }
    },

    /**
     * Checkout details
     */
    checkoutDetails: {
      value: checkoutDetails
    },

    /**
     * Whether or not the a validate payment method REST call is in progress
     */
    isValidatePaymentMethodInProgress: {
      value: false,
      writable: true
    },

    /**
     * This function makes a rest service call to validate the payment method and
     * applies the payment method to order.
     */
    validatePaymentMethod: {
      value: function validatePaymentMethod () {

        if (this.isValidatePaymentMethodInProgress) {
          return;
        }

        this.checkoutDetails.hasPaymentMethodBeenValidated(true);

        if (this.checkoutDetails.creditCardNumber() &&
          this.checkoutDetails.creditCardVerificationNumber() &&
          this.checkoutDetails.creditCardExpirationMonth() &&
          this.checkoutDetails.creditCardExpirationYear() &&
          this.checkoutDetails.isPaymentMethodValid()) {

          // all fields are completed and the payment method is
          // valid so continue to the billing address step
          this.checkoutDetails.isBillingAddressAccordionPanelVisible(true);
        }
        else {
          var paymentMethod = {
            'cardNickName': this.checkoutDetails.cardNickName(),
            'creditCardNumber': this.checkoutDetails.creditCardNumber(),
            'creditCardType': this.checkoutDetails.creditCardType(),
            'creditCardVerificationNumber': this.checkoutDetails.creditCardVerificationNumber(),
            'creditCardExpirationMonth': this.checkoutDetails.creditCardExpirationMonth(),
            'creditCardExpirationYear': this.checkoutDetails.creditCardExpirationYear(),
            'saveCreditCardToProfile': this.checkoutDetails.saveCreditCardToProfile(),
            'useSavedCreditCard': this.checkoutDetails.useSavedCreditCard()
          };

          this.isValidatePaymentMethodInProgress = true;

          checkoutServices.applyPaymentMethod(paymentMethod).then(function (response) {
            if (response.formError) {
              this.checkoutDetails.isPaymentMethodValid(false);
            }
            else {
              this.checkoutDetails.isPaymentMethodValid(true);
              this.checkoutDetails.isBillingAddressAccordionPanelVisible(true);
            }
            this.isValidatePaymentMethodInProgress = false;
          }.bind(this));
        }
      }
    },

    /**
     * Display the localized options caption for the select state dropdown.
     */
    expirationYearOptionsText: {
      value: function expirationYearOptionsText () {
        return i18next.t('checkout.placeholder.selectYear');
      }
    },

    /**
     * Clear all payment method details
     */
    clearPaymentMethod: {
      value: function () {
        this.checkoutDetails.cardNickName('');
        this.checkoutDetails.creditCardNumber('');
        this.checkoutDetails.creditCardType('');
        this.checkoutDetails.creditCardExpirationMonth('');
        this.checkoutDetails.creditCardExpirationYear('');
        this.checkoutDetails.creditCardVerificationNumber('');
        this.checkoutDetails.saveCreditCardToProfile(false);
        this.checkoutDetails.useSavedCreditCard(false);

        this.checkoutDetails.isCreditCardSelectedFromProfile(false);
      }
    },

    /**
     * Find the selected nickname in the list of available payment
     * methods and update the payment method details
     */
    setSelectedPaymentMethod: {
      value: function () {
        $.each (this.checkoutDetails.availablePaymentMethods(), function (index, paymentMethod) {
          if (paymentMethod.cardNickname === this.checkoutDetails.currentPaymentMethodSelection()) {
            this.copyToPaymentMethod(paymentMethod);
          }
        }.bind(this));
      }
    },

    /**
     * Copy values from the passed payment method to the payment method details
     * also updates the billing address details.
     */
    copyToPaymentMethod: {
      value: function (paymentMethod) {
        this.checkoutDetails.isBillingAddressSelectedFromProfile(true);
        this.checkoutDetails.isBillingAddressSameAsShippingAddress(false);
        this.checkoutDetails.cardNickName(paymentMethod.cardNickname);
        this.checkoutDetails.creditCardNumber('XXXXXXXXXXXX' + paymentMethod.creditCardNumber);
        this.checkoutDetails.creditCardType(paymentMethod.creditCardType);
        this.checkoutDetails.creditCardExpirationMonth(paymentMethod.expirationMonth);
        this.checkoutDetails.creditCardExpirationYear(paymentMethod.expirationYear);
        this.checkoutDetails.creditCardVerificationNumber('');
        this.checkoutDetails.saveCreditCardToProfile(false);
        this.checkoutDetails.useSavedCreditCard(true);
        this.checkoutDetails.isCreditCardSelectedFromProfile(true);
        this.checkoutDetails.billingFirstName(paymentMethod.billingAddress.firstName);
        this.checkoutDetails.billingLastName(paymentMethod.billingAddress.lastName);
        this.checkoutDetails.billingAddress1(paymentMethod.billingAddress.address1);
        this.checkoutDetails.billingAddress2(paymentMethod.billingAddress.address2);
        this.checkoutDetails.billingCountry(paymentMethod.billingAddress.country);
        this.checkoutDetails.billingCity(paymentMethod.billingAddress.city);
        this.checkoutDetails.billingState(paymentMethod.billingAddress.state);
        this.checkoutDetails.billingPostalCode(paymentMethod.billingAddress.postalCode);
        this.checkoutDetails.billingPhoneNumber(paymentMethod.billingAddress.phoneNumber);

        $('[data-name="payment-form"]').trigger('resetvalidation');
        $('[data-name="billToAddress-form"]').trigger('resetvalidation');
      }
    },

    dispose: {
      value: function dispose () {
        ContentItem.prototype.dispose.call(this);

        this.updatePaymentMethodValidation.dispose();

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
        var viewModel = new PaymentMethodSelector().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    PaymentMethodSelector: PaymentMethodSelector
  };
});
