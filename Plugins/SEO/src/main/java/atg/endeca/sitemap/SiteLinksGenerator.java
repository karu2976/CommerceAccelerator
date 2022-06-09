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

package atg.endeca.sitemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import atg.nucleus.GenericService;

import com.endeca.navigation.AggrERec;
import com.endeca.navigation.AssocDimLocations;
import com.endeca.navigation.DimLocation;
import com.endeca.navigation.DimLocationList;
import com.endeca.navigation.DimVal;
import com.endeca.navigation.DimValList;
import com.endeca.navigation.DimensionList;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ERec;
import com.endeca.navigation.FieldList;
import com.endeca.navigation.PropertyMap;
import com.endeca.soleng.sitemap.FatalModuleException;
import com.endeca.soleng.sitemap.NameValuePair;
import com.endeca.soleng.sitemap.SitemapQuery;
import com.endeca.soleng.sitemap.SitemapTemplate;
import com.endeca.soleng.urlformatter.UrlFormatter;
import com.endeca.soleng.urlformatter.UrlState;

/**
 * This class generates the list of navigation links and detail links based on
 * given site configuration.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/main/java/atg/endeca/sitemap/SiteLinksGenerator.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $ 
 */
public class SiteLinksGenerator extends GenericService {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/main/java/atg/endeca/sitemap/SiteLinksGenerator.java#1 $$Change: 1385662 $";
  
  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------
  
  /** Site base url key. */
  public static final String SITE_BASE_URL = "SITEBASEURL";
  
  /** Locale key. */
  public static final String LOCALE = "LOCALE";
  
  /** Record id key. */
  public static final String RECID = "RECID";
  
  /** Id key. */
  public static final String ID = "ID";
  
  /** Formatted url key. */ 
  public static final String FORMATTED_URL = "FORMATTED_URL";
  
  /** Rollup key. */
  public static final String ROLLUP_KEY = "ROLLUP_KEY";
  
  /** Maximum records in query results. */
  public static final String MAX_RECORDS = "70000";
  
  //---------------------------------------------------------------------------
  // MEMBERS
  //---------------------------------------------------------------------------
  
  /** Default replace properties for url formatter. */
  Properties mDefaultReplaceProperties = new Properties();

  /** Map of url states for generating navigation links. */
  private Map<String, UrlState> mNavigationLinkMap = new HashMap<String, UrlState>();
  
  /** Detail replace properties for detail links. */
  Properties mDetailReplaceProperties;
  
  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------
  
  //-----------------------------------
  // property: storeSites 
  //-----------------------------------
  StoreSiteConfiguration[] mStoreSites;
  
  /**
   * @return
   *   Returns store sites.   
   */
  public StoreSiteConfiguration[] getStoreSites() {
    return mStoreSites;
  }
  
  /**
   * @param pStoreSites
   *   Sites to generate site links.
   */
  public void setStoreSites(StoreSiteConfiguration[] pStoreSites) {
    mStoreSites = pStoreSites;
  }
  
  //-----------------------------------
  // property: navigationSpecList
  //-----------------------------------
  NavigationPageSpec[] mNavigationPageSpecList;
  
  /**
   * @return
   *   Returns the navigation page spec list.
   */
  public NavigationPageSpec[] getNavigationPageSpecList() {
    return mNavigationPageSpecList;
  }

  /**
   * @param pNavigationPageSpecList
   *   Navigation page specification list.
   */
  public void setNavigationPageSpecList(NavigationPageSpec[] pNavigationPageSpecList) {
    mNavigationPageSpecList = pNavigationPageSpecList;
  }
  
  //-----------------------------------
  // property: queryFields
  //-----------------------------------
  List<String> mQueryFields;
  
  /**
   * @return
   *   Returns the query fields to return for each record in query results.  
   */
  public List<String> getQueryFields() {
    return mQueryFields;
  }
  
  /**
   * @param pFields
   *   Fields to return in each record in results. 
   */
  public void setQueryFields(List<String> pFields) {
    mQueryFields = pFields;
  }
  
  //-----------------------------------
  // property: urlEncoding
  //-----------------------------------
  private String mUrlEncoding = null;
  
  /**
   * @return
   *   Returns the Url encoding.
   */
  public String getUrlEncoding() {
    return mUrlEncoding;
  }

  /**
   * @param pUrlEncoding
   *   Set url encoding.
   */
  public void setUrlEncoding(String pUrlEncoding) {
    mUrlEncoding = pUrlEncoding;
  }
  
