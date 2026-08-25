package com.example.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.print.PrintAttributes
import android.print.PrintManager
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.webkit.WebView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.LessonPlan
import com.example.data.model.SchemeLessonRow
import com.example.data.model.SchemeOfWork
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object PdfExporter {

    private const val A4_WIDTH_PORTRAIT = 595
    private const val A4_HEIGHT_PORTRAIT = 842

    private const val A4_WIDTH_LANDSCAPE = 842
    private const val A4_HEIGHT_LANDSCAPE = 595

    /**
     * Generates a standard multi-page PDF document for a Scheme of Work.
     */
    fun generateSchemePdf(context: Context, scheme: SchemeOfWork): File {
        val pdfDoc = PdfDocument()
        val pageWidth = A4_WIDTH_LANDSCAPE
        val pageHeight = A4_HEIGHT_LANDSCAPE
        val margin = 28f

        val titlePaint = TextPaint().apply {
            color = Color.rgb(30, 27, 75) // Indigo
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subPaint = TextPaint().apply {
            color = Color.rgb(71, 85, 105)
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val headerCellPaint = TextPaint().apply {
            color = Color.WHITE
            textSize = 7.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyPaint = TextPaint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 7f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val boldBodyPaint = TextPaint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 7f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val borderPaint = Paint().apply {
            color = Color.rgb(203, 213, 225)
            style = Paint.Style.STROKE
            strokeWidth = 0.8f
            isAntiAlias = true
        }

        val headerBgPaint = Paint().apply {
            color = Color.rgb(30, 27, 75) // Indigo
            style = Paint.Style.FILL
        }

        val bannerBgPaint = Paint().apply {
            color = Color.rgb(241, 245, 249)
            style = Paint.Style.FILL
        }

        val altRowPaint = Paint().apply {
            color = Color.rgb(248, 250, 252)
            style = Paint.Style.FILL
        }

        val inquiryBgPaint = Paint().apply {
            color = Color.rgb(254, 243, 199)
            style = Paint.Style.FILL
        }

        // Table column widths (Total = 786 pt, fits in 842 - 2*28 = 786)
        val colWidths = floatArrayOf(
            30f,  // Week
            28f,  // Lesson
            70f,  // Strand
            75f,  // Sub-strand
            135f, // Learning Outcomes
            88f,  // Inquiry
            120f, // Experiences
            85f,  // Resources
            75f,  // Assessment
            80f   // Reflection
        )
        val colHeaders = arrayOf(
            "Wk", "Lsn", "Strand", "Sub-strand", "Specific Learning Outcomes",
            "Key Inquiry Question", "Learning Experiences", "Learning Resources", "Assessment", "Reflection"
        )

        val rowsPerPage = 5
        val totalRows = scheme.rows.size
        val totalPages = Math.max(1, (totalRows + rowsPerPage - 1) / rowsPerPage)

        for (pageIndex in 0 until totalPages) {
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageIndex + 1).create()
            val page = pdfDoc.startPage(pageInfo)
            val canvas = page.canvas

            var curY = margin

            // Draw Top Header Info on Every Page
            canvas.drawText("${scheme.schoolName.uppercase()} - SCHEME OF WORK", margin, curY + 12f, titlePaint)
            curY += 16f
            canvas.drawText(
                "GRADE: ${scheme.grade}  |  LEARNING AREA: ${scheme.learningArea}  |  TERM: ${scheme.term}  |  YEAR: ${scheme.year}  |  TEACHER: ${scheme.teacherName.ifBlank { "N/A" }}",
                margin,
                curY + 9f,
                subPaint
            )
            curY += 14f

            // Merged activities banner on page 1
            if (pageIndex == 0) {
                val bannerRect = android.graphics.RectF(margin, curY, pageWidth - margin, curY + 22f)
                canvas.drawRect(bannerRect, bannerBgPaint)
                canvas.drawRect(bannerRect, borderPaint)
                val actPaint = TextPaint().apply {
                    color = Color.rgb(15, 23, 42)
                    textSize = 6.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                    isAntiAlias = true
                }
                val bannerText = "TERM PRACTICAL ACTIVITIES: ${scheme.activitiesOverview.ifBlank { "Group experiments, learner-led inquiry, hands-on tasks, realia observation, portfolio building." }}"
                drawWrappedText(canvas, bannerText, actPaint, margin + 4f, curY + 3f, (pageWidth - margin * 2) - 8f)
                curY += 26f
            }

            // Table Header Row
            val headerHeight = 18f
            canvas.drawRect(margin, curY, pageWidth - margin, curY + headerHeight, headerBgPaint)

            var curX = margin
            for (i in colHeaders.indices) {
                val cw = colWidths[i]
                drawCenteredText(canvas, colHeaders[i], headerCellPaint, curX, curY + 4f, cw)
                canvas.drawLine(curX, curY, curX, curY + headerHeight, borderPaint)
                curX += cw
            }
            canvas.drawLine(pageWidth - margin, curY, pageWidth - margin, curY + headerHeight, borderPaint)
            curY += headerHeight

            // Table Body Rows for this page
            val startIdx = pageIndex * rowsPerPage
            val endIdx = Math.min(startIdx + rowsPerPage, totalRows)

            val rowHeight = 72f
            for (r in startIdx until endIdx) {
                val row = scheme.rows[r]
                val rowTop = curY
                val rowBottom = curY + rowHeight

                // Alternating row background
                if (r % 2 == 1) {
                    canvas.drawRect(margin, rowTop, pageWidth - margin, rowBottom, altRowPaint)
                }

                curX = margin
                // Col 0: Week
                drawCenteredText(canvas, "${row.week}", boldBodyPaint, curX, rowTop + 8f, colWidths[0])
                curX += colWidths[0]

                // Col 1: Lesson
                drawCenteredText(canvas, "${row.lesson}", bodyPaint, curX, rowTop + 8f, colWidths[1])
                curX += colWidths[1]

                // Col 2: Strand
                drawWrappedText(canvas, row.strand, boldBodyPaint, curX + 3f, rowTop + 4f, colWidths[2] - 6f)
                curX += colWidths[2]

                // Col 3: Sub-strand
                drawWrappedText(canvas, row.subStrand, bodyPaint, curX + 3f, rowTop + 4f, colWidths[3] - 6f)
                curX += colWidths[3]

                // Col 4: Outcomes (Knowledge, Skill, Attitude)
                val outcomesText = "• K: ${row.knowledgeOutcome}\n• S: ${row.skillOutcome}\n• A: ${row.attitudeOutcome}"
                drawWrappedText(canvas, outcomesText, bodyPaint, curX + 3f, rowTop + 4f, colWidths[4] - 6f)
                curX += colWidths[4]

                // Col 5: Inquiry
                val inqRect = android.graphics.RectF(curX + 2f, rowTop + 3f, curX + colWidths[5] - 2f, rowBottom - 3f)
                canvas.drawRect(inqRect, inquiryBgPaint)
                val inqPaint = TextPaint().apply {
                    color = Color.rgb(30, 27, 75)
                    textSize = 6.5f
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                    isAntiAlias = true
                }
                drawWrappedText(canvas, row.keyInquiryQuestion, inqPaint, curX + 4f, rowTop + 5f, colWidths[5] - 8f)
                curX += colWidths[5]

                // Col 6: Experiences
                drawWrappedText(canvas, row.learningExperiences, bodyPaint, curX + 3f, rowTop + 4f, colWidths[6] - 6f)
                curX += colWidths[6]

                // Col 7: Resources
                drawWrappedText(canvas, row.learningResources, bodyPaint, curX + 3f, rowTop + 4f, colWidths[7] - 6f)
                curX += colWidths[7]

                // Col 8: Assessment
                drawWrappedText(canvas, row.assessment, bodyPaint, curX + 3f, rowTop + 4f, colWidths[8] - 6f)
                curX += colWidths[8]

                // Col 9: Reflection
                drawWrappedText(canvas, row.reflection.ifBlank { "Achieved" }, bodyPaint, curX + 3f, rowTop + 4f, colWidths[9] - 6f)

                // Row borders
                canvas.drawRect(margin, rowTop, pageWidth - margin, rowBottom, borderPaint)

                // Vertical column dividers
                var divX = margin
                for (cw in colWidths) {
                    canvas.drawLine(divX, rowTop, divX, rowBottom, borderPaint)
                    divX += cw
                }
                canvas.drawLine(pageWidth - margin, rowTop, pageWidth - margin, rowBottom, borderPaint)

                curY += rowHeight
            }

            // Footer / Signatures on Last Page
            if (pageIndex == totalPages - 1) {
                curY += 10f
                val sigText = "Prepared by: ${scheme.teacherName.ifBlank { "Teacher" }}  __________   |   Checked by HOD: ______________   |   Approved by Principal/Deputy: ______________   |   Date: __________"
                canvas.drawText(sigText, margin, curY + 8f, subPaint)
            }

            // Page Number
            val footerText = "Schemly CBC Planner • KICD Curriculum Standard • Page ${pageIndex + 1} of $totalPages"
            canvas.drawText(footerText, margin, pageHeight - 12f, subPaint)

            pdfDoc.finishPage(page)
        }

        // Save to cache directory
        val docsFolder = File(context.cacheDir, "documents")
        if (!docsFolder.exists()) docsFolder.mkdirs()

        val sanitizedName = "${scheme.learningArea}_${scheme.grade}_Scheme_of_Work".replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val file = File(docsFolder, "$sanitizedName.pdf")
        FileOutputStream(file).use { out ->
            pdfDoc.writeTo(out)
        }
        pdfDoc.close()
        return file
    }

    /**
     * Generates a standard multi-page PDF document for a Lesson Plan.
     */
    fun generateLessonPlanPdf(context: Context, plan: LessonPlan): File {
        val pdfDoc = PdfDocument()
        val pageWidth = A4_WIDTH_PORTRAIT
        val pageHeight = A4_HEIGHT_PORTRAIT
        val margin = 32f

        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDoc.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = TextPaint().apply {
            color = Color.rgb(30, 27, 75)
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val subPaint = TextPaint().apply {
            color = Color.rgb(71, 85, 105)
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val sectionPaint = TextPaint().apply {
            color = Color.rgb(30, 27, 75)
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val bodyPaint = TextPaint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val boldBodyPaint = TextPaint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        val borderPaint = Paint().apply {
            color = Color.rgb(203, 213, 225)
            style = Paint.Style.STROKE
            strokeWidth = 0.8f
            isAntiAlias = true
        }

        val cardBgPaint = Paint().apply {
            color = Color.rgb(248, 250, 252)
            style = Paint.Style.FILL
        }

        val inquiryBgPaint = Paint().apply {
            color = Color.rgb(254, 243, 199)
            style = Paint.Style.FILL
        }

        val headerBgPaint = Paint().apply {
            color = Color.rgb(30, 27, 75)
            style = Paint.Style.FILL
        }

        val headerCellPaint = TextPaint().apply {
            color = Color.WHITE
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }

        var curY = margin

        // Header Title
        canvas.drawText("${plan.schoolName.uppercase()} - CBC LESSON PLAN", margin, curY + 12f, titlePaint)
        val createdStamp = "Created: ${plan.formattedCreatedDateTime()}"
        val createdStampWidth = subPaint.measureText(createdStamp)
        canvas.drawText(createdStamp, pageWidth - margin - createdStampWidth, curY + 12f, subPaint)
        curY += 18f

        // Administrative Info Box (4 Rows for Complete Metadata)
        val adminRectHeight = 62f
        val adminRect = android.graphics.RectF(margin, curY, pageWidth - margin, curY + adminRectHeight)
        canvas.drawRect(adminRect, cardBgPaint)
        canvas.drawRect(adminRect, borderPaint)

        val col1X = margin + 8f
        val col2X = margin + 185f
        val col3X = margin + 370f

        val teacherDisplay = buildString {
            append("Teacher: ")
            append(if (plan.teacherName.isNotBlank()) plan.teacherName else "N/A")
            if (plan.teacherTscNo.isNotBlank()) append(" (TSC: ${plan.teacherTscNo})")
        }
        val classDisplay = "Class: ${if (plan.className.isNotBlank()) plan.className else plan.grade}"

        // Row 1: Teacher, Class/Stream, Creation Date & Time
        canvas.drawText(teacherDisplay, col1X, curY + 13f, boldBodyPaint)
        canvas.drawText(classDisplay, col2X, curY + 13f, boldBodyPaint)
        canvas.drawText("Plan Created: ${plan.formattedCreatedDateTime()}", col3X, curY + 13f, subPaint)

        // Row 2: Grade, Learning Area, Date of Lesson
        canvas.drawText("Grade: ${plan.grade}", col1X, curY + 27f, bodyPaint)
        canvas.drawText("Learning Area: ${plan.learningArea}", col2X, curY + 27f, boldBodyPaint)
        canvas.drawText("Lesson Date: ${if (plan.date.isNotBlank()) plan.date else "___________"}", col3X, curY + 27f, boldBodyPaint)

        // Row 3: Week/Lesson, Lesson Time, Roll
        canvas.drawText("Week: ${plan.week}  |  Lesson: ${plan.lessonNumber}", col1X, curY + 41f, boldBodyPaint)
        canvas.drawText("Time: ${plan.time}", col2X, curY + 41f, bodyPaint)
        canvas.drawText("Roll: ${plan.roll}", col3X, curY + 41f, bodyPaint)

        // Row 4: Strand, Sub-strand
        canvas.drawText("Strand: ${plan.strand}", col1X, curY + 55f, bodyPaint)
        canvas.drawText("Sub-strand: ${plan.subStrand}", col2X, curY + 55f, boldBodyPaint)
        if (plan.teacherContact.isNotBlank()) {
            canvas.drawText("Contact: ${plan.teacherContact}", col3X, curY + 55f, subPaint)
        }

        curY += adminRectHeight + 8f

        // 1. Specific Learning Outcomes
        canvas.drawText("1. SPECIFIC LEARNING OUTCOMES (CBC K-S-A FORMAT)", margin, curY + 10f, sectionPaint)
        curY += 14f

        val outcomesRect = android.graphics.RectF(margin, curY, pageWidth - margin, curY + 54f)
        canvas.drawRect(outcomesRect, cardBgPaint)
        canvas.drawRect(outcomesRect, borderPaint)

        val outPaint = TextPaint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        drawWrappedText(canvas, "• Knowledge: ${plan.knowledgeOutcome}", outPaint, margin + 8f, curY + 6f, pageWidth - margin * 2 - 16f)
        drawWrappedText(canvas, "• Skill: ${plan.skillOutcome}", outPaint, margin + 8f, curY + 22f, pageWidth - margin * 2 - 16f)
        drawWrappedText(canvas, "• Attitude: ${plan.attitudeOutcome}", outPaint, margin + 8f, curY + 38f, pageWidth - margin * 2 - 16f)
        curY += 60f

        // 2. Key Inquiry Question
        val inqRect = android.graphics.RectF(margin, curY, pageWidth - margin, curY + 26f)
        canvas.drawRect(inqRect, inquiryBgPaint)
        canvas.drawRect(inqRect, borderPaint)

        val inqTitlePaint = TextPaint().apply {
            color = Color.rgb(120, 53, 15)
            textSize = 7.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val inqTextPaint = TextPaint().apply {
            color = Color.rgb(30, 27, 75)
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
            isAntiAlias = true
        }
        canvas.drawText("KEY INQUIRY QUESTION (HOW...?):", margin + 8f, curY + 10f, inqTitlePaint)
        drawWrappedText(canvas, plan.keyInquiryQuestion, inqTextPaint, margin + 8f, curY + 13f, pageWidth - margin * 2 - 16f)
        curY += 32f

        // 3. Competencies, Values & Resources
        val pciRect = android.graphics.RectF(margin, curY, pageWidth - margin, curY + 44f)
        canvas.drawRect(pciRect, cardBgPaint)
        canvas.drawRect(pciRect, borderPaint)

        drawWrappedText(canvas, "Core Competencies: ${plan.coreCompetencies}", outPaint, margin + 8f, curY + 6f, pageWidth - margin * 2 - 16f)
        drawWrappedText(canvas, "Values & PCIs: ${plan.values} | ${plan.pertAndContIssues}", outPaint, margin + 8f, curY + 19f, pageWidth - margin * 2 - 16f)
        drawWrappedText(canvas, "Learning Resources: ${plan.learningResources}", outPaint, margin + 8f, curY + 32f, pageWidth - margin * 2 - 16f)
        curY += 50f

        // 4. Lesson Development Matrix (4-Step)
        canvas.drawText("4. LESSON DEVELOPMENT MATRIX (40 MINUTES)", margin, curY + 10f, sectionPaint)
        curY += 14f

        val stepColWidths = floatArrayOf(60f, 75f, 195f, 120f, 81f)
        val stepHeaders = arrayOf("Time", "Stage", "Teacher Activities", "Learner Activities", "Assessment")

        val tableHeaderHeight = 16f
        canvas.drawRect(margin, curY, pageWidth - margin, curY + tableHeaderHeight, headerBgPaint)

        var curX = margin
        for (i in stepHeaders.indices) {
            val cw = stepColWidths[i]
            drawCenteredText(canvas, stepHeaders[i], headerCellPaint, curX, curY + 3f, cw)
            canvas.drawLine(curX, curY, curX, curY + tableHeaderHeight, borderPaint)
            curX += cw
        }
        canvas.drawLine(pageWidth - margin, curY, pageWidth - margin, curY + tableHeaderHeight, borderPaint)
        curY += tableHeaderHeight

        val steps = arrayOf(
            Triple("5 Mins", "Step 1: Introduction", plan.step1Intro),
            Triple("20 Mins", "Step 2: Development", plan.step2Development),
            Triple("10 Mins", "Step 3: Application", plan.step3Application),
            Triple("5 Mins", "Step 4: Conclusion", plan.step4Conclusion)
        )

        val stepAssessments = arrayOf("Oral probing", "Observation & group tasks", "Written exercises / tasks", "Oral wrap-up summary")
        val stepHeight = 52f

        for (idx in steps.indices) {
            val (time, stage, desc) = steps[idx]
            val rowTop = curY
            val rowBottom = curY + stepHeight

            if (idx % 2 == 1) {
                canvas.drawRect(margin, rowTop, pageWidth - margin, rowBottom, cardBgPaint)
            }

            curX = margin
            drawCenteredText(canvas, time, boldBodyPaint, curX, rowTop + 6f, stepColWidths[0])
            curX += stepColWidths[0]

            drawWrappedText(canvas, stage, boldBodyPaint, curX + 3f, rowTop + 4f, stepColWidths[1] - 6f)
            curX += stepColWidths[1]

            drawWrappedText(canvas, desc, bodyPaint, curX + 3f, rowTop + 4f, stepColWidths[2] - 6f)
            curX += stepColWidths[2]

            drawWrappedText(canvas, "Learners participate actively in $stage tasks, collaborate with peers and apply concepts.", bodyPaint, curX + 3f, rowTop + 4f, stepColWidths[3] - 6f)
            curX += stepColWidths[3]

            drawWrappedText(canvas, stepAssessments[idx], bodyPaint, curX + 3f, rowTop + 4f, stepColWidths[4] - 6f)

            // Borders
            canvas.drawRect(margin, rowTop, pageWidth - margin, rowBottom, borderPaint)
            var divX = margin
            for (cw in stepColWidths) {
                canvas.drawLine(divX, rowTop, divX, rowBottom, borderPaint)
                divX += cw
            }
            canvas.drawLine(pageWidth - margin, rowTop, pageWidth - margin, rowBottom, borderPaint)

            curY += stepHeight
        }

        curY += 8f

        // 5. Extended Activities & Reflection
        val refRect = android.graphics.RectF(margin, curY, pageWidth - margin, curY + 36f)
        canvas.drawRect(refRect, cardBgPaint)
        canvas.drawRect(refRect, borderPaint)

        drawWrappedText(canvas, "Extended Activity: ${plan.extendedActivity.ifBlank { "Investigate applications of $plan.subStrand in community." }}", outPaint, margin + 8f, curY + 4f, pageWidth - margin * 2 - 16f)
        drawWrappedText(canvas, "Teacher's Self-Reflection: ${plan.reflection.ifBlank { "Outcomes achieved. Learners demonstrated mastery of skills." }}", outPaint, margin + 8f, curY + 18f, pageWidth - margin * 2 - 16f)
        curY += 42f

        // Signatures
        val sigText = "Teacher's Signature: __________________   Date: __________   |   HOD / Supervisor: __________________   Date: __________"
        canvas.drawText(sigText, margin, curY + 8f, subPaint)

        // Footer
        canvas.drawText("Schemly CBC Lesson Plan • Ministry of Education / KICD Format", margin, pageHeight - 12f, subPaint)

        pdfDoc.finishPage(page)

        val docsFolder = File(context.cacheDir, "documents")
        if (!docsFolder.exists()) docsFolder.mkdirs()

        val sanitizedName = "${plan.learningArea}_LessonPlan_W${plan.week}_L${plan.lessonNumber}".replace(Regex("[^a-zA-Z0-9._-]"), "_")
        val file = File(docsFolder, "$sanitizedName.pdf")
        FileOutputStream(file).use { out ->
            pdfDoc.writeTo(out)
        }
        pdfDoc.close()
        return file
    }

    /**
     * Prints or Saves as PDF using Android's native system print spooler.
     */
    fun printOrSaveAsPdf(context: Context, webView: WebView, docTitle: String) {
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            val jobName = "Schemly_$docTitle"
            val printAdapter = webView.createPrintDocumentAdapter(jobName)
            val printAttributes = PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .build()

            printManager?.print(jobName, printAdapter, printAttributes)
        } catch (e: Exception) {
            Toast.makeText(context, "Print service error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun drawWrappedText(canvas: Canvas, text: String, paint: TextPaint, x: Float, y: Float, width: Float) {
        if (width <= 0 || text.isBlank()) return
        val staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width.toInt())
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setLineSpacing(0f, 1.0f)
            .setIncludePad(false)
            .build()
        canvas.save()
        canvas.translate(x, y)
        staticLayout.draw(canvas)
        canvas.restore()
    }

    private fun drawCenteredText(canvas: Canvas, text: String, paint: TextPaint, x: Float, y: Float, width: Float) {
        val textWidth = paint.measureText(text)
        val textX = x + (width - textWidth) / 2f
        canvas.drawText(text, textX, y + paint.textSize, paint)
    }
}
