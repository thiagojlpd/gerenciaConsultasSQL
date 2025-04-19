package com.example.consultas.controller.painel;

import com.example.consultas.entity.ResultadoConsulta;
import com.example.consultas.repository.ResultadoConsultaRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping
public class ResultadoConsultaSIGAPCEWebController {

    @Autowired
    private ResultadoConsultaRepository repository;

    @GetMapping("/painel")
    public String painel() {
        return "painel/painel"; // Vai buscar templates/index.html
    }

    @GetMapping("/painel_30dias")
    public String painel30Dias() {
        return "painel/painel_30dias"; // Vai buscar templates/index.html
    }

    @GetMapping("/painel_ano_corrente")
    public String painelAnoCorrentes() {
        return "painel/painel_ano_corrente"; // Vai buscar templates/index.html
    }

    @GetMapping("/painel_com_problemas_30dias")
    public String painelComProblemas30Dias() {
        return "painel/painel_com_problemas_30dias"; // Vai buscar templates/index.html
    }

    @GetMapping("/painel_em_aberto_domingos")
    public String painelEmAbertoDomingos() {
        return "painel/painel_em_aberto_domingos"; // Vai buscar templates/index.html
    }

}
