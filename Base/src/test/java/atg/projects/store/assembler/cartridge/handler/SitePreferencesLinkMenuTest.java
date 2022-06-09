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

package atg.projects.store.assembler.cartridge.handler;

import static org.junit.Assert.*;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.adapter.util.RepositoryUtils;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.multisite.SiteManager;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.ServletUtil;
import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * This unit test will test the methods of the SitePreferencesLinkMenu class.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/assembler/cartridge/handler/SitePreferencesLinkMenuTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class SitePreferencesLinkMenuTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/assembler/cartridge/handler/SitePreferencesLinkMenuTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  /** The SitePreferencesLinkMenu that will be tested. */
  private static SitePreferencesLinkMenu mSitePreferencesLinkMenu = null;

  /** The SiteContextManager component to be used in the tests. */
  private static SiteContextManager mSiteContextManager = null;

  /** The SiteRepository component to be used in the tests. */
  private static GSARepository mSiteRepository = null;

  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the SitePreferencesLinkMenu instance to be used in this test.
   *
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base" },
      SitePreferencesLinkMenuTest.class,
      "SitePreferencesLinkMenuTest",
      "/atg/Initial");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    // Set up CategoryNavigationHandler.
    mSitePreferencesLinkMenu = (SitePreferencesLinkMenu)
      request.resolveName("/atg/endeca/assembler/cartridge/handler/SitePreferences", true);

    assertNotNull(mSitePreferencesLinkMenu);

    mSiteRepository = (GSARepository) mNucleus.resolveName("/atg/multisite/SiteRepository", true);
    String[] siteDataFileNames = { "sites.xml" };
    TemplateParser.loadTemplateFiles(mSiteRepository, 1, siteDataFileNames,
      true, new PrintWriter(System.out), null);

    mSiteContextManager =
      (SiteContextManager) mNucleus.resolveName("/atg/multisite/SiteContextManager", true);
  }

  /**
   * Ensure Nucleus is shutdown properly and perform general clean-up of member variables.
   *
   * @throws Exception
   *   When there's a problem shutting down Nucleus.
   */
  @AfterClass
  public static void tearDownAfterClass() throws IOException, ServiceException {
    if(mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
      mNucleus = null;
    }

    mSitePreferencesLinkMenu = null;
    mSiteContextManager = null;
    mSiteRepository = null;
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  /**
   * Test the process method with a single site and no manual menu options set up in component.
   */
  @Test
  public void testProcessWithoutPredefinedMenuOptions1Site() throws RepositoryException,
    SiteContextException, CartridgeHandlerException {

    List<MutableRepositoryItem> sites = createSites(NewSiteCount.One);
    MutableRepositoryItem site = sites.get(0);

    site.setPropertyValue("defaultCountry", "DE");
    site.setPropertyValue("languages",
      new ArrayList(){{
        add("en");
        add("de");
      }}
    );
    site.setPropertyValue("defaultLanguage", "de");

    SiteContext sc = mSiteContextManager.getSiteContext(site.getRepositoryId());
    mSiteContextManager.pushSiteContext(sc);

    ContentItem contentItem = mSitePreferencesLinkMenu.process(new BasicContentItem());
    List<Map<String,Object>> menu =
      (List<Map<String,Object>>) contentItem.get(SitePreferencesLinkMenu.SITE_OPTIONS_NAME);

    assertEquals("Germany", menu.get(0).get("displayName"));
    assertEquals("/csa/storeus/", menu.get(0).get("url"));
    assertEquals("Germany", contentItem.get("currentCountry"));
    assertEquals("English", contentItem.get("currentLanguage"));
  }

  /**
   * Test the process method with 2 sites and no manual menu options set up in component.
   */
  @Test
  public void testProcessWithoutPredefinedMenuOptions2Sites() throws RepositoryException,
    SiteContextException, CartridgeHandlerException {

    List<MutableRepositoryItem> sites = createSites(NewSiteCount.Two);
    MutableRepositoryItem site1 = sites.get(0);

    site1.setPropertyValue("productionURL", "/csa/storede");
    site1.setPropertyValue("defaultCountry", "DE");
    site1.setPropertyValue("languages",
      new ArrayList(){{
        add("en");
        add("de");
      }}
    );
    site1.setPropertyValue("defaultLanguage", "de");

    MutableRepositoryItem site2 = sites.get(1);

    site2.setPropertyValue("defaultCountry", "US");
    site2.setPropertyValue("languages",
      new ArrayList(){{
        add("es");
        add("us");
      }}
    );
    site2.setPropertyValue("defaultLanguage", "en");

    SiteContext sc = mSiteContextManager.getSiteContext(site1.getRepositoryId());
    mSiteContextManager.pushSiteContext(sc);

    ContentItem contentItem = mSitePreferencesLinkMenu.process(new BasicContentItem());

    List<Map<String,Object>> menu =
      (List<Map<String,Object>>) contentItem.get(SitePreferencesLinkMenu.SITE_OPTIONS_NAME);

    assertEquals("Germany", menu.get(0).get("displayName"));
    assertEquals("/csa/storede/", menu.get(0).get("url"));

    assertEquals("United States", menu.get(1).get("displayName"));
    assertEquals("/csa/storeus/", menu.get(1).get("url"));

    assertEquals("Germany", contentItem.get("currentCountry"));
    assertEquals("English", contentItem.get("currentLanguage"));
  }

  /**
   * Test the process method with a single site and some manual menu options set up in component.
   */
  @Test
  public void testProcessWithPredefinedMenuOptions() throws RepositoryException, SiteContextException,
    CartridgeHandlerException {

    List<MutableRepositoryItem> sites = createSites(NewSiteCount.One);
    MutableRepositoryItem site = sites.get(0);

    site.setPropertyValue("defaultCountry", "DE");
    site.setPropertyValue("languages",
      new ArrayList(){{
        add("en");
        add("de");
      }}
    );
    site.setPropertyValue("defaultLanguage", "de");

    SiteContext sc = mSiteContextManager.getSiteContext(site.getRepositoryId());
    mSiteContextManager.pushSiteContext(sc);

    DynamoHttpServletRequest request = setUpCurrentRequest();

    SitePreferencesLinkMenu sitePreferencesLinkMenu = (SitePreferencesLinkMenu)
      request.resolveName("/atg/endeca/assembler/cartridge/handler/TestPreferencesMenu", true);

    ContentItem contentItem = sitePreferencesLinkMenu.process(new BasicContentItem());

    assertEquals("loginLink", ((Map) ((List) contentItem.get("testMenuOptions")).get(0)).get("displayName"));
    assertEquals("loginPath", ((Map) ((List) contentItem.get("testMenuOptions")).get(0)).get("url"));

    List<Map<String,Object>> menu =
      (List<Map<String,Object>>) contentItem.get(SitePreferencesLinkMenu.SITE_OPTIONS_NAME);

    assertEquals("Germany", menu.get(0).get("displayName"));
    assertEquals("/csa/storeus/", menu.get(0).get("url"));
    assertEquals("Germany", contentItem.get("currentCountry"));
    assertEquals("English", contentItem.get("currentLanguage"));
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
      utils.createDynamoHttpServletRequestForSession(
        mNucleus, "mySessionId", "new");
    ServletUtil.setCurrentRequest(request);

    return request;
  }

  /**
   * Create a number of sites to be used in the tests. Any existing sites will be removed
   * from the repository and the SiteContext stack.
   *
   * @param pNumSitesToCreate
   *
   * @return
   */
  public static List<MutableRepositoryItem> createSites(NewSiteCount pNumSitesToCreate)
    throws RepositoryException {

    List<MutableRepositoryItem> sites = new ArrayList<>(2);

    mSiteContextManager.clearSiteContextStack();

    RepositoryItem[] items =
      mSiteRepository.getItems(new String[]{"store1", "store2"});

    for (RepositoryItem item : items) {
      if (item != null) {
        MutableRepositoryItem oldSite = mSiteRepository.getItemForUpdate(item.getRepositoryId());
        RepositoryUtils.removeReferencesToItem(oldSite);
        atg.repository.RepositoryUtils.removeItem(oldSite);
      }
    }

    for (int i = 1; i <= pNumSitesToCreate.count; i++) {
      MutableRepositoryItem newSite = mSiteRepository.createItem("store" + i, "siteConfiguration");
      populateSite(newSite);
      sites.add(newSite);
      mSiteRepository.addItem(newSite);
    }

    return sites;
  }

  /**
   * Populate the passed in site item with some common property values.
   *
   * @param pSite
   *   The site item to populate.
   */
  public static void populateSite(MutableRepositoryItem pSite) {
    pSite.setPropertyValue("name", pSite.getRepositoryId());
    pSite.setPropertyValue("sitePriority", 1);
    pSite.setPropertyValue("enabled", true);
    pSite.setPropertyValue("contextRoot", "/csa");
    pSite.setPropertyValue("productionURL", "/csa/storeus");
  }


  /**
   * enum to be used in the createSites utility method.
   */
  enum NewSiteCount {
    One(1),
    Two(2);

    private final int count;

    NewSiteCount(int pCount) {
      count = pCount;
    }
  }
}
