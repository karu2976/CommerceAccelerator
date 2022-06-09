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
package atg.projects.store.filter.bean;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.nucleus.GenericService;
import atg.projects.store.i18n.CustomDateFormatter;
import atg.service.filter.bean.BeanFilterException;
import atg.service.filter.bean.PropertyCustomizer;
import atg.servlet.ServletUtil;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * A Property customizer used to return a date property in a localized format. The local is obtained
 * from the current request. The dateFormatter component is used to format dates.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/filter/bean/LocalizedDatePropertyCustomizer.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class LocalizedDatePropertyCustomizer extends GenericService implements PropertyCustomizer {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/filter/bean/LocalizedDatePropertyCustomizer.java#1 $$Change: 1385662 $";


  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: dateFormatter
  //-----------------------------------
  private CustomDateFormatter mDateFormatter = null;

  /**
   * @return
   *   Component used to format dates with custom patterns.
   */
  public CustomDateFormatter getDateFormatter() {
    return mDateFormatter;
  }

  /**
   * @param pDateFormatter
   *   Set a component used to format dates with custom patterns.
   */
  public void setDateFormatter(CustomDateFormatter pDateFormatter) {
    mDateFormatter = pDateFormatter;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Returns a date String in a locale specific format. A format parameter can be passed
   * in the pAttributes map which controls the date pattern, e.g MM/dd/yyyy. The returned
   * date String will be formatted in a locale specific way using the pattern.
   *
   * @param pTargetObject
   *   The object which the specified property is associated with.
   * @param pPropertyName
   *   The name of the property to return.
   * @param pAttributes
   *   The key/value pair attributes defined in the beanFilteringConfiguration.xml file
   *   for this property.
   *
   * @return
   *   A date String in a locale specific format.
   *
   * @throws BeanFilterException
   */
  @Override
  public Object getPropertyValue(Object pTargetObject, String pPropertyName,
      Map<String,String> pAttributes) throws BeanFilterException {

    // Get date value that needs formatted.
    Object propValue = null;

    try {
      propValue = DynamicBeans.getPropertyValue(pTargetObject, pPropertyName);
    }
    catch (PropertyNotFoundException e) {
      throw new BeanFilterException(e);
    }

    // Make sure it's a date.
    if (!(propValue instanceof Date)) {
      vlogDebug("Property {0} was not a date: {1}", pPropertyName, propValue);
      return null;
    }

    // Get the locale and pattern to use for formatting.
    Locale locale = ServletUtil.getUserLocale();
    return getDateFormatter().getLocalizedDateString((Date) propValue, locale);
  }
}
