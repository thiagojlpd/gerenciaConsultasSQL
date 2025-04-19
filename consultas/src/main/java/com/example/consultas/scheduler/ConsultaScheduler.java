package com.example.consultas.scheduler;

import com.example.consultas.entity.ResultadoConsulta;
import com.example.consultas.repository.ResultadoConsultaRepository;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class ConsultaScheduler {

    @Autowired
    private ResultadoConsultaRepository repository;

    @Autowired
    @Qualifier("origemJdbcTemplate")
    private JdbcTemplate origemJdbcTemplate;

    private void executarConsulta(String tipoConsulta, String sql) {
        List<Map<String, Object>> resultado = origemJdbcTemplate.queryForList(sql);
        String json = new Gson().toJson(resultado);

        ResultadoConsulta rc = new ResultadoConsulta();
        rc.setTipoConsulta(tipoConsulta);
        rc.setDataExecucao(LocalDateTime.now());
        rc.setResultadoJson(json);
        System.out.println(json);
        repository.save(rc);
    }

    // ┌───────────── segundo (0–59)
    // │ ┌───────────── minuto (0–59)
    // │ │ ┌───────────── hora (0–23)
    // │ │ │ ┌───────────── dia do mês (1–31)
    // │ │ │ │ ┌───────────── mês (1–12 ou JAN-DEC)
    // │ │ │ │ │ ┌───────────── dia da semana (0–6 ou SUN-SAT, 0=Domingo)
    // │ │ │ │ │ │
    // │ │ │ │ │ │
    //   * * * * * *

    @Scheduled(cron = "0 */1 * * * *")
    public void consultaTeste() {
        System.out.println("[Agora] Executando consulta teste: " + LocalDateTime.now());
        executarConsulta("consulta-teste", "SELECT * FROM tabela_origem");
    }

    @Scheduled(cron = "0 0 22 * * SUN")
    public void consultaDomingo() {
        System.out.println("[Domingo] Executando consulta aos domingos: " + LocalDateTime.now());
        executarConsulta("consulta-domingo", "SELECT * FROM tabela_origem");
    }

    @Scheduled(cron = "0 0 22 * * *")
    public void consultaDiaria() {
        System.out.println("[Diário] Executando consulta diária: " + LocalDateTime.now());
        executarConsulta("consulta_diaria", "SELECT * FROM tabela_origem");
    }

    @Scheduled(cron = "0 0 22 * * *")
    public void consultaUltimoDiaMes() {
        LocalDate hoje = LocalDate.now();
        if (hoje.equals(hoje.withDayOfMonth(hoje.lengthOfMonth()))) {
            System.out.println("[Mensal] Executando consulta mensal: " + LocalDateTime.now());
            executarConsulta("consulta_ultimo_dia", "SELECT * FROM tabela_origem");
        }
    }

}
