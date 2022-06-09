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

package atg.projects.store.catalog;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.collections.validator.CollectionObjectValidator;
import atg.service.collections.validator.StartEndDateValidator;
import atg.service.util.CurrentDate;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * This unit test will test the methods of the CatalogNavigationService class.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/catalog/CatalogNavigationServiceTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class CatalogNavigationServiceTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/catalog/CatalogNavigationServiceTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  private static CatalogNavigationService mCatalogNavigationService = null;
  private static GSARepository mProductCatalog = null;
  private static DynamoHttpServletRequest mRequest = null;
  private static Nucleus mNucleus = null;
  private static CollectionObjectValidator[] mValidators = new CollectionObjectValidator[1];

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the CatalogNavigationService instance to be used in this test.
   *
   * @throws Exception When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException, RepositoryException, PropertyNotFoundException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[]{"CommerceAccelerator.Base"},
      CatalogNavigationServiceTest.class,
      "CatalogNavigationServiceTest",
      "/atg/Initial");

    mRequest = setUpCurrentRequest();

    // Initialize repository and import sample data
    mProductCatalog =
      (GSARepository) mRequest.resolveName("/atg/commerce/catalog/ProductCatalog", true);

    String[] dataFileNames = {"catalog.xml"};

    TemplateParser.loadTemplateFiles(
      mProductCatalog, 1, dataFileNames, true, new PrintWriter(System.out), null);

    mCatalogNavigationService = (CatalogNavigationService)
      mRequest.resolveName("/atg/store/catalog/CatalogNavigation", true);

    assertNotNull(mCatalogNavigationService);

    setUpCurrentProfile();
    setCurrentCatalog((RepositoryItem) mProductCatalog.getItem("masterCatalog"));

    // Create a StartEndDateValidator and set it's currentDate, startDatePropertyName
    // and endDatePropertyName properties.

    mValidators[0] = (StartEndDateValidator)
      mRequest.resolveName("/atg/store/collections/validator/StartEndDateValidator");

    ((StartEndDateValidator)mValidators[0]).setCurrentDate(
      (CurrentDate) mRequest.resolveName("/atg/dynamo/service/CurrentDate"));

    ((StartEndDateValidator)mValidators[0]).setEndDatePropertyName("startDate");
    ((StartEndDateValidator)mValidators[0]).setEndDatePropertyName("endDate");
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

    mCatalogNavigationService = null;
    mProductCatalog = null;
    mRequest = null;
    mValidators = null;
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  /**
   * Ensure that validateCategory returns true when the validators property is null.
   */
  @Test
  public void testValidateCategoryNullValidatorsProperty() {
    mCatalogNavigationService.setValidators(null);
    assertTrue(mCatalogNavigationService.validateCategory("catMenValidStartDate"));
  }

  /**
   * Ensure that validateCategory returns true when the validators property is an empty array.
   */
  @Test
  public void testValidateCategoryEmptyValidatorsProperty() {
    mCatalogNavigationService.setValidators(new CollectionObjectValidator[0]);
    assertTrue(mCatalogNavigationService.validateCategory("catMenValidStartDate"));
  }

  /**
   * Ensure that validateCategory returns true when a category's startDate is valid.
   */
  @Test
  public void testValidateCategoryValidStartDate() {
    mCatalogNavigationService.setValidators(mValidators);
    assertTrue(mCatalogNavigationService.validateCategory("catMenValidStartDate"));
  }

  /**
   * Ensure that validateCategory returns false when a category's startDate is invalid.
   */
  @Test
  public void testValidateCategoryInvalidStartDate() {
    mCatalogNavigationService.setValidators(mValidators);
    assertFalse(mCatalogNavigationService.validateCategory("catMenInvalidStartDate"));
  }

  /**
   * Ensure that when a category with a valid startDate and no ancestor categories sets
   * that category to the top-level category and the ancestor category ArrayList is empty.
   */
  @Test
  public void testNavigateNoAncestorsValidTopLevelCatStartDate() {
    mCatalogNavigationService.clear();
    mCatalogNavigationService.setValidators(mValidators);
    mCatalogNavigationService.navigate("catMenValidStartDate", null);

    assertEquals("catMenValidStartDate", mCatalogNavigationService.getTopLevelCategory());
    assertTrue(mCatalogNavigationService.getAncestorCategories().size() == 0);
    assertEquals("catMenValidStartDate", mCatalogNavigationService.getCurrentCategory());
    assertTrue(mCatalogNavigationService.isCurrentCategoryValid());
    assertEquals("catMenValidStartDate", mCatalogNavigationService.getCategoryNavigationPath().get(0));
  }

  /**
   * Ensure that when a category with a valid startDate and a single ancestor category sets
   * the current category and category navigation path to that category. It should also set
   * that category's ancestor to the top-level category.
   */
  @Test
  public void testNavigateOneAncestorValidTopLevelCatStartDate() {
    mCatalogNavigationService.clear();
    mCatalogNavigationService.setValidators(mValidators);

    List<String> ancestors = new ArrayList<String>(){{
      add("catMenValidStartDate");
    }};

    mCatalogNavigationService.navigate("catMenShirtValidStartDate", ancestors);

    assertEquals("catMenValidStartDate", mCatalogNavigationService.getTopLevelCategory());
    assertTrue(mCatalogNavigationService.getAncestorCategories().size() == 1);
    assertEquals("catMenValidStartDate", mCatalogNavigationService.getAncestorCategories().get(0));
    assertTrue(mCatalogNavigationService.isCurrentCategoryValid());
    assertEquals("catMenShirtValidStartDate", mCatalogNavigationService.getCurrentCategory());
    assertEquals("catMenValidStartDate", mCatalogNavigationService.getCategoryNavigationPath().get(0));
  }

  /**
   * Ensure that when a category with a invalid startDate and a single ancestor category sets
   * the currentCategoryValid property to false.
   */
  @Test
  public void testNavigateOneAncestorInvalidSubCatStartDate() {
    mCatalogNavigationService.clear();
    mCatalogNavigationService.setValidators(mValidators);

    List<String> ancestors = new ArrayList<String>(){{
      add("catMenInvalidStartDate");
    }};

    mCatalogNavigationService.navigate("catMenShirtInvalidStartDate", ancestors);
    assertFalse(mCatalogNavigationService.isCurrentCategoryValid());
  }

  /**
   * Ensure that the clear method resets the appropriate properties.
   */
  @Test
  public void testClear() {
    mCatalogNavigationService.setCategoryNavigationPath(
      new ArrayList<String>(){{
        add("testCategory");
      }}
    );

    mCatalogNavigationService.setAncestorCategories(
      new ArrayList<String>(){{
        add("testAncestorCategory");
      }}
    );

    mCatalogNavigationService.setCurrentCategory("testCategory");
    mCatalogNavigationService.setTopLevelCategory("testCategory");

    mCatalogNavigationService.clear();
    assertEquals("", mCatalogNavigationService.getCurrentCategory());
    assertEquals("", mCatalogNavigationService.getTopLevelCategory());
    assertTrue(mCatalogNavigationService.getAncestorCategories().size() == 0);
    assertTrue(mCatalogNavigationService.getCategoryNavigationPath().size() == 0);
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
    DynamoHttpServletRequest request = utils.createDynamoHttpServletRequestForSession(mNucleus, null, null);
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