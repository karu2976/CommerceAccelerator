/**
 * @module csa/base/application/component/site-preferences
 * @alias module:site-preferences
 * @requires module:browser
 * @requires module:content-item
 * @requires module:content-item-array
 * @requires module:./site-preferences.html
 */
define([
  'browser',
  'content-item',
  'content-item-array',
  'text!./site-preferences.html'
], function (browser, ContentItem, contentItemArray, template) {
  'use strict';

  var location = browser.location;

  /**
   * @class
   * @extends ContentItem
   */
  function SitePreferences () {
    ContentItem.apply(this, arguments);

    // Set default values.
    this.show(false);
  }

  SitePreferences.prototype = Object.create(ContentItem.prototype, {

    /**
     * @property
     * @lends SitePreferences
     */
    constructor: {
      value: SitePreferences
    },

    /**
     * @property
     * @lends SitePreferences
     */
    observables: {
      value: ContentItem.prototype.observables.concat([
        'currentCountry',
        'currentLanguage',
        'show',
        'selectedSiteOption',
        'selectedLanguageOption'
      ])
    },

    /**
     * @property
     * @lends SitePreferences
     */
    observableArrays: {
      value: ContentItem.prototype.observableArrays.concat([
        'siteOptions',
        'siteLanguages',
        'selectedSiteLanguageOptions'
      ])
    },

    /**
     * Listener for 'country/language' switcher event.
     */
    showSiteLanguageSwitcher: {
      value: function showSiteLanguageSwitcher () {
        this.show(true);
      }
    },

    /**
     * Ensure that the pre-selected country option is set to the current site country.
     */
    updateCountryOption: {
      value: function updateCountryOption (option, item) {
        if (item.displayName === this.currentCountry()) {
          this.selectedSiteOption(item.url);
          this.updateLanguages();
        }
      }
    },

    /**
     * When the country option is changed, this function retrieves it's corresponding
     * languages and sets them in the selectedSiteLanguageOptions observableArray.
     */
    updateLanguages: {
      value: function updateLanguages () {
        var options = [];

        this.siteLanguages().forEach(function (data) {
          var selectedSiteKey = this.selectedSiteOption();
          var currentLanguageKey = Object.keys(data);

          if (selectedSiteKey === currentLanguageKey[0]) {
            var languages = data[currentLanguageKey];

            if (languages) {
              languages.forEach(function (language) {
                options.push({
                  'displayName' : language.languageDisplayName,
                  'url' : language.language
                });
              });

              this.selectedSiteLanguageOptions(options);
            }
          }
        }, this);
      }
    },

    /**
     * This function reloads the application with the chosen country and language.
     */
    updateSitePreferences: {
      value: function updateSitePreferences () {
        if (this.selectedSiteOption() && this.selectedLanguageOption()) {

          var url = location.origin + this.selectedSiteOption() + this.selectedLanguageOption().split('_')[0] + '/home';

          location.href = url;
        }
        else {
          throw new Error('To change preferences site (country) and language must be selected');
        }
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
        var viewModel = new SitePreferences().init(params);

        return viewModel;
      }
    },

    // The component view model class.
    SitePreferences: SitePreferences
  };
});
