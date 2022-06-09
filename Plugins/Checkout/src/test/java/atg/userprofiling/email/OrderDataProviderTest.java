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
package atg.userprofiling.email;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.commerce.CommerceException;
import atg.commerce.fulfillment.SubmitOrder;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingTools;
import atg.commerce.pricing.ShippingPricingEngine;
import atg.commerce.states.ObjectStates;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.shipping.ShippingTools;
import atg.projects.store.userprofiling.StoreProfileTools;
import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import org.junit.*;

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
 * Unit tests for atg.projects.store.shipping.ShippingTools.
 *
 * @author Oracle
 */
public class OrderDataProviderTest {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/test/java/atg/userprofiling/email/OrderDataProviderTest.java#1 $$Change: 1385662 $";


  private static final String ORDER_DATA_PROVIDER = "/atg/userprofiling/email/OrderDataProvider";
  public static final String ORDER_REPOSITORY_PATH = "/atg/commerce/order/OrderRepository";
  
  private static Nucleus mNucleus = null;
  private static OrderDataProvider mOrderDataProvider = null;
  private static GSARepository mOrderRepository = null;
  private static GSARepository mProductCatalog = null;
  private static GSARepository mInventoryRepository = null;
  
  /**
   * One time setup.
   */
  @BeforeClass
  public static void beforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[]{"CommerceAccelerator.Base", "CommerceAccelerator.Plugins.Checkout"},
      OrderDataProviderTest.class, "OrderDataProviderTest", "/atg/Initial");
    
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
  public void before() {
	  
    // Set the current request & profile
    ServletTestUtils utils = new ServletTestUtils();
    DynamoHttpServletRequest request = utils.createDynamoHttpServletRequestForSession(
      mNucleus, "mySessionId", "new");
    
    ServletUtil.setCurrentRequest(request);
    
    mOrderDataProvider = (OrderDataProvider) request.resolveName(ORDER_DATA_PROVIDER, true);
    assertNotNull(mOrderDataProvider);
  }
  
  /**
   * Test the setTemplateName and getTemplateName methods.
   */
  @Test
  public void testSetTemplateName() {
    mOrderDataProvider.setTemplateName("orderDetails.ftl");
    assertEquals(mOrderDataProvider.getTemplateName(),"orderDetails.ftl");
  }
  
  /**
   * Test the setTemplateResourcesPath and getTemplateResourcesPath methods.
   */
  @Test
  public void testSetTemplateResourcesPath() {
    mOrderDataProvider.setTemplateResourcesPath("/atg/userprofiling/email/locales/{locale}/OrderConfirmationEmail.xlf");
    assertEquals(mOrderDataProvider.getTemplateResourcesPath(),"/atg/userprofiling/email/locales/{locale}/OrderConfirmationEmail.xlf");
  }
  
  /**
   * Test the setSiteServerPort and getSiteServerPort methods.
   */
  @Test
  public void testSetSiteServerPort() {
    mOrderDataProvider.setSiteServerPort(0);
    assertEquals(mOrderDataProvider.getSiteServerPort(), 0);
  }

  /**
   * Test the setSiteServerName and getSiteServerName methods.
   */
  @Test
  public void testSetSiteServerName() {
    mOrderDataProvider.setSiteServerName("/atg/dynamo/Configuration.siteHttpServerName");
    assertEquals(mOrderDataProvider.getSiteServerName(), "/atg/dynamo/Configuration.siteHttpServerName");
  }
  
  /**
   * Test the setSiteRepository and getSiteRepository methods.
   */
  @Test
  public void testSetStates() {
    ObjectStates states = mOrderDataProvider.getStates();
    assertNotNull(states);
    mOrderDataProvider.setStates(states);
    assertNotNull(mOrderDataProvider.getStates());
  }

  
  /**
   * Test the setOrderManager and getOrderManager methods.
   */
  @Test
  public void testSetOrderManager() {
    OrderManager orderManager = mOrderDataProvider.getOrderManager();
    assertNotNull(orderManager);
    mOrderDataProvider.setOrderManager(orderManager);
    assertNotNull(mOrderDataProvider.getOrderManager());
  }

  /**
   * Test the setSiteRepository and getSiteRepository methods.
   */
  @Test
  public void testSetSiteRepository() {
    Repository repository = mOrderDataProvider.getSiteRepository();
    assertNotNull(repository);
    mOrderDataProvider.setSiteRepository(repository);
    assertNotNull(mOrderDataProvider.getSiteRepository());
  }
  
  /**
   * Test the setCommerceItemImagePropertyName and getCommerceItemImagePropertyName methods.
   */
  @Test
  public void testSetCommerceItemImagePropertyName() {
    mOrderDataProvider.setCommerceItemImagePropertyName("thumbnailImage");
    assertEquals(mOrderDataProvider.getCommerceItemImagePropertyName(), "thumbnailImage");
  }

  /**
   * Test the setSiteLogoUrl and getSiteLogoUrl methods.
   */
  @Test
  public void testSetSiteLogoUrl() {
    mOrderDataProvider.setSiteLogoUrl("thumbnailImage");
    assertEquals(mOrderDataProvider.getSiteLogoUrl(), "thumbnailImage");
  }
  
  /**
   * Test the getStateDetail method.
   */
  @Test
  public void testGetStateDetail1() throws CommerceException {
    OrderManager orderManager = mOrderDataProvider.getOrderManager();
    String state = null;
    state = mOrderDataProvider.getStateDetail(orderManager.loadOrder("o200002"));
    assertNotNull(state);
  }
  
  /**
   * Test the getStateDetail method.
   */
  @Test
  public void testGetStateDetail2() throws CommerceException {
    String state = null;
    state = mOrderDataProvider.getStateDetail(state);
    assertNull(state);
  }

  
  /**
   * Test the getShippingGroupData method.
   */
  @Test
  public void testGetShippingGroupData() throws CommerceException {
    OrderManager orderManager = mOrderDataProvider.getOrderManager();
    Order order = orderManager.loadOrder("o200002");
    ShippingGroup sg = (ShippingGroup)order.getShippingGroups().get(0);
    Map<String,String> shippingGroupMap = mOrderDataProvider.getShippingGroupData(sg);
    assertEquals("Stuart" ,shippingGroupMap.get("firstName"));
  }

  /**
   * Test the getPaymentGroupData method.
   */
  @Test
  public void testGetPaymentGroupData() throws CommerceException {
    OrderManager orderManager = mOrderDataProvider.getOrderManager();
    Order order = orderManager.loadOrder("o200002");
    PaymentGroup pg = (PaymentGroup) order.getPaymentGroups().get(0);
    Map<String,String> paymentGroupMap = mOrderDataProvider.getPaymentGroupData(pg);
    assertEquals("Stuart" ,paymentGroupMap.get("firstName"));
    assertEquals("visa" ,paymentGroupMap.get("cardType"));
  }
    
  /**
   * Test the getHttpServerUrl method.
   */
  @Test
  public void testGetHttpServerUrl() {
    mOrderDataProvider.setSiteServerName("localhost");
    mOrderDataProvider.setSiteServerPort(8006);

    String httpUrl = mOrderDataProvider.getHttpServerUrl();
    assertEquals("http://localhost:8006", httpUrl);
  }
}