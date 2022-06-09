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

package atg.endeca.sitemap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;

import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.endeca.infront.navigation.NavigationException;
import com.endeca.soleng.sitemap.NameValuePair;


public class NavigationPageSpecTest {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/test/java/atg/endeca/sitemap/NavigationPageSpecTest.java#1 $$Change: 1385662 $";

  

  
  /** Nucleus variable */
  private static Nucleus mNucleus = null;

  public NavigationPageSpec mNavigationPageSpec;
  
  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------  
  
  /**
   * Perform test initialization.
   * 
   * @throws ServletException 
   * @throws NavigationException 
   */
  @BeforeClass
  public static void setUp() throws ServletException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] {"DAS","CommerceAccelerator.Plugins.SEO"},
      MDEXEngineTest.class,
      "/atg/endeca/sitemap/CategoryNavigationpageSpec");
 
  }

  /**
   * Clean up and shut down Nucleus.
   * 
   * @throws IOException 
   * @throws ServiceException 
   */
  @AfterClass
  public static void tearDown() throws ServiceException, IOException {
    if (mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
    }
  }
  
  //---------------------------------------------------------------------------
  // TEST METHODS                          
  //---------------------------------------------------------------------------
      
  /**
   * This method tests that all the getters/setters are working. It also ensures
   * that the SEOCanonicalLinkBuilder methods set the correct data.
   * 
   * @throws IOException 
   */
  @Test
  public void testNameValuePairList() throws IOException {
    
    mNavigationPageSpec = (NavigationPageSpec)mNucleus.resolveName("/atg/endeca/sitemap/CategoryNavigationPageSpec", true);
    
    assertNotNull(mNavigationPageSpec);
    assertEquals(1, mNavigationPageSpec.getDimensionsAsNameValuePair().size());
    NameValuePair dimension = mNavigationPageSpec.getDimensionsAsNameValuePair().get(0);
    assertEquals("product.category", dimension.getName());
    assertEquals("true", dimension.getValue());
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
    HashMap<String, String> params = new HashMap<String,String>();
    DynamoHttpServletRequest request = 
      utils.createDynamoHttpServletRequestForSession(mNucleus, params, 1024, "GET", null);
    ServletUtil.setCurrentRequest(request);
    
    return request;
  }
  
}
