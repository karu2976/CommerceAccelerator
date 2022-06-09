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

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.endeca.sitemap.MDEXEngine;
import atg.endeca.sitemap.SiteLinksGenerator;
import atg.endeca.sitemap.StoreSiteConfiguration;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/test/java/atg/repository/seo/SitemapPageCacheRendererTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class SitemapPageCacheRendererTest {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/test/java/atg/repository/seo/SitemapPageCacheRendererTest.java#1 $$Change: 1385662 $";


  /** The SitemapPageCacheRenderer component to be tested. */
  public static SitemapPageCacheRenderer mSitemapPageCacheRenderer;

  /** The SEOPage repository used in the tests */
  public static GSARepository mSEOPageRepository;

  /** Site repository */
  private static GSARepository mSiteRepository = null;

  /** The Test Store Configuration used in the tests */
  public static StoreSiteConfiguration mTestStoreSiteConfiguration;

  /** Nucleus variable */
  private static Nucleus mNucleus = null;

  /** The mock markup returned by the MockMarkupRenderer */
  public final static String mMockMarkup = "<p> Mock Markup for test </p>";

  /** The mock url for the test */
  public final static String mMockUrl = "/csa/home";

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Perform test initialization.
   *
   * @throws ServletException
   */
  @BeforeClass
  public static void setUp() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
        new String[]{"CommerceAccelerator.Base", "CommerceAccelerator.Plugins.SEO"},
        SitemapPageCacheRendererTest.class,
        "SitemapPageCacheRendererTest",
        "/atg/Initial");

    // Resolve request/session scoped components.
    // Create the current request to be used in this test class.
    DynamoHttpServletRequest request = setUpCurrentRequest();

    mSitemapPageCacheRenderer =
        (SitemapPageCacheRenderer) request.resolveName("/atg/repository/seo/SitemapPageCacheRenderer");
    assertNotNull(mSitemapPageCacheRenderer);

    // Initialize the page repository and import the sample data.
    mSEOPageRepository = (GSARepository) request.resolveName("/atg/seo/SEOPageRepository", true);
    assertNotNull(mSEOPageRepository);

    // Initialize repository and import sample data
    mSiteRepository = (GSARepository)
        request.resolveName("/atg/multisite/SiteRepository", true);
    assertNotNull(mSiteRepository);
    String[] dataFileNames = {"sites.xml"};
    TemplateParser.loadTemplateFiles(mSiteRepository,
        1, dataFileNames, true, new PrintWriter(System.out), null);

    // Initialize the test stroe configuration.
    mTestStoreSiteConfiguration =
        (StoreSiteConfiguration) request.resolveName("/atg/endeca/sitemap/TestStoreConfiguration", true);
    assertNotNull(mTestStoreSiteConfiguration);

    // Add mock links to the engine
    MDEXEngine engine = mTestStoreSiteConfiguration.getSiteMDEXEngines().get(0);
    assertNotNull(engine);

    List<String> links = new ArrayList<>();
    links.add(mMockUrl);

    List<List<String>> siteLinks = new ArrayList<>();
    siteLinks.add(links);

    engine.setSiteLinksList(siteLinks);
  }

  /**
   * Clean up and shut down Nucleus.
   *
   * @throws IOException
   * @throws ServiceException
   */
  @AfterClass
  public static void tearDown() throws ServiceException, IOException {
    mSitemapPageCacheRenderer = null;
    mSEOPageRepository = null;

    if (mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
    }
  }

  //---------------------------------------------------------------------------
  // TEST METHODS
  //---------------------------------------------------------------------------

  /**
   * This method tests the static HTTP_SCHEME property.
   */
  @Test
  public void testHttpScheme() {
    assertEquals("http", SitemapPageCacheRenderer.HTTP_SCHEME);
  }

  /**
   * This method tests the getter and setter for the SiteLinksGenerator property.
   */
  @Test
  public void testGetSetSiteLinksGenerator() {
    mSitemapPageCacheRenderer.setLinkGenerator(null);
    assertNull(mSitemapPageCacheRenderer.getLinkGenerator());
  }

  /**
   * This method tests the getter and setter for the SEOPageRepository property.
   */
  @Test
  public void testGetSetSEOPageRepository() {
    GSARepository seoPageRepository = (GSARepository) mSitemapPageCacheRenderer.getSeoPageRepository();
    assertEquals("SEOPageRepository", seoPageRepository.getName());
  }

  /**
   * This method tests the getter and setter for the Renderer property.
   */
  @Test
  public void testGetSetRenderer() {
    mSitemapPageCacheRenderer.setLinkGenerator(null);
    assertNull(mSitemapPageCacheRenderer.getLinkGenerator());
  }

  /**
   * This method tests the getter and setter for the SiteServerName property.
   */
  @Test
  public void testGetSetSiteServerName() {
    mSitemapPageCacheRenderer.setSiteServerName(null);
    assertNull(mSitemapPageCacheRenderer.getSiteServerName());
  }

  /**
   * This method tests the getter and setter for the SiteServerPort property.
   */
  @Test
  public void testGetSetSiteServerPort() {
    mSitemapPageCacheRenderer.setSiteServerPort(8080);
    assertEquals(8080, mSitemapPageCacheRenderer.getSiteServerPort());
  }

  /**
   * This method tests the getter and setter for the MaxThreadPoolSize property.
   */
  @Test
  public void testGetSetMaxThreadPoolSize() {
    mSitemapPageCacheRenderer.setMaxThreadPoolSize(0);
    assertEquals(0, mSitemapPageCacheRenderer.getMaxThreadPoolSize());
  }

  /**
   * This method tests that the getHttpServerUrl() method correctly generates the server URL.
   */
  @Test
  public void testGetHttpServerUrl() {
    mSitemapPageCacheRenderer.setSiteServerName("localhost");
    mSitemapPageCacheRenderer.setSiteServerPort(8080);
    assertEquals("http://localhost:8080", mSitemapPageCacheRenderer.getHttpServerUrl());
  }

  /**
   * This method tests GeneratePageCache() method.
   */
  @Test
  public void testGeneratePageCache() throws RepositoryException, InterruptedException, IOException {

    StoreSiteConfiguration[] siteConfigArray = new StoreSiteConfiguration[1];
    siteConfigArray[0] = mTestStoreSiteConfiguration;
    MockSiteLinksGenerator mockSiteLinksGenerator = new MockSiteLinksGenerator();
    mockSiteLinksGenerator.setStoreSites(siteConfigArray);

    mSitemapPageCacheRenderer.setRenderer(new MockMarkupRenderer());
    mSitemapPageCacheRenderer.setSeoPageRepository(mSEOPageRepository);
    mSitemapPageCacheRenderer.setLinkGenerator(mockSiteLinksGenerator);
    mSitemapPageCacheRenderer.setMaxThreadPoolSize(1);

    // Execute the cache renderer.
    mSitemapPageCacheRenderer.generatePageCache();

    // Sleep to give the other thread a chance to complete.
    TimeUnit.SECONDS.sleep(5);

    // Assert.
    RepositoryItem seoPage = mSEOPageRepository.getItem(mMockUrl, "SEOPage");
    assertEquals(mMockUrl, seoPage.getPropertyValue("name"));
    assertEquals(mMockMarkup, seoPage.getPropertyValue("content"));

  }

  //---------------------------------------------------------------------------
  // UTILITY METHODS
  //---------------------------------------------------------------------------

  /**
   * Create a request to be used in our tests.
   *
   * @return request
   */
  public static DynamoHttpServletRequest setUpCurrentRequest() {
    ServletTestUtils utils = new ServletTestUtils();
    HashMap<String, String> params = new HashMap<>();
    DynamoHttpServletRequest request =
        utils.createDynamoHttpServletRequestForSession(mNucleus, params, 1024, "GET", null);
    ServletUtil.setCurrentRequest(request);

    return request;
  }

  /**
   * Mock SiteLinksGenerator to ensure the runSiteMapGenerator method does nothing.
   */
  private class MockSiteLinksGenerator extends SiteLinksGenerator {

    @Override
    public void runSiteMapGenerator() {
    }
  }

  /**
   * Mock markup renderer which is an extension of PhantomjsRenderer.  This
   * ensures a constant String of mock markup is returned each time the
   * getHtmlContent method is invoked.
   */
  private static class MockMarkupRenderer extends PhantomjsRenderer {

    @Override
    public PhantomJSDriver newWebDriver() {
      return null;
    }

    @Override
    public String getRenderCompleteScript() {
      return "return true;";
    }

    @Override
    public String getHtmlContent(URL pRequestUrl, PhantomJSDriver pPhantomDriver) throws MalformedURLException {
      return mMockMarkup;
    }
  }
}
