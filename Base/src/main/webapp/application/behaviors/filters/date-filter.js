/**
 * @module csa/base/application/behaviors/filters/date-filter
 * @alias module:date-filter
 * @requires module:browser
 * @requires module:libs
 */
define([
  'browser',
  'libs'
], function (browser, libs) {
  'use strict';

  var sessionStorage = browser.sessionStorage;
  var _ = libs._;
  var ko = libs.ko;

  // Get the language from session storage.
  var language = sessionStorage.getItem('language');

  // Default date formatting options.
  var defaultOptions = {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  };

  ko.filters.date = function (value, options) {
    value = ko.unwrap(value);
    options = ko.unwrap(options);

    // Null values are valid but don't need formatting.
    if (value == null) {
      return;
    }

    // Determine date formatting options.
    options = _.defaults(options || {}, defaultOptions);

    // Create Date object from value.
    var date = new Date(value);

    // Format the date.
    var formatedDate = date.toLocaleDateString(language, defaultOptions);

    return formatedDate;
  };
});
