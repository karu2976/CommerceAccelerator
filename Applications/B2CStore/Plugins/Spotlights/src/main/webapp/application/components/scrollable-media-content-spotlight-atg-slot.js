/**
 * @module csa/applications/b2cstore/plugins/spotlights/application/components/scrollable-media-content-spotlight-atg-slot
 * @alias module:scrollable-media-content-spotlight-atg-slot
 * @requires module:libs
 * @requires module:csa/plugins/spotlights/application/components/scrollable-media-content-spotlight-atg-slot
 * @requires module:text!./scrollable-media-content-spotlight-atg-slot.html
 */
define([
  'libs',
  'csa/plugins/spotlights/application/components/scrollable-media-content-spotlight-atg-slot',
  'text!./scrollable-media-content-spotlight-atg-slot.html'
], function (libs, scrollableMediaContentSpotlightATGSlot, template) {
  'use strict';

  var $ = libs.$;
  var i18next = libs.i18next;

  // Get the component class.
  var ScrollableMediaContentSpotlightATGSlot = scrollableMediaContentSpotlightATGSlot.ScrollableMediaContentSpotlightATGSlot;

  // Set the missing image path URL.
  ScrollableMediaContentSpotlightATGSlot.prototype.missingImagePath = '/csadocroot/content/images/products/large/MissingProduct_large.jpg';

  // Extend the component class prototype
  Object.defineProperties(ScrollableMediaContentSpotlightATGSlot.prototype, {

    /**
     * Function to allow user to stop/start carousel
     *
     * @function
     * @lends ScrollableMediaContentSpotlightATGSlot
     */
    controlCarousel: {
      value: function controlCarousel (data, event) {
        var $el = $(event.currentTarget);

        if ($el.data('action') === 'play') {
          $el.closest('.carousel').carousel('cycle');
          $el.data('action', 'pause');
          $el.html('<span class="glyphicon glyphicon-pause" aria-hidden="true"></span>' + '<span class="sr-only">' + i18next.t(this.appLocaleNamespace + ':button.pauseAnimation') + '</span>');
        }
        else {
          $el.closest('.carousel').carousel('pause');
          $el.data('action', 'play');
          $el.html('<span class="glyphicon glyphicon-play" aria-hidden="true"></span>' + '<span class="sr-only">' + i18next.t(this.appLocaleNamespace + ':button.startAnimation') + '</span>');
        }
      }
    }
  });

  // Override the component template.
  scrollableMediaContentSpotlightATGSlot.template = template;

  // The component defintion.
  return scrollableMediaContentSpotlightATGSlot;
});
