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

package atg.projects.store.servlet;

import atg.nucleus.logging.ApplicationLogging;
import atg.nucleus.logging.ClassLoggingFactory;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.RequestLocale;

import java.util.Locale;

/**
 * Preview extension of StoreRequestLocale.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/servlet/StorePreviewRequestLocale.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StorePreviewRequestLocale extends StoreRequestLocale {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/servlet/StorePreviewRequestLocale.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------

  /** Logging component */
  private static final ApplicationLogging mLogger =
    ClassLoggingFactory.getFactory().getLoggerForClass(StoreRequestLocale.class);

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * Obtains the locale from the http request. This preview specific method ensures
   * that if the locale hasn't been found after it has checked the current path,
   * the passed in RequestLocale is used. If the RequestLocale's locale is null,
   * normal processing (as in the super class) occurs.
   *
   * @param pRequest DynamoHttpServletRequest
   *    The HTTP request object.
   * @param pRequestLocale
   *   The request locale object
   *
   * @return
   *   The locale object.
   */
  @Override
  public Locale discernRequestLocale(DynamoHttpServletRequest pRequest, RequestLocale pRequestLocale) {

    // Try to retrieve the locale from the 'locale' query parameter. This will likely
    // have been set on a rest request URL path or some other non-standard URL.
    // E.g. /rest/model/atg/blah?locale=en_US
    Locale locale = fillLocaleFromLangSelection(pRequest);

    if (locale == null) {

      // Try to retrieve the locale from the request URL path.
      // E.g. /csa/storeus/en/browse
      locale = getLocaleFromPath(pRequest);
    }

    if (locale == null && pRequestLocale.getLocaleString() != null) {
      locale = pRequestLocale.getLocale();
    }

    // The locale should have been retrieved by now for a multi-language EAC app set-up.
    // The following locale processing will only really need to happen for single locale sites.

    if (locale == null) {

      // Try to retrieve the default locale for the current site.
      locale = RequestLocale.getCachedLocale(getDefaultSiteLocaleCode());
    }

    if (locale == null) {
      locale = super.discernRequestLocale(pRequest, pRequestLocale);
    }

    if (isLoggingDebug()) {
      mLogger.logDebug(locale + " will be used as the current RequestLocale.");
    }

    return locale;
  }
}
