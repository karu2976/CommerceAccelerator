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

package atg.projects.store.assembler.cartridge.handler;

import atg.core.i18n.LocaleUtils;
import atg.endeca.assembler.AssemblerTools;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.ServletUtil;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A cartridge used to render a site preferences link menu.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/assembler/cartridge/handler/SitePreferencesLinkMenu.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class SitePreferencesLinkMenu extends LinkMenu {

  /** Class version string. */
  public static String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/assembler/cartridge/handler/SitePreferencesLinkMenu.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /** Site options content item key */
  public static final String SITE_OPTIONS_NAME = "siteOptions";

  /** Default language property name */
  public static final String DEFAULT_LANGUAGE_PROPERTY_NAME = "defaultLanguage";

  /** Site languages property name */
  public static final String SITE_LANGUAGES_PROPERTY_NAME = "languages";

  /** Default country code property name */
  public static final String DEFAULT_COUNTRY_CODE_PROPERTY_NAME = "defaultCountry";

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Adds menu options and corresponding paths to the content item.
   *
   * <p>
   *   If the menuOptions property contains values, the super.process method
   *   is invoked instead of this one. Because this handler needs to retrieve
   *   values for different sites, the menuOptions property must have paths
   *   with preceding forward slashes i.e. /csa/storeus. The prepended forward
   *   slash should tell the client that the whole context path should be replaced.
   * </p>
   *
   * <p>
   *   When the menuOptions property is empty, the site names and paths will be
   *   retrieved via the buildSiteMenuOptions method in this handler.
   * </p>
   *
   * @param pContentItem
   *   The content item to be processed.
   * @return
   *   A processed content item.
   * @throws com.endeca.infront.assembler.CartridgeHandlerException
   */
  @Override
  public ContentItem process(ContentItem pContentItem) throws CartridgeHandlerException {
    ContentItem contentItem = pContentItem;

    if (super.getMenuOptions() != null) {
      contentItem = super.process(pContentItem);
    }

    contentItem.put(SITE_OPTIONS_NAME, createMenu(buildSiteMenuOptions(), null));
    buildLanguageMenuOptions(contentItem);

    RepositoryItem site = SiteContextManager.getCurrentSite();
    Locale currentUserLocale = ServletUtil.getUserLocale();
    String defaultCountryCode = (String) site.getPropertyValue(DEFAULT_COUNTRY_CODE_PROPERTY_NAME);

    Locale locale = new Locale(currentUserLocale.getLanguage(), defaultCountryCode);
    String displayCountry = locale.getDisplayCountry(currentUserLocale);

    String languageDisplayName = LocaleUtils.getCapitalizedDisplayLanguage(locale, locale);

    contentItem.put("currentCountry", displayCountry);
    contentItem.put("currentLanguage", languageDisplayName);

    return contentItem;
  }

  /**
   * This method retrieves the currently active sites from the SiteManager and
   * adds their displayNames and productionURLs to a map to be returned.
   *
   * @return
   *   A map that contains all of the displayNames and corresponding productionURLs
   *   of the currently active sites. The order in which the sites are added to the
   *   map is maintained.
   *
   * @exception com.endeca.infront.assembler.CartridgeHandlerException
   */
  public LinkedHashMap<String, String> buildSiteMenuOptions() throws CartridgeHandlerException {
    RepositoryItem[] sites = null;

    try {
      sites = SiteManager.getSiteManager().getActiveSites();
    }
    catch (RepositoryException re) {
      throw new CartridgeHandlerException(re);
    }

    if (sites != null && sites.length > 0) {
      LinkedHashMap<String, String> mapMenu = new LinkedHashMap(sites.length);

      for (RepositoryItem site : sites) {
        String language = (String) site.getPropertyValue(DEFAULT_LANGUAGE_PROPERTY_NAME);
        if(language == null){
          AssemblerTools.getApplicationLogging().vlogWarning("Default language for site {0} is null, no entry will appear in the site preferences", site);
          continue;
        }

        String defaultCountryCode = (String) site.getPropertyValue(DEFAULT_COUNTRY_CODE_PROPERTY_NAME);
        if(defaultCountryCode == null){
          AssemblerTools.getApplicationLogging().vlogWarning("Default country code for site {0} is null, no entry will appear in the site preferences", site);
          continue;
        }

        Locale locale = new Locale(language, defaultCountryCode);
        String displayCountry = locale.getDisplayCountry(ServletUtil.getUserLocale());

        mapMenu.put(displayCountry, site.getPropertyValue("productionURL") + "/");
      }
      return mapMenu;
    }
    return null;
  }

  /**
   * This method retrieves the valid languages from the current site  adds their
   * displayNames and values to a map to be returned.
   *
   * @exception com.endeca.infront.assembler.CartridgeHandlerException
   */
  public void buildLanguageMenuOptions(ContentItem pContentItem) throws CartridgeHandlerException {
    RepositoryItem[] sites = null;

    try {
      sites = SiteManager.getSiteManager().getActiveSites();
    }
    catch (RepositoryException re) {
      throw new CartridgeHandlerException(re);
    }

    List<Map<String,List<Map<String,String>>>> sitesLanguages = new ArrayList<>();

    if (sites != null && sites.length > 0) {
      for (RepositoryItem site : sites) {
        List<String> languages = (List<String>) site.getPropertyValue(SITE_LANGUAGES_PROPERTY_NAME);

        if (languages != null && languages.size() > 0) {
          Map<String, List<Map<String,String>>> siteLanguages = new HashMap<>();

          String siteProductionURL = site.getPropertyValue("productionURL") + "/";
          siteLanguages.put(siteProductionURL, new ArrayList<Map<String,String>>(2));

          String defaultCountryCode = (String) site.getPropertyValue(DEFAULT_COUNTRY_CODE_PROPERTY_NAME);

          for (String language : languages) {
            Locale locale = new Locale(language, defaultCountryCode);

            Map<String, String> languageDetails = new HashMap<>(2);

            languageDetails.put(
              "languageDisplayName", LocaleUtils.getCapitalizedDisplayLanguage(locale, locale));
            languageDetails.put(
              "language", language + "_" + defaultCountryCode);

            siteLanguages.get(siteProductionURL).add(languageDetails);
          }

          sitesLanguages.add(siteLanguages);
        }
      }
      pContentItem.put("siteLanguages", sitesLanguages);
    }
  }
}