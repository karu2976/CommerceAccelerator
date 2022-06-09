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

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.states.ObjectStates;
import atg.core.i18n.CountryList;
import atg.core.i18n.PlaceList;
import atg.core.util.StringUtils;
import atg.endeca.assembler.AssemblerTools;
import atg.service.filter.bean.BeanFilterException;
import atg.service.filter.bean.BeanFilterRegistry;
import atg.userprofiling.Profile;

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;
import com.endeca.infront.navigation.model.FilterState;
import com.endeca.infront.navigation.request.MdexRequest;
import com.endeca.infront.navigation.request.RecordsMdexQuery;
import com.endeca.navigation.ENEQueryResults;

import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.nucleus.naming.ParameterName;
import atg.projects.store.assembler.cartridge.StoreCartridgeTools;
import atg.servlet.ServletUtil;

/**
 * The cartridge handler for the AccountOrderDetails cartridge. This adds parts of the order to the
 * response for use in rendering the page. A security check is performed to ensure that the
 * requesting user has permission to view the order.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/assembler/cartridge/handler/AccountOrderDetailHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class AccountOrderDetailHandler extends NavigationCartridgeHandler {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/assembler/cartridge/handler/AccountOrderDetailHandler.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /** The input parameter that indicates which order to get */
  public static final ParameterName ORDER_ID = ParameterName.getParameterName("orderId");

  /** Key in the content item that identifies if a user can view this order */
  public static final String CAN_VIEW_ORDER = "canViewOrder";

  /** Key in the content item for the order */
  public static final String MY_ORDER = "myOrder";

  /** Key in the content item for the order state display name */
  public static final String ORDER_STATE = "orderState";

  /** Key in the content item for the shipping address country display name */
  public static final String SHIPPING_COUNTRY_DISPLAY_NAME = "shippingAddressCountryDisplayName";

  /** Key in the content item for the billing address display name */
  public static final String BILLING_COUNTRY_DISPLAY_NAME = "billingAddressCountryDisplayName";

  /** Key in the content item for the site info */
  public static final String SITE = "site";
  
  /** Key for endeca driven product page urls object in content item */
  public static final String PRODUCT_URLS = "productURLs";
  
  //---------------------------------------------------------------------------
  // MEMBERS
  //---------------------------------------------------------------------------
  private MdexRequest mMdexRequest = null;

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: filterId
  //-----------------------------------
  private String mFilterId = "detailed";

  /**
   * @return The filter id to use when filtering the order. This controls what's returned in the
   * response representing the order repository item.
   */
  public String getFilterId() {
    return mFilterId;
  }

  /**
   * @param pFilterId Set the filter to use when filtering the order.
   */
  public void setFilterId(String pFilterId) {
    mFilterId = pFilterId;
  }

  //-----------------------------------
  // property: beanFilterRegistry
  //-----------------------------------
  private BeanFilterRegistry mBeanFilterRegistry;

  /**
   * @return BeanFilterRegistry component used to filter properties on a bean.
   */
  public BeanFilterRegistry getBeanFilterRegistry() {
    return mBeanFilterRegistry;
  }

  /**
   * @param pBeanFilterRegistry Set a new BeanFilterRegistry.
   */
  public void setBeanFilterRegistry(BeanFilterRegistry pBeanFilterRegistry) {
    mBeanFilterRegistry = pBeanFilterRegistry;
  }

  //-----------------------------------
  // property: permittedCountries
  //-----------------------------------
  private CountryList mPermittedCountries;

  /**
   * @param pPermittedCountries Sets the shippable countries.
   **/
  public void setPermittedCountries(CountryList pPermittedCountries) {
    mPermittedCountries = pPermittedCountries;
  }

  /**
   * @return Returns the shippable countries list.
   **/
  public CountryList getPermittedCountries() {
    return mPermittedCountries;
  }
  
  //-------------------------------------
  // property: orderStates
  //-------------------------------------
  private ObjectStates mOrderStates;

  /**
   * Sets the {@link ObjectStates} that contains the raw and translated state values.
   */
  public void setOrderStates(ObjectStates pOrderStates) {
    mOrderStates = pOrderStates;
  }

  /**
   * Gets the {@link ObjectStates} that contains the raw and translated state values.
   */
  public ObjectStates getOrderStates() {
    return mOrderStates;
  }

  //-------------------------------------
  // property: siteContextManager
  //-------------------------------------
  private SiteContextManager mSiteContextManager;

  /**
   * @return Gets the SiteContextManager. The default path will be resolved if this property has
   * not been set.
   */
  public SiteContextManager getSiteContextManager() {
    return mSiteContextManager;
  }

  /**
   * @param pSiteContextManager the siteContextManager to set
   */
  public void setSiteContextManager(SiteContextManager pSiteContextManager) {
    mSiteContextManager = pSiteContextManager;
  }
  
  //-------------------------------------
  // property: orderManager
  //-------------------------------------
  private OrderManager mOrderManager;

  /**
   * @param pOrderManager The OrderManager component to set.
   */
  public void setOrderManager(OrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }
  
  /**
   * Returns the orderManager property which is a component that is useful for many order management
   * functions.
   */
  public OrderManager getOrderManager() {
    return mOrderManager;
  }
  
  //------------------------------------
  // property: storeCartridgeTools
  //------------------------------------
  private StoreCartridgeTools mStoreCartridgeTools = null;

  /**
   * @param pStoreCartridgeTools
   *   A component to perform common cartridge operations.
   */
  public void setStoreCartridgeTools(StoreCartridgeTools pStoreCartridgeTools) {
    mStoreCartridgeTools = pStoreCartridgeTools;
  }

  /**
   * @return
   *   A component to perform common cartridge operations.
   */
  public StoreCartridgeTools getStoreCartridgeTools() {
    return mStoreCartridgeTools;
  }
  
  //------------------------------------
  // property: productRecordRepositoryIdName
  //------------------------------------
  private String mProductRecordRepositoryIdName = "product.repositoryId";

  /**
   * @param pProductRecordRepositoryIdName
   *   The product record repository ID name.
   */
  public void setProductRecordRepositoryIdName(String pProductRecordRepositoryIdName) {
    mProductRecordRepositoryIdName = pProductRecordRepositoryIdName;
  }

  /**
   * @return
   *   The product record repository ID name.
   */
  public String getProductRecordRepositoryIdName() {
    return mProductRecordRepositoryIdName;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * This method implements wrapConfig method of super class as super class method
   * is abstract.
   *  
   * @param pContentItem Content item.
   * @return Returns the content item.
   */  
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return pContentItem;
  }

  /**
   * Creates an MdexRequest which queries for the records corresponding to the carts current items.
   *
   * @param pContentItem The content item.
   * @throws CartridgeHandlerException
   */
  @Override
  public void preprocess(ContentItem pContentItem) throws CartridgeHandlerException {
    String orderId = (String) ServletUtil.getCurrentRequest().getLocalParameter(ORDER_ID);
    Order order = null;
    try{
      if (getOrderManager().orderExists(orderId)) {
        order = getOrderManager().loadOrder(orderId);
      }
    }
    catch (CommerceException e) {
      if(AssemblerTools.getApplicationLogging().isLoggingError()) {
        AssemblerTools.getApplicationLogging().logError("There was a problem retrieving the order from the order repository", e);
      }
    }

    if(order == null){
      AssemblerTools.getApplicationLogging().vlogDebug("Order {0} does not exist.");
      return;
    }

    List<CommerceItem> commerceItems = order.getCommerceItems();
    if(commerceItems == null || commerceItems.isEmpty()){
      return;
    }

    // Get the product ids of the items within the order.
    List<String> productIds = new ArrayList<>(commerceItems.size());
    for(CommerceItem item : commerceItems) { // synchronize?
      productIds.add(item.getAuxiliaryData().getProductId());
    }

    FilterState filterState = getNavigationState().getFilterState();
    RecordsMdexQuery query = new RecordsMdexQuery();

    getStoreCartridgeTools().populateRecordSpecMdexRequestParams(
      query, filterState, productIds, getProductRecordRepositoryIdName());

    mMdexRequest = createMdexRequest(filterState, query);
  }
  
  /**
   * This method adds the order to the content item based on order id passed.
   *  
   * @param pContentItem Content item for getting configuration from experience manager.
   * @return Returns the Content item with page configuration.
   * @throws CartridgeHandlerException Throws exception if there is any issue while invoking  handler.
   */
  @Override
  public ContentItem process(ContentItem pContentItem) throws CartridgeHandlerException {
    // Get the orderId parameter
    String orderId = (String) ServletUtil.getCurrentRequest().getLocalParameter(ORDER_ID);
    if(StringUtils.isEmpty(orderId)){
      pContentItem.put(CAN_VIEW_ORDER, false);
      return pContentItem;
    }

    try {
      Order order = null;
      if (getOrderManager().orderExists(orderId)) {
        order = getOrderManager().loadOrder(orderId);
      }

      // Make sure the user can view this order.
      Profile profile = (Profile) ServletUtil.getCurrentUserProfile();
      if (order == null || !order.getProfileId().equals(profile.getRepositoryId())) {
        pContentItem.put(CAN_VIEW_ORDER, false);
        return pContentItem;
      }

      // Populate the content item
      pContentItem.put(CAN_VIEW_ORDER, true);
      pContentItem.put(MY_ORDER, getFilteredOrderMap(order));
      pContentItem.put(ORDER_STATE, getOrderStateDisplayName(order));
      
      // Query for the record specs so we can link to the Endeca driven product page.
      if (mMdexRequest != null) {
        ENEQueryResults results = executeMdexRequest(mMdexRequest);

        Map productIdsToRecordSpecs =
          getStoreCartridgeTools().getItemIdToRecordSpecMap(
            results, getProductRecordRepositoryIdName());

        if (productIdsToRecordSpecs != null) {
          pContentItem.put(PRODUCT_URLS, productIdsToRecordSpecs);
        }
      }

      // Add the display name for the shipping address. We only handle a single hard good shipping
      // group for the time being.
      if(order.getShippingGroups() != null && !order.getShippingGroups().isEmpty())
      {
        HardgoodShippingGroup hgsg = (HardgoodShippingGroup) order.getShippingGroups().get(0);
        String countryCode = hgsg.getShippingAddress().getCountry();
        PlaceList.Place place = getPermittedCountries().getPlaceForCode(countryCode);
        pContentItem.put(SHIPPING_COUNTRY_DISPLAY_NAME, place.getDisplayName());
      }

      // Add the display name for the billing address. We only handle a single credit card payment
      // group for the time being.
      if(order.getPaymentGroups() != null && !order.getPaymentGroups().isEmpty())
      {
        CreditCard card = (CreditCard) order.getPaymentGroups().get(0);
        String countryCode = card.getBillingAddress().getCountry();
        PlaceList.Place place = getPermittedCountries().getPlaceForCode(countryCode);
        pContentItem.put(BILLING_COUNTRY_DISPLAY_NAME, place.getDisplayName());
      }

      try {
        pContentItem.put(SITE, getSiteContextManager().getSite(order.getSiteId()));
      }
      catch (SiteContextException e) {
        if(AssemblerTools.getApplicationLogging().isLoggingError()) {
          AssemblerTools.getApplicationLogging().logError("There was a problem retrieving the site context", e);
        }
      }
    }
    catch (CommerceException e) {
      if(AssemblerTools.getApplicationLogging().isLoggingError()) {
        AssemblerTools.getApplicationLogging().logError("There was a problem retrieving the order from the order repository", e);
      }
    }

    return pContentItem;
  }

  /**
   * Invokes BeanFilterRegistry.applyFilter to convert the repository item into a format we can
   * send in the response. The filter getFilterId is used, which should only returns the properties
   * the renderer is interested in.
   *
   * @param pOrder The order.
   * @return A map of properties useful to the renderer.
   * @throws CartridgeHandlerException
   */
  protected Map getFilteredOrderMap(Order pOrder) throws CartridgeHandlerException{
    try {
      return (Map) getBeanFilterRegistry().applyFilter(pOrder, getFilterId(),
        new HashMap<BeanFilterRegistry.FilterOptionKey,Object>(1));
    }
    catch (BeanFilterException e) {
      throw new CartridgeHandlerException(e);
    }
  }

  /**
   * Converts the given raw state value into its readable string.
   *
   * @param pOrder The order.
   * @return the readable state value
   */
  protected String getOrderStateDisplayName(Order pOrder) {
    int state = pOrder.getState();
    return getOrderStates().getStateDescriptionAsUserResource(state);
  }
}