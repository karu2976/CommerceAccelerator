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

import atg.endeca.assembler.navigation.DefaultActionPathProvider;
import atg.multisite.SiteContextException;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryException;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.cartridge.Breadcrumbs;
import com.endeca.infront.cartridge.BreadcrumbsConfig;
import com.endeca.infront.cartridge.model.Ancestor;
import com.endeca.infront.cartridge.model.RefinementBreadcrumb;

import static org.junit.Assert.*;

import com.endeca.infront.site.model.SiteDefinition;
import com.endeca.infront.site.model.SiteState;
import com.endeca.store.configuration.InternalNode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;

/**
 * This class test functionality of StoreBreadcrumbsHandler.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/handler/StoreBreadcrumbsHandlerTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreBreadcrumbsHandlerTest {

  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/handler/StoreBreadcrumbsHandlerTest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------

  /** The path to the Breadcrumbs component */
  public static final String BREADCRUMBS_PATH = "/atg/endeca/assembler/cartridge/handler/Breadcrumbs";

  /** The Breadcrumbs configuration content item to use in our tests */
  public static BreadcrumbsConfig mBreadcrumbsConfig = null;

  /** Nucleus instance to use in our tests. */
  private static Nucleus mNucleus = null;

  /** The name of the root category dimension */
  private static final String CATEGORY_DIMENSION_NAME = "product.category";

  //----------------------------------------------------------------------------
  // MEMBERS
  //----------------------------------------------------------------------------

  private static StoreBreadcrumbsHandler mStoreBreadcrumbsHandler = null;

  //-----------------------------------------------------------------------------
  // METHODS
  //-----------------------------------------------------------------------------

  /**
   * Perform test initialization.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException {

    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] {"CommerceAccelerator.Plugins.SearchAndNavigation"},
      StoreBreadcrumbsHandlerTest.class,
      "StoreBreadcrumbsHandlerTest",
      "/atg/Initial");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    //-----------------------------------------
    // Set up StoreBreadcrumbsHandler.
    //-----------------------------------------
    mStoreBreadcrumbsHandler = (StoreBreadcrumbsHandler)
      request.resolveName(BREADCRUMBS_PATH, true);

    assertNotNull(mStoreBreadcrumbsHandler);

    InternalNode node = new InternalNode("/browse");
    SiteDefinition siteDefinition = new SiteDefinition(node, "StoreSiteUS");
    SiteState siteState = new SiteState("StoreSiteUS", siteDefinition);

    DefaultActionPathProvider provider = (DefaultActionPathProvider)
      request.resolveName("/atg/endeca/assembler/cartridge/manager/DefaultActionPathProvider", true);

    provider.setSiteState(siteState);
  }

  /**
   * Clean up and shut down Nucleus.
   */
  @AfterClass
  public static void tearDownAfterClass() throws IOException, ServiceException {

    if(mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
      mNucleus = null;
    }

    mStoreBreadcrumbsHandler = null;
  }

  /**
   * Re-initialize the cartridge configuration object before each test.
   */
  @Before
  public void setUpBeforeMethod() {
    mBreadcrumbsConfig = new BreadcrumbsConfig();
    mBreadcrumbsConfig.put(StoreBreadcrumbsHandler.DISPLAY_NAME_PROPERTY_ALIAS, "displayName");
  }

  //-----------------------------------------------------------------------------
  // TEST METHODS                          
  //-----------------------------------------------------------------------------

  /**
   * Test the skuActivePriceDimensionName getter/setter.
   */
  @Test
  public void testSkuActivePriceDimensionNameGetterSetter() {
    mStoreBreadcrumbsHandler.setSkuActivePriceDimensionName("sku.activePrice");
    assertEquals("sku.activePrice", mStoreBreadcrumbsHandler.getSkuActivePriceDimensionName());
  }

  /**
   * Test the wrapConfig method.
   */
  @Test
  public void testWrapConfig() {
    assertNotNull(mStoreBreadcrumbsHandler.wrapConfig(mBreadcrumbsConfig));
  }

  /**
   * Test the preprocess method when the displayNameAlias exists.
   */
  @Test
  public void testPreprocessWithAlias() throws CartridgeHandlerException {

    DynamoHttpServletRequest request = setUpCurrentRequest();
    request.setParameter("locale", "de_DE");
    ServletUtil.setCurrentRequest(request);

    mStoreBreadcrumbsHandler.preprocess(mBreadcrumbsConfig);

    assertEquals("displayName_de",
      mBreadcrumbsConfig.get(StoreBreadcrumbsHandler.DISPLAY_NAME_PROPERTY));
  }

  /**
   * Test the preprocess method when the displayNameAlias does not exist.
   */
  @Test
  public void testPreprocessWithNoAlias() throws CartridgeHandlerException {
    mBreadcrumbsConfig.put(
      StoreBreadcrumbsHandler.DISPLAY_NAME_PROPERTY_ALIAS, "nonExistentDisplayName");

    mStoreBreadcrumbsHandler.preprocess(mBreadcrumbsConfig);

    assertEquals("nonExistentDisplayName",
      mBreadcrumbsConfig.get(StoreBreadcrumbsHandler.DISPLAY_NAME_PROPERTY));
  }

  /**
   * Test the process method.
   */
  @Test
  public void testProcess() throws CartridgeHandlerException {
    mStoreBreadcrumbsHandler.preprocess(mBreadcrumbsConfig);
    Breadcrumbs breadcrumbs = mStoreBreadcrumbsHandler.process(mBreadcrumbsConfig);
    List refinementCrumbs = (ArrayList) breadcrumbs.get("refinementCrumbs");

    assertNotNull(breadcrumbs);
    assertEquals(2, refinementCrumbs.size());
    assertEquals("Men", ((RefinementBreadcrumb) refinementCrumbs.get(0)).getDimensionName());
    assertEquals("product.category", ((RefinementBreadcrumb) refinementCrumbs.get(1)).getDimensionName());
  }

  /**
   * Test the findLocalizedLabel method. Creates category breadcrumbs, where
   * each refinement crumb has displayName_en, displayName_de properties and 
   * label set. Then mStoreBreadcrumbsHandler.findLocalizedLabel method is
   * called to update the breadcrumbs with localized display name -
   * displayName_de
   */
  @Test
  public void testFindLocalizedLabel() {
    Breadcrumbs breadcrumbs = createBreadcrumbs();
   
    if (breadcrumbs != null) {
      List<RefinementBreadcrumb> refinementsCrumbs = breadcrumbs.getRefinementCrumbs();
      List<RefinementBreadcrumb> localizedRefinementsCrumbs = new ArrayList<>();

      if (refinementsCrumbs != null && refinementsCrumbs.size() > 0) {
        for (RefinementBreadcrumb refinementBreadcrumb: refinementsCrumbs) {

          //If it is a category refinement  - use localized names
          if (CATEGORY_DIMENSION_NAME.equals(refinementBreadcrumb.getDimensionName())) {    
            List<Ancestor> ancestorsCrumbs = refinementBreadcrumb.getAncestors();
            
            if (ancestorsCrumbs != null && ancestorsCrumbs.size() > 0) {
              List<Ancestor> localizedAncestorsCrumbs = new ArrayList<>(ancestorsCrumbs.size());

              for (Ancestor ancestor: ancestorsCrumbs) {

                // Look for localized label
                String currentLabel = ancestor.getLabel();

                String localizedLabel =
                  mStoreBreadcrumbsHandler.findLocalizedLabel(
                    ancestor.getProperties(), currentLabel, "displayName_de");

                ancestor.setLabel(localizedLabel);
                localizedAncestorsCrumbs.add(ancestor);
              }

              refinementBreadcrumb.setAncestors(localizedAncestorsCrumbs);
            }

            // Look for localized label
            String currentLabel = refinementBreadcrumb.getLabel();

            String localizedLabel =
              mStoreBreadcrumbsHandler.findLocalizedLabel(
                refinementBreadcrumb.getProperties(), currentLabel, "displayName_de");

            refinementBreadcrumb.setLabel(localizedLabel);
          }
          localizedRefinementsCrumbs.add(refinementBreadcrumb);
        }
        breadcrumbs.setRefinementCrumbs(localizedRefinementsCrumbs);
      }
    }    
    
    // Check the results
    if (breadcrumbs != null) {
      List<RefinementBreadcrumb> refinementsCrumbs = breadcrumbs.getRefinementCrumbs();

      for(RefinementBreadcrumb refinementBreadcrumb: refinementsCrumbs) {
        List<Ancestor> ancestorsCrumbs = refinementBreadcrumb.getAncestors();

        for (Ancestor ancestorsCrumb: ancestorsCrumbs) {
          assertTrue(ancestorsCrumb.getLabel().startsWith("displayName_de"));
        }
        assertTrue(refinementBreadcrumb.getLabel().startsWith("displayName_de"));
      }
    }
  }
  
  /**
   * Test the findLocalizedLabel method. Creates breadcrumbs, where
   * each refinement breadcrumbs has displayName_en, displayName_de properties and 
   * label set. Then mStoreBreadcrumbsHandler.findLocalizedLabel method is
   * called to update the refinemnet breadcrumbs with localized display name -
   * displayName_es. As refinements were created without displayName_es
   * property, label shouldn't change.
   */
  @Test
  public void testFindLocalizedLabelNoLocalizedLabel() {
    Breadcrumbs breadcrumbs = createBreadcrumbs();
    
    if (breadcrumbs != null) {
      List<RefinementBreadcrumb> refinementsCrumbs = breadcrumbs.getRefinementCrumbs();
      List<RefinementBreadcrumb> localizedRefinementsCrumbs = new ArrayList<>();

      if (refinementsCrumbs != null && refinementsCrumbs.size() > 0) {
        for (RefinementBreadcrumb refinementBreadcrumb: refinementsCrumbs) {

          //If it is a category refinement  - use localized names
          if (CATEGORY_DIMENSION_NAME.equals(refinementBreadcrumb.getDimensionName())) {    
            List<Ancestor> ancestorsCrumbs = refinementBreadcrumb.getAncestors();
            
            if (ancestorsCrumbs != null && ancestorsCrumbs.size() > 0) {
              List<Ancestor> localizedAncestorsCrumbs = new ArrayList<>(ancestorsCrumbs.size());

              for (Ancestor ancestor: ancestorsCrumbs) {

                // Look for localized label
                String currentLabel = ancestor.getLabel();

                String localizedLabel =
                  mStoreBreadcrumbsHandler.findLocalizedLabel(
                    ancestor.getProperties(), currentLabel, "displayName_es");

                ancestor.setLabel(localizedLabel);
                localizedAncestorsCrumbs.add(ancestor);
              }
              refinementBreadcrumb.setAncestors(localizedAncestorsCrumbs);
            }

            // Look for localized label
            String currentLabel = refinementBreadcrumb.getLabel();

            String localizedLabel =
              mStoreBreadcrumbsHandler.findLocalizedLabel(
                refinementBreadcrumb.getProperties(), currentLabel, "displayName_es");

            refinementBreadcrumb.setLabel(localizedLabel);
          }
          localizedRefinementsCrumbs.add(refinementBreadcrumb);
        }
        breadcrumbs.setRefinementCrumbs(localizedRefinementsCrumbs);
      }
    }    
    
    // Check the results
    if (breadcrumbs != null) {
      List<RefinementBreadcrumb> refinementsCrumbs = breadcrumbs.getRefinementCrumbs();

      for(RefinementBreadcrumb refinementBreadcrumb: refinementsCrumbs) {
        List<Ancestor> ancestorsCrumbs = refinementBreadcrumb.getAncestors();

        for (Ancestor ancestorsCrumb: ancestorsCrumbs) {
          assertTrue(ancestorsCrumb.getLabel().startsWith("displayName_en"));
        }
        assertTrue(refinementBreadcrumb.getLabel().startsWith("displayName_en"));
      }
    }
  }

  //-----------------------------------------------------------------------------
  // Utility methods                       
  //-----------------------------------------------------------------------------
  
  /**
   * Creates Breadcrumbs with 5 refinement crumbs. Each refinement crumb
   * has 5 ancestirs. Each refinement crumb has displayName_en = displayName_en + i, 
   * displayName_de = displayName_de + i and label property that
   * is by default the same as displayName_en.
   */
  protected static Breadcrumbs createBreadcrumbs() {
    BreadcrumbsConfig config = new BreadcrumbsConfig("Breadcrumbs");
    Breadcrumbs breadcrumbs = new Breadcrumbs(config);
    List<RefinementBreadcrumb> refinementCrumbs = new LinkedList<>();
  
    String displayName = "displayName_en";
    String displayNameDe = "displayName_de";

    for (int i = 1; i <= 5; i++) {
      refinementCrumbs.add(createRefinementBreadcrumb(displayName + i, displayNameDe + i));
    }

    breadcrumbs.setRefinementCrumbs(refinementCrumbs);
    return breadcrumbs;
  }
  
  /**
   * Creates category Breadcrumbs with given displayName_en, displayName_de and
   * 5 ancestors.
   */
  protected static RefinementBreadcrumb createRefinementBreadcrumb(String pDisplayName, String pDisplayNameDe) {
    RefinementBreadcrumb refinementCrumb = new RefinementBreadcrumb();
    refinementCrumb.setLabel(pDisplayName);
    refinementCrumb.setDimensionName(CATEGORY_DIMENSION_NAME);
    
    // Create map of refinement breadcrumb properties
    Map<String,String> map = new HashMap<>();
    map.put("displayName_en", pDisplayName);
    map.put("displayName_de", pDisplayNameDe);  
    refinementCrumb.setProperties(map);
    
    List<Ancestor> ancestorsCrumbs = new ArrayList<>();

    for (int i = 1; i <= 5; i++) {
      ancestorsCrumbs.add(createAncestor(pDisplayName + i, pDisplayNameDe + i));
    }
    refinementCrumb.setAncestors(ancestorsCrumbs);
    
    return refinementCrumb;
  }

  /**
   * Creates category Ancestor with given displayName_en, displayName_de
   */
  protected static Ancestor createAncestor(String pDisplayName, String pDisplayNameDe) {
    Ancestor ancestor = new Ancestor();
    ancestor.setLabel(pDisplayName);
    
    // Create map of refinement breadcrumb properties
    Map<String,String> map = new HashMap<>();
    map.put("displayName_en", pDisplayName);
    map.put("displayName_de", pDisplayNameDe);  
    ancestor.setProperties(map);
    
    return ancestor;
  }

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
