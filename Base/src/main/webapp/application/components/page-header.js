/**
 * @module csa/base/application/component/page-header
 * @alias module:page-header
 * @requires module:libs
 * @requires module:content-item
 * @requires module:content-item-array
 * @requires module:./page-header.html
 */
define([
  'libs',
  'content-item',
  'content-item-array',
  'text!./page-header.html'
], function (libs, ContentItem, contentItemArray, template) {
  'use strict';

  var $ = libs.$;

  /**
   * @class
   * @extends ContentItem
   */
  function PageHeader () {
    ContentItem.apply(this, arguments);
  }

  PageHeader.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends PageHeader
     */
    constructor: {
      value: PageHeader
    },

    /**
     * @property
     * @lends PageHeader
     */
    associations: {
      value: ContentItem.prototype.associations.concat([
        {
          name: 'headerActions',
          factory: contentItemArray
        },
        {
          name: 'headerContent',
          factory: contentItemArray
        }
      ])
    },

    /**
     * The primary page branding logo fpr the site. This sould be overridden by each application.
     *
     * @property
     * @lends PageHeader
     */
    siteLogo: {
      value: {
        src: '',
        alt: '',
        title: '',
        height: 0,
        width: 0
      }
    },

    /**
     * Function to allow a keyboard user to jump to the main
     * site content, bypassing the header block. Focus is
     * set to the main element, first setting its tabindex
     * to ensure it gets input focus and not just visual focus
     *
     * @function
     * @lends PageHeader
     */
    skipToContent: {
      value: function () {
        $('main').attr('tabindex', '-1').focus();
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
        var viewModel = new PageHeader().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    PageHeader: PageHeader
  };
});
