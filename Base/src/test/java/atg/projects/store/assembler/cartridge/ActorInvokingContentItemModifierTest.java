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

package atg.projects.store.assembler.cartridge;

import static org.junit.Assert.*;

import atg.endeca.assembler.actor.ActorExecutor;
import atg.nucleus.ResolvingMap;
import atg.nucleus.ServiceException;
import atg.service.actor.Actor;
import atg.service.actor.ActorContext;
import atg.service.actor.ActorException;
import atg.service.actor.ModelMap;
import atg.servlet.ServletUtil;
import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.ContentItem;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;

import javax.servlet.ServletException;
import java.io.IOException;


/**
 * This unit test will test the methods of the ActorInvokingContentItemModifier class.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/assembler/cartridge/ActorInvokingContentItemModifierTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ActorInvokingContentItemModifierTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/assembler/cartridge/ActorInvokingContentItemModifierTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  /** The ActorInvokingContentItemModifier that will be tested. */
  private static ActorInvokingContentItemModifier mActorInvokingContentItemModifier = null;

  private static ActorExecutor mActorExecutor = null;

  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the ActorInvokingContentItemModifier instance to be used in this test.
   *
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base" },
      ActorInvokingContentItemModifierTest.class,
      "ActorInvokingContentItemModifierTest",
      "/atg/Initial");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    // Set up CategoryNavigationHandler.
    mActorInvokingContentItemModifier = (ActorInvokingContentItemModifier)
      request.resolveName("/atg/projects/store/assembler/cartridge/TestActorInvokingContentItemModifier", true);

    assertNotNull(mActorInvokingContentItemModifier);

    mActorExecutor = (ActorExecutor)
      request.resolveName("/atg/dynamo/service/actor/ActorExecutor", true);
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

    mActorInvokingContentItemModifier = null;
    mActorExecutor = null;
  }

  /**
   * Re-initialize the cartridge configuration object before each test.
   */
  @Before
  public void setUpBeforeMethod() {
    mActorInvokingContentItemModifier.setActorExecutor(mActorExecutor);
    mActorInvokingContentItemModifier.setActorChain("/path/to/ActorChain");
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  //------------------------------------
  // Test the class setters and getters
  //------------------------------------

  /**
   * Test the actorExecutor setter/getter.
   */
  @Test
  public void testSetGetActorExecutor() {
    mActorInvokingContentItemModifier.setActorExecutor(
      mActorInvokingContentItemModifier.getActorExecutor());

    assertNotNull(mActorInvokingContentItemModifier.getActorExecutor());
  }

  /**
   * Test the actorChain setter/getter.
   */
  @Test
  public void testSetGetActorChain() {
    mActorInvokingContentItemModifier.setActorChain(
      mActorInvokingContentItemModifier.getActorChain());

    assertEquals("/path/to/ActorChain", mActorInvokingContentItemModifier.getActorChain());
  }

  /**
   * Test the actorToInvoke setter/getter.
   */
  @Test
  public void testSetGetActorToInvoke() {
    mActorInvokingContentItemModifier.setActorToInvoke(new Actor() {
      @Override
      public void act(ActorContext pActorContext, ModelMap pModelMap) throws ActorException {
        // Empty implementation for mock test purposes.
      }
    });

    assertNotNull(mActorInvokingContentItemModifier.getActorToInvoke());
  }

  /**
   * Test the contentItemAdditionsMap setter/getter.
   */
  @Test
  public void testSetGetContentItemAdditionsMap() {
    mActorInvokingContentItemModifier.setContentItemAdditionsMap(new ResolvingMap());

    assertTrue(
      mActorInvokingContentItemModifier.getContentItemAdditionsMap() instanceof ResolvingMap);
  }

  //------------------------
  // Test the class methods
  //------------------------

  /**
   * Test modify method with non-null actor chain.
   */
  @Test
  public void testModifyNonNullActorChain() {
    ContentItem contentItem = new BasicContentItem();

    mActorInvokingContentItemModifier.modify(contentItem,
      mActorInvokingContentItemModifier.getActorExecutor(),
      mActorInvokingContentItemModifier.getActorChain(),
      new Actor() {
        @Override
        public void act(ActorContext pActorContext, ModelMap pModelMap) throws ActorException {
          // Empty implementation for mock test purposes.
        }
      }, new ResolvingMap());

    assertEquals("testValue", contentItem.get("testKey"));
  }

  /**
   * Test modify method with null actor chain.
   */
  @Test
  public void testModifyNullActorChain() {
    mActorInvokingContentItemModifier.setActorChain(null);
    ContentItem contentItem = new BasicContentItem();

    mActorInvokingContentItemModifier.modify(contentItem,
      mActorInvokingContentItemModifier.getActorExecutor(),
      mActorInvokingContentItemModifier.getActorChain(),
      new Actor() {
        @Override
        public void act(ActorContext pActorContext, ModelMap pModelMap) throws ActorException {
          // Empty implementation for mock test purposes.
        }
      }, new ResolvingMap());

    assertEquals("testValue", contentItem.get("testKey"));
  }

  /**
   * Test modify method with null actor chain.
   */
  @Test
  public void testModifyNullActorExecutor() {
    ContentItem contentItem = new BasicContentItem();

    mActorInvokingContentItemModifier.modify(contentItem, null,
      mActorInvokingContentItemModifier.getActorChain(),
      new Actor() {
        @Override
        public void act(ActorContext pActorContext, ModelMap pModelMap) throws ActorException {
          // Empty implementation for mock test purposes.
        }
      }, new ResolvingMap());

    assertEquals(0, contentItem.size());
  }

  /**
   * Test modify method with null contentItemAdditionsMap.
   */
  @Test
  public void testModifyWithNullContentItemAdditionsMap() {
    ContentItem contentItem = new BasicContentItem();

    mActorInvokingContentItemModifier.modify(contentItem,
      mActorInvokingContentItemModifier.getActorExecutor(),
      mActorInvokingContentItemModifier.getActorChain(),
      new Actor() {
        @Override
        public void act(ActorContext pActorContext, ModelMap pModelMap) throws ActorException {
          // Empty implementation for mock test purposes.
        }
      }, null);

    assertEquals("testValue", contentItem.get("testKey"));
  }

  /**
   * Test modify method with populated contentItemAdditionsMap.
   */
  @Test
  public void testModifyWithContentItemAdditionsMap() {
    ResolvingMap map = new ResolvingMap();
    map.put("repository", "/atg/content/ContentManagementRepository");

    ContentItem contentItem = new BasicContentItem();

    mActorInvokingContentItemModifier.modify(contentItem,
      mActorInvokingContentItemModifier.getActorExecutor(),
      mActorInvokingContentItemModifier.getActorChain(),
      new Actor() {
        @Override
        public void act(ActorContext pActorContext, ModelMap pModelMap) throws ActorException {
          // Empty implementation for mock test purposes.
        }
      }, map);

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
