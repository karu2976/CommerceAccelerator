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

package atg.cim.productconfig.endeca;

import atg.cim.SessionContext;
import atg.cim.util.Util;
import atg.cim.worker.common.ReplaceDynamicValuesTask;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;

import java.io.File;
import java.util.Locale;
import java.util.Properties;

/**
 * Configuration writer class for CIM.  This class is used by CIM to control the writing of
 * properties to a property file used during Endeca EAC application creation.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/CIMExtension/src/main/java/atg/cim/productconfig/endeca/CsaEndecaDeployConfigWriterTask.java#1 $$Change: 1385662 $
 */
public class CsaEndecaDeployConfigWriterTask extends ReplaceDynamicValuesTask {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Applications/B2CStore/CIMExtension/src/main/java/atg/cim/productconfig/endeca/CsaEndecaDeployConfigWriterTask.java#1 $$Change: 1385662 $";


  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------
  private static final String APP_NAME_PROP_KEY = "appName";
  private static final String DEPLOYMENT_PATH_PROP_KEY = "deploymentPath";
  private static final String EAC_PORT_PROP_KEY = "eacPort";
  private static final String WORKBENCH_PORT_PROP_KEY = "workbenchPort";
  private static final String WORKBENCH_ENABLED_PROP_KEY = "workbenchEnabled";
  private static final String DGRAPH_PORT_PROP_KEY = "dgraphPort";
  private static final String AUTHORING_PORT_PROP_KEY = "authoringDgraphPort";
  private static final String LOGSERVER_PORT_PROP_KEY = "logServerPort";
  private static final String CAS_PORT_PROP_KEY = "casPort";
  private static final String CAS_ROOT_PROP_KEY = "casRoot";
  private static final String PRODUCTION_APPLICATION_SERVER_HOST_PROP_KEY = "productionAppServerHost";
  private static final String PRODUCTION_APPLICATION_SERVER_PORT_PROP_KEY = "productionAppServerPort";
  private static final String PREVIEW_HOST_PROP_KEY = "previewHost";
  private static final String PREVIEW_PORT_PROP_KEY = "previewPort";
  private static final String PREVIEW_CONTEXT_ROOT_PROP_KEY = "previewContextRoot";
  private static final String USER_SEGMENTS_HOST_PROP_KEY = "userSegmentsHost";
  private static final String USER_SEGMENTS_PORT_PROP_KEY = "userSegmentsPort";
  private static final String JSP_CONFIG_FILE_LOCATION_PROP_KEY = "jpsConfigFileLocation";
  private static final String APPLICATION_CONFIG_EXPORT_ARCHIVE_PROP_KEY = "applicationConfigExportArchive";
  private static final String AUTHORING_APPLICATION_EXPORT_DIRECTORY = "authoringApplicationExportDirectory";

  /** Product resource bundle  */
  public static final String PRODUCT_RESOURCES = "atg.cim.ProductResources";

  /** Resource bundle */
  private java.util.ResourceBundle resourceBundle = LayeredResourceBundle
    .getBundle(PRODUCT_RESOURCES, Locale.getDefault(), SessionContext.getResourceLoader());

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------

  //-------------------------------------
  // property: appName
  //-------------------------------------
  private String mAppName = null;

  /**
   * @return
   *   Return the appName.
   */
  public String getAppName() {
    return mAppName;
  }

  /**
   * @param pAppName
   *   The appName to set.
   */
  public void setAppName(String pAppName) {
    mAppName = pAppName;
  }

  //-------------------------------------
  // property: deploymentPath
  //-------------------------------------
  private String mDeploymentPath = null;

  /**
   * @return
   *   Return the deploymentPath.
   */
  public String getDeploymentPath() {
    return mDeploymentPath;
  }

  /**
   * @param pDeploymentPath
   *   The deploymentPath to set.
   */
  public void setDeploymentPath(String pDeploymentPath) {
    mDeploymentPath = pDeploymentPath;
  }

  //-------------------------------------
  // property: eacPort
  //-------------------------------------
  private int mEacPort = -1;

  /**
   * @return
   *   Return the eacPort.
   */
  public int getEacPort() {
    return mEacPort;
  }

  /**
   * @param pEacPort
   *   The eacPort to set.
   */
  public void setEacPort(int pEacPort) {
    mEacPort = pEacPort;
  }

  //-------------------------------------
  // property: workbenchPort
  //-------------------------------------
  private int mWorkbenchPort = -1;

  /**
   * @return
   *   Return the workbenchPort.
   */
  public int getWorkbenchPort() {
    return mWorkbenchPort;
  }

