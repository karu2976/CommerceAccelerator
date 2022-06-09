// jshint jasmine: true, latedef: false
define([
  'EditCreditCard'
], function (editCreditCardMixin) {
  'use strict';

  var object = {
    '@type': 'EditCreditCard',
    'cardNickname': function () { return 'Visa'; },
    'billingAddress': {
      'country' : function () { return 'US'; }
    }
  };

  describe('csa/plugins/account/application/components/EditCreditCard', function () {
    it('decorates and objects correctly.', function () {
      editCreditCardMixin(object);
      expect(object['@type']).toBe('EditCreditCard');
      expect(object.cid).toMatch(/^EditCreditCard-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(editCreditCardFn(undefined)).toThrow();
      expect(editCreditCardFn(null)).toThrow();
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });

    it('contains a property called cardDisplayName.', function () {
      expect(object.cardDisplayName).toBe('Visa');
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
      object.addresses.push(
      {
        'firstName': 'Test1',
        'lastName': 'Test2',
        'country': function () { return 'US'; }
      });

      object.copyAddressFields();

      expect(object.billingAddress.firstName()).toBe('Test1');
      expect(object.billingAddress.lastName()).toBe('Test2');
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
  function editCreditCardFn (viewModel) {
    return function () {
      return editCreditCardMixin(viewModel);
    };
  }
});
