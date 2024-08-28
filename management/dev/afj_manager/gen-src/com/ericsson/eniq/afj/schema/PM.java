//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1-b02-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.10.10 at 03:55:53 PM IST 
//


package com.ericsson.eniq.afj.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Capabilities" type="{}CapabilitiesType"/>
 *         &lt;element name="data" type="{}dataType"/>
 *         &lt;element name="pmMimMetadata" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="nodeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="node_major_version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="node_minor_version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *                   &lt;element name="pmMimVersion" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="common_baseline_doc_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="common_baseline_major_version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="common_baseline_minor_version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "description",
    "capabilities",
    "data",
    "pmMimMetadata"
})
@XmlRootElement(name = "PM")
public class PM {

    @XmlElement(required = true, defaultValue = "\"\"")
    protected String description;
    @XmlElement(name = "Capabilities", required = true)
    protected CapabilitiesType capabilities;
    @XmlElement(required = true)
    protected DataType data;
    protected PM.PmMimMetadata pmMimMetadata;
    @XmlAttribute(required = true)
    protected String id;

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the capabilities property.
     * 
     * @return
     *     possible object is
     *     {@link CapabilitiesType }
     *     
     */
    public CapabilitiesType getCapabilities() {
        return capabilities;
    }

    /**
     * Sets the value of the capabilities property.
     * 
     * @param value
     *     allowed object is
     *     {@link CapabilitiesType }
     *     
     */
    public void setCapabilities(CapabilitiesType value) {
        this.capabilities = value;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link DataType }
     *     
     */
    public DataType getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataType }
     *     
     */
    public void setData(DataType value) {
        this.data = value;
    }

    /**
     * Gets the value of the pmMimMetadata property.
     * 
     * @return
     *     possible object is
     *     {@link PM.PmMimMetadata }
     *     
     */
    public PM.PmMimMetadata getPmMimMetadata() {
        return pmMimMetadata;
    }

    /**
     * Sets the value of the pmMimMetadata property.
     * 
     * @param value
     *     allowed object is
     *     {@link PM.PmMimMetadata }
     *     
     */
    public void setPmMimMetadata(PM.PmMimMetadata value) {
        this.pmMimMetadata = value;
    }

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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="nodeType" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="node_major_version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="node_minor_version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
     *         &lt;element name="pmMimVersion" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="common_baseline_doc_id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="common_baseline_major_version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="common_baseline_minor_version" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
        "nodeType",
        "nodeMajorVersion",
        "nodeMinorVersion",
        "timestamp",
        "pmMimVersion",
        "commonBaselineDocId",
        "commonBaselineMajorVersion",
        "commonBaselineMinorVersion"
    })
    public static class PmMimMetadata {

        @XmlElement(required = true)
        protected String nodeType;
        @XmlElement(name = "node_major_version")
        protected String nodeMajorVersion;
        @XmlElement(name = "node_minor_version")
        protected String nodeMinorVersion;
        protected XMLGregorianCalendar timestamp;
        @XmlElement(required = true)
        protected String pmMimVersion;
        @XmlElement(name = "common_baseline_doc_id")
        protected String commonBaselineDocId;
        @XmlElement(name = "common_baseline_major_version")
        protected String commonBaselineMajorVersion;
        @XmlElement(name = "common_baseline_minor_version")
        protected String commonBaselineMinorVersion;

        /**
         * Gets the value of the nodeType property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNodeType() {
            return nodeType;
        }

        /**
         * Sets the value of the nodeType property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNodeType(String value) {
            this.nodeType = value;
        }

        /**
         * Gets the value of the nodeMajorVersion property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNodeMajorVersion() {
            return nodeMajorVersion;
        }

        /**
         * Sets the value of the nodeMajorVersion property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNodeMajorVersion(String value) {
            this.nodeMajorVersion = value;
        }

        /**
         * Gets the value of the nodeMinorVersion property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNodeMinorVersion() {
            return nodeMinorVersion;
        }

        /**
         * Sets the value of the nodeMinorVersion property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNodeMinorVersion(String value) {
            this.nodeMinorVersion = value;
        }

        /**
         * Gets the value of the timestamp property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getTimestamp() {
            return timestamp;
        }

        /**
         * Sets the value of the timestamp property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setTimestamp(XMLGregorianCalendar value) {
            this.timestamp = value;
        }

        /**
         * Gets the value of the pmMimVersion property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPmMimVersion() {
            return pmMimVersion;
        }

        /**
         * Sets the value of the pmMimVersion property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPmMimVersion(String value) {
            this.pmMimVersion = value;
        }

        /**
         * Gets the value of the commonBaselineDocId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCommonBaselineDocId() {
            return commonBaselineDocId;
        }

        /**
         * Sets the value of the commonBaselineDocId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCommonBaselineDocId(String value) {
            this.commonBaselineDocId = value;
        }

        /**
         * Gets the value of the commonBaselineMajorVersion property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCommonBaselineMajorVersion() {
            return commonBaselineMajorVersion;
        }

        /**
         * Sets the value of the commonBaselineMajorVersion property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCommonBaselineMajorVersion(String value) {
            this.commonBaselineMajorVersion = value;
        }

        /**
         * Gets the value of the commonBaselineMinorVersion property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCommonBaselineMinorVersion() {
            return commonBaselineMinorVersion;
        }

        /**
         * Sets the value of the commonBaselineMinorVersion property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCommonBaselineMinorVersion(String value) {
            this.commonBaselineMinorVersion = value;
        }

    }

}
