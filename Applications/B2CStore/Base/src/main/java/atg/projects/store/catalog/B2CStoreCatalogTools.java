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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import atg.repository.RepositoryItem;

/**
 * The extensions to StoreCatalogTools. Contains properties and methods specific to the catalog for
 * the B2CStore
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Base/src/main/java/atg/projects/store/catalog/B2CStoreCatalogTools.java#1 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class B2CStoreCatalogTools extends StoreCatalogTools {

  // Class version string
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Base/src/main/java/atg/projects/store/catalog/B2CStoreCatalogTools.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------
  
  //------------------------------------
  // property: sizeSortOrder
  //------------------------------------
  String[] mSizeSortOrder = null;

  /**
   * @return 
   *   Returns the SizeSortOrder used for correctly sorting clothing sizes.
   */
  public String[] getSizeSortOrder() {
    return mSizeSortOrder;
  }

  /**
   * @param pSizeSortOrder 
   *   Sets the array that is used for correctly sorting clothing sizes.
   */
  public void setSizeSortOrder(String[] pSizeSortOrder) {
    mSizeSortOrder = pSizeSortOrder;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------
  
  /**
   * Returns the list of possible sizes for a given collection of SKUs.
   *
   * @param pSkus 
   *   Collection of SKUs
   * @return 
   *   List of colors
   */
  public List<String> getPossibleSizes(Collection<RepositoryItem> pSkus) {
    B2CStoreCatalogProperties catalogProperties =
      (B2CStoreCatalogProperties) getCatalogProperties();

    return getPossibleValuesForSkus(pSkus, catalogProperties.getSizePropertyName());
  }

  /**
   * Obtains a list of sizes used by SKUs specified. This list will be sorted in correspondence to 
   * the {@link #getSizeSortOrder()} property.
   * 
   * @see {@link #getSizeSortOrder()}
   * @param pSkus 
   *   {@code Collection} of SKUs sizes should be taken from.
   * @return 
   *   A List<String> of sorted sizes.
   */
  public List<String> getSortedSizes(Collection<RepositoryItem> pSkus) {
    return sortSizes(getPossibleSizes(pSkus));
  }

  /**
   * Sorts a list of colors.
   * 
   * @param pColors
   *   {@code List} of colors
   * @return 
   *   The sorted list of colors.
   */
  public List<String> sortColors(List<String> pColors) {
    
    ArrayList<String> sorted = new ArrayList<>(pColors.size());
    sorted.addAll(pColors);
    Collections.sort(sorted);

    return sorted;
  }
  
  /**
   * Sorts sizes according to the configured template.
   * 
   * @see #getSizeSortOrder()
   * 
   * @param pSizes
   *   <code>List</code> of sizes
   * @return 
   *   The sorted <code>List</code> of sizes.
   */
  public List<String> sortSizes(List<String> pSizes) {
    
    String[] sortOrder = getSizeSortOrder();

    if ((sortOrder != null) && (sortOrder.length > 0)) {
      return sortStrings(pSizes, getSizeSortOrder());
    } 
    else {
      return pSizes;
    }
  }
}
