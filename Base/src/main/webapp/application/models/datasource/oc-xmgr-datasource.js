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
  'datasource'
], function (browser, libs, Datasource) {
  'use strict';

  var console = browser.console;
  var document = browser.document;
  var history = browser.history;
  var location = browser.location;
  var sessionStorage = browser.sessionStorage;
  var URL = browser.URL;
  var $ = libs.$;

  /**
   * @class
   */
  function OcXmgrDatasource () {
    Datasource.apply(this, arguments);
  }

  OcXmgrDatasource.prototype = Object.create(Datasource.prototype, {

    /**
     * @property
     * @lends OcXmgrDatasource
     */
    constructor: {
      value: OcXmgrDatasource
    },

    /**
     * An object of numeric HTTP codes and functions to be called when the response has the corresponding code.
     * For example, the following will alert when the response status is a 404.
     *
     * @property
     * @lends OcXmgrDatasource
     */
    statusCode: {
      value: {
        404: function notFound () {
          location.reload();
        },
        500: function internalServerError () {
          location.reload();
        }
      }
    },

    /**
     * The loads method loads (via AJAX) content (as JSON) from the targetURL. If the response is
     * successful, call the executeSuccess method. If the response is an error call the loadError
     * method.
     *
     * @function
     * @lends OcXmgrDatasource
     */
    execute: {
      value: function execute (options) {
        console.assert(options && typeof options.url === 'string', 'Datasource "options" must specify a url.');

        // If an AJAX request is already in progress.
        if (this.xhr) {
          // Abort it.
          this.xhr.abort();
        }

        var targetURL = options.url;

        // When the targetURL has a trailing slash and is the same as the baseURI, this means
        // that the path doesn't have a contentPath e.g. /home or /browse etc. So just ensure
        // that the targetURL matches the baseURI exactly for subsequent comparisons.
        if (targetURL.lastIndexOf('/') === (targetURL.length - 1) && (targetURL === document.baseURI)) {
          targetURL = targetURL.substring(0, targetURL.length - 2);
        }

        // When the targetURL path length is smaller than the baseURI length and the baseURI
        // actually contains the full targetURL, just make the targetURL the same as the baseURI.
        // For example, if the targetURL is /csa/storeus and the baseURI is /csa/storeus/en,
        // the targetURL will be updated with the '/en' part of the path. This will indicate
        // later on in the code that the targetURL doesn't have a contentPath e.g. '/home' or
        // '/browse' etc.
        if (targetURL.length < document.baseURI.length && document.baseURI.indexOf(targetURL) !== -1) {
          targetURL = document.baseURI;
        }

        // Create target URL object.
        targetURL = new URL(targetURL, document.baseURI);

        // We only want to rewrite the targetURL if the baseURI is currently in the URL.
        if (targetURL.pathname.indexOf(document.baseURI.split(
            location.origin)[1].substring(0, document.baseURI.length - 1)) !== -1) {

          // Site context roots can be in the form of '/', '/csa', '/csa/foo' etc.
          // Using the above examples, virtual context roots can also then be added
          // to the context roots e.g. '/store1', 'csa/store1', '/csa/foo/store1'.
          //
          // To ensure that the application automatically opens at the home page when
          // a user only enters the context root or virtual context root, a check
          // will be done that checks the target URL path (entered by the user)
          // against the application's baseURI property. So for example, if the user
          // enters http://host:port/csa and the baseURI value is
          // http://host:port/csa/store1, the following logic will determine that the
          // user's target URI path is shorter than the baseURI path and append
          // 'home' to URI path. In this case the default virtual context root
          // will be used by the server.
          //
          // Similarly, if the user enters http://host:port/csa/store1 and the baseURI value
          // is http://host:port/csa/store1, the following logic will determine that the
          // user's target URI path is the same length than the baseURI path and append
          // 'home' to URI path.

          // Retrieve the context path part of the baseURI e.g. /csa/store1/
          var contextPath = document.baseURI.split(location.origin);

          // If the context path has a trailing forward slash, remove it. This will
          // keep the path in a consistent format so that it can be compared with the
          // target path.
          var contextPathArr = contextPath[1].replace(/\/$/, '').split('/');

          // If the target path has a trailing forward slash, remove it. This will
          // keep the path in a consistent format so that it can be compared with
          // the base path.
          var targetPathNameArr = location.pathname.replace(/\/$/, '').split('/');

          if (targetURL.href !== document.baseURI || targetPathNameArr.length !== 0) {

            // Check if target path has any thing other then context root (e.g. /csa) or the
            // virtual context root (e.g. /csa/store1). If present then append it to baseURI.
            var subPathIndex = 0;

            while (targetPathNameArr[subPathIndex] === contextPathArr[subPathIndex]) {
              if (++subPathIndex >= Math.min(targetPathNameArr.length, contextPathArr.length)) {
                break;
              }
            }

            if (subPathIndex < targetPathNameArr.length) {
              targetURL.pathname = contextPath[1] + targetPathNameArr.slice(subPathIndex).join('/');
            }
          }
        }

        // Append 'format=json' to search parameters.
        targetURL.searchParams.set('format', 'json');

        // Define request options.
        $.extend(options, {
          url: targetURL.href,
          type: 'GET',
          dataType: this.dataType,
          cache: this.cache,
          context: this,
          statusCode: this.statusCode,
          success: this.executeSuccess,
          error: this.executeError
        });

        // Get content from URL.
        this.xhr = $.ajax(options);

        // Return the xhr promise for call chaining.
        return this.xhr;
      }
    },

    /**
     * The executeSuccess method is invoked on every successful execute request. The executeSuccess
     * does the following:
     *
     * <ul>
     *   <li>Map the response data into the viewModel object.</li>
     *   <li>
     *     Detect if a redirect occurred (i.e. if the response URL pathname is different from the
     *     current request URL pathname); if so, update the current pathname with the response pathname.
     *   </li>
     * </ul>
     *
     * @function
     * @lends OcXmgrDatasource
     */
    executeSuccess: {
      value: function executeSuccess (data) {

        // Map response data.
        this.model.set(data);

        // Handle redirect.
        // Get the assembler request information.
        var siteState = data['endeca:siteState'];

        // Get the response path, i.e. the URL path associated with the HTTP response.
        var responsePath = siteState ? siteState.contentPath : null;

        // Get the language code from sessionStorage e.g. get 'en' from 'en_US'.
        var languageCode = sessionStorage.getItem('language').slice(0, 2);

        if (responsePath) {

          // Create the response URL.
          var responseURL = new URL(responsePath.replace(/^\//, ''), document.baseURI);

          // Create the current URL.
          var currentURL = new URL(location.href);

          // If the response path is not the same as the current path.
          if (responseURL.pathname.replace('/' + languageCode + '/', '/').replace(/\/$/, '') !==
              currentURL.pathname.replace('/' + languageCode + '/', '/').replace(/\/$/, '')) {

            // A redirect has occurred. Update the current path with the response path.
            currentURL.pathname = responseURL.pathname;

            // Ensure the browser address bar reflects the URL change.
            history.replaceState(null, null, currentURL.href);
          }
        }
      }
    },

    /**
     * The executeError method is invoked  on every failed execute request.
     *
     * @function
     * @lends OcXmgrDatasource
     */
    executeError: {
      value: function executeError () {}
    }
  });

  return OcXmgrDatasource;
});
