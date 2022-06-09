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

package atg.cim.worker.base;

import static atg.cim.Constants.RESET_ADMIN_PASSWORD_WIZARDID;
import atg.cim.command.CommandType;
import atg.cim.model.Command;
import atg.cim.model.Label;
import atg.cim.model.NavigationOption;
import atg.cim.SessionContext;
import atg.cim.status.StatusMonitorStore;
import atg.cim.util.Util;
import atg.cim.worker.INavigationOptionProvider;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class creates menu option for key store configuration under custom plugin launcher.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/CIMExtension/src/main/java/atg/cim/worker/base/KeyStorePasswordMenuNavigation.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $$Author: jsiddaga $
 */

public class KeyStorePasswordMenuNavigation implements INavigationOptionProvider {

  /** Class version string */
  public static String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/CIMExtension/src/main/java/atg/cim/worker/base/KeyStorePasswordMenuNavigation.java#1 $$Change: 1385662 $";
  
  //---------------------------------------------------------------------------
  // Constants
  //---------------------------------------------------------------------------

  /** Key store configuration wizard id */
  public static final String KEYSTORE_CONFIGURATION_WIZARDID = "KeyStoreConfigurationWizard";
 
  /** Product resource bundle  */
  public static final String PRODUCT_RESOURCES = "atg.cim.ProductResources";
  
  /** Resource bundle */
  private java.util.ResourceBundle resourceBundle = LayeredResourceBundle
    .getBundle(PRODUCT_RESOURCES, Locale.getDefault(), SessionContext.getResourceLoader());
  
  //---------------------------------------------------------------------------
  // Methods
  //---------------------------------------------------------------------------

  /**
   * This method creates menu option for key store configuration.
   * 
   * @return 
   *   List of navigation option.
   */     
  @Override
  public List<NavigationOption> provide() {
    StatusMonitorStore statusMonStore = StatusMonitorStore.getInstance();
    List<NavigationOption> navOptions = new ArrayList<NavigationOption>();
    
    // next step command used by all of the nav options
    Command nextStep = new Command();
    nextStep.setType(CommandType.NEXT_STEP);
    
    NavigationOption keyStoreConfigWizard = new NavigationOption();
    statusMonStore.newStatusMonitor(KEYSTORE_CONFIGURATION_WIZARDID);
    boolean keyStoreConfDone = statusMonStore.isDone(KEYSTORE_CONFIGURATION_WIZARDID);
    keyStoreConfigWizard.setId("keyStoreConfig");
    Label keyStoreConfigLabel = new Label();
    String keyStoreLabelTxt = ResourceUtils.getMsgResource("keyStorePasswordTask.java.option.title",
      PRODUCT_RESOURCES, resourceBundle);
    keyStoreConfigLabel.setValue(keyStoreLabelTxt);
    keyStoreConfigWizard.setDisplayLabel(keyStoreConfigLabel);
    if (keyStoreConfDone) {
      Label keyStoreDescpLbl = new Label();
      keyStoreDescpLbl.setValue(ResourceUtils.getMsgResource("keyStorePasswordTask.java.common.done", 
        PRODUCT_RESOURCES, resourceBundle));
      keyStoreConfigWizard.setDescription(keyStoreDescpLbl);
    }
    Command launchKeyStoreConfigWizard = new Command();
    launchKeyStoreConfigWizard.setType(CommandType.LAUNCH_WIZARD);
    launchKeyStoreConfigWizard.setValue(KEYSTORE_CONFIGURATION_WIZARDID);

    keyStoreConfigWizard.setDefaultOption(true);
    navOptions.add(keyStoreConfigWizard);

    keyStoreConfigWizard.addCommand(launchKeyStoreConfigWizard);
    keyStoreConfigWizard.addCommand(nextStep);
    
    return navOptions;
  }
}
