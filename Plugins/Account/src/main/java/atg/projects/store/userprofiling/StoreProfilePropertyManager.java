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

import atg.commerce.profile.CommercePropertyManager;

/**
 * Extends CommercePropertyManager, used to store profile related properties allowing the property
 * names to be configured in component property files.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/userprofiling/StoreProfilePropertyManager.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreProfilePropertyManager extends CommercePropertyManager {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/userprofiling/StoreProfilePropertyManager.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: newCardNicknamePropertyName
  //-----------------------------------
  private String mNewCardNicknamePropertyName = "newCardNickname";

  /**
   * @return The property used to identify the the nickname of the card.
   */
  public String getNewCardNicknamePropertyName() {
    return mNewCardNicknamePropertyName;
  }

  /**
   * @param pNewCardNicknamePropertyName Set the property used to identify the new card nickname
   * property.
   */
  public void setNewCardNicknamePropertyName(String pNewCardNicknamePropertyName) {
    mNewCardNicknamePropertyName = pNewCardNicknamePropertyName;
  }

  //-----------------------------------
  // property: removeCardPropertyName
  //-----------------------------------
  private String mRemoveCardPropertyName = "removeCardNickname";

  /**
   * @return The property used to identify the card to remove.
   */
  public String getRemoveCardPropertyName() {
    return mRemoveCardPropertyName;
  }

  /**
   * @param pRemoveCardPropertyName Set the property used to identify the card to remove.
   */
  public void setRemoveCardPropertyName(String pRemoveCardPropertyName) {
    mRemoveCardPropertyName = pRemoveCardPropertyName;
  }

  //-----------------------------------
  // property: removeAddressPropertyName
  //-----------------------------------
  private String mRemoveAddressPropertyName = "removeAddressNickname";

  /**
   * @return The property used to identify the address to remove.
   */
  public String getRemoveAddressPropertyName() {
    return mRemoveAddressPropertyName;
  }

  /**
   * @param pRemoveAddressPropertyName Set the property used to identify the address to remove.
   */
  public void setRemoveAddressPropertyName(String pRemoveAddressPropertyName) {
    mRemoveAddressPropertyName = pRemoveAddressPropertyName;
  }

  //-----------------------------------
  // property: newAddressNicknamePropertyName
  //-----------------------------------
  private String mNewAddressNicknamePropertyName = "newAddressNickname";

  /**
   * @return The new address nickname property name. Used when updating an address nickname, as we
   * also need reference to that.
   */
  public String getNewAddressNicknamePropertyName(){
    return mNewAddressNicknamePropertyName;
  }

  /**
   * @param pNewAddressNicknamePropertyName Set the new address nickname property name. Used when
   * updating an address nickname, as we also need reference to that.
   */
  public void setNewAddressNicknamePropertyName(String pNewAddressNicknamePropertyName){
    mNewAddressNicknamePropertyName = pNewAddressNicknamePropertyName;
  }

  //-----------------------------------
  // property: setAsDefaultCardPropertyName
  //-----------------------------------
  private String mSetAsDefaultCardPropertyName = "setAsDefaultCard";

  /**
   * @return The property name that specifies if a card should be made the default card.
   */
  public String getSetAsDefaultCardPropertyName() {
    return mSetAsDefaultCardPropertyName;
  }

  /**
   * @param pSetAsDefaultCardPropertyName Set the property name that specifies if a card should be
   * made the default
   */
  public void setSetAsDefaultCardPropertyName(String pSetAsDefaultCardPropertyName) {
    mSetAsDefaultCardPropertyName = pSetAsDefaultCardPropertyName;
  }

  //-----------------------------------
  // property: addressNicknamePropertyName
  //-----------------------------------
  private String mAddressNicknamePropertyName = "addressNickname";

  /**
   * @return The address nickname property name.
   */
  public String getAddressNicknamePropertyName() {
    return mAddressNicknamePropertyName;
  }

  /**
   * @param pAddressNicknamePropertyName Set the address nickname property name.
   */
  public void setAddressNicknamePropertyName(String pAddressNicknamePropertyName){
    mAddressNicknamePropertyName = pAddressNicknamePropertyName;
  }

  //-----------------------------------
  // property: saveAddressPropertyName
  //-----------------------------------
  private String mSaveAddressPropertyName = "saveAddress";

  /**
   * @return The save address property name.
   */
  public String getSaveAddressPropertyName() {
    return mSaveAddressPropertyName;
  }

  /**
   * @param pSaveAddressPropertyName Set the save address property name.
   */
  public void setSaveAddressPropertyName(String pSaveAddressPropertyName){
    mSaveAddressPropertyName = pSaveAddressPropertyName;
  }
  
  //-----------------------------------
  // useAsDefaultShippingAddressPropertyName
  //-----------------------------------
  private String mUseAsDefaultShippingAddressPropertyName = "useAsDefaultShippingAddress";

  /**
   * @return The property name whose value is used to indicate if an address should be used as
   * the default shipping address.
   */
  public String getUseAsDefaultShippingAddressPropertyName(){
    return mUseAsDefaultShippingAddressPropertyName;
  }

  /**
   * @param pUseAsDefaultShippingAddressPropertyName Set the property whose value indicates if an
   * address should be used as the default shipping address.
   */
  public void setUseAsDefaultShippingAddressPropertyName(String pUseAsDefaultShippingAddressPropertyName){
    mUseAsDefaultShippingAddressPropertyName = pUseAsDefaultShippingAddressPropertyName;
  }

  //-----------------------------------
  // dateOfBirthPropertyName
  //-----------------------------------
  private String mDateOfBirthPropertyName = "dateOfBirth";

  /**
   * @return The property whose value indicates the users date of birth.
   */
  public String getDateOfBirthPropertyName(){
    return mDateOfBirthPropertyName;
  }

  /**
   * @param pDateOfBirthPropertyName Set the property whose value indicates the users date of birth.
   */
  public void setDateOfBirthPropertyName(String pDateOfBirthPropertyName){
    mDateOfBirthPropertyName = pDateOfBirthPropertyName;
  }
}