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
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import atg.nucleus.ServiceException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceMap;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;

import javax.servlet.ServletException;

public class SkuPickersTest {

  //----------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/sku/picker/SkuPickersTest.java#1 $$Change: 1385662 $";
  
  //----------------------------------------------------------------------------
  // STATIC MEMBERS
  //----------------------------------------------------------------------------
  
  /** The SkuPickers that will be tested */
  private static SkuPickers mSkuPickers = null;
  
  /** The Nucleus to be used in this test class. */
  private static Nucleus mNucleus = null;
  
  //----------------------------------------------------------------------------
  // SET-UP
  //----------------------------------------------------------------------------
  
  /**
   * Start Nucleus with the required modules and retrieve a reference to
   * the SkuTypeProperties instance to be used in this test.
   * 
   * @throws Exception
   *   When there's a problem starting Nucleus or resolving components.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] { "CommerceAccelerator.Base",
                     "CommerceAccelerator.Plugins.SearchAndNavigation"},
      SkuPickersTest.class,
      "SkuPickersTest",
      "/atg/Initial");
    
  }
  
  /**
   * Re-initialize the SkuPickers component
   */
  @Before
  public void setUpBeforeMethod() {

    DynamoHttpServletRequest request = setUpCurrentRequest();
    
    // Set up SkuPickers for the tests.
    mSkuPickers = (SkuPickers)
      request.resolveName("/atg/commerce/product/detail/SkuPickers", true);
    
    assertNotNull(mSkuPickers);
    
    // Populate the product picker map with mock ProductDetailPicker objects
    ServiceMap productPickerMap = new ServiceMap();
    productPickerMap.put("color", new MockProductDetailsPicker("color"));
    productPickerMap.put("size", new MockProductDetailsPicker("size"));
    productPickerMap.put("woodFinish", new MockProductDetailsPicker("woodFinish"));
    
    mSkuPickers.setSkuPickerMap(productPickerMap);
    
    // Populate the sku type picker map with mock ProductDetailPicker objects
    ServiceMap skuTypePickerMap = new ServiceMap();
    skuTypePickerMap.put("clothing-sku", new MockProductDetailsPicker("color"));
    skuTypePickerMap.put("furniture-sku", new MockProductDetailsPicker("size"));
    
    mSkuPickers.setSkuTypeToSkuPickerMap(skuTypePickerMap);
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
    
    mSkuPickers = null;
  }
  
  /**
   * Ensure the instance of SkuPickers has been cleared
   */
  @After
  public void tearDownAfterMethod() {
    
    mSkuPickers = null;
  }
  
  //----------------------------------------------------------------------------
  // TESTS
  //----------------------------------------------------------------------------
  
  //------------------------------------
  // Test the class setters and getters
  //------------------------------------
  
  @Test
  public void testGetSkuPickerMap() {
    
    assertNotNull(mSkuPickers.getSkuPickerMap());
  }
  
  @Test
  public void testSetSkuPickerMap() {
    
    mSkuPickers.setSkuPickerMap(null);
    
    assertNull(mSkuPickers.getSkuPickerMap());
  }
  
  @Test
  public void testGetSkuTypeToSkuPickerMap() {
    
    assertNotNull(mSkuPickers.getSkuTypeToSkuPickerMap());
  }
  
  @Test
  public void testSetSkuTypeToSkuPickerMap() {
    
    mSkuPickers.setSkuTypeToSkuPickerMap(null);
    
    assertNull(mSkuPickers.getSkuTypeToSkuPickerMap());
  }
  
  @Test
  public void testGetCatalogProperties() {
    
    assertNotNull(mSkuPickers.getCatalogProperties());
  }
  
  @Test
  public void testSetCatalogProperties() {
    
    mSkuPickers.setCatalogProperties(null);
    
    assertNull(mSkuPickers.getCatalogProperties());
  }
  
  //------------------------
  // Test the class methods
  //------------------------
  
  @Test
  public void testGetSelectedSkuForType() throws RepositoryException {
    
    Map<String, String> options = new HashMap<>();
    
    // First test passing an empty pOptions parameter map to the getSelectedSkuForType method
    assertEquals(null, mSkuPickers.getSelectedSkuForType(options));
    
    // Now set up the options map with an invalid SKU type.
    options.put(mSkuPickers.getCatalogProperties().getSkuTypePropertyName(), "invalid-sku");
    
    assertEquals(null, mSkuPickers.getSelectedSkuForType(options));
    
    // Now set up the options map with a SKU type.
    options.clear();
    options.put(mSkuPickers.getCatalogProperties().getSkuTypePropertyName(), "clothing-sku");
    
    assertEquals(null, mSkuPickers.getSelectedSkuForType(options));

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
  // MOCK CLASSES                          
  //--------------------------------------------------------------------------- 

 /**
  * Create a mock implementation of ProductDetailsPicker so it can be added to the 
  * ProductDetaislPickers object for complete testing.
  * 
  * @author Oracle
  *
  */
  private class MockProductDetailsPicker implements SkuPicker {
    
    private String mName = null;
    
    public MockProductDetailsPicker(String pName) {
      mName = pName;
    }

    @Override
    public String getSelectedOption(Map<String, String> pOptions)
        throws RepositoryException {

      return "selectedOption";
    }

    @Override
    public Collection<String> getAllOptions(Map<String, String> pOptions)
        throws RepositoryException {
      
      List<String> result = new ArrayList<>();
      result.add(mName + "Option1");
      result.add(mName + "Option2");
      result.add(mName + "Option3");
      
      return result;
    }

    @Override
    public RepositoryItem findSkuByOption(Collection<RepositoryItem> pSkus, String pOption) {
      
      return null;
    }

    @Override
    public RepositoryItem getSelectedSku(Map<String, String> pOptions)
        throws RepositoryException {
      
      return null;
    }

    @Override
    public Map<String, String> getHelperInformation(String pPropertyName, Map<String, String> options)
      throws RepositoryException {

      return new HashMap<>();
    }
  }
}
