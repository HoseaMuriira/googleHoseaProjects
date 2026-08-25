package com.example.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object ShareUtils {

    /**
     * Downloads document as Microsoft Word compatible (.doc) file into the device Downloads folder.
     */
    fun downloadWordDoc(context: Context, fileName: String, htmlContent: String): File? {
        val sanitizedName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val finalFileName = if (sanitizedName.endsWith(".doc")) sanitizedName else "$sanitizedName.doc"

        try {
            // Save to Public Downloads via MediaStore on Android 10+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, finalFileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/msword")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Schemly")
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        out.write(htmlContent.toByteArray(Charsets.UTF_8))
                    }
                }
            }

            // Also save to app external files / standard Downloads for direct File access
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val schemlyDir = File(downloadsDir, "Schemly").apply { if (!exists()) mkdirs() }
            val targetFile = File(if (schemlyDir.exists()) schemlyDir else (context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.cacheDir), finalFileName)

            FileOutputStream(targetFile).use { out ->
                out.write(htmlContent.toByteArray(Charsets.UTF_8))
            }

            MediaScannerConnection.scanFile(context, arrayOf(targetFile.absolutePath), arrayOf("application/msword"), null)
            Toast.makeText(context, "Saved Word Doc to Downloads/Schemly/$finalFileName", Toast.LENGTH_LONG).show()
            return targetFile
        } catch (e: Exception) {
            // Fallback to cacheDir
            val cacheFile = File(context.cacheDir, finalFileName)
            FileOutputStream(cacheFile).use { out ->
                out.write(htmlContent.toByteArray(Charsets.UTF_8))
            }
            Toast.makeText(context, "Saved Word Doc: $finalFileName", Toast.LENGTH_SHORT).show()
            return cacheFile
        }
    }

    /**
     * Downloads PDF file into the device Downloads folder.
     */
    fun downloadPdfDoc(context: Context, fileName: String, sourcePdfFile: File): File? {
        val sanitizedName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val finalFileName = if (sanitizedName.endsWith(".pdf")) sanitizedName else "$sanitizedName.pdf"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, finalFileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Schemly")
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        FileInputStream(sourcePdfFile).use { input ->
                            input.copyTo(out)
                        }
                    }
                }
            }

            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val schemlyDir = File(downloadsDir, "Schemly").apply { if (!exists()) mkdirs() }
            val targetFile = File(if (schemlyDir.exists()) schemlyDir else (context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.cacheDir), finalFileName)

            FileInputStream(sourcePdfFile).use { input ->
                FileOutputStream(targetFile).use { out ->
                    input.copyTo(out)
                }
            }

            MediaScannerConnection.scanFile(context, arrayOf(targetFile.absolutePath), arrayOf("application/pdf"), null)
            Toast.makeText(context, "Saved PDF to Downloads/Schemly/$finalFileName", Toast.LENGTH_LONG).show()
            return targetFile
        } catch (e: Exception) {
            Toast.makeText(context, "Saved PDF: $finalFileName", Toast.LENGTH_SHORT).show()
            return sourcePdfFile
        }
    }

    /**
     * Shares document as a Microsoft Word compatible (.doc) file via Android Share Intent
     */
    fun shareAsWordDoc(context: Context, fileName: String, htmlContent: String) {
        try {
            val docsFolder = File(context.cacheDir, "documents")
            if (!docsFolder.exists()) docsFolder.mkdirs()

            val sanitizedName = fileName.replace(Regex("[^a-zA-Z0-9._-]"), "_")
            val file = File(docsFolder, "$sanitizedName.doc")
            FileOutputStream(file).use { out ->
                out.write(htmlContent.toByteArray(Charsets.UTF_8))
            }

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/msword"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, fileName)
                putExtra(Intent.EXTRA_TEXT, "Here is the CBC Scheme of Work / Lesson Plan (.doc): $fileName")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share Word Document"))
        } catch (e: Exception) {
            shareAsPlainText(context, fileName, htmlContent)
        }
    }

    /**
     * Shares document as a PDF (.pdf) file via Android Share Intent
     */
    fun shareAsPdf(context: Context, fileName: String, pdfFile: File) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, fileName)
                putExtra(Intent.EXTRA_TEXT, "Here is the CBC Scheme of Work / Lesson Plan (PDF): $fileName")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share PDF Document"))
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to share PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens a downloaded file in an external viewer (Word, Docs, Acrobat, Drive PDF, etc.)
     */
    fun openFile(context: Context, file: File, mimeType: String) {
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(Intent.createChooser(intent, "Open with"))
        } catch (e: Exception) {
            Toast.makeText(context, "No app found to open this file format.", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareAsPlainText(context: Context, title: String, content: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share Document"))
    }

    fun copyToClipboard(context: Context, label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
    }
}

