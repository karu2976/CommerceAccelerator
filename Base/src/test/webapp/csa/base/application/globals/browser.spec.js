// jshint jasmine: true
define([
  'browser'
], function (browser) {
  'use strict';

  describe('csa/base/application/globals/browser', function () {
    it('is defined.', function () {
      expect(browser).toBeDefined();
    });
  });
});
