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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;

import com.endeca.navigation.AggrERec;
import com.endeca.navigation.CSAMockDataFactory;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.ERec;
import com.endeca.navigation.ERecList;
import com.endeca.navigation.MockAggrERec;
import com.endeca.navigation.MockAggrERecList;
import com.endeca.navigation.MockDimensionList;
import com.endeca.navigation.MockENEQueryResults;
import com.endeca.navigation.MockERec;
import com.endeca.navigation.MockERecList;
import com.endeca.navigation.MockESearchReports;
import com.endeca.navigation.MockNavigation;
import com.endeca.navigation.MockProperty;
import com.endeca.navigation.MockPropertyMap;
import com.endeca.soleng.sitemap.SitemapQuery;
import com.endeca.soleng.sitemap.SitemapTemplate;
import com.endeca.soleng.urlformatter.AggrERecUrlParam;
import com.endeca.soleng.urlformatter.ERecUrlParam;
import com.endeca.soleng.urlformatter.NavStateUrlParam;
import com.endeca.soleng.urlformatter.UrlFormatException;
import com.endeca.soleng.urlformatter.UrlFormatter;
import com.endeca.soleng.urlformatter.UrlParam;
import com.endeca.soleng.urlformatter.UrlState;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.endeca.infront.navigation.NavigationException;


public class SiteLinksGeneratorTest {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/test/java/atg/endeca/sitemap/SiteLinksGeneratorTest.java#1 $$Change: 1385662 $";

  
  /** Nucleus variable */
  private static Nucleus mNucleus = null;

  /** The SiteLinkGenerator component to be tested */
  private static SiteLinksGenerator mSiteLinksGenerator;

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------  
  
  /**
   * Perform test initialization.
   * 
   * @throws ServletException 
   * @throws NavigationException 
   */
  @BeforeClass
  public static void setUp() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] {"DAS","CommerceAccelerator.Plugins.SEO"},
      SiteLinksGeneratorTest.class,
      "SiteLinksGeneratorTest",
      "/atg/endeca/sitemap/SiteLinksGenerator");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    mSiteLinksGenerator =
        (SiteLinksGenerator) request.resolveName("/atg/endeca/sitemap/SiteLinksGenerator", true);
    assertNotNull(mSiteLinksGenerator);
  }

  /**
   * Clean up and shut down Nucleus.
   * 
   * @throws IOException 
   * @throws ServiceException 
   */
  @AfterClass
  public static void tearDown() throws ServiceException, IOException {
    mSiteLinksGenerator = null;

    if (mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
    }
  }
  
  //---------------------------------------------------------------------------
  // TEST METHODS                          
  //---------------------------------------------------------------------------
      
  /**
   * This method tests that all the getters/setters are working. It also ensures
   * that the SiteLinksGenerator methods set the correct data.
   */
  @Test
  public void testSiteLinksGeneratorConfiguration() {

    assertEquals(mSiteLinksGenerator.getQueryFields().size(), 2);
    assertEquals(mSiteLinksGenerator.getQueryFields().size(), 2);
    assertEquals(mSiteLinksGenerator.getLinksPerThread(), 100);
    assertEquals(mSiteLinksGenerator.getDetailLinkFormat(),
      "**SITEBASEURL**/product**FORMATTED_URL|HtmlEscape**");
    assertEquals(mSiteLinksGenerator.getNavigationLinkFormat(),
      "**SITEBASEURL**/browse**FORMATTED_URL|HtmlEscape**");
    assertEquals(mSiteLinksGenerator.getUrlEncoding(), "UTF-8");
    assertEquals(mSiteLinksGenerator.getStoreSites().length, 2);
  }

  /**
   * Test method for testing the getter and setter methods for the isUseLanguageInSiteBaseURL property.
   */
  @Test
  public void testGetSetIsUseLanguageInSiteBaseURL() {
    mSiteLinksGenerator.setUseLanguageInSiteBaseURL(false);
    assertFalse(mSiteLinksGenerator.isUseLanguageInSiteBaseURL());
    mSiteLinksGenerator.setUseLanguageInSiteBaseURL(true);
    assertTrue(mSiteLinksGenerator.isUseLanguageInSiteBaseURL());
  }

  /**
   * Test method for testing the getter and setter methods for the SiteMpTemplate property.
   */
  @Test
  public void testGetSiteMapTemplate() {
    assertNotNull(mSiteLinksGenerator.getSitemapTemplate());
    assertThat(mSiteLinksGenerator.getSitemapTemplate(), instanceOf(SitemapTemplate.class));
  }

  /**
   * Test method for testing the getter and setter methods for the NavigationPageSpecList property.
   */
  @Test
  public void testGetNavigationPageSpecList() {
    assertTrue(mSiteLinksGenerator.getNavigationPageSpecList() instanceof NavigationPageSpec[]);
  }

  /**
   * Test method for testing the getter and setter methods for the QueryFieldList property.
   */
  @Test
  public void testGetQueryFieldList() {
    assertEquals(2, mSiteLinksGenerator.getQueryFieldList().size());
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
