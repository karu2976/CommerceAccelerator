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
import java.util.List;
import java.util.Map;

import atg.core.util.StringUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * Extension of ColorProductDetailsPicker that overrides the default method implementations in
 * order to work with both the 'size' and 'color' properties of a SKU.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/sku/picker/SizeColorSkuPicker.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 * 
 * @see ColorSkuPicker
 */
public class SizeColorSkuPicker extends ColorSkuPicker {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/sku/picker/SizeColorSkuPicker.java#1 $$Change: 1385662 $";
  
  //-------------------------------------
  // property: sizePropertyName
  //-------------------------------------
  private String mSizePropertyName = "size";
  
  /**
   * @return mSizePropertyName
   *   Returns the the name of the property that contains the size of a SKU.
   */
  public String getSizePropertyName() {
    return mSizePropertyName;
  }

  /**
   * @param pSizePropertyName
   *   Sets the the name of the property that contains the size of a SKU.
   */
  public void setSizePropertyName(String pSizePropertyName) {
    mSizePropertyName = pSizePropertyName;
  }
  
  //-------------------------------------
  // property: selectedSizePropertyName
  //-------------------------------------
  private String mSelectedSizePropertyName = "selectedSize";

  /**
   * @return 
   *   Returns the name of the property that contains the selected value of a SKU size picker.
   */
  public String getSelectedSizePropertyName() {
    return mSelectedSizePropertyName;
  }

  /**
   * @param pSelectedSize
   *   Sets the name of the property that contains the selected value of a SKU size picker.
   */
  public void setSelectedSizePropertyName(String pSelectedSize) {
    mSelectedSizePropertyName = pSelectedSize;
  }

  //-----------------------------------
  // property: availableSizesPropertyName
  //-----------------------------------
  private String mAvailableSizesPropertyName = "availableSizes";

  /**
   * @return
   *   The property used to identify the all of the available values for the SKUs size property.
   */
  public String getAvailableSizesPropertyName() {
    return mAvailableSizesPropertyName;
  }

