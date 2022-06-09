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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "skl",
    "phaseGroup",
    "propGroupOrGlossaryOrReferenceOrNoteOrCountGroup"
})
@XmlRootElement(name = "header")
public class Header {

    protected Skl skl;
    @XmlElement(name = "phase-group")
    protected PhaseGroup phaseGroup;
    @XmlElements({
        @XmlElement(name = "prop-group", type = PropGroup.class),
        @XmlElement(name = "glossary", type = Glossary.class),
        @XmlElement(name = "reference", type = Reference.class),
        @XmlElement(name = "note", type = Note.class),
        @XmlElement(name = "count-group", type = CountGroup.class)
    })
    protected List<Object> propGroupOrGlossaryOrReferenceOrNoteOrCountGroup;

    /**
     * Gets the value of the skl property.
     * 
     * @return
     *     possible object is
     *     {@link Skl }
     *     
     */
    public Skl getSkl() {
        return skl;
    }

    /**
     * Sets the value of the skl property.
     * 
     * @param value
     *     allowed object is
     *     {@link Skl }
     *     
     */
    public void setSkl(Skl value) {
        this.skl = value;
    }

    /**
     * Gets the value of the phaseGroup property.
     * 
     * @return
     *     possible object is
     *     {@link PhaseGroup }
     *     
     */
    public PhaseGroup getPhaseGroup() {
        return phaseGroup;
    }

    /**
     * Sets the value of the phaseGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link PhaseGroup }
     *     
     */
    public void setPhaseGroup(PhaseGroup value) {
        this.phaseGroup = value;
    }

    /**
     * Gets the value of the propGroupOrGlossaryOrReferenceOrNoteOrCountGroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the propGroupOrGlossaryOrReferenceOrNoteOrCountGroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPropGroupOrGlossaryOrReferenceOrNoteOrCountGroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PropGroup }
     * {@link Glossary }
     * {@link Reference }
     * {@link Note }
     * {@link CountGroup }
     * 
     * 
     */
    public List<Object> getPropGroupOrGlossaryOrReferenceOrNoteOrCountGroup() {
        if (propGroupOrGlossaryOrReferenceOrNoteOrCountGroup == null) {
            propGroupOrGlossaryOrReferenceOrNoteOrCountGroup = new ArrayList<Object>();
        }
        return this.propGroupOrGlossaryOrReferenceOrNoteOrCountGroup;
    }

}
