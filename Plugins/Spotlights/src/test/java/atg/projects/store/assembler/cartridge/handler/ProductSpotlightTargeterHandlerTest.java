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

import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.service.actor.ModelBeanImpl;
import atg.service.actor.ModelListImpl;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.ContentItem;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This unit test will test the methods of the ProductSpotlightTargeterHandler class.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Spotlights/src/test/java/atg/projects/store/assembler/cartridge/handler/ProductSpotlightTargeterHandlerTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ProductSpotlightTargeterHandlerTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Spotlights/src/test/java/atg/projects/store/assembler/cartridge/handler/ProductSpotlightTargeterHandlerTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  /**
   * The ProductSpotlightTargeter that will be tested.
   */
  public static ProductSpotlightTargeterHandler mProductSpotlightTargeterHandler = null;

  public static BasicContentItem mCartridgeConfig = null;

  /** The Nucleus to be used in this test class */
  private static Nucleus mNucleus = null;

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the ProductSpotlightTargeter instance to be used in this test.
   *
   * @throws Exception When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[]{"CommerceAccelerator.Plugins.Spotlights"},
      ProductSpotlightTargeterHandlerTest.class,
      "ProductSpotlightTargeterHandlerTest",
      "/atg/Initial");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    // Set up ProductSpotlightTargeter component.
    mProductSpotlightTargeterHandler = (ProductSpotlightTargeterHandler)
      request.resolveName("/atg/endeca/assembler/cartridge/handler/ProductSpotlightTargeter", true);

    assertNotNull(mProductSpotlightTargeterHandler);

    mCartridgeConfig = new BasicContentItem();
    mCartridgeConfig.put("componentPath", "/atg/registry/Slots/FeaturedProducts");
  }

  /**
   * Ensure Nucleus is shutdown properly and perform general clean-up of member variables.
   *
   * @throws Exception When there's a problem shutting down Nucleus.
   */
  @AfterClass
  public static void tearDownAfterClass() throws IOException, ServiceException {
    if (mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
      mNucleus = null;
    }
    mProductSpotlightTargeterHandler = null;
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  //------------------------------------
  // Test the class setters and getters
  //------------------------------------

  /**
   * Test the productRecordRepositoryIdName setter/getter.
   */
  @Test
  public void testSetGetProductRecordRepositoryIdName() {
    mProductSpotlightTargeterHandler.setProductRecordRepositoryIdName("product.repositoryId");
    assertEquals("product.repositoryId",
      mProductSpotlightTargeterHandler.getProductRecordRepositoryIdName());
  }

  /**
   * Test the storeCartridgeTools setter/getter.
   */
  @Test
  public void testSetGetStoreCartridgeTools() {
    mProductSpotlightTargeterHandler.setStoreCartridgeTools(
      mProductSpotlightTargeterHandler.getStoreCartridgeTools());
    assertNotNull(mProductSpotlightTargeterHandler.getStoreCartridgeTools());
  }

  //------------------------
  // Test the class methods
  //------------------------

  /**
   * This method tests the ProductSpotlightTargeterHandler's process method
   */
  @Test
  public void testProcess() {
    ContentItem contentItem = mProductSpotlightTargeterHandler.process(mCartridgeConfig);
    ModelListImpl items = (ModelListImpl) contentItem.get("items");

    for (int i = 1; i <= items.size(); i++) {
      ModelBeanImpl item = (ModelBeanImpl) items.get(i - 1);
      assertEquals("product" + i, item.get("repositoryId"));
      assertEquals("product" + i, item.get("id"));
      assertEquals("A-prod" + i, item.get("spec"));
    }
  }

  //---------------------------------------------------------------------------
  // UTILITY METHODS
  //---------------------------------------------------------------------------

  /**
   * Create a request to be used in our tests.
   *
   * @return request
   */
  public static DynamoHttpServletRequest setUpCurrentRequest() {
    ServletTestUtils utils = new ServletTestUtils();
    DynamoHttpServletRequest request =
      utils.createDynamoHttpServletRequestForSession(
        mNucleus, "mySessionId", "new");
    ServletUtil.setCurrentRequest(request);

    return request;
  }

}