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
package atg.projects.store.order;

import atg.commerce.CommerceException;
import atg.commerce.order.DuplicateShippingGroupException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.ShippingGroupManager;
import atg.core.util.Address;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.projects.store.order.StoreOrderTools;
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
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Unit tests for atg.projects.store.order.StoreOrderTools.
 *
 * @author Oracle
 */
public class StoreOrderToolsTest {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/test/java/atg/projects/store/order/StoreOrderToolsTest.java#1 $$Change: 1385662 $";


  private static final String STORE_ORDER_TOOLS_PATH = "/atg/commerce/order/OrderTools";
    
  private static Nucleus mNucleus = null;
  private static StoreOrderTools mStoreOrderTools = null;
  private Order mOrder = null;
  
  /**
   * One time setup.
   */
  @BeforeClass
  public static void beforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[]{"CommerceAccelerator.Base", "CommerceAccelerator.Plugins.Account",
      "CommerceAccelerator.Plugins.Promotions", "CommerceAccelerator.Plugins.Checkout"},
      StoreOrderToolsTest.class, STORE_ORDER_TOOLS_PATH);
  }

  /**
   * One time tear down.
   */
  @AfterClass
  public static void afterClass() throws IOException, ServiceException {
    if (mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
      mStoreOrderTools = null;
      mNucleus = null;
    }
  }

  /**
   * Set up a new order every time
   */
  @Before
  public void before() throws CommerceException {
    // Set the current request & order
    ServletTestUtils utils = new ServletTestUtils();
    DynamoHttpServletRequest request = utils.createDynamoHttpServletRequestForSession(mNucleus, null, null);
    ServletUtil.setCurrentRequest(request);
    mStoreOrderTools = (StoreOrderTools) request.resolveName(STORE_ORDER_TOOLS_PATH, true);
    assertNotNull(mStoreOrderTools);
    mOrder = mStoreOrderTools.createOrder("default");
    assertNotNull(mOrder);
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------
  
  //------------------------------------
  // Test the class setters and getters
  //------------------------------------
  
  /**
   * Test the setShippingGroupManager and getShippingGroupManager methods.
   */
  @Test
  public void testSetShippingGroupManager() {
    ShippingGroupManager shippingGroupManager = mStoreOrderTools.getShippingGroupManager();
    assertNotNull(shippingGroupManager);
    mStoreOrderTools.setShippingGroupManager(shippingGroupManager);
    assertNotNull(mStoreOrderTools.getShippingGroupManager());
  }

  
  //------------------------
  // Test the class methods
  //------------------------
  
  /**
   * Validate order has shipping group.
   */
  @Test
  public void testGetShippingGroup1() throws CommerceException {
    assertNotNull(mStoreOrderTools.getShippingGroup(mOrder));
  }
  
  /**
   * Validate order has payment group.
   */
  @Test
  public void testGetCreditCard1() {
    assertNotNull(mStoreOrderTools.getCreditCard(mOrder));
  } 
  
  /**
   * Validate order has payment group.
   */
  @Test
  public void testGetCreditCard2() {
    Order order = null;
    assertNull(mStoreOrderTools.getCreditCard(order));
  } 
  
  /**
   * Validate order has payment group.
   */
  @Test
  public void testGetCreditCard3() {
    Order order = mOrder;
    order.removeAllPaymentGroups();
    assertNull(mStoreOrderTools.getCreditCard(order));
  } 


  
  /**
   * Validate credit card verification number.
   */
  @Test
  public void testValidateCreditCardAuthorizationNumber() {
    String authNumber = null;
    assertEquals(false, mStoreOrderTools.validateCreditCardAuthorizationNumber(authNumber));
    authNumber = "abc";
    assertEquals(false, mStoreOrderTools.validateCreditCardAuthorizationNumber(authNumber));
    authNumber = "123";
    assertEquals(true, mStoreOrderTools.validateCreditCardAuthorizationNumber(authNumber));
  }
}