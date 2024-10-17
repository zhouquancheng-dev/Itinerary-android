package com.example.common.util.mime

import java.util.regex.Pattern

class MimeTypeMap private constructor() {

    companion object {

        private val sMimeTypeMap = MimeTypeMap()

        /**
         * Returns the file extension or an empty string iff there is no
         * extension. This method is a convenience method for obtaining the
         * extension of a url and has undefined results for other Strings.
         *
         * @param url The URL to the file
         * @return The file extension of the given url.
         */
        fun getFileExtensionFromUrl(url: String?): String {
            var urlVar = url
            if (!urlVar.isNullOrEmpty()) {
                val fragment = urlVar.lastIndexOf('#')
                if (fragment > 0) {
                    urlVar = urlVar.substring(0, fragment)
                }

                val query = urlVar.lastIndexOf('?')
                if (query > 0) {
                    urlVar = urlVar.substring(0, query)
                }

                val filenamePos = urlVar.lastIndexOf('/')
                val filename = if (filenamePos >= 0) urlVar.substring(filenamePos + 1) else urlVar

                // if the filename contains special characters, we don't
                // consider it valid for our matching purposes:
                if (filename.isNotEmpty() &&
                    Pattern.matches("[a-zA-Z_0-9.\\-()%]+", filename)) {
                    val dotPos = filename.lastIndexOf('.')
                    if (dotPos >= 0) {
                        return filename.substring(dotPos + 1)
                    }
                }
            }
            return ""
        }

        /**
         * Get the singleton instance of MimeTypeMap.
         *
         * @return The singleton instance of the MIME-type map.
         */
        val singleton: MimeTypeMap
            get() = sMimeTypeMap
    }

    /**
     * Return true if the given MIME type has an entry in the map.
     *
     * @param mimeType A MIME type (i.e. text/plain)
     * @return True iff there is a mimeType entry in the map.
     */
    fun hasMimeType(mimeType: String): Boolean {
        return MimeUtils.hasMimeType(mimeType)
    }

    /**
     * Return the MIME type for the given extension.
     *
     * @param extension A file extension without the leading '.'
     * @return The MIME type for the given extension or null iff there is none.
     */
    fun getMimeTypeFromExtension(extension: String): String? {
        return MimeUtils.guessMimeTypeFromExtension(extension)
    }

    /**
     * Return true if the given extension has a registered MIME type.
     *
     * @param extension A file extension without the leading '.'
     * @return True iff there is an extension entry in the map.
     */
    fun hasExtension(extension: String): Boolean {
        return MimeUtils.hasExtension(extension)
    }

    /**
     * Return the registered extension for the given MIME type. Note that some
     * MIME types map to multiple extensions. This call will return the most
     * common extension for the given MIME type.
     *
     * @param mimeType A MIME type (i.e. text/plain)
     * @return The extension for the given MIME type or null iff there is none.
     */
    fun getExtensionFromMimeType(mimeType: String): String? {
        return MimeUtils.guessExtensionFromMimeType(mimeType)
    }
}