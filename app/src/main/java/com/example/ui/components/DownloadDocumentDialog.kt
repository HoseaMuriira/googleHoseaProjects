package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.LessonPlan
import com.example.data.model.SchemeOfWork
import com.example.data.model.UserAccount
import com.example.ui.theme.AmberTertiary
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.TealSecondary
import com.example.util.PdfExporter
import com.example.util.ShareUtils
import com.example.util.WordDocExporter
import java.io.File

sealed class ExportableDoc {
    data class Scheme(val scheme: SchemeOfWork) : ExportableDoc()
    data class Lesson(val plan: LessonPlan) : ExportableDoc()
}

@Composable
fun DownloadDocumentDialog(
    doc: ExportableDoc,
    user: UserAccount? = null,
    onDismiss: () -> Unit,
    onGatedDownload: ((perform: () -> Unit) -> Unit)? = null,
    onTopUpMpesa: (() -> Unit)? = null,
    onPrintWebView: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var isDownloading by remember { mutableStateOf(false) }
    var downloadedFile by remember { mutableStateOf<File?>(null) }
    var downloadedType by remember { mutableStateOf<String?>(null) }

    val (title, fileName, gradeSubjectInfo, docTypeLabel) = when (doc) {
        is ExportableDoc.Scheme -> {
            val s = doc.scheme
            listOf(
                "${s.learningArea} - ${s.grade}",
                "${s.learningArea}_${s.grade}_Scheme_of_Work",
                "${s.schoolName} • ${s.term} ${s.year}",
                "Scheme of Work"
            )
        }
        is ExportableDoc.Lesson -> {
            val p = doc.plan
            listOf(
                "Lesson Plan: ${p.learningArea}",
                "${p.learningArea}_LessonPlan_W${p.week}_L${p.lessonNumber}",
                "${p.grade} • Week ${p.week} Lesson ${p.lessonNumber} • ${p.subStrand}",
                "Lesson Plan"
            )
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .testTag("download_document_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(IndigoPrimary)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Download,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Download & Export Document",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = docTypeLabel,
                                color = AmberTertiary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Download Quota & M-PESA Balance Banner
                    if (user != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (user.totalAvailableDownloads > 0) TealSecondary.copy(alpha = 0.1f) else Color(0xFFFEF2F2),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (user.totalAvailableDownloads > 0) TealSecondary.copy(alpha = 0.3f) else Color(0xFFFCA5A5)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (user.totalAvailableDownloads > 0) "⚡" else "⚠️",
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (user.freeDownloadsRemaining > 0) {
                                                "${user.freeDownloadsRemaining}/3 Free Downloads Left"
                                            } else if (user.paidDownloadsRemaining > 0) {
                                                "${user.paidDownloadsRemaining} Paid Downloads Left"
                                            } else {
                                                "0 Downloads Left (KES 10/doc)"
                                            },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (user.totalAvailableDownloads > 0) TealSecondary else Color(0xFFDC2626)
                                        )
                                        Text(
                                            text = if (user.totalAvailableDownloads > 0) "Auto-synced with @${user.username}" else "Pay KES 10 via Safaricom M-PESA: 0748053644",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (onTopUpMpesa != null) {
                                    Button(
                                        onClick = onTopUpMpesa,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF008751)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Top Up", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    // Document Details Preview Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = gradeSubjectInfo,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Text(
                        text = "CHOOSE DOWNLOAD FORMAT",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = IndigoPrimary
                    )

                    // Helper to execute download
                    val executePdfDownload = {
                        isDownloading = true
                        try {
                            val pdfFile = when (doc) {
                                is ExportableDoc.Scheme -> PdfExporter.generateSchemePdf(context, doc.scheme)
                                is ExportableDoc.Lesson -> PdfExporter.generateLessonPlanPdf(context, doc.plan)
                            }
                            val savedFile = ShareUtils.downloadPdfDoc(context, fileName, pdfFile)
                            downloadedFile = savedFile
                            downloadedType = "application/pdf"
                        } catch (e: Exception) {
                            Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isDownloading = false
                        }
                    }

                    val executeWordDownload = {
                        isDownloading = true
                        try {
                            val html = when (doc) {
                                is ExportableDoc.Scheme -> WordDocExporter.generateSchemeWordHtml(doc.scheme)
                                is ExportableDoc.Lesson -> WordDocExporter.generateLessonPlanWordHtml(doc.plan)
                            }
                            val savedFile = ShareUtils.downloadWordDoc(context, fileName, html)
                            downloadedFile = savedFile
                            downloadedType = "application/msword"
                        } catch (e: Exception) {
                            Toast.makeText(context, "Export error: ${e.message}", Toast.LENGTH_SHORT).show()
                        } finally {
                            isDownloading = false
                        }
                    }

                    // Option 1: PDF Document Card
                    DownloadOptionCard(
                        title = "PDF Document (.pdf)",
                        subtitle = "High-fidelity formatted for printing & official submission",
                        badgeText = "Recommended for Print",
                        badgeColor = Color(0xFFDC2626),
                        icon = Icons.Default.PictureAsPdf,
                        iconTint = Color(0xFFDC2626),
                        onDownload = {
                            if (onGatedDownload != null) {
                                onGatedDownload { executePdfDownload() }
                            } else {
                                executePdfDownload()
                            }
                        },
                        onShare = {
                            val pdfFile = when (doc) {
                                is ExportableDoc.Scheme -> PdfExporter.generateSchemePdf(context, doc.scheme)
                                is ExportableDoc.Lesson -> PdfExporter.generateLessonPlanPdf(context, doc.plan)
                            }
                            ShareUtils.shareAsPdf(context, fileName, pdfFile)
                        }
                    )

                    // Option 2: Microsoft Word Document Card
                    DownloadOptionCard(
                        title = "Microsoft Word Document (.doc)",
                        subtitle = "Fully editable in MS Word, Google Docs, WPS & LibreOffice",
                        badgeText = "Editable Format",
                        badgeColor = Color(0xFF0284C7),
                        icon = Icons.Default.Description,
                        iconTint = Color(0xFF0284C7),
                        onDownload = {
                            if (onGatedDownload != null) {
                                onGatedDownload { executeWordDownload() }
                            } else {
                                executeWordDownload()
                            }
                        },
                        onShare = {
                            val html = when (doc) {
                                is ExportableDoc.Scheme -> WordDocExporter.generateSchemeWordHtml(doc.scheme)
                                is ExportableDoc.Lesson -> WordDocExporter.generateLessonPlanWordHtml(doc.plan)
                            }
                            ShareUtils.shareAsWordDoc(context, fileName, html)
                        }
                    )

                    // Success banner with Quick Open if downloaded
                    if (downloadedFile != null && downloadedType != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = TealSecondary.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TealSecondary.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Download Complete!",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TealSecondary
                                    )
                                    Text(
                                        text = "Saved in Downloads/Schemly",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Button(
                                    onClick = {
                                        ShareUtils.openFile(context, downloadedFile!!, downloadedType!!)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TealSecondary),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                    modifier = Modifier.height(28.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.FileOpen, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Open File", fontSize = 11.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                // Bottom bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onPrintWebView != null) {
                        OutlinedButton(
                            onClick = {
                                onPrintWebView()
                                onDismiss()
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("System Print / PDF", fontSize = 11.5.sp)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Done", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DownloadOptionCard(
    title: String,
    subtitle: String,
    badgeText: String,
    badgeColor: Color,
    icon: ImageVector,
    iconTint: Color,
    onDownload: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = iconTint.copy(alpha = 0.12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = subtitle,
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDownload,
                    colors = ButtonDefaults.buttonColors(containerColor = iconTint),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1.3f).height(34.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Download", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color.White)
                }

                OutlinedButton(
                    onClick = onShare,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(34.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", fontSize = 11.5.sp)
                }
            }
        }
    }
}
