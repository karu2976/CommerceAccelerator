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
import java.util.List;

import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.collections.validator.CollectionObjectValidator;

/**
 * Helper bean to store user catalog navigation information providing convenience methods for:
 *
 * <ul>
 *   <li>The current category that user is currently viewing.</li>
 *   <li>The ancestor categories from the current category to the top level category.</li>
 *   <li>The full category path from and including the current category to the top level category.</li>
 *   <li>The top level category.</li>
 *   <li>Whether a category is valid or not.</li>
 * </ul>
 *
 * The bean is intended to be used anywhere requiring access to the current shopper catalog navigation such as
 * breadcrumbs, continue shopping, and with targeters to specify targeting rules based on the currently viewed
 * category.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/catalog/CatalogNavigationService.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class CatalogNavigationService extends GenericService {

  /** Class version string. */
  public final static String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/catalog/CatalogNavigationService.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------

  //------------------------------------
  // property: topLevelCategoryId
  //------------------------------------
  private String mTopLevelCategoryId = "";

  /**
   * @param pTopLevelCategoryId
   *   The top level category for the category that user is currently viewing.
   */
  public void setTopLevelCategory( String pTopLevelCategoryId ) {
    mTopLevelCategoryId = pTopLevelCategoryId;
  }

  /**
   * @return
   *   The top level category for the category that user is currently viewing.
   */
  public String getTopLevelCategory() {
    return mTopLevelCategoryId;
  }

  //------------------------------------
  // property: currentCategoryId
  //------------------------------------
  private String mCurrentCategoryId = "";

  /**
   * @param pCurrentCategoryId
   *   The current category that shopper is currently viewing.
   */
  public void setCurrentCategory( String pCurrentCategoryId ) {
    mCurrentCategoryId = pCurrentCategoryId;
  }

  /**
   * @return
   *   The current category that user is currently viewing.
   */
  public String getCurrentCategory() {
    return mCurrentCategoryId;
  }

  //------------------------------------
  // property: ancestorCategoryIds
  //------------------------------------
  private List<String> mAncestorCategoryIds = new ArrayList<>();

  /**
   * @param pAncestorCategoryIds
   *   The ancestors of the category that user is currently viewing. The first category
   *   in the list is the top level category and the last category in the list is the
   *   parent category of the category that user is currently viewing.
   */
  public void setAncestorCategories( List<String> pAncestorCategoryIds ) {
    mAncestorCategoryIds = pAncestorCategoryIds;
  }

  /**
   * @return
   *   The ancestors of the category that user is currently viewing. The first category
   *   in the list is the top level category and the last category in the list is the
   *   parent category of the category that user is currently viewing.
   */
  public List<String> getAncestorCategories() {
    return mAncestorCategoryIds;
  }

  //------------------------------------
  // property: categoryNavigationPath
  //------------------------------------
  private List<String> mCategoryNavigationPath = new ArrayList<>();

  /**
   * @param pCategoryNavigationPath
   *   The full category path from and including the current category to the top level category.
   */
  public void setCategoryNavigationPath( List<String> pCategoryNavigationPath ) {
    mCategoryNavigationPath = pCategoryNavigationPath;
  }

  /**
   * @return
   *   Returns full category path from and including the current category to the top level category.
   *   The first category in the list is the top level category and the last category in the list
   *   is the category that user is currently viewing.
   */
  public List<String> getCategoryNavigationPath() {
    return mCategoryNavigationPath;
  }

  //------------------------------------
  // property: currentCategoryValid
  //------------------------------------
  private boolean mCurrentCategoryValid = true;
  
  /**
   * @return
   *   True or false depending on whether the current category is valid or not.
   */
  public boolean isCurrentCategoryValid() {
    return mCurrentCategoryValid;
  }

  /**
   * @param pCurrentCategoryValid
   *   True or false depending on whether the current category is valid or not.
   */
  public void setCurrentCategoryValid(boolean pCurrentCategoryValid) {
    mCurrentCategoryValid = pCurrentCategoryValid;
  }

  //------------------------------------
  // property: catalogTools
  //------------------------------------
  private StoreCatalogTools mCatalogTools = null;
 
  /**
   * @return
   *   The CatalogTools component.
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  /**
   * @param pCatalogTools
   *   The CatalogTools component.
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  //------------------------------------
  // property: validators
  //------------------------------------
  private CollectionObjectValidator[] mValidators = null;

  /**
   * @return
   *   The validator components to be applied to this service.
   */
  public CollectionObjectValidator[] getValidators() {
    return mValidators;
  }

  /**
   * @param pValidators
   *   The validator components to be applied to this service.
   */
  public void setValidators(CollectionObjectValidator[] pValidators) {
    mValidators = pValidators;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * Tracks the shopper's catalog navigation by setting the current category being viewed, the
   * ancestor categories of the current category, the full category path from the top level
   * category to the current category, and the top level category.
   *
   * @param pCategoryId
   *   The current category's ID.
   * @param pAncestorCategoryIds
   *   The ancestors of the current category that user is currently viewing.
   */
  public void navigate(String pCategoryId, List<String> pAncestorCategoryIds) {

    List<String> categoryNavigationPath = new ArrayList<>((pAncestorCategoryIds == null)
      ? new ArrayList<String>() : new ArrayList<>(pAncestorCategoryIds));

    setCurrentCategoryValid(validateCategory(pCategoryId));
    categoryNavigationPath.add(pCategoryId);
    setCategoryNavigationPath(categoryNavigationPath);
                                                  
    setCurrentCategory( pCategoryId );
    
    if (pAncestorCategoryIds != null && !pAncestorCategoryIds.isEmpty()) {
      setTopLevelCategory(pAncestorCategoryIds.get(0));
      setAncestorCategories(pAncestorCategoryIds);
    }
    else {
      setTopLevelCategory(pCategoryId);
      setAncestorCategories(new ArrayList<String>());
    }
  }
  
  /**
   * This method retrieves category and applies configured validators to it.
   *
   * @param pCategoryId
   *   The ID of category to validate.
   * @return
   *   True if category is valid, otherwise false.
   */
  public boolean validateCategory(String pCategoryId) {

    // There are no validators set so no filtering is needed.
    if (getValidators() == null || getValidators().length == 0) {
      return true;
    }
    
    Repository catalog = getCatalogTools().getCatalog();

    StoreCatalogProperties catalogProperties =
      (StoreCatalogProperties) getCatalogTools().getCatalogProperties();

    // Retrieve currentCategoryItem from Catalog.
    RepositoryItem currentCategoryItem = null;

    try {
      currentCategoryItem = catalog.getItem(pCategoryId, catalogProperties.getCategoryItemName());
    }
    catch (RepositoryException re) {
      if (isLoggingError()) {
        logError("There was a problem retrieving the category from the catalog", re);
      }
    }

    if (currentCategoryItem != null) {
      for (CollectionObjectValidator validator: getValidators()) {
        if (!validator.validateObject(currentCategoryItem)) {
          
          // Item doesn't pass validation. Just return false as there is no need to
          // check all other validators.
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Clears the current navigation settings.
   */
  public void clear(){
    setCategoryNavigationPath(new ArrayList());
    setCurrentCategory("");
    setTopLevelCategory("");
    setAncestorCategories(new ArrayList());
  }
}
