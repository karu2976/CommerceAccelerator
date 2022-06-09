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

package atg.servlet.pipeline;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.seo.MarkupRenderer;
import atg.servlet.BrowserTyper;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/test/java/atg/servlet/pipeline/SEOPipelineServletTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class SEOPipelineServletTest {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/test/java/atg/servlet/pipeline/SEOPipelineServletTest.java#1 $$Change: 1385662 $";

  /** The SEOCanonicalLinkBuilder component to be tested. */
  public static SEOPipelineServlet mSEOPiplineServlet;

  /** The SEOPage repository used in the tests */
  public static GSARepository mSEOPageRepository;

  /** Nucleus variable */
  private static Nucleus mNucleus = null;

  /** The mock markup returned by the MockMarkupRenderer */
  public final static String mMockMarkup = "<p> Mock Markup for /csa/home </p>";

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
        new String[] {"CommerceAccelerator.Base", "CommerceAccelerator.Plugins.SEO"},
        SEOPipelineServletTest.class,
        "SEOPipelineServletTest",
        "/atg/Initial");

    // Resolve request/session scoped components.
    // Create the current request to be used in this test class.
    DynamoHttpServletRequest request = setUpCurrentRequest();

    mSEOPiplineServlet = (SEOPipelineServlet) request.resolveName("/atg/dynamo/servlet/dafpipeline/SEOPipelineServlet");
    assertNotNull(mSEOPiplineServlet);

    // Initialize the page repository and import the sample data.
    mSEOPageRepository = (GSARepository) request.resolveName("/atg/seo/SEOPageRepository", true);
    assertNotNull(mSEOPageRepository);
    String[] dataFileNames = { "page.xml" };
    TemplateParser.loadTemplateFiles(mSEOPageRepository, 1, dataFileNames, true,
        new PrintWriter(System.out), null);
  }

  /**
   * Clean up and shut down Nucleus.
   *
   * @throws IOException
   * @throws ServiceException
   */
  @AfterClass
  public static void tearDown() throws ServiceException, IOException {
    mSEOPiplineServlet = null;
    mSEOPageRepository = null;

    if (mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
    }
  }

  //---------------------------------------------------------------------------
  // TEST METHODS
  //---------------------------------------------------------------------------

  /**
   * This method tests the getter and setter for the SEOPageRepository property.
   */
  @Test
  public void testGetSetSEOPageRepository() {
    GSARepository seoPage = (GSARepository) mSEOPiplineServlet.getSeoPageRepository();
    assertEquals(seoPage.getName(), "SEOPageRepository");
  }

  /**
   * This method tests the getter and setter for the BrowserType property.
   */
  @Test
  public void testGetSetBrowserType() {
    mSEOPiplineServlet.setBrowserTypeName(null);
    assertNull(mSEOPiplineServlet.getBrowserTypeName());
    mSEOPiplineServlet.setBrowserTypeName("robot");
  }

  /**
   * This method tests the getter and setter for the SEOPageContentPropertyName property.
   */
  @Test
  public void testGetSetContentPropertyName() {
    mSEOPiplineServlet.setSEOPageContentPropertyName(null);
    assertNull(mSEOPiplineServlet.getSEOPageContentPropertyName());
    mSEOPiplineServlet.setSEOPageContentPropertyName("content");
  }

  /**
   * This method tests the getter and setter for the SEOPageContentPropertyName property.
   */
  @Test
  public void testGetSetNamePropertyName() {
    mSEOPiplineServlet.setSEOPageNamePropertyName(null);
    assertNull(mSEOPiplineServlet.getSEOPageNamePropertyName());
    mSEOPiplineServlet.setSEOPageNamePropertyName("name");
  }

  /**
   * This method tests the getter and setter for the SEOPageItemDescriptor property.
   */
  @Test
  public void testGetSetPageItemDescriptor() {
    mSEOPiplineServlet.setSEOPageItemDescriptor(null);
    assertNull(mSEOPiplineServlet.getSEOPageItemDescriptor());
    mSEOPiplineServlet.setSEOPageItemDescriptor("SEOPage");
  }

  /**
   * This method tests the getter and setter for the MarkupRenderer property.
   */
  @Test
  public void testGetSetMarkupRenderer() {
    assertThat(mSEOPiplineServlet.getMarkupRenderer(), instanceOf(MarkupRenderer.class));
  }

  /**
   * This method tests the isSearchBotRequest method
   */
  @Test
  public void testIsSearchBotRequest() {
    assertFalse(mSEOPiplineServlet.isSearchBotRequest(setUpCurrentRequest()));
  }

  /**
   * This method tests the service method for a page that has not yet been cached.
   *
   * @throws IOException
   */
  @Test
  public void testService1() throws IOException, ServletException, RepositoryException {

    String homeUri = "/csa/home";
    DynamoHttpServletRequest request = setUpCurrentRequest();
    request.setRequestURI(homeUri);
    request.setBrowserTyper(new MockBrowserTyper());

    DynamoHttpServletResponse response = setUpCurrentResponse();

    mSEOPiplineServlet.setMarkupRenderer(new MockMarkupRenderer());
    mSEOPiplineServlet.service(request, response);

    RepositoryItem seoPage = mSEOPageRepository.getItem(homeUri, mSEOPiplineServlet.getSEOPageItemDescriptor());
    assertEquals(homeUri, seoPage.getPropertyValue(mSEOPiplineServlet.getSEOPageNamePropertyName()));
    assertEquals(mMockMarkup, seoPage.getPropertyValue(mSEOPiplineServlet.getSEOPageContentPropertyName()));

  }

  /**
   * This method tests the service method for a page that has been cached.
   *
   * @throws IOException
   */
  @Test
  public void testService2() throws IOException, ServletException, RepositoryException {

    String browseUri = "/csa/browse";
    DynamoHttpServletRequest request = setUpCurrentRequest();
    request.setRequestURI(browseUri);
    request.setBrowserTyper(new MockBrowserTyper());

    DynamoHttpServletResponse response = setUpCurrentResponse();

    mSEOPiplineServlet.setMarkupRenderer(new MockMarkupRenderer());
    mSEOPiplineServlet.service(request, response);

    // "<p> Mock Markup for /csa/browse </p>"
    assertTrue(response.isWriterUsed());
    assertEquals(200, response.getStatus());
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
   * Create a response to be used in our tests.
   *
   * @return response
   */
  public static DynamoHttpServletResponse setUpCurrentResponse() {
    ServletTestUtils utils = new ServletTestUtils();
    return utils.createDynamoHttpServletResponse();
  }

  /**
   * Mock BroserTyper that will always return true for the isBrowserType method.  Allows use to access the SEO
   * functionality without adding a robot USER-AGENT header to the request.
   */
  public class MockBrowserTyper extends BrowserTyper {

      @Override
      public boolean isBrowserType(String pType, DynamoHttpServletRequest pRequest) {
        return true;
      }
    }

    /**
     * Mock markup renderer.
     */
    public class MockMarkupRenderer implements MarkupRenderer {

      @Override
    public String getHtmlContent(URL pRequestUrl) throws MalformedURLException {
      return mMockMarkup;
    }
  }
}
