package com.example.data.repository

import com.example.data.model.SchemeLessonRow
import com.example.data.model.SchemeOfWork
import java.util.UUID

object KicdCurriculumData {

    val GRADES = listOf("Grade 7", "Grade 8", "Grade 9")
    val TERMS = listOf("Term 1", "Term 2", "Term 3")

    val SUBJECTS = listOf(
        "Mathematics",
        "Integrated Science",
        "English",
        "Kiswahili",
        "Social Studies",
        "Agriculture and Nutrition",
        "Pre-Technical Studies",
        "Creative Arts and Sports",
        "Christian Religious Education (CRE)",
        "Business Studies"
    )

    data class SubStrandData(
        val strand: String,
        val subStrand: String,
        val knowledge: String,
        val skill: String,
        val attitude: String,
        val inquiryQuestion: String, // Starts with "How..."
        val experiences: String,
        val resources: String,
        val assessment: String
    )

    /**
     * Retrieves or generates an authentic 9-week (5 lessons/week) KICD Scheme of Work
     */
    fun getSchemeFor(grade: String, subject: String, term: String = "Term 1"): SchemeOfWork {
        val subStrandPool = getCurriculumStrands(grade, subject, term)
        val rows = mutableListOf<SchemeLessonRow>()

        // 9 Weeks total, 5 lessons each week
        for (w in 1..9) {
            when (w) {
                1 -> {
                    // Week 1: Opening Week, Orientation & Entry Diagnostics
                    val s = subStrandPool.firstOrNull() ?: getDefaultSubStrand(grade, subject)
                    for (l in 1..5) {
                        val lessonTopic = when (l) {
                            1 -> "Opening & Term Course Overview: Entry Behavior Diagnostic Assessment"
                            2 -> "Orientation to $subject Grade Tools, Safety, and Learning Materials"
                            3 -> "Introduction to Strand: ${s.strand} (${s.subStrand} Concepts)"
                            4 -> "${s.subStrand}: Guided Exploration and Foundation Knowledge"
                            else -> "${s.subStrand}: Practical Application and Skill Modeling"
                        }
                        rows.add(
                            SchemeLessonRow(
                                id = UUID.randomUUID().toString(),
                                week = 1,
                                lesson = l,
                                strand = s.strand,
                                subStrand = lessonTopic,
                                knowledgeOutcome = "Identify key prior concepts and outline term objectives in ${s.strand}.",
                                skillOutcome = "Apply entry-level diagnostic techniques to solve foundational problems in ${s.subStrand}.",
                                attitudeOutcome = "Appreciate the relevance of $subject in real-life junior secondary learning.",
                                keyInquiryQuestion = "How do foundational concepts in ${s.subStrand} connect to our daily experiences?",
                                learningExperiences = "Learners participate in diagnostic question-and-answer sessions, review core safety guidelines, and explore textbook and digital learning aids in groups.",
                                learningResources = "KICD Junior School Curriculum Design, Approved Course Books, Teacher's Guide, Diagnostic assessment charts, Digital tablets/projector.",
                                assessment = "Oral questioning, Diagnostic checklist, Written entry test, Peer observation.",
                                reflection = ""
                            )
                        )
                    }
                }
                9 -> {
                    // Week 9: End of Term Assessment, Culminating Project & Remediation
                    val s = subStrandPool.lastOrNull() ?: getDefaultSubStrand(grade, subject)
                    for (l in 1..5) {
                        val lessonTopic = when (l) {
                            1 -> "Comprehensive Review: Strands 1 and 2 Key Competencies"
                            2 -> "Comprehensive Review: Strands 3 and 4 Problem Solving & Practical Tasks"
                            3 -> "End of Term Formative & Summative Evaluation (Section A & B)"
                            4 -> "Practical Project Exhibition / Portfolio Assessment & Presentation"
                            else -> "Assessment Feedback, Diagnostic Remediation, and Term Wrap-up"
                        }
                        rows.add(
                            SchemeLessonRow(
                                id = UUID.randomUUID().toString(),
                                week = 9,
                                lesson = l,
                                strand = "Revision, Assessment & Project Evaluation",
                                subStrand = lessonTopic,
                                knowledgeOutcome = "Review and consolidate core facts, rules, formulas, and concepts mastered across the term.",
                                skillOutcome = "Demonstrate mastery by completing summative test items and presenting term project work.",
                                attitudeOutcome = "Value constructive feedback, honesty, integrity, and self-efficacy in academic evaluation.",
                                keyInquiryQuestion = "How can we evaluate our learning progress and apply feedback to improve future performance?",
                                learningExperiences = "Learners collaborate on revision mind-maps, complete structured assessment tasks, present individual/group portfolio exhibits, and analyze teacher feedback.",
                                learningResources = "Term Assessment Papers, Marking Schemes, Learner Portfolios, Project Rubrics, KICD Teacher Guide.",
                                assessment = "Standardized test, Project rubric evaluation, Portfolio assessment, Self-reflection log.",
                                reflection = ""
                            )
                        )
                    }
                }
                else -> {
                    // Weeks 2 to 8: Core KICD Curricular Strands
                    val poolIndex = (w - 2) % subStrandPool.size
                    val s = subStrandPool[poolIndex]
                    for (l in 1..5) {
                        val subTopic = "${s.subStrand} (Lesson $l/5 - Focus: ${getLessonFocus(l)})"
                        rows.add(
                            SchemeLessonRow(
                                id = UUID.randomUUID().toString(),
                                week = w,
                                lesson = l,
                                strand = s.strand,
                                subStrand = subTopic,
                                knowledgeOutcome = s.knowledge,
                                skillOutcome = s.skill,
                                attitudeOutcome = s.attitude,
                                keyInquiryQuestion = s.inquiryQuestion,
                                learningExperiences = s.experiences,
                                learningResources = s.resources,
                                assessment = s.assessment,
                                reflection = ""
                            )
                        )
                    }
                }
            }
        }

        return SchemeOfWork(
            id = UUID.randomUUID().toString(),
            schoolName = "JUNIOR SECONDARY SCHOOL",
            grade = grade,
            learningArea = subject,
            year = "2026",
            term = term,
            teacherName = "",
            activitiesOverview = "Learners engage in collaborative discussions, hands-on practical activities, digital explorations, group projects, guided problem solving, real-world investigations, and formative peer assessments.",
            rows = rows,
            lastModified = System.currentTimeMillis()
        )
    }

    private fun getLessonFocus(lessonNum: Int): String {
        return when (lessonNum) {
            1 -> "Conceptual Introduction & Definition"
            2 -> "Guided Demonstration & Group Exploration"
            3 -> "Hands-on Practical / Computational Tasks"
            4 -> "Problem Solving & Critical Thinking Scenarios"
            else -> "Formative Assessment, Synthesis & Remediation"
        }
    }

    private fun getDefaultSubStrand(grade: String, subject: String): SubStrandData {
        return SubStrandData(
            strand = "Foundations of $subject",
            subStrand = "Key Concepts & Core Principles",
            knowledge = "Define key terminology, state fundamental principles, and outline real-world applications in $subject.",
            skill = "Apply problem-solving methods, analyze sample cases, and demonstrate accurate operational procedures.",
            attitude = "Appreciate the value of $subject in addressing everyday life challenges and technological development.",
            inquiryQuestion = "How do principles of $subject influence modern innovation and sustainable development?",
            experiences = "Learners observe teacher demonstrations, analyze case studies in small groups, and formulate solutions using learning aids.",
            resources = "KICD Curriculum Design, Textbooks, Digital resources, Realia, Worksheets.",
            assessment = "Oral questioning, Observation checklist, Written test, Group presentation."
        )
    }

    private fun getCurriculumStrands(grade: String, subject: String, term: String): List<SubStrandData> {
        return when (subject) {
            "Mathematics" -> getMathematicsStrands(grade)
            "Integrated Science" -> getScienceStrands(grade)
            "English" -> getEnglishStrands(grade)
            "Kiswahili" -> getKiswahiliStrands(grade)
            "Social Studies" -> getSocialStudiesStrands(grade)
            "Agriculture and Nutrition" -> getAgricultureStrands(grade)
            "Pre-Technical Studies" -> getPreTechnicalStrands(grade)
            "Creative Arts and Sports" -> getCreativeArtsStrands(grade)
            "Christian Religious Education (CRE)" -> getCreStrands(grade)
            "Business Studies" -> getBusinessStudiesStrands(grade)
            else -> listOf(getDefaultSubStrand(grade, subject))
        }
    }

