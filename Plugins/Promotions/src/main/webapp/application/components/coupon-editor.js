/**
 * @module csa/plugins/promotions/application/components/coupon-editor
 * @alias module:coupon-editor
 * @requires module:order
 */
define([
  'libs',
  'content-item',
  'text!./coupon-editor.html',
  'order',
  'oc-rest-claim-coupon',
  'oc-rest-remove-coupon'
], function (libs, ContentItem, template, order, claimCoupon, removeCoupon) {
  'use strict';

  var $ = libs.$;

  function CouponEditor() {
    ContentItem.apply(this, arguments);
  }

  CouponEditor.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends CouponEditor
     */
    constructor: {
      value: CouponEditor
    },

    /**
     * @property
     * @lends CouponEditor
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'couponCode'
      ])
    },

    /**
     * @property
     * @lends CouponEditor
     */
    order: {
      value: order,
      enumerable: true
    },

    /**
     * The claimCoupon function calls the promotion-services claimCoupon web service;
     * which, if valid, applies the promotion to the order, or returns a form error if invalid.
     */
    claimCoupon: {
      value: function () {
        var couponCode = this.couponCode();

        if (couponCode) {
          claimCoupon(couponCode).then(function (response) {

            if (response.formError) {
              response.formExceptions.forEach(function (exception) {
                switch (exception.errorCode){
                  case 'noCouponFound':
                    var $couponCode = $('input[data-name="couponCode"]');
                    $couponCode.setCustomValidity(exception.localizedMessage);
                    $couponCode.trigger('updatevalidation');
                    $couponCode.on('input', function () {
                      this.setCustomValidity('');
                    });
                    break;
                }
              });
            }
            else {
              this.couponCode('');
            }
          }.bind(this));
        }
      }
    },

    /**
     * The removeCoupon function calls the promotion-services removeCoupon web service;
     * which, if valid, removes the promotion to the order, or returns a form error if invalid.
     */
    removeCoupon: {
      value: function () {
        var couponCode = order.couponCode();

        removeCoupon();

        this.couponCode(couponCode);
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
        var viewModel = new CouponEditor().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    CouponEditor: CouponEditor
  };
});
