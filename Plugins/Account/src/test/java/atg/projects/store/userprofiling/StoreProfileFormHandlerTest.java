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
package atg.projects.store.userprofiling;

import atg.beans.PropertyNotFoundException;
import atg.droplet.DropletException;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.projects.store.userprofiling.StoreProfileFormHandler;
import atg.projects.store.userprofiling.StoreProfileTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for atg.projects.store.userprofiling.StoreProfileFormHandler.
 *
 * @author Oracle
 */
public class StoreProfileFormHandlerTest {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/test/java/atg/projects/store/userprofiling/StoreProfileFormHandlerTest.java#1 $$Change: 1385662 $";


  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  private static final String PROFILE_FORM_HANDLER_PATH = "/atg/userprofiling/ProfileFormHandler";
  private static final String PROFILE = "/atg/userprofiling/Profile";
  private static final String PROFILE_TOOLS_PATH = "/atg/userprofiling/ProfileTools";

  private static Nucleus mNucleus = null;
  private static StoreProfileFormHandler mStoreProfileFormHandler = null;
  private static StoreProfileTools mStoreProfileTools = null;

  //---------------------------------------------------------------------------
  // JUNIT SETUP
  //---------------------------------------------------------------------------

  /**
   * One time setup.
   */
  @BeforeClass
  public static void beforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[]{"DPS", "DCS", "CommerceAccelerator.Base", "CommerceAccelerator.Plugins.Account"},
        StoreProfileFormHandlerTest.class, PROFILE_TOOLS_PATH);

    mStoreProfileTools = (StoreProfileTools) mNucleus.resolveName(PROFILE_TOOLS_PATH, true);
    assertNotNull(mStoreProfileTools);
  }

  /**
   * One time tear down.
   */
  @AfterClass
  public static void afterClass() throws IOException, ServiceException {
    if(mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
      mNucleus = null;
    }
  }

  /**
   * Set up a new profile every time
   */
  @Before
  public void before(){
    // Set the current request & profile
    ServletTestUtils utils = new ServletTestUtils();
    DynamoHttpServletRequest request = utils.createDynamoHttpServletRequestForSession(mNucleus, null, null);
    ServletUtil.setCurrentRequest(request);
    Profile profile = (Profile) request.resolveName(PROFILE);
    ServletUtil.setCurrentUserProfile(profile);
    mStoreProfileTools.createNewUser("user", profile);

    mStoreProfileFormHandler = (StoreProfileFormHandler) request.resolveName(PROFILE_FORM_HANDLER_PATH, true);
    assertNotNull(mStoreProfileFormHandler);
  }

  //---------------------------------------------------------------------------
  // UTILITY
  //---------------------------------------------------------------------------

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
   * Adds an address to the profile.
   *
   * @param pAddress
   * @throws Exception
   */
  public void addAddressToProfile(Map pAddress) throws ClassNotFoundException, IntrospectionException,
    InstantiationException, RepositoryException, IllegalAccessException
  {
    mStoreProfileTools.addAddress(getProfile(), pAddress);
  }

  /**
   * Returns a card map.
   *
   * @param pCardName Card nickname.
   * @param pDefault Set this as the default card.
   * @return Map of card properties.
   */
  public Map getCardMap(String pCardName, boolean pDefault) {
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
   * Adds a card to the profile.
   *
   * @param pCard Map of card properties.
   * @param pBillingAddress Map of billing address properties.
   * @throws Exception
   */
  public void addCardToProfile(Map pCard, Map pBillingAddress) throws RepositoryException,
    IntrospectionException, PropertyNotFoundException
  {
    mStoreProfileTools.createProfileCreditCard(getProfile(), pCard,
      pCard.get("cardNickname").toString(), pBillingAddress);
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

  //---------------------------------------------------------------------------
  // TESTS
  //---------------------------------------------------------------------------

  /**
   * Tests adding an address to the profile.
   */
  @Test
  public void testHandleAddAddress() throws ServletException, IOException {
    Map<String, Object> address = getAddressMap("Home", true);

    mStoreProfileFormHandler.getAddressUserInputValues().clear();
    mStoreProfileFormHandler.getAddressUserInputValues().putAll(address);

    mStoreProfileFormHandler.handleAddAddress(ServletUtil.getCurrentRequest(), null);

    assertFalse(mStoreProfileFormHandler.getFormError());

    // Now make sure the address is on the profile
    Map secondaryAddressMap = (Map) getProfile().getPropertyValue("secondaryAddresses");
    List<String> nicknames = new ArrayList(secondaryAddressMap.keySet());
    assertTrue(nicknames.contains("Home"));
  }

  /**
   * Create credit card
   */
  @Test
  public void testHandleCreateCreditCard() throws ServletException, IOException {
    Profile profile = getProfile();
    Map<String, Object> address = getAddressMap("Home", false);
    Map<String, Object> card = getCardMap("Visa", true);

    mStoreProfileFormHandler.getAddressUserInputValues().clear();
    mStoreProfileFormHandler.getAddressUserInputValues().putAll(address);
    mStoreProfileFormHandler.getCreditCardUserInputValues().clear();
    mStoreProfileFormHandler.getCreditCardUserInputValues().putAll(card);

    mStoreProfileFormHandler.handleCreateCreditCard(ServletUtil.getCurrentRequest(), null);
    assertFalse(mStoreProfileFormHandler.getFormError());

    // Now make sure the card is on the profile
    Map creditCards = (Map) profile.getPropertyValue("creditCards");
    List<String> nicknames = new ArrayList(creditCards.keySet());
    assertTrue(nicknames.contains("Visa"));
  }

  /**
   * Update address
   */
  @Test
  public void testHandleUpdateAddress() throws ClassNotFoundException, IntrospectionException,
    InstantiationException, RepositoryException, IllegalAccessException, ServletException, IOException
  {
    Profile profile = getProfile();
    Map<String, Object> address = getAddressMap("Home", false);
    addAddressToProfile(address);

    Map secondaryAddressMap = (Map) profile.getPropertyValue("secondaryAddresses");
    List<String> addressNicknames = new ArrayList(secondaryAddressMap.keySet());
    assertTrue(addressNicknames.contains("Home"));

    RepositoryItem home = (RepositoryItem) secondaryAddressMap.get("Home");
    String address1 = (String) home.getPropertyValue("address1");
    assertEquals("Test", address1);

    // Perform the update
    address.put("address1", "home");
    address.put("newAddressNickname", "Home");

    mStoreProfileFormHandler.getAddressUserInputValues().clear();
    mStoreProfileFormHandler.getAddressUserInputValues().putAll(address);

    mStoreProfileFormHandler.handleUpdateAddress(ServletUtil.getCurrentRequest(), null);
    assertEquals(false, mStoreProfileFormHandler.getFormError());

    secondaryAddressMap = (Map) profile.getPropertyValue("secondaryAddresses");
    addressNicknames = new ArrayList(secondaryAddressMap.keySet());
    assertTrue(addressNicknames.contains("Home"));

    home = (RepositoryItem) secondaryAddressMap.get("Home");
    address1 = (String) home.getPropertyValue("address1");
    assertEquals("home", address1);
  }

  /**
   * Remove address
   */
  @Test
  public void testHandleRemoveAddress() throws ServletException, IOException, ClassNotFoundException,
    IntrospectionException, InstantiationException, RepositoryException, IllegalAccessException
  {
      Profile profile = getProfile();
      Map<String, Object> address = getAddressMap("Home", false);
      addAddressToProfile(address);

      Map secondaryAddressMap = (Map) profile.getPropertyValue("secondaryAddresses");
      List<String> addressNicknames = new ArrayList(secondaryAddressMap.keySet());
      assertTrue(addressNicknames.contains("Home"));

      address = new HashMap<>();
      address.put("removeAddressNickname", "Home");

      mStoreProfileFormHandler.getAddressUserInputValues().clear();
      mStoreProfileFormHandler.getAddressUserInputValues().putAll(address);

      mStoreProfileFormHandler.handleRemoveAddress(ServletUtil.getCurrentRequest(), null);
      assertEquals(false, mStoreProfileFormHandler.getFormError());

      secondaryAddressMap = (Map) profile.getPropertyValue("secondaryAddresses");
      addressNicknames = new ArrayList(secondaryAddressMap.keySet());
      assertFalse(addressNicknames.contains("Home"));
    }

  /**
   * Remove a card
   */
  @Test
  public void testHandleRemoveCard() throws ServletException, IOException, RepositoryException,
    IntrospectionException, PropertyNotFoundException
  {
    Profile profile = getProfile();
    Map<String, Object> address = getAddressMap("Home", false);
    Map<String, Object> card = getCardMap("Visa", false);
    addCardToProfile(card, address);

    Map creditCards = (Map) profile.getPropertyValue("creditCards");
    List<String> nicknames = new ArrayList(creditCards.keySet());
    assertTrue(nicknames.contains("Visa"));

    card = new HashMap<>();
    card.put("removeCardNickname", "Visa");

    mStoreProfileFormHandler.getCreditCardUserInputValues().clear();
    mStoreProfileFormHandler.getCreditCardUserInputValues().putAll(card);

    mStoreProfileFormHandler.handleRemoveCard(ServletUtil.getCurrentRequest(), null);
    assertEquals(false, mStoreProfileFormHandler.getFormError());

    creditCards = (Map) profile.getPropertyValue("creditCards");
    nicknames = new ArrayList(creditCards.keySet());
    assertFalse(nicknames.contains("Visa"));
  }

  /**
   * Update a card property and nickname
   */
  @Test
  public void testHandleUpdateCard() throws RepositoryException, IntrospectionException,
    PropertyNotFoundException, ServletException, IOException
  {
    Profile profile = getProfile();
    Map<String, Object> address = getAddressMap("Home", false);
    Map<String, Object> card = getCardMap("Visa", false);

    String nickname = mStoreProfileTools.createProfileCreditCard(profile, card, "Visa", address);
    Map creditCards = (Map) profile.getPropertyValue("creditCards");
    List<String> nicknames = new ArrayList<>(creditCards.keySet());
    assertTrue(nicknames.contains("Visa"));

    RepositoryItem cardNickname = mStoreProfileTools.getCreditCardByNickname(nickname, profile);
    assertNotNull(cardNickname);
    String year = (String) cardNickname.getPropertyValue("expirationYear");
    assertEquals("2050", year);

    // Update it
    card = new HashMap<>();
    card.put("expirationMonth", "12");
    card.put("cardNickname", "Visa");
    card.put("expirationYear", "2051");
    card.put("newCardNickname", "VISA");

    mStoreProfileFormHandler.getAddressUserInputValues().clear();
    mStoreProfileFormHandler.getAddressUserInputValues().putAll(address);
    mStoreProfileFormHandler.getCreditCardUserInputValues().clear();
    mStoreProfileFormHandler.getCreditCardUserInputValues().putAll(card);

    mStoreProfileFormHandler.handleUpdateCard(ServletUtil.getCurrentRequest(), null);
    assertEquals(false, mStoreProfileFormHandler.getFormError());

    // Old card nickname doesn't exist
    RepositoryItem oldCardNickname = mStoreProfileTools.getCreditCardByNickname("Visa", profile);
    assertNull(oldCardNickname);

    // New one does with updated property
    cardNickname = mStoreProfileTools.getCreditCardByNickname("VISA", profile);
    assertNotNull(cardNickname);
    year = (String) cardNickname.getPropertyValue("expirationYear");
    assertEquals("2051", year);
  }

  /**
   * Test pre create user
   */
  @Test
  public void testPreCreateUser() throws ServletException, IOException {
    // Invalid login format
    mStoreProfileFormHandler.getValueMap().put("login", "test");
    mStoreProfileFormHandler.handleCreate(ServletUtil.getCurrentRequest(), null);
    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("invalidEmailAddress", e.getErrorCode());

    assertNull(mStoreProfileFormHandler.getValueMap().get("dateOfBirth"));
    assertNull(mStoreProfileFormHandler.getValueMap().get("email"));
  }

  /**
   * Test pre create user
   */
  @Test
  public void testPreCreateUser2() throws ServletException, IOException {
    // Invalid email format
    mStoreProfileFormHandler.getValueMap().put("login", "test@test.com");
    mStoreProfileFormHandler.getValueMap().put("email", "test");
    mStoreProfileFormHandler.handleCreate(ServletUtil.getCurrentRequest(), null);
    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e2 = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("invalidEmailAddress", e2.getErrorCode());

    assertNull(mStoreProfileFormHandler.getValueMap().get("dateOfBirth"));
  }

  /**
   * Test pre create user
   */
  @Test
  public void testPreCreateUser3() throws ServletException, IOException {
    // Invalid birthday
    mStoreProfileFormHandler.getValueMap().put("login", "test@test.com");
    mStoreProfileFormHandler.getValueMap().put("email", "test@test.com");

    Calendar c = Calendar.getInstance();
    c.add(Calendar.YEAR, 1);
    mStoreProfileFormHandler.setBirthday(c.getTime().toString());

    mStoreProfileFormHandler.handleCreate(ServletUtil.getCurrentRequest(), null);
    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("invalidDateFormat", e.getErrorCode());

    assertNull(mStoreProfileFormHandler.getValueMap().get("dateOfBirth"));
  }

  /**
   * Test pre create user
   */
  @Test
  public void testPreCreateUser4() throws ServletException, IOException {
    mStoreProfileFormHandler.getValueMap().put("login", "test@test.com");
    mStoreProfileFormHandler.getValueMap().put("email", "test@test.com");
    mStoreProfileFormHandler.setBirthday("04/05/1986");

    mStoreProfileFormHandler.handleCreate(ServletUtil.getCurrentRequest(), null);

    // Make sure everything is in the value map
    assertNotNull(mStoreProfileFormHandler.getValueMap().get("login"));
    assertNotNull(mStoreProfileFormHandler.getValueMap().get("dateOfBirth"));
    assertNotNull(mStoreProfileFormHandler.getValueMap().get("email"));
  }

  /**
   * Test pre update user
   */
  @Test
  public void testPreUpdateUser() throws ServletException, IOException {
    // Invalid login format
    mStoreProfileFormHandler.getValueMap().put("email", "test");

    mStoreProfileFormHandler.handleUpdate(ServletUtil.getCurrentRequest(), null);
    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("invalidEmailAddress", e.getErrorCode());

    assertNull(mStoreProfileFormHandler.getValueMap().get("login"));
    assertNull(mStoreProfileFormHandler.getValueMap().get("dateOfBirth"));
  }

  /**
   * Test pre update user
   */
  @Test
  public void testPreUpdateUser2() throws ServletException, IOException {
    // Invalid birthday
    mStoreProfileFormHandler.getValueMap().put("email", "test@test.com");
    Profile profile = getProfile();
    profile.setPropertyValue("email", "test@test.com");

    Calendar c = Calendar.getInstance();
    c.add(Calendar.YEAR, 1);
    mStoreProfileFormHandler.setBirthday(c.getTime().toString());

    mStoreProfileFormHandler.handleUpdate(ServletUtil.getCurrentRequest(), null);
    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("invalidDateFormat", e.getErrorCode());

    assertNull(mStoreProfileFormHandler.getValueMap().get("login"));
    assertNull(mStoreProfileFormHandler.getValueMap().get("dateOfBirth"));
  }

  /**
   * Test pre update user
   */
  @Test
  public void testPreUpdateUser3() throws ServletException, IOException {
    // Valid
    mStoreProfileFormHandler.getValueMap().put("email", "test@test.com");
    Profile profile = getProfile();
    profile.setPropertyValue("email", "test@test.com");
    mStoreProfileFormHandler.setBirthday("04/05/1986");

    mStoreProfileFormHandler.handleUpdate(ServletUtil.getCurrentRequest(), null);

    // Preupdate user copies these values into the value map if successfully executed
    assertNotNull(mStoreProfileFormHandler.getValueMap().get("login"));
    assertNotNull(mStoreProfileFormHandler.getValueMap().get("dateOfBirth"));
  }

  /**
   * Test pre update user with null birthday
   */
  @Test
  public void testPreUpdateUser4() throws ServletException, IOException {
    // Valid
    mStoreProfileFormHandler.getValueMap().put("email", "test@test.com");
    Profile profile = getProfile();
    profile.setPropertyValue("email", "test@test.com");
    mStoreProfileFormHandler.setBirthday(null);

    mStoreProfileFormHandler.handleUpdate(ServletUtil.getCurrentRequest(), null);

    // Preupdate user copies these values into the value map if successfully executed
    assertNotNull(mStoreProfileFormHandler.getValueMap().get("login"));
    assertNull(mStoreProfileFormHandler.getValueMap().get("dateOfBirth"));
  }

  /**
   * Test pre update user
   */
  @Test
  public void testHandleUpdateCheckoutDefaults() throws ClassNotFoundException, IntrospectionException,
    InstantiationException, RepositoryException, IllegalAccessException, PropertyNotFoundException,
    ServletException, IOException
  {
    Profile profile = getProfile();
    addAddressToProfile("Home", false);
    addCardToProfile("Visa", false);

    mStoreProfileFormHandler.getCheckoutDefaultsUserInputValues().put("defaultCarrier", "Ground");
    mStoreProfileFormHandler.getCheckoutDefaultsUserInputValues().put("shippingAddress", "Home");
    mStoreProfileFormHandler.getCheckoutDefaultsUserInputValues().put("defaultCreditCard", "Visa");
    mStoreProfileFormHandler.handleUpdateCheckoutDefaults(ServletUtil.getCurrentRequest(), null);

    assertEquals("Ground", profile.getPropertyValue("defaultCarrier"));

    assertNotNull(profile.getPropertyValue("shippingAddress"));
    RepositoryItem shippingAddress = (RepositoryItem) profile.getPropertyValue("shippingAddress");
    assertEquals("Test", shippingAddress.getPropertyValue("address1"));

    assertNotNull(profile.getPropertyValue("defaultCreditCard"));
    RepositoryItem creditCard = (RepositoryItem) profile.getPropertyValue("defaultCreditCard");
    assertEquals("4111111111111111", creditCard.getPropertyValue("creditCardNumber"));

    // Now unset them
    mStoreProfileFormHandler.getCheckoutDefaultsUserInputValues().put("defaultCarrier", "");
    mStoreProfileFormHandler.getCheckoutDefaultsUserInputValues().put("shippingAddress", "");
    mStoreProfileFormHandler.getCheckoutDefaultsUserInputValues().put("defaultCreditCard", "");
    mStoreProfileFormHandler.handleUpdateCheckoutDefaults(ServletUtil.getCurrentRequest(), null);

    assertNull(profile.getPropertyValue("shippingAddress"));
    assertNull(profile.getPropertyValue("defaultCreditCard"));
    assertNull(profile.getPropertyValue("defaultCarrier"));
  }

  /**
   * Test error code for invalid new card nickname.
   */
  @Test
  public void testPreUpdateCard() throws ServletException, IOException {
    mStoreProfileFormHandler.getCreditCardUserInputValues().put("newCardNickname", "");

    mStoreProfileFormHandler.preUpdateCard(ServletUtil.getCurrentRequest(), null);

    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("missingCreditCardProperty", e.getErrorCode());
  }

  /**
   * Test error code for invalid card nickname.
   */
  @Test
  public void testPreUpdateCard2() throws ServletException, IOException {
    mStoreProfileFormHandler.getCreditCardUserInputValues().put("newCardNickname", "Visa");
    mStoreProfileFormHandler.getCreditCardUserInputValues().put("cardNickname", "");

    mStoreProfileFormHandler.preUpdateCard(ServletUtil.getCurrentRequest(), null);

    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("missingCreditCardProperty", e.getErrorCode());
  }

  /**
   * Test error code for duplicate card nickname.
   */
  @Test
  public void testPreUpdateCard3() throws RepositoryException, IntrospectionException, PropertyNotFoundException,
    ServletException, IOException
  {
    addCardToProfile("MyCard", false);
    addCardToProfile("Visa", false);

    mStoreProfileFormHandler.getCreditCardUserInputValues().put("newCardNickname", "Visa");
    mStoreProfileFormHandler.getCreditCardUserInputValues().put("cardNickname", "MyCard");

    mStoreProfileFormHandler.preUpdateCard(ServletUtil.getCurrentRequest(), null);

    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("errorDuplicateCCNickname", e.getErrorCode());
  }

  /**
   * Test error code for invalid address nickname.
   */
  @Test
  public void testPreUpdateAddress() throws ServletException, IOException {
    mStoreProfileFormHandler.getAddressUserInputValues().put("addressNickname", "");

    mStoreProfileFormHandler.preUpdateAddress(ServletUtil.getCurrentRequest(), null);

    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("missingAddressProperty", e.getErrorCode());
  }

  /**
   * Test error code for invalid new address nickname.
   */
  @Test
  public void testPreUpdateAddress2() throws ServletException, IOException {
    mStoreProfileFormHandler.getAddressUserInputValues().put("addressNickname", "Home");
    mStoreProfileFormHandler.getAddressUserInputValues().put("newAddressNickname", "");

    mStoreProfileFormHandler.preUpdateAddress(ServletUtil.getCurrentRequest(), null);

    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("missingAddressProperty", e.getErrorCode());
  }

  /**
   * Test error code for duplicate card nickname.
   */
  @Test
  public void testPreUpdateAddress3() throws ServletException, IOException, ClassNotFoundException,
    IntrospectionException, InstantiationException, RepositoryException, IllegalAccessException
  {
    addAddressToProfile("Home", false);
    addAddressToProfile("Work", false);

    mStoreProfileFormHandler.getAddressUserInputValues().put("addressNickname", "Home");
    mStoreProfileFormHandler.getAddressUserInputValues().put("newAddressNickname", "Work");

    mStoreProfileFormHandler.preUpdateAddress(ServletUtil.getCurrentRequest(), null);

    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("errorDuplicateNickname", e.getErrorCode());
  }

  /**
   * Test error code for invalid card nickname.
   */
  @Test
  public void testPreCreateCreditCard() throws ServletException, IOException
  {
    mStoreProfileFormHandler.getCreditCardUserInputValues().put("cardNickname", "");

    mStoreProfileFormHandler.preCreateCreditCard(ServletUtil.getCurrentRequest(), null);

    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("missingCreditCardProperty", e.getErrorCode());
  }

  /**
   * Test error code for duplicate card nickname.
   */
  @Test
  public void testPreCreateCreditCard2() throws ServletException, IOException, RepositoryException,
    IntrospectionException, PropertyNotFoundException
  {
    addCardToProfile("MyCard", false);
    mStoreProfileFormHandler.getCreditCardUserInputValues().put("cardNickname", "MyCard");

    mStoreProfileFormHandler.preCreateCreditCard(ServletUtil.getCurrentRequest(), null);

    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("errorDuplicateCCNickname", e.getErrorCode());
  }

  /**
   * Test error code for invalid address nickname.
   */
  @Test
  public void testPreAddAddress() throws ServletException, IOException {
    mStoreProfileFormHandler.getAddressUserInputValues().put("addressNickname", "");

    mStoreProfileFormHandler.preAddAddress(ServletUtil.getCurrentRequest(), null);

    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("missingAddressProperty", e.getErrorCode());
  }

  /**
   * Test error code for duplicate address nickname.
   */
  @Test
  public void testPreAddAddress2() throws ServletException, IOException, ClassNotFoundException,
    IntrospectionException, InstantiationException, RepositoryException, IllegalAccessException
  {
    addAddressToProfile("Home", false);
    addAddressToProfile("Work", false);

    mStoreProfileFormHandler.getAddressUserInputValues().put("addressNickname", "Home");
    mStoreProfileFormHandler.getAddressUserInputValues().put("newAddressNickname", "Work");

    mStoreProfileFormHandler.preUpdateAddress(ServletUtil.getCurrentRequest(), null);

    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("errorDuplicateNickname", e.getErrorCode());
  }

  /**
   * Test error code for invalid email address
   */
  @Test
  public void testHandleEmailCheck() throws ServletException, IOException {
    // Invalid
    mStoreProfileFormHandler.getValueMap().put("email", "test");

    mStoreProfileFormHandler.handleEmailCheck(ServletUtil.getCurrentRequest(), null);

    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("invalidEmailAddress", e.getErrorCode());
  }

  /**
   * Tests the handle email check method
   */
  @Test
  public void testHandleEmailCheck2() throws ServletException, IOException {
    mStoreProfileFormHandler.getValueMap().put("email", "test@test.com");

    mStoreProfileFormHandler.handleEmailCheck(ServletUtil.getCurrentRequest(), null);

    assertFalse(mStoreProfileFormHandler.getFormError());
  }

  /**
   * Tests error code for pre remove address
   */
  @Test
  public void testPreRemoveAddress() {
    mStoreProfileFormHandler.getAddressUserInputValues().put("removeAddressNickname", "");

    mStoreProfileFormHandler.preRemoveAddress(ServletUtil.getCurrentRequest(), null);

    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("errorRemovingAddress", e.getErrorCode());
  }

  /**
   * Tests error code for pre remove card
   */
  @Test
  public void testPreRemoveCard() {
    mStoreProfileFormHandler.getAddressUserInputValues().put("removeCardNickname", "");

    mStoreProfileFormHandler.preRemoveCard(ServletUtil.getCurrentRequest(), null);

    assertTrue(mStoreProfileFormHandler.getFormError());
    DropletException e = (DropletException) mStoreProfileFormHandler.getFormExceptions().get(0);
    assertEquals("errorRemovingCard", e.getErrorCode());
  }
}