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

package atg.projects.store.order.purchase;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.commerce.CommerceException;
import atg.commerce.order.DuplicateShippingGroupException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.droplet.DropletException;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.projects.store.order.purchase.ShippingInfoFormHandler;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.userprofiling.StoreProfileTools;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for atg.projects.store.order.purchase.ShippingInfoFormHandler.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/test/java/atg/projects/store/order/purchase/ShippingInfoFormHandlerTest.java#1 $$Change: 1385662 $ 
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ShippingInfoFormHandlerTest {

  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/test/java/atg/projects/store/order/purchase/ShippingInfoFormHandlerTest.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // STATIC MEMBERS
  //---------------------------------------------------------------------------
	 
  private static Nucleus mNucleus = null;
  private static ShippingInfoFormHandler mShippingInfoFormHandler = null;
  private static StoreOrderTools mStoreOrderTools = null;
  private static StoreProfileTools mStoreProfileTools = null;
  private static GSARepository mOrderRepository = null;
  private static GSARepository mSiteRepository = null;
  private static OrderManager mOrderManager = null;
  private static GSARepository mProductCatalog = null;
  private static GSARepository mInventoryRepository = null;
  private static GSARepository mPriceList = null;

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  public static final String SHIPPING_GROUP_FORM_HANDLER_PATH =
    "/atg/commerce/order/purchase/ShippingGroupFormHandler";
  public static final String PROFILE = "/atg/userprofiling/Profile";
  public static final String ORDER_TOOLS_PATH = "/atg/commerce/order/OrderTools";
  public static final String PROFILE_TOOLS_PATH = "/atg/userprofiling/ProfileTools";
  public static final String ORDER_REPOSITORY_PATH = "/atg/commerce/order/OrderRepository";
  public static final String ORDER_MANAGER_PATH = "/atg/commerce/order/OrderManager";

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------
  
  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the ShippingInfoFormHandler instance to be used in this test.
   * 
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void beforeClass() throws ServletException, CommerceException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[]{"CommerceAccelerator.Base", "CommerceAccelerator.Plugins.Account",
      "CommerceAccelerator.Plugins.Promotions", "CommerceAccelerator.Plugins.Checkout"},
      ShippingInfoFormHandlerTest.class,"ShippingInfoFormHandlerTest",
      ORDER_TOOLS_PATH);

    mStoreOrderTools = (StoreOrderTools) mNucleus.resolveName(ORDER_TOOLS_PATH, true);
    assertNotNull(mStoreOrderTools);

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
   * Ensure Nucleus is shutdown properly and perform general clean-up of member variables.
   * 
   * @throws Exception 
   *   When there's a problem shutting down Nucleus. 
   */
  @AfterClass
  public static void afterClass() throws IOException, ServiceException {
    if(mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
      mOrderRepository = null;
      mSiteRepository = null;
      mShippingInfoFormHandler = null;
      mStoreOrderTools = null;
      mStoreProfileTools = null;
      mOrderManager = null;
      mProductCatalog = null;
      mInventoryRepository = null;
      mPriceList = null;
      mNucleus = null;
    }
  }

  /**
   * Set up a new order and new profile every time.
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
    
    mShippingInfoFormHandler = 
      (ShippingInfoFormHandler) request.resolveName(SHIPPING_GROUP_FORM_HANDLER_PATH, true);
    assertNotNull(mShippingInfoFormHandler);
    
    Order order = mOrderManager.loadOrder("o200002");
    mShippingInfoFormHandler.setOrder(order);
  }
  
  //---------------------------------------------------------------------------
  // TESTS
  //---------------------------------------------------------------------------
  
  /**
   * Test the setShippingMethod and getShippingMethod methods.
   */
  @Test
  public void testSetShippingMethod() {
    mShippingInfoFormHandler.setShippingMethod("Ground");
    assertEquals(mShippingInfoFormHandler.getShippingMethod(),"Ground");
  }

  /**
   * Test the setShipToAddressErrorURL and getShipToAddressErrorURL methods.
   */
  @Test
  public void testSetShipToAddressErrorURL() {
    mShippingInfoFormHandler.setShipToAddressErrorURL("/checkout");
    assertEquals(mShippingInfoFormHandler.getShipToAddressErrorURL(), "/checkout");
  }
  
  /**
   * Test the setShipToAddressSuccessURL and getShipToAddressSuccessURL methods.
   */
  @Test
  public void testSetShipToAddressSuccessURL() {
    mShippingInfoFormHandler.setShipToAddressSuccessURL("/checkout");
    assertEquals(mShippingInfoFormHandler.getShipToAddressSuccessURL(), "/checkout");
  }

  /**
   * Test the setShippingHelper and getShippingHelper methods.
   */
  @Test
  public void testGetShippingHelper() {
    StorePurchaseProcessHelper shippingHelper = mShippingInfoFormHandler.getShippingHelper();
    assertNotNull(shippingHelper);
    mShippingInfoFormHandler.setShippingHelper(shippingHelper);
    assertNotNull(mShippingInfoFormHandler.getShippingHelper());
  }

  /**
   * Validate an address input from user.
   */
  @Test
  public void testPreShipToAddress1() throws ServletException, IOException {
    Map<String, Object> address = new HashMap<>();
    address.put("addressNickname", "Home1");
    address.put("firstName", "Test1");
    address.put("lastName", "Test1");
    address.put("address1", "Test1");
    address.put("state", "CA");
    address.put("postalCode", "Test1");
    address.put("country", "US");
    
    mShippingInfoFormHandler.getShippingAddressUserInputValues().clear();
    mShippingInfoFormHandler.getShippingAddressUserInputValues().putAll(address);

    mShippingInfoFormHandler.preShipToAddress(ServletUtil.getCurrentRequest(), null);
    assertEquals(0, mShippingInfoFormHandler.getFormExceptions().size());
  } 

  /**
   * Test error code for invalid address.
   */
  @Test
  public void testPreShipToAddress2() throws ServletException, IOException {
    Map<String, Object> address = new HashMap<>();
    address.put("addressNickname", "Home1");
    address.put("firstName", "Test1");
    
    mShippingInfoFormHandler.getShippingAddressUserInputValues().clear();
    mShippingInfoFormHandler.getShippingAddressUserInputValues().putAll(address);
    
    mShippingInfoFormHandler.preShipToAddress(ServletUtil.getCurrentRequest(), null);

    assertTrue(mShippingInfoFormHandler.getFormError());
    DropletException e = (DropletException) mShippingInfoFormHandler.getFormExceptions().get(0);
    assertEquals("missingAddressProperty", e.getErrorCode());
  }

  
  /**
   * Validate an address input from user.
   */
  @Test
  public void testHandleShipToAddress1() throws ServletException, IOException {
    Map<String, Object> address = new HashMap<>();
    address.put("addressNickname", "Home1");
    address.put("firstName", "Test1");
    address.put("lastName", "Test1");
    address.put("address1", "Test1");
    address.put("state", "CA");
    address.put("postalCode", "Test1");
    address.put("country", "US");

    mShippingInfoFormHandler.getShippingAddressUserInputValues().clear();
    mShippingInfoFormHandler.getShippingAddressUserInputValues().putAll(address);

    mShippingInfoFormHandler.handleShipToAddress(ServletUtil.getCurrentRequest(), null);
    assertEquals(0, mShippingInfoFormHandler.getFormExceptions().size());
  } 

  /**
   * Validate an address and shipping method input from user.
   */
  @Test
  public void testHandleShipToAddress2() throws ServletException, IOException, CommerceException {
    Map<String, Object> address = new HashMap<>();
    address.put("addressNickname", "Home2");
    address.put("firstName", "Test2");
    address.put("lastName", "Test2");
    address.put("address1", "Test2");
    address.put("state", "CA");
    address.put("postalCode", "Test2");
    address.put("country", "US");

    mShippingInfoFormHandler.getShippingAddressUserInputValues().clear();
    mShippingInfoFormHandler.getShippingAddressUserInputValues().putAll(address);
    mShippingInfoFormHandler.setShippingMethod("Ground");

    mShippingInfoFormHandler.handleShipToAddress(ServletUtil.getCurrentRequest(), null);
    assertEquals(0, mShippingInfoFormHandler.getFormExceptions().size());

    assertEquals("Ground", ((mStoreOrderTools.getShippingGroup(mShippingInfoFormHandler.getOrder()))).getShippingMethod());
    assertEquals("Test2", ((mStoreOrderTools.getShippingGroup(mShippingInfoFormHandler.getOrder()))).getShippingAddress().getFirstName());
    assertEquals("Test2", ((mStoreOrderTools.getShippingGroup(mShippingInfoFormHandler.getOrder()))).getShippingAddress().getLastName());
    assertEquals("Test2", ((mStoreOrderTools.getShippingGroup(mShippingInfoFormHandler.getOrder()))).getShippingAddress().getAddress1());
    assertEquals("CA", ((mStoreOrderTools.getShippingGroup(mShippingInfoFormHandler.getOrder()))).getShippingAddress().getState());
    assertEquals("Test2", ((mStoreOrderTools.getShippingGroup(mShippingInfoFormHandler.getOrder()))).getShippingAddress().getPostalCode());
    assertEquals("US", ((mStoreOrderTools.getShippingGroup(mShippingInfoFormHandler.getOrder()))).getShippingAddress().getCountry());
  }    
}