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

package atg.projects.store.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryManager;
import atg.nucleus.GenericService;
import atg.nucleus.ResolvingMap;
import atg.projects.store.catalog.StoreCatalogProperties;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.collections.validator.StartEndDateValidator;
import atg.service.filter.bean.BeanFilterException;
import atg.service.filter.bean.BeanFilterRegistry;

/**
 * A tools class that contains a collection of methods that are commonly used while working with the 
 * product detail page.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/product/ProductDetailsTools.java#1 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ProductDetailsTools extends GenericService {
  
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/product/ProductDetailsTools.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------
  public static final String ID = "id";
  
  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------
  
  //----------------------------------------------------------
  //  property: startEndDateValidator
  //----------------------------------------------------------
  private StartEndDateValidator mStartEndDateValidator = null;
  
  /**
   * @param pStartEndDateValidator
   *   The start/end date validator component which is used to check if a repository items start and
   *   end date are in range.
   */
  public void setStartEndDateValidator(StartEndDateValidator pStartEndDateValidator) {
    mStartEndDateValidator = pStartEndDateValidator;
  }
  
  /**
   * @return 
   *   Return the start/end date validator in order to validate the repository items start and end 
   *   dates.
   */
  public StartEndDateValidator getStartEndDateValidator() {
    return mStartEndDateValidator;
  }
  
  //-------------------------------------
  // property: inventoryServices
  //-------------------------------------
  private InventoryManager mInventoryManager = null;
  
  /**
   * @param pInventoryManager
   *   Component that contains utility methods for checking the inventory status of a given SKU.
   */
  public void setInventoryManager(InventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }
  
  /**
   * @return
   *   Returns the component that contains utility methods for checking the inventory status of a 
   *   given SKU.
   */
  public InventoryManager getInventoryManager() {
    return mInventoryManager;
  }
  
  //-------------------------------------
  // property: catalogTools
  //-------------------------------------
  private StoreCatalogTools mCatalogTools = null;
  
  /**
   * @param pCatalogTools
   *   Sets a tools component for working with catalog's.  This includes access to CatalogProperties
   *   and convenience methods for querying the repository.
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }
  
  /**
   * @return
   *   Returns the catalog tools component for working with catalog's.  This includes access to
   *   CatalogProperties and convenience methods for querying the repository.
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }
  
  //----------------------------------------
  // property: productDetailsPropertyManager
  //----------------------------------------
  private ProductDetailsPropertyManager mProductDetailsPropertyManager = null;
  
  /**
   * @param pProductDetailsPropertyManager
   *   A property manager component that contains properties that are commonly used within the 
   *   product detail page.   
   */
  public void setProductDetailsPropertyManager(ProductDetailsPropertyManager pProductDetailsPropertyManager) {
    mProductDetailsPropertyManager = pProductDetailsPropertyManager;
  }
  
  /**
   * @return
   *   Returns the product details property manager component.
   */
  public ProductDetailsPropertyManager getProductDetailsPropertyManager() {
    return mProductDetailsPropertyManager;
  }

  //-----------------------------------
  // property: beanFilterRegistry
  //-----------------------------------
  private BeanFilterRegistry mBeanFilterRegistry;

  /**
   * @return
   *   BeanFilterRegistry component used to filter properties on a bean.
   */
  public BeanFilterRegistry getBeanFilterRegistry() {
    return mBeanFilterRegistry;
  }

  /**
   * @param pBeanFilterRegistry
   *   Set a new BeanFilterRegistry.
   */
  public void setBeanFilterRegistry(BeanFilterRegistry pBeanFilterRegistry) {
    mBeanFilterRegistry = pBeanFilterRegistry;
  }
  
  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * Using CatalogTools get the {@code RepositoryItem} that matches the passed product ID
   *
   * @param pProductId
   *   The product ID.
   * @return
   *   The product {@code RepositoryItem} from the Repository that matches the product ID
   *   passed.
   */
  public RepositoryItem getProduct(String pProductId) {

    RepositoryItem product = null;
    if (pProductId != null) {

      // Using CatalogTools query the repository for the product
      try {
        product = getCatalogTools().findProduct(pProductId);
      }
      catch (RepositoryException e) {
        vlogError(e, "An error occurred when attempting to retrieve product [{0}] from the repository",
          pProductId);
      }
    }
    return product;
  }
  
  /**
   * Retrieves any helper information that may have been configured for a SKU property that is used
   * as the source of a SKU selector/picker for example, a size chart for a SKUs size property.
   *
   * @param pSkuPropertyName
   *   The name of the SKU property that we are looking for helper information that may be
   *   configured for it.
   * @return
   *   A {@code Map} containing the helper information for the passed SKU property.
   */
  public Map<String, String> getHelperInformation(String pSkuPropertyName) {

    Map<String, Object> pickerHelperInformation =
        getProductDetailsPropertyManager().getAdditionalInformationProperties();

    Map<String, String> helperInfo = new HashMap<>();

    // If additional information has been configured for the passed SKU property name then retrieve
    // it so it may be returned.
    if (pickerHelperInformation != null) {
      Map<String, String> configuredHelperInformation =
        (Map<String, String>) pickerHelperInformation.get(pSkuPropertyName);
      if (configuredHelperInformation != null) {
        helperInfo.putAll(configuredHelperInformation);
      }
    }
    return helperInfo;
  }
  
  /**
   * Validate RepositoryItem start/end dates. If the items start date is in the future or it's end
   * date is in the past, this method will return false.  Otherwise it will return true.
   * 
   * @param pRepositoryItem
   *   The {@code RepositoryItem} that we wish to test.
   * 
   * @return 
   *   <p>{@code true} or {@code false} based on the following logic.</p>
   *   <p>If the RepositoryItem start date is in the future or it's end date is in the past then
   *   return false.</p>
   *   <p>If the RepositoryItem start date is in the past or it's end date is in the future then 
   *   return true.</p>
   */
  public boolean isValidItem(RepositoryItem pRepositoryItem) {
    return getStartEndDateValidator() != null && pRepositoryItem != null &&
      getStartEndDateValidator().validateObject(pRepositoryItem);
  }

  /**
   * Determine if a repository item is in stock or not.
   *
   * @param pRepositoryItem
   *   The {@code RepositoryItem} that we wish to test.
   *
   * @return
   *   true if the item is in stock; false if the item is not in stock.
   */
  public boolean isItemInStock(RepositoryItem pRepositoryItem) {

    boolean inStock = false;
    try {

      // Get the item inventory availability status.
      int availabilityStatus = getInventoryManager().queryAvailabilityStatus(pRepositoryItem.getRepositoryId());

      // Determine if the item is in stock.
      inStock = InventoryManager.AVAILABILITY_STATUS_IN_STOCK == availabilityStatus;
    }
    catch (InventoryException e) {
      vlogWarning("Missing Inventory Information for {0}. Setting inStock property to false",
              pRepositoryItem.getRepositoryId());
      vlogDebug(e, "An error occurred while attempting to retrieve Inventory information for {0}. Setting inStock property to false",
              pRepositoryItem.getRepositoryId());
    }
    return inStock;
  }

  /**
   * Takes a {@code RepositoryItem} and applies the bean filter identified by the filter id
   * passed in pFilterId then returns the result.
   *
   * @param pItem
   *   A {@code RepositoryItem} to be filtered.
   * @param pFilterId
   *   The id of the filter that should be applied to pItem.
   * @return
   *   A {@code Map} of the {@code RepositoryItem} properties that are included in the
   *   filter identified by pFilterId.
   * @throws BeanFilterException
   */
  public Map getFilteredItemMap(RepositoryItem pItem, String pFilterId)
    throws BeanFilterException {

    Map filteredMap = (Map) getBeanFilterRegistry().applyFilter(pItem, pFilterId,
      new HashMap<BeanFilterRegistry.FilterOptionKey,Object>());

    if (filteredMap == null) {
      filteredMap = new HashMap();
    }

    return filteredMap;
  }

  /**
   * Takes the product {@code RepositoryItem} and applies the bean filter identified by the
   * filter id passed in pFilterId.  Once the filter has been applied additional properties relating
   * to the type of child SKUs belonging to the product are added to the {@code Map} and then
   * the result is returned.
   *
   * @param pProduct
   *   The product {@code RepositoryItem} to filter.
   * @param pFilterId
   *   The id of the filter that should be applied to pProduct.
   * @param pSkuTypes
   *   A {@code ResolvingMap} of SKU types that the product detail page works with.
   * @return
   *   A {@code Map} of the product {@code RepositoryItem} properties that are included in the
   *   filter identified by pFilterId.
   * @throws BeanFilterException
   */
  public Map getFilteredProductMap(RepositoryItem pProduct, String pFilterId, ResolvingMap pSkuTypes)
    throws BeanFilterException {

    Map filteredMap = getFilteredItemMap(pProduct, pFilterId);
    StoreCatalogProperties catalogProperties =
      (StoreCatalogProperties) getCatalogTools().getCatalogProperties();

    // Mark the availability of the product
    filteredMap.put(catalogProperties.getIsAvailablePropertyName(), isValidItem(pProduct));

    // Get the list of child SKUs in order to access the SKU specific properties.
    if (pProduct != null) {
      List<RepositoryItem> childSKUs = (List<RepositoryItem>)
        pProduct.getPropertyValue(catalogProperties.getChildSkusPropertyName());

      // If the product has child SKUs add the following properties to the filtered product map if
      // present.
      //  1) SKU type.
      //  2) The SKU properties that have been configured in the skuType property.  These are unique
      //     to this SKU type and can be useful later.
      if (childSKUs != null && !childSKUs.isEmpty()) {
        String skuType =
          (String) childSKUs.get(0).getPropertyValue(catalogProperties.getSkuTypePropertyName());
        filteredMap.put(catalogProperties.getChildSkuTypePropertyName(), skuType);

        if (pSkuTypes != null) {
          List<String> skuPickerProperties = (List<String>) pSkuTypes.get(skuType);
          if (skuPickerProperties != null) {
            filteredMap.put(catalogProperties.getChildSkuPickerPropertiesPropertyName(),
              skuPickerProperties);
          }
        }
      }
    }

    return filteredMap;
  }

  /**
   * Takes the SKU {@code RepositoryItem} and applies the bean filter identified by the filter
   * id passed in pFilterId.  Once the filter has been applied additional properties related to the
   * SKU are added to the {@code Map} and then the result is returned.
   *
   * @param pSku
   *   The SKU {@code RepositoryItem} to filter.
   * @param pFilterId
   *   The id of the filter that should be applied to pSku.
   * @return
   *   A {@code Map} of the SKU {@code RepositoryItem} properties that are included in
   *   the filter identified by pFilterId.
   * @throws BeanFilterException
   * @throws InventoryException
   */
  public Map getFilteredSkuMap(RepositoryItem pSku, String pFilterId) throws BeanFilterException {

    Map filteredMap = getFilteredItemMap(pSku, pFilterId);

    // Get a local instance of StoreCatalogProperties from CatalogTools
    StoreCatalogProperties catalogProperties =
      (StoreCatalogProperties) getCatalogTools().getCatalogProperties();

    // Once the filtered map of SKU properties has been returned add the isAvailable and inStock
    // properties to the map as well.
    filteredMap.put(catalogProperties.getIsAvailablePropertyName(), isValidItem(pSku));
    filteredMap.put(catalogProperties.getInStockPropertyName(), isItemInStock(pSku));

    return filteredMap;
  }
}
