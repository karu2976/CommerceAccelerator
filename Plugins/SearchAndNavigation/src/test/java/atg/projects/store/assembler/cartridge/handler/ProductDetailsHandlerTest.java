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
import atg.endeca.assembler.navigation.ExtendedNavigationStateBuilder;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ResolvingMap;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.cartridge.RecordDetails;
import com.endeca.infront.cartridge.RecordDetailsConfig;
import com.endeca.infront.navigation.NavigationException;
import com.endeca.infront.navigation.NavigationState;
import com.endeca.infront.navigation.RecordState;
import com.endeca.infront.navigation.model.FilterState;
import com.endeca.infront.navigation.model.MdexResource;
import com.endeca.navigation.CSAMockDataFactory;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.MockAggrERec;
import com.endeca.navigation.MockENEQueryResults;
import com.endeca.navigation.MockERecList;
import com.endeca.navigation.MockMdexRequestBroker;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * A tes class for the ProductDetailsBaseHandler
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/handler/ProductDetailsHandlerTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ProductDetailsHandlerTest {

  //----------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/handler/ProductDetailsHandlerTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  /** The ProductDetailsHandler that will be tested */
  private static ProductDetailsHandler mProductDetailsHandler = null;

  /** The Site Repository for the test */
  private static GSARepository mSiteRepository = null;

  /** The Catalog Repository for the test */
  private static GSARepository mProductCatalog = null;

  /** The Inventory Repository for the test */
  private static GSARepository mInventoryRepository = null;

  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;

  /** The NavigationState used by the cartridge handler */
  private static NavigationState mNavigationState = null;

  /** The NavigationStateBuilder used to generate the NavigationState for the test */
  private static ExtendedNavigationStateBuilder mNavigationStateBuilder = null;

  private static MockMdexRequestBroker mMdexRequestBroker = null;

  /** The RecordDetailsConfig that will be used when making requests to the MDEX */
  private static RecordDetailsConfig mCartridgeConfig = null;

  private static SiteContextManager mSiteContextManager = null;

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the ProductDetailsHandler instance to be used in this test.
   *
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException, RepositoryException, SiteContextException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[]{"CommerceAccelerator.Base",
        "CommerceAccelerator.Plugins.SearchAndNavigation"},
        ProductDetailsHandlerTest.class,
        "ProductDetailsHandlerTest",
        "/atg/Initial");

    // Create a request and use it to resolve the NavigationStateBuilder
    DynamoHttpServletRequest request = setUpCurrentRequest();

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

    // Initialize site repository and import sample data
    mSiteRepository = (GSARepository) mNucleus.resolveName("/atg/multisite/SiteRepository", true);
    String[] siteDataFileNames = { "sites.xml" };
    TemplateParser.loadTemplateFiles(mSiteRepository, 1, siteDataFileNames,
      true, new PrintWriter(System.out), null);

    assertNotNull(mSiteRepository);

    RepositoryItem storeSiteUS = mSiteRepository.getItem("storeSiteUS");

    mSiteContextManager =
      (SiteContextManager) mNucleus.resolveName("/atg/multisite/SiteContextManager", true);

    assertNotNull(mSiteContextManager);

    SiteContext sc = mSiteContextManager.getSiteContext(storeSiteUS.getRepositoryId());
    mSiteContextManager.pushSiteContext(sc);

    // Endeca set up and configuration

    // Set up NavigationStateBuilder that will be used of for the requests
    mNavigationStateBuilder = (ExtendedNavigationStateBuilder)
      request.resolveName("/atg/endeca/assembler/cartridge/manager/NavigationStateBuilder", true);

    assertNotNull(mNavigationStateBuilder);

    // Get the NavigationState.
    try {
      mNavigationState = mNavigationStateBuilder.parseNavigationState(request);
    }
    catch (NavigationException ne) {
      fail(ne.getMessage());
    }

    assertNotNull(mNavigationState);

    // Create a new MDEX request broker.
    mMdexRequestBroker = new MockMdexRequestBroker(new MdexResource(), false);
  }

  /**
   * Re-initialize the mProductDetailsHandler component
   */
  @SuppressWarnings({ "serial", "unchecked" })
  @Before
  public void setUpBeforeMethod() {

    DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();

    // Set up ProductDetailsHandler for the tests.
    mProductDetailsHandler = (ProductDetailsHandler)
      request.resolveName("/atg/endeca/assembler/cartridge/handler/ProductDetails", true);

    assertNotNull(mProductDetailsHandler);

    mProductDetailsHandler.setSkuTypes(getSkuTypes());
    mProductDetailsHandler.setNavigationState(mNavigationState);
    mProductDetailsHandler.setMdexRequestBroker(mMdexRequestBroker);

    // Set up the record details config
    if (mCartridgeConfig == null) {

      // Initialize the cartridge configuration to be used for all tests.
      mCartridgeConfig = new RecordDetailsConfig();
    }

    // Initialize the field and sub field names that should be returned from the MDEX
    mCartridgeConfig.setFieldNames(new ArrayList<String>() {{
      add("product.repositoryId");
    }});

    mCartridgeConfig.setSubRecordFieldNames(new ArrayList<String>() {{
      add("sku.repositoryId");
    }});
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
    mProductDetailsHandler = null;
    mMdexRequestBroker = null;
    mNavigationState = null;
    mNavigationStateBuilder = null;
    mCartridgeConfig = null;
    mSiteContextManager = null;
    mSiteRepository = null;
    mInventoryRepository = null;
  }

  /**
   * Ensure the instance of ProductDetailTools has been cleared
   */
  @After
  public void tearDownAfterMethod() {

    mProductDetailsHandler = null;
    mCartridgeConfig = null;
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  //------------------------------------
  // Test the class setters and getters
  //------------------------------------

  @Test
  public void testGetCatalogTools() {
    assertNotNull(mProductDetailsHandler.getCatalogTools());
  }

  @Test
  public void testSetCatalogTools() {
    mProductDetailsHandler.setCatalogTools(null);
    assertNull(mProductDetailsHandler.getCatalogTools());
  }

  @Test
  public void testGetProductDetailTools() {
    assertNotNull(mProductDetailsHandler.getProductDetailsTools());
  }

  @Test
  public void testSetProductDetailTools() {
    mProductDetailsHandler.setProductDetailsTools(null);
    assertNull(mProductDetailsHandler.getProductDetailsTools());
  }

  @Test
  public void testGetProductRepositoryId() {
    assertNotNull(mProductDetailsHandler.getProductIdPropertyName());
  }

  @Test
  public void testSetProductRepositoryId() {
    mProductDetailsHandler.setProductIdPropertyName(null);
    assertNull(mProductDetailsHandler.getProductIdPropertyName());
  }

  @Test
  public void testGetFilterId() {
    assertEquals("productDetailsProduct", mProductDetailsHandler.getFilterId());
  }

  @Test
  public void testSetFilterId() {
    mProductDetailsHandler.setFilterId(null);
    assertNull(mProductDetailsHandler.getFilterId());
    mProductDetailsHandler.setFilterId("productDetailsProduct");
  }

  @Test
  public void testGetSkuTypes() {
    assertNotNull(mProductDetailsHandler.getSkuTypes());
  }

  @Test
  public void testSetSkuTypes() {
    mProductDetailsHandler.setSkuTypes(null);
    assertNull(mProductDetailsHandler.getSkuTypes());
  }

  @Test
  public void testGetContentItemModifiers() {
    assertNotNull(mProductDetailsHandler.getContentItemModifiers());
  }

  @Test
  public void testSetContentItemModifiers() {
    mProductDetailsHandler.setContentItemModifiers(null);
    assertNull(mProductDetailsHandler.getContentItemModifiers());
  }

  //------------------------
  // Test the class methods
  //------------------------

  /**
   * Ensure that the filter state that is passed is the same that the one that is returned.
   */
  @Test
  public void testGetFilterState() {

    FilterState filterState = mProductDetailsHandler.getFilterState(mNavigationState);
    assertEquals(mNavigationState.getFilterState(), filterState);
  }

  /**
   * Sets the query parameter to the product identifier used by the MDEX before retrieving the
   * products details.
   *
   * @throws com.endeca.infront.assembler.CartridgeHandlerException
   */
  @Test
  public void testProcessWithRecordState() throws CartridgeHandlerException, UnsupportedEncodingException {

    // Create a mock RecordState for the request
    RecordState recordState = new MockRecordState();
    recordState.putParameter("A", "testProductShirt");

    // Add the newly created RecordState to the handler
    mProductDetailsHandler.setRecordState(recordState);
    testProcess();
  }

  /**
   * Sets the query parameter to the product identifier used by the repository and ensures that the
   * process method will retrieve the productId from this parameter before retrieving the products
   * details.
   *
   * @throws com.endeca.infront.assembler.CartridgeHandlerException
   */
  @Test
  public void testProcessProductWithoutRecordState() throws CartridgeHandlerException {

    // Add the product id for the test product as a repository parameter to the request
    ServletUtil.getCurrentRequest().setParameter("productId", "testProductShirt");
    mProductDetailsHandler.setRecordState(null);
    testProcess();
  }

  //---------------------------------------------------------------------------
  // UTILITY METHODS
  //---------------------------------------------------------------------------

  /**
   * Ensure that the product properties that have been configured are returned
   *
   * @throws CartridgeHandlerException
   */
  public void testProcess() throws CartridgeHandlerException {
    // Initialize & pre-process
    mProductDetailsHandler.initialize(mCartridgeConfig);
    mProductDetailsHandler.preprocess(mCartridgeConfig);

    // Set up the mock response from the MDEX for the testProductShirt product
    setupResults(createMockAggProductShirtRecord());

    // Process the request and return the result
    RecordDetails recordDetails =  mProductDetailsHandler.process(mCartridgeConfig);

    Map<String, Object> productPropertyMap =
      (Map<String, Object>) recordDetails.get(ProductDetailsHandler.PRODUCT);

    // Assert that the product property map is as expected.
    assertEquals(getExpectedProductMap(), productPropertyMap);

    Map<String, Object> skuPropertyMap =
      (Map<String, Object>) recordDetails.get(ProductDetailsHandler.SELECTED_SKU);

    // Assert that the selected sku property map is as expected.
    assertEquals(getExpectedSelectedSkuMap(), skuPropertyMap);
  }

  /**
   * Convenience method for creating the expected product property map result.
   *
   * @return
   */
  public Map<String, Object> getExpectedProductMap() {

    Map<String, Object> expectedResult = new HashMap<>();
    expectedResult.put("repositoryId", "testProductShirt");
    expectedResult.put("longDescription", "This is a long description");
    expectedResult.put("isAvailable", false);
    expectedResult.put("largeImageUrl", null);
    expectedResult.put("displayName", "Test Product 1");
    expectedResult.put("childSkuType", "clothing-sku");

    List<String> pickerProperties = new ArrayList<>();
    pickerProperties.add("color");
    pickerProperties.add("size");

    expectedResult.put("childSkuPickerProperties", pickerProperties);

    return expectedResult;
  }

  /**
   * Convenience method for creating the expected selected SKU property map result.
   *
   * @return
   */
  public Map<String, Object> getExpectedSelectedSkuMap() {

    Map<String, Object> expectedResult = new HashMap<>();
    expectedResult.put("type", "clothing-sku");
    expectedResult.put("repositoryId", "testSkuShirt1");
    expectedResult.put("isAvailable", true);
    expectedResult.put("displayName", "Test SKU 1");
    expectedResult.put("inStock", true);

    return expectedResult;
  }

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

  private static void setupResults(MockAggrERec rec) {
    MockENEQueryResults results = new MockENEQueryResults();
    results.setAggrRecord(rec);
    mMdexRequestBroker.setResults(results);
  }

  /**
   * Create the mock results that we expect back from the MDEX
   *
   * @return
   */
  public static MockAggrERec createMockAggProductShirtRecord() {

    CSAMockDataFactory factory = new CSAMockDataFactory();
    String productId = "testProductShirt";

    MockAggrERec aggERec = new MockAggrERec();
    aggERec.setRepresentative(factory.createMockProductERec(productId));
    aggERec.setProperties(aggERec.getRepresentative().getProperties());
    aggERec.setSpec(productId);

    MockERecList list = new MockERecList();
    list.appendERec(factory.createMockSkuERec("testSkuShirt1"));
    aggERec.setErecs(list);

    aggERec.setTotalErecs(1);

    return aggERec;
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

  /**
   * Mock class to represent the RecordState.
   *
   */
  private class MockRecordState implements RecordState {

    @Override
    public RecordState updateParameters(Map<String, String> map) {
      return this;
    }

    @Override
    public RecordState removeParameter(String s) {
      return this;
    }

    @Override
    public RecordState putParameter(String s, String s1) {
      return this;
    }

    @Override
    public RecordState putAllParameters(Map<String, String> map) {
      return this;
    }

    @Override
    public RecordState clearParameters() {
      return this;
    }

    @Override
    public String getRecordSpec() {
      return "";
    }

    @Override
    public RecordState updateRecordSpec(String s, boolean b) {
      return this;
    }

    @Override
    public boolean isAggregateRecord() {
      return true;
    }

    @Override
    public void inform(ENEQueryResults eneQueryResults) {

    }

    @Override
    public String getCanonicalLink() {
      return "";
    }

    @Override
    public String getParameter(String s) {
      return "";
    }

    @Override
    public String getEncoding() {
      return "UTF-8";
    }
  }
}