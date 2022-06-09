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

package atg.cim.productconfig.productselector;

import atg.cim.SessionContext;
import atg.cim.security.opss.utils.OPSSSecurityUtils;
import atg.cim.status.StatusMonitorStore;
import atg.cim.worker.IProgressMonitor;
import atg.cim.worker.SecureWorkerTask;
import atg.cim.worker.TaskException;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.crypto.KeyStoreKeyManager;
import atg.crypto.SecretKeyGenerator;
import atg.security.opss.csf.GenericCredentialProperties;

import java.io.File;
import java.util.Locale;

import javax.crypto.SecretKey;

/**
 * This task has one responsibility and that's to store the hashed password of keystore and 
 * encrypt/decrypt key password into CSF wallet for CSA module to use later.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/CIMExtension/src/main/java/atg/cim/productconfig/productselector/KeyStorePasswordTask.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $$Author: jsiddaga $
 */
public class KeyStorePasswordTask extends SecureWorkerTask {

  /** Class version string */
  public static String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/CIMExtension/src/main/java/atg/cim/productconfig/productselector/KeyStorePasswordTask.java#1 $$Change: 1385662 $";
  
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
 
  //---------------------------------------------------------------------------
  // Methods
  //---------------------------------------------------------------------------
    
  /**
   * @see atg.cim.worker.ITask#canExecute()
   */
  @Override
  public boolean canExecute() {
    return isValidSecureTask() ? true : false;
  }

  /**
   * @see atg.cim.worker.ITask#execute(atg.cim.worker.IProgressMonitor)
   */
  @Override
  public void execute(IProgressMonitor pProgressMonitor) throws TaskException {
    try {
       String keyStorePassword = "";
       String decryptEncryptPassword = "";
       GenericCredentialProperties keyStorePasswordProperties = 
         (GenericCredentialProperties)OPSSSecurityUtils.retrieveCredentialStoreManager()
         .retrieveCredentialProperties(KEY_STORE_MAP, KEY_STORE_PASSWORD_ALIAS);
       if ((keyStorePasswordProperties != null) && 
         keyStorePasswordProperties.hasValidCredentialData()) {
         keyStorePassword = (String)keyStorePasswordProperties.getSingleCredential();
       } 
       GenericCredentialProperties keyPasswordProperties = 
         (GenericCredentialProperties)OPSSSecurityUtils.retrieveCredentialStoreManager()
         .retrieveCredentialProperties(KEY_STORE_MAP, ENCRYPT_KEY_ALIAS);
       if ((keyPasswordProperties != null) && keyPasswordProperties.hasValidCredentialData()) {
         decryptEncryptPassword = (String)keyPasswordProperties.getSingleCredential();
       }
       
       KeyStoreKeyManager keyManager = new KeyStoreKeyManager();
       // Specify provider and store type
       keyManager.setSecurityProviders(new String[]{"atg.crypto.provider.AtgCE"});
       keyManager.setStoreType("SKS");
       keyManager.setProvider("AtgCE");
       
       File keyStoreFile = 
         new File(SessionContext.getDynamoRoot() + KEY_STORE_LOCATION_FROM_DYNAMO_ROOT);
       
       // Check and create all necessary directories.
       if (!keyStoreFile.getParentFile().exists()) {
         boolean success = keyStoreFile.getParentFile().mkdirs();
         if(!success){
           SessionContext.getLogger().logWarning(ResourceUtils.getMsgResource("warning.mkdirsFail",
             PRODUCT_RESOURCES, resourceBundle, keyStoreFile.getParentFile().getPath()), this);
         }
       }
       // Delete current old file if available to start fresh.
       if(keyStoreFile.exists()){
         keyStoreFile.delete();
       }
       keyManager.setFile(keyStoreFile);
       keyManager.setPassword(keyStorePassword);
       // Initialize key store manager
       keyManager.doStartService();
       
       SecretKeyGenerator generator = new SecretKeyGenerator();
       String javavendor = System.getProperty("java.vendor");
       if(javavendor != null && javavendor.toUpperCase().contains("IBM")){
         generator.setProvider("IBMJCE");
         
         // Following properties are used by Cipher service at the time of data load
         SessionContext.getLookupPersistence().put("CSACipherSecurityProviders", "com.ibm.crypto.provider.IBMJCE");
         SessionContext.getLookupPersistence().put("CSACipherProvider", "IBMJCE");
       } else {
         generator.setProvider("SunJCE");
         
         // Following properties are used by Cipher service at the time of data load
         SessionContext.getLookupPersistence().put("CSACipherSecurityProviders", "com.sun.crypto.provider.SunJCE");
         SessionContext.getLookupPersistence().put("CSACipherProvider", "SunJCE");
       }
       SecretKey skey = generator.generateSecretKey();
       keyManager.setKey(ENCRYPT_KEY_ALIAS, skey, decryptEncryptPassword.toCharArray());
       keyManager.storeKeyStore();
       //Also clear the deploy status so that user is forced to deploy OPSs file again.
       OPSSSecurityUtils.clearDeployStatus();
    } catch (Exception e) {
      mMessage = ResourceUtils.getMsgResource("KeyStorePasswordTask.java.exception.detail", PRODUCT_RESOURCES, resourceBundle);
      logError(mMessage);
    }
    if (pProgressMonitor != null) {
      pProgressMonitor.setTaskComplete(true);
    }
    StatusMonitorStore.getInstance().setWorkDone("AddKeyStorePasswordTemplateProgress", 1);
  }

  /** 
   * @see atg.cim.worker.ITask#canUndo()
   */
  @Override
  public boolean canUndo() {
    return false;
  }

  /**
   * @see atg.cim.worker.ITask#undo(atg.cim.worker.IProgressMonitor)
   */
  @Override
  public void undo(IProgressMonitor progressMonitor) throws TaskException {
  }
}