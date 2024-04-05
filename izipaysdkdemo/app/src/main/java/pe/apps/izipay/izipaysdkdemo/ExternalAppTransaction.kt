package pe.apps.izipay.izipaysdkdemo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import pe.apps.izipay.izipaysdkdemo.utils.StringUtils.repeat
import java.text.SimpleDateFormat
import java.util.*

/**
 * La respuesta de esta actividad está definida en el método 'response'.
 * Desde este método se definirán los datos de respuesta y si debe ser RESULT_OK o RESULT_CANCELED
 *
 * El comprobante que se envía de vuelta a FrontRetail Mobile está definido en el método 'getComprobante'
 */
@SuppressLint("DefaultLocale", "SimpleDateFormat")
class ExternalAppTransaction : Activity() {
    private var transactionType: String? = null
    private var amount: Double? = null
    private var taxes: Double? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createContent()
        context = this
    }

    private fun createContent() {
        transactionType = intent.extras!!.getString("TransactionType")
        val tvTransactionData = findViewById<View>(R.id.transactionData) as TextView
        tvTransactionData.visibility = View.INVISIBLE
        val tvCobro = findViewById<View>(R.id.textCobro) as TextView
        if (transactionType == "SALE") tvCobro.text =
            "Operación recibida (Venta)" else if (transactionType == "REFUND") {
            tvCobro.text = "Operación recibida (Abono)"
            val transactionData = intent.extras!!.getString("TransactionData")
            tvTransactionData.visibility = View.VISIBLE
            tvTransactionData.text = "Transacción original: $transactionData"
        } else if (transactionType == "VOID_TRANSACTION") tvCobro.text =
            "Operación recibida (Void)" else if (transactionType == "QUERY_TRANSACTION") tvCobro.text =
            "Operación recibida (Query)" else tvCobro.text = "Operación recibida ($transactionType)"
        val tvId = findViewById<View>(R.id.idCobro) as TextView
        val id = intent.extras!!.getString("TransactionId")
        tvId.text = "ID Transacción: $id"
        val tvTax = findViewById<View>(R.id.textTotalTaxes) as TextView
        if (intent.hasExtra("TaxAmount")) {
            val taxesText = intent.extras!!.getString("TaxAmount")
            val taxesParse =
                taxesText!!.substring(0, taxesText.length - 2) + "." + taxesText.substring(
                    taxesText.length - 2, taxesText.length
                )
            taxes = java.lang.Double.valueOf(taxesParse)
            tvTax.text = String.format("Tasas incluidas: %.2f €", taxes)
        } else tvTax.visibility = View.INVISIBLE
        val amountText = intent.extras!!.getString("Amount")
        val amountParse =
            amountText!!.substring(0, amountText.length - 2) + "." + amountText.substring(
                amountText.length - 2, amountText.length
            )
        amount = java.lang.Double.valueOf(amountParse)
        val tvImporte = findViewById<View>(R.id.appextImporte) as TextView
        tvImporte.text = String.format("Importe: %.2f €", amount)
    }

    fun onCancelClick(v: View?) {
        response("", false)
    }

    fun onAcceptClick(v: View?) {
        response("ACCEPTED", true)
    }

    fun onRefusedClick(v: View?) {
        response("FAILED", true)
    }

    fun onUnknownClick(v: View?) {
        response("UNKNOWN_RESULT", true)
    }

    private fun response(transactionValue: String, bOk: Boolean) {
        if (bOk) {
            val intent = Intent()
            intent.putExtra("TransactionResult", transactionValue)
            intent.putExtra("TransactionType", transactionType)
            intent.putExtra("Amount", amount)
            intent.putExtra("TipAmount", 0)
            intent.putExtra("TaxAmount", 0)
            intent.putExtra("BatchNumber", "TEST")
            if (transactionType == "SALE") intent.putExtra(
                "TransactionData",
                "AJH1239425NZ2NHH2-24566F2"
            )
            intent.putExtra("MerchantReceipt", comprobante)
            intent.putExtra("CustomerReceipt", comprobante)
            intent.putExtra("IdTransaction", "123456789012345")
            intent.putExtra("IdOperation", "AB845684DAWDAD654AWDAWD6AWDAWD")
            intent.putExtra("CardBin", "**7018")
            intent.putExtra("CardType", "VISA")
            intent.putExtra("ErrorMessage", "Error de prueba.")
            setResult(-1, intent)
        } else setResult(-1)
        finish()
        overridePendingTransition(0, 0)
    }


    // Separador

    // COMPROBANTE

    // Separador

    // Población

    // Provincia

    // Código Postal

    // Dirección

    // Fecha

    // Separador

    // Importe

    // Tasas incluidas

    // Separador

    // ID Transacción

    // Corte de papel

    // Separador

    // Código QR

    // Separador
    private val comprobante: String
        private get() {
            var result = ""
            val XML = ArrayList<String>()
            XML.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
            XML.add("<Receipt numCols=\"42\">")

            // Separador
            XML.add("<ReceiptLine type=\"TEXT\">")
            XML.add("	<Text>                                         </Text>")
            XML.add("</ReceiptLine>")

            // COMPROBANTE
            XML.add("<ReceiptLine type=\"TEXT\">")
            XML.add("	<Formats>")
            XML.add("		<Format from=\"0\" to=\"42\">NORMAL</Format>")
            XML.add("	</Formats>")
            XML.add("	<Text>               COMPROBANTE               </Text>")
            XML.add("</ReceiptLine>")
            if (transactionType == "REFUND") {
                XML.add("<ReceiptLine type=\"TEXT\">")
                XML.add("	<Formats>")
                XML.add("		<Format from=\"0\" to=\"42\">NORMAL</Format>")
                XML.add("	</Formats>")
                XML.add("	<Text>                  ABONO                   </Text>")
                XML.add("</ReceiptLine>")
            }

            // Separador
            XML.add("<ReceiptLine type=\"TEXT\">")
            XML.add("	<Text>                                         </Text>")
            XML.add("</ReceiptLine>")

            // Población
            XML.add("<ReceiptLine type=\"TEXT\">")
            XML.add("	<Formats>")
            XML.add("		<Format from=\"0\" to=\"30\">NORMAL</Format>")
            XML.add("		<Format from=\"30\" to=\"42\">BOLD</Format>")
            XML.add("	</Formats>")
            XML.add("	<Text>Población:                    Torrefarrera</Text>")
            XML.add("</ReceiptLine>")

            // Provincia
            XML.add("<ReceiptLine type=\"TEXT\">")
            XML.add("	<Formats>")
            XML.add("		<Format from=\"0\" to=\"35\">NORMAL</Format>")
            XML.add("		<Format from=\"35\" to=\"42\">BOLD</Format>")
            XML.add("	</Formats>")
            XML.add("	<Text>Provincia:                          Lleida</Text>")
            XML.add("</ReceiptLine>")

            // Código Postal
            XML.add("<ReceiptLine type=\"TEXT\">")
            XML.add("	<Formats>")
            XML.add("		<Format from=\"0\" to=\"37\">NORMAL</Format>")
            XML.add("		<Format from=\"37\" to=\"42\">BOLD</Format>")
            XML.add("	</Formats>")
            XML.add("	<Text>Codigo Postal:                       25123</Text>")
            XML.add("</ReceiptLine>")

            // Dirección
            XML.add("<ReceiptLine type=\"TEXT\">")
            XML.add("	<Formats>")
            XML.add("		<Format from=\"0\" to=\"28\">NORMAL</Format>")
            XML.add("		<Format from=\"28\" to=\"42\">BOLD</Format>")
            XML.add("	</Formats>")
            XML.add("	<Text>Dirección:                  C/ Mestral s/n</Text>")
            XML.add("</ReceiptLine>")

            // Fecha
            XML.add("<ReceiptLine type=\"TEXT\">")
            XML.add("	<Formats>")
            XML.add("		<Format from=\"0\" to=\"32\">NORMAL</Format>")
            XML.add("		<Format from=\"32\" to=\"42\">BOLD</Format>")
            XML.add("	</Formats>")
            XML.add("	<Text>" + formatDate("Fecha:", Date()) + "</Text>")
            XML.add("</ReceiptLine>")

            // Separador
            XML.add("<ReceiptLine type=\"TEXT\">")
            XML.add("	<Text>                                         </Text>")
            XML.add("</ReceiptLine>")

            // Importe
            XML.add("<ReceiptLine type=\"TEXT\">")
            XML.add("	<Formats>")
            XML.add("		<Format from=\"0\" to=\"42\">DOUBLE_HEIGHT</Format>")
            XML.add("	</Formats>")
            XML.add("	<Text>" + getImporteFormat("Importe:", amount!!) + "</Text>")
            XML.add("</ReceiptLine>")

            // Tasas incluidas
            if (taxes != null) {
                XML.add("<ReceiptLine type=\"TEXT\">")
                XML.add("	<Formats>")
                XML.add("		<Format from=\"0\" to=\"42\">NORMAL</Format>")
                XML.add("	</Formats>")
                XML.add("	<Text>" + getImporteFormat("Tasas incluidas:", taxes!!) + "</Text>")
                XML.add("</ReceiptLine>")
            }

            // Separador
            XML.add("<ReceiptLine type=\"TEXT\">")
            XML.add("	<Text>                                         </Text>")
            XML.add("</ReceiptLine>")

            // ID Transacción
            XML.add("<ReceiptLine type=\"TEXT\">")
            XML.add("	<Formats>")
            XML.add("		<Format from=\"0\" to=\"42\">NORMAL</Format>")
            XML.add("	</Formats>")
            XML.add("	<Text>ID Transacción: AJH1239425NZ2NHH2-24566F2</Text>")
            XML.add("</ReceiptLine>")

            // Corte de papel
            XML.add("<ReceiptLine type=\"CUT_PAPER\">")
            XML.add("</ReceiptLine>")

            // Separador
            XML.add("<ReceiptLine type=\"TEXT\">")
            XML.add("	<Text>                                         </Text>")
            XML.add("</ReceiptLine>")

            // Código QR
            XML.add("<ReceiptLine type=\"QR_CODE\">")
            XML.add("	<Text>Transacción realizada en entorno de pruebas</Text>")
            XML.add("</ReceiptLine>")

            // Separador
            XML.add("<ReceiptLine type=\"TEXT\">")
            XML.add("	<Text>                                         </Text>")
            XML.add("</ReceiptLine>")
            XML.add("</Receipt>")
            for (line in XML) result += line
            return result
        }

    private fun getImporteFormat(literal: String, value: Double): String {
        var result = ""
        val importe = String.format("%.2f €", value)
        val spaces = 42 - literal.length - importe.length
        repeat(" ", spaces)
        result = literal + repeat(" ", spaces) + importe
        return result
    }

    private fun formatDate(literal: String, date: Date): String {
        var result = ""
        val format = SimpleDateFormat("dd/MM/yyyy")
        val dateString = format.format(date)
        val spaces = 42 - literal.length - dateString.length
        result = literal + repeat(" ", spaces) + dateString
        return result
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            onCancelClick(null)
            true
        } else super.onKeyDown(keyCode, event)
    }

    companion object {
        var context: ExternalAppTransaction? = null
    }
}