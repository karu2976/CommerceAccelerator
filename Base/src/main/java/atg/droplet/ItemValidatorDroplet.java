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
package atg.droplet;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.service.collections.validator.CollectionObjectValidator;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * This droplet will validate a single item according to the array of configured validators.
 * 
 * Input parameters:
 *   item
 *     an item to be validated.
 *     
 *  Open parameters:
 *    output
 *     Always rendered.
 *     
 *  Output parameters:
 *   itemIsValid
 *     It is set to 'true' if item has valid start\end dates,
 *     and to 'false' if item has invalid start and end dates.
 * 
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/droplet/ItemValidatorDroplet.java#1 $Change: 630322 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 *
 */
public class ItemValidatorDroplet extends DynamoServlet {

  /** Class version string */
  public static final String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/droplet/ItemValidatorDroplet.java#1 $Change: 630322 $";

  //----------------------------------------------------------------------------
  // CONSTANTS
  //----------------------------------------------------------------------------

  /** Rendered when item is valid. */
  private static final String OPARAM_TRUE = "true";

  /** Rendered when item is invalid. */
  private static final String OPARAM_FALSE = "false";

  /** The 'item' parameter name. */
  private static final String ITEM = "item";

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------

  //------------------------------------
  // property: validators
  //------------------------------------
  private CollectionObjectValidator[] mValidators = null;
  
  /**
   * @return
   *   An array of validators that will be applied to the given item.
   */
  public CollectionObjectValidator[] getValidators() {
    return mValidators;
  }

  /**
   * @param pValidators
   *   An array of validators that will be applied to the given item.
   */
  public void setValidators(CollectionObjectValidator[] pValidators) {
    this.mValidators = pValidators;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * Validates item in request using configured validators.
   *
   * @param pRequest
   *   The HTTP request object.
   * @param pResponse
   *   The HTTP response object.
   * @exception ServletException
   * @exception IOException
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    Object item = pRequest.getObjectParameter(ITEM);

    boolean isValid = validateItem(item);

    if (isValid) {
      pRequest.serviceLocalParameter(OPARAM_TRUE, pRequest, pResponse);
    }
    else {
      pRequest.serviceLocalParameter(OPARAM_FALSE, pRequest, pResponse);
    }
  }
  
  /**
   * Validate object using configured array of validators.
   *
   * @param pItem
   *   The item to be validated.
   * @return
   *   'false' if <code>pItem</code> fails validation, 'true' if no validators have
   *   been configured or the object has passed validation.
   */
  protected boolean validateItem(Object pItem) {
    
    // There are no validators set so pItem is valid.
    if (getValidators() == null || getValidators().length == 0) {
      return true;
    }
    
    boolean isValid = true;

    for (CollectionObjectValidator validator: getValidators()) {
      if (!validator.validateObject(pItem)) {
        
        if (isLoggingDebug()) {
          vlogDebug("Object: {0} doesn't pass validator: {1}", pItem, validator);
        }

        // Item hasn't passed validation. Set isValid to false and exit the loop
        // as there is no need to check any other validators.
        isValid = false;
        break;
      }
    }
    return isValid;
  }
}