    private fun getMathematicsStrands(grade: String): List<SubStrandData> {
        return listOf(
            SubStrandData(
                strand = "Numbers",
                subStrand = "Whole Numbers: Place Value, Total Value & Operations",
                knowledge = "State place values and total values of large numbers up to billions, and identify divisibility tests.",
                skill = "Perform accurate four basic operations, factorize numbers, and compute GCD and LCM in real-life contexts.",
                attitude = "Appreciate the utility of whole numbers in financial transactions and commercial trade.",
                inquiryQuestion = "How do whole numbers and place values assist in managing commercial business transactions?",
                experiences = "Learners use place value charts, number cards, and digital calculators to model large numbers and solve word problems in pairs.",
                resources = "KICD Mathematics Design, Approved Course Book, Place value charts, Abacus, Flashcards, Counters.",
                assessment = "Oral questions, Timed calculation drill, Written exercise, Peer assessment."
            ),
            SubStrandData(
                strand = "Numbers",
                subStrand = "Fractions and Decimals",
                knowledge = "Describe proper, improper, and mixed fractions, and explain conversion between fractions, decimals, and percentages.",
                skill = "Execute addition, subtraction, multiplication, and division involving fractions and recurring decimals.",
                attitude = "Show precision and diligence when computing fractional quantities in trade and measurement.",
                inquiryQuestion = "How can fractions and decimals be applied to share resources equitably in our community?",
                experiences = "Learners divide geometric shapes into equal segments, use digital fraction strips, and compute real-world proportional sharing problems.",
                resources = "Fraction strips, Cut-out shapes, Grid papers, Course text, Digital tablets.",
                assessment = "Observation of hands-on fraction manipulation, Written worksheet, Oral presentation."
            ),
            SubStrandData(
                strand = "Algebra",
                subStrand = "Algebraic Expressions and Linear Equations",
                knowledge = "Define variables, terms, coefficients, constants, and explain the rules of simplifying algebraic expressions.",
                skill = "Formulate algebraic expressions from word problems and solve single-variable linear equations accurately.",
                attitude = "Develop critical thinking and logical reasoning through algebraic problem solving.",
                inquiryQuestion = "How can algebraic models help us find unknown quantities in daily commercial scenarios?",
                experiences = "Learners model algebraic equations using balance scales and algebra tiles, translate verbal statements to algebraic symbols, and solve equations.",
                resources = "Algebra balance scale, Colored algebra tiles, KICD Mathematics Book, Graph chalkboard.",
                assessment = "Board working, Peer checking, Individual homework, Formative quiz."
            ),
            SubStrandData(
                strand = "Measurements",
                subStrand = "Length, Perimeter, Area and Volume",
                knowledge = "State units of length, area, volume, and explain formulas for perimeters and areas of plane figures (triangles, trapeziums, circles).",
                skill = "Measure dimensions using standard instruments, calculate surface area of prisms and volume of cylinders.",
                attitude = "Value accuracy and resource conservation when planning spatial layouts and construction.",
                inquiryQuestion = "How does accurate geometric measurement prevent wastage in construction and tailoring?",
                experiences = "Learners measure school grounds with tape measures, derive circle area using string and grid paper, and calculate water tank volumes.",
                resources = "Measuring tape, Metre rule, Geometric solids (cubes, cylinders, prisms), Thread, Graph paper.",
                assessment = "Practical measuring task, Group lab report, Calculation test, Rubric evaluation."
            ),
            SubStrandData(
                strand = "Geometry",
                subStrand = "Angles, Triangles and Geometrical Constructions",
                knowledge = "Identify types of angles, angle properties of parallel lines, and state conditions for congruence of triangles.",
                skill = "Construct angles (60°, 90°, 45°, 30°), bisect line segments, and construct triangles using ruler and compasses.",
                attitude = "Exhibit patience, neatness, and artistic precision in geometric drafting.",
                inquiryQuestion = "How do geometric angles and parallel lines ensure stability in architectural roof trusses?",
                experiences = "Learners use mathematical sets to construct perpendicular bisectors, measure angles with protractors, and build triangular trusses.",
                resources = "Mathematical set (compass, protractor, ruler), Drawing paper, Model bridge structures, Geoboard.",
                assessment = "Portfolio of construction drawings, Observation checklist of compass technique, Test."
            ),
            SubStrandData(
                strand = "Data Handling and Probability",
                subStrand = "Data Collection, Frequency Tables & Bar Graphs",
                knowledge = "Define primary and secondary data, frequency, mean, median, and mode for ungrouped data.",
                skill = "Collect school attendance data, tabulate frequency distribution tables, and draw representative bar graphs and pie charts.",
                attitude = "Uphold integrity and objectivity when collecting and reporting statistical data.",
                inquiryQuestion = "How can graphical data representations guide informed decision making in school management?",
                experiences = "Learners conduct a survey on favorite sports, record tally marks, draw bar graphs on squared paper, and interpret statistical trends.",
                resources = "Survey tally sheets, Graph books, Colored pencils, Spreadsheet app / digital devices.",
                assessment = "Project report, Data chart presentation, Written test on averages, Peer critique."
            ),
            SubStrandData(
                strand = "Financial Mathematics",
                subStrand = "Money: Profit, Loss, Discount & Simple Interest",
                knowledge = "Define cost price, selling price, profit percentage, loss percentage, discount, and simple interest formula (I=PRT/100).",
                skill = "Calculate profit/loss margins on business transactions, determine net prices after discount, and compute simple interest.",
                attitude = "Cultivate prudent financial habits, savings culture, and consumer vigilance.",
                inquiryQuestion = "How can knowledge of interest and profit help an entrepreneur run a sustainable business?",
                experiences = "Learners simulate a classroom mini-market, roleplay bank savings accounts, and calculate transaction receipts in small groups.",
                resources = "Sample price tags, Play currency, Bank deposit slips, Business calculation worksheets.",
                assessment = "Roleplay observation, Business ledger calculation test, Formative quiz."
            )
        )
    }

    private fun getScienceStrands(grade: String): List<SubStrandData> {
        return listOf(
            SubStrandData(
                strand = "Scientific Investigation",
                subStrand = "Laboratory Safety, Apparatus & Scientific Method",
                knowledge = "Identify common laboratory hazards, state safety rules, and describe functions of basic apparatus (Bunsen burner, measuring cylinder).",
                skill = "Light and adjust a Bunsen burner flame, measure liquid volumes accurately, and formulate testable scientific hypotheses.",
                attitude = "Embrace safety consciousness and responsibility when handling chemicals and glassware.",
                inquiryQuestion = "How do strict laboratory safety measures safeguard learners and enhance accurate discoveries?",
                experiences = "Learners tour the school science lab, identify safety signs, sketch laboratory apparatus, and practice lighting luminous/non-luminous flames.",
                resources = "Bunsen burner, Measuring cylinders, Test tubes, Safety goggles, Lab safety poster chart.",
                assessment = "Practical safety checklist, Apparatus identification quiz, Lab safety diagram rating."
            ),
            SubStrandData(
                strand = "Mixtures, Elements and Compounds",
                subStrand = "Separation of Mixtures (Filtration, Evaporation, Chromatography)",
                knowledge = "Define solute, solvent, solution, suspension, and explain principles of separation methods.",
                skill = "Assemble apparatus and separate sand-water mixture by filtration, salt solution by evaporation, and ink pigments by paper chromatography.",
                attitude = "Value resourcefulness and environmental cleanliness in water purification.",
                inquiryQuestion = "How can local materials be used to separate contaminated water into clean drinking water?",
                experiences = "Learners design a sand-charcoal water filter in groups, boil salt water to obtain crystals, and spot food coloring on filter paper.",
                resources = "Filter paper, Funnels, Evaporating dishes, Sand, Charcoal, Salt, Water, Beakers, Tripod stand.",
                assessment = "Lab practical evaluation, Group report on water filtration, Written concept quiz."
            ),
            SubStrandData(
                strand = "Living Things and their Environment",
                subStrand = "Cell Structure: Plant and Animal Cells Under Light Microscope",
                knowledge = "Identify parts of a light microscope and state functions of cell wall, cell membrane, cytoplasm, nucleus, and chloroplasts.",
                skill = "Prepare wet mounts of onion epidermis and cheek cells, focus a light microscope, and draw biological diagrams.",
                attitude = "Demonstrate care and gentleness when operating precision optical equipment.",
                inquiryQuestion = "How do differences in cell structures enable plants to stand upright while animals move freely?",
                experiences = "Learners mount onion skin with iodine stain, view specimen at x10 and x40 magnification, and draw labeled cell diagrams.",
                resources = "Light microscopes, Glass slides, Cover slips, Droppers, Iodine solution, Onion bulbs, Scalpel.",
                assessment = "Microscope handling rubric, Cell diagram labeling assessment, Oral questions."
            ),
            SubStrandData(
                strand = "Human Body Systems",
                subStrand = "Human Digestive System: Organs, Enzymes & Balanced Diet",
                knowledge = "Name organs of the digestive canal, state roles of saliva, gastric juice, bile, and identify deficiency diseases.",
                skill = "Conduct food tests for starch (iodine test), reducing sugars (Benedict's test), and proteins (Biuret test).",
                attitude = "Adopt healthy dietary choices and appreciate proper food digestion for active bodily function.",
                inquiryQuestion = "How does digestion transform complex food molecules into nutrients absorbed by the body?",
                experiences = "Learners perform chemical tests on food samples, trace the food path on anatomical torso models, and plan a balanced school lunch menu.",
                resources = "Anatomical digestive torso model, Reagents (Iodine, Benedict's, Biuret, Copper sulphate), Test tubes, Food samples.",
                assessment = "Food test practical sheet, Anatomical labeling test, Peer review of dietary meal plan."
            ),
            SubStrandData(
                strand = "Force and Energy",
                subStrand = "Types of Forces, Friction & Pressure in Fluids",
                knowledge = "Define contact and non-contact forces, explain factors affecting friction, and state formula for liquid pressure (P=hρg).",
                skill = "Measure weight using a spring balance, investigate methods of reducing/increasing friction, and demonstrate liquid pressure variation with depth.",
                attitude = "Acknowledge the vital role of friction in walking, braking vehicles, and machinery design.",
                inquiryQuestion = "How can controlling frictional forces improve fuel efficiency and prevent machine wear?",
                experiences = "Learners drag wooden blocks across smooth and rough surfaces with spring balances, observe water jets from perforated tins, and record data.",
                resources = "Spring balances, Wooden blocks with hooks, Sandpaper, Oil/lubricant, Perforated water cans, Manometer.",
                assessment = "Experiment observation sheet, Graphical force analysis, Written physics test."
            ),
            SubStrandData(
                strand = "Force and Energy",
                subStrand = "Simple Electrical Circuits: Series, Parallel & Electrical Safety",
                knowledge = "Identify electrical circuit components (cell, switch, bulb, ammeter, voltmeter) and state differences between series and parallel circuits.",
                skill = "Connect functional series and parallel circuits, measure current and voltage, and replace blown fuses safely.",
                attitude = "Exercise caution and respect for high-voltage electricity and electrical safety standards.",
                inquiryQuestion = "How does parallel circuit wiring prevent a whole household from going dark when one bulb blows?",
                experiences = "Learners connect dry cells, connecting wires, switches, and LED bulbs on circuit boards, compare brightness, and measure ammeter readings.",
                resources = "Dry cells, Cell holders, Connecting wires, Switches, Mini bulbs, Ammeters, Voltmeters, Multimeter.",
                assessment = "Circuit assembly performance test, Circuit diagram drawing quiz, Oral question-answer."
            ),
            SubStrandData(
                strand = "Earth and Space Science",
                subStrand = "The Solar System, Earth's Rotation, Revolution & Seasons",
                knowledge = "Describe characteristics of the eight planets, distinguish between rotation and revolution, and explain formation of day/night and seasons.",
                skill = "Construct a scaled model of the solar system and demonstrate solar and lunar eclipses using ray boxes and spheres.",
                attitude = "Cultivate wonder, curiosity, and environmental stewardship toward Planet Earth.",
                inquiryQuestion = "How does the Earth's tilted axis of revolution determine climatic seasons and agricultural cycles?",
                experiences = "Learners create papier-mâché planet models, simulate day/night cycles using a globe and torch, and track shadows on the playground.",
                resources = "Globe, High-intensity torch/lamp, Styrofoam balls, Solar system chart, Shadows tracker rod.",
                assessment = "Solar model exhibition rubric, Written astronomy test, Explanation presentation."
            )
        )
    }

