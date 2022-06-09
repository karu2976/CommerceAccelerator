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

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.nucleus.Nucleus;
import atg.nucleus.ServiceException;
import atg.servlet.DynamoHttpServletRequest;
import atg.nucleus.NucleusTestUtils;
import atg.servlet.ServletUtil;
import atg.servlet.ServletTestUtils;

import com.endeca.infront.cartridge.ResultsList;
import com.endeca.infront.cartridge.ResultsListConfig;
import com.endeca.infront.cartridge.model.Attribute;
import com.endeca.infront.cartridge.model.Record;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * This unit test will test the methods of the PreviewResultsListContentItemModifier class.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/PreviewResultsListContentItemModifierTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class PreviewResultsListContentItemModifierTest {

//----------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/PreviewResultsListContentItemModifierTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  /** The PreviewResultsListContentItemModifier that will be tested. */
  private static PreviewResultsListContentItemModifier mPreviewResultsListContentItemModifier = null;

  /** The product catalog to use in the tests. */
  private static GSARepository mProductCatalog = null;

  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the PreviewResultsListContentItemModifier instance to be used in this test.
   *
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Plugins.SearchAndNavigation" },
      PreviewResultsListContentItemModifierTest.class,
      "PreviewResultsListContentItemModifierTest",
      "/atg/Initial");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    // Set up PreviewResultsListContentItemModifier.
    mPreviewResultsListContentItemModifier = (PreviewResultsListContentItemModifier)
      request.resolveName("/atg/endeca/assembler/cartridge/PreviewResultsListContentItemModifier", true);

    assertNotNull(mPreviewResultsListContentItemModifier);

    // Initialize repository and import sample data
    mProductCatalog = (GSARepository) mNucleus.resolveName("/atg/commerce/catalog/ProductCatalog", true);
    String[] dataFileNames = { "catalog.xml" };
    TemplateParser.loadTemplateFiles(mProductCatalog, 1, dataFileNames, true, new PrintWriter(System.out), null);
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

    mPreviewResultsListContentItemModifier = null;
    mProductCatalog = null;
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  //------------------------------------
  // Test the class setters and getters
  //------------------------------------

  /**
   * Test the setRepository and setRepository methods.
   */
  @Test
  public void testSetGetRepository() {
    mPreviewResultsListContentItemModifier.setRepository(
      mPreviewResultsListContentItemModifier.getRepository());

    assertNotNull(mPreviewResultsListContentItemModifier.getRepository());
  }

  /**
   * Test the setItemDescriptor and setItemDescriptor methods.
   */
  @Test
  public void testSetGetItemDescriptor() {
    mPreviewResultsListContentItemModifier.setItemDescriptorName(
      mPreviewResultsListContentItemModifier.getItemDescriptorName());

    assertEquals("product", mPreviewResultsListContentItemModifier.getItemDescriptorName());
  }

  /**
   * Test that no products get removed from the content item when they all exist in the repository.
   */
  @Test
  public void testModifyNoMissingProduct() {
    ResultsListConfig resultsListConfig = new ResultsListConfig();
    resultsListConfig.setRecordsPerPage(12);

    ResultsList contentItem = new ResultsList(resultsListConfig);

    Record record = new Record();

    Attribute attribute1 = new Attribute();
    attribute1.add(0, "testProduct1");

    record.getAttributes().put(
      PreviewResultsListContentItemModifier.PRODUCT_REPOSITORY_ID, attribute1);

    Record record2 = new Record();

    Attribute attribute2 = new Attribute();
    attribute2.add(0, "testProduct2");

    record2.getAttributes().put(
      PreviewResultsListContentItemModifier.PRODUCT_REPOSITORY_ID, attribute2);

    contentItem.setRecords(new ArrayList<Record>(2));
    contentItem.getRecords().add(record);
    contentItem.getRecords().add(record2);

    mPreviewResultsListContentItemModifier.modify(contentItem);
    assertEquals(2, contentItem.getRecords().size());
  }

  /**
   * Test that 1 product get removed from the content item when it doesn't exist in the repository.
   */
  @Test
  public void testModifyMissingProduct() {
    ResultsListConfig resultsListConfig = new ResultsListConfig();
    resultsListConfig.setRecordsPerPage(12);

    ResultsList contentItem = new ResultsList(resultsListConfig);

    Record record = new Record();

    Attribute attribute1 = new Attribute();
    attribute1.add(0, "testProduct1");

    record.getAttributes().put(
      PreviewResultsListContentItemModifier.PRODUCT_REPOSITORY_ID, attribute1);

    Record record2 = new Record();

    Attribute attribute2 = new Attribute();
    attribute2.add(0, "nonExistentProduct");

    record2.getAttributes().put(
      PreviewResultsListContentItemModifier.PRODUCT_REPOSITORY_ID, attribute2);

    contentItem.setRecords(new ArrayList<Record>(2));
    contentItem.getRecords().add(record);
    contentItem.getRecords().add(record2);

    mPreviewResultsListContentItemModifier.setItemDescriptorName("product");

    mPreviewResultsListContentItemModifier.modify(contentItem);
    assertEquals(1, contentItem.getRecords().size());
  }

  /**
   * Test that when a repository exception is thrown, a product isn't removed from the content item.
   */
  @Test
  public void testModifyRepositoryExceptionWithDebug() {
    ResultsListConfig resultsListConfig = new ResultsListConfig();
    resultsListConfig.setRecordsPerPage(12);

    ResultsList contentItem = new ResultsList(resultsListConfig);

    Record record = new Record();

    Attribute attribute1 = new Attribute();
    attribute1.add(0, "testProduct1");

    record.getAttributes().put(
      PreviewResultsListContentItemModifier.PRODUCT_REPOSITORY_ID, attribute1);

    contentItem.setRecords(new ArrayList<Record>(2));
    contentItem.getRecords().add(record);

    mPreviewResultsListContentItemModifier.setItemDescriptorName("incorrectProductDescriptor");
    mPreviewResultsListContentItemModifier.modify(contentItem);

    assertEquals(1, contentItem.getRecords().size());
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
