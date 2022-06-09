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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import atg.multisite.SiteContextException;
import atg.nucleus.ServiceException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import javax.servlet.ServletException;

public class StoreCatalogToolsTest {

//----------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/catalog/StoreCatalogToolsTest.java#1 $$Change: 1385662 $";
  
  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------
  
  /** The StoreCatalogTools that will be tested */
  private static StoreCatalogTools mStoreCatalogTools = null;
  
  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;
  
  /** The Product Repository for the test */
  private static GSARepository mProductCatalog = null;
  
  private static GSARepository mSiteRepository = null;
  private static SiteContextManager mSiteContextManager = null;
  
  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------
  
  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the StoreCatalogTools instance to be used in this test.
   * 
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException, RepositoryException, SiteContextException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base"},
      StoreCatalogToolsTest.class,
      "StoreCatalogToolsTest",
      "/atg/Initial");
    
    // Set up StoreCatalogTools for the tests.
    mStoreCatalogTools = (StoreCatalogTools)
      mNucleus.resolveName("/atg/commerce/catalog/CatalogTools", true);
    
    assertNotNull(mStoreCatalogTools);
    
    // Initialize inventory repository and import sample data
    mProductCatalog = (GSARepository) 
        mNucleus.resolveName("/atg/commerce/catalog/ProductCatalog", true);
    String[] dataFileNames = { "catalog.xml" };
    TemplateParser.loadTemplateFiles(mProductCatalog, 1, dataFileNames, true, 
        new PrintWriter(System.out), null);
    
    mSiteRepository = (GSARepository) mNucleus.resolveName("/atg/multisite/SiteRepository", true);
    String[] siteDataFileNames = { "sites.xml" };
    TemplateParser.loadTemplateFiles(mSiteRepository, 1, siteDataFileNames,
      true, new PrintWriter(System.out), null);

    RepositoryItem storeSiteUS = mSiteRepository.getItem("storeSiteUS");

    mSiteContextManager =
      (SiteContextManager) mNucleus.resolveName("/atg/multisite/SiteContextManager", true);

    SiteContext sc = mSiteContextManager.getSiteContext(storeSiteUS.getRepositoryId());
    mSiteContextManager.pushSiteContext(sc);
  }

  //----------------------------------------------------------------------------
  // TEAR DOWN
  //----------------------------------------------------------------------------
  
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
    
    mStoreCatalogTools = null;
    mProductCatalog = null;
  }
  
  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------
  
  //------------------------
  // Test the class methods
  //------------------------
  
  /**
   * Tests the findProduct, getProductChildSkus & getPossibleValuesForSkus methods
   * 
   * @throws RepositoryException
   */
  @Test
  public void testGetProductChildSkus() throws RepositoryException {
    
    // Get the test product
    RepositoryItem product = mStoreCatalogTools.findProduct("testProduct1");
    
    List<RepositoryItem> childSkus = 
      (List<RepositoryItem>) mStoreCatalogTools.getProductChildSkus(product);
    
    // Check that we have the expected number of child SKUs
    assertEquals(4, childSkus.size());
    
    // Get the possible values from catalog tools
    List<String> possibleValues = mStoreCatalogTools.getPossibleValuesForSkus(childSkus, "color");
    
    // Build a list of the expected results
    List<String> expectedResult = new ArrayList<>();
    expectedResult.add("red");
    expectedResult.add("yellow");
    expectedResult.add("blue");
    
    // Compare the expected result with the possibleValues returned
    assertEquals(expectedResult, possibleValues);
    
    // Test for an empty list of skus being passed
    List<String> emptyResult = 
        mStoreCatalogTools.getPossibleValuesForSkus(new ArrayList<RepositoryItem>(), "color");
    
    // Compare the expected result with the possibleValues returned
    assertEquals(new ArrayList<String>(), emptyResult);
    
  }
  
  /**
   * Testing the sortStrings method
   */
  @Test
  public void testSortStrings() {
    
    // Defined the sort order
    String[] sortOrder = {"3", "12", "4", "2", "0", "1", "5"};  
    
    // Create the list to be sorted
    List<String> listToSort = new ArrayList<>();
    listToSort.add("0");
    listToSort.add("1");
    listToSort.add("2");
    listToSort.add("3");
    listToSort.add("4");
    listToSort.add("5");
    
    // Create the list of the expected result
    List<String> expectedResult = new ArrayList<>();
    expectedResult.add("3");
    expectedResult.add("4");
    expectedResult.add("2");
    expectedResult.add("0");
    expectedResult.add("1");
    expectedResult.add("5");
    
    // Call the sortStrings method
    List<String> sortedList = mStoreCatalogTools.sortStrings(listToSort, sortOrder);
    
    // Compare the expected result with the sorted list returned
    assertEquals(expectedResult, sortedList);
  }
  
  /**
   * Ensure that a category item can be retrieved by its ID.
   */
  @Test
  public void testGetExistingCategory() throws RepositoryException {
    assertNotNull(mStoreCatalogTools.getCategory("catMenValidStartDate"));
  }

  /**
   * Ensure that a null category ID returns null.
   */
  @Test
  public void testGetNullCategory() throws RepositoryException {
    assertNull(mStoreCatalogTools.getCategory(null));
  }

  /**
   * Ensure that null is returned when a category ID doesn't exist.
   */
  @Test
  public void testGetNonExistentCategory() throws RepositoryException {
    assertNull(mStoreCatalogTools.getCategory("invalidCategoryId"));
  }

  /**
   * Ensure that the category is valid for the current site context.
   */
  @Test
  public void testIsCategoryOnCurrentSite_Valid() throws SiteContextException, RepositoryException {
    assertTrue(mStoreCatalogTools.isCategoryOnCurrentSite("catMenValidStartDate"));
  }

  /**
   * Ensure that the category is not valid for the current site context.
   */
  @Test
  public void testIsCategoryOnCurrentSite_Invalid() throws SiteContextException, RepositoryException {
    assertFalse(mStoreCatalogTools.isCategoryOnCurrentSite("catMenInvalidSite"));
  }
}