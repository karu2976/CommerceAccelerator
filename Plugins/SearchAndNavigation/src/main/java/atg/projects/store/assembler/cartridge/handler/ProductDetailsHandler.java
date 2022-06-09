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

import atg.commerce.inventory.InventoryException;
import atg.core.util.StringUtils;
import atg.endeca.assembler.AssemblerTools;
import atg.nucleus.ResolvingMap;
import atg.nucleus.naming.ParameterName;
import atg.projects.store.assembler.cartridge.ContentItemModifier;
import atg.projects.store.catalog.StoreCatalogProperties;
import atg.projects.store.catalog.StoreCatalogTools;
import atg.projects.store.product.ProductDetailsTools;
import atg.repository.RepositoryItem;
import atg.repository.seo.SEOUtils;
import atg.service.filter.bean.BeanFilterException;
import atg.servlet.ServletUtil;

import com.endeca.infront.assembler.CartridgeHandlerException;
import com.endeca.infront.cartridge.RecordDetails;
import com.endeca.infront.cartridge.RecordDetailsConfig;
import com.endeca.infront.cartridge.RecordDetailsHandler;
import com.endeca.infront.cartridge.model.Attribute;
import com.endeca.infront.cartridge.model.Record;
import com.endeca.infront.navigation.NavigationState;
import com.endeca.infront.navigation.model.FilterState;

import java.util.List;
import java.util.Map;

