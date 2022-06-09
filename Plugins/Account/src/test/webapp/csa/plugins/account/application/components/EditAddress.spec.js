// jshint jasmine: true, latedef: false
define([
  'EditAddress'
], function (editAddressMixin) {
  'use strict';

  var object = {
    '@type': 'EditAddress',
    'addressNickname': function () { return 'Home'; }
  };

  describe('csa/plugins/account/application/components/EditAddress', function () {
    it('decorates and objects correctly.', function () {
      editAddressMixin(object);
      expect(object['@type']).toBe('EditAddress');
      expect(object.cid).toMatch(/^EditAddress-\d+$/);
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('arguments undefined/null throw errors.', function () {
      expect(editAddressFn(undefined)).toThrow();
      expect(editAddressFn(null)).toThrow();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });

    it('contains a property called addressDisplayName.', function () {
      expect(object.addressDisplayName).toBe('Home');
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
  function editAddressFn (viewModel) {
    return function () {
      return editAddressMixin(viewModel);
    };
  }
});
