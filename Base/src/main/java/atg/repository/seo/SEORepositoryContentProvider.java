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

import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryView;
import atg.repository.rql.RqlStatement;

import atg.servlet.ServletUtil;
import com.endeca.infront.assembler.ContentItem;

/**
 * This class provides the implementation for adding the SEORepository SEOTags 
 * data to the content item based content path and site id provided. 
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/repository/seo/SEORepositoryContentProvider.java#1 $ 
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class SEORepositoryContentProvider extends GenericService implements SEOContentProvider {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/repository/seo/SEORepositoryContentProvider.java#1 $$Change: 1385662 $";


  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------
  
  /** Title property name of SEOTags repository item. */
  public static final String TITLE_PROPERTY_NAME = "title";  
  
  /** Description property name of SEOTags repository item. */
  public static final String DESCRIPTION_PROPERTY_NAME = "description";  
  
  /** Keywords property name of SEOTags repository item. */
  public static final String KEYWORDS_PROPERTY_NAME = "keywords";
  
  /** Keywords property name of SEOTags repository item. */
  public static final String SEO_META_DATA = "metadata";
  
  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //------------------------------------
  // property: repository
  //------------------------------------
  private Repository mRepository = null;

  /**
   * @param pRepository
   *   Set the repository that contains SEO tags data.
   */
  public void setRepository(Repository pRepository) {
    mRepository = pRepository;
  }

  /**
   * @return
   *   Get the repository that contains the SEO tags data. 
   */
  public Repository getRepository() {
    return mRepository;
  }
  
  //------------------------------------
  // property: itemDescriptorName
  //------------------------------------
  private String mItemDescriptorName = null;

  /**
   * @param pItemDescriptorName
   *   Name of the itemDescriptor to get the SEO tags data from repository.
   */
  public void setItemDescriptorName(String pItemDescriptorName) {
    mItemDescriptorName = pItemDescriptorName;
  }

  /**
   * @return
   *   Name of the itemDescriptor to get the SEO tags data from repository.
   */
  public String getItemDescriptorName() {
    return mItemDescriptorName;
  }

  //------------------------------------
  // property: rqlQuery
  //------------------------------------
  private String mRqlQuery = null;

  /**
   * @param pRqlQuery
   *   RqlQuery to get the SEO tags data from repository.
   */
  public void setRqlQuery(String pRqlQuery) {
    mRqlQuery = pRqlQuery;
  }

  /**
   * @return
   *   RqlQuery to get the SEO tags data from repository.
   */
  public String getRqlQuery() {
    return mRqlQuery;
  }

  //------------------------------------
  // property: removeLanguageFromKey
  //------------------------------------
  private boolean mRemoveLanguageFromKey = true;

  /**
   * @param pRemoveLanguageFromKey
   *   Flag that determines whether or not to remove the language portion
   *   of the key, if one exists.
   */
  public void setRemoveLanguageFromKey(boolean pRemoveLanguageFromKey) {
    mRemoveLanguageFromKey = pRemoveLanguageFromKey;
  }

  /**
   * @return
   *   Flag that determines whether or not to remove the language portion
   *   of the key, if one exists.
   */
  public boolean isRemoveLanguageFromKey() {
    return mRemoveLanguageFromKey;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------
  
  /**
   * This method retrieves the SEO data from SEORepository for the given key, 
   * site id. 
   *   
   * @param pKey
   *   Content path for the current page.
   * @param pSiteId
   *   Site id for which SEO data to fetch. 
   * @param pContentItem
   *   Response content item from endeca.
   */
  @Override
  public void fillSEOTagData(String pKey, String pSiteId, ContentItem pContentItem) {
    try {
      RepositoryView view = getRepository().getView(getItemDescriptorName());
      RqlStatement statement = RqlStatement.parseRqlStatement(mRqlQuery, true);

      String currentLanguage = ServletUtil.getUserLocale().getLanguage();

      Object params[] = new Object[2];

      if (isRemoveLanguageFromKey() && pKey.startsWith("/" + currentLanguage + "/")) {
        params[0] = pKey.replaceFirst("/" + currentLanguage, "");
      }
      else {
        params[0] = pKey;
      }

      params[1] = pSiteId;
      RepositoryItem [] items = statement.executeQuery(view, params);
      
      if (items != null && items.length > 0) {
        HashMap<String, Object> metaData = new HashMap<>();

        metaData.put(TITLE_PROPERTY_NAME,items[0].getPropertyValue(TITLE_PROPERTY_NAME));
        metaData.put(DESCRIPTION_PROPERTY_NAME,items[0].getPropertyValue(DESCRIPTION_PROPERTY_NAME));
        metaData.put(KEYWORDS_PROPERTY_NAME,items[0].getPropertyValue(KEYWORDS_PROPERTY_NAME));
        pContentItem.put(SEO_META_DATA, metaData);
      }
    }
    catch (RepositoryException repositoryException) {
      if(isLoggingError()) {
        logError("An error occurred while querying SEO repository.");
      }
    }
  }
}