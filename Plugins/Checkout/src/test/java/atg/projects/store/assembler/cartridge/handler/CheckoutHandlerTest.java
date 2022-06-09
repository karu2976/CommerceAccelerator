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

import atg.beans.PropertyNotFoundException;
import atg.commerce.order.OrderHolder;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingTools;
import atg.core.i18n.CountryList;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.projects.store.shipping.ShippingTools;
import atg.projects.store.userprofiling.StoreProfilePropertyManager;
import atg.projects.store.userprofiling.StoreProfileTools;
import atg.projects.store.util.YearListGenerator;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.service.filter.bean.BeanFilterException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.ContentItem;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * This unit test will test the methods of the CheckoutHandler class.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/test/java/atg/projects/store/assembler/cartridge/handler/CheckoutHandlerTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class CheckoutHandlerTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/test/java/atg/projects/store/assembler/cartridge/handler/CheckoutHandlerTest.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  private static CheckoutHandler mCheckoutHandler = null;
  private static Nucleus mNucleus = null;
  private static StoreProfileTools mStoreProfileTools = null;

  private static final String CHECKOUT_HANDLER_PATH = "/atg/endeca/assembler/cartridge/handler/CheckoutHandler";
  public static final String PROFILE = "/atg/userprofiling/Profile";
  public static final String PROFILE_TOOLS_PATH = "/atg/userprofiling/ProfileTools";

  //---------------------------------------------------------------------------
  // JUNIT
  //---------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the CheckoutHandler instance to be used in this test.
   *
   * @throws Exception When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[]{"CommerceAccelerator.Plugins.Checkout"},
      CheckoutHandlerTest.class,
      "CheckoutHandlerTest",
      "/atg/Initial");
    
    mStoreProfileTools = (StoreProfileTools) mNucleus.resolveName(PROFILE_TOOLS_PATH, true);
    assertNotNull(mStoreProfileTools);
  }

  /**
   * Set up a new profile every time
   */
  @Before
  public void before() {
    // Set the current request & profile
    ServletTestUtils utils = new ServletTestUtils();
    DynamoHttpServletRequest request = utils.createDynamoHttpServletRequestForSession(mNucleus, null, null);
    ServletUtil.setCurrentRequest(request);
    
    Profile profile = (Profile) request.resolveName(PROFILE);
    ServletUtil.setCurrentUserProfile(profile);
    mStoreProfileTools.createNewUser("110000", "user", profile);

    mCheckoutHandler = (CheckoutHandler) request.resolveName(CHECKOUT_HANDLER_PATH, true);
    assertNotNull(mCheckoutHandler);
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

    mCheckoutHandler = null;
  }
  
  /**
   * @return The current profile
   */
  public Profile getProfile(){
    return (Profile) ServletUtil.getCurrentUserProfile();
  }
  
  /**
   * Returns an address map.
   *
   * @param pAddressName
   * @return
   */
  public Map getAddressMap(String pAddressName, boolean pDefault){
    Map<String, Object> address = new HashMap<>();
    address.put("addressNickname", pAddressName);
    address.put("firstName", "Test");
    address.put("lastName", "Test");
    address.put("address1", "Test");
    address.put("state", "CA");
    address.put("postalCode", "Test");
    address.put("country", "US");

    if(pDefault){
      address.put("useAsDefaultShippingAddress", "true");
    }

    return address;
  }
  
  /**
   * Adds address pAddressName to the profile.
   *
   * @param pAddressName Name of the address to add, e.g "Home".
   * @throws Exception
   */
  public void addAddressToProfile(String pAddressName, boolean pDefault) throws ClassNotFoundException,
    IntrospectionException, InstantiationException, RepositoryException, IllegalAccessException
  {
    mStoreProfileTools.addAddress(getProfile(), getAddressMap(pAddressName, pDefault));
  }

  /**
   * Returns a card map.
   *
   * @param pCardName Card nickname.
   * @param pDefault Set this as the default card.
   * @return Map of card properties.
   */
  public Map getCardMap(String pCardName, boolean pDefault){
    Map<String, Object> card = new HashMap<>();
    card.put("creditCardNumber", "4111111111111111");
    card.put("creditCardType", "Visa");
    card.put("expirationMonth", "12");
    card.put("expirationYear", "2050");
    card.put("cardNickname", pCardName);
    //card.put("billingAddress", "Home");

    if(pDefault) {
      card.put("setAsDefaultCard", "true");
    }

    return card;
  }

  /**
   * Add a card to the profile.
   *
   * @param pCardName The card name.
   * @param pDefault Default card.
   * @throws Exception
   */
  public void addCardToProfile(String pCardName, boolean pDefault) throws RepositoryException,
    IntrospectionException, PropertyNotFoundException
  {
    mStoreProfileTools.createProfileCreditCard(getProfile(), getCardMap(pCardName, pDefault),
      pCardName, getAddressMap("Home", pDefault));
  }
  
  /**
   * Test the setUserPricingModels and getUserPricingModels methods.
   */
  @Test
  public void testSetUserPricingModels() {
    PricingModelHolder pricingModel = mCheckoutHandler.getUserPricingModels();
    assertNotNull(pricingModel);
    mCheckoutHandler.setUserPricingModels(pricingModel);
    assertNotNull(mCheckoutHandler.getUserPricingModels());
  }
  
  /**
   * Test the setShippingTools and getShippingTools methods.
   */
  @Test
  public void testSetShippingTools() {
    ShippingTools shippingTools = mCheckoutHandler.getShippingTools();
    assertNotNull(shippingTools);
    mCheckoutHandler.setShippingTools(shippingTools);
    assertNotNull(mCheckoutHandler.getShippingTools());
  }

  /**
   * Test the setShoppingCart and getShoppingCart methods.
   */
  @Test
  public void testSetShoppingCart() {
    OrderHolder orderHolder = mCheckoutHandler.getShoppingCart();
    assertNotNull(orderHolder);
    mCheckoutHandler.setShoppingCart(orderHolder);
    assertNotNull(mCheckoutHandler.getShoppingCart());
  }
  
  /**
   * Test the setResourceBundleName and getResourceBundleName methods.
   */
  @Test
  public void testSetResourceBundleName() {
    String resourceBundle = mCheckoutHandler.getResourceBundleName();
    assertNotNull(resourceBundle);
    mCheckoutHandler.setResourceBundleName(resourceBundle);
    assertNotNull(mCheckoutHandler.getResourceBundleName());
  }
  
  /**
   * Test the setPermittedCountries and getPermittedCountries methods.
   */
  @Test
  public void testSetPermittedCountries() {
    CountryList countryList = mCheckoutHandler.getPermittedCountries();
    assertNotNull(countryList);
    mCheckoutHandler.setPermittedCountries(countryList);
    assertNotNull(mCheckoutHandler.getPermittedCountries());
  }

  /**
   * Test the setFilterId and getFilterId methods.
   */
  @Test
  public void testSetFilterId() {
    String filterId = mCheckoutHandler.getFilterId();
    assertNotNull(filterId);
    mCheckoutHandler.setFilterId(filterId);
    assertNotNull(mCheckoutHandler.getFilterId());
  }
  
  /**
   * Test the setProfilePropertyManager and getProfilePropertyManager methods.
   */
  @Test
  public void testSetProfilePropertyManager() {
    StoreProfilePropertyManager profileProperty = mCheckoutHandler.getProfilePropertyManager();
    assertNotNull(profileProperty);
    mCheckoutHandler.setProfilePropertyManager(profileProperty);
    assertNotNull(mCheckoutHandler.getProfilePropertyManager());
  }

  /**
   * Test the setProfileTools and getProfileTools methods.
   */
  @Test
  public void testSetProfileTools() {
    StoreProfileTools profileTools = mCheckoutHandler.getProfileTools();
    assertNotNull(profileTools);
    mCheckoutHandler.setProfileTools(profileTools);
    assertNotNull(mCheckoutHandler.getProfileTools());
  }
  
  /**
   * Test the setProfile and getProfile methods.
   */
  @Test
  public void testSetProfile() {
    Profile profile = mCheckoutHandler.getProfile();
    assertNotNull(profile);
    mCheckoutHandler.setProfile(profile);
    assertNotNull(mCheckoutHandler.getProfile());
  }
  
  /**
   * Test the setPricingTools and getPricingTools methods.
   */
  @Test
  public void testSetPricingTools() {
	PricingTools pricingTools = mCheckoutHandler.getPricingTools();
    assertNotNull(pricingTools);
    mCheckoutHandler.setPricingTools(pricingTools);
    assertNotNull(mCheckoutHandler.getPricingTools());
  }
  
  /**
   * Test the setYearList and getYearList methods.
   */
  @Test
  public void testSetYearList() {
    YearListGenerator yearList = mCheckoutHandler.getYearList();
    assertNotNull(yearList);
    mCheckoutHandler.setYearList(yearList);
    assertNotNull(mCheckoutHandler.getYearList());
  }
  
  /**
   * Test the populateAvailableCountries.
   */
  @Test
  public void testPopulateAvailableCountries() {
    ContentItem contentItem = new BasicContentItem();
    mCheckoutHandler.populateAvailableCountries(contentItem);
    assertNotNull(contentItem.get("availableCountries"));
  }
  
  /**
   * Test the populateAvailableStates.
   */
  @Test
  public void testPopulateAvailableStates() {
	ContentItem contentItem = new BasicContentItem();
    mCheckoutHandler.populateAvailableStates(contentItem);
    assertNotNull(contentItem.get("states"));
    assertNotNull(contentItem.get("currentCountry"));
  }
  
  /**
   * Test the populateAvailablePaymentMethods.
   */
  @Test
  public void testPopulateAvailablePaymentMethods() throws BeanFilterException, RepositoryException,
    IntrospectionException, PropertyNotFoundException
  {
    ContentItem contentItem = new BasicContentItem();
    addCardToProfile("Visa", false);
    mCheckoutHandler.populateAvailablePaymentMethods(contentItem);
    assertNotNull(contentItem.get("availablePaymentMethods"));
  }
  
  /**
   * Test the populateAvailableShippingAddress.
  */ 
  @Test
  public void testPopulateAvailableShippingAddress() throws ClassNotFoundException, IntrospectionException,
    InstantiationException, RepositoryException, IllegalAccessException, BeanFilterException
  {
    ContentItem contentItem = new BasicContentItem();
    addAddressToProfile("Home", false);
    mCheckoutHandler.populateAvailableShippingAddress(contentItem);
    assertNotNull(contentItem.get("availableShippingAddress"));
  }
}
