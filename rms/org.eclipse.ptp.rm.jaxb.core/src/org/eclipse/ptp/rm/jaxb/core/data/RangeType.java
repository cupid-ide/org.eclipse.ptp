//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.08.01 at 05:11:32 PM CDT 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for range-type complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="range-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="greaterThan" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="greaterThanOrEqualTo" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="lessThan" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="lessThanOrEqualTo" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "range-type")
public class RangeType {

	@XmlAttribute
	protected String greaterThan;
	@XmlAttribute
	protected String greaterThanOrEqualTo;
	@XmlAttribute
	protected String lessThan;
	@XmlAttribute
	protected String lessThanOrEqualTo;

	/**
	 * Gets the value of the greaterThan property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getGreaterThan() {
		return greaterThan;
	}

	/**
	 * Gets the value of the greaterThanOrEqualTo property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getGreaterThanOrEqualTo() {
		return greaterThanOrEqualTo;
	}

	/**
	 * Gets the value of the lessThan property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getLessThan() {
		return lessThan;
	}

	/**
	 * Gets the value of the lessThanOrEqualTo property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getLessThanOrEqualTo() {
		return lessThanOrEqualTo;
	}

	/**
	 * Sets the value of the greaterThan property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setGreaterThan(String value) {
		this.greaterThan = value;
	}

	/**
	 * Sets the value of the greaterThanOrEqualTo property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setGreaterThanOrEqualTo(String value) {
		this.greaterThanOrEqualTo = value;
	}

	/**
	 * Sets the value of the lessThan property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setLessThan(String value) {
		this.lessThan = value;
	}

	/**
	 * Sets the value of the lessThanOrEqualTo property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setLessThanOrEqualTo(String value) {
		this.lessThanOrEqualTo = value;
	}

}
