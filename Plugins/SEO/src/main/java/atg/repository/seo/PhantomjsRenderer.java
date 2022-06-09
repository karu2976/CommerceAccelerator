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

package atg.repository.seo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import atg.nucleus.logging.ApplicationLoggingImpl;

import com.google.common.base.Function;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.FluentWait;

/**
 * This component renders the content with phantomjs headless browser. 
 * This requires phantomjs should be installed and environment variable is set 
 * to access the executable.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/main/java/atg/repository/seo/PhantomjsRenderer.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class PhantomjsRenderer extends ApplicationLoggingImpl implements MarkupRenderer {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/SEO/src/main/java/atg/repository/seo/PhantomjsRenderer.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /** String to identify the script tags in the returned page content. */
  private final static String SCRIPT_TAGS = "<script.+</script>";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------
  
  //-----------------------------------
  // property: waitTime
  //-----------------------------------
  private int mWaitTime;

  /**
   * @return
   *   Returns wait time for page rendering.
   */
  public int getWaitTime() {
    return mWaitTime;
  }

  /**
   * @param pWaitTime
   *   Set the wait time for page rendering.
   */
  public void setWaitTime(int pWaitTime) {
    mWaitTime = pWaitTime;
  }

  //-----------------------------------
  // property: poleInterval
  //-----------------------------------
  private int mPoleInterval = 2500;

  /**
   * @return
   *   Returns the interval in ms at which the phantom driver is poled for the renderComplete flag.
   *   Defaults to 500ms.
   */
  public int getPoleInterval() {
    return mPoleInterval;
  }

  /**
   * @param pPoleInterval
   *   Set the interval in ms at which the phantom driver is poled for the renderComplete flag.
   */
  public void setPoleInterval(int pPoleInterval) {
    mPoleInterval = pPoleInterval;
  }

  //-----------------------------------
  // property: renderCompleteScript
  //-----------------------------------
  private String mRenderCompleteScript;

  /**
   * @return
   *   Returns the Javascript that will be executed against the client during the fluent wait to
   *   determine if rendering has completed or not.
   */
  public String getRenderCompleteScript() {
    return mRenderCompleteScript;
  }

  /**
   * @param pRenderCompleteScript
   *   Set the Javascript that will be executed against the client during the fluent wait to
   *   determine if rendering has completed or not.
   */
  public void setRenderCompleteScript(String pRenderCompleteScript) {
    mRenderCompleteScript = pRenderCompleteScript;
  }
  
  //-----------------------------------
  // property: phantomExecutablePath
  //-----------------------------------
  private String mPhantomExecutablePath;
  
  /**
   * @return
   *   Returns the path of phantomjs executable path.
   */
  public String getPhantomExecutablePath() {
    return mPhantomExecutablePath;
  }

  /**
   * @param pPhantomExecutablePath
   *   Set phantomjs executable path.
   */
  public void setPhantomExecutablePath(String pPhantomExecutablePath) {
    mPhantomExecutablePath = pPhantomExecutablePath;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------
  
  /**
   * This method creates an instance of phantomjs process and returns the handle.
   * 
   * @return
   *   Returns the phantomjs driver handle.
   */
  public PhantomJSDriver newWebDriver() {
    
    // Settings for phantomjs.
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
      getPhantomExecutablePath());
    capabilities.setJavascriptEnabled(true);
    
    // Creates the driver instance and a phantomjs browser instance will be running.
    PhantomJSDriver phantomDriver = new PhantomJSDriver(capabilities);
    return phantomDriver;
  }

  /**
   *  This method invokes the phantomjs headless browser through 
   *  selenium API and return the rendered content.
   * 
   *  @param  pRequestUrl
   *    Incoming request URL.
   *  @return
   *    Returns the rendered markup.  
   */
  @Override
  public String getHtmlContent(URL pRequestUrl) throws MalformedURLException {
    String pageSource = "";
    PhantomJSDriver phantomDriver = newWebDriver();

    try {

      // Get the page source for the passed URL.
      pageSource = requestPageContent(pRequestUrl, phantomDriver);
    }
    catch (TimeoutException e) {
      vlogError("Timeout occurred while rendering the URL {0} using phantomjs.",
        pRequestUrl.toString());
    }
    finally {

      // Close the driver.
      phantomDriver.quit();
    }
    return pageSource;
  }
  
  /**
   *  This method invokes the phantomjs headless browser through selenium API and return the
   *  rendered content.  It is the responsibility of the caller to close the driver that is passed
   *  to this method.
   * 
   *  @param  pRequestUrl
   *    Incoming request URL.
   *  @return
   *    Returns the rendered markup.  
   */
  public String getHtmlContent(URL pRequestUrl, PhantomJSDriver pPhantomDriver)
    throws MalformedURLException {

    String pageSource = "";
    try {

      // Get the page source for the passed URL.
      pageSource = requestPageContent(pRequestUrl, pPhantomDriver);
    }
    catch (TimeoutException e) {
      vlogError("Timeout occurred while rendering the URL {0} using phantomjs.",
        pRequestUrl.toString());
    }
    return pageSource;
  }

  public String requestPageContent(URL pRequestUrl, PhantomJSDriver pPhantomDriver)
    throws TimeoutException {

    String pageSource = "";

    // Create a fluent wait which will be applied to the phantomDriver in order to allow the
    // Javascript content to be rendered before it is returned.
    FluentWait<PhantomJSDriver> wait =
      new FluentWait<>(pPhantomDriver)
        .withTimeout(getWaitTime(), TimeUnit.MILLISECONDS)
        .pollingEvery(getPoleInterval(), TimeUnit.MILLISECONDS);

    vlogInfo("Rendering the URL [{0}] using phantomjs.", pRequestUrl.toString());

    // Sends a page request.
    pPhantomDriver.get(pRequestUrl.toString());

    // Wait for the Javascript content to be returned before continuing.  This wait will continue
    // to pole the WebDriver until the application signals that JavaScript rendering has completed
    // or the maximum wait time has been reached.
    pageSource = wait.until(new Function<PhantomJSDriver, String>() {
      @Override
      public String apply(PhantomJSDriver phantomJSDriver) {

        // Query the application for the current state of the renderComplete variable.
        Object renderComplete = phantomJSDriver.executeScript(getRenderCompleteScript());
        if (renderComplete instanceof Boolean) {
          if ((Boolean) renderComplete) {

            // Return the fully rendered page source minus the script tags.
            return phantomJSDriver.getPageSource().replaceAll(SCRIPT_TAGS, "");
          }
        }
        return null;
      }
    });

    // return the rendered page source of and empty sting if no content was rendered in time.
    return pageSource;
  }
}
