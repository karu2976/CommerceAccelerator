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

import atg.core.i18n.CountryList;

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;

/**
 * This is handler for shipping address and it adds the shipping countries to the content item.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/assembler/cartridge/handler/ShippingAddressHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ShippingAddressHandler extends NavigationCartridgeHandler {
  
  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/assembler/cartridge/handler/ShippingAddressHandler.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------
  
  /** The cartridge availableCountries property name. */
  public static final String AVAILABLE_COUNTRIES = "availableCountries";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------
  
  //-----------------------------------
  // property: permittedCountries
  //-----------------------------------
  private CountryList mPermittedCountries = null;

  /**
   * @param pPermittedCountries
   *   Sets the shippable countries.
   */
  public void setPermittedCountries(CountryList pPermittedCountries) {
    mPermittedCountries = pPermittedCountries;
  }

  /**
   * @return mPermittedCountries
   *   Returns the shippable countries list.
   */
  public CountryList getPermittedCountries() {
    return mPermittedCountries;
  }  
  
  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * This method implements wrapConfig method of super class as super class method
   * is abstract.
   *  
   * @param pContentItem
   *   Content item.
   * @return
   *   Returns the content item.
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return pContentItem;
  }

  /**
   * This method adds the permitted countries to the content item.
   *  
   * @param pContentItem
   *   Content item for getting configuration from experience manager.
   * @return
   *   Returns the Content item with page configuration.
   * @throws CartridgeHandlerException
   *   Throws exception if there is any issue while invoking  handler.
   */
  @Override
  public ContentItem process(ContentItem pContentItem) throws CartridgeHandlerException {
    
    // Populate the content item with a list of permitted countries
    // for the current site.
    populateAvailableCountries(pContentItem);
    return pContentItem;
  }
  
  /**
   * Add the permitted available countries to the given content item.
   * 
   * @param pContentItem
   *   The content item to populate.
   */
  public void populateAvailableCountries(ContentItem pContentItem) {
    pContentItem.put(AVAILABLE_COUNTRIES, getPermittedCountries().getPlaces());
  }
}