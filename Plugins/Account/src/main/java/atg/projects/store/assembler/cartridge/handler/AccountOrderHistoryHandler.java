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

import atg.commerce.order.Order;
import atg.commerce.states.ObjectStates;
import atg.endeca.assembler.AssemblerTools;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.projects.store.assembler.cartridge.ContentItemModifier;
import atg.service.filter.bean.BeanFilterException;
import atg.service.filter.bean.BeanFilterRegistry;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler for the AccountOrderHistory cartridge. This cartridge is an extension of the
 * ActorInvokingCartridgeHandler. It will invoke the existing rest end point to retrieve the order
 * history for user. The bean filter is applied in the process method with the filter getFilterId().
 * This provides us a map for each order, additional properties are added to the map such as the
 * orders state display name and orders site display name. A list of the filtered maps are returned
 * in the content item.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/assembler/cartridge/handler/AccountOrderHistoryHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class AccountOrderHistoryHandler extends
    NavigationCartridgeHandler<ContentItem, ContentItem> {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/assembler/cartridge/handler/AccountOrderHistoryHandler.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /** Key in the content item for the orders associated with this profile */
  public static final String MY_ORDERS = "myOrders";

  /** Key in the content item for the order state string */
  public static final String ORDER_STATE = "orderState";

  /** Key in the content item for the site name */
  public static final String SITE = "site";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

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
   * @param pSiteContextManager The siteContextManager to set.
   */
  public void setSiteContextManager(SiteContextManager pSiteContextManager) {
    mSiteContextManager = pSiteContextManager;
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

  //-----------------------------------
  // property: filterId
  //-----------------------------------
  private String mFilterId = "short";

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

  //----------------------------------------
  //  property: contentItemModifiers
  //----------------------------------------
  private ContentItemModifier[] mContentItemModifiers = null;

  /**
   * @param pContentItemModifiers
   *   The content item modifiers that will be used in this handler.
   */
  public void setContentItemModifiers(ContentItemModifier[] pContentItemModifiers) {
    mContentItemModifiers = pContentItemModifiers;
  }

  /**
   * @return
   *   The content ite modifiers that will be used in this handler.
   */
  public ContentItemModifier[] getContentItemModifiers() {
    return mContentItemModifiers;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Return the passed in content item.
   *
   * @param pContentItem
   *   The cartridge content item to be wrapped.
   * @return
   *   A new TargetedItems configuration.
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return pContentItem;
  }

  /**
   * Filters the orders passed in the content item using the getBeanFilterRegistery()
   * and getFilterId(). For every order adds the state display name and site display name.
   *
   * @param pContentItem The content item.
   * @return A modified content item.
   */
  @Override
  public ContentItem process(ContentItem pContentItem) {
    // Invoke the content item modifier interface.
    modifyContentItem(pContentItem);

    List<Order> orderList = (List) pContentItem.get(MY_ORDERS);
    
    if(orderList != null && !orderList.isEmpty()) {
      List<Map> filteredOrders = new ArrayList<>(orderList.size());

      for (Order order : orderList) {
        try {
          // Manually apply the bean filter (the order default filter would be applied if we didn't do this)
          Map filteredOrder = getFilteredOrderMap(order);

          // Add the order state display name for this order
          filteredOrder.put(ORDER_STATE, getOrderStateDisplayName(order));

          // Add the site name to the returned content item (uses the default bean filter)
          filteredOrder.put(SITE, getSiteContextManager().getSite(order.getSiteId()));

          // At this stage we've got a map that contains all the properties we care about
          filteredOrders.add(filteredOrder);
        }
        catch (BeanFilterException e) {
          AssemblerTools.getApplicationLogging().vlogError(e,
            "A problem occured filtering Order {0}", order);

          // Just return
          return pContentItem;
        }
        catch (SiteContextException e) {
          AssemblerTools.getApplicationLogging().vlogError(e,
            "A problem getting the site {0} for order {1}", order.getSiteId(), order);
        }
      }

      // Swap in our filtered orders
      pContentItem.put(MY_ORDERS, filteredOrders);
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
   * @throws BeanFilterException
   */
  protected Map getFilteredOrderMap(Order pOrder) throws BeanFilterException {
    return (Map) getBeanFilterRegistry().applyFilter(pOrder, getFilterId(),
      new HashMap<BeanFilterRegistry.FilterOptionKey,Object>(1));
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

  /**
   * Loop all configured {@code ContentItemModifiers} and execute their modify method against
   * the passed {@code RecordDetails}.
   *
   * @param pContentItem
   *   The current ContentItem being processed by the request.
   */
  public void modifyContentItem(ContentItem pContentItem) {

    if (getContentItemModifiers() != null && getContentItemModifiers().length > 0) {
      for (ContentItemModifier contentItemModifier : getContentItemModifiers()) {
        contentItemModifier.modify(pContentItem);
      }
    }
  }
}
