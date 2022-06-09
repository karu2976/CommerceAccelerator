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

import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/test/java/atg/repository/seo/PhantomjsRendererTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class PhantomjsRendererTest {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/test/java/atg/repository/seo/PhantomjsRendererTest.java#1 $$Change: 1385662 $";


  /** The SEOCanonicalLinkBuilder component to be tested. */
  public static PhantomjsRenderer mPhantomjsRenderer;

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
        new String[]{"CommerceAccelerator.Base", "CommerceAccelerator.Plugins.SEO"},
        PhantomjsRendererTest.class,
        "PhantomjsRendererTest",
        "/atg/Initial");

    // Resolve request/session scoped components.
    // Create the current request to be used in this test class.
    DynamoHttpServletRequest request = setUpCurrentRequest();

    mPhantomjsRenderer = (PhantomjsRenderer) request.resolveName("/atg/repository/seo/PhantomjsRenderer");
    assertNotNull(mPhantomjsRenderer);
  }

  /**
   * Clean up and shut down Nucleus.
   *
   * @throws IOException
   * @throws ServiceException
   */
  @AfterClass
  public static void tearDown() throws IOException, ServiceException {
    mPhantomjsRenderer = null;

    if (mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
    }
  }

  //---------------------------------------------------------------------------
  // TEST METHODS
  //---------------------------------------------------------------------------

  /**
   * This method tests the getter and setter for the WaitTime property.
   */
  @Test
  public void testGetSetWaitTime() {
    mPhantomjsRenderer.setWaitTime(2000);
    assertEquals(2000, mPhantomjsRenderer.getWaitTime());
  }

  /**
   * This method tests the getter and setter for the PoleInterval property.
   */
  @Test
  public void testGetSetPoleInterval() {
    assertEquals(2500, mPhantomjsRenderer.getPoleInterval());
    mPhantomjsRenderer.setPoleInterval(1000);
    assertEquals(1000, mPhantomjsRenderer.getPoleInterval());
  }

  /**
   * This method tests the getter and setter for the RenderCompleteScript property.
   */
  @Test
  public void testGetSetRenderCompleteScript() {
    mPhantomjsRenderer.setRenderCompleteScript("return true;");
    assertEquals("return true;", mPhantomjsRenderer.getRenderCompleteScript());
  }

  /**
   * This method tests the getter and setter for the PhantomExecutablePath property.
   */
  @Test
  public void testGetSetPhantomExecutablePath() {
    assertNull(mPhantomjsRenderer.getPhantomExecutablePath());
    mPhantomjsRenderer.setPhantomExecutablePath("C:\\");
    assertEquals("C:\\", mPhantomjsRenderer.getPhantomExecutablePath());
  }

  /**
   * This method expects an exception to be thrown as phantomJS must be installed
   * on the machine that this test is executed on in order to work.
   */
  @Test (expected = IllegalStateException.class)
  public void testNewWebDriver() {
    PhantomJSDriver driver = mPhantomjsRenderer.newWebDriver();
    assertNull(driver);
  }

  /**
   * This method expects an exception to be thrown as phantomJS must be installed
   * on the machine that this test is executed on in order to work.
   */
  @Test (expected = IllegalStateException.class)
  public void testGetHtmlContent1() throws MalformedURLException {
    URL url = new URL("http://csaServer:8080/csa/home");
    String pageSource = mPhantomjsRenderer.getHtmlContent(url);
    assertNull(pageSource);
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
}
