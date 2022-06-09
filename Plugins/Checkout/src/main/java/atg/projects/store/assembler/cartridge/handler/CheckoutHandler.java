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

import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingTools;
import atg.commerce.util.PlaceList;
import atg.core.i18n.CountryList;
import atg.core.i18n.LayeredResourceBundle;
import atg.projects.store.shipping.ShippingTools;
import atg.projects.store.userprofiling.StoreProfilePropertyManager;
import atg.projects.store.userprofiling.StoreProfileTools;
import atg.projects.store.util.YearListGenerator;
import atg.repository.RepositoryItem;
import atg.service.filter.bean.BeanFilterException;
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

/**
 * Handler for checkout cartridge (which is a single page checkout). Adds the available shipping
 * methods, available payment methods. available shipping addresses and available countries to the
 * returned content item.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/assembler/cartridge/handler/CheckoutHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class CheckoutHandler extends NavigationCartridgeHandler {

  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/assembler/cartridge/handler/CheckoutHandler.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /** The cartridge availableCountries property name. */
  public static final String AVAILABLE_COUNTRIES = "availableCountries";

  /** The cartridge availableShippingMethods property name. */
  public static final String AVAILABLE_SHIPPING_METHODS = "availableShippingMethods";
  
  /** The cartridge availableShippingAddress property name. */
  public static final String AVAILABLE_SHIPPING_ADDRESS = "availableShippingAddress";
  
  /** The cartridge availablePaymentMethods property name. */
  public static final String AVAILABLE_PAYMENT_METHOD = "availablePaymentMethods";

  /** Key in the content item for shipping method display name. */
  public static final String SHIPPING_METHOD_DISPLAY_NAME = "displayName";

  /** Key in the content item for shipping method. */
  public static final String SHIPPING_METHOD = "method";

  /** Key in the content item for shipping method price. */
  public static final String SHIPPING_METHOD_PRICE = "price";

  /** Key in the content item for default shipping method. */
  public static final String DEFAULT_SHIPPING_METHOD = "useAsDefaultShippingMethod";

  /** Key in the content item for the list of expiry years. */
  public static final String EXPIRY_YEARS = "expiryYears";
  
  /** Key for states in the content item. */
  public static final String STATES = "states";
  
  /** Key for current country in the content item. */
  public static final String CURRENT_COUNTRY = "currentCountry";
  
  /** Key for current order in the content item. */
  public static final String ORDER = "order";
  
  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------
  
  //-----------------------------------
  // property: resourceBundleName
  //-----------------------------------
  private String mResourceBundleName = "atg.projects.store.CheckoutResources";

  /**
   * @param pResourceBundleName
   *   Sets a resource bundle for localizing strings.
   */
  public void setResourceBundleName(String pResourceBundleName) {
    mResourceBundleName = pResourceBundleName;
  }

  /**
   * @return
   *   Returns a resource bundle for localizing strings.
   */
  public String getResourceBundleName() {
    return mResourceBundleName;
  }

  //-----------------------------------
  // property: shoppingCart
  //-----------------------------------
  private OrderHolder mShoppingCart = null;

  /**
   * @param pShoppingCart
   *   Sets the shopping cart component.
   */
  public void setShoppingCart(OrderHolder pShoppingCart) {
      mShoppingCart = pShoppingCart;
  }

  /**
   * @return
   *   Returns the shopping cart component.
   */
  public OrderHolder getShoppingCart() {
    return mShoppingCart;
  }

  //-----------------------------------
  // property: permittedCountries
  //-----------------------------------
  private CountryList mPermittedCountries = null;

  /**
   * @param pPermittedCountries
   *   Sets the shippable countries.
   */
  public void setPermittedCountries(CountryList pPermittedCountries) {
    mPermittedCountries = pPermittedCountries;
  }

  /**
   * @return
   *   Returns the shippable countries list.
   */
  public CountryList getPermittedCountries() {
    return mPermittedCountries;
  }
  
  //-----------------------------------
  // property: shippingTools
  //-----------------------------------
  private ShippingTools mShippingTools = null;
  
  /**
   * @param pShippingTools
   *   Set the ShippingTools component to perform generic store tasks.
   */
  public void setShippingTools(ShippingTools pShippingTools) {
    mShippingTools = pShippingTools;
  }
  
  /**
   * @return
   *   Returns the ShippingTools component to perform generic store tasks.
   */
  public ShippingTools getShippingTools() {
    return mShippingTools;
  }

  //-----------------------------------
  // property: userPricingModels
  //-----------------------------------
  private PricingModelHolder mUserPricingModels = null;

  /**
   * @param pUserPricingModels
   *   Sets the PricingModelHolder that should be used for pricing.
   */
  public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
    mUserPricingModels = pUserPricingModels;
  }

  /**
   * @return
   *   Returns the PricingModelHolder component.
   */
  public PricingModelHolder getUserPricingModels() {
    return mUserPricingModels;
  }  
  
  //-----------------------------------
  // property: filterId
  //-----------------------------------
  private String mFilterId = "summary";

  /**
   * @return
   *   Returns the filter id to use when filtering addresses.
   */
  public String getFilterId() {
    return mFilterId;
  }

  /**
   * @param pFilterId
   *   Set the filter to use when filtering addresses
   */
  public void setFilterId(String pFilterId) {
    mFilterId = pFilterId;
  }

  //-----------------------------------
  // property: profilePropertyManager
  //-----------------------------------
  private StoreProfilePropertyManager mProfilePropertyManager = null;

  /**
   * @param pProfilePropertyManager
   *   Set a component used to manage properties related to the profile.
   */
  public void setProfilePropertyManager(StoreProfilePropertyManager pProfilePropertyManager) {
    mProfilePropertyManager = pProfilePropertyManager;
  }

  /**
   * @return
   *   Returns a component used to manage properties related to the profile.
   */
  public StoreProfilePropertyManager getProfilePropertyManager() {
    return mProfilePropertyManager;
  }

  //-----------------------------------
  // property: profileTools
  //-----------------------------------
  private StoreProfileTools mProfileTools = null;

  /**
   * @param pProfileTools
   *   Set the profile tools component.
   */
  public void setProfileTools(StoreProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }
  /**
   * @return
   *   Returns the profile tools component.
   */
  public StoreProfileTools getProfileTools() {
    return mProfileTools;
  }

  //-----------------------------------
  // property: profile
  //-----------------------------------
  private Profile mProfile = null;

  /**
   * @param pProfile
   *   Set the Profile property.
   */
  public void setProfile(Profile pProfile) {
    mProfile = pProfile;
  }

  /**
   * @return
   *   Returns the Profile property.
   */
  public Profile getProfile() {
    return mProfile;
  }

  //-----------------------------------
  // property: yearList
  //-----------------------------------
  private YearListGenerator mYearList = null;

  /**
   * @return
   *   Set a component used to generate a list of years.
   */
  public YearListGenerator getYearList() {
    return mYearList;
  }

  /**
   * @param pYearList
   *   Returns a component used to generate a list of years.
   */
  public void setYearList(YearListGenerator pYearList) {
    mYearList = pYearList;
  }
  
  //-----------------------------------
  // property: pricingTools
  //-----------------------------------
  private PricingTools mPricingTools = null;

  /**
   * @param pPricingTools
   *   Set the pricing tools component.
   */
  public void setPricingTools(PricingTools pPricingTools) {
    mPricingTools = pPricingTools;
  }

  /**
   * @return
   *   Returns the pricing tools component.
   */
  public PricingTools getPricingTools() {
    return mPricingTools;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Return the passed in content item.
   *  
   * @param pContentItem
   *   Content item.
   *
   * @return 
   *   Returns the content item.
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return pContentItem;
  }

  /**
   * Adds the available countries, available shipping methods, available addresses and available
   * payment methods to the content item.
   *  
   * @param pContentItem
   *   Content item for getting configuration from experience manager.
   * @return 
   *   Returns the Content item with page configuration.
   *
   * @throws CartridgeHandlerException
   *   Throws exception if there's any issue while invoking handler.
   */
  @Override
  public ContentItem process(ContentItem pContentItem) throws CartridgeHandlerException {
    try {

      // Populate the content item with a list of permitted countries for the current site.
      populateAvailableCountries(pContentItem);
      
      // Populate the content item with a list of states based on locale for the current site.
      populateAvailableStates(pContentItem);
      
      // Populate the content item with a list of valid shipping methods.
      populateAvailableShippingMethods(pContentItem);
      
      //Populate the content item with list of shipping addresses.
      populateAvailableShippingAddress(pContentItem);
      
      //Populate the content item with list of payment methods.
      populateAvailablePaymentMethods(pContentItem);

      // Add a year list to the content item.
      pContentItem.put(EXPIRY_YEARS, getYearList().generateYearList());

      // Reprice the order.
      repriceOrder();

      // Add the order to the content item.
      pContentItem.put(ORDER, getShoppingCart().getCurrent());
    } 
    catch (PricingException | ShippingGroupNotFoundException | InvalidParameterException |
        BeanFilterException exception) {
      throw new CartridgeHandlerException(exception);
    }
    return pContentItem;
  }

  /**
   * Add the shipping addresses available in profile.
   * 
   * @param pContentItem
   *   The content item to populate.
   */
  public void populateAvailableShippingAddress(ContentItem pContentItem)
      throws BeanFilterException {

    Map<String, RepositoryItem> addresses = (Map) getProfile()
      .getPropertyValue(getProfilePropertyManager().getSecondaryAddressPropertyName());

    List addressList = new ArrayList(
      (addresses != null && addresses.size() > 0) ? addresses.size() : 1);

    // Add all addresses to the addressList, defining the current state of each address and which
    // one is the default. The properties for each address will also be refined using the bean
    // filter ID defined in the filterId property of this class.
    if (addresses != null) {
      for (Map.Entry<String, RepositoryItem> entry : addresses.entrySet()) {
        addressList.add(getProfileTools().getFilteredAddressMap(getProfile(), entry.getKey(),
            entry.getValue(), getFilterId(), true, true, false));
      }
    }

    pContentItem.put(AVAILABLE_SHIPPING_ADDRESS, addressList);
  }
  
  /**
   * Add the payment methods available in profile.
   * 
   * @param pContentItem
   *   The content item to populate.
   */
  public void populateAvailablePaymentMethods(ContentItem pContentItem)
      throws BeanFilterException {

    Map<String, RepositoryItem> cards =
      (Map) getProfile().getPropertyValue(getProfilePropertyManager().getCreditCardPropertyName());

    List cardList = new ArrayList(
      (cards != null && cards.size() > 0) ? cards.size() : 1);

    // Add all cards to the cardList, defining the current state of each card and which
    // one is the default. The properties for each card will also be refined using the bean
    // filter ID defined in the filterId property of this class.
    if (cards != null) {
      for (Map.Entry<String, RepositoryItem> entry : cards.entrySet()) {
        cardList.add(getProfileTools().getFilteredCardMap(getProfile(), entry.getKey(),
            entry.getValue(), getFilterId(), true, true, false));
      }
    }

    pContentItem.put(AVAILABLE_PAYMENT_METHOD, cardList);
  }
  
  /**
   * Add the permitted available countries to the given content item.
   * 
   * @param pContentItem
   *   The content item to populate.
   */
  public void populateAvailableCountries(ContentItem pContentItem) {
    pContentItem.put(AVAILABLE_COUNTRIES, getPermittedCountries().getPlaces());
  }
  
  /**
   * Populate the content item with a list of states based on
   * locale for the current site.
   * 
   * @param pContentItem
   *   The content item to populate.
   */
  public void populateAvailableStates(ContentItem pContentItem) {
    String country = ServletUtil.getUserLocale().getCountry();

    PlaceList.Place[] states =
      getProfileTools().getPlaceUtils().getLocalizedSortedPlaces(
        country, ServletUtil.getUserLocale());

    // Add the list of states for the default country.
    pContentItem.put(STATES, states);
    pContentItem.put(CURRENT_COUNTRY, country);
  }

  /**
   * Add the valid available shipping methods to the given content item.
   * 
   * @param pContentItem
   *   The content item to populate.
   *
   * @throws InvalidParameterException
   *   If a null shipping group ID is used to retrieve a shipping group.
   * @throws ShippingGroupNotFoundException
   *   When a shipping group can't be found in the shipping group container.
   * @throws PricingException
   *   If there was an error while computing the pricing information.
   */
  public void populateAvailableShippingMethods(ContentItem pContentItem) throws PricingException,
      ShippingGroupNotFoundException, InvalidParameterException {

    // Stores shipping method and corresponding shipping price.
    List<Map<String,Object>> availableShippingMethods = new ArrayList<>();

    if (!getShoppingCart().getCurrent().getShippingGroups().isEmpty()) {
      ShippingGroup shippingGroup = 
        (ShippingGroup)getShoppingCart().getCurrent().getShippingGroups().get(0);

      String defaultShippingMethodPropertyName = 
        getProfilePropertyManager().getDefaultShippingMethodPropertyName();

      String defaultShippingMethod =
        (String) getProfile().getPropertyValue(defaultShippingMethodPropertyName);

      ResourceBundle bundle = null;

      if (getResourceBundleName() != null) {
        bundle = LayeredResourceBundle.getBundle(getResourceBundleName(),
          ServletUtil.getUserLocale());
      }

      // List of available shipping methods.
      List<String> shippingMethods =
        getShippingTools().getAvailableShippingMethods(shippingGroup, getUserPricingModels());

      // Iterate over available shipping method to get the shipping price.
      for (String method : shippingMethods) {
        double shippingPrice = getShippingTools().getShippingPrice(
          shippingGroup, method, getUserPricingModels(), getShoppingCart());

        Map<String, Object> methodAndPrice = new HashMap<>(4);
        String shippingMethod = null;

        if (bundle != null) {
          shippingMethod = bundle.getString(method);
        }
        else {
          shippingMethod = method;
        }

        methodAndPrice.put(SHIPPING_METHOD_DISPLAY_NAME, shippingMethod);
        methodAndPrice.put(SHIPPING_METHOD, method);
        methodAndPrice.put(SHIPPING_METHOD_PRICE, shippingPrice);
        methodAndPrice.put(DEFAULT_SHIPPING_METHOD, method.equals(defaultShippingMethod));
        availableShippingMethods.add(methodAndPrice);
      }
    }

    pContentItem.put(AVAILABLE_SHIPPING_METHODS, availableShippingMethods);
  }
  
  /**
   * Logic to reprice order and parse any errors. This method does a
   * priceOrderSubTotal operation.
   * 
   * @throws PricingException
   *   Throws exception if there's any issue while pricing the order.
   */
  public void repriceOrder() throws PricingException {
    Locale locale = ServletUtil.getUserLocale();
    Order order = getShoppingCart().getCurrent();

    // Reprice the order.
    getPricingTools().performPricingOperation(PricingConstants.OP_REPRICE_ORDER_SUBTOTAL, order,
      getUserPricingModels(), locale, getProfile(), null);
  }
}