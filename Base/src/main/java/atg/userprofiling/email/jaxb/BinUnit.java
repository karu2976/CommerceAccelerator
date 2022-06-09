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
    "binSource",
    "binTarget",
    "noteOrContextGroupOrPropGroupOrTransUnit"
})
@XmlRootElement(name = "bin-unit")
public class BinUnit {

    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String id;
    @XmlAttribute(name = "mime-type", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String mimeType;
    @XmlAttribute(name = "approved")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String approved;
    @XmlAttribute(name = "translate")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String translate;
    @XmlAttribute(name = "reformat")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String reformat;
    @XmlAttribute(name = "ts")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String ts;
    @XmlAttribute(name = "restype")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String restype;
    @XmlAttribute(name = "resname")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String resname;
    @XmlAttribute(name = "phase-name")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String phaseName;
    @XmlElement(name = "bin-source", required = true)
    protected BinSource binSource;
    @XmlElement(name = "bin-target")
    protected BinTarget binTarget;
    @XmlElements({
        @XmlElement(name = "note", type = Note.class),
        @XmlElement(name = "context-group", type = ContextGroup.class),
        @XmlElement(name = "prop-group", type = PropGroup.class),
        @XmlElement(name = "trans-unit", type = TransUnit.class)
    })
    protected List<Object> noteOrContextGroupOrPropGroupOrTransUnit;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

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
     * Gets the value of the approved property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApproved() {
        return approved;
    }

    /**
     * Sets the value of the approved property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApproved(String value) {
        this.approved = value;
    }

    /**
     * Gets the value of the translate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTranslate() {
        if (translate == null) {
            return "yes";
        } else {
            return translate;
        }
    }

    /**
     * Sets the value of the translate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTranslate(String value) {
        this.translate = value;
    }

    /**
     * Gets the value of the reformat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReformat() {
        if (reformat == null) {
            return "yes";
        } else {
            return reformat;
        }
    }

    /**
     * Sets the value of the reformat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReformat(String value) {
        this.reformat = value;
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
     * Gets the value of the binSource property.
     * 
     * @return
     *     possible object is
     *     {@link BinSource }
     *     
     */
    public BinSource getBinSource() {
        return binSource;
    }

    /**
     * Sets the value of the binSource property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinSource }
     *     
     */
    public void setBinSource(BinSource value) {
        this.binSource = value;
    }

    /**
     * Gets the value of the binTarget property.
     * 
     * @return
     *     possible object is
     *     {@link BinTarget }
     *     
     */
    public BinTarget getBinTarget() {
        return binTarget;
    }

    /**
     * Sets the value of the binTarget property.
     * 
     * @param value
     *     allowed object is
     *     {@link BinTarget }
     *     
     */
    public void setBinTarget(BinTarget value) {
        this.binTarget = value;
    }

    /**
     * Gets the value of the noteOrContextGroupOrPropGroupOrTransUnit property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the noteOrContextGroupOrPropGroupOrTransUnit property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNoteOrContextGroupOrPropGroupOrTransUnit().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Note }
     * {@link ContextGroup }
     * {@link PropGroup }
     * {@link TransUnit }
     * 
     * 
     */
    public List<Object> getNoteOrContextGroupOrPropGroupOrTransUnit() {
        if (noteOrContextGroupOrPropGroupOrTransUnit == null) {
            noteOrContextGroupOrPropGroupOrTransUnit = new ArrayList<Object>();
        }
        return this.noteOrContextGroupOrPropGroupOrTransUnit;
    }

}