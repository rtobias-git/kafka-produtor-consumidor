# Aplicacao Produtor e Consumidor usando Apache Kafka - Fila de Mensagens

Projeto desenvolvido para a disciplina de Sistemas Distribuidos, implementando o padrao de comunicacao assincrona Pub/Sub (Publish-Subscribe) utilizando Apache Kafka no modo KRaft (sem Zookeeper), com servicos conteinerizados via Docker e Docker Compose.

---

## 1. Visao Geral da Arquitetura

O sistema e composto por tres elementos centrais conectados em uma rede bridge dedicada:

- **Broker de Mensagens (Apache Kafka 3.9.1)**: Executando em modo KRaft (Kafka Raft Metadata Mode), responsavel por receber, persistir e ordenar as mensagens no topico `fila-pedidos`.
- **Produtor de Pedidos (Python 3.12)**: Aplica a biblioteca `confluent-kafka` para serializar e despachar 10 pedidos culinarios sequenciais ao broker, recebendo confirmacoes de entrega (callbacks de delivery report).
- **Consumidor de Pedidos (Java 21)**: Utiliza a biblioteca oficial `kafka-clients` (3.8.1) para subscrever o topico `fila-pedidos`, operando em loop de polling contínuo para processar cada mensagem recebida.

---

## 2. Demonstracao de Execucao

### Inicializacao do Broker Apache Kafka
Exibicao do conteiner do Kafka ativo e ouvindo conexoes na porta 9092.

![Broker Apache Kafka](docs/screenshots/01-kafka-broker.png)

---

### Execucao do Produtor (Python)
Envio ordenado dos 10 pedidos para o topico `fila-pedidos` com confirmacao do offset de cada mensagem gravada no broker.

![Execucao do Produtor](docs/screenshots/02-produtor-envio.png)

---

### Execucao do Consumidor (Java)
Processamento e exibicao em tempo real de cada pedido retirado da fila pelo consumidor Java pertencente ao grupo consumidor `grupo-simples`.

![Execucao do Consumidor](docs/screenshots/03-consumidor-recebimento.png)

---

### Topologia de Comunicacao
Visao geral do fluxo assincrono dos dados e do isolamento por rede Docker.

![Topologia de Comunicacao](docs/screenshots/04-arquitetura-kafka.png)

---

## 3. Imagens Publicadas no Docker Hub

As imagens de cada servico foram compiladas e estao disponiveis publicamente no Docker Hub:

| Servico | Imagem Docker Hub | Versao |
| :--- | :--- | :--- |
| Produtor (Python) | `tobiasrafaelrosa/kafka-fila-produtor:1.0` | `1.0` / `latest` |
| Consumidor (Java) | `tobiasrafaelrosa/kafka-fila-consumidor:1.0` | `1.0` / `latest` |

---

## 4. Estrutura do Projeto

```text
ProjetoSistemasDistribuidos/
├── docker-compose.yml          # Orquestracao do broker Apache Kafka em modo KRaft
├── .gitignore                  # Arquivos ignorados pelo Git
├── README.md                   # Documentacao completa da solucao
├── docs/
│   └── screenshots/            # Evidencias visuais de execucao dos servicos
├── Produtor/
│   ├── Dockerfile              # Imagem base Python 3.12-slim com confluent-kafka
│   └── produtor.py             # Script de publicacao de pedidos no topico
└── Consumidor/
    ├── Dockerfile              # Multi-stage build com Maven 3.9 e Eclipse Temurin 21
    ├── pom.xml                 # Gerenciamento de dependencias (kafka-clients 3.8.1)
    └── src/main/java/br/edu/ifsp/pep/consumidor/
        └── Consumidor.java     # Classe principal de escuta e consumo de mensagens
```

---

## 5. Como Executar

### Pre-requisitos
- Docker instalado (versao 20.10 ou superior)
- Docker Compose instalado

---

### Passo 1: Inicializar o Broker Kafka

Na raiz do projeto, suba o conteiner do Kafka:

```bash
docker compose up -d
```

Verifique se o broker esta em execucao:

```bash
docker compose ps
```

---

### Passo 2: Executar o Consumidor (Java)

Em um terminal, inicie o consumidor conectado a rede do Kafka:

```bash
docker run --rm -it --network net-kafka tobiasrafaelrosa/kafka-fila-consumidor:1.0
```

O consumidor ficara aguardando o envio de novas mensagens:
```text
Consumidor esperando pedidos...
```

---

### Passo 3: Executar o Produtor (Python)

Em outro terminal, execute o produtor para despachar os 10 pedidos:

```bash
docker run --rm -it --network net-kafka tobiasrafaelrosa/kafka-fila-produtor:1.0
```

O produtor exibira o envio e a confirmacao de cada item na fila:
```text
Produtor colocando pedidos na fila...
Produtor enviou: Pedido 1: pizza
  broker guardou na posicao 0 da fila
...
Produtor acabou, os 10 pedidos estao na fila do broker
```

Automaticamente, o terminal do consumidor recebera e exibira os 10 itens processados.

---

### Passo 4: Encerrar o Ambiente

Para parar o broker Kafka:

```bash
docker compose down
```