    private fun getEnglishStrands(grade: String): List<SubStrandData> {
        return listOf(
            SubStrandData(
                strand = "Listening and Speaking",
                subStrand = "Conversational Etiquette, Active Listening & Pronunciation",
                knowledge = "Identify polite conversational formulas, turn-taking cues, and minimal phonetic pairs (/s/ vs /z/, /p/ vs /b/).",
                skill = "Demonstrate active listening techniques, articulate sounds with correct stress and intonation, and participate in debates.",
                attitude = "Demonstrate empathy, respect, and confidence in interpersonal discourse.",
                inquiryQuestion = "How does active listening and polite turn-taking foster peaceful communication in society?",
                experiences = "Learners roleplay formal interviews, conduct paired pronunciation drills, and engage in a structured mini-debate on social media usage.",
                resources = "Audio dialogue recordings, Pronunciation flashcards, Dialogue cue cards, Course text.",
                assessment = "Oral presentation rubric, Pronunciation listening quiz, Debate participation checklist."
            ),
            SubStrandData(
                strand = "Reading",
                subStrand = "Intensive Reading: Narrative Texts, Themes & Character Analysis",
                knowledge = "Define theme, plot, setting, character traits, and context-based vocabulary meanings.",
                skill = "Read grade-level passages fluently, make valid inferences, analyze character motivations, and summarize key chapters.",
                attitude = "Develop a lifelong passion for extensive literary reading and critical reflection.",
                inquiryQuestion = "How do literary characters and stories help us navigate moral dilemmas in real life?",
                experiences = "Learners read class reader chapters silently and aloud, highlight unfamiliar vocabulary, complete character web diagrams, and answer questions.",
                resources = "CBC Junior School Class Reader, Dictionaries, Reading comprehension worksheets, Character map charts.",
                assessment = "Comprehension test, Character profile writing assignment, Reading fluency rubric."
            ),
            SubStrandData(
                strand = "Grammar in Use",
                subStrand = "Nouns, Pronouns, and Subject-Verb Agreement",
                knowledge = "Distinguish countable/uncountable nouns, collective nouns, relative pronouns (who, whom, whose, which), and concord rules.",
                skill = "Construct grammatically sound sentences applying subject-verb agreement with singular and plural compound subjects.",
                attitude = "Value grammatical precision in academic writing and formal speech.",
                inquiryQuestion = "How does correct subject-verb agreement eliminate ambiguity in written communication?",
                experiences = "Learners identify grammatical errors in sample newspaper extracts, rewrite sentences applying concord rules, and play grammar bingo.",
                resources = "Grammar charts, Sentence strip cards, Junior English Coursebook, Grammar flashcards.",
                assessment = "Sentence correction exercise, Fill-in-the-blank quiz, Written paragraph composition."
            ),
            SubStrandData(
                strand = "Grammar in Use",
                subStrand = "Tenses: Simple Present, Present Continuous & Past Perfect",
                knowledge = "State structural forms of simple present, present continuous, simple past, and past perfect tenses with regular/irregular verbs.",
                skill = "Transform active sentences across different tenses and use past perfect to sequence historical events accurately.",
                attitude = "Appreciate chronological clarity and precision when recounting experiences.",
                inquiryQuestion = "How do verb tenses help us clearly sequence past and ongoing events in a story?",
                experiences = "Learners construct timeline diagrams of their daily routines, rewrite narrative excerpts into past tense, and complete verb tables.",
                resources = "Verb conjugation charts, Timeline worksheets, Grammar reference handbook.",
                assessment = "Tense transformation worksheet, Cloze test, Peer grammar review."
            ),
            SubStrandData(
                strand = "Writing",
                subStrand = "Functional Writing: Friendly Letters, Invitations & Emails",
                knowledge = "Outline structural components of informal letters (sender address, date, salutation, body, complimentary close, signature).",
                skill = "Compose a well-structured friendly letter and a polite digital email inviting a guest speaker to school.",
                attitude = "Embrace cordiality, etiquette, and digital literacy in personal communication.",
                inquiryQuestion = "How does adherence to formatting rules enhance the effectiveness of written functional messages?",
                experiences = "Learners examine sample friendly letters, draft an invitation letter to an environmental club event, and edit drafts in pairs.",
                resources = "Sample letter templates, Email layout worksheets, Junior English writing guide.",
                assessment = "Writing rubric evaluating format, content, grammar, punctuation, and tone."
            ),
            SubStrandData(
                strand = "Writing",
                subStrand = "Creative Writing: Narrative and Descriptive Compositions",
                knowledge = "Describe elements of captivating storytelling: hook, sensory details, vivid imagery, conflict, and climax.",
                skill = "Write an original 250-word narrative essay utilizing descriptive adjectives, similes, and dialogue effectively.",
                attitude = "Express creativity, imagination, and personal voice through written literature.",
                inquiryQuestion = "How can sensory details and figurative language make written stories vivid and memorable?",
                experiences = "Learners brainstorm story ideas from picture prompts, generate sensory word banks (sight, sound, touch), and draft narrative essays.",
                resources = "Visual picture prompts, Story organizer templates, Sensory word walls, Thesaurus.",
                assessment = "Creative composition rubric assessing creativity, plot development, vocabulary, and mechanics."
            ),
            SubStrandData(
                strand = "Reading & Poetry",
                subStrand = "Oral Literature: Riddles, Proverbs & Simple Poems",
                knowledge = "Define riddles, proverbs, rhyme, rhythm, stanza, and identify poetic devices (alliteration, personification).",
                skill = "Recite short poems with dynamic expression and gestures, solve riddles, and interpret the moral lessons in proverbs.",
                attitude = "Cherish cultural heritage, indigenous wisdom, and artistic expression through oral poetry.",
                inquiryQuestion = "How do proverbs and poetry pass down vital cultural wisdom across generations?",
                experiences = "Learners perform choral verse recitations, analyze stanzas for rhyming words, and exchange traditional community riddles.",
                resources = "Anthology of Junior Poetry, Audio recordings of spoken word, Cultural proverbs compilation.",
                assessment = "Poem recitation performance rubric, Poetic analysis test, Oral literature quiz."
            )
        )
    }

