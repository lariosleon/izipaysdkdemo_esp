package pe.apps.izipay.izipaysdkdemo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.databinding.DataBindingUtil
import pe.apps.izipay.izipaysdkdemo.databinding.ActivityMainBinding
import pe.izipay.izipaysdk.baseclass.Constants.Environment
import pe.izipay.izipaysdk.baseclass.Constants.Operation
import pe.izipay.izipaysdk.baseclass.Constants.Currency
import pe.izipay.izipaysdk.baseclass.Constants.Mode
import pe.izipay.izipaysdk.entities.OperationInfo
import pe.izipay.izipaysdk.entities.OperationResult
import pe.izipay.izipaysdk.entities.OperationResult.TypeResult
import pe.izipay.izipaysdk.views.ui.IzipaySDK

private const val ENTER = "\r\n"

class MainActivity : AppCompatActivity() {

    private val REQUEST_BEHAVIOR = 1
    private val REQUEST_TRANSACTION = 2
    private lateinit var binding: ActivityMainBinding

    private val startForResult =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { handleSDKResponse(it) }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.pay.setOnClickListener {
            purchase()
        }
        binding.voidButton.setOnClickListener {
            void()
        }
        binding.reprint.setOnClickListener {
            reprint()
        }
        binding.report.setOnClickListener {
            detailedReport()
        }
        binding.settings.setOnClickListener {
            info()
        }
        /**
         * ·  En el evento prinicpal de creación se filtrará la acción recibida
         * ·  Esta acción se puede obtener de 'getIntent().getAction()'
         *
         * ·  En el ejemplo de esta APK llamamos a dos actividades diferentes en función de la acción
         * ·  La comunicación puede hacerse sin necesidad de llamar a una actividad
         * siempre y cuando se devuelva un RESULT_OK si se desea continuar con la transacción
         * o un RESULT_CANCELED si se desea cancelar la transacción
         *
         * ·  Los datos de respuesta se deben implementar en función del documento 'FRM - Cobro TEF APP Externa 1.0.pdf'
         */

