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
import atg.commerce.order.purchase.ShippingGroupFormHandler;
import atg.droplet.DropletFormException;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.userprofiling.Profile;

/**
 * This form Handler validates shipping address and shipping method. 
 * If validation passed then applies shipping details to order otherwise 
 * throws error.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/order/purchase/ShippingInfoFormHandler.java#1 $$Change: 1385662 $ 
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ShippingInfoFormHandler extends ShippingGroupFormHandler {

  /** Class version string. */
  public static final String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Checkout/src/main/java/atg/projects/store/order/purchase/ShippingInfoFormHandler.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: shippingHelper
  //-----------------------------------
  private StorePurchaseProcessHelper mShippingHelper = null;

  /**
   * @return
   *   Returns the Shipping Helper component.
   */
  public StorePurchaseProcessHelper getShippingHelper() {
    return mShippingHelper;
  }

  /**
   * @param pShippingHelper 
   *   Set the shipping helper component.
   */
  public void setShippingHelper(StorePurchaseProcessHelper pShippingHelper) {
    mShippingHelper = pShippingHelper;
  }

  //-----------------------------------
  // property: shippingMethod
  //-----------------------------------
  private String mShippingMethod = null;

  /**
   * @return
   *   Returns the shipping method.
   */
  public String getShippingMethod() {
    return mShippingMethod;
  }

  /**
   * @param pShippingMethod 
   *   Set the shipping method.
   */
  public void setShippingMethod(String pShippingMethod) {
    mShippingMethod = pShippingMethod;
  }

  //-----------------------------------
  // property: shipToAddressErrorURL
  //-----------------------------------
  private String mShipToAddressErrorURL = null;

  /**
   * @return
   *   Returns the ship to address error URL.
   */
  public String getShipToAddressErrorURL() {
    return mShipToAddressErrorURL;
  }
  
  /**
   * @param pShipToAddressErrorURL 
   *   Set the ship to address error URL.
   */
  public void setShipToAddressErrorURL(String pShipToAddressErrorURL) {
    mShipToAddressErrorURL = pShipToAddressErrorURL;
  }

  //-----------------------------------
  // property: shipToAddressSuccessURL
  //-----------------------------------
  private String mShipToAddressSuccessURL = null;

  /**
   * @return
   *   Returns the ship to address success URL.
   */
  public String getShipToAddressSuccessURL() {
    return mShipToAddressSuccessURL;
  }

  /**
   * @param pShipToAddressSuccessURL
   *   Set the ship to address success URL.
   */
  public void setShipToAddressSuccessURL(String pShipToAddressSuccessURL) {
    mShipToAddressSuccessURL = pShipToAddressSuccessURL;
  }

  //-----------------------------------
  // property: shippingAddressUserInputValues
  //-----------------------------------
  private Map<String, Object> mShippingAddressUserInputValues = new HashMap<>();

  /**
   * @return
   *   This is a map that stores the pending values for an address.
   *   Values in this map are generally is set by the ShippingGroupActor.
   */
  public Map<String, Object> getShippingAddressUserInputValues() {
    return mShippingAddressUserInputValues;
  }
  
  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------
  
  /**
   * Performs input data validations for shipping address and shipping method
   * specified by shopper.
   * 
   * @param pRequest
   *   A <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *   A <code>DynamoHttpServletResponse</code> value.
   * @exception ServletException
   *   If an error occurs.
   * @exception IOException
   *   If an error occurs.
   */
  protected void preShipToAddress(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    if (isAnyHardgoodShippingGroups()) {
      Map<String, String> validationErrors = new HashMap<>();
      
      // Validate shipping details.
      try {
        boolean validAddressDetails =
          getShippingHelper().validateShippingDetails(getShippingAddressUserInputValues(),
            validationErrors, getShippingMethod());

        if (!validAddressDetails) {
          for (Map.Entry<String, String> errorEntry : validationErrors.entrySet()) {
            addFormException(new DropletFormException(
              errorEntry.getValue(), getAbsoluteName(), errorEntry.getKey()));
          }
        }
      } 
      catch (ClassNotFoundException | InstantiationException | IllegalAccessException
          | IntrospectionException e) {
        if (isLoggingError()) {
          logError("Error while validating shipping details to shipping group.", e);
        }

        // Add exception so validation doesn't pass.
        addFormException(new DropletFormException(
          getShippingHelper().getResourceFromBundle(StorePurchaseProcessHelper.INVALID_ADDRESS),
            getAbsoluteName(), StorePurchaseProcessHelper.INVALID_ADDRESS));
      }
    }
  }

  /**
   * This method creates shipping address from shipping address map.
   * After creating shipping address it applies it to shipping group.
   * Also applies shipping method to shipping group.
   * 
   * @param pRequest
   *   A <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *   A <code>DynamoHttpServletResponse</code> value.
   * @exception ServletException
   *   If an error occurs.
   * @exception IOException
   *   If an error occurs.
   */
  protected void shipToAddress(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    try {
      getShippingHelper().applyShippingDetails((Profile)getProfile(), getOrder(),
        getShippingAddressUserInputValues(), getShippingMethod(), getUserPricingModels());
    } 
    catch (RunProcessException | CommerceException | InstantiationException | IllegalAccessException
        | ClassNotFoundException | IntrospectionException e) {
      if (isLoggingError()) {
        logError("Error while applying shipping details to shipping group.", e);
      }
    }
  }

  /**
   * This method does anything required after applying
   * the shipping address and shipping method on order.
   * 
   * @param pRequest
   *   A <code>DynamoHttpServletRequest</code> value.
   * @param pResponse
   *   A <code>DynamoHttpServletResponse</code> value.
   * @exception ServletException
   *   If an error occurs.
   * @exception IOException
   *   If an error occurs.
   */
  protected void postShipToAddress(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) throws ServletException, IOException {
  }
  
  /**
   * Handle for validating shipping address info. 
   * After validation apply it to shipping group.
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
  public boolean handleShipToAddress(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {

    Transaction tr = null;

    try {
      tr = ensureTransaction();

      synchronized (getOrder()) {

        // Actions to be performed before adding the shipping info.
        preShipToAddress(pRequest, pResponse);

        if (getFormError()) {
          return checkFormRedirect(null, getShipToAddressErrorURL(), pRequest, pResponse);
        }

        shipToAddress(pRequest, pResponse);

        if (getFormError()) {
          return checkFormRedirect(null, getShipToAddressErrorURL(), pRequest, pResponse);
        }

        // Empty out the user input maps.
        getShippingAddressUserInputValues().clear();

        // Actions to be performed after adding the shipping info.
        postShipToAddress(pRequest, pResponse);

        if (getFormError()) {
          return checkFormRedirect(null, getShipToAddressErrorURL(), pRequest, pResponse);
        }
      }

      // If no form errors are found, redirect to the success URL,
      // otherwise redirect to the error URL.
      return checkFormRedirect(getShipToAddressSuccessURL(), getShipToAddressErrorURL(),
        pRequest, pResponse);
    } 
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }
}