    private fun getKiswahiliStrands(grade: String): List<SubStrandData> {
        return listOf(
            SubStrandData(
                strand = "Kusikiliza na Kuongea",
                subStrand = "Maamkizi, Adabu za Mazungumzo na Ufahamu wa Kusikiliza",
                knowledge = "Kutaja maamkizi kulingana na wakati na hadhi ya wahusika, na kueleza kaida za mawasiliano ya heshima.",
                skill = "Kutumia lugha yenye staha katika maongezi, kutofautisha sauti tatanishi (/r/ na /l/, /b/ na /p/), na kujibu maswali ya kusikiliza.",
                attitude = "Kudhihirisha heshima, uzalendo, na ustadi wa kuthamini lugha ya Kiswahili.",
                inquiryQuestion = "How can polite greetings and respectful dialogue promote unity and peaceful coexistence in the community?",
                experiences = "Wanafunzi wanaigiza maamkizi mbalimbali, wanasikiliza taarifa ya sauti na kujibu maswali, na kufanya mijadala ya vikundi.",
                resources = "Kinasa sauti, Chati za maamkizi, Mwongozo wa Mwalimu wa KICD, Kamusi ya Kiswahili Sanifu.",
                assessment = "Tathmini ya igizo, Jaribio la ufahamu wa kusikiliza, Orodha hakiki ya matamshi."
            ),
            SubStrandData(
                strand = "Kusoma",
                subStrand = "Kusoma kwa Ufahamu: Vifungu vya Kuelimisha na Fasihi Simulizi",
                knowledge = "Kueleza maana ya msamiati mpya kimuktadha, kutaja maudhui makuu na dhamira ya mwandishi katika kifungu.",
                skill = "Kusoma kifungu kwa ufasaha na kasi inayofaa, kuchambua tabia za wahusika, na kutoa muhtasari wa aya.",
                attitude = "Kukuza ari ya kusoma vitabu mbalimbali ili kupanua upeo wa maarifa.",
                inquiryQuestion = "How does reading comprehension enhance critical analysis of societal contemporary issues?",
                experiences = "Wanafunzi wanasoma vifungu kimya na kwa sauti, wanatafuta maana za maneno kwenye kamusi, na kujibu maswali ya ufahamu.",
                resources = "Kitabu cha Mwanafunzi cha Kiswahili Gredi 7-9, Kamusi, Kadi za msamiati.",
                assessment = "Maswali ya ufahamu ya kuandika, Tathmini ya muhtasari wa kifungu, Maswali ya papo kwa papo."
            ),
            SubStrandData(
                strand = "Sarufi",
                subStrand = "Ngeli za Nomino (A-WA, KI-VI, LI-YA, I-ZI) na Upatanisho wa Kisarufi",
                knowledge = "Kufafanua dhana ya ngeli, kutambua viambishi ngeli, na kueleza sheria za upatanisho wa kisarufi.",
                skill = "Kupanga nomino katika ngeli zao sahihi na kutunga sentensi sahihi katika umoja na wingi.",
                attitude = "Kuthamini usahihi wa kisarufi katika mazungumzo na maandishi rasmi.",
                inquiryQuestion = "How does accurate noun class concord preserve grammatical coherence in Kiswahili sentences?",
                experiences = "Wanafunzi wanapanga kadi za nomino kwenye chati za ngeli, wanageuza sentensi kutoka umoja hadi wingi, na kurekebisha makosa.",
                resources = "Chati za ngeli za nomino, Kadi za sentensi, Kitabu cha sarufi cha KICD.",
                assessment = "Zoezi la kujaza mapengo ya ngeli, Zoezi la kubadili sentensi, Jaribio fupi la sarufi."
            ),
            SubStrandData(
                strand = "Sarufi",
                subStrand = "Aina za Maneno: Vitenzi, Vivumishi, Vielezi na Viunganishi",
                knowledge = "Kutambua aina nane za maneno katika Kiswahili na kueleza kazi zake kisentensi.",
                skill = "Kubainisha vitenzi, vielezi vya namna/mahali/wakati, vivumishi vya sifa/idadi, na kuunganisha sentensi kwa viunganishi vifaavyo.",
                attitude = "Kudhihirisha ubunifu katika utungaji wa sentensi kamilifu.",
                inquiryQuestion = "How do adverbs and descriptive adjectives enrich sentence variety and clarity in writing?",
                experiences = "Wanafunzi wanachambua sentensi ubaoni, wanabainisha aina za maneno kwa rangi tofauti, na kutunga aya fupi.",
                resources = "Chati ya aina za maneno, Vibao vya sentensi, Kijitabu cha mazoezi ya sarufi.",
                assessment = "Uchanganuzi wa sentensi, Jaribio la kuandika, Majadiliano ya marika."
            ),
            SubStrandData(
                strand = "Kuandika",
                subStrand = "Insha za Maelezo, Masimulizi na Barua Rasmi",
                knowledge = "Kueleza muundo wa barua rasmi (anwani mbili, tarehe, mtajo, kichwa cha habari, mwili, na hitimisho).",
                skill = "Kuandika barua rasmi ya kuomba nafasi ya ziara ya kimasomo na insha ya masimulizi yenye mtiririko mzuri.",
                attitude = "Kuthamini uandishi nadhifu, hati safi, na uakifishaji sahihi.",
                inquiryQuestion = "How does structured functional writing facilitate formal correspondence in institutions?",
                experiences = "Wanafunzi wanasoma sampuli ya barua rasmi, wanapanga muundo wa aya, na kuandika insha zao wakizingatia viakifishi.",
                resources = "Sampuli za barua rasmi, Mwongozo wa kutathmini insha, Vitabu vya insha.",
                assessment = "Tathmini ya insha kwa kutumia rubriki ya maudhui, msamiati, sarufi, na muundo."
            ),
            SubStrandData(
                strand = "Fasihi Simulizi",
                subStrand = "Semi: Methali, Vitendawili, Nahau na Misemo",
                knowledge = "Kufafanua maana ya nahau, methali, vitendawili, na kueleza muktadha wa matumizi yake.",
                skill = "Kutumia nahau na methali mwafaka katika tungo na maongezi ili kupamba lugha.",
                attitude = "Kujivunia urithi wa fasihi simulizi na hekima ya jadi ya jamii.",
                inquiryQuestion = "How do Swahili idioms and proverbs communicate moral life lessons creatively?",
                experiences = "Wanafunzi wanatengeneza kamusi ndogo ya nahau na methali, wanategana vitendawili darasani, na kutumia semi katika insha.",
                resources = "Kitabu cha Methali na Nahau, Kadi za methali zilizogawanywa, Chati za picha.",
                assessment = "Mchezo wa kutegua vitendawili, Jaribio la nahau na methali, Tathmini ya insha iliyonakshiwa."
            ),
            SubStrandData(
                strand = "Kusikiliza na Kusoma Ushairi",
                subStrand = "Mashairi ya Kawaida: Vina, Mizani, Ubeti na Kibwagizo",
                knowledge = "Kutambua sifa za shairi: beti, mishororo, vina vya ndani na nje, mizani, na kibwagizo.",
                skill = "Kughani shairi kwa hisia, kuhesabu mizani ya mshororo, na kubainisha vina vya ubeti.",
                attitude = "Kufurahia sanaa ya ushairi na ujumbe wake kwa jamii.",
                inquiryQuestion = "How does poetic rhythm and rhyme enhance the musicality and retention of spoken messages?",
                experiences = "Wanafunzi wanakariri mashairi kwa pamoja, wanapiga makofi kulingana na mizani, na kuchambua ujumbe wa ubeti.",
                resources = "Diwani ya Mashairi ya Shule za Upili Ndogo, Rekodi za sauti za washairi maarufu.",
                assessment = "Tathmini ya ughani wa shairi, Maswali ya uchambuzi wa muundo wa shairi."
            )
        )
    }

