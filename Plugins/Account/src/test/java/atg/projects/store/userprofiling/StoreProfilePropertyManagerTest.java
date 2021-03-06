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
package atg.projects.store.userprofiling;

import atg.projects.store.userprofiling.StoreProfilePropertyManager;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for atg.projects.store.userprofiling.StoreProfilePropertyManager.
 *
 * @author Oracle
 */
public class StoreProfilePropertyManagerTest {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/test/java/atg/projects/store/userprofiling/StoreProfilePropertyManagerTest.java#1 $$Change: 1385662 $";

  private static StoreProfilePropertyManager mStoreProfilePropertyManager;

  /**
   * One time setup.
   */
  @BeforeClass
  public static void beforeClass(){
    mStoreProfilePropertyManager = new StoreProfilePropertyManager();
  }

  /**
   * New Card nickname
   */
  @Test
  public void testNewCardNicknamePropertyName(){
    mStoreProfilePropertyManager.setNewAddressNicknamePropertyName("newNicknamePropertyName");
    String value = mStoreProfilePropertyManager.getNewAddressNicknamePropertyName();
    assertEquals("newNicknamePropertyName", value);
  }

  /**
   * Remove card
   */
  @Test
  public void testRemoveCardPropertyName(){
    mStoreProfilePropertyManager.setRemoveCardPropertyName("removeCard");
    String value = mStoreProfilePropertyManager.getRemoveCardPropertyName();
    assertEquals("removeCard", value);
  }

  /**
   * Remove address
   */
  @Test
  public void testRemoveAddressPropertyName(){
    mStoreProfilePropertyManager.setRemoveAddressPropertyName("removeAddress");
    String value = mStoreProfilePropertyManager.getRemoveAddressPropertyName();
    assertEquals("removeAddress", value);
  }

  /**
   * New address
   */
  @Test
  public void testNewAddressNicknamePropertyName(){
    mStoreProfilePropertyManager.setNewAddressNicknamePropertyName("newAddress");
    String value = mStoreProfilePropertyManager.getNewAddressNicknamePropertyName();
    assertEquals("newAddress", value);
  }

  /**
   * Default card
   */
  @Test
  public void testSetAsDefaultCardPropertyName(){
    mStoreProfilePropertyManager.setSetAsDefaultCardPropertyName("defaultCard");
    String value = mStoreProfilePropertyManager.getSetAsDefaultCardPropertyName();
    assertEquals("defaultCard", value);
  }

  /**
   * Address nickname
   */
  @Test
  public void testAddressNicknamePropertyName(){
    mStoreProfilePropertyManager.setAddressNicknamePropertyName("addressNickname");
    String value = mStoreProfilePropertyManager.getAddressNicknamePropertyName();
    assertEquals("addressNickname", value);
  }

  /**
   * Default address
   */
  @Test
  public void testUseAsDefaultShippingAddressPropertyName(){
    mStoreProfilePropertyManager.setUseAsDefaultShippingAddressPropertyName("defaultAddress");
    String value = mStoreProfilePropertyManager.getUseAsDefaultShippingAddressPropertyName();
    assertEquals("defaultAddress", value);
  }
}
