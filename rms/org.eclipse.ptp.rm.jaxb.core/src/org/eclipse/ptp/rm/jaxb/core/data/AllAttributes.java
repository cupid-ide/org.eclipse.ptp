//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.02.24 at 04:22:23 PM CST 
//


package org.eclipse.ptp.rm.jaxb.core.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}viewer" minOccurs="0"/>
 *         &lt;element name="include">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                 &lt;attribute name="headers" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                 &lt;attribute name="sort" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                 &lt;attribute name="tooltip" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "viewer",
    "include"
})
@XmlRootElement(name = "all-attributes")
public class AllAttributes {

    protected Viewer viewer;
    @XmlElement(required = true)
    protected AllAttributes.Include include;

    /**
     * 
     * 							The presence of this element indicates all attributes should be placed in a single table; otherwise, attributes
     * 							are mapped serially to widgets by type.
     * 						
     * 
     * @return
     *     possible object is
     *     {@link Viewer }
     *     
     */
    public Viewer getViewer() {
        return viewer;
    }

    /**
     * 
     * 							The presence of this element indicates all attributes should be placed in a single table; otherwise, attributes
     * 							are mapped serially to widgets by type.
     * 						
     * 
     * @param value
     *     allowed object is
     *     {@link Viewer }
     *     
     */
    public void setViewer(Viewer value) {
        this.viewer = value;
    }

    /**
     * Gets the value of the include property.
     * 
     * @return
     *     possible object is
     *     {@link AllAttributes.Include }
     *     
     */
    public AllAttributes.Include getInclude() {
        return include;
    }

    /**
     * Sets the value of the include property.
     * 
     * @param value
     *     allowed object is
     *     {@link AllAttributes.Include }
     *     
     */
    public void setInclude(AllAttributes.Include value) {
        this.include = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *       &lt;attribute name="headers" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *       &lt;attribute name="sort" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *       &lt;attribute name="tooltip" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Include {

        @XmlAttribute
        protected Boolean description;
        @XmlAttribute
        protected Boolean headers;
        @XmlAttribute
        protected Boolean name;
        @XmlAttribute
        protected Boolean sort;
        @XmlAttribute
        protected Boolean tooltip;

        /**
         * Gets the value of the description property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isDescription() {
            return description;
        }

        /**
         * Sets the value of the description property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setDescription(Boolean value) {
            this.description = value;
        }

        /**
         * Gets the value of the headers property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isHeaders() {
            return headers;
        }

        /**
         * Sets the value of the headers property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setHeaders(Boolean value) {
            this.headers = value;
        }

        /**
         * Gets the value of the name property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isName() {
            return name;
        }

        /**
         * Sets the value of the name property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setName(Boolean value) {
            this.name = value;
        }

        /**
         * Gets the value of the sort property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isSort() {
            return sort;
        }

        /**
         * Sets the value of the sort property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setSort(Boolean value) {
            this.sort = value;
        }

        /**
         * Gets the value of the tooltip property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isTooltip() {
            return tooltip;
        }

        /**
         * Sets the value of the tooltip property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setTooltip(Boolean value) {
            this.tooltip = value;
        }

    }

}