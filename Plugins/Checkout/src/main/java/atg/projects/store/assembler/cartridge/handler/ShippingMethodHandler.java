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
import atg.commerce.order.OrderHolder;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingModelHolder;
import atg.projects.store.shipping.ShippingTools;

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is handler for shipping methods selector and it adds the available 
 * shipping methods to the content item.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/assembler/cartridge/handler/ShippingMethodHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ShippingMethodHandler extends NavigationCartridgeHandler {
  
  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/assembler/cartridge/handler/ShippingMethodHandler.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------
  
  /** The cartridge availableShippingMethods property name. */
  public static final String AVAILABLE_SHIPPING_METHODS = "availableShippingMethods";
  
  /** Key in the content item for shipping method. */
  public static final String SHIPPING_METHOD = "method";

  /** Key in the content item for shipping method price. */
  public static final String SHIPPING_METHOD_PRICE = "price";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: shoppingCart
  //-----------------------------------
  private OrderHolder mShoppingCart = null;

  /**
   * @param pShoppingCart
   *   Sets the shopping cart component.
   **/
  public void setShoppingCart(OrderHolder pShoppingCart) {
      mShoppingCart = pShoppingCart;
  }

  /**
   * @return mShoppingCart
   *   Returns the shopping cart component.
   **/
  public OrderHolder getShoppingCart() {
    return mShoppingCart;
  }
  
  //-----------------------------------
  // property: shippingTools
  //-----------------------------------
  public ShippingTools mShippingTools = null;
  
  /**
   * @param pShippingTools
   *   Set the ShippingTools component to perform generic store tasks.
   */
  public void setShippingTools(ShippingTools pShippingTools) {
    mShippingTools = pShippingTools;
  }
  
  /**
   * @return mShippingTools
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
   * @return mUserPricingModels
   *    Returns the PricingModelHolder component.
   */
  public PricingModelHolder getUserPricingModels() {
    return mUserPricingModels;
  }
   
  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * This method implements wrapConfig method of super class as super class method
   * is abstract.
   *  
   * @param pContentItem
   *   Content item.
   * @return
   *   Returns the content item.
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return pContentItem;
  }

  /**
   * This method adds the available shipping methods to the content item.
   *  
   * @param pContentItem
   *   Content item for getting configuration from experience manager.
   * @return
   *   Returns the Content item with page configuration.
   * @throws CartridgeHandlerException
   *   Throws exception if there is any issue while invoking handler.
   */
  @Override
  public ContentItem process(ContentItem pContentItem) throws CartridgeHandlerException {
    try {
      // Populate the content item with a list of valid shipping methods.
      populateAvailableShippingMethods(pContentItem);
    } 
    catch (PricingException | ShippingGroupNotFoundException | InvalidParameterException ex) {
      throw new CartridgeHandlerException(ex);
    }

    return pContentItem;
  }

  /**
   * Add the valid available shipping methods to the given content item.
   * 
   * @param pContentItem
   *   The content item to populate. 
   * @throws InvalidParameterException
   *   If a null shipping group ID is used to retrieve a shipping group. 
   * @throws ShippingGroupNotFoundException
   *   When a shipping group can't be found in the shipping group container.
   * @throws PricingException 
   *   If there was an error while computing the pricing information. 
   */
  public void populateAvailableShippingMethods(ContentItem pContentItem) 
    throws PricingException, ShippingGroupNotFoundException,
    InvalidParameterException {

    // Stores shipping method and corresponding shipping price.
    List<Map<String,String>> availableShippingMethods = new ArrayList<>();

    if (getShoppingCart().getCurrent().getShippingGroups().size() > 0) {
      ShippingGroup shippingGroup = (ShippingGroup)
        getShoppingCart().getCurrent().getShippingGroups().get(0);
      List<String> shippingMethods = 
        getShippingTools().getAvailableShippingMethods(shippingGroup, getUserPricingModels());

      // Iterate over available shipping method to get the shipping price.
      for (String method : shippingMethods) {
        double shippingPrice = getShippingTools().getShippingPrice(
          shippingGroup, method, getUserPricingModels(), getShoppingCart());

        Map<String,String> methodAndPrice = new HashMap<>(2);
        methodAndPrice.put(SHIPPING_METHOD, method);
        methodAndPrice.put(SHIPPING_METHOD_PRICE, Double.toString(shippingPrice));

        availableShippingMethods.add(methodAndPrice);
      }
    }

    pContentItem.put(AVAILABLE_SHIPPING_METHODS, availableShippingMethods);
  }
}