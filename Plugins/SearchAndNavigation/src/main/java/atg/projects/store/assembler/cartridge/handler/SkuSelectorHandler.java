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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import atg.endeca.assembler.AssemblerTools;
import atg.projects.store.catalog.StoreCatalogProperties;
import atg.projects.store.sku.picker.SkuPicker;
import atg.projects.store.sku.picker.SkuPickers;
import atg.projects.store.sku.picker.SkuPickerPropertyManager;
import atg.repository.RepositoryException;

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.cartridge.RecordDetails;
import com.endeca.infront.cartridge.RecordDetailsConfig;

/**
 * Extension of {@Link ProductDetailsHandler}.  This class will return a
 * {@link com.endeca.infront.assembler.ContentItem} that, if configured to will contain a data
 * structures for all of the SKU pickers/selectors that should be associated with the current
 * product.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/assembler/cartridge/handler/SkuSelectorHandler.java#1 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class SkuSelectorHandler extends ProductDetailsHandler {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/assembler/cartridge/handler/SkuSelectorHandler.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------
  public static final String REPOSITORY_ID = "repositoryId";

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------

  //-------------------------------------
  // property: skuPickers
  //-------------------------------------
  private SkuPickers mSkuPickers = null;

  /**
   * @param pSkuPickers
   *   This is a {@code SkuPickers}  that holds a {@code ServiceMap}  containing the components
   *   that represent the picker components that are used to refine a product down to a single SKU.
   *   This map should be keyed on the SKU type.
   */
  public void setSkuPickers(SkuPickers pSkuPickers) {
    mSkuPickers = pSkuPickers;
  }

  /**
   * @return
   *   The {@code SkuPickers} that holds a {@code ServiceMap}  containing the components
   *   that represent the picker components.
   */
  public SkuPickers getSkuPickers() {
    return mSkuPickers;
  }

  //----------------------------------------
  // property: skuPickerPropertyManager
  //----------------------------------------
  private SkuPickerPropertyManager mSkuPickerPropertyManager;

  /**
   * @param pPropertyManager
   *   Sets the mSkuPickerPropertyManager.  This component will contain the commonly used
   *   properties related to the SKU picker components.
   */
  public void setSkuPickerPropertyManager(SkuPickerPropertyManager pPropertyManager) {
    mSkuPickerPropertyManager = pPropertyManager;
  }

  /**
   * @return mSkuPickerPropertyManager
   *   Returns the mSkuPickerPropertyManager component that contains the commonly used SKU
   *   picker properties.
   */
  public SkuPickerPropertyManager getSkuPickerPropertyManager() {
    return mSkuPickerPropertyManager;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * Extends the process method in {@link ProductDetailsHandler}.  Using the product information
   * returned the SKU pickers/selectors for the current product are built and added to the
   * content item.  Once the pickers have been build the product information is removed from the
   * content item as it is redundant.
   *
   * @param pCartridgeConfig
   *   The record details configuration for this request.
   */
  @Override
  public RecordDetails process(RecordDetailsConfig pCartridgeConfig)
    throws CartridgeHandlerException {

    RecordDetails contentItem = super.process(pCartridgeConfig);

    // Get the product properties from the contentItem.
    Map<String, Object> productProperties =
      (Map<String, Object>) contentItem.get(PRODUCT);

    List skuPickersList = new ArrayList<>();

    if (productProperties != null && !productProperties.isEmpty()) {

      StoreCatalogProperties catalogProperties =
        (StoreCatalogProperties) getCatalogTools().getCatalogProperties();
      String skuType =
        (String) productProperties.get(catalogProperties.getChildSkuTypePropertyName());
      List<String> pickerProperties =
        (List<String>) productProperties.get(catalogProperties.getChildSkuPickerPropertiesPropertyName());
      Map<String, Map<String, Object>> skuPickers =
        getSkuPickers((String) productProperties.get(REPOSITORY_ID), skuType, pickerProperties);

      skuPickersList.addAll(skuPickers.values());
    }

    // Add the SKU picker properties to the ContentItem.
    contentItem.put(getSkuPickerPropertyManager().getSkuPickersPropertyName(), skuPickersList);

    // Remove the product entry from the contentItem as it is no longer required.
    contentItem.remove(PRODUCT);
    contentItem.remove(SELECTED_SKU);

    return contentItem;
  }

  /**
   * Using the product and SKU properties for the current product build up the required pickers that
   * will allow a user to refine their selection to a single SKU.
   *
   * @param pProductId
   *   A {@code String} that contains the products Id.
   * @param pSkuType
   *   A {@code Map} of the properties that belong to a SKU from the current product.
   * @param pSkuPickerProperties
   *   A {@code List} of the properties that belong to a SKU that should be used to create a SKU
   *   selector.
   * @return
   *   A {@code Map} of maps that represents the structure of a collection of SKU pickers for
   *   the current product.
   */
  protected Map<String, Map<String, Object>> getSkuPickers(String pProductId, String pSkuType,
                                                           List<String> pSkuPickerProperties) {

    Map<String, Map<String, Object>> skuPickers = new TreeMap<>();

    if (pSkuType != null) {

      // Get the list of properties that should be used as pickers for the current SKU type.
      List<String> skuTypeProperties = (List<String>) getSkuTypes().get(pSkuType);

      if (skuTypeProperties != null) {

        for (String skuTypeProperty : skuTypeProperties) {

          // If the SKU type property is present in the current SKUs properties list then it is a
          // property that is used for a picker.
          if (pSkuPickerProperties.contains(skuTypeProperty)) {
            buildSkuPicker(skuTypeProperty, pProductId, skuPickers);
          }
        }
      }
    }
    return skuPickers;
  }

  /**
   * Determines the type of SKU that the product owns and then using this information gets the
   * correct {@code ProductDetailsPicker}  from the service map so the correct properties can
   * be built for the picker.
   *
   * @param pSkuProperty
   *   The current property for the SKU type of the products child SKUs.
   * @param pProductId
   *   A {@code String}  that contains the products Id.
   * @param pSkuPickers
   *   A {@code Map}  of maps that represents the structure of a collection of SKU pickers for
   *   the current product.
   */
  protected void buildSkuPicker(String pSkuProperty, String pProductId,
                                    Map<String, Map<String, Object>> pSkuPickers) {

    // Depending on the type of SKU that the product has get the correct SkuPicker from
    // the service map in the product details picker component.
    SkuPicker skuPicker = (SkuPicker) getSkuPickers().getSkuPickerMap().get(pSkuProperty);

    // Build up the contents of the picker.
    Map<String, Object> pickerProperties =
      buildPickerProperties(skuPicker, pSkuProperty, pProductId);

    if (pickerProperties != null && !pickerProperties.isEmpty()) {
      pSkuPickers.put(pSkuProperty, pickerProperties);
    }
  }

  /**
   * Using the passed {@code SkuPicker}  and pProductProperties get the picker the picker with its
   * type, currently selected option, list of available properties and populate options and also any
   * configured helper info for the picker.
   *
   * @param pPicker
   *   The {@code SkuPicker} to be used to populate the picker with the correct
   *   picker properties.
   * @param pSkuProperty
   *   The current property for the SKU type of the products child SKUs.
   * @param pProductId
   *   A {@code String} that contains the products Id.
   * @return
   *   A {@code Map} representing the current picker
   */
  protected Map<String, Object> buildPickerProperties(SkuPicker pPicker, String pSkuProperty,
                                                      String pProductId) {

    Map<String, Object> picker = new HashMap<>();

    // Create the pOptions map to be passed to the {@code SkuPicker}.  All of the calls to the
    // {@code SkuPicker} require the ProductIdPropertyName to be present in the options map.
    Map<String, String> options =
      Collections.singletonMap(getSkuPickerPropertyManager().getProductIdPropertyName(), pProductId);

    try {
      List<String> allOptions = (List<String>) pPicker.getAllOptions(options);
      if (!allOptions.isEmpty()) {
        String selectedOption = allOptions.size() == 1 ? allOptions.get(0) : null;

        // Populate the type, available options and selected value properties of the picker.
        picker.put(getSkuPickerPropertyManager().getSelectedValuePropertyName(), selectedOption);
        picker.put(getSkuPickerPropertyManager().getOptionsPropertyName(), allOptions);
        picker.put(getSkuPickerPropertyManager().getPickerTypePropertyName(), pSkuProperty);

        // Finally, add any configured picker helper info.
        Map<String, String> helperInfo = pPicker.getHelperInformation(pSkuProperty, options);

        if (helperInfo != null && !helperInfo.isEmpty()) {
          picker.put(getSkuPickerPropertyManager().getHelperInformation(), helperInfo);
        }
      }
    }
    catch (RepositoryException e) {
      AssemblerTools.getApplicationLogging().vlogError(e,
        "An error occurred while attempting to retrieve all options for the current [Product]");
    }
    return picker;
  }
}