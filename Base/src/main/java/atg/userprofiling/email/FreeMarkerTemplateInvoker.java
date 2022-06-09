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

package atg.userprofiling.email;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import atg.userprofiling.email.jaxb.TransUnit;
import atg.userprofiling.email.jaxb.Xliff;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import freemarker.template.Configuration;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * This class will extend TemplateInvoker to provide the freemarker template 
 * rendering instead of jsp template rendering.
 * 
 * @author Oracle
 * @version $Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/userprofiling/email/FreeMarkerTemplateInvoker.java#1 $$Change: 1385662 $
 * @updated $DateTime: 2017/03/09 10:29:42 $
 */
public class FreeMarkerTemplateInvoker extends TemplateInvoker {

  /** Class version string */
  public static String CLASS_VERSION =
    "$Id: //hosting-blueprint/CSA/version/11.3/Base/src/main/java/atg/userprofiling/email/FreeMarkerTemplateInvoker.java#1 $$Change: 1385662 $";

  //----------------------------------------------------------------------------
  // CONSTRUCTORS
  //----------------------------------------------------------------------------

  /**
   * Default constructor.
   */
  public FreeMarkerTemplateInvoker(){
    super();
  }

  /**
   * Create a new FreeMarkerTemplateInvoker.
   *
   * @param pSender
   *   The template email sender.
   */
  public FreeMarkerTemplateInvoker(TemplateEmailSender pSender){
    super(pSender);
  }
  
  //----------------------------------------------------------------------------
  // METHODS
  //----------------------------------------------------------------------------

  /**
   * Create a session for TemplateEmailRendering. endSession() should be called to
   * restore the current thread request and free session resources.
   *
   * @param pParameterMap
   *   Parameters that should be set on each request.
   *
   * @param pReqResInitializer
   *   An object that implements pReqResInitializer to do additional request/response
   *   initialization.
   */
  public TemplateSession createSession(Map pParameterMap,
      RequestResponseInitializer pReqResInitializer) throws TemplateEmailException {

    return new TemplateSession(pParameterMap, pReqResInitializer);
  }

  //----------------------------------------------------------------------------
  // INNER CLASSES
  //----------------------------------------------------------------------------

  public class TemplateSession extends TemplateInvoker.TemplateSession {
    
    /** UTF-8 encoding format. */
    public static final String FORMAT_UTF8 = "UTF-8";
    
    /** Method name to get the locale specific value. */
    public static final String GET_STRING_METHOD = "getString";
    
    /** Locale place holder. */
    public static final String LOCALE_PLACEHOLDER = "{locale}";
    
    /** Xsd file name. */
    public static final String SCHEMA_FILE_NAME = "xliff.xsd";
    
    /** External general entities feature key. */
    public static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    
    /** External parameter entities feature key. */
    public static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";

    /** Disallow DOCTYPE tags feature key. */
    public static final String DISALLOW_DOCTYPE_DECL = "http://apache.org/xml/features/disallow-doctype-decl";

    /**
     * Create a new TemplateSession.
     *
     * @param pParameterMap
     *   The map of Parameters to go into the session.
     * @param pReqResInitializer
     *   The request/response initializer.
     *
     * @throws TemplateEmailException
     */
    public TemplateSession(Map pParameterMap,
        RequestResponseInitializer pReqResInitializer) throws TemplateEmailException {

      super(pParameterMap,pReqResInitializer);
    }
    
