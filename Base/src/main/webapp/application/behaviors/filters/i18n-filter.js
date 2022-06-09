/**
 * @module csa/base/application/behaviors/filters/i18n-filter
 * @alias module:i18n-filter
 * @requires module:libs
 */
define([
  'libs'
], function (libs) {
  'use strict';

  var _ = libs._;
  var i18next = libs.i18next;
  var ko = libs.ko;

  /**
   * A knockout text filter that returns a translated string resource for the given key.
   * Use in templates: <span data-bind="text: <key> | i18n: [<arg1>: <arg2>] "></span>
   * e.g. <span data-bind="text: link.add | i18n"></span>
   * or with double curly brace syntax: {{ 'link.add' | i18n }}
   *
   * @function
   *   @param {String} key - the namespaced key of resource in this modules locales file.
   */
  ko.filters.i18n = function (key) {
    key = ko.unwrap(key);

    if (!key) {
      throw new Error('The i18n text filter requires a key.');
    }

    // create a params array to pass to the i18next translate
    // function - need to remove the "key" argument using _.rest
    var params = _.map(_.rest(_.toArray(arguments), 1), function (param) {
      return ko.unwrap(param);
    });

    return i18next.t(key, params);
  };

  /**
   * A knockout text filter that returns a translated string resource for the given key
   * and is able to return a plural alternative if the count parameter is not 1.
   * Use in templates: <span data-bind="text: <key> | i18nPlural: count [:<arg1> :<arg2>] "></span>
   * e.g. <span data-bind="text: heading.itemsInCart | i18nPlural: totalItems"></span>
   * or with double curly brace syntax: {{ 'link.itemsInCart' | i18nPlural: totalItems }}
   *
   * @function
   *   @param {String} key - the namespaced key of resource in this modules locales file.
   *   @param {Number} count - if 1 return singular version of resource, plural otherwise.
   */
  ko.filters.i18nPlural = function (key, count) {
    key = ko.unwrap(key);
    count = ko.unwrap(count);

    if (!key) {
      throw new Error('The i18nPlural text filter requires a key.');
    }

    if (isNaN(parseInt(ko.unwrap(count), 10))) {
      throw new Error('The i18nPlural text filter requires a count.');
    }

    // create a params array to pass to the i18next translate
    // function - need to remove the "key" argument using _.rest
    // NOTE - we don't remove the count argument as it may be used
    // inside the resource string which looks up the params by index
    var params = _.map(_.rest(_.toArray(arguments), 1), function (param) {
      return ko.unwrap(param);
    });

    // add the count property, used by i18next to
    // determine whether or not to use plural string
    params.count = count;

    return i18next.t(key, params);
  };
});
