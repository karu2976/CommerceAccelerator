// jshint jasmine: true, latedef: false
define([
  'CouponEditor'
], function (couponEditorMixin) {
  'use strict';

  describe('csa/plugins/promotions/application/components/CouponEditor', function () {
    it('decorates and objects correctly.', function () {
      var object = {
        '@type': 'CouponEditor'
      };

      couponEditorMixin(object);

      expect(object['@type']).toBe('CouponEditor');
      expect(object.cid).toMatch(/^CouponEditor-\d+$/);
    });

    it('arguments undefined/null throw errors.', function () {
      expect(couponEditorFn(undefined)).toThrow();
      expect(couponEditorFn(null)).toThrow();
    });
  });

  // Utilities
  function couponEditorFn (viewModel) {
    return function () {
      return couponEditorMixin(viewModel);
    };
  }
});
