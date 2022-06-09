/**
 * This script is for enabling Bcc Preview.
 * It checks for the preview environment and defines appropriate bindings.
 *
 * @module csa/plugins/preview/application/preview/bcc-preview
 * @requires module:browser
 * @requires module:libs
 */
define([
  'browser',
  'libs'
], function (browser, libs) {
  'use strict';

  var document = browser.document;
  var location = browser.location;
  var window = browser.window;
  var $ = libs.$;

  return function (promise) {
    var bccPreview = {};

    //check for bcc preview environment
    var parentWindow = window.parent;
    if (parentWindow !== window) {

      var hostAndPort = location.protocol + '//' + location.hostname + (location.port ? ':' + location.port : '');
      //require in the preview.js script from webui preview
      require([
        hostAndPort + '/WebUI-Preview/js/preview.js',
        'custom-bcc-preview-bindings'
      ], function (ocpreview, createCustomBindingsForPreview) {

        bccPreview.ocpreview = ocpreview;

        //add location change listener
        $(window).on('changestate', ocpreview.updateLocation);

        //also invoke it for current location
        ocpreview.updateLocation();

        //add cart change listener
        $(window).on('addItemToOrderSuccess removeItemFromOrderSuccess setOrderSuccess', ocpreview.updatePreviewInfo);

        //Create custom bindings to enable edit from preview
        createCustomBindingsForPreview(ocpreview);

        promise();
      });

      //Also include hotspot css
      var pathForCss = hostAndPort + '/WebUI-Preview/css/hotspots.css';

      var head = document.getElementsByTagName('head')[0];
      var bccCssLink = document.createElement('link');
      bccCssLink.setAttribute('rel', 'stylesheet');
      bccCssLink.setAttribute('type', 'text/css');
      bccCssLink.setAttribute('href', pathForCss);
      head.appendChild(bccCssLink);
    }

    return bccPreview;
  };
});
