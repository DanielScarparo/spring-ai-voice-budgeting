# 🤖 AI Budgeting Assistant — Spring AI & Clean Architecture

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4+-6DB33F?style=for-the-badge&logo=spring)
![Spring AI](https://img.shields.io/badge/Spring_AI-1.0.0--M4-6DB33F?style=for-the-badge&logo=spring)
![Groq](https://img.shields.io/badge/Groq_Cloud-Llama_3.3_70B-f38020?style=for-the-badge)
![MySQL](https://img.shields.io/badge/MySQL-JPA%20%2F%20Hibernate-4479A1?style=for-the-badge&logo=mysql)
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Clean Architecture](https://img.shields.io/badge/Architecture-Clean%20%2F%20Hexagonal-blue?style=for-the-badge)

Uma API REST inteligente para controle financeiro pessoal que permite registrar gastos através de **mensagens de voz (áudio)** ou texto, além de gerar relatórios e somatórias automáticas conversando diretamente com uma Inteligência Artificial.

O projeto aplica estritamente os conceitos de **Arquitetura Limpa (Clean Architecture)** e **Domain-Driven Design (DDD)**, integrando o ecossistema do **Spring Boot** com modelos de linguagem de ponta via **Spring AI**, rodando sobre uma infraestrutura conteinerizada com **Docker Compose**.

---

## 🚀 Funcionalidades Principais

* 🎙️ **Transcrição de Áudio (Speech-to-Text):** Processamento de arquivos de áudio (`.m4a`) com transcrição automática utilizando o modelo **Whisper Large v3**.
* 🧠 **Tool Calling (Chamada de Ferramentas Java via IA):** O modelo de IA analisa a intenção do usuário e aciona autonomamente métodos Java anotados com `@Tool` para consultar ou persistir dados.
* 📊 **Cálculo Acumulado por Categoria:** Nova funcionalidade implementada com `@Query` customizada no Spring Data JPA e tratamento com `COALESCE` para retorno seguro de somatórios diretamente na base de dados.
* 💬 **Endpoint Híbrido (Áudio e Texto):** Flexibilidade total para interagir com a aplicação via microfone ou chat de texto puro.
* 🐳 **Infraestrutura Conteinerizada:** Banco de dados MySQL isolado e pré-configurado via Docker Compose para execução instantânea do ambiente local.

---

## 🏗️ Arquitetura Limpa (Clean Architecture)

A aplicação foi desenhada para manter a regra de negócio completamente isolada de frameworks, bancos de dados ou APIs externas:

    src/main/java/dio/budgeting/
     ├── domain/                    # Núcleo da aplicação (Zero dependências externas)
     │    ├── Category.java         # Enum com as categorias (GROCERIES, PHARMA, AUTO)
     │    ├── Transaction.java      # Entidade de domínio
     │    └── TransactionRepository.java # Contrato (Interface) de acesso aos dados
     │
     ├── application/               # Casos de Uso (Regras de Negócio & Spring AI @Tool)
     │    ├── PersistTransactionUseCase.java
     │    ├── ListTransactionsByCategoryUseCase.java
     │    ├── GetTotalBalanceByCategoryUseCase.java # 💡 Nova tool de somatória de saldo
     │    └── output/BalanceOutput.java
     │
     └── infrastructure/            # Adaptadores externos (Web, JPA, IA)
          ├── http/                 # Controllers REST e DTOs HTTP
          └── persistence/          # Implementação JPA/Hibernate do repositório MySQL

---

## 🛠️ Decisões Técnicas e a Jornada com a IA

Durante o desenvolvimento deste projeto, enfrentamos desafios reais de arquitetura de software, custos e limitações de APIs em nuvem que exigiram refatorações estratégicas:

### 1. Por que não seguimos com a configuração original do projeto?
O projeto base propunha arquiteturas ou modelos que não se adequavam à melhor relação de performance, custo e acessibilidade. Testamos alternativas como o **Google Gemini**, mas esbarramos em burocracias de plataformas de nuvem (como a exigência obrigatória de cadastro de cartão de crédito e faturamento para liberar requisições da série Pro/Flash mais recentes no AI Studio).

Para manter este projeto **100% gratuito, open-source e acessível** para qualquer desenvolvedor rodar localmente sem cadastrar cartões de crédito, optamos pela nuvem da **Groq Cloud**.

### 2. A escolha do modelo: `llama-3.3-70b-versatile` na Groq
Iniciamos os testes utilizando o modelo menor (`llama-3.1-8b-instant`). No entanto, o recurso de **Tool Calling do Spring AI** envia a definição completa e os metadados de todas as classes Java anotadas com `@Tool` a cada requisição para "ensinar" o modelo a usar o sistema.
* **O Problema:** Isso gerou um consumo elevado de tokens por requisição, estourando rapidamente o teto de *Rate Limit* (6.000 TPM - Tokens Por Minuto) do modelo 8B e gerando erros `HTTP 429 (Resource Exhausted)`.
* **A Solução:** Migramos para o **`llama-3.3-70b-versatile`** na Groq. Além de possuir um limite de tokens infinitamente superior e mais robusto, é um modelo de 70 bilhões de parâmetros com um raciocínio muito mais sofisticado, compreendendo parâmetros de Enums Java e contextos financeiros com precisão absoluta.

### 3. Estratégia de Testes Isolados (A superação da falta de tokens)
Durante a fase de testes, validamos com sucesso que a IA escuta os áudios gravados (`recording-1.m4a`, etc.) usando o **Whisper v3**, converte para texto e aciona o banco de dados. 

Porém, para evitar o desperdício de banda e o consumo desnecessário de cotas de áudio ao testar exaustivamente novas regras de negócio (como o cálculo de saldo acumulado), **aplicamos o padrão de Isolamento de Testes**:
1. Criamos um novo endpoint REST (`/transactions/ai/chat`) focado exclusivamente em texto puro.
2. Com isso, provamos que a transcrição de áudio (STT) funciona na camada de entrada, enquanto os testes de regressão das ferramentas `@Tool` rodam via texto em milissegundos e sem estourar nenhum token!

---

## ⚙️ Como Configurar e Rodar o Projeto

### Pré-requisitos
* **Java SDK 21** ou superior
* **Docker** e **Docker Compose** instalados
* Chave de API gratuita da **[Groq Cloud](https://console.groq.com/)**

### 1. Subir o Banco de Dados com Docker Compose
Para evitar a necessidade de instalar e configurar um servidor MySQL localmente, o projeto já conta com uma infraestrutura conteinerizada. 

O arquivo `docker-compose.yml` na raiz do projeto está estruturado da seguinte forma:

    services:
      db:
        image: mysql:8.0
        container_name: budgeting-mysql
        restart: always
        environment:
          MYSQL_DATABASE: budgeting
          MYSQL_ROOT_PASSWORD: root
        ports:
          - "3306:3306"
        volumes:
          - mysql_data:/var/lib/mysql

    volumes:
      mysql_data:

Para iniciar o contêiner do banco de dados em segundo plano, abra o terminal na pasta do projeto e execute:

    docker compose up -d

### 2. Configurar a Variável de Ambiente
Defina a variável de ambiente no seu sistema ou na sua IDE com a chave gerada na Groq:

    export GROQ_API_KEY="gsk_sua_chave_aqui..."

### 3. Configurações do `application.properties`
Certifique-se de que o arquivo `src/main/resources/application.properties` está apontando para o banco local rodando no Docker e configurado para o modelo 70B:

    spring.application.name=budgeting
    spring.datasource.url=jdbc:mysql://localhost:3306/budgeting
    spring.datasource.username=root
    spring.datasource.password=root
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true

    # IA Principal / Tool Calling (Llama 3.3 70B na Groq)
    spring.ai.openai.chat.base-url=https://api.groq.com/openai
    spring.ai.openai.chat.api-key=${GROQ_API_KEY}
    spring.ai.openai.chat.options.model=llama-3.3-70b-versatile
    spring.ai.openai.chat.options.temperature=0.2

    # Transcrição de Áudio STT (Whisper Large v3 na Groq)
    spring.ai.openai.audio.transcription.base-url=https://api.groq.com/openai
    spring.ai.openai.audio.transcription.api-key=${GROQ_API_KEY}
    spring.ai.openai.audio.transcription.options.model=whisper-large-v3
    spring.ai.openai.audio.transcription.options.language=pt
    spring.ai.openai.audio.transcription.options.response-format=text

    # Desativação de TTS para evitar erros de autoconfiguração do Spring
    spring.ai.openai.audio.speech.enabled=false
    spring.ai.openai.api-key=${GROQ_API_KEY}

### 4. Executar a Aplicação
Com o Docker rodando o MySQL, inicie o Spring Boot com o comando:

    ./gradlew bootRun

---

## 🧪 Como Testar (Roteiro Completo de Validação)

Você pode utilizar o HTTP Client integrado do IntelliJ IDEA (ou o VS Code REST Client) com o roteiro abaixo. Ele prova o funcionamento da nossa IA registrando dois gastos independentes e, em seguida, acionando a nossa nova ferramenta para calcular o somatório na base de dados:

    ### 1. [TEXTO] IA registra o primeiro gasto no Mercado (R$ 80,00)
    POST http://localhost:8080/transactions/ai/chat
    Content-Type: text/plain

    Gastei 80 reais no mercado fazendo compras de casa.

    ###

    ### 2. [TEXTO] IA registra o segundo gasto no Mercado (R$ 200,00)
    POST http://localhost:8080/transactions/ai/chat
    Content-Type: text/plain

    Acabei de gastar mais 200 reais no mercado com carnes e bebidas.

    ###

    ### 3. [A HORA DA VERDADE] A IA aciona o novo @Tool e calcula o saldo total!
    ### O Llama 3.3 70B raciocina, chama GetTotalBalanceByCategoryUseCase, roda o SUM() no MySQL e responde.
    POST http://localhost:8080/transactions/ai/chat
    Content-Type: text/plain

    Qual é o meu saldo total acumulado de gastos na categoria GROCERIES?

    ###

    ### 4. [AUDITORIA] Confirmação direta via JPA das transações salvas nas tabelas
    GET http://localhost:8080/transactions/GROCERIES
    Accept: application/json

**Resultado Esperado no Passo 3:**
> *"O seu saldo total acumulado na categoria mercado (GROCERIES) é de R$ 280,00."*

---

## 📝 Licença
Este projeto foi desenvolvido para fins didáticos e de portfólio técnico como evolução do bootcamp de Java e Inteligência Artificial da DIO.
