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
import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.pricing.PricingModelHolder;
import atg.core.util.Address;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.payment.creditcard.ExtendableCreditCardTools;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.order.purchase.StorePurchaseProcessHelper;
import atg.projects.store.profile.StoreCheckoutPropertyManager;
import atg.projects.store.shipping.ShippingConfiguration;
import atg.projects.store.userprofiling.StoreProfileTools;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.*;

/**
 * Unit tests for atg.projects.store.order.purchase.StorePurchaseProcessHelper.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/test/java/atg/projects/store/order/purchase/StorePurchaseProcessHelperTest.java#1 $$Change: 1385662 $ 
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StorePurchaseProcessHelperTest {

  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/test/java/atg/projects/store/order/purchase/StorePurchaseProcessHelperTest.java#1 $$Change: 1385662 $";
  
  //---------------------------------------------------------------------------
  // STATIC MEMBERS
  //---------------------------------------------------------------------------
  
  private static Nucleus mNucleus = null;
  private static StorePurchaseProcessHelper mStorePurchaseProcessHelper = null;
  private static GSARepository mOrderRepository = null;
  private static StoreOrderTools mStoreOrderTools = null;
  private static StoreProfileTools mStoreProfileTools = null;
  private static GSARepository mSiteRepository = null;
  private static OrderManager mOrderManager = null;
  private static PricingModelHolder mUserPricingModels = null;
  private static GSARepository mProductCatalog = null;
  private static GSARepository mInventoryRepository = null;
  private static GSARepository mPriceList = null;
  
  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  public static final String STORE_PURCHASE_PROCESS_HELPER_PATH =
    "/atg/commerce/order/purchase/PurchaseProcessHelper";
  public static final String PROFILE = "/atg/userprofiling/Profile";
  public static final String ORDER_TOOLS_PATH = "/atg/commerce/order/OrderTools";
  public static final String PROFILE_TOOLS_PATH = "/atg/userprofiling/ProfileTools";
  public static final String ORDER_REPOSITORY_PATH = "/atg/commerce/order/OrderRepository";
  public static final String ORDER_MANAGER_PATH = "/atg/commerce/order/OrderManager";
  public static final String PRICING_MODEL_HOLDER_PATH = "/atg/commerce/pricing/UserPricingModels";

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------
  
  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the StorePurchaseProcessHelper instance to be used in this test.
   * 
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void beforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] {"CommerceAccelerator.Base", "CommerceAccelerator.Plugins.Account",
      "CommerceAccelerator.Plugins.Promotions", "CommerceAccelerator.Plugins.Checkout"},
      StorePurchaseProcessHelperTest.class, "StorePurchaseProcessHelperTest", 
      "/atg/Initial");
    
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
    if (mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
      mOrderRepository = null;
      mSiteRepository = null;
      mStorePurchaseProcessHelper = null;
      mStoreOrderTools = null;
      mStoreProfileTools = null;
      mOrderManager = null;
      mProductCatalog = null;
      mInventoryRepository = null;
      mUserPricingModels = null;
      mPriceList = null;
      mNucleus = null;
    }
  }

  /**
   * Set up a new object every time
   */
  @Before
  public void before() throws RepositoryException {
    // Set the current request & order
    ServletTestUtils utils = new ServletTestUtils();
    DynamoHttpServletRequest request = 
      utils.createDynamoHttpServletRequestForSession(
      mNucleus, "mySessionId", "new");
    
    ServletUtil.setCurrentRequest(request);
    
    Profile profile = (Profile) request.resolveName(PROFILE);
    profile.setPropertyValue("priceList", mPriceList.getItem("listPrices"));
    ServletUtil.setCurrentUserProfile(profile);
    mStoreProfileTools.createNewUser("110000", "user", profile);

    mStorePurchaseProcessHelper = 
      (StorePurchaseProcessHelper) request.resolveName(STORE_PURCHASE_PROCESS_HELPER_PATH, true);
    assertNotNull(mStorePurchaseProcessHelper);
    
    mUserPricingModels = 
      (PricingModelHolder) request.resolveName(PRICING_MODEL_HOLDER_PATH, true);
    assertNotNull(mUserPricingModels);
  }

  //---------------------------------------------------------------------------
  // TESTS
  //---------------------------------------------------------------------------

  /**
   * Test the setClaimableManager and getClaimableManager methods.
   */
  @Test
  public void testGetClaimableManager() {
    ClaimableManager claimableManager = mStorePurchaseProcessHelper.getClaimableManager();
    assertNotNull(claimableManager);
    mStorePurchaseProcessHelper.setClaimableManager(claimableManager);
    assertNotNull(mStorePurchaseProcessHelper.getClaimableManager());
  }
  
  /**
   * Test the setResourceBundleName and getResourceBundleName methods.
   */
  @Test
  public void testSetResourceBundleName() {
    mStorePurchaseProcessHelper.setResourceBundleName("atg.commerce.order.purchase.UserMessages");
    assertEquals(mStorePurchaseProcessHelper.
      getResourceBundleName(),"atg.commerce.order.purchase.UserMessages");
  }
  
  /**
   * Test the setStoreProfileTools and getStoreProfileTools methods.
   */
  @Test
  public void testGetStoreProfileTools() {
    StoreProfileTools storeProfileTools = mStorePurchaseProcessHelper.getStoreProfileTools();
    assertNotNull(storeProfileTools);
    mStorePurchaseProcessHelper.setStoreProfileTools(storeProfileTools);
    assertNotNull(mStorePurchaseProcessHelper.getStoreProfileTools());
  }
  
  /**
   * Test the setCheckoutPropertyManager and getCheckoutPropertyManager methods.
   */
  @Test
  public void testGetStoreCheckoutPropertyManager() {
    StoreCheckoutPropertyManager checkoutPropertyManager = 
      mStorePurchaseProcessHelper.getCheckoutPropertyManager();
    assertNotNull(checkoutPropertyManager);
    mStorePurchaseProcessHelper.setCheckoutPropertyManager(checkoutPropertyManager);
    assertNotNull(mStorePurchaseProcessHelper.getCheckoutPropertyManager());
  }
  
  /**
   * Test the setCheckoutPropertyManager and getCheckoutPropertyManager methods.
   */
  @Test
  public void testGetShippingConfiguration() {
    ShippingConfiguration shippingConfiguration = 
      mStorePurchaseProcessHelper.getShippingConfiguration();
    assertNotNull(shippingConfiguration);
    mStorePurchaseProcessHelper.setShippingConfiguration(shippingConfiguration);
    assertNotNull(mStorePurchaseProcessHelper.getShippingConfiguration());
  }
  
  /**
   * Test the setCreditCardTools and getCreditCardTools methods.
   */
  @Test
  public void testGetCreditCardTools() {
    ExtendableCreditCardTools creditCardTools = mStorePurchaseProcessHelper.getCreditCardTools();
    assertNotNull(creditCardTools);
    mStorePurchaseProcessHelper.setCreditCardTools(creditCardTools);
    assertNotNull(mStorePurchaseProcessHelper.getCreditCardTools());
  }
  
  /**
   * Test the setInventoryManager and getInventoryManager methods.
   */
  @Test
  public void testGetInventoryManager() {
    InventoryManager inventoryManager = mStorePurchaseProcessHelper.getInventoryManager();
    assertNotNull(inventoryManager);
    mStorePurchaseProcessHelper.setInventoryManager(inventoryManager);
    assertNotNull(mStorePurchaseProcessHelper.getInventoryManager());
  }
  
  /**
   * Test the setCatalogTools and getCatalogTools methods.
   */
  @Test
  public void testGetCatalogTools() {
    CatalogTools catalogTools = mStorePurchaseProcessHelper.getCatalogTools();
    assertNotNull(catalogTools);
    mStorePurchaseProcessHelper.setCatalogTools(catalogTools);
    assertNotNull(mStorePurchaseProcessHelper.getCatalogTools());
  }

  /**
   * Validate copy credit card info method. 
   */
  @Test
  public void testCopyImmutableCardProperties() throws RepositoryException {
    Map<String, Object> pCardProperties = new HashMap<String, Object>();
    
    //  Define the credit card item
    MutableRepositoryItem mReposItem = mOrderRepository.createItem("creditCard");

   // create the credit card
    mReposItem.setPropertyValue("creditCardNumber", "4111111111111111");
    mReposItem.setPropertyValue("creditCardType", "Visa");  

    // Ensure that the resolved SiteRepository is not null.
    assertNotNull(mReposItem);
    
    mStorePurchaseProcessHelper.copyImmutableCardProperties(mReposItem, pCardProperties);
    assertEquals((String)pCardProperties.get("creditCardNumber"), "4111111111111111");
    assertEquals((String)pCardProperties.get("creditCardType"), "Visa");    
  }

  /**
   * Validate an address and shipping method input from user.
   */
  @Test
  public void testValidateShippingMethod1() {
    Address address = new Address();
    address.setState("AK");
    boolean flag = mStorePurchaseProcessHelper.
      validateShippingMethod(address, "Ground", new HashMap());
    assertFalse(flag);
  } 
  
  /**
   * Validate an address and shipping method input from user.
   */
  @Test
  public void testValidateShippingMethod2() {
    Address address = new Address();
    address.setState("NY");
    boolean flag = mStorePurchaseProcessHelper.
      validateShippingMethod(address, "Overnight", new HashMap());
    assertTrue(flag);
  }
  
  /**
   * Validate a new nickname.
   */
  @Test
  public void testValidateAddressNickname1() throws IllegalAccessException, RepositoryException,
    IntrospectionException, InstantiationException, PropertyNotFoundException, ClassNotFoundException
  {
  
    Map<String, Object> address = new HashMap<>();
    
    address.put("addressNickname", "Home");
    address.put("saveShippingAddress", "true");
    
    boolean valid = mStorePurchaseProcessHelper.validateAddressNickname(address, new HashMap());
    assertTrue(valid);
  }
  
  /**
   * Nickname not available with saveShippingAddress property true.
   */
  @Test
  public void testValidateAddressNickname2() throws IllegalAccessException, RepositoryException,
    IntrospectionException, InstantiationException, PropertyNotFoundException, ClassNotFoundException
  {
    
    Map<String, Object> address = new HashMap<>();

    address.put("saveShippingAddress", "true");
    boolean valid = mStorePurchaseProcessHelper.validateAddressNickname(address, new HashMap());
    assertFalse(valid);
  }

  
  /**
   * Validate shipping details.
   */
  @Test
  public void testValidateShippingDetails1() throws IllegalAccessException, RepositoryException,
    IntrospectionException, InstantiationException, PropertyNotFoundException, ClassNotFoundException
  {
    // Missing required properties
    Map<String, Object> address = new HashMap<>();

    // Required properties
    address.put("firstName", "Test");
    address.put("lastName", "Test");
    address.put("address1", "Test");
    address.put("city", "Test");
    address.put("state", "CA");
    address.put("postalCode", "Test");
    address.put("country", "US");
    address.put("phoneNumber", "8090876634");
    boolean valid = mStorePurchaseProcessHelper.
      validateShippingDetails(address, new HashMap(), "Overnight");
    assertTrue(valid);
  }
  
  /**
   * Returns false if shipping details is invalid.
   */
  @Test
  public void testValidateShippingDetails2() throws IllegalAccessException, RepositoryException,
    IntrospectionException, InstantiationException, PropertyNotFoundException, ClassNotFoundException
  {
    // Missing required properties
    Map<String, Object> address = new HashMap<>();

    // Required properties
    address.put("firstName", "Test");
    address.put("city", "Test");
    address.put("state", "CA");
    address.put("postalCode", "Test");
    address.put("country", "US");
    address.put("phoneNumber", "8090876634");
    boolean valid = mStorePurchaseProcessHelper.
      validateShippingDetails(address, new HashMap(), "Overnight");
    assertFalse(valid);
  }
  
  /**
   * Applies shipping details to shipping group.
   */
  @Test
  public void testApplyShippingDetails() throws CommerceException, ClassNotFoundException,
    RunProcessException, IntrospectionException, InstantiationException, IllegalAccessException
  {
	   Order order = mOrderManager.loadOrder("o200002");
	
    // Missing required properties
    Map<String, Object> address = new HashMap<>();

    // Required properties
    address.put("firstName", "Test");
    address.put("lastName", "Test");
    address.put("address1", "Test");
    address.put("city", "Test");
    address.put("state", "CA");
    address.put("postalCode", "Test");
    address.put("country", "US");
    address.put("phoneNumber", "8090876634");
    mStorePurchaseProcessHelper.applyShippingDetails(
      (Profile)ServletUtil.getCurrentUserProfile(), order, address, "Ground", mUserPricingModels);
    
    assertEquals("Ground", mStoreOrderTools.getShippingGroup(order).getShippingMethod());

    assertEquals("Test", ((HardgoodShippingGroup)(mStoreOrderTools.
      getShippingGroup(order))).getShippingAddress().getFirstName());
    assertEquals("Test", ((HardgoodShippingGroup)(mStoreOrderTools.
      getShippingGroup(order))).getShippingAddress().getLastName());
    assertEquals("Test", ((HardgoodShippingGroup)(mStoreOrderTools.
      getShippingGroup(order))).getShippingAddress().getAddress1());
    assertEquals("CA", ((HardgoodShippingGroup)(mStoreOrderTools.
      getShippingGroup(order))).getShippingAddress().getState());
    assertEquals("Test", ((HardgoodShippingGroup)(mStoreOrderTools.
      getShippingGroup(order))).getShippingAddress().getPostalCode());
    assertEquals("US", ((HardgoodShippingGroup)(mStoreOrderTools.
      getShippingGroup(order))).getShippingAddress().getCountry());
  }
  
  /**
   * Validate and apply shipping information on shipping group.
   * @throws Exception
   */
  @Test
  public void testValidateAndApplyShippingInformation1() throws CommerceException, ClassNotFoundException,
    IntrospectionException, InstantiationException, IllegalAccessException
  {

    Order order = mOrderManager.loadOrder("o200002");
	
    // Missing required properties
    Map<String, Object> address = new HashMap<>();

    // Required properties
    address.put("firstName", "Test");
    address.put("lastName", "Test");
    address.put("address1", "Test");
    address.put("city", "Test");
    address.put("state", "CA");
    address.put("postalCode", "Test");
    address.put("country", "US");
    address.put("phoneNumber", "8090876634");
    boolean valid = mStorePurchaseProcessHelper.
      validateAndApplyShippingInformation(order, "Ground", address, new HashMap());
    assertTrue(valid);
    
    assertEquals("Ground", mStoreOrderTools.getShippingGroup(order).getShippingMethod());

    assertEquals("Test", ((HardgoodShippingGroup)(mStoreOrderTools.
      getShippingGroup(order))).getShippingAddress().getFirstName());
    assertEquals("Test", ((HardgoodShippingGroup)(mStoreOrderTools.
      getShippingGroup(order))).getShippingAddress().getLastName());
    assertEquals("Test", ((HardgoodShippingGroup)(mStoreOrderTools.
      getShippingGroup(order))).getShippingAddress().getAddress1());
    assertEquals("CA", ((HardgoodShippingGroup)(mStoreOrderTools.
      getShippingGroup(order))).getShippingAddress().getState());
    assertEquals("Test", ((HardgoodShippingGroup)(mStoreOrderTools.
      getShippingGroup(order))).getShippingAddress().getPostalCode());
    assertEquals("US", ((HardgoodShippingGroup)(mStoreOrderTools.
      getShippingGroup(order))).getShippingAddress().getCountry());
  }
  
  /**
   * If address is invalid returns false.
   * @throws Exception
   */
  @Test
  public void testValidateAndApplyShippingInformation2() throws CommerceException, ClassNotFoundException,
    IntrospectionException, InstantiationException, IllegalAccessException
  {

    Order order = mOrderManager.loadOrder("o200002");
	
    // Missing required properties
    Map<String, Object> address = new HashMap<>();

    // Required properties
    address.put("firstName", "Test");
    address.put("city", "Test");
    address.put("state", "CA");
    address.put("postalCode", "Test");
    address.put("country", "US");
    address.put("phoneNumber", "8090876634");
    boolean valid = mStorePurchaseProcessHelper.
      validateAndApplyShippingInformation(order, "Ground", address, new HashMap());
    assertFalse(valid);
  }
  
  /**
   * Validate new card.
   */
  @Test
  public void testValidateCreditCardNickname1() throws IllegalAccessException, RepositoryException,
    IntrospectionException, InstantiationException, PropertyNotFoundException, ClassNotFoundException
  {

    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("cardNickname", "Visa");
    card.put("creditCardType", "Visa");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2050");
    card.put("saveCreditCard", "true");
    
    // Missing billingAddress
    boolean valid = mStorePurchaseProcessHelper.validateCreditCardNickname(card, new HashMap());
    assertTrue(valid);
  }
  
  /**
   * Returns false if card details is invalid.
   */
  @Test
  public void testValidateCreditCardNickname2() throws IllegalAccessException, RepositoryException,
    IntrospectionException, InstantiationException, PropertyNotFoundException, ClassNotFoundException
  {

    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("creditCardType", "Visa");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2050");
    card.put("saveCreditCard", "true");

    // Missing billingAddress
    boolean valid = mStorePurchaseProcessHelper.validateCreditCardNickname(card, new HashMap());
    assertFalse(valid);
  }
  
  /**
   * Fill credit card values in credit card from credit card map.
   */
  @Test
  public void testFillCreditCardFieldsWithUserInput() throws CommerceException {
    Order order = mOrderManager.loadOrder("o200002");

    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("creditCardType", "Visa");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2051");

    mStorePurchaseProcessHelper.fillCreditCardFieldsWithUserInput(order, card, "111");
    
    assertEquals("4111111111111111", ((mStoreOrderTools.
      getCreditCard(order)).getCreditCardNumber()));
    assertEquals("Visa", ((mStoreOrderTools.getCreditCard(order)).getCreditCardType()));
    assertEquals("12", ((mStoreOrderTools.getCreditCard(order)).getExpirationMonth()));
    assertEquals("2051", ((mStoreOrderTools.getCreditCard(order)).getExpirationYear()));
    assertEquals("111", ((mStoreOrderTools.getCreditCard(order)).getCardVerificationNumber()));
  }

  
  /**
   * Validate credit card and apply credit card details.
   * @throws Exception
   */
  @Test
  public void testValidateAndApplyCreditCardInformation1() throws CommerceException, ClassNotFoundException,
    IntrospectionException, InstantiationException, IllegalAccessException
  {

    Order order = mOrderManager.loadOrder("o200002");

    // Missing required properties
    Map<String, Object> address = new HashMap<>();

    // Required properties
    address.put("firstName", "Test");
    address.put("lastName", "Test");
    address.put("address1", "Test");
    address.put("city", "Test");
    address.put("state", "CA");
    address.put("postalCode", "Test");
    address.put("country", "US");
    address.put("phoneNumber", "8090876634");
    
    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("creditCardType", "Visa");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2050");
    boolean valid = mStorePurchaseProcessHelper.validateAndApplyCreditCardInformation(false,
      (Profile)ServletUtil.getCurrentUserProfile(), order, address, "111", card, new HashMap());
    assertTrue(valid);
    
    assertEquals("Test", ((mStoreOrderTools.
      getCreditCard(order)).getBillingAddress().getFirstName()));
    assertEquals("Test", ((mStoreOrderTools.
      getCreditCard(order)).getBillingAddress().getLastName()));
    assertEquals("Test", ((mStoreOrderTools.
      getCreditCard(order)).getBillingAddress().getAddress1()));
    assertEquals("CA", ((mStoreOrderTools.
      getCreditCard(order)).getBillingAddress().getState()));
    assertEquals("Test", ((mStoreOrderTools.
      getCreditCard(order)).getBillingAddress().getPostalCode()));
    assertEquals("US", ((mStoreOrderTools.
      getCreditCard(order)).getBillingAddress().getCountry()));
    assertEquals("4111111111111111", ((mStoreOrderTools.
      getCreditCard(order)).getCreditCardNumber()));
    assertEquals("Visa", ((mStoreOrderTools.getCreditCard(order)).getCreditCardType()));
    assertEquals("12", ((mStoreOrderTools.getCreditCard(order)).getExpirationMonth()));
    assertEquals("2050", ((mStoreOrderTools.getCreditCard(order)).getExpirationYear()));
    assertEquals("111", ((mStoreOrderTools.getCreditCard(order)).getCardVerificationNumber()));
  }
  
  /**
   * Validate credit card details and if not validated return false.
   * @throws Exception
   */
  @Test
  public void testValidateAndApplyCreditCardInformation2() throws CommerceException, ClassNotFoundException,
    IntrospectionException, InstantiationException, IllegalAccessException
  {

    Order order = mOrderManager.loadOrder("o200002");

    // Missing required properties
    Map<String, Object> address = new HashMap<>();

    // Required properties
    address.put("firstName", "Test");
    address.put("lastName", "Test");
    address.put("address1", "Test");
    address.put("city", "Test");
    address.put("state", "CA");
    address.put("postalCode", "Test");
    address.put("country", "US");
    address.put("phoneNumber", "8090876634");
    
    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2022");
    boolean valid = mStorePurchaseProcessHelper.validateAndApplyCreditCardInformation(false,
      (Profile)ServletUtil.getCurrentUserProfile(), order, address, "111", card, new HashMap());
    assertFalse(valid);
  }
  
  /**
   * Validate credit card details.
   * @throws Exception
   */
  @Test
  public void testValidateCreditCardDetails1() throws CommerceException, IllegalAccessException,
    RepositoryException, IntrospectionException, InstantiationException, PropertyNotFoundException,
    ClassNotFoundException
  {
    Order order = mOrderManager.loadOrder("o200002");
    
    // Missing required properties
    Map<String, Object> address = new HashMap<>();

    // Required properties
    address.put("firstName", "Test");
    address.put("lastName", "Test");
    address.put("address1", "Test");
    address.put("city", "Test");
    address.put("state", "CA");
    address.put("postalCode", "Test");
    address.put("country", "US");
    address.put("phoneNumber", "8090876634");
    
    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("creditCardType", "Visa");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2050");
    boolean valid = mStorePurchaseProcessHelper.validateCreditCardDetails(
      (Profile)ServletUtil.getCurrentUserProfile(), order, "111", address, card, new HashMap());
    assertTrue(valid);
  }
  
  /**
   * Validate credit card details if details is invalid returns false.
   * @throws Exception
   */
  @Test
  public void testValidateCreditCardDetails2() throws CommerceException, IllegalAccessException,
    RepositoryException, IntrospectionException, InstantiationException, PropertyNotFoundException,
    ClassNotFoundException
  {
    Order order = mOrderManager.loadOrder("o200002");
    
    // Missing required properties
    Map<String, Object> address = new HashMap<>();

    // Required properties
    address.put("firstName", "Test");
    address.put("lastName", "Test");
    address.put("address1", "Test");
    address.put("city", "Test");
    address.put("state", "CA");
    address.put("postalCode", "Test");
    address.put("country", "US");
    address.put("phoneNumber", "8090876634");
    
    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2050");
    boolean valid = mStorePurchaseProcessHelper.validateCreditCardDetails(
      (Profile)ServletUtil.getCurrentUserProfile(), order, "111", address, card, new HashMap());
    assertFalse(valid);
  }
  
  /**
   * Validate credit card and apply credit card details.
   * @throws Exception
   */
  @Test
  public void testApplyCreditCardDetails() throws RunProcessException, IntrospectionException,
    InstantiationException, CommerceException, IllegalAccessException, ClassNotFoundException
  {

    Order order = mOrderManager.loadOrder("o200002");

    // Missing required properties
    Map<String, Object> address = new HashMap<>();

    // Required properties
    address.put("firstName", "Test");
    address.put("lastName", "Test");
    address.put("address1", "Test");
    address.put("city", "Test");
    address.put("state", "CA");
    address.put("postalCode", "Test");
    address.put("country", "US");
    address.put("phoneNumber", "8090876634");
    
    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("creditCardType", "Visa");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2050");
    mStorePurchaseProcessHelper.applyCreditCardDetails(
     (Profile)ServletUtil.getCurrentUserProfile(), order, address, card, mUserPricingModels);
    
    assertEquals("Test", ((mStoreOrderTools.getCreditCard(order)).
      getBillingAddress().getFirstName()));
    assertEquals("Test", ((mStoreOrderTools.getCreditCard(order)).
      getBillingAddress().getLastName()));
    assertEquals("Test", ((mStoreOrderTools.getCreditCard(order)).
      getBillingAddress().getAddress1()));
    assertEquals("CA", ((mStoreOrderTools.getCreditCard(order)).
      getBillingAddress().getState()));
    assertEquals("Test", ((mStoreOrderTools.getCreditCard(order)).
      getBillingAddress().getPostalCode()));
    assertEquals("US", ((mStoreOrderTools.getCreditCard(order)).
      getBillingAddress().getCountry()));
  }
  
  /**
   * Add address to profile.
   */
  @Test
  public void testAddAddressToProfile1() throws IllegalAccessException, RepositoryException,
    IntrospectionException, InstantiationException, PropertyNotFoundException, ClassNotFoundException
  {
    Profile profile = (Profile) ServletUtil.getCurrentUserProfile();

    Map<String, Object> shippingAddress = new HashMap<>();
    shippingAddress.put("addressNickname", "Home1");
    shippingAddress.put("firstName", "Test1");
    shippingAddress.put("lastName", "Test1");
    shippingAddress.put("address1", "Test1");
    shippingAddress.put("state", "CA");
    shippingAddress.put("postalCode", "Test1");
    shippingAddress.put("country", "US");
    shippingAddress.put("phoneNumber", "908765432");
    shippingAddress.put("saveShippingAddress", "true");
    
    boolean valid = mStorePurchaseProcessHelper.
      addAddressToProfile(profile, shippingAddress, new HashMap());
    assertTrue(valid);
    
    // Make sure address is there
    Map secondaryAddressMap = (Map) ServletUtil.getCurrentUserProfile().
      getPropertyValue("secondaryAddresses");
    List<String> nicknames = new ArrayList(secondaryAddressMap.keySet());
    assertTrue(nicknames.contains("Home1"));
  }
  
  /**
   * Returns false if nick name is not valid.
   */
  @Test
  public void testAddAddressToProfile2() throws IllegalAccessException, RepositoryException,
    IntrospectionException, InstantiationException, PropertyNotFoundException, ClassNotFoundException
  {
    Profile profile = (Profile) ServletUtil.getCurrentUserProfile();

    Map<String, Object> shippingAddress = new HashMap<>();
    shippingAddress.put("addressNickname", "");
    shippingAddress.put("firstName", "Test1");
    shippingAddress.put("lastName", "Test1");
    shippingAddress.put("address1", "Test1");
    shippingAddress.put("state", "CA");
    shippingAddress.put("postalCode", "Test1");
    shippingAddress.put("country", "US");
    shippingAddress.put("phoneNumber", "908765432");
    shippingAddress.put("saveShippingAddress", "true");
    
    boolean valid = mStorePurchaseProcessHelper.
      addAddressToProfile(profile, shippingAddress, new HashMap());
    assertFalse(valid);
  }

  /**
   * Add credit card to profile.
   * @throws Exception
   */
  @Test
  public void testAddCreditCardToProfile1() throws IllegalAccessException, RepositoryException,
    IntrospectionException, InstantiationException, PropertyNotFoundException, ClassNotFoundException
  {
    Profile profile = (Profile)ServletUtil.getCurrentUserProfile();

    // Missing required properties
    Map<String, Object> address = new HashMap<>();

    // Required properties
    address.put("firstName", "Test");
    address.put("lastName", "Test");
    address.put("address1", "Test");
    address.put("city", "Test");
    address.put("state", "CA");
    address.put("postalCode", "Test");
    address.put("country", "US");
    address.put("phoneNumber", "8090876634");
    
    Map<String, Object> card = new HashMap<>();
    card.put("cardNickname", "Visa");
    card.put("creditCardNumber", "4111111111111111");
    card.put("creditCardType", "Visa");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2050");
    card.put("billingAddress", address);
    card.put("saveCreditCard", "true");
    boolean valid = mStorePurchaseProcessHelper.
      addCreditCardToProfile(profile, card, new HashMap());
    assertTrue(valid);
   
    RepositoryItem cardNickname = mStoreProfileTools.getCreditCardByNickname("Visa", profile);
    assertNotNull(cardNickname);
    assertEquals("4111111111111111", (String)cardNickname.getPropertyValue("creditCardNumber"));
    assertEquals("Visa", (String)cardNickname.getPropertyValue("creditCardType"));
    assertEquals("12", (String)cardNickname.getPropertyValue("expirationMonth"));
    assertEquals("2050", (String)cardNickname.getPropertyValue("expirationYear"));
  }
  
  /**
   * Returns false when credit card nickname is invalid.
   * @throws Exception
   */
  @Test
  public void testAddCreditCardToProfile2() throws IllegalAccessException, RepositoryException,
    IntrospectionException, InstantiationException, PropertyNotFoundException, ClassNotFoundException
  {
    Profile profile = (Profile)ServletUtil.getCurrentUserProfile();

    // Missing required properties
    Map<String, Object> address = new HashMap<>();

    // Required properties
    address.put("firstName", "Test");
    address.put("lastName", "Test");
    address.put("address1", "Test");
    address.put("city", "Test");
    address.put("state", "CA");
    address.put("postalCode", "Test");
    address.put("country", "US");
    address.put("phoneNumber", "8090876634");
    
    Map<String, Object> card = new HashMap<>();
    card.put("cardNickname", "");
    card.put("creditCardNumber", "4111111111111111");
    card.put("creditCardType", "Visa");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2050");
    card.put("billingAddress", address);
    card.put("saveCreditCard", "true");
    boolean valid = mStorePurchaseProcessHelper.
      addCreditCardToProfile(profile, card, new HashMap());
    assertFalse(valid);
  }

  /**
   * Validate quantity in an order.
   */
  @Test
  public void testValidateQuantity1() throws CommerceException, RepositoryException {
    Order order = mOrderManager.loadOrder("o200002");
    boolean valid = mStorePurchaseProcessHelper.validateQuantity(order, new HashMap());
    assertTrue(valid);
  }
  
  /**
   * If quantity is invalid returns false.
   */
  @Test
  public void testValidateQuantity2() throws CommerceException, RepositoryException {
    Order order = mStoreOrderTools.createOrder("default");
    CommerceItem commerceItem = mStoreOrderTools.createCommerceItem("default");
    ShippingGroupCommerceItemRelationship shipItemRel = (ShippingGroupCommerceItemRelationship)
      mStoreOrderTools.createRelationship("shippingGroupCommerceItem");

    shipItemRel.setCommerceItem(commerceItem);
    order.addCommerceItem(commerceItem);
    order.addRelationship(shipItemRel);
    boolean valid = mStorePurchaseProcessHelper.validateQuantity(order, new HashMap());
    assertFalse(valid);
  }
}