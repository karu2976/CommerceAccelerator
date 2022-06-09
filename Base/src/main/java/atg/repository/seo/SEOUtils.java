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

package atg.repository.seo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * This is an utility class for creating seo meta data from product catalog for
 * category and product detail pages.Handlers will make use of this class to create
 * seo meta data and add it to the content item.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/repository/seo/SEOUtils.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class SEOUtils extends GenericService {
  
  /** Class version string */
  public static String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/repository/seo/SEOUtils.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC
  //----------------------------------------------------------------------------
  
  /**  Title property name of SEO meta data. */
  public static final String TITLE_PROPERTY_NAME = "title";
  
  /** Description property name of SEO meta data. */
  public static final String DESCRIPTION_PROPERTY_NAME = "description";
  
  /** Keywords property name of SEO meta data. */
  public static final String KEYWORDS_PROPERTY_NAME = "keywords";
  
  /** Catalog item display name property. */
  public static final String CATALOG_ITEM_DISPLAY_NAME_PROPERTY = "displayName";
  
  /** Catalog item longDescription name property. */
  public static final String CATALOG_ITEM_LONG_DESCRIPTION_PROPERTY = "longDescription";
  
  /** Catalog item keywords name property. */
  public static final String CATALOG_ITEM_KEYWORDS_PROPERTY = "keywords";
  
  /** SEO keywords separator. */
  public static final String KEYWORDS_SEPARATOR = ",";
  
  //----------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------- 
  
  //-----------------------------------
  //  property: categoryItemDescriptor
  //-----------------------------------
  private String mCategoryItemDescriptor = null;
  
  /**
   * @return
   *   Category item descriptor name.
   */
  public String getCategoryItemDescriptor() {
    return mCategoryItemDescriptor;
  }

  /**
   * @param pCategoryItemDescriptor
   *   Category item descriptor name.
   */
  public void setCategoryItemDescriptor(String pCategoryItemDescriptor) {
    mCategoryItemDescriptor = pCategoryItemDescriptor;
  }
  
  //-----------------------------------
  //  property: productItemDescriptor
  //-----------------------------------
  private String mProductItemDescriptor = null;
  
  /**
   * @return
   *   Product item descriptor name.
   */
  public String getProductItemDescriptor() {
    return mProductItemDescriptor;
  }

  /**
   * @param pProductItemDescriptor
   *   Product item descriptor name.
   */
  public void setProductItemDescriptor(String pProductItemDescriptor) {
    mProductItemDescriptor = pProductItemDescriptor;
  }

  //-----------------------------------
  //  property: productCatalog
  //-----------------------------------
  private Repository mProductCatalog = null;
  
  /**
   * @return
   *   Product catalog repository. 
   */
  public Repository getProductCatalog() {
    return mProductCatalog;
  }

  /**
   * @param pProductCatalog
   *   Product catalog repository. 
   */
  public void setProductCatalog(Repository pProductCatalog) {
    mProductCatalog = pProductCatalog;
  }

	//-----------------------------------
	//  property: storeName
	//-----------------------------------
	private String mStoreName = "";

	/**
	 * @param pStoreName
	 *   The name of the store that will be prepended to each meta title.
	 */
	public void setStoreName(String pStoreName) {
		mStoreName = pStoreName;
	}

	/**
	 * @return
	 *   The name of the store that will be prepended to each meta title.
	 */
	public String getStoreName() {
		return mStoreName;
	}

  //----------------------------------------------------------------------------
  //  METHODS
  //----------------------------------------------------------------------------
  
  /**
   * This method retrieves the display name, long description and keywords from 
   * category repository item and return it as map having seo title, description 
   * and keywords.
   * 
   * @param pCategoryId
   *   Category id of the current category page.
   * @return
   *   Returns map which contains seo meta data.
   */
  public Map<String,Object> getCategorySEOMetaData(String pCategoryId) {
    HashMap<String, Object> metaData = new HashMap<>();
    
    try {
      RepositoryItem category = getProductCatalog().getItem(pCategoryId, getCategoryItemDescriptor());
      
      if (category != null) {
        getSEOMetaData(category, metaData);
      }
    }
    catch (RepositoryException e) {
      vlogError("An error occurred while querying product catalog repository for category: {0}.", pCategoryId);
    }

    return metaData;
  }
  
  /**
   * This method retrieves the display name, long description and keywords from
   * product repository item and return it as map having seo title, description 
   * and keywords.
   * 
   * @param pProductId
   *   Product id of the current category page.
   * @return
   *   Returns map which contains seo meta data.
   */
  public Map<String,Object> getProductSEOMetaData(String pProductId) {
    HashMap<String, Object> metaData = new HashMap<>();
    
    try {
      RepositoryItem product = getProductCatalog().getItem(pProductId, getProductItemDescriptor());
      
      if (product != null) {
        getSEOMetaData(product, metaData);
      }
    }
    catch (RepositoryException e) {
      vlogError("An error occurred while querying product catalog repository for product: {0}.", pProductId);
    }

    return metaData;
  }

  /**
   * This method retrieves the display name, long description and keywords from
   * the passed repository item and populates the map so it contains the seo title,
   * description and keywords.
   *
   * @param pRepositoryItem
   *   The repository item to retrieve the meta data from.
   * @param pMetaData
   *   The map to populate the with the SEO meta data.
   * @throws RepositoryException
   */
  public void getSEOMetaData(RepositoryItem pRepositoryItem, HashMap<String, Object> pMetaData)
      throws RepositoryException {

    String productName = (String) pRepositoryItem.getPropertyValue(CATALOG_ITEM_DISPLAY_NAME_PROPERTY);

    pMetaData.put(TITLE_PROPERTY_NAME,
        (getStoreName() != null) ? getStoreName() + " - " + productName : productName);
    pMetaData.put(KEYWORDS_PROPERTY_NAME,
        getKeywordsAsString((List)pRepositoryItem.getPropertyValue(CATALOG_ITEM_KEYWORDS_PROPERTY)));
    pMetaData.put(DESCRIPTION_PROPERTY_NAME,
        pRepositoryItem.getPropertyValue(CATALOG_ITEM_LONG_DESCRIPTION_PROPERTY));
  }
  
  /**
   * This method converts the list of keywords into a string of keywords 
   * separated by commas.
   * 
   * @param pKeywords
   *   List of catalog item keywords.
   * @return
   *   Comma separated keywords string.
   */
  public String getKeywordsAsString(List pKeywords){
    StringBuilder keywords = new StringBuilder();

    for (Object pKeyword : pKeywords) {
      keywords.append((String) pKeyword);
      keywords.append(KEYWORDS_SEPARATOR);
    }

    return keywords.toString();
  }
}
