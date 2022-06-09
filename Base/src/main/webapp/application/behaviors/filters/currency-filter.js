/**
 * @module csa/base/application/behaviors/filters/currency-filter
 * @alias module:currency-filter
 * @requires module:browser
 * @requires module:libs
 */
define([
  'browser',
  'libs'
], function (browser, libs) {
  'use strict';

  var sessionStorage = browser.sessionStorage;
  var ko = libs.ko;

  // Get the language from session storage.
  var language = sessionStorage.getItem('language');

  // Get the default currency from session storage.
  var defaultCurrency = sessionStorage.getItem('currency');

  var formatPrice = function (value, currency) {
    var originalValue = value;

    if (typeof value !== 'number') {
      // Convert string to number
      value = parseFloat(value.toString().replace(/\D+.-|,/g, ''), 10);
    }

    // Return the original value if result of parseFloat is NaN.
    if (isNaN(value)) {
      return originalValue;
    }

    // Format the currency value.
    return value.toLocaleString(language, {
      style: 'currency',
      currency: currency,
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  };

  ko.filters.currency = function (value, currency) {
    value = ko.unwrap(value);
    currency = ko.unwrap(currency) || defaultCurrency;

    // Null values are valid but don't need formatting.
    if (value == null) {
      return;
    }

    var prices = value.toString().split('-');

    if (prices.length === 2) {
      // price range
      return formatPrice(prices[0], currency) + ' - ' + formatPrice(prices[1], currency);
    } else {
      // single price
      return formatPrice(prices[0], currency);
    }
  };
});