    /**
     * RenderTemplate method resolves the email template component which provides 
     * template and data.It renders the template with freemarker.Template will
     * be rendered in the provided locale.
     * 
     * @param pComponentPath
     *   Email template component which provides template name and data.
     * @param pLocale
     *   Locale for sending email.
     *
     * @return
     *   Rendered template string.
     *
     * @throws TemplateEmailException
     *   Throws exception if rendering has any issues.
     */
    public String renderTemplate(String pComponentPath, Locale pLocale) throws TemplateEmailException {

      TemplateDataProvider dataProvider =
        (TemplateDataProvider)getNucleus().resolveName(pComponentPath, null, true);

      Map templateData = dataProvider.processTemplateData(mParameterMap);
      Configuration configuration = new Configuration();
      configuration.setClassForTemplateLoading(dataProvider.getClass(),"");
      configuration.setLocale(pLocale);
      configuration.setDefaultEncoding(FORMAT_UTF8);
      configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

      final List<Object> templateTranslations =
        getXliff(getTemplateResourceLocalePath(dataProvider.getTemplateResourcesPath(), pLocale));

      // Create a custom FreeMarker function to support the function already in the templates.
      templateData.put(GET_STRING_METHOD, new TemplateMethodModelEx() {
        @Override
        public TemplateModel exec(List pArgs) throws TemplateModelException {

          // First parameter is the id of the message.
          String id = ((SimpleScalar) pArgs.get(0)).getAsString();
          TransUnit matchingTransUnit = getMatchingTransUnit(templateTranslations, id);

          // Things are looking bad - didn't find a match for the supplied ID - give up.
          if (matchingTransUnit == null) {
            return new SimpleScalar("??" + id);
          }

          // Use the target in preference to the source if there is one.
          String messageText =
            matchingTransUnit.getTarget() != null ?
              matchingTransUnit.getTarget().getvalue() :
              matchingTransUnit.getSource().getvalue();

          // User may have supplied additional parameters for substitution purposes.
          if (pArgs.size() > 1) {

            // Turn the text from xliff file into a java  message format.
            MessageFormat messageFormat = new MessageFormat(messageText);

            // Convert all the supplied arguments (after the first one) to a list of strings.
            List<String> arguments = new ArrayList<>();

            for (SimpleScalar simpleScalar : (List<SimpleScalar>) pArgs.subList(1, pArgs.size())) {
              arguments.add(simpleScalar.getAsString());
            }

            // Using our new string array and message format object, create a composite string
            // to return.
            return new SimpleScalar(messageFormat.format(
              arguments.toArray(), new StringBuffer(), null).toString());
          }
          else {

            // No additional processing required - just turn the java string into a FreeMarker entity.
            return new SimpleScalar(messageText);
          }

        }
      });

      Writer templateStringWriter = new StringWriter();

      try {
        Template template = configuration.getTemplate(dataProvider.getTemplateName());
        template.process(templateData , templateStringWriter);

        return templateStringWriter.toString();
      }
      catch(Exception exception) {
        throw new TemplateEmailException(exception.toString(), exception);
      }
    }
    
    /**
     * Walk through all the translation stuff look for a matching TransUnit.
     * 
     * @param pGroupOrTransUnitOrBinUnitList
     *   Translation objects list.
     * @param pId
     *   Translation id.
     *
     * @return
     *   Returns TransUnit for the component name and id or null.
     */
    private TransUnit getMatchingTransUnit(final List<Object> pGroupOrTransUnitOrBinUnitList, String pId) {
      
      // Start with the outer list and see what we get.
      for (Object groupOrTransUnitOrBinUnit : pGroupOrTransUnitOrBinUnitList) {

        // Should only be a TransUnit - ignore everything else.
        if (groupOrTransUnitOrBinUnit instanceof TransUnit) {

          TransUnit transUnit = (TransUnit) groupOrTransUnitOrBinUnit;

          if (transUnit.getId().equals(pId)) {
            return transUnit;
          }
        }
      }
      
      // This should not really happen.
      return null;
    }
    
    /**
     * This method will parse the xliff file in the given path and returns the
     * translations as a list.
     * 
     * @param pTemplateResourcePath
     *   Resource path for loading translations data.
     *
     * @return 
     *   JAXB object list containing translation information built from the XML.
     */
    private List<Object> getXliff(String pTemplateResourcePath) {

      Xliff xliff = null;

      try {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        URL xsdURL = getClass().getResource(SCHEMA_FILE_NAME);
        Schema schema = schemaFactory.newSchema(xsdURL);

        URL url = this.getClass().getResource(pTemplateResourcePath);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setExpandEntityReferences(false);
        documentBuilderFactory.setXIncludeAware(false);
        documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        documentBuilderFactory.setFeature(DISALLOW_DOCTYPE_DECL, true);
        documentBuilderFactory.setFeature(EXTERNAL_GENERAL_ENTITIES, false); 
        documentBuilderFactory.setFeature(EXTERNAL_PARAMETER_ENTITIES, false);
        documentBuilderFactory.setValidating(true);
        documentBuilderFactory.setSchema(schema);

        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        documentBuilder.setErrorHandler(null);

        Document document = documentBuilder.parse(new ByteArrayInputStream(IOUtils.toByteArray(url.openStream())));

        Unmarshaller jaxbUnmarshaller = JAXBContext.newInstance(Xliff.class).createUnmarshaller();
        jaxbUnmarshaller.setSchema(schema);
        xliff = (Xliff) jaxbUnmarshaller.unmarshal(document);
      }
      catch (JAXBException | SAXException | ParserConfigurationException | IOException e) {
        if(isLoggingError()){
          logError(e);
        }
      }

      if(xliff != null) {

        // Just return the bits we need.
        return xliff.getFile().get(0).getBody().getGroupOrTransUnitOrBinUnit();
      }

      return null;
    }
    
    /**
     * This method replaces the locale placeholder in the resource path with the
     * given locale language.
     * 
     * @param pResourcePath
     *   Resource path for xlf file.
     * @param pLocale
     *   Profile locale.
     *
     * @return
     *   Returns the locale resource path.
     */
    public String getTemplateResourceLocalePath(String pResourcePath, Locale pLocale){
      return pResourcePath.replace(LOCALE_PLACEHOLDER, pLocale.getLanguage());
    }
    
  }
}