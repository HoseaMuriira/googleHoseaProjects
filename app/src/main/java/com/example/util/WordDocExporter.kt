package com.example.util

import com.example.data.model.LessonPlan
import com.example.data.model.SchemeOfWork

object WordDocExporter {

    /**
     * Generates a fully formatted Microsoft Word compatible (.doc / HTML) document for Schemes of Work
     * strictly following the 4-row header + 10-column table structure:
     * - Row 1: SCHOOL | GRADE | LEARNING AREA | YEAR | TERM | TEACHER
     * - Row 2: [Editable / Filled Values]
     * - Row 3: MERGED ROW: ACTIVITIES & TERM OVERVIEW
     * - Row 4: 10 COLUMNS:
     *   1. Week | 2. Lesson | 3. Strand | 4. Sub-strand | 5. Specific Learning Outcomes (Knowledge, Skill, Attitude) |
     *   6. Key Inquiry Questions (How...?) | 7. Learning Experiences | 8. Learning Resources | 9. Assessment | 10. Reflection
     */
    fun generateSchemeWordHtml(scheme: SchemeOfWork): String {
        val sb = StringBuilder()
        sb.append("""
<!DOCTYPE html>
<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:w="urn:schemas-microsoft-com:office:word" xmlns="http://www.w3.org/TR/REC-html40">
<head>
<meta charset="utf-8">
<title>${scheme.learningArea} - ${scheme.grade} Scheme of Work</title>
<!--[if gte mso 9]>
<xml>
 <w:WordDocument>
  <w:View>Print</w:View>
  <w:Zoom>100</w:Zoom>
  <w:DoNotOptimizeForBrowser/>
 </w:WordDocument>
</xml>
<![endif]-->
<style>
  @page {
    size: landscape;
    margin: 1.5cm;
  }
  body {
    font-family: "Calibri", "Segoe UI", Arial, sans-serif;
    font-size: 10.5pt;
    color: #1a1a1a;
    line-height: 1.35;
    background-color: #ffffff;
    padding: 10px;
  }
  .doc-title {
    text-align: center;
    font-size: 15pt;
    font-weight: bold;
    color: #1e3a8a;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    margin-bottom: 2px;
  }
  .doc-subtitle {
    text-align: center;
    font-size: 11.5pt;
    font-weight: 600;
    color: #475569;
    margin-bottom: 12px;
  }
  table.scheme-table {
    width: 100%;
    border-collapse: collapse;
    margin-top: 8px;
    margin-bottom: 20px;
    page-break-inside: auto;
  }
  tr {
    page-break-inside: avoid;
    page-break-after: auto;
  }
  th, td {
    border: 1px solid #334155;
    padding: 5.5px 7px;
    font-size: 9.5pt;
    vertical-align: top;
    text-align: left;
  }
  /* First Row: School, Grade, Learning Area, Year Header */
  .meta-header-row th {
    background-color: #1e3a8a;
    color: #ffffff;
    font-weight: bold;
    text-align: center;
    font-size: 10pt;
    text-transform: uppercase;
  }
  /* Second Row: Filled Values */
  .meta-value-row td {
    background-color: #f8fafc;
    font-weight: 600;
    text-align: center;
    font-size: 10pt;
    color: #0f172a;
    padding: 8px;
  }
  /* Third Row: Merged Activities Row */
  .activities-row th {
    background-color: #e0e7ff;
    color: #1e1b4b;
    font-weight: bold;
    text-align: left;
    font-size: 10pt;
    padding: 8px 10px;
    border: 1.5px solid #334155;
  }
  /* Fourth Row: 10 Column Headers */
  .col-headers-row th {
    background-color: #0284c7;
    color: #ffffff;
    font-weight: bold;
    text-align: center;
    font-size: 9pt;
    text-transform: uppercase;
  }
  .week-cell, .lesson-cell {
    text-align: center;
    font-weight: bold;
  }
  .outcome-box {
    margin-bottom: 3px;
  }
  .outcome-k { color: #1e3a8a; font-weight: 600; }
  .outcome-s { color: #047857; font-weight: 600; }
  .outcome-a { color: #b45309; font-weight: 600; }
  .inquiry-q {
    color: #4338ca;
    font-style: italic;
    font-weight: 500;
  }
  .zebra-even {
    background-color: #ffffff;
  }
  .zebra-odd {
    background-color: #f8fafc;
  }
  .footer-stamp {
    margin-top: 30px;
    display: flex;
    justify-content: space-between;
    font-size: 10pt;
    color: #334155;
  }
  .stamp-box {
    border-top: 1px dashed #64748b;
    padding-top: 6px;
    width: 28%;
    display: inline-block;
  }
</style>
</head>
<body>

<div class="doc-title">${scheme.schoolName.ifBlank { "JUNIOR SECONDARY SCHOOL" }}</div>
<div class="doc-subtitle">COMPETENCY BASED CURRICULUM (CBC) - SCHEMES OF WORK</div>

<table class="scheme-table">
  <thead>
    <!-- FIRST ROW: SCHOOL, GRADE, LEARNING AREA, YEAR, TERM -->
    <tr class="meta-header-row">
      <th colspan="2">SCHOOL</th>
      <th colspan="2">GRADE</th>
      <th colspan="2">LEARNING AREA</th>
      <th colspan="2">YEAR</th>
      <th colspan="2">TERM / TEACHER</th>
    </tr>

    <!-- SECOND ROW: BLANK / FILLED VALUES -->
    <tr class="meta-value-row">
      <td colspan="2">${scheme.schoolName.ifBlank { "JUNIOR SECONDARY SCHOOL" }}</td>
      <td colspan="2">${scheme.grade}</td>
      <td colspan="2">${scheme.learningArea}</td>
      <td colspan="2">${scheme.year}</td>
      <td colspan="2">${scheme.term} ${if (scheme.teacherName.isNotBlank()) " | Tr. " + scheme.teacherName else ""}</td>
    </tr>

    <!-- THIRD ROW: MERGED WITH ONLY ACTIVITIES -->
    <tr class="activities-row">
      <th colspan="10">
        <strong>ACTIVITIES & TERM FOCUS:</strong> ${scheme.activitiesOverview.ifBlank { "Collaborative discussions, hands-on practical activities, digital explorations, group projects, guided problem solving, real-world investigations, and formative peer assessments." }}
      </th>
    </tr>

    <!-- FOURTH ROW: 10 COLUMNS -->
    <tr class="col-headers-row">
      <th style="width: 4%;">WEEK</th>
      <th style="width: 4%;">LESSON</th>
      <th style="width: 10%;">STRAND</th>
      <th style="width: 12%;">SUB-STRAND</th>
      <th style="width: 22%;">SPECIFIC LEARNING OUTCOMES<br><small>(Knowledge, Skill, Attitude)</small></th>
      <th style="width: 12%;">KEY ENQUIRY QUESTIONS<br><small>(Starts with How?)</small></th>
      <th style="width: 14%;">LEARNING EXPERIENCES</th>
      <th style="width: 10%;">LEARNING RESOURCES</th>
      <th style="width: 6%;">ASSESSMENT</th>
      <th style="width: 6%;">REFLECTION</th>
    </tr>
  </thead>
  <tbody>
        """.trimIndent())

        scheme.rows.forEachIndexed { index, row ->
            val zebraClass = if (row.week % 2 == 0) "zebra-even" else "zebra-odd"
            sb.append("""
    <tr class="$zebraClass">
      <td class="week-cell">${row.week}</td>
      <td class="lesson-cell">${row.lesson}</td>
      <td><strong>${escapeHtml(row.strand)}</strong></td>
      <td>${escapeHtml(row.subStrand)}</td>
      <td>
        <div><strong>By the end of the lesson, the learner should be able to:</strong></div>
        <div class="outcome-box"><span class="outcome-k">• Knowledge:</span> ${escapeHtml(row.knowledgeOutcome)}</div>
        <div class="outcome-box"><span class="outcome-s">• Skill:</span> ${escapeHtml(row.skillOutcome)}</div>
        <div class="outcome-box"><span class="outcome-a">• Attitude:</span> ${escapeHtml(row.attitudeOutcome)}</div>
      </td>
      <td class="inquiry-q">${escapeHtml(row.keyInquiryQuestion)}</td>
      <td>${escapeHtml(row.learningExperiences)}</td>
      <td>${escapeHtml(row.learningResources)}</td>
      <td>${escapeHtml(row.assessment)}</td>
      <td>${escapeHtml(row.reflection)}</td>
    </tr>
            """.trimIndent())
        }

        sb.append("""
  </tbody>
</table>

<table style="width: 100%; border: none; margin-top: 30px;">
  <tr style="border: none;">
    <td style="border: none; width: 33%; text-align: left;">
      <div style="border-top: 1px solid #64748b; padding-top: 5px; width: 85%;">
        <strong>Subject Teacher's Signature:</strong><br>Date: ____________________
      </div>
    </td>
    <td style="border: none; width: 33%; text-align: center;">
      <div style="border-top: 1px solid #64748b; padding-top: 5px; width: 85%; margin: auto;">
        <strong>HOD / Senior Teacher's Stamp:</strong><br>Date: ____________________
      </div>
    </td>
    <td style="border: none; width: 33%; text-align: right;">
      <div style="border-top: 1px solid #64748b; padding-top: 5px; width: 85%; margin-left: auto;">
        <strong>Principal / Headteacher's Stamp:</strong><br>Date: ____________________
      </div>
    </td>
  </tr>
</table>

</body>
</html>
        """.trimIndent())

        return sb.toString()
    }

