package com.example.consultas.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "resultados_consulta")
public class ResultadoConsulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tipoConsulta;

    private LocalDateTime dataExecucao;

    @Column(columnDefinition = "json")
    private String resultadoJson;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTipoConsulta() { return tipoConsulta; }
    public void setTipoConsulta(String tipoConsulta) { this.tipoConsulta = tipoConsulta; }

    public LocalDateTime getDataExecucao() { return dataExecucao; }
    public void setDataExecucao(LocalDateTime dataExecucao) { this.dataExecucao = dataExecucao; }

    public String getResultadoJson() { return resultadoJson; }
    public void setResultadoJson(String resultadoJson) { this.resultadoJson = resultadoJson; }
}
