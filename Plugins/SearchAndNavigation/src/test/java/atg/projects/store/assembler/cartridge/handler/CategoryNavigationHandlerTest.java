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

import static org.junit.Assert.*;

import atg.commerce.endeca.cache.DimensionValueCache;
import atg.multisite.SiteContextException;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryUtils;
import atg.service.collections.validator.StartEndDateValidator;
import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.navigation.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;

import com.endeca.infront.assembler.CartridgeHandlerException;

import atg.adapter.gsa.GSARepository;
import atg.repository.RepositoryItem;
import atg.adapter.gsa.xml.TemplateParser;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;

import com.endeca.infront.navigation.model.MdexResource;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * This unit test will test the methods of the CategoryNavigationHandler class.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/handler/CategoryNavigationHandlerTest.java#1 $$Change: 1385662 $ 
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class CategoryNavigationHandlerTest {
  
  //----------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/handler/CategoryNavigationHandlerTest.java#1 $$Change: 1385662 $";
  
  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------
  
  /** The CategoryNavigationHandler that will be tested. */
  private static CategoryNavigationHandler mCategoryNavigationHandler = null;
  
  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;

  private static MockMdexRequestBroker mMdexRequestBroker = null;
  private static ContentItem mCartridgeConfig = null;
  private static SiteContextManager mSiteContextManager = null;
  private static GSARepository mProductCatalog = null;
  private static GSARepository mSiteRepository = null;

  private static String CAT_MEN = "Men";
  private static String CAT_MEN_SHOES = "Men Shoes";
  private static String CAT_MEN_SHIRTS = "Men Shirts";
  private static String CAT_FURNITURE = "Furniture";
  private static String CAT_SEATING = "Seating";
  private static String CAT_CHAIRS = "Chairs";

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------
  
  public static final String NAVIGATION_STATE_PATH =
    "/atg/endeca/assembler/cartridge/manager/NavigationState";
  public static final String NAVIGATION_STATE_BUILDER =
    "/atg/endeca/assembler/cartridge/manager/NavigationStateBuilder";
  
  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------
  
  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the CategoryNavigationHandler instance to be used in this test.
   * 
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException, RepositoryException, SiteContextException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Plugins.SearchAndNavigation" },
      CategoryNavigationHandlerTest.class,
      "CategoryNavigationHandlerTest",
      "/atg/Initial");
           
    DynamoHttpServletRequest request = setUpCurrentRequest();

      // Set up CategoryNavigationHandler.
    mCategoryNavigationHandler = (CategoryNavigationHandler) 
      request.resolveName("/atg/endeca/assembler/cartridge/handler/CategoryNavigation", true);  
    
    assertNotNull(mCategoryNavigationHandler);
      
    // Initialize repository and import sample data
    mProductCatalog = (GSARepository) mNucleus.resolveName("/atg/commerce/catalog/ProductCatalog", true);
    String[] dataFileNames = { "catalog.xml" };
    TemplateParser.loadTemplateFiles(mProductCatalog, 1, dataFileNames, true, new PrintWriter(System.out), null);

    mSiteRepository = (GSARepository) mNucleus.resolveName("/atg/multisite/SiteRepository", true);
    String[] siteDataFileNames = { "sites.xml" };
    TemplateParser.loadTemplateFiles(mSiteRepository, 1, siteDataFileNames,
      true, new PrintWriter(System.out), null);

    RepositoryItem storeSiteUS = mSiteRepository.getItem("storeSiteUS");

    mSiteContextManager =
      (SiteContextManager) mNucleus.resolveName("/atg/multisite/SiteContextManager", true);
    
    SiteContext sc = mSiteContextManager.getSiteContext(storeSiteUS.getRepositoryId());
    mSiteContextManager.pushSiteContext(sc);

    mMdexRequestBroker = new MockMdexRequestBroker(new MdexResource(), false);
    mCategoryNavigationHandler.setMdexRequestBroker(mMdexRequestBroker);
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
    
    mCategoryNavigationHandler = null;
    mProductCatalog = null;
    mSiteRepository = null;
    mSiteContextManager = null;
    mMdexRequestBroker = null;
    mCartridgeConfig = null;
  }

  /**
   * Re-initialize the cartridge configuration object before each test.
   */
  @Before
  public void setUpBeforeMethod() {

    if (mCartridgeConfig == null) {

      // Initialize the cartridge configuration to be used for all tests.
      mCartridgeConfig = new BasicContentItem();
    }

    List dimensionList = new ArrayList() {{
      add(Long.toString(CSAMockDataFactory.ID_CATEGORY_TYPE));
    }};

    mCategoryNavigationHandler.setUseUnindexedCategory(false);
    mCategoryNavigationHandler.setStartEndDateValidator(null);
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------
  
  //------------------------------------
  // Test the class setters and getters
  //------------------------------------

  /**
   * Test the useUnindexedCategory getter/setter.
   */
  @Test
  public void testSetGetUseUnindexedCategory() {
    mCategoryNavigationHandler.setUseUnindexedCategory(false);
    assertEquals(false, mCategoryNavigationHandler.isUseUnindexedCategory());
  }

  /**
   * Test the startEndDateValidator getter/setter.
   */
  @Test
  public void testSetGetStartEndDateValidator() {
    mCategoryNavigationHandler.setStartEndDateValidator(new StartEndDateValidator());
    assertNotNull(mCategoryNavigationHandler.getStartEndDateValidator());
    mCategoryNavigationHandler.setStartEndDateValidator(null);
  }

  /**
   * Test the rootNavigationCategoryPropertyName getter/setter.
   */
  @Test
  public void testSetGetRootNavigationCategoryPropertyName() {
    mCategoryNavigationHandler.setRootNavigationCategoryPropertyName("rootNavigationCategory");
    assertEquals("rootNavigationCategory", mCategoryNavigationHandler.getRootNavigationCategoryPropertyName());
  }

  //------------------------
  // Test the class methods
  //------------------------

  /**
   * Test the wrapConfig method.
   */
  @Test
  public void testWrapConfig() {
    assertNotNull(mCategoryNavigationHandler.wrapConfig(mCartridgeConfig));
  }

  /**
   * Test the process method for whenever category dimension doesn't exist.
   */
  @Test
  public void testProcessNoCategoryDimension() throws CartridgeHandlerException {
    mCategoryNavigationHandler.getDimensionValueCacheTools().createEmptyCache();
    ContentItem results = mCategoryNavigationHandler.process(mCartridgeConfig);
    assertEquals(0, ((List) results.get(CategoryNavigationHandler.CATEGORIES)).size());
  }
  
  /**
   * Test the process method for whenever a single category has no sub-categories.
   */
  @Test
  public void testProcessOneTopLevelNoSubCategory() throws RepositoryException, CartridgeHandlerException {
    clearCategories();

    MutableRepositoryItem item = mProductCatalog.createItem("catMen", "category");

    List<RepositoryItem> ancestors = new ArrayList<>();
    ancestors.add(mProductCatalog.getItem("rootCategory", "category"));

    Set catalog = new HashSet<>();
    catalog.add(mProductCatalog.getItem("masterCatalog", "catalog"));

    Set siteIds = new HashSet<>();
    siteIds.add("storeSiteUS");

    item.setPropertyValue("ancestorCategories", ancestors);
    item.setPropertyValue("displayNameDefault", "Men");
    item.setPropertyValue("computedCatalogs", catalog);
    item.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(item);

    List<RepositoryItem> rootCategoryfixedChildCategories = new ArrayList<>();
    rootCategoryfixedChildCategories.add(mProductCatalog.getItem("catMen", "category"));

    mProductCatalog.getItemForUpdate("rootCategory", "category")
      .setPropertyValue("fixedChildCategories", rootCategoryfixedChildCategories);

    List<String> categoriesToCache = new ArrayList<>();
    categoriesToCache.add(CAT_MEN);
    setupDimensionValueCache(categoriesToCache);

    ContentItem results = mCategoryNavigationHandler.process(mCartridgeConfig);

    assertNotNull(results);
    assertEquals(1, ((List) results.get("categories")).size());

    Map<String,Object> category = 
      (Map<String,Object>) ((List) results.get("categories")).get(0);
    
    Map<String,Object> topLevelCategory = 
      (Map<String,Object>) category.get(CategoryNavigationHandler.TOP_LEVEL_CATEGORY);
    
    List<Map<String,Object>> subCategories = 
      (List<Map<String,Object>>) category.get(CategoryNavigationHandler.SUB_CATEGORIES);
    
    assertEquals("Men", topLevelCategory.get(CategoryNavigationHandler.DISPLAY_NAME));
    
    assertEquals(0, subCategories.size());
    
    assertEquals("?N=" + Long.toString(CSAMockDataFactory.ID_MEN),
                 topLevelCategory.get("navigationState").toString().substring(0, 7));
  }
  
  /**
   * Test the process method for whenever a single category only has one sub-category.
   * For example:
   * 
   * <pre>
   *   Men           (top-level)
   *    |--Shirts    (sub-category)
   * </pre>
   */
  @Test
  public void testProcessOneTopLevelOneSubCategory() throws RepositoryException, CartridgeHandlerException {
    clearCategories();

    // Create the Men category.
    MutableRepositoryItem menItem = mProductCatalog.createItem("catMen", "category");

    List<RepositoryItem> ancestors = new ArrayList<>();
    ancestors.add(mProductCatalog.getItem("rootCategory", "category"));

    Set catalog = new HashSet<>();
    catalog.add(mProductCatalog.getItem("masterCatalog", "catalog"));

    Set siteIds = new HashSet<>();
    siteIds.add("storeSiteUS");

    menItem.setPropertyValue("ancestorCategories", ancestors);
    menItem.setPropertyValue("displayNameDefault", "Men");
    menItem.setPropertyValue("computedCatalogs", catalog);
    menItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(menItem);

    // Create the Men Shirts category.
    MutableRepositoryItem menShirtsItem = mProductCatalog.createItem("catMenShirts", "category");

    List<RepositoryItem> ancestors2 = new ArrayList<>();
    ancestors2.add(menItem);

    menShirtsItem.setPropertyValue("ancestorCategories", ancestors2);
    menShirtsItem.setPropertyValue("displayNameDefault", "Men Shirts");
    menShirtsItem.setPropertyValue("computedCatalogs", catalog);
    menShirtsItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(menShirtsItem);

    List<RepositoryItem> menFixedChildCategories = new ArrayList<>();
    menFixedChildCategories.add(menShirtsItem);

    mProductCatalog.getItemForUpdate("catMen", "category")
      .setPropertyValue("fixedChildCategories", menFixedChildCategories);

    List<RepositoryItem> rootCategoryFixedChildCategories = new ArrayList<>();
    rootCategoryFixedChildCategories.add(mProductCatalog.getItem("catMen", "category"));

    mProductCatalog.getItemForUpdate("rootCategory", "category")
      .setPropertyValue("fixedChildCategories", rootCategoryFixedChildCategories);

    List<String> categoriesToCache = new ArrayList<>();
    categoriesToCache.add(CAT_MEN);
    categoriesToCache.add(CAT_MEN_SHIRTS);
    setupDimensionValueCache(categoriesToCache);

    ContentItem results = mCategoryNavigationHandler.process(mCartridgeConfig);

    assertNotNull(results);
    assertEquals(1, ((List) results.get("categories")).size());
    
    Map<String,Object> category = 
      (Map<String,Object>) ((List) results.get("categories")).get(0);
    
    Map<String,Object> topLevelCategory = 
      (Map<String,Object>) category.get(mCategoryNavigationHandler.TOP_LEVEL_CATEGORY);
    
    List<Map<String,Object>> subCategories = 
      (List<Map<String,Object>>) category.get(mCategoryNavigationHandler.SUB_CATEGORIES);
    
    // Display names
    assertEquals("Men", topLevelCategory.get(CategoryNavigationHandler.DISPLAY_NAME));
    assertEquals("Men Shirts", subCategories.get(0).get(CategoryNavigationHandler.DISPLAY_NAME));
    
    // Navigation states.
    assertEquals("?N=" + Long.toString(CSAMockDataFactory.ID_MEN), 
                 topLevelCategory.get("navigationState").toString().substring(0, 7));
    assertEquals("?N=" + Long.toString(CSAMockDataFactory.ID_SHIRTS), 
                 subCategories.get(0).get("navigationState").toString().substring(0, 7));
  }

  /**
   * Test the process method for whenever a single category has two sub-categories.
   * For example:
   * 
   * <pre>
   *   Men           (top-level)
   *    |--Shirts    (sub-category)
   *    |--Shoes     (sub-category)
   * </pre>
   */
  @Test
  public void testProcessOneTopLevelTwoSubCategories() throws RepositoryException, CartridgeHandlerException {
    clearCategories();

    // Create the Men category.
    MutableRepositoryItem menItem = mProductCatalog.createItem("catMen", "category");

    List<RepositoryItem> ancestors = new ArrayList<>();
    ancestors.add(mProductCatalog.getItem("rootCategory", "category"));

    Set catalog = new HashSet<>();
    catalog.add(mProductCatalog.getItem("masterCatalog", "catalog"));

    Set siteIds = new HashSet<>();
    siteIds.add("storeSiteUS");

    menItem.setPropertyValue("ancestorCategories", ancestors);
    menItem.setPropertyValue("displayNameDefault", "Men");
    menItem.setPropertyValue("computedCatalogs", catalog);
    menItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(menItem);

    // Create the Men Shirts category.
    MutableRepositoryItem menShirtsItem = mProductCatalog.createItem("catMenShirts", "category");

    List<RepositoryItem> ancestors2 = new ArrayList<>();
    ancestors2.add(menItem);

    menShirtsItem.setPropertyValue("ancestorCategories", ancestors2);
    menShirtsItem.setPropertyValue("displayNameDefault", "Men Shirts");
    menShirtsItem.setPropertyValue("computedCatalogs", catalog);
    menShirtsItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(menShirtsItem);

    // Create the Men Shoes category.
    MutableRepositoryItem menShoesItem = mProductCatalog.createItem("catMenShoes", "category");

    List<RepositoryItem> ancestors3 = new ArrayList<>();
    ancestors3.add(menItem);

    menShoesItem.setPropertyValue("ancestorCategories", ancestors3);
    menShoesItem.setPropertyValue("displayNameDefault", "Men Shoes");
    menShoesItem.setPropertyValue("computedCatalogs", catalog);
    menShoesItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(menShoesItem);

    List<RepositoryItem> menFixedChildCategories = new ArrayList<>();
    menFixedChildCategories.add(menShirtsItem);
    menFixedChildCategories.add(menShoesItem);

    mProductCatalog.getItemForUpdate("catMen", "category")
      .setPropertyValue("fixedChildCategories", menFixedChildCategories);

    List<RepositoryItem> rootCategoryFixedChildCategories = new ArrayList<>();
    rootCategoryFixedChildCategories.add(mProductCatalog.getItem("catMen", "category"));

    mProductCatalog.getItemForUpdate("rootCategory", "category")
      .setPropertyValue("fixedChildCategories", rootCategoryFixedChildCategories);

    List<String> categoriesToCache = new ArrayList<>();
    categoriesToCache.add(CAT_MEN);
    categoriesToCache.add(CAT_MEN_SHIRTS);
    categoriesToCache.add(CAT_MEN_SHOES);
    setupDimensionValueCache(categoriesToCache);

    ContentItem results = mCategoryNavigationHandler.process(mCartridgeConfig);

    assertNotNull(results);
    assertEquals(1, ((List) results.get("categories")).size());
    
    Map<String,Object> category = 
      (Map<String,Object>) ((List) results.get("categories")).get(0);
    
    Map<String,Object> topLevelCategory = 
      (Map<String,Object>) category.get(mCategoryNavigationHandler.TOP_LEVEL_CATEGORY);
    
    List<Map<String,Object>> subCategories = 
      (List<Map<String,Object>>) category.get(mCategoryNavigationHandler.SUB_CATEGORIES);
    
    // Display names
    assertEquals("Men", topLevelCategory.get(CategoryNavigationHandler.DISPLAY_NAME));
    
    // Sub-category display names should be in alphabetical order.
    assertEquals("Men Shirts", subCategories.get(0).get(CategoryNavigationHandler.DISPLAY_NAME));
    assertEquals("Men Shoes", subCategories.get(1).get(CategoryNavigationHandler.DISPLAY_NAME));
    
    // Navigation states.
    assertEquals("?N=" + Long.toString(CSAMockDataFactory.ID_MEN), 
                 topLevelCategory.get("navigationState").toString().substring(0, 7));
    assertEquals("?N=" + Long.toString(CSAMockDataFactory.ID_SHIRTS), 
                 subCategories.get(0).get("navigationState").toString().substring(0, 7));
    assertEquals("?N=" + Long.toString(CSAMockDataFactory.ID_SHOES), 
        subCategories.get(1).get("navigationState").toString().substring(0, 7));
  }

  /**
   * Test the process method for whenever a single category has a sub-category and that
   * sub-category has it's own sub-category. Only the top-level's direct sub-category should
   * be returned. For example:
   * 
   * <pre>
   *   Furniture         (top-level)
   *    |--Seating       (sub-category)
   *         |--Chairs   (sub-category) - This 2nd level sub-category shouldn't exist in results.
   * </pre>
   */
  @Test
  public void testProcessOneTopLevelOneSubCategoryWithSubCategory() throws RepositoryException, CartridgeHandlerException {
    clearCategories();

    // Create the Furniture category.
    MutableRepositoryItem furnitureItem = mProductCatalog.createItem("catFurniture", "category");

    List<RepositoryItem> ancestors = new ArrayList<>();
    ancestors.add(mProductCatalog.getItem("rootCategory", "category"));

    Set catalog = new HashSet<>();
    catalog.add(mProductCatalog.getItem("masterCatalog", "catalog"));

    Set siteIds = new HashSet<>();
    siteIds.add("storeSiteUS");

    furnitureItem.setPropertyValue("ancestorCategories", ancestors);
    furnitureItem.setPropertyValue("displayNameDefault", "Furniture");
    furnitureItem.setPropertyValue("computedCatalogs", catalog);
    furnitureItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(furnitureItem);

    // Create the Seating category.
    MutableRepositoryItem seatingItem = mProductCatalog.createItem("homeStoreSeating", "category");

    List<RepositoryItem> ancestors2 = new ArrayList<>();
    ancestors2.add(furnitureItem);

    seatingItem.setPropertyValue("ancestorCategories", ancestors2);
    seatingItem.setPropertyValue("displayNameDefault", "Seating");
    seatingItem.setPropertyValue("computedCatalogs", catalog);
    seatingItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(seatingItem);

    // Create the Chairs category.
    MutableRepositoryItem chairsItem = mProductCatalog.createItem("homeStoreChairs", "category");

    List<RepositoryItem> ancestors3 = new ArrayList<>();
    ancestors3.add(seatingItem);

    chairsItem.setPropertyValue("ancestorCategories", ancestors3);
    chairsItem.setPropertyValue("displayNameDefault", "Chairs");
    chairsItem.setPropertyValue("computedCatalogs", catalog);
    chairsItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(chairsItem);

    List<RepositoryItem> furnitureChildCategories = new ArrayList<>();
    furnitureChildCategories.add(seatingItem);

    List<RepositoryItem> seatingChildCategories = new ArrayList<>();
    seatingChildCategories.add(chairsItem);

    mProductCatalog.getItemForUpdate("catFurniture", "category")
      .setPropertyValue("fixedChildCategories", furnitureChildCategories);

    mProductCatalog.getItemForUpdate("homeStoreSeating", "category")
      .setPropertyValue("fixedChildCategories", seatingChildCategories);

    List<RepositoryItem> rootCategoryFixedChildCategories = new ArrayList<>();
    rootCategoryFixedChildCategories.add(mProductCatalog.getItem("catFurniture", "category"));

    mProductCatalog.getItemForUpdate("rootCategory", "category")
      .setPropertyValue("fixedChildCategories", rootCategoryFixedChildCategories);

    List<String> categoriesToCache = new ArrayList<>();
    categoriesToCache.add(CAT_FURNITURE);
    categoriesToCache.add(CAT_SEATING);
    categoriesToCache.add(CAT_CHAIRS);
    setupDimensionValueCache(categoriesToCache);

    ContentItem results = mCategoryNavigationHandler.process(mCartridgeConfig);

    assertNotNull(results);
    assertEquals(1, ((List) results.get("categories")).size());
    
    Map<String,Object> category = 
      (Map<String,Object>) ((List) results.get("categories")).get(0);
    
    Map<String,Object> topLevelCategory = 
      (Map<String,Object>) category.get(mCategoryNavigationHandler.TOP_LEVEL_CATEGORY);
    
    List<Map<String,Object>> subCategories = 
      (List<Map<String,Object>>) category.get(mCategoryNavigationHandler.SUB_CATEGORIES);
    
    // Display names
    assertEquals("Furniture", topLevelCategory.get(CategoryNavigationHandler.DISPLAY_NAME));
    
    // Chairs sub-category shouldn't exist in results.
    assertEquals(1, subCategories.size());
    assertEquals("Seating", subCategories.get(0).get(CategoryNavigationHandler.DISPLAY_NAME));
    
    // Navigation states.
    assertEquals("?N=" + Long.toString(CSAMockDataFactory.ID_FURNITURE), 
                 topLevelCategory.get("navigationState").toString().substring(0, 7));
    assertEquals("?N=" + Long.toString(CSAMockDataFactory.ID_SEATING), 
                 subCategories.get(0).get("navigationState").toString().substring(0, 7));
  }

  /**
   * Test that when a navigation state doesn't exist, the unindexed category is used.
   */
  @Test
  public void testUnindexedTopLevelCategory() throws RepositoryException, CartridgeHandlerException {
    mCategoryNavigationHandler.setUseUnindexedCategory(true);

    clearCategories();

    // Create the Men category.
    MutableRepositoryItem menItem = mProductCatalog.createItem("catMen", "category");

    List<RepositoryItem> ancestors = new ArrayList<>();
    ancestors.add(mProductCatalog.getItem("rootCategory", "category"));

    Set catalog = new HashSet<>();
    catalog.add(mProductCatalog.getItem("masterCatalog", "catalog"));

    Set siteIds = new HashSet<>();
    siteIds.add("storeSiteUS");

    menItem.setPropertyValue("ancestorCategories", ancestors);
    menItem.setPropertyValue("displayNameDefault", "Men");
    menItem.setPropertyValue("computedCatalogs", catalog);
    menItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(menItem);

    // Create the Furniture category.
    MutableRepositoryItem furnitureItem = mProductCatalog.createItem("catFurniture", "category");

    List<RepositoryItem> ancestors2 = new ArrayList<>();
    ancestors2.add(mProductCatalog.getItem("rootCategory", "category"));

    furnitureItem.setPropertyValue("ancestorCategories", ancestors);
    furnitureItem.setPropertyValue("displayNameDefault", "Furniture");
    furnitureItem.setPropertyValue("computedCatalogs", catalog);
    furnitureItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(furnitureItem);

    List<RepositoryItem> rootCategoryFixedChildCategories = new ArrayList<>();
    rootCategoryFixedChildCategories.add(furnitureItem);
    rootCategoryFixedChildCategories.add(menItem);

    mProductCatalog.getItemForUpdate("rootCategory", "category")
      .setPropertyValue("fixedChildCategories", rootCategoryFixedChildCategories);

    List<String> categoriesToCache = new ArrayList<>();
    categoriesToCache.add(CAT_FURNITURE);

    setupDimensionValueCache(categoriesToCache);

    ContentItem results = mCategoryNavigationHandler.process(mCartridgeConfig);

    // categories[1] -> topLevelCategory -> navigationState - This will NOT be an unindexed category.
    assertEquals("?N=8030",
      ((Map) ((Map) ((List) results.get("categories")).get(0)).get("topLevelCategory")).get("navigationState"));

    // categories[0] -> topLevelCategory -> navigationState - This will be an unindexed category.
    assertEquals("browse?categoryId=catMen",
      ((Map) ((Map) ((List) results.get("categories")).get(1)).get("topLevelCategory")).get("navigationState"));
  }

  /**
   * Test that when a navigation state doesn't exist, the unindexed category is used.
   */
  @Test
  public void testUnindexedSubCategory() throws RepositoryException, CartridgeHandlerException {
    mCategoryNavigationHandler.setUseUnindexedCategory(true);

    clearCategories();

    // Create the Men category.
    MutableRepositoryItem menItem = mProductCatalog.createItem("catMen", "category");

    List<RepositoryItem> ancestors = new ArrayList<>();
    ancestors.add(mProductCatalog.getItem("rootCategory", "category"));

    Set catalog = new HashSet<>();
    catalog.add(mProductCatalog.getItem("masterCatalog", "catalog"));

    Set siteIds = new HashSet<>();
    siteIds.add("storeSiteUS");

    menItem.setPropertyValue("ancestorCategories", ancestors);
    menItem.setPropertyValue("displayNameDefault", "Men");
    menItem.setPropertyValue("computedCatalogs", catalog);
    menItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(menItem);

    // Create the Men Shirts category.
    MutableRepositoryItem menShirtsItem = mProductCatalog.createItem("catMenShirts", "category");

    List<RepositoryItem> ancestors2 = new ArrayList<>();
    ancestors2.add(menItem);

    menShirtsItem.setPropertyValue("ancestorCategories", ancestors2);
    menShirtsItem.setPropertyValue("displayNameDefault", "Men Shirts");
    menShirtsItem.setPropertyValue("computedCatalogs", catalog);
    menShirtsItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(menShirtsItem);

    List<RepositoryItem> menFixedChildCategories = new ArrayList<>();
    menFixedChildCategories.add(menShirtsItem);

    mProductCatalog.getItemForUpdate("catMen", "category")
      .setPropertyValue("fixedChildCategories", menFixedChildCategories);

    List<RepositoryItem> rootCategoryFixedChildCategories = new ArrayList<>();
    rootCategoryFixedChildCategories.add(mProductCatalog.getItem("catMen", "category"));

    mProductCatalog.getItemForUpdate("rootCategory", "category")
      .setPropertyValue("fixedChildCategories", rootCategoryFixedChildCategories);

    List<String> categoriesToCache = new ArrayList<>();
    categoriesToCache.add(CAT_MEN);
    setupDimensionValueCache(categoriesToCache);

    ContentItem results = mCategoryNavigationHandler.process(mCartridgeConfig);

    // categories[0] -> topLevelCategory -> navigationState - This will NOT be an unindexed category.
    assertEquals("?N=" + Long.toString(CSAMockDataFactory.ID_MEN),
      ((Map) ((Map) ((List) results.get("categories")).get(0)).get("topLevelCategory")).get("navigationState"));

    // categories[0] -> subCategories[0] -> navigationState - This will be an unindexed category.
    assertEquals("browse?categoryId=catMenShirts",
      ((Map) ((List) ((Map) ((List) results.get("categories")).get(0)).get("subCategories")).get(0)).get("navigationState"));
  }

  /**
   * Ensure that a top-level category with an invalid end date doesn't get added to the results.
   */
  @Test
  public void testInvalidTopLevelCategoryStartEndDate() throws RepositoryException, CartridgeHandlerException {
    DynamoHttpServletRequest request = setUpCurrentRequest();

    StartEndDateValidator startEndDateValidator = (StartEndDateValidator)
      request.resolveName("/atg/store/collections/validator/StartEndDateValidator", true);

    mCategoryNavigationHandler.setStartEndDateValidator(startEndDateValidator);

    clearCategories();

    // Create the Men category.
    MutableRepositoryItem menItem = mProductCatalog.createItem("catMen", "category");

    List<RepositoryItem> ancestors = new ArrayList<>();
    ancestors.add(mProductCatalog.getItem("rootCategory", "category"));

    Set catalog = new HashSet<>();
    catalog.add(mProductCatalog.getItem("masterCatalog", "catalog"));

    Set siteIds = new HashSet<>();
    siteIds.add("storeSiteUS");

    menItem.setPropertyValue("ancestorCategories", ancestors);
    menItem.setPropertyValue("displayNameDefault", "Men");
    menItem.setPropertyValue("computedCatalogs", catalog);
    menItem.setPropertyValue("siteIds", siteIds);

    // End date in the past.
    menItem.setPropertyValue("endDate", new Date(System.currentTimeMillis() - 12345678));

    mProductCatalog.addItem(menItem);

    // Create the Men Shirts category.
    MutableRepositoryItem menShirtsItem = mProductCatalog.createItem("catMenShirts", "category");

    List<RepositoryItem> ancestors2 = new ArrayList<>();
    ancestors2.add(menItem);

    menShirtsItem.setPropertyValue("ancestorCategories", ancestors2);
    menShirtsItem.setPropertyValue("displayNameDefault", "Men Shirts");
    menShirtsItem.setPropertyValue("computedCatalogs", catalog);
    menShirtsItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(menShirtsItem);

    List<RepositoryItem> menFixedChildCategories = new ArrayList<>();
    menFixedChildCategories.add(menShirtsItem);

    mProductCatalog.getItemForUpdate("catMen", "category")
      .setPropertyValue("fixedChildCategories", menFixedChildCategories);

    List<RepositoryItem> rootCategoryFixedChildCategories = new ArrayList<>();
    rootCategoryFixedChildCategories.add(mProductCatalog.getItem("catMen", "category"));

    mProductCatalog.getItemForUpdate("rootCategory", "category")
      .setPropertyValue("fixedChildCategories", rootCategoryFixedChildCategories);

    List<String> categoriesToCache = new ArrayList<>();
    categoriesToCache.add(CAT_MEN);
    categoriesToCache.add(CAT_MEN_SHIRTS);
    setupDimensionValueCache(categoriesToCache);

    ContentItem results = mCategoryNavigationHandler.process(mCartridgeConfig);

    assertEquals(0, ((List) results.get("categories")).size());
  }

  /**
   * Ensure that a sub-category with an invalid end date doesn't get added to the results.
   */
  @Test
  public void testInvalidSubCategoryStartEndDate() throws RepositoryException, CartridgeHandlerException {
    DynamoHttpServletRequest request = setUpCurrentRequest();

    StartEndDateValidator startEndDateValidator = (StartEndDateValidator)
      request.resolveName("/atg/store/collections/validator/StartEndDateValidator", true);

    mCategoryNavigationHandler.setStartEndDateValidator(startEndDateValidator);

    clearCategories();

    // Create the Men category.
    MutableRepositoryItem menItem = mProductCatalog.createItem("catMen", "category");

    List<RepositoryItem> ancestors = new ArrayList<>();
    ancestors.add(mProductCatalog.getItem("rootCategory", "category"));

    Set catalog = new HashSet<>();
    catalog.add(mProductCatalog.getItem("masterCatalog", "catalog"));

    Set siteIds = new HashSet<>();
    siteIds.add("storeSiteUS");

    menItem.setPropertyValue("ancestorCategories", ancestors);
    menItem.setPropertyValue("displayNameDefault", "Men");
    menItem.setPropertyValue("computedCatalogs", catalog);
    menItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(menItem);

    // Create the Men Shirts category.
    MutableRepositoryItem menShirtsItem = mProductCatalog.createItem("catMenShirts", "category");

    List<RepositoryItem> ancestors2 = new ArrayList<>();
    ancestors2.add(menItem);

    menShirtsItem.setPropertyValue("ancestorCategories", ancestors2);
    menShirtsItem.setPropertyValue("displayNameDefault", "Men Shirts");
    menShirtsItem.setPropertyValue("computedCatalogs", catalog);
    menShirtsItem.setPropertyValue("siteIds", siteIds);

    // Future start date.
    menShirtsItem.setPropertyValue("startDate", new Date(System.currentTimeMillis() + 12345678));

    mProductCatalog.addItem(menShirtsItem);

    List<RepositoryItem> menFixedChildCategories = new ArrayList<>();
    menFixedChildCategories.add(menShirtsItem);

    mProductCatalog.getItemForUpdate("catMen", "category")
      .setPropertyValue("fixedChildCategories", menFixedChildCategories);

    List<RepositoryItem> rootCategoryFixedChildCategories = new ArrayList<>();
    rootCategoryFixedChildCategories.add(mProductCatalog.getItem("catMen", "category"));

    mProductCatalog.getItemForUpdate("rootCategory", "category")
      .setPropertyValue("fixedChildCategories", rootCategoryFixedChildCategories);

    List<String> categoriesToCache = new ArrayList<>();
    categoriesToCache.add(CAT_MEN);
    categoriesToCache.add(CAT_MEN_SHIRTS);
    setupDimensionValueCache(categoriesToCache);

    ContentItem results = mCategoryNavigationHandler.process(mCartridgeConfig);

    assertEquals(0,
      ((List) ((Map) ((List) results.get("categories")).get(0)).get("subCategories")).size());
  }

  /**
   * Test the process method for whenever a single category has two sub-categories.
   * For example:
   *
   * <pre>
   *   Furniture         (top-level)          Men           (top-level)
   *       |--Seating    (sub-category)        |--Shirts    (sub-category)
   * </pre>
   */
  @Test
  public void testProcessTwoTopLevelTwoSubCategories() throws RepositoryException, CartridgeHandlerException {
    clearCategories();

    // Create the Men category.
    MutableRepositoryItem menItem = mProductCatalog.createItem("catMen", "category");

    List<RepositoryItem> ancestors = new ArrayList<>();
    ancestors.add(mProductCatalog.getItem("rootCategory", "category"));

    Set catalog = new HashSet<>();
    catalog.add(mProductCatalog.getItem("masterCatalog", "catalog"));

    Set siteIds = new HashSet<>();
    siteIds.add("storeSiteUS");

    menItem.setPropertyValue("ancestorCategories", ancestors);
    menItem.setPropertyValue("displayNameDefault", "Men");
    menItem.setPropertyValue("computedCatalogs", catalog);
    menItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(menItem);

    // Create the Men Shirts category.
    MutableRepositoryItem menShirtsItem = mProductCatalog.createItem("catMenShirts", "category");

    List<RepositoryItem> ancestors2 = new ArrayList<>();
    ancestors2.add(menItem);

    menShirtsItem.setPropertyValue("ancestorCategories", ancestors2);
    menShirtsItem.setPropertyValue("displayNameDefault", "Men Shirts");
    menShirtsItem.setPropertyValue("computedCatalogs", catalog);
    menShirtsItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(menShirtsItem);

    // Create the Furniture category.
    MutableRepositoryItem furnitureItem = mProductCatalog.createItem("catFurniture", "category");

    List<RepositoryItem> ancestors3 = new ArrayList<>();
    ancestors3.add(mProductCatalog.getItem("rootCategory", "category"));

    furnitureItem.setPropertyValue("ancestorCategories", ancestors3);
    furnitureItem.setPropertyValue("displayNameDefault", "Furniture");
    furnitureItem.setPropertyValue("computedCatalogs", catalog);
    furnitureItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(furnitureItem);

    // Create the Seating category.
    MutableRepositoryItem seatingItem = mProductCatalog.createItem("homeStoreSeating", "category");

    List<RepositoryItem> ancestors4 = new ArrayList<>();
    ancestors4.add(furnitureItem);

    seatingItem.setPropertyValue("ancestorCategories", ancestors4);
    seatingItem.setPropertyValue("displayNameDefault", "Seating");
    seatingItem.setPropertyValue("computedCatalogs", catalog);
    seatingItem.setPropertyValue("siteIds", siteIds);

    mProductCatalog.addItem(seatingItem);

    List<RepositoryItem> menFixedChildCategories = new ArrayList<>();
    menFixedChildCategories.add(menShirtsItem);

    mProductCatalog.getItemForUpdate("catMen", "category")
      .setPropertyValue("fixedChildCategories", menFixedChildCategories);

    List<RepositoryItem> furnitureFixedChildCategories = new ArrayList<>();
    furnitureFixedChildCategories.add(seatingItem);

    mProductCatalog.getItemForUpdate("catFurniture", "category")
      .setPropertyValue("fixedChildCategories", furnitureFixedChildCategories);

    List<RepositoryItem> rootCategoryFixedChildCategories = new ArrayList<>();
    rootCategoryFixedChildCategories.add(mProductCatalog.getItem("catMen", "category"));
    rootCategoryFixedChildCategories.add(mProductCatalog.getItem("catFurniture", "category"));

    mProductCatalog.getItemForUpdate("rootCategory", "category")
      .setPropertyValue("fixedChildCategories", rootCategoryFixedChildCategories);

    List<String> categoriesToCache = new ArrayList<>();
    categoriesToCache.add(CAT_MEN);
    categoriesToCache.add(CAT_MEN_SHIRTS);
    categoriesToCache.add(CAT_FURNITURE);
    categoriesToCache.add(CAT_SEATING);
    setupDimensionValueCache(categoriesToCache);

    ContentItem results = mCategoryNavigationHandler.process(mCartridgeConfig);

    assertNotNull(results);
    assertEquals(2, ((List) results.get("categories")).size());

    // Ensure that the 'Furniture' top-level category and 'Seating' sub-category have been
    // included as the first categories in the category list, as the top-level categories
    // should be returned in an alphabetical order.

    Map<String,Object> category =
      (Map<String,Object>) ((List) results.get("categories")).get(1);

    Map<String,Object> topLevelCategory =
      (Map<String,Object>) category.get(mCategoryNavigationHandler.TOP_LEVEL_CATEGORY);

    List<Map<String,Object>> subCategories =
      (List<Map<String,Object>>) category.get(mCategoryNavigationHandler.SUB_CATEGORIES);

    // Display names
    assertEquals("Furniture", topLevelCategory.get(CategoryNavigationHandler.DISPLAY_NAME));
    assertEquals("Seating", subCategories.get(0).get(CategoryNavigationHandler.DISPLAY_NAME));

    // Navigation states.
    assertEquals("?N=" + Long.toString(CSAMockDataFactory.ID_FURNITURE),
                 topLevelCategory.get("navigationState").toString().substring(0, 7));
    assertEquals("?N=" + Long.toString(CSAMockDataFactory.ID_SEATING),
                 subCategories.get(0).get("navigationState").toString().substring(0, 7));

    // Ensure that the 'Men' top-level category and 'Shirts' sub-category have been
    // included as the next categories in the category list, as the top-level categories
    // should be returned in an alphbetical order.

    category =
      (Map<String,Object>) ((List) results.get("categories")).get(0);

    topLevelCategory =
      (Map<String,Object>) category.get(mCategoryNavigationHandler.TOP_LEVEL_CATEGORY);

    subCategories =
      (List<Map<String,Object>>) category.get(mCategoryNavigationHandler.SUB_CATEGORIES);

    // Display names
    assertEquals("Men", topLevelCategory.get(CategoryNavigationHandler.DISPLAY_NAME));
    assertEquals("Men Shirts", subCategories.get(0).get(CategoryNavigationHandler.DISPLAY_NAME));

    // Navigation states.
    assertEquals("?N=" + Long.toString(CSAMockDataFactory.ID_MEN),
                 topLevelCategory.get("navigationState").toString().substring(0, 7));
    assertEquals("?N=" + Long.toString(CSAMockDataFactory.ID_SHIRTS),
                 subCategories.get(0).get("navigationState").toString().substring(0, 7));
  }

  /**
   * Test the createCategory method.
   */
  @Test
  public void testCreateCategory() {
    Map<String, Object> category = mCategoryNavigationHandler.createCategory("category1", "?N=12345", "10001");
    assertEquals("category1", category.get(CategoryNavigationHandler.DISPLAY_NAME));
    assertEquals("?N=12345", category.get(CategoryNavigationHandler.NAVIGATION_STATE));
    assertEquals("10001", category.get(CategoryNavigationHandler.CATEGORY_ID));
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
   * Remove all categories created in the test methods from the repository.
   */
  public static void clearCategories() throws RepositoryException {
    MutableRepositoryItem men = mProductCatalog.getItemForUpdate("catMen", "category");
    MutableRepositoryItem menShoes = mProductCatalog.getItemForUpdate("catShoes", "category");
    MutableRepositoryItem menShirts = mProductCatalog.getItemForUpdate("catMenShirts", "category");
    MutableRepositoryItem furniture = mProductCatalog.getItemForUpdate("catFurniture", "category");
    MutableRepositoryItem seating = mProductCatalog.getItemForUpdate("homeStoreSeating", "category");
    MutableRepositoryItem chairs = mProductCatalog.getItemForUpdate("homeStoreChairs", "category");

    if (chairs != null) {
      RepositoryUtils.removeReferencesToItem(seating);
      RepositoryUtils.removeItem(seating);
    }

    if (seating != null) {
      RepositoryUtils.removeReferencesToItem(chairs);
      RepositoryUtils.removeItem(chairs);
    }
    if (menShirts != null) {
      RepositoryUtils.removeReferencesToItem(menShirts);
      RepositoryUtils.removeItem(menShirts);
    }

    if (menShoes != null) {
      RepositoryUtils.removeReferencesToItem(menShoes);
      RepositoryUtils.removeItem(menShoes);
    }

    if (men != null) {
      RepositoryUtils.removeReferencesToItem(men);
      RepositoryUtils.removeItem(men);
    }

    if (furniture != null) {
      RepositoryUtils.removeReferencesToItem(furniture);
      RepositoryUtils.removeItem(furniture);
    }
  }

  /**
   * Set-up a mock DimensionValueCache.
   *
   * @param pCategories
   *   The categories that should be added to the cache.
   */
  public static void setupDimensionValueCache(List<String> pCategories) {
    DimensionValueCache cache = mCategoryNavigationHandler.getDimensionValueCacheTools().getCache();
    cache.flush();

    // Add the root category.
    cache.put("rootCategory", "8022L", "?N=" + "8022L", null);

    // Add 'Men' category.
    if (pCategories.contains(CAT_MEN)) {
      List<String> menAncestors = new ArrayList<>();
      menAncestors.add("rootCategory");

      cache
        .put("catMen", "8025L", "?N=" + Long.toString(CSAMockDataFactory.ID_MEN), menAncestors);
    }

    // Add 'Men Shirts' category.
    if (pCategories.contains(CAT_MEN_SHIRTS)) {
      List<String> menShirtsAncestors = new ArrayList<>();
      menShirtsAncestors.add("catMen");

      cache
        .put("catMenShirts", "8027L", "?N=" + Long.toString(CSAMockDataFactory.ID_SHIRTS), menShirtsAncestors);
    }

    // Add 'Men Shoes' category.
    if (pCategories.contains(CAT_MEN_SHOES)) {
      List<String> menShoesAncestors = new ArrayList<>();
      menShoesAncestors.add("catMen");

      cache
        .put("catMenShoes", "8026L", "?N=" + Long.toString(CSAMockDataFactory.ID_SHOES), menShoesAncestors);
    }

    // Add 'Furniture' category.
    if (pCategories.contains(CAT_FURNITURE)) {
      List<String> furnitureAncestors = new ArrayList<>();
      furnitureAncestors.add("rootCategory");

      mCategoryNavigationHandler.getDimensionValueCacheTools().getCache()
        .put("catFurniture", "8030L", "?N=" + Long.toString(CSAMockDataFactory.ID_FURNITURE), furnitureAncestors);
    }

    // Add 'Seating' category.
    if (pCategories.contains(CAT_SEATING)) {
      List<String> seatingAncestors = new ArrayList<>();
      seatingAncestors.add("catFurniture");

      cache
        .put("homeStoreSeating", "8031L", "?N=" + Long.toString(CSAMockDataFactory.ID_SEATING), seatingAncestors);
    }

    // Add 'Chairs' category.
    if (pCategories.contains(CAT_CHAIRS)) {
      List<String> chairsAncestors = new ArrayList<>();
      chairsAncestors.add("homeStoreSeating");

      cache
        .put("homeStoreChairs", "8032L", "?N=" + Long.toString(CSAMockDataFactory.ID_CHAIRS), chairsAncestors);
    }
  }

}