  /**
   * @param pWorkbenchPort
   *   The workbenchPort to set.
   */
  public void setWorkbenchPort(int pWorkbenchPort) {
    mWorkbenchPort = pWorkbenchPort;
  }

  //-------------------------------------
  // property: workbenchEnabled
  //-------------------------------------
  private boolean mWorkbenchEnabled = true;

  /**
   * @return
   *   Return workbenchEnabled.
   */
  public boolean isWorkbenchEnabled() {
    return mWorkbenchEnabled;
  }

  /**
   * @param pWorkbenchEnabled
   *   The value to set workbenchEnabled
   */
  public void setWorkbenchEnabled(boolean pWorkbenchEnabled) {
    mWorkbenchEnabled = pWorkbenchEnabled;
  }

  //-------------------------------------
  // property: dgraphPort
  //-------------------------------------
  private int mDgraphPort = -1;

  /**
   * @return
   *   Return the dgraphPort.
   */
  public int getDgraphPort() {
    return mDgraphPort;
  }

  /**
   * @param pDgraphPort
   *   The dgraphPort to set.
   */
  public void setDgraphPort(int pDgraphPort) {
    mDgraphPort = pDgraphPort;
  }

  //-------------------------------------
  // property: authoringPort
  //-------------------------------------
  private int mAuthoringPort = -1;

  /**
   * @return
   *   Return the authoringPort.
   */
  public int getAuthoringPort() {
    return mAuthoringPort;
  }

  /**
   * @param pAuthoringPort
   *   The authoringPort to set.
   */
  public void setAuthoringPort(int pAuthoringPort) {
    mAuthoringPort = pAuthoringPort;
  }

  //-------------------------------------
  // property: logserverPort
  //-------------------------------------
  private int mLogserverPort = -1;

  /**
   * @return
   *   Return the logserverPort.
   */
  public int getLogserverPort() {
    return mLogserverPort;
  }

  /**
   * @param pLogserverPort
   *   The logserverPort to set.
   */
  public void setLogserverPort(int pLogserverPort) {
    mLogserverPort = pLogserverPort;
  }

  //-------------------------------------
  // property: casPort
  //-------------------------------------
  private int mCasPort = -1;

  /**
   * @return
   *   Return the casPort.
   */
  public int getCasPort() {
    return mCasPort;
  }

  /**
   * @param pCasPort
   *   The casPort to set.
   */
  public void setCasPort(int pCasPort) {
    mCasPort = pCasPort;
  }

  //-------------------------------------
  // property: casPath
  //-------------------------------------
  private String mCasPath = null;

  /**
   * @return
   *   Return the casPath.
   */
  public String getCasPath() {
    return mCasPath;
  }

  /**
   * @param pCasPath
   *   The casPath to set.
   */
  public void setCasPath(String pCasPath) {
    mCasPath = pCasPath;
  }

  //-------------------------------------
  // property: productionAppServerHost
  //-------------------------------------
  private String mProductionAppServerHost = null;

  /**
   * @return
   *   Return the mProductionAppServerHost.
   */
  public String getProductionAppServerHost() {
    return mProductionAppServerHost;
  }

  /**
   * @param pProductionAppServerHost
   *   The productionAppServerHost to set.
   */
  public void setProductionAppServerHost(String pProductionAppServerHost) {
    mProductionAppServerHost = pProductionAppServerHost;
  }

  //-------------------------------------
  // property: productionAppServerPort
  //-------------------------------------
  private int mProductionAppServerPort = -1;

  /**
   * @return
   *   Return the mProductionAppServerPort.
   */
  public int getProductionAppServerPort() {
    return mProductionAppServerPort;
  }

  /**
   * @param pProductionAppServerPort
   *   The productionAppServerPort to set.
   */
  public void setProductionAppServerPort(int pProductionAppServerPort) {
    mProductionAppServerPort = pProductionAppServerPort;
  }

  //-------------------------------------
  // property: previewHost
  //-------------------------------------
  private String mPreviewHost = null;

  /**
   * @return
   *   Return the mPreviewHost.
   */
  public String getPreviewHost() {
    return mPreviewHost;
  }

  /**
   * @param pPreviewHost
   *   The previewHost to set.
   */
  public void setPreviewHost(String pPreviewHost) {
    mPreviewHost = pPreviewHost;
  }

  //-------------------------------------
  // property: previewPort
  //-------------------------------------
  private int mPreviewPort = -1;

  /**
   * @return
   *   Return the mPreviewPort.
   */
  public int getPreviewPort() {
    return mPreviewPort;
  }

