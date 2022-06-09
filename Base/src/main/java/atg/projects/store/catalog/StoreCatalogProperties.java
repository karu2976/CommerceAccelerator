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

package atg.projects.store.catalog;

import atg.commerce.catalog.custom.CatalogProperties;

/**
 * This class provides a mechanism to access the property names for catalog item descriptors 
 * modified for ATG.  For example, if a calling class needed the property name of the sku that 
 * provides the start date, the getStartDatePropertyName() method will return the string value for 
 * the property name as used in the repository definition.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/catalog/StoreCatalogProperties.java#1 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreCatalogProperties extends CatalogProperties {

  /** Class version string. */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/catalog/StoreCatalogProperties.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------
  
  //------------------------------------
  // property: productPropertyName
  //------------------------------------
  private String mProductPropertyName = "product";

  /**
   * @return 
   *   The property name used to identify a product.
   */
  public String getProductPropertyName() {
    return mProductPropertyName;
  }

  /**
   * @param pProductPropertyName
   *   Set the property name used to identify a product.
   */
  public void setProductPropertyName(String pProductPropertyName) {
    mProductPropertyName = pProductPropertyName;
  }
  
  //------------------------------------
  // property: skuPropertyName
  //------------------------------------
  private String mSkuPropertyName = "sku";

  /**
   * @return
   *   The property name used to identify a SKU.
   */
  public String getSkuPropertyName() {
    return mSkuPropertyName;
  }

  /**
   * @param pSkuPropertyName
   *   Set the property name used to identify a SKU.
   */
  public void setSkuPropertyName(String pSkuPropertyName) {
    mSkuPropertyName = pSkuPropertyName;
  }

  //------------------------------------
  // property: childSkusPropertyName
  //------------------------------------
  private String mChildSkusPropertyName = "childSkus";

  /**
   * @return
   *   The property name used to identify a the childSKUs belonging to a product.
   */
  public String getChildSkusPropertyName() {
    return mChildSkusPropertyName;
  }

  /**
   * @param pChildSkusPropertyName
   *   Set the property name used to identify a the childSKUs belonging to a product.
   */
  public void setChildSkusPropertyName(String pChildSkusPropertyName) {
    mChildSkusPropertyName = pChildSkusPropertyName;
  }
  
  //------------------------------------
  // property: skuTypePropertyName
  //------------------------------------
  private String mSkuTypePropertyName = "type";

  /**
   * @return
   *   The property name used to identify a SKUs type.
   */
  public String getSkuTypePropertyName() {
    return mSkuTypePropertyName;
  }

  /**
   * @param pSkuTypePropertyName
   *   Set the property name used to identify a SKUs type.
   */
  public void setSkuTypePropertyName(String pSkuTypePropertyName) {
    mSkuTypePropertyName = pSkuTypePropertyName;
  }

  //------------------------------------
  // property: childSkuTypePropertyName
  //------------------------------------
  private String mChildSkuTypePropertyName = "childSkuType";

  /**
   * @return
   *   The property name used to identify a child SKUs type.
   */
  public String getChildSkuTypePropertyName() {
    return mChildSkuTypePropertyName;
  }

  /**
   * @param pChildSkuTypePropertyName
   *   Set the property name used to identify a child SKUs type.
   */
  public void setChildSkuTypePropertyName(String pChildSkuTypePropertyName) {
    mChildSkuTypePropertyName = pChildSkuTypePropertyName;
  }

  //------------------------------------
  // property: childSkuPickerPropertiesPropertyName
  //------------------------------------
  private String mChildSkuPickerPropertiesPropertyName = "childSkuPickerProperties";

  /**
   * @return
   *   The property name used to identify the SKU properties that are used to create SKU pickers.
   */
  public String getChildSkuPickerPropertiesPropertyName() {
    return mChildSkuPickerPropertiesPropertyName;
  }

  /**
   * @param pChildSkuPickerPropertiesPropertyName
   *   Set the property name used to identify the SKU properties that are used to create SKU pickers.
   */
  public void setChildSkuPickerPropertiesPropertyName(String pChildSkuPickerPropertiesPropertyName) {
    mChildSkuPickerPropertiesPropertyName = pChildSkuPickerPropertiesPropertyName;
  }
  
  //------------------------------------
  // property: inStockPropertyName
  //------------------------------------
  private String mInStockPropertyName = "inStock";
  
  /**
   * @return
   *   The property name used to identify the in stock property.
   */
  public String getInStockPropertyName() {
    return mInStockPropertyName;
  }

  /**
   * @param pInStockPropertyName
   *   Set the property name used to identify the in stock property.
   */
  public void setInStockPropertyName(String pInStockPropertyName) {
    mInStockPropertyName = pInStockPropertyName;
  }
  
  //------------------------------------
  // property: isAvailablePropertyName
  //------------------------------------
  private String mIsAvailablePropertyName = "isAvailable";
  
  /**
   * @return 
   *   The property name used to identify the availability status of an item.
   */
  public String getIsAvailablePropertyName() {
    return mIsAvailablePropertyName;
  }

  /**
   * @param pIsAvailablePropertyName
   *   Set the property name used to identify the availability status of an item.
   */
  public void setIsAvailablePropertyName(String pIsAvailablePropertyName) {
    mIsAvailablePropertyName = pIsAvailablePropertyName;
  }
}
