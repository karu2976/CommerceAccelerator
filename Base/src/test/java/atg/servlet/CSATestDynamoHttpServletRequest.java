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

package atg.servlet;

import atg.nucleus.naming.ParameterName;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Wrapper for the DynamoHttpServletRequest.
 *
 * This class provides methods that allow developers to manipulate otherwise
 * unobtainable properties for unit testing purposes.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/servlet/CSATestDynamoHttpServletRequest.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class CSATestDynamoHttpServletRequest extends DynamoHttpServletRequest {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/test/java/atg/servlet/CSATestDynamoHttpServletRequest.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // FINAL MEMBERS
  //----------------------------------------------------------------------------

  private final Map<String, String> cookieParameters = new HashMap();
  private final Map<String, String> headers = new HashMap();
  private final Map<String, Object> parameters = new HashMap();
  private final Map<String, Object> attributes = new HashMap();

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * Encode a URL.
   *
   * @param pUrl
   *   The URL to be encoded.
   * @return
   *   The encoded URL.
   */
  public String encodeURL(String pUrl) {
    return pUrl + ";sessionId=" + System.currentTimeMillis();
  }

  /**
   * Retrieve a particular request attribute.
   *
   * @param pAttribute
   *   The name of the attribute.
   * @return
   *   The value of the attribute.
   */
  public Object getAttribute(String pAttribute) {
    return this.attributes.get(pAttribute);
  }

  /**
   * @return
   *   Retrieve all request attribute names.
   */
  public Enumeration getAttributeNames() {
    Properties properties = new Properties();
    Iterator it = this.attributes.keySet().iterator();
    while (it.hasNext()) {
      String s = (String)it.next();
      properties.put(s, s);
    }
    return properties.elements();
  }

  /**
   * Retrieve a particular cookie.
   *
   * @param pParameter
   *   The name of the cookie to be retrieved.
   * @return
   *   The cookie value associated with the supplied name.
   */
  public String getCookieParameter(String pParameter) {
    return ((String)this.cookieParameters.get(pParameter));
  }

  /**
   * Set a new cookie parameter.
   *
   * @param pName
   *   The name of the cookie to be set.
   * @@param pValue
   *   The cookie value associated with the supplied name.
   */
  public void setCookieParameter(String pName, String pValue) {
    this.cookieParameters.put(pName, pValue);
  }

  /**
   * Retrieve a particular header value.
   *
   * @param pParam
   *   The header parameter to retrieve.
   * @return
   *   The header value.
   */
  public String getHeader(String pParam) {
    return ((String)this.headers.get(pParam));
  }

  /**
   * Retrieve the value of a particular local parameter.
   *
   * @param pName
   *   The name of the local parameter to retrieve.
   * @return
   *   The value of the local parameter.
   */
  public Object getLocalParameter(String pName) {
    return this.parameters.get(pName);
  }

  /**
   * Retrieve the value of a particular object parameter.
   *
   * @param pName
   *   The name of the object parameter to retrieve.
   * @return
   *   The value of the object parameter.
   */
  public Object getObjectParameter(ParameterName pName) {
    return getObjectParameter(pName.getName());
  }

  /**
   * Retrieve the value of a particular object parameter.
   *
   * @param pName
   *   The name of the object parameter to retrieve.
   * @return
   *   The value of the object parameter.
   */
  public Object getObjectParameter(String pName) {
    return this.parameters.get(pName);
  }

  /**
   * Retrieve the value of a particular parameter.
   *
   * @param pName
   *   The name of the parameter to retrieve.
   * @return
   *   The value of the parameter.
   */
  public String getParameter(String pName) {
    return ((String)this.parameters.get(pName));
  }

  /**
   * Remove a particular request attribute.
   *
   * @param pName
   *   The name of the request attribute to remove.
   */
  public void removeAttribute(String pName) {
    this.attributes.remove(pName);
  }

  /**
   * Add a new request attribute.
   *
   * @param pName
   *   The name of the new attribute.
   * @param pValue
   *   The value of the new attribute.
   */
  public void setAttribute(String pName, Object pValue) {
    this.attributes.put(pName, pValue);
  }

}