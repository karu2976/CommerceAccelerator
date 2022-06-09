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

import atg.projects.store.assembler.cartridge.ActorInvokingContentItemModifier;

import com.endeca.infront.cartridge.RefinementMenu;
import com.endeca.infront.cartridge.RefinementMenuConfig;

/**
 * Extends the StoreRefinementMenuHandler to add more application specific functionality.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/assembler/cartridge/handler/B2CStoreRefinementMenuHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class B2CStoreRefinementMenuHandler extends StoreRefinementMenuHandler {

  /** Class version string. */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/assembler/cartridge/handler/B2CStoreRefinementMenuHandler.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------

  /** The name of the actor chain to be invoked to set a currency symbol in the content item. */
  public static final String SET_CURRENCY_SYMBOL_ACTOR_CHAIN = "setCurrencySymbol";

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------

  //----------------------------------------
  //  property: priceRangeDimensionName
  //----------------------------------------
  private String mPriceRangeDimensionName = "product.price_range";

  /**
   * @param pPriceRangeDimensionName
   *   The name of the price range dimension.
   */
  public void setPriceRangeDimensionName(String pPriceRangeDimensionName) {
    mPriceRangeDimensionName = pPriceRangeDimensionName;
  }

  /**
   * @return
   *   The name of the price range dimension.
   */
  public String getPriceRangeDimensionName() {
    return mPriceRangeDimensionName;
  }

  //----------------------------------------
  //  property: pricingContentItemModifier
  //----------------------------------------
  private ActorInvokingContentItemModifier mPricingContentItemModifier = null;

  /**
   * @param pPricingContentItemModifier
   *   The content item modifier that will be used in this handler to retrieve pricing information.
   */
  public void setPricingContentItemModifier(ActorInvokingContentItemModifier pPricingContentItemModifier) {
    mPricingContentItemModifier = pPricingContentItemModifier;
  }

  /**
   * @return
   *   The content item modifier that will be used in this handler to retrieve pricing information.
   */
  public ActorInvokingContentItemModifier getPricingContentItemModifier() {
    return mPricingContentItemModifier;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * This method is overridden in order to append the appropriate currency symbol to
   * the content item when the current refinement is a price range dimension.
   *
   * @param pCartridgeConfig
   *   The cartridge configuration for the RefinementMenu.
   * @param pRefinementMenu
   *   A populated refinement menu that will be processed to include extra configuration.
   * @return
   *   An updated content item.
   */
  public RefinementMenu processRefinements(RefinementMenuConfig pCartridgeConfig,
      RefinementMenu pRefinementMenu) {

    RefinementMenu refinementMenu = super.processRefinements(pCartridgeConfig, pRefinementMenu);

    if (refinementMenu.getDimensionName().equals(getPriceRangeDimensionName())) {
      if (getPricingContentItemModifier() != null) {
        getPricingContentItemModifier().setActorChain(SET_CURRENCY_SYMBOL_ACTOR_CHAIN);
        getPricingContentItemModifier().modify(refinementMenu);
      }
    }

    return refinementMenu;
  }

}
