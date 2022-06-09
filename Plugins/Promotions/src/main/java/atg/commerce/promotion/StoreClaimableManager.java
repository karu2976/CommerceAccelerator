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

import java.util.Collection;
import java.util.Locale;

import atg.commerce.CommerceException;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.OrderTools;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.promotion.PromotionTools;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

/**
 * The class extends the ClaimableManager. This class adds a method
 * that retrieves the current coupon code applied on the order.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Promotions/src/main/java/atg/commerce/promotion/StoreClaimableManager.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $$Author: jsiddaga $
 */
public class StoreClaimableManager extends ClaimableManager {
  
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Promotions/src/main/java/atg/commerce/promotion/StoreClaimableManager.java#1 $$Change: 1385662 $";
  
  //-------------------------------------
  // property: OrderTools
  //-------------------------------------
  private OrderTools mOrderTools;

  /**
   * Sets property OrderTools
   *
   * @param OrderTools
   *   An <code>OrderTools</code> value
   */
  public void setOrderTools(OrderTools pOrderTools) {
    mOrderTools = pOrderTools;
  }

  /**
   * Returns property OrderTools
   * @return an <code>OrderTools</code> value
   *
   */
  public OrderTools getOrderTools() {
    return mOrderTools;
  }
  
  /**
   * This method removes a coupon from the order specified.
   * The order is re-priced if the 'pRepriceOrder' parameter is true.
   *
   * @param pCouponCode
   *   Coupon code to be removed.
   * @param pOrder
   *   Order with coupon claimed.
   * @param pProfile
   *   User who removes a coupon.
   * @param pUserPricingModels
   *   User's pricing models to be used in order re-pricing process.
   * @param pUserLocale
   *   User's locale to be used when re-pricing order.
   * 
   * @throws CommerceException
   *   If something goes wrong.
   * @throws RepositoryException 
   */
  public void removeCoupon(String pCouponCode, OrderImpl pOrder, RepositoryItem pProfile,
    PricingModelHolder pUserPricingModels, Locale pUserLocale) throws CommerceException,
    RepositoryException {

    // If there's no couponCode, there's nothing to remove.
    if (pCouponCode == null) {
      return;
    }

    RepositoryItem profile = pProfile;

    // Get and remove all coupon's promotions from order.
    RepositoryItem coupon = claimItem(pCouponCode);
    String promotionsPropertyName = getClaimableTools().getPromotionsPropertyName();

    //Ok to suppress because coupon.promotions contains a set of promotions (in form of repository items).
    @SuppressWarnings("unchecked") 
    Collection<RepositoryItem> promotions = 
      (Collection<RepositoryItem>) coupon.getPropertyValue(promotionsPropertyName);

    // Ensure profile to be a MutableRepositoryItem.
    if (!(profile instanceof MutableRepositoryItem)) {

      // Profile uses a MutableRepository for sure.
      profile = 
        ((MutableRepository) profile.getRepository()).getItemForUpdate(profile.getRepositoryId(),
          profile.getItemDescriptor().getItemDescriptorName());
    }

    for (RepositoryItem promotion: promotions) {

      // Now we can cast profile to the type we need
      getPromotionTools().removePromotion((MutableRepositoryItem) profile, promotion, false);
    }

    // Initialize pricing models to use current promotions, that is exclude coupon's 
    // promotions from pricing models.
    pUserPricingModels.initializePricingModels();
  }
  
  /**
   * This method calculates a coupon code used by the order pOrder. It looks up
   * an owner of the order (i.e. profile) and iterates over its active promotion
   * statuses. It calculates all coupons linked by these statuses. First coupon
   * with proper site ID (i.e. from the shared cart site group) will be returned.
   * 
   * @param pOrder
   *  Order to be inspected.
   * @return
   *   Coupon code used by the order or {@code null} if none of coupons used.
   * @throws CommerceException 
   *   If we are unable to get the profile from the repository.
   */
  @SuppressWarnings("unchecked") // OK to suppress, we just cast collections.
  public String getCouponCode(OrderImpl pOrder) throws CommerceException {
    
    // pOrder can be null when there is no profile. This can occur when viewing
    // a component through the admin browser.
    if (pOrder == null){
      return null;
    }

    OrderTools orderTools = getOrderTools();
    Repository profileRepository = orderTools.getProfileRepository();
    CommerceProfileTools profileTools = orderTools.getProfileTools();
    ClaimableManager claimableManager = profileTools.getClaimableManager();
    PromotionTools promotionTools = claimableManager.getPromotionTools();
    RepositoryItem profile = null;
    
    try {
      profile = profileRepository.getItem(pOrder.getProfileId(),
        profileTools.getDefaultProfileType());
    }
    catch (RepositoryException e) {
      throw new CommerceException(e);
    }
    
    // Profile can be null when validating a rest actor or when viewing a component through the admin browser
    // on the publishing server.
    if (profile == null) {
      return null;
    }
    
    String promoStatusCouponsPropertyName = 
        promotionTools.getPromoStatusCouponsPropertyName();
      
    // Get the active promotions on the current user profile and iterate over
    // them inspecting their coupon codes.
    Collection<RepositoryItem> activePromotions = (Collection<RepositoryItem>) 
      profile.getPropertyValue(promotionTools.getActivePromotionsProperty());
        
    for (RepositoryItem promotionStatus: activePromotions) {
    
      // Each promotionStatus contains a list of coupons that activated a 
      // promotion. Inspect these coupons.
      Collection<RepositoryItem> coupons = (Collection<RepositoryItem>) 
        promotionStatus.getPropertyValue(promoStatusCouponsPropertyName);
        
      for (RepositoryItem coupon: coupons) {
        
        // Check to see if the coupon is valid for the current site.
        if (claimableManager.checkCouponSite(coupon)) {
          String couponId = (String) 
            coupon.getPropertyValue(claimableManager.getClaimableTools().getIdPropertyName());
          return couponId;
        }
      }
    }

    return null;
  }
}
