/**
 * @module csa/base/application/models/datasources/oc-xmgr-datasource
 * @alias module:oc-xmgr-datasource
 * @requires module:browser
 * @requires module:libs
 * @requires module:datasource
 */
define([
  'browser',
  'libs',
  'model'
], function (browser, libs, Model) {
  'use strict';

  var console = browser.console;
  var document = browser.document;
  var window = browser;
  var $ = libs.$;

  // Listen for 'ajaxSuccess' events.
  $(document).ajaxSuccess(function (e, xhr, options, data) {
    if (options.serviceId) {

      // If a form error occurred.
      if (data.error || data.formError) {

        // Rethrow as {serviceId}Error event.
        $(window).trigger(options.serviceId + 'Error', [xhr, options, data]);
      }

      // Otherwise this is a successful rest request.
      else {

        // Rethrow as {serviceId}Success event.
        $(window).trigger(options.serviceId + 'Success', [xhr, options, data]);
      }
    }
  });

  // Listen for 'ajaxError' events
  $(document).ajaxError(function (e, xhr, settings, errorThrown) {
    if (settings.serviceId) {

      // Rethrow as {serviceId}Error event.
      $(window).trigger(settings.serviceId + 'Error', [xhr, settings, errorThrown]);
    }
  });

  /**
   * @class
   */
  function Datasource (model) {
    console.assert(model instanceof Model, 'Datasource "model" must be an instanceof Model');

    // Define intstance properties
    Object.defineProperties(this, {
      model: {
        value: model
      }
    });
  }

  // Define class prototype
  Object.defineProperties(Datasource.prototype, {

    /**
     * @property
     * @lends Datasource
     */
    constructor: {
      value: Datasource
    },

    /**
     * @property
     * @lends Datasource
     */
    cache: {
      value: null,
      writable: true
    },

    /**
     * @property
     * @lends Datasource
     */
    contentType: {
      value: 'application/json; charset=utf-8',
      writable: true
    },

    /**
     * @property
     * @lends Datasource
     */
    dataType: {
      value: 'json',
      writable: true
    },

    /**
     * @property
     * @lends Datasource
     */
    statusCode: {
      value: {}
    },

    /**
     * @function
     * @lends Datasource
     */
    execute: {
      value: function execute () {
        throw new Error('Method not implemented.');
      }
    }
  });

  return Datasource;
});
