package com.example.consultas.controller.painel;

import com.example.consultas.entity.ResultadoConsulta;
import com.example.consultas.repository.ResultadoConsultaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resultados/sigapce")  //http://localhost:8080/api/resultados/sigapce/graph/sigapce-30-dias
public class ResultadoConsultaSIGAPCEController {

    private final ResultadoConsultaRepository repository;

    public ResultadoConsultaSIGAPCEController(ResultadoConsultaRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/page/{tipo}")
    public Page<ResultadoConsulta> buscarMaisRecentePorTipo(@PathVariable String tipo) {
        Pageable pageable = PageRequest.of(0, 1); // primeira página, tamanho 1
        return repository.findByTipoConsultaOrderByDataExecucaoDesc(tipo, pageable);
    }

    @GetMapping("/graph/{tipo}")
    public String buscarMaisRecentePorTipoGrafico(@PathVariable String tipo) {
        return repository.findTopResultadoJsonByTipo(tipo);  // Usando o método com @Query
    }

}
