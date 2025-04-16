package com.example.consultas.repository;

import com.example.consultas.entity.ResultadoConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResultadoConsultaRepository extends JpaRepository<ResultadoConsulta, Long> {
    List<ResultadoConsulta> findTop10ByTipoConsultaOrderByDataExecucaoDesc(String tipoConsulta);
}
