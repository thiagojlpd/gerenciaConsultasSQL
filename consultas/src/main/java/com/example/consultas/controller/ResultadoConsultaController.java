package com.example.consultas.controller;

import com.example.consultas.entity.ResultadoConsulta;
import com.example.consultas.repository.ResultadoConsultaRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resultados")
public class ResultadoConsultaController {

    private final ResultadoConsultaRepository repository;

    public ResultadoConsultaController(ResultadoConsultaRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{tipo}")
    public List<ResultadoConsulta> listarPorTipo(@PathVariable String tipo) {
        return repository.findTop10ByTipoConsultaOrderByDataExecucaoDesc(tipo);
    }

}
