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
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.projects.store.userprofiling.StoreProfileTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.filter.bean.BeanFilterException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import org.junit.*;

import javax.servlet.ServletException;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for atg.projects.store.userprofiling.StoreProfileTools.
 *
 * @author Oracle
 */
public class StoreProfileToolsTest {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/test/java/atg/projects/store/userprofiling/StoreProfileToolsTest.java#1 $$Change: 1385662 $";


  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  private static final String PROFILE_TOOLS_PATH = "/atg/userprofiling/ProfileTools";
  private static final String PROFILE = "/atg/userprofiling/Profile";

  private static Nucleus mNucleus = null;
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
        StoreProfileToolsTest.class, PROFILE_TOOLS_PATH);

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
   * Add an address
   */
  @Test
  public void testAddAddress() throws ClassNotFoundException, IntrospectionException, InstantiationException,
    RepositoryException, IllegalAccessException
  {
    Profile profile = getProfile();
    Map<String, Object> address = getAddressMap("Home", false);

    mStoreProfileTools.addAddress(profile, address);

    Map secondaryAddressMap = (Map) profile.getPropertyValue("secondaryAddresses");
    List<String> nicknames = new ArrayList(secondaryAddressMap.keySet());
    assertTrue(nicknames.contains("Home"));
  }

  /**
   * Validate email address format test.
   */
  @Test
  public void testValidateEmailFormat() {
    // Missing
    assertFalse(mStoreProfileTools.validateEmailFormat(null, null));

    // Valid
    assertTrue(mStoreProfileTools.validateEmailFormat("test@test.com", null));

    // Invalid
    assertFalse(mStoreProfileTools.validateEmailFormat("notAnEmail", null));
  }

  /**
   * Validate address with a new nickname.
   */
  @Test
  public void testValidateAddress1() throws ClassNotFoundException, IntrospectionException,
    InstantiationException, RepositoryException, IllegalAccessException
  {
    Profile profile = getProfile();

    // Missing required properties
    Map<String, Object> address = new HashMap<>();
    boolean valid = mStoreProfileTools.validateAddress("Home", "Home", address, new HashMap());
    assertFalse(valid);

    // Required properties
    address = getAddressMap("Home", false);
    valid = mStoreProfileTools.validateAddress("Home", "Home", address, new HashMap());
    assertTrue(valid);

    // Address already exists
    address.put("addressNickname", "Home");
    mStoreProfileTools.addAddress(profile, address);

    address.put("addressNickname", "Work");
    mStoreProfileTools.addAddress(profile, address);

    valid = mStoreProfileTools.validateAddress("Work", "Home", address, new HashMap());
    assertFalse(valid);
  }

  /**
   * Validate address.
   */
  @Test
  public void testValidateAddress2() throws ClassNotFoundException, IntrospectionException, InstantiationException,
    RepositoryException, IllegalAccessException
  {
    Profile profile = getProfile();

    // Missing required properties
    Map<String, Object> address = new HashMap<>();
    boolean valid = mStoreProfileTools.validateAddress("Home", address, new HashMap());
    assertFalse(valid);

    // Required properties
    address = getAddressMap("Home", false);
    valid = mStoreProfileTools.validateAddress("Home", address, new HashMap());
    assertTrue(valid);

    // Address already exists
    address.put("addressNickname", "Home");
    mStoreProfileTools.addAddress(profile, address);

    valid = mStoreProfileTools.validateAddress("Home", address, new HashMap());
    assertFalse(valid);
  }

  /**
   * Validate address without nickname.
   */
  @Test
  public void testValidateAddress3() {
    // Missing required properties
    Map<String, Object> address = new HashMap<>();
    boolean valid = mStoreProfileTools.validateAddress(address, new HashMap());
    assertFalse(valid);

    // Required properties
    address = getAddressMap("Home", false);
    valid = mStoreProfileTools.validateAddress(address, new HashMap());
    assertTrue(valid);
  }

  /**
   * Validate address with incorrect state.
   */
  @Test
  public void testValidateAddress4() {
    // Wrong state
    Map<String, Object> address = getAddressMap("Home", false);
    address.put("state", "TEST");
    boolean valid = mStoreProfileTools.validateAddress(address, new HashMap());
    assertFalse(valid);
  }

  /**
   * Validation method used for update operations.
   */
  @Test
  public void testValidateCreditCard1() throws RepositoryException, IntrospectionException,
    PropertyNotFoundException
  {
    Profile profile = getProfile();
    Map<String, Object> address = getAddressMap("Home", false);
    Map<String, Object> card = getCardMap("Visa", false);
    card.put("billingAddress", address);
    mStoreProfileTools.createProfileCreditCard(profile, card, "Work", address);

    // Remove a required property to make validation fail
    card.put("expirationYear", "");

    boolean valid = mStoreProfileTools.validateCreditCard(profile, "Work", "Work", card, new HashMap());
    assertFalse(valid);

    // Add the exp year back.
    card.put("expirationYear", "2050");

    valid = mStoreProfileTools.validateCreditCard(profile, "Work", "Work", card, new HashMap());
    assertTrue(valid);
  }

  /**
   * Validate new card
   */
  @Test
  public void testValidateCreditCard2() {
    Map<String, Object> address = getAddressMap("Home", false);
    Map<String, Object> card = getCardMap("Work", false);

    // Missing billingAddress
    boolean valid = mStoreProfileTools.validateCreditCard("Work", card, new HashMap());
    assertFalse(valid);

    // Add billingAddress
    card.put("billingAddress", address);
    valid = mStoreProfileTools.validateCreditCard("Work", card, new HashMap());
    assertTrue(valid);
  }

  /**
   * Validate credit card.
   */
  @Test
  public void testValidateCreditCard3() {
    Map<String, Object> address = getAddressMap("Home", false);

    Map<String, Object> card = getCardMap("Visa", false);
    card.put("expirationYear", "2010");
    card.put("billingAddress", address);

    // Invalid date
    boolean valid = mStoreProfileTools.validateCreditCard(card, new HashMap());
    assertFalse(valid);

    // Valid date
    card.put("expirationYear", "2050");
    valid = mStoreProfileTools.validateCreditCard(card, new HashMap());
    assertTrue(valid);
  }

  /**
   * Test credit card nickname validation.
   */
  @Test
  public void testIsValidCreditCardNickname() throws IllegalAccessException, RepositoryException,
    IntrospectionException, InstantiationException, PropertyNotFoundException, ClassNotFoundException
  {
    // Test empty
    assertFalse(mStoreProfileTools.isValidCreditCardNickname(""));

    Profile profile = getProfile();
    Map<String, Object> address = getAddressMap("Home", false);
    Map<String, Object> card = getCardMap("Visa", false);
    card.put("billingAddress", address);

    // Add a card
    String nickname = mStoreProfileTools.createProfileCreditCardAndAddress(profile, card);

    // Test already added cart
    assertFalse(mStoreProfileTools.isValidCreditCardNickname(nickname));

    // Test new nickname
    assertTrue(mStoreProfileTools.isValidCreditCardNickname("dfskfkdsj"));
  }

  /**
   * Test address nickname validation.
   */
  @Test
  public void testIsValidAddressNickname() throws ClassNotFoundException, IntrospectionException,
    InstantiationException, RepositoryException, IllegalAccessException
  {
    // Test empty
    assertFalse(mStoreProfileTools.isValidAddressNickname(""));

    Profile profile = getProfile();
    Map<String, Object> address = getAddressMap("Home", false);
    mStoreProfileTools.addAddress(profile, address);

    // Test already added cart
    assertFalse(mStoreProfileTools.isValidAddressNickname("Home"));

    // Test new nickname
    assertTrue(mStoreProfileTools.isValidAddressNickname("kjkjfdkjg"));
  }

  /**
   * Test valid state and country combination
   */
  @Test
  public void testIsValidCountryStateCombination() {
    // Invalid
    assertFalse(mStoreProfileTools.isValidCountryStateCombination("US", "BLAH"));

    // Valid
    assertTrue(mStoreProfileTools.isValidCountryStateCombination("US", "CA"));

    // Unknown
    assertTrue(mStoreProfileTools.isValidCountryStateCombination("unknown", ""));
  }

  /**
   * Test valid state and country combination
   */
  @Test
  public void testCreateProfileCreditCardAndAddress() throws IllegalAccessException, RepositoryException,
    IntrospectionException, InstantiationException, PropertyNotFoundException, ClassNotFoundException
  {
    Profile profile = getProfile();
    Map<String, Object> address = getAddressMap("Home", false);
    Map<String, Object> card = getCardMap("Visa", false);
    card.put("billingAddress", address);

    // Add a card
    assertEquals("Visa", mStoreProfileTools.createProfileCreditCardAndAddress(profile, card));
  }

  /**
   * Update an address
   */
  @Test
  public void testUpdateAddress() throws ClassNotFoundException, IntrospectionException,
    InstantiationException, RepositoryException, IllegalAccessException
  {
    Profile profile = getProfile();
    Map<String, Object> address = getAddressMap("Home", false);

    // Add an address so we can update it
    mStoreProfileTools.addAddress(profile, address);

    // Make sure address is there
    Map secondaryAddressMap = (Map) ServletUtil.getCurrentUserProfile().getPropertyValue("secondaryAddresses");
    List<String> nicknames = new ArrayList(secondaryAddressMap.keySet());
    assertTrue(nicknames.contains("Home"));

    RepositoryItem addressItem = (RepositoryItem) secondaryAddressMap.get("Home");
    String address1 = (String) addressItem.getPropertyValue("address1");
    assertEquals("Test", address1);

    // Update the address1 field
    address.put("newAddressNickname", "Home");
    address.put("address1", "home");
    mStoreProfileTools.updateAddress(profile, address, null);

    // Check for the updated field
    addressItem = (RepositoryItem) secondaryAddressMap.get("Home");
    address1 = (String) addressItem.getPropertyValue("address1");
    assertEquals("home", address1);
  }

  /**
   * Remove an address
   */
  @Test
  public void testRemoveAddress() throws ClassNotFoundException, IntrospectionException, InstantiationException,
    RepositoryException, IllegalAccessException
  {
    Profile profile = getProfile();
    Map<String, Object> address = getAddressMap("Home", false);

    // Add an address so we can update it
    mStoreProfileTools.addAddress(profile, address);

    // Make sure address is there
    Map secondaryAddressMap = (Map) profile.getPropertyValue("secondaryAddresses");
    List<String> nicknames = new ArrayList(secondaryAddressMap.keySet());
    assertTrue(nicknames.contains("Home"));

    // Now remove it and make sure it isn't there
    mStoreProfileTools.removeAddress("Home", profile, null);
    secondaryAddressMap = (Map) profile.getPropertyValue("secondaryAddresses");
    nicknames = new ArrayList(secondaryAddressMap.keySet());
    assertFalse(nicknames.contains("Home"));
  }

  /**
   * Update a credit card
   */
  @Test
  public void testUpdateCreditCard() throws IllegalAccessException, RepositoryException, IntrospectionException,
    InstantiationException, PropertyNotFoundException, ClassNotFoundException
  {
    Profile profile = getProfile();

    Map<String, Object> address = getAddressMap("Home", false);

    Map<String, Object> card = getCardMap("Visa", false);
    card.put("billingAddress", address);

    // Add a card so we can update it
    String nickname = mStoreProfileTools.createProfileCreditCardAndAddress(profile, card);
    RepositoryItem cardNickname = mStoreProfileTools.getCreditCardByNickname(nickname, profile);
    assertNotNull(cardNickname);

    String year = (String) cardNickname.getPropertyValue("expirationYear");
    assertEquals("2050", year);

    // Update it
    card.put("expirationYear", "2051");
    card.put("newCardNickname", "Visa");
    mStoreProfileTools.updateCreditCard(profile, card, address);

    cardNickname = mStoreProfileTools.getCreditCardByNickname("Visa", profile);
    year = (String) cardNickname.getPropertyValue("expirationYear");
    assertEquals("2051", year);
  }

  /**
   * Remove a card
   */
  @Test
  public void testRemoveCard() throws IllegalAccessException, RepositoryException, IntrospectionException,
    InstantiationException, PropertyNotFoundException, ClassNotFoundException
  {
    Profile profile = getProfile();
    Map<String, Object> address = getAddressMap("Home", false);
    Map<String, Object> card = getCardMap("Visa", false);
    card.put("billingAddress", address);

    // Make sure card is there
    String nickname = mStoreProfileTools.createProfileCreditCardAndAddress(profile, card);
    RepositoryItem cardNickname = mStoreProfileTools.getCreditCardByNickname(nickname, profile);
    assertNotNull(cardNickname);

    // Now remove it
    mStoreProfileTools.removeCard(nickname, profile);
    cardNickname = mStoreProfileTools.getCreditCardByNickname(nickname, profile);
    assertNull(cardNickname);
  }

  /**
   * Test changing address nickname.
   */
  @Test
  public void testChangeAddressNickname() throws ClassNotFoundException, IntrospectionException,
    InstantiationException, RepositoryException, IllegalAccessException
  {
    Profile profile = getProfile();
    addAddressToProfile("Home", false);

    // Make sure address is there
    Map secondaryAddressMap = (Map) profile.getPropertyValue("secondaryAddresses");
    List<String> nicknames = new ArrayList(secondaryAddressMap.keySet());
    assertTrue(nicknames.contains("Home"));

    mStoreProfileTools.changeSecondaryAddressName(profile, "Home", "Home2");

    // Make sure address is there
    secondaryAddressMap = (Map) profile.getPropertyValue("secondaryAddresses");
    nicknames = new ArrayList(secondaryAddressMap.keySet());
    assertFalse(nicknames.contains("Home"));
    assertTrue(nicknames.contains("Home2"));
  }

  /**
   * Test getting a filtered map of credit card properties.
   */
  @Test
  public void testGetFilteredCardMap() throws IllegalAccessException, RepositoryException,
    IntrospectionException, InstantiationException, PropertyNotFoundException, ClassNotFoundException,
    BeanFilterException
  {
    Profile profile = getProfile();
    Map<String, Object> address = getAddressMap("Home", false);
    Map<String, Object> card = getCardMap("Visa", false);
    card.put("billingAddress", address);

    // Make sure card is there
    String nickname = mStoreProfileTools.createProfileCreditCardAndAddress(profile, card);
    RepositoryItem cardItem = mStoreProfileTools.getCreditCardByNickname(nickname, profile);
    assertNotNull(cardItem);

    Map filteredCardMap = mStoreProfileTools.getFilteredCardMap(profile, nickname, "summary", true, true, true);
    assertTrue(filteredCardMap.containsKey("cardNickname"));
    assertEquals(nickname, filteredCardMap.get("cardNickname"));

    assertTrue(filteredCardMap.containsKey("billingAddress"));
    Map billingAddress = (Map) filteredCardMap.get("billingAddress");
    assertTrue(billingAddress.containsKey("states"));

    assertTrue(filteredCardMap.containsKey("setAsDefaultCard"));
    assertTrue(filteredCardMap.containsKey("creditCardNumber"));
    assertEquals("1111", filteredCardMap.get("creditCardNumber"));
  }

  /**
   * Test getting a filtered map of credit card properties.
   */
  @Test
  public void testGetFilteredAddressMap() throws ClassNotFoundException, IntrospectionException,
    InstantiationException, RepositoryException, IllegalAccessException, BeanFilterException
  {
    Profile profile = getProfile();
    addAddressToProfile("Home", false);

    // Make sure address is there
    Map secondaryAddressMap = (Map) profile.getPropertyValue("secondaryAddresses");
    List<String> nicknames = new ArrayList(secondaryAddressMap.keySet());
    assertTrue(nicknames.contains("Home"));

    Map filteredAddressMap = mStoreProfileTools.getFilteredAddressMap(profile, "Home", "summary", true, true, true);
    assertTrue(filteredAddressMap.containsKey("addressNickname"));
    assertEquals("Home", filteredAddressMap.get("addressNickname"));
    assertTrue(filteredAddressMap.containsKey("states"));
    assertTrue(filteredAddressMap.containsKey("useAsDefaultShippingAddress"));
    assertTrue(filteredAddressMap.containsKey("address1"));
    assertEquals("Test", filteredAddressMap.get("address1"));
  }

  /**
   * Test date validation
   */
  @Test
  public void testValidateBirthDate() {
    // Date before a predefined date
    assertFalse(mStoreProfileTools.validateBirthDate("01/01/1800", new HashMap()));

    // Invalid date
    assertFalse(mStoreProfileTools.validateBirthDate("invalid", new HashMap()));

    // Future date
    assertFalse(mStoreProfileTools.validateBirthDate("01/01/3000", new HashMap()));
  }
}