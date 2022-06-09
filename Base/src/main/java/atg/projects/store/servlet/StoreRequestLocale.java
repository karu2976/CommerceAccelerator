/*<ORACLECOPYRIGHT>
 * Copyright (C) 1994, 2017, Oracle and/or its affiliates. All rights reserved.
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 * UNIX is a registered trademark of The Open Group.
 *
 * This software and related documentation are provided under a license agreement
 * containing restrictions on use and disclosure and are protected by intellectual property laws.
 * Except as expressly permitted in your license agreement or allowed by law, you may not use, copy,
 * reproduce, translate, broadcast, modify, license, transmit, distribute, exhibit, perform, publish,
 * or display any part, in any form, or by any means. Reverse engineering, disassembly,
 * or decompilation of this software, unless required by law for interoperability, is prohibited.
 *
 * The information contained herein is subject to change without notice and is not warranted to be error-free.
 * If you find any errors, please report them to us in writing.
 *
 * U.S. GOVERNMENT RIGHTS Programs, software, databases, and related documentation and technical data delivered to U.S.
 * Government customers are "commercial computer software" or "commercial technical data" pursuant to the applicable
 * Federal Acquisition Regulation and agency-specific supplemental regulations.
 * As such, the use, duplication, disclosure, modification, and adaptation shall be subject to the restrictions and
 * license terms set forth in the applicable Government contract, and, to the extent applicable by the terms of the
 * Government contract, the additional rights set forth in FAR 52.227-19, Commercial Computer Software License
 * (December 2007). Oracle America, Inc., 500 Oracle Parkway, Redwood City, CA 94065.
 *
 * This software or hardware is developed for general use in a variety of information management applications.
 * It is not developed or intended for use in any inherently dangerous applications, including applications that
 * may create a risk of personal injury. If you use this software or hardware in dangerous applications,
 * then you shall be responsible to take all appropriate fail-safe, backup, redundancy,
 * and other measures to ensure its safe use. Oracle Corporation and its affiliates disclaim any liability for any
 * damages caused by use of this software or hardware in dangerous applications.
 *
 * This software or hardware and documentation may provide access to or information on content,
 * products, and services from third parties. Oracle Corporation and its affiliates are not responsible for and
 * expressly disclaim all warranties of any kind with respect to third-party content, products, and services.
 * Oracle Corporation and its affiliates will not be responsible for any loss, costs,
 * or damages incurred due to your access to or use of third-party content, products, or services.
 </ORACLECOPYRIGHT>*/

package atg.projects.store.servlet;

import java.util.Arrays;
import java.util.Locale;

import atg.core.net.URLUtils;
import atg.core.util.StringUtils;
import atg.multisite.Site;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteManager;
import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.RequestLocale;
import atg.userprofiling.ProfileRequestLocale;

import java.util.List;

