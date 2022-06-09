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

package atg.projects.store.sku.picker;

import java.util.Collection;
import java.util.Map;

import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * Interface to define the common methods available on all product detail pickers.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/sku/picker/SkuPicker.java#1 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public interface SkuPicker {
  
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/sku/picker/SkuPicker.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------
  
  /**
   * Gets the currently selected option for the picker invoking the method.
   * 
   * @param pOptions 
   *   {@code Map} of the currently available property options.
   * @return 
   *   The currently selected Option.
   */
  public String getSelectedOption(Map<String, String> pOptions) throws RepositoryException;
  
  /**
   * Gets all of the possible options available for the SKU picker invoking the method.
   * 
   * @param pOptions 
   *   {@code Map} of the currently available property options.
   * @return 
   *   A {@code Collection} of all of the possible options for the current type.
   */
  public Collection<String> getAllOptions(Map<String, String> pOptions) throws RepositoryException;
  
  /**
   * Find the SKU in the passed {@code Collection} that matches the value passed in pOption.
   * 
   * @param pSkus 
   *   A {@code Collection} of all SKUs.
   * @param pOption 
   *   The currently selected option
   * @return 
   *   A {@code RepositoryItem} representing the currently selected SKU.
   */
  public RepositoryItem findSkuByOption(Collection<RepositoryItem> pSkus, String pOption);
  
  /**
   * Find the selected SKU for the current passed product Id using the SKU information passed in the
   * pOptions map.
   * 
   * @param pOptions
   *   A {@code Map<String,String>} of options.  If "productIdPropertyName" is not present
   *   then {@code null} will be used as the repository id and may result in a
   *   {@code RepositoryException} being thrown.
   * @return
   *   {@code RepositoryItem} The currently selected SKU.
   * @throws RepositoryException
   */
  public RepositoryItem getSelectedSku(Map<String, String> pOptions) throws RepositoryException;

  /**
   * Get any configured helper information for the current picker.
   *
   * @param pPropertyName
   *   A {@code String} property name identifying the SKU property that must be present for
   *   this method to return a non null result.
   * @param pOptions
   *   A {@code Map<String,String>} of options.  If "productIdPropertyName" is not present
   *   then {@code null} will be used as the repository id and may result in a
   *   {@code RepositoryException} being thrown.
   * @return
   *   {@code Map} The configured helper information for the SKU picker.
   */
  public Map<String, String> getHelperInformation(String pPropertyName, Map<String, String> pOptions)
    throws RepositoryException;

}
