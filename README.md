# OndaCare — Um Passo à Frente do Calor (API RESTful)

> **FIAP · Engenharia de Software · Global Solution 2026 · CitizenTech**  
> **Disciplina:** Domain Driven Design - Java

API RESTful desenvolvida em **Java 21** e **Spring Boot** conectada ao banco de dados **Oracle (FIAP)** para o acompanhamento preventivo e check-in ativo de idosos independentes que moram sozinhos em dias de calor extremo.

---

## Integrantes do Grupo (Turma 2ESPH)

| Nome Completo | RM |
| :--- | :--- |
| Eduardo Delorenzo Moraes | 561749 |
| Kauê de Almeida Pena | 564211 |
| Ronaldo Aparecido Monteiro Almeida | 565017 |
| William Queiroz | 565032 |

---

## Sobre a Solução

O **OndaCare** atua de forma proativa nos dias de alerta de onda de calor (INMET) na cidade cadastrada do idoso:

1. **Aviso Preventivo na Véspera:** Exibe orientações de hidratação/ventilação e indica o ponto fresco mais próximo (como a UBS de referência no CNES), com distância a pé e horário de funcionamento.
2. **Check-in Ativo:** Dispara alarme em tela cheia em dois horários definidos pela família (`"Tudo bem por aí?"`) com as opções **`"Tô bem"`** e **`"Preciso de ajuda"`**.
3. **Escada de Confirmação:** Caso o idoso não responda após repetições a cada 20 minutos durante 1 hora, o caso sobe para a **Família** (`"Falei, tá bem"`, `"Não consegui falar"`, `"Não posso agora"` ou `"Saiu, confiro depois"` com reavaliação em 2 horas) e, sem confirmação ou quando não há família cadastrada, entra por ordem de prioridade no painel de pendentes da **UBS** do bairro.
4. **Privacidade (LGPD):** Não armazena diagnósticos clínicos, medicamentos nem rastreamento GPS contínuo.

---

## Tecnologias e Dependências

* **Java JDK 21**
* **Spring Boot 4.1.2**
  * Spring Web (`spring-boot-starter-webmvc`)
  * Spring Data JDBC (`spring-boot-starter-data-jdbc`)
  * Bean Validation (`spring-boot-starter-validation`)
  * Lombok (`lombok`)
  * Spring Boot DevTools (`spring-boot-devtools`)
* **Oracle Database FIAP** (`ojdbc17`)
* **Apache Maven** (Maven Wrapper `mvnw` / `mvnw.cmd` incluso)
* **Docker** (para deploy no Render.com)

---

## Arquitetura do Projeto

```text
ondacare/
├── Dockerfile
├── pom.xml
├── schema_oracle.sql
└── src/main/java/br/com/fiap/citizentech/ondacare/
    ├── OndacareApplication.java
    ├── controller/   # Endpoints RESTful (GET, POST, PUT, DELETE)
    ├── bo/           # Regras de negócio (escada, prioridade UBS, consentimento)
    ├── dao/          # Persistência JDBC no Oracle Database
    ├── model/        # Entidades do domínio
    └── exception/    # Tratamento global de exceções (@RestControllerAdvice)
```

---

## Como Executar Localmente (IntelliJ IDEA)

1. **Clonar o repositório e abrir o projeto:**
   Abra a pasta `ondacare` no **IntelliJ IDEA** selecionando o arquivo `pom.xml` como projeto Maven.
   > *Observação:* Verifique em **Settings > Build, Execution, Deployment > Compiler > Annotation Processors** se a opção **Enable annotation processing** está ativada para o Lombok.

2. **Criar as tabelas no Oracle FIAP:**
   Execute o script `schema_oracle.sql` no **Oracle SQL Developer** conectado ao banco `oracle.fiap.com.br:1521:ORCL`.

3. **Configurar as variáveis de ambiente:**
   Em **Run > Edit Configurations...** na classe `OndacareApplication`, adicione:
   ```env
   ORACLE_URL=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
   ORACLE_USER=seu_rm
   ORACLE_PASSWORD=sua_senha
   ```

4. **Iniciar a aplicação:**
   Execute `OndacareApplication`. A API estará disponível em `http://localhost:8080`.

---

## Endpoints Principais (`GET`, `POST`, `PUT`, `DELETE`)

| Recurso | Método | Endpoint | Descrição |
| :--- | :--- | :--- | :--- |
| **Idosos** | `POST` | `/api/idosos` | Cadastra idoso com consentimento obrigatório e UBS |
| **Idosos** | `GET` | `/api/idosos` | Lista idosos (filtros opcionais `?cidade=` e `?ubsId=`) |
| **Idosos** | `GET` | `/api/idosos/{id}` | Busca idoso por ID |
| **Idosos** | `PUT` | `/api/idosos/{id}` | Atualiza dados cadastrais e horários de check-in |
| **Idosos** | `DELETE` | `/api/idosos/{id}` | Remove cadastro do idoso |
| **Família** | `POST` | `/api/idosos/{idosoId}/familia/convites` | Convida contato para a Família (máx. 2 principais) |
| **Família** | `GET` | `/api/idosos/{idosoId}/familia` | Lista integrantes da Família |
| **Família** | `PUT` | `/api/familia/convites/{conviteId}/responder` | Responde convite (`ACEITO`, `RECUSADO`, `SAIU`) |
| **Família** | `DELETE` | `/api/idosos/{idosoId}/familia/membros/{membroId}` | Remove membro da Família |
| **Alertas** | `POST` | `/api/alertas-calor` | Registra alerta de onda de calor (INMET) |
| **Alertas** | `GET` | `/api/alertas-calor?cidade={cidade}` | Consulta alertas ativos por cidade |
| **Ponto Fresco** | `GET` | `/api/idosos/{idosoId}/ponto-fresco` | Consulta UBS mais próxima, distância e horário |
| **Alertas** | `PUT` | `/api/alertas-calor/{alertaId}` | Atualiza orientações ou status do alerta |
| **Alertas** | `DELETE` | `/api/alertas-calor/{alertaId}` | Remove alerta de calor |
| **Check-in** | `POST` | `/api/checkins` | Agenda/sincroniza check-in do idoso |
| **Check-in** | `GET` | `/api/idosos/{idosoId}/checkins/hoje` | Lista check-ins do idoso |
| **Check-in** | `PUT` | `/api/checkins/{checkinId}/responder` | Registra `TO_BEM`, `PRECISO_DE_AJUDA` ou `SEM_RESPOSTA` |
| **Check-in** | `DELETE` | `/api/checkins/{checkinId}` | Remove check-in |
| **Escalação** | `GET` | `/api/escalacoes/{escalacaoId}` | Consulta estado da escalação |
| **Escalação** | `PUT` | `/api/escalacoes/{escalacaoId}/acao-familia` | Ação da Família (`FALEI_TA_BEM`, `NAO_CONSEGUI_FALAR`, `NAO_POSSO_AGORA`, `SAIU_CONFIRO_DEPOIS`) |
| **UBS** | `GET` | `/api/ubs/{ubsId}/pendentes` | Lista pendentes da UBS ordenados por prioridade |
| **UBS** | `POST` | `/api/ubs/{ubsId}/visitas` | Registra visita (`ESTA_BEM` ou `PRECISOU_DE_ATENDIMENTO`) |
| **UBS** | `GET` | `/api/ubs/{ubsId}/visitas` | Lista visitas registradas pela UBS |

---

## Deploy (Render.com)

1. Conecte este repositório no **Render.com** como **Web Service** utilizando o runtime **Docker** (via `Dockerfile` na raiz).
2. Configure as variáveis de ambiente `ORACLE_URL`, `ORACLE_USER` e `ORACLE_PASSWORD`.
3. Acesse a URL pública gerada em `.onrender.com` (ex.: `GET /api/idosos`).