/**
 * @module csa/base/application/models/datasources/oc-rest-datasource
 * @alias module:oc-rest-datasource
 * @requires module:browser
 * @requires module:libs
 * @requires module:model
 * @requires module:entity
 */
define([
  'browser',
  'libs',
  'datasource',
  'entity'
], function (browser, libs, Datasource, Entity) {
  'use strict';

  var console = browser.console;
  var location = browser.location;
  var sessionStorage = browser.sessionStorage;
  var $ = libs.$;
  var toastr = libs.toastr;

  /**
   * @class
   */
  function OcRestDatasource (model) {
    console.assert(model instanceof Entity, 'Datasource "model" must be an instanceof Entity');

    Datasource.apply(this, arguments);
  }

  OcRestDatasource.prototype = Object.create(Datasource.prototype, {

    /**
     * @property
     * @lends OcRestDatasource
     */
    constructor: {
      value: OcRestDatasource
    },

    /**
     * An object of numeric HTTP codes and functions to be called when the response has the corresponding code.
     * For example, the following will alert when the response status is a 404.
     *
     * @property
     * @lends OcRestDatasource
     */
    statusCode: {
      value: {
        409: function conflict () {
          // Force full load of the current page to ensure a fresh session is created.
          location.reload();
        }
      }
    },

    /**
     * @property
     * @lends OcRestDatasource
     */
    execute: {
      value: function execute (options) {
        console.assert(options && typeof options.url === 'string', 'Datasource "options" must specify a url.');

        // initialize request options.
        options = this.initOptions(options);

        // Ensure request options are set.
        // Return the promise for call chaining.
        this.model.promise = this.model.promise
          // Chain the rest service call.
          .then(function () {
            return $.ajax(options);
          });

        return this.model.promise;
      }
    },

    /**
     * @property
     * @lends OcRestDatasource
     */
    initOptions: {
      value: function initOptions (options) {
        options = $.extend(
          {
            type: 'GET',
            data: {}
          },
          options,
          {
            dataType: this.dataType,
            contentType: this.contentType,
            cache: this.cache,
            context: this,
            statusCode: this.statusCode,
            success: this.executeSuccess,
            error: this.executeError
          });

        // Set the pushSite query parameter value to the ID of the current site. This will
        // ensure that the current site context will be retained for both GET and POST requests.
        var siteId = sessionStorage.getItem('siteId');

        // pushSite query parameter for GET requests.
        options.data.pushSite = siteId;

        // Convert the client language value to the format that the server expects
        // e.g. from en-US to en_US.
        var languageParameter = window.sessionStorage.getItem('language').replace('-', '_');

        if (options.type !== 'GET') {

          // pushSite query parameter and locale query parameter for POST requests.
          options.url += '?pushSite=' + siteId + '&locale=' + languageParameter;

          // For non GET request.
          // Set session confirmation number on the request.
          options.data._dynSessConf = sessionStorage.getItem('_dynSessConf');

          // Replace null values with '' as the 3rd party library we're using to parse gives us the string "null" back.
          options.data = JSON.stringify(options.data, function (key, value) { return (value == null) ? '' : value; });
        }
        else {

          // Including the locale as a request parameter ensures that the server always has
          // access to the correct locale on GET rest requests.
          options.url += '?locale=' + languageParameter;
        }

        return options;
      }
    },

    /**
     * @property
     * @lends OcRestDatasource
     */
    executeSuccess: {
      value: function executeSuccess (data) {
        this.processErrors(data);
        this.processResponseData(data);
      }
    },

    /**
     * @property
     * @lends OcRestDatasource
     */
    processErrors: {
      value: function processErrors (data) {
        // If an error has occurred.
        if (data.error) {
          this.handleError(data);
        }
        // If a form error has occurred.
        else if (data.formError) {
          this.handleFormErrors(data);
        }
      }
    },

    /**
     * @property
     * @lends OcRestDatasource
     */
    handleError: {
      value: function handleError (data) {
        // Display the error message as a toastr message.
        toastr.error(data.error.localizedMessage);
      }
    },

    /**
     * @property
     * @lends OcRestDatasource
     */
    handleFormErrors: {
      value: function handleFormErrors (data) {
        // For each form error.
        data.formExceptions.forEach(function (error) {
          // Display the form error message as a toastr message.
          toastr.error(error.localizedMessage);
        });
      }
    },

    /**
     * @property
     * @lends OcRestDatasource
     */
    processResponseData: {
      value: function processResponseData (data) {
        // Set the model data.
        this.model.set(this.parseResponseData(data));
      }
    },

    /**
     * @property
     * @lends OcRestDatasource
     */
    parseResponseData: {
      value: function parseResponseData (data) {
        if (this.model.entityName) {
          return data[this.model.entityName];
        }
      }
    },

    /**
     * @property
     * @lends OcRestDatasource
     */
    executeError: {
      value: function executeError () {}
    }
  });

  return OcRestDatasource;
});
