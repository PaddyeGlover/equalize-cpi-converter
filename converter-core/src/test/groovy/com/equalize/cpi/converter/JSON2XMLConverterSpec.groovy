package com.equalize.cpi.converter

import org.apache.camel.CamelContext
import org.apache.camel.Exchange
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.DefaultExchange

import spock.lang.Specification

class JSON2XMLConverterSpec extends Specification {
	static final String filePath = 'src/test/resources/JSON'

	Exchange exchange
	Map<String,Object> properties

	String inputFileName
	String outputFileName
	File expectedOutputFile

	def setup() {
		// Setup the Camel context, Camel exchange
		CamelContext context = new DefaultCamelContext()
		this.exchange = new DefaultExchange(context)
		this.properties = ['converterClass':'com.equalize.cpi.converter.JSON2XMLConverter']
	}

	private process() {
		this.exchange.getIn().setBody(new File("$filePath/$inputFileName"))
		this.expectedOutputFile = new File("$filePath/$outputFileName")

		def fcb = new FormatConversionBean(this.exchange, properties)
		fcb.convert()
	}

	// Reference - https://blogs.sap.com/2015/03/17/jsontransformbean-part-1-converting-json-content-to-xml/

	def 'JSON -> XML - exception is thrown when documentName is not configured'() {
		given:
		this.properties << ['documentNamespace':'urn:equalize:com']

		when:
		process()

		then:
		RuntimeException e = thrown()
		e.message == "Mandatory parameter 'documentName' is missing"
	}

	def 'JSON -> XML - exception is thrown when documentNamespace is not configured'() {
		given:
		this.properties << ['documentName':'MT_JSON2XML']

		when:
		process()

		then:
		RuntimeException e = thrown()
		e.message == "Mandatory parameter 'documentNamespace' is missing"
	}

	def 'JSON -> XML - default with only mandatory parameters'() {
		given:
		this.properties << ['documentName':'MT_JSON2XML']
		this.properties << ['documentNamespace':'urn:equalize:com']
		this.inputFileName = 'JSON2XML_Scenario1_glossary.json'
		this.outputFileName = 'JSON2XML_Scenario1_glossary_output.xml'

		expect:
		new String(process()) == this.expectedOutputFile.text
	}

	def 'JSON -> XML - indentFactor set to 2'() {
		given:
		this.properties << ['indentFactor':'2']
		this.properties << ['documentName':'MT_JSON2XML']
		this.properties << ['documentNamespace':'urn:equalize:com']
		this.inputFileName = 'JSON2XML_Scenario1_glossary.json'
		this.outputFileName = 'JSON2XML_Scenario1_glossary_output_indent.xml'

		expect:
		new String(process()) == this.expectedOutputFile.text
	}

	def 'JSON -> XML - exception is thrown if topArrayName is not configured when allowArrayAtTop is set'() {
		given:
		this.properties << ['documentName':'MT_JSON2XML']
		this.properties << ['documentNamespace':'urn:equalize:com']
		this.properties << ['allowArrayAtTop':'Y']

		when:
		process()

		then:
		RuntimeException e = thrown()
		e.message == "Mandatory parameter 'topArrayName' is missing"
	}

	def 'JSON -> XML - set allowArrayAtTop'() {
		given:
		this.properties << ['indentFactor':'2']
		this.properties << ['documentName':'MT_JSON2XML']
		this.properties << ['documentNamespace':'urn:equalize:com']
		this.properties << ['allowArrayAtTop':'Y']
		this.properties << ['topArrayName':'record']
		this.inputFileName = 'JSON2XML_Scenario3_array.json'
		this.outputFileName = 'JSON2XML_Scenario3_array_output.xml'

		expect:
		new String(process()) == this.expectedOutputFile.text
	}

	def 'JSON -> XML - set escapeInvalidNameStartChar'() {
		given:
		this.properties << ['indentFactor':'2']
		this.properties << ['documentName':'MT_JSON2XML']
		this.properties << ['documentNamespace':'urn:equalize:com']
		this.properties << ['escapeInvalidNameStartChar':'Y']
		this.inputFileName = 'JSON2XML_Scenario2.json'
		this.outputFileName = 'JSON2XML_Scenario2_output.xml'

		expect:
		new String(process()) == this.expectedOutputFile.text
	}

	def 'JSON -> XML - set mangleInvalidNameChar'() {
		given:
		this.properties << ['indentFactor':'2']
		this.properties << ['documentName':'MT_JSON2XML']
		this.properties << ['documentNamespace':'urn:equalize:com']
		this.properties << ['mangleInvalidNameChar':'Y']
		this.inputFileName = 'JSON2XML_Scenario4.json'
		this.outputFileName = 'JSON2XML_Scenario4_output.xml'

		expect:
		new String(process()) == this.expectedOutputFile.text
	}
}