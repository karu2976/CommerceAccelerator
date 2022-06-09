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

package atg.endeca.assembler.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;

import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.assembler.event.request.RequestEvent;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.endeca.infront.navigation.NavigationException;


public class SEOCanonicalLinkBuilderTest {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/test/java/atg/endeca/assembler/event/SEOCanonicalLinkBuilderTest.java#1 $$Change: 1385662 $";


  /** The SEOCanonicalLinkBuilder component to be tested. */
  public static SEOCanonicalLinkBuilder mSEOCanonicalLinkBuilder;

  /** The SiteRepository component to be used in the tests. */
  private static GSARepository mSiteRepository = null;

  /** The SiteContextManager component to be used in the tests. */
  private static SiteContextManager mSiteContextManager = null;
  
  /** Nucleus variable */
  private static Nucleus mNucleus = null;

  private final String csaDotCom = "csa.com";
  
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
  public static void setUp() throws ServletException, RepositoryException, SiteContextException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] {"CommerceAccelerator.Base", "CommerceAccelerator.Plugins.SEO"},
        SEOCanonicalLinkBuilder.class, "SEOCanonicalLinkBuilderTest",
        "/atg/endeca/assembler/event/CanonicalLinkBuilder");

		// Resolve request/session scoped components.
		// Create the current request to be used in this test class.
		DynamoHttpServletRequest request = setUpCurrentRequest();

		// Set up UserAgentDetector.
		mSEOCanonicalLinkBuilder = (SEOCanonicalLinkBuilder)
				request.resolveName("/atg/endeca/assembler/event/CanonicalLinkBuilder", true);

		assertNotNull(mSEOCanonicalLinkBuilder);

    mSiteRepository = (GSARepository) mNucleus.resolveName("/atg/multisite/SiteRepository", true);
    String[] siteDataFileNames = { "sites.xml" };
    TemplateParser.loadTemplateFiles(mSiteRepository, 1, siteDataFileNames,
            true, new PrintWriter(System.out), null);

    assertNotNull(mSiteRepository);

    mSiteContextManager =
            (SiteContextManager) mNucleus.resolveName("/atg/multisite/SiteContextManager", true);

    assertNotNull(mSiteContextManager);

    RepositoryItem storeSiteUS = mSiteRepository.getItem("storeSiteUS");
    SiteContext sc = mSiteContextManager.getSiteContext(storeSiteUS.getRepositoryId());
    mSiteContextManager.pushSiteContext(sc);
 
  }

  /**
   * Clean up and shut down Nucleus.
   * 
   * @throws IOException 
   * @throws ServiceException 
   */
  @AfterClass
  public static void tearDown() throws ServiceException, IOException {
		mSEOCanonicalLinkBuilder = null;
    if (mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
    }
  }
  
  //---------------------------------------------------------------------------
  // TEST METHODS                          
  //---------------------------------------------------------------------------
      
  /**
   * This method tests that all the getters/setters are working. It also ensures
   * that the SEOCanonicalLinkBuilder methods set the correct data.
   * 
   * @throws IOException 
   */
  @Test
  public void testHandler() throws IOException {
    mSEOCanonicalLinkBuilder.setSiteServerName(csaDotCom);
    mSEOCanonicalLinkBuilder.setSiteServerPort(80);
    assertEquals(mSEOCanonicalLinkBuilder.getSiteServerName(), csaDotCom);
    assertEquals(mSEOCanonicalLinkBuilder.getSiteServerPort(), 80);
  }

	/**
	 * Ensure that query parameters are removed from any URLs passed to the method.
	 */
	@Test
	public void testRemoveParametersFromUrl() {

		String url = mSEOCanonicalLinkBuilder.removeParametersFromUrl("csa.com?Nr=12345");
		assertEquals(url, csaDotCom);

    url = mSEOCanonicalLinkBuilder.removeParametersFromUrl(csaDotCom);
    assertEquals(url, csaDotCom);

	}

  /**
   * Test to make sure that the canonical link is produced as expected for Urls containing a
   * Navigation Action.
   */
  @Test
  public void testHandleAssemblerRequestEventForNavigation() {

    // Create a ContentItem and add a NavigationAction.
    ContentItem contentItem = createTestContentItem();
    Map<String, String> canonicalLink = new HashMap<>();
    canonicalLink.put("navigationState", "?Nf=product.priceListPair%3AsalePrices_listPrices%2Cproduct.siteId%3AstoreSiteUS%29");
    contentItem.put("canonicalLink", canonicalLink);

    mSEOCanonicalLinkBuilder.setSiteServerName(csaDotCom);
    mSEOCanonicalLinkBuilder.setSiteServerPort(80);
    mSEOCanonicalLinkBuilder.handleAssemblerRequestEvent(createTestRequestEvent(), contentItem);

    assertEquals(contentItem.get("canonical"), "http://csa.com:80/csa/storeus/home");
  }

  /**
   * Test to make sure that the canonical link is produced as expected for Urls containing a
   * Record Action.
   */
  @Test
  public void testHandleAssemblerRequestEventForRecord() {

    // Create a ContentItem and add a RecordAction.
    ContentItem contentItem = createTestContentItem();
    Map<String, String> canonicalLink = new HashMap<>();
    canonicalLink.put("recordState", "/Leather-Jacket/_/A-clothing-sku-sku40153..prod20019.masterCatalog.en__US.salePrices__listPrices");
    canonicalLink.put("@class", "com.endeca.infront.cartridge.model.RecordAction");
    contentItem.put("canonicalLink", canonicalLink);

    mSEOCanonicalLinkBuilder.setSiteServerName(csaDotCom);
    mSEOCanonicalLinkBuilder.setSiteServerPort(80);
    mSEOCanonicalLinkBuilder.handleAssemblerRequestEvent(createTestRequestEvent(), contentItem);

    assertEquals(contentItem.get("canonical"), "http://csa.com:80/csa/storeus/home");
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
   * Create a ContentItem for the test.
   *
   * @return ContentItem
   */
  public ContentItem createTestContentItem() {
    ContentItem contentItem = new BasicContentItem();
    contentItem.put("@type", "PageSlot");
    contentItem.put("atg:currentSiteProductionURL", "/csa/storeus");
    contentItem.put("endeca:contentPath", "/home");

    return contentItem;
  }

  /**
   * Create a RequestEvent for the test.
   *
   * @return ContentItem
   */
  public RequestEvent createTestRequestEvent() {
    RequestEvent requestEvent = new RequestEvent();
    requestEvent.put("@type", "AssemblerRequestEvent");
    requestEvent.put("endeca:contentPath", "/home");
    requestEvent.put("endeca:RootPath", "/pages");
    requestEvent.put("endeca:requestId", "1");
    return requestEvent;
  }
  
}
