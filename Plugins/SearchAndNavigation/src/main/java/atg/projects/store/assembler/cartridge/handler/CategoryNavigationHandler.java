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

import atg.commerce.endeca.cache.DimensionValueCacheObject;
import atg.commerce.endeca.cache.DimensionValueCacheTools;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryException;
import atg.commerce.catalog.custom.CatalogProperties;
import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.endeca.assembler.AssemblerTools;
import atg.multisite.Site;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteManager;
import atg.service.collections.validator.StartEndDateValidator;

import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.CartridgeHandler;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

/**
 * Category navigation menu handler.
 * 
 * This handler will build a list of top-level categories and sub-categories that will be returned
 * in the content item.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/assembler/cartridge/handler/CategoryNavigationHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class CategoryNavigationHandler extends NavigationCartridgeHandler<ContentItem, ContentItem> {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/assembler/cartridge/handler/CategoryNavigationHandler.java#1 $$Change: 1385662 $";


  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------
  
  /** The name associated with the full list of categories returned in the content item. */
  public static final String CATEGORIES = "categories";
  
  /** The name associated with any top-level (root) category items returned in the content item. */
  public static final String TOP_LEVEL_CATEGORY = "topLevelCategory";
  
  /** The name associated with any sub-category items returned in the content item. */
  public static final String SUB_CATEGORIES = "subCategories";
  
  /** The name associated with each category/sub-category display name. */
  public static final String DISPLAY_NAME = "displayName";
  
  /** The name associated with each category/sub-category navigation state. */
  public static final String NAVIGATION_STATE = "navigationState";
  
  /** The name associated with each category/sub-category repository Id. */
  public static final String CATEGORY_ID = "categoryId";
  
  /** The request parameter name to hold unindexed category repository Id. */
  public static final String UNINDEXED_CATEGORY_ID_PARAM = "browse?categoryId=";
  
  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------
  
  //------------------------------------
  // property: catalogTools
  //------------------------------------
  private CustomCatalogTools mCatalogTools = null;
  
  /** 
   * @param pCatalogTools
   *   A catalog tools component for performing common catalog operations.
   */
  public void setCatalogTools(CustomCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }
  
  /**
   * @return
   *   A catalog tools component for performing common catalog operations.
   */
  public CustomCatalogTools getCatalogTools() {
    return mCatalogTools;
  }
  
  //------------------------------------
  // property: rootNavigationCategoryPropertyName
  //------------------------------------
  private String mRootNavigationCategoryPropertyName = "rootNavigationCategory";
  
  /**
   * @param pRootNavigationCategoryPropertyName
   *   The name of the "rootNavigationCategory" property in the catalog repository item.
   */
  public void setRootNavigationCategoryPropertyName(String pRootNavigationCategoryPropertyName) {
    mRootNavigationCategoryPropertyName = pRootNavigationCategoryPropertyName;
  }

  /**
   * @return 
   *   The name of the "rootNavigationCategory" property in the catalog repository item.
   */
  public String getRootNavigationCategoryPropertyName() {
    return mRootNavigationCategoryPropertyName;
  }

  //------------------------------------
  //  property: startEndDateValidator
  //------------------------------------
  private StartEndDateValidator mStartEndDateValidator = null;

  /**
   * @param pStartEndDateValidator
   *   The start/end date validator component.
   */
  public void setStartEndDateValidator(StartEndDateValidator pStartEndDateValidator) {
    mStartEndDateValidator = pStartEndDateValidator;
  }

  /**
   * @return
   *   The start/end date validator component.
   */
  public StartEndDateValidator getStartEndDateValidator() {
    return mStartEndDateValidator;
  }

  //------------------------------------
  //  property: useUnindexedCategory
  //------------------------------------
  private boolean mUseUnindexedCategory = false;

  /**
   * @return
   *   Whether or not to use the unindexed category.
   */
  public boolean isUseUnindexedCategory() {
    return mUseUnindexedCategory;
  }

  /**
   * @param pUseUnindexedCategory
   *   Whether or not to use the unindexed category.
   */
  public void setUseUnindexedCategory(boolean pUseUnindexedCategory) {
    mUseUnindexedCategory = pUseUnindexedCategory;
  }

  //------------------------------------
  // property: dimensionValueCacheTools
  //------------------------------------
  private DimensionValueCacheTools mDimensionValueCacheTools = null;

  /**
   * @param pDimensionValueCacheTools
   *   The utility class for access to the ATG<-->Endeca catalog cache.
   */
  public void setDimensionValueCacheTools(DimensionValueCacheTools pDimensionValueCacheTools) {
    mDimensionValueCacheTools = pDimensionValueCacheTools;
  }

  /**
   * @return
   *   The utility class for access to the ATG<-->Endeca catalog cache.
   */
  public DimensionValueCacheTools getDimensionValueCacheTools() {
    return mDimensionValueCacheTools;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * Create a new BasicContentItem using the passed in ContentItem. The map defined in
   * the properties file will be put into the BasicContentItem.
   *
   * @param pContentItem
   *   The cartridge content item to be wrapped.
   * @return
   *   A new TargetedItems configuration.
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    BasicContentItem contentItem = new BasicContentItem(pContentItem);
    return contentItem;
  }

  /**
   * This method will invoke the 'getCategories' method in order to retrieve a list of
   * category/sub-categories to be returned in the content item.
   * 
   * @param pCartridgeConfig
   *   The cartridge configuration returned by {@link CartridgeHandler#initialize(ContentItem)}.
   *   Note that this instance may have been modified by the preprocess(ContentItem) method.
   *   
   * @return
   *   A content item containing the list of categories and corresponding sub-categories.
   *   
   * @throws CartridgeHandlerException
   */
  public ContentItem process(ContentItem pCartridgeConfig)
      throws CartridgeHandlerException {

    BasicContentItem contentItem = new BasicContentItem(pCartridgeConfig);
    contentItem.put(CATEGORIES, getCategories());

    return contentItem;
  }
  
  /**
   * Get the list of top-level categories and their corresponding lists of sub-categories
   * from the repository. This method will arrange the categories in the same order as defined
   * in the BCC. Each category will also be mapped with the corresponding navigation states.

   * @return
   *   A list of sorted top-level categories and their lists of corresponding 1st level
   *   sub-categories.
   */
  public List<Map<String,Object>> getCategories() {

    // List of categories/sub-categories to be returned to the invoking method.
    List<Map<String,Object>> categories = new ArrayList<>();

    CatalogProperties catalogProperties = getCatalogTools().getCatalogProperties();

    RepositoryItem catalog = null;

    try {
      Site currentSite = SiteContextManager.getCurrentSiteContext().getSite();

      if (currentSite != null) {
        RepositoryItem site = SiteManager.getSiteManager().getSite(currentSite.getRepositoryId());
        catalog = getCatalogTools().getCatalogForSite(site);
      }
      else {
        catalog = getCatalogTools().getDefaultCatalog();
      }
    }
    catch (RepositoryException re) {
      AssemblerTools.getApplicationLogging().logError(
        "There was an error retrieving a current site or site catalog.", re);
    }

    if (catalog != null) {
      RepositoryItem rootNavigationCategory = (RepositoryItem)
        catalog.getPropertyValue(getRootNavigationCategoryPropertyName());

      if (rootNavigationCategory != null) {
        List<RepositoryItem> rootCategories =
          (List<RepositoryItem>) rootNavigationCategory.getPropertyValue(
            catalogProperties.getAllChildCategoriesPropertyName());

        if (rootCategories != null && !rootCategories.isEmpty()) {

          for (RepositoryItem rootCategory : rootCategories) {
            if (getStartEndDateValidator() != null
              && !getStartEndDateValidator().validateObject(rootCategory)) {

              AssemblerTools.getApplicationLogging().vlogDebug(
                "{0} does not have a valid start or end date.", rootCategory.getRepositoryId());

              // If the top-level-category isn't valid, we don't want to add sub-categories.
              continue;
            }

            // The map that will hold the top-level-category and corresponding sub-categories. 
            // This will be appended to the list of 'categories' to be returned. 
            Map<String, Object> category = new HashMap<>(1);

            List<RepositoryItem> topLevelCategoryAncestors =
              (List<RepositoryItem>) rootCategory.getPropertyValue("ancestorCategories");

            List<String> topLevelCategoryAncestorRepositoryIds = null;

            if (topLevelCategoryAncestors != null && !topLevelCategoryAncestors.isEmpty()) {
              topLevelCategoryAncestorRepositoryIds =
                new ArrayList<>(topLevelCategoryAncestors.size());

              for (RepositoryItem item : topLevelCategoryAncestors) {
                topLevelCategoryAncestorRepositoryIds.add(item.getRepositoryId());
              }
            }

            DimensionValueCacheObject topLevelCategoryCacheObject =
              getDimensionValueCacheTools().get(rootCategory.getRepositoryId(),
                topLevelCategoryAncestorRepositoryIds);

            String topLevelCategoryNavigationState = null;

            if (topLevelCategoryCacheObject == null) {

              // This will indicate that the category probably hasn't been indexed.
              AssemblerTools.getApplicationLogging().vlogError(
                "{0} does not have an associated navigation state.",
                  rootCategory.getRepositoryId());

              if (isUseUnindexedCategory()) {

                // Use unindexed category url format to construct navigation state.
                topLevelCategoryNavigationState =
                  UNINDEXED_CATEGORY_ID_PARAM + rootCategory.getRepositoryId();
              }
              else {

                // The top-level-category isn't valid so we don't want to add any sub-categories.
                continue;
              }
            }
            else {
              String url = topLevelCategoryCacheObject.getUrl();

              // If the URL starts with a forward slash, remove it as this makes it
              // easier for the client to use the relative path.
              topLevelCategoryNavigationState =
                url.startsWith("/") ? url.substring(1, url.length()) : url;
            }

            Map<String, Object> topLevelCategory =
              createCategory(rootCategory.getItemDisplayName(), topLevelCategoryNavigationState,
                rootCategory.getRepositoryId());

            category.put(TOP_LEVEL_CATEGORY, topLevelCategory);

            // Retrieve the list of sub-categories belonging to the top-level-category.
            List<RepositoryItem> childCategories = (List<RepositoryItem>)
              rootCategory.getPropertyValue(catalogProperties.getAllChildCategoriesPropertyName());

            List<Map<String, Object>> subCategories = new ArrayList<>();

            if (childCategories != null) {
              for (RepositoryItem childCategory : childCategories) {
                if (getStartEndDateValidator() != null
                  && !getStartEndDateValidator().validateObject(childCategory)) {

                  AssemblerTools.getApplicationLogging().vlogDebug(
                    "{0} does not have a valid start or end date.",
                    childCategory.getRepositoryId());

                  // If the sub-category isn't valid, we don't want to add it
                  // to sub-categories list.
                  continue;
                }

                List<RepositoryItem> subCategoryAncestors =
                  (List<RepositoryItem>) childCategory.getPropertyValue("ancestorCategories");

                List<String> subCategoryAncestorRepositoryIds = null;

                if (subCategoryAncestors != null && !subCategoryAncestors.isEmpty()) {
                  subCategoryAncestorRepositoryIds = new ArrayList<>(subCategoryAncestors.size());

                  for (RepositoryItem item : subCategoryAncestors) {
                    subCategoryAncestorRepositoryIds.add(item.getRepositoryId());
                  }
                }

                DimensionValueCacheObject subCategoryCacheObject =
                  getDimensionValueCacheTools().get(childCategory.getRepositoryId(),
                    subCategoryAncestorRepositoryIds);

                String subCategoryNavigationState = null;

                if (subCategoryCacheObject == null) {

                  // This will indicate that the sub-category probably hasn't been indexed.
                  AssemblerTools.getApplicationLogging().vlogError(
                    "{0} does not have an associated navigation state.",
                    childCategory.getRepositoryId());

                  if (isUseUnindexedCategory()) {

                    // Use unindexed category url format to construct navigation state.
                    subCategoryNavigationState =
                      UNINDEXED_CATEGORY_ID_PARAM + childCategory.getRepositoryId();
                  } else {

                    // The sub-category isn't valid so we don't want to add it to
                    // the list of sub-categories.
                    continue;
                  }
                }
                else {
                  String url = subCategoryCacheObject.getUrl();

                  // If the URL starts with a forward slash, remove it as this makes it
                  // easier for the client to use the relative path.
                  subCategoryNavigationState =
                    url.startsWith("/") ? url.substring(1, url.length()) : url;
                }

                Map<String, Object> subCategory =
                  createCategory(childCategory.getItemDisplayName(), subCategoryNavigationState,
                    childCategory.getRepositoryId());

                subCategories.add(subCategory);
              }
            }

            category.put(SUB_CATEGORIES, subCategories);
            categories.add(category);
          }
        }
        else {
          AssemblerTools.getApplicationLogging().logDebug(
            "No top-level categories could be found to build the category navigation items.");
        }
      }
      else {
        AssemblerTools.getApplicationLogging().logDebug(
          "No rootNavigationCategory could be found to build the category navigation.");
      }
    }
    else {
      AssemblerTools.getApplicationLogging().logDebug(
        "No catalog could be found to build the category navigation.");
    }

    return categories;
  }

  /**
   * Create a category Map item using the passed in name and navigation state.
   * 
   * @param pName
   *   The displayName for the category.
   * @param pNavigationState
   *   The navigationState for the category.
   * @return
   *  The new category Map item.
   */
  public Map<String, Object> createCategory(String pName, String pNavigationState, String repositoryId) {
    Map<String, Object> category = new TreeMap<>();
    category.put(DISPLAY_NAME, pName);
    category.put(NAVIGATION_STATE, pNavigationState);
    category.put(CATEGORY_ID, repositoryId);
    return category;
  }

}
