//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.08.18 at 03:50:01 PM BST 
//


package atg.userprofiling.email.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "internalFileOrExternalFile"
})
@XmlRootElement(name = "bin-target")
public class BinTarget {

    @XmlAttribute(name = "mime-type")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String mimeType;
    @XmlAttribute(name = "ts")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String ts;
    @XmlAttribute(name = "state")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String state;
    @XmlAttribute(name = "phase-name")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String phaseName;
    @XmlAttribute(name = "restype")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String restype;
    @XmlAttribute(name = "resname")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String resname;
    @XmlElements({
        @XmlElement(name = "internal-file", required = true, type = InternalFile.class),
        @XmlElement(name = "external-file", required = true, type = ExternalFile.class)
    })
    protected List<Object> internalFileOrExternalFile;

    /**
     * Gets the value of the mimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Sets the value of the mimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMimeType(String value) {
        this.mimeType = value;
    }

    /**
     * Gets the value of the ts property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTs() {
        return ts;
    }

    /**
     * Sets the value of the ts property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTs(String value) {
        this.ts = value;
    }

    /**
     * Gets the value of the state property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the value of the state property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setState(String value) {
        this.state = value;
    }

    /**
     * Gets the value of the phaseName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhaseName() {
        return phaseName;
    }

    /**
     * Sets the value of the phaseName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhaseName(String value) {
        this.phaseName = value;
    }

    /**
     * Gets the value of the restype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRestype() {
        return restype;
    }

    /**
     * Sets the value of the restype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRestype(String value) {
        this.restype = value;
    }

    /**
     * Gets the value of the resname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResname() {
        return resname;
    }

    /**
     * Sets the value of the resname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResname(String value) {
        this.resname = value;
    }

    /**
     * Gets the value of the internalFileOrExternalFile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the internalFileOrExternalFile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInternalFileOrExternalFile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InternalFile }
     * {@link ExternalFile }
     * 
     * 
     */
    public List<Object> getInternalFileOrExternalFile() {
        if (internalFileOrExternalFile == null) {
            internalFileOrExternalFile = new ArrayList<Object>();
        }
        return this.internalFileOrExternalFile;
    }

}
