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
package atg.projects.store.shipping;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderManager;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.ShippingPricingEngine;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.order.purchase.ShippingInfoFormHandler;
import atg.projects.store.shipping.ShippingTools;
import atg.projects.store.userprofiling.StoreProfileTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import org.junit.*;

import javax.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for atg.projects.store.shipping.ShippingTools.
 *
 * @author Oracle
 */
public class ShippingToolsTest {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/test/java/atg/projects/store/shipping/ShippingToolsTest.java#1 $$Change: 1385662 $";


  private static final String SHIPPING_TOOLS_PATH = "/atg/store/shipping/ShippingTools";
  private static final String PROFILE_TOOLS_PATH = "/atg/userprofiling/ProfileTools";
  private static final String PROFILE = "/atg/userprofiling/Profile";
  private static final String SHOPPING_CART_PATH="/atg/commerce/ShoppingCart";
  private static final String PRICING_MODEL_HOLDER_PATH="/atg/commerce/pricing/UserPricingModels";
  public static final String ORDER_MANAGER_PATH = "/atg/commerce/order/OrderManager";
  public static final String ORDER_REPOSITORY_PATH = "/atg/commerce/order/OrderRepository";
  
  private static Nucleus mNucleus = null;
  private static ShippingTools mShippingTools = null;
  private static StoreProfileTools mStoreProfileTools = null;
  private static OrderHolder mShoppingCart = null;
  private static PricingModelHolder mPricingModelHolder = null;
  private static GSARepository mOrderRepository = null;
  private static GSARepository mSiteRepository = null;
  private static OrderManager mOrderManager = null;
  private static GSARepository mProductCatalog = null;
  private static GSARepository mInventoryRepository = null;
  private static GSARepository mPriceList = null;

  
  /**
   * One time setup.
   */
  @BeforeClass
  public static void beforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[]{"CommerceAccelerator.Base", "CommerceAccelerator.Plugins.Checkout"},
        ShippingToolsTest.class, "ShippingToolsTest", SHIPPING_TOOLS_PATH);

    mShippingTools = (ShippingTools) mNucleus.resolveName(SHIPPING_TOOLS_PATH, true);
    assertNotNull(mShippingTools);
    mStoreProfileTools = (StoreProfileTools) mNucleus.resolveName(PROFILE_TOOLS_PATH, true);
    assertNotNull(mStoreProfileTools);
    
    mOrderManager = (OrderManager) mNucleus.resolveName(ORDER_MANAGER_PATH, true);
    assertNotNull(mOrderManager);
    
    // Initialize catalog repository and import sample data
    mProductCatalog = (GSARepository)
        mNucleus.resolveName("/atg/commerce/catalog/ProductCatalog", true);
    assertNotNull(mProductCatalog);
    String[] dataFileNames = { "catalog.xml" };
    TemplateParser.loadTemplateFiles(mProductCatalog, 1, dataFileNames, true,
      new PrintWriter(System.out), null);

    // Initialize the inventory repository and import the sample data
    mInventoryRepository =
      (GSARepository) mNucleus.resolveName("/atg/commerce/inventory/InventoryRepository", true);
    assertNotNull(mInventoryRepository);
    String[] inventoryDataFiles = { "inventoryData.xml" };
    TemplateParser.loadTemplateFiles(mInventoryRepository, 1, inventoryDataFiles, true,
      new PrintWriter(System.out), null);
    
    // Initialize order repository and import sample data.
    mOrderRepository = (GSARepository) mNucleus.resolveName(ORDER_REPOSITORY_PATH, true);
    assertNotNull(mOrderRepository);
    String[] orderDataFileNames = { "orders.xml", "sites.xml" };
    TemplateParser.loadTemplateFiles(mOrderRepository, 1, orderDataFileNames, true,
      new PrintWriter(System.out), null);
 
    // Initialize site repository and import sample data.    
    mSiteRepository = 
      (GSARepository) mNucleus.resolveName("/atg/multisite/SiteRepository", true);
    assertNotNull(mSiteRepository);
    String[] siteDataFileNames = { "sites.xml" };
    TemplateParser.loadTemplateFiles(mSiteRepository, 1, siteDataFileNames, true,
      new PrintWriter(System.out), null);

    // Initialize price list repository and import sample data.
    mPriceList = 
      (GSARepository) mNucleus.resolveName("/atg/commerce/pricing/priceLists/PriceLists", true);
    assertNotNull(mPriceList);
    String[] priceDataFileNames = { "pricelists.xml" };
    TemplateParser.loadTemplateFiles(mPriceList, 1, priceDataFileNames, true,
      new PrintWriter(System.out), null);
  }

  /**
   * One time tear down.
   */
  @AfterClass
  public static void afterClass() throws IOException, ServiceException {
    if(mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
      mShippingTools=null;
      mOrderRepository = null;
      mSiteRepository = null;
      mStoreProfileTools = null;
      mOrderManager = null;
      mProductCatalog = null;
      mInventoryRepository = null;
      mPriceList = null;
      mNucleus = null;
    }
  }

  /**
   * Set up a new profile every time
   */
  @Before
  public void before() throws RepositoryException, CommerceException {
    // Set the current request & order.
    ServletTestUtils utils = new ServletTestUtils();
    DynamoHttpServletRequest request = 
      utils.createDynamoHttpServletRequestForSession(
        mNucleus, "mySessionId", "new");

    ServletUtil.setCurrentRequest(request);
    Profile profile = (Profile) request.resolveName(PROFILE);
    profile.setPropertyValue("priceList", mPriceList.getItem("listPrices"));
    ServletUtil.setCurrentUserProfile(profile);
    mStoreProfileTools.createNewUser("110000", "user", profile);

    Order order = mOrderManager.loadOrder("o200002");

    mPricingModelHolder = (PricingModelHolder)request.resolveName(PRICING_MODEL_HOLDER_PATH, true);
    assertNotNull(mPricingModelHolder);
    mShoppingCart = (OrderHolder) request.resolveName(SHOPPING_CART_PATH, true);
    assertNotNull(mShoppingCart);
    mShoppingCart.setCurrent(order);
  }
  
  /**
   * Test the setShippingPricingEngine and getShippingPricingEngine methods.
   */
  @Test
  public void testGetShippingPricingEngine() {
    ShippingPricingEngine shippingPricingEngine = mShippingTools.getShippingPricingEngine();
    assertNotNull(shippingPricingEngine);
    mShippingTools.setShippingPricingEngine(shippingPricingEngine);
    assertNotNull(mShippingTools.getShippingPricingEngine());
  }
  
  /**
   * Test the setPricingTools and getPricingTools methods.
   */
  @Test
  public void testGetPricingTools() {
    PricingTools pricingTools = mShippingTools.getPricingTools();
    assertNotNull(pricingTools);
    mShippingTools.setPricingTools(pricingTools);
    assertNotNull(mShippingTools.getPricingTools());
  }

  /**
   * Get available shipping methods
   */
  @Test
  public void testGetAvailableShippingMethods() throws PricingException {
    HardgoodShippingGroup sg = 
      (HardgoodShippingGroup) mShoppingCart.getCurrent().getShippingGroups().get(0);
    List<String> shippingMethods = mShippingTools.getAvailableShippingMethods(sg, mPricingModelHolder);
    
    assertEquals(shippingMethods.get(0), "Ground");
    assertEquals(shippingMethods.get(1), "2-Day");
    assertEquals(shippingMethods.get(2), "Overnight");
  }
  
  /**
   * Test the setShippingPrice and getShippingPrice methods.
   */
  @Test
  public void testGetShippingPrice() throws PricingException, InvalidParameterException,
    ShippingGroupNotFoundException {

    ShippingGroup shippingGroup = (ShippingGroup) mShoppingCart.getCurrent().getShippingGroups().get(0);
    double price =
      mShippingTools.getShippingPrice(shippingGroup, "Overnight", mPricingModelHolder, mShoppingCart);
    assertEquals(price, 18.95, 0.001);
  }
}