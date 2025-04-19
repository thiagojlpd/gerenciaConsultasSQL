package com.example.consultas.repository;

import com.example.consultas.entity.ResultadoConsulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ResultadoConsultaRepository extends JpaRepository<ResultadoConsulta, Long> {

    List<ResultadoConsulta> findTop10ByTipoConsultaOrderByDataExecucaoDesc(String tipoConsulta);

    Page<ResultadoConsulta> findByTipoConsultaOrderByDataExecucaoDesc(String tipoConsulta, Pageable pageable);

    Page<ResultadoConsulta> findByTipoConsultaContaining(String tipo, Pageable pageable);

    Page<ResultadoConsulta> findByDataExecucaoBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<ResultadoConsulta> findByTipoConsultaContainingAndDataExecucaoBetween(
            String tipo,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    @Query("SELECT r.resultadoJson FROM ResultadoConsulta r WHERE r.tipoConsulta = :tipo ORDER BY r.dataExecucao DESC")
    String findTopResultadoJsonByTipo(@Param("tipo") String tipo);
}
