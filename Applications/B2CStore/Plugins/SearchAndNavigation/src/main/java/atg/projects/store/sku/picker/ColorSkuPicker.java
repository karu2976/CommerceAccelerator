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
 * Extension of DefaultProductDetailsPicker that overrides the default method implementations in
 * order to work with the 'color' property of a SKU.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/sku/picker/ColorSkuPicker.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 * 
 * @see DefaultSkuPicker
 */
public class ColorSkuPicker extends DefaultSkuPicker {
  
  /** Class version string. */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/sku/picker/ColorSkuPicker.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------

  //------------------------------------
  // property: colorPropertyName
  //------------------------------------
  private String mColorPropertyName = "color";
  
  /**
   * @return mColorPropertyName
   *   Returns the name of the property that contains the color of a SKU.
   */
  public String getColorPropertyName() {
    return mColorPropertyName;
  }

  /**
   * @param pColorPropertyName
   *   Sets the name of the property that contains the color of a SKU.
   */
  public void setColorPropertyName(String pColorPropertyName) {
    mColorPropertyName = pColorPropertyName;
  }
  
  //------------------------------------
  // property: selectedColorPropertyName
  //------------------------------------
  private String mSelectedColorPropertyName = "selectedColor";
  
  /**
   * @return mSelectedColorPropertyName
   *   Returns the name of the property that contains the selected value of a SKU color picker.
   */
  public String getSelectedColorPropertyName() {
    return mSelectedColorPropertyName;
  }

  /**
   * @param pSelectedColorPropertyName
   *   Sets the name of the property that contains the selected value of a SKU color picker.
   */
  public void setSelectedColorPropertyName(String pSelectedColorPropertyName) {
    mSelectedColorPropertyName = pSelectedColorPropertyName;
  }

  //-----------------------------------
  // property: availableColorsPropertyName
  //-----------------------------------
  private String mAvailableColorsPropertyName = "availableColors";

  /**
   * @return
   *   The property used to identify the all of the available values for the SKUs color property.
   */
  public String getAvailableColorsPropertyName() {
    return mAvailableColorsPropertyName;
  }

  /**
   * @param pAvailableColorsPropertyName
   *   Set the name of the property used to identify the all of the available values for the SKUs
   *   color property.
   */
  public void setAvailableColorsPropertyName(String pAvailableColorsPropertyName) {
    mAvailableColorsPropertyName = pAvailableColorsPropertyName;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * This method obtains currently selected color. It first looks into {@code selectedColor} 
   * pOptions entry. If something is specified within this parameter, this value will be returned.
   * If nothing found, this method calculates all available SKUs colors; if there is only one color 
   * available, this color is returned as selected.
   * 
   * @param pOptions
   *   A {@code Map<String, String>} of options.  This method requires the presence of a map
   *   entry with a key of "productIdPropertyName".  Otherwise {@code null} will be used as the
   *   repository id and may result in a {@code RepositoryException} being thrown.
   * @return 
   *   A {@code String} representing the selected color if only one color is available or null.
   * @throws RepositoryException 
   *   a {@code RepositoryException} may be thrown by this method if the pOptions parameter
   *   does not include an entry with a key of "productIdPropertyName".
   */
  @Override
  public String getSelectedOption(Map<String, String> pOptions) throws RepositoryException {
    
    String selectedColor = pOptions.get(getSelectedColorPropertyName());
    
    if (!StringUtils.isBlank(selectedColor)) {
      return selectedColor;
    }
    
    RepositoryItem product = getCurrentProduct(pOptions);
    
    List<String> possibleColors = 
        getCatalogTools().getPossibleValuesForSkus(getAllSkus(product), getColorPropertyName());
    
    if (possibleColors.size() == 1) {
      return possibleColors.get(0);
    }
    
    return null;
  }

  /**
   * This method derives all possible colors for the SKUs specified. These colors are returned in
   * the form of a Collection of Strings.
   * 
   * @param pOptions
   *   A {@code Map<String, String>} of options.  This method requires the presence of a map
   *   entry with a key of "productIdPropertyName".  Otherwise {@code null} will be used as the
   *   repository id and may result in a {@code RepositoryException} being thrown.
   * @return 
   *   A sorted {@code Collection} of all possible color names.
   */
  @Override
  public Collection<String> getAllOptions(Map<String, String> pOptions) throws RepositoryException {
    
    RepositoryItem product = getCurrentProduct(pOptions);
    List<String> allColors = 
        getCatalogTools().getPossibleValuesForSkus(getAllSkus(product), getColorPropertyName());

    return getCatalogTools().sortColors(allColors);
  }
  
  /**
   * This method derives currently selected SKU based on the currently selected color.
   * 
   * @param pOptions
   *   A {@code Map<String, String>} of options.  This method requires the presence of a map
   *   entries with keys of "productIdPropertyName" and "selectedColor".  If no "selectedColor" is 
   *   included in pOptions then there is no selected color and and empty {@code Map} will be 
   *   returned.  If "productIdPropertyName" is not present then {@code null} will be used as
   *   the repository id and may result in a {@code RepositoryException} being thrown.
   * @return 
   *   {@code RepositoryItem} The currently selected SKU.
   * @throws RepositoryException 
   *   A {@code RepositoryException} may be thrown by this method if the pOptions parameter
   *   does not include an entry with a key of "productIdPropertyName"
   */
  @Override
  public RepositoryItem getSelectedSku(Map<String, String> pOptions)
      throws RepositoryException {
    
    String selectedColor = pOptions.get(getSelectedColorPropertyName());

    // No color selected? then no SKU selected.
    if (selectedColor == null) {
      return null;
    }

    RepositoryItem product = getCurrentProduct(pOptions);
    return findSkuByOption(getAllSkus(product), selectedColor);
  }

  /**
   * This method searches for a SKU by its color. It iterates over all specified SKUs and retrieves 
   * first SKU with color specified.
   * 
   * @param pSkus 
   *   A {@code Collection} of repository items representing the SKUs to check.
   * @param pOption 
   *   A {@code String} representing the color to search for in the SKUs.
   * @return 
   *   A {@code RepositoryItem} which is the SKU with a color property matching value passed in
   *   pOption.
   */
  @Override
  public RepositoryItem findSkuByOption(Collection<RepositoryItem> pSkus, String pOption) {
    
    for (RepositoryItem sku : pSkus) {
      String skuColor = (String) sku.getPropertyValue(getColorPropertyName());
      
      if (pOption.equals(skuColor)) {
        return sku;
      }
    }
    return null;
  }
}
