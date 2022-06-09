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

import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.projects.store.userprofiling.StoreProfileTools;
import atg.servlet.ServletUtil;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;

/**
 * Handler for the AddAddress cartridge. Adds the list of supported countries, the list of
 * states in the default country and a list of valid expiry years to the content item.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/assembler/cartridge/handler/AddAddressHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class AddAddressHandler extends NavigationCartridgeHandler{

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/assembler/cartridge/handler/AddAddressHandler.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  // Default country property on site.
  protected static final String DEFAULT_COUNTRY = "defaultCountry";

  /** Key for countries in the content item */
  public static final String COUNTRIES = "countries";

  /** Key for states in the content item */
  public static final String STATES = "states";

  /** Key for country the content item */
  public static final String COUNTRY = "country";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: defaultCountryCode
  //-----------------------------------
  private String mDefaultCountryCode = "US";

  /**
   * @return The default country code. Used to return the initial list of states.
   */
  public String getDefaultCountryCode() {
    return mDefaultCountryCode;
  }

  /**
   * @param pDefaultCountryCode Set the default country code. Used to return the initial list of
   * states.
   */
  public void setDefaultCountryCode(String pDefaultCountryCode) {
    mDefaultCountryCode = pDefaultCountryCode;
  }

  //-----------------------------------
  // property: profileTools
  //-----------------------------------
  private StoreProfileTools mProfileTools;

  /**
   * @return The profile tools.
   */
  public StoreProfileTools getProfileTools() {
    return mProfileTools;
  }

  /**
   * @param pProfileTools Set the profile tools.
   */
  public void setProfileTools(StoreProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Return the passed in content item.
   *
   * @param pContentItem A content item.
   * @return pContentItem.
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return pContentItem;
  }

  /**
   * Adds a list of supported countries and the states in the default country to the content item.
   *
   * @param pContentItem The content item.
   * @return A content item with additional information.
   * @throws com.endeca.infront.assembler.CartridgeHandlerException
   */
  @Override
  public ContentItem process(ContentItem pContentItem) throws CartridgeHandlerException {
    // Add the list of supported countries
    pContentItem.put(COUNTRIES, getProfileTools().getShipableCountries());

    // Add the default selected country code for this site
    String country = getDefaultCountryCode();
    Site site = SiteContextManager.getCurrentSite();
    if(site != null) {
      country = (String) site.getPropertyValue(DEFAULT_COUNTRY);
      if(country == null){
        country = getDefaultCountryCode();
      }
    }

    pContentItem.put(COUNTRY, country);

    // Add the list of states for the default county
    pContentItem.put(STATES, getProfileTools().getPlaceUtils().getLocalizedSortedPlaces(country,
      ServletUtil.getUserLocale()));

    return pContentItem;
  }
}
