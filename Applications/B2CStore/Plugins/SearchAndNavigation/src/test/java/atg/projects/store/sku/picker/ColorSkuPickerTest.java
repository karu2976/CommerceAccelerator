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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.PrintWriter;
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

public class ColorSkuPickerTest {

  //----------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/sku/picker/ColorSkuPickerTest.java#1 $$Change: 1385662 $";
  
  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------
  
  /** The ColorSkuPicker that will be tested */
  private static ColorSkuPicker mColorSkuPickerTest = null;

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
   * the ColorSkuPicker instance to be used in this test.
   * 
   * @throws ServletException
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base",
                     "CommerceAccelerator.Applications.B2CStore.Base",
                     "CommerceAccelerator.Plugins.SearchAndNavigation",
                     "CommerceAccelerator.Applications.B2CStore.Plugins.SearchAndNavigation"},
      ColorSkuPickerTest.class,
      "ColorSkuPickerTest",
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
   * Re-initialize the ColorSkuPicker component
   */
  @Before
  public void setUpBeforeMethod() {

    DynamoHttpServletRequest request = setUpCurrentRequest();
    
    // Set up ColorSkuPicker for the tests.
    mColorSkuPickerTest = (ColorSkuPicker) 
      request.resolveName("/atg/commerce/product/detail/ColorSkuPicker", true);
    
    assertNotNull(mColorSkuPickerTest);
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
    
    mColorSkuPickerTest = null;
  }
  
  /**
   * Ensure the instance of ColorSkuPicker has been cleared
   */
  @After
  public void tearDownAfterMethod() {
    
    mColorSkuPickerTest = null;
  }
  
  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------
  
  //------------------------------------
  // Test the class setters and getters
  //------------------------------------
  
  @Test
  public void testGetColorPropertyName() {
    
    assertEquals("color", mColorSkuPickerTest.getColorPropertyName());
  }
  
  @Test
  public void testSetColorPropertyName() {
    
    mColorSkuPickerTest.setColorPropertyName(TEST);
    assertEquals(TEST, mColorSkuPickerTest.getColorPropertyName());
  }

  @Test
  public void testGetAvailableColorsPropertyName() {

    assertEquals("availableColors",
      mColorSkuPickerTest.getAvailableColorsPropertyName());
  }

  @Test
  public void testSetAvailableColorsPropertyName() {

    mColorSkuPickerTest.setAvailableColorsPropertyName(TEST);
    assertEquals(TEST, mColorSkuPickerTest.getAvailableColorsPropertyName());
  }
  
  @Test
   public void testGetSelectedColorPropertyName() {

    assertEquals("selectedColor", mColorSkuPickerTest.getSelectedColorPropertyName());
  }

  @Test
  public void testSetSelectedColorPropertyName() {

    mColorSkuPickerTest.setSelectedColorPropertyName(TEST);
    assertEquals(TEST, mColorSkuPickerTest.getSelectedColorPropertyName());
  }

  //------------------------------------
  // Test the class methods
  //------------------------------------
  
  @Test
  public void testGetSelectedOption() throws RepositoryException {
    
    Map<String, String> pOptions = new HashMap<>();
    pOptions.put("selectedColor", "green");
   
    // If a selected color is passed in the pOptions map then it is what should be returned.
    assertEquals("green", mColorSkuPickerTest.getSelectedOption(pOptions));
    
    // If a product with multiple child skus is passed, each with a unique color then null should be
    // returned
    pOptions.clear();
    pOptions.put("productIdPropertyName", "testProduct1");
    assertNull(mColorSkuPickerTest.getSelectedOption(pOptions));
    
    // If a product with a single child sku is passed, and it has a color then that color should be
    // returned
    pOptions.clear();
    pOptions.put("productIdPropertyName", "testProduct2");
    assertEquals("yellow", mColorSkuPickerTest.getSelectedOption(pOptions));
  }
  
  @Test
  public void testGetAllOptions() throws RepositoryException {
    
    Map<String, String> pOptions = new HashMap<>();
    pOptions.put("productIdPropertyName", "testProduct1");
    
    // If a product with multiple child skus is passed, each with a unique color then null should be
    // returned   
    List<String> allOptions = (List<String>) mColorSkuPickerTest.getAllOptions(pOptions);
    
    // While testProduct1 has 4 child SKUs two of them have the same value for the color property.  
    // Therefore we expect a list of size 3 to be returned.
    assertEquals(3, allOptions.size());
    
    // Ensure the returned list is sorted as expected
    assertEquals("black", allOptions.get(0));
    assertEquals("green", allOptions.get(1));
    assertEquals("red", allOptions.get(2));
  }
  
  @Test
  public void testGetSelectedSku() throws RepositoryException {
    
    Map<String, String> pOptions = new HashMap<>();
    pOptions.put("productIdPropertyName", "testProduct1");
    
    // As the selectedColor property has not been passed we expect null to be returned.
    assertEquals(null,
        mColorSkuPickerTest.getSelectedSku(pOptions));
    
    // If the passed selectedColor does not exist then null should be returned
    pOptions.put("selectedColor", "purple");
    assertEquals(null,
        mColorSkuPickerTest.getSelectedSku(pOptions));
    
    // When a valid color is passed the details of the matching SKU should be returned
    pOptions.put("selectedColor", "black");

    RepositoryItem selectedSku = mColorSkuPickerTest.getSelectedSku(pOptions);

    RepositoryItem expectedResult = mStoreCatalogTools.findSKU("testSku4");
    
    assertEquals(expectedResult, selectedSku);
    
  }
  
  @Test
  public void testGetFilteredSkus() throws RepositoryException {
    
    Map<String, String> pOptions = new HashMap<>();
    pOptions.put("productIdPropertyName", "testProduct1");
    
    // Get a list of all active (in range) child SKUs for testProduct1
    RepositoryItem product = mColorSkuPickerTest.getCurrentProduct(pOptions);
    List<RepositoryItem> filteredSkus = 
        (List<RepositoryItem>) mColorSkuPickerTest.getFilteredSkus(product);
    
    // out of the 4 available child SKUs 2 are inactive therefore we expect the returned list to
    // have a size of 2
    assertEquals(2, filteredSkus.size());
    
  }

  @Test
  public void testGetHelperInformation() throws RepositoryException {

    // As the ColorProductDetailPicker does not override the default implementation an empty map is
    // the expected result.
    assertEquals(new HashMap<>(),
      mColorSkuPickerTest.getHelperInformation("color", new HashMap<String, String>()));

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
