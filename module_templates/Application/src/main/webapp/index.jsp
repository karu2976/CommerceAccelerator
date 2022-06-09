<%-- JSTL --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- DSP --%>
<%@ taglib uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_1" prefix="dsp" %>

<dsp:page>

  <%-- Imports --%>
  <dsp:importbean var="currentSite" bean="/atg/multisite/Site"/>
  <dsp:importbean var="originatingRequest" bean="/OriginatingRequest"/>
  <dsp:importbean var="sessionConfirmation" bean="/atg/rest/SessionConfirmation"/>
  <dsp:importbean var="moduleDependencies" bean="/atg/dynamo/service/ModuleDependencies"/>

  <dsp:getvalueof var="content" vartype="com.endeca.infront.assembler.ContentItem" value="${originatingRequest.contentItem}"/>

  <c:choose>
    <c:when test="${ empty currentSite.productionURL }">
      <c:set var="productionURL" value="${ pageContext.request.contextPath }"/>
    </c:when>
    <c:otherwise>
      <c:set var="productionURL" value="${currentSite.productionURL}"/>
    </c:otherwise>
  </c:choose>

  <c:set var="locale" value="${originatingRequest.requestLocale.locale}"/>

  <%--
    This droplet will retrieve a currency code based on the supplied locale.

    Input Parameters:
      locale
        The current locale.

    Open Parameters:
      output
        Always rendered.

    Output Parameters:
      currencyCode
        The appropriate currency code for given locale.
      currencySymbol
        The appropriate currency symbol for given locale.
  --%>
  <dsp:droplet name="/atg/commerce/pricing/CurrencyCodeDroplet">
    <dsp:param name="locale" value="${ locale }"/>
    <dsp:oparam name="output">
      <dsp:getvalueof var="currency" vartype="java.lang.String" param="currencyCode"/>
    </dsp:oparam>
  </dsp:droplet>

  <%--
    On a full page request we may already have a content item available, i.e if the full page
    request is for /home then the assembler executes, builds the content item, this jsp is
    configured as the content item renderer. We need to convert the content item to JSON so we
    can use it.

    Input Parameters:
      toJSON
        The object that should be returned as a json string.

    Open Parameters:
      output
        Rendered when there are no errors.

    Output Parameters:
      json
        A JSON string representation of the toJSON input parameter.
  --%>
  <c:choose>
    <c:when test="${content != null}">
      <dsp:droplet name="/atg/droplet/ToJSONDroplet">
        <dsp:param name="toJSON" value="${ content }"/>
        <dsp:oparam name="output">
          <dsp:getvalueof var="contentJson" vartype="java.lang.String" param="json"/>
        </dsp:oparam>
      </dsp:droplet>
    </c:when>
    <c:otherwise>
      <c:set var="contentJson" value="null"/>
    </c:otherwise>
  </c:choose>

  <!DOCTYPE html>
  <html lang="${ locale.getLanguage() }">
    <head>
      <meta charset="utf-8">
      <meta http-equiv="X-UA-Compatible" content="IE=edge">
      <title data-bind="text: metadata.title"></title>
      <meta name="description" content="{{ metadata.description }}">
      <meta name="keywords" content="{{ metadata.keywords }}">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <base href="${ productionURL }/${ locale.getLanguage() }/">
      <link rel="canonical" href="{{ canonical }}">
      <link rel="icon" data-bind="attr: { href: metadata.faviconPath }" type="image/png" />
      <script>
        sessionStorage.setItem('_dynSessConf', '${ sessionConfirmation.sessionConfirmationNumber }');
        sessionStorage.setItem('currency', '${ currency }');
        sessionStorage.setItem('language', '${ locale.toLanguageTag() }');
        sessionStorage.setItem('locale', '${ locale }');
        sessionStorage.setItem('siteId', '${ currentSite.repositoryId }');
        sessionStorage.setItem('packages', JSON.stringify([${ moduleDependencies.dependencyList }]));
        sessionStorage.setItem('content', JSON.stringify(${ contentJson }));

      </script>
      <script data-main="/csa/base/launcher/main.js" src="/csa/base/bower_components/requirejs/require.js"></script>
    </head>
    <body role="document">
      <!-- ko if: contentReady -->
        <div data-bind="contentItem"></div>
      <!-- /ko -->
    </body>
  </html>
</dsp:page>
<%-- @version $Id: //hosting-blueprint/CSA/version/11.3/module_templates/Application/src/main/webapp/index.jsp#1 $$Change: 1385662 $--%>
