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
import com.endeca.infront.navigation.request.MdexRequest;
import com.endeca.navigation.ENEQueryResults;
import com.endeca.navigation.MockAggrERec;
import com.endeca.navigation.MockAggrERecList;
import com.endeca.navigation.MockENEQueryResults;
import com.endeca.navigation.MockERec;
import com.endeca.navigation.MockNavigation;
import com.endeca.navigation.MockProperty;
import com.endeca.navigation.MockPropertyMap;

/**
 * Mock AccountOrderDetailHandler that returns fake MDEX data.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/test/java/atg/projects/store/assembler/cartridge/handler/MockAccountOrderDetailHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class MockAccountOrderDetailHandler extends AccountOrderDetailHandler {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/test/java/atg/projects/store/assembler/cartridge/handler/MockAccountOrderDetailHandler.java#1 $$Change: 1385662 $";


  @Override
  protected ENEQueryResults executeMdexRequest(MdexRequest pMdexRequest)
    throws CartridgeHandlerException {

    MockERec prod1 = new MockERec();
    prod1.setSpec("prod1_spec");
    MockPropertyMap props1 = new MockPropertyMap();
    props1.addProperty(new MockProperty("product.repositoryId", "prod1_id"));
    prod1.setProperties(props1);

    MockERec prod2 = new MockERec();
    prod2.setSpec("prod2_spec");
    MockPropertyMap props2 = new MockPropertyMap();
    props2.addProperty(new MockProperty("product.repositoryId", "prod2_id"));
    prod2.setProperties(props2);

    MockAggrERec aggrRec1 = new MockAggrERec();
    aggrRec1.setRepresentative(prod1);

    MockAggrERec aggrRec2= new MockAggrERec();
    aggrRec2.setRepresentative(prod2);

    MockAggrERecList aggrRecs = new MockAggrERecList();
    aggrRecs.append(aggrRec1);
    aggrRecs.append(aggrRec2);

    MockNavigation navigation = new MockNavigation();
    navigation.setAggrERecs(aggrRecs);

    MockENEQueryResults results = new MockENEQueryResults();
    results.setNavigation(navigation);

    return results;
  }
}