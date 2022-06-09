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
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.nucleus.ServiceMap;
import atg.projects.store.sku.picker.SkuPicker;
import atg.projects.store.sku.picker.SkuPickers;
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
import com.endeca.infront.navigation.model.MdexResource;
import com.endeca.infront.navigation.url.UrlNavigationStateBuilder;
import com.endeca.navigation.CSAMockDataFactory;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.MockAggrERec;
import com.endeca.navigation.MockENEQueryResults;
import com.endeca.navigation.MockERecList;
import com.endeca.navigation.MockMdexRequestBroker;

import org.junit.*;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * A test class for the SkuSelectorHandler
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/handler/SkuSelectorHandlerTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class SkuSelectorHandlerTest {

  //----------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/handler/SkuSelectorHandlerTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  /** The ProductDetailsPickerHandler that will be tested */
  private static SkuSelectorHandler mSkuSelectorHandler = null;

  /** The Catalog Repository for the test */
  private static GSARepository mProductCatalog = null;

  /** The Inventory Repository for the test */
  private static GSARepository mInventoryRepository = null;

  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;

  /** The NavigationState used by the cartridge handler */
  private static NavigationState mNavigationState = null;

  /** The RecordState used by the cartridge handler */
  private static RecordState mRecordState = null;

  /** The NavigationStateBuilder used to generate the NavigationState for the test */
  private static UrlNavigationStateBuilder mNavigationStateBuilder = null;

  private static MockMdexRequestBroker mMdexRequestBroker = null;

  /** The RecordDetailsConfig that will be used when making requests to the MDEX */
  private static RecordDetailsConfig mCartridgeConfig = null;

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
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[]{"CommerceAccelerator.Base",
        "CommerceAccelerator.Plugins.SearchAndNavigation"},
      SkuSelectorHandlerTest.class,
      "SkuSelectorHandlerTest",
      "/atg/Initial");

    // Initialize catalog repository and import sample data
    mProductCatalog =
      (GSARepository) mNucleus.resolveName("/atg/commerce/catalog/ProductCatalog", true);
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

    // Endeca set up and configuration

    // Create a request and use it to resolve the NavigationStateBuilder
    DynamoHttpServletRequest request = setUpCurrentRequest();

    // Set up NavigationStateBuilder that will be used of for the requests
    mNavigationStateBuilder = (UrlNavigationStateBuilder)
      request.resolveName("/atg/endeca/assembler/cartridge/manager/NavigationStateBuilder", true);

    assertNotNull(mNavigationStateBuilder);

    try {
      // Get the NavigationState.
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
   * Re-initialize the mProductDetailsPickerHandler component
   */
  @SuppressWarnings({ "serial", "unchecked" })
  @Before
  public void setUpBeforeMethod() {

    DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();

    // Set up ProductDetailsMainContentHandler for the tests.
    mSkuSelectorHandler = (SkuSelectorHandler)
      request.resolveName("/atg/endeca/assembler/cartridge/handler/SkuSelector", true);

    assertNotNull(mSkuSelectorHandler);

    // Populate the product picker map with mock ProductDetailPicker objects
    ServiceMap skuPickerMap = new ServiceMap();
    skuPickerMap.put("color", new MockProductDetailsPicker("color"));
    skuPickerMap.put("size", new MockProductDetailsPicker("size"));

    SkuPickers skuPickers = new SkuPickers();
    skuPickers.setSkuPickerMap(skuPickerMap);

    mSkuSelectorHandler.setSkuPickers(skuPickers);
    mSkuSelectorHandler.setNavigationState(mNavigationState);
    mSkuSelectorHandler.setMdexRequestBroker(mMdexRequestBroker);

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
    mInventoryRepository = null;
    mSkuSelectorHandler = null;
    mMdexRequestBroker = null;
    mNavigationState = null;
    mNavigationStateBuilder = null;
    mCartridgeConfig = null;
  }

  /**
   * Ensure the instance of ProductDetailsPickerHandler has been cleared
   */
  @After
  public void tearDownAfterMethod() {

    mSkuSelectorHandler = null;
    mCartridgeConfig = null;
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  @Test
  public void testGetProductPickers() {
    assertNotNull(mSkuSelectorHandler.getSkuPickers());
  }

  @Test
  public void testSetProductPickers() {
    mSkuSelectorHandler.setSkuPickers(null);
    assertNull(mSkuSelectorHandler.getSkuPickers());
  }

  @Test
  public void testGetSkuPickerPropertyManager() {
    assertNotNull(mSkuSelectorHandler.getSkuPickerPropertyManager());
  }

  @Test
  public void testSetSkuPickerPropertyManager() {
    mSkuSelectorHandler.setSkuPickerPropertyManager(null);
    assertNull(mSkuSelectorHandler.getSkuPickerPropertyManager());
  }

  /**
   * Ensure that the product properties that have been configured are returned along with the
   * additional product properties.  Finally, ensure that the sku picker data is also returned.
   *
   * @throws com.endeca.infront.assembler.CartridgeHandlerException
   */
  @Test
  public void testProcessWithRecordState() throws CartridgeHandlerException {

    // Create a mock RecordState for the request
    RecordState recordState = new MockRecordState();
    recordState.putParameter("A", "testProductShirt");

    // Add the newly created RecordState to the handler
    mSkuSelectorHandler.setRecordState(recordState);
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
    mSkuSelectorHandler.setRecordState(null);
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

    // Initialize & preprocess
    mSkuSelectorHandler.initialize(mCartridgeConfig);
    mSkuSelectorHandler.preprocess(mCartridgeConfig);

    // Set up the mock response from the MDEX for the testProductShirt product
    setupResults(createMockAggProductShirtRecord());

    // Process the request and return the result
    RecordDetails recordDetails =  mSkuSelectorHandler.process(mCartridgeConfig);

    // Assert that the SKU picker information is returned as expected.
    List<Map<String, Object>> skuPickers =
      (List<Map<String, Object>>) recordDetails.get("skuPickers");

    assertEquals(buildExpectedPickerResult(), skuPickers);
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
    list.appendERec(factory.createMockSkuERec("testSkuShirt2"));
    aggERec.setErecs(list);


    aggERec.setTotalErecs(2);

    return aggERec;
  }

  /**
   * Helper method to build the expected result of the skuPickers object in the RecordDetails
   *  returned from the handler.
   *
   * @return
   *   A <code>List</code> containing the expected contents of the skuPickers Map entry
   */
  public static List<Map<String, Object>> buildExpectedPickerResult() {

    // build the options lists for both colors and sizes
    List<String> sizeOptions = new ArrayList<>();
    sizeOptions.add("sizeOption1");
    sizeOptions.add("sizeOption2");
    sizeOptions.add("sizeOption3");

    List<String> colorOptions = new ArrayList<>();
    colorOptions.add("colorOption1");
    colorOptions.add("colorOption2");
    colorOptions.add("colorOption3");

    // build the expected helper information
    Map<String, String> helperInformation = new HashMap<>();
    helperInformation.put("helperInfo", "testHelperInfo");

    // Build the expected size picker
    Map<String, Object> expectedSizePicker = new HashMap<>();
    expectedSizePicker.put("type", "size");
    expectedSizePicker.put("value", null);
    expectedSizePicker.put("options", sizeOptions);
    expectedSizePicker.put("helperInformation", helperInformation);

    // Build the expected color picker
    Map<String, Object> expectedColorPicker = new HashMap<>();
    expectedColorPicker.put("type", "color");
    expectedColorPicker.put("value", null);
    expectedColorPicker.put("options", colorOptions);
    expectedColorPicker.put("helperInformation", helperInformation);

    List<Map<String, Object>> expectedPickers = new ArrayList<>();
    expectedPickers.add(expectedColorPicker);
    expectedPickers.add(expectedSizePicker);

    return expectedPickers;
  }

  //---------------------------------------------------------------------------
  // MOCK CLASSES
  //---------------------------------------------------------------------------

  /**
   * Create a mock implementation of ProductDetailsPicker so it can be added to the
   * ProductDetaislPickers object for complete testing.
   *
   * @author Oracle
   *
   */
  private class MockProductDetailsPicker implements SkuPicker {

    private String mName = null;

    public MockProductDetailsPicker(String pName) {
      mName = pName;
    }

    @Override
    public String getSelectedOption(Map<String, String> pOptions)
      throws RepositoryException {

      return mName + "SelectedOption";
    }

    @Override
    public Collection<String> getAllOptions(Map<String, String> pOptions)
      throws RepositoryException {

      List<String> result = new ArrayList<String>();
      result.add(mName + "Option1");
      result.add(mName + "Option2");
      result.add(mName + "Option3");

      return result;
    }

    @Override
    public RepositoryItem findSkuByOption(Collection<RepositoryItem> pSkus, String pOption) {
      return null;
    }

    @Override
    public RepositoryItem getSelectedSku(Map<String, String> pOptions)
      throws RepositoryException {

      RepositoryItem product =
        mSkuSelectorHandler.getCatalogTools().findProduct("testProductShirt");

      List<RepositoryItem> childSkus = (List<RepositoryItem>) product.getPropertyValue("childSkus");

      return childSkus.get(0);
    }

    @Override
    public Map<String, String> getHelperInformation(String pPropertyName, Map<String, String> options)
      throws RepositoryException {
      Map<String, String> helperInfo = new HashMap<>();
      helperInfo.put("helperInfo", "testHelperInfo");

      return helperInfo;
    }
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
