// jshint browser: true

require.config({
  baseUrl: '/csa/../',
  paths: {
    'bootstrap': 'csa/base/bower_components/bootstrap/dist/js/bootstrap',
    'crossroads': 'csa/base/bower_components/crossroads/dist/crossroads',
    'es6-promise': 'csa/base/bower_components/es6-promise/dist/es6-promise',
    'i18next': 'csa/base/bower_components/i18next/i18next.amd',
    'i18next-builder': 'csa/base/bower_components/require-i18next/require-i18next/i18next-builder',
    'Intl': 'csa/base/bower_components/Intl/dist/Intl.complete',
    'jquery': 'csa/base/bower_components/jquery/dist/jquery',
    'knockout': 'csa/base/bower_components/knockout/dist/knockout',
    'knockout-punches': 'csa/base/bower_components/knockout.punches/knockout.punches',
    'polyfiller': 'csa/base/bower_components/webshim/js-webshim/minified/polyfiller',
    'signals': 'csa/base/bower_components/js-signals/dist/signals',
    'text': 'csa/base/bower_components/requirejs-text/text',
    'toastr': 'csa/base/bower_components/toastr/toastr',
    'underscore': 'csa/base/bower_components/underscore/underscore',
    'url': 'csa/base/bower_components/polyfills/url'
  },
  map: {
    '*': {
      'css': 'csa/base/bower_components/require-css/css',
      'i18n': 'csa/base/launcher/shims/libs/require-i18next',
      'document': 'csa/base/launcher/shims/browser/document',
      'history': 'csa/base/launcher/shims/browser/history',
      'html5': 'csa/base/launcher/shims/browser/html5',
      'location': 'csa/base/launcher/shims/browser/location',
      'es': 'csa/base/launcher/shims/es/es',
      'shims': 'csa/base/launcher/shims/shims'
    }
  },
  // Shim libraries that don't naively support AMD -->
  shim: {
    'bootstrap': {
      deps: [
        'jquery'
      ],
      exports: 'jQuery'
    },

    'url': {
      exports: 'URL'
    }
  },
  i18next: {
    fallbackLng : 'en', // Default locale en
    useLocalStorage : false, // Turn off local storage caching
    useCookie : true, // Enable cookie
    debug : false, // Disable debug logging
    resGetPath: '{{ns}}_{{lng}}.json', // Resource URL template
    load: 'unspecific', // Load 'en' instead of 'en-US' etc.
    cookieName: 'language',
    interpolationPrefix: '{{',
    interpolationSuffix: '}}',
    supportedLngs: { //supportedLngs required for optimised build
      en:['translation'],
      es:['translation'],
      de:['translation']
    }
  },
  waitSeconds: 0
});

// Load the configuration modules.
require(JSON.parse(this.sessionStorage.getItem('configurationURLs')), function main () {

  // Get the promises (if any) returned by each configuration module.
  var configurationPromises = Array.prototype.slice.call(arguments);

  require([
    'app'
  ], function (app) {

    // Start (render) the application after the configuration promises have been resolved.
    app.start(configurationPromises);
  });
});
