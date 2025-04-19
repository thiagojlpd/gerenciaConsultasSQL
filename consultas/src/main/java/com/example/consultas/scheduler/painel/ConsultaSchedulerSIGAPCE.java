package com.example.consultas.scheduler.painel;

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
public class ConsultaSchedulerSIGAPCE {

    @Autowired
    private ResultadoConsultaRepository repository;

    @Autowired
    @Qualifier("origemSIGAPCEJdbcTemplate")
    private JdbcTemplate origemSIGAPCEJdbcTemplate;

    private void executarConsulta(String tipoConsulta, String sql) {
        List<Map<String, Object>> resultado = origemSIGAPCEJdbcTemplate.queryForList(sql);
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

    @Scheduled(cron = "0 */10 * * * *")
    public void consultasACada10Minutos() {
        System.out.println("[Agora] SIGAPCE-ANO-CORRENTE: " + LocalDateTime.now());
        executarConsulta("sigapce-ano-corrente", "SET lc_time_names = 'pt_BR'; -- Define o idioma para português\n" +
                "\n" +
                "SELECT \n" +
                "    COUNT(*) AS quantidade,  -- Conta a quantidade de processos abertos em cada data\n" +
                "    DATE_FORMAT(dt_abertura_processo, '%d/%m/%Y') AS data_abertura,  -- Formata a data de abertura do processo no formato 'dd/mm/aaaa'\n" +
                "    DATE_FORMAT(dt_abertura_processo, '%W') AS dia_semana  -- Retorna o nome do dia da semana da data de abertura (ex: \"Segunda-feira\")\n" +
                "FROM processo  -- Especifica a tabela 'processo' para realizar a consulta\n" +
                "WHERE dt_abertura_processo >= DATE_FORMAT(NOW(), '%Y-01-01')  -- Filtra os processos abertos a partir do primeiro dia do ano atual\n" +
                "GROUP BY DATE(dt_abertura_processo)  -- Agrupa os resultados por data de abertura (ignorando o horário)\n" +
                "ORDER BY DATE(dt_abertura_processo) DESC;  -- Ordena os resultados pela data de abertura, do mais recente para o mais antigo\n");

        System.out.println("[Agora] SIGAPCE-30-DIAS: " + LocalDateTime.now());
        executarConsulta("sigapce-30-dias", "SET lc_time_names = 'pt_BR'; -- Define o idioma para português\n" +
                "\n" +
                "SELECT \n" +
                "    COUNT(*) AS quantidade,  \n" +
                "    DATE_FORMAT(DATE(dt_abertura_processo), '%d/%m/%Y') AS data_abertura\n" +
                "FROM processo\n" +
                "WHERE DATE(dt_abertura_processo) > CURDATE() - INTERVAL 30 DAY\n" +
                "GROUP BY data_abertura\n" +
                "ORDER BY DATE(dt_abertura_processo) DESC;\n");

        System.out.println("[Agora] SIGAPCE-ANO-CORRENTE: " + LocalDateTime.now());
        executarConsulta("sigapce-com-problemas-30-dias", "SET lc_time_names = 'pt_BR';  \n" +
                "\n" +
                "SELECT \n" +
                "    u.nm_unidade AS om, -- Seleciona o nome da unidade (nm_unidade) e o renomeia como 'om'\n" +
                "    \n" +
                "    COUNT(*) AS total, -- Conta o total de processos para cada unidade (sem nenhuma condição)\n" +
                "\n" +
                "    COUNT(CASE \n" +
                "        WHEN pa.id_processo_status NOT IN (3,7,15,16,18,21) -- Filtra os processos que não possuem em seu último status um dos tipos sem alteração\n" +
                "        THEN 1 -- Conta 1 para processos que atendem a essa condição\n" +
                "    END) AS sem_alteracao, -- Resultado é a quantidade de processos sem alteração (status não está em (3,7,15,16,18,21))\n" +
                "\n" +
                "    COUNT(CASE \n" +
                "        WHEN pa.id_processo_status IN (3,7,15,16,18,21) -- Filtra os processos que possuem em seu último status um dos tipos com alteração\n" +
                "        THEN 1 -- Conta 1 para processos que atendem a essa condição\n" +
                "    END) AS com_alteracao, -- Resultado é a quantidade de processos com alteração (status está em (3,7,15,16,18,21))\n" +
                "\n" +
                "    CONCAT(ROUND((COUNT(CASE \n" +
                "        WHEN pa.id_processo_status IN (3,7,15,16,18,21) -- Filtra os processos que contém em seu último status um dos tipos com alteração\n" +
                "        THEN 1 -- Conta 1 para processos com alteração\n" +
                "    END) / COUNT(*)) * 100, 2), ' %') AS percentual_com_alteracao -- Calcula o percentual de processos com alteração (com alteração / total) e limita a 2 casas decimais, concatenando o símbolo de porcentagem\n" +
                "\n" +
                "FROM processo p -- Tabela de processos (p)\n" +
                "JOIN processo_andamento pa ON p.id_processo = pa.id_processo -- Junta a tabela processo_andamento (pa) para trazer informações sobre o andamento do processo\n" +
                "JOIN unidade u ON p.id_unidade = u.id_unidade -- Junta a tabela unidade (u) para trazer o nome da unidade (nm_unidade)\n" +
                "\n" +
                "WHERE \n" +
                "p.dt_abertura_processo >= DATE_SUB(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) + 1 DAY), INTERVAL 30 DAY) -- Filtra os processos abertos nos últimos 30 dias a partir do último domingo (data de abertura do processo)\n" +
                "\n" +
                "AND \n" +
                "    pa.dt_processo_andamento = ( -- Filtra para pegar o último andamento de cada processo\n" +
                "        SELECT MAX(pa2.dt_processo_andamento) -- Subconsulta que retorna a maior data de andamento para cada processo\n" +
                "        FROM processo_andamento pa2 -- Tabela de andamentos de processos (pa2)\n" +
                "        WHERE pa2.id_processo = pa.id_processo -- Condição que garante que estamos buscando o andamento do mesmo processo\n" +
                "    )\n" +
                "\n" +
                "GROUP BY u.nm_unidade -- Agrupa os resultados pelo nome da unidade (nm_unidade), para contar processos por unidade\n" +
                "\n" +
                "ORDER BY percentual_com_alteracao DESC; -- Ordena os resultados pelo percentual de processos com alteração de forma decrescente (do maior para o menor)\n");

    }

    @Scheduled(cron = "0 0 22 * * SUN")
    public void consultaAosDomingos() {
        System.out.println("[Agora] SIGAPCE-EM-ABERTO-DOMINGOS: " + LocalDateTime.now());
        executarConsulta("sigapce-em-aberto-domingos", "SET lc_time_names = 'pt_BR';  \n" +
                "\n" +
                "SELECT \n" +
                "    s.ds_servico AS servico, -- Seleciona o nome do serviço e renomeia para 'servico'\n" +
                "\n" +
                "    -- Conta os processos abertos até o último domingo\n" +
                "    COUNT(CASE \n" +
                "            WHEN p.dt_abertura_processo <= DATE_SUB(CURDATE(), INTERVAL (WEEKDAY(CURDATE()) + 1) DAY) \n" +
                "            THEN 1 ELSE NULL \n" +
                "        END) AS quantidade_ultimo_domingo,\n" +
                "\n" +
                "    -- Conta os processos abertos até o penúltimo domingo\n" +
                "    COUNT(CASE \n" +
                "            WHEN p.dt_abertura_processo <= DATE_SUB(DATE_SUB(CURDATE(), INTERVAL (WEEKDAY(CURDATE()) + 1) DAY), INTERVAL 7 DAY) \n" +
                "            THEN 1 ELSE NULL \n" +
                "        END) AS quantidade_penultimo_domingo,\n" +
                "\n" +
                "    -- Conta os processos abertos até o antepenúltimo domingo\n" +
                "    COUNT(CASE \n" +
                "            WHEN p.dt_abertura_processo <= DATE_SUB(DATE_SUB(CURDATE(), INTERVAL (WEEKDAY(CURDATE()) + 1) DAY), INTERVAL 14 DAY) \n" +
                "            THEN 1 ELSE NULL \n" +
                "        END) AS quantidade_antepenultimo_domingo,\n" +
                "\n" +
                "    -- Conta os processos abertos até o domingo anterior ao antepenúltimo domingo\n" +
                "    COUNT(CASE \n" +
                "            WHEN p.dt_abertura_processo <= DATE_SUB(DATE_SUB(CURDATE(), INTERVAL (WEEKDAY(CURDATE()) + 1) DAY), INTERVAL 21 DAY) \n" +
                "            THEN 1 ELSE NULL \n" +
                "        END) AS quantidade_domingo_anterior_antepenultimo_domingo\n" +
                "\n" +
                "FROM \n" +
                "    processo p -- Tabela principal que contém os processos\n" +
                "JOIN processo_servico ps ON p.id_processo = ps.id_processo -- Junta com a tabela intermediária 'processo_servico' para associar processos e serviços\n" +
                "JOIN servico s ON ps.id_servico = s.id_servico -- Junta com a tabela 'servico' para obter o nome do serviço\n" +
                "JOIN processo_andamento pa ON p.id_processo = pa.id_processo -- Junta com 'processo_andamento' para obter informações do andamento do processo\n" +
                "\n" +
                "WHERE \n" +
                "    -- Filtra para considerar apenas o último andamento registrado para cada processo\n" +
                "    pa.dt_processo_andamento = ( \n" +
                "        SELECT MAX(pa2.dt_processo_andamento) -- Obtém a maior data de andamento do processo\n" +
                "        FROM processo_andamento pa2 \n" +
                "        WHERE pa2.id_processo = pa.id_processo\n" +
                "    )\n" +
                "    \n" +
                "    -- Filtra para incluir apenas processos com status em aberto\n" +
                "    AND pa.id_processo_status IN (1, 2, 4, 5, 12, 14, 15, 16, 20, 23, 24)\n" +
                "\n" +
                "-- Agrupa os resultados por serviço para contar os processos por tipo de serviço\n" +
                "GROUP BY s.ds_servico;\n");
    }

}
