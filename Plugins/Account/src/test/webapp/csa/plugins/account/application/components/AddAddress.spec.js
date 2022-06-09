// jshint jasmine: true, latedef: false
define([
  'AddAddress'
], function (addAddressMixin) {
  'use strict';

  var object = {
    '@type': 'AddAddress'
  };

  describe('csa/plugins/account/application/components/AddAddress', function () {
    it('decorates and objects correctly.', function () {
      addAddressMixin(object);
      expect(object['@type']).toBe('AddAddress');
      expect(object.cid).toMatch(/^AddAddress-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(addAddressFn(undefined)).toThrow();
      expect(addAddressFn(null)).toThrow();
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });

    it('contains a property called addressNickname.', function () {
      expect(object.addressNickname).toBeDefined();
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

    it('contains a property called city.', function () {
      expect(object.city).toBeDefined();
    });

    it('contains a property called state.', function () {
      expect(object.state).toBeDefined();
    });

    it('contains a property called postalCode.', function () {
      expect(object.postalCode).toBeDefined();
    });

    it('contains a property called phoneNumber.', function () {
      expect(object.phoneNumber).toBeDefined();
    });

    it('contains a property called useAsDefaultShippingAddress.', function () {
      expect(object.useAsDefaultShippingAddress()).toBe(false);
    });

    it('contains state options text', function () {
      expect(object.selectStateOptionsText).toBeDefined();
      expect(object.selectStateOptionsText()).toBe('Select state');

    });

    it('contains country options text', function () {
      expect(object.selectCountryOptionsText).toBeDefined();
      expect(object.selectCountryOptionsText()).toBe('Select country');
    });

  });

  // Utilities
  function addAddressFn (viewModel) {
    return function () {
      return addAddressMixin(viewModel);
    };
  }
});
