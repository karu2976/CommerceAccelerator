/**
 * Services component used for location based operations.
 *
 * @module csa/plugins/account/application/services/web/location-services
 * @requires module:entity
 */
define([
  'profile'
], function (profile) {
  'use strict';

  return {

    /**
     * getStatesForCountry:
     *   This service returns the list of states for the given country code. E.g 'US'.
     *
     * Input:
     *   countryCode - country code. e.g 'US'.
     *
     * Ouput: (example)
     *   {"states":[
     *     {"code":"AK","displayName":"AK - Alaska"},
     *     {"code":"AL","displayName":"AL - Alabama"},
     *     ..]
     *   }
     */
    getStatesForCountry : function (countryCode) {

      var params = {
        countryCode : countryCode
      };

      // Define rest options
      var options = {
        url: '/rest/model/atg/commerce/util/StateListActor/states',
        type: 'GET',
        data: params
      };

      // Call rest service
      return profile.datasource.execute(options);
    }

  };
});