    private fun getSocialStudiesStrands(grade: String): List<SubStrandData> {
        return listOf(
            SubStrandData(
                strand = "Natural and Built Environments",
                subStrand = "Maps and Map Reading: Direction, Scale & Grid References",
                knowledge = "Identify cardinal compass directions, types of map scales (linear, representative fraction), and 4-figure/6-figure grid references.",
                skill = "Calculate real ground distances using linear scales and locate physical features using grid coordinates on topographical maps.",
                attitude = "Appreciate the value of spatial mapping skills in navigation and town planning.",
                inquiryQuestion = "How do topographical maps and scale measurements guide spatial navigation and infrastructure planning?",
                experiences = "Learners study Kenya survey topographical maps in groups, measure road distances using thread and linear scales, and determine grid references.",
                resources = "Topographical maps of Kenya, Atlas, Rulers, Thread, Compass, Flashcards.",
                assessment = "Map work practical exercise, Grid location test, Scale calculation quiz."
            ),
            SubStrandData(
                strand = "Physical Features and Climate",
                subStrand = "Formation of Landforms (Rift Valley, Mountains) & Weather Patterns",
                knowledge = "Explain internal earth processes (faulting, folding, volcanicity) that formed the Great Rift Valley and Mount Kenya.",
                skill = "Construct 3D relief models of volcanic mountains and read weather instruments (Stevenson screen, rain gauge, hygrometer).",
                attitude = "Advocate for environmental conservation and climate action.",
                inquiryQuestion = "How do tectonic earth movements shape human settlement patterns and regional climates?",
                experiences = "Learners mold plasticine models of fold mountains, record daily rainfall and temperature at the school weather station, and graph findings.",
                resources = "Relief globe, Plasticine, School weather station instruments, Weather charts.",
                assessment = "Relief model presentation rubric, Weather recording log check, Written test."
            ),
            SubStrandData(
                strand = "People and Population",
                subStrand = "Human Origin, Early Man & Migration of Language Groups in Eastern Africa",
                knowledge = "Describe archaeological evidence of early human evolution in Eastern Africa (Olduvai, Rusinga) and trace migration routes (Bantus, Nilotes, Cushites).",
                skill = "Draw historical migration route maps and analyze factors that influenced historical population settlement.",
                attitude = "Celebrate ethnic diversity and foster national unity and harmony.",
                inquiryQuestion = "How did early human adaptations and migration foster cultural exchange and technological advancement in Eastern Africa?",
                experiences = "Learners sketch migration maps in atlases, examine replicas of early stone tools (Acheulean handaxe), and engage in group presentations.",
                resources = "Historical wall maps, Kenyan Atlas, Replicas of Early Stone Age tools, Timeline charts.",
                assessment = "Migration map drawing evaluation, Historical essay question, Group project report."
            ),
            SubStrandData(
                strand = "Social and Political Systems",
                subStrand = "Traditional Governance Systems and the Constitution of Kenya",
                knowledge = "Explain traditional systems of governance (e.g. Council of Elders) and outline key arms of government (Executive, Legislature, Judiciary).",
                skill = "Compare traditional conflict resolution with modern court systems and participate in a mock parliamentary debate.",
                attitude = "Uphold democratic values, constitutionalism, human rights, and the rule of law.",
                inquiryQuestion = "How does adherence to the Constitution protect human rights and promote social justice?",
                experiences = "Learners simulate a school parliament session, analyze articles of the Kenyan Bill of Rights, and roleplay alternative dispute resolution.",
                resources = "Constitution of Kenya 2010 booklet, Simplified Bill of Rights posters, Video documentary.",
                assessment = "Mock parliament observation, Constitutional knowledge quiz, Essay on democratic values."
            ),
            SubStrandData(
                strand = "Resources and Economic Activities",
                subStrand = "Agriculture, Mining and Sustainable Tourism in Kenya",
                knowledge = "Identify major export cash crops (tea, coffee, horticulture), mining centers (soda ash, titanium), and top tourist attractions.",
                skill = "Analyze economic contributions of agriculture and tourism to national GDP and propose solutions to human-wildlife conflict.",
                attitude = "Embrace sustainable resource utilization and wildlife protection.",
                inquiryQuestion = "How can sustainable eco-tourism generate national income while conserving natural biodiversity?",
                experiences = "Learners create economic activity resource maps of Kenya, analyze tourist arrival data graphs, and design wildlife conservation posters.",
                resources = "Economic resource map of Kenya, Case study cards, Statistical data charts, Atlas.",
                assessment = "Resource map project, Data interpretation worksheet, Written test."
            ),
            SubStrandData(
                strand = "Citizenship and Peace Education",
                subStrand = "National Values, Human Rights & Peaceful Conflict Resolution",
                knowledge = "State national values (patriotism, national unity, integrity), fundamental human rights, and causes of community conflicts.",
                skill = "Apply mediation and negotiation strategies to resolve interpersonal disputes peacefully.",
                attitude = "Demonstrate patriotism, honesty, anti-corruption stance, and community solidarity.",
                inquiryQuestion = "How can youth champion national values and integrity to curb corruption in our institutions?",
                experiences = "Learners create anti-corruption slogans, roleplay community peer mediation, and analyze case studies on human rights defense.",
                resources = "National values handbook, Case study vignettes, Peace building posters.",
                assessment = "Peer mediation roleplay rubric, Reflective journal entry, Written test."
            ),
            SubStrandData(
                strand = "Global Connections",
                subStrand = "Regional Cooperation: East African Community (EAC) & African Union",
                knowledge = "State member states, organs, objectives, and benefits of the East African Community (EAC) and African Union (AU).",
                skill = "Examine economic integration benefits (common market, customs union) and locate member countries on the political map of Africa.",
                attitude = "Appreciate pan-Africanism, regional integration, and international solidarity.",
                inquiryQuestion = "How does free movement of goods and labor under the EAC common market stimulate economic growth?",
                experiences = "Learners locate EAC capitals on political maps, simulate an EAC summit session discussing cross-border trade, and design flags.",
                resources = "Political map of Africa, EAC treaty summary chart, Flags of member states.",
                assessment = "EAC summit simulation rubric, Map location test, Written assessment."
            )
        )
    }

    private fun getAgricultureStrands(grade: String): List<SubStrandData> {
        return listOf(
            SubStrandData(
                strand = "Conservation of Agricultural Resources",
                subStrand = "Soil Conservation: Structures, Mulching & Agroforestry",
                knowledge = "Describe types of soil erosion (splash, sheet, rill, gully) and explain methods of soil conservation (terracing, cover cropping, mulching).",
                skill = "Construct a model bench terrace, apply organic mulch to vegetable seedbeds, and plant agroforestry tree seedlings.",
                attitude = "Demonstrate responsibility in safeguarding fertile topsoil for future food security.",
                inquiryQuestion = "How can soil conservation practices mitigate the adverse effects of drought and topsoil degradation?",
                experiences = "Learners tour the school agricultural plot, identify erosion signs, construct model soil conservation bunds, and apply grass mulch.",
                resources = "School farm/plot, Jembes, Pangas, Mulching grass, Tree seedlings, Soil sample boxes.",
                assessment = "Practical farm performance rubric, Soil structure diagram test, Group fieldwork report."
            ),
            SubStrandData(
                strand = "Crop Production",
                subStrand = "Vegetable Crop Establishment: Nursery Bed Preparation & Transplanting",
                knowledge = "Distinguish between nursery beds and seedbeds, explain seed selection criteria, and state management practices (watering, shading, hardening off).",
                skill = "Prepare a fine-tilth nursery bed, sow small vegetable seeds, thin seedlings, and transplant tomatoes/kales at correct spacing.",
                attitude = "Value hard work, self-reliance, and agribusiness enterprise in vegetable farming.",
                inquiryQuestion = "How does proper nursery management ensure high crop yield and vigorous plant growth?",
                experiences = "Learners measure a 1m x 3m nursery bed on the school farm, level soil, drill seeds, build a shade structure, and water seedlings.",
                resources = "Vegetable seeds (tomatoes, kales, spinach), Rakes, Watering cans, Sisal twine, Measuring tape, Timber pegs.",
                assessment = "Nursery bed construction practical test, Transplanting technique checklist, Oral questions."
            ),
            SubStrandData(
                strand = "Animal Production",
                subStrand = "Rearing Small Domestic Animals: Poultry & Rabbit Management",
                knowledge = "Identify common poultry and rabbit breeds, describe housing requirements (ventilation, predator safety), and feeding routines.",
                skill = "Clean and disinfect a hutch/coop, prepare balanced feed rations, and perform daily animal health checks.",
                attitude = "Display compassion, animal welfare ethics, and hygiene in handling livestock.",
                inquiryQuestion = "How does bio-security and hygienic housing prevent disease outbreaks in poultry production?",
                experiences = "Learners inspect the school rabbit hutch, mix poultry feeds using local grains, and record daily feed intake in livestock logbooks.",
                resources = "School livestock unit, Rabbit hutch model, Poultry feeders, Drinkers, Feed ingredients, Disinfectants.",
                assessment = "Livestock handling checklist, Farm logbook assessment, Written animal production test."
            ),
            SubStrandData(
                strand = "Food Preparation and Preservation",
                subStrand = "Preservation of Farm Produce: Solar Drying, Blanching & Fermentation",
                knowledge = "Explain principles of food spoilage (microorganisms, enzymes) and describe preservation methods for surplus harvest.",
                skill = "Construct a simple solar food dryer, blanch green leafy vegetables, and store dried produce in airtight containers.",
                attitude = "Value reduction of post-harvest food waste and nutrition security.",
                inquiryQuestion = "How does solar drying preserve the nutritional value and shelf-life of indigenous vegetables?",
                experiences = "Learners slice vegetables and fruits, construct a wooden solar dryer frame with polythene sheet, dry vegetables, and evaluate moisture loss.",
                resources = "Fresh vegetables, Solar dryer materials (mesh, polythene, timber), Airtight jars, Cutting boards, Knives.",
                assessment = "Solar dryer design evaluation, Preserved produce quality check, Written test."
            ),
            SubStrandData(
                strand = "Consumer Education and Nutrition",
                subStrand = "Meal Planning: Balanced Diets for Special Nutritional Needs",
                knowledge = "Classify food nutrients (carbohydrates, proteins, vitamins, minerals, fats) and identify requirements for adolescents and athletes.",
                skill = "Formulate a balanced three-course meal plan utilizing locally available affordable food items.",
                attitude = "Embrace healthy eating habits and reject ultra-processed junk food diets.",
                inquiryQuestion = "How can families use indigenous affordable crops to meet all essential nutritional requirements?",
                experiences = "Learners analyze nutritional labels on food packages, create diet charts for growing teenagers, and calculate food group proportions.",
                resources = "Food nutrient charts, Sample food packaging labels, Indigenous food recipe book.",
                assessment = "Meal planning portfolio, Nutritional calculation test, Peer meal presentation."
            ),
            SubStrandData(
                strand = "Agribusiness and Marketing",
                subStrand = "Value Addition and Marketing of Agricultural Products",
                knowledge = "Define value addition (e.g. processing peanuts into peanut butter, milk into yoghurt) and identify direct and digital marketing channels.",
                skill = "Package farm produce hygienically, design an eye-catching product label with expiry dates, and calculate profit margins.",
                attitude = "Cultivate entrepreneurial mindset and customer-focused ethical trade.",
                inquiryQuestion = "How does value addition increase farmers' income and open new market opportunities?",
                experiences = "Learners package dried fruit snacks in food-grade pouches, design brand logos on paper/digital apps, and pitch products in class.",
                resources = "Packaging pouches, Label design sheets, Sample value-added products, Costing sheets.",
                assessment = "Product packaging presentation rubric, Cost-profit calculation worksheet, Written test."
            ),
            SubStrandData(
                strand = "Water Harvesting and Irrigation",
                subStrand = "Drip Irrigation and Rainwater Catchment Systems",
                knowledge = "Explain methods of harvesting rainwater (rooftop catchment, farm ponds) and principles of low-cost drip irrigation.",
                skill = "Assemble a bottle-drip irrigation system for kitchen garden sacks and measure water discharge rates.",
                attitude = "Champion water conservation and climate-smart agriculture.",
                inquiryQuestion = "How can low-cost drip irrigation maximize crop yields in arid and semi-arid homesteads?",
                experiences = "Learners collect plastic bottles, perforate caps with needles, install drip bottles beside vegetable plants, and monitor soil moisture.",
                resources = "Recycled plastic bottles, Perforating pins, Kitchen garden grow bags, Watering hose/can.",
                assessment = "Drip setup practical evaluation, Moisture retention logbook, Written quiz."
            )
        )
    }

