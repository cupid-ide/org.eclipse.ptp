//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.5-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.27 at 09:36:08 AM CDT 
//

package org.eclipse.ptp.rm.jaxb.core.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for control-state-type complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType name="control-state-type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="show-if" type="{http://org.eclipse.ptp/rm}control-state-rule-type" minOccurs="0"/>
 *           &lt;element name="hide-if" type="{http://org.eclipse.ptp/rm}control-state-rule-type" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;choice minOccurs="0">
 *           &lt;element name="enable-if" type="{http://org.eclipse.ptp/rm}control-state-rule-type" minOccurs="0"/>
 *           &lt;element name="disable-if" type="{http://org.eclipse.ptp/rm}control-state-rule-type" minOccurs="0"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "control-state-type", propOrder = { "showIf", "hideIf", "enableIf", "disableIf" })
public class ControlStateType {

	@XmlElement(name = "show-if")
	protected ControlStateRuleType showIf;
	@XmlElement(name = "hide-if")
	protected ControlStateRuleType hideIf;
	@XmlElement(name = "enable-if")
	protected ControlStateRuleType enableIf;
	@XmlElement(name = "disable-if")
	protected ControlStateRuleType disableIf;

	/**
	 * Gets the value of the disableIf property.
	 * 
	 * @return possible object is {@link ControlStateRuleType }
	 * 
	 */
	public ControlStateRuleType getDisableIf() {
		return disableIf;
	}

	/**
	 * Gets the value of the enableIf property.
	 * 
	 * @return possible object is {@link ControlStateRuleType }
	 * 
	 */
	public ControlStateRuleType getEnableIf() {
		return enableIf;
	}

	/**
	 * Gets the value of the hideIf property.
	 * 
	 * @return possible object is {@link ControlStateRuleType }
	 * 
	 */
	public ControlStateRuleType getHideIf() {
		return hideIf;
	}

	/**
	 * Gets the value of the showIf property.
	 * 
	 * @return possible object is {@link ControlStateRuleType }
	 * 
	 */
	public ControlStateRuleType getShowIf() {
		return showIf;
	}

	/**
	 * Sets the value of the disableIf property.
	 * 
	 * @param value
	 *            allowed object is {@link ControlStateRuleType }
	 * 
	 */
	public void setDisableIf(ControlStateRuleType value) {
		this.disableIf = value;
	}

	/**
	 * Sets the value of the enableIf property.
	 * 
	 * @param value
	 *            allowed object is {@link ControlStateRuleType }
	 * 
	 */
	public void setEnableIf(ControlStateRuleType value) {
		this.enableIf = value;
	}

	/**
	 * Sets the value of the hideIf property.
	 * 
	 * @param value
	 *            allowed object is {@link ControlStateRuleType }
	 * 
	 */
	public void setHideIf(ControlStateRuleType value) {
		this.hideIf = value;
	}

	/**
	 * Sets the value of the showIf property.
	 * 
	 * @param value
	 *            allowed object is {@link ControlStateRuleType }
	 * 
	 */
	public void setShowIf(ControlStateRuleType value) {
		this.showIf = value;
	}

}