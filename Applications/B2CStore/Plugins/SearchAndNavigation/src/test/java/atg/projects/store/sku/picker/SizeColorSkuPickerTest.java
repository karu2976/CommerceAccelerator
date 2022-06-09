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

package atg.projects.store.sku.picker;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import atg.projects.store.catalog.StoreCatalogTools;

import javax.servlet.ServletException;

public class SizeColorSkuPickerTest {

  //----------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/sku/picker/SizeColorSkuPickerTest.java#1 $$Change: 1385662 $";
  
  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------
  
  /** The SizeColorSkuPicker that will be tested */
  private static SizeColorSkuPicker mSizeColorSkuPickerTest = null;

  /** The StoreCatalogTools that will be tested */
  private static StoreCatalogTools mStoreCatalogTools = null;
  
  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;
  
  /** The Catalog Repository for the test */
  private static GSARepository mProductCatalog = null;
  
  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------
  
  private static final String TEST = "test";
  
  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------
  
  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the SizeColorSkuPicker instance to be used in this test.
   * 
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base",
                     "CommerceAccelerator.Applications.B2CStore.Base",
                     "CommerceAccelerator.Plugins.SearchAndNavigation",
                     "CommerceAccelerator.Applications.B2CStore.Plugins.SearchAndNavigation"},
      SizeColorSkuPickerTest.class,
      "SizeColorSkuPickerTest",
      "/atg/Initial");
    
    // Initialize catalog repository and import sample data
    mProductCatalog = (GSARepository) 
        mNucleus.resolveName("/atg/commerce/catalog/ProductCatalog", true);
    String[] dataFileNames = { "catalog.xml" };
    TemplateParser.loadTemplateFiles(mProductCatalog, 1, dataFileNames, true, 
        new PrintWriter(System.out), null);

    // Set up StoreCatalogTools for the tests.
    mStoreCatalogTools = (StoreCatalogTools)
      mNucleus.resolveName("/atg/commerce/catalog/CatalogTools", true);
    
  }

  /**
   * Re-initialize the SizeColorSkuPicker component
   */
  @Before
  public void setUpBeforeMethod() {

    DynamoHttpServletRequest request = setUpCurrentRequest();
    
    // Set up SizeColorSkuPicker for the tests.
    mSizeColorSkuPickerTest = (SizeColorSkuPicker) 
      request.resolveName("/atg/commerce/product/detail/SizeColorSkuPicker", true);
    
    assertNotNull(mSizeColorSkuPickerTest);
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
    
    mSizeColorSkuPickerTest = null;
  }
  
  /**
   * Ensure the instance of SizeColorSkuPicker has been cleared
   */
  @After
  public void tearDownAfterMethod() {
    
    mSizeColorSkuPickerTest = null;
  }
  
  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------
  
  //------------------------------------
  // Test the class setters and getters
  //------------------------------------
  
  @Test
  public void testGetSizePropertyName() {
    
    assertEquals("size", mSizeColorSkuPickerTest.getSizePropertyName());
  }
  
  @Test
  public void testSetSizePropertyName() {
    
    mSizeColorSkuPickerTest.setSizePropertyName(TEST);
    assertEquals(TEST, mSizeColorSkuPickerTest.getSizePropertyName());
  }

  @Test
  public void testGetAvailableSizesPropertyName() {

    assertEquals("availableSizes",
      mSizeColorSkuPickerTest.getAvailableSizesPropertyName());
  }

  @Test
  public void testSetAvailableSizesPropertyName() {

    mSizeColorSkuPickerTest.setAvailableSizesPropertyName(TEST);
    assertEquals(TEST, mSizeColorSkuPickerTest.getAvailableSizesPropertyName());
  }
  
  @Test
  public void testGetSelectedSizePropertyName() {
    
    assertEquals("selectedSize", mSizeColorSkuPickerTest.getSelectedSizePropertyName());
  }
  
  @Test
  public void testSetSelectedSizePropertyName() {
    
    mSizeColorSkuPickerTest.setSelectedSizePropertyName(TEST);
    assertEquals(TEST, mSizeColorSkuPickerTest.getSelectedSizePropertyName());
  }

  @Test
  public void testGetHelperPropertyBlackList() {

    List<String> expectedResult = new ArrayList<>();
    expectedResult.add("One Size");

    assertEquals(expectedResult, mSizeColorSkuPickerTest.getHelperPropertyBlackList());
  }

  @Test
  public void testSetHelperPropertyBlackList() {

    mSizeColorSkuPickerTest.setHelperPropertyBlackList(null);
    assertEquals(null, mSizeColorSkuPickerTest.getHelperPropertyBlackList());

    List<String> blackList = new ArrayList<>();
    blackList.add("One Size");

    mSizeColorSkuPickerTest.setHelperPropertyBlackList(blackList);
  }

  @Test
  public void testGetSelectedOption() throws RepositoryException {
    
    Map<String, String> pOptions = new HashMap<>();
    pOptions.put("selectedSize", "XXL");
   
    // If a selected size is passed in the pOptions map then it is what should be returned.
    assertEquals("XXL", mSizeColorSkuPickerTest.getSelectedOption(pOptions));
    
    // If a product with multiple child skus is passed, each with a unique size then null should be
    // returned
    pOptions.clear();
    pOptions.put("productIdPropertyName", "testProduct1");
    assertNull(mSizeColorSkuPickerTest.getSelectedOption(pOptions));
    
    // If a product with a single child sku is passed, and it has a size then that size should be
    // returned
    pOptions.clear();
    pOptions.put("productIdPropertyName", "testProduct2");
    assertEquals("L", mSizeColorSkuPickerTest.getSelectedOption(pOptions));
  }
  
  @Test
  public void testGetAllOptions() throws RepositoryException {
    
    Map<String, String> pOptions = new HashMap<>();
    pOptions.put("productIdPropertyName", "testProduct1");
    
    // If a product with multiple child skus is passed, each with a unique size then null should be
    // returned   
    List<String> allOptions = 
        (List<String>) mSizeColorSkuPickerTest.getAllOptions(pOptions);
    
    // While testProduct1 has 4 child SKUs two of them have the same value for the size property.  
    // Therefore we expect a list of size 3 to be returned.
    assertEquals(3, allOptions.size());
    
    // Ensure the returned list is sorted as expected
    assertEquals("S", allOptions.get(0));
    assertEquals("M", allOptions.get(1));
    assertEquals("XL", allOptions.get(2));
  }
  
  @Test
  public void testGetSelectedSku() throws RepositoryException {

    Map<String, String> pOptions = new HashMap<>();
    RepositoryItem selectedSku = null;
    RepositoryItem expectedResult = null;
    
    // As the selectedSize and selectedColor properties have not been passed we expect null
    // to be returned.
    pOptions.put("productIdPropertyName", "testProduct1");
    
    assertEquals(null,  mSizeColorSkuPickerTest.getSelectedSku(pOptions));
    
    // If the passed selectedColor does not exist for the passed product then null should be
    // returned 
    pOptions.put("selectedColor", "pink");
    pOptions.put("selectedSize", "XXXS");
    
    assertEquals(null, mSizeColorSkuPickerTest.getSelectedSku(pOptions));
    
    // If the passed selectedColor and selectedSize do exist then the details of the matching SKU 
    // should be returned 
    pOptions.clear();
    pOptions.put("productIdPropertyName", "testProduct1");
    pOptions.put("selectedColor", "green");
    pOptions.put("selectedSize", "M");
    
    selectedSku = mSizeColorSkuPickerTest.getSelectedSku(pOptions);
    
    expectedResult = mStoreCatalogTools.findSKU("testSku2");
    
    assertEquals(expectedResult, selectedSku);
    
    // If the passed selectedColor exists but there are no sizes for the child SKUs then the details
    // of the matching SKU should be returned by the super class using only the color to find the 
    // match
    pOptions.clear();
    pOptions.put("productIdPropertyName", "testProduct3");
    pOptions.put("selectedColor", "purple");
    
    selectedSku = mSizeColorSkuPickerTest.getSelectedSku(pOptions);

    expectedResult = mStoreCatalogTools.findSKU("testSku7");
    
    assertEquals(expectedResult, selectedSku);
    
    // If the passed selectedSize exists but there are no colors for the child SKUs then the details
    // of the matching SKU should be found using only the size and returned 
    pOptions.clear();
    pOptions.put("productIdPropertyName", "testProduct4");
    pOptions.put("selectedSize", "XL");
    
    selectedSku = mSizeColorSkuPickerTest.getSelectedSku(pOptions);

    expectedResult = mStoreCatalogTools.findSKU("testSku8");
    
    assertEquals(expectedResult, selectedSku);
    
    // If the passed selectedSize  does not exist an there are no colors for the child SKUs then the 
    // null should be returned
    pOptions.put("productIdPropertyName", "testProduct4");
    pOptions.put("selectedSize", "XXXXXXXXXXL");
    
    selectedSku = mSizeColorSkuPickerTest.getSelectedSku(pOptions);

    assertEquals(null, selectedSku);
  }

  @Test
  public void testGetHelperInformation() throws RepositoryException {

    // Test with a single SKU product that only has a single 'One Size' size option.  This should
    // return an empty map.
    Map<String, String> pOptions = new HashMap<>();
    pOptions.put("productIdPropertyName", "testProduct5");

    // Passing null for either of the getHelperInformation arguments should result in null
    // being returned.
    assertNull(mSizeColorSkuPickerTest.getHelperInformation("size", null));

    Map<String, String> expectedResult = new HashMap<>();

    assertEquals(expectedResult,
      mSizeColorSkuPickerTest.getHelperInformation("size", pOptions));

    // Passing a product that has only a single size that is not present in the black list should
    // result in valid helper information being returned
    pOptions.clear();
    pOptions.put("productIdPropertyName", "testProduct2");

    expectedResult.put("type", "sizeChart");
    expectedResult.put("contentUrl", "SizeChart.html");

    assertEquals(expectedResult,
      mSizeColorSkuPickerTest.getHelperInformation("size", pOptions));

    // Test with a multi SKU product that has multiple different size options.  This should return a
    // valid helper information map.
    pOptions.clear();
    pOptions.put("productIdPropertyName", "testProduct1");

    assertEquals(expectedResult,
      mSizeColorSkuPickerTest.getHelperInformation("size", pOptions));
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