        // Nombre de las acciones
        // Este nombre es el definido en el <intent-filter> del 'AndroidManifest.xml'
        val accionGetBehavior = "icg.actions.electronicpayment.izipay.GET_BEHAVIOR"
        val accionTransaction = "icg.actions.electronicpayment.izipay.TRANSACTION"
        when (intent.action) {
            /**accionGetBehavior -> {
                val behaviorIntent = Intent(this, ExternalAppBehavior::class.java)
                startActivityForResult(behaviorIntent, REQUEST_BEHAVIOR)
            }
            accionTransaction -> {
                val transactionIntent = Intent(this, ExternalAppTransaction::class.java)
                transactionIntent.putExtras(intent.extras!!)
                startActivityForResult(transactionIntent, REQUEST_TRANSACTION)
            }*/
            accionGetBehavior -> {
               doBehavior()
            }
            accionTransaction -> {
                doTransaction(intent)
            }
        }
    }

    private fun doBehavior(){
        setResult (RESULT_OK)
        finish()
    }

    private fun doTransaction(intent: Intent){
        val transactionType = intent.extras!!.getString("TransactionKey")
        val id: String = intent.extras !!.getString ("TransactionId").toString()
        val amountText = intent.extras !!.getString ("Amount")
        val amountParce = amountText!!.substring(0,amountText.length -2) + "." + amountText.substring(
            amountText.length -2,amountText.length
        )
        if (transactionType=="SALE")
            sendDataToSDK(operationType= Operation.PURCHASE, amount = amountParce)
        else if (transactionType=="REFUND")
            sendDataToSDK(operationType= Operation.VOID , amount = amountParce , referenceCode= id)
        else{
            val resultIntent = Intent()
            resultIntent.putExtra("ErrorMessage","tipo transaccion desconocidad")
            setResult(RESULT_OK,resultIntent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_BEHAVIOR, REQUEST_TRANSACTION -> {
                setResult(resultCode, data)
                finish()
            }
        }
    }

    // This method shows the implementation to make a payment of 1 PEN
    private fun purchase() {
        sendDataToSDK(operationType = Operation.PURCHASE, amount = "1")
    }

    private fun purchaseWithTip() {
        sendDataToSDK(
            operationType = Operation.PURCHASE, amount = "5",
            tipAmount = "0.50", waiterCode = "01"
        )
    }

    private fun void() {
        // Replace the amount and the reference number to be void
        sendDataToSDK(operationType = Operation.VOID, amount = "1", referenceCode = binding.refNumber.text.toString())
    }

    private fun reprint() {
        sendDataToSDK(operationType = Operation.REPRINT, referenceCode = binding.refNumber.text.toString())
    }

    private fun logon() {
        sendDataToSDK(operationType = Operation.LOGON)
    }

    private fun info() {
        sendDataToSDK(operationType = Operation.INFO)
    }

    private fun detailedReport() {
        sendDataToSDK(operationType = Operation.DETAILED_REPORT)
    }

    private fun sendDataToSDK(
        operationType: Operation,
        amount: String = "",
        tipAmount: String = "",
        waiterCode: String = "",
        referenceCode: String = ""
    ) {
        val info = OperationInfo()
        info.apply {
            this.operationType = operationType
            currency = Currency.SOLES
            isGuestMode=true
            environment = Environment.RELEASE
            mode = Mode.ACQUIRER
        }

        amount.takeIf { it.isNotBlank() }?.let {
            info.amount = it.toDouble()
        }

        tipAmount.takeIf {
            it.isNotBlank()
        }?.let { info.tipAmount = it.toDouble() }

        waiterCode.takeIf { it.isNotBlank() }?.let { info.waiterCode = it }
        referenceCode.takeIf { it.isNotBlank() }?.let { info.referenceCode = it }

        Log.d("FATAL", "Sending to SDK: $info")

        val intent = Intent(this, IzipaySDK::class.java)
        val bundle = Bundle()
        bundle.apply {
            putSerializable(OperationInfo.OPERATION_INFO, info)
        }
        intent.putExtras(bundle)

        startForResult.launch(intent)
    }

    private fun handleSDKResponse(intent: Intent) {
        val bundle = intent.extras
        bundle?.let {
            val results = StringBuilder()
            val opResult = it.get(OperationResult.OPERATION_RESULT) as OperationResult

            when (opResult.typeResult) {
                TypeResult.ERROR, TypeResult.INFO -> {
                    results.append("TypeResult = ").append(opResult.typeResult).append(ENTER)
                    results.append("ResponseCode = ${opResult.responseCode}").append(ENTER)
                    results.append("ResponseMessage = ${opResult.responseMessage}").append(ENTER)
                }
                TypeResult.LOGON -> {
                    results.append("TypeResult = ").append(opResult.typeResult).append(ENTER)
                    results.append("ResponseCode = ${opResult.responseCode}").append(ENTER)
                    results.append("ResponseMessage = ${opResult.responseMessage}").append(ENTER)
                    results.append("tipOn = ${opResult.isTipOn}").append(ENTER)
                    results.append("dollarsOn = ${opResult.isDollarsOn}").append(ENTER)
                    results.append("iziJrOn = ${opResult.isIziJrOn}").append(ENTER)
                }
                TypeResult.REPORTS -> {
                    results.append("TypeResult = ").append(opResult.typeResult).append(ENTER)
                    results.append("Voucher = ").append(opResult.voucher)
                }
                TypeResult.PURCHASE -> {
                    results.append("TypeResult = ").append(opResult.typeResult).append(ENTER)
                    results.append("ResponseCode = ").append(opResult.responseCode).append(ENTER)
                    results.append("ResponseMessage = ").append(opResult.responseMessage)
                        .append(ENTER)
                    results.append("TransactionResult = ").append(opResult.transactionResult)
                        .append(ENTER)
                    results.append("MerchantName = ").append(opResult.merchantName).append(ENTER)
                    results.append("MaskedCard = ").append(opResult.maskedCard).append(ENTER)
                    results.append("CardBin = ").append(opResult.cardBin).append(ENTER)
                    results.append("CardBrand = ").append(opResult.cardBrand).append(ENTER)
                    results.append("BuyerName = ").append(opResult.buyerName).append(ENTER)
                    results.append("Currency = ").append(opResult.currency).append(ENTER)
                    results.append("Amount = ").append(opResult.amount).append(ENTER)
                    results.append("TipAmount = ").append(opResult.tipAmount).append(ENTER)
                    results.append("WaiterCode = ").append(opResult.waiterCode).append(ENTER)
                    results.append("ReferenceNumber = ").append(opResult.referenceNumber)
                        .append(ENTER)
                    results.append("Installments = ").append(opResult.installments).append(ENTER)
                    results.append("InstallmentsAmount = ").append(opResult.installmentsAmount).append(ENTER)
                    results.append("FirstDueDate = ").append(opResult.firstDueDate).append(ENTER)
                    results.append("TransactionId = ").append(opResult.transactionId).append(ENTER)
                    results.append("PhoneIMEI = ").append(opResult.phoneIMEI).append(ENTER)
                    results.append("ApprovalCode = ").append(opResult.approvalCode).append(ENTER)
                    results.append("TerminalID = ").append(opResult.terminalID).append(ENTER)
                    results.append("BatchNumber = ").append(opResult.batchNumber).append(ENTER)
                    results.append("TransactionDate = ").append(opResult.transactionDate)
                        .append(ENTER)
                    results.append("TransactionTime = ").append(opResult.transactionTime)
                        .append(ENTER)
                    results.append("AID = ").append(opResult.aid).append(ENTER)
                    results.append("AppLabel = ").append(opResult.appLabel).append(ENTER)
                    results.append("Voucher = ").append(opResult.voucher).append(ENTER)
                    results.append("SignatureInBase64 = ").append(opResult.signatureInBase64)
                        .append(ENTER)
                }
                TypeResult.VOID -> {
                    results.append("TypeResult = ").append(opResult.typeResult).append(ENTER)
                    results.append("ResponseCode = ").append(opResult.responseCode).append(ENTER)
                    results.append("ResponseMessage = ").append(opResult.responseMessage)
                        .append(ENTER)
                    results.append("TransactionResult = ").append(opResult.transactionResult)
                        .append(ENTER)
                    results.append("MerchantName = ").append(opResult.merchantName).append(ENTER)
                    results.append("MaskedCard = ").append(opResult.maskedCard).append(ENTER)
                    results.append("CardBin = ").append(opResult.cardBin).append(ENTER)
                    results.append("CardBrand = ").append(opResult.cardBrand).append(ENTER)
                    results.append("BuyerName = ").append(opResult.buyerName).append(ENTER)
                    results.append("Currency = ").append(opResult.currency).append(ENTER)
                    results.append("Amount = ").append(opResult.amount).append(ENTER)
                    results.append("ReferenceNumber = ").append(opResult.referenceNumber)
                        .append(ENTER)
                    results.append("Installments = ").append(opResult.installments).append(ENTER)
                    results.append("InstallmentsAmount = ").append(opResult.installmentsAmount).append(ENTER)
                    results.append("TransactionId = ").append(opResult.transactionId).append(ENTER)
                    results.append("PhoneIMEI = ").append(opResult.phoneIMEI).append(ENTER)
                    results.append("ApprovalCode = ").append(opResult.approvalCode).append(ENTER)
                    results.append("TerminalID = ").append(opResult.terminalID).append(ENTER)
                    results.append("BatchNumber = ").append(opResult.batchNumber).append(ENTER)
                    results.append("TransactionDate = ").append(opResult.transactionDate)
                        .append(ENTER)
                    results.append("TransactionTime = ").append(opResult.transactionTime)
                        .append(ENTER)
                    results.append("Voucher = ").append(opResult.voucher).append(ENTER)
                }
                else -> {
                    results.append(opResult)
                }
            }
            binding.results.text = results
            Log.d("FATAL", "Results: $results")
        }
    }
}