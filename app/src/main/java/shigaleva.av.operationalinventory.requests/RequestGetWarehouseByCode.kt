package shigaleva.av.operationalinventory.requests

import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

fun createSoapRequestGetWarehouseByCode(codeWarehouse: String): String {

    // SOAP request body from SOAP UI
    val requestXML = """
            <soap:Envelope xmlns:soap="http://www.w3.org/2003/05/soap-envelope" xmlns:psk="http://psk_ws.klimov.ru">
               <soap:Header/>
               <soap:Body>
                  <psk:GetWarehouseByCode>
                     <psk:WarehouseCode>$codeWarehouse</psk:WarehouseCode>
                  </psk:GetWarehouseByCode>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

    // getting response from SOAP UI
    val responseXML = callSoapService(requestXML)
    //println(responseXML)
    return responseXML
}

// function for receiving response from SOAP UI
private fun callSoapService(soapRequest: String): String {

    var responseString = ""
    var connect: HttpURLConnection? = null

    try {

        // setting up access to the local server
        val wsdURL = "http://192.168.0.109:8080/MockTestService?WSDL"
        val url = URL(wsdURL)
        connect = url.openConnection() as HttpURLConnection

        // setting up request generation
        connect.requestMethod = "GET"
        connect.setRequestProperty("Content-Type", "text/xml; charset=utf-8")
        connect.doOutput = true

        // request record
        val writeRequest = DataOutputStream(connect.outputStream)
        writeRequest.writeBytes(soapRequest)
        writeRequest.flush()
        writeRequest.close()

        // line-by-line recording of response from local server
        val inResponse = BufferedReader(InputStreamReader(connect.inputStream))
        val responseXML = StringBuilder()
        var inputLine: String?
        while (inResponse.readLine().also { inputLine = it } != null) {
            responseXML.append(inputLine)
        }
        inResponse.close()

        // clipping the warehouse name from the response from the local server
        responseString = responseXML.toString()
        responseString = responseString.substring(
            responseString.indexOf("<m:Name>") + 8,
            responseString.indexOf("</m:Name>")
            )

    } catch (e: Exception) {
        e.message ?: ""
    } finally {
        connect?.disconnect()
    }

    return responseString
}

