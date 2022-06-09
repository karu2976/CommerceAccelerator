/**
 * This is the base CSA application object. This is used to bootstrap the CSA application.
 *
 * @module csa/base/application/application
 * @alias module:application
 * @requires module:browser
 * @requires module:libs
 * @requires module:page
 * @requires module:behaviors
 * @requires module:router
 * @requires module:css!style
 */
define([
  'browser',
  'libs',
  'page',
  'behaviors',
  'router',
  'css!style'
], function (browser, libs, page) {
  'use strict';

  var document = browser.document;
  var history = browser.history;
  var location = browser.location;
  var ko = libs.ko;

  /**
   * @class
   */
  function Application () {}

  Object.defineProperties(Application.prototype, {

    /**
     * When all the configuration promises are resolved (configuration is complete),
     * start the application, i.e. initialize the knockout bindings and load page content.
     *
     * @function
     * @lends Application
     */
    start: {
      value: function (configurationPromises) {
        Promise

          // When all the configuration promises are resolved (configuration complete).
          .all(configurationPromises)

          // Then start the application.
          .then(function () {

            // Initialize the knockout bindings.
            ko.applyBindings(page, document.documentElement);

            // Render any preloaded content.
            var preloadedContent = JSON.parse(sessionStorage.getItem('content'));
            page.set(preloadedContent);

            // If there is no preloaded content, trigger page load.
            if (!preloadedContent) {
              history.replaceState(null, null, location.href, true);
            }
          });
      }
    }
  });

  return new Application();
});
