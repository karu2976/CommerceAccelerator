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

package atg.droplet;

import static org.junit.Assert.*;

import javax.servlet.ServletException;

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.commerce.catalog.CatalogTools;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryException;
import atg.service.collections.validator.CollectionObjectValidator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import atg.droplet.DropletInvoker;
import atg.droplet.DropletInvoker.DropletResult;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import atg.servlet.ServletTestUtils.TestingDynamoHttpServletRequest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * This unit test will test the methods of the ItemValidatorDroplet class.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/droplet/ItemValidatorDropletTest.java#1 $$Change: 1385662 $ 
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ItemValidatorDropletTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/droplet/ItemValidatorDropletTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------

  /** The ItemValidatorDroplet that will be tested. */
  private static ItemValidatorDroplet mItemValidatorDroplet = null;

  /** The CatalogTools component. */
  private static CatalogTools mCatalogTools = null;

  /** The product catalog repository. */
  private static GSARepository mProductCatalog = null;

  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------

  // Component paths.
  public static final String ITEM_VALIDATOR_DROPLET_PATH = "/atg/droplet/ItemValidatorDroplet";
  public static final String CATALOG_TOOLS_PATH = "/atg/commerce/catalog/CatalogTools";

  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------

  /**
   * Start Nucleus with the required modules and components to test
   * the ItemValidatorDroplet.
   *
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base" },
      ItemValidatorDropletTest.class,
      "ItemValidatorDropletTest",
      "/atg/Initial");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    // Set up CategoryNavigationHandler.
    mItemValidatorDroplet = (ItemValidatorDroplet)
      request.resolveName(ITEM_VALIDATOR_DROPLET_PATH, true);

    assertNotNull(mItemValidatorDroplet);

    // Initialize repository and import sample data
    mProductCatalog = (GSARepository) mNucleus.resolveName("/atg/commerce/catalog/ProductCatalog", true);
    String[] dataFileNames = { "catalog.xml" };
    TemplateParser.loadTemplateFiles(mProductCatalog, 1, dataFileNames, true, new PrintWriter(System.out), null);

    mCatalogTools = (CatalogTools)
      request.resolveName(CATALOG_TOOLS_PATH, true);

    assertNotNull(mCatalogTools);
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

    mItemValidatorDroplet = null;
    mProductCatalog = null;
    mCatalogTools = null;
  }

  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------

  //------------------------------------
  // Test the class setters and getters
  //------------------------------------

  /**
   * Test the setValidators and getValidators methods.
   */
  @Test
  public void testSetGetValidators() {
    mItemValidatorDroplet.setValidators(null);
    assertNull(mItemValidatorDroplet.getValidators());

    mItemValidatorDroplet.setValidators(new CollectionObjectValidator[1]);
    assertNotNull(mItemValidatorDroplet.getValidators());
  }

  //------------------------
  // Test the class methods
  //------------------------

  /**
   * Ensure that the ItemValidatorDroplet returns a 'true' output parameter when an item
   * has no start/end date values defined (product1 in the catalog.xml).
   *
   * @throws Exception
   */
  @Test
  public void testServiceProductNoDatesSet() throws RepositoryException, ServletException, IOException {

    // Set-up the droplet invoker and dummy request/response objects.
    DropletInvoker invoker = new DropletInvoker(mNucleus);
    ServletTestUtils utils = new ServletTestUtils();

    TestingDynamoHttpServletRequest request =
      utils.createDynamoHttpServletRequestForSession(mNucleus, null, 1024, "GET", "foobarSessionid");

    request.setContextPath("/csa/storeus");

    RepositoryItem product1 = mCatalogTools.findProduct("testProduct1", "masterCatalog");

    HashMap<String, Object> params = new HashMap<>(1);
    params.put("item", product1);

    DropletResult result =
      invoker.invokeDroplet(ITEM_VALIDATOR_DROPLET_PATH, params, request,
        utils.createDynamoHttpServletResponse());

    // This indicates the product is VALID.
    assertNotNull(result.getRenderedOutputParameter("true"));
  }

  /**
   * Ensure that the ItemValidatorDroplet returns a 'false' output parameter when an item
   * has an invalid end date values defined (product2 in the catalog.xml).
   *
   * @throws Exception
   */
  @Test
  public void testServiceProductInValidEndDate() throws ServletException, IOException, RepositoryException {

    // Set-up the droplet invoker and dummy request/response objects.
    DropletInvoker invoker = new DropletInvoker(mNucleus);
    ServletTestUtils utils = new ServletTestUtils();

    TestingDynamoHttpServletRequest request =
      utils.createDynamoHttpServletRequestForSession(mNucleus, null, 1024, "GET", "foobarSessionid");

    request.setContextPath("/csa/storeus");

    RepositoryItem product2 = mCatalogTools.findProduct("testProduct2", "masterCatalog");

    HashMap<String, Object> params = new HashMap<>(1);
    params.put("item", product2);

    DropletResult result =
      invoker.invokeDroplet(ITEM_VALIDATOR_DROPLET_PATH, params, request,
        utils.createDynamoHttpServletResponse());

    // This indicates the product is INVALID.
    assertNotNull(result.getRenderedOutputParameter("false"));
  }

  /**
   * Ensure that the ItemValidatorDroplet returns a 'false' output parameter when an item
   * has an invalid start date values defined (product3 in the catalog.xml).
   *
   * @throws Exception
   */
  @Test
  public void testServiceProductInValidStartDate() throws ServletException, IOException, RepositoryException {

    // Set-up the droplet invoker and dummy request/response objects.
    DropletInvoker invoker = new DropletInvoker(mNucleus);
    ServletTestUtils utils = new ServletTestUtils();

    TestingDynamoHttpServletRequest request =
      utils.createDynamoHttpServletRequestForSession(mNucleus, null, 1024, "GET", "foobarSessionid");

    request.setContextPath("/csa/storeus");

    RepositoryItem product3 = mCatalogTools.findProduct("testProduct3", "masterCatalog");

    HashMap<String, Object> params = new HashMap<>(1);
    params.put("item", product3);

    DropletResult result =
      invoker.invokeDroplet(ITEM_VALIDATOR_DROPLET_PATH, params, request,
        utils.createDynamoHttpServletResponse());

    // This indicates the product is INVALID.
    assertNotNull(result.getRenderedOutputParameter("false"));
  }

  /**
   * Ensure that "true" is serviced when the validators property is null.
   */
  @Test
  public void testNullValidator() throws RepositoryException, ServletException, IOException {

    // Set-up the droplet invoker and dummy request/response objects.
    DropletInvoker invoker = new DropletInvoker(mNucleus);
    ServletTestUtils utils = new ServletTestUtils();

    TestingDynamoHttpServletRequest request =
      utils.createDynamoHttpServletRequestForSession(mNucleus, null, 1024, "GET", "foobarSessionid");

    request.setContextPath("/csa/storeus");

    RepositoryItem product3 = mCatalogTools.findProduct("testProduct3", "masterCatalog");

    HashMap<String, Object> params = new HashMap<>(1);

    DropletResult result =
      invoker.invokeDroplet("/atg/droplet/ItemValidatorDroplet2", params, request,
        utils.createDynamoHttpServletResponse());

    // This indicates the product is VALID.
    assertNotNull(result.getRenderedOutputParameter("true"));
  }

  /**
   * Ensure that "true" is serviced when the validators property is an empty array.
   */
  @Test
  public void testEmptyValidator() throws ServletException, IOException {

    // Set-up the droplet invoker and dummy request/response objects.
    DropletInvoker invoker = new DropletInvoker(mNucleus);
    ServletTestUtils utils = new ServletTestUtils();

    TestingDynamoHttpServletRequest request =
      utils.createDynamoHttpServletRequestForSession(mNucleus, null, 1024, "GET", "foobarSessionid");

    HashMap<String, Object> params = new HashMap<>(1);

    DropletResult result =
      invoker.invokeDroplet("/atg/droplet/ItemValidatorDroplet3", params, request,
        utils.createDynamoHttpServletResponse());

    // This indicates the product is VALID.
    assertNotNull(result.getRenderedOutputParameter("true"));
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
