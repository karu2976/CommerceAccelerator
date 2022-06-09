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

package atg.endeca.assembler.actor;

import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
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
 * This unit test will test the methods of the ActorExecutor class.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/endeca/assembler/actor/ActorExecutorTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ActorExecutorTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/endeca/assembler/actor/ActorExecutorTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  /** The ActorExecutor that will be tested. */
  private static ActorExecutor mActorExecutor = null;

  /** The Actor component to use in the tests. */
  private static MockBasicActor mBasicActor = null;

  /** The VariantActor component to use in the tests. */
  private static MockVariantActor mVariantActor = null;

  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the ActorExecutor instance to be used in this test.
   *
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base" },
      ActorExecutorTest.class,
      "ActorExecutorTest",
      "/atg/Initial");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    // Set up ActorExecutor.
    mActorExecutor = (ActorExecutor)
      request.resolveName("/atg/dynamo/service/actor/ActorExecutor", true);

    assertNotNull(mActorExecutor);

    mVariantActor = (MockVariantActor)
      request.resolveName("/atg/endeca/assembler/cartridge/actor/TestVariantActor", true);

    assertNotNull(mVariantActor);

    mBasicActor = (MockBasicActor)
      request.resolveName("/atg/endeca/assembler/cartridge/actor/TestActor", true);

    assertNotNull(mVariantActor);

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

    mActorExecutor = null;
    mBasicActor = null;
    mVariantActor = null;
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  // Test the setters/getters

  /**
   * Test the modelMapFactory setter/getter.
   */
  @Test
  public void testModelMapFactory() {
    mActorExecutor.setModelMapFactory(mActorExecutor.getModelMapFactory());
    assertNotNull(mActorExecutor.getModelMapFactory());
  }

  /**
   * Test the actorContextFactory setter/getter.
   */
  @Test
  public void testActorContextFactory() {
    mActorExecutor.setActorContextFactory(mActorExecutor.getActorContextFactory());
    assertNotNull(mActorExecutor.getActorContextFactory());
  }

  /**
   * Test the contentItemPropertyName setter/getter.
   */
  @Test
  public void testContentItemPropertyName() {
    mActorExecutor.setContentItemPropertyName(mActorExecutor.getContentItemPropertyName());
    assertEquals("currentContentItem", mActorExecutor.getContentItemPropertyName());
  }

  // Test the class methods.

  /**
   * Test the invokeActor method with a null Actor.
   */
  @Test
  public void testInvokeActorWithNullActor() {
    ContentItem contentItem = new BasicContentItem();
    mActorExecutor.invokeActor(null, contentItem);
    assertEquals(0, contentItem.size());
  }

  /**
   * Test the invokeActor method without supplying a chainId.
   */
  @Test
  public void testInvokeActorWithDefaultChainId() {
    ContentItem contentItem = new BasicContentItem();
    mActorExecutor.invokeActor(mVariantActor, contentItem);
    assertEquals("testValue", contentItem.get("testKey"));
  }

  /**
   * Test the invokeActor method, supplying a chainId.
   */
  @Test
  public void testInvokeActorWithSpecificChainId() {
    ContentItem contentItem = new BasicContentItem();
    mActorExecutor.invokeActor(mVariantActor, "testChainId", contentItem);
    assertEquals("testValue", contentItem.get("testKey"));
  }

  /**
   * Test the invokeActor method with an Actor that isn't an instanceof VariantActor.
   */
  @Test
  public void testInvokeActorWithNonVariantActor() {
    ContentItem contentItem = new BasicContentItem();
    mActorExecutor.invokeActor(mBasicActor, "testChainId", contentItem);
    assertEquals("testValue", contentItem.get("testKey"));
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
