package pe.apps.izipay.izipaysdkdemo.utils

object StringUtils {
    fun repeat(s: String, i: Int): String {
        var r = ""
        for (j in 1..i) {
            r += s
        }
        return r
    }
}