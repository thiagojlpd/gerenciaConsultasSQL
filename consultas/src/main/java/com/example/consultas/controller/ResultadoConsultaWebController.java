package com.example.consultas.controller;

import com.example.consultas.entity.ResultadoConsulta;
import com.example.consultas.repository.ResultadoConsultaRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.itextpdf.layout.Document;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.element.*;

import java.io.IOException;
import java.time.LocalDate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class ResultadoConsultaWebController {

    @Autowired
    private ResultadoConsultaRepository repository;

    @GetMapping("/inicio")
    public String index() {
        return "index"; // Vai buscar templates/index.html
    }

    @RequestMapping("/web/resultados")
    public String listarComFiltro(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("dataExecucao").descending());

        Page<ResultadoConsulta> resultadosPaginados;

        if (tipo != null && data != null) {
            resultadosPaginados = repository.findByTipoConsultaContainingAndDataExecucaoBetween(
                    tipo,
                    data.atStartOfDay(),
                    data.plusDays(1).atStartOfDay(),
                    pageable
            );
        } else if (tipo != null) {
            resultadosPaginados = repository.findByTipoConsultaContaining(tipo, pageable);
        } else if (data != null) {
            resultadosPaginados = repository.findByDataExecucaoBetween(
                    data.atStartOfDay(),
                    data.plusDays(1).atStartOfDay(),
                    pageable
            );
        } else {
            resultadosPaginados = repository.findAll(pageable);
        }

        model.addAttribute("resultados", resultadosPaginados.getContent());
        model.addAttribute("pagina", resultadosPaginados);
        model.addAttribute("filtroTipo", tipo);
        model.addAttribute("filtroData", data != null ? data.toString() : "");

        return "resultados";
    }

    @GetMapping("/export/excel")
    public void exportarExcel(HttpServletResponse response) throws IOException {
        List<ResultadoConsulta> resultados = repository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Resultados");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Tipo");
        header.createCell(1).setCellValue("Data Execução");
        header.createCell(2).setCellValue("Resultado JSON");

        int rowNum = 1;
        for (ResultadoConsulta res : resultados) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(res.getTipoConsulta());
            row.createCell(1).setCellValue(res.getDataExecucao().toString());
            row.createCell(2).setCellValue(res.getResultadoJson());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=resultados.xlsx");
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    @GetMapping("/export/pdf")
    public void exportarPdf(HttpServletResponse response) throws IOException {
        List<ResultadoConsulta> resultados = repository.findAll();

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=resultados.pdf");

        PdfDocument pdf = new PdfDocument(new PdfWriter(response.getOutputStream()));
        Document doc = new Document(pdf);

        doc.add(new Paragraph("📄 Resultados da Consulta").setBold().setFontSize(16));
        for (ResultadoConsulta res : resultados) {
            doc.add(new Paragraph("Tipo: " + res.getTipoConsulta()));
            doc.add(new Paragraph("Data: " + res.getDataExecucao().toString()));
            doc.add(new Paragraph("Resultado JSON: " + res.getResultadoJson()));
            doc.add(new Paragraph(" "));
        }

        doc.close();
    }
}