  //-----------------------------------
  // property: detailLinkFormat
  //-----------------------------------
  private String mDetailLinkFormat;
  
  /**
   * @return
   *   Returns the detail link format.
   */
  public String getDetailLinkFormat() {
    return mDetailLinkFormat;
  }

  /**
   * @param pDetailLinkFormat
   *   Set detail link format.
   */
  public void setDetailLinkFormat(String pDetailLinkFormat) {
    mDetailLinkFormat = pDetailLinkFormat;
  }
  
  //-----------------------------------
  // property: navigationLinkFormat
  //-----------------------------------
  private String mNavigationLinkFormat;
  
  /**
   * @return
   *   Returns the detail link format.
   */
  public String getNavigationLinkFormat() {
    return mNavigationLinkFormat;
  }

  /**
   * @param pNavigationLinkFormat
   *   Set detail link format.
   */
  public void setNavigationLinkFormat(String pNavigationLinkFormat) {
    mNavigationLinkFormat = pNavigationLinkFormat;
  }
  
  //-----------------------------------
  // property: linksPerThread
  //-----------------------------------
  private int mLinksPerThread;
  
  /**
   * @return
   *   Links per each thread to render the pages.
   */
  public int getLinksPerThread(){
    return mLinksPerThread;
  }
  
  /**
   * @param pLinksPerThread
   *   Links per each thread to render the pages.
   */
  public void setLinksPerThread(int pLinksPerThread){
    mLinksPerThread = pLinksPerThread;
  }
  
  //-----------------------------------
  // property: urlFormatter
  //-----------------------------------
  private UrlFormatter mUrlFormatter;

  /**
   * @return
   *   Returns endeca seo url formatter component for generating links.
   */
  public UrlFormatter getUrlFormatter() {
    return mUrlFormatter;
  }

  /**
   * @param pUrlFormatter
   *   Set endeca seo url formatter component for generating links.
   */
  public void setUrlFormatter(UrlFormatter pUrlFormatter) {
    mUrlFormatter = pUrlFormatter;
  }

  //-----------------------------------
  // property: sitemapTemplate
  //-----------------------------------
  private SitemapTemplate mSitemapTemplate=new SitemapTemplate();
  
  /**
   * @return
   *   Returns the sitemap template.
   */
  public SitemapTemplate getSitemapTemplate() {
    return mSitemapTemplate;
  }

  //------------------------------------------
  // property: useLanguageInSiteBaseURL
  //------------------------------------------
  private boolean mUseLanguageInSiteBaseURL = true;

  /**
   * @return
   *   Flag to determine whether or not to include the current MDEX language in the site base URL.
   */
  public boolean isUseLanguageInSiteBaseURL() {
    return mUseLanguageInSiteBaseURL;
  }

  /**
   * @param pUseLanguageInSiteURL
   *   Flag to determine whether or not to include the current MDEX language in the site base URL.
   */
  public void setUseLanguageInSiteBaseURL(boolean pUseLanguageInSiteURL) {
    mUseLanguageInSiteBaseURL = pUseLanguageInSiteURL;
  }
  
  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * This method iterates through query fields and creates FieldList.
   * 
   * @return
   *   Returns the query fields as FieldList. 
   */
  public FieldList getQueryFieldList() {
    FieldList fieldList = new FieldList();
    
    for (int fieldIndex=0;fieldIndex<getQueryFields().size();fieldIndex++) {
      fieldList.addField(getQueryFields().get(fieldIndex));
    }
    return fieldList;
  }
  
