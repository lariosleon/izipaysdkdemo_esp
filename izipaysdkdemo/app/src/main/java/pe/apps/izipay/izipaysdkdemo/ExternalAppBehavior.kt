package pe.apps.izipay.izipaysdkdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import pe.izipay.izipaysdk.baseclass.Constants

/**
 * La respuesta de esta actividad está definida en los métodos 'onAcceptClick' y 'onCancelClick'
 * en función de si debe continuar con la transacción o cancelarse
 */
class ExternalAppBehavior : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createContent()
        context = this
    }

    private fun createContent() {
        setContentView(R.layout.activity_main)
    }

    fun onCancelClick(v: View?) {
        setResult(-1)
        finish()
        overridePendingTransition(0, 0)
    }

    fun onAcceptClick(v: View?) {
        val intent = Intent()
        intent.putExtra(
            "SupportsTransactionVoid",
            (findViewById<View>(R.id.SupportsTransactionVoid) as CheckBox).isChecked
        )
        intent.putExtra(
            "SupportsTransactionQuery",
            (findViewById<View>(R.id.SupportsTransactionQuery) as CheckBox).isChecked
        )
        intent.putExtra(
            "SupportsNegativeSales",
            (findViewById<View>(R.id.SupportsNegativeSales) as CheckBox).isChecked
        )
        intent.putExtra(
            "SupportsPartialRefund",
            (findViewById<View>(R.id.SupportsPartialRefund) as CheckBox).isChecked
        )
        intent.putExtra(
            "SupportsBatchClose",
            (findViewById<View>(R.id.SupportsBatchClose) as CheckBox).isChecked
        )
        intent.putExtra(
            "SupportsTipAdjustment",
            (findViewById<View>(R.id.SupportsTipAdjustment) as CheckBox).isChecked
        )
        intent.putExtra(
            "OnlyCreditForTipAdjustment",
            (findViewById<View>(R.id.OnlyCreditForTipAdjustment) as CheckBox).isChecked
        )
        intent.putExtra(
            "SupportsCredit",
            (findViewById<View>(R.id.SupportsCredit) as CheckBox).isChecked
        )
        intent.putExtra(
            "SupportsDebit",
            (findViewById<View>(R.id.SupportsDebit) as CheckBox).isChecked
        )
        intent.putExtra(
            "SupportsEBTFoodstamp",
            (findViewById<View>(R.id.SupportsEBTFoodstamp) as CheckBox).isChecked
        )
        setResult(RESULT_OK, intent)
        finish()
        overridePendingTransition(0, 0)
    }

    companion object {
        //private View v;
        var context: ExternalAppBehavior? = null
    }
}