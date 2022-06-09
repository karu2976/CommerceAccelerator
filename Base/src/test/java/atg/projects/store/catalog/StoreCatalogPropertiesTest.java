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

package atg.projects.store.catalog;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import atg.nucleus.ServiceException;
import org.junit.After;
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

public class StoreCatalogPropertiesTest {

  //----------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/projects/store/catalog/StoreCatalogPropertiesTest.java#1 $$Change: 1385662 $";
  
  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------
  
  /** The StoreInventoryManager that will be tested */
  private static StoreCatalogProperties mStoreCatalogProperties = null;
  
  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------
  
  private static final String TEST = "test";
  
  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------
  
  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the StoreInventoryManager instance to be used in this test.
   * 
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base"},
      StoreCatalogPropertiesTest.class,
      "StoreCatalogPropertiesTest",
      "/atg/Initial");

    // Set up StoreCatalogProperties for the tests.
    mStoreCatalogProperties = (StoreCatalogProperties)
      mNucleus.resolveName("/atg/commerce/catalog/custom/CatalogProperties", true);

    assertNotNull(mStoreCatalogProperties);
    
  }
  
  //----------------------------------------------------------------------------
  // TEAR DOWN
  //----------------------------------------------------------------------------
  
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
    
    mStoreCatalogProperties = null;
  }
  
  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------
  
  //------------------------------------
  // Test the class setters and getters
  //------------------------------------
  
  @Test
  public void testDefaultProductPropertyName() {
    assertEquals("product", mStoreCatalogProperties.getProductPropertyName());
  }
  
  @Test
  public void testSetProductPropertyName() {
    mStoreCatalogProperties.setProductPropertyName(TEST);
    assertEquals(mStoreCatalogProperties.getProductPropertyName(), TEST);

    // Reset property back to default value
    mStoreCatalogProperties.setProductPropertyName("product");
  }
  
  @Test
  public void testDefaultSkuPropertyName() {
    assertEquals("sku", mStoreCatalogProperties.getSkuPropertyName());
  }
  
  @Test
  public void testSetSkuPropertyName() {
    mStoreCatalogProperties.setSkuPropertyName(TEST);
    assertEquals(mStoreCatalogProperties.getSkuPropertyName(), TEST);

    // Reset property back to default value
    mStoreCatalogProperties.setSkuPropertyName("sku");
  }
  
  @Test
  public void testDefaultSkuTypePropertyName() {
    assertEquals("type", mStoreCatalogProperties.getSkuTypePropertyName());
  }
  
  @Test
  public void testSetSkuTypePropertyName() {
    mStoreCatalogProperties.setSkuTypePropertyName(TEST);
    assertEquals(mStoreCatalogProperties.getSkuTypePropertyName(), TEST);

    // Reset property back to default value
    mStoreCatalogProperties.setSkuTypePropertyName("type");
  }
  
  @Test
  public void testDefaultInStockPropertyName() {
    assertEquals("inStock", mStoreCatalogProperties.getInStockPropertyName());
  }
  
  @Test
  public void testSetInStockPropertyName() {
    mStoreCatalogProperties.setInStockPropertyName(TEST);
    assertEquals(mStoreCatalogProperties.getInStockPropertyName(), TEST);

    // Reset property back to default value
    mStoreCatalogProperties.setInStockPropertyName("inStock");
  }
  
  @Test
  public void testDefaultIsAvailablePropertyName() {
    assertEquals("isAvailable", mStoreCatalogProperties.getIsAvailablePropertyName());
  }
  
  @Test
  public void testSetIsAvailablePropertyName() {
    mStoreCatalogProperties.setIsAvailablePropertyName(TEST);
    assertEquals(mStoreCatalogProperties.getIsAvailablePropertyName(), TEST);

    // Reset property back to default value
    mStoreCatalogProperties.setIsAvailablePropertyName("isAvailable");
  }

  @Test
  public void tesGetChildSkuTypePropertyName() {
    assertEquals("childSkuType", mStoreCatalogProperties.getChildSkuTypePropertyName());
  }

  @Test
  public void testSetChildSkuTypePropertyName() {
    mStoreCatalogProperties.setChildSkuTypePropertyName(TEST);
    assertEquals(mStoreCatalogProperties.getChildSkuTypePropertyName(), TEST);

    // Reset property back to default value
    mStoreCatalogProperties.setChildSkuTypePropertyName("childSkuType");
  }

  @Test
  public void tesGetChildSkuPickerPropertiesPropertyName() {
    assertEquals("childSkuPickerProperties",
      mStoreCatalogProperties.getChildSkuPickerPropertiesPropertyName());
  }

  @Test
  public void testSetChildSkuPickerPropertiesPropertyName() {
    mStoreCatalogProperties.setChildSkuPickerPropertiesPropertyName(TEST);
    assertEquals(mStoreCatalogProperties.getChildSkuPickerPropertiesPropertyName(), TEST);

    // Reset property back to default value
    mStoreCatalogProperties.setChildSkuPickerPropertiesPropertyName("childSkuPickerProperties");
  }
}