    private fun getPreTechnicalStrands(grade: String): List<SubStrandData> {
        return listOf(
            SubStrandData(
                strand = "Safety in Work Environments",
                subStrand = "Workshop Safety Rules, Personal Protective Equipment (PPE) & First Aid",
                knowledge = "Identify common workshop hazards (mechanical, electrical, chemical), state safety rules, and list PPE items.",
                skill = "Wear appropriate PPE (goggles, gloves, apron, boots), operate fire extinguishers, and administer first aid for minor cuts and burns.",
                attitude = "Foster safety mindset and proactive hazard prevention in production environments.",
                inquiryQuestion = "How does strict adherence to safety protocols prevent occupational injuries in technical workshops?",
                experiences = "Learners inspect the school pre-technical workshop, simulate a fire evacuation drill, don safety gear, and practice bandage dressing.",
                resources = "Safety goggles, Leather gloves, Aprons, Fire extinguisher, First aid kit, Safety chart posters.",
                assessment = "Workshop safety inspection checklist, First aid simulation rubric, Written safety test."
            ),
            SubStrandData(
                strand = "Materials for Production",
                subStrand = "Classification and Properties of Materials (Metals, Wood, Plastics, Ceramics)",
                knowledge = "Classify materials into metallic (ferrous, non-ferrous) and non-metallic, and describe physical properties (hardness, elasticity, conductivity).",
                skill = "Perform simple tests to identify magnetism, thermal conductivity, and density of different material specimens.",
                attitude = "Appreciate the economic value of sustainable and recyclable engineering materials.",
                inquiryQuestion = "How do material physical properties dictate their suitability for specific engineering applications?",
                experiences = "Learners test metal and plastic rods with magnets and hot water, record property tables in groups, and categorize workshop off-cuts.",
                resources = "Material sample kit (copper, aluminum, iron, softwood, hardwood, PVC, ceramic), Bar magnets, Beakers, Burner.",
                assessment = "Material testing practical report, Material classification chart check, Written test."
            ),
            SubStrandData(
                strand = "Technical Drawing and Design",
                subStrand = "Drawing Instruments, Lettering, Line Types & Orthographic Projection",
                knowledge = "Identify drawing instruments (T-square, set squares, drawing board), line types (construction, outline, hidden, dimension), and standard lettering.",
                skill = "Mount drawing paper accurately, letter title blocks neatly, and draw isometric and orthographic views of simple wooden blocks.",
                attitude = "Cultivate precision, neatness, patience, and spatial visual thinking.",
                inquiryQuestion = "How does standardized technical drawing serve as the universal language of engineering and fabrication?",
                experiences = "Learners tape A3 drawing paper to boards, draw border lines and title blocks, practice uppercase single-stroke lettering, and project 3D shapes.",
                resources = "Drawing boards, T-squares, 30°/60° and 45° set squares, Compass, 2H/HB pencils, Isometric grid paper.",
                assessment = "Drafting plate evaluation rubric (linework, accuracy, lettering), Spatial projection test."
            ),
            SubStrandData(
                strand = "Tools and Equipment",
                subStrand = "Marking, Measuring, Cutting and Joining Hand Tools",
                knowledge = "Identify marking/measuring tools (steel rule, try square, scriber), cutting tools (tenon saw, hacksaw, chisel), and joining fasteners.",
                skill = "Measure and mark workpieces to ±1mm accuracy, cut timber with a tenon saw along marked lines, and join pieces using screws and wood glue.",
                attitude = "Demonstrate tool maintenance culture (cleaning, oiling, proper storage).",
                inquiryQuestion = "How does accurate marking and cutting ensure seamless assembly in joinery and fabrication?",
                experiences = "Learners mark a wooden lap joint using try square and marking gauge, clamp timber in a bench vice, saw along waste lines, and chisel joint.",
                resources = "Steel rules, Try squares, Tenon saws, Bench vices, Wood chisels, Mallets, Screwdrivers, Timber pieces.",
                assessment = "Joint fabrication accuracy check, Tool handling safety observation, Written tools quiz."
            ),
            SubStrandData(
                strand = "Simple Machines and Mechanisms",
                subStrand = "Levers, Pulleys, Gears & Mechanical Advantage",
                knowledge = "Define mechanical advantage (MA), velocity ratio (VR), efficiency, and classify classes of levers (1st, 2nd, 3rd class).",
                skill = "Assemble single and compound pulley systems, calculate effort required to lift a load, and construct a functional model crane.",
                attitude = "Acknowledge the transformative role of mechanisms in reducing human physical labor.",
                inquiryQuestion = "How do gears and pulley systems transmit motion and multiply force in heavy machinery?",
                experiences = "Learners build lever systems with meter rules and fulcrums, test gear trains with bicycle cogs, and calculate mechanical advantage.",
                resources = "Pulley sets, Slotted masses, Spring balances, Model gear kits, Meter rules, Fulcrums.",
                assessment = "Mechanism assembly rubric, Mechanical calculation test, Group project evaluation."
            ),
            SubStrandData(
                strand = "Basic Electrical & Electronics",
                subStrand = "Electronic Components: Resistors, Diodes, LEDs & Breadboard Prototyping",
                knowledge = "Identify electronic symbols (resistor, capacitor, diode, LED, transistor) and read 4-band resistor color codes.",
                skill = "Interpret resistor color codes, calculate resistance values, and assemble an LED indicator circuit on a solderless breadboard.",
                attitude = "Embrace digital curiosity, innovation, and electronics prototyping.",
                inquiryQuestion = "How do basic semiconductor diodes and resistors regulate current flow in modern gadgets?",
                experiences = "Learners decode resistor color bands, test resistances with digital multimeters, and wire a light-sensitive sensor on a breadboard.",
                resources = "Solderless breadboards, Assorted resistors, LEDs, 9V batteries, Digital multimeters, Jumper wires, LDRs.",
                assessment = "Breadboard circuit functional check, Resistor code calculation quiz, Oral questions."
            ),
            SubStrandData(
                strand = "Entrepreneurship and Innovation",
                subStrand = "Product Design, Prototyping and Business Plan Development",
                knowledge = "Explain steps in design thinking (Empathize, Define, Ideate, Prototype, Test) and components of a simple business pitch.",
                skill = "Design a useful household artifact using recycled materials and present a 2-minute business pitch.",
                attitude = "Display problem-solving initiative, environmental responsibility, and commercial vision.",
                inquiryQuestion = "How can design thinking turn community challenges into viable commercial enterprises?",
                experiences = "Learners brainstorm solutions for school bag storage, sketch product designs, build a cardboard prototype, and pitch to peers.",
                resources = "Cardboard, Glue guns, Recycled plastics, Design sketching paper, Pitch evaluation sheets.",
                assessment = "Prototype innovation rubric, Business pitch presentation score, Peer review."
            )
        )
    }

