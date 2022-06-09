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

package atg.cim.worker.databaseconfig;

import atg.cim.ProductContext;
import atg.cim.SessionContext;
import atg.cim.model.ProductSelection;
import atg.cim.worker.Validator;
import atg.cim.worker.ValidatorException;
import atg.security.opss.csf.GenericCredentialProperties;
import atg.cim.security.opss.utils.OPSSSecurityUtils;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletException;

/**
 * This is the PreDatabaseConfigValidator to validate if key and KeyStore passwords are set in OPSS file.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/CIMExtension/src/main/java/atg/cim/worker/databaseconfig/KeyStoreValidator.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $$Author: jsiddaga $
 */
public class KeyStoreValidator extends Validator {

  /** Class version string */
  public static String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/CIMExtension/src/main/java/atg/cim/worker/databaseconfig/KeyStoreValidator.java#1 $$Change: 1385662 $";
  
  //---------------------------------------------------------------------------
  // Constants
  //---------------------------------------------------------------------------
  
  /** Key store map name */
  public static final String KEY_STORE_MAP = "keyStoreMap";
  
  /** Alias name for keystore password */
  public static final String KEY_STORE_PASSWORD_ALIAS = "atg-key-store";
  
  /** Alias name for encrypt/decrypt key */
  public static final String ENCRYPT_KEY_ALIAS = "creditcardkey";
  
  /** Key store location from dynamo root */
  public static final String KEY_STORE_LOCATION_FROM_DYNAMO_ROOT = "/CommerceAccelerator/Applications/B2CStore/keystore/b2c-store-crypto.jks";
  
  /** Product resource bundle  */
  public static final String PRODUCT_RESOURCES = "atg.cim.ProductResources";
 
  /** Resource bundle */
  private java.util.ResourceBundle resourceBundle = LayeredResourceBundle
    .getBundle(PRODUCT_RESOURCES, Locale.getDefault(), SessionContext.getResourceLoader());

  //-------------------------------------
  // Member variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------
  List<String> mAddOnIds = new ArrayList<>();

  /**
   * Get the list of Add-on ids to validate
   *
   * @return the addOnIds List of Add-on Ids
   */
  public List<String> getAddOnIds() {
    return mAddOnIds;
  }

  /**
   * Sets the required Add-On ID list to validated
   *
   * @param pAddOnIds
   *          List of Add-On Ids the addOnIds to set
   */
  public void setAddOnIds(List<String> pAddOnIds) {
    mAddOnIds = pAddOnIds;
  }
    
  //---------------------------------------------------------------------------
  // Methods
  //---------------------------------------------------------------------------

  /**
   * Checks if it can be validated
   * 
   * @return boolean
   * @see atg.cim.worker.IValidator#canValidate()
   */
  @Override
  public boolean canValidate() {
    if (mAddOnIds == null) {
      return false;
    }
    if (mAddOnIds.size() == 0) {
      return false;
    }
    return true;
  }
  
  /**
   * Check is passwords are set in OPSS and key store exists.
   * 
   * @return boolean
   * @exception ValidatorException
   *   if validation error occurs.
   * @see atg.cim.worker.IValidator#canValidate()
   */  
  @Override
  public boolean validate() throws ValidatorException {

    // We only want to perform the validation if any of the supplied AddOnIds are present in the
    // ProductSelection Add-On id list.  If they are not present then return true.
    if (!checkForAddOns()) {
      return true;
    }
    // Check if key store password is available
    else if (performCheck()) {

      // Also validate if OPSS file was deployed or not
      if (!OPSSSecurityUtils.isDeployed()) {
        mMessage = ResourceUtils.getMsgResource(
          "KeyStoreConfigValidator.java.opssNotDeployed.error",
          PRODUCT_RESOURCES, resourceBundle);
        logDebug(mMessage);
        return false;
      }
      mMessage = ResourceUtils.getMsgResource("KeyStoreConfigValidator.java.success",
        PRODUCT_RESOURCES, resourceBundle);
      logDebug(mMessage);
      return true;
    }
    else {
      mMessage = ResourceUtils.getMsgResource("KeyStoreConfigValidator.java.error",
        PRODUCT_RESOURCES, resourceBundle);
      logDebug(mMessage);
      return false;
    }
  }

  /**
   * Compares the supplied addOnIds list with the currently selected Add-ons. Returns true if at
   * least one of the ids in the addOnIds list are contained in the ProductSelection Add-On Id list.
   *
   * @return boolean
   */
  public boolean checkForAddOns() {
    ProductSelection ps = ProductContext.getProductSelection();
    List<String> selectedAddOnIds = ps.getSelectedAddOnIdList();
    for (String addOn : mAddOnIds) {
      if (selectedAddOnIds.contains(addOn)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Perform the check
   */
  public boolean performCheck() {
    try {

      //Check if key store password is available.
      GenericCredentialProperties keyStorePasswordProperties = 
        (GenericCredentialProperties)OPSSSecurityUtils.retrieveCredentialStoreManager()
        .retrieveCredentialProperties(KEY_STORE_MAP, KEY_STORE_PASSWORD_ALIAS);
      if ((keyStorePasswordProperties == null) || 
        !keyStorePasswordProperties.hasValidCredentialData()) {
        return false;
      } 
       
      //Check if key store's key password is available
      GenericCredentialProperties keyPasswordProperties = 
        (GenericCredentialProperties)OPSSSecurityUtils.retrieveCredentialStoreManager()
        .retrieveCredentialProperties(KEY_STORE_MAP, ENCRYPT_KEY_ALIAS);
      if ((keyPasswordProperties == null) || !keyPasswordProperties.hasValidCredentialData()) {
        return false;
      } 
       
      //Finally validate that key store file is available.
      File f = new File(SessionContext.getDynamoRoot() + KEY_STORE_LOCATION_FROM_DYNAMO_ROOT);
      if (!f.exists()) {
        return false;   
      } 
      return true;
    }
    catch (Exception e) {
      mMessage = ResourceUtils.getMsgResource("KeyStoreConfigValidator.java.error",
        PRODUCT_RESOURCES, resourceBundle);
      getLogger().logError(mMessage, e, this);
      return false;
    }
  }
}