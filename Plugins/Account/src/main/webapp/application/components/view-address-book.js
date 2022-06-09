/**
 * Render for the ViewAddressBook cartridge. Views the addresses associated with the profile.
 *
 * @module csa/plugins/account/application/components/view-address-book
 * @requires module:content-item
 * @requires module:text!./view-address-book.html
 * @requires module:profile
 * @requires module:profile-services
 * @requires module:libs
 */
define([
  'content-item',
  'text!./view-address-book.html',
  'profile',
  'profile-services'
], function (ContentItem, template, profile, profileServices) {
  'use strict';

  function ViewAddressBook() {
    ContentItem.apply(this, arguments);
  }

  // Define class prototype
  ViewAddressBook.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends ViewAddressBook
     */
    constructor: {
      value: ViewAddressBook
    },

    /**
     * @property
     * @lends ViewAddressBook
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'addresses'
      ])
    },

    /**
     * Removes an address whose nickname is passed in within the viewmodel data. The response from
     * the server is then mapped to the addresses property.
     *
     * @function
     * @lends AddCreditCard
     * @param {Object} newData An object of new data values for the model properties.
     */
    removeAddress: {
      value: function removeAddress (address) {
        var remove = address.addressNickname;

        return profileServices.removeAddress(remove).then(function () {
          this.addresses(profile.addresses());
        }.bind(this));
      }
    }
  });

  // The component defintion.
  return {

    // The component HTML template.
    template: template,

    // The component view model factory.
    viewModel: {
      createViewModel: function createViewModel (params) {
        var viewModel = new ViewAddressBook().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    ViewAddressBook: ViewAddressBook
  };
});