    private fun getCreativeArtsStrands(grade: String): List<SubStrandData> {
        return listOf(
            SubStrandData(
                strand = "Visual Arts",
                subStrand = "Drawing and Painting: Color Theory, Shading & Landscape Composition",
                knowledge = "Identify primary, secondary, and tertiary colors on the color wheel, and describe shading techniques (cross-hatching, stippling, blending).",
                skill = "Mix complementary colors to create harmonious palettes, shade 3D geometric forms, and paint a local landscape composition.",
                attitude = "Express emotional aesthetic appreciation and visual artistic creativity.",
                inquiryQuestion = "How do color harmonies and tonal values create the illusion of three-dimensional depth on flat canvas?",
                experiences = "Learners draw a 12-hue color wheel, shade spheres with pencils, paint a sunset landscape using poster colors, and critique artworks.",
                resources = "Drawing books, Poster paints, Paintbrushes, Pencils (2B-6B), Color wheel chart, Mixing palettes.",
                assessment = "Artwork assessment rubric (composition, technique, color harmony), Peer art critique."
            ),
            SubStrandData(
                strand = "Visual Arts & Crafts",
                subStrand = "Sculpture and Pottery: Clay Modeling, Pinching & Coil Technique",
                knowledge = "Describe properties of clay, wedging process, coil technique, slab construction, and firing/glazing basics.",
                skill = "Prepare and wedge clay to remove air bubbles, construct a functional coil pot, and decorate surfaces with incised patterns.",
                attitude = "Cherish traditional pottery heritage, craftsmanship, and patience.",
                inquiryQuestion = "How does the coil technique enable potters to craft diverse hollow vessels without a potter's wheel?",
                experiences = "Learners wedge local clay, roll uniform clay coils, build symmetric vessels, smooth walls with wooden ribs, and dry pots slowly.",
                resources = "Clay, Modeling tools, Sponges, Water containers, Wooden boards, Incising tools.",
                assessment = "Pottery structure rubric (symmetry, thickness, finish), Formative feedback."
            ),
            SubStrandData(
                strand = "Performing Arts - Music",
                subStrand = "Musical Notation, Sol-fa Notes, Western & African Instruments",
                knowledge = "Identify lines and spaces of the Treble Staff, musical notes (semibreve, minim, crotchet, quaver), and traditional African instruments (Nyatiti, Deso).",
                skill = "Sing major scale sol-fa syllables (d-r-m-f-s-l-t-d) in tune, sight-read simple rhythms, and play rhythmic patterns on a percussion instrument.",
                attitude = "Foster musical expression, cultural pride, and collaborative harmony in ensemble performance.",
                inquiryQuestion = "How does musical notation enable musicians around the world to read and perform identical compositions?",
                experiences = "Learners pitch sol-fa hand signs, clap rhythmic phrases, play traditional drum patterns in polyrhythmic sync, and record songs.",
                resources = "Treble staff manuscript books, Descant recorders, Traditional drums, Shakers, Audio speaker, Sol-fa chart.",
                assessment = "Sight-singing performance rubric, Rhythm clapping test, Notation writing worksheet."
            ),
            SubStrandData(
                strand = "Performing Arts - Drama & Dance",
                subStrand = "Folk Dance, Choreography, Costumes & Theatrical Storytelling",
                knowledge = "Describe elements of traditional African folk dance (rhythm, body movement, formations, props) and theatrical stage blocking.",
                skill = "Perform an authentic community folk dance with coordinated formations, express dramatic character emotions, and design traditional props.",
                attitude = "Embrace cultural heritage, teamwork, expressive self-confidence, and discipline.",
                inquiryQuestion = "How do traditional dance choreographies communicate cultural milestones and collective identity?",
                experiences = "Learners rehearse a traditional dance (e.g. Isukuti or Kilumi), coordinate stepping to drumming, construct costumes from local fibers, and perform.",
                resources = "Traditional costumes, Props, Ankle bells, Recorded music, Open performance space.",
                assessment = "Dance performance rubric (rhythm, expression, teamwork), Drama characterization score."
            ),
            SubStrandData(
                strand = "Physical Education and Sports",
                subStrand = "Athletics: Sprints, Relays (Baton Exchange) & Long Jump Technique",
                knowledge = "Explain phases of sprint starts (crouch start: 'On your marks, Set, Go'), visual/non-visual baton exchange, and long jump phases (approach, takeoff, flight, landing).",
                skill = "Execute a sprint start from blocks, perform fluent baton exchanges inside the changeover zone, and execute a hang-style long jump.",
                attitude = "Cultivate sportsmanship, fair play, physical fitness, and resilient perseverance.",
                inquiryQuestion = "How does synchronized timing during baton exchange shave critical seconds off relay race times?",
                experiences = "Learners perform warm-up dynamic stretches, practice non-visual baton passing in pairs, run 4x100m relays, and measure long jump distances.",
                resources = "Running track / field, Relay batons, Starting blocks, Stopwatches, Long jump sandpit, Rake, Measuring tape.",
                assessment = "Athletic skill execution checklist, Relay changeover technique rating, Fitness test."
            ),
            SubStrandData(
                strand = "Physical Education and Sports",
                subStrand = "Ball Games: Football (Passing, Dribbling, Shooting & Rules)",
                knowledge = "State FIFA rules of football (offsides, fouls, throw-ins), dimensions of the field, and positions of players.",
                skill = "Demonstrate push-passing with the inside of the foot, close-control dribbling through cones, and accurate shooting on goal.",
                attitude = "Value team strategy, communication, mutual respect for opponents, and referee decisions.",
                inquiryQuestion = "How does tactical spatial positioning and quick short passing unlock tight defensive formations?",
                experiences = "Learners execute passing drills in grids, practice 3v3 small-sided possession games, referee short matches, and perform cool-down stretches.",
                resources = "Footballs (size 5), Training cones, Bibs/jerseys, Whistle, Goalposts, Inflation pump.",
                assessment = "Ball control skill checklist, Game participation rubric, Sports rules oral quiz."
            ),
            SubStrandData(
                strand = "Physical Education and Sports",
                subStrand = "Ball Games: Netball / Volleyball (Overhead Pass, Digging & Serving)",
                knowledge = "Explain volleyball rules (rotations, 3-touch rule, line faults) and netball footwork rules (held ball, obstruction).",
                skill = "Perform the underhand volleyball serve, forearm dig, and accurate chest and bounce passing in netball.",
                attitude = "Embrace physical agility, teamwork, coordination, and positive encouragement.",
                inquiryQuestion = "How does effective communication between players enhance defensive coverage on court?",
                experiences = "Learners practice partner forearm passing, serve over regulation nets, participate in mini-matches, and analyze match statistics.",
                resources = "Volleyballs, Netball balls, Court nets, Whistles, Antennas, Scoreboard.",
                assessment = "Volleyball serve and pass technical rating, Game performance rubric, Peer review."
            )
        )
    }

    private fun getCreStrands(grade: String): List<SubStrandData> {
        return listOf(
            SubStrandData(
                strand = "Creation and the Bible",
                subStrand = "The Biblical Accounts of Creation (Genesis 1 & 2) & Environmental Stewardship",
                knowledge = "Compare the two biblical creation accounts and explain the human responsibility to care for God's creation (Genesis 2:15).",
                skill = "Analyze human stewardship actions (tree planting, anti-pollution) and lead an environmental clean-up initiative.",
                attitude = "Display reverence for the Creator, environmental responsibility, and sanctity of all life.",
                inquiryQuestion = "How does the biblical mandate of stewardship inspire contemporary environmental conservation?",
                experiences = "Learners read Genesis 1 & 2 in study bibles, compare differences in tabular charts, and plant commemorative class trees.",
                resources = "Good News / Revised Standard Version Bibles, Environmental posters, Tree seedlings, Reflection journals.",
                assessment = "Biblical analysis worksheet, Tree planting project checklist, Reflective essay."
            ),
            SubStrandData(
                strand = "The Bible and Salvation History",
                subStrand = "Faith and God's Promises to Abraham (Genesis 12 & 15)",
                knowledge = "Narrate the call of Abraham, list God's covenant promises, and explain the significance of faith and obedience in Christian living.",
                skill = "Evaluate contemporary faith challenges and roleplay modern scenarios demonstrating obedience to God.",
                attitude = "Cultivate unwavering faith, trust in divine promises, and moral integrity.",
                inquiryQuestion = "How can Abraham's exemplary faith guide young people in making righteous moral choices today?",
                experiences = "Learners trace Abraham's journey from Ur to Canaan on biblical maps, discuss covenant symbols, and compose personal faith prayers.",
                resources = "Bible atlas, Study Bibles, Covenant chart, Flashcards of biblical patriarchs.",
                assessment = "Biblical comprehension test, Map work on Abraham's journey, Oral presentation."
            ),
            SubStrandData(
                strand = "The Life and Ministry of Jesus Christ",
                subStrand = "The Baptism, Temptations of Jesus & The Sermon on the Mount",
                knowledge = "Describe the baptism and three temptations of Jesus, and explain teachings of the Beatitudes (Matthew 5:1-12).",
                skill = "Apply biblical strategies (scripture quoting, prayer) to overcome moral temptations and resolve conflicts peacefully.",
                attitude = "Emulate Christ's humility, purity of heart, mercy, and peacemaking spirit.",
                inquiryQuestion = "How can young Christians overcome modern temptations and live by the moral values of the Beatitudes?",
                experiences = "Learners dramatize the temptations of Jesus in the wilderness, memorize key Beatitudes verses, and analyze case studies on peer pressure.",
                resources = "Bibles, Drama script outlines, Beatitudes memory verse flashcards.",
                assessment = "Dramatization assessment rubric, Memory verse recitation, Written test on Sermon on the Mount."
            ),
            SubStrandData(
                strand = "The Early Church and Christian Living",
                subStrand = "The Day of Pentecost, Gifts & Fruits of the Holy Spirit (Galatians 5:22-23)",
                knowledge = "Describe events of Pentecost (Acts 2), distinguish between spiritual gifts (1 Cor 12) and the fruits of the Holy Spirit.",
                skill = "Demonstrate the fruits of love, joy, peace, patience, kindness, and self-control in daily school relationships.",
                attitude = "Seek divine guidance of the Holy Spirit and foster communal unity and charity.",
                inquiryQuestion = "How do the fruits of the Holy Spirit transform interpersonal relationships and curb social vices?",
                experiences = "Learners read Acts 2 in pairs, draw the 'Tree of the Holy Spirit Fruits', and write reflective personal testimony entries.",
                resources = "Bibles, Fruit of the Spirit illustration posters, Journal books, Hymn books.",
                assessment = "Spiritual fruits matching test, Reflective journal check, Class participation."
            ),
            SubStrandData(
                strand = "Moral and Spiritual Values",
                subStrand = "Christian Values: Honesty, Integrity, Sexual Purity & Anti-Substance Abuse",
                knowledge = "State biblical teachings on sexual purity, honesty, the body as the temple of the Holy Spirit (1 Cor 6:19), and dangers of substance abuse.",
                skill = "Formulate assertive refusal skills when faced with negative peer pressure regarding drug abuse or illicit sexual behavior.",
                attitude = "Value bodily sanctity, moral courage, personal dignity, and healthy lifestyles.",
                inquiryQuestion = "How does treating the human body as the temple of God protect teenagers from destructive social vices?",
                experiences = "Learners roleplay refusal techniques in peer pressure scenarios, analyze case studies on drug addiction recovery, and sign integrity pledges.",
                resources = "Life skills & Christian living booklet, Bible study guides, Case study scenario cards.",
                assessment = "Refusal skills roleplay rating, Essay on Christian integrity, Class discussion contribution."
            ),
            SubStrandData(
                strand = "Contemporary Issues and Christian Response",
                subStrand = "Social Justice: Care for the Needy, Inclusivity & Combating Corruption",
                knowledge = "Explain prophetic teachings on social justice (Amos, Micah) and the Christian obligation to care for orphans, widows, and people with disabilities.",
                skill = "Organize a school charity initiative and articulate righteous anti-corruption principles in public discussions.",
                attitude = "Cultivate compassion, generous philanthropy, and passionate commitment to justice and equity.",
                inquiryQuestion = "How can prophetic calls for social justice guide Christian youth in championing fairness and anti-corruption in society?",
                experiences = "Learners read excerpts from the Book of Amos, plan a community visit or food drive for vulnerable community members, and write poetry.",
                resources = "Bibles, Social justice case studies, Charity planning template sheets.",
                assessment = "Charity outreach project participation, Prophetic analysis test, Group presentation."
            ),
            SubStrandData(
                strand = "Christianity and Family Life",
                subStrand = "Family Roles, Mutual Respect, Parenting & Honoring Elders",
                knowledge = "Explain biblical teachings on family relationships (Ephesians 5 & 6, Colossians 3) and reciprocal duties of children and parents.",
                skill = "Demonstrate respectful communication with parents/guardians and practice constructive conflict resolution within families.",
                attitude = "Honor parents, uphold family unity, and promote domestic peace and mutual support.",
                inquiryQuestion = "How does reciprocal love and obedience in Christian families nurture responsible citizenship?",
                experiences = "Learners analyze family case scenarios, write gratitude letters to their parents/guardians, and debate on balancing work and family time.",
                resources = "Bibles, Family case study worksheets, Stationery for gratitude letters.",
                assessment = "Family case study analysis score, Gratitude letter completion check, Oral questions."
            )
        )
    }

