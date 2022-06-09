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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import org.openqa.selenium.phantomjs.PhantomJSDriver;

/**
 * This class generates pre rendered pages for list of urls and add them to the 
 * repository. 
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/main/java/atg/repository/seo/PageRenderer.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $ 
 */
class PageRenderer implements Runnable {
  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/main/java/atg/repository/seo/PageRenderer.java#1 $$Change: 1385662 $";

  /** Logging component */
  private static final ApplicationLogging mLogger =
          ClassLoggingFactory.getFactory().getLoggerForClass(PageRenderer.class);

  //---------------------------------------------------------------------------
  //  PROPERTIES
  //---------------------------------------------------------------------------
  
  //-----------------------------------
  // property: urlPrefix
  //-----------------------------------
  private String mUrlPrefix;
  
  /**
   * @return
   *   Returns site url prefix.
   */
  public String getUrlPrefix() {
    return mUrlPrefix;
  }
  
  /**
   * @param pUrlPrefix
   *   Set the site url prefix.
   */
  public void setUrlPrefix(String pUrlPrefix) {
    mUrlPrefix = pUrlPrefix;
  }
  
  //-----------------------------------
  // property: links
  //-----------------------------------
  private List<String> mLinks;
  
  /**
   * @return
   *   Returns the links for which pre-rendered pages has to be generated.
   */
  public List<String> getLinks() {
    return mLinks;
  }
  
  /**
   * @param pLinks
   *   Set site links for which pre-rendered pages has to be generated.
   */
  public void setLinks(List<String> pLinks) {
    mLinks = pLinks;
  }
  
  //-----------------------------------
  // property: repository
  //-----------------------------------
  private MutableRepository mRepository;
  
  /**
   * @return
   *   Returns the repository.
   */
  public MutableRepository getRepository() {
    return mRepository;
  }
  
  /**
   * @param pRepository
   *   Set repository for adding pre-rendered pages.
   */
  public void setRepository(MutableRepository pRepository) {
    mRepository = pRepository;
  }
  
  //-----------------------------------
  // property: renderer
  //-----------------------------------
  private PhantomjsRenderer mRenderer;
  
  /**
   * @return
   *   Returns the phantomjs renderer.
   */
  public PhantomjsRenderer getRenderer() {
    return mRenderer;
  }
  
  /**
   * @param pRenderer
   *   Set pahntomjs renderer.
   */
  public void setRenderer(PhantomjsRenderer pRenderer) {
    mRenderer = pRenderer;
  }
  
  //-----------------------------------
  // property: seoPageItemDescriptor
  //-----------------------------------
  private String mSeoPageItemDescriptor = "SEOPage";
  
  /**
   * @return
   *   Returns seo page item descriptor.
   */
  public String getSeoPageItemDescriptor() {
    return mSeoPageItemDescriptor;
  }

  /**
   * @param pSeoPageItemDescriptor
   *   Set seo page item descriptor.
   */
  public void setSeoPageItemDescriptor(String pSeoPageItemDescriptor) {
    mSeoPageItemDescriptor = pSeoPageItemDescriptor;
  }

  //-----------------------------------
  // property: seoPageNamePropertyName
  //-----------------------------------
  private String mSeoPageNamePropertyName = "name";
  
  /**
   * @return 
   *   Returns seo page name property name.
   */
  public String getSeoPageNamePropertyName() {
    return mSeoPageNamePropertyName;
  }

  /**
   * @param pSeoPageNamePropertyName
   *   Set seo page name property name.
   */
  public void setSeoPageNamePropertyName(String pSeoPageNamePropertyName) {
    mSeoPageNamePropertyName = pSeoPageNamePropertyName;
  }

  //-----------------------------------
  // property: seoPageContentPropertyName
  //-----------------------------------
  private String mSeoPageContentPropertyName = "content";
  
  /**
   * @return
   *   Returns seo page content property name.
   */
  public String getSeoPageContentPropertyName() {
    return mSeoPageContentPropertyName;
  }

  /**
   * @param pSeoPageContentPropertyName
   *   Set seo page content property name.
   */
  public void setSeoPageContentPropertyName(String pSeoPageContentPropertyName) {
    mSeoPageContentPropertyName = pSeoPageContentPropertyName;
  }

  //---------------------------------------------------------------------------
  // CONSTRUCTORS
  //---------------------------------------------------------------------------
  /**
   * Constructor for page renderer.
   * 
   * @param pUrlPrefix
   *   Site url prefix.
   * @param pLinks
   *   Site links for which pre-rendered pages has to be generated.
   * @param pRepository
   *   Repository to add pre-rendered page content.
   * @param pRenderer
   *   Phantomjs renderer to generate pre-rendered content.
   */
  public PageRenderer(String pUrlPrefix,List<String> pLinks,MutableRepository pRepository,
    PhantomjsRenderer pRenderer){
    mUrlPrefix = pUrlPrefix;
    mLinks = pLinks;
    mRepository = pRepository;
    mRenderer = pRenderer;
  }
  
  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------
  /**
   * This method iterates through the list of links and generated the pre-rendered
   * pages using phantomjs and add them to SEO repository.
   */
  @Override
  public void run() {
    PhantomJSDriver driver = getRenderer().newWebDriver();

    try {
      Iterator<String> linksIterator = getLinks().iterator();
      while(linksIterator.hasNext()){
        String pageSource = "";
        String keyUrl = linksIterator.next();
        URL pageUrl = new URL(getUrlPrefix() + keyUrl);
        pageSource = getRenderer().getHtmlContent(pageUrl, driver);

        // Only cache the returned page source if it is not empty.
        if (!pageSource.isEmpty()) {
          RepositoryItem seoPage = getRepository().getItem(keyUrl, getSeoPageItemDescriptor());

          if (seoPage != null) {
            MutableRepositoryItem pageItem = getRepository().getItemForUpdate(keyUrl, getSeoPageItemDescriptor());
            pageItem.setPropertyValue(getSeoPageContentPropertyName(), pageSource);
            getRepository().updateItem(pageItem);
          } else {
            MutableRepositoryItem pageItem = getRepository().createItem(getSeoPageItemDescriptor());
            pageItem.setPropertyValue(getSeoPageNamePropertyName(), keyUrl);
            pageItem.setPropertyValue(getSeoPageContentPropertyName(), pageSource);
            getRepository().addItem(pageItem);
          }
        }
      }
    }
    catch (MalformedURLException | RepositoryException e) {
      mLogger.logError("Error occurred while updating or adding SEO page item");
    }
    finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
}



