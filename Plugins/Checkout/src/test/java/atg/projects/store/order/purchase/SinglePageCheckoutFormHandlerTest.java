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
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.pricing.OrderPriceInfo;
import atg.droplet.DropletException;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.projects.store.order.purchase.SinglePageCheckoutFormHandler;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.userprofiling.StoreProfileTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.datacollection.DataSource;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for atg.projects.store.order.purchase.SinglePageCheckoutFormHandler.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/test/java/atg/projects/store/order/purchase/SinglePageCheckoutFormHandlerTest.java#1 $$Change: 1385662 $ 
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class SinglePageCheckoutFormHandlerTest {

  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/test/java/atg/projects/store/order/purchase/SinglePageCheckoutFormHandlerTest.java#1 $$Change: 1385662 $";
 
  //---------------------------------------------------------------------------
  // STATIC MEMBERS
  //---------------------------------------------------------------------------

  private static Nucleus mNucleus = null;
  private static SinglePageCheckoutFormHandler mSinglePageCheckoutFormHandler = null;
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
	
  public static final String SINGLE_PAGE_CHECKOUT_FORM_HANDLER_PATH = 
    "/atg/store/order/purchase/SinglePageCheckoutFormHandler";
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
   * the SinglePageCheckoutFormHandler instance to be used in this test.
   * 
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void beforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[]{"CommerceAccelerator.Base", "CommerceAccelerator.Plugins.Account",
      "CommerceAccelerator.Plugins.Promotions", "CommerceAccelerator.Plugins.Checkout"},
      SinglePageCheckoutFormHandlerTest.class, "ShippingInfoFormHandlerTest", ORDER_TOOLS_PATH);
    
    mStoreOrderTools = (StoreOrderTools) mNucleus.resolveName(ORDER_TOOLS_PATH, true);
    assertNotNull(mStoreOrderTools);
    
    mStoreProfileTools = (StoreProfileTools) mNucleus.resolveName(PROFILE_TOOLS_PATH, true);
    assertNotNull(mStoreProfileTools);
    
    mOrderManager = (OrderManager) mNucleus.resolveName(ORDER_MANAGER_PATH, true);
    assertNotNull(mOrderManager);
    
    // Initialize catalog repository and import sample data.
    mProductCatalog = (GSARepository)
      mNucleus.resolveName("/atg/commerce/catalog/ProductCatalog", true);
    assertNotNull(mProductCatalog);
    String[] dataFileNames = { "catalog.xml" };
    TemplateParser.loadTemplateFiles(mProductCatalog, 1, dataFileNames, true,
      new PrintWriter(System.out), null);

    // Initialize the inventory repository and import the sample data.
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

    // Initialize price repository and import sample data.
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
      mSinglePageCheckoutFormHandler = null;
      mOrderRepository = null;
      mSiteRepository = null;
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
   * Set up a new order and a profile every time
   */
  @Before
  public void before() throws RepositoryException, CommerceException {
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
    
    mSinglePageCheckoutFormHandler = 
      (SinglePageCheckoutFormHandler) request.resolveName(
      SINGLE_PAGE_CHECKOUT_FORM_HANDLER_PATH, true);
    assertNotNull(mSinglePageCheckoutFormHandler);
    
    Order order = mOrderManager.loadOrder("o200002");
	mSinglePageCheckoutFormHandler.setOrder(order);
  }
  
  //---------------------------------------------------------------------------
  // TESTS
  //---------------------------------------------------------------------------
  
  /**
   * Test the useShippingAddressForBillingAddress getter/setter.
   */
  @Test
  public void testSetUseShippingAddressforBillingAddress() {
    mSinglePageCheckoutFormHandler.setUseShippingAddressForBillingAddress(false);
    assertEquals(false, mSinglePageCheckoutFormHandler.isUseShippingAddressForBillingAddress());
  }

  /**
   * Test the setShippingMethod and getShippingMethod methods.
   */
  @Test
  public void testSetShippingMethod() {
    mSinglePageCheckoutFormHandler.setShippingMethod("Ground");
    assertEquals(mSinglePageCheckoutFormHandler.getShippingMethod(),"Ground");
  }
  
  /**
   * Test the setCreditCardVerificationNumber and getCreditCardVerificationNumber methods.
   */
  @Test
  public void testSetCreditCardVerificationNumber() {
    mSinglePageCheckoutFormHandler.setCreditCardVerificationNumber("111");
    assertEquals(mSinglePageCheckoutFormHandler.getCreditCardVerificationNumber(),"111");
  }
  
  /**
   * Validate an address input from user and validate whether its gets applied to 
   * shipping group.
   */
  @Test
  public void testValidateAndApplyShippingInformation1() throws ServletException, IOException {
    Map<String, Object> shippingAddress = new HashMap<>();
    shippingAddress.put("addressNickname", "Home1");
    shippingAddress.put("firstName", "Test1");
    shippingAddress.put("lastName", "Test1");
    shippingAddress.put("address1", "Test1");
    shippingAddress.put("state", "CA");
    shippingAddress.put("postalCode", "Test1");
    shippingAddress.put("country", "US");
    
    mSinglePageCheckoutFormHandler.getShippingAddressUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getShippingAddressUserInputValues().putAll(shippingAddress);

    mSinglePageCheckoutFormHandler.setShippingMethod("Ground");

    mSinglePageCheckoutFormHandler.setCommitOrder(false);
 
    mSinglePageCheckoutFormHandler.validateAndApplyShippingInformation(
      ServletUtil.getCurrentRequest(), null);
    assertEquals(0, mSinglePageCheckoutFormHandler.getFormExceptions().size());
    
    assertEquals("Ground", mSinglePageCheckoutFormHandler.
      getShippingGroup().getShippingMethod());
    
    assertEquals("Test1", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getFirstName());
    assertEquals("Test1", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getLastName());
    assertEquals("Test1", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getAddress1());
    assertEquals("CA", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getState());
    assertEquals("Test1", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getPostalCode());
    assertEquals("US", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getCountry());
  }
  
  /**
   * Validate an address input from user and if address is
   * not valid it throws exception.
   */
  @Test
  public void testValidateAndApplyShippingInformation2() throws ServletException, IOException {

    Map<String, Object> shippingAddress = new HashMap<>();
    shippingAddress.put("addressNickname", "Home1");
    shippingAddress.put("firstName", "Test1");
    shippingAddress.put("state", "CA");
    shippingAddress.put("postalCode", "Test1");
    shippingAddress.put("country", "US");

    mSinglePageCheckoutFormHandler.getShippingAddressUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getShippingAddressUserInputValues().putAll(shippingAddress);

    mSinglePageCheckoutFormHandler.setShippingMethod("Ground");
 
    mSinglePageCheckoutFormHandler.setCommitOrder(false);
 
    mSinglePageCheckoutFormHandler.validateAndApplyShippingInformation(
      ServletUtil.getCurrentRequest(), null);
    assertTrue(mSinglePageCheckoutFormHandler.getFormError());
    DropletException e = (DropletException) mSinglePageCheckoutFormHandler.
      getFormExceptions().get(0);
    assertEquals("missingAddressProperty", e.getErrorCode());
  }
  
  /**
   * Validate a credit card input from user and validate whether
   * its gets applied to payment group.
   */
  @Test
  public void testValidateAndApplyCreditCardInformation1() {
    
    Map<String, Object> billingAddress = new HashMap<>();
    billingAddress.put("addressNickname", "Home2");
    billingAddress.put("firstName", "Test2");
    billingAddress.put("lastName", "Test2");
    billingAddress.put("address1", "Test2");
    billingAddress.put("state", "CA");
    billingAddress.put("postalCode", "Test2");
    billingAddress.put("country", "US");

    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("creditCardType", "Visa");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2050");
    card.put("billingAddress", billingAddress);

    mSinglePageCheckoutFormHandler.getCreditCardUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getCreditCardUserInputValues().putAll(card);
    mSinglePageCheckoutFormHandler.setCreditCardVerificationNumber("111");
    mSinglePageCheckoutFormHandler.getBillingAddressUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getBillingAddressUserInputValues().putAll(billingAddress);
 
    mSinglePageCheckoutFormHandler.setCommitOrder(false);
    mSinglePageCheckoutFormHandler.setUseShippingAddressForBillingAddress(false);
 
    mSinglePageCheckoutFormHandler.validateAndApplyCreditCardInformation(
      ServletUtil.getCurrentRequest(), null);
    assertEquals(0, mSinglePageCheckoutFormHandler.getFormExceptions().size());
    
    assertEquals("Test2", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getFirstName());
    assertEquals("Test2", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getLastName());
    assertEquals("Test2", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getAddress1());
    assertEquals("CA", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getState());
    assertEquals("Test2", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getPostalCode());
    assertEquals("US", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getCountry());
    
    assertEquals("4111111111111111", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getCreditCardNumber());
    assertEquals("Visa", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getCreditCardType());
    assertEquals("12", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getExpirationMonth());
    assertEquals("2050", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getExpirationYear());
    
    assertEquals("111", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getCardVerificationNumber());
  }
  
  /**
   * Validate a credit card input from user and
   * if not validated throws an exception.
   */
  @Test
  public void testValidateAndApplyCreditCardInformation2() {
    
    Map<String, Object> billingAddress = new HashMap<>();
    billingAddress.put("addressNickname", "Home2");
    billingAddress.put("firstName", "Test2");
    billingAddress.put("lastName", "Test2");
    billingAddress.put("address1", "Test2");
    billingAddress.put("state", "CA");
    billingAddress.put("postalCode", "Test2");
    billingAddress.put("country", "US");

    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("expirationYear", "2051");
    card.put("billingAddress", billingAddress);

    mSinglePageCheckoutFormHandler.getCreditCardUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getCreditCardUserInputValues().putAll(card);
    mSinglePageCheckoutFormHandler.setCreditCardVerificationNumber("111");
    mSinglePageCheckoutFormHandler.getBillingAddressUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getBillingAddressUserInputValues().putAll(billingAddress);
 
    mSinglePageCheckoutFormHandler.setCommitOrder(false);
    mSinglePageCheckoutFormHandler.setUseShippingAddressForBillingAddress(false);
 
    mSinglePageCheckoutFormHandler.validateAndApplyCreditCardInformation(
      ServletUtil.getCurrentRequest(), null);
    assertTrue(mSinglePageCheckoutFormHandler.getFormError());
    DropletException e = (DropletException) mSinglePageCheckoutFormHandler.
      getFormExceptions().get(0);
    assertEquals("missingCreditCardProperty", e.getErrorCode());
  }
  
  /**
   * Validate quantity in an order.
   * 
   */
  @Test
  public void testValidateQuantity1() throws ServletException, IOException {
    mSinglePageCheckoutFormHandler.setCommitOrder(false);
    mSinglePageCheckoutFormHandler.validateQuantity(ServletUtil.getCurrentRequest(), null);
    assertEquals(0, mSinglePageCheckoutFormHandler.getFormExceptions().size());
  }

  
  /**
   * Add credit card and address to profile.
   */
  @Test
  public void testAddCreditCardAndAddressToProfile1() {
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
    
    Map<String, Object> billingAddress = new HashMap<>();
    billingAddress.put("addressNickname", "Home2");
    billingAddress.put("firstName", "Test2");
    billingAddress.put("lastName", "Test2");
    billingAddress.put("address1", "Test2");
    billingAddress.put("state", "CA");
    billingAddress.put("postalCode", "Test2");
    billingAddress.put("country", "US");
    billingAddress.put("phoneNumber", "908765432");

    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("creditCardType", "Visa");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2050");
    card.put("cardNickname", "Visa");
    card.put("saveCreditCard", "true");
    card.put("billingAddress", billingAddress);

    mSinglePageCheckoutFormHandler.getShippingAddressUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getShippingAddressUserInputValues().putAll(shippingAddress);

    mSinglePageCheckoutFormHandler.setShippingMethod("Ground");

    mSinglePageCheckoutFormHandler.getCreditCardUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getCreditCardUserInputValues().putAll(card);
    mSinglePageCheckoutFormHandler.setCreditCardVerificationNumber("111");
    mSinglePageCheckoutFormHandler.getBillingAddressUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getBillingAddressUserInputValues().putAll(billingAddress);
 
    mSinglePageCheckoutFormHandler.setCommitOrder(false);
    mSinglePageCheckoutFormHandler.setUseShippingAddressForBillingAddress(false);
 
    mSinglePageCheckoutFormHandler.addCreditCardAndAddressToProfile(
      ServletUtil.getCurrentRequest(), null);
    assertEquals(0, mSinglePageCheckoutFormHandler.getFormExceptions().size());
    
    RepositoryItem cardNickname = mStoreProfileTools.getCreditCardByNickname("Visa", profile);
    assertNotNull(cardNickname);
    assertEquals("4111111111111111", cardNickname.getPropertyValue("creditCardNumber"));
    assertEquals("Visa", cardNickname.getPropertyValue("creditCardType"));
    assertEquals("12", cardNickname.getPropertyValue("expirationMonth"));
    assertEquals("2050", cardNickname.getPropertyValue("expirationYear"));

    // Make sure address is there
    Map secondaryAddressMap = (Map) ServletUtil.getCurrentUserProfile().
      getPropertyValue("secondaryAddresses");
    List<String> nicknames = new ArrayList(secondaryAddressMap.keySet());
    assertTrue(nicknames.contains("Home1"));
  }
      
  /**
   * Validate an address and credit card input from user and validate whether
   * its gets applied to shipping group and payment group.
   */
  @Test
  public void testPreExpressCheckout1() throws ServletException, IOException {

    Map<String, Object> shippingAddress = new HashMap<>();
    shippingAddress.put("addressNickname", "Home1");
    shippingAddress.put("firstName", "Test1");
    shippingAddress.put("lastName", "Test1");
    shippingAddress.put("address1", "Test1");
    shippingAddress.put("state", "CA");
    shippingAddress.put("postalCode", "Test1");
    shippingAddress.put("country", "US");
    
    Map<String, Object> billingAddress = new HashMap<>();
    billingAddress.put("addressNickname", "Home2");
    billingAddress.put("firstName", "Test2");
    billingAddress.put("lastName", "Test2");
    billingAddress.put("address1", "Test2");
    billingAddress.put("state", "CA");
    billingAddress.put("postalCode", "Test2");
    billingAddress.put("country", "US");

    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("creditCardType", "Visa");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2050");
    card.put("billingAddress", billingAddress);

    mSinglePageCheckoutFormHandler.getShippingAddressUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getShippingAddressUserInputValues().putAll(shippingAddress);

    mSinglePageCheckoutFormHandler.setShippingMethod("Ground");

    mSinglePageCheckoutFormHandler.getCreditCardUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getCreditCardUserInputValues().putAll(card);
    mSinglePageCheckoutFormHandler.setCreditCardVerificationNumber("111");
    mSinglePageCheckoutFormHandler.getBillingAddressUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getBillingAddressUserInputValues().putAll(billingAddress);
 
    mSinglePageCheckoutFormHandler.setCommitOrder(false);
    mSinglePageCheckoutFormHandler.setUseShippingAddressForBillingAddress(false);
    
    mSinglePageCheckoutFormHandler.preExpressCheckout(ServletUtil.getCurrentRequest(), null);
    assertEquals(0, mSinglePageCheckoutFormHandler.getFormExceptions().size());
    
    assertEquals("Ground", mSinglePageCheckoutFormHandler.
      getShippingGroup().getShippingMethod());
    
    assertEquals("Test1", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getFirstName());
    assertEquals("Test1", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getLastName());
    assertEquals("Test1", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getAddress1());
    assertEquals("CA", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getState());
    assertEquals("Test1", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getPostalCode());
    assertEquals("US", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getCountry());
    
    assertEquals("Test2", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getFirstName());
    assertEquals("Test2", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getLastName());
    assertEquals("Test2", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getAddress1());
    assertEquals("CA", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getState());
    assertEquals("Test2", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getPostalCode());
    assertEquals("US", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getCountry());
    
    assertEquals("4111111111111111", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getCreditCardNumber());
    assertEquals("Visa", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getCreditCardType());
    assertEquals("12", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getExpirationMonth());
    assertEquals("2050", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getExpirationYear());
    
    assertEquals("111", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getCardVerificationNumber());
  }
  
  /**
   * Shipping address is same as billing address.
   */
  @Test
  public void testPreExpressCheckout2() throws ServletException, IOException {

    Map<String, Object> shippingAddress = new HashMap<>();
    shippingAddress.put("addressNickname", "Home1");
    shippingAddress.put("firstName", "Test1");
    shippingAddress.put("lastName", "Test1");
    shippingAddress.put("address1", "Test1");
    shippingAddress.put("state", "CA");
    shippingAddress.put("postalCode", "Test1");
    shippingAddress.put("country", "US");

    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("creditCardType", "Visa");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2050");

    mSinglePageCheckoutFormHandler.getShippingAddressUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getShippingAddressUserInputValues().putAll(shippingAddress);

    mSinglePageCheckoutFormHandler.setShippingMethod("Ground");

    mSinglePageCheckoutFormHandler.getCreditCardUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getCreditCardUserInputValues().putAll(card);
    mSinglePageCheckoutFormHandler.setCreditCardVerificationNumber("111");
    mSinglePageCheckoutFormHandler.getBillingAddressUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getBillingAddressUserInputValues().putAll(shippingAddress);
 
    mSinglePageCheckoutFormHandler.setCommitOrder(false);
    mSinglePageCheckoutFormHandler.setUseShippingAddressForBillingAddress(true);
    
    mSinglePageCheckoutFormHandler.preExpressCheckout(ServletUtil.getCurrentRequest(), null);
    assertEquals(0, mSinglePageCheckoutFormHandler.getFormExceptions().size());
    
    assertEquals("Ground", mSinglePageCheckoutFormHandler.
      getShippingGroup().getShippingMethod());
    
    assertEquals("Test1", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getFirstName());
    assertEquals("Test1", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getLastName());
    assertEquals("Test1", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getAddress1());
    assertEquals("CA", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getState());
    assertEquals("Test1", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getPostalCode());
    assertEquals("US", ((HardgoodShippingGroup)(mSinglePageCheckoutFormHandler.
      getShippingGroup())).getShippingAddress().getCountry());
    
    assertEquals("Test1", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getFirstName());
    assertEquals("Test1", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getLastName());
    assertEquals("Test1", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getAddress1());
    assertEquals("CA", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getState());
    assertEquals("Test1", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getPostalCode());
    assertEquals("US", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getBillingAddress().getCountry());
    
    assertEquals("4111111111111111", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getCreditCardNumber());
    assertEquals("Visa", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getCreditCardType());
    assertEquals("12", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getExpirationMonth());
    assertEquals("2050", ((CreditCard)(mSinglePageCheckoutFormHandler.
      getPaymentGroup())).getExpirationYear());
    
    assertEquals("111", ((CreditCard)(mSinglePageCheckoutFormHandler.getPaymentGroup())).
      getCardVerificationNumber());
  }
    
  /**
   * Validate an address and credit card input from user and if data passed is
   * not validated it throws an exception.
   */
  @Test
  public void testpreExpressCheckout3() throws ServletException, IOException {

    Map<String, Object> shippingAddress = new HashMap<>();
    shippingAddress.put("addressNickname", "Home1");
    shippingAddress.put("firstName", "Test1");
    shippingAddress.put("state", "CA");
    shippingAddress.put("postalCode", "Test1");
    shippingAddress.put("country", "US");
    
    Map<String, Object> billingAddress = new HashMap<>();
    billingAddress.put("addressNickname", "Home2");
    billingAddress.put("firstName", "Test2");
    billingAddress.put("lastName", "Test2");
    billingAddress.put("address1", "Test2");
    billingAddress.put("state", "CA");
    billingAddress.put("postalCode", "Test2");
    billingAddress.put("country", "US");

    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("creditCardType", "Visa");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2050");
    card.put("billingAddress", billingAddress);

    mSinglePageCheckoutFormHandler.getShippingAddressUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getShippingAddressUserInputValues().putAll(shippingAddress);

    mSinglePageCheckoutFormHandler.setShippingMethod("Ground");

    mSinglePageCheckoutFormHandler.getCreditCardUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getCreditCardUserInputValues().putAll(card);
    mSinglePageCheckoutFormHandler.setCreditCardVerificationNumber("111");
    mSinglePageCheckoutFormHandler.getBillingAddressUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getBillingAddressUserInputValues().putAll(billingAddress);
 
    mSinglePageCheckoutFormHandler.setCommitOrder(false);
 
    mSinglePageCheckoutFormHandler.preExpressCheckout(ServletUtil.getCurrentRequest(), null);
    assertTrue(mSinglePageCheckoutFormHandler.getFormError());
    DropletException e = 
      (DropletException) mSinglePageCheckoutFormHandler.getFormExceptions().get(0);
    assertEquals("missingAddressProperty", e.getErrorCode());
  }
  
  /**
   * Validate an address and credit card input from user and if data passed is
   * not validated it throws an exception.
   */
  @Test
  public void testpreExpressCheckout4() throws ServletException, IOException {

    Map<String, Object> shippingAddress = new HashMap<>();
    shippingAddress.put("addressNickname", "Home1");
    shippingAddress.put("firstName", "Test1");
    shippingAddress.put("lastName", "Test1");
    shippingAddress.put("address1", "Test1");
    shippingAddress.put("state", "CA");
    shippingAddress.put("postalCode", "Test1");
    shippingAddress.put("country", "US");
    
    Map<String, Object> billingAddress = new HashMap<>();
    billingAddress.put("addressNickname", "Home2");
    billingAddress.put("firstName", "Test2");
    billingAddress.put("lastName", "Test2");
    billingAddress.put("address1", "Test2");
    billingAddress.put("state", "CA");
    billingAddress.put("postalCode", "Test2");
    billingAddress.put("country", "US");

    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2050");
    card.put("billingAddress", billingAddress);

    mSinglePageCheckoutFormHandler.getShippingAddressUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getShippingAddressUserInputValues().putAll(shippingAddress);

    mSinglePageCheckoutFormHandler.setShippingMethod("Ground");

    mSinglePageCheckoutFormHandler.getCreditCardUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getCreditCardUserInputValues().putAll(card);
    mSinglePageCheckoutFormHandler.setCreditCardVerificationNumber("111");
    mSinglePageCheckoutFormHandler.getBillingAddressUserInputValues().clear();
    mSinglePageCheckoutFormHandler.getBillingAddressUserInputValues().putAll(billingAddress);
 
    mSinglePageCheckoutFormHandler.setCommitOrder(false);
 
    mSinglePageCheckoutFormHandler.preExpressCheckout(ServletUtil.getCurrentRequest(), null);
    assertTrue(mSinglePageCheckoutFormHandler.getFormError());
    DropletException e = 
      (DropletException) mSinglePageCheckoutFormHandler.getFormExceptions().get(0);
    assertEquals("missingCreditCardProperty", e.getErrorCode());
  }
}