/**
 * Extension of the OOTB RequestLocale.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/servlet/StoreRequestLocale.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreRequestLocale extends ProfileRequestLocale {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/servlet/StoreRequestLocale.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------

  /** The name of the 'locale' request parameter. */
  public static final String LANG_SELECTION_PARAMETER = "locale";

  /** The name of the 'unset' property. */
  public static final String PROFILE_LOCALE_UNSET_VALUE = "unset";

  /** The site's 'languages' property. */
  public static final String LANGUAGES_ATTRIBUTE_NAME = "languages";

  /** The site's default country attribute name. */
  public static final String DEFAULT_COUNTRY_ATTRIBUTE_NAME = "defaultCountry";

  /** The site's default language attribute name. */
  public static final String DEFAULT_LANGUAGE_ATTRIBUTE_NAME = "defaultLanguage";

  /** Logging component */
  private static final ApplicationLogging mLogger =
    ClassLoggingFactory.getFactory().getLoggerForClass(StoreRequestLocale.class);

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------

  //------------------------------------
  // property: loggingDebug
  //------------------------------------
  private boolean mLoggingDebug = false;

  /**
   * @param pLoggingDebug
   *   Whether or not to use the debug logging flag.
   */
  public void setLoggingDebug(boolean pLoggingDebug) {
    mLoggingDebug = pLoggingDebug;
  }

  /**
   * @return
   *   Whether or not to use the debug logging flag.
   */
  public boolean isLoggingDebug() {
    return mLoggingDebug;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * Obtains the locale from the http request.
   *
   * @param pRequest DynamoHttpServletRequest
   *    The HTTP request object.
   * @param pRequestLocale
   *   The request locale object
   * @return
   *   The locale object.
   */
  @Override
  public Locale discernRequestLocale(DynamoHttpServletRequest pRequest, RequestLocale pRequestLocale) {

    // Try to retrieve the locale from the 'locale' query parameter. This will likely
    // have been set on a rest request URL path or some other non-standard URL.
    // E.g. /rest/model/atg/blah?locale=en_US
    Locale locale = fillLocaleFromLangSelection(pRequest);

    if (locale == null) {

      // Try to retrieve the locale from the request URL path.
      // E.g. /csa/storeus/en/browse
      locale = getLocaleFromPath(pRequest);
    }

    // The locale should have been retrieved by now for a multi-language EAC app set-up.
    // The following locale processing will only really need to happen for single locale sites.

    if (locale == null) {
      // Try to retrieve the default locale for the current site.
      locale = RequestLocale.getCachedLocale(getDefaultSiteLocaleCode());
    }

    if (locale == null) {
      locale = super.discernRequestLocale(pRequest, pRequestLocale);
    }

    if (isLoggingDebug()) {
      mLogger.logDebug(locale + " will be used as the current RequestLocale.");
    }

    return locale;
  }

  /**
   * When the language has been set in the request path, extract and return it's full
   * locale value. For example, if the path is /csa/storeus/en/home and the site's
   * default country code is US; the resulting locale will be en_US.
   *
   * @param pRequest The current request.
   * @return The locale from the base url.
   */
  protected Locale getLocaleFromPath(DynamoHttpServletRequest pRequest) {
    Locale locale = null;

    StringBuilder contentPath =
      new StringBuilder(URLUtils.urlFromPunycode(pRequest.getRequestURI()));

    Site site = SiteContextManager.getCurrentSite();

    if (site != null) {
      String baseURL =
        (String) site.getPropertyValue(SiteManager.getSiteManager().getProductionURLPropertyName());

      // Do check with trailing "/" so we don't remove start of a content path that is not actually
      // the baseURL e.g. do not remove "/csa/storede" from "/csa/storedetails/.." do not remove
      // siteBaseURL if the base url is "/".
      if (contentPath.indexOf(baseURL + "/") == 0 && !baseURL.contentEquals("/")) {
        contentPath.delete(0, baseURL.length());
      }

      List<String> storeLanguages = (List<String>) site.getPropertyValue(LANGUAGES_ATTRIBUTE_NAME);

      // Try to determine a valid language code i.e. '/en/', from somewhere in the path.
      if (storeLanguages != null) {
        for (String language : storeLanguages) {
          if (contentPath.indexOf("/" + language + "/") != -1
              || contentPath.indexOf("/" + language) == (contentPath.length() - 3)) {
            locale = RequestLocale.getCachedLocale(language + "_"
              + ((String) site.getPropertyValue(DEFAULT_COUNTRY_ATTRIBUTE_NAME)).toUpperCase());

            break;
          }
        }
      }

      if (locale != null) {
        if (!isLocaleValid(locale)) {
          if (isLoggingDebug()) {
            mLogger.logDebug("Invalid Locale has been found on path.");
          }
          locale = null;
        }
        else {
          if (isLoggingDebug()) {
            mLogger.logDebug("Valid Locale has been found on path.");
          }
        }
      }
    }

    return locale;
  }

  /**
   * Set the language based on the locale request parameter.
   *
   * @param pRequest
   *   The DynamoHttpServletRequest object.
   *
   * @return
   *   The locale based on language selection.
   */
  protected Locale fillLocaleFromLangSelection(DynamoHttpServletRequest pRequest) {
    Locale locale = null;

    // The request language selection parameter.
    String langSelection = pRequest.getParameter(LANG_SELECTION_PARAMETER);

    if (Arrays.asList(getValidLocaleNames()).contains(langSelection)) {

      // Initialize the locale using the request language selection.
      locale = RequestLocale.getCachedLocale(langSelection);

      // Check if the locale in the request is applied for the current site. If it's not applied,
      // the method shouldn't return the locale from the request.
      if (!validLanguageForCurrentSite(locale) || !isLocaleValid(locale)) {
        if (isLoggingDebug()) {
          mLogger.logDebug(
            "The supplied query parameter was not valid with the current site.");
        }

        locale = null;
      }
      else if (isLoggingDebug()) {
        mLogger.logDebug("Valid Locale has been found in 'locale' query parameter.");
      }
    }

    return locale;
  }

  /**
   * Determines the default locale code to use for the store. The locale code is constructed from
   * the site default language and site default country code.
   *
   * @return
   *   A valid default store locale code.
   */
  protected String getDefaultSiteLocaleCode() {
    SiteContext currentSiteContext = SiteContextManager.getCurrentSiteContext();

    if (currentSiteContext != null) {
      Site site = currentSiteContext.getSite();

      if (site != null) {
        String storeDefaultCountry = (String) site.getPropertyValue(DEFAULT_COUNTRY_ATTRIBUTE_NAME);

        //Create the locale code from the sites default language and country code.
        String storeDefaultLanguage = (String)site.getPropertyValue(DEFAULT_LANGUAGE_ATTRIBUTE_NAME);

        if (!StringUtils.isEmpty(storeDefaultLanguage) && !StringUtils.isEmpty(storeDefaultCountry)) {
          return storeDefaultLanguage + "_" + storeDefaultCountry;
        }
      }
    }

    return null;
  }

  /**
   * Set the locale based on the current profile.
   *
   * @param pRequest
   *   The DynamoHttpServletRequest object.
   * @param pRequestLocale
   *   The RequestLocale object to be used for setting the locale on the profile.
   *
   * @return Locale based on profile.
   */
  @Override
  public Locale localeFromProfileAttribute(DynamoHttpServletRequest pRequest, RequestLocale pRequestLocale) {
    Locale locale = null;
    Locale profileLocale = super.localeFromProfileAttribute(pRequest, pRequestLocale);

    if (profileLocale != null && !PROFILE_LOCALE_UNSET_VALUE.equals(profileLocale.toString())) {
      locale = RequestLocale.getCachedLocale(profileLocale.toString());

      // Check if the locale in the request is applied for the current site. If it's not applied,
      // the method shouldn't return the locale from the request.
      if (!validLanguageForCurrentSite(locale) || !isLocaleValid(locale)) {
        locale =  null;
      }
    }

    return locale;
  }


  /**
   * Check if language can be applied to current site.
   *
   * @param pLocale
   *   The request locale to  check.
   *
   * @return
   *   True if language can be applied to current site, otherwise false.
   */
  protected boolean validLanguageForCurrentSite(Locale pLocale) {
    if (pLocale == null) {
      return false;
    }

    boolean languageValidForCurrentSite = false;
    SiteContext currentSiteContext = SiteContextManager.getCurrentSiteContext();

    if (currentSiteContext != null) {
      Site site = currentSiteContext.getSite();

      if (site != null) {
        List<String> siteLanguages = (List<String>) site.getPropertyValue(LANGUAGES_ATTRIBUTE_NAME);
        String localeLang = pLocale.getLanguage();

        if (siteLanguages.contains(localeLang)) {
          languageValidForCurrentSite = true;
        }
      }
    }

    return languageValidForCurrentSite;
  }

  /**
   * Determine whether the passed in Locale is valid or not.
   *
   * @param pLocale
   *   The Locale object to be evaluated.
   *
   * @return
   *   'true' if the Locale is valid, otherwise 'false'.
   */
  public boolean isLocaleValid(Locale pLocale) {
      return Arrays.asList(getValidLocales()).contains(pLocale);
  }

}