  /**
   * @param pPreviewPort
   *   The previewPort to set.
   */
  public void setPreviewPort(int pPreviewPort) {
    mPreviewPort = pPreviewPort;
  }

  //-------------------------------------
  // property: previewContextRoot
  //-------------------------------------
  private String mPreviewContextRoot = null;

  /**
   * @return
   *   Return the mPreviewContextRoot.
   */
  public String getPreviewContextRoot() {
    return mPreviewContextRoot;
  }

  /**
   * @param pPreviewContextRoot
   *   The previewContextRoot to set.
   */
  public void setPreviewContextRoot(String pPreviewContextRoot) {
    mPreviewContextRoot = pPreviewContextRoot;
  }

  //-------------------------------------
  // property: userSegmentsHost
  //-------------------------------------
  private String mUserSegmentsHost = null;

  /**
   * @return
   *   Return the mUserSegmentsHost.
   */
  public String getUserSegmentsHost() {
    return mUserSegmentsHost;
  }

  /**
   * @param pUserSegmentsHost
   *   The UserSegmentsHost to set.
   */
  public void setUserSegmentsHost(String pUserSegmentsHost) {
    mUserSegmentsHost = pUserSegmentsHost;
  }

  //-------------------------------------
  // property: userSegmentsPort
  //-------------------------------------
  private int mUserSegmentsPort = -1;

  /**
   * @return
   *   Return the mUserSegmentsPort
   */
  public int getUserSegmentsPort() {
    return mUserSegmentsPort;
  }

  /**
   * @param pUserSegmentsPort
   *   The UserSegmentsHost to set
   */
  public void setUserSegmentsPort(int pUserSegmentsPort) {
    mUserSegmentsPort = pUserSegmentsPort;
  }

  //-------------------------------------
  // property: jpsConfigFileLocation
  //-------------------------------------
  private String mJpsConfigFileLocation = null;

  /**
   * @return
   *   Return the mJpsConfigFileLocation.
   */
  public String getJpsConfigFileLocation() {
    return mJpsConfigFileLocation;
  }

  /**
   * @param pJpsConfigFileLocation
   *   the jpsConfigFileLocation to set.
   */
  public void setJpsConfigFileLocation(String pJpsConfigFileLocation) {
    mJpsConfigFileLocation = pJpsConfigFileLocation;
  }

  //-------------------------------------
  // property: applicationConfigExportArchive
  //-------------------------------------
  private String mApplicationConfigExportArchive = null;

  /**
   * @return
   *   Return the mApplicationConfigExportArchive.
   */
  public String getApplicationConfigExportArchive() {
    return mApplicationConfigExportArchive;
  }

  /**
   * @param pApplicationConfigExportArchive
   *   The applicationConfigExportArchive to set.
   */
  public void setApplicationConfigExportArchive(String pApplicationConfigExportArchive) {
    mApplicationConfigExportArchive = pApplicationConfigExportArchive;
  }

  //-------------------------------------
  // property: authoringApplicationExportDirectory
  //-------------------------------------
  private String mAuthoringApplicationExportDirectory = null;

  /**
   * @return
   *   Return the mAuthoringApplicationExportDirectory.
   */
  public String getAuthoringApplicationExportDirectory() {
    return mAuthoringApplicationExportDirectory;
  }

