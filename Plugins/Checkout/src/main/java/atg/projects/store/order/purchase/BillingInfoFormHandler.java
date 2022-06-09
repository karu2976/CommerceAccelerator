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
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.purchase.PurchaseProcessFormHandler;
import atg.droplet.DropletFormException;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

/**
 * This form Handler validates billing address and payment method. 
 * If validation passed then applies billing details to order otherwise 
 * throws error.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/order/purchase/BillingInfoFormHandler.java#1 $$Change: 1385662 $ 
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class BillingInfoFormHandler extends PurchaseProcessFormHandler {

  /** Class version string. */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/order/purchase/BillingInfoFormHandler.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------
  
  //-----------------------------------
  // property: billingHelper
  //-----------------------------------
  private StorePurchaseProcessHelper mBillingHelper = null;

  /**
   * @return
   *   Returns the billing helper component.
   */
  public StorePurchaseProcessHelper getBillingHelper() {
    return mBillingHelper;
  }

  /**
   * @param pBillingHelper 
   *   Set the billingHelper component
   */
  public void setBillingHelper(StorePurchaseProcessHelper pBillingHelper) {
    mBillingHelper = pBillingHelper;
  }

  //-----------------------------------
  // property: billingWithAddressAndCardDetailsErrorURL
  //-----------------------------------
  private String mBillingWithAddressAndCardDetailsErrorURL = null;

  /**
   * @return
   *   Returns billing with an address and a card error URL.
   */
  public String getBillingWithAddressAndCardDetailsErrorURL() {
    return mBillingWithAddressAndCardDetailsErrorURL;
  }

  /**
   * @param pBillingWithAddressAndCardDetailsErrorURL
   *   Set billing with an address and a card error URL.
   */
  public void setBillingWithAddressAndCardDetailsErrorURL(
    String pBillingWithAddressAndCardDetailsErrorURL) {
    mBillingWithAddressAndCardDetailsErrorURL = pBillingWithAddressAndCardDetailsErrorURL;
  }

  //-----------------------------------
  // property: billingWithAddressAndCardDetailsSuccessURL
  //-----------------------------------
  private String mBillingWithAddressAndCardDetailsSuccessURL = null;

  /**
   * @return
   *   Returns billing with an address and a card success URL.
   */
  public String getBillingWithAddressAndCardDetailsSuccessURL() {
    return mBillingWithAddressAndCardDetailsSuccessURL;
  }

  /**
   * @param pBillingWithAddressAndCardDetailsSuccessURL
   *   Set billing with an address and a card success URL.
   */
  public void setBillingWithAddressAndCardDetailsSuccessURL(
    String pBillingWithAddressAndCardDetailsSuccessURL) {
    mBillingWithAddressAndCardDetailsSuccessURL = pBillingWithAddressAndCardDetailsSuccessURL;
  }

  //-----------------------------------
  // property: billingAddressUserInputValues
  //-----------------------------------
  private Map<String, Object> mBillingAddressUserInputValues = new HashMap<>();

  /**
   * @return
   *   This is a map that stores the pending values for a billing address.
   *   Values in this map are generally set by the BillingFormHandlerActor.
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
   *   This is a map that stores the pending values for a credit card.
   *   Values in this map are generally is set by the BillingFormHandlerActor.
   */
  public Map<String, Object> getCreditCardUserInputValues() {
    return mCreditCardUserInputValues;
  }
  
  //-----------------------------------
  // property: creditCardVerificationNumber
  //-----------------------------------
  private String mCreditCardVerificationNumber = null;

  /**
   * @return
   *   Returns the credit card verification number.
   */
  public String getCreditCardVerificationNumber() {
    return mCreditCardVerificationNumber;
  }

  /**
   * @param pCreditCardVerificationNumber
   *   Set the credit card verification number to set.
   */
  public void setCreditCardVerificationNumber(String pCreditCardVerificationNumber) {
    mCreditCardVerificationNumber = pCreditCardVerificationNumber;
  }
  
  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Handler for validating billing address and credit card info. After validation apply it to payment group.
   *
   * @param pRequest
   *   A <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *   A <code>DynamoHttpServletResponse</code> value.
   * @return 
   *   If no form error then it returns true otherwise false.
   * @exception ServletException
   *   If an error occurs.
   * @exception IOException
   *   If an error occurs.
   */
  public boolean handleBillingWithAddressAndCardDetails(DynamoHttpServletRequest pRequest, 
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    Transaction tr = null;

    try {
      tr = ensureTransaction();

      // Synchronize on the order because we invoke updateOrder.
      synchronized (getOrder()) {

        // Actions to be performed before updating the order.
        preBillingWithAddressAndCardDetails(pRequest, pResponse);

        if (getFormError()) {
          return checkFormRedirect(null,
            getBillingWithAddressAndCardDetailsErrorURL(), pRequest, pResponse);
        }

        billingWithAddressAndCardDetails(pRequest, pResponse);

        if (getFormError()) {
          return checkFormRedirect(null,
            getBillingWithAddressAndCardDetailsErrorURL(), pRequest, pResponse);
        }

        // Empty out the user input maps.
        getBillingAddressUserInputValues().clear();
        getCreditCardUserInputValues().clear();

        // Actions to be performed after updating the order.
        postBillingWithAddressAndCardDetails(pRequest, pResponse);

        if (getFormError()) {
          return checkFormRedirect(null,
            getBillingWithAddressAndCardDetailsErrorURL(), pRequest, pResponse);
        }
      }

      return checkFormRedirect(getBillingWithAddressAndCardDetailsSuccessURL(),
        getBillingWithAddressAndCardDetailsErrorURL(), pRequest, pResponse);
    }
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }
  
  /**
   * Validate the credit card and billing address and user input values. This method will then set-up a credit card
   * payment group.
   * 
   * @param pRequest
   *   A <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *   A <code>DynamoHttpServletResponse</code> value.
   *
   * @exception ServletException
   *   If an error occurs.
   * @exception IOException
   *   If an error occurs.
   */
  protected void preBillingWithAddressAndCardDetails(DynamoHttpServletRequest pRequest, 
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    if (getOrder().getPriceInfo().getTotal() > 0) {
      Map<String, String> validationErrors = new HashMap<>();

      boolean validCreditCardDetails = getBillingHelper().validateCreditCardDetails(
        (Profile) getProfile(), getOrder(), getCreditCardVerificationNumber(),
          getBillingAddressUserInputValues(), getCreditCardUserInputValues(), validationErrors);

      // If the details are invalid add a form exception whose details can be displayed on the UI.
      if (!validCreditCardDetails) {
        for (Map.Entry<String, String> errorEntry : validationErrors.entrySet()) {
          addFormException(new DropletFormException(
            errorEntry.getValue(), getAbsoluteName(), errorEntry.getKey()));
        }
      }
    }
  }

  /**
   * After validation, the billing address will be applied to the credit card, the re-price order
   * pipeline chain will be invoked and the order will be updated.
   * 
   * @param pRequest
   *   A <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *   A <code>DynamoHttpServletResponse</code> value.
   *
   * @exception ServletException
   *   If an error occurs.
   * @exception IOException
   *   If an error occurs.
   */
  protected void billingWithAddressAndCardDetails(DynamoHttpServletRequest pRequest, 
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    try {
      getBillingHelper().applyCreditCardDetails((Profile)getProfile(), getOrder(),
        getBillingAddressUserInputValues(), getCreditCardUserInputValues(), getUserPricingModels());
    }
    catch (RunProcessException | CommerceException | InstantiationException | IllegalAccessException
        | ClassNotFoundException | IntrospectionException exception) {
      if (isLoggingError()) {
        logError("Error while applying credit card details to payment group.", exception);
      }
    }
  }

  /**
   * This method does anything required after applying the billing address and credit card to an order.
   * 
   * @param pRequest
   *   A <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *   A <code>DynamoHttpServletResponse</code> value.
   *
   * @exception ServletException
   *   If an error occurs.
   * @exception IOException
   *   If an error occurs.
   */
  protected void postBillingWithAddressAndCardDetails(DynamoHttpServletRequest pRequest, 
    DynamoHttpServletResponse pResponse) throws ServletException, IOException {
  }
}