  /**
   * This method creates the list of site links by running the sitemap query for
   * the given configuration and creates the link.
   */
  public void runSiteMapGenerator() {
    try {
      String rollupKey = null;
      FieldList fieldList = null;
      
      for (int siteIndex = 0; siteIndex < getStoreSites().length; siteIndex++) {
        StoreSiteConfiguration currentSite = getStoreSites()[siteIndex];
        
        for (int mdexIndex = 0; mdexIndex < currentSite.getSiteMDEXEngines().size(); mdexIndex++) {
          currentSite.getSiteMDEXEngines().get(mdexIndex).setSiteLinksList(new ArrayList<List<String>>());
          MDEXEngine engine = currentSite.getSiteMDEXEngines().get(mdexIndex);
          rollupKey = null;

          if (engine.getRollupKey() != null) {
            rollupKey = engine.getRollupKey();
          }
          SitemapQuery query = new SitemapQuery(engine.getHostName(), engine.getPort(), MAX_RECORDS, rollupKey);
          fieldList = getQueryFieldList();
          if (query.submitQuery(engine.getRootQuery(), fieldList)) {
            generateDetailLinks(query, currentSite, engine);
            String names = "";
            String ids = "";
            List terms = new ArrayList(mNavigationLinkMap.keySet());
            List<String> navigationLinks = new ArrayList<String>();
            
            for (int index = 0; index < terms.size(); ++index) {
              UrlState urlState = (UrlState)mNavigationLinkMap.get((String)terms.get(index));
              String formattedURL = urlState.toString();
              DimLocationList locationList = urlState.getNavState();
              
              if (locationList != null) {
                for (int locationIndex=0; locationIndex< locationList.size(); locationIndex++) {
                  DimVal dimVal = ((DimLocation)locationList.get(locationIndex)).getDimValue();
                  if (!names.equals("")) {
                    names += ", ";
                  }
                  if (!ids.equals("")) {
                    ids += "+";
                  }
                  names += dimVal.getDimensionName() + " > " + dimVal.getName();
                  ids += dimVal.getId();
                }
              }
              Map<String, String> navigationLinkReplaceMap = new HashMap<String, String>();
              
              navigationLinkReplaceMap.put(LOCALE, engine.getLocale());

              navigationLinkReplaceMap.put(SITE_BASE_URL,
                isUseLanguageInSiteBaseURL()
                  ? currentSite.getSiteBaseUrl() + "/" + engine.getLocale().substring(0, 2)
                  : currentSite.getSiteBaseUrl());

              navigationLinkReplaceMap.put(SitemapTemplate.DIMVAL_IDS, ids);
              navigationLinkReplaceMap.put(SitemapTemplate.DIMVAL_NAMES, names);
              navigationLinkReplaceMap.put(SitemapTemplate.FORMATTED_URL, formattedURL);
              navigationLinks.add(getSitemapTemplate().replace(getNavigationLinkFormat(), navigationLinkReplaceMap));
              if (navigationLinks.size() == getLinksPerThread()) {
                currentSite.getSiteMDEXEngines().get(mdexIndex).getSiteLinksList().add(navigationLinks);
                navigationLinks = new ArrayList<String>();
              }
            }
            if (navigationLinks.size() > 0) {
              currentSite.getSiteMDEXEngines().get(mdexIndex).getSiteLinksList().add(navigationLinks);
            }
            mNavigationLinkMap = new HashMap<String, UrlState>();
            
          }
        }
      }
    } 
    catch(FatalModuleException e) {
      vlogError("An error occured while running sitemap query");
    }
    catch (Exception e) {
      vlogError("An error occured while generating the site links");
    }
  }

