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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;

import org.junit.*;

import com.endeca.infront.navigation.NavigationException;

public class StoreSiteConfigurationTest {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/test/java/atg/endeca/sitemap/StoreSiteConfigurationTest.java#1 $$Change: 1385662 $";


  /**
   * Nucleus variable
   */
  private static Nucleus mNucleus = null;

  /**
   * Site repository
   */
  private static GSARepository mSiteRepository = null;

  /**
   * Store site configuration object
   */
  private static StoreSiteConfiguration mStoreSiteConfiguration = null;

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
            new String[]{"CommerceAccelerator.Base",
                    "CommerceAccelerator.Plugins.SEO"},
            StoreSiteConfigurationTest.class,
            "StoreSiteConfigurationTest",
            "/atg/initial");

    // Set up a request
    DynamoHttpServletRequest request = setUpCurrentRequest();

    // Initialize repository and import sample data
    mSiteRepository = (GSARepository)
            request.resolveName("/atg/multisite/SiteRepository", true);
    String[] dataFileNames = {"sites.xml"};
    TemplateParser.loadTemplateFiles(mSiteRepository,
            1, dataFileNames, true, new PrintWriter(System.out), null);

    assertNotNull(mSiteRepository);

    // initialize the StoreSiteConfiguration component
    mStoreSiteConfiguration = (StoreSiteConfiguration)
            request.resolveName("/atg/endeca/sitemap/StoreUSConfiguration", true);

    assertNotNull(mStoreSiteConfiguration);

  }

  /**
   * Clean up and shut down Nucleus.
   *
   * @throws IOException
   * @throws ServiceException
   */
  @AfterClass
  public static void tearDown() throws ServiceException, IOException {
    mSiteRepository = null;
    mStoreSiteConfiguration = null;
    if (mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
    }
  }

  //---------------------------------------------------------------------------
  // TEST METHODS                          
  //---------------------------------------------------------------------------

  /**
   * Test the general Store Configuration
   *
   * @throws IOException Throws exception if any io issues.
   */
  @Test
  public void testStoreConfiguration() throws ServiceException {

    // ensure the properties that can be retrieved from the repository are null
    mStoreSiteConfiguration.setSupportedLanguages(null);
    mStoreSiteConfiguration.setDefaultCountryCode(null);
    mStoreSiteConfiguration.setSiteBaseUrl(null);

    // re-initialize the component
    mStoreSiteConfiguration.doStartService();

    // now test the default configuration
    assertEquals("US", mStoreSiteConfiguration.getDefaultCountryCode());
    assertEquals("/csa/storeus", mStoreSiteConfiguration.getSiteBaseUrl());
    assertEquals("storeSiteUS", mStoreSiteConfiguration.getSiteId());
    assertEquals(2, mStoreSiteConfiguration.getSupportedLanguages().size());
    assertEquals(2, mStoreSiteConfiguration.getSiteMDEXEngines().size());
  }

  /**
   * Test MDEX Engine objects for the store are created as expected
   *
   * @throws IOException Throws exception if any io issues.
   */
  @Test
  public void testStoreMDEXEngines() throws ServiceException {

    List<String> supportedLanguages = new ArrayList<>();
    supportedLanguages.add("en");

    mStoreSiteConfiguration.setSupportedLanguages(supportedLanguages);
    mStoreSiteConfiguration.setDefaultCountryCode("ES");
    mStoreSiteConfiguration.setSiteBaseUrl("csa/test/base/url");

    // re-initialize the component
    mStoreSiteConfiguration.doStartService();

    MDEXEngine mdexEngine = mStoreSiteConfiguration.getSiteMDEXEngines().get(0);
    assertEquals("N=0&Nr=AND(product.priceListPair:salePrices_listPrices,product.siteId:storeSiteUS)",
            mdexEngine.getRootQuery());

    assertEquals("ES", mStoreSiteConfiguration.getDefaultCountryCode());
    assertEquals("csa/test/base/url", mStoreSiteConfiguration.getSiteBaseUrl());
    assertEquals(1, mStoreSiteConfiguration.getSupportedLanguages().size());
    assertEquals(1, mStoreSiteConfiguration.getSiteMDEXEngines().size());
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
    DynamoHttpServletRequest request =
            utils.createDynamoHttpServletRequestForSession(mNucleus, "mySessionId", "new");
    ServletUtil.setCurrentRequest(request);

    return request;
  }

}
