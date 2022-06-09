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

package atg.projects.store.assembler.util;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;

import atg.commerce.catalog.CatalogTools;
import atg.multisite.SiteContextException;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.projects.store.servlet.StoreServletUtilService;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.endeca.infront.navigation.NavigationException;


public class NucleusObjectLocatorTest {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/test/java/atg/projects/store/assembler/util/NucleusObjectLocatorTest.java#1 $$Change: 1385662 $";


  /** The NucleusObjectLocator component to be tested. */
  public static NucleusObjectLocator mNucleusObjectLocator;
  
  /** Nucleus variable */
  private static Nucleus mNucleus = null;
  
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
  public static void setUp() throws ServletException, RepositoryException, SiteContextException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] {"CommerceAccelerator.Base", "CommerceAccelerator.Plugins.SEO"},
              NucleusObjectLocator.class,
              "/atg/endeca/assembler/util/NucleusObjectLocator");

		// Resolve request/session scoped components.
		// Create the current request to be used in this test class.
		DynamoHttpServletRequest request = setUpCurrentRequest();

		// Set up NucleusObjectLocator.
    mNucleusObjectLocator = (NucleusObjectLocator)
				request.resolveName("/atg/endeca/assembler/util/NucleusObjectLocator", true);

		assertNotNull(mNucleusObjectLocator);
  }

  /**
   * Clean up and shut down Nucleus.
   * 
   * @throws IOException 
   * @throws ServiceException 
   */
  @AfterClass
  public static void tearDown() throws ServiceException, IOException {
    mNucleusObjectLocator = null;
    if (mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
    }
  }
  
  //---------------------------------------------------------------------------
  // TEST METHODS                          
  //---------------------------------------------------------------------------
      
  /**
   * This method tests that Nucleus objects can be located as expected by using the
   * NucleusObjectLocator.
   */
  @Test
  public void testLocate() {
    CatalogTools catalogTools =
            (CatalogTools) mNucleusObjectLocator.locate("atg/commerce/catalog/CatalogTools");
    assertThat(catalogTools, instanceOf(CatalogTools.class));
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
    HashMap<String, String> params = new HashMap<>();
    DynamoHttpServletRequest request = 
      utils.createDynamoHttpServletRequestForSession(mNucleus, params, 1024, "GET", null);
    ServletUtil.setCurrentRequest(request);
    
    return request;
  }
}
