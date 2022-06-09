// jshint jasmine: true
define([
  'browser',
  'datasource',
  'load-page-content'
], function (browser, Datasource, loadPageContent) {
  'use strict';

  var window = browser;

  describe('csa/base/application/services/web/load-page-content', function () {
    var datasourceCache = Datasource.prototype.cache;

    beforeEach(function () {
      jasmine.Ajax.install();
      Datasource.prototype.cache = undefined;
    });

    afterEach(function () {
      jasmine.Ajax.uninstall();
      Datasource.prototype.cache = datasourceCache;
    });

    it('Can load content from a URL.', function () {
      var consoleSpy = spyOn(window.console, 'log');

      jasmine.Ajax.stubRequest(window.location.origin + '/some/url?format=json').andReturn({
        'status': 200,
        'contentType': 'application/json',
        'responseText': '{"message": "OK"}'
      });

      loadPageContent('/some/url')
        .done(function (responseText) {
          window.console.log(responseText);
        })
        .fail(function () {
          window.console.log('error');
        })
        .always(function () {
          window.console.log('complete');
        });

      expect(consoleSpy).toHaveBeenCalledWith({'message': 'OK'});
    });
  });
});
