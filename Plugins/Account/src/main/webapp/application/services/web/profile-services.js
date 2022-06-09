/**
 * Services class for profile related operations.
 *
 * @module csa/plugins/account/application/services/web/profile-services
 * @requires module:browser
 * @requires module:profile
 */
define([
  'browser',
  'profile'
], function (browser, profile) {
  'use strict';

  var location = browser.location;

  return Object.create({}, {

    /**
     * Checks the status of the user to determine if they are logged in. Returns some details about
     * the current profile such as the firstName.
     *
     * Output: (example)
     *   {"profile":{
     *      "lastName":"Moore",
     *      "securityStatus":4,
     *      "email":"lisa@example.com",
     *      "firstName":"Lisa"
     *     }
     *   }
     */
    init: {
      value: function init () {
        // Define rest options
        var options = {
          serviceId: 'getProfileInit',
          url: '/rest/model/atg/userprofiling/ProfileActor/init',
          type: 'GET'
        };

        // Call rest service
        return profile.datasource.execute(options);
      }
    },

    /**
     * Creates a new address on the current profile.
     *
     * Input:
     *   address - An address object which may contain the following values
     *     addressNickname: The nickname in the address book.
     *     firstName: The first name of the customer associated with this address.
     *     middleName: The middle name or initial of the customer associated with this address.
     *     lastName: The last name of the customer associated with this address.
     *     address1: The first address field of the address.
     *     address2: The second address field of the address.
     *     city: The city of the address.
     *     state: The state or province of the address.
     *     postalCode: The postal code of the address.
     *     country: The country of the address.
     *     phoneNumber: The phone number associated with this address.
     *     useAsDefaultShippingAddress: Sets this address as the default shipping address if 'true'.
     *
     * Output:
     *   {}
     */
    addAddress: {
      value: function addAddress (address) {
        // Define rest options
        var options = {
          url: '/rest/model/atg/userprofiling/ProfileActor/addAddress',
          type: 'POST',
          data: address
        };

        // Call rest service
        return profile.datasource.execute(options);
      }
    },

    /**
     * Update an address identified by the nickname on the current user profile.
     *
     * Input:
     *   address - An address object to update which may contain the following values
     *     addressNickname: The nickname in the address book.
     *     newAddressNickname: The new nickname for this address.
     *     firstName: The first name of the customer associated with this address.
     *     middleName: The middle name or initial of the customer associated with this address.
     *     lastName: The last name of the customer associated with this address.
     *     address1: The first address field of the address.
     *     address2: The second address field of the address.
     *     city: The city of the address.
     *     state: The state or province of the address.
     *     postalCode: The postal code of the address.
     *     country: The country of the address.
     *     phoneNumber: The phone number associated with this address.
     *     useAsDefaultShippingAddress: Sets this address as the default shipping address if 'true'.
     *
     * Output:
     *   {}
     */
    updateAddress: {
      value: function updateAddress (address) {
        // Define rest options
        var options = {
          url: '/rest/model/atg/userprofiling/ProfileActor/updateAddress',
          type: 'POST',
          data: address
        };

        // Call rest service
        return profile.datasource.execute(options);
      }
    },

    /**
     * Deletes the address removeAddressNickname on the current customer profile.
     *
     * Input:
     *   removeAddressNickname - The nickname in the address book to remove.
     *
     * Output: (example)
     *   {"addresses":
     *     {"secondaryAddresses":[
     *        {"lastName":"Schmidt",
     *         "useAsDefaultShippingAddress":true,
     *         "address1":"457 Main St",
     *         "repositoryId":"se-980030",
     *         "addressNickname":"Home",
     *         "firstName":"Stuart"
     *        },
     *        {"lastName":"Schmidt",
     *        "useAsDefaultShippingAddress":false,
     *        "address1":"12 Woodside Ave",
     *        "repositoryId":"se-140011",
     *        "addressNickname":"Father's home",
     *        "firstName":"Andreas"}]
     *      }
     *    }
     */
    removeAddress: {
      value: function removeAddress (removeAddressNickname) {

        var data = {
          removeAddressNickname: removeAddressNickname
        };

        // Define rest options
        var options = {
          url: '/rest/model/atg/userprofiling/ProfileActor/removeAddress',
          type: 'POST',
          data: data
        };

        // Call rest service
        return profile.datasource.execute(options).then(function (response) {
          if (response.addresses) {
            profile.addresses(response.addresses);
          }
        }.bind(this));
      }
    },

    /**
     * Returns detailed information about the addresses associated with the current customer profile.
     *
     * Output: (example)
     *  {"addresses":{
     *   "secondaryAddresses":[{
     *     "lastName":"Schmidt",
     *     "useAsDefaultShippingAddress":true,
     *     "state":"NH",
     *     "address1":"457 Main St",
     *     "address2":"",
     *     "repositoryId":"se-980030",
     *     "city":"Exeter",
     *     "country":"US",
     *     "addressNickname":"Home",
     *     "postalCode":"03833",
     *     "phoneNumber":"212-555-6341",
     *     "firstName":"Stuart"},
     *
     *     {"lastName":"Schmidt",
     *     "useAsDefaultShippingAddress":false,
     *     "state":"NH",
     *     "address1":"12 Woodside Ave",
     *     "address2":"",
     *     "repositoryId":"se-140011",
     *     "city":"Keene",
     *     "country":"US",
     *     "addressNickname":"Father's home",
     *     "postalCode":"03431",
     *     "phoneNumber":"212-555-1234",
     *     "firstName":"Andreas"}]
     *    }
     *  }
     */
    viewAddresses: {
      value: function viewAddresses () {
        // Define rest options
        var options = {
          url: '/rest/model/atg/userprofiling/ProfileActor/addresses',
          type: 'GET'
        };

        // Call rest service
        return profile.datasource.execute(options).then(function (response) {
          if (response.addresses) {
            profile.addresses(response.addresses);
          }
        }.bind(this));
      }
    },

    /**
     * Creates a new address on the current profile.
     *
     * Input:
     *   card - The card object which may contain the following values
     *     cardNickname: The nickname associated with this card.
     *     creditCardType: Type/make of the credit card.
     *     creditCardNumber: Card number.
     *     expirationMonth: Month card expires.
     *     expirationYear: Year card expires.
     *     setAsDefaultCard: Sets this card as the default card if 'true'.
     *     firstName: The first name of the customer associated with this address.
     *     middleName: The middle name or initial of the customer associated with this address.
     *     lastName: The last name of the customer associated with this address.
     *     address1: The first address field of the address.
     *     address2: The second address field of the address.
     *     city: The city of the address.
     *     state: The state or province of the address.
     *     postalCode: The postal code of the address.
     *     country: The country of the address.
     *     phoneNumber: The phone number associated with this address.
     *
     * Output:
     *   {}
     */
    addCard: {
      value: function addCard (card) {
        // Define rest options
        var options = {
          url: '/rest/model/atg/userprofiling/ProfileActor/createCreditCard',
          type: 'POST',
          data: card
        };

        // Call rest service
        return profile.datasource.execute(options);
      }
    },

    /**
     * Returns the (non sensitive) credit card info saved on the current user profile.
     *
     * Output: (example)
     * "paymentInfo": {
     *   "creditCards": [
     *   {
     *     "lastName": "Schmidt",
     *     "setAsDefaultCard": true,
     *     "id": "se-usercc110030",
     *     "expirationMonth": "03",
     *     "expirationYear": "2017",
     *     "creditCardNumber": "6288",
     *     "firstName": "Andreas",
     *     "cardNickname": "Visa"
     *   },
     *   {
     *     "lastName": "p",
     *     "setAsDefaultCard": false,
     *     "id": "usercc30001",
     *     "expirationMonth": "01",
     *     "expirationYear": "2018",
     *     "creditCardNumber": "1111",
     *     "firstName": "p",
     *     "cardNickname": "test"
     *   },
     *   {
     *     "lastName": "Schmidt",
     *     "setAsDefaultCard": false,
     *     "id": "usercc10001",
     *     "expirationMonth": "01",
     *     "expirationYear": "2021",
     *     "creditCardNumber": "1111",
     *     "firstName": "Stuart",
     *     "cardNickname": "Testc"
     *   }]
     * }
     */
    viewCards: {
      value: function viewCards () {
        // Define rest options
        var options = {
          url: '/rest/model/atg/userprofiling/ProfileActor/creditCards',
          type: 'GET'
        };

        // Call rest service
        return profile.datasource.execute(options);
      }
    },

    /**
     * Deletes a card on the current customer profile.
     *
     * Input:
     *   removeCardNickname - The nickname of the card to remove.
     *
     * Output:
     *   The remaining cards, for example
     *   "paymentInfo": {
     *     "creditCards": [
     *     {
     *       "lastName": "Schmidt",
     *       "setAsDefaultCard": true,
     *       "id": "se-usercc110030",
     *       "expirationMonth": "03",
     *       "expirationYear": "2017",
     *       "creditCardNumber": "6288",
     *       "firstName": "Andreas",
     *       "cardNickname": "Visa"
     *     },
     *     {
     *       "lastName": "Schmidt",
     *       "setAsDefaultCard": false,
     *       "id": "usercc10001",
     *       "expirationMonth": "01",
     *       "expirationYear": "2021",
     *       "creditCardNumber": "1111",
     *       "firstName": "Stuart",
     *       "cardNickname": "Testc"
     *     }]
     *   }
     */
    removeCard: {
      value: function removeCard (removeCardNickname) {

        var data = {
          removeCardNickname: removeCardNickname
        };

        // Define rest options
        var options = {
          url: '/rest/model/atg/userprofiling/ProfileActor/removeCard',
          type: 'POST',
          data: data
        };

        // Call rest service
        return profile.datasource.execute(options).then(function (response) {
          if (response.paymentInfo) {
            profile.paymentInfo(response.paymentInfo);
          }
        }.bind(this));
      }
    },

    /**
     * Updates information with the data passed in the card parameter.
     *
     * Input:
     *   card - A card object which may contain the following values
     *     cardNickname: The nickname in the address book.
     *     newCardNickname: The nickname in the address book.
     *     phoneNumber: The phone number associated with this address.
     *     expirationMonth: Month card expires.
     *     expirationYear: Year card expires.
     *     firstName: The first name of the customer associated with this address.
     *     middleName: The middle name or initial of the customer associated with this address.
     *     lastName: The last name of the customer associated with this address.
     *     address1: The first address field of the address.
     *     address2: The second address field of the address.
     *     city: The city of the address.
     *     state: The state or province of the address.
     *     postalCode: The postal code of the address.
     *     country: The country of the address.
     *
     *  Output:
     *    {}
     */
    updateCard: {
      value: function updateCard (card) {
        // Define rest options
        var options = {
          url: '/rest/model/atg/userprofiling/ProfileActor/updateCard',
          type: 'POST',
          data: card
        };

        // Call rest service
        return profile.datasource.execute(options);
      }
    },

    /**
     * Logs a customer into the site.
     *
     * Input:
     *   credentials - A credentials object which may contain the following values
     *     login: The customer username.
     *     password: The customer password.
     *
     * Output: (example)
     *   {
     *     "profile":{
     *       "lastName":"Moore",
     *       "securityStatus":4,
     *       "email":"lisa@example.com",
     *       "firstName":"Lisa"
     *     }
     *   }
     */
    login: {
      value: function login (credentials) {
        // Define rest options
        var options = {
          serviceId: 'login',
          url: '/rest/model/atg/userprofiling/ProfileActor/login',
          type: 'POST',
          data: credentials
        };

        // Call rest service
        return profile.datasource.execute(options);
      }
    },

    /**
    * Logs a customer out of the site.
    *
    * Output: (example)
    *   {
    *     "profile":{"
    *       lastName":null,
    *       "securityStatus":0,
    *       "email":null,
    *       "firstName":null
    *     }
    *   }
    */
    logout: {
      value: function logout () {
        // Define rest options
        var options = {
          serviceId: 'logout',
          url: '/rest/model/atg/userprofiling/ProfileActor/logout',
          type: 'POST'
        };

        return profile.datasource.execute(options).then(function () {
          location.href = 'login';
        });
      }
    },

    /**
    * Creates a new user account.
    *
    * Input:
    *   account - An account object which may contain the following values
    *     login: The account login
    *     email: The users email address
    *     password: The account password
    *     confirmPassword: The account password confirmation
    *     firstName: Users first name
    *     lastName: Users last name
    *     postalCode - (optional) Users zip or postal code.
    *     gender - (optional) Users gender. unknown, unspecified, male or female.
    *     birthday - (optional) Users DOB.
    *
    * Output: (example)
    *   {
    *     "profile":{
    *       "lastName":"Moore",
    *       "securityStatus":4,
    *       "email":"lisa@example.com",
    *       "firstName":"Lisa"
    *     }
    *   }
    */
    create: {
      value: function create (account) {

        // objectParams need to be sent in the atg-rest-* objects.
        account.homeAddress = {
          'atg-rest-class-type': 'java.util.HashMap',
          'atg-rest-values': {
            postalCode: account.postalCode
          }
        };

        // Define rest options
        var options = {
          url: '/rest/model/atg/userprofiling/ProfileActor/create',
          type: 'POST',
          data: account
        };

        // Call rest service
        return profile.datasource.execute(options);
      }
    },

    /**
    * Updates a user account.
    *
    * Input:
    *   account - An account object which may contain the following values
    *     login: The account login
    *     email: The users email address
    *     firstName: Users first name
    *     lastName: Users last name
    *     postalCode: (optional) Users zip or postal code.
    *     gender: (optional) Users gender. unknown, male or female.
    *     birthday: (optional) Users DOB.
    *
    * Output:
    *   {}
    */
    updateProfile: {
      value: function updateProfile (account) {

        // objectParams need to be sent in the atg-rest-* objects.
        account.homeAddress = {
          'atg-rest-class-type': 'java.util.HashMap',
          'atg-rest-values': {
            postalCode: account.postalCode
          }
        };

        // Define rest options
        var options = {
          url: '/rest/model/atg/userprofiling/ProfileActor/update',
          type: 'POST',
          data: account
        };

        // Call rest service
        return profile.datasource.execute(options);
      }
    },

    /**
     * Returns summary information about the current user profile.
     *
     * Output: (example)
     *   {"profile":{
     *     "dateOfBirth":null,
     *     "lastName":"p",
     *     "postalCode":"p",
     *     "email":"test@example.com",
     *     "gender":"unknown",
     *     "login":"test@example.com",
     *     "firstName":"p2"
     *     }
     *   }
     */
    viewProfile: {
      value: function viewProfile () {
        // Define rest options
        var options = {
          url: '/rest/model/atg/userprofiling/ProfileActor/summary',
          type: 'GET'
        };

        // Call rest service
        return profile.datasource.execute(options);
      }
    },

    /**
     * Updates a users password.
     *
     * Input:
     *   account - An account object which may contain the following values
     *     oldPassword: The users current password
     *     newPassword: The users new password
     *     confirmPassword: The users new password confirmation
     *
     * Output:
     *   {}
     */
    changePassword: {
      value: function changePassword (account) {
        var data = {
          confirmPassword : account.confirmPassword,
          oldPassword : account.oldPassword,
          password : account.newPassword
        };

        // Define rest options
        var options = {
          url: '/rest/model/atg/userprofiling/ProfileActor/changePassword',
          type: 'POST',
          data: data
        };

        // Call rest service
        return profile.datasource.execute(options);
      }
    },

    /**
     * Recovers a lost password.
     *
     * Input:
     *   email - The email address to send a recovery mail.
     *
     * Output: {}
     */
    recoverPassword: {
      value: function recoverPassword (email) {
        var data = {
          email : email
        };

        // Define rest options
        var options = {
          url: '/rest/model/atg/userprofiling/ProfileActor/recoverPassword',
          type: 'POST',
          data: data
        };

        // Call rest service
        return profile.datasource.execute(options);
      }
    },

    /**
     * Updates the checkout defaults for the shipping method, shipping address and card.
     *
     * Input:
     *   defaultCarrier - The default shipping method.
     *   defaultAddress - The default address name.
     *   defaultCard - The default card name.
     *
     * Output: {}
     */
    updateCheckoutDefaults: {
      value: function updateCheckoutDefaults (defaultCarrier, defaultAddress, defaultCard) {
        var data = {
          defaultCarrier: defaultCarrier,
          defaultAddress: defaultAddress,
          defaultCard: defaultCard
        };

        // Define rest options
        var options = {
          url: '/rest/model/atg/userprofiling/ProfileActor/updateCheckoutDefaults',
          type: 'POST',
          data: data
        };

        // Call rest service
        return profile.datasource.execute(options);
      }
    },

    /**
     * Validates email login on the server.
     *
     * Input:
     *   email - The users email address
     *
     * Output:
     *   {}
     */
    emailCheck: {
      value: function emailCheck (email) {
        var data = {
          email: email
        };

        // Define rest options
        var options = {
          url: '/rest/model/atg/userprofiling/ProfileActor/emailCheck',
          type: 'POST',
          data: data
        };

        // Call rest service
        return profile.datasource.execute(options);
      }
    }
  });
});
