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

package atg.projects.store.droplet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import atg.service.filter.bean.BeanFilterException;
import atg.service.filter.bean.BeanFilterRegistry;
import atg.service.filter.bean.BeanFilterService;
import atg.service.response.output.OutputCustomizer;
import atg.service.response.output.OutputException;
import atg.service.response.output.ResponseGenerator;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Accepts an object "toJSON" on the request then performs bean filtering and serialization to JSON. The object is
 * returned as a json string with the output parameter name "json" inside the "output" open parameter.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/droplet/ToJSONDroplet.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ToJSONDroplet extends DynamoServlet {

  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/droplet/ToJSONDroplet.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // CONSTANTS
  //---------------------------------------------------------------------------

  /** Object to serialize request parameter. */
  public static final String TO_JSON = "toJSON";

  /** Output parameter name. */
  public static final String JSON = "json";

  /** Open parameter name. */
  public static final String OUTPUT_OPARAM = "output";

  //---------------------------------------------------------------------------
  // PROPERTIES
  //---------------------------------------------------------------------------

  //-----------------------------------
  // property: beanFilterService
  //-----------------------------------
  private BeanFilterService mBeanFilterService = null;

  /**
   * @return
   *   Used to filter objects before serialization.
   */
  public BeanFilterService getBeanFilterService() {
    return mBeanFilterService;
  }

  /**
   * @param pBeanFilterService
   *   Set a new BeanFilterService.
   */
  public void setBeanFilterService(BeanFilterService pBeanFilterService) {
    mBeanFilterService = pBeanFilterService;
  }

  //-----------------------------------
  // property: responseGenerator
  //-----------------------------------
  private ResponseGenerator mResponseGenerator = null;

  /**
   * @return
   *   The ResponseGenerator used to serialize the input object.
   */
  public ResponseGenerator getResponseGenerator() {
    return mResponseGenerator;
  }

  /**
   * @param pResponseGenerator
   *   Set a new ResponseGenerator.
   */
  public void setResponseGenerator(ResponseGenerator pResponseGenerator) {
    mResponseGenerator = pResponseGenerator;
  }

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Takes an object on the request "toJSON", performs bean filtering then serializes it to a
   * json string which is rendered in the open parameter "output" with the name "json".
   *
   * @param pRequest
   *   An HTTP request object.
   * @param pResponse
   *   An HTTP response object.
   *
   * @throws ServletException
   * @throws IOException
   */
  @Override
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {

    Object toJSON = pRequest.getLocalParameter(TO_JSON);

    if (toJSON == null) {
      if (isLoggingError()) {
        logError("The object to serialize wasn't passed in.");
      }
      return;
    }

    // Perform bean filtering.
    Object filteredToJSON = null;

    try {
      filteredToJSON =
        getBeanFilterService().applyFilter(
          toJSON, new HashMap<BeanFilterRegistry.FilterOptionKey, Object>());
    }
    catch (BeanFilterException e) {
      vlogError(e, "An error occurred filtering object {0}", toJSON);
      return;
    }

    // Convert to JSON.
    Map<OutputCustomizer.OptionKey, Object> options = new HashMap<>();
    options.put(OutputCustomizer.StandardOptionKey.Format,
      atg.service.response.output.Configuration.FORMAT_JSON);

    String inputConvertedToJSONString = null;

    try {
      inputConvertedToJSONString = getResponseGenerator().generateResponse(filteredToJSON, options);
    }
    catch (OutputException e) {
      vlogError(e, "Couldn't parse input object {0} to JSON", filteredToJSON);
      return;
    }

    pRequest.setParameter(JSON, inputConvertedToJSONString);
    pRequest.serviceLocalParameter(OUTPUT_OPARAM, pRequest, pResponse);
  }
}
