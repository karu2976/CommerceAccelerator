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

package atg.endeca.assembler.cartridge.handler;

import atg.endeca.assembler.actor.ActorExecutor;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ResolvingMap;
import atg.nucleus.ServiceException;
import atg.service.actor.*;
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
 * This unit test will test the methods of the ActorInvokingCartridgeHandler class.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/endeca/assembler/cartridge/handler/ActorInvokingCartridgeHandlerTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ActorInvokingCartridgeHandlerTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/endeca/assembler/cartridge/handler/ActorInvokingCartridgeHandlerTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  /** The ActorInvokingCartridgeHandler that will be tested. */
  private static ActorInvokingCartridgeHandler mActorInvokingCartridgeHandler = null;

  /** The content item that will be used in the methods of the class being tested. */
  private static ContentItem mContentItem = new BasicContentItem();

  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the ActorInvokingCartridgeHandler instance to be used in this test.
   *
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base" },
      ActorInvokingCartridgeHandlerTest.class,
      "ActorInvokingCartridgeHandlerTest",
      "/atg/Initial");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    // Set up ActorInvokingCartridgeHandler.
    mActorInvokingCartridgeHandler = (ActorInvokingCartridgeHandler)
      request.resolveName("/atg/endeca/assembler/cartridge/handler/TestActorInvokingCartridgeHandler", true);

    assertNotNull(mActorInvokingCartridgeHandler);
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

    mActorInvokingCartridgeHandler = null;
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  // Test the setters/getters

  /**
   * Test the actorExecuter setter/getter.
   */
  @Test
  public void testActorExecutorSetterGetter() {
    mActorInvokingCartridgeHandler.setActorExecutor(new ActorExecutor());
    assertNotNull(mActorInvokingCartridgeHandler.getActorExecutor());
  }

  /**
   * Test the actorChain setter/getter.
   */
  @Test
  public void testActorChainSetterGetter() {
    mActorInvokingCartridgeHandler.setActorChain("/chain/to/execute");
    assertEquals("/chain/to/execute", mActorInvokingCartridgeHandler.getActorChain());
  }

  /**
   * Test the actorToInvoke setter/getter.
   */
  @Test
  public void testActorToInvokeSetterGetter() {
    mActorInvokingCartridgeHandler.setActorToInvoke(new Actor() {
      @Override
      public void act(ActorContext pActorContext, ModelMap pModelMap) throws ActorException {
        // Empty implementation.
      }
    });

    assertNotNull(mActorInvokingCartridgeHandler.getActorToInvoke());
  }

  /**
   * Test the contentItemAdditionsMap setter/getter.
   */
  @Test
  public void testContentItemAdditionsMapSetterGetter() {
    mActorInvokingCartridgeHandler.setContentItemAdditionsMap(new ResolvingMap());
    assertNotNull(mActorInvokingCartridgeHandler.getContentItemAdditionsMap());
  }

  // Test the methods.

  /**
   * Test the wrapConfig method.
   */
  @Test
  public void testWrapConfig() {
    assertNotNull(mActorInvokingCartridgeHandler.wrapConfig(new BasicContentItem()));
  }

  /**
   * Test the process method when the handler doesn't have a ContentItemModifier set up.
   */
  @Test
  public void testprocessWithoutContentItemModifier() {
    mActorInvokingCartridgeHandler = null;
    setupHandler(false);

    assertEquals(0,
      mActorInvokingCartridgeHandler.process(new BasicContentItem()).size());
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

  /**
   * This method sets up the handler to be used in the tests with or without a ContentItemModifier.
   *
   * @param pWithContentItemModifier
   *   Whether or not to create the handler with a ContentItemModifier.
   */
  public static void setupHandler(boolean pWithContentItemModifier) {
    DynamoHttpServletRequest request = setUpCurrentRequest();

    String handlerComponentPath = null;

    if (pWithContentItemModifier) {
      handlerComponentPath =
        "/atg/endeca/assembler/cartridge/handler/TestActorInvokingCartridgeHandler";
    }
    else {
      handlerComponentPath =
        "/atg/endeca/assembler/cartridge/handler/TestActorInvokingCartridgeHandler2";
    }

    // Set up ActorInvokingCartridgeHandler.
    mActorInvokingCartridgeHandler = (ActorInvokingCartridgeHandler)
      request.resolveName(handlerComponentPath, true);
  }

}
