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

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.commerce.CommerceException;
import atg.commerce.endeca.cache.DimensionValueCache;
import atg.commerce.order.Order;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingTools;
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

import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;
import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.CartridgeHandlerException;

import com.endeca.infront.assembler.ContentItem;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.runners.MethodSorters;
import org.junit.*;

/**
 * This unit test will test the methods of the CartEditorHandler class.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/ShoppingCart/src/test/java/atg/projects/store/assembler/cartridge/handler/CartEditorHandlerTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CartEditorHandlerTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/ShoppingCart/src/test/java/atg/projects/store/assembler/cartridge/handler/CartEditorHandlerTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  public static CartEditorHandler mCartEditorHandler = null;

  public static BasicContentItem mCartridgeConfig = null;

  public static ProfileTools mProfileTools = null;

  private static final String PROFILE = "/atg/userprofiling/Profile";

  private static final String PROFILE_TOOLS_PATH = "/atg/userprofiling/ProfileTools";

  /** The Nucleus to be used in this test class */
  private static Nucleus mNucleus = null;

  private static SiteContextManager mSiteContextManager = null;
  private static GSARepository mProductCatalog = null;
  private static GSARepository mSiteRepository = null;
  private static GSARepository mOrderRepository = null;
  private static GSARepository mUserRepository = null;

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the CartEditorHandler instance to be used in this test.
   *
   * @throws Exception When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException, RepositoryException, SiteContextException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] {"CommerceAccelerator.Plugins.ShoppingCart"},
      CartEditorHandler.class,
      "CartEditorHandlerTest",
      "/atg/Initial");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    mProfileTools = (ProfileTools) mNucleus.resolveName(PROFILE_TOOLS_PATH, true);

    mUserRepository = (GSARepository) mNucleus.resolveName("/atg/userprofiling/ProfileAdapterRepository", true);
    String[] profileDataFileNames = { "users.xml" };
    TemplateParser.loadTemplateFiles(mUserRepository, 1, profileDataFileNames, true,
      new PrintWriter(System.out), null);

    mSiteRepository = (GSARepository) mNucleus.resolveName("/atg/multisite/SiteRepository", true);
    String[] siteDataFileNames = { "sites.xml" };
    TemplateParser.loadTemplateFiles(mSiteRepository, 1, siteDataFileNames,
      true, new PrintWriter(System.out), null);

    mProductCatalog = (GSARepository) mNucleus.resolveName("/atg/commerce/catalog/ProductCatalog");
    String[] dataFileNames = { "catalog.xml" };
    TemplateParser.loadTemplateFiles(mProductCatalog, 1, dataFileNames,
      true, new PrintWriter(System.out), null);

    mOrderRepository = (GSARepository) mNucleus.resolveName("/atg/commerce/order/OrderRepository", true);
    String[] orderDataFileNames = { "orders.xml" };
    TemplateParser.loadTemplateFiles(mOrderRepository, 1, orderDataFileNames, true, new PrintWriter(System.out), null);

    RepositoryItem storeSiteUS = mSiteRepository.getItem("storeSiteUS");

    mSiteContextManager =
      (SiteContextManager) mNucleus.resolveName("/atg/multisite/SiteContextManager", true);

    SiteContext sc = mSiteContextManager.getSiteContext(storeSiteUS.getRepositoryId());
    mSiteContextManager.pushSiteContext(sc);

    // Set up CartEditorHandler component.
    mCartEditorHandler = (CartEditorHandler)
      request.resolveName("/atg/endeca/assembler/cartridge/handler/CartEditorHandler", true);

    assertNotNull(mCartEditorHandler);

    mCartridgeConfig = new BasicContentItem();

  }

  /**
   * Ensure Nucleus is shutdown properly and perform general clean-up of member variables.
   *
   * @throws Exception When there's a problem shutting down Nucleus.
   */
  @AfterClass
  public static void tearDownAfterClass() throws IOException, ServiceException {
    if (mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
      mNucleus = null;
    }

    mCartEditorHandler = null;
    mCartridgeConfig = null;
    mSiteContextManager = null;
    mProductCatalog = null;
    mSiteRepository = null;
    mOrderRepository = null;
    mUserRepository = null;
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  /**
   * Test that the preprocess and process methods populate a ContentItem
   * with the appropriate record specs for 2 products.
   */
  @Test
  public void test1PreprocessAndProcess() throws CartridgeHandlerException,
      RepositoryException, CommerceException {
    RepositoryItem profileItem = mProfileTools.getItemFromEmail("amy@example.com");
    ServletUtil.setCurrentUserProfile(profileItem);
    Profile profile = (Profile)ServletUtil.getCurrentRequest().resolveName(PROFILE);
    profile.getProfileTools().createNewUser("user", profile);

    mCartEditorHandler.setProfile(profile);
    mCartEditorHandler.getShoppingCart().setProfile(ServletUtil.getCurrentUserProfile());
    mCartEditorHandler.setPricingTools(new PricingTools() {
      /**
       * We don't want an actual pricing operation to be performed in this test.
       */
      @Override
      public void performPricingOperation(String pPricingOperation, Order pOrder,
        PricingModelHolder pPricingModels, Locale pLocale, RepositoryItem pProfile,
          Map pExtraParameters) throws PricingException {
        // Do nothing here.
      }
    });

    mCartEditorHandler.preprocess(mCartridgeConfig);
    ContentItem contentItem = mCartEditorHandler.process(mCartridgeConfig);

    assertEquals("A-prod2", ((Map)contentItem.get("productURLs")).get("xprod2071"));
    assertEquals("A-prod1", ((Map)contentItem.get("productURLs")).get("xprod2073"));
    assertEquals(".", contentItem.get("continueShoppingURL"));
  }

  /**
   * Test that the preprocess and process methods populate a ContentItem
   * with the appropriate record specs for 2 products. Also ensure that
   * when a current category is found, the URL is retrieved from the
   * DimensionValueCache.
   */
  @Test
  public void test2PreprocessAndProcess() throws CartridgeHandlerException {
    RepositoryItem profileItem = mProfileTools.getItemFromEmail("amy@example.com");
    ServletUtil.setCurrentUserProfile(profileItem);
    Profile profile = (Profile)ServletUtil.getCurrentRequest().resolveName(PROFILE);
    profile.getProfileTools().createNewUser("user", profile);

    mCartEditorHandler.setProfile(profile);
    mCartEditorHandler.getShoppingCart().setProfile(ServletUtil.getCurrentUserProfile());
    mCartEditorHandler.setPricingTools(new PricingTools() {
      /**
       * We don't want an actual pricing operation to be performed in this test.
       */
      @Override
      public void performPricingOperation(String pPricingOperation, Order pOrder,
        PricingModelHolder pPricingModels, Locale pLocale, RepositoryItem pProfile,
          Map pExtraParameters) throws PricingException {
        // Do nothing here.
      }
    });

    mCartEditorHandler.getCatalogNavigation().setCurrentCategory("catMen");

    DimensionValueCache dimensionValueCache = new DimensionValueCache();
    dimensionValueCache.put("catMen", "12345", "A-blah", new ArrayList<String>() {{
      add("rootCategory");
    }});
    mCartEditorHandler.getDimensionValueCacheTools().swapCache(dimensionValueCache);

    mCartEditorHandler.preprocess(mCartridgeConfig);
    ContentItem contentItem = mCartEditorHandler.process(mCartridgeConfig);

    assertEquals("A-prod2", ((Map) contentItem.get("productURLs")).get("xprod2071"));
    assertEquals("A-prod1", ((Map) contentItem.get("productURLs")).get("xprod2073"));
    assertEquals("A-blah", contentItem.get("continueShoppingURL"));
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

}