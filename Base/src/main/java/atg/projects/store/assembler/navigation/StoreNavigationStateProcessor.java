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

package atg.projects.store.assembler.navigation;

import java.util.List;

import atg.commerce.endeca.cache.DimensionValueCacheObject;
import atg.commerce.endeca.cache.DimensionValueCacheTools;
import atg.core.util.StringUtils;
import atg.endeca.assembler.AssemblerTools;
import atg.endeca.assembler.navigation.NavigationStateProcessor;
import atg.multisite.SiteContextException;
import atg.projects.store.catalog.CatalogNavigationService;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.repository.RepositoryException;

import com.endeca.infront.navigation.NavigationState;

/**
 * This Processor processes navigation state to determine if it represents a user catalog
 * navigation and updates the details on the catalog navigation tracking component.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/assembler/navigation/StoreNavigationStateProcessor.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreNavigationStateProcessor implements NavigationStateProcessor {

  /** Class version string. */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/assembler/navigation/StoreNavigationStateProcessor.java#1 $$Change: 1385662 $";
  
  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------

  //------------------------------------
  // property: dimensionValueCacheTools
  //------------------------------------
  private DimensionValueCacheTools mDimensionValueCacheTools = null;

  /**
   * @param pDimensionValueCacheTools
   *   The utility class for access to the ATG<->Endeca catalog cache.
   */
  public void setDimensionValueCacheTools(DimensionValueCacheTools pDimensionValueCacheTools) {
    mDimensionValueCacheTools = pDimensionValueCacheTools;
  }

  /**
   * @return
   *   The utility class for access to the ATG<->Endeca catalog cache.
   */
  public DimensionValueCacheTools getDimensionValueCacheTools() {
    return mDimensionValueCacheTools;
  }
 
  //------------------------------------
  // property: catalogNavigationService
  //------------------------------------
  private CatalogNavigationService mCatalogNavigationService = null;

  /**
   * @param pCatalogNavigationService
   *   The component used to track users catalog navigation.
   */
  public void setCatalogNavigation(CatalogNavigationService pCatalogNavigationService) {
    mCatalogNavigationService = pCatalogNavigationService;
  }

  /**
   * @return
   *   The component used to track users catalog navigation.
   */
  public CatalogNavigationService getCatalogNavigation() {
    return mCatalogNavigationService;
  }
  
  //------------------------------------
  // property: ignoredRangeFilters
  //------------------------------------
  protected List<String> mIgnoredRangeFilters = null;
  
  /**
   * @param pIgnoredRangeFilters
   *   The range filters to ignore when determining whether to add the
   *   CatalogNavigation user segment or not.
   */
  public void setIgnoredRangeFilters(List<String> pIgnoredRangeFilters) {
    mIgnoredRangeFilters = pIgnoredRangeFilters;
  }
  
  /**
   * @return
   *   The range filters to ignore when determining whether to add the
   *   CatalogNavigation user segment or not.
   */
  public List<String> getIgnoredRangeFilters() {
    return mIgnoredRangeFilters;
  }
  
  //------------------------------------
  // property: catalogTools
  //------------------------------------
  protected StoreCatalogTools mCatalogTools = null;

  /**
   * @param pCatalogTools
   *   The CatalogTools object to use when looking up products, categories and SKUs.
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
   * @return
   *   The CatalogTools object to use when looking up products, categories and SKUs.
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }
  
  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * Process the navigation state to determine if it represents a user catalog
   * navigation and update the details on the catalog navigation tracking
   * component.
   * 
   * @inheritDoc
   */
  @Override
  public NavigationState process(NavigationState pNavigationState) {

    String navigationFilterId = "";
    List<String> navigationFilters = pNavigationState.getFilterState().getNavigationFilters();

    // Obtain the navigation dimension filter (dimension value ID). The
    // catalog navigation will always contain a single dimension filter.
    if (navigationFilters.size() == 1) {
      navigationFilterId = navigationFilters.get(0);
    }

    // Access the catalog cache to find if we have a matching category entry for
    // this navigation filter (dimension value ID). If we find a match and we
    // have no search refinements, retrieve the cached object.
    if (!StringUtils.isEmpty(navigationFilterId)) {
      DimensionValueCacheObject cacheObject =
        getDimensionValueCacheTools().getCachedObjectForDimval(navigationFilterId);
      
      // Only update the catalog navigation component when the category is from the same site.
      try {
        if (cacheObject != null && getCatalogTools().isCategoryOnCurrentSite(cacheObject.getRepositoryId())) {
        
          // Update the last browsed category.
          updateCatalogNavigation(
            cacheObject.getRepositoryId(), cacheObject.getAncestorRepositoryIds());
        }
      }
      catch (RepositoryException | SiteContextException e) {
        AssemblerTools.getApplicationLogging().vlogError(e,
          "An error occurred when retrieving category for Id: {1}",
          cacheObject.getRepositoryId());
      }
    }

    return pNavigationState;
  }

  /**
   * Update the catalog navigation component.
   * 
   * @param pCategoryId
   *   The categoryId to set as the last browsed category.
   * @param pAncestors
   *   The ancestor category IDs.
   */
  protected void updateCatalogNavigation(String pCategoryId, List<String> pAncestors) {
    if (getCatalogNavigation() != null) {
      if (pCategoryId != null) {
        getCatalogNavigation().navigate(pCategoryId, pAncestors);
      }
      else {
        getCatalogNavigation().clear();
      }
    }
  }
}