  /**
   * @param pAuthoringApplicationExportDirectory
   *   The authoringApplicationExportDirectory to set.
   */
  public void setAuthoringApplicationExportDirectory(String pAuthoringApplicationExportDirectory) {
    mAuthoringApplicationExportDirectory = pAuthoringApplicationExportDirectory;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * @return
   * @see atg.cim.worker.common.ReplaceDynamicValuesTask#getProperties()
   */
  @Override
  public Properties getProperties() {
    Properties props = new Properties();
    props.setProperty(APP_NAME_PROP_KEY, getAppName());
    props.setProperty(DEPLOYMENT_PATH_PROP_KEY,
      Util.replaceBackSlashesWithForwardSlashes(getDeploymentPath()));
    props.setProperty(EAC_PORT_PROP_KEY, String.valueOf(getEacPort()));
    props.setProperty(WORKBENCH_PORT_PROP_KEY,
      String.valueOf(getWorkbenchPort()));
    props.setProperty(WORKBENCH_ENABLED_PROP_KEY,
      String.valueOf(isWorkbenchEnabled()));
    props.setProperty(DGRAPH_PORT_PROP_KEY, String.valueOf(getDgraphPort()));
    props.setProperty(AUTHORING_PORT_PROP_KEY, String.valueOf(getAuthoringPort()));
    props.setProperty(LOGSERVER_PORT_PROP_KEY, String.valueOf(getLogserverPort()));
    props.setProperty(CAS_PORT_PROP_KEY, String.valueOf(getCasPort()));
    props.setProperty(CAS_ROOT_PROP_KEY,
      Util.replaceBackSlashesWithForwardSlashes(getCasPath()));
    props.setProperty(PRODUCTION_APPLICATION_SERVER_HOST_PROP_KEY,
      getProductionAppServerHost());
    props.setProperty(PRODUCTION_APPLICATION_SERVER_PORT_PROP_KEY,
      String.valueOf(getProductionAppServerPort()));
    props.setProperty(PREVIEW_HOST_PROP_KEY, getPreviewHost());
    props.setProperty(PREVIEW_PORT_PROP_KEY, String.valueOf(getPreviewPort()));
    props.setProperty(PREVIEW_CONTEXT_ROOT_PROP_KEY, getPreviewContextRoot());
    if (getUserSegmentsHost() != null) {
      props.setProperty(USER_SEGMENTS_HOST_PROP_KEY, getUserSegmentsHost());
    }
    props.setProperty(USER_SEGMENTS_PORT_PROP_KEY, String.valueOf(getUserSegmentsPort()));
    props.setProperty(JSP_CONFIG_FILE_LOCATION_PROP_KEY, this.getJpsConfigFileLocation());
    props.setProperty(APPLICATION_CONFIG_EXPORT_ARCHIVE_PROP_KEY,
      getApplicationConfigExportArchive());
    props.setProperty(AUTHORING_APPLICATION_EXPORT_DIRECTORY,
      getAuthoringApplicationExportDirectory());

    return props;
  }

  /**
   * @return
   * @see atg.cim.worker.ITask#canExecute()
   */
  @Override
  public boolean canExecute() {

    if (Util.isNullOrBlank(getAppName())) {
      setMessage("Application Name cannot be null.");
      return false;
    }
    if (Util.isNullOrBlank(getDeploymentPath())) {
      setMessage("Deployment Path cannot be null.");
      return false;
    }

    boolean success = new File(getDeploymentPath()).mkdirs();
    if(!success){
      // Directory may already exist, log a warning.
      logWarning(ResourceUtils.getMsgResource("warning.mkdirsFail",
        PRODUCT_RESOURCES, resourceBundle, getDeploymentPath()));
    }

    if (getEacPort() <= 0) {
      setMessage("EAC Port must be set");
      return false;
    }
    if (getWorkbenchPort() <= 0) {
      setMessage("Workbench Port must be set");
      return false;
    }
    if (getDgraphPort() <= 0) {
      setMessage("Dgraph Port must be set");
      return false;
    }
    if (getAuthoringPort() <= 0) {
      setMessage("Authoring Dgraph Port must be set");
      return false;
    }
    if (getLogserverPort() <= 0) {
      setMessage("Log Server Port must be set");
      return false;
    }
    if (getCasPort() <= 0) {
      setMessage("CAS Port must be set");
      return false;
    }
    if (Util.isNullOrBlank(getCasPath())) {
      setMessage("CAS Path must be set");
      return false;
    }
    if (Util.isNullOrBlank(getProductionAppServerHost())) {
      setMessage("Production application server host name must be set");
      return false;
    }
    if (getProductionAppServerPort() <= 0) {
      setMessage("Production application server port number must be set");
      return false;
    }
    if (Util.isNullOrBlank(getPreviewHost())) {
      setMessage("Preview host name must be set");
    }
    if (getPreviewPort() <= 0) {
      setMessage("Preview port number must be set");
    }
    if (Util.isNullOrBlank(getPreviewContextRoot())) {
      setMessage("Preview context root must be set");
    }
    if (Util.isNullOrBlank(getUserSegmentsHost())) {
      setMessage("User segments host name must be set");
    }
    if (getUserSegmentsPort() <= 0) {
      setMessage("User segments port number must be set");
    }
    if (Util.isNullOrBlank(getJpsConfigFileLocation())) {
      setMessage("The jps-config.xml file location must be set");
      return false;
    }
    if (Util.isNullOrBlank(this.getApplicationConfigExportArchive())) {
      setMessage("The application configuration export archive directory must be set");
      return false;
    }
    if (Util.isNullOrBlank(this.getAuthoringApplicationExportDirectory())) {
      setMessage("The authoring application export directory must be set");
      return false;
    }
    return super.canExecute();
  }

}
