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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/test/java/atg/repository/seo/PageRendererTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class PageRendererTest {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/test/java/atg/repository/seo/PageRendererTest.java#1 $$Change: 1385662 $";


  /** The PageRenderer component to be tested. */
  public static PageRenderer mPageRenderer;

  /** The SEOPage repository used in the tests */
  public static GSARepository mSEOPageRepository;

  /** Nucleus variable */
  private static Nucleus mNucleus = null;

  /** The mock markup returned by the MockMarkupRenderer */
  public final static String mMockMarkup = "<p> Mock Markup post test </p>";

  /** The mock url prefix */
  public final static String mMockUrlPrefix = "http://csaServer:8080";


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
        PageRendererTest.class,
        "PageRendererTest",
        "/atg/Initial");

    // Resolve request/session scoped components.
    // Create the current request to be used in this test class.
    DynamoHttpServletRequest request = setUpCurrentRequest();

    // Initialize the page repository and import the sample data.
    mSEOPageRepository = (GSARepository) request.resolveName("/atg/seo/SEOPageRepository", true);
    assertNotNull(mSEOPageRepository);
    String[] dataFileNames = { "page.xml" };
    TemplateParser.loadTemplateFiles(mSEOPageRepository, 1, dataFileNames, true,
        new PrintWriter(System.out), null);

    mPageRenderer = new PageRenderer(mMockUrlPrefix, new ArrayList<String>(), mSEOPageRepository, new MockMarkupRenderer());
    assertNotNull(mPageRenderer);
  }

  /**
   * Clean up and shut down Nucleus.
   *
   * @throws IOException
   * @throws ServiceException
   */
  @AfterClass
  public static void tearDown() throws ServiceException, IOException {
    mPageRenderer = null;
    mSEOPageRepository = null;

    if (mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
    }
  }

  //---------------------------------------------------------------------------
  // TEST METHODS
  //---------------------------------------------------------------------------

  /**
   * This method tests the getter and setter for the Url Prefix property.
   */
  @Test
  public void testGetSetUrlPrefix() {
    mPageRenderer.setUrlPrefix(null);
    assertNull(mPageRenderer.getUrlPrefix());
    mPageRenderer.setUrlPrefix(mMockUrlPrefix);
  }

  /**
   * This method tests the getter and setter for the links property.
   */
  @Test
  public void testGetSetLinks() {
    mPageRenderer.setLinks(null);
    assertNull(mPageRenderer.getLinks());
    mPageRenderer.setLinks(new ArrayList<String>());
  }

  /**
   * This method tests the getter and setter for the Repository property.
   */
  @Test
  public void testGetSetSEOPageRepository() {
    mPageRenderer.setRepository(null);
    mPageRenderer.setRepository(mSEOPageRepository);
    GSARepository seoPage = (GSARepository) mPageRenderer.getRepository();
    assertEquals("SEOPageRepository", seoPage.getName());
  }

  /**
   * This method tests the getter and setter for the SeoPageItemDescriptor property.
   */
  @Test
  public void testGetSetSeoPageItemDescriptor() {
    assertEquals("SEOPage", mPageRenderer.getSeoPageItemDescriptor());
    mPageRenderer.setSeoPageItemDescriptor(null);
    assertNull(mPageRenderer.getSeoPageItemDescriptor());
    mPageRenderer.setSeoPageItemDescriptor("SEOPage");
  }

  /**
   * This method tests the getter and setter for the SeoPageNameProperty property.
   */
  @Test
  public void testGetSetSeoPageNameProperty() {
    assertEquals("name", mPageRenderer.getSeoPageNamePropertyName());
    mPageRenderer.setSeoPageNamePropertyName(null);
    assertNull(mPageRenderer.getSeoPageNamePropertyName());
    mPageRenderer.setSeoPageNamePropertyName("name");
  }

  /**
   * This method tests the getter and setter for the SeoPageContentProperty property.
   */
  @Test
  public void testGetSetSeoPageContentProperty() {
    assertEquals("content", mPageRenderer.getSeoPageContentPropertyName());
    mPageRenderer.setSeoPageContentPropertyName(null);
    assertNull(mPageRenderer.getSeoPageContentPropertyName());
    mPageRenderer.setSeoPageContentPropertyName("content");
  }

  /**
   * This method tests the getter and setter for the Repository property.
   */
  @Test
  public void testGetSetRenderer() {
    mPageRenderer.setRenderer(null);
    mPageRenderer.setRenderer(new MockMarkupRenderer());
    assertThat(mPageRenderer.getRenderer(), instanceOf(PhantomjsRenderer.class));
  }

  /**
   * This method tests the run method.
   */
  @Test
  public void testRun() throws RepositoryException {
    String homeUri = "/csa/home";
    String browseUri = "/csa/browse";

    // Create a list of links for the test.
    List<String> links = new ArrayList<>();
    links.add(homeUri);
    links.add(browseUri);

    // Set the links and invoke the run method.
    mPageRenderer.setLinks(links);
    mPageRenderer.run();

    RepositoryItem seoHomePage = mSEOPageRepository.getItem(homeUri, mPageRenderer.getSeoPageItemDescriptor());
    assertEquals(homeUri, seoHomePage.getPropertyValue(mPageRenderer.getSeoPageNamePropertyName()));
    assertEquals(mMockMarkup, seoHomePage.getPropertyValue(mPageRenderer.getSeoPageContentPropertyName()));

    RepositoryItem seoBrowsePage = mSEOPageRepository.getItem(browseUri, mPageRenderer.getSeoPageItemDescriptor());
    assertEquals(browseUri, seoBrowsePage.getPropertyValue(mPageRenderer.getSeoPageNamePropertyName()));
    assertEquals(mMockMarkup, seoBrowsePage.getPropertyValue(mPageRenderer.getSeoPageContentPropertyName()));
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
   * Mock markup renderer which is an extension of PhantomjsRenderer.
   */
  public static class MockMarkupRenderer extends PhantomjsRenderer {

    @Override
    public PhantomJSDriver newWebDriver() {
      return null;
    }

    @Override
    public String getHtmlContent(URL pRequestUrl, PhantomJSDriver pPhantomDriver) throws MalformedURLException {
      return mMockMarkup;
    }
  }

}