  /**
   * Using the query results from each MDEX, this method creates the product 
   * detail links.
   * 
   * @param pQuery
   *   Sitemap query with results to generate detail links.
   * @param pCurrentSite
   *   Current site configuration for generating detail links.
   * @param pCurrentMdex
   *   Current Mdex configuration for generating detail links.
   */
  public void generateDetailLinks(SitemapQuery pQuery, StoreSiteConfiguration pCurrentSite, MDEXEngine pCurrentMdex) {
    try {
      Iterator recordIterator = null;
      String link = "";
      DimensionList descriptorDimensionList = null;
      NavigationPageSpec[] navigationPageSpecList = getNavigationPageSpecList();
      String rollupKey = "";
            
      // Get records from query results via bulk iterator, and loop.
      ENEQueryResults queryResults = pQuery.getResults();

      if (!pQuery.isAggregateQuery()) {
        recordIterator = queryResults.getNavigation().getBulkERecIter();
      }
      else {
        recordIterator = queryResults.getNavigation().getBulkAggrERecIter();
        rollupKey = pQuery.rollupKey;
        mDefaultReplaceProperties.setProperty(ROLLUP_KEY, rollupKey);
        descriptorDimensionList = queryResults.getNavigation().getDescriptorDimensions();
      }
      List<String> linksList = new ArrayList<String>();

      while (recordIterator.hasNext()) {
        Object record = null;
        
        // Get record, build and write detail link.
        try {
          record = recordIterator.next();
        } 
        catch (NoSuchElementException e) {
          break;
        }

       // Create a new detailReplaceProperties object for this record
       // using the defaultReplaceProperties.
       mDetailReplaceProperties = new Properties(mDefaultReplaceProperties);
       mDefaultReplaceProperties.setProperty(LOCALE, pCurrentMdex.getLocale());

       mDefaultReplaceProperties.setProperty(SITE_BASE_URL,
         isUseLanguageInSiteBaseURL()
           ? pCurrentSite.getSiteBaseUrl() + "/" + pCurrentMdex.getLocale().substring(0, 2)
           : pCurrentSite.getSiteBaseUrl());

       if (!pQuery.isAggregateQuery()) {
         link = buildDetailLink((ERec)record, getDetailLinkFormat(), true);
       } 
       else {
         link = buildAggregateDetailLink((AggrERec)record, getDetailLinkFormat(), rollupKey, descriptorDimensionList);
       }

       linksList.add(link);
       if(linksList.size() == getLinksPerThread()){
         pCurrentMdex.getSiteLinksList().add(linksList);
         linksList = new ArrayList<String>();
       }
            
       // Here's where we build unique navigation links from each record.
       // For each navigation page specification, traverse relevant dimvals associated
       // with each record.
       for (int i=0; i<navigationPageSpecList.length; i++) {
         List navigationPageSpec = navigationPageSpecList[i].getDimensionsAsNameValuePair();
         
         if (!pQuery.isAggregateQuery()) {
           traverseDims((ERec)record, navigationPageSpec, new UrlState(getUrlFormatter(), getUrlEncoding()), 0, null);
         }
         else {
           
           // Get record list.
           List repRecords = ((AggrERec)record).getERecs(); 
           Iterator repRecordIterator = repRecords.iterator();
           
           while (repRecordIterator.hasNext()) {
             Object repRecord = repRecordIterator.next();
             traverseDims((ERec)repRecord, navigationPageSpec, new UrlState(getUrlFormatter(), getUrlEncoding()), 0, rollupKey);
           }
         }
       }
     }
     if(!linksList.isEmpty()){
       pCurrentMdex.getSiteLinksList().add(linksList);
     }
    } 
    catch(FatalModuleException e) {
      vlogError("An error occured while running sitemap query.");
    }
    catch (Exception e) {
      vlogError("An error occured while generating detail links.");
    }
  }

  /**
   * This method traverses through the record for all the dimensions in the given 
   * navigation page specification. Adds the urlstate to navigation link map to 
   * generate the unique navigation link for navigation specification. 
   * 
   * @param pRecord
   *   Record for traversing the dimension values.
   * @param pNavigationPageSpec
   *   Navigation page specification for generating unique navigation link.
   * @param pPreviousUrlState
   *   Previous url state generated in recursion.
   * @param pDepth
   *   Index of the dimension in navigation page specification.
   * @param pRollupKey
   *   Rollup key.
   */
  public void traverseDims(ERec pRecord, List pNavigationPageSpec,UrlState pPreviousUrlState,
    int pDepth, String pRollupKey) {

    NameValuePair dimension = (NameValuePair)pNavigationPageSpec.get(pDepth);
    String dimensionName = dimension.getName();
    String traverseAncestors = dimension.getValue();

    AssocDimLocations dimLocations = pRecord.getDimValues().getAssocDimLocations(dimensionName);

    if (dimLocations != null) {
      
      // Adding ability to Generate URLs using URL formatter.
      for (int dimLocationIndex=0; dimLocationIndex<dimLocations.size(); dimLocationIndex++) {

        // First clone previous UrlState to the current state.
        UrlState urlState = (UrlState)pPreviousUrlState.clone();

        // Add new dimension value to urlState.
        urlState.selectRefinement((DimLocation)dimLocations.get(dimLocationIndex), false);

        // Check to see if ancestor values should be traversed.
        // If true, then add them to the list and recursively call
        // traverseDims.
        if (traverseAncestors.equals("true")) {
          DimValList ancestors = ((DimLocation)dimLocations.get(dimLocationIndex)).getAncestors();

          if (ancestors!=null) {
            DimLocationList dimLocationList = urlState.getNavState();
            DimVal dimVal = null;

            if (dimLocationList != null) {
              for (int dimLocationListIndex=0; dimLocationListIndex < dimLocationList.size(); dimLocationListIndex++) {
                DimLocation dimLocation = (DimLocation)dimLocationList.get(dimLocationListIndex);
                dimVal = dimLocation.getDimValue();

                for (int ancestorIndex = 0; ancestorIndex < ancestors.size();ancestorIndex++) {
                  if (((DimVal)ancestors.get(ancestorIndex)).getDimensionId() == dimVal.getDimensionId()) {
                    UrlState ancestorUrlState = urlState.selectAncestor((DimVal)ancestors.get(ancestorIndex), dimVal, true);
                    
                    if (pDepth < pNavigationPageSpec.size()-1) {
                      traverseDims(pRecord, pNavigationPageSpec, ancestorUrlState, pDepth+1, pRollupKey);
                    }
                    else {
                      DimLocationList locationList = ancestorUrlState.getNavState();
                      String ids = "";

                      // Using the dimval list as a key for the map.
                      for (int j=0; j< locationList.size(); j++) {
                        DimVal locationListDimVal = ((DimLocation)locationList.get(j)).getDimValue();
                        if (!ids.equals("")) {
                          ids += "+";
                        }
                        ids += locationListDimVal.getId();
                      }
                      mNavigationLinkMap.put(ids, ancestorUrlState);
                    }
                  }
                }
              }
            }
          }
        }
        
        // If this is not the last dimension in the navigation page specification,
        // recursively call traverseDims method with new dimVals list
        // and depth+1.
        if (pDepth < pNavigationPageSpec.size()-1) {
          traverseDims(pRecord, pNavigationPageSpec, urlState, pDepth+1, pRollupKey);
        }
        else {
          
          // If this is the last dimension in the navigation page specification,
          // place each unique list of dimvals in the navigationLinkMap.
          DimLocationList locationList = urlState.getNavState();
          String ids = "";

          // Using the dimval list as a key for the map, for backwards compatibility.
          for (int locationListIndex=0; locationListIndex < locationList.size(); locationListIndex++) {
            DimVal dimVal = ((DimLocation)locationList.get(locationListIndex)).getDimValue();
            if (!ids.equals("")) {
              ids += "+";
            }
            ids += dimVal.getId();
          }
          mNavigationLinkMap.put(ids, urlState);
        }
      }
    }
  }
  
