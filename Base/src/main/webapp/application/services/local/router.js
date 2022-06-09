/**
 * @module csa/base/application/services/local/router
 * @alias module:router
 * @requires module:browser
 * @requires module:libs
 * @requires module:page
 */
define([
  'browser',
  'libs',
  'oc-xmgr-load-page-content'
], function (browser, libs, loadPageContent) {
  'use strict';

  var addEventListener = browser.addEventListener;
  var document = browser.document;
  var history = browser.history;
  var location = browser.location;
  var scrollTo = browser.scrollTo;
  var $ = libs.$;
  var crossroads = libs.crossroads;

  // Create a router instance.
  var router = crossroads.create();

  // Ensure that EVERY router.parse() call is processed.
  router.ignoreState = true;

  // When the state changes.
  addEventListener('changestate', function (event) {

    // Load content for this URL.
    loadPageContent(event.detail.URL).then(function () {

      // For NON popstate events.
      if (!event.detail.popstate) {

        // Force scroll to top of screen to minic normal browser loading behavior.
        scrollTo(0, 0);
      }
    });

    // Notify the router of the URL change.
    router.parse(event.detail.URL);
  });

  function updateState (URL) {

    // If the form action is the current location.
    if (URL === location.href) {

      // Replace the same state to the browser history (simulate a refresh).
      history.replaceState(null, null, URL, true);
    }
    else {

      // Push the new state to the browser history.
      history.pushState(null, null, URL, true);
    }
  }

  // Hijack 'click'.
  $(document).on('click', 'a:not([data-bypass],[target])', function (event) {

    // Prevent the default 'click' behaviour.
    event.preventDefault();

    updateState(this.href);
  });

  // Hijack 'submit' (method="GET" only).
  $(document).on('submit', 'form[method="GET"]', function (event) {

    // As we are hijacking the submit event there is no way of accessing the submitted URL. (The sumitted
    // URL is the form action appended with the form data serialised as a querystring), e.g.:
    //   if ACTION = "browse" and Setialized form data = "x=1&Y=2" then URL = "browse?x=1&Y=2".
    var formData = $(this).serialize();

    // If the form action is not set, use the current location.
    var formAction = this.action || location.href;

    // Build the complete URL
    var URL = formAction + (formData ? '?' + formData : '');

    // Prevent the default 'submit' behaviour.
    event.preventDefault();

    updateState(URL);
  });

  return router;
});
