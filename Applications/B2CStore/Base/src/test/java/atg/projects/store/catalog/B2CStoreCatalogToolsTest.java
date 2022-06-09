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

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import atg.nucleus.ServiceException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;

import javax.servlet.ServletException;

public class B2CStoreCatalogToolsTest {

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------
  
  public final static String[] NEW_SORT_ORDER = {"S","M","L","XL","XXL"};
  
  public final static String[] SIZE_SORT_ARRAY = {"One Size","Small","Large","XS","S","M","L","XL",
    "XXL","0","1","2", "3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18",
    "19","20","21", "22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37",
    "38","39", "40","41","42"};
  
  //----------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Base/src/test/java/atg/projects/store/catalog/B2CStoreCatalogToolsTest.java#1 $$Change: 1385662 $";
  
  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------
  
  /** The StoreInventoryManager that will be tested */
  private static B2CStoreCatalogTools mStoreCatalogTools = null;
  
  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;
  
  /** The Inventory Repository for the test */
  private static GSARepository mProductCatalog = null;
  
  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------
  
  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the StoreInventoryManager instance to be used in this test.
   * 
   * @throws ServletException
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base", 
                     "CommerceAccelerator.Applications.B2CStore.Base"},
      B2CStoreCatalogToolsTest.class,
      "B2CStoreCatalogToolsTest",
      "/atg/Initial");

    // Initialize inventory repository and import sample data
    mProductCatalog = (GSARepository) 
        mNucleus.resolveName("/atg/commerce/catalog/ProductCatalog", true);
    String[] dataFileNames = { "catalog.xml" };
    TemplateParser.loadTemplateFiles(mProductCatalog, 1, dataFileNames, true, 
        new PrintWriter(System.out), null);

  }
  
  /**
   * Re-initialize the StoreCatalogProperties component
   */
  @Before
  public void setUpBeforeMethod() {

    DynamoHttpServletRequest request = setUpCurrentRequest();
    
    // Set up StoreInventoryManager for the tests.
    mStoreCatalogTools = (B2CStoreCatalogTools) 
      request.resolveName("/atg/commerce/catalog/CatalogTools", true);  
    
    assertNotNull(mStoreCatalogTools);
  }

  //----------------------------------------------------------------------------
  // TEAR DOWN
  //----------------------------------------------------------------------------
  
  /**
   * Ensure Nucleus is shutdown properly and perform general clean-up of member variables.
   * 
   * @throws IOException, ServiceException
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
  
  /**
   * Ensure the instance of B2CStoreCatalogTools has been cleared
   */
  @After
  public void tearDownAfterMethod() {
    
    mStoreCatalogTools = null;
  }
  
  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------
  
  //------------------------
  // Test the class methods
  //------------------------
  
  /**
   * Tests the getSortOrder methods
   */
  @Test
  public void testGetSortOrder() {
    
    assertArrayEquals(SIZE_SORT_ARRAY, mStoreCatalogTools.getSizeSortOrder());
  }
  
  /**
   * Tests the setSortOrder method
   */
  @Test
  public void testSetSortOrder() {
    
    mStoreCatalogTools.setSizeSortOrder(NEW_SORT_ORDER);
    assertArrayEquals(NEW_SORT_ORDER, mStoreCatalogTools.getSizeSortOrder());
  }
  
  /**
   * Tests the getSortedSizes, getPossibleSizes & sortSizes methods
   * 
   * @throws RepositoryException
   */
  @Test
  public void testGetSortedSizes() throws RepositoryException {
    
    // Get the test product
    RepositoryItem product = mStoreCatalogTools.findProduct("testProduct1");
    
    List<RepositoryItem> childSkus = 
      (List<RepositoryItem>) mStoreCatalogTools.getProductChildSkus(product);
    
    // Check that we have the expected number of child SKUs
    assertEquals(4, childSkus.size());
    
    // Get the possible values from catalog tools
    List<String> possibleSizes = mStoreCatalogTools.getSortedSizes(childSkus);
    
    // Build a list of the expected results
    List<String> expectedResult = new ArrayList<>();
    expectedResult.add("S");
    expectedResult.add("M");
    expectedResult.add("XXL");
    
    // While there are 4 child SKUs there are only 3 valid sizes so the returned list should only 
    // have a length of 3
    assertEquals(3, possibleSizes.size());
    
    // Compare the expected result with the possibleValues returned
    assertEquals(expectedResult, possibleSizes);
    
    // Ensure the resulting list has been sorted correctly.
    assertEquals(expectedResult.get(0), possibleSizes.get(0));
    assertEquals(expectedResult.get(1), possibleSizes.get(1));
    assertEquals(expectedResult.get(2), possibleSizes.get(2));
  }
  
  /**
   * Tests the sortColors methods
   */
  @Test
  public void testSortColors() {
    
    // Build a list of the expected results
    List<String> listToSort = new ArrayList<>();
    listToSort.add("Purple");
    listToSort.add("Green");
    listToSort.add("Red");
    
    // Get the possible values from catalog tools
    List<String> sortedColors = mStoreCatalogTools.sortColors(listToSort);
    
    // Build a list of the expected results
    List<String> expectedResult = new ArrayList<>();
    expectedResult.add("Green");
    expectedResult.add("Purple");
    expectedResult.add("Red");    
    
    // Ensure the resulting list has been sorted correctly.
    assertEquals(expectedResult.get(0), sortedColors.get(0));
    assertEquals(expectedResult.get(1), sortedColors.get(1));
    assertEquals(expectedResult.get(2), sortedColors.get(2));
  }
  
  /**
   * Tests the sortSizes method with a null size sort order
   */
  @Test
  public void testSortSizeNullSortOrder() {
    
    // Build a list of the expected results
    List<String> listToSort = new ArrayList<>();
    listToSort.add("Purple");
    listToSort.add("Green");
    listToSort.add("Red");
    
    // Set the sort order to null to ensure that branch of the logic is covered by the test.
    mStoreCatalogTools.setSizeSortOrder(null);
    
    // Get the possible values from catalog tools
    List<String> sortedSizes = mStoreCatalogTools.sortSizes(listToSort);  
    
    // Ensure the resulting list has been sorted correctly.
    assertEquals(listToSort.get(0), sortedSizes.get(0));
    assertEquals(listToSort.get(1), sortedSizes.get(1));
    assertEquals(listToSort.get(2), sortedSizes.get(2));

  }
  
  /**
   * Tests the sortSizes method with an empty size sort order
   */
  @Test
  public void testSortSizesEmptySortOrder() {
    
    // Build a list of the expected results
    List<String> listToSort = new ArrayList<>();
    listToSort.add("Purple");
    listToSort.add("Green");
    listToSort.add("Red");
    
    // Set the sort order to an empty array to ensure that branch of the logic is covered by 
    // the test.
    mStoreCatalogTools.setSizeSortOrder(new String[] {});
    
    // Get the possible values from catalog tools
    List<String> sortedSizes = mStoreCatalogTools.sortSizes(listToSort);  
    
    // Ensure the resulting list has been sorted correctly.
    assertEquals(listToSort.get(0), sortedSizes.get(0));
    assertEquals(listToSort.get(1), sortedSizes.get(1));
    assertEquals(listToSort.get(2), sortedSizes.get(2));

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