  /**
   * Build aggregate detail link from formatting defined in formatting template.
   *
   * @param pAggregateRecord
   *   Record for generating detail URL.
   * @param pDetailLink
   *   Detail link format.
   * @param pRollupKey
   *   Rollup key.
   * @throws Exception
   *   Throws an exception if any issue.
   */
  public String buildAggregateDetailLink(AggrERec pAggregateRecord, String pDetailLink,
    String pRollupKey, DimensionList pDescriptorDimensionList) throws Exception {

    PropertyMap aggregatePropertyMap = pAggregateRecord.getProperties();
    Iterator keys = aggregatePropertyMap.keySet().iterator();
    
    while (keys.hasNext()) {
      String key = (String)keys.next();
      mDetailReplaceProperties.setProperty(key.toUpperCase(),(String)aggregatePropertyMap.get(key));
    }

    // Necessary to get at the other properties.
    ERec repRecord = pAggregateRecord.getRepresentative(); 

    // Add record id to map.
    mDetailReplaceProperties.setProperty(RECID, pAggregateRecord.getSpec());

    // Generate URL using URL Formatter.
    String formattedURL = "";
    UrlState urlState = new UrlState(getUrlFormatter(), getUrlEncoding());

    // Set the Aggregated Record.
    urlState.setAggrERec(pAggregateRecord);
    formattedURL = urlState.toString();
    mDetailReplaceProperties.setProperty(FORMATTED_URL, formattedURL);
    return buildDetailLink(repRecord, pDetailLink, false);
  }
  
  /**
   * Build detail link from formatting defined in formatting template.
   *
   * @param pRecord
   *   Record for generating detail URL.
   * @param pDetailLink
   *   Detail link format.
   * @param pAddRecordIdToMap
   *   Flag to add record id into replace map.
   * @throws Exception
   *   Throws an exception if any issue.
   */
  public String buildDetailLink(ERec pRecord, String pDetailLink, boolean pAddRecordIdToMap) throws Exception {
    PropertyMap propertyMap = pRecord.getProperties();
    Iterator keys = propertyMap.keySet().iterator();
    
    while (keys.hasNext()) {
      String key = (String)keys.next();
      mDetailReplaceProperties.setProperty(key.toUpperCase(),(String)propertyMap.get(key));
    }

    // Add record id to map.
    if (pAddRecordIdToMap) {
      mDetailReplaceProperties.setProperty(RECID, pRecord.getSpec());

      // Generate URL using URL Formatter.
      String formattedURL = "";
      UrlState urlState = new UrlState(getUrlFormatter(), getUrlEncoding());
      urlState.setERec(pRecord);
      formattedURL = urlState.toString();
      mDetailReplaceProperties.setProperty(FORMATTED_URL, formattedURL);
    }
    return getSitemapTemplate().replace(pDetailLink, mDetailReplaceProperties);
  }
}
