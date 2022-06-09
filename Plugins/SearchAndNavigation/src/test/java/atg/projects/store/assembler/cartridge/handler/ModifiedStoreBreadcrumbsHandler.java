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

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.cartridge.RefinementMenu;
import com.endeca.infront.navigation.request.MdexRequest;
import com.endeca.navigation.*;

import java.util.HashMap;
import java.util.List;

/**
 * StoreRefinementMenuHandler extension that generates dummy MDEX data and 
 * wraps protected methods so that we can access them in our tests.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/handler/ModifiedStoreBreadcrumbsHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ModifiedStoreBreadcrumbsHandler extends StoreBreadcrumbsHandler {

  /** Class version string */
  public static String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/test/java/atg/projects/store/assembler/cartridge/handler/ModifiedStoreBreadcrumbsHandler.java#1 $$Change: 1385662 $";

  //=======================================================================
  // PROPERTIES
  //=======================================================================

  // Properties to set when creating the dummy navigation results.
  public boolean mIsAggregated = false;
  public int mNumERecsToGenerate = 0;
  public int mTotalNumAggrERecs = 0;
  public long mDimensionId = 10011L;
  public String categoryId = "catMen";
  
  //=======================================================================
  // METHODS
  //=======================================================================

  //-----------------------------------------------------------------------
  /**
   * This method allows us to generate dummy MDEX request results.
   * 
   * @param pMdexRequest
   *   Not used in this method.
   * @return
   *   The dummy MDEX query results.
   */
  @Override
  protected ENEQueryResults executeMdexRequest(MdexRequest pMdexRequest)
      throws CartridgeHandlerException {

    return createDummyNavigationRefinements(
      mNumERecsToGenerate, false,  mTotalNumAggrERecs);
  }
  
  /**
   * Generate dummy navigation refinement query results.
   *
   * @param pNumERecs
   *   The number of dummy ERec objects to generate.
   * @param pIsAggregated
   *   Flag to determine whether to generate dummy AggrERecs or not.
   * @param pNumAggregated
   *   The number of dummy AggrERec objects to generate.
   * @return
   *   The populated dummy ENEQueryResults object.
   */
  public MockENEQueryResults createDummyNavigationRefinements(
      int pNumERecs, boolean pIsAggregated, int pNumAggregated) {

    MockENEQueryResults results = 
      createDummyNavigationResults(pNumERecs, pIsAggregated, pNumAggregated);

    CSAMockDataFactory mockDataFactory = new CSAMockDataFactory();

    MockDimension dimension = (MockDimension) mockDataFactory.createCategoryTypeMenDimension();

    MockDimVal root = (MockDimVal) mockDataFactory.createCategoryDimVal();
    root.setMultiSelectAnd(false);
    root.setMultiSelectOr(true);
    
    dimension.setRootDimVal(root);

    MockDimVal descriptor = (MockDimVal) mockDataFactory.createMenCategoryDimVal();

    MockPropertyMap props = new MockPropertyMap();
    MockProperty countProp = new MockProperty("DGraph.Bins", "10");
    props.addProperty(countProp);
    
    descriptor.setProperties(props);
    descriptor.setDimValName("descriptor");

    dimension.setDescriptor(descriptor);

    MockDimValList dimValList = (MockDimValList) mockDataFactory.createCategoryTypeMenDimValList();
    dimValList.appendDimVal(root);

    dimension.setAncestors(dimValList);
    ((MockDimension)dimension).setEdgesList(mockDataFactory.createCategoryTypeMenRefinements());

    MockDimensionList dimList = (MockDimensionList) mockDataFactory.createMenDimensionList();
    dimList.appendDimension(dimension);
    
    ((MockNavigation)results.getNavigation()).setRefinementDimensions(dimList);
    ((MockNavigation)results.getNavigation()).setDescriptorDimensions(dimList);


    return results;
  }

  //----------------------------------------------------------------------------
  /**
   * Generate a dummy ENEQueryResults object with a dummy populated Navigation object.
   *
   * @param pNumERecs
   *   The number of dummy ERec objects to generate.
   * @param pIsAggregated
   *   Flag to determine whether to generate dummy AggrERecs or not.
   * @param pNumAggregated
   *   The number of dummy AggrERec objects to generate.
   * @return
   *   The populated dummy ENEQueryResults object.
   */
  public static MockENEQueryResults createDummyNavigationResults(
    int pNumERecs, boolean pIsAggregated, int pNumAggregated) {

    MockERecList erecList = new MockERecList();

    for (int i = 0; i < pNumERecs; ++i) {
      MockERec erec = new MockERec("spec" + i);
      MockPropertyMap props = new MockPropertyMap();
      props.addProperty(new MockProperty("product.id", "prod_id_" + i));
      props.addProperty(new MockProperty("product.name", "Product " + i));
      props.addProperty(new MockProperty("product.code", "prod_code_" + i));
      props.addProperty(new MockProperty("product.price", "1" + i + ".95"));
      props.addProperty(new MockProperty("extra", "should not be retained when using field lists"));
      erec.setProperties(props);
      erecList.appendERec(erec);
    }

    MockNavigation navigation = new MockNavigation();

    if (pIsAggregated) {
      navigation.setTotalNumAggrERecs(pNumAggregated);
      MockAggrERecList aggrRecs = new MockAggrERecList();

      for (int i = 0; i < pNumAggregated; ++i) {
        MockAggrERec aggrRec = new MockAggrERec();
        aggrRec.setErecs(erecList);
        aggrRec.setTotalErecs(pNumERecs);
        aggrRec.setSpec("spec" + i);
        aggrRecs.append(aggrRec);

        MockPropertyMap props = new MockPropertyMap();
        props.addProperty(new MockProperty("product.id", "prod_id_10" + i));
        props.addProperty(new MockProperty("product.name", "Product 10" + i));
        props.addProperty(new MockProperty("product.code", "prod_code_10" + i));
        props.addProperty(new MockProperty("product.price", "1" + i + ".95"));
        props.addProperty(new MockProperty("extra", "should not be retained when using field lists"));
        aggrRec.setProperties(props);
      }

      navigation.setAggrERecs(aggrRecs);
    }
    else {
      navigation.setTotalNumERecs(pNumERecs);
      navigation.setERecs(erecList);
    }

    MockENEQueryResults result = new MockENEQueryResults();
    navigation.setESearchReportsComplete(new HashMap<String, List<MockESearchReports>>());
    result.setNavigation(navigation);
    return result;
  }

}