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
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.model.LinkBuilder;
import com.endeca.infront.cartridge.model.UrlAction;
import com.endeca.infront.navigation.url.ActionPathProvider;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This unit test will test the methods of the StoreCartridgeTools class.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/assembler/cartridge/handler/StaticPageNavigationTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StaticPageNavigationTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/assembler/cartridge/handler/StaticPageNavigationTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  /** The StaticPageNavigation that will be tested. */
  private static StaticPageNavigation mStaticPageNavigation = null;

  /** The content item that will be used in the methods of the class being tested. */
  private static ContentItem mContentItem = new BasicContentItem();

  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the StaticPageNavigation instance to be used in this test.
   *
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base" },
      StaticPageNavigationTest.class,
      "StaticPageNavigationTest",
      "/atg/Initial");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    // Set up StaticPageNavigation.
    mStaticPageNavigation = (StaticPageNavigation)
      request.resolveName("/atg/endeca/assembler/cartridge/handler/StaticPageNavigation", true);

    assertNotNull(mStaticPageNavigation);
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

    mStaticPageNavigation = null;
  }

  /**
   * Perform set-up before each test method is invoked.
   */
  @Before
  public void beforeMethod() {
    if (mContentItem == null) {
      mContentItem = new BasicContentItem();
    }

    mContentItem.put("testKey", "testValue");
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  /**
   * Test the wrapConfig method.
   */
  @Test
  public void testWrapConfig() {
    assertEquals("testValue", mStaticPageNavigation.wrapConfig(mContentItem).get("testKey"));
  }

  /**
   * Test the process method to ensure that the content item gets updated.
   */
  @Test
  public void testProcess() throws CartridgeHandlerException {
    ActionPathProvider provider = new TestActionPathProvider();
    mStaticPageNavigation.setActionPathProvider(provider);

    List<ContentItem> links = new ArrayList<>(2);

    LinkBuilder linkBuilder1 = new LinkBuilder();
    linkBuilder1.setPath("/about-us");
    linkBuilder1.setLinkType(LinkBuilder.LinkType.ABSOLUTE);
    ContentItem contentItem1 = new BasicContentItem();
    contentItem1.put(StaticPageNavigation.LINK_PROPERTY_NAME, linkBuilder1);

    LinkBuilder linkBuilder2 = new LinkBuilder();
    linkBuilder2.setPath("/contact-us");
    linkBuilder2.setLinkType(LinkBuilder.LinkType.ABSOLUTE);
    ContentItem contentItem2 = new BasicContentItem();
    contentItem2.put(StaticPageNavigation.LINK_PROPERTY_NAME, linkBuilder2);

    links.add(contentItem1);
    links.add(contentItem2);

    ContentItem contentItem = new BasicContentItem();
    contentItem.put(StaticPageNavigation.LINKS_PROPERTY_NAME, links);

    ContentItem result = mStaticPageNavigation.process(contentItem);
    assertNotNull(result);

    assertEquals("/about-us", result.get("currentStaticPage"));
    assertEquals(2, links.size());
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

  //----------------------------------------------------------------------------
  // UTILITY CLASSES
  //----------------------------------------------------------------------------

  /**
   * ActionPathProvider implementation that always returns a value for the
   * getDefaultNavigationActionContentPath to be used by the testProcess method.
   */
  class TestActionPathProvider implements ActionPathProvider {

    @Override
    public String getDefaultRecordActionContentPath() {
      return "/about-us";
    }

    @Override
    public String getDefaultNavigationActionContentPath() {
      return null;
    }

    @Override
    public String getDefaultNavigationActionSiteRootPath() {
      return null;
    }

    @Override
    public String getDefaultRecordActionSiteRootPath() {
      return null;
    }
  }

}
