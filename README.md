# 💳 Meu Banco Digital – Sistema de Gerenciamento Bancário

> Sistema de gerenciamento de clientes, contas, cartões e seguros para uma instituição financeira, com validações, regras de negócio e integração com APIs.

---

## 🧠 Contexto

Este projeto é parte da solução oferecida por uma startup de tecnologia para uma instituição financeira, com foco na criação de um sistema web de cadastro e gerenciamento de clientes, contas, cartões e seguros.

A aplicação permite operações bancárias como:

- Cadastro de clientes
- Abertura de contas (Corrente/Poupança)
- Emissão de cartões (Crédito/Débito)
- Realização de transações (PIX, transferências)
- Gerenciamento de seguros de cartão
- Validações rigorosas e aplicação de regras de negócio

---

## ⚙️ Tecnologias Utilizadas

- **Java 19** – Linguagem principal
- **Spring Boot** – Framework backend
- **JDBC Puro** – Acesso direto ao banco de dados
- **MySQL** – Banco de dados relacional
- **Swagger/OpenAPI** – Documentação interativa
- **Maven** – Gerenciamento de dependências e build
- **Thymeleaf** – (Opcional) para interface baseada em servidor
- **Bootstrap** – Estilização e responsividade (frontend)
- **Arquitetura Hexagonal (Ports & Adapters)** – Isolamento de domínio e infraestrutura
- **APIs externas** – ViaCEP (endereço), Microserviço de E-mail
- **RabbitMQ** - Sistema fila/evento

---


## ✉️ Integração com Microserviço de E-mail

Ao cadastrar um cliente ou emitir um cartão, o sistema envia um evento (via REST ou fila/evento) para o microserviço de e-mail, que trata o envio assíncrono de mensagens para os usuários.


## 📜 Regras de Negócio

### 🧾 Cadastro de Clientes
- CPF único com validação via API (formato `xxxxxxxxxxx`)
- Nome: Apenas letras e espaços (2–100 caracteres)
- Data de nascimento: formato `DD/MM/AAAA`, apenas maiores de 18 anos
- Endereço completo com validação de CEP utilizando API viacep (`xxxxxxxx`)
- Categorias: **Comum**, **Super**, **Premium**

### 🏦 Contas Bancárias

**Tipos de conta:**
- Corrente: cobra taxa de manutenção mensal
- Poupança: rendimento mensal com base em juro composto

| Tipo de Cliente | Taxa Corrente (R$) | Rendimento Poupança (%)  |
|-----------------|--------------------|--------------------------|
| Comum           | 12,00              | 0,5% ao ano              |
| Super           | 8,00               | 0,7% ao ano              |
| Premium         | Isenta             | 0,9% ao ano              |

### 💸 Operações Bancárias

- Consultar saldo
- Efetuar PIX
- Aplicar Rendimento conta Poupança
- Aplicar Taxa em conta corrente
- Transferências entre contas
- Depósitos

### 💳 Cartões (Crédito/Débito)

**Cartão de Crédito:**
- Limites: Comum (R$1.000), Super (R$5.000), Premium (R$10.000)
- Bloqueio de pagamentos ao atingir limite
- Operações: alterar senha, ativar/desativar, efetuar pagamento de fatura

**Cartão de Débito:**
- Limite diário ajustável
- Bloqueia pagamentos após atingir o limite
- Operações: ativar/desativar, trocar senha, consultar saldo

---

## 📋 Funcionalidades

### 📌 Menu Principal
1. Cadastro de Cliente
2. Abertura de Conta
3. Operações Conta Corrente/Poupança
4. Cartões de Crédito/Débito
5. Listar Clientes
0. Sair

### 👤 Cadastro de Cliente
- Validações: campos vazios, CPF, data de nascimento, CEP
- Verificação de duplicidade

### 🏦 Abertura de Conta
- Verifica cadastro do cliente
- Seleção entre conta corrente e/ou poupança

### 🔁 Operações Conta Corrente/Poupança
1. Exibir contas
2. PIX
3. Transferência entre contas
4. Exibir saldo
5. Efetuar depósito

### 💳 Cartões
- Consulta de cartões existentes
- Emissão e gestão de cartão de crédito/débito
- Operações:
    - Alterar senha/limite
    - Ativar/desativar


### 📋 Listagem de Clientes
- Exibe todos os clientes cadastrados
- Edição e exclusão de dados

---

## 🚧 Status do Projeto

> 🛠️ Em Desenvolvimento
- Taxa de 5% sobre gastos mensais caso exceda 80% do limite


---

## 🚀 Como Executar

### 1. Clone o repositório:

```bash
git clone https://https:/github.com/Mikallina/BancoDigitalJDBC
