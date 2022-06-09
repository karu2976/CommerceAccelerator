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

package atg.projects.store.profile;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.projects.store.userprofiling.StoreProfileTools;
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
 * Unit tests for atg.projects.store.order.purchase.CheckoutLoginFormHandler.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/test/java/atg/projects/store/profile/CheckoutLoginFormHandlerTest.java#1 $$Change: 1385662 $ 
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class CheckoutLoginFormHandlerTest {

  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/test/java/atg/projects/store/profile/CheckoutLoginFormHandlerTest.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // STATIC MEMBERS
  //---------------------------------------------------------------------------
	 
  private static Nucleus mNucleus = null;
  private static CheckoutLoginFormHandler mCheckoutLoginFormHandler = null;
  private static GSARepository mProfileRepository = null;
  private static StoreProfileTools mStoreProfileTools = null;

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  public static final String CHECKOUT_LOGIN_FORM_HANDLER_PATH =
    "/atg/store/profile/CheckoutLoginFormHandler";
  public static final String PROFILE_REPOSITORY_PATH = "/atg/userprofiling/ProfileAdapterRepository";
  public static final String PROFILE_TOOLS_PATH = "/atg/userprofiling/ProfileTools";

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
  public static void beforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[]{"CommerceAccelerator.Base", "CommerceAccelerator.Plugins.Account",
      "CommerceAccelerator.Plugins.Promotions", "CommerceAccelerator.Plugins.Checkout"},
      CheckoutLoginFormHandlerTest.class,"CheckoutLoginFormHandlerTest",
      "/atg/Initial");
    
    // Initialize profile repository and import sample data.
    mProfileRepository = (GSARepository) mNucleus.resolveName(PROFILE_REPOSITORY_PATH, true);
    assertNotNull(mProfileRepository);
    String[] profileDataFileNames = { "users.xml" };
    TemplateParser.loadTemplateFiles(mProfileRepository, 1, profileDataFileNames, true,
      new PrintWriter(System.out), null);
    
    mStoreProfileTools = (StoreProfileTools) mNucleus.resolveName(PROFILE_TOOLS_PATH, true);
    assertNotNull(mStoreProfileTools);

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
      mNucleus = null;
    }
  }

  /**
   * Set up a new order and new profile every time.
   */
  @Before
  public void before() {

	// Set the current request & order.
    ServletTestUtils utils = new ServletTestUtils();
    DynamoHttpServletRequest request = 
      utils.createDynamoHttpServletRequestForSession(
        mNucleus, "mySessionId", "new");

    ServletUtil.setCurrentRequest(request);
    
    mCheckoutLoginFormHandler = 
      (CheckoutLoginFormHandler) request.resolveName(CHECKOUT_LOGIN_FORM_HANDLER_PATH, true);
    assertNotNull(mCheckoutLoginFormHandler);
    
    Profile profile = mCheckoutLoginFormHandler.getProfile();
    mStoreProfileTools.createNewUser("110000", "user", profile);
    ServletUtil.setCurrentUserProfile(profile);
    mCheckoutLoginFormHandler.setProfile(profile);
  }
  
  //---------------------------------------------------------------------------
  // TESTS
  //---------------------------------------------------------------------------
  
  /**
   * Test the setNewEmailAddress and getNewEmailAddress methods.
   */
  @Test
  public void testSetNewEmailAddress() {
    mCheckoutLoginFormHandler.setNewEmailAddress("abc@example.com");
    assertEquals(mCheckoutLoginFormHandler.getNewEmailAddress(),"abc@example.com");
  }

  /**
   * Test the setAddEmailToAnonymousUserSuccessURL and getAddEmailToAnonymousUserSuccessURL methods.
   */
  @Test
  public void testSetAddEmailToAnonymousUserSuccessURL() {
    mCheckoutLoginFormHandler.setAddEmailToAnonymousUserSuccessURL("/checkout");
    assertEquals(mCheckoutLoginFormHandler.getAddEmailToAnonymousUserSuccessURL(), "/checkout");
  }
  
  /**
   * Test the setAddEmailToAnonymousUserErrorURL and getAddEmailToAnonymousUserErrorURL methods.
   */
  @Test
  public void testSetAddEmailToAnonymousUserErrorURL() {
    mCheckoutLoginFormHandler.setAddEmailToAnonymousUserErrorURL("");
    assertEquals(mCheckoutLoginFormHandler.getAddEmailToAnonymousUserErrorURL(), "");
  }
  
  /**
   * If a user already exists with the provided email address.
   */
  @Test
  public void testPreAddEmailToAnonymousUser1() throws ServletException, IOException {
    mCheckoutLoginFormHandler.setNewEmailAddress("abcd@example.com");
    
    mCheckoutLoginFormHandler.preAddEmailToAnonymousUser(ServletUtil.getCurrentRequest(), null);
    assertEquals(0, mCheckoutLoginFormHandler.getFormExceptions().size());
  } 
  
  /**
   * If a user already exists with the provided email address.
   */
  @Test
  public void testPreAddEmailToAnonymousUser2() throws ServletException, IOException {
    mCheckoutLoginFormHandler.setNewEmailAddress("andrew@example.com");

    mCheckoutLoginFormHandler.preAddEmailToAnonymousUser(ServletUtil.getCurrentRequest(), null);
    assertTrue(mCheckoutLoginFormHandler.getFormError());
  }
  
  /**
   * If a user already exists with the provided email address.
   */
  @Test
  public void testHandleAddEmailToAnonymousUser1() throws ServletException, IOException {
    mCheckoutLoginFormHandler.setNewEmailAddress("abcd@example.com");

    mCheckoutLoginFormHandler.handleAddEmailToAnonymousUser(ServletUtil.getCurrentRequest(), null);
    assertEquals(0, mCheckoutLoginFormHandler.getFormExceptions().size());
  } 

  /**
   * If a user already exists with the provided email address.
   */
  @Test
  public void testHandleAddEmailToAnonymousUser2() throws ServletException, IOException {
    mCheckoutLoginFormHandler.setNewEmailAddress("abcd@example.com");

    mCheckoutLoginFormHandler.handleAddEmailToAnonymousUser(ServletUtil.getCurrentRequest(), null);
    assertEquals(0, mCheckoutLoginFormHandler.getFormExceptions().size());
  } 

}