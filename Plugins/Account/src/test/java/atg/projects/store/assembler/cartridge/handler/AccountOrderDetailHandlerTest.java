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

import atg.nucleus.ServiceException;
import atg.projects.store.userprofiling.StoreProfileTools;
import atg.userprofiling.Profile;
import com.endeca.infront.assembler.CartridgeHandlerException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.ContentItem;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This unit test will test the methods of the AccountOrderDetailsHandler class.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/test/java/atg/projects/store/assembler/cartridge/handler/AccountOrderDetailHandlerTest.java#1 $$Change: 1385662 $ 
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class AccountOrderDetailHandlerTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/test/java/atg/projects/store/assembler/cartridge/handler/AccountOrderDetailHandlerTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------

  private static MockAccountOrderDetailHandler mAccountOrderDetailsHandler = null;
  private static Nucleus mNucleus = null;
  private static GSARepository mOrderRepository = null;
  private static GSARepository mSiteRepository = null;
  private static GSARepository mPricelistsRepository = null;
  private static GSARepository mProductCatalog = null;
  private static StoreProfileTools mStoreProfileTools = null;

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------
  
  private static final String ORDER_DETAILS_HANDLER_PATH = "/atg/endeca/assembler/cartridge/handler/AccountOrderDetail";
  private static final String PROFILE = "/atg/userprofiling/Profile";
  private static final String PROFILE_TOOLS_PATH = "/atg/userprofiling/ProfileTools";
  
  //---------------------------------------------------------------------------
  // JUNIT
  //---------------------------------------------------------------------------
  
  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the OrderHistoryDetailHandler instance to be used in this test.
   * 
   * @throws Exception When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Plugins.Account" },
      AccountOrderDetailHandlerTest.class,
      "AccountOrderDetailHandlerTest",
      "/atg/Initial");

    mStoreProfileTools = (StoreProfileTools) mNucleus.resolveName(PROFILE_TOOLS_PATH, true);
    DynamoHttpServletRequest request = setUpCurrentRequest();

    mAccountOrderDetailsHandler = (MockAccountOrderDetailHandler) request.resolveName(ORDER_DETAILS_HANDLER_PATH, true);
    assertNotNull(mAccountOrderDetailsHandler);

    mPricelistsRepository = (GSARepository) mNucleus.resolveName("/atg/commerce/pricing/priceLists/PriceLists");
    String[] pricelistsDataFileNames = { "pricelists.xml" };
    TemplateParser.loadTemplateFiles(mPricelistsRepository, 1,
      pricelistsDataFileNames, true, new PrintWriter(System.out), null);

    mProductCatalog = (GSARepository) mNucleus.resolveName("/atg/commerce/catalog/ProductCatalog");
    String[] dataFileNames = { "catalog.xml" };
    TemplateParser.loadTemplateFiles(mProductCatalog, 1, dataFileNames,
      true, new PrintWriter(System.out), null);

    mOrderRepository = (GSARepository) mNucleus.resolveName("/atg/commerce/order/OrderRepository", true);
    String[] orderDataFileNames = { "orders.xml" };
    TemplateParser.loadTemplateFiles(mOrderRepository, 1, orderDataFileNames, true, new PrintWriter(System.out), null);
    
    mSiteRepository = (GSARepository) mNucleus.resolveName("/atg/multisite/SiteRepository", true);
    String[] siteDataFileNames = { "sites.xml" };
    TemplateParser.loadTemplateFiles(mSiteRepository, 1, siteDataFileNames,
      true, new PrintWriter(System.out), null);

    mAccountOrderDetailsHandler.getOrderManager().setOrderItemDescriptorName("order");
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

    mAccountOrderDetailsHandler = null;
    mOrderRepository = null;
    mSiteRepository = null;
    mPricelistsRepository = null;
    mProductCatalog = null;
    mStoreProfileTools = null;
  }
  
  /**
   * Create a request to be used in our tests.
   * 
   * @return request
   */
  public static DynamoHttpServletRequest setUpCurrentRequest() {
    ServletTestUtils utils = new ServletTestUtils();
    DynamoHttpServletRequest request = utils.createDynamoHttpServletRequestForSession(mNucleus, "mySessionId", "new");
    ServletUtil.setCurrentRequest(request);
    return request;
  }

  //---------------------------------------------------------------------------
  // TESTS
  //---------------------------------------------------------------------------

  /**
   * Non existent orders.
   */
  @Test
  public void testProcess() throws CartridgeHandlerException {
    ServletUtil.getCurrentRequest().setParameter("orderId", "");
    ContentItem contentItem = mAccountOrderDetailsHandler.process(new BasicContentItem());
    assertNotNull(contentItem.get("canViewOrder"));
    assertFalse((Boolean)contentItem.get("canViewOrder"));

    ServletUtil.getCurrentRequest().setParameter("orderId", "nonexistentId");
    contentItem = mAccountOrderDetailsHandler.process(new BasicContentItem());
    assertNotNull(contentItem.get("canViewOrder"));
    assertFalse((Boolean)contentItem.get("canViewOrder"));
  }

  /**
   * Order exists but user doesn't have permission to view
   */
  @Test
  public void testProcess2() throws CartridgeHandlerException {
    Profile profile = (Profile) ServletUtil.getCurrentRequest().resolveName(PROFILE);
    mStoreProfileTools.createNewUser("myNewUser", "user", profile);
    ServletUtil.setCurrentUserProfile(profile);
    ServletUtil.getCurrentRequest().setParameter("orderId", "xo10002");

    ContentItem contentItem = mAccountOrderDetailsHandler.process(new BasicContentItem());
    assertNotNull(contentItem.get("canViewOrder"));
    assertFalse((Boolean)contentItem.get("canViewOrder"));
  }

  /**
   * Test the process method.
   */
  @Test
  public void testProcess3() throws CartridgeHandlerException {
    Profile profile = (Profile) ServletUtil.getCurrentRequest().resolveName(PROFILE);
    mStoreProfileTools.createNewUser("se-570030", "user", profile);
    ServletUtil.setCurrentUserProfile(profile);
    ServletUtil.getCurrentRequest().setParameter("orderId", "xo10002");

    // Setup the mdex request
    mAccountOrderDetailsHandler.preprocess(new BasicContentItem());

    ContentItem pContentItem = new BasicContentItem();
    ContentItem contentItem = mAccountOrderDetailsHandler.process(pContentItem);
    assertNotNull(contentItem.get("canViewOrder"));
    assertTrue((Boolean) contentItem.get("canViewOrder"));
    assertNotNull(contentItem.get("myOrder"));
    assertNotNull(contentItem.get("productURLs"));
    assertNotNull(contentItem.get("orderState"));
    assertNotNull(contentItem.get("shippingAddressCountryDisplayName"));
    assertNotNull(contentItem.get("billingAddressCountryDisplayName"));
    assertNotNull(contentItem.get("site"));
  }
}
