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
package atg.commerce.promotion;

import javax.servlet.ServletException;
import atg.commerce.CommerceException;
import atg.repository.RepositoryException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.commerce.order.OrderImpl;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.promotion.CouponFormHandler;
import java.io.IOException;

/**
 * Extension of {@link CouponFormHandler} for working with coupons.
 * This form handler processes add/remove operations on order coupons.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Promotions/src/main/java/atg/commerce/promotion/StoreCouponFormHandler.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $$Author: jsiddaga $
 */
public class StoreCouponFormHandler extends CouponFormHandler {
  
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Promotions/src/main/java/atg/commerce/promotion/StoreCouponFormHandler.java#1 $$Change: 1385662 $";

  protected static final String MSG_INVALID_COUPON = "invalidCoupon";
  protected static final String MSG_DUPLICATE_COUPON = "duplicateCoupon";
  protected static final String MSG_COUPON_REMOVE_FAILURE = "couponNotRemoved";
  protected static final String MSG_COUPON_NOT_CLAIMED = "couponNotClaimed";
  
  
  //-----------------------------------
  // property: removeCouponErrorURL
  //-----------------------------------
  private String mRemoveCouponErrorURL;
  
  /**
   * @return remove coupon error redirect URL.
   */
  public String getRemoveCouponErrorURL() {
    return mRemoveCouponErrorURL;
  }
  
  /**
   * @param pRemoveCouponErrorURL
   *   Set remove coupon error redirect URL.
   */
  public void setRemoveCouponErrorURL(String pRemoveCouponErrorURL) {
    mRemoveCouponErrorURL = pRemoveCouponErrorURL;
  }
  
  //-----------------------------------
  // property: removeCouponSuccessURL
  //-----------------------------------
  private String mRemoveCouponSuccessURL;
  
  /**
   * @return remove coupon success redirect URL.
   */
  public String getRemoveCouponSuccessURL() {
    return mRemoveCouponSuccessURL;
  }

  /**
   * @param pRemoveCouponSuccessURL
   *   Set remove coupon success redirect URL.
   */
  public void setRemoveCouponSuccessURL(String pRemoveCouponSuccessURL) {
    mRemoveCouponSuccessURL = pRemoveCouponSuccessURL;
  }
  
  //-------------------------------------
  // property: order
  //-------------------------------------
  private OrderImpl mOrder=null;

  /**
   * Sets property order
   *
   * @param pOrder 
   *   An <code>OrderImpl</code> value
   */
  public void setOrder(OrderImpl pOrder) {
    mOrder = pOrder;
  }

  /**
   * Returns property order
   * @return an <code>OrderImpl</code> value
   *
   */
  public OrderImpl getOrder() {
    return mOrder;
  }

  //-------------------------------------
  // property: userPricingModels
  //-------------------------------------
  PricingModelHolder mUserPricingModels;

  /**
   * Sets property UserPricingModels
   *
   * @param pUserPricingModels
   *   A <code>PricingModelHolder</code> value
   */
  public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
    mUserPricingModels = pUserPricingModels;
  }

  /**
   * Returns property UserPricingModels
   * @return a <code>PricingModelHolder</code> value
   */
  public PricingModelHolder getUserPricingModels() {
    return mUserPricingModels;
  }
  
  /**
   * This getter implements a read-only <code>currentCouponCode</code> property. 
   * This property calculates a coupon code used by the current user's shopping cart.
   * 
   * @return currently used coupon code, or {@code null} if none of coupons used.
   */
  public String getCurrentCouponCode() {
    try {
      return ((StoreClaimableManager)getClaimableManager()).getCouponCode(getOrder());
    } 
    catch (CommerceException commerceException) {
      return null;
    }
  }
  
  /**
   * Remove the specified coupon, register a form exception if the coupon could not be removed or
   * an error occurs.
   * 
   * @param pRequest
   *   Current HTTP servlet request.
   * @param pResponse
   *   Current HTTP servlet response.
   * @return form redirect.
   *
   * @throws ServletException
   *   If an error occurred during removing the coupon.
   * @throws IOException
   *   If an error occurred during removing the coupon.
   */
  public boolean handleRemoveCoupon(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
      throws ServletException, IOException{
    
    try {

      // Attempt to remove the specified coupon
      ((StoreClaimableManager)getClaimableManager()).removeCoupon(getCurrentCouponCode(), getOrder(), getProfile(),
        getUserPricingModels(), getUserLocale(pRequest));
    }
    catch (CommerceException commerceException) {
      processError(MSG_COUPON_REMOVE_FAILURE, commerceException, pRequest);
    } 
    catch (RepositoryException repositoryException) {
      processError(MSG_COUPON_REMOVE_FAILURE, repositoryException, pRequest);
    }
    if (getFormError()) {
      return checkFormRedirect(null, getRemoveCouponErrorURL(), pRequest, pResponse);
    }
    return checkFormRedirect(getRemoveCouponSuccessURL(), getRemoveCouponErrorURL(), pRequest,
      pResponse);
  }
}