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

import atg.nucleus.GenericService;
import atg.projects.store.catalog.B2CStoreCatalogProperties;
import atg.projects.store.catalog.B2CStoreCatalogTools;
import atg.projects.store.product.ProductDetailsTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class that should be used as the base for SKU pickers on the Single/Multiple SKU Product
 * Details Pages.  It provides the common methods and properties that are used to derive the
 * currently selected SKU and its properties.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/sku/picker/DefaultSkuPicker.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public abstract class DefaultSkuPicker extends GenericService implements SkuPicker {
  
  /** Class version string. */ 
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/sku/picker/DefaultSkuPicker.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------
  
  //-----------------------------------
  // property: catalogTools
  //-----------------------------------
  private B2CStoreCatalogTools mCatalogTools = null;

  /**
   * @return mCatalogTools
   *   Returns the mCatalogTools component
   */
  public B2CStoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  /**
   * @param pCatalogTools
   *   Sets the mCatalogTools component
   */
  public void setCatalogTools(B2CStoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }
  
  //-----------------------------------
  // property: productDetailsTools
  //-----------------------------------
  private ProductDetailsTools mProductDetailsTools = null;

  /**
   * @return mProductDetailTools
   *   Returns the defined instance of the ProductDetailTools which contains a number of helper 
   *   methods.
   */
  public ProductDetailsTools getProductDetailTools() {
    return mProductDetailsTools;
  }

  /**
   * @param pProductDetailTools
   *   Sets the defined instance of the ProductDetailTools which contains a number of helper
   *   methods.
   */
  public void setProductDetailsTools(ProductDetailsTools pProductDetailTools) {
    mProductDetailsTools = pProductDetailTools;
  }
  
  //-----------------------------------
  // property: catalogProperties
  //-----------------------------------
  /**
   * @return mCatalogProperties
   *   Returns the CatalogProperties from the CatalogTools property.
   */
  public B2CStoreCatalogProperties getCatalogProperties() {
    return (B2CStoreCatalogProperties) getCatalogTools().getCatalogProperties();
  }
  
  //----------------------------------------
  // property: skuPickerPropertyManager
  //----------------------------------------
  private SkuPickerPropertyManager mSkuPickerPropertyManager = null;
  
  /**
   * @return mSkuPickerPropertyManager
   *   Returns the defined instance of the SkuPickerPropertyManager.
   */
  public SkuPickerPropertyManager getSkuPickerPropertyManager() {
    return mSkuPickerPropertyManager;
  }

  /**
   * @param pSkuPickerPropertyManager
   *   Sets the defined instance of the SkuPickerPropertyManager.
   */
  public void setSkuPickerPropertyManager(SkuPickerPropertyManager pSkuPickerPropertyManager) {
    mSkuPickerPropertyManager = pSkuPickerPropertyManager;
  }

  //-------------------------------------
  // property: helperPropertyBlackList
  //-------------------------------------
  private List<String> mHelperPropertyBlackList;

  /**
   * @return
   *   Returns a list of property names that, if present, will result in configured helper
   *   information not being returned.
   */
  public List<String> getHelperPropertyBlackList() {
    return mHelperPropertyBlackList;
  }

  /**
   * @param pHelperPropertyBlackList
   *   Sets a list of property names that, if present, will result in configured helper
   *   information not being returned.
   */
  public void setHelperPropertyBlackList(List<String> pHelperPropertyBlackList) {
    mHelperPropertyBlackList = pHelperPropertyBlackList;
  }
  
  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * This method retrieves a product from the repository using the repository id passed as an 
   * argument
   * 
   * @param pOptions
   *   A {@code Map<String, String>} of options.  This method requires the presence of a map
   *   entry with a key of "productIdPropertyName".  Otherwise {@code null} will be used as the
   *   repository id and may result in a {@code RepositoryException} being thrown.
   * @return 
   *   The {@code RepositoryItem} for the productIdPropertyName that was passed in pOptions.
   * @throws RepositoryException
   *   a {@code RepositoryException} may be thrown by this method if the pOtions parameter 
   *   does not include an entry with a key of "productIdPropertyName".
   */
  protected RepositoryItem getCurrentProduct(Map<String, String> pOptions)
      throws RepositoryException {
    
    // Get the product repository id from the pOptions map.
    String productId = pOptions.get(getSkuPickerPropertyManager().getProductIdPropertyName());
    
    return getCatalogTools().findProduct(productId);
  }
  
  /**
   * This method obtains a {@code Collection} of all child SKUs for the product passed.  If 
   * null is returned for the child SKUs then an empty collection will be returned instead.
   * 
   * @param pProduct 
   *   A product {@code RepositoryItem} that we wish to retrieve the child SKUs for.
   * 
   * @return 
   *   a {@code Collection} of the products child SKUs.
   */
  protected Collection<RepositoryItem> getAllSkus(RepositoryItem pProduct) {

    Collection<RepositoryItem> childSkus = new ArrayList<>();

    if (pProduct != null) {
      Object childSkuObjs =
        pProduct.getPropertyValue(getCatalogProperties().getChildSkusPropertyName());

      if (childSkuObjs instanceof Collection<?>) {
        childSkus = (Collection<RepositoryItem>) childSkuObjs;
      }
    }
    return childSkus;
  }
  
  /**
   * This method obtains a {@code Collection} of all specified product SKUs. These SKU are 
   * taken from the childSkus property of the current product and are filtered so that only SKUs 
   * that are currently active are returned.
   * 
   * @param pProduct
   *   A product {@code RepositoryItem} that we wish to retrieve the filtered list of child 
   *   SKUs for.
   * 
   * @return 
   *   a filtered {@code Collection} of the products child SKUs.
   */
  protected Collection<RepositoryItem> getFilteredSkus(RepositoryItem pProduct) {
    
    // Get all of the child SKUs for the given product.
    Collection<RepositoryItem> childSkus = getAllSkus(pProduct);
    
    // Initialize the returned ArrayList to the size of the number of child SKUs.
    Collection<RepositoryItem> filteredSkus = new ArrayList<>(childSkus.size());
    
    // Validate each child SKU to ensure that its start and end dates are in range.  If they are 
    // then add it to the collection to be returned
    for (RepositoryItem sku : childSkus) {
      if (getProductDetailTools().isValidItem(sku)) {
        filteredSkus.add(sku);
      }
    }
    
    return filteredSkus;
  }

  /**
   * Get any configured helper information for the current picker.  Returns and empty map if the
   * only option available for the current picker is present in the property black list.
   *
   * @param pPropertyName
   *   A {@code String} property name identifying the SKU property that must be present for this
   *   method to return a non empty result.
   * @return
   *   {@code Map} The configured helper information for the SKU picker.
   */
  @Override
  public Map<String, String> getHelperInformation(String pPropertyName, Map<String, String> pOptions)
    throws RepositoryException {

    if (pOptions != null && pPropertyName != null) {

      Map<String, String> helperInfo = new HashMap<>();

      // Get a list of all of the options available for the current picker.
      List<String> allOptions = (List<String>) getAllOptions(pOptions);

      // Only get the help information if on of the following is true.
      //  1) There is more than one option available for the current picker.
      //  2) There is only one option available for the current picker and that option is not
      //     present in the black list.
      if(allOptions.size() > 1) {
        helperInfo = getProductDetailTools().getHelperInformation(pPropertyName);
      }
      else if (getHelperPropertyBlackList() != null) {
        if (allOptions.size() == 1 && !getHelperPropertyBlackList().contains(allOptions.get(0))) {
          helperInfo = getProductDetailTools().getHelperInformation(pPropertyName);
        }
      }
      return helperInfo;
    }
    return null;
  }
}
