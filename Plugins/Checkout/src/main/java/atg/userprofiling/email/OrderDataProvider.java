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

package atg.userprofiling.email;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

import atg.adapter.gsa.GSAItem;
import atg.commerce.CommerceException;
import atg.commerce.fulfillment.SubmitOrder;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.states.ObjectStates;
import atg.core.util.Address;
import atg.multisite.SiteManager;
import atg.nucleus.GenericService;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * This is the OrderDataProvider class that provides the data
 * for order confirmation email to be sent to the user on 
 * successful order submission.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/userprofiling/email/OrderDataProvider.java#1 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class OrderDataProvider extends GenericService implements TemplateDataProvider {

  /** Class version string. */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/userprofiling/email/OrderDataProvider.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------  

  /** Message property name in email parameters. */
  public static final String MESSAGE_PROPERTY_NAME = "message";
  
  /** Address first name property name. */
  public static final String ADDRESS_FIRSTNAME = "firstName";
  
  /** Address last name property name. */
  public static final String ADDRESS_LASTNAME = "lastName";
  
  /** Address address1 property name. */
  public static final String ADDRESS_ADDRESS1 = "address1";
  
  /** Address address2 property name. */
  public static final String ADDRESS_ADDRESS2 = "address2";
  
  /** Address city property name. */
  public static final String ADDRESS_CITY = "city";
  
  /** Address state property name. */
  public static final String ADDRESS_STATE = "state";
  
  /** Address country property name. */
  public static final String ADDRESS_COUNTRY = "country";
  
  /** Address postal code property name. */
  public static final String ADDRESS_POSTAL_CODE = "postalCode";
  
  /** Shipping method property name. */
  public static final String SHIPPING_METHOD = "shippingMethod";
  
  /** Card number property name. */
  public static final String BILLING_CARD_NUMBER = "cardNumber";
  
  /** Card type property name. */
  public static final String BILLING_CARD_TYPE = "cardType";
  
  /** Card expiration month. */
  public static final String  BILLING_CARD_MONTH = "cardExpiryMonth";

  /** Card expiration year. */
  public static final String  BILLING_CARD_YEAR = "cardExpiryYear";  
  
  /** Thumbnail image property name. */
  public static final String COMMERCE_ITEM_IMAGE_NAME = "image";
  
  /** Url property name in thumbnail item. */
  public static final String IMAGE_URL = "url";
  
  /** Repository id property name. */
  public static final String COMMERCE_ITEM_REPOSITORY_ID = "repositoryId";
  
  /** Repository id property name. */
  public static final String REPOSITORY_ID_NAME ="id";
  
  /** Commerce item display name property. */
  public static final String COMMERCE_ITEM_DISPLAY_NAME = "displayName";
  
  /** Item price property name. */
  public static final String COMMERCE_ITEM_PRICE  = "itemPrice";
  
  /** Commerce item total amount property name. */
  public static final String COMMERCE_ITEM_TOTAL_AMOUNT = "totalAmount";
  
  /** Commerce item quantity property name. */
  public static final String COMMERCE_ITEM_QUANTITY = "quantity";
  
  /** Commerce items property name. */
  public static final String COMMERCE_ITEMS = "commerceItems";
  
  /** Shipping groups property name. */
  public static final String SHIPPING_GROUPS = "shippingGroups";
  
  /** Payment groups property name. */
  public static final String PAYMENT_GROUPS = "paymentGroups";
  
  /** Order status property name. */
  public static final String ORDER_STATUS = "status";
  
  /** Order id property name. */
  public static final String ORDER_ID = "orderId";
  
  /** Order submitted date property name. */
  public static final String ORDER_SUBMITTED_DATE = "submittedDate";
  
  /** Order raw subtotal property name. */
  public static final String ORDER_RAW_SUB_TOTAL = "rawSubTotal";
  
  /** Order discounts property name. */
  public static final String ORDER_DISCOUNTS = "discounts";
  
  /** Order shipping price property name. */
  public static final String ORDER_SHIPPING = "shipping";
  
  /** Order tax price property name. */
  public static final String ORDER_TAX = "tax";
  
  /** Order total price property name. */
  public static final String ORDER_TOTAL = "total";
  
  /** Site url. **/
  public static final String SITE_URL = "siteUrl";
  
  /** Site name property name. */
  public static final String SITE_NAME = "siteName";
  
  /** Site logo url. */
  public static final String SITE_LOGO_URL = "siteLogoUrl";
  
  /** Site production url property name. */
  public static final String PRODUCTION_URL_PROPERTY = "productionURL";
  
  /** Site name property name. */
  public static final String SITE_NAME_PROPERTY = "name";
    
  /** Http scheme. */
  public static final String HTTP_SCHEME = "http";
  
  /** Scheme and host separator. */
  public static final String SCHEME_HOST_SEPARATOR = "://";
  
  /** Host , port separator. */
  public static final String HOST_PORT_SEPARATOR = ":";
  
  /** From Email. */
  public static final String EMAIL_FROM = "emailFrom";
  
  /** Order confirmation from property name. */
  public static final String ORDER_CONFIRMATION_FROM = "orderConfirmationFromAddress";
  
  /** Site configuration item descriptor name. */
  public static final String SITE_CONFIGURATION = "siteConfiguration";
  
  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------
    
  //-----------------------------------
  // property: templateName
  //-----------------------------------
  public String mTemplateName = null;
  
  /**
   * @return
   *   Returns order details template name.
   */
  @Override
  public String getTemplateName() {
    return mTemplateName;
  }

  /**
   * @param pTemplateName
   *   Set the order details template name.
   */
  public void setTemplateName(String pTemplateName) {
    mTemplateName = pTemplateName;
  }
  
  //-----------------------------------
  // property: templateResourcesPath
  //-----------------------------------
  public String mTemplateResourcesPath = null;
  
  /**   
   * @return
   *   Returns the freemarker email template resources path.
   */
  @Override
  public String getTemplateResourcesPath() {
    return mTemplateResourcesPath;
  }

  /**
   * @param pTemplateResourcesPath
   *   Set template resources path.
   */
  public void setTemplateResourcesPath(String pTemplateResourcesPath) {
    mTemplateResourcesPath = pTemplateResourcesPath;
  }  
  
  //-----------------------------------
  // property: siteServerName
  //-----------------------------------
  public String mSiteServerName = null;
  
  /**
   * @return
   *   Returns the server name.
   */
  public String getSiteServerName() {
    return mSiteServerName;
  }
  
  /**
   * @param pSiteServerName
   *   Sets the site server name.
   */
  public void setSiteServerName(String pSiteServerName) {
    mSiteServerName = pSiteServerName;
  }
  
  //-----------------------------------
  // property: siteServerPort
  //-----------------------------------
  public int mSiteServerPort = 0;
  
  /**
   * @return
   *   Returns the server port.
   */
  public int getSiteServerPort() {
    return mSiteServerPort;
  }
  
  /**
   * @param pSiteServerPort
   *   Sets the site server port.
   */
  public void setSiteServerPort(int pSiteServerPort) {
    mSiteServerPort = pSiteServerPort;
  }

  //-----------------------------------
  // property: states
  //-----------------------------------

  private ObjectStates mStates = null;

  /**
   * @param pStates
   *   Sets the objectStates that contains the raw and translated state values.
   */
  public void setStates(ObjectStates pStates) {
    mStates = pStates;
  }

  /**
   * @return
   *   Returns the objectStates that contains the raw and translated state values.
   */
  public ObjectStates getStates() {
    return mStates;
  }

  //-----------------------------------
  // property: orderManager
  //-----------------------------------
  
  private OrderManager mOrderManager = null;
  
  /**
   * @param pOrderManager 
   *   Sets the property orderManager.
   */
  public void setOrderManager(OrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }
  
  /**
   * @return
   *   Returns the orderManager property which is a component that is
   *   useful for many order management functions.
   */
  public OrderManager getOrderManager() {
    return mOrderManager;
  }
  
  //-----------------------------------
  // property: siteRepository
  //-----------------------------------
  public Repository mSiteRepository = null;
  
  /**
   * @param pSiteRepository
   *   Sets site repository.
   */
  public void setSiteRepository(Repository pSiteRepository) {
    mSiteRepository = pSiteRepository;
  }
  
  /**
   * @return
   *   Returns site repository.
   */
  public Repository getSiteRepository(){
    return mSiteRepository;
  }
  
  //-----------------------------------
  // property: commerceItemImagePropertyName
  //-----------------------------------
  private String mCommerceItemImagePropertyName = null;
  
  /**
   * @param pCommerceItemImagePropertyName
   *   Sets the commerce item image property name.
   */
  public void setCommerceItemImagePropertyName(String pCommerceItemImagePropertyName) {
    mCommerceItemImagePropertyName = pCommerceItemImagePropertyName;
  }
  
  /**
   * @return
   *   Returns the commerce item image property name.
   */
  public String getCommerceItemImagePropertyName() {
    return mCommerceItemImagePropertyName;
  }
  
  //-----------------------------------
  // property: 
  //-----------------------------------
  private String mSiteLogoUrl = null;
  
  /**
   * @param pSiteLogoUrl
   *   Site logo url.
   */
  public void setSiteLogoUrl(String pSiteLogoUrl) {
    mSiteLogoUrl = pSiteLogoUrl;
  }
  
  /**
   * @return
   *   Returns the site logo url.
   */
  public String getSiteLogoUrl() {
    return mSiteLogoUrl;
  }
  
  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------
  
  /** 
   * Converts the given raw state value into its readable string. The readable
   * value will be localized if useResourcedStateDescriptions is true.
   *  
   * @param pState
   *   Order raw state.
   * @return 
   *   Returns readable oder state value.
   */
  public String getStateDetail(int pState) {

    ObjectStates states = getStates();
    if (states == null) {
      return null;
    }

    return getStateDetail(states.getStateString(pState));
  }
  
  /** 
   * Converts the given raw state value into its readable string. The readable
   * value will be localized if useResourcedStateDescriptions is true.
   *  
   * @param pState 
   *   Order raw state.
   * @return 
   *   Returns readable order state value.
   */
  public String getStateDetail(String pState) {

    ObjectStates states = getStates();
    if (states == null) {
      return null;
    }

    try {
      return states.getStateDescriptionAsUserResource(pState);
    } 
    catch (MissingResourceException e) {
      if (isLoggingError()) {
        logError("Error while fetching localized order state description from states", e);
      }
    }
    return null;
  }
  
  /**
   * Converts the given raw state value into its readable string.
   * 
   * @param pOrder The order whose state to convert to readable text.
   * @return 
   *   The readable state value.
   */
  public String getStateDetail(Order pOrder) {

    if (getStates() == null) {
      return null;
    }

    // Figure out if the state is a string or integer.
    String detailedState = null;

    try {
      // First try integer.
      detailedState = getStateDetail(pOrder.getState());
    }
    catch (NumberFormatException e1){
      // Must be a string state.
      detailedState = getStateDetail(Integer.toString(pOrder.getState()));
    }

    return detailedState;
  }

  /**
   * Returns the from email id for current event for site passed.
   * 
   * @param pSiteId
   *   The current site id.
   * @return 
   *   from email id for current event and passed site id.
   */
  @Override
  public String getEmailFrom(String pSiteId) {
    try {
      RepositoryItem site = SiteManager.getSiteManager().getSite(pSiteId);
      if (site != null) {
        return (String) site.getPropertyValue(ORDER_CONFIRMATION_FROM);
      }
    }
    catch (RepositoryException ex){
      if (isLoggingError()) {
        logError("Error while accessing site repository", ex);
      } 
    }

    return null;
  }

  /**
   * This method creates the template data by processing the order from template email
   * parameters and creates a map to populate the email template.
   * 
   * @param pEmailParameters
   *   Template email parameters.
   */
  @Override
  public Map<String, Object> processTemplateData(Map pEmailParameters) {

    Map<String, Object> orderData = new HashMap<>();
    SubmitOrder orderMessage = (SubmitOrder) pEmailParameters.get(MESSAGE_PROPERTY_NAME);

    if (orderMessage != null) {
      Order order = orderMessage.getOrder();

      if (order != null) {
        try{
          RepositoryItem site = getSiteRepository().getItem(order.getSiteId(), SITE_CONFIGURATION);

          if (site != null) {
            orderData.put(SITE_URL, getHttpServerUrl() + site.getPropertyValue(PRODUCTION_URL_PROPERTY));
            orderData.put(SITE_NAME, site.getPropertyValue(SITE_NAME_PROPERTY));
            orderData.put(SITE_LOGO_URL, getHttpServerUrl() + getSiteLogoUrl());
            orderData.put(EMAIL_FROM, site.getPropertyValue(ORDER_CONFIRMATION_FROM));
          }
        }
        catch(RepositoryException exception){
          if (isLoggingError()) {
            logError("Error while accessing site repository", exception);
          }
        }

        // Adds the shipping groups data.
        List<ShippingGroup> shippingGroups = order.getShippingGroups();
        List shippingGroupList = new ArrayList();

        for (ShippingGroup shippingGroup : shippingGroups) {
          shippingGroupList.add(getShippingGroupData(shippingGroup));
        }

        // Adds the payment groups data.
        List<PaymentGroup> paymentGroups = order.getPaymentGroups();
        List paymentGroupList = new ArrayList();

        for (PaymentGroup paymentGroup : paymentGroups) {
          paymentGroupList.add(getPaymentGroupData(paymentGroup));
        }

        // Adds the commerce items data.
        List<CommerceItem> commerceItems = order.getCommerceItems();
        List commerceItemList = new ArrayList<>();

        for (CommerceItem commerceItem : commerceItems) {
          commerceItemList.add(getCommerceItemData(commerceItem));
        }

        // Adds the order pricing.
        OrderPriceInfo orderPrice = order.getPriceInfo();

        orderData.put(COMMERCE_ITEMS, commerceItemList);
        orderData.put(SHIPPING_GROUPS, shippingGroupList);
        orderData.put(PAYMENT_GROUPS, paymentGroupList);
        orderData.put(ORDER_STATUS, getStateDetail(order));
        orderData.put(ORDER_ID, order.getId());
        orderData.put(ORDER_SUBMITTED_DATE, order.getSubmittedDate());
        orderData.put(ORDER_RAW_SUB_TOTAL, orderPrice.getRawSubtotal());
        orderData.put(ORDER_DISCOUNTS, orderPrice.getDiscountAmount());
        orderData.put(ORDER_SHIPPING, orderPrice.getShipping());
        orderData.put(ORDER_TAX, orderPrice.getTax());
        orderData.put(ORDER_TOTAL, orderPrice.getTotal());
      }
    }

    return orderData;
  }
  
  
  /**
   * This method returns the shipping group data in the form of a map for the 
   * given shipping group.
   * 
   * @param pShippingGroup
   *   Shipping group.
   * @return
   *   Returns the shipping group data in the form of map.
   */
  public Map<String, String> getShippingGroupData(ShippingGroup pShippingGroup) {

    Map<String, String> shippingGroupMap = new HashMap<>(9);
    
    if (pShippingGroup instanceof HardgoodShippingGroup) {
      Address address = ((HardgoodShippingGroup)pShippingGroup).getShippingAddress();
      
      shippingGroupMap.put(ADDRESS_FIRSTNAME, address.getFirstName());
      shippingGroupMap.put(ADDRESS_LASTNAME, address.getLastName());
      shippingGroupMap.put(ADDRESS_ADDRESS1, address.getAddress1());
      shippingGroupMap.put(ADDRESS_ADDRESS2, address.getAddress2());
      shippingGroupMap.put(ADDRESS_CITY, address.getCity());
      shippingGroupMap.put(ADDRESS_STATE, address.getState());
      shippingGroupMap.put(ADDRESS_COUNTRY, address.getCountry());
      shippingGroupMap.put(ADDRESS_POSTAL_CODE, address.getPostalCode());
      shippingGroupMap.put(SHIPPING_METHOD, pShippingGroup.getShippingMethod());
    }
    return shippingGroupMap;
  }
  
  /**
   * This method returns the payment group data in the form of a map for the 
   * given payment group.
   * 
   * @param pPaymentGroup
   *   Payment group.
   * @return
   *   Returns the payment group data in the form of map.
   */
  public Map<String,String> getPaymentGroupData(PaymentGroup pPaymentGroup) {

    Map<String, String> paymentGroupMap = new HashMap<>(12);
    
    if (pPaymentGroup instanceof CreditCard){
      Address address = ((CreditCard) pPaymentGroup).getBillingAddress();
      
      paymentGroupMap.put(ADDRESS_FIRSTNAME, address.getFirstName());
      paymentGroupMap.put(ADDRESS_LASTNAME, address.getLastName());
      paymentGroupMap.put(ADDRESS_ADDRESS1, address.getAddress1());
      paymentGroupMap.put(ADDRESS_ADDRESS2, address.getAddress2());
      paymentGroupMap.put(ADDRESS_CITY, address.getCity());
      paymentGroupMap.put(ADDRESS_STATE, address.getState());
      paymentGroupMap.put(ADDRESS_COUNTRY, address.getCountry());
      paymentGroupMap.put(ADDRESS_POSTAL_CODE, address.getPostalCode());

      String creditCardNumber = ((CreditCard)pPaymentGroup).getCreditCardNumber();

      paymentGroupMap.put(BILLING_CARD_NUMBER, creditCardNumber.substring(creditCardNumber.length() - 4));
      paymentGroupMap.put(BILLING_CARD_TYPE, ((CreditCard) pPaymentGroup).getCreditCardType());
      paymentGroupMap.put(BILLING_CARD_MONTH, ((CreditCard) pPaymentGroup).getExpirationMonth());
      paymentGroupMap.put(BILLING_CARD_YEAR, ((CreditCard) pPaymentGroup).getExpirationYear());
    }    
    return paymentGroupMap;
  }
  
  /**
   * This method returns the commerce item data in the form of a map for the 
   * given commerce item. 
   * 
   * @param pCommerceItem
   *   Commerce item.
   * @return
   *   Returns the commerce item data in the form of map.
   */
  public Map<String, Object> getCommerceItemData(CommerceItem pCommerceItem) {

    GSAItem product = (GSAItem) pCommerceItem.getAuxiliaryData().getProductRef();
    GSAItem sku = (GSAItem) pCommerceItem.getAuxiliaryData().getCatalogRef();
    GSAItem media = (GSAItem) product.getPropertyValue(getCommerceItemImagePropertyName());

    Map<String, Object> currentCommerceItem = new HashMap<>();
    currentCommerceItem.put(COMMERCE_ITEM_IMAGE_NAME, media.getPropertyValue(IMAGE_URL));
    currentCommerceItem.put(COMMERCE_ITEM_REPOSITORY_ID, sku.getPropertyValue(REPOSITORY_ID_NAME));
    currentCommerceItem.put(COMMERCE_ITEM_DISPLAY_NAME, product.getPropertyValue(COMMERCE_ITEM_DISPLAY_NAME));

    ItemPriceInfo priceInfo = pCommerceItem.getPriceInfo();

    if (priceInfo != null) {
      if (priceInfo.isOnSale()) {
        currentCommerceItem.put(COMMERCE_ITEM_PRICE, priceInfo.getSalePrice());
      } else {
        currentCommerceItem.put(COMMERCE_ITEM_PRICE, priceInfo.getListPrice());
      }
      currentCommerceItem.put(COMMERCE_ITEM_TOTAL_AMOUNT, priceInfo.getAmount());
    }
    currentCommerceItem.put(COMMERCE_ITEM_QUANTITY, pCommerceItem.getQuantity());

    return currentCommerceItem;
  }
  
  /**
   * This method returns the http url for the server.
   * 
   * @return
   *   Returns the http URL.
   */
  public String getHttpServerUrl() {
    return HTTP_SCHEME + SCHEME_HOST_SEPARATOR + getSiteServerName() + HOST_PORT_SEPARATOR
      + getSiteServerPort();
  }  
}