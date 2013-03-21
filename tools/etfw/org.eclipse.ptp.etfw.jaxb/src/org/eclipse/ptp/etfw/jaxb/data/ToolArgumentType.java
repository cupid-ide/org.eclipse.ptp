//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.03.21 at 03:37:10 PM EDT 
//


package org.eclipse.ptp.etfw.jaxb.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ToolArgumentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ToolArgumentType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="flag" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="separator" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="conf-val" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="localFile" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="useConfValue" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="requireValue" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="argType" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ToolArgumentType")
public class ToolArgumentType {

    @XmlAttribute(name = "flag")
    protected String flag;
    @XmlAttribute(name = "value")
    protected String value;
    @XmlAttribute(name = "separator")
    protected String separator;
    @XmlAttribute(name = "conf-val")
    protected String confVal;
    @XmlAttribute(name = "localFile")
    protected Boolean localFile;
    @XmlAttribute(name = "useConfValue")
    protected Boolean useConfValue;
    @XmlAttribute(name = "requireValue")
    protected Boolean requireValue;
    @XmlAttribute(name = "argType")
    protected Integer argType;

    /**
     * Gets the value of the flag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlag() {
        return flag;
    }

    /**
     * Sets the value of the flag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlag(String value) {
        this.flag = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
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
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the separator property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Sets the value of the separator property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeparator(String value) {
        this.separator = value;
    }

    /**
     * Gets the value of the confVal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfVal() {
        return confVal;
    }

    /**
     * Sets the value of the confVal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfVal(String value) {
        this.confVal = value;
    }

    /**
     * Gets the value of the localFile property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isLocalFile() {
        if (localFile == null) {
            return false;
        } else {
            return localFile;
        }
    }

    /**
     * Sets the value of the localFile property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLocalFile(Boolean value) {
        this.localFile = value;
    }

    /**
     * Gets the value of the useConfValue property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isUseConfValue() {
        if (useConfValue == null) {
            return false;
        } else {
            return useConfValue;
        }
    }

    /**
     * Sets the value of the useConfValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setUseConfValue(Boolean value) {
        this.useConfValue = value;
    }

    /**
     * Gets the value of the requireValue property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isRequireValue() {
        if (requireValue == null) {
            return false;
        } else {
            return requireValue;
        }
    }

    /**
     * Sets the value of the requireValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRequireValue(Boolean value) {
        this.requireValue = value;
    }

    /**
     * Gets the value of the argType property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getArgType() {
        if (argType == null) {
            return  0;
        } else {
            return argType;
        }
    }

    /**
     * Sets the value of the argType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setArgType(Integer value) {
        this.argType = value;
    }

}
