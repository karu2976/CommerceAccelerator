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

package atg.projects.store.catalog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.core.util.StringUtils;
import atg.endeca.assembler.AssemblerTools;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteMembershipManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * The extensions to out of the box CatalogTools. At the time of its writing, all it contained were 
 * the ids of the Store Catalog and the corresponding repository items.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/catalog/StoreCatalogTools.java#1 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreCatalogTools extends CustomCatalogTools {

  /** Class version string. */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/catalog/StoreCatalogTools.java#1 $$Change: 1385662 $";
  
  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------
  
  /**
   * Returns the list of possible values for a given collection of SKUs.
   *
   * @param pSkus 
   *   A {@code Collection} of SKUs.
   * @param pPropertyName 
   *   The property name of the SKU to use.
   * @return 
   *   A {@code Collection} of values.
   */
  public List<String> getPossibleValuesForSkus(Collection<RepositoryItem> pSkus, 
      String pPropertyName) {
    
    List<String> values = new ArrayList<>();

    if (pSkus != null) {
      for (RepositoryItem sku : pSkus) {
        Object value = sku.getPropertyValue(pPropertyName);

        if (value instanceof String && !values.contains(value)
            && !StringUtils.isBlank((String) value)) {
          values.add((String) value);
        }
      }
    }

    return values;
  }

  /**
   * Returns a product's child SKUs.
   * 
   * @param pProduct
   *   {@code RepositoryItem} product.
   * @return 
   *   A {@code Collection} of SKUs.
   */
  public Collection<RepositoryItem> getProductChildSkus(RepositoryItem pProduct) {
    return (Collection<RepositoryItem>) 
      pProduct.getPropertyValue(getCatalogProperties().getChildSkusPropertyName());
  }

  /**
   * Sorts the collection of strings relative to their ordinal position in the sort order array.
   * 
   * @param pStrings 
   *   A {@code Collection} of strings to sort.
   * @param pSortOrder 
   *   The string array of possible values and their relative order.
   * @return 
   *   The {@code List} of sorted Strings.
   */
  public List<String> sortStrings(List<String> pStrings, String[] pSortOrder) {

    List<String> sortedStrings = new ArrayList<>(pStrings.size());
    int sortStringIndex = 0;

    for (int i = 0; (i < pSortOrder.length) && (sortStringIndex < pStrings.size()); i++) {
      
      if (pStrings.contains(pSortOrder[i])) {
        sortedStrings.add(sortStringIndex++, pSortOrder[i]);
        pStrings.remove(pSortOrder[i]);
      }
    }
    
    // Add any sizes that weren't in the sort order at the end.
    sortedStrings.addAll(pStrings);

    return sortedStrings;
  }
  
  /**
   * Retrieves category item using its repository ID and checks
   * whether the category belongs to current site. 
   * 
   * @param pRepositoryId
   *   The category repository id.
   * @return
   *   true if category is on current site, otherwise false.
   * @throws RepositoryException
   *   If an error occurs.
   * @throws SiteContextException 
   */
  public boolean isCategoryOnCurrentSite(String pRepositoryId) 
      throws RepositoryException, SiteContextException {
    boolean siteFound = false;

    // Get current category item.
    RepositoryItem category = getCategory(pRepositoryId);

    StoreCatalogProperties catalogProperties = (StoreCatalogProperties) getCatalogProperties();
    
    if (category != null) {

      // Get the current site.
      String currentSiteId = SiteContextManager.getCurrentSiteId();

      if (!StringUtils.isEmpty(currentSiteId)) {
        String sitesPropertyName = catalogProperties.getSitesPropertyName();

        if (!StringUtils.isEmpty(sitesPropertyName) &&
          category.getItemDescriptor().hasProperty(sitesPropertyName)) {

          Collection<String> sites = (Collection<String>) category.getPropertyValue(sitesPropertyName);
          sites = SiteMembershipManager.getSiteMembershipManager().getSiteIds(sites);

          for (String siteId: sites) {
            if (currentSiteId.equals(siteId)) {
              siteFound = true;
              break;
            }
          }
        } 
      }

      // If current site id is empty, then we not care on what site we are.
      else {
        siteFound = true;
      }      
    }
    return siteFound;
  }
  
  /**
   * Retrieve the category RepositoryItem by its ID.
   *
   * @param pCategoryId
   *   The category to retrieve.
   * @return
   *   Category repository item.
   * @throws RepositoryException
   *   If an error occurs.
   */
  
  public RepositoryItem getCategory(String pCategoryId) throws RepositoryException {
    
    // If no category ID is specified, return null.
    if (StringUtils.isEmpty(pCategoryId)) {
      return null;
    }
    
    // Look up category repository item for the specified category ID.
    RepositoryItem categoryItem = findCategory(pCategoryId);

    if (categoryItem == null) {
      AssemblerTools.getApplicationLogging().vlogError(
        "No category found for the id: {0}", pCategoryId);
      
      return null;
    }
  
    return categoryItem;
  }
}