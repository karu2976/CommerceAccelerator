//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.08.18 at 03:50:01 PM BST 
//


package atg.userprofiling.email.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "value"
})
@XmlRootElement(name = "context")
public class Context {

    @XmlAttribute(name = "context-type", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String contextType;
    @XmlAttribute(name = "match-mandatory")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String matchMandatory;
    @XmlAttribute(name = "crc")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String crc;
    @XmlValue
    protected String value;

    /**
     * Gets the value of the contextType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContextType() {
        return contextType;
    }

    /**
     * Sets the value of the contextType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContextType(String value) {
        this.contextType = value;
    }

    /**
     * Gets the value of the matchMandatory property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMatchMandatory() {
        if (matchMandatory == null) {
            return "no";
        } else {
            return matchMandatory;
        }
    }

    /**
     * Sets the value of the matchMandatory property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMatchMandatory(String value) {
        this.matchMandatory = value;
    }

    /**
     * Gets the value of the crc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCrc() {
        return crc;
    }

    /**
     * Sets the value of the crc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCrc(String value) {
        this.crc = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getvalue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setvalue(String value) {
        this.value = value;
    }

}
