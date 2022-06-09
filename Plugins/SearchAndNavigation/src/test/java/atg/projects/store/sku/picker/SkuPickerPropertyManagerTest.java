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

package atg.projects.store.sku.picker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import atg.nucleus.ServiceException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;

import javax.servlet.ServletException;
import java.io.IOException;

public class SkuPickerPropertyManagerTest {

  //----------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/sku/picker/SkuPickerPropertyManagerTest.java#1 $$Change: 1385662 $";
  
  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------
  
  /** The SkuTypeProperties that will be tested */
  private static SkuPickerPropertyManager mSkuPickerPropertyManager = null;
  
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
   * the SkuPickerPropertyManager instance to be used in this test.
   * 
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base",
                     "CommerceAccelerator.Plugins.SearchAndNavigation"},
      SkuPickerPropertyManagerTest.class,
      "SkuPickerPropertyManagerTest",
      "/atg/Initial");
    
    // Set up SkuPickerPropertyManager for the tests.
    mSkuPickerPropertyManager = (SkuPickerPropertyManager) 
      mNucleus.resolveName("/atg/commerce/productDetail/SkuPickerPropertyManager", true);  
    
    assertNotNull(mSkuPickerPropertyManager);
    
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
    
    mSkuPickerPropertyManager = null;
  }
  
  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------
  
  //------------------------------------
  // Test the class setters and getters
  //------------------------------------
  
  @Test
  public void testGetProductIdPropertyName() {
    
    assertEquals("productIdPropertyName", mSkuPickerPropertyManager.getProductIdPropertyName());
  }
  
  @Test
  public void testSetProductIdPropertyName() {
    
    mSkuPickerPropertyManager.setProductIdPropertyName(TEST);
    assertEquals(TEST, mSkuPickerPropertyManager.getProductIdPropertyName());
    
    // Set the property back to its default value
    mSkuPickerPropertyManager.setProductIdPropertyName("productIdPropertyName");
  }
  
  @Test
  public void testGetOptionsPropertyName() {
    
    assertEquals("options", mSkuPickerPropertyManager.getOptionsPropertyName());
  }
  
  @Test
  public void testSetOptionsPropertyName() {
    
    mSkuPickerPropertyManager.setOptionsPropertyName(TEST);
    assertEquals(TEST, mSkuPickerPropertyManager.getOptionsPropertyName());
    
    // Set the property back to its default value
    mSkuPickerPropertyManager.setOptionsPropertyName("options");
  }
  
  @Test
  public void testGetSkuPickersPropertyName() {
    
    assertEquals("skuPickers", mSkuPickerPropertyManager.getSkuPickersPropertyName());
  }
  
  @Test
  public void testSetSkuPickersPropertyName() {
    
    mSkuPickerPropertyManager.setSkuPickersPropertyName(TEST);
    assertEquals(TEST, mSkuPickerPropertyManager.getSkuPickersPropertyName());
    
    // Set the property back to its default value
    mSkuPickerPropertyManager.setSkuPickersPropertyName("skuPickers");
  }
  
  @Test
  public void testGetPickerTypePropertyName() {
    
    assertEquals("type", mSkuPickerPropertyManager.getPickerTypePropertyName());
  }
  
  @Test
  public void testSetPickerTypePropertyName() {
    
    mSkuPickerPropertyManager.setPickerTypePropertyName(TEST);
    assertEquals(TEST, mSkuPickerPropertyManager.getPickerTypePropertyName());
    
    // Set the property back to its default value
    mSkuPickerPropertyManager.setPickerTypePropertyName("type");
  }
  
  @Test
  public void testGetSelectedValuePropertyName() {
    
    assertEquals("value", mSkuPickerPropertyManager.getSelectedValuePropertyName());
  }
  
  @Test
  public void testSetSelectedValuePropertyName() {
    
    mSkuPickerPropertyManager.setSelectedValuePropertyName(TEST);
    assertEquals(TEST, mSkuPickerPropertyManager.getSelectedValuePropertyName());
  
    // Set the property back to its default value
    mSkuPickerPropertyManager.setSelectedValuePropertyName("value");
  }

  @Test
  public void testGetHelperInformation() {

    assertEquals("helperInformation", mSkuPickerPropertyManager.getHelperInformation());
  }

  @Test
  public void testSetHelperInformation() {

    mSkuPickerPropertyManager.setHelperInformation(TEST);
    assertEquals(TEST, mSkuPickerPropertyManager.getHelperInformation());

    // Set the property back to its default value
    mSkuPickerPropertyManager.setHelperInformation("helperInformation");
  }
}
