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

package atg.projects.store.order.purchase;

import java.beans.IntrospectionException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.purchase.PurchaseProcessHelper;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.pricing.PricingModelHolder;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.payment.creditcard.ExtendableCreditCardTools;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.profile.StoreCheckoutPropertyManager;
import atg.projects.store.shipping.ShippingConfiguration;
import atg.projects.store.userprofiling.StoreProfilePropertyManager;
import atg.projects.store.userprofiling.StoreProfileTools;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.RunProcessException;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.address.AddressTools;

/**
 * This is a helper class. It includes helper methods for validating shipping as well as billing information.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/order/purchase/StorePurchaseProcessHelper.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StorePurchaseProcessHelper extends PurchaseProcessHelper {
  
  /** Class version string. */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/order/purchase/StorePurchaseProcessHelper.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /** Error key when the user input causes card validation to throw an exception. */
  protected static final String INVALID_CARD = "invalidCard";

  /** Error key when the user input causes address validation to throw an exception. */
  protected static final String INVALID_ADDRESS = "invalidAddress";

  /** Error key for a required credit card property is invalid. */
  protected static final String CC_PROPERTY_INVALID = "invalidCreditCardProperty";
  
  /** Error key for verification number. */
  protected static final String VERIFICATION_NUMBER_INVALID = "invalidCreditCardVerificationNumber";
  
  /** Error key for a required credit card property is missing. */
  public static final String MSG_MISSING_CC_PROPERTY = "missingCreditCardProperty";

  /** Error key for a required address property is missing. */
  public static final String MSG_MISSING_ADDRESS_PROPERTY = "missingAddressProperty";

  /** Error key for a duplicate credit card nickname. */
  public static final String MSG_DUPLICATE_CC_NICKNAME = "errorDuplicateCCNickname";
  
  /** Error key for a duplicate nickname. */
  public static final String MSG_DUPLICATE_NICKNAME = "errorDuplicateNickname";

  /** Error key for multiple coupons per order. */
  public static final String MSG_MULTIPLE_COUPONS_PER_ORDER = "multipleCouponsPerOrder";

  /** Error key for unclaimable coupon. */
  public static final String MSG_UNCLAIMABLE_COUPON = "couponNotClaimable";
  
  /** Error key for invalid state for method. */
  public static final String MSG_INVALID_STATE_FOR_METHOD = "invalidStateForMethod";
  
  /** Error key for product display name. */
  public static final String DISPLAY_NAME_PROPERTY_NAME = "displayName";
  
  /** Error key for invalid quantity for commerce item. */
  public static final String MSG_INVALID_QUANTITY = "invalidQuantity";
  
  /** Error key for item out of stock for shipping. */
  public static final String ITEM_OUT_OF_STOCK = "itemOutOfStock";
  
  /** Error key for item more than available quantity. */
  public static final String ITEM_MORE_THAN_AVAILABLE_QUANTITY = "itemMoreThanAvailableQuantity";
  
  /** Constant for unlimited sku stock level. */
  public static final long UNLIMITED_SKU_STOCK_LEVEL = -1;
  
  /** Constant for product not found in repository. */
  public static final String PRODUCT_NOT_FOUND = "productNotFound";
  
  /** Error key for invalid quantity for commerce item. */
  public static final String MSG_INVALID_PRODUCT_ID = "invalidProductId";
  
  /** Error key for invalid quantity for commerce item. */
  public static final String MSG_INVALID_SKU_ID = "invalidSkuId";
  
  /** Error key for unlimited sku stock level. */
  public static final String SKU_STOCK_LEVEL_UNLIMITED = "unlimitedSkuStockLevel";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------
  
  //-----------------------------------
  // property: claimableManager
  //-----------------------------------
  private ClaimableManager mClaimableManager = null;

  /**
   * @return
   *   Set the claimable manager component.
   */
  public ClaimableManager getClaimableManager() {
    return mClaimableManager;
  }

  /**
   * @param pClaimableManager
   *   Returns the claimable manager component.
   */
  public void setClaimableManager(ClaimableManager pClaimableManager) {
    mClaimableManager = pClaimableManager;
  }
  
  //-----------------------------------
  // property: invalidStatesForShipMethod.
  //-----------------------------------
  private Properties mInvalidStatesForShipMethod = null;

  /**
   * @param pInvalidStatesForShipMethod
   *   The invalid states for shipping methods. Expects a pipe-delimited string 
   *   containing 2-digit state abbreviations that are invalid for a particular 
   *   shipping method. The key is the name of the shipping method the value is 
   *   the pipe-delimited list of states.
   */
  public void setInvalidStatesForShipMethod(Properties pInvalidStatesForShipMethod) {
    mInvalidStatesForShipMethod = pInvalidStatesForShipMethod;
  }

  /**
   * @return
   *   The invalid states for shipping method. Expects a pipe-delimited string  
   *   containing 2-digit state  abbreviations that are invalid for a particular 
   *   shipping method. The key is the name of the shipping method the value is 
   *   the pipe-delimited list of states.
   */
  public Properties getInvalidStatesForShipMethod() {
    return mInvalidStatesForShipMethod;
  }
  
  //-----------------------------------
  // property: resourceBundleName
  //-----------------------------------
  private String mResourceBundleName = "atg.commerce.order.purchase.UserMessages";

  /**
   * @param pResourceBundleName 
   *   Set a resource bundle for localizing errors.
   */
  public void setResourceBundleName(String pResourceBundleName) {
    mResourceBundleName = pResourceBundleName;
  }

  /**
   * @return
   *   Returns resource bundle for localizing errors.
   */
  public String getResourceBundleName() {
    return mResourceBundleName;
  }
 
  //-----------------------------------
  // property: storeProfileTools
  //-----------------------------------
  private StoreProfileTools mStoreProfileTools = null;
 
  /**
   * @return
   *   Returns the profile tools component.
   */
  public StoreProfileTools getStoreProfileTools() {
    return mStoreProfileTools;
  }

  /**
   * @param pStoreProfileTools
   *   Set the profile tools component.
   */
  public void setStoreProfileTools(StoreProfileTools pStoreProfileTools) {
    mStoreProfileTools = pStoreProfileTools;
  }
  
  //-----------------------------------
  // property: checkoutPropertyManager
  //-----------------------------------
  private StoreCheckoutPropertyManager mCheckoutPropertyManager = null;

  /**
   * @param pCheckoutPropertyManager 
   *   Set a component used to manage property names for the checkout.
   */
  public void setCheckoutPropertyManager(StoreCheckoutPropertyManager pCheckoutPropertyManager) {
    mCheckoutPropertyManager = pCheckoutPropertyManager;
  }

  /**
   * @return
   *   Returns a component used to manage property names for the checkout.
   */
  public StoreCheckoutPropertyManager getCheckoutPropertyManager() {
    return mCheckoutPropertyManager;
  }
  
  //-----------------------------------
  // property: shippingConfiguration
  //-----------------------------------
  private ShippingConfiguration mShippingConfiguration = null;

  /**
   * @return
   *   Returns a shipping configuration component.
   */
  public ShippingConfiguration getShippingConfiguration() {
    return mShippingConfiguration;
  }

  /**
   * @param pShippingConfiguration
   *   Set the shipping configuration component.
   */
  public void setShippingConfiguration(ShippingConfiguration pShippingConfiguration) {
    mShippingConfiguration = pShippingConfiguration;
  }
  
  //-----------------------------------
  // property: creditCardTools
  //-----------------------------------
  private ExtendableCreditCardTools mCreditCardTools = null;

  /**
   * @return
   *   Returns the credit card tools component. Which contains methods useful 
   *   in dealing with credit cards.
   */
  public ExtendableCreditCardTools getCreditCardTools() {
    return mCreditCardTools;
  }

  /**
   * @param pCreditCardTools 
   *   Sets the credit card tools component. Which contains methods useful 
   *   in dealing with credit cards.
   */
  public void setCreditCardTools(ExtendableCreditCardTools pCreditCardTools) {
    mCreditCardTools = pCreditCardTools;
  }
  
  //-----------------------------------
  // property: inventoryManager
  //-----------------------------------
  private InventoryManager mInventoryManager = null;

  /**
   * @param pInventoryManager
   *   Set a component used to manage inventory level of commerce items.
   */
  public void setInventoryManager(InventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }

  /**
   * @return
   *   Returns a component used to manage inventory level of commerce items.
   */
  public InventoryManager getInventoryManager() {
    return mInventoryManager;
  }
  
  //-----------------------------------
  // property: catalogTools
  //-----------------------------------
  private CatalogTools mCatalogTools = null;

  /**
   * @param pCatalogTools
   *   Sets property catalog tools component.
   */
  public void setCatalogTools(CatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
   * @return
   *   Returns the catalog tools component.
   */
  public CatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------
  
  /**
   * This method will ensure the user isn't trying to use a shipping method 
   * that isn't valid for the state. For example, Express shipping to Alaska 
   * is not allowed.
   *
   * @param pAddress
   *   Address to validate against the shipping method.
   * @param pShippingMethod
   *   Shipping method to validate address.
   * @param pValidationErrors
   *   An optional map that's populated when an error validating occurs.
   * @return
   *   If shipping method validation against address passed it returns true otherwise false.
   */
  public boolean validateShippingMethod(Address pAddress, String pShippingMethod,
      Map<String, String> pValidationErrors) {

    // Check the state entered in the address against the shipping method.
    String shippingState = pAddress.getState();

    vlogDebug("Checking to see if state {0} is valid for method {1}", shippingState,
      pShippingMethod);

    Properties properties = getInvalidStatesForShipMethod();

    if ((properties != null) && !StringUtils.isEmpty(shippingState) && (pShippingMethod != null)) {
      
      // A pipe-delimited list of states should be a safe place to just do an indexOf check.
      String invalidStates = properties.getProperty(pShippingMethod);

      if (!StringUtils.isEmpty(invalidStates)) {
        if (invalidStates.contains(shippingState)) {
          vlogDebug("Found invalid state {0} for method {1}", shippingState, pShippingMethod);

          if (pValidationErrors != null) {
            pValidationErrors.put(MSG_INVALID_STATE_FOR_METHOD,
              getResourceFromBundle(MSG_INVALID_STATE_FOR_METHOD,
                new String[] { pShippingMethod, shippingState }));
          }
          return false;
        }
      }
    }
    return true;
  }
  
  /**
   * Logic to re-price order and parse any errors. This method does a priceOrderTotal operation.
   *
   * @param pOrder
   *   The order to be re-priced.
   * @param pUserLocale
   *   The locale of the user - may be null.
   * @param pProfile
   *   The user profile repository item - may be null.
   * @param pUserPricingModels
   *   Object that contains all the pricing models associated with a user 
   *   i.e. item, shipping, order and tax.
   * @throws RunProcessException
   *   If an error occurs.
   */
  public void repriceOrder(String pRepriceOrderChainId, String pPricingConstant, Order pOrder,
    PricingModelHolder pUserPricingModels, Locale pUserLocale, RepositoryItem pProfile)
      throws RunProcessException {

    if (isLoggingDebug()) {
      logDebug("Repricing with 'priceOrderTotal' operation.");
    }

    super.runRepricingProcess(pRepriceOrderChainId, pPricingConstant, pOrder, pUserPricingModels,
      pUserLocale, pProfile, null);
  }
  
  /**
   * Copy over immutable card properties. By default that is the credit card number and the credit
   * card type.
   *
   * @param pCard 
   *   The card repository item.
   * @param pCardProperties 
   *   Mutable card properties.
   */
  public void copyImmutableCardProperties(RepositoryItem pCard,
      Map<String, Object> pCardProperties) {

    StoreProfilePropertyManager propertyManager =
      getStoreProfileTools().getProfilePropertyManager();

    // Copy over the cards immutable properties. By default the immutable
    // properties are the credit card number and the credit card type.
    String cardNumProp = propertyManager.getCreditCardNumberPropertyName();
    pCardProperties.put(cardNumProp, pCard.getPropertyValue(cardNumProp));

    String cardTypeProp = propertyManager.getCreditCardTypePropertyName();
    pCardProperties.put(cardTypeProp, pCard.getPropertyValue(cardTypeProp));
  }
    
  /**
   * This method should be wrapped in a transaction by the invoking code. E.g FormHandler. 
   * If save credit card will be true then this method does necessary processing to save
   * credit card to profile. The credit card is passed in as a map, the keys are the credit
   * card property names and the values are the credit card values. It's assumed that validation
   * has already been done on the values passed within the Map except validation for nick name.
   * This method validates nick name if validation passed then it adds that credit card to the
   * profile.
   *
   * @param pProfile
   *   User profile object.
   * @param pCreditCardUserInputValues
   *   Map of credit card property values.
   * @param pValidationErrors
   *   An optional map that's populated when an error validating occurs.
   * @throws PropertyNotFoundException
   *   If an error occurs. 
   * @throws IntrospectionException
   *   If an error occurs.
   * @throws RepositoryException
   *   If an error occurs.
   * @throws IllegalAccessException
   *   If an error occurs. 
   * @throws InstantiationException
   *   If an error occurs.
   * @throws ClassNotFoundException
   *   If an error occurs.
   * @return
   *   If credit card nickname validation passed it returns true otherwise false.
   */
  public boolean addCreditCardToProfile(Profile pProfile,
    Map<String, Object> pCreditCardUserInputValues, Map<String, String> pValidationErrors)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException,
        RepositoryException, IntrospectionException, PropertyNotFoundException {

    if (!validateCreditCardNickname(pCreditCardUserInputValues, pValidationErrors)) {
      return false;
    }
 
    // Based on parameter passed it saves credit card and address to profile.
    Object saveCreditCard = pCreditCardUserInputValues
      .get(getCheckoutPropertyManager().getSaveCreditCardPropertyName());
    
    if (saveCreditCard instanceof String && ((String)saveCreditCard).equalsIgnoreCase("true")) {
      getStoreProfileTools().createProfileCreditCardAndAddress(pProfile,
        pCreditCardUserInputValues);
    }

    return true;
  }
  
  /**
   * This method should be wrapped in a transaction by the invoking code. E.g FormHandler. 
   * If save shipping address will be true then this method does necessary processing to save
   * address to profile. The address is passed in as a map, the keys are the address property
   * names and the values are the address values. It's assumed that validation has already been
   * done on the values passed within the Map except validation for nick name. This method
   * validates nick name if validation passed then it adds that address to the profile.
   *
   * @param pProfile
   *   User profile object.
   * @param pAddressUserInputValues
   *   Map of address property values.
   * @param pValidationErrors
   *   An optional map that's populated when an error validating occurs.
   * @throws IntrospectionException
   *   If an error occurs.
   * @throws RepositoryException
   *   If an error occurs.
   * @throws IllegalAccessException
   *   If an error occurs. 
   * @throws InstantiationException
   *   If an error occurs.
   * @throws ClassNotFoundException
   *   If an error occurs.
   * @return
   *   If address nickname validation passed it returns true otherwise false.
   */
  public boolean addAddressToProfile(Profile pProfile, Map<String, Object> pAddressUserInputValues,
    Map<String, String> pValidationErrors) throws ClassNotFoundException, InstantiationException,
      IllegalAccessException, RepositoryException, IntrospectionException {

    if (!validateAddressNickname(pAddressUserInputValues, pValidationErrors)) {
      return false;
    }

    // Save shipping address to profile.
    Object saveShippingAddress = pAddressUserInputValues
      .get(getCheckoutPropertyManager().getSaveShippingAddressPropertyName());

    if (saveShippingAddress instanceof String
        && ((String)saveShippingAddress).equalsIgnoreCase("true")) {
      getStoreProfileTools().addAddress(pProfile, pAddressUserInputValues);
    }

    return true;
  }
  
  /**
   * If save credit card will be true then this method does necessary processing to validate
   * the credit card nick name. The credit card is passed in as a map, the keys are the credit
   * card property names and the values are the credit card values. It's assumed that validation
   * has already been done on the values passed within the Map except validation for nick name.
   * This method validates the nick name.
   *
   * @param pCreditCardUserInputValues
   *   Map of credit card property values.
   * @param pValidationErrors
   *   An optional map that's populated when an error validating occurs.
   * @return
   *   If credit card nickname validation passed it returns true otherwise false.
   */
  public boolean validateCreditCardNickname(Map<String, Object> pCreditCardUserInputValues,
    Map<String, String> pValidationErrors) {

    StoreProfileTools profileTools = getStoreProfileTools();
    StoreProfilePropertyManager profilePropertyManager = profileTools.getProfilePropertyManager();

    // It validates credit card nick name. If nick name empty then throws an exception.
    // If nick name already present in the list of saved credit cards and
    // saved addresses it throws a form exception.
    Object saveCreditCard = pCreditCardUserInputValues
      .get(getCheckoutPropertyManager().getSaveCreditCardPropertyName());
    
    if (saveCreditCard instanceof String && ((String)saveCreditCard).equalsIgnoreCase("true")) {

      // Check if the credit card nick name is available.
      Object cardNickname =
        pCreditCardUserInputValues.get(profilePropertyManager.getCreditCardNicknamePropertyName());

      if (!(cardNickname instanceof String) || ((String) cardNickname).isEmpty()) {
        if (pValidationErrors != null) {
          pValidationErrors.put(MSG_MISSING_CC_PROPERTY,
            getResourceFromBundle(MSG_MISSING_CC_PROPERTY, new String[] { (String)cardNickname }));
        }
        return false;
      }
      else {
  
        // Validates credit card nick name based on whether it already exists in list
        // of saved credit card or not.
        if (!profileTools.isValidCreditCardNickname((String) cardNickname)) {
          if (pValidationErrors != null) {
            pValidationErrors.put(MSG_DUPLICATE_CC_NICKNAME,
              getResourceFromBundle(MSG_DUPLICATE_CC_NICKNAME,
                new String[] { (String)cardNickname }));
          }
          return false;
        }
      }
    }

    return true;
  }
  
  /** 
   * If save shipping address will be true then this method does necessary processing to
   * validate the nick name. The address is passed in as a map, the keys are the address property
   * names and the values are the address values. It's assumed that validation has already been
   * done on the values passed within the Map except validation for nick name. This method
   * validates the nick name.
   *
   * @param pAddressUserInputValues
   *   Map of address property values.
   * @param pValidationErrors
   *   An optional map that's populated when an error validating occurs.
   * @return
   *   If address nickname validation passed it returns true otherwise false.
   */
  public boolean validateAddressNickname(Map<String, Object> pAddressUserInputValues,
    Map<String, String> pValidationErrors) {

    StoreProfileTools profileTools = getStoreProfileTools();
    StoreProfilePropertyManager profilePropertyManager = profileTools.getProfilePropertyManager();

    // Save shipping address to profile.
    Object saveShippingAddress = pAddressUserInputValues
      .get(getCheckoutPropertyManager().getSaveShippingAddressPropertyName());

    if (saveShippingAddress instanceof String &&
        ((String)saveShippingAddress).equalsIgnoreCase("true")) {

      // Checking address nick name is available.
      Object addressNickname =
        pAddressUserInputValues.get(profilePropertyManager.getAddressNicknamePropertyName());

      if (!(addressNickname instanceof String) || ((String) addressNickname).isEmpty()) {
        if (pValidationErrors != null) {
          pValidationErrors.put(MSG_MISSING_ADDRESS_PROPERTY, 
            getResourceFromBundle(MSG_MISSING_ADDRESS_PROPERTY,
              new String[] { (String)addressNickname }));
        }
        return false;
      }
      else {

        // Validates shipping address nick name based on whether 
        // it's already exists in list of saved addresses or not.
        if (!profileTools.isValidAddressNickname((String)addressNickname)) {
          if (pValidationErrors != null) {
            pValidationErrors.put(MSG_DUPLICATE_NICKNAME, 
              getResourceFromBundle(MSG_DUPLICATE_NICKNAME,
                new String[] { (String)addressNickname }));
          }
          return false;
        }
      }
    }

    return true;
  }
  
  /**
   * This method will validate the shipping address information entered by the user.
   * If the information is valid, it will be added to the shipping group.
   * 
   * @param pOrder
   *   The current order.
   * @param pShippingMethod
   *   Shipping method to validate address.
   * @param pAddressUserInputValues
   *   Map of address property values.
   * @param pValidationErrors
   *   An optional map that's populated when an error validating occurs.
   * @throws IntrospectionException
   *   If an error occurs.
   * @throws ClassNotFoundException
   *   If an error occurs.
   * @throws IllegalAccessException 
   *   If an error occurs.
   * @throws InstantiationException
   *   If an error occurs.
   * @throws CommerceException 
   *   If an error occurs.
   * @return 
   *   If shipping details validation passes it returns true otherwise false.
   */
  public boolean validateAndApplyShippingInformation(Order pOrder, String pShippingMethod,
    Map<String, Object> pAddressUserInputValues, Map<String, String> pValidationErrors)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException,
        IntrospectionException, CommerceException {

    StoreProfileTools profileTools = getStoreProfileTools();

    // Validate the address data entered by the user.
    if (!profileTools.validateAddress(pAddressUserInputValues, pValidationErrors)) {
      return false;
    }

    // Create an address object from the values user entered.
    Address addressObject = AddressTools.createAddressFromMap(pAddressUserInputValues,
      profileTools.getShippingAddressClassName());
     
    // Validate shipping method against shipping address.
    if (!validateShippingMethod(addressObject, pShippingMethod, pValidationErrors)) {
      return false;  
    }

    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    ShippingGroup shippingGroup = orderTools.getShippingGroup(pOrder);

    // Set shipping method.
    shippingGroup.setShippingMethod(pShippingMethod);
    
    // Set the shipping address.
    ((HardgoodShippingGroup) shippingGroup).setShippingAddress(addressObject);

    return true;
  }
  
  /**
   * This method will validate the credit card information entered by the user.
   * If the information is valid, it will be added to the payment group.
   * 
   * @param pUseShippingAddressForBillingAddress
   *   If true then use shipping address for billing address otherwise false.
   * @param pProfile
   *   The profile object.
   * @param pOrder
   *   The current order.
   * @param pAddressUserInputValues
   *   Map of address property values.
   * @param pCreditCardUserInputValues
   *   Map of credit card property values.
   * @param pValidationErrors
   *   An optional map that's populated when an error validating occurs.
   * @throws IntrospectionException
   *   If an error occurs.
   * @throws ClassNotFoundException
   *   If an error occurs.
   * @throws IllegalAccessException
   *   If an error occurs.
   * @throws InstantiationException 
   *   If an error occurs.
   * @return 
   *   If credit card and billing details validation passes it returns true otherwise false.
   */
  public boolean validateAndApplyCreditCardInformation(boolean pUseShippingAddressForBillingAddress,
    Profile pProfile, Order pOrder, Map<String, Object> pAddressUserInputValues, String pCVVNumber,
      Map<String, Object> pCreditCardUserInputValues, Map<String, String> pValidationErrors)
        throws InstantiationException, IllegalAccessException, ClassNotFoundException,
          IntrospectionException {

    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    StoreProfileTools profileTools = getStoreProfileTools();
    CreditCard paymentGroup = orderTools.getCreditCard(pOrder);

    if (pUseShippingAddressForBillingAddress) {

      // Create an address object from the values the user entered.
      Address addressObject = AddressTools.createAddressFromMap(pAddressUserInputValues,
        profileTools.getShippingAddressClassName());

      // Set billing address as shipping address.
      paymentGroup.setBillingAddress(addressObject);
    }
    else {
        
      // Validate the address data entered by the user.
      if (!profileTools.validateAddress(pAddressUserInputValues, pValidationErrors)) {
        return false;    
      }
    
      // Create an address object from the values the user entered.
      Address addressObject = AddressTools.createAddressFromMap(pAddressUserInputValues,
        profileTools.getShippingAddressClassName());

      paymentGroup.setBillingAddress(addressObject);
    }

    StoreProfilePropertyManager profilePropertyManager = profileTools.getProfilePropertyManager();

    // Copy the billing address map to the billingAddress property of the card as this is a 
    // required property. 
    if (!pCreditCardUserInputValues.containsKey(
        profilePropertyManager.getBillingAddressPropertyName())) {
      pCreditCardUserInputValues.put(profilePropertyManager.getBillingAddressPropertyName(), 
        pAddressUserInputValues);
    }

    Object useSavedCreditCard = pCreditCardUserInputValues
      .get(getCheckoutPropertyManager().getUseSavedCreditCardPropertyName());
      
    // Using saved credit card for checkout.
    if (useSavedCreditCard instanceof String &&
      ((String)useSavedCreditCard).equalsIgnoreCase("true")) {

      // Fetch credit card nick name.
      Object cardNickname =
        pCreditCardUserInputValues.get(profilePropertyManager.getCreditCardNicknamePropertyName());
   
      // Get credit card and copy over its immutable properties. By default the immutable
      // properties are the credit card number and the credit card type.
      RepositoryItem creditCard = 
        profileTools.getCreditCardByNickname((String)cardNickname, pProfile);

      copyImmutableCardProperties(creditCard, pCreditCardUserInputValues);
    }

    // Validate credit card verification number.
    if (getShippingConfiguration().isRequireCreditCardVerification()) {
      boolean validAuthorizationNumber = 
        orderTools.validateCreditCardAuthorizationNumber(pCVVNumber);

      if (!validAuthorizationNumber) {
        if (pValidationErrors != null) {
          pValidationErrors.put(VERIFICATION_NUMBER_INVALID,
            getResourceFromBundle(VERIFICATION_NUMBER_INVALID));
        }
        return false;
      }
    }
      
    // Validate the credit card data entered by the user.
    if (!profileTools.validateCreditCard(pCreditCardUserInputValues, pValidationErrors)) {
      return false;
    }

    // Applying payment information on payment group.

    // Credit card number.
    paymentGroup.setCreditCardNumber((String)pCreditCardUserInputValues
      .get(profilePropertyManager.getCreditCardNumberPropertyName()));
   
    // Credit card verification number.
    paymentGroup.setCardVerficationNumber(pCVVNumber);
    
    // Credit card type. 
    paymentGroup.setCreditCardType((String)pCreditCardUserInputValues
      .get(profilePropertyManager.getCreditCardTypePropertyName()));
   
    // Credit card expiration month.
    paymentGroup.setExpirationMonth((String)pCreditCardUserInputValues
      .get(profilePropertyManager.getCreditCardExpirationMonthPropertyName()));
   
    // Credit card expiration year.
    paymentGroup.setExpirationYear((String)pCreditCardUserInputValues
      .get(profilePropertyManager.getCreditCardExpirationYearPropertyName()));
    
    return true;
  }
  
  /**
   * This method creates shipping address from shipping address map.
   * After creating shipping address it applies it to shipping group.
   * Also applies shipping method to shipping group.
   * 
   * @param pProfile
   *   The profile object.
   * @param pOrder
   *   The current order.
   * @param pAddressUserInputValues
   *   Map of address property values.
   * @param pShippingMethod
   *   The shipping method.
   * @throws CommerceException
   *   If an error occurs.
   * @throws IntrospectionException
   *   If an error occurs.
   * @throws ClassNotFoundException
   *   If an error occurs.
   * @throws IllegalAccessException 
   *   If an error occurs.
   * @throws InstantiationException
   *   If an error occurs.
   * @throws RunProcessException
   *   If an error occurs.
   */
  public void applyShippingDetails(Profile pProfile, Order pOrder,
    Map<String, Object> pAddressUserInputValues, String pShippingMethod,
      PricingModelHolder pUserPricingModels) throws CommerceException, InstantiationException,
        IllegalAccessException, ClassNotFoundException, IntrospectionException,
          RunProcessException {

    StoreProfileTools profileTools = getStoreProfileTools();
    StoreProfilePropertyManager profilePropertyManager = profileTools.getProfilePropertyManager();

    // Checking address nick name is available.
    Object addressFirstName =
      pAddressUserInputValues.get(profilePropertyManager.getAddressFirstNamePropertyName());

    if (addressFirstName instanceof String && !((String)addressFirstName).isEmpty()
        && pShippingMethod != null && !pShippingMethod.isEmpty()) {

      StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
      ShippingGroup shippingGroup = orderTools.getShippingGroup(pOrder);

      // Apply shipping method
      shippingGroup.setShippingMethod(pShippingMethod);

      // Create an address object from the values the user entered.
      Address shippingAddress = AddressTools.createAddressFromMap(pAddressUserInputValues,
        profileTools.getShippingAddressClassName());

      // Set the shipping address.
      ((HardgoodShippingGroup) shippingGroup).setShippingAddress(shippingAddress);
      
      // Reprice the order after applying shipping address and shipping method to order.
      repriceOrder(getRepriceOrderChainId(), PricingConstants.OP_REPRICE_ORDER_TOTAL, pOrder,
        pUserPricingModels, getLocale(), pProfile);

      // Updating the order should be called from within a transaction and synchronized on the order.
      getOrderManager().updateOrder(pOrder);
    }
  }

  /**
   * This method creates billing address from billing address map.
   * After creating billing address it applies it to payment group.
   * 
   * @param pProfile
   *   The profile object.
   * @param pOrder
   *   The current order.
   * @param pAddressUserInputValues
   *   Map of address property values.
   * @param pCreditCardUserInputValues
   *   Map of credit card property values.
   * @throws RunProcessException
   *   If an error occurs.
   * @throws CommerceException 
   *   If an error occurs.
   * @throws IntrospectionException
   *   If an error occurs.
   * @throws ClassNotFoundException
   *   If an error occurs.
   * @throws IllegalAccessException
   *   If an error occurs. 
   * @throws InstantiationException
   *   If an error occurs.
   */
  public void applyCreditCardDetails(Profile pProfile, Order pOrder,
    Map<String, Object> pAddressUserInputValues, Map<String, Object> pCreditCardUserInputValues,
      PricingModelHolder pUserPricingModels) throws RunProcessException, CommerceException,
        ClassNotFoundException, IntrospectionException, InstantiationException,
          IllegalAccessException {

    StoreProfileTools profileTools = getStoreProfileTools();
    StoreProfilePropertyManager profilePropertyManager = profileTools.getProfilePropertyManager();
     
    // Check if the address nickname is available.
    Object addressFirstName =
      pAddressUserInputValues.get(profilePropertyManager.getAddressFirstNamePropertyName());
    Object cardNumber =
      pCreditCardUserInputValues.get(profilePropertyManager.getCreditCardNumberPropertyName());
    
    // Apply billing address to credit card only if compulsory fields
    // card number and first name for billing address is not empty.
    if (cardNumber instanceof String && !((String)cardNumber).isEmpty()
        && addressFirstName instanceof String && !((String)addressFirstName).isEmpty()) {
      
      // Create an address object from the values the user entered.
      Address billingAddress = AddressTools.createAddressFromMap(pAddressUserInputValues,
        profileTools.getShippingAddressClassName());

      // Get credit card from order.
      StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
      CreditCard card = orderTools.getCreditCard(pOrder);

      // Applies billing address into credit card.
      card.setBillingAddress(billingAddress);

      // Re-price the order after applying billing address and payment method to order.
      repriceOrder(getRepriceOrderChainId(), PricingConstants.OP_REPRICE_ORDER_TOTAL, pOrder,
        pUserPricingModels, getLocale(), pProfile);

      // Updating the order should be called from within a transaction and synchronized on the order.
      getOrderManager().updateOrder(pOrder);
    }
  }
  
  /**
   * Performs input data validations for shipping address and shipping method specified by shopper.
   * 
   * @param pAddressUserInputValues
   *   Map of address property values.
   * @param pValidationErrors
   *   An optional map that's populated when an error validating occurs.
   * @param pShippingMethod
   *   The shipping method.
   * @throws IntrospectionException
   *   If an error occurs.
   * @throws IllegalAccessException 
   *   If an error occurs.
   * @throws InstantiationException 
   *   If an error occurs.
   * @throws ClassNotFoundException
   *   If an error occurs.
   * @return 
   *   If shipping details validation passes it returns true otherwise false.
   */
  public boolean validateShippingDetails(Map<String, Object> pAddressUserInputValues,
    Map<String, String> pValidationErrors, String pShippingMethod) throws ClassNotFoundException,
      InstantiationException, IllegalAccessException, IntrospectionException {

    StoreProfileTools profileTools = getStoreProfileTools();
    StoreProfilePropertyManager profilePropertyManager = profileTools.getProfilePropertyManager();

    // Check if the address nickname is available.
    String addressFirstNamePropertyName = 
      profilePropertyManager.getAddressFirstNamePropertyName();
    Object addressFirstName = 
      pAddressUserInputValues.get(addressFirstNamePropertyName);

    if (addressFirstName instanceof String && !((String)addressFirstName).isEmpty()) {

      // Validate the address data.
      if (!profileTools.validateAddress(pAddressUserInputValues, pValidationErrors)) {
        return false;
      }
        
      // Validate address nick name.
      if (!validateAddressNickname(pAddressUserInputValues, pValidationErrors)) {
        return false;
      }
    }

    // Creates address object from address only when user's first name and
    // shipping method will be available. Making user's first name as compulsory field.
    if (addressFirstName instanceof String && !((String)addressFirstName).isEmpty()
        && pShippingMethod != null && !pShippingMethod.isEmpty()) {

      // Create an address object from the values the user entered.
      Address shippingAddress = AddressTools.createAddressFromMap(pAddressUserInputValues,
        profileTools.getShippingAddressClassName());

      // Validates shipping method against shipping state.
      // Validates the address, make sure user isn't trying to Express ship to AK, etc.
      if (!validateShippingMethod(shippingAddress, pShippingMethod, pValidationErrors)) {
        return false; 
      }
    }
    return true;
  }
  
  /**
   * Validate credit card, billing address and user input values.
   * This method will then set-up a credit card payment group.
   * 
   * @param pProfile
   *   The profile object.
   * @param pOrder
   *   The current order.
   * @param pAddressUserInputValues
   *   Map of address property values.
   * @param pCreditCardUserInputValues
   *   Map of credit card property values.
   * @param pValidationErrors
   *   An optional map that's populated when an error validating occurs.
   * @return 
   *   If credit card and billing details validation passes it returns true otherwise false.
   */
  public boolean validateCreditCardDetails(Profile pProfile, Order pOrder, String pCVVNumber,
    Map<String, Object> pAddressUserInputValues, Map<String, Object> pCreditCardUserInputValues,
      Map<String, String> pValidationErrors) {

    StoreProfileTools profileTools = getStoreProfileTools();
    StoreProfilePropertyManager profilePropertyManager = profileTools.getProfilePropertyManager();
     
    // Check if the address nickname is available.
    Object addressFirstName =
      pAddressUserInputValues.get(profilePropertyManager.getAddressFirstNamePropertyName());

    if (addressFirstName instanceof String && !((String)addressFirstName).isEmpty()) {
        
      // Validate the address data entered by the user.
      if (!profileTools.validateAddress(pAddressUserInputValues, pValidationErrors)) {
        return false;
      }
    }

    Object cardNumber =
      pCreditCardUserInputValues.get(profilePropertyManager.getCreditCardNumberPropertyName());

    if (cardNumber instanceof String && !((String)cardNumber).isEmpty()) {
      Object useSavedCreditCard = pCreditCardUserInputValues
        .get(getCheckoutPropertyManager().getUseSavedCreditCardPropertyName());

      // Using Saved credit card for checkout.
      if (useSavedCreditCard instanceof String &&
        ((String)useSavedCreditCard).equalsIgnoreCase("true")) {

        // Credit card nick name.
        Object cardNickname = pCreditCardUserInputValues.get(
          profilePropertyManager.getCreditCardNicknamePropertyName());
          
        // Get credit card and copy over its immutable properties. By default the immutable
        // properties are the credit card number and the credit card type.
        RepositoryItem creditCard = 
          profileTools.getCreditCardByNickname((String)cardNickname, pProfile);

        copyImmutableCardProperties(creditCard, pCreditCardUserInputValues);
      }

      StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();

      // Validate credit card verification number.
      if (getShippingConfiguration().isRequireCreditCardVerification()) {
        boolean validAuthorizationNumber =
          orderTools.validateCreditCardAuthorizationNumber(pCVVNumber);

        if (!validAuthorizationNumber) {
          if (pValidationErrors != null) {
            pValidationErrors.put(VERIFICATION_NUMBER_INVALID, 
              getResourceFromBundle(VERIFICATION_NUMBER_INVALID));
          }
          return false;
        }
      }

      // Populate credit card details with user input.
      fillCreditCardFieldsWithUserInput(pOrder, pCreditCardUserInputValues, pCVVNumber);

      // Verify that card number and expiration date are valid.
      int creditCardVerificationStatus =
        getCreditCardTools().verifyCreditCard(orderTools.getCreditCard(pOrder));
        
      if (creditCardVerificationStatus != ExtendableCreditCardTools.SUCCESS) {
        if (pValidationErrors != null) {
          String msg = getCreditCardTools().getStatusCodeMessage(creditCardVerificationStatus,
            getLocale());
          pValidationErrors.put(StorePurchaseProcessHelper.CC_PROPERTY_INVALID, msg);
        }
        return false;
      }

      // Validate credit card nick name.
      if (!validateCreditCardNickname(pCreditCardUserInputValues, pValidationErrors)) {
        return false;
      }
    }

    return true;
  }
  
  /**
   * Pre-fill credit card details from the user input.
   */
  protected void fillCreditCardFieldsWithUserInput(Order pOrder,
      Map<String, Object> pCreditCardUserInputValues, String pCVVNumber) {

    StoreProfilePropertyManager profilePropertyManager = 
      getStoreProfileTools().getProfilePropertyManager();

    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    CreditCard card = orderTools.getCreditCard(pOrder);

    card.setCreditCardNumber((String) pCreditCardUserInputValues
      .get(profilePropertyManager.getCreditCardNumberPropertyName()));

    card.setCreditCardType((String) pCreditCardUserInputValues
      .get(profilePropertyManager.getCreditCardTypePropertyName()));

    card.setExpirationMonth((String) pCreditCardUserInputValues
      .get(profilePropertyManager.getCreditCardExpirationMonthPropertyName()));

    card.setExpirationYear((String) pCreditCardUserInputValues
      .get(profilePropertyManager.getCreditCardExpirationYearPropertyName()));

    card.setCardVerificationNumber(pCVVNumber);
  }
  
  /**
   * Validate quantity for all the commerce items and also verifies the stock level for each sku.
   *
   * @param pOrder 
   *   The current order.
   * @param pValidationErrors
   *   An optional map that's populated when an error validating occurs.  
   * @throws RepositoryException 
   *   If an error occurs.
   * @throws InventoryException
   *   If an error occurs.
   * @return 
   *   If quantity in order passes validation it returns true otherwise false.
   */
  public boolean validateQuantity(Order pOrder, Map<String, String> pValidationErrors)
      throws RepositoryException, InventoryException {

    // List of shipping group commerce item relationship.
    List relationships = pOrder.getRelationships();

    if (relationships != null) {
      for (Object objRelationship : relationships) {

        // ShippingGroupCommerceItemRelationship in the order.
        ShippingGroupCommerceItemRelationship relationship =
          (ShippingGroupCommerceItemRelationship) objRelationship;

        // Getting the quantity from relationship and validating
        // the quantity of commerce item for each relationship.
        CommerceItem commerceItem = relationship.getCommerceItem();
        long quantity  = relationship.getQuantity();

        String productId = commerceItem.getAuxiliaryData().getProductId();
        String skuId = commerceItem.getCatalogRefId();

        RepositoryItem productParam = getCatalogTools().findProduct(productId);

        if (productParam == null) {
          vlogError("Cannot get the product from the repository. Product is {0}", productId);

          pValidationErrors.put(PRODUCT_NOT_FOUND, getResourceFromBundle(PRODUCT_NOT_FOUND));
          return false;
        }

        if (quantity <= 0) {
          String productDisplayName =
            (String) productParam.getPropertyValue(DISPLAY_NAME_PROPERTY_NAME);

          pValidationErrors.put(MSG_INVALID_QUANTITY,
            getResourceFromBundle(MSG_INVALID_QUANTITY, new String[]{productDisplayName}));

          return false;
        }

        // Checks the stock level for a sku.
        return checkSkuStockLevel(productId, skuId, quantity, pValidationErrors);
      }
    }

    return true;
  }
  
  /**
   * This method checks the stock level for a SKU. If item is out of stock
   * or quantity required is greater than the available quantity then it 
   * throws a form exception.
   * 
   * @param productId 
   *   The product Id.
   * @param skuId 
   *   The item's SKU Id.
   * @param quantityRequired 
   *   The quantity required.
   * @param pValidationErrors
   *   An optional map that's populated when an error validating occurs.  
   * @throws RepositoryException 
   *   If an error occurs.
   * @throws InventoryException
   *   If an error occurs.
   * @return 
   *   If item is in stock returns true otherwise false.
   */
   protected boolean checkSkuStockLevel(String productId, String skuId, long quantityRequired,
       Map<String, String> pValidationErrors) throws RepositoryException, InventoryException {

     // Checks for invalid productId.
     if (productId == null) {
       if (isLoggingDebug()) {
         logDebug("Cannot determine if sku " + skuId + " is available as productId is null");
       }

       pValidationErrors.put(MSG_INVALID_PRODUCT_ID,
         getResourceFromBundle(MSG_INVALID_PRODUCT_ID, new String[] { skuId }));

       return false;
     }
 
     // Checks for invalid skuId.
     if (skuId == null || StringUtils.isEmpty(skuId.trim())) {
       if (isLoggingDebug()) {
         logDebug("The skuId is null or an empty string");
       }

       pValidationErrors.put(MSG_INVALID_SKU_ID, getResourceFromBundle(MSG_INVALID_SKU_ID));
       return false;
     }

     // Get product repository item.
     RepositoryItem productParam = getCatalogTools().findProduct(productId);

     if (productParam == null) {
       vlogDebug("Cannot get the product from the repository. Product is " + productId);
       pValidationErrors.put(PRODUCT_NOT_FOUND, getResourceFromBundle(PRODUCT_NOT_FOUND));
       return false;
     }

     InventoryManager invManager = getInventoryManager();

     // Check for unlimited stock level for the sku.
     long stockLevel = invManager.queryStockLevel(skuId);

     if (stockLevel == UNLIMITED_SKU_STOCK_LEVEL) {
       pValidationErrors.put(SKU_STOCK_LEVEL_UNLIMITED,
         getResourceFromBundle(SKU_STOCK_LEVEL_UNLIMITED, new String[] { skuId }));

       return false;
     }

     // If the item is out of stock for shipping.
     int availability = invManager.queryAvailabilityStatus(skuId);

     if (availability == InventoryManager.AVAILABILITY_STATUS_OUT_OF_STOCK) {
       String productDisplayName =
         (String) productParam.getPropertyValue(DISPLAY_NAME_PROPERTY_NAME);

       pValidationErrors.put(ITEM_OUT_OF_STOCK,
         getResourceFromBundle(ITEM_OUT_OF_STOCK, new String[] { productDisplayName }));

       return false;
     }
       
     // If the item is more than available quantity.
     if (stockLevel < quantityRequired) {
       String productDisplayName =
         (String) productParam.getPropertyValue(DISPLAY_NAME_PROPERTY_NAME);

       pValidationErrors.put(ITEM_MORE_THAN_AVAILABLE_QUANTITY,
         getResourceFromBundle(ITEM_MORE_THAN_AVAILABLE_QUANTITY,
           new String[] { productDisplayName }));

       return false;
     }

     return true;
   }
  
  //---------------------------------------------------------------------------
  // UTILITY METHODS
  //---------------------------------------------------------------------------
  
  /**
   * Create a form exception, by looking up the exception code in a resource
   * file identified by the resourceBundleName property.
   *
   * @param pResource 
   *   The message key in the resource bundle.
   */
  public String getResourceFromBundle(String pResource) {
    ResourceBundle bundle = LayeredResourceBundle.getBundle(getResourceBundleName(), getLocale());
    return bundle.getString(pResource);
  }

  /**
   * Create a form exception, by looking up the exception code in a resource
   * file identified by the resourceBundleName property.
   *
   * @param pResource 
   *   The message key in the resource bundle.
   * @param pArgs 
   *   Array with arguments used in message formatting.
   */
  public String getResourceFromBundle(String pResource, Object[] pArgs) {
    ResourceBundle bundle = LayeredResourceBundle.getBundle(getResourceBundleName(), getLocale());
    String errorStr = bundle.getString(pResource);

    if (pArgs != null && pArgs.length > 0) {
      errorStr = (new MessageFormat(errorStr)).format(pArgs);
    }

    return errorStr;
  }

  /**
   * Determine the user's current locale, if available.
   *
   * @return 
   *   The requests locale.
   */
  public Locale getLocale() {
    return ServletUtil.getUserLocale();
  }
}