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

package atg.userprofiling.email;

import java.util.Map;

import atg.adapter.gsa.GSAItem;
import atg.commerce.order.CommerceItem;

/**
 * This class extends the OrderDataProvider class and adds the application 
 * specific order data such as clothing-sku, wood-finish and color etc for 
 * order confirmation email to be sent to the user on successful order submission.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Plugins/Checkout/src/main/java/atg/userprofiling/email/StoreOrderDataProvider.java#1 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreOrderDataProvider extends OrderDataProvider {
  
  /** Class version string. */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Plugins/Checkout/src/main/java/atg/userprofiling/email/StoreOrderDataProvider.java#1 $$Change: 1385662 $";
  
  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------
  
  /** Sku type property name. */
  public static final String SKU_TYPE_PROPERTY_NAME = "type";
  
  /** Clothing sku type property name. */
  public static final String CLOTHING_SKU_TYPE = "clothing-sku";
  
  /** Furniture sku type property name. */
  public static final String FURNITURE_SKU_TYPE = "furniture-sku";
  
  /** Sku size property name. */
  public static final String SKU_SIZE = "size";
  
  /** Sku color property name. */
  public static final String SKU_COLOR = "color";
  
  /** Sku wood finish property name. */
  public static final String SKU_WOOD_FINISH = "woodFinish";
  
  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------
  
  /**
   * This method overrides super class methods to add application specific
   * data to the commerce item data in the form of a map for the 
   * given commerce item. 
   * 
   * @param pCommerceItem
   *   Commerce item.
   * @return
   *   Returns the commerce item data in the form of map.
   */
  public Map<String, Object> getCommerceItemData(CommerceItem pCommerceItem) {
    Map<String, Object> currentCommerceItem = super.getCommerceItemData(pCommerceItem);
    GSAItem sku = (GSAItem) pCommerceItem.getAuxiliaryData().getCatalogRef();
    String skuType = (String) sku.getPropertyValue(SKU_TYPE_PROPERTY_NAME);
        
    currentCommerceItem.put(SKU_TYPE_PROPERTY_NAME, skuType);
    if (skuType.equals(CLOTHING_SKU_TYPE)) {
      currentCommerceItem.put(SKU_SIZE, sku.getPropertyValue(SKU_SIZE));
      currentCommerceItem.put(SKU_COLOR, sku.getPropertyValue(SKU_COLOR));
    }
    else {
      if (skuType.equals(FURNITURE_SKU_TYPE)) {
        currentCommerceItem.put(SKU_WOOD_FINISH, sku.getPropertyValue(SKU_WOOD_FINISH));
      }
    }
    return currentCommerceItem;
  }
}
