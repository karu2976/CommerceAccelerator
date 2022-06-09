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
package atg.projects.store.userprofiling;

import atg.core.util.Address;
import atg.userprofiling.address.AddressTools;

/**
 * A tools class containing useful and re-useable methods for use with addresses.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/userprofiling/StoreAddressTools.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class StoreAddressTools extends AddressTools {

  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/CSA/version/11.3/Plugins/Account/src/main/java/atg/projects/store/userprofiling/StoreAddressTools.java#1 $$Change: 1385662 $";

  //---------------------------------------------------------------------------
  // METHODS
  //---------------------------------------------------------------------------

  /**
   * Compares the properties of two addresses to determine if they are the same address.
   * For an address to be considered the same the following properties must be equal:
   *
   * <ul>
   *   <li>address1
   *   <li>address2
   *   <li>city
   *   <li>state
   *   <li>postalcode
   *   <li>country
   * </ul>
   *
   * @param pAddressA An Address
   * @param pAddressB An Address
   * @return A boolean indicating whether or not pAddressA and pAddressB 
   * represent the same address.
   */
  public boolean sameAddress(Address pAddressA, Address pAddressB){
    /*
     * Test the actual address objects. We don't want to use .equals to compare
     * the fields as we don't want to compare the owner Id. The address
     * associated with the order wont have an owner Id.
     */
    if(!equal(pAddressA, pAddressB, false)){
      return false;
    }

    /*
     * Test individual address fields that we are interested in. If they are all
     * equal then we say the addresses are equal, even though every property of 
     * both addresses may not be the same.
     */
    if(!equal(pAddressA.getAddress1(), pAddressB.getAddress1(), true)){
      return false;
    }

    if(!equal(pAddressA.getAddress2(), pAddressB.getAddress2(), true)){
      return false;
    }

    if(!equal(pAddressA.getCity(), pAddressB.getCity(), true)){
      return false;
    }

    if(!equal(pAddressA.getState(), pAddressB.getState(), true)){
      return false;
    }

    if(!equal(pAddressA.getPostalCode(), pAddressB.getPostalCode(), true)){
      return false;
    }

    return equal(pAddressA.getCountry(), pAddressB.getCountry(), true);
  }

  /**
   * Return true if both are null or equal, false otherwise.
   *
   * @param pOne First object
   * @param pTwo Second object
   * @param pUseEquals Use Object.equals
   * @return A boolean indicating object equality.
   */
  protected boolean equal(Object pOne, Object pTwo,
                                 boolean pUseEquals)
  {
    if(pOne == null && pTwo != null){
      return false;
    }

    if(pOne != null && pTwo == null){
      return false;
    }

    // They both must be null or non-null.
    if(pOne == null){
      return true;
    }

    if(pUseEquals){
      return pOne.equals(pTwo);
    }

    return true; // Both non null, we don't care if they are equal
  }
}
