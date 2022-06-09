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

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.ShippingPricingEngine;
import atg.core.i18n.LayeredResourceBundle;
import atg.projects.store.userprofiling.StoreProfilePropertyManager;
import atg.projects.store.userprofiling.StoreProfileTools;
import atg.repository.RepositoryItem;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Returns the checkout defaults for the shipping address, credit cards and shipping method.
 * Also returns all available shipping methods, addresses and credit cards. The shipping methods
 * returned are those supported by HardgoodShippingGroup.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/assembler/cartridge/handler/AccountCheckoutDefaultsHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class AccountCheckoutDefaultsHandler extends NavigationCartridgeHandler{

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/assembler/cartridge/handler/AccountCheckoutDefaultsHandler.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /** The key to add all the addresses in the content item */
  public static final String ALL_ADDRESSES = "allAddresses";

  /** The key to add all the cards in the content item */
  public static final String ALL_CARDS = "allCards";

  /** The key to add all the shipping methods in the content item */
  public static final String ALL_CARRIERS = "allCarriers";

  /** The display name key for carriers */
  public static final String CARRIER_DISPLAY_NAME = "displayName";

  /** The value key for carries */
  public static final String CARRIER_VALUE = "value";

  // Resource key pattern
  private static final Pattern RESOURCE_STRING_REPLACE = Pattern.compile("\\s");

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: resourceBundleName
  //-----------------------------------
  private String mResourceBundleName;

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
  // property: profilePropertyManager
  //-----------------------------------
  private StoreProfilePropertyManager mProfilePropertyManager;

  /**
   * @param pProfilePropertyManager Set a component used to manage properties related to the profile.
   */
  public void setProfilePropertyManager(StoreProfilePropertyManager pProfilePropertyManager) {
    mProfilePropertyManager = pProfilePropertyManager;
  }

  /**
   * @return A component used to manage properties related to the profile.
   */
  public StoreProfilePropertyManager getProfilePropertyManager(){
    return mProfilePropertyManager;
  }

  //------------------------------------
  // property: userPricingModels
  //------------------------------------
  private PricingModelHolder mUserPricingModels;

  /**
   * @param pUserPricingModels A {@code PricingModelHolder} value.
   */
  public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
    mUserPricingModels = pUserPricingModels;
  }

  /**
   * @return A {@code PricingModelHolder} value.
   */
  public PricingModelHolder getUserPricingModels() {
    return mUserPricingModels;
  }

  //-------------------------------------
  // property: shippingPricingEngine
  //-------------------------------------
  private ShippingPricingEngine mShippingPricingEngine;

  /**
   * @param pShippingPricingEngine
   *   The ShippingPricingEngine component.
   */
  public void setShippingPricingEngine(
          ShippingPricingEngine pShippingPricingEngine) {
    mShippingPricingEngine = pShippingPricingEngine;
  }

  /**
   * @return The ShippingPricingEngine component.
   */
  public ShippingPricingEngine getShippingPricingEngine() {
    return mShippingPricingEngine;
  }

  //-----------------------------------
  // property: profileTools
  //-----------------------------------
  private StoreProfileTools mProfileTools;

  /**
   * @return The ProfileTools component.
   */
  public StoreProfileTools getProfileTools() {
    return mProfileTools;
  }

  /**
   * @param pProfileTools Set the ProfileTools component.
   */
  public void setProfileTools(StoreProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Default implementation of wrapConfig. Just return the passed in content item.
   *
   * @param pContentItem A content item.
   * @return pContentItem
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return pContentItem;
  }

  /**
   * Adds the default shipping address, default credit card and default shipping method to the
   * content item. The keys will be the values for these properties set in the
   * profilePropertyManager. If a default value isn't set then null will be returned.
   *
   * @param pContentItem A content item.
   * @return The content item with the defaults set.
   * @throws com.endeca.infront.assembler.CartridgeHandlerException
   */
  @Override
  public ContentItem process(ContentItem pContentItem) throws CartridgeHandlerException {
    Profile profile = (Profile) ServletUtil.getCurrentUserProfile();

    // Shipping Addresses
    pContentItem.put(getProfilePropertyManager().getShippingAddressPropertyName(),
      getProfileTools().getDefaultShippingAddressNickname(profile));

    Map<String, RepositoryItem> secondaryAddressMap = (Map)
      profile.getPropertyValue(getProfilePropertyManager().getSecondaryAddressPropertyName());
    if(secondaryAddressMap != null) {
      pContentItem.put(ALL_ADDRESSES, secondaryAddressMap.keySet());
    }
    else{
      pContentItem.put(ALL_ADDRESSES, null);
    }

    // Credit Cards
    pContentItem.put(getProfilePropertyManager().getDefaultCreditCardPropertyName(),
      getProfileTools().getDefaultCreditCardNickname(profile));

    Map<String, RepositoryItem> creditCardsMap =
      (Map) profile.getPropertyValue(getProfilePropertyManager().getCreditCardPropertyName());
    if(creditCardsMap != null) {
      pContentItem.put(ALL_CARDS, creditCardsMap.keySet());
    }
    else{
      pContentItem.put(ALL_CARDS, null);
    }

    // Carriers
    String defaultShippingMethodPropertyName =
      getProfilePropertyManager().getDefaultShippingMethodPropertyName();

    pContentItem.put(defaultShippingMethodPropertyName,
      profile.getPropertyValue(defaultShippingMethodPropertyName));

    ResourceBundle bundle = null;
    Locale locale = ServletUtil.getUserLocale();
    if (getResourceBundleName() != null) {
      bundle = LayeredResourceBundle.getBundle(getResourceBundleName(), locale);
    }

    try {
      List<String> carrierValues = getShippingPricingEngine().getAvailableMethods(new HardgoodShippingGroup(),
        getUserPricingModels().getShippingPricingModels(), locale, profile, null);

      List carrierList = new ArrayList(carrierValues.size());

      if(bundle != null) {
        for (String carrierValue : carrierValues) {
          String displayName =
            bundle.getString("shipping" + RESOURCE_STRING_REPLACE.matcher(carrierValue).replaceAll(""));

          Map<String, String> shippingMethod = new HashMap(2);
          shippingMethod.put(CARRIER_DISPLAY_NAME, displayName);
          shippingMethod.put(CARRIER_VALUE, carrierValue);

          carrierList.add(shippingMethod);
        }
      }

      pContentItem.put(ALL_CARRIERS, carrierList);
    }
    catch(PricingException e){
      throw new CartridgeHandlerException(e);
    }

    return pContentItem;
  }
}
