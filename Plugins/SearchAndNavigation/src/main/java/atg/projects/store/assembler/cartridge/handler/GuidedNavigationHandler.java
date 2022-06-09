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

import atg.projects.store.assembler.cartridge.StoreCartridgeTools;
import com.endeca.infront.assembler.BasicContentItem;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;

/**
 * Handler for the GuidedNavigation cartridge.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/assembler/cartridge/handler/GuidedNavigationHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class GuidedNavigationHandler
  extends NavigationCartridgeHandler<BasicContentItem, ContentItem> {
  
  /** Class version string */
  public static String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/assembler/cartridge/handler/GuidedNavigationHandler.java#1 $$Change: 1385662 $";

  //------------------------------------------------------------------------
  // CONSTANTS
  //------------------------------------------------------------------------

  /**
   * The property that will be used in the content item that defines whether the
   * navigation path is valid or not
   */
  public static final String VALID_NAVIGATION_PATH_PROPERTY_NAME = "validNavigationPath";

  //------------------------------------------------------------------------
  // PROPERTIES
  //------------------------------------------------------------------------ 

  //----------------------------------------
  //  property: storeCartridgeTools
  //----------------------------------------
  private StoreCartridgeTools mStoreCartridgeTools = null;

  /**
   * @param pStoreCartridgeTools
   *   The store cartridge tools component.
   */
  public void setStoreCartridgeTools(StoreCartridgeTools pStoreCartridgeTools) {
    mStoreCartridgeTools = pStoreCartridgeTools;
  }

  /**
   *  @return
   *    The store cartridge tools component.
   */
  public StoreCartridgeTools getStoreCartridgeTools() {
    return mStoreCartridgeTools;
  }

  //------------------------------------------------------------------------
  // METHODS
  //------------------------------------------------------------------------

  /**
   * Create a new BasicContentItem using the passed in ContentItem.
   *
   * @param pContentItem
   *   The cartridge content item to be wrapped.
   *
   * @return
   *   A new Breadcrumbs configuration.
   */
  @Override
  protected BasicContentItem wrapConfig(ContentItem pContentItem) {
    return new BasicContentItem(pContentItem);
  }

  /**
   * This process method determines whether a category is in a valid state or not. If the
   * category currently being navigated to is in a valid state or the navigation filter
   * ID doesn't correspond to a category, a 'validNavigationPath' property is appended
   * to the content item with its value set to true. If the current category being
   * navigated to is invalid, the 'validNavigationPath' property's value is set to false.
   *
   * @param pCartridgeConfig
   *   The cartridge configuration that will be updated with the current category status.
   *
   * @return
   *   The updated guided navigation configuration.
   *
   * @throws CartridgeHandlerException
   *   If an error occurs that is scoped to an individual cartridge instance. This
   *   exception will not halt the entire assembly process, which occurs across multiple
   *   cartridges; instead, this exception will be packaged in the overall response model.
   *   If an unchecked  exception is thrown, then the entire assembly process will be halted.
   */
  @Override
  public BasicContentItem process(BasicContentItem pCartridgeConfig)
      throws CartridgeHandlerException {

    pCartridgeConfig.put(VALID_NAVIGATION_PATH_PROPERTY_NAME,
      getStoreCartridgeTools().validateCategoryNavigation());

    return pCartridgeConfig;
  }

}
