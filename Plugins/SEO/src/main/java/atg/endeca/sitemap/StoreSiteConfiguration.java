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
import java.util.List;
import java.util.Map;

import atg.endeca.assembler.configuration.AssemblerApplicationConfiguration;
import atg.endeca.assembler.configuration.HostAndPort;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * This class represents the site configuration for generating the site links.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/main/java/atg/endeca/sitemap/StoreSiteConfiguration.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $ 
 */
public class StoreSiteConfiguration extends GenericService {
  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/main/java/atg/endeca/sitemap/StoreSiteConfiguration.java#1 $$Change: 1385662 $";
  
  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------
  
  /** Language country separator. */
  public static final String LANGUAGE_COUNTRY_SEPERATOR = "_";
  
  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------
  
  //-----------------------------------
  // property: siteId
  //-----------------------------------
  private String mSiteId;
  
  /**
   * @return
   *   Returns the site id.
   */
  public String getSiteId() {
    return mSiteId;
  }
  
  /**
   * @param pSiteId
   *   Set the site id.
   */
  public void setSiteId(String pSiteId) {
    mSiteId = pSiteId;
  }
  
  //-----------------------------------
  // property: siteBaseUrl
  //-----------------------------------
  private String mSiteBaseUrl;
  
  /**
   * @return
   *   Return site base url.
   */
  public String getSiteBaseUrl() {
    return mSiteBaseUrl;
  }
  
  /**
   * @param pSiteBaseUrl
   *   Set the site base url.
   */
  public void setSiteBaseUrl(String pSiteBaseUrl) {
    mSiteBaseUrl = pSiteBaseUrl;
  }
  
  //-----------------------------------
  // property: applicationConfiguration
  //-----------------------------------
  private AssemblerApplicationConfiguration mApplicationConfiguration;

  /**
   * @return
   *   Returns endeca assembler application configuration.
   */
  public AssemblerApplicationConfiguration getApplicationConfiguration() {
    return mApplicationConfiguration;
  }

  /**
   * @param pApplicationConfiguration
   *   Set the endeca assembler application configuration.
   */
  public void setApplicationConfiguration(AssemblerApplicationConfiguration pApplicationConfiguration) {
    mApplicationConfiguration = pApplicationConfiguration;
  }
  
  //-----------------------------------
  // property: rollupKey
  //-----------------------------------
  private String mRollupKey;

  /**
   * @return
   *   Return rollup key.
   */
  public String getRollupKey() {
    return mRollupKey;
  }

  /**
   * @param pRollupKey
   *   Set rollup key.
   */
  public void setRollupKey(String pRollupKey) {
    mRollupKey = pRollupKey;
  }

  //-----------------------------------
  // property: defaultRootQuery
  //-----------------------------------
  private String mDefaultRootQuery;

  /**
   * @return
   *   Returns the default root query.
   */
  public String getDefaultRootQuery() {
    return mDefaultRootQuery;
  }

  /**
   * @param pDefaultRootQuery
   *   Set the default root query.
   */
  public void setDefaultRootQuery(String pDefaultRootQuery) {
    mDefaultRootQuery = pDefaultRootQuery;
  }

	//-----------------------------------
	// property: languageToRootQuery
	//-----------------------------------
	private Map<String, String> mLanguageToRootQuery;

	/**
	 * @return
	 *   Returns the language to root query map.
	 */
	public Map<String, String> getLanguageToRootQuery() {
		return mLanguageToRootQuery;
	}

	/**
	 * @param pLanguageToRootQuery
	 *   Set the language to root query map.
	 */
	public void setLanguageToRootQuery(Map<String, String> pLanguageToRootQuery) {
		mLanguageToRootQuery = pLanguageToRootQuery;
	}
  
  //-----------------------------------
  // property: siteRepository
  //-----------------------------------
  private Repository mSiteRepository;
  
  /**
   * @return
   *   Returns site repository.
   */
  public Repository getSiteRepository() {
    return mSiteRepository;
  }

  /**
   * @param pSiteRepository
   *   Set site repository.
   */
  public void setSiteRepository(Repository pSiteRepository) {
    mSiteRepository = pSiteRepository;
  }
  
  //-----------------------------------
  // property: siteConfigurationItemDescriptor
  //-----------------------------------
  private String mSiteConfigurationItemDescriptor = "siteConfiguration";
  
  /**
   * @return
   *   Returns site configuration item descriptor name.
   */
  public String getSiteConfigurationItemDescriptor() {
    return mSiteConfigurationItemDescriptor;
  }

  /**
   * @param pSiteConfigurationItemDescriptor
   *   Set site configuration item descriptor name.
   */
  public void setSiteConfigurationItemDescriptor(String pSiteConfigurationItemDescriptor) {
    mSiteConfigurationItemDescriptor = pSiteConfigurationItemDescriptor;
  }

  //-----------------------------------
  // property: defaultCountryCode
  //-----------------------------------
  private String mDefaultCountryCode;
  
  /**
   * @return
   *   Return site default country code.
   */
  public String getDefaultCountryCode() {
    return mDefaultCountryCode;
  }

