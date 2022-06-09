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

package atg.projects.store.assembler.cartridge;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import atg.endeca.assembler.navigation.filter.DateRangeFilterBuilder;
import atg.endeca.assembler.navigation.filter.RangeFilterBuilder;
import atg.nucleus.ServiceException;
import atg.projects.store.catalog.CatalogNavigationService;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.navigation.model.FilterState;
import com.endeca.infront.navigation.model.RangeFilter;
import com.endeca.infront.navigation.model.SubRecordsPerAggregateRecord;
import com.endeca.infront.navigation.request.RecordsMdexQuery;
import com.endeca.navigation.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This unit test will test the methods of the StoreCartridgeTools class.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/assembler/cartridge/StoreCartridgeToolsTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreCartridgeToolsTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/assembler/cartridge/StoreCartridgeToolsTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  /** The StoreCartridgeTools that will be tested. */
  private static StoreCartridgeTools mStoreCartridgeTools = null;

  /** The TestProductRangeFilterBuilder to be used in the test. */
  private static DateRangeFilterBuilder mTestProductRangeFilterBuilder = null;

  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the StoreCartridgeTools instance to be used in this test.
   *
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base" },
      StoreCartridgeToolsTest.class,
      "StoreCartridgeToolsTest",
      "/atg/Initial");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    // Set up CategoryNavigationHandler.
    mStoreCartridgeTools = (StoreCartridgeTools)
      request.resolveName("/atg/endeca/assembler/cartridge/StoreCartridgeTools", true);

    assertNotNull(mStoreCartridgeTools);

    mTestProductRangeFilterBuilder = (DateRangeFilterBuilder)
      request.resolveName(
        "/atg/projects/store/assembler/cartridge/manager/filter/TestProductRangeFilterBuilder", true);

    assertNotNull(mTestProductRangeFilterBuilder);
  }

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

    mStoreCartridgeTools = null;
    mTestProductRangeFilterBuilder = null;
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  //------------------------------------
  // Test the class setters and getters
  //------------------------------------

  /**
   * Test the setDimensionName and setDimensionName methods.
   */
  @Test
  public void testSetGetCatalogNavigation() {
    CatalogNavigationService catalogNavigationService = mStoreCartridgeTools.getCatalogNavigation();
    assertNotNull(catalogNavigationService);
    mStoreCartridgeTools.setCatalogNavigation(catalogNavigationService);
    assertNotNull(mStoreCartridgeTools.getCatalogNavigation());
  }

  //------------------------
  // Test the class methods
  //------------------------

  /**
   * Test the testUpdateRangeFilters method, ensuring that existing RangeFilters
   * get merged with ones generated by the RangeFilterBuilder.
   */
  @Test
  public void testUpdateRangeFiltersMerge() {

    RangeFilter rangeFilter1 = new RangeFilter();
    rangeFilter1.setPropertyName("testItem.property1");

    RangeFilter rangeFilter2 = new RangeFilter();
    rangeFilter2.setPropertyName("testItem.property2");

    List<RangeFilter> rangeFilters = new ArrayList<>(2);
    rangeFilters.add(rangeFilter1);
    rangeFilters.add(rangeFilter2);

    RangeFilterBuilder[] rangeFilterBuilders = { mTestProductRangeFilterBuilder };

    List<RangeFilter> results =
      mStoreCartridgeTools.updateRangeFilters(rangeFilters, rangeFilterBuilders);

    assertEquals(4, results.size());
  }

  /**
   * Test the testUpdateRangeFilters method, ensuring that passing in 'null'
   * for the RangeFilters parameter returns the correct results (only RangeFilters
   * generated by the RangeFilterBuilder).
   */
  @Test
  public void testUpdateRangeFiltersNullRangeFilters() {

    RangeFilterBuilder[] rangeFilterBuilders = { mTestProductRangeFilterBuilder };

    List<RangeFilter> results =
      mStoreCartridgeTools.updateRangeFilters(null, rangeFilterBuilders);

    assertEquals(2, results.size());
  }

  /**
   * Test the testUpdateRangeFilters method, ensuring that passing in 'null'
   * for the RangeFilterBuilders parameter returns the correct results (only
   * existing RangeFilters).
   */
  @Test
  public void testUpdateRangeFiltersNullRangeFilterBuilders() {

    RangeFilter rangeFilter1 = new RangeFilter();
    rangeFilter1.setPropertyName("testItem.property1");

    RangeFilter rangeFilter2 = new RangeFilter();
    rangeFilter2.setPropertyName("testItem.property2");

    List<RangeFilter> rangeFilters = new ArrayList<>(2);
    rangeFilters.add(rangeFilter1);
    rangeFilters.add(rangeFilter2);

    List<RangeFilter> results =
      mStoreCartridgeTools.updateRangeFilters(rangeFilters, null);

    assertEquals(2, results.size());
  }

  /**
   * Test the testUpdateRangeFilters method, ensuring that existing RangeFilters
   * get merged with ones generated by the RangeFilterBuilder with no duplicates.
   */
  @Test
  public void testUpdateRangeFiltersNoDuplicates() {

    RangeFilter rangeFilter1 = new RangeFilter();
    rangeFilter1.setPropertyName("testItem.property1");

    // This RangeFilter shouldn't be duplicated in the results.
    RangeFilter rangeFilter2 = new RangeFilter();
    rangeFilter2.setPropertyName("product.startDate");

    List<RangeFilter> rangeFilters = new ArrayList<>(2);
    rangeFilters.add(rangeFilter1);
    rangeFilters.add(rangeFilter2);

    RangeFilterBuilder[] rangeFilterBuilders = { mTestProductRangeFilterBuilder };

    List<RangeFilter> results =
      mStoreCartridgeTools.updateRangeFilters(rangeFilters, rangeFilterBuilders);

    assertEquals(3, results.size());
  }

  /**
   * Test the validateCategoryNavigation method returns a valid path flag when the
   * navigation path is a valid category path.
   */
  @Test
  public void testValidateCategoryNavigationValidCategoryPath() throws CartridgeHandlerException {
    mStoreCartridgeTools.getCatalogNavigation().setCurrentCategory("catMen");
    mStoreCartridgeTools.getCatalogNavigation().setCurrentCategoryValid(true);
    assertEquals(true, mStoreCartridgeTools.validateCategoryNavigation());
  }

  /**
   * Test the validateCategoryNavigation method returns a valid path flag when the
   * navigation path is not category path.
   */
  @Test
  public void testValidateCategoryNavigationNoCategoryPath() throws CartridgeHandlerException {
    mStoreCartridgeTools.getCatalogNavigation().setCurrentCategory("");
    assertEquals(true, mStoreCartridgeTools.validateCategoryNavigation());
  }

  /**
   * Test the validateCategoryNavigation method returns a valid path flag when the
   * navigation path is not category path.
   */
  @Test
  public void testValidateCategoryNavigationInvalidCategoryPath() throws CartridgeHandlerException {
    mStoreCartridgeTools.getCatalogNavigation().setCurrentCategory("invalidCategory");
    mStoreCartridgeTools.getCatalogNavigation().setCurrentCategoryValid(false);
    assertEquals(false, mStoreCartridgeTools.validateCategoryNavigation());
  }

  /**
   * Ensure that the populateRecordSpecMdexRequestParams method populates
   * the MdexRecordQuery and FilterState objects with the correct values.
   */
  @Test
  public void testPopulateRecordSpecMdexRequestParams() {
    List<String> itemIds = new ArrayList<String>() {{
      add("product0001");
      add("product0002");
    }};

    RecordsMdexQuery recordsMdexQuery = new RecordsMdexQuery();
    FilterState filterState = new FilterState();

    mStoreCartridgeTools.populateRecordSpecMdexRequestParams(
      recordsMdexQuery, filterState, itemIds, "product.repositoryId");

    assertEquals(SubRecordsPerAggregateRecord.ALL,
      recordsMdexQuery.getSubRecordsPerAggregateRecord());

    assertEquals("OR(product.repositoryId:product0001,OR(product.repositoryId:product0002))",
      filterState.getRecordFilters().get(0));
  }

  /**
   * Test that the getItemIdToRecordSpecMap method creates a Map that contains a repositoryId
   * key with a corresponding record spec value.
   */
  @Test
  public void testGetItemIdToRecordSpecMapWithResults() throws CartridgeHandlerException {
    MockAggrERec aggrERec = new MockAggrERec();
    aggrERec.setSpec("clothing-sku-xsku2504__2..xprod2504.masterCatalog.en__US.plist3080003__plist3080002");

    MockPropertyMap propertMap = new MockPropertyMap();
    propertMap.addProperty(new MockProperty("allAncestors.displayName", "Jackets"));
    propertMap.addProperty(new MockProperty("allAncestors.displayName", "Women"));
    propertMap.addProperty(new MockProperty("allAncestors.repositoryId", "cat50001"));
    propertMap.addProperty(new MockProperty("allAncestors.repositoryId", "cat700012"));
    propertMap.addProperty(new MockProperty("allAncestors.repositoryId", "rootCategory"));
    propertMap.addProperty(new MockProperty("product.repositoryId", "xprod2504"));
    propertMap.addProperty(new MockProperty("record.id", "clothing-sku-xsku2504__2..xprod2504.masterCatalog.en__US.plist3080003__plist3080002"));

    MockERec erec = new MockERec();
    erec.setSpec("clothing-sku-xsku2504__2..xprod2504.masterCatalog.en__US.plist3080003__plist3080002");
    erec.setProperties(propertMap);

    aggrERec.setRepresentative(erec);

    MockAggrERecList aggrERecList = new MockAggrERecList();
    aggrERecList.append(aggrERec);

    MockNavigation navigation = new MockNavigation();
    navigation.setAggrERecs(aggrERecList);

    MockENEQueryResults eneQueryResults = new MockENEQueryResults();
    eneQueryResults.setNavigation(navigation);

    Map result =
      mStoreCartridgeTools.getItemIdToRecordSpecMap(eneQueryResults, "product.repositoryId");

    assertEquals("clothing-sku-xsku2504__2..xprod2504.masterCatalog.en__US.plist3080003__plist3080002",
      result.get("xprod2504"));
  }

  /**
   * Test that the getItemIdToRecordSpecMap method creates a Map that contains a repositoryId
   * key with a corresponding record spec value.
   */
  @Test
  public void testGetItemIdToRecordSpecMapNoResults() throws CartridgeHandlerException {
    MockAggrERecList aggrERecList = null;

    MockNavigation navigation = new MockNavigation();
    navigation.setAggrERecs(aggrERecList);

    MockENEQueryResults eneQueryResults = new MockENEQueryResults();
    eneQueryResults.setNavigation(navigation);

    Map result =
      mStoreCartridgeTools.getItemIdToRecordSpecMap(eneQueryResults, "product.repositoryId");

    assertNull(result);
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
