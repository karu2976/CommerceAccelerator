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
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.navigation.request.MdexRequest;
import com.endeca.navigation.*;

/**
 * This modified version of the ProductSpotlightTargeterHandler includes methods for retreving
 * mock objects to be used in unit tests.
 *
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Spotlights/src/test/java/atg/projects/store/assembler/cartridge/handler/ModifiedSpotlightTargeterHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ModifiedSpotlightTargeterHandler extends ProductSpotlightTargeterHandler {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Spotlights/src/test/java/atg/projects/store/assembler/cartridge/handler/ModifiedSpotlightTargeterHandler.java#1 $$Change: 1385662 $";


  /**
   * This method returns a content item with mock items.

   * @param pCartridgeConfig
   *
   * @return
   *   A fully configured content item.
   */
  @Override
  public ContentItem process(ContentItem pCartridgeConfig) {

    atg.service.actor.ModelListImpl modelList = new atg.service.actor.ModelListImpl();
    atg.service.actor.ModelBeanImpl modelBean1 = new atg.service.actor.ModelBeanImpl();
    atg.service.actor.ModelBeanImpl modelBean2 = new atg.service.actor.ModelBeanImpl();
    atg.service.actor.ModelBeanImpl modelBean3 = new atg.service.actor.ModelBeanImpl();

    modelBean1.put("repositoryId", "product1");
    modelBean1.put("id", "product1");

    modelBean2.put("repositoryId", "product2");
    modelBean2.put("id", "product2");

    modelBean3.put("repositoryId", "product3");
    modelBean3.put("id", "product3");

    modelList.add(modelBean1);
    modelList.add(modelBean2);
    modelList.add(modelBean3);

    pCartridgeConfig.put("items", modelList);

    return super.process(pCartridgeConfig);
  }

  /**
   * Mock ENEQueryResults builder method.
   *
   * @param mdexRequest
   *   Not used here.
   * @return
   *   A Mock ENEQueryResults object.
   * @throws com.endeca.infront.assembler.CartridgeHandlerException
   */
  protected ENEQueryResults executeMdexRequest(MdexRequest mdexRequest) throws CartridgeHandlerException {
    com.endeca.navigation.MockENEQueryResults results = new com.endeca.navigation.MockENEQueryResults();

    MockAggrERec aggrERec1 = new MockAggrERec();
    aggrERec1.setSpec("A-prod1");
    MockERec erec1 = new MockERec();
    erec1.setSpec("A-prod1");
    MockPropertyMap propertyMap1 = new MockPropertyMap();
    propertyMap1.addProperty(new MockProperty("product.repositoryId", "product1"));
    erec1.setProperties(propertyMap1);
    aggrERec1.setRepresentative(erec1);

    MockAggrERec aggrERec2 = new MockAggrERec();
    aggrERec2.setSpec("A-prod2");
    MockERec erec2 = new MockERec();
    erec2.setSpec("A-prod2");
    MockPropertyMap propertyMap2 = new MockPropertyMap();
    propertyMap2.addProperty(new MockProperty("product.repositoryId", "product2"));
    erec2.setProperties(propertyMap2);
    aggrERec2.setRepresentative(erec2);

    MockAggrERec aggrERec3 = new MockAggrERec();
    aggrERec3.setSpec("A-prod3");
    MockERec erec3 = new MockERec();
    erec3.setSpec("A-prod3");
    MockPropertyMap propertyMap3 = new MockPropertyMap();
    propertyMap3.addProperty(new MockProperty("product.repositoryId", "product3"));
    erec3.setProperties(propertyMap3);
    aggrERec3.setRepresentative(erec3);

    MockAggrERecList aggrERecList = new MockAggrERecList();
    aggrERecList.append(aggrERec1);
    aggrERecList.append(aggrERec2);
    aggrERecList.append(aggrERec3);

    MockNavigation navigation = new MockNavigation();
    navigation.setAggrERecs(aggrERecList);

    results.setNavigation(navigation);

    return results;
  }

}