    /**
     * Generates a fully formatted Word-compatible (.doc / HTML) Lesson Plan
     */
    fun generateLessonPlanWordHtml(plan: LessonPlan): String {
        val teacherInfoDisplay = buildString {
            if (plan.teacherName.isNotBlank()) append(escapeHtml(plan.teacherName)) else append("____________________")
            if (plan.teacherTscNo.isNotBlank()) append(" (TSC: ${escapeHtml(plan.teacherTscNo)})")
            if (plan.teacherContact.isNotBlank()) append(" | ${escapeHtml(plan.teacherContact)}")
        }
        val classDisplay = if (plan.className.isNotBlank()) escapeHtml(plan.className) else escapeHtml(plan.grade)

        return """
<!DOCTYPE html>
<html xmlns:o="urn:schemas-microsoft-com:office:office" xmlns:w="urn:schemas-microsoft-com:office:word" xmlns="http://www.w3.org/TR/REC-html40">
<head>
<meta charset="utf-8">
<title>${escapeHtml(plan.learningArea)} - Lesson Plan (${escapeHtml(plan.grade)} W${plan.week} L${plan.lessonNumber})</title>
<style>
  @page { size: portrait; margin: 1.6cm; }
  body { font-family: "Calibri", Arial, sans-serif; font-size: 11pt; color: #1e293b; line-height: 1.4; }
  .header-title { text-align: center; font-size: 15pt; font-weight: bold; color: #1e3a8a; margin-bottom: 2px; }
  .header-sub { text-align: center; font-size: 12pt; font-weight: 600; color: #475569; margin-bottom: 6px; }
  .header-created { text-align: right; font-size: 9.5pt; color: #64748b; font-style: italic; margin-bottom: 12px; }
  table.meta-table, table.steps-table { width: 100%; border-collapse: collapse; margin-bottom: 16px; }
  table.meta-table th, table.meta-table td, table.steps-table th, table.steps-table td {
    border: 1px solid #475569; padding: 6px 9px; font-size: 10.5pt; text-align: left; vertical-align: middle;
  }
  table.meta-table th, table.steps-table th { background-color: #1e3a8a; color: #ffffff; font-weight: bold; }
  .section-badge { background-color: #e2e8f0; font-weight: bold; color: #0f172a; padding: 5px 8px; margin-top: 10px; margin-bottom: 6px; }
  .outcome-k { color: #1e3a8a; font-weight: bold; }
  .outcome-s { color: #047857; font-weight: bold; }
  .outcome-a { color: #b45309; font-weight: bold; }
</style>
</head>
<body>

<div class="header-title">${escapeHtml(plan.schoolName.ifBlank { "JUNIOR SECONDARY SCHOOL" })}</div>
<div class="header-sub">KENYA INSTITUTE OF CURRICULUM DEVELOPMENT (KICD) CBC LESSON PLAN</div>
<div class="header-created">Lesson Plan Created: ${plan.formattedCreatedDateTime()}</div>

<table class="meta-table">
  <tr>
    <th style="width: 18%;">TEACHER</th>
    <td colspan="3" style="width: 82%;"><strong>$teacherInfoDisplay</strong></td>
  </tr>
  <tr>
    <th style="width: 18%;">SCHOOL</th>
    <td style="width: 32%;">${escapeHtml(plan.schoolName)}</td>
    <th style="width: 18%;">CLASS / STREAM</th>
    <td style="width: 32%;"><strong>$classDisplay</strong></td>
  </tr>
  <tr>
    <th>DATE OF LESSON</th>
    <td>${if (plan.date.isNotBlank()) escapeHtml(plan.date) else "___________"}</td>
    <th>TIME & DURATION</th>
    <td>${escapeHtml(plan.time)}</td>
  </tr>
  <tr>
    <th>LEARNING AREA</th>
    <td><strong>${escapeHtml(plan.learningArea)}</strong></td>
    <th>ROLL / ATTENDANCE</th>
    <td>${escapeHtml(plan.roll)}</td>
  </tr>
  <tr>
    <th>GRADE & TIMING</th>
    <td>${escapeHtml(plan.grade)} (Week ${plan.week}, Lesson ${plan.lessonNumber})</td>
    <th>CREATED AT</th>
    <td>${plan.formattedCreatedDateTime()}</td>
  </tr>
  <tr>
    <th>STRAND</th>
    <td>${escapeHtml(plan.strand)}</td>
    <th>SUB-STRAND</th>
    <td>${escapeHtml(plan.subStrand)}</td>
  </tr>
</table>

<div class="section-badge">1. SPECIFIC LEARNING OUTCOMES</div>
<div style="padding-left: 12px; margin-bottom: 12px;">
  <p>By the end of the lesson, the learner should be able to:</p>
  <p><span class="outcome-k">• Knowledge:</span> ${escapeHtml(plan.knowledgeOutcome)}</p>
  <p><span class="outcome-s">• Skill:</span> ${escapeHtml(plan.skillOutcome)}</p>
  <p><span class="outcome-a">• Attitude:</span> ${escapeHtml(plan.attitudeOutcome)}</p>
</div>

<div class="section-badge">2. KEY INQUIRY QUESTION</div>
<div style="padding-left: 12px; margin-bottom: 12px; font-style: italic; color: #4338ca; font-weight: 600;">
  ${escapeHtml(plan.keyInquiryQuestion)}
</div>

<div class="section-badge">3. CORE COMPETENCIES, VALUES & PCIs</div>
<div style="padding-left: 12px; margin-bottom: 12px;">
  <p><strong>Core Competencies:</strong> ${escapeHtml(plan.coreCompetencies)}</p>
  <p><strong>Values:</strong> ${escapeHtml(plan.values)}</p>
  <p><strong>Pertinent & Contemporary Issues (PCIs):</strong> ${escapeHtml(plan.pertAndContIssues)}</p>
</div>

<div class="section-badge">4. LEARNING RESOURCES</div>
<div style="padding-left: 12px; margin-bottom: 12px;">
  ${escapeHtml(plan.learningResources)}
</div>

<div class="section-badge">5. LESSON DEVELOPMENT MATRIX</div>
<table class="steps-table">
  <thead>
    <tr>
      <th style="width: 22%;">LESSON STEP</th>
      <th style="width: 12%;">TIME</th>
      <th style="width: 66%;">LEARNER & TEACHER ACTIVITIES</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td><strong>Step 1: Introduction</strong></td>
      <td>5 Mins</td>
      <td>${escapeHtml(plan.step1Intro)}</td>
    </tr>
    <tr>
      <td><strong>Step 2: Lesson Development</strong></td>
      <td>20 Mins</td>
      <td>${escapeHtml(plan.step2Development)}</td>
    </tr>
    <tr>
      <td><strong>Step 3: Application & Assessment</strong></td>
      <td>10 Mins</td>
      <td>${escapeHtml(plan.step3Application)}</td>
    </tr>
    <tr>
      <td><strong>Step 4: Conclusion & Summary</strong></td>
      <td>5 Mins</td>
      <td>${escapeHtml(plan.step4Conclusion)}</td>
    </tr>
  </tbody>
</table>

<div class="section-badge">6. EXTENDED LEARNING ACTIVITY</div>
<div style="padding-left: 12px; margin-bottom: 12px;">
  ${escapeHtml(plan.extendedActivity)}
</div>

<div class="section-badge">7. TEACHER'S SELF-REFLECTION & REMARKS</div>
<div style="padding-left: 12px; margin-bottom: 24px; min-height: 40px; border-bottom: 1px dashed #94a3b8;">
  ${escapeHtml(plan.reflection)}
</div>

<table style="width: 100%; border: none; margin-top: 25px;">
  <tr style="border: none;">
    <td style="border: none; width: 50%; text-align: left;">
      <div style="border-top: 1px solid #64748b; padding-top: 5px; width: 90%;">
        <strong>Teacher's Signature:</strong> ____________________<br>
        <strong>Date:</strong> ____________________
      </div>
    </td>
    <td style="border: none; width: 50%; text-align: right;">
      <div style="border-top: 1px solid #64748b; padding-top: 5px; width: 90%; margin-left: auto;">
        <strong>HOD / Supervisor's Signature & Stamp:</strong><br>
        <strong>Date:</strong> ____________________
      </div>
    </td>
  </tr>
</table>

</body>
</html>
        """.trimIndent()
    }

    private fun escapeHtml(text: String): String {
        return text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
            .replace("\n", "<br>")
    }
}
