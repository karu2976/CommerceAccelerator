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
import com.endeca.infront.navigation.NavigationException;
import com.endeca.infront.navigation.model.FilterState;
import com.endeca.infront.navigation.request.MdexQuery;
import com.endeca.infront.navigation.request.MdexRequest;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.MockPropertyMap;
import com.endeca.navigation.MockProperty;
import com.endeca.navigation.MockAggrERec;
import com.endeca.navigation.MockERec;
import com.endeca.navigation.MockAggrERecList;
import com.endeca.navigation.MockNavigation;

/**
 * This modified version of the CartEditorHandler includes methods for retrieving
 * mock objects to be used in unit tests.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/ShoppingCart/src/test/java/atg/projects/store/assembler/cartridge/handler/ModifiedCartEditorHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ModifiedCartEditorHandler extends CartEditorHandler {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/ShoppingCart/src/test/java/atg/projects/store/assembler/cartridge/handler/ModifiedCartEditorHandler.java#1 $$Change: 1385662 $";

  /**
   * Create a dummy MdexRequest object.
   *
   * @param pFilterState
   * @param pMdexQuery
   *
   * @return
   *   A dummy MdexRequest object.
   * @throws CartridgeHandlerException
   */
  @Override
  protected MdexRequest createMdexRequest(FilterState pFilterState, MdexQuery pMdexQuery)
      throws CartridgeHandlerException {
    return new MdexRequest() {
      @Override
      protected ENEQueryResults executeQuery() throws NavigationException {
        return null;
      }
    };
  }

  /**
   * Mock ENEQueryResults builder method.
   *
   * @param mdexRequest
   *   Not used here.
   * @return
   *   A Mock ENEQueryResults object.
   * @throws CartridgeHandlerException
   */
  protected ENEQueryResults executeMdexRequest(MdexRequest mdexRequest) throws CartridgeHandlerException {
    com.endeca.navigation.MockENEQueryResults results = new com.endeca.navigation.MockENEQueryResults();

    MockAggrERec aggrERec1 = new MockAggrERec();
    aggrERec1.setSpec("A-prod1");
    MockERec erec1 = new MockERec();
    erec1.setSpec("A-prod1");
    MockPropertyMap propertyMap1 = new MockPropertyMap();
    propertyMap1.addProperty(new MockProperty("product.repositoryId", "xprod2073"));
    erec1.setProperties(propertyMap1);
    aggrERec1.setRepresentative(erec1);

    MockAggrERec aggrERec2 = new MockAggrERec();
    aggrERec2.setSpec("A-prod2");
    MockERec erec2 = new MockERec();
    erec2.setSpec("A-prod2");
    MockPropertyMap propertyMap2 = new MockPropertyMap();
    propertyMap2.addProperty(new MockProperty("product.repositoryId", "xprod2071"));
    erec2.setProperties(propertyMap2);
    aggrERec2.setRepresentative(erec2);

    MockAggrERecList aggrERecList = new MockAggrERecList();
    aggrERecList.append(aggrERec1);
    aggrERecList.append(aggrERec2);

    MockNavigation navigation = new MockNavigation();
    navigation.setAggrERecs(aggrERecList);

    results.setNavigation(navigation);

    return results;
  }

}