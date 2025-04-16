
# Agendador de Consultas SQL com Spring Boot

Este projeto consiste em um agendador de consultas SQL utilizando Spring Boot. O agendador executa consultas em um banco de dados de origem e armazena os resultados em um banco de dados de destino no formato JSON. Ele também fornece uma API para consultar os resultados das execuções.

## Funcionalidades

- Agendamento de consultas SQL em horários específicos.
- Armazenamento dos resultados das consultas no banco de dados de destino como JSON.
- API para consultar os resultados das consultas armazenados.
- Exemplos de consultas agendadas: semanalmente, diariamente e mensalmente.

## Tecnologias

- Java 17
- Spring Boot 2.x
- MySQL
- JPA (Hibernate)

## Como executar

### Pré-requisitos

- JDK 17 ou superior.
- Banco de dados MySQL.

### Configuração

1. Clone o repositório:

   ```bash
   git clone https://github.com/seu-usuario/agendador-consultas.git
   ```

2. Configure os dados de acesso ao banco de dados no arquivo `application.properties`:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/seu_banco_destino
   spring.datasource.username=root
   spring.datasource.password=root
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   ```

3. Execute a aplicação:

   ```bash
   mvn spring-boot:run
   ```

4. A API estará disponível em `http://localhost:8080`.

## API

### Consultar resultados de consulta

- **Endpoint:** `/resultados/{tipoConsulta}`
- **Método:** `GET`
- **Descrição:** Retorna os resultados das consultas realizadas, filtrados pelo tipo de consulta.

Exemplo de resposta:

```json
{
  "id": 1,
  "tipoConsulta": "consulta-teste",
  "dataExecucao": "2025-04-15T20:00:00",
  "resultadoJson": "{"campo1": "valor1", "campo2": "valor2"}"
}
```

## Licença

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

MIT License

Copyright (c) 2025 [Seu Nome ou Organização]

Permissão é concedida, gratuitamente, a qualquer pessoa que obtenha uma cópia deste software e dos arquivos de documentação associados (o "Software"), para negociar no Software sem restrição, incluindo, sem limitação, os direitos de usar, copiar, modificar, fundir, publicar, distribuir, sublicenciar e/ou vender cópias do Software, e para permitir que as pessoas a quem o Software é fornecido o façam, sujeito às seguintes condições:

A acima descrição de direitos e a seguinte nota de isenção deverão ser incluídas em todas as cópias ou partes substanciais do Software.

O Software é fornecido "no estado em que se encontra", sem qualquer garantia de qualquer tipo, expressa ou implícita, incluindo, mas não se limitando a garantias de comercialização, adequação a um propósito específico ou não violação. Em nenhum caso os autores ou detentores dos direitos autorais serão responsáveis por qualquer reclamação, dano ou outra responsabilidade, seja em uma ação de contrato, ato ilícito ou outro, decorrente de, ou em conexão com, o Software ou o uso ou outros negócios no Software.
