// jshint jasmine: true, latedef: false
define([
  'ViewAddressBook'
], function (viewAddressBookMixin) {
  'use strict';

  var object = {
    '@type': 'ViewAddressBook'
  };

  describe('csa/plugins/account/application/components/ViewAddressBook', function () {
    it('decorates and objects correctly.', function () {
      viewAddressBookMixin(object);
      expect(object['@type']).toBe('ViewAddressBook');
      expect(object.cid).toMatch(/^ViewAddressBook-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(viewAddressBookFn(undefined)).toThrow();
      expect(viewAddressBookFn(null)).toThrow();
    });

    it('contains a property called template.', function () {
      expect(object.template).toBeDefined();
    });

    it('contains a property called localeNamespace.', function () {
      expect(object.localeNamespace).toBeDefined();
    });
  });

  // Utilities
  function viewAddressBookFn (viewModel) {
    return function () {
      return viewAddressBookMixin(viewModel);
    };
  }
});