  /**
   * @param pAvailableSizesPropertyName
   *   Set the name of the property used to identify the all of the available values for the SKUs
   *   size property.
   */
  public void setAvailableSizesPropertyName(String pAvailableSizesPropertyName) {
    mAvailableSizesPropertyName = pAvailableSizesPropertyName;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------
  
  /**
   * This method derives currently selected SKU based on the currently selected color and size.
   * 
   * @param pOptions
   *   <p>A {@code Map<String, String>} of options.  This method requires the presence of a map
   *   entries with keys of "productIdPropertyName", "selectedSize" and "selectedColor".</p>  
   *   
   *   <p>If "selectedColor" and "selectedSize" are not included in pOptions then there is nothing 
   *   to search and an empty {@code Map} will be returned.</p>
   *   
   *   <p>When both "selectedColor" and "selectedSize" are included in pOptions then they are both 
   *   used to search for a SKU that have a matching color and size properties.</p>  
   *   
   *   <p>When a "selectedColor" is included in pOptions but not "selectedSize" then a 
   *   {@code Collection} of all possible sizes for the product is retrieved, if it is empty 
   *   then a matching SKU is searched for using only the "selectedColor".</p>
   *   
   *   <p>When a "selectedSize" is included in pOptions but not "selectedColor" then a 
   *   {@code Collection} of all possible colors for the product is retrieved, if it is empty 
   *   then a matching SKU is searched for using only the "selectedSize".</p>
   * @return 
   *   {@code RepositoryItem} The currently selected SKU which was searched for.
   * @throws RepositoryException 
   *   A {@code RepositoryException} may be thrown by this method if the pOtions parameter
   *   does not include an entry with a key of "productIdPropertyName".
   */
  @Override
  public RepositoryItem getSelectedSku(Map<String, String> pOptions) throws RepositoryException {

    String selectedColor = super.getSelectedOption(pOptions);
    String selectedSize = getSelectedOption(pOptions);
    RepositoryItem product = getCurrentProduct(pOptions);
    RepositoryItem sku = null;

    if (selectedColor != null && selectedSize != null) {

      // A color and size have been selected so get the sku by color and size.
      sku = findSkuByColorSize(getAllSkus(product), selectedColor, selectedSize);
    }
    else if (selectedColor != null && getAllOptions(pOptions).isEmpty()) {

      // The item does not have size options so get the sku by the selected color.
      sku = super.findSkuByOption(getAllSkus(product), selectedColor);
    }
    else if (selectedSize != null && super.getAllOptions(pOptions).isEmpty()) {

      // The item does not have color options so get the sku by the selected size only.
      sku = findSkuByOption(getAllSkus(product), selectedSize);
    }

    return sku;
  }

  /**
   * This method obtains currently selected size. It first looks into {@code selectedSize} 
   * pOptions entry. If something is specified within this parameter, this value will be returned
   * If nothing found, this method calculates all available SKUs sizes; if there is only one size 
   * available, this size is returned as selected.
   * 
   * @param pOptions
   *   A {@code Map<String, String>} of options.  This method requires the presence of a map
   *   entry with a key of "productIdPropertyName".  Otherwise {@code null} will be used as the
   *   repository id and may result in a {@code RepositoryException} being thrown.
   * @return 
   *   A {@code String} representing the selected size if only one size is available or null.
   * @throws RepositoryException 
   *   A {@code RepositoryException} may be thrown by this method if the pOtions parameter
   *   does not include an entry with a key of "productIdPropertyName".
   */
  @Override
  public String getSelectedOption(Map<String, String> pOptions) throws RepositoryException {
    
    String selectedSize = pOptions.get(getSelectedSizePropertyName());
    
    if (!StringUtils.isBlank(selectedSize)) {
      return selectedSize;
    }
    
    RepositoryItem product = getCurrentProduct(pOptions);
    List<String> possibleSizes = 
        getCatalogTools().getPossibleValuesForSkus(getAllSkus(product), getSizePropertyName());
    
    if (possibleSizes.size() == 1) {
      return possibleSizes.get(0);
    }
    return null;
  }

  /**
   * This method derives all possible sizes for the SKUs specified. These sizes are returned in
   * the form of a {@code Collection} of Strings.
   * 
   * @param pOptions
   *   A {@code Map<String, String>} of options.  This method requires the presence of a map
   *   entry with a key of "productIdPropertyName".  Otherwise {@code null} will be used as the
   *   repository id and may result in a {@code RepositoryException} being thrown.
   * @return 
   *   A sorted {@code Collection} of all possible size names.
   */
  @Override
  public Collection<String> getAllOptions(Map<String, String> pOptions) throws RepositoryException {
    
    RepositoryItem product = getCurrentProduct(pOptions);
    
    return getCatalogTools().getSortedSizes(getAllSkus(product));
  }

  /**
   * This method searches for a SKU by its size. It iterates over all specified SKUs and retrieves 
   * first SKU with size specified.
   * 
   * @param pSkus 
   *   A {@code Collection} of repository items representing the SKUs to check.
   * @param pOption 
   *   A {@code String} representing the size to search for in the SKUs.
   * @return 
   *   A {@code RepositoryItem} which is the SKU with a size property matching value passed in
   *   pOption.
   */
  @Override
  public RepositoryItem findSkuByOption(Collection<RepositoryItem> pSkus, String pOption) {
    
    for (RepositoryItem sku : pSkus) {
      String skuSize = (String) sku.getPropertyValue(getSizePropertyName());
      
      if (pOption.equals(skuSize)) {
        return sku;
      }
    }
    return null;
  }
    
  /**
   * This method searches for a matching SKU using the color and size passed.
   * 
   * @param pSkus 
   *   A {@code Collection} of SKUs to search for matching color and size properties.
   * @param pColor
   *   A {@code String} representing the color to search for.
   * @param pSize
   *   A {@code String} representing the size to search for.
   * @return 
   *   A {@code RepositoryItem} of the first SKU from the passed {@code Collection{@code  that 
   *   matches the values of both the pColor and pSize arguments.
   */
  protected RepositoryItem findSkuByColorSize(Collection<RepositoryItem> pSkus, String pColor,
      String pSize) {
    
    for (RepositoryItem sku : pSkus) {
      String skuColor = (String) sku.getPropertyValue(getColorPropertyName());
      String skuSize = (String) sku.getPropertyValue(getSizePropertyName());
      
      if (pColor.equals(skuColor) && pSize.equals(skuSize)) {
        return sku;
      }
    }
    return null;
  }
}