/**
 * Extension of {@link RecordDetailsHandler}.  This class will return a
 * {@link com.endeca.infront.assembler.ContentItem} that, if configured to will contain a "product"
 * and "SKU" {@code RepositoryItem}.
 *
 * This class will first check the MDEX for the requested product.  If the product exists in the
 * MDEX then it is deemed available and the repository will be queried for the products
 * {@code RepositoryItem}.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/assembler/cartridge/handler/ProductDetailsHandler.java#1 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ProductDetailsHandler extends RecordDetailsHandler {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SearchAndNavigation/src/main/java/atg/projects/store/assembler/cartridge/handler/ProductDetailsHandler.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------

  // Static identifier for a productId.
  public static final ParameterName PRODUCT_ID = ParameterName.getParameterName("productId");

  // Static identifier for a product.  Used a the key for product entries in the {@code ContentItem}.
  public static final String PRODUCT = "product";

  // Static identifier for a product.  Used a the key for a selected SKU entry in the
  // {@code ContentItem}.
  public static final String SELECTED_SKU = "selectedSku";

  // The filterId for the summary bean filter to be used.
  public static final String SUMMARY_FILTER = "summary";

  // Static identifier for SEO meta data.  Used s the key for all SEO meta data that will be added
  // to the {@code ContentItem}.
  public static final String SEO_META_DATA = "metadata";

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------

  //-------------------------------------
  // property: productRepositoryId
  //-------------------------------------
  private String mProductIdPropertyName = "product.repositoryId";

  /**
   * @param pProductIdPropertyName
   *   This property should contain the property name returned by the MDEX that holds a products
   *   Repository Id of a product.  This is used to retrieve the products repository Id from the
   *   MDEX so it can then be used to query the repository.  Defaults to "product.repositoryId"
   */
  public void setProductIdPropertyName(String pProductIdPropertyName) {
    mProductIdPropertyName = pProductIdPropertyName;
  }

  /**
   * @return
   *   Returns the repository's product Id property name so it can be used to query the repository.
   */
  public String getProductIdPropertyName() {
    return mProductIdPropertyName;
  }

  //-------------------------------------
  // property: productDetailsTools
  //-------------------------------------
  private ProductDetailsTools mProductDetailsTools = null;

  /**
   * @param pProductDetailsTools
   *   A tools component for the Product Detail Page.  This includes convenience methods for
   *   retrieving the property details for products and SKUs.
   */
  public void setProductDetailsTools(ProductDetailsTools pProductDetailsTools) {
    mProductDetailsTools = pProductDetailsTools;
  }

  /**
   * @return
   *   Returns the tools component that contains the helper methods for working with the product
   *   detail page.
   */
  public ProductDetailsTools getProductDetailsTools() {
    return mProductDetailsTools;
  }

  //-------------------------------------
  // property: catalogTools
  //-------------------------------------
  private StoreCatalogTools mCatalogTools = null;

  /**
   * @param pCatalogTools
   *   Sets a tools component for working with catalog's.  This includes access to CatalogProperties
   *   and convenience methods for querying the repository.
   */
  public void setCatalogTools(StoreCatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
   * @return
   *   Returns a tools component for working with catalog's.  This includes access to
   *   CatalogProperties and convenience methods for querying the repository.
   */
  public StoreCatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  //----------------------------------------
  //  property: filterId
  //----------------------------------------
  private String mFilterId = "productDetailsProduct";

  /**
   * @param pFilterId
   *   The id of the bean filter for filtering the Product RepositoryItem.
   */
  public void setFilterId(String pFilterId) {
    mFilterId = pFilterId;
  }

  /**
   * @return
   *   The id of the bean filter for filtering the Product RepositoryItem.
   */
  public String getFilterId() {
    return mFilterId;
  }

  //-------------------------------------
  // property: skuTypes
  //-------------------------------------
  private ResolvingMap mSkuTypes = null;

  /**
   * @param pSkuTypes
   *   A {@code ResolvingMap} that holds a map of the stores configured SKU types and any SKU
   *   properties that makes each SKU type unique.  This map should be keyed on the SKU type.
   */
  public void setSkuTypes(ResolvingMap pSkuTypes) {
    mSkuTypes = pSkuTypes;
  }

  /**
   * @return
   *   Returns the {@code ResolvingMap} that holds a map of the stores configured SKU types.
   */
  public ResolvingMap getSkuTypes() {
    return mSkuTypes;
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
  
  //-----------------------------------
  // property : seoUtils
  //-----------------------------------
  private SEOUtils mSeoUtils;
  
  /**
   * @return
   *   SEO utility component that populates the seo meta data.
   */
  public SEOUtils getSeoUtils() {
    return mSeoUtils;
  }

  /**
   * @param pSeoUtils
   *   SEO utility component that populates the seo meta data.
   */
  public void setSeoUtils(SEOUtils pSeoUtils) {
    mSeoUtils = pSeoUtils;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * The default implementation is to return an empty filter state. This means that all records will
   * be returned. We may want to apply default filters such as site, language, etc. In this
   * overridden class we retrieve the filterState from the passed in NavigationState.
   *
   * @param pNavigationState
   *   The current navigation state of the request.
   */
  @Override
  protected FilterState getFilterState(NavigationState pNavigationState) {
    return pNavigationState.getFilterState();
  }

  /**
   * If we don't have a record spec then skip pre-processing.
   *
   * @param pCartridgeConfig
   *   The cartridge configuration returned by {@link com.endeca.infront.assembler.CartridgeHandler}.
   * @throws com.endeca.infront.assembler.CartridgeHandlerException
   */
  @Override
  public void preprocess(RecordDetailsConfig pCartridgeConfig) throws CartridgeHandlerException {

    // Only continue with normal pre-processing if a record spec exists.
    if (mRecordState != null) {
      super.preprocess(pCartridgeConfig);
    }
  }

  /**
   * Execute a MDEX request that returns the record details from the MDEX. Once returned the
   * repository Id is used to retrieve all of the required information from the repository. The
   * record details for both products and SKUs are formatted in such a way that the client will have
   * to perform minimal processing in order to use the provided information.
   *
   * @param pCartridgeConfig
   *   The record details configuration for this request.
   */
  @Override
  public RecordDetails process(RecordDetailsConfig pCartridgeConfig)
    throws CartridgeHandlerException {

    String productId = null;

    if (mRecordState == null) {

      // Get the product id from the URL if the productId query parameter is present.
      productId = ServletUtil.getCurrentRequest().getParameter(PRODUCT_ID);
    }
    else {

      // If the product id was not present in the URL then get it from the record instead.
      RecordDetails recordDetails = super.process(pCartridgeConfig);
      productId = getProductIdFromRecord(recordDetails);
    }

    // Get the product RepositoryItem.
    RepositoryItem product = getProductDetailsTools().getProduct(productId);

    // Copy the contentItem.
    RecordDetails contentItem = new RecordDetails(pCartridgeConfig);

    // Add seo tags meta data for product.
    Map<String,Object> metaData = getSeoUtils().getProductSEOMetaData(productId);
    contentItem.put(SEO_META_DATA, metaData);
    
    try {
      // get the filtered product map.
      Map filteredProductMap =
        getProductDetailsTools().getFilteredProductMap(product, getFilterId(), getSkuTypes());

      // Add the product properties to the ContentItem.
      contentItem.put(PRODUCT, filteredProductMap);

      StoreCatalogProperties catalogProperties =
        (StoreCatalogProperties) getCatalogTools().getCatalogProperties();

      if (product != null) {
        List<RepositoryItem> childSKUs =
          (List<RepositoryItem>) product.getPropertyValue(catalogProperties.getChildSkusPropertyName());

        if (childSKUs != null && childSKUs.size() == 1) {
          Map filteredSkuMap =
            getProductDetailsTools().getFilteredSkuMap(childSKUs.get(0), SUMMARY_FILTER);

          // Add the SKU properties to the ContentItem.
          contentItem.put(SELECTED_SKU, filteredSkuMap);
        }
      }

      // Modify the content of the contentItem via the use of any configured ContentItemModifiers.
      modifyContentItem(contentItem);
    }
    catch (BeanFilterException e) {
      AssemblerTools.getApplicationLogging().vlogError(e,
        "An error occurred while attempting to apply the BeanFilter");
    }

    return contentItem;
  }

  /**
   * This method retrieves the Products repository ID from the RecordDetails passed and returns it
   * as a {@code String}.
   *
   * @param pRecordDetails
   *   The {@code RecordDetails} for the current request.
   * @return
   *   The product ID as a {@code String}.
   */
  public String getProductIdFromRecord(RecordDetails pRecordDetails) {

    String productId = null;

    if (!StringUtils.isBlank(getProductIdPropertyName())) {

      // Check that the product was returned by the MDEX and retrieve its repository Id.
      Record record = pRecordDetails.getRecord();

      if (record != null) {
        Map<String, Attribute> attributes = record.getAttributes();
        Attribute attribute = attributes.get(getProductIdPropertyName());

        if (attribute != null) {
          productId = attribute.toString();
        }
      }
    }
    return productId;
  }

  /**
   * Loop all configured {@code ContentItemModifiers} and execute their modify method against
   * the passed {@code RecordDetails}.
   *
   * @param pRecordDetails
   *   The current ContentItem being processed by the request.
   */
  public void modifyContentItem(RecordDetails pRecordDetails) {

    if (getContentItemModifiers() != null && getContentItemModifiers().length > 0) {
      for (ContentItemModifier contentItemModifier : getContentItemModifiers()) {
        contentItemModifier.modify(pRecordDetails);
      }
    }
  }
}

