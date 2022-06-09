/**
 * Exposes 3rd party libraries to a CSA application.
 *
 * @module csa/base/application/globals/libs
 * @alias module:libs
 * @requires module:underscore
 * @requires module:jquery
 * @requires module:crossroads
 * @requires module:i18next
 * @requires module:knockout
 * @requires module:toastr
 * @requires module:bootstrap
 * @requires module:knockout-punches
 */
define([
  'underscore',
  'jquery',
  'crossroads',
  'i18next',
  'knockout',
  'toastr',
  'bootstrap',
  'knockout-punches'
], function (_, $, crossroads, i18next, ko, toastr) {
  'use strict';

  ko.options.deferUpdates = true;
  ko.punches.enableAll();
  ko.punches.textFilter.enableForBinding('optionsCaption');

  toastr.options = {
    closeButton: true,
    positionClass: 'toast-top-right',
    onclick: null,
    showDuration: '300',
    hideDuration: '1000',
    timeOut: '3000', // How long the toast will display without user interaction (0 = no timeout)
    extendedTimeOut: '6000', // How long the toast will display after a user hovers over it (0 = no timeout)
    showEasing: 'swing', // 'swing' or 'linear' (built in to jQuery) - see http://api.jquery.com/animate/
    hideEasing: 'linear', // 'swing' or 'linear' (built in to jQuery) - see http://api.jquery.com/animate/
    showMethod: 'fadeIn', // 'fadeIn', 'slideDown', 'slideUp' or 'show'
    hideMethod: 'fadeOut' // 'fadeOut' 'slideDown', 'slideUp' or 'hide'
  };

  return /** @alias module:libs */ {

    /** @alias module:underscore */
    _: _,

    /** @alias module:jquery */
    $: $,

    /** @alias module:crossroads */
    crossroads: crossroads,

    /** @alias module:i18next */
    i18next: i18next,

    /** @alias module:knockout */
    ko: ko,

    /** @alias module:toastr */
    toastr: toastr
  };
});
