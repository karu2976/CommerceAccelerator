/**
 * @module csa/base/application/models/domain/profile/profile
 * @alias module:profile
 * @requires module:browser
 * @requires module:libs
 * @requires module:entity
 * @requires module:oc-rest-datasource
 */
define([
  'browser',
  'libs',
  'entity',
  'oc-rest-datasource'
], function (browser, libs, Entity, Datasource) {
  'use strict';

  var location = browser.location;
  var ko = libs.ko;

  /**
   * @class
   */
  function Profile () {
    Entity.apply(this, arguments);

    // Set default values.
    this.securityStatus(0);

    // Define the model datasource.
    this.datasource = new Datasource(this);

    // Define a rule that forces a reload when the user becomes deauthenticated.
    ko.computed(function () {
      if (this.authenticated()) {
        ko.computed(function () {
          if (!this.authenticated()) {
            // Force full load of the current page to ensure client domain information is purged.
            location.reload();
          }
        }, this);
      }
    }, this);
  }

  Profile.prototype = Object.create(Entity.prototype, {

    /**
     * @property
     * @lends Profile
     */
    constructor: {
      value: Profile
    },

    /**
     * @property
     * @lends Profile
     */
    entityName: {
      value: 'profile'
    },

    /**
     * @property
     * @lends Profile
     */
    observables: {
      value: Entity.prototype.observables.concat([
        'dateOfBirth',
        'email',
        'firstName',
        'gender',
        'lastName',
        'login',
        'postalCode',
        'securityStatus'
      ]),
    },

    /**
     * @property
     * @lends Profile
     */
    observableArrays: {
      value: Entity.prototype.observableArrays.concat([
        'paymentInfo',
        'addresses'
      ])
    },
    /**
     * @property
     * @lends Profile
     */
    computeds: {
      value: Entity.prototype.computeds.concat([
        'authenticated'
      ])
    },

    /**
     * @function
     * @lends Profile
     */
    authenticated: {
      value: function authenticated () {
        return this.securityStatus() > 0;
      },
      writable: true
    }
  });

  return new Profile();
});
