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

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.endeca.assembler.navigation.DefaultActionPathProvider;
import atg.multisite.SiteContext;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.ServletTestUtils;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.cartridge.RefinementMenu;
import com.endeca.infront.cartridge.RefinementMenuConfig;
import com.endeca.infront.cartridge.model.Refinement;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import static org.junit.Assert.*;

import com.endeca.infront.site.model.SiteDefinition;
import com.endeca.infront.site.model.SiteState;
import com.endeca.store.configuration.InternalNode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.endeca.infront.navigation.NavigationState;
import com.endeca.infront.navigation.model.FilterState;

/**
 * This class test functionality of StoreRefinementMenuHandler
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/handler/StoreRefinementMenuHandlerTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreRefinementMenuHandlerTest {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/handler/StoreRefinementMenuHandlerTest.java#1 $$Change: 1385662 $";

  //=======================================================================
  // CONSTANTS                           
  //=======================================================================  
  
  // Component paths.
  public static final String REFINEMENT_MENU_PATH = "/atg/endeca/assembler/cartridge/handler/RefinementMenu";

  //=======================================================================
  // MEMBER VARIABLES                           
  //=======================================================================  
  
  private static Nucleus mNucleus = null;
  private static ModifiedStoreRefinementMenuHandler mStoreRefinementMenuHandler = null;
  private static SiteContextManager mSiteContextManager = null;
  private static GSARepository mProductCatalog = null;
  private static GSARepository mSiteRepository = null;

  //=======================================================================
  // METHODS                          
  //=======================================================================  

  /**
   * Perform test initialization.
   */
  @BeforeClass
  public static void setUpBeforeClass() throws ServletException, RepositoryException, SiteContextException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[] {"CommerceAccelerator.Plugins.SearchAndNavigation"},
      StoreRefinementMenuHandlerTest.class,
      "StoreRefinementMenuHandlerTest",
      "/atg/Initial");

    DynamoHttpServletRequest request = setUpCurrentRequest();

    //-----------------------------------------
    // Set up StoreRefinementMenuHandler.
    //-----------------------------------------
    mStoreRefinementMenuHandler = (ModifiedStoreRefinementMenuHandler)
      request.resolveName(REFINEMENT_MENU_PATH, true);

    assertNotNull(mStoreRefinementMenuHandler);

    //-----------------------------------------
    // Set the NavigationState and FilterState.
    //-----------------------------------------
    NavigationState navigationState = mStoreRefinementMenuHandler.getNavigationState();
    mStoreRefinementMenuHandler.setNavigationState(navigationState);
    mStoreRefinementMenuHandler.getNavigationState().updateFilterState(new FilterState());

    //-----------------------------------------
    // Initialize catalog repository and import sample data.
    //-----------------------------------------
    mProductCatalog = (GSARepository) mNucleus.resolveName("/atg/commerce/catalog/ProductCatalog", true);
    String[] dataFileNames = { "catalog.xml" };
    TemplateParser.loadTemplateFiles(mProductCatalog, 1, dataFileNames, true, new PrintWriter(System.out), null);

    //-----------------------------------------
    // Initialize site repository and set site context.
    //-----------------------------------------
    mSiteRepository = (GSARepository) mNucleus.resolveName("/atg/multisite/SiteRepository", true);
    String[] siteDataFileNames = { "sites.xml" };
    TemplateParser.loadTemplateFiles(mSiteRepository, 1, siteDataFileNames,
      true, new PrintWriter(System.out), null);

    RepositoryItem storeSiteUS = mSiteRepository.getItem("storeSiteUS");

    mSiteContextManager =
      (SiteContextManager) mNucleus.resolveName("/atg/multisite/SiteContextManager", true);

    SiteContext sc = mSiteContextManager.getSiteContext(storeSiteUS.getRepositoryId());
    mSiteContextManager.pushSiteContext(sc);

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

    mStoreRefinementMenuHandler = null;
    mSiteContextManager = null;
    mProductCatalog = null;
    mSiteRepository = null;
  }

  /**
   * Test the categoryIdPropertyName getter/setter.
   */
  @Test
  public void testCategoryIdPropertyName() {
    mStoreRefinementMenuHandler.setCategoryIdPropertyName("category.repositoryId");
    assertEquals("category.repositoryId", mStoreRefinementMenuHandler.getCategoryIdPropertyName());
  }

  /**
   * Test the wrapConfig method.
   */
  @Test
  public void testWrapConfig() {
    assertNotNull(mStoreRefinementMenuHandler.wrapConfig(new RefinementMenuConfig()));
  }

  /**
   * Test the preprocess method when the displayNameAlias exists.
   */
  @Test
  public void testPreprocessGerman() throws CartridgeHandlerException {

    DynamoHttpServletRequest request = setUpCurrentRequest();
    request.setParameter("locale", "de_DE");
    ServletUtil.setCurrentRequest(request);

    RefinementMenuConfig cartridgeConfig = new RefinementMenuConfig();
    cartridgeConfig.put(StoreRefinementMenuHandler.DISPLAY_NAME_PROPERTY_ALIAS, "displayName");
    cartridgeConfig.setDimensionId("12345");
    cartridgeConfig.put("dimensionName", "clothing-sku.color");

    mStoreRefinementMenuHandler.preprocess(cartridgeConfig);

    assertEquals("displayName_de",
      cartridgeConfig.get(StoreRefinementMenuHandler.DISPLAY_NAME_PROPERTY));
  }

  /**
   * Ensure that the preprocess method adds the correct number of RangeFilters to the FilterState.
   */
  @Test
  public void testPreprocess() throws CartridgeHandlerException {
    RefinementMenuConfig cartridgeConfig = new RefinementMenuConfig();
    cartridgeConfig.put(StoreRefinementMenuHandler.DISPLAY_NAME_PROPERTY_ALIAS, "displayName");
    cartridgeConfig.setDimensionId("12345");
    cartridgeConfig.put("dimensionName", "clothing-sku.color");

    mStoreRefinementMenuHandler.preprocess(cartridgeConfig);
    assertEquals(2, mStoreRefinementMenuHandler.getNavigationState().getFilterState().getRangeFilters().size());
  }

  /**
   * Ensure that the correct refinement menu is returned from the process method.
   */
  @Test
  public void testProcess() throws CartridgeHandlerException {
    RefinementMenuConfig cartridgeConfig = new RefinementMenuConfig();
    cartridgeConfig.put(StoreRefinementMenuHandler.DISPLAY_NAME_PROPERTY_ALIAS, "displayName");
    cartridgeConfig.setDimensionId("10011");

    mStoreRefinementMenuHandler.preprocess(cartridgeConfig);
    RefinementMenu refinementMenu = mStoreRefinementMenuHandler.process(cartridgeConfig);
    assertEquals("Men", refinementMenu.getRefinements().get(0).getLabel());

    // Ensure moreLink is present if lessLink is and vice versa.
    boolean moreLink = refinementMenu.containsKey("moreLink");
    boolean lessLink = refinementMenu.containsKey("lessLink");
    assertEquals(moreLink, lessLink);
  }

  /**
   * Test the findLocalizedLabel method. Creates refinementMenu, where
   * each refinement has displayName_en, displayName_de properties and 
   * label set. Then mStoreRefinementMenuHandler.findLocalizedLabel method is 
   * called to update the refinements with localized display name -
   * displayName_de
   */
  @Test
  public void testFindLocalizedLabel() {
    RefinementMenu refinementMenu = createRefinementMenu();
   
    if (refinementMenu != null) {
      List<Refinement> refinements = refinementMenu.getRefinements();

      if (refinements != null) {
        List<Refinement> localizedRefinements = new ArrayList<>(refinements.size());

        for (Refinement refinement : refinements) {

         // For each category refinement look for localized label.
         String currentLabel = refinement.getLabel();

         String localizedLabel = mStoreRefinementMenuHandler.findLocalizedLabel(
          refinement.getProperties(), currentLabel, "displayName_de");

         refinement.setLabel(localizedLabel);
         localizedRefinements.add(refinement);
        }

        refinementMenu.setRefinements(localizedRefinements);
      }
    }
    
    // Check the results.
    if (refinementMenu != null) {
      List<Refinement> refinements = refinementMenu.getRefinements();
      for (Refinement refinement: refinements) {
        assertTrue(refinement.getLabel().startsWith("displayName_de"));
      }
    }
  }
  
  /**
   * Test the findLocalizedLabel method. Creates refinementMenu, where
   * each refinement has displayName_en, displayName_de properties and 
   * label set. Then mStoreRefinementMenuHandler.findLocalizedLabel method is 
   * called to update the refinements with localized display name -
   * displayName_es. As refinements were created without displayName_es
   * property, label shouldn't change.
   */
  @Test
  public void testFindLocalizedLabelNoLocalizedLabel() {
    RefinementMenu refinementMenu = createRefinementMenu();
   
    if (refinementMenu != null) {
      List<Refinement> refinements = refinementMenu.getRefinements();

      if (refinements != null) {
        List<Refinement> localizedRefinements = new ArrayList<>(refinements.size());

        for (Refinement refinement : refinements) {

          // For each category refinement look for localized label.
          String currentLabel = refinement.getLabel();

          String localizedLabel = mStoreRefinementMenuHandler.findLocalizedLabel(
            refinement.getProperties(), currentLabel, "displayName_es");

          refinement.setLabel(localizedLabel);
          localizedRefinements.add(refinement);
        }

        refinementMenu.setRefinements(localizedRefinements);
      }
    }
    
    // Check the results. As display name.
    if (refinementMenu != null) {
      List<Refinement> refinements = refinementMenu.getRefinements();

      for (Refinement refinement: refinements) {
        assertTrue(refinement.getLabel().startsWith("displayName_en"));
      }
    }
  }
  
  //=======================================================================
  // Utility methods                       
  //=======================================================================  
  
  /**
   * Creates RefinementMenu with 5 refinements.
   * Each refinement has displayName_en = displayName_en + i, 
   * displayName_de = displayName_de + i and label property that
   * is by default the same as displayName_en.
   */
  protected static RefinementMenu createRefinementMenu() {
    RefinementMenuConfig config = new RefinementMenuConfig("RefinementMenu");
    RefinementMenu menu = new RefinementMenu(config);
    List<Refinement> refinements = new LinkedList<>();
  
    String displayName = "displayName_en";
    String displayNameDe = "displayName_de";

    for (int i = 1; i <= 5; i++) {
      refinements.add(createRefinement(displayName + i, displayNameDe + i));
    }

    menu.setRefinements(refinements);
    return menu;
  }
  
  /**
   * Creates Refinement with given displayName_en, displayName_de
   */
  protected static Refinement createRefinement(String pDisplayName, String pDisplayNameDe) {
    Refinement refinement = new Refinement();
    refinement.setLabel(pDisplayName);
    
    // Create map of refinement properties
    Map<String,String> map = new HashMap<String, String>();
    map.put("displayName_en", pDisplayName);
    map.put("displayName_de", pDisplayNameDe);  
    refinement.setProperties(map);
    
    return refinement;
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
