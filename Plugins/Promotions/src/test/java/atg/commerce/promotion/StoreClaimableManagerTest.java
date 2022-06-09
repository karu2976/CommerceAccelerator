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

import atg.adapter.gsa.GSARepository;
import atg.adapter.gsa.xml.TemplateParser;
import atg.commerce.CommerceException;
import atg.commerce.claimable.ClaimableTools;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderImpl;
import atg.commerce.order.OrderTools;
import atg.commerce.pricing.PricingModelHolder;
import atg.multisite.SiteContextException;
import atg.multisite.SiteContextManager;
import atg.nucleus.Nucleus;
import atg.nucleus.NucleusTestUtils;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletTestUtils;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * This unit test will test the methods of the StoreClaimableManager class.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Promotions/src/test/java/atg/commerce/promotion/StoreClaimableManagerTest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreClaimableManagerTest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Promotions/src/test/java/atg/commerce/promotion/StoreClaimableManagerTest.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // STATIC
  //---------------------------------------------------------------------------

  private static final String CLAIMABLE_MANAGER_PATH = "/atg/commerce/claimable/ClaimableManager";
  private static final String PROFILE = "/atg/userprofiling/Profile";
  private static final String ORDER_TOOLS = "/atg/commerce/order/OrderTools";
  private static final String USER_PRICING_MODELS = "/atg/commerce/pricing/UserPricingModels";
  private static final String PROFILE_TOOLS_PATH = "/atg/userprofiling/ProfileTools";
  private static final String SHOPPING_CART = "/atg/commerce/ShoppingCart";

  private static Nucleus mNucleus = null;
  private static StoreClaimableManager mStoreClaimableManager = null;
  private static OrderTools mOrderTools = null;
  private static SiteContextManager mSiteContextManager;
  private static ProfileTools mProfileTools = null;

  private static GSARepository mClaimableRepository = null;
  private static GSARepository mProductCatalog = null;
  private static GSARepository mSiteRepository = null;
  private static GSARepository mPricelistsRepository = null;

  //---------------------------------------------------------------------------
  // JUNIT SETUP
  //---------------------------------------------------------------------------

  /**
   * One time setup.
   */
  @BeforeClass
  public static void beforeClass() throws ServletException, SiteContextException {
    mNucleus = NucleusTestUtils.startNucleusWithModules(
      new String[]{"CommerceAccelerator.Plugins.Promotions"},
      StoreClaimableManagerTest.class, "StoreClaimableManagerTest", "/atg/Initial");

    mProductCatalog = (GSARepository) mNucleus.resolveName("/atg/commerce/catalog/ProductCatalog");
    String[] dataFileNames = { "catalog.xml" };
    TemplateParser.loadTemplateFiles(mProductCatalog, 1, dataFileNames,
      true, new PrintWriter(System.out), null);

    mPricelistsRepository = (GSARepository) mNucleus.resolveName("/atg/commerce/pricing/priceLists/PriceLists");
    String[] pricelistsDataFileNames = { "pricelists.xml" };
    TemplateParser.loadTemplateFiles(mPricelistsRepository, 1,
      pricelistsDataFileNames, true, new PrintWriter(System.out), null);

    mSiteRepository = (GSARepository) mNucleus.resolveName("/atg/multisite/SiteRepository", true);
    String[] siteDataFileNames = { "sites.xml" };
    TemplateParser.loadTemplateFiles(mSiteRepository, 1, siteDataFileNames,
      true, new PrintWriter(System.out), null);

    mSiteContextManager = (SiteContextManager) mNucleus.resolveName("/atg/multisite/SiteContextManager", true);
    mSiteContextManager.pushSiteContext(mSiteContextManager.getSiteContext("storeSiteUS"));

    String[] promoDataFileNames = { "promotions.xml" };
    TemplateParser.loadTemplateFiles(mProductCatalog, 1, promoDataFileNames,
      true, new PrintWriter(System.out), null);

    mClaimableRepository = (GSARepository) mNucleus.resolveName("/atg/commerce/claimable/ClaimableRepository");
    String[] claimableDataFileNames = { "claimable.xml" };
    TemplateParser.loadTemplateFiles(mClaimableRepository, 1,
      claimableDataFileNames, true, new PrintWriter(System.out), null);

    mStoreClaimableManager = (StoreClaimableManager) mNucleus.resolveName(CLAIMABLE_MANAGER_PATH, true);
    assertNotNull(mStoreClaimableManager);

    mOrderTools = (OrderTools) mNucleus.resolveName(ORDER_TOOLS, true);
    assertNotNull(mOrderTools);

    mProfileTools = (ProfileTools) mNucleus.resolveName(PROFILE_TOOLS_PATH, true);
  }

  /**
   * One time tear down.
   */
  @AfterClass
  public static void afterClass() throws IOException, ServiceException {
    if(mNucleus != null) {
      NucleusTestUtils.shutdownNucleus(mNucleus);
      mNucleus = null;
    }
  }

  /**
   * Set up a new profile every time
   */
  @Before
  public void before(){
    // Set the current request & profile
    ServletTestUtils utils = new ServletTestUtils();
    DynamoHttpServletRequest request = utils.createDynamoHttpServletRequestForSession(mNucleus, null, null);
    ServletUtil.setCurrentRequest(request);
    Profile profile = (Profile) request.resolveName(PROFILE);
    ServletUtil.setCurrentUserProfile(profile);
    mProfileTools.createNewUser("user", profile);

    OrderHolder cart = (OrderHolder) request.resolveName(SHOPPING_CART);
    Order current = cart.getCurrent();
    assertNotNull(current);
  }

  //---------------------------------------------------------------------------
  // TESTS
  //---------------------------------------------------------------------------

  @Test
  public void testRemoveCoupon() throws CommerceException, RepositoryException {
    // This is our coupon
    RepositoryItem coupon = mClaimableRepository.getItem("FREESHIP");
    assertNotNull(coupon);

    // This is the promotion associated with the coupon
    Set promotions = (Set) coupon.getPropertyValue("promotions");
    assertNotNull(promotions);
    assertFalse(promotions.isEmpty());

    // This is the profile id
    String profileId = getProfile().getRepositoryId();
    assertNotNull(profileId);

    // Claim the coupon the make sure its status is claimed.
    mStoreClaimableManager.claimCoupon(profileId, "FREESHIP");
    RepositoryItem claimedCoupon = mStoreClaimableManager.findAndClaimCoupon("FREESHIP");
    assertEquals(ClaimableTools.CLAIMED, claimedCoupon.getPropertyValue("status"));

    // Make sure the promotion is on the profile.
    List activePromotions = (List) getProfile().getPropertyValue("activePromotions");
    assertNotNull(activePromotions);
    assertFalse(activePromotions.isEmpty());

    // Remove the coupon
    mStoreClaimableManager.removeCoupon("FREESHIP", null, getProfile(), getPricingModelHolder(), null);

    // Make sure the promotion was removed from the profile.
    activePromotions = (List) getProfile().getPropertyValue("activePromotions");
    assertNotNull(activePromotions);
    assertTrue(activePromotions.isEmpty());
  }

  @Test
  public void testGetCouponCode() throws RepositoryException, CommerceException {
    // This is our coupon
    RepositoryItem coupon = mClaimableRepository.getItem("FREESHIP");
    assertNotNull(coupon);

    // This is the promotion associated with the coupon
    Set promotions = (Set) coupon.getPropertyValue("promotions");
    assertNotNull(promotions);
    assertFalse(promotions.isEmpty());

    // This is the profile id
    String profileId = getProfile().getRepositoryId();
    assertNotNull(profileId);

    // Claim the coupon the make sure its status is claimed.
    mStoreClaimableManager.claimCoupon(profileId, "FREESHIP");
    RepositoryItem claimedCoupon = mStoreClaimableManager.findAndClaimCoupon("FREESHIP");
    assertEquals(ClaimableTools.CLAIMED, claimedCoupon.getPropertyValue("status"));

    // Make sure the promotion is on the profile.
    List activePromotions = (List) getProfile().getPropertyValue("activePromotions");
    assertNotNull(activePromotions);
    assertFalse(activePromotions.isEmpty());

    // Get the coupon code that's currently applied.
    String couponCode = mStoreClaimableManager.getCouponCode(getOrder());
    assertEquals("FREESHIP", couponCode);
  }

  //---------------------------------------------------------------------------
  // UTILITY
  //---------------------------------------------------------------------------

  /**
   * @return The current profile
   */
  public RepositoryItem getProfile(){
    return ServletUtil.getCurrentUserProfile();
  }

  /**
   * @return The pricing model holder.
   */
  public PricingModelHolder getPricingModelHolder(){
    return (PricingModelHolder) ServletUtil.getCurrentRequest().resolveName(USER_PRICING_MODELS);
  }

  /**
   * @return The current order.
   * @throws CommerceException
   */
  private OrderImpl getOrder() throws CommerceException {
    OrderHolder cart = (OrderHolder) ServletUtil.getCurrentRequest().resolveName(SHOPPING_CART);
    return (OrderImpl) cart.getCurrent();
  }

}