    private fun getBusinessStudiesStrands(grade: String): List<SubStrandData> {
        return listOf(
            SubStrandData(
                strand = "Introduction to Business Studies",
                subStrand = "Nature, Importance of Business & Internal/External Business Environments",
                knowledge = "Define business, business studies, goods, services, and identify elements of internal environment (owner, staff, capital) and external environment (economy, law, tech).",
                skill = "Analyze how changes in technology and government policies impact small business operations in the local neighborhood.",
                attitude = "Appreciate the role of business in fulfilling human needs and creating employment.",
                inquiryQuestion = "How do environmental factors create opportunities or pose challenges for budding enterprises?",
                experiences = "Learners map businesses within their local shopping center, classify internal vs external factors on charts, and present case studies.",
                resources = "Business studies coursebook, Local market survey worksheets, Business environment charts.",
                assessment = "Local enterprise survey report, Classification quiz, Oral presentation."
            ),
            SubStrandData(
                strand = "Money and Financial Institutions",
                subStrand = "Functions of Money, Commercial Banks & Mobile Money Services (M-Pesa)",
                knowledge = "State characteristics and functions of money (medium of exchange, unit of account), and describe banking services (savings, loans, mobile transfers).",
                skill = "Complete bank deposit/withdrawal slips, calculate mobile transaction fees, and create a personal weekly budget.",
                attitude = "Develop a savings culture, financial discipline, and vigilance against mobile money fraud.",
                inquiryQuestion = "How have digital mobile banking innovations revolutionized financial inclusion and commerce in Kenya?",
                experiences = "Learners roleplay commercial bank teller transactions, calculate M-Pesa transaction costs on fee charts, and draft personal savings budgets.",
                resources = "Sample bank deposit slips, Mobile money fee charts, Mock play currency, Budget template worksheets.",
                assessment = "Budget preparation project, Bank slip completion test, Financial calculation quiz."
            ),
            SubStrandData(
                strand = "Production of Goods and Services",
                subStrand = "Factors of Production (Land, Labor, Capital, Entrepreneurship) & Rewards",
                knowledge = "Define production, utility (form, place, time), list factors of production, and state their respective rewards (rent, wages, interest, profit).",
                skill = "Identify how an entrepreneur mobilizes land, labor, and capital to start a poultry or bakery business in the community.",
                attitude = "Recognize and value the dignified contribution of all forms of labor in the economy.",
                inquiryQuestion = "How does entrepreneurial innovation coordinate labor and capital to satisfy consumer demands efficiently?",
                experiences = "Learners trace the production chain of bread from wheat farming to retail shops, tabulate factors of production and rewards, and roleplay.",
                resources = "Production chain diagrams, Factor-reward matching cards, Business case studies.",
                assessment = "Production chain analysis test, Matching exercise, Written business studies exam."
            ),
            SubStrandData(
                strand = "Entrepreneurship",
                subStrand = "Characteristics of an Entrepreneur, Business Idea Generation & Feasibility",
                knowledge = "Describe traits of successful entrepreneurs (risk-taking, innovative, persistent) and explain sources of business ideas.",
                skill = "Conduct a SWOT analysis (Strengths, Weaknesses, Opportunities, Threats) for a proposed school canteen or stationery shop.",
                attitude = "Cultivate initiative, self-reliance, resilience, and ethical commercial leadership.",
                inquiryQuestion = "How does SWOT analysis help an entrepreneur choose viable and profitable business ventures?",
                experiences = "Learners interview local business owners, compile entrepreneur biography profiles, and formulate SWOT matrices for small businesses in pairs.",
                resources = "SWOT analysis templates, Local entrepreneur case study booklets, Interview questionnaires.",
                assessment = "SWOT matrix evaluation rubric, Entrepreneur interview report, Written quiz."
            ),
            SubStrandData(
                strand = "Consumer Protection and Ethics",
                subStrand = "Consumer Rights, Responsibilities & Unethical Business Practices",
                knowledge = "State fundamental consumer rights (right to safety, information, choice, refund), responsibilities, and identify deceptive business practices (hoarding, false ads).",
                skill = "Inspect product expiry dates, evaluate warranty certificates, and draft a formal consumer complaint letter for defective goods.",
                attitude = "Champion honesty, fair trade, consumer awareness, and zero tolerance for counterfeit products.",
                inquiryQuestion = "How can consumers protect themselves against misleading advertisements and substandard commodities?",
                experiences = "Learners scrutinize KEBS standardization marks on product packaging, roleplay resolving a defective goods dispute, and write complaint letters.",
                resources = "Product packaging samples with KEBS marks, Consumer protection act summary, Sample complaint letters.",
                assessment = "Consumer complaint letter grading rubric, Packaging audit checklist, Written test."
            ),
            SubStrandData(
                strand = "Marketing and Sales",
                subStrand = "The Marketing Mix (4Ps: Product, Price, Place, Promotion) & Customer Service",
                knowledge = "Explain the 4Ps of marketing and outline principles of exemplary customer care and retention.",
                skill = "Design an attractive promotional sales poster and set competitive pricing strategies for a handmade handicraft product.",
                attitude = "Embrace courtesy, customer empathy, ethical advertising, and professional presentation.",
                inquiryQuestion = "How does a well-balanced marketing mix attract and retain loyal customers in a competitive market?",
                experiences = "Learners develop marketing strategies for a school art exhibition, create digital/hand-drawn promotional flyers, and pitch products.",
                resources = "Promotional flyer templates, Colored markers, Marketing mix charts, Sample product prototypes.",
                assessment = "Marketing campaign presentation rubric, 4Ps strategy worksheet, Peer evaluation."
            ),
            SubStrandData(
                strand = "Office and Record Keeping",
                subStrand = "Basic Bookkeeping: Source Documents (Invoices, Receipts) & Cash Book",
                knowledge = "Identify common business source documents (receipts, invoices, delivery notes) and explain the layout of a two-column cash book.",
                skill = "Write accurate sales receipts and record cash and bank transactions in a two-column cash book.",
                attitude = "Exhibit meticulous accuracy, honesty, transparency, and accountability in financial bookkeeping.",
                inquiryQuestion = "How does systematic bookkeeping prevent financial mismanagement and business insolvency?",
                experiences = "Learners fill out blank invoice and receipt booklets for simulated transactions and balance a sample two-column cash book.",
                resources = "Sample receipt books, Invoice pads, Cash book ledger sheets, Calculation worksheets.",
                assessment = "Cash book ledger recording test, Source document completion check, Written exam."
            )
        )
    }
}
