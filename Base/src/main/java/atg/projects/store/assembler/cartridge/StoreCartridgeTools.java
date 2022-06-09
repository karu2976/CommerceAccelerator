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

package atg.projects.store.assembler.cartridge;

import java.util.*;

import atg.core.util.StringUtils;
import atg.endeca.assembler.navigation.filter.RangeFilterBuilder;
import atg.projects.store.catalog.CatalogNavigationService;
import com.endeca.infront.navigation.model.FilterState;
import com.endeca.infront.navigation.model.RangeFilter;

import com.endeca.infront.navigation.model.SubRecordsPerAggregateRecord;
import com.endeca.infront.navigation.request.RecordsMdexQuery;
import com.endeca.navigation.AggrERec;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ERec;

/**
 * This is the helper class containing reusable methods for obtaining/modifying a user's 
 * navigation information.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/assembler/cartridge/StoreCartridgeTools.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreCartridgeTools {
  
  /** Class version string. */
  protected static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/assembler/cartridge/StoreCartridgeTools.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------

  //----------------------------------------
  //  property: catalogNavigation
  //----------------------------------------
  private CatalogNavigationService mCatalogNavigation = null;

  /**
   * @param pCatalogNavigation
   *   The catalog navigation service component.
   */
  public void setCatalogNavigation(CatalogNavigationService pCatalogNavigation) {
    mCatalogNavigation = pCatalogNavigation;
  }

  /**
   * @return
   *   The catalog navigation service component.
   */
  public CatalogNavigationService getCatalogNavigation() {
    return mCatalogNavigation;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------
  
  /**
   * Return a List of unique RangeFilters by the property they filter on.
   * 
   * @param pRangeFilters
   *   The List of RangeFilters to be updated.
   * @param pRangeFilterBuilders
   *   The RangeFilterBuilders that will generate RangeFilters to be added to pRangeFilters.
   * 
   * @return 
   *   a new RangeFilter list consisting of pRangeFilters and the RangeFilters 
   *   generated by pRangeFilterBuilders. 
   */
  public List<RangeFilter> updateRangeFilters(List<RangeFilter> pRangeFilters, 
      RangeFilterBuilder[] pRangeFilterBuilders) {
    
    List<RangeFilter> allRangeFilters = pRangeFilters;
    
    if (pRangeFilters == null) {
      allRangeFilters = new ArrayList<>();
    }
    
    if (pRangeFilterBuilders == null) {
      return pRangeFilters;
    }
    
    // Generate the RangeFilters.
    List<RangeFilter> generatedRangeFilters = new ArrayList<>();
    
    for (RangeFilterBuilder builder : pRangeFilterBuilders) {
      generatedRangeFilters.addAll(builder.buildRangeFilters());
    }
      
    // Ensure that no generated RangeFilters are added as duplicates.
    for (RangeFilter generatedRangeFilter : generatedRangeFilters) {
      boolean addToList = true;

      for (RangeFilter rf1 : allRangeFilters) {
        if (rf1.getPropertyName().equals(generatedRangeFilter.getPropertyName())) {
          addToList = false;
          break;
        }
      }
        
      if (addToList) {
        allRangeFilters.add(generatedRangeFilter);
      }
    }
    
    return allRangeFilters;
  }

  /**
   * If the current navigation path is to a category, validate whether it is valid or not.
   *
   * @return
   *   True if the current category is valid or path is not a category, otherwise false.
   */
  public boolean validateCategoryNavigation() {
    if (!StringUtils.isEmpty(getCatalogNavigation().getCurrentCategory())
        && !getCatalogNavigation().isCurrentCategoryValid()) {
      return false;
    }
    return true;
  }

  /**
   * This method populates the passed in RecordsMdexQuery and FilterState with the configuration
   * required to execute an MDEX request that will retrieve the record specs that correspond
   * to the passed in list of item IDs.
   *
   * @param pQuery
   *   The query object that will be used as a parameter in an MDEX request.
   * @param pFilterState
   *   The filter state that will be updated with a new item ID record filter.
   * @param pItemRecordRepositoryIdName
   *   The item record repository ID name that will be used for building a record filter string.
   */
  public void populateRecordSpecMdexRequestParams(RecordsMdexQuery pQuery, FilterState pFilterState,
      List<String> pItemIds, String pItemRecordRepositoryIdName) {

    // Configure the query.
    pQuery.setSubRecordsPerAggregateRecord(SubRecordsPerAggregateRecord.ALL);
    pQuery.setRecordsPerPage(pItemIds.size());

    if (pFilterState.getRecordFilters() == null) {
      pFilterState.setRecordFilters(new ArrayList<String>());
    }

    // Populate the new record filter that will be used in the MDEX request to retrieve
    // the records corresponding to the product IDs.
    pFilterState.getRecordFilters().add(
      buildRecordFilterString(pItemRecordRepositoryIdName, pItemIds));
  }

  /**
   * <p>
   *   This method iterates through a list of items, extracting the repository IDs and
   *   builds a record filter string in the format:
   * </p>
   *
   * <code>
   *   OR(product.repositoryId:prod12345,OR(product.repositoryId:prod23456))
   * </code>
   *
   * @param pPropertyName
   *   The property name to query against.
   * @param pPropertyValues
   *   Records who have a pPropertyName with any of these pPropertyValues
   *   will be returned in the query.
   * @return
   *   A fully constructed record filter string.
   */
  public String buildRecordFilterString(String pPropertyName, List<String> pPropertyValues) {
    StringBuilder sb = new StringBuilder();

    Iterator itr = pPropertyValues.iterator();

    while (itr.hasNext()) {
      sb.append("OR(");
      sb.append(pPropertyName);
      sb.append(':');
      sb.append(itr.next());

      if (itr.hasNext()) {
        sb.append(',');
      }
    }

    // The OR query format needs to be nested, so we close off the individual queries here.
    for (int i = 0; i < pPropertyValues.size(); i++) {
      sb.append(')');
    }

    return sb.toString();
  }

  /**
   * Using the passed in results, build a map containing item IDs as keys and
   * corresponding record specs as values.
   *
   * @param pResults
   *   The results object that that will be used to populate the map.
   * @param pItemRecordRepositoryIdName
   *   The item record repository ID name, e.g. product.repositoryId.
   * @return
   *   A populated map of item IDs and corresponding record specs, or null if the
   *   passed in results object doesn't contain any aggregated records.
   */
  public Map getItemIdToRecordSpecMap(ENEQueryResults pResults, String pItemRecordRepositoryIdName) {
    List records = pResults.getNavigation().getAggrERecs();
    Map itemIdsToRecordSpecs = null;

    if (records != null) {
      itemIdsToRecordSpecs = new HashMap(records.size());

      for (AggrERec record : (Iterable<AggrERec>) records) {
        ERec representative = record.getRepresentative();
        itemIdsToRecordSpecs.put(
          representative.getProperties().get(pItemRecordRepositoryIdName), representative.getSpec());
      }
    }

    return itemIdsToRecordSpecs;
  }

}
