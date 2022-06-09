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

package atg.projects.store.assembler.navigation;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
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

import com.endeca.navigation.MockNavigationState;
import com.endeca.infront.navigation.NavigationState;
import com.endeca.infront.navigation.model.FilterState;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * This unit test will test the methods of the StoreNavigationStateProcessor class.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/assembler/navigation/StoreNavigationStateProcessorTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreNavigationStateProcessorTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/assembler/navigation/StoreNavigationStateProcessorTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  private static StoreNavigationStateProcessor mStoreNavigationStateProcessor = null;
  private static GSARepository mProductCatalog = null;
  private static GSARepository mSiteRepository = null;
  private static SiteContextManager mSiteContextManager = null;
  private static DynamoHttpServletRequest mRequest = null;
  private static Nucleus mNucleus = null;

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the StoreNavigationStateProcessor instance to be used in this test.
   *
   * @throws Exception When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException, RepositoryException, SiteContextException,
    PropertyNotFoundException
  {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[]{"CommerceAccelerator.Base"},
      StoreNavigationStateProcessorTest.class,
      "StoreNavigationStateProcessorTest",
      "/atg/Initial");

    mRequest = setUpCurrentRequest();

    // Initialize repository and import sample data.
    mProductCatalog =
      (GSARepository) mRequest.resolveName("/atg/commerce/catalog/ProductCatalog", true);

    String[] dataFileNames = {"catalog.xml"};

    TemplateParser.loadTemplateFiles(
      mProductCatalog, 1, dataFileNames, true, new PrintWriter(System.out), null);

    mStoreNavigationStateProcessor = (StoreNavigationStateProcessor)

      mRequest.resolveName("/atg/endeca/assembler/cartridge/manager/StoreNavigationStateProcessor",
        true);

    assertNotNull(mStoreNavigationStateProcessor);

    mSiteRepository = (GSARepository) mNucleus.resolveName("/atg/multisite/SiteRepository", true);
    String[] siteDataFileNames = { "sites.xml" };
    TemplateParser.loadTemplateFiles(mSiteRepository, 1, siteDataFileNames,
      true, new PrintWriter(System.out), null);

    RepositoryItem storeSiteUS = mSiteRepository.getItem("storeSiteUS");

    mSiteContextManager =
      (SiteContextManager) mNucleus.resolveName("/atg/multisite/SiteContextManager", true);

    SiteContext sc = mSiteContextManager.getSiteContext(storeSiteUS.getRepositoryId());
    mSiteContextManager.pushSiteContext(sc);

    setUpCurrentProfile();
    setCurrentCatalog((RepositoryItem) mProductCatalog.getItem("masterCatalog"));
  }

  /**
   * Ensure Nucleus is shutdown properly and perform general clean-up of member variables.
   *
   * @throws Exception
   *   When there's a problem shutting down Nucleus.
   */
  @AfterClass
  public static void tearDownAfterClass() throws IOException, ServiceException {
    if (mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
      mNucleus = null;
    }

    mStoreNavigationStateProcessor = null;
    mProductCatalog = null;
    mRequest = null;
    mSiteRepository = null;
    mSiteContextManager = null;
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  /**
   * Test the get/set ignoredRangeFilters property.
   */
  @Test
  public void testGetSetIgnoredRangeFilters() {
    mStoreNavigationStateProcessor.setIgnoredRangeFilters(new ArrayList<String>());
    assertNotNull(mStoreNavigationStateProcessor.getIgnoredRangeFilters());
  }

  /**
   * Ensure the the CatalogNavigation gets updated correctly with a valid category.
   */
  @Test
  public void testUpdateCatalogNavigationValidCategory() {
    mStoreNavigationStateProcessor.updateCatalogNavigation("catMenValidStartDate", null);
    assertTrue(mStoreNavigationStateProcessor.getCatalogNavigation().isCurrentCategoryValid());
    assertEquals("catMenValidStartDate",
      mStoreNavigationStateProcessor.getCatalogNavigation().getTopLevelCategory());
  }

  /**
   * Ensure the the CatalogNavigation gets updated correctly with an invalid category.
   */
  @Test
  public void testUpdateCatalogNavigationInvalidCategory() {
    mStoreNavigationStateProcessor.updateCatalogNavigation("catMenInvalidStartDate", null);
    assertFalse(mStoreNavigationStateProcessor.getCatalogNavigation().isCurrentCategoryValid());
  }

  /**
   * Ensure that a non-existent category clears the CatalogNavigationService's properties.
   */
  @Test
  public void testUpdateCatalogNavigationMissingCategory() {
    mStoreNavigationStateProcessor.getCatalogNavigation().setTopLevelCategory("testCategory");
    mStoreNavigationStateProcessor.updateCatalogNavigation(null, null);
    assertEquals("", mStoreNavigationStateProcessor.getCatalogNavigation().getTopLevelCategory());
  }

  /**
   * Ensure that the process method updates the catalog navigation service with a valid category.
   */
  @Test
  public void testProcessValidDimValMapping() {
    FilterState filterState = new FilterState();
    filterState.getNavigationFilters().add("12345");

    NavigationState navigationState =
      new MockNavigationState(filterState, new HashMap<String,String>());

    mStoreNavigationStateProcessor.getDimensionValueCacheTools().getCache()
      .put("catMenValidStartDate", "12345", "?catMenValidStartDate", new ArrayList<String>());

    mStoreNavigationStateProcessor.process(navigationState);

    assertEquals("catMenValidStartDate",
      mStoreNavigationStateProcessor.getCatalogNavigation().getTopLevelCategory());

    assertTrue(mStoreNavigationStateProcessor.getCatalogNavigation().isCurrentCategoryValid());
  }

  /**
   * Ensure that the process method sets the catalog navigation service's
   * currentCategoryValid property to false when a category's startDate is
   * invalid.
   */
  @Test
  public void testProcessInvalidDimValMapping() {
    FilterState filterState = new FilterState();
    filterState.getNavigationFilters().add("23456");

    NavigationState navigationState =
      new MockNavigationState(filterState, new HashMap<String,String>());

    mStoreNavigationStateProcessor.getDimensionValueCacheTools().getCache()
      .put("catMenInvalidStartDate", "23456", "?catMenInvalidStartDate", new ArrayList<String>());

    mStoreNavigationStateProcessor.process(navigationState);

    assertEquals("catMenInvalidStartDate",
      mStoreNavigationStateProcessor.getCatalogNavigation().getTopLevelCategory());

    assertFalse(mStoreNavigationStateProcessor.getCatalogNavigation().isCurrentCategoryValid());
  }

  //---------------------------------------------------------------------------
  // UTILITY METHODS
  //---------------------------------------------------------------------------

  /**
   * Create a request to be used in our tests.
   *
   * @return
   *   An HTTP request Object
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
   * This method creates an anonymous user in the current servlet request object
   * and attaches it to the Profile component.
   */
  public static void setUpCurrentProfile() {
    ServletTestUtils utils = new ServletTestUtils();
    DynamoHttpServletRequest request =
      utils.createDynamoHttpServletRequestForSession(mNucleus, null, null);
    ServletUtil.setCurrentRequest(request);
    Profile profile = (Profile) request.resolveName("/atg/userprofiling/Profile");
    ServletUtil.setCurrentUserProfile(profile);
    profile.getProfileTools().createNewUser("user", profile);
  }

  /**
   * Sets the current catalog context to the given catalog.
   *
   * @param pCatalog
   *   The catalog to set.
   */
  public static void setCurrentCatalog(RepositoryItem pCatalog)
    throws PropertyNotFoundException {
    Object profile = ServletUtil.getCurrentUserProfile();
    assertNotNull(profile);
    DynamicBeans.setPropertyValue(profile, "catalog", pCatalog);
  }
}