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

package atg.endeca.assembler.event;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.seo.SEOContentProvider;
import atg.repository.seo.SEORepositoryContentProvider;
import com.endeca.infront.assembler.*;
import com.endeca.infront.assembler.event.AssemblerEvent;
import com.endeca.infront.assembler.event.AssemblerEventListener;
import com.endeca.infront.cartridge.RecordDetailsHandler;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * This unit test will test the methods of the SEOAssemblerEventListenerTest class.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/endeca/assembler/event/SEOAssemblerEventListenerTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class SEOAssemblerEventListenerTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/endeca/assembler/event/SEOAssemblerEventListenerTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  /** The SEOAssemblerEventListener that will be tested. */
  private static SEOAssemblerEventListener mSEOAssemblerEventListenerTest = null;

  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;

  /** The SiteContextManager component to be used in this test class. */
  private static SiteContextManager mSiteContextManager = null;

  /** The GSARepository to be used in this test class. */
  private static GSARepository mSiteRepository = null;

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the SEOAssemblerEventListenerTest instance to be used in this test.
   *
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException, RepositoryException, SiteContextException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base" },
      SEOAssemblerEventListenerTest.class,
      "SEOAssemblerEventListenerTest",
      "/atg/Initial");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    // Set up SEOAssemblerEventListenerTest.
    mSEOAssemblerEventListenerTest = (SEOAssemblerEventListener)
      request.resolveName("/atg/endeca/assembler/event/SEOAssemblerEventListener", true);

    mSiteRepository = (GSARepository) mNucleus.resolveName("/atg/multisite/SiteRepository", true);
    String[] siteDataFileNames = { "sites.xml" };
    TemplateParser.loadTemplateFiles(mSiteRepository, 1, siteDataFileNames,
      true, new PrintWriter(System.out), null);

    RepositoryItem storeSiteUS = mSiteRepository.getItem("storeSiteUS");

    mSiteContextManager =
      (SiteContextManager) mNucleus.resolveName("/atg/multisite/SiteContextManager", true);

    SiteContext sc = mSiteContextManager.getSiteContext(storeSiteUS.getRepositoryId());
    mSiteContextManager.pushSiteContext(sc);
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

    mSEOAssemblerEventListenerTest = null;
    mSiteContextManager = null;
    mSiteRepository = null;
  }

  /**
   * Re-initialize.
   */
  @Before
  public void setUpBeforeMethod() {
    mSEOAssemblerEventListenerTest.setContentProvider(new MockContentProvider());
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  //------------------------------------
  // Test the class setters and getters.
  //------------------------------------

  /**
   * Test the setter/getter for contentProvider.
   */
  @Test
  public void testSetGetContentProvider() {
    mSEOAssemblerEventListenerTest.setContentProvider(
      mSEOAssemblerEventListenerTest.getContentProvider());

    assertNotNull(mSEOAssemblerEventListenerTest.getContentProvider());
  }

  //------------------------------------
  // Test class methods.
  //------------------------------------

  /**
   * Test that the assemblerComplete method populates the content item with SEO metadata.
   */
  @Test
  public void testAssemblyComplete() {
    ContentItem contentItem = new BasicContentItem();
    contentItem.put(SEOAssemblerEventListener.CONTENT_PATH_KEY, "/test/path");

    AssemblerEvent assemblerEvent = new AssemblerEvent(
      new MockAssembler(), contentItem, new RecordDetailsHandler(), new Throwable());

    mSEOAssemblerEventListenerTest.assemblyComplete(assemblerEvent);

    assertEquals("testTitle",
      ((Map) contentItem.get(SEORepositoryContentProvider.SEO_META_DATA))
        .get(SEORepositoryContentProvider.TITLE_PROPERTY_NAME));

    assertEquals("testKeyword",
      ((Map) contentItem.get(SEORepositoryContentProvider.SEO_META_DATA))
        .get(SEORepositoryContentProvider.KEYWORDS_PROPERTY_NAME));

    assertEquals("testDescription",
      ((Map) contentItem.get(SEORepositoryContentProvider.SEO_META_DATA))
        .get(SEORepositoryContentProvider.DESCRIPTION_PROPERTY_NAME));
  }

  /**
   * That that when no contentPath exists in the content item, the content item won't be populated.
   */
  @Test
  public void testAssemblyCompleteNullContentPath() {
    ContentItem contentItem = new BasicContentItem();

    AssemblerEvent assemblerEvent = new AssemblerEvent(
      new MockAssembler(), contentItem, new RecordDetailsHandler(), new Throwable());

    mSEOAssemblerEventListenerTest.assemblyComplete(assemblerEvent);

    assertEquals(0, contentItem.size());
  }

  /**
   * That that when the contentPath is an empty string, the content item won't be populated.
   */
  @Test
  public void testAssemblyCompleteEmptyContentPath() {
    ContentItem contentItem = new BasicContentItem();
    contentItem.put(SEOAssemblerEventListener.CONTENT_PATH_KEY, "");

    AssemblerEvent assemblerEvent = new AssemblerEvent(
      new MockAssembler(), contentItem, new RecordDetailsHandler(), new Throwable());

    mSEOAssemblerEventListenerTest.assemblyComplete(assemblerEvent);

    assertEquals(1, contentItem.size());
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

  //---------------------------------------------------------------------------
  // UTILITY CLASSES
  //---------------------------------------------------------------------------

  /**
   * Mock Assembler class to be used in the unit tests.
   */
  static class MockAssembler implements Assembler {
    @Override
    public ContentItem assemble(ContentItem pContentItem) throws AssemblerException {
      return null;
    }

    @Override
    public void addAssemblerEventListener(AssemblerEventListener pListener) {
    }

    @Override
    public void removeAssemblerEventListener(AssemblerEventListener pListener) {
    }
  }

  /**
   * Mock ContentProvider class to be used in the unit tests.
   */
  static class MockContentProvider implements SEOContentProvider {
    @Override
    public void fillSEOTagData(String pPath, String pSiteId, ContentItem pContenItem) {
      HashMap<String, Object> metaData = new HashMap<>();

      metaData.put(SEORepositoryContentProvider.TITLE_PROPERTY_NAME, "testTitle");
      metaData.put(SEORepositoryContentProvider.DESCRIPTION_PROPERTY_NAME, "testDescription");
      metaData.put(SEORepositoryContentProvider.KEYWORDS_PROPERTY_NAME, "testKeyword");

      pContenItem.put(SEORepositoryContentProvider.SEO_META_DATA, metaData);
    }
  }

}
