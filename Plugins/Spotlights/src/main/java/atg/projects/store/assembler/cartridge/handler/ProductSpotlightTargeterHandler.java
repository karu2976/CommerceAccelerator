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

import atg.endeca.assembler.AssemblerTools;
import atg.projects.store.assembler.cartridge.ContentItemModifier;
import atg.projects.store.assembler.cartridge.StoreCartridgeTools;

import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;
import com.endeca.infront.navigation.model.FilterState;
import com.endeca.infront.navigation.request.RecordsMdexQuery;
import com.endeca.infront.navigation.request.MdexRequest;
import com.endeca.navigation.ENEQueryResults;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This cartridge handler queries the MDEX in order to retrieve record specs for items
 * created in the content item.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Spotlights/src/main/java/atg/projects/store/assembler/cartridge/handler/ProductSpotlightTargeterHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ProductSpotlightTargeterHandler extends
    NavigationCartridgeHandler<ContentItem, ContentItem> {

  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Spotlights/src/main/java/atg/projects/store/assembler/cartridge/handler/ProductSpotlightTargeterHandler.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------

  /** The name of the 'items' property belonging to the content item. */
  public static final String ITEMS_PROPERTY_NAME = "items";

  /**
   * The name of the 'spec' property that is used in an ERec object
   * to reference a record spec value.
   */
  public static final String ITEM_SPEC_PROPERTY_NAME = "spec";

  /** The name of the content item property that references the product ID. */
  public static final String ITEMS_PRODUCT_ID_PROPERTY_NAME = "repositoryId";

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------

  //------------------------------------
  // property: productRecordRepositoryIdName
  //------------------------------------
  private String mProductRecordRepositoryIdName = "product.repositoryId";

  /**
   * @param pProductRecordRepositoryIdName
   *   The product record repository ID name.
   */
  public void setProductRecordRepositoryIdName(String pProductRecordRepositoryIdName) {
    mProductRecordRepositoryIdName = pProductRecordRepositoryIdName;
  }

  /**
   * @return
   *   The product record repository ID name.
   */
  public String getProductRecordRepositoryIdName() {
    return mProductRecordRepositoryIdName;
  }

  //------------------------------------
  // property: storeCartridgeTools
  //------------------------------------
  private StoreCartridgeTools mStoreCartridgeTools = null;

  /**
   * @param pStoreCartridgeTools
   *   A component to perform common cartridge operations.
   */
  public void setStoreCartridgeTools(StoreCartridgeTools pStoreCartridgeTools) {
    mStoreCartridgeTools = pStoreCartridgeTools;
  }

  /**
   * @return
   *   A component to perform common cartridge operations.
   */
  public StoreCartridgeTools getStoreCartridgeTools() {
    return mStoreCartridgeTools;
  }

  //----------------------------------------
  //  property: contentItemModifiers
  //----------------------------------------
  private ContentItemModifier[] mContentItemModifiers = null;

  /**
   * @param pContentItemModifiers
   *   The content item modifiers that will be used in this handler.
   */
  public void setContentItemModifiers(ContentItemModifier[] pContentItemModifiers) {
    mContentItemModifiers = pContentItemModifiers;
  }

  /**
   * @return
   *   The content ite modifiers that will be used in this handler.
   */
  public ContentItemModifier[] getContentItemModifiers() {
    return mContentItemModifiers;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * Return the passed in content item.
   *
   * @param pContentItem
   *   The cartridge content item to be wrapped.
   * @return
   *   A new TargetedItems configuration.
   */
  @Override
  protected ContentItem wrapConfig(ContentItem pContentItem) {
    return pContentItem;
  }

  /**
   * This method takes the list of items (created in the super.process method), queries the
   * MDEX for the record specs for each item and appends those record specs to the corresponding
   * 'items' in the content item.
   *
   * @param pContentItem
   *   The content item to be configured.
   *
   * @return
   *   A fully configured content item.
   */
  @Override
  public ContentItem process(ContentItem pContentItem) {
    modifyContentItem(pContentItem);

    List<Map> items = (ArrayList) pContentItem.get(ITEMS_PROPERTY_NAME);

    if (items != null && items.size() > 0) {

      // Get the product ids of the items within the order.
      List<String> productIds = new ArrayList<>(items.size());

      for (Map item : items) {
        if (item != null) {
          productIds.add((String) item.get(ITEMS_PRODUCT_ID_PROPERTY_NAME));
        }
      }

      FilterState filterState = getNavigationState().getFilterState();
      RecordsMdexQuery query = new RecordsMdexQuery();

      getStoreCartridgeTools().populateRecordSpecMdexRequestParams(
        query, filterState, productIds, getProductRecordRepositoryIdName());

      try {
        MdexRequest mdexRequest = createMdexRequest(filterState, query);
        ENEQueryResults results = executeMdexRequest(mdexRequest);

        Map productIdsToRecordSpecs =
          getStoreCartridgeTools().getItemIdToRecordSpecMap(results, getProductRecordRepositoryIdName());

        if (productIdsToRecordSpecs != null) {
          populateItemsWithRecordSpecs(items, productIdsToRecordSpecs);

          // Overwrite the content item's 'items' value with the updated 'items' object.
          pContentItem.put(ITEMS_PROPERTY_NAME, items);
        }
      }
      catch (CartridgeHandlerException che) {
        AssemblerTools.getApplicationLogging().logDebug(che);
      }

    }

    return pContentItem;
  }

  /**
   * <p>
   *   This method takes the record spec values from the passed in
   *   record specs map and appends them to their corresponding items
   *   in the passed in items list.
   * </p>
   * <p>
   *   Using this method means that there is no need to create a new content item
   *   property to hold the product ID -> record spec map in the content item.
   * </p>
   *
   * @param pItems
   *   A list of items that will be updated with corresponding record spec values.
   * @param pRecordSpecs
   *   A product ID -> record spec map.
   */
  public void populateItemsWithRecordSpecs(List<Map> pItems, Map pRecordSpecs) {

    if (pRecordSpecs != null) {

      // Extract the record specs from the query results and append them to
      // their corresponding items.
      for (Map item : pItems) {
        // The currently iterated element could be null, for example if it
        // has an invalid start/end date. If this is the case, don't process.
        if (item != null) {
          Set<Map.Entry> recordSpecs = pRecordSpecs.entrySet();

          for (Map.Entry recordSpec : recordSpecs) {
            // Iterate through the list of record specs and add the record spec values
            // to their corresponding items.
            if (recordSpec.getKey().equals(item.get(ITEMS_PRODUCT_ID_PROPERTY_NAME))) {
              item.put(ITEM_SPEC_PROPERTY_NAME, recordSpec.getValue());
            }
          }
        }
      }
    }
  }

  /**
   * Loop all configured {@code ContentItemModifiers} and execute their modify method against
   * the passed {@code RecordDetails}.
   *
   * @param pContentItem
   *   The current ContentItem being processed by the request.
   */
  public void modifyContentItem(ContentItem pContentItem) {

    if (getContentItemModifiers() != null && getContentItemModifiers().length > 0) {
      for (ContentItemModifier contentItemModifier : getContentItemModifiers()) {
        contentItemModifier.modify(pContentItem);
      }
    }
  }

}
