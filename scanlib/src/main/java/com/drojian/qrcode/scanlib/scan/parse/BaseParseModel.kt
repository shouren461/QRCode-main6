package com.drojian.qrcode.scanlib.scan.parse

import java.util.regex.Pattern

/**
 * @author yangfengfan 2020-08-27
 *
 * 解析结果基类
 */
abstract class BaseParseModel(val parsedFormat: ParsedFormat) {

    abstract fun getShowText(): String?


    protected fun getBestShowContent(vararg string: String?): String? {
        string.forEach { it ->
            it?.let {
                if (it.trim().isNotEmpty()) {
                    return it
                }
            }
        }
        return null
    }

    // Transforms a string that represents a URI into something more proper, by adding or canonicalizing the protocol.
    protected fun massageURI(uri: String): String {
        try {
            var messagedUri = uri.trim { it <= ' ' }
            val protocolEnd = messagedUri.indexOf(':')
            if (protocolEnd < 0 || isColonFollowedByPortNumber(messagedUri, protocolEnd)) {
                // No protocol, or found a colon, but it looks like it is after the host, so the protocol is still missing,
                // so assume http
                messagedUri = "http://$messagedUri"
            }
            return messagedUri
        } catch (e: Exception) {
        }
        return uri;
    }

    private fun isColonFollowedByPortNumber(uri: String, protocolEnd: Int): Boolean {
        val start = protocolEnd + 1
        var nextSlash = uri.indexOf('/', start)
        if (nextSlash < 0) {
            nextSlash = uri.length
        }
        return isSubstringOfDigits(uri, start, nextSlash - start)
    }

    private fun isSubstringOfDigits(value: CharSequence?, offset: Int, length: Int): Boolean {
        if (value == null || length <= 0) {
            return false
        }
        val max = offset + length
        return value.length >= max && Pattern.compile("\\d+").matcher(value.subSequence(offset, max)).matches()
    }

}