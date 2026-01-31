/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.biruarenrollmentsystem;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import java.awt.Color;
import java.io.FileOutputStream;
import java.awt.Desktop;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class Reports extends BiruarEnrollmentSystem{
    public void createStudReport(int studid, String uName){
        try {
            String dest = "report"+".pdf";

            // Create document and writer
            Document document = new Document(PageSize.LEGAL);
            PdfWriter.getInstance(document, new FileOutputStream(dest));
            document.open();

            // Header with Logo + Text side by side, centered as one block
            PdfPTable headerTable = new PdfPTable(2); //2 columns
            headerTable.setTotalWidth(300); // adjust width of the whole block
            headerTable.setWidths(new int[]{1, 3}); // logo smaller, text wider
            headerTable.setLockedWidth(true); // lock the width
            headerTable.setHorizontalAlignment(Element.ALIGN_CENTER); // center the table on page
            headerTable.setSpacingAfter(6f);

            // Logo cell
            try {
                Image logo = Image.getInstance("logo.png");
                logo.scaleToFit(60, 60);

                PdfPCell logoCell = new PdfPCell(logo); //everytime u create a cell just use this
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT); //not sure what this does
                logoCell.setVerticalAlignment(Element.ALIGN_RIGHT);
                headerTable.addCell(logoCell);
                //if no logo
            } catch (Exception e) {
                PdfPCell emptyLogo = new PdfPCell(new Phrase(""));
                emptyLogo.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(emptyLogo);
                System.out.println("No logo found, skipping logo.");
            }

            // Text cell
            PdfPCell textCell = new PdfPCell();
            Paragraph headerText = new Paragraph("College of Adu Ban Kal\nRegistrar's Office",
                    FontFactory.getFont(FontFactory.TIMES, 11));
            headerText.setAlignment(Element.ALIGN_LEFT);

            textCell.addElement(headerText);
            textCell.setBorder(Rectangle.NO_BORDER);
            textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(textCell);

            // Add header to document
            document.add(headerTable);

            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            // Title
            Paragraph title = new Paragraph("Student Grade Sheet\n",
                    FontFactory.getFont(FontFactory.TIMES_BOLD, 12));
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(6f);
            document.add(title);

            //document.add(Chunk.NEWLINE);

            // Student Info
            //fix use a table
         
            PdfPTable studInfoTable = new PdfPTable(2);
            studInfoTable.setWidthPercentage(100);
            //studInfoTable.setTotalWidth(300);
            
            //for id info
            Paragraph idPara = new Paragraph(String.format("Student ID: %d", studid), FontFactory.getFont(FontFactory.TIMES_BOLD));
            //for School Year
            Paragraph syPara = new Paragraph(String.format("School Year: %s", db), FontFactory.getFont(FontFactory.TIMES_BOLD));
            //for name info
            Paragraph namePara = new Paragraph(String.format("Student Name: %s", uName), FontFactory.getFont(FontFactory.TIMES_BOLD));
            //for course and name info
            String courseQuery = "SELECT course FROM students WHERE studid = " + studid + ";";
            String course = "";
            try{
                rs = st.executeQuery(courseQuery);
                while(rs.next()){
                    course = rs.getString("Course");
                }
            }
            catch(SQLException e){
                System.out.println(e);
            }
            Paragraph coursePara = new Paragraph(String.format("Student Course: %s", course), FontFactory.getFont(FontFactory.TIMES_BOLD));
            //for studyear
            String yearQuery = "SELECT Yearlvl FROM students WHERE studid = " + studid + ";";
            String year = "";
            try{
                rs = st.executeQuery(yearQuery);
                while(rs.next()){
                    year = rs.getString("Yearlvl");
                }
            }
            catch(SQLException e){
                System.out.println(e);
            }
            Paragraph yearInfoPara = new Paragraph(String.format("Student Year: %s", year), FontFactory.getFont(FontFactory.TIMES_BOLD));
            //for
            Paragraph empty = new Paragraph("");
            
            Paragraph[] a = {idPara, syPara, namePara, coursePara, yearInfoPara, empty};
            for(Paragraph b:a){
                PdfPCell idParaCell = new PdfPCell(b);
                idParaCell.setBorder(Rectangle.NO_BORDER);
                studInfoTable.addCell(idParaCell);
            }
            
            document.add(studInfoTable);

            document.add(Chunk.NEWLINE);
            
            
            String query = String.format(
                    "SELECT e.subjid, "
                    + "s.subjcode, "
                    + "g.prelim, "
                    + "g.midterm, "
                    + "g.prefinal, "
                    + "g.final "
                    + "FROM enroll e "
                    + "JOIN subjects s ON s.subjid = e.subjid "
                    + "LEFT JOIN grades g ON g.eid = e.eid "
                    + "WHERE e.studid = %d;",
                    studid
            );
            
            /*
            String query = String.format("SELECT e.subjid,\n"
                    + "       (SELECT s.subjcode FROM subjects s WHERE s.subjid = e.subjid) AS subjcode,\n"
                    + "       (SELECT g.prelim   FROM grades g WHERE g.eid = e.eid) AS prelim,\n"
                    + "       (SELECT g.midterm  FROM grades g WHERE g.eid = e.eid) AS midterm,\n"
                    + "       (SELECT g.prefinal FROM grades g WHERE g.eid = e.eid) AS prefinal,\n"
                    + "       (SELECT g.final    FROM grades g WHERE g.eid = e.eid) AS final\n"
                    + "FROM enroll e\n"
                    + "WHERE e.studid = %d;", studid);
            */
            // Table with 6 columns
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            //table.setWidths(columnWidths);
            table.setSpacingAfter(10f); 
            
            PdfPTable table2 = new PdfPTable(6);
            table2.setWidthPercentage(100);
            table2.setSpacingBefore(0f);
            //table2.setWidths(columnWidths);
            
            // Table headers
            String[] headers = {"SubjID", "Subj Code", "Prelim", "Midterm", "Prefinal", "Final"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.TIMES_BOLD)));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }
            
            int count = 0;
            
            try{
                rs = st.executeQuery(query);
                while(rs.next()){
                    String subjid = rs.getString("subjid");
                    String subjcode = rs.getString("subjcode");
                    String prelim = rs.getString("prelim") /*== null ? "null" :rs.getString("prelim") FALLBACK */ ;
                    String midterm = rs.getString("midterm");
                    String prefinal = rs.getString("prefinal");
                    String finals = rs.getString("final");
                   
                    //for links
                    Object[] set = {subjid, subjcode, prelim, midterm, prefinal, finals};
                    if(finals != null){
                        String dbName = this.db;
                        String encodedDbName = Base64.getEncoder().encodeToString(dbName.getBytes());
                        String studidStr = String.valueOf(studid);
                        String encodedStudId = Base64.getEncoder().encodeToString(studidStr.getBytes());
                        String encodedSubjId = Base64.getEncoder().encodeToString(subjid.getBytes());

                        //the link
                        String cgiUrl = "http://localhost/eval.py";
                        String fullCgiUrl = String.format("%s?s_id=%s&subj_id=%s&db_name=%s",
                                cgiUrl,
                                encodedStudId,
                                encodedSubjId,
                                encodedDbName);
                        
                        Anchor subjLink = new Anchor(subjid, FontFactory.getFont(FontFactory.TIMES, 10f));
                        
                        subjLink.setReference(fullCgiUrl);
                        subjLink.getFont().setColor(Color.blue);
                        subjLink.getFont().setStyle(Font.UNDERLINE);
                        set[0] = subjLink;
                    }
                    
                    for(Object item:set){
                        PdfPCell cell2; 
                        if (item instanceof Anchor) {
                            Phrase linkPhrase = new Phrase();
                            linkPhrase.add((Anchor) item);
                            cell2 = new PdfPCell(linkPhrase);
                        } else {
                            
                            String text = (item == null) ? "" : String.valueOf(item); // Handle potential nulls
                            cell2 = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.TIMES)));
                        }

                        cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table2.addCell(cell2); // Add the correctly created cell to the table
                    }
                    count++;
                }
                
                document.add(table);
                //document.add(new Paragraph("\n"));
                document.add(table2);
                
            }
            catch(SQLException ex){
                System.out.println(ex);
            }
            

            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("Number of Subjects Listed: " + count, FontFactory.getFont(FontFactory.TIMES_BOLD, 11)));

            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            // Registrar signature
            PdfPTable regisTable = new PdfPTable(2);
            
            regisTable.setWidthPercentage(100);
            Paragraph registrar = new Paragraph("_________________________");
            registrar.setAlignment(Element.ALIGN_RIGHT);
            
            PdfPCell regisCell = new PdfPCell(registrar);
            regisCell.setBorder(Rectangle.NO_BORDER);
            regisCell.setHorizontalAlignment(Element.ALIGN_RIGHT); //not sure what this does
            regisCell.setVerticalAlignment(Element.ALIGN_RIGHT); 
            
            PdfPCell cellNull = new PdfPCell(new Paragraph(""));
            cellNull.setBorder(Rectangle.NO_BORDER);
            
            regisTable.addCell(cellNull);
            regisTable.addCell(regisCell);
            regisTable.addCell(cellNull);
            
            Paragraph regisWord = new Paragraph("                                      Registrar", FontFactory.getFont(FontFactory.TIMES_BOLD, 11)); //forced
            regisWord.setAlignment(Element.ALIGN_CENTER);
            
            PdfPCell textCellRegis = new PdfPCell(regisWord);
            textCellRegis.setBorder(Rectangle.NO_BORDER);
            textCellRegis.setHorizontalAlignment(Element.ALIGN_CENTER);
            regisTable.addCell(textCellRegis);
            
            document.add(regisTable);
            
            document.close();
            System.out.println("PDF created with OpenPDF 3.0.0: " + dest);

            // Auto-open PDF after creation
            File pdfFile = new File(dest);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void createReportRoot(int studid, String uName, String course, String gender){
        //try to do without ai
        try {
            //only fix
            var localDate = LocalDateTime.now();
            String dest = studid + "_" + localDate.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))+".pdf";

            // Create document and writer
            Document document = new Document(PageSize.LEGAL);
            PdfWriter.getInstance(document, new FileOutputStream(dest));
            document.open();

            // ✅ Header with Logo + Text side by side, centered as one block
            PdfPTable headerTable = new PdfPTable(2); //2 columns
            headerTable.setTotalWidth(300); // adjust width of the whole block
            headerTable.setWidths(new int[]{1, 3}); // logo smaller, text wider
            headerTable.setLockedWidth(true); // lock the width
            headerTable.setHorizontalAlignment(Element.ALIGN_CENTER); // center the table on page
            headerTable.setSpacingAfter(6f);

            // Logo cell
            try {
                Image logo = Image.getInstance("logo.png");
                logo.scaleToFit(60, 60);

                PdfPCell logoCell = new PdfPCell(logo); //everytime u create a cell just use this
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setHorizontalAlignment(Element.ALIGN_RIGHT); //not sure what this does
                logoCell.setVerticalAlignment(Element.ALIGN_RIGHT);
                headerTable.addCell(logoCell);
                //if no logo
            } catch (Exception e) {
                PdfPCell emptyLogo = new PdfPCell(new Phrase(""));
                emptyLogo.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(emptyLogo);
                System.out.println("No logo found, skipping logo.");
            }

            // Text cell
            PdfPCell textCell = new PdfPCell();
            Paragraph headerText = new Paragraph("College of Adu Ban Kal\nRegistrar's Office",
                    FontFactory.getFont(FontFactory.TIMES, 11));
            headerText.setAlignment(Element.ALIGN_LEFT);

            textCell.addElement(headerText);
            textCell.setBorder(Rectangle.NO_BORDER);
            textCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(textCell);

            // Add header to document
            document.add(headerTable);

            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            // Title
            Paragraph title = new Paragraph("Official Transcript of Records\n",
                    FontFactory.getFont(FontFactory.TIMES_BOLD, 12));
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(6f);
            document.add(title);
            
            //fix use a table
            PdfPTable studInfoTable = new PdfPTable(2);
            studInfoTable.setWidthPercentage(100);
            //studInfoTable.setTotalWidth(300);

            //for id info
            Paragraph idPara = new Paragraph(String.format("Student ID: %d", studid), FontFactory.getFont(FontFactory.TIMES_BOLD));
            //for School Name
            Paragraph studName = new Paragraph(String.format("Student Name: %s", uName), FontFactory.getFont(FontFactory.TIMES_BOLD));
            //for course and name info
            Paragraph coursePara = new Paragraph(String.format("Student Course: %s", course), FontFactory.getFont(FontFactory.TIMES_BOLD));
            //for studyear
            Paragraph genderPara = new Paragraph(String.format("Gender: %s", gender), FontFactory.getFont(FontFactory.TIMES_BOLD));
            
            Paragraph[] a = {idPara, studName, coursePara, genderPara};
            for(Paragraph b:a){
                PdfPCell idParaCell = new PdfPCell(b);
                idParaCell.setBorder(Rectangle.NO_BORDER);
                studInfoTable.addCell(idParaCell);
            }
            
            studInfoTable.setSpacingAfter(6f);
            
            document.add(studInfoTable);
            
            PdfPTable schoolRecordTable = new PdfPTable(5);
            schoolRecordTable.setWidthPercentage(100);
            
            Paragraph subjId = new Paragraph("Subjid", FontFactory.getFont(FontFactory.TIMES_BOLD));
            Paragraph subjCode = new Paragraph("Subj Code", FontFactory.getFont(FontFactory.TIMES_BOLD));
            Paragraph descTitle = new Paragraph("Descriptive Title", FontFactory.getFont(FontFactory.TIMES_BOLD));
            Paragraph finalPara = new Paragraph("Final", FontFactory.getFont(FontFactory.TIMES_BOLD));
            Paragraph credit = new Paragraph("Credit", FontFactory.getFont(FontFactory.TIMES_BOLD));
            
            Paragraph[] schoolHead = {subjId,subjCode,descTitle,finalPara, credit};
            for(Paragraph ab:schoolHead){
                ab.setAlignment(Element.ALIGN_CENTER);
                PdfPCell cell = new PdfPCell(ab);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                schoolRecordTable.addCell(cell);
            }
            
            document.add(schoolRecordTable);
            
            
            
            LinkedList <String> dbName = new LinkedList<String>();
            
            final String listAySchemasSql = """
                 SELECT schema_name as databaseNames
                 FROM information_schema.schemata as databaseNames       
                 WHERE schema_name REGEXP '^(1st|2nd|summer)_sy[0-9]{4}_[0-9]{4}$' ORDER BY databaseNames;
                """;
            try{
                rs = st.executeQuery(listAySchemasSql);
                
                while(rs.next()){
                    
                    String potDb = rs.getString("databaseNames");
                    String dbAddQuery = String.format("SELECT studid, name FROM `%s`.Students WHERE studid = %d AND Name = '%s';", potDb, studid, uName);
                    Statement tempSt = con.createStatement();
                    ResultSet tempRs = tempSt.executeQuery(dbAddQuery);

                    if(tempRs.next()){
                        dbName.add(potDb);
                    }
                    tempRs.close();
                    tempSt.close();
                }
            }
            catch(SQLException ex){
                System.out.println("SQLEXception: " + ex);
            }
            
            // UNION ALL
            StringBuilder queryBuilder = new StringBuilder();
            int i = 0;
            for (String db : dbName) {
                if (i > 0) {
                    queryBuilder.append(" UNION ALL "); //union all
                }
                String term = db.split("_sy")[0];  // "1st", "2nd", "summer"
                int termOrder;
                switch (term) {
                    case "1st":
                        termOrder = 1;
                        break;
                    case "2nd":
                        termOrder = 2;
                        break;
                    case "summer":
                        termOrder = 3;
                        break;
                    default:
                        termOrder = 99; // fallback
                }
                String schoolYr = db.replaceAll(".*_sy", "").replaceAll("_", ""); // "20252026"
                queryBuilder.append(String.format(
                        "SELECT '%s' AS dbName, %s as schoolYr, %d as term, e.subjid, s.SubjCode, s.SubjDesc, "
                        + "COALESCE(g.final, 'No Grade') AS Final, s.SubjUnits "
                        + "FROM `%s`.enroll e "
                        + "JOIN `%s`.subjects s ON e.subjid = s.subjid "
                        + "LEFT JOIN `%s`.grades g ON g.eid = e.eid "
                        + "WHERE e.studid = %d",
                        db, schoolYr, termOrder, db, db, db, studid
                ));
                i++;
            }
            queryBuilder.append(" ORDER BY schoolYr, term;");
            String finalQuery = queryBuilder.toString();

            // Execute UNION ALL query
            PdfPTable infoTable = new PdfPTable(5);
            infoTable.setWidthPercentage(100);
            int counter = 0;
            try {
                rs = st.executeQuery(finalQuery);
                String previousDb = null;
                while (rs.next()) {
                    String db = rs.getString("dbName");
                    String subjid = rs.getString("subjid");
                    String subjcode = rs.getString("SubjCode");
                    String subjDesc = rs.getString("SubjDesc");
                    String finalGrade = rs.getString("Final") == null ? "" : rs.getString("Final");
                    String credit2 = rs.getString("SubjUnits");

                    // add db header if new
                    if (previousDb == null || !db.equals(previousDb)) {
                        Phrase phrase = new Phrase();
                        Chunk chunk = new Chunk(db, FontFactory.getFont(FontFactory.TIMES_BOLD, 10));
                        chunk.setUnderline(0.1f, -3f);
                        phrase.add(chunk);

                        PdfPCell dbCell = new PdfPCell(phrase);
                        dbCell.setColspan(5);
                        dbCell.setBorder(Rectangle.NO_BORDER);
                        infoTable.addCell(dbCell);
                    }
                    previousDb = db; //dont really need

                    // add normal row
                    String[] rowSet = {subjid, subjcode, subjDesc, finalGrade, credit2};
                    for (String set : rowSet) {
                        PdfPCell cell = new PdfPCell(new Phrase(set, FontFactory.getFont(FontFactory.TIMES)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(Rectangle.NO_BORDER);
                        infoTable.addCell(cell);
                    }
                    counter++;
                }
            } catch (SQLException ex) {
                System.out.println("Query Exception: " + ex);
            }

            // Add total count row
            PdfPCell emptyCell = new PdfPCell(new Phrase(""));
            emptyCell.setColspan(5);
            emptyCell.setBorder(Rectangle.NO_BORDER);
            infoTable.addCell(emptyCell);
            
            //document.add(infoTable);
            
            Phrase phrase = new Phrase("Number of Subjects Listed: " + counter, FontFactory.getFont(FontFactory.TIMES_BOLD, 12));
            PdfPCell countCell = new PdfPCell(phrase);
            countCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            countCell.setBorder(Rectangle.NO_BORDER);
            countCell.setColspan(5);
            
            infoTable.addCell(countCell);
            
            document.add(infoTable);
            
            PdfPTable gradingTable = new PdfPTable(2);
            gradingTable.setWidthPercentage(100);
            gradingTable.setSpacingBefore(10f);

            // Left column (Old Grading System)
            String leftText
                    = "Grading System up to SY 1981-82\n\n"
                    + "95-100 = 1.0 = Excellent\n"
                    + "90-94 = 1.5 = Very Good\n"
                    + "85-89 = 2.0 = Good\n"
                    + "80-84 = 2.5 = Fair\n"
                    + "75-79 = 3.0 = Passed\n"
                    + "Below 75 = 5.0 = Failed\n";

            PdfPCell leftCell = new PdfPCell(new Phrase(leftText, FontFactory.getFont(FontFactory.TIMES, 12)));
            leftCell.setBorder(Rectangle.TOP);
            gradingTable.addCell(leftCell);

            // Right column (New Grading System)
            String rightText
                    = "New Grading System (1st Sem. SY1982-83 & up)\n\n"
                    + "95-100 = 1.0 = Excellent\n"
                    + "90-94 = 1.5 = Very Good\n"
                    + "85-89 = 2.0 = Good\n"
                    + "80-84 = 2.5 = Fair\n"
                    + "75-79 = 3.0 = Passed\n"
                    + "Below 75 = 5.0 = Failed\n";

            PdfPCell rightCell = new PdfPCell(new Phrase(rightText, FontFactory.getFont(FontFactory.TIMES, 12)));
            rightCell.setBorder(Rectangle.TOP);
            gradingTable.addCell(rightCell);

            document.add(gradingTable);

            Paragraph fcfGrades = new Paragraph(
                    "FOR FCF Grades: O - Outstanding, HS - Highly Satisfactory, MS - Moderately Satisfactory, "
                    + "S - Satisfactory, F - Fair, P - Poor\n"
                    + "Quality Point Equivalent: 1.0 = 4.0, 1.5 = 3.5, 2.0 = 3.0, 2.5 = 2.5, 3.0 = 2.0",
                    FontFactory.getFont(FontFactory.TIMES, 11)
            );
            fcfGrades.setSpacingBefore(4f);
            document.add(fcfGrades);

            Paragraph sealNote = new Paragraph("NOT VALID WITHOUT SCHOOL SEAL",
                    FontFactory.getFont(FontFactory.TIMES_BOLD, 11));
            sealNote.setAlignment(Element.ALIGN_LEFT);
            sealNote.setSpacingBefore(20f);
            document.add(sealNote);
            
            // signatories
            PdfPTable signTable = new PdfPTable(2);
            signTable.setWidthPercentage(100);
            signTable.setSpacingBefore(20f);

            // Prepared By (with underline)
            Chunk preparedChunk = new Chunk("Prepared By: MARY ANN D. MATURAN",
                    FontFactory.getFont(FontFactory.TIMES, 12));
            preparedChunk.setUnderline(0.5f, -2f); // thickness, y-position
            PdfPCell prepared = new PdfPCell(new Phrase(preparedChunk));
            prepared.setBorder(Rectangle.NO_BORDER);
            prepared.setHorizontalAlignment(Element.ALIGN_LEFT);

            // Checked By (with underline)
            Chunk checkedChunk = new Chunk("Checked By: MYRNA VILLACOSA",
                    FontFactory.getFont(FontFactory.TIMES, 12));
            checkedChunk.setUnderline(0.5f, -2f);
            PdfPCell checked = new PdfPCell(new Phrase(checkedChunk));
            checked.setBorder(Rectangle.NO_BORDER);
            checked.setHorizontalAlignment(Element.ALIGN_LEFT);

            // Format today's date
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            String formattedDate = today.format(formatter);

            // Date (no underline)
            Paragraph datePara = new Paragraph("Date: " + formattedDate,
                    FontFactory.getFont(FontFactory.TIMES, 12));
            datePara.setAlignment(Element.ALIGN_LEFT);
            PdfPCell dateCell = new PdfPCell(datePara);
            dateCell.setBorder(Rectangle.NO_BORDER);
            dateCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            dateCell.setPaddingTop(25f); 

            // Registrar (with underline)
            Chunk registrarChunk = new Chunk("Registrar: ATTY. EDGAR ESCALANTE",
                    FontFactory.getFont(FontFactory.TIMES, 12));
            registrarChunk.setUnderline(0.5f, -2f);
            PdfPCell registrar = new PdfPCell(new Phrase(registrarChunk));
            registrar.setBorder(Rectangle.NO_BORDER);
            registrar.setHorizontalAlignment(Element.ALIGN_LEFT);
            registrar.setPaddingTop(25f); 
            
            

            // Add to table
            signTable.addCell(prepared);
            signTable.addCell(checked);
            signTable.addCell(dateCell);
            signTable.addCell(registrar);
            document.add(signTable);
            
            document.close();
            
            // Auto-open PDF after creation
            File pdfFile = new File(dest);
            if (pdfFile.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }   
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
