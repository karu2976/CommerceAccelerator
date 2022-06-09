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
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;

import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.inventory.InventoryException;
import atg.commerce.order.purchase.ExpressCheckoutFormHandler;
import atg.droplet.DropletFormException;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

/**
 * <p>
 *   This ExpressCheckoutFormHandler extension provides functionality to
 *   capture shipping/payment form data that will be provided in a single 
 *   page checkout process.
 * </p>
 * <p>
 *   So that the shipping/payment will be taken from a form, the super class's
 *   <code>paymentGroupNeeded</code> and <code>shippingGroupNeeded</code>
 *   properties must be set to 'false'.
 * </p>
 * <p>
 *   The shipping and payment form data will also be validated in this class.
 *   FormExceptions will be created and returned in the error response.
 * </p>
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/order/purchase/SinglePageCheckoutFormHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class SinglePageCheckoutFormHandler extends ExpressCheckoutFormHandler {

  /** Class version string */
  public static String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/order/purchase/SinglePageCheckoutFormHandler.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------
  
  /** Constant for profile property. */
  public static final String PROFILE = "Profile";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: shippingMethod
  //-----------------------------------
  private String mShippingMethod = null;
  
  /**
   * @param pShippingMethod
   *   Set the shipping method.
   */
  public void setShippingMethod(String pShippingMethod) {
    mShippingMethod = pShippingMethod;
  }
  
  /**
   * @return
   *   Returns the shipping method.
   */
  public String getShippingMethod() {
   return mShippingMethod;
  }
  
  //-----------------------------------
  // property: useShippingAddressForBillingAddress
  //-----------------------------------
  private boolean mUseShippingAddressForBillingAddress = true;
  
  /**
   * @param pUseShippingAddressForBillingAddress
   *   Set the flag that determines whether or not to use the shipping address for
   *   the billing address.
   */
  public void setUseShippingAddressForBillingAddress(boolean pUseShippingAddressForBillingAddress) {
    mUseShippingAddressForBillingAddress = pUseShippingAddressForBillingAddress;
  }
  
  /**
   * @return
   *   Returns the flag that determines whether or not to use the shipping address for
   *   the billing address.
   */
  public boolean isUseShippingAddressForBillingAddress() {
    return mUseShippingAddressForBillingAddress;
  }
 
  //-----------------------------------
  // property: creditCardVerificationNumber
  //-----------------------------------
  private String mCreditCardVerificationNumber = null;
  
  /**
   * @param pCreditCardVerificationNumber
   *   Set the credit card's verification security code.
   */
  public void setCreditCardVerificationNumber(String pCreditCardVerificationNumber) {
    mCreditCardVerificationNumber = pCreditCardVerificationNumber;
  }
  
  /**
   * @return
   *   Returns the credit card's verification security code.
   */
  public String getCreditCardVerificationNumber() {
    return mCreditCardVerificationNumber;
  }

  //-----------------------------------
  // property: shippingAddressUserInputValues
  //-----------------------------------
  private Map<String, Object> mShippingAddressUserInputValues = new HashMap<>();

  /**
   * @return
   *   This is a map that stores the pending values for an address operations.
   *   Values in this map are generally is set by the SinglePageCheckoutActor.
   */
  public Map<String, Object> getShippingAddressUserInputValues() {
    return mShippingAddressUserInputValues;
  }
  
  //-----------------------------------
  // property: billingAddressUserInputValues
  //-----------------------------------
  private Map<String, Object> mBillingAddressUserInputValues = new HashMap<>();

  /**
   * @return mBillingAddressUserInputValues
   *   This is a map that stores the pending values for an address operations.
   *   Values in this map are generally is set by the SinglePageCheckoutActor.
   */
  public Map<String, Object> getBillingAddressUserInputValues() {
    return mBillingAddressUserInputValues;
  }
  
  //-----------------------------------
  // property: creditCardUserInputValues
  //-----------------------------------
  private Map<String, Object> mCreditCardUserInputValues = new HashMap<>();

  /**
   * @return
   *   This is a map that stores the pending values for credit card operations.
   *   Values in this map are generally is set by the SinglePageCheckoutActor.
   */
  public Map<String, Object> getCreditCardUserInputValues() {
    return mCreditCardUserInputValues;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------
  
  /**
   * The shipping group and payment group will be populated with the corresponding form input
   * details and validated. Any validation errors will be recorded in FormExceptions.
   *
   * @param pRequest 
   *   A <code>DynamoHttpServletRequest</code> request.
   * @param pResponse
   *   A <code>DynamoHttpServletRequest</code> response.
   * @exception ServletException 
   *   If there was an error while executing the code.
   * @exception IOException
   *   If there was an error with servlet io.
   */
  @Override
  public void preExpressCheckout(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    
    // If the shipping information is valid, it will be added to the shipping group.
    validateAndApplyShippingInformation(pRequest, pResponse);

    // If the credit card information is valid, it will be added to the payment group.
    if (!getFormError()) {
      validateAndApplyCreditCardInformation(pRequest, pResponse);
    }

    // Validates the quantity of an item in order.
    if (!getFormError()) {
      validateQuantity(pRequest, pResponse);
    }

    // This method is for any processing required after shipping and billing info is validated.
    // It will save address and credit card into the profile if the user chose that option.
    if (!getFormError()) {
      addCreditCardAndAddressToProfile(pRequest, pResponse);
    }

    // Empty out the user input maps.
    getShippingAddressUserInputValues().clear();
    getBillingAddressUserInputValues().clear();
    getCreditCardUserInputValues().clear();
  }

  /**
   * This method will validate the shipping address information entered by the user. If the
   * information is valid, it will be added to the shipping group.
   * 
   * @param pRequest
   *   a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *   a <code>DynamoHttpServletResponse</code> value.
   * @throws IOException
   *   If an error occurs.
   * @throws ServletException
   *   If an error occurs.
   */
  public void validateAndApplyShippingInformation(DynamoHttpServletRequest pRequest, 
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    // When the 'shippingGroupNeeded' property is false, we are expecting
    // input form details rather than taking the details from previously 
    // saved shipping groups.
    if (!isShippingGroupNeeded()) {
      Map<String, String> validationErrors = new HashMap<>();
      
      // Validate the address data entered by the user. If there are any parts of the
      // address that fail validation an error will be added to the validationErrors
      // map. These error codes should correspond to a key in the resourceBundleName.
      // The value can be displayed to the user on the UI.
      try {
        boolean validateShippingDetails = ((StorePurchaseProcessHelper)getPurchaseProcessHelper()).
          validateAndApplyShippingInformation(getOrder(), getShippingMethod(),
            getShippingAddressUserInputValues(), validationErrors);
     
        if (!validateShippingDetails) {
          for (Map.Entry<String, String> errorEntry : validationErrors.entrySet()) {
            addFormException(new DropletFormException(errorEntry.getValue(), getAbsoluteName(),
              errorEntry.getKey()));
          }
        }
      }
      catch (InstantiationException | IllegalAccessException | ClassNotFoundException
          | CommerceException | IntrospectionException exception) {
        if (isLoggingError()) {
          logError("Error while validating the shipping information.", exception);
        }

        // Add exception so validation doesnt pass.
        addFormException(
          new DropletFormException(((StorePurchaseProcessHelper)getPurchaseProcessHelper())
            .getResourceFromBundle(StorePurchaseProcessHelper.INVALID_ADDRESS), getAbsoluteName(),
              StorePurchaseProcessHelper.INVALID_ADDRESS));
      }
    }
  }
  
  /**
   * This method will validate the credit card information entered by the user.
   * If the information is valid, it will be added to the payment group.
   * 
   * @param pRequest
   *   a <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *   a <code>DynamoHttpServletResponse</code> value.
   */
  public void validateAndApplyCreditCardInformation(DynamoHttpServletRequest pRequest, 
    DynamoHttpServletResponse pResponse) {
    
    // When the 'paymentGroupNeeded' property is false, we are expecting
    // input form details rather than taking the details from previously 
    // saved payment groups.
    if (!isPaymentGroupNeeded()) {
      Map<String, String> validationErrors = new HashMap<>();
      
      // Validate the credit card data entered by the user. If there are any parts
      // of the credit card details that fail validation an error will be added to
      // the validationErrors map. These error codes should correspond to a key in
      // the resourceBundleName. The value can be displayed to the user on the UI.
      try {
        boolean validateCreditCardDetails = ((StorePurchaseProcessHelper)getPurchaseProcessHelper())
          .validateAndApplyCreditCardInformation(isUseShippingAddressForBillingAddress(),
            (Profile)getProfile(), getOrder(), getBillingAddressUserInputValues(),
              getCreditCardVerificationNumber(), getCreditCardUserInputValues(), validationErrors);
        
        if (!validateCreditCardDetails) {
          for (Map.Entry<String, String> errorEntry : validationErrors.entrySet()) {
            addFormException(new DropletFormException(errorEntry.getValue(), getAbsoluteName(),
              errorEntry.getKey()));
          }
        }
      } 
      catch (InstantiationException | IllegalAccessException | ClassNotFoundException
        | IntrospectionException exception)
      {
        if (isLoggingError()) {
          logError("Error while validating the credit card information.", exception);
        }

        // Add exception so validation doesnt pass.
        addFormException(new DropletFormException(
          ((StorePurchaseProcessHelper)getPurchaseProcessHelper())
            .getResourceFromBundle(StorePurchaseProcessHelper.INVALID_CARD),
              getAbsoluteName(), StorePurchaseProcessHelper.INVALID_CARD));
      }
    }
  }
  
  /**
   * This method is for any processing required after shipping and billing info is validated.
   * It will save address and credit card into the profile if the user chose that option.
   *
   * @param pRequest
   *   A <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *   A <code>DynamoHttpServletResponse</code> value.
   */
  public void addCreditCardAndAddressToProfile(DynamoHttpServletRequest pRequest, 
    DynamoHttpServletResponse pResponse) {

    Map<String, String> validationErrors = new HashMap<>();

    // It will save address into the profile. If nickname will be empty or
    // address exist in saved address list then it throws an error.
    try {
     boolean saveAddressToProfile = ((StorePurchaseProcessHelper)getPurchaseProcessHelper())
       .addAddressToProfile((Profile)getProfile(), getShippingAddressUserInputValues(), 
         validationErrors);
      
      if (!saveAddressToProfile) {
        for (Map.Entry<String, String> errorEntry : validationErrors.entrySet()) {
          addFormException(new DropletFormException(errorEntry.getValue(), getAbsoluteName(),
            errorEntry.getKey()));
        }
      }
    }
    catch (ClassNotFoundException | InstantiationException | IllegalAccessException
        | RepositoryException | IntrospectionException exception) {
      if (isLoggingError()) {
        logError("Error while saving the address from map into profile", exception);
      }

      // Add exception so validation doesn't pass.
      addFormException(new DropletFormException(
        ((StorePurchaseProcessHelper)getPurchaseProcessHelper())
          .getResourceFromBundle(StorePurchaseProcessHelper.INVALID_ADDRESS),
            getAbsoluteName(), StorePurchaseProcessHelper.INVALID_ADDRESS));
    }

    // It will save credit card into the profile. If nickname will be empty or
    // credit card exist in saved credit card list then it throws an error. 
    try {
      boolean saveCreditCardToProfile = ((StorePurchaseProcessHelper)getPurchaseProcessHelper())
        .addCreditCardToProfile((Profile)getProfile(), getCreditCardUserInputValues(),
          validationErrors);
      
      if (!saveCreditCardToProfile) {
        for (Map.Entry<String, String> errorEntry : validationErrors.entrySet()) {
          addFormException(new DropletFormException(errorEntry.getValue(), getAbsoluteName(),
            errorEntry.getKey()));
        }
        validationErrors.clear();
      }
    }
    catch (ClassNotFoundException | InstantiationException | IllegalAccessException
        | RepositoryException | IntrospectionException | PropertyNotFoundException exception) {
      if (isLoggingError()) {
        logError("Error while saving the credit card from map into profile", exception);
      }

      // Add exception so validation doesn't pass.
      addFormException(new DropletFormException(
        ((StorePurchaseProcessHelper)getPurchaseProcessHelper()).getResourceFromBundle(
          StorePurchaseProcessHelper.INVALID_CARD), getAbsoluteName(),
            StorePurchaseProcessHelper.INVALID_CARD));
    }
  }

  /**
   * Validate quantity for all the commerce items and also verifies the stock level for each sku.
   *
   * @param pRequest 
   *   A <code>DynamoHttpServletRequest</code> request.
   * @param pResponse 
   *   A <code>DynamoHttpServletRequest</code> response.
   * @throws ServletException 
   *   If servlet error occurs.
   * @throws IOException 
   *   If IO error occurs.
   */
  protected void validateQuantity(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    Map<String, String> validationErrors = new HashMap<>();
    
    try {
      boolean validateQuantity = ((StorePurchaseProcessHelper)getPurchaseProcessHelper()).
        validateQuantity(getOrder(), validationErrors);

      if (!validateQuantity) {
        for (Map.Entry<String, String> errorEntry : validationErrors.entrySet()) {
          addFormException(new DropletFormException(errorEntry.getValue(), getAbsoluteName(),
            errorEntry.getKey()));
        }
      }
    } 
    catch (InventoryException | RepositoryException exception) {
      if (isLoggingError()) {
        logError("Error while validating the quantity in order", exception);
      }

      // Add exception so validation doesn't pass.
      addFormException(new DropletFormException(
        ((StorePurchaseProcessHelper)getPurchaseProcessHelper()).getResourceFromBundle(
          StorePurchaseProcessHelper.MSG_INVALID_QUANTITY), getAbsoluteName(),
            StorePurchaseProcessHelper.MSG_INVALID_QUANTITY));
    }
  }
  
  /**
   * One of the pipeline processors requires the Profile to be in the process order map, so set it
   * here.
   * 
   * @param pLocale
   *   The locale to include in the map.
   * @return
   *   A map that holds additional information for the process order pipeline.
   */
  @Override
  public HashMap getProcessOrderMap (Locale pLocale) throws CommerceException {
    HashMap processOrderMap =  super.getProcessOrderMap(pLocale);
    processOrderMap.put(PROFILE, getProfile());
    return processOrderMap;
  }
}