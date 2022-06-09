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

import atg.core.util.StringUtils;
import atg.projects.store.assembler.cartridge.StoreCartridgeTools;
import atg.search.record.alias.AttributeAliasManager;
import atg.servlet.ServletUtil;

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.Breadcrumbs;
import com.endeca.infront.cartridge.BreadcrumbsConfig;
import com.endeca.infront.cartridge.BreadcrumbsHandler;
import com.endeca.infront.cartridge.model.Ancestor;
import com.endeca.infront.cartridge.model.RefinementBreadcrumb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BreadcrumbsHandler extension that overrides preprocess method to use the
 * CategoryToDimensionAttributeAliasManager to get the source name for attribute
 * alias. Process method is overridden to update unlocalized category names to
 * localized ones.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/assembler/cartridge/handler/StoreBreadcrumbsHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreBreadcrumbsHandler extends BreadcrumbsHandler {

  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/assembler/cartridge/handler/StoreBreadcrumbsHandler.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------

  /** The name of the category dimension. */
  public static final String CATEGORY_DIMENSION_NAME = "product.category";

  /** The name of the display name property that will be added to the content item. */
  public static final String DISPLAY_NAME_PROPERTY = "displayNameProperty";

  /** The name of the display name property alias that belongs to the content item. */
  public static final String DISPLAY_NAME_PROPERTY_ALIAS = "displayNamePropertyAlias";

  /**
   * The property that will be used in the content item that defines whether the
   * navigation path is valid or not
   */
  public static final String VALID_NAVIGATION_PATH_PROPERTY_NAME = "validNavigationPath";

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------

  //------------------------------------
  // property: localeAttributeAliasManager
  //------------------------------------
  private AttributeAliasManager mLocaleAttributeAliasManager = null;
  
  /**
   * @return
   *   The manager component used to determine the source property for the display
   *   name aliased property.
   */
  public AttributeAliasManager getLocaleAttributeAliasManager() {
    return mLocaleAttributeAliasManager;
  }

  /**
   * @param pLocaleAttributeAliasManager
   *   The manager component used to determine the source property for the display
   *   name aliased property.
   */
  public void setLocaleAttributeAliasManager(AttributeAliasManager pLocaleAttributeAliasManager) {
    mLocaleAttributeAliasManager = pLocaleAttributeAliasManager;
  }

  //----------------------------------------
  //  property: storeCartridgeTools
  //----------------------------------------
  private StoreCartridgeTools mStoreCartridgeTools = null;

  /**
   * @param pStoreCartridgeTools
   *   The store cartridge tools component.
   */
  public void setStoreCartridgeTools(StoreCartridgeTools pStoreCartridgeTools) {
    mStoreCartridgeTools = pStoreCartridgeTools;
  }

  /**
   *  @return
   *    The store cartridge tools component.
   */
  public StoreCartridgeTools getStoreCartridgeTools() {
    return mStoreCartridgeTools;
  }

  //----------------------------------------
  //  property: skuActivePriceDimensionName
  //----------------------------------------
  private String mSkuActivePriceDimensionName = "sku.activePrice";

  /**
   * @param pSkuActivePriceDimensionName
   *   The name of the dimension that contains a SKU's active price. Defaults to sku.activePrice.
   */
  public void setSkuActivePriceDimensionName(String pSkuActivePriceDimensionName) {
    mSkuActivePriceDimensionName = pSkuActivePriceDimensionName;
  }

  /**
   *  @return pSkuActivePriceDimensionName
   *    The name of the dimension that contains a SKU's active price. Defaults to sku.activePrice.
   */
  public String getSkuActivePriceDimensionName() {
    return mSkuActivePriceDimensionName;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * Create a new BreadcrumbsConfig using the passed in ContentItem.
   * 
   * @param pContentItem
   *   The cartridge content item to be wrapped.
   * @return
   *   A new Breadcrumbs configuration.
   */
  @Override
  protected BreadcrumbsConfig wrapConfig(ContentItem pContentItem) {
    return new BreadcrumbsConfig(pContentItem);
  }

  /**
   * Determine the source property name for aliased display name property before
   * calling the super.preprocess method.
   * 
   * @param pCartridgeConfig
   *   The Breadcrumbs cartridge configuration.
   *
   * @throws CartridgeHandlerException
   *   If an error occurs that is scoped to an individual cartridge instance. This exception
   *   will not halt the entire assembly process, which occurs across multiple cartridges;
   *   instead, this exception will be packaged in the overall response model. If an unchecked
   *   exception is thrown, then the entire assembly process will be halted.
   */
  @Override
  public void preprocess(BreadcrumbsConfig pCartridgeConfig) throws CartridgeHandlerException {
    
    // If we have an alias, try to find the source property.
    String displayNameAlias = (String) pCartridgeConfig.get(DISPLAY_NAME_PROPERTY_ALIAS);
    
    if (displayNameAlias != null) {
      String sourceName = getLocaleAttributeAliasManager().getSourceNameForAttributeAlias(
        ServletUtil.getCurrentRequest(), displayNameAlias);
    
      if (sourceName != null) {
        pCartridgeConfig.put(DISPLAY_NAME_PROPERTY, sourceName);
      }
      else {
        pCartridgeConfig.put(DISPLAY_NAME_PROPERTY, displayNameAlias);
      }
    }
       
    super.preprocess(pCartridgeConfig); 
  }

  /**
   * Creates a new Breadcrumbs cartridge model with localized category labels and
   * group refinement crumbs by dimension name, preserving the order in that the
   * dimension names appears. 
   * 
   * Adds a property that specifies when the current navigation path is a category 
   * page, whether it is valid or not.
   * 
   * @param pCartridgeConfig
   *   The cartridge configuration for the Breadcrumbs.
   * 
   * @throws CartridgeHandlerException
   *   If an error occurs that is scoped to an individual cartridge instance. This
   *   exception will not halt the entire assembly process, which occurs across multiple
   *   cartridges; instead, this exception will be packaged in the overall response model.
   *   If an unchecked  exception is thrown, then the entire assembly process will be halted.
   */
  @Override
  public Breadcrumbs process(BreadcrumbsConfig pCartridgeConfig) throws CartridgeHandlerException {

    Breadcrumbs breadcrumbs = super.process(pCartridgeConfig);

    if (breadcrumbs != null) {
      List<RefinementBreadcrumb> refinementsCrumbs = breadcrumbs.getRefinementCrumbs();

      if (!refinementsCrumbs.isEmpty()) {
        int refinementsSize = refinementsCrumbs.size();
        
        Map<String, ArrayList<RefinementBreadcrumb>> localizedAndGroupedRefinementsCrumbs =
          new HashMap<>(refinementsSize);
        ArrayList<String> dimensionsOrder = new ArrayList<>(refinementsSize);

        for (RefinementBreadcrumb refinementBreadcrumb: refinementsCrumbs) {

          // Get dimension name of the refinement crumb.
          String refinementCrumbDimensionName = refinementBreadcrumb.getDimensionName();

          // If it is a category refinement, use localized names.
          if (CATEGORY_DIMENSION_NAME.equals(refinementCrumbDimensionName)) {
            List<Ancestor> ancestorsCrumbs = refinementBreadcrumb.getAncestors();

            if (!ancestorsCrumbs.isEmpty()) {
              List<Ancestor> localizedAncestorsCrumbs = new ArrayList<>(ancestorsCrumbs.size());

              for (Ancestor ancestor: ancestorsCrumbs) {

                // Look for localized label.
                String currentLabel = ancestor.getLabel();
                String localizedLabel = findLocalizedLabel(ancestor.getProperties(), currentLabel,
                  (String) pCartridgeConfig.get(DISPLAY_NAME_PROPERTY));
                ancestor.setLabel(localizedLabel);
                localizedAncestorsCrumbs.add(ancestor);
              }

              refinementBreadcrumb.setAncestors(localizedAncestorsCrumbs);
            }

            // Look for localized label.
            String currentLabel = refinementBreadcrumb.getLabel();
            String localizedLabel = findLocalizedLabel(refinementBreadcrumb.getProperties(),
              currentLabel, (String) pCartridgeConfig.get(DISPLAY_NAME_PROPERTY));

            refinementBreadcrumb.setLabel(localizedLabel);
          }

          // Add refinementBreadcrumb to the localizedAndGroupedRefinementsCrumbs refinement map.
          if (localizedAndGroupedRefinementsCrumbs.containsKey(refinementCrumbDimensionName)) {
            ArrayList<RefinementBreadcrumb> dimensionCrumbs =
              localizedAndGroupedRefinementsCrumbs.get(refinementCrumbDimensionName);
            dimensionCrumbs.add(refinementBreadcrumb);
          }
          else {
            ArrayList<RefinementBreadcrumb> dimensionCrumbs = new ArrayList<>(refinementsSize);
            dimensionCrumbs.add(refinementBreadcrumb);
            localizedAndGroupedRefinementsCrumbs.put(refinementCrumbDimensionName, dimensionCrumbs);

            // Save the order in that breadcrumbs dimension names occur.
            dimensionsOrder.add(refinementBreadcrumb.getDimensionName());
          }
        }

        ArrayList<RefinementBreadcrumb> resultRefinementsCrumbs =
          new ArrayList<>(refinementsCrumbs.size());

        // Retrieve grouped breadcrumbs from the localized and grouped refinement map in the order.
        for (String dimensionName: dimensionsOrder) {
          ArrayList<RefinementBreadcrumb> dimensionCrumbs =
            localizedAndGroupedRefinementsCrumbs.get(dimensionName);
          resultRefinementsCrumbs.addAll(dimensionCrumbs);
        }

        breadcrumbs.setRefinementCrumbs(resultRefinementsCrumbs);
      }

      breadcrumbs.put(VALID_NAVIGATION_PATH_PROPERTY_NAME,
        getStoreCartridgeTools().validateCategoryNavigation());
    }

    return breadcrumbs;
  }

  /**
   * Looks for localized label for category refinements.
   *
   * @param pProperties
   *   The properties of current refinement.
   * @param pLabel
   *   The non-localized label of refinement.
   * @param pDisplayNameProperty
   *   The localized display name property.
   *
   * @return
   *   A localized label.
   */
  public String findLocalizedLabel(Map<String, String> pProperties, String pLabel,
      String pDisplayNameProperty) {

    String localizedLabel = pLabel;
    String localizedName = pProperties.get(pDisplayNameProperty);

    if (!StringUtils.isEmpty(localizedName)) {
      localizedLabel = localizedName;
    }

    return localizedLabel;
  }

}
