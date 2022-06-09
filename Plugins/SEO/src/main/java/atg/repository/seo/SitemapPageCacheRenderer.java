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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import atg.endeca.sitemap.MDEXEngine;
import atg.endeca.sitemap.SiteLinksGenerator;
import atg.endeca.sitemap.StoreSiteConfiguration;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;

/**
 * This class generates the pre-rendered pages for the links and adds them to
 * SEO Repository.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/main/java/atg/repository/seo/SitemapPageCacheRenderer.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $ 
 */
public class SitemapPageCacheRenderer {
  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/main/java/atg/repository/seo/SitemapPageCacheRenderer.java#1 $$Change: 1385662 $";
  
  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------
  
  /** Http scheme string. */
  public static final String HTTP_SCHEME = "http";
  
  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------
  
  //-----------------------------------
  // property: linkGenerator
  //-----------------------------------
  private SiteLinksGenerator mLinkGenerator;
  
  /**
   * @return
   *   LinkGenerator which generates the site links.
   */
  public SiteLinksGenerator getLinkGenerator() {
    return mLinkGenerator;
  }
  
  /**
   * Set the link generator.
   * 
   * @param pLinkGenerator
   *   Link generator which generates site links.
   */
  public void setLinkGenerator(SiteLinksGenerator pLinkGenerator) {
    mLinkGenerator = pLinkGenerator;
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
   *   Set phantomjs renderer.
   */
  public void setRenderer(PhantomjsRenderer pRenderer) {
    mRenderer = pRenderer;
  }
  
  //-----------------------------------
  //  property: seoPageRepository
  //-----------------------------------
  private MutableRepository mSeoPageRepository;
  
  /**
   * @return
   *   Returns the SEO Page repository.
   */
  public MutableRepository getSeoPageRepository() {
    return mSeoPageRepository;
  }
  
  /**
   * @param pSeoPageRepository
   *   Set the SEO Page repository.
   */  
  public void setSeoPageRepository(MutableRepository pSeoPageRepository) {
      mSeoPageRepository = pSeoPageRepository;
  }
  
  //-----------------------------------
  // property: siteServerName
  //-----------------------------------
  private String mSiteServerName;
  
  /**
   * @return
   *   Returns server name.
   */
  public String getSiteServerName() {
    return mSiteServerName;
  }
  
  /**
   * @param pSiteServerName
   *   Sets the server name.
   */
  public void setSiteServerName(String pSiteServerName) {
    mSiteServerName = pSiteServerName;
  }

  //-----------------------------------
  // property: siteServerPort
  //-----------------------------------
  private int mSiteServerPort;
  
  /**
   * @return
   *   Returns the server port.
   */
  public int getSiteServerPort() {
    return mSiteServerPort;
  }

  /**
   * @param pSiteServerPort
   *   Sets the server port.
   */
  public void setSiteServerPort(int pSiteServerPort) {
    mSiteServerPort = pSiteServerPort;
  }

  //-----------------------------------
  // property: maxThreadPoolSize
  //-----------------------------------
  private int mMaxThreadPoolSize;

  /**
   * @return
   *   Returns the max thread pool size.
   */
  public int getMaxThreadPoolSize() {
    return mMaxThreadPoolSize;
  }

  /**
   * @param pMaxThreadPoolSize
   *   Sets the max thread pool size.
   */
  public void setMaxThreadPoolSize(int pMaxThreadPoolSize) {
    mMaxThreadPoolSize = pMaxThreadPoolSize;
  }
  
  //---------------------------------------------------------------------------
  //  METHODS
  //---------------------------------------------------------------------------

  /**
   * This method generates the page cache by generating the site links and renders
   * page for each link using phantomjs and add it to the SEO repository. To speed up
   * the generation thread pool is used and each thread will render list of links using
   * the single phantomjs instance.
   *
   * @throws IOException
   *   Throws exception if any io issues.
   * @throws RepositoryException
   *   Throws exception any issues with repository operation.
   * @throws InterruptedException
   *   Throws exception if there are any issues with thread operation.
   */
  public void generatePageCache() throws IOException, RepositoryException, InterruptedException{
    getLinkGenerator().runSiteMapGenerator();

    // Set the thread pool size for the executor to the number of cpu cores available to
    // the jvm.
    int threadPoolSize = Runtime.getRuntime().availableProcessors();

    // If the max thread pool size has been configured and it is less than the number of
    // cores available to the jvm then use the configured value instead.
    if (getMaxThreadPoolSize() > 0 && getMaxThreadPoolSize() < threadPoolSize) {
      threadPoolSize = getMaxThreadPoolSize();
    }

    // Create the executor service for controlling the page rendering across multiple threads.
    ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

    for (int siteIndex = 0; siteIndex < getLinkGenerator().getStoreSites().length; siteIndex++) {
      StoreSiteConfiguration currentSite = getLinkGenerator().getStoreSites()[siteIndex];

      for (int mdexEngine = 0; mdexEngine < currentSite.getSiteMDEXEngines().size(); mdexEngine++) {
        MDEXEngine engine = currentSite.getSiteMDEXEngines().get(mdexEngine);
        List<List<String>> siteLinksList = engine.getSiteLinksList();

        for (List<String> links : siteLinksList) {
          executor.
            execute(new PageRenderer(getHttpServerUrl(), links, getSeoPageRepository(), getRenderer()));
        }
      }
    }

    executor.shutdown();
  }
  
  /**
   * @return
   *   Returns server http url.
   */
  public String getHttpServerUrl() {
    StringBuilder httpServerUrl = new StringBuilder(HTTP_SCHEME);
    httpServerUrl.append("://")
      .append(getSiteServerName())
      .append(":")
      .append(getSiteServerPort());
    return httpServerUrl.toString();
  }
}
