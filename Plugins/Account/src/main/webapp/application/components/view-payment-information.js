/**
 * Render for the AccountViewPaymentInformation cartridge. Lets a user view payment
 * information as well as remove a payment information item.
 *
 * @module csa/plugins/account/application/components/view-payment-information
 * @requires module:content-item
 * @requires module:text!./view-payment-information.html
 * @requires module:profile
 * @requires module:profile-services
 * @requires module:libs
 */
define([
  'content-item',
  'text!./view-payment-information.html',
  'profile',
  'profile-services'
], function (ContentItem, template, profile, profileServices) {
  'use strict';

  function ViewPaymentInformation() {
    ContentItem.apply(this, arguments);
  }

  // Define class prototype
  ViewPaymentInformation.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends ViewPaymentInformation
     */
    constructor: {
      value: ViewPaymentInformation
    },

    /**
     * @property
     * @lends ViewPaymentInformation
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'paymentInfo'
      ])
    },

    /**
     * Removes a credit card whose nickname is passed in within the viewmodel data. The
     * response from the server is then mapped to the view model.
     */
    removeCard: {
      value: function removeCard (card) {
        var remove = card.cardNickname;

        return profileServices.removeCard(remove).then(function () {
          this.paymentInfo(profile.paymentInfo());
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
        var viewModel = new ViewPaymentInformation().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    ViewPaymentInformation: ViewPaymentInformation
  };
});
