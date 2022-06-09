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

package atg.projects.store.assembler.cartridge;

import atg.endeca.assembler.actor.ActorExecutor;
import atg.nucleus.ResolvingMap;
import atg.service.actor.Actor;
import com.endeca.infront.assembler.ContentItem;

/**
 * This class will invoke specific actors to modify a particular content item.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/assembler/cartridge/ActorInvokingContentItemModifier.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ActorInvokingContentItemModifier implements ContentItemModifier {

  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/projects/store/assembler/cartridge/ActorInvokingContentItemModifier.java#1 $$Change: 1385662 $";

  //------------------------------------------------------------------------
  // PROPERTIES
  //------------------------------------------------------------------------

  //-----------------------------------
  // property: actorExecutor
  //-----------------------------------
  private ActorExecutor mActorExecutor = null;

  /**
   * @return
   *   The ActorExecutor that will invoke the actorToInvoke.
   */
  public ActorExecutor getActorExecutor() {
    return mActorExecutor;
  }

  /**
   * @param pActorExecutor
   *   The ActorExecutor that will invoke the actorToInvoke.
   */
  public void setActorExecutor(ActorExecutor pActorExecutor) {
    mActorExecutor = pActorExecutor;
  }

  //------------------------------------
  // property: actorChain
  //------------------------------------
  private String mActorChain = null;

  /**
   * @return
   *   The actor chain to execute. If this is not set the default actor chain
   *   will be executed.
   */
  public String getActorChain() {
    return mActorChain;
  }

  /**
   * @param pActorChain
   *   The actor chain to execute. If this is not set the default actor chain
   *   will be executed.
   */
  public void setActorChain(String pActorChain) {
    mActorChain = pActorChain;
  }

  //------------------------------------
  // property: actorToInvoke
  //------------------------------------
  private Actor mActorToInvoke = null;

  /**
   * @return
   *   The Actor that will be invoked in the process method.
   */
  public Actor getActorToInvoke() {
    return mActorToInvoke;
  }

  /**
   * @param pActorToInvoke
   *   The Actor that will be invoked in the process method.
   */
  public void setActorToInvoke(Actor pActorToInvoke) {
    mActorToInvoke = pActorToInvoke;
  }

  //------------------------------------
  // property: contentItemAdditionsMap
  //------------------------------------
  private ResolvingMap mContentItemAdditionsMap = new ResolvingMap();

  /**
   * @return
   *   A map whose keys are cartridge types and values are handler components. Essentially,
   *   these are properties that will be passed to actorToInvoke through the ContentItem.
   */
  public ResolvingMap getContentItemAdditionsMap() {
    return mContentItemAdditionsMap;
  }

  /**
   * @param pContentItemAdditionsMap
   *   A map whose keys are cartridge types and values are handler components. Essentially,
   *   these are properties that will be passed to actorToInvoke through the ContentItem.
   */
  public void setContentItemAdditionsMap(ResolvingMap pContentItemAdditionsMap) {
    mContentItemAdditionsMap = pContentItemAdditionsMap;
  }

  //------------------------------------------------------------------------
  // METHODS
  //------------------------------------------------------------------------

  /**
   * This method will invoke an actor to perform modifications to the passed in content item.
   *
   * @param pContentItem
   *   The content item to be modified.
   */
  @Override
  public void modify(ContentItem pContentItem) {

    if (getContentItemAdditionsMap() != null && getContentItemAdditionsMap().size() > 0) {
      pContentItem.putAll(getContentItemAdditionsMap());
    }

    if (getActorExecutor() != null) {
      if (getActorChain() == null) {
        getActorExecutor().invokeActor(getActorToInvoke(), pContentItem);
      }
      else {
        getActorExecutor().invokeActor(getActorToInvoke(), getActorChain(), pContentItem);
      }
    }
  }

  /**
   * Overloaded modify method that allows actor properties to be passed in as parameters
   * instead of being set in the component's properties file.
   *
   * @param pContentItem
   *   The content item to be modified.
   * @param pActorExecutor
   *   The ActorExecutor that will invoke the actorToInvoke.
   * @param pActorChain
   *   The actor chain to execute. If this is not set the default actor chain
   *   will be executed.
   * @param pActorToInvoke
   *   The Actor that will be invoked in the process method.
   * @param pContentItemAdditionsMap
   *   A map whose keys are cartridge types and values are handler components. Essentially,
   *   these are properties that will be passed to actorToInvoke through the ContentItem.
   */
  public static void modify(ContentItem pContentItem, ActorExecutor pActorExecutor, String pActorChain,
    Actor pActorToInvoke, ResolvingMap pContentItemAdditionsMap) {


    if (pContentItemAdditionsMap != null && pContentItemAdditionsMap.size() > 0) {
      pContentItem.putAll(pContentItemAdditionsMap);
    }

    if (pActorExecutor != null) {
      if (pActorChain == null) {
        pActorExecutor.invokeActor(pActorToInvoke, pContentItem);
      }
      else {
        pActorExecutor.invokeActor(pActorToInvoke, pActorChain, pContentItem);
      }
    }
  }

}
