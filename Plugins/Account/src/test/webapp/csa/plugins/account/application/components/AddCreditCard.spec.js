// jshint jasmine: true, latedef: false
define([
  'AddCreditCard'
], function (addCreditCardMixin) {
  'use strict';

  var object = {
    '@type': 'AddCreditCard',
    'country': function () {
      return 'US';
    }
  };

  describe('csa/plugins/account/application/components/AddCreditCard', function () {
    it('decorates and objects correctly.', function () {
      addCreditCardMixin(object);
      expect(object['@type']).toBe('AddCreditCard');
      expect(object.cid).toMatch(/^AddCreditCard-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(addCreditCardFn(undefined)).toThrow();
      expect(addCreditCardFn(null)).toThrow();
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });

    it('contains a property called cardNickname.', function () {
      expect(object.cardNickname).toBeDefined();
    });

    it('contains a property called creditCardNumber.', function () {
      expect(object.creditCardNumber).toBeDefined();
    });

    it('contains a property called creditCardType.', function () {
      expect(object.creditCardType).toBeDefined();
    });

    it('contains a property called expirationMonth.', function () {
      expect(object.expirationMonth).toBeDefined();
    });

    it('contains a property called expirationYear.', function () {
      expect(object.expirationYear).toBeDefined();
    });

    it('contains a property called firstName.', function () {
      expect(object.firstName).toBeDefined();
    });

    it('contains a property called lastName.', function () {
      expect(object.lastName).toBeDefined();
    });

    it('contains a property called address1.', function () {
      expect(object.address1).toBeDefined();
    });

    it('contains a property called address2.', function () {
      expect(object.address2).toBeDefined();
    });

    it('contains a property called postalCode.', function () {
      expect(object.postalCode).toBeDefined();
    });

    it('contains a property called city.', function () {
      expect(object.city).toBeDefined();
    });

    it('contains a property called state.', function () {
      expect(object.state).toBeDefined();
    });

    it('contains a property called phoneNumber.', function () {
      expect(object.phoneNumber).toBeDefined();
    });

    it('contains a property called setAsDefaultCard.', function () {
      expect(object.setAsDefaultCard()).toBe(false);
    });

    it('contains a property called addresses.', function () {
      expect(object.addresses).toBeDefined();
    });

    it('contains a property called selectedAddressIndex.', function () {
      expect(object.selectedAddressIndex).toBeDefined();
    });

    it('contains a property called formPopulatedFromSavedAddress.', function () {
      expect(object.formPopulatedFromSavedAddress()).toBe(false);
    });

    it('contains a property called isSavedAddressModalVisible.', function () {
      expect(object.isSavedAddressModalVisible()).toBe(false);
      object.showSelectSavedAddress();
      expect(object.isSavedAddressModalVisible()).toBe(true);
    });

    it('copies the address fields.', function () {
      expect(object.firstName()).toBe(undefined);
      expect(object.lastName()).toBe(undefined);

      object.addresses.push(
      {
        'firstName': 'Test1',
        'lastName': 'Test2',
        'country': function () { return 'US'; }
      });

      object.copyAddressFields();

      expect(object.firstName()).toBe('Test1');
      expect(object.lastName()).toBe('Test2');
    });

    it('sets the card type correctly', function () {
      expect(object.creditCardType()).toBe(undefined);

      object.creditCardNumber('4');
      object.setCardType();
      expect(object.creditCardType()).toBe('visa');

      object.creditCardNumber('51');
      object.setCardType();
      expect(object.creditCardType()).toBe('mastercard');

      object.creditCardNumber('34');
      object.setCardType();
      expect(object.creditCardType()).toBe('amex');

      object.creditCardNumber('6011');
      object.setCardType();
      expect(object.creditCardType()).toBe('discover');
    });

    it('contains state options text', function () {
      expect(object.selectStateOptionsText).toBeDefined();
      expect(object.selectStateOptionsText()).toBe('Select state');
    });

    it('contains country options text', function () {
      expect(object.selectCountryOptionsText).toBeDefined();
      expect(object.selectCountryOptionsText()).toBe('Select country');
    });

    it('contains year options text', function () {
      expect(object.selectYearOptionsText).toBeDefined();
      expect(object.selectYearOptionsText()).toBe('Select year');
    });

  });

  // Utilities
  function addCreditCardFn (viewModel) {
    return function () {
      return addCreditCardMixin(viewModel);
    };
  }
});