  /**
   * @param pDefaultCountryCode
   *   Set the site default country code.
   */
  public void setDefaultCountryCode(String pDefaultCountryCode) {
    mDefaultCountryCode = pDefaultCountryCode;
  }
  
  //-----------------------------------
  // property: defaultCountryPropertyName
  //-----------------------------------
  private String mDefaultCountryPropertyName = "defaultCountry";
  
  /**
   * @return
   *   Returns default country property name of site configuration.
   */
  public String getDefaultCountryPropertyName() {
    return mDefaultCountryPropertyName;
  }

  /**
   * @param pDefaultCountryPropertyName
   *   Set default country property name of site configuration.
   */
  public void setDefaultCountryPropertyName(String pDefaultCountryPropertyName) {
    mDefaultCountryPropertyName = pDefaultCountryPropertyName;
  }

  //-----------------------------------
  // property: supportedLanguages
  //-----------------------------------
  private List<String> mSupportedLanguages;
  
  /**
   * @return
   *   Returns site supported languages.
   */
  public List<String> getSupportedLanguages() {
    return mSupportedLanguages;
  }
  
  /**
   * @param pSupportedLanguages
   *   Set the site supported languages.
   */
  public void setSupportedLanguages(List<String> pSupportedLanguages) {
    mSupportedLanguages = pSupportedLanguages;
  }
  
  //-----------------------------------
  // property: languagesPropertyName
  //-----------------------------------
  public String mLanguagesPropertyName = "languages";
  
  /**
   * @return
   *   Returns languages property name of site configuration.
   */
  public String getLanguagesPropertyName() {
    return mLanguagesPropertyName;
  }
  
  //-----------------------------------
  // property: siteBaseUrlPropertyName
  //-----------------------------------
  private String mSiteBaseUrlPropertyName = "productionURL";
  
  /**
   * @return
   *   Returns site production url for current site.
   */
  public String getSiteBaseUrlPropertyName() {
    return mSiteBaseUrlPropertyName;
  }

  /**
   * @param pSiteBaseUrlPropertyName
   *   Set site production url for current site.
   */
  public void setSiteBaseUrlPropertyName(String pSiteBaseUrlPropertyName) {
    mSiteBaseUrlPropertyName = pSiteBaseUrlPropertyName;
  }  
  
  //------------------------------------
  // property: siteMDEXEngines
  //------------------------------------
  private List<MDEXEngine> mSiteMDEXEngines;
  
  /**
   * @return
   *   Return the list of mdex configurations for site supported locales.
   */
  public List<MDEXEngine> getSiteMDEXEngines() {
    return mSiteMDEXEngines;
  }
  
  /**
   * @param pSiteMDEXEngines
   *   Set the list of mdex configurations for site supported locales.
   */
  public void setSiteMDEXEngines(List<MDEXEngine> pSiteMDEXEngines) {
    mSiteMDEXEngines = pSiteMDEXEngines;
  }
  
  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------
  
  /**
   * This method set the default country and supported languages for current site
   * for generating the site links.
   */
  @Override
  public void doStartService() throws ServiceException {
    super.doStartService();
    try {
      RepositoryItem site = getSiteRepository().getItem(getSiteId(), getSiteConfigurationItemDescriptor());

      // If the default country property has not been configured set it to the default country
      // configured for the site.
      if (getDefaultCountryCode() == null) {
        setDefaultCountryCode((String) site.getPropertyValue(getDefaultCountryPropertyName()));
      }

      // If the supported languages property has not been configured set it to the languages
      // configured for the site.
      if (getSupportedLanguages() == null) {
        List<String> supportedLocales =
          (List<String>) site.getPropertyValue(getLanguagesPropertyName());
        setSupportedLanguages(supportedLocales);
      }

      // If the site base url property has not been configured set it to the site base url
      // configured for the site.
      if (getSiteBaseUrl() == null) {
        setSiteBaseUrl((String) site.getPropertyValue(getSiteBaseUrlPropertyName()));
      }

      List<MDEXEngine> mdexEngines = new ArrayList<>();

      // Loop each of the supported languages for the configured site id and create a new MDEX
      // Engine for each.
      for (int languageIndex = 0; languageIndex < getSupportedLanguages().size(); languageIndex++) {
        String language = getSupportedLanguages().get(languageIndex);
        String rootQuery = getLanguageToRootQuery().containsKey(language) ?
                getLanguageToRootQuery().get(language) : getDefaultRootQuery();
        HostAndPort hostAndPort = getApplicationConfiguration().
          getParsedApplicationKeyToMdexHostAndPort().get(language);
        String locale = language + LANGUAGE_COUNTRY_SEPERATOR + getDefaultCountryCode();
        
        mdexEngines.add(new MDEXEngine(hostAndPort.getHostName(),
          String.valueOf(hostAndPort.getPort()), getRollupKey(), rootQuery, locale));
      }
      setSiteMDEXEngines(mdexEngines);
    }
    catch (RepositoryException e) {
      logError("There is an issue retrieving site configuration from site repository");
    }
  }
}
