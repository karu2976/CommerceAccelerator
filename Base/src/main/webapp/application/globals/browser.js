// jshint browser: true

/**
 * Exposes reference to the browser's global objects. CSA application should avoid referencing
 * browser globals directly, and instead use this module to access the require object. The reason
 * for this is to facilitate dependency injection, allowing the browser globals to be mocked during
 * unit testing.
 *
 * @module csa/base/application/globals/browser
 * @alias module:browser
 */
define(/** @alias module:browser */ window);
