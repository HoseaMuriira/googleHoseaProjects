package com.example.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.LessonPlan
import com.example.data.model.SchemeOfWork
import com.example.ui.components.DownloadDocumentDialog
import com.example.ui.components.ExportableDoc
import com.example.ui.components.SchemlyTopBar
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.TealSecondary
import com.example.util.PdfExporter
import com.example.util.ShareUtils
import com.example.util.WordDocExporter
import com.example.viewmodel.CurrentDocView

@Composable
fun WordDocViewerScreen(
    docView: CurrentDocView,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var showDownloadDialog by remember { mutableStateOf(false) }

    val (docTitle, docFileName, htmlContent, exportableDoc) = when (docView) {
        is CurrentDocView.SchemeDoc -> {
            val scheme = docView.scheme
            listOf(
                "${scheme.learningArea} - ${scheme.grade} Scheme",
                "${scheme.learningArea}_${scheme.grade}_Scheme_of_Work",
                WordDocExporter.generateSchemeWordHtml(scheme),
                ExportableDoc.Scheme(scheme)
            )
        }
        is CurrentDocView.LessonDoc -> {
            val plan = docView.plan
            listOf(
                "Lesson Plan: ${plan.learningArea} (W${plan.week} L${plan.lessonNumber})",
                "${plan.learningArea}_LessonPlan_W${plan.week}_L${plan.lessonNumber}",
                WordDocExporter.generateLessonPlanWordHtml(plan),
                ExportableDoc.Lesson(plan)
            )
        }
    }

    val docTitleStr = docTitle as String
    val docFileNameStr = docFileName as String
    val htmlContentStr = htmlContent as String
    val expDoc = exportableDoc as ExportableDoc

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            SchemlyTopBar(
                title = docTitleStr,
                subtitle = "Document Preview • PDF & Word Downloads",
                currentScreen = "word_viewer",
                onNavigateHome = onNavigateBack,
                onShareWordDoc = {
                    showDownloadDialog = true
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFE2E8F0))
        ) {
            // Action Banner with direct PDF and Word download buttons
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Downloadable Documents Ready",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = IndigoPrimary
                        )
                        Text(
                            text = "PDF (.pdf) & Microsoft Word (.doc)",
                            fontSize = 10.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Download PDF Button
                        Button(
                            onClick = {
                                val pdfFile = when (expDoc) {
                                    is ExportableDoc.Scheme -> PdfExporter.generateSchemePdf(context, expDoc.scheme)
                                    is ExportableDoc.Lesson -> PdfExporter.generateLessonPlanPdf(context, expDoc.plan)
                                }
                                ShareUtils.downloadPdfDoc(context, docFileNameStr, pdfFile)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = Color.White, modifier = Modifier.height(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // Download Word Button
                        Button(
                            onClick = {
                                ShareUtils.downloadWordDoc(context, docFileNameStr, htmlContentStr)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, tint = Color.White, modifier = Modifier.height(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Word", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }

                        // System Print / Save as PDF
                        IconButton(
                            onClick = {
                                webViewInstance?.let { wv ->
                                    PdfExporter.printOrSaveAsPdf(context, wv, docFileNameStr)
                                }
                            },
                            modifier = Modifier.height(32.dp).width(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Print, contentDescription = "Print / Save PDF", tint = IndigoPrimary, modifier = Modifier.height(18.dp))
                        }

                        // Share / Export modal
                        IconButton(
                            onClick = { showDownloadDialog = true },
                            modifier = Modifier.height(32.dp).width(32.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "More Export Options", tint = TealSecondary, modifier = Modifier.height(18.dp))
                        }
                    }
                }
            }

            // Authentic Document Render
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp)
                    .testTag("word_doc_webview")
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White, RoundedCornerShape(4.dp)),
                    factory = { ctx ->
                        WebView(ctx).apply {
                            webViewClient = WebViewClient()
                            settings.javaScriptEnabled = false
                            settings.builtInZoomControls = true
                            settings.displayZoomControls = false
                            settings.useWideViewPort = true
                            settings.loadWithOverviewMode = true
                            loadDataWithBaseURL(null, htmlContentStr, "text/html", "UTF-8", null)
                            webViewInstance = this
                        }
                    },
                    update = { webView ->
                        webViewInstance = webView
                        webView.loadDataWithBaseURL(null, htmlContentStr, "text/html", "UTF-8", null)
                    }
                )
            }
        }
    }

    if (showDownloadDialog) {
        DownloadDocumentDialog(
            doc = expDoc,
            onDismiss = { showDownloadDialog = false },
            onPrintWebView = {
                webViewInstance?.let { wv ->
                    PdfExporter.printOrSaveAsPdf(context, wv, docFileNameStr)
                }
            }
        )
    }
}

