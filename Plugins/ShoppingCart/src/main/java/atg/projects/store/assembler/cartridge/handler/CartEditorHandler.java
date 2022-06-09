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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import atg.commerce.endeca.cache.DimensionValueCacheObject;
import atg.commerce.endeca.cache.DimensionValueCacheTools;
import atg.commerce.order.CommerceItem;
import atg.commerce.pricing.PricingTools;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingModelHolder;
import atg.endeca.assembler.AssemblerTools;
import atg.multisite.SiteContextException;
import atg.projects.store.assembler.cartridge.StoreCartridgeTools;
import atg.projects.store.catalog.CatalogNavigationService;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.repository.RepositoryException;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.assembler.ContentItem;
import com.endeca.infront.cartridge.NavigationCartridgeHandler;
import com.endeca.infront.navigation.model.FilterState;
import com.endeca.infront.navigation.request.MdexRequest;
import com.endeca.infront.navigation.request.RecordsMdexQuery;
import com.endeca.navigation.ENEQueryResults;

/**
 * This handler reprices the order on cart page and adds the order to the content item. A request is
 * made to Endeca for the record spec which is used to link the products to the Endeca driven product
 * details page.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/ShoppingCart/src/main/java/atg/projects/store/assembler/cartridge/handler/CartEditorHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class CartEditorHandler extends NavigationCartridgeHandler {

  /** Class version string */
  public static String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/ShoppingCart/src/main/java/atg/projects/store/assembler/cartridge/handler/CartEditorHandler.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------
  
  /** Key for current order in the content item. */
  public static final String ORDER = "order";

  /** Key for endeca driven product page urls object in content item. */
  public static final String PRODUCT_URLS = "productURLs";
  
  /** Key for continue shopping url link. */
  public static final String CONTINUE_SHOPPING_URL = "continueShoppingURL";

  /** Key for ancestor category list. */
  public static final String ANCESTOR_LIST = "ancestors";

  //---------------------------------------------------------------------------
  // MEMBERS
  //---------------------------------------------------------------------------
  private MdexRequest mMdexRequest = null;
  
  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: shoppingCart
  //-----------------------------------
  private OrderHolder mShoppingCart = null;

  /**
   * @param pShoppingCart
   *   Sets the shopping cart component.
   **/
  public void setShoppingCart(OrderHolder pShoppingCart) {
      mShoppingCart = pShoppingCart;
  }

  /**
   * @return
   *   Returns the shopping cart component.
   **/
  public OrderHolder getShoppingCart() {
    return mShoppingCart;
  }

  //-----------------------------------
  // property: userPricingModels
  //-----------------------------------
  private PricingModelHolder mUserPricingModels = null;

  /**
   * @param pUserPricingModels
   *   Sets the PricingModelHolder that should be used for pricing.
   */
  public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
    mUserPricingModels = pUserPricingModels;
  }

  /**
   * @return
   *   Returns the PricingModelHolder component.
   */
  public PricingModelHolder getUserPricingModels() {
    return mUserPricingModels;
  }  

  //-----------------------------------
  // property: profile
  //-----------------------------------
  private Profile mProfile = null;

  /**
   * @param pProfile
   *   Set the Profile property.
   */
  public void setProfile(Profile pProfile) {
    mProfile = pProfile;
  }

  /**
   * @return
   *   Returns the Profile property.
   */
  public Profile getProfile() {
    return mProfile;
  }
  
  //-----------------------------------
  // property: pricingTools
  //-----------------------------------
  private PricingTools mPricingTools = null;

  /**
   * @param pPricingTools
   *   Set the pricing tools component.
   */
  public void setPricingTools(PricingTools pPricingTools) {
    mPricingTools = pPricingTools;
  }

  /**
   * @return
   *   Returns the pricing tools component.
   */
  public PricingTools getPricingTools() {
    return mPricingTools;
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
  // property: catalogNavigation
  //------------------------------------
  private CatalogNavigationService mCatalogNavigation = null;

  /**
   * @param pCatalogNavigation
   *   A component to perform common cartridge operations.
   */
  public void setCatalogNavigation(CatalogNavigationService pCatalogNavigation) {
    mCatalogNavigation = pCatalogNavigation;
  }

  /**
   * @return
   *   A component to perform common cartridge operations.
   */
  public CatalogNavigationService getCatalogNavigation() {
    return mCatalogNavigation;
  }

  //-----------------------------------
  // property: dimensionValueCacheTools
  //-----------------------------------
  private DimensionValueCacheTools mDimensionValueCacheTools = null;
  
  /**
   * @return 
   *   The mDimensionValueCacheTools. A tools class containing useful
   *   and reusable methods.
   */
  public DimensionValueCacheTools getDimensionValueCacheTools() {
    return mDimensionValueCacheTools;
  }

  /**
   * @param pDimensionValueCacheTools
   *   The mDimensionValueCacheTools to set.
   */
  public void setDimensionValueCacheTools(DimensionValueCacheTools pDimensionValueCacheTools) {
    mDimensionValueCacheTools = pDimensionValueCacheTools;
  }
  
  //-----------------------------------
  // property: repositoryIdDelim
  //-----------------------------------
  private String mRepositoryIdDelim = ":";
  
  /**
   * @return 
   *   The delimited used to split repository ids when passed in from a page in string form.
   *   E.g "id0001:id0002:id0003". Defaults to a colon.
   */
  public String getRepositoryIdDelim() {
    return mRepositoryIdDelim;
  }

  /**
   * @param pRepositoryIdDelim
   *   The mRepositoryIdDelim to set.
   */
  public void setRepositoryIdDelim(String pRepositoryIdDelim) {
    mRepositoryIdDelim = pRepositoryIdDelim;
  }
  
  //------------------------------------
  // property: catalogTools
  //------------------------------------
  protected StoreCatalogTools mCatalogTools = null;

  /**
   * @param pCatalogTools
   *   The CatalogTools object to use when looking up products, categories and SKUs.
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
   * @return
   *   The CatalogTools object to use when looking up products, categories and SKUs.
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Return the passed in content item.
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
   * Creates an MdexRequest which queries for the records corresponding to the carts current items.
   *
   * @param pContentItem The content item.
   * @throws CartridgeHandlerException
   */
  @Override
  public void preprocess(ContentItem pContentItem) throws CartridgeHandlerException {
    Order order = getShoppingCart().getCurrent();

    List<CommerceItem> commerceItems = order.getCommerceItems();

    if (commerceItems == null || commerceItems.isEmpty()) {
      return;
    }

    // Get the product ids of the items within the order.
    List<String> productIds = new ArrayList<>(commerceItems.size());

    for (CommerceItem item : commerceItems) {
      productIds.add(item.getAuxiliaryData().getProductId());
    }

    FilterState filterState = getNavigationState().getFilterState();
    RecordsMdexQuery query = new RecordsMdexQuery();

    getStoreCartridgeTools().populateRecordSpecMdexRequestParams(
      query, filterState, productIds, getProductRecordRepositoryIdName());

    mMdexRequest = createMdexRequest(filterState, query);
  }

  /**
   * This method reprices the order on cart page and adds the order to the content item. The
   * MDEX request generated in the preprocess method is executed, if there are any records
   * returned the specs are extracted and added to the content item so Endeca driven page urls
   * can be constructed on the renderer.
   *  
   * @param pContentItem
   *   Content item for getting configuration from experience manager.
   * @return 
   *   Returns the Content item with page configuration.
   * @throws CartridgeHandlerException
   *   Throws exception if there's any issue while invoking handler.
   */
  @Override
  public ContentItem process(ContentItem pContentItem) throws CartridgeHandlerException {

    // Query for the record specs so we can link to the Endeca driven product page.
    if (mMdexRequest != null) {
      ENEQueryResults results = executeMdexRequest(mMdexRequest);

      Map productIdsToRecordSpecs =
        getStoreCartridgeTools().getItemIdToRecordSpecMap(
          results, getProductRecordRepositoryIdName());

      if (productIdsToRecordSpecs != null) {
        pContentItem.put(PRODUCT_URLS, productIdsToRecordSpecs);
      }
    }

    // The Catalog is a hierarchy, so position 1 in the hierarchy will
    // always be the current top level category if there is one. The first
    // entry is always the catalog root node.
    String currentCategory = getCatalogNavigation().getCurrentCategory();
    
    try {
      if (!currentCategory.isEmpty() && getCatalogTools().isCategoryOnCurrentSite(currentCategory))
      {
        // Get the cache object for a particular category with a particular set of ancestors.
        List<String> ancestors = getDimensionValueCacheTools().getAncestorIds(currentCategory);
        DimensionValueCacheObject result = getDimensionValueCacheTools().get(currentCategory, ancestors);
        pContentItem.put(CONTINUE_SHOPPING_URL, result.getUrl());
      } 
      else {
        pContentItem.put(CONTINUE_SHOPPING_URL, ".");
      }
    }
    catch (RepositoryException | SiteContextException e) {
      AssemblerTools.getApplicationLogging().vlogError(e,
        "An error occurred when retrieving category for Id: {1}", currentCategory);
    }
    
    // Reprice the order.
    try {
      repriceOrder();
      pContentItem.put(ORDER, getShoppingCart().getCurrent());
    } 
    catch (PricingException ex) {
      if (AssemblerTools.getApplicationLogging().isLoggingError()) {
        AssemblerTools.getApplicationLogging().logError(
          "An error occurred repricing the order", ex);
      }

      throw new CartridgeHandlerException(ex);
    }

    return pContentItem;
  }

  /**
   * Logic to re-price order and parse any errors. This method does a
   * priceOrderSubTotal operation.
   * 
   * @throws PricingException
   *   Throws exception if there's any issue while pricing the order.
   */
  public void repriceOrder() throws PricingException {
    Order order = getShoppingCart().getCurrent();

    // Re-price order.
    getPricingTools().performPricingOperation(PricingConstants.OP_REPRICE_ORDER_SUBTOTAL,
      order, getUserPricingModels(), ServletUtil.getUserLocale(), getProfile(), null);
  }
}