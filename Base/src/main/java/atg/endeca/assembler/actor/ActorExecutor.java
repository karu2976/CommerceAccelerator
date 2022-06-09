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

package atg.endeca.assembler.actor;

import atg.nucleus.GenericService;
import atg.service.actor.Actor;
import atg.service.actor.ActorContext;
import atg.service.actor.ActorContextFactory;
import atg.service.actor.ActorException;
import atg.service.actor.ActorUtils;
import atg.service.actor.ModelMap;
import atg.service.actor.ModelMapFactory;
import atg.service.actor.VariantActor;

import com.endeca.infront.assembler.ContentItem;

import java.util.Map;

/**
 * Invokes an {@link Actor} and makes a ContentItem available to the Actor with
 * the contentItemPropertyName key.
 *
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/endeca/assembler/actor/ActorExecutor.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class ActorExecutor extends GenericService {

  /** Class version string */
  public static String CLASS_VERSION = 
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/endeca/assembler/actor/ActorExecutor.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // PROPERTIES
  //----------------------------------------------------------------------------
  
  //-----------------------------------
  // property: modelMapFactory
  //-----------------------------------
  private ModelMapFactory mModelMapFactory = null;

  /** 
   * @see atg.service.actor.ModelMapFactory 
   */
  public ModelMapFactory getModelMapFactory() {
    return mModelMapFactory;
  }

  /**
   * @see atg.service.actor.ModelMapFactory 
   */
  public void setModelMapFactory(ModelMapFactory pModelMapFactory) {
    mModelMapFactory = pModelMapFactory;
  }

  //-----------------------------------
  // property: actorContextFactory
  //-----------------------------------
  private ActorContextFactory mActorContextFactory = null;

  /** 
   * @see atg.service.actor.ActorContextFactory 
   */
  public ActorContextFactory getActorContextFactory() {
    return mActorContextFactory;
  }

  /** 
   * @see atg.service.actor.ActorContextFactory 
   */
  public void setActorContextFactory(ActorContextFactory pActorContextFactory) {
    mActorContextFactory = pActorContextFactory;
  }

  //-----------------------------------
  // property: contentItemPropertyName
  //-----------------------------------
  private String mContentItemPropertyName = "currentContentItem";

  /**
   * @return
   *   The key of the content item property available to the actor.
   */
  public String getContentItemPropertyName() {
    return mContentItemPropertyName;
  }

  /**
   * @param pContentItemPropertyName
   *   The key for the content item property.
   */
  public void setContentItemPropertyName(String pContentItemPropertyName) {
    mContentItemPropertyName = pContentItemPropertyName;
  }

  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * Invokes the given actor with the default chain id. The content item is made available in the
   * actor context. The Actor output model map is then added to the content item.
   *
   * @param pActor
   *   The actor to invoke.
   * @param pContentItem
   *   The ContentItem to update.
   *
   * @return
   *   The updated content item.
   */
  public ContentItem invokeActor(Actor pActor, ContentItem pContentItem){
    return invokeActor(pActor, null, pContentItem);
  }

  /**
   * Invokes the given actor. The content item is made available in the actor
   * context. The Actor output model map is then added to the content item.
   * 
   * @param pActor
   *   The actor to invoke.
   * @param pChainId
   *   The chain id on the actor that will be executed.
   * @param pContentItem
   *   The ContentItem to update.
   *   
   * @return
   *   The updated content item
   */
  public ContentItem invokeActor(Actor pActor, String pChainId, ContentItem pContentItem) {
    if (pActor == null) {
      if(isLoggingError()) {
        logError("Actor is null");
      }
      return pContentItem;
    }

    try {
      ActorContext actorContext = getActorContextFactory().createActorContext();
      
      // Set the content item so the actor can access it.
      actorContext.putAttribute(getContentItemPropertyName(), pContentItem);

      ModelMap modelMap = getModelMapFactory().createModelMap();
      
      if (pActor instanceof VariantActor) {

        // Get the default actor chain to execute if its not specified.
        String chainId = pChainId == null ? ((VariantActor) pActor).getDefaultActorChainId() : pChainId;

        // Set the chainId in the context.
        ActorUtils.putActorChainId(actorContext, chainId);
      }
      pActor.act(actorContext, modelMap);

      // Add the items directly to the content item.
      for(Map.Entry entry : modelMap.entrySet()){
        pContentItem.put(entry.getKey().toString(), entry.getValue());
      }
    }
    catch (ActorException ae) {
      if(isLoggingError()) {
        logError("Error executing actor", ae);
      }
    }

    return pContentItem;
  }

}
