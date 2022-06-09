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

import atg.core.i18n.LayeredResourceBundle;
import atg.core.net.URLUtils;
import atg.endeca.assembler.AssemblerTools;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * A cartridge used to render a link menu. This class contains an array of maps that will be added
 * to the content item menu options. The maps are configured through the properties file, if resource
 * keys are used then the localized display name resources will be returned.
 *
 * The returned ContentItem response will look similar to the following:
 *
 * <pre>
 * "unauthenticatedMenuOptions": [
 * {
 *   "displayName": "Login",
 *   "url": "login"
 * }
 * ],
 * "authenticatedMenuOptions": [
 * {
 *   "displayName": "Your Profile",
 *   "activeLink": true,
 *   "url": "account"
 * },
 * {
 *   "displayName": "Your Orders",
 *   "url": "account/orderhistory"
 * },
 * {
 *   "displayName": "Your Payment Info",
 *   "url": "account/viewcards"
 * },
 * {
 *   "displayName": "Your Address Book",
 *   "url": "account/viewaddresses"
 * },
 * {
 *   "displayName": "Your Country & Language",
 *   "url": "#"
 * }
 * ],
 * </pre>
 *
 * The properties file would have the following property configuration to output the above content:
 *
 * <pre>
 *   menuOptions=\
 *       unauthenticatedMenuOptions=\
 *           login=login,\
 *       authenticatedMenuOptions=\
 *           yourProfile=account:\
 *           yourOrders=account/yourorders
 * </pre>
 *
 * Note that the key/value pairs must be separated by colons.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/assembler/cartridge/handler/LinkMenu.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class LinkMenu extends NavigationCartridgeHandler {

  /** Class version string. */
  public static String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/assembler/cartridge/handler/LinkMenu.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /** Display name key that will be added to the content item. */
  public static final String DISPLAY_NAME = "displayName";

  /** Active link key that will be added to the content item. */
  public static final String ACTIVE_LINK = "activeLink";

  /** The key under which all the menu values are added. */
  public static final String VALUES = "values";

  /** The key under which the link is added. */
  public static final String URL = "url";

  /** = always separates keys and values. */
  private static final Pattern VALUE_SEPARATOR = Pattern.compile("=");

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: resourceBundleName
  //-----------------------------------
  private String mResourceBundleName = null;

  /**
   * @param pResourceBundleName
   *   A resource bundle for localizing strings.
   */
  public void setResourceBundleName(String pResourceBundleName) {
    mResourceBundleName = pResourceBundleName;
  }

  /**
   * @return
   *   A resource bundle for localizing strings.
   */
  public String getResourceBundleName() {
    return mResourceBundleName;
  }

  //-----------------------------------
  // property: menuOptions
  //-----------------------------------
  private HashMap<String,String> mMenuOptions = null;

  /**
   * @return
   *   The contents of a menu (list of display names and urls). The order
   *   will be the order defined in the properties file.
   */
  public HashMap<String,String> getMenuOptions() {
    return mMenuOptions;
  }

  /**
   * @param pMenuOptions
   *   The contents of a menu (list of display names and urls). The order
   *   will be the order defined in the properties file.
   */
  public void setMenuOptions(HashMap<String,String> pMenuOptions) {
    mMenuOptions = pMenuOptions;
  }

  //-----------------------------------
  // property: mapEntrySeparator
  //-----------------------------------
  private String mMapEntrySeparator = ":";

  /**
   * @param pMapEntrySeparator
   *   The separator to be used when splitting map entries; defaults to ':'.
   *   For example, in the properties file if the menuOptions array contains
   *
   *   menuOption1=key1=value1:key2=value2:key3=value3
   *
   *   this class will split each key/value pair using a colon delimiter.
   */
  public void setMapEntrySeparator(String pMapEntrySeparator) {
    mMapEntrySeparator = pMapEntrySeparator;
  }

  /**
   * @return
   *   The separator to be used when splitting map entries; defaults to ':'.
   *   For example, if the menuOptions map contains:
   *
   *   menuOption1=key1=value1:key2=value2:key3=value3
   *
   *   this class will split each key/value pair using a colon delimiter.
   */
  public String getMapEntrySeparator() {
    return mMapEntrySeparator;
  }

  //-----------------------------------
  // property: identifyActiveLink
  //-----------------------------------
  protected boolean mIdentifyActiveLink = false;

  /**
   * @return If set to true the ACTIVE_LINK will be returned in the content item. This can be used
   * on the client for styling.
   */
  public boolean isIdentifyActiveLink() {
    return mIdentifyActiveLink;
  }

  /**
   * @param pIdentifyActiveLink Sets the identifyActiveLink.
   */
  public void setIdentifyActiveLink(boolean pIdentifyActiveLink) {
    mIdentifyActiveLink = pIdentifyActiveLink;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Implements wrapConfig to return the passed ContentItem.
   *
   * @param pContentItem
   *   The ContentItem to be wrapped.
   *
   * @return
   *   The wrapped ContentItem.
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return pContentItem;
  }

  /**
   * Adds menu options and corresponding paths to the content item.
   *
   * @param pContentItem
   *   The content item to be processed.
   *
   * @return
   *   A processed content item.
   *
   * @throws com.endeca.infront.assembler.CartridgeHandlerException
   */
  @Override
  public ContentItem process(ContentItem pContentItem) throws CartridgeHandlerException {
    ResourceBundle bundle = null;

    if (getResourceBundleName() != null) {
      bundle = LayeredResourceBundle.getBundle(getResourceBundleName(),
        ServletUtil.getUserLocale());
    }

    // Parse the configuration file and create the menu(s).
    if (getMenuOptions() != null) {
      for (Map.Entry<String, String> entry : getMenuOptions().entrySet()) {
        LinkedHashMap<String,String> menuMap = buildMenuMap(entry.getValue());
        pContentItem.put(entry.getKey(), createMenu(menuMap, bundle));
      }
    }

    return pContentItem;
  }

  /**
   * Takes a String of colon delimited values that will be translated into a Map. For example, if
   * the passed in String contains the following:
   *
   * <pre>
   *   myMenuOptions=menuOption1=/path/to/page1:menuOption2=/path/to/page2:menuOption3=/path/to/page3
   * </pre>
   *
   * The resulting Map will be:
   *
   * <pre>
   *   myMenuOptions
   *     key="menuOption1", value="/path/to/page1"
   *     key="menuOption2", value="/path/to/page2"
   *     key="menuOption3", value="/path/to/page3"
   *  </pre>
   *
   * @param pStringMap
   *   The String that will be translated into a Map.
   * @return
   *   A Map containing the key/value pairs of what was defined in the String parameter.
   */
  public LinkedHashMap<String,String> buildMenuMap(String pStringMap) {
    LinkedHashMap<String,String> mapMenu = new LinkedHashMap();
    String[] maps = pStringMap.split(getMapEntrySeparator());

    if (maps.length > 0) {
      for (String map : maps) {
        String[] keyValue = VALUE_SEPARATOR.split(map);

        if (keyValue.length == 2) {
          mapMenu.put(keyValue[0], keyValue[1]);
        }
      }
    }

    return mapMenu;
  }

  /**
   * Create the menu items for a particular configuration map.
   *
   * @param pMenuMap
   *   A mapping between display key (or name) and URL.
   * @param pBundle
   *   The resource bundle to use for the menu option display names.
   *
   * @return
   *   A List of Maps, where each map is a menu item.
   */
  protected List<Map<String, Object>> createMenu(Map<String, String> pMenuMap, ResourceBundle pBundle) {
    List<Map<String, Object>> menuList = new ArrayList<>(pMenuMap.size());

    // Set the active URL.
    String activeLink = null;

    if (isIdentifyActiveLink()) {
      activeLink = determineActiveLink(pMenuMap.values());
    }

    // Iterate over the passed in map and create a list of menu options.
    for (Map.Entry<String, String> entry : pMenuMap.entrySet()) {
      Map<String, Object> menuOption = new HashMap<>(3);

      // The URL.
      menuOption.put(URL, entry.getValue());

      // The activeLink.
      if (isIdentifyActiveLink() && activeLink != null) {
        if (activeLink.equals(entry.getValue())) {
          menuOption.put(ACTIVE_LINK, true);
        }
      }

      // The displayName.
      if (pBundle != null) {
        try {
          menuOption.put(DISPLAY_NAME, pBundle.getString(entry.getKey()));
        }
        catch (MissingResourceException mre) {
          AssemblerTools.getApplicationLogging().vlogDebug("{0}", mre.getMessage());
          menuOption.put(DISPLAY_NAME, entry.getKey());
        }
      }
      else {
        menuOption.put(DISPLAY_NAME, entry.getKey());
      }

      menuList.add(menuOption);
    }

    return menuList;
  }

  /**
   * Sets the active link property to a link defined in the map. It does this by looking at the
   * request URL. If there is no direct match for a URL defined in the configuration it will break
   * the request URL and try to find a match.
   *
   * <p>
   *   E.g 1: Request URL is /account/addressbook, this matches a URL in the configuration so its set
   *   as the active link.
   * </p>
   *
   * <p>
   *   E.g 2: Request URL is /account/addressbook/edit, this does not match a URL in the configuration.
   *   It's broken from the last /. This matches a URL in the configuration /account/addressbook, so
   *   this is set as the active link.
   * </p>
   *
   * @param pURLs
   *   All the urls associated with a particular menu.
   */
  protected String determineActiveLink(Collection pURLs) {
    DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();

    // Handle non-ascii characters with punycode conversion.
    StringBuilder contentPath =
      new StringBuilder(URLUtils.urlFromPunycode(request.getRequestURI()));

    // Keep track of whether or not the site base URL has been removed.
    boolean removedSiteBaseURL = false;

    // Remove the site base URL.
    Site site = SiteContextManager.getCurrentSite();

    if (site != null) {
      String[] additionalProductionURLs =
        (String[]) site.getPropertyValue(SiteManager.getSiteManager()
          .getAdditionalProductionURLPropertyName());

      if (additionalProductionURLs != null) {
        for (String productionURL : additionalProductionURLs) {
          if (productionURL != null && productionURL.length() < 1) {
            continue;
          }
          removedSiteBaseURL = updateContentPath(contentPath, productionURL);
        }
      }

      String baseURL =
        (String) site.getPropertyValue(SiteManager.getSiteManager().getProductionURLPropertyName());

      if (!removedSiteBaseURL && baseURL != null) {
        removedSiteBaseURL = updateContentPath(contentPath, baseURL);
      }
    }

    // Just remove the context root.
    if (!removedSiteBaseURL) {
      String context = URLUtils.urlFromPunycode(request.getContextPath());

      if (contentPath.indexOf(context) == 0  && !context.contentEquals("/")) {
        contentPath.delete(0, context.length());
      }
    }

    // Finally just remove any leading '/'.
    if (contentPath.charAt(0) == '/') {
      contentPath.deleteCharAt(0);
    }

    // Check if the current URI is contained in our map.
    String uri = contentPath.toString();
    if (pURLs.contains(uri)) {
      return uri;
    }

    // Otherwise it could be a child page, check for this so we can highlight the parent.
    int breakAt = 0;

    while ((breakAt = uri.lastIndexOf('/')) != -1) {
      uri = uri.substring(0, breakAt);

      if (pURLs.contains(uri)) {
        return uri;
      }
    }

    return null;
  }

  /**
   * Remove a particular URL from the start of a content path. For example, if the content path
   * is /csa/storeus/home and the URL to be removed is /csa/storeus; the updated content path
   * will be /home and true will be returned.
   *
   * If the URL to be removed is '/' or it can't be found at the start of the content path, the
   * contenten path will not be updated and false will be returned.
   *
   * @param pContentPath
   *   The content path to be updated.
   * @param pUrl
   *   The URL to be removed from the content path.
   * @return
   *   True if the content path has been updated, otherwise false.
   */
  protected boolean updateContentPath(StringBuilder pContentPath, String pUrl) {
    if (pContentPath.indexOf(pUrl) == 0 && !pUrl.contentEquals("/")) {
      pContentPath.delete(0, pUrl.length());
      return true;
    }
    return false;
  }
}
