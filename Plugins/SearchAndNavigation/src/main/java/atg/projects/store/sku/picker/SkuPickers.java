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

import java.util.Map;

import atg.core.util.StringUtils;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceMap;
import atg.projects.store.catalog.StoreCatalogProperties;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * Nucleus component that contains a service map of type ProductDetailsPicker.  Is also the target 
 * of REST requests from the client when a SKU selection is made.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/sku/picker/SkuPickers.java#1 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class SkuPickers extends GenericService {
  
  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/sku/picker/SkuPickers.java#1 $$Change: 1385662 $";
  
  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------
  
  //-------------------------------------
  // property: skuPickerMap
  //-------------------------------------
  private ServiceMap mSkuPickerMap = null;
  
  /**  
   * @param pSkuPickers
   *   This is a {@code ServiceMap} that holds the components of type {@code SkuPicker} that
   *   represent the picker components that are used to build up the SKU Picker data structure that
   *   is returned to the client.  This map should be keyed on the the unique property for the SKU
   *   type, for example "color" or "size".
   */
  public void setSkuPickerMap(ServiceMap pSkuPickers) {
    mSkuPickerMap = pSkuPickers;
  }
  
  /**
   * @return 
   *   The map containing values of type {@code SkuPicker} that has been defined in the
   *   {@code ServiceMap}.
   */
  public ServiceMap getSkuPickerMap() {
    return mSkuPickerMap;
  }
  
  //-------------------------------------
  // property: skuTypeToSkuPickerMap
  //-------------------------------------
  private ServiceMap mSkuTypeToSkuPickerMap = null;
  
  /**  
   * @param pSkuPickers
   *   This is a {@code ServiceMap} that holds the components of type {@code ProductDetailsPicker}
   *   that represent the picker components that are used to refine a product down to a single SKU.
   *   This map should be keyed on the SKU type.
   */
  public void setSkuTypeToSkuPickerMap(ServiceMap pSkuPickers) {
    mSkuTypeToSkuPickerMap = pSkuPickers;
  }
  
  /**
   * @return 
   *   The map containing values of type {@code SkuPicker} that has been defined in the
   *   {@code ServiceMap}
   */
  public ServiceMap getSkuTypeToSkuPickerMap() {
    return mSkuTypeToSkuPickerMap;
  }
  
  //-------------------------------------
  // property: catalogProperties
  //-------------------------------------
  private StoreCatalogProperties mCatalogProperties = null;
  
  /**  
   * @param pCatalogProperties
   *   This is a {@code StoreCatalogProperties}  This component contains a number of commonly 
   *   used properties when interacting with the catalog and its child items.
   */
  public void setCatalogProperties(StoreCatalogProperties pCatalogProperties) {
    mCatalogProperties = pCatalogProperties;
  }
  
  /**
   * @return 
   *   Returns the catalog properties component.  This component contains a number of commonly used
   *   properties when interacting with the catalog and its child items.
   */
  public StoreCatalogProperties getCatalogProperties() {
    return mCatalogProperties;
  }
  
  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * This method routes the request to the correct picker component in the mSkuPickers
   * {@code ServiceMap} based on the SKU type passed in the pOptions {@code Map}.
   *
   * @param pOptions
   *   A {@code Map<String,String>} of options.  This method requires the presence of a map
   *   entry with a key of "type".  If no "type" is included in pOptions then there is no
   *   way to determine which sku picker to route the request to an an exception is thrown.
   * @return
   *   {@code RepositoryItem} The currently selected SKU.
   * @throws RepositoryException
   */
  public RepositoryItem getSelectedSkuForType(Map<String, String> pOptions)
    throws RepositoryException {

    // Get the skuType property from pOptions.
    String skuType = pOptions.get(getCatalogProperties().getSkuTypePropertyName());

    // If no skuType was passed then we cannot proceed so log an error.
    if (StringUtils.isBlank(skuType)) {
      vlogError("The mandatory MapEntry in the pOptions map with a key of [type] can not be found.");
    }
    else {

      // Get the correct picker using the skuType and then populate the property map by calling the
      // getSelectedSku method on the picker.
      SkuPicker picker = (SkuPicker) getSkuTypeToSkuPickerMap().get(skuType);

      if (picker != null) {
        return picker.getSelectedSku(pOptions);
      }
      else {
        vlogError("The MapEntry in the pOptions map with a key of [type] does not match any configured ProductDetailPickers.");
      }
    }

    return null;
  }
}
