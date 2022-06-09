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

package atg.projects.store.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import atg.commerce.inventory.InventoryException;
import atg.nucleus.ResolvingMap;
import atg.nucleus.ServiceException;
import atg.service.filter.bean.BeanFilterException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.commerce.pricing.priceLists.PriceListException;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;

import javax.servlet.ServletException;

public class ProductDetailsToolsTest {

  //----------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/product/ProductDetailsToolsTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------

  /** The filter id for the tests */
  private static String mFilterId = "productDetailProduct";
  
  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------
  
  /** The ProductDetailsTools that will be tested */
  private static ProductDetailsTools mProductDetailsTools = null;
  
  /** The StoreCatalogTools that will be tested */
  private static StoreCatalogTools mStoreCatalogTools = null;
  
  /** The Catalog Repository for the test */
  private static GSARepository mProductCatalog = null;

  /** The Inventory Repository for the test */
  private static GSARepository mInventoryRepository = null;
  
  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;
  
  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------
  
  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the ProductDetailsTools instance to be used in this test.
   * 
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base",
                     "CommerceAccelerator.Plugins.SearchAndNavigation"},
      ProductDetailsToolsTest.class,
      "ProductDetailsToolsTest",
      "/atg/Initial");

    // Set up CatalogTools for the tests.
    mStoreCatalogTools = (StoreCatalogTools)
      mNucleus.resolveName("/atg/commerce/catalog/CatalogTools", true);
    
    assertNotNull(mStoreCatalogTools);

    // Initialize catalog repository and import sample data
    mProductCatalog = (GSARepository)
        mNucleus.resolveName("/atg/commerce/catalog/ProductCatalog", true);
    String[] dataFileNames = { "catalog.xml" };
    TemplateParser.loadTemplateFiles(mProductCatalog, 1, dataFileNames, true,
      new PrintWriter(System.out), null);

    assertNotNull(mProductCatalog);

    // Initialize the inventory repository and import the sample data
    mInventoryRepository =
      (GSARepository) mNucleus.resolveName("/atg/commerce/inventory/InventoryRepository", true);
    String[] inventoryDataFiles = { "inventoryData.xml" };
    TemplateParser.loadTemplateFiles(mInventoryRepository, 1, inventoryDataFiles, true,
      new PrintWriter(System.out), null);

    assertNotNull(mInventoryRepository);
  }
  
  /**
   * Re-initialize the ProductDetailsTools component as well as sets up the
   * profile for the current request.
   * 
   * @throws PriceListException 
   */
  @Before
  public void setUpBeforeMethod() throws PriceListException {

    DynamoHttpServletRequest request = setUpCurrentRequest();
    
    // Set up ProductDetailsTools for the tests.
    mProductDetailsTools = (ProductDetailsTools)
        request.resolveName("/atg/commerce/product/detail/ProductDetailsTools", true);
    
    assertNotNull(mProductDetailsTools);
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
    
    mProductCatalog = null;
    mInventoryRepository = null;
    mStoreCatalogTools = null;
    mProductDetailsTools = null;
  }
  
  /**
   * Ensure the instance of SkuTypeProperties has been cleared
   */
  @After
  public void tearDownAfterMethod() {
    
    mProductDetailsTools = null;
  }
  
  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------
  
  //------------------------------------
  // Test the class setters and getters
  //------------------------------------
  
  @Test
  public void testGetStartEndDateValidator() {
    assertNotNull(mProductDetailsTools.getStartEndDateValidator());
  }
  
  @Test
  public void testSetStartEndDateValidator() {
    mProductDetailsTools.setStartEndDateValidator(null);
    assertNull(mProductDetailsTools.getStartEndDateValidator());
  }

  @Test
  public void testGetCatalogTools() {
    assertNotNull(mProductDetailsTools.getCatalogTools());
  }
  
  @Test
  public void testSetCatalogTools() {
    mProductDetailsTools.setCatalogTools(null);
    assertNull(mProductDetailsTools.getCatalogTools());
  }
  
  @Test
  public void testGetInventoryManager() {
    assertNotNull(mProductDetailsTools.getInventoryManager());
  }
  
  @Test
  public void testSetInventoryManager() {
    mProductDetailsTools.setInventoryManager(null);
    assertNull(mProductDetailsTools.getInventoryManager());
  }
  
  @Test
  public void testGetProductDetailPropertyManager() {
    assertNotNull(mProductDetailsTools.getProductDetailsPropertyManager());
  }
  
  @Test
  public void testSetProductDetailPropertyManager() {
    mProductDetailsTools.setProductDetailsPropertyManager(null);
    assertNull(mProductDetailsTools.getProductDetailsPropertyManager());
  }

  @Test
  public void testSetBeanFilterRegistry() {
    mProductDetailsTools.setBeanFilterRegistry(null);
    assertNull(mProductDetailsTools.getBeanFilterRegistry());
  }

  @Test
  public void testGetBeanFilterRegistry() {
    assertNotNull(mProductDetailsTools.getBeanFilterRegistry());
  }
  
  //------------------------
  // Test the class methods
  //------------------------


  /**
   * Get the helper information for the passed SKU property
   */
  @Test
  public void testGetHelperInformation() {

    // Create the size property map for the expected result
    Map<String, String> sizeInfoProperties = new HashMap<>();
    sizeInfoProperties.put("type", "Size Chart");
    sizeInfoProperties.put("contentUrl", "SizeChart.html");

    assertEquals(sizeInfoProperties, mProductDetailsTools.getHelperInformation("size"));
  }
  
  /**
   * Test the isValidItem method.  This test ensures that the StartEndDate validator has been 
   * configured correctly.
   * 
   * @throws RepositoryException
   */
  @Test
  public void testIsValidItem() throws RepositoryException {

    // Get a product that is invalid
    RepositoryItem product = mStoreCatalogTools.findProduct("testProduct1");
    boolean isValid = mProductDetailsTools.isValidItem(product);
    
    assertEquals(false, isValid);

    // Get a product that is valid
    product = mStoreCatalogTools.findProduct("testProduct2");
    isValid = mProductDetailsTools.isValidItem(product);

    assertEquals(true, isValid);
  }

  @Test
  public void testGetProduct() throws RepositoryException {

    // Get the expected product
    RepositoryItem expectedProduct = mStoreCatalogTools.findProduct("testProduct1");

    // Get the product using ProductDetailsTools
    RepositoryItem product = mProductDetailsTools.getProduct("testProduct1");

    assertEquals(expectedProduct, product);
  }

  /**
   * Test the getFilteredProductMap method.  This method ensures that the correct product
   * properties are returned by the bean filter for the passed filter id.
   *
   * @throws RepositoryException
   * @throws BeanFilterException
   */
  @Test
  public void testGetFilteredProductMap() throws RepositoryException, BeanFilterException {

    RepositoryItem product = mStoreCatalogTools.findProduct("testProduct2");

    List<String> childSkuProps = new ArrayList<>();
    childSkuProps.add("color");
    childSkuProps.add("size");

    Map<String, Object> expectedResult = new HashMap<>();
    expectedResult.put("repositoryId", "testProduct2");
    expectedResult.put("longDescription", "This is a long description 2");
    expectedResult.put("largeImageUrl", "Test/Image/Url/largeImage2.jpg");
    expectedResult.put("displayName", "Test Product 2");
    expectedResult.put("childSkuType", "clothing-sku");
    expectedResult.put("childSkuPickerProperties", childSkuProps);
    expectedResult.put("isAvailable", true);
    expectedResult.put("description", null);
    expectedResult.put("thumbnailImageUrl", null);

    Map filteredMap =
      mProductDetailsTools.getFilteredProductMap(product, mFilterId, getSkuTypes());

    assertEquals(expectedResult, filteredMap);
  }

  /**
   * Test the getFilteredSkuMap method.  This method ensures that the correct SKU properties are
   * returned by the bean filter for the passed filter id.
   *
   * @throws RepositoryException
   * @throws BeanFilterException
   * @throws InventoryException
   */
  @Test
  public void testGetFilteredSkuMap() throws RepositoryException, BeanFilterException {

    RepositoryItem product = mStoreCatalogTools.findProduct("testProduct2");
    List<RepositoryItem> childSkus =
      (List<RepositoryItem>) mStoreCatalogTools.getProductChildSkus(product);

    Map<String, Object> expectedResult = new HashMap<>();
    expectedResult.put("repositoryId", "testSku2");
    expectedResult.put("type", "clothing-sku");
    expectedResult.put("isAvailable", true);
    expectedResult.put("displayName", "Test SKU 2");
    expectedResult.put("inStock", true);

    Map filteredMap =
      mProductDetailsTools.getFilteredSkuMap(childSkus.get(0), "detailed");

    assertEquals(expectedResult, filteredMap);
  }

  /**
   * Test the isItemInStock method for the scenario where no inventory has been
   * created for a SKU.  The result should be the item shows as not being in stock.
   *
   * @throws RepositoryException
   */
  @Test
  public void testIsItemInStockNoInventory() throws RepositoryException {
    RepositoryItem sku = mStoreCatalogTools.findSKU("testSku4");
    boolean isInStock = mProductDetailsTools.isItemInStock(sku);
    assertEquals(false, isInStock);
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
   * Builds a Map of skuType properties
   *
   * @return a Map of skuType properties
   */
  public static ResolvingMap getSkuTypes() {

    List<String> clothingProps = new ArrayList<>();
    clothingProps.add("color");
    clothingProps.add("size");

    List<String> furnitureProps = new ArrayList<>();
    furnitureProps.add("woodFinish");

    ResolvingMap skuTypes = new ResolvingMap();
    skuTypes.put("clothing-sku", clothingProps);
    skuTypes.put("furniture-sku", furnitureProps);

    return skuTypes;
  }
}
