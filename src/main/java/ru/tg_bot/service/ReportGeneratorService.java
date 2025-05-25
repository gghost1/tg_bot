package ru.tg_bot.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.tg_bot.model.form.Form;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportGeneratorService {
    private final FormService formService;

    public byte[] generateReport() throws IOException {
        try (XWPFDocument document = new XWPFDocument();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            XWPFTable table = document.createTable();
            table.setWidth("100%");
            createHeaderRow(table);

            int pageNumber = 0;
            Page<Form> page;
            do {
                page = formService.getAll(pageNumber);
                addDataRows(table, page.getContent());
                pageNumber++;
            } while (page.hasNext());

            document.write(out);
            return out.toByteArray();
        }
    }

    private void createHeaderRow(XWPFTable table) {
        XWPFTableRow headerRow = table.getRow(0);
        if (headerRow == null) {
            headerRow = table.createRow();
        }

        String[] headers = {"Имя", "Email", "Оценка"};
        for (int i = 0; i < headers.length; i++) {
            if (i != headers.length - 1) {
                headerRow.createCell();
            }

            setCellText(headerRow.getCell(i), headers[i], true);
        }

        setHeaderStyle(headerRow);
    }

    private void addDataRows(XWPFTable table, List<Form> forms) {
        for (Form form : forms) {
            XWPFTableRow row = table.createRow();
            setCellText(row.getCell(0), form.getName(), false);
            setCellText(row.getCell(1), form.getEmail(), false);
            setCellText(row.getCell(2), String.valueOf(form.getMark()), false);
        }
    }

    private void setCellText(XWPFTableCell cell, String text, boolean isBold) {
        if (cell == null) return;

        cell.removeParagraph(0);
        XWPFParagraph paragraph = cell.addParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(isBold);
    }

    private void setHeaderStyle(XWPFTableRow row) {
        for (XWPFTableCell cell : row.getTableCells()) {
            cell.setVerticalAlignment(XWPFTableCell.XWPFVertAlign.CENTER);

            CTTcPr tcPr = cell.getCTTc().addNewTcPr();
            tcPr.addNewTcBorders().addNewBottom().setVal(STBorder.SINGLE);
            tcPr.addNewTcBorders().addNewTop().setVal(STBorder.SINGLE);
            tcPr.addNewTcBorders().addNewLeft().setVal(STBorder.SINGLE);
            tcPr.addNewTcBorders().addNewRight().setVal(STBorder.SINGLE);
        }
    }

}
