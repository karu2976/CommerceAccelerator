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

import atg.endeca.assembler.actor.ActorExecutor;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.projects.store.assembler.cartridge.ActorInvokingContentItemModifier;
import atg.service.actor.Actor;
import atg.servlet.ServletTestUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.RefinementMenu;
import com.endeca.infront.cartridge.RefinementMenuConfig;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * This class test functionality of B2CStoreRefinementMenuHandler
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/handler/B2CStoreRefinementMenuHandlerTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class B2CStoreRefinementMenuHandlerTest {

  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/handler/B2CStoreRefinementMenuHandlerTest.java#1 $$Change: 1385662 $";

  //=======================================================================
  // CONSTANTS                           
  //=======================================================================  
  
  // Component paths.
  public static final String REFINEMENT_MENU_PATH =
    "/atg/endeca/assembler/cartridge/handler/RefinementMenu";

  //=======================================================================
  // MEMBER VARIABLES                           
  //=======================================================================  
  
  private static Nucleus mNucleus = null;
  private static ModifiedB2CStoreRefinementMenuHandler mStoreRefinementMenuHandler = null;
  private static ActorExecutor mActorExecutor = null;
  private static ActorInvokingContentItemModifier mContentItemModifier = null;

  //=======================================================================
  // METHODS                          
  //=======================================================================  

  /**
   * Perform test initialization.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] {"CommerceAccelerator.Plugins.SearchAndNavigation"},
      B2CStoreRefinementMenuHandlerTest.class,
      "B2CStoreRefinementMenuHandlerTest",
      "/atg/Initial");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    //-----------------------------------------
    // Set up B2CStoreRefinementMenuHandler.
    //-----------------------------------------
    mStoreRefinementMenuHandler = (ModifiedB2CStoreRefinementMenuHandler)
      request.resolveName(REFINEMENT_MENU_PATH, true);

    assertNotNull(mStoreRefinementMenuHandler);

    mContentItemModifier = (ActorInvokingContentItemModifier)
      request.resolveName("/atg/projects/store/assembler/cartridge/TestCiModifier", true);

    mContentItemModifier.setActorExecutor(new MockActorExecutor());
  }

  /**
   * Clean up and shut down Nucleus.
   */
  @AfterClass
  public static void tearDownAfterClass() throws IOException, ServiceException {
    if(mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
      mNucleus = null;
    }

    mActorExecutor = null;
    mActorExecutor = null;
    mContentItemModifier = null;
  }

  /**
   * Re-initialize before each test.
   */
  @Before
  public void setUpBeforeMethod() {
    mStoreRefinementMenuHandler.setPricingContentItemModifier(mContentItemModifier);
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  //------------------------------------
  // Test the class setters and getters.
  //------------------------------------

  /**
   * Test the priceRangeDimensionName getter/setter.
   */
  @Test
  public void testSetGetPriceRangeDimensionName() {
    mStoreRefinementMenuHandler.setPriceRangeDimensionName(
      mStoreRefinementMenuHandler.getPriceRangeDimensionName());

    assertEquals("product.price_range", mStoreRefinementMenuHandler.getPriceRangeDimensionName());
  }

  /**
   * Test the pricingContentItemModifier getter/setter.
   */
  @Test
  public void testSetGetPricingContentItemModifier() {
    mStoreRefinementMenuHandler.setPricingContentItemModifier(
      mStoreRefinementMenuHandler.getPricingContentItemModifier());

    assertNotNull(mStoreRefinementMenuHandler.getPricingContentItemModifier());
  }

  //------------------------------------
  // Test the class methods.
  //------------------------------------

  /**
   * Ensure that the correct refinement menu is returned from the process method.
   */
  @Test
  public void testProcessRefinementsWithPriceRangeDimensionName() {
    RefinementMenuConfig contentItem = new RefinementMenuConfig();
    RefinementMenu refinementMenu = new RefinementMenu(contentItem);

    mStoreRefinementMenuHandler.setDimensionName("product.price_range");

    RefinementMenu results =
      mStoreRefinementMenuHandler.processRefinements(contentItem, refinementMenu);

    assertEquals("$", results.get("currencySymbol"));
  }

  /**
   * Ensure that a non-null refinement menu is returned from the process method when
   * the ContentItemModifier is null.
   */
  @Test
  public void testProcessRefinementsWithNullContentItemModifier() {
    mStoreRefinementMenuHandler.setPricingContentItemModifier(null);

    RefinementMenuConfig contentItem = new RefinementMenuConfig();
    RefinementMenu refinementMenu = new RefinementMenu(contentItem);

    mStoreRefinementMenuHandler.setDimensionName("product.price_range");

    RefinementMenu results =
      mStoreRefinementMenuHandler.processRefinements(contentItem, refinementMenu);

    assertNotNull(results);
  }

  /**
   * Ensure that a non-null refinement menu is returned from the process method when the
   * dimension name is not product.price_range.
   */
  @Test
  public void testProcessRefinementsWithNonPriceRangeDimensionName() {
    RefinementMenuConfig contentItem = new RefinementMenuConfig();
    RefinementMenu refinementMenu = new RefinementMenu(contentItem);

    mStoreRefinementMenuHandler.setDimensionName("product.test");

    RefinementMenu results =
      mStoreRefinementMenuHandler.processRefinements(contentItem, refinementMenu);

    assertNotNull(results);
  }

  //=======================================================================
  // UTILITY METHODS
  //=======================================================================

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

  //=======================================================================
  // UTILITY CLASSES
  //=======================================================================

  /**
   * Mock ActorExecutor class to be used in the tests.
   */
  static class MockActorExecutor extends ActorExecutor {

    /**
     * This overridden method just returns some mock data in the content item.
     *
     * @param pActor
     *   The actor to invoke
     * @param pChainId
     *   The chain id on the actor that will be executed.
     * @param pContentItem
     *   The ContentItem to update
     *
     * @return
     */
    public ContentItem invokeActor(Actor pActor, String pChainId, ContentItem pContentItem) {
      pContentItem.put("currencySymbol", "$");
      return pContentItem;
    }
  }

}
