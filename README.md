# Sistema do Laboratorio de Enfermagem

Sistema desenvolvido em Java para gerenciar o agendamento de aulas praticas, controle de estoque, preparacao de kits e checklists em laboratorios de enfermagem. O projeto foi elaborado como trabalho academico, aplicando conceitos de engenharia de software, modelagem UML e programacao orientada a objetos.

---

## Integrantes

| Nome | Funcao |
|------|--------|
| Santhiago Figueiredo | Desenvolvimento e Documentacao |
| Lucas Policarpo | Desenvolvimento e Documentacao |
| Samuel Asafe | Desenvolvimento e Documentacao |
| Kayke Augusto | Desenvolvimento e Documentacao |
| Thiago Leonardo | Desenvolvimento e Documentacao |

---

## Sobre o Projeto

O sistema permite o gerenciamento completo de um laboratorio de enfermagem, abrangendo desde o agendamento de aulas praticas pelos professores ate o controle de estoque e preparacao de kits pela equipe tecnica. A coordenacao administra o sistema, aprovando cadastros e resolvendo conflitos.

---

## Atores do Sistema

| Ator | Funcao no Sistema |
|------|-------------------|
| **Professor** | Solicita reservas de laboratorio, solicita kits/insumos antecipadamente, registra o uso/condicoes da sala e cancela agendamentos |
| **Equipe de Laboratorio** | Prepara kits, realiza checklist de materiais/equipamentos, altera e controla o estoque, atualiza a condicao das salas |
| **Coordenacao** | Administra o sistema, aprova cadastros de usuarios, edita dados dos usuarios, gerencia salas e resolve conflitos |
| **Aluno** | Realiza cadastro/login, consulta a agenda de aulas praticas, recebe mensagens/alertas do sistema |

---

## Casos de Uso

| Codigo | Caso de Uso | Ator Principal | Objetivo |
|--------|-------------|----------------|----------|
| UC01 | Agendar Aula Pratica | Professor | Reservar o laboratorio de enfermagem informando data, horario, disciplina e quantidade de alunos |
| UC02 | Solicitar Kit de Insumos | Professor | Requisitar os materiais e equipamentos necessarios vinculados a uma aula previamente agendada |
| UC03 | Realizar Check List de Preparacao | Equipe do Laboratorio | Conferir insumos, testar equipamentos e registrar a liberacao da sala antes do inicio da aula |
| UC04 | Controlar Estoque de Insumos | Equipe do Laboratorio | Dar entrada, registrar saidas/baixas e manter atualizada a quantidade fisica dos materiais |
| UC05 | Aprovar Cadastros e Excecoes | Coordenacao | Validar o acesso de novos usuarios no sistema e aprovar solicitacoes de reserva ou kits feitos fora do prazo |

---

## Descricao Detalhada do Caso de Uso UC01 - Agendar Aula Pratica

### Pre-condicoes
1. O Professor deve estar com o cadastro aprovado pela Coordenacao e logado no sistema
2. O laboratorio desejado deve estar previamente cadastrado no sistema

### Fluxo Principal
1. O Professor acessa o sistema e clica na funcionalidade "Agendar Aula Pratica"
2. O sistema exibe a agenda dos laboratorios
3. O Professor seleciona o laboratorio, a data e o horario desejados
4. O sistema bloqueia temporariamente o horario selecionado e exibe um alerta visual de reserva para impedir agendamentos duplicados (RN02)
5. O Professor preenche as informacoes da aula: disciplina, area tematica, turma e quantidade de alunos
6. O sistema valida se a reserva atende a antecedencia minima de 7 dias (RN01) e se nao ultrapassa a capacidade maxima da sala (RN06)
7. O sistema confirma a reserva, salva os dados e atualiza o calendario de agendamentos
8. O sistema exibe uma mensagem de confirmacao para o Professor e notifica a Equipe do Laboratorio sobre o novo agendamento

### Fluxos Alternativos

**Alternativa 1 - Capacidade Excedida:**
No passo 6, se o numero de alunos ultrapassar a capacidade da sala, o sistema notifica o professor que nao e possivel realizar a reserva e orienta que ele converse com a coordenacao (RN06). A reserva e cancelada.

**Alternativa 2 - Agendamento fora do prazo:**
No passo 6, se a tentativa de reserva ocorrer com menos de 7 dias de antecedencia, o sistema avisa que a solicitacao exige aprovacao. O agendamento e salvo com o status "Pendente" e a Coordenacao e notificada para avaliar a liberacao (RN01).

**Alternativa 3 - Laboratorio em manutencao:**
No passo 3, se a sala ou seus equipamentos estiverem com status de manutencao, o sistema impede a selecao do horario e exibe um alerta de que a reserva nao podera ser realizada (RN07).

### Pos-condicao
A aula estara registrada no calendario geral do sistema (como aprovada ou pendente), a data/horario ficarao indisponiveis para outros professores e os alertas automaticos terao sido disparados para a Equipe de Laboratorio.

---

## Relacionamentos Include e Extend

### Include (Obrigatorio)

**1. Agendar Aula Pratica <<include>> Verificar Disponibilidade**
- Justificativa: E uma verificacao obrigatoria. O sistema nao pode permitir que uma aula seja agendada sem antes checar se a sala e o horario estao livres no calendario, aplicando o bloqueio temporario (conforme RN02).

**2. Solicitar Kit de Insumos <<include>> Verificar Estoque**
- Justificativa: Sempre que o professor solicitar um kit, o sistema precisa obrigatoriamente checar as quantidades disponiveis para garantir que ha material suficiente no laboratorio. Caso o estoque atinja o nivel critico, isso tambem permitira disparar o alerta automatico da RN05.

### Extend (Opcional/Alternativo)

**Aprovar Solicitacao Fora do Prazo <<extend>> Agendar Aula Pratica**
- Justificativa: Esta acao nao acontece sempre. A aprovacao da Coordenacao so sera acionada se o professor tentar agendar o laboratorio ou solicitar kits com menos de 7 dias de antecedencia (RN01 e RN04). Como isso e uma excecao a regra geral, classifica-se perfeitamente como uma extensao.

---

## Regras de Negocio

| Codigo | Regra | Descricao |
|--------|-------|-----------|
| RN01 | Antecedencia Minima | Agendamentos devem ser feitos com no minimo 7 dias de antecedencia. Solicitacoes fora desse prazo sao bloqueadas e exigem aprovacao da Coordenacao |
| RN02 | Bloqueio Anti-Concorrencia | Ao selecionar uma sala e horario, o sistema bloqueia temporariamente a data para outros usuarios e exibe um alerta visual de reserva, impedindo agendamentos duplicados |
| RN03 | Alcada da Coordenacao | Apenas a Coordenacao possui permissao para aprovar cadastros de novos usuarios, gerenciar/excluir salas e autorizar cadastros de kits |
| RN04 | Solicitacao Extemporanea | Pedidos de kits efetuados fora do prazo regulamentar ficam com status "Pendente" e so sao liberados para a Equipe do Laboratorio apos autorizacao manual da Coordenacao |
| RN05 | Alerta de Estoque Minimo | Quando a quantidade de um insumo atingir 25% da capacidade maxima (ou o estoque minimo de seguranca), o sistema notifica automaticamente a Coordenacao e a Equipe do Laboratorio para reposicao |
| RN06 | Validacao de Capacidade | O sistema recusa agendamentos cuja quantidade de alunos cadastrados seja maior do que a capacidade fisica suportada pelo laboratorio |
| RN07 | Suspensao por Manutencao | O registro de status "Em Manutencao" para um equipamento invalida novas reservas na sala e cancela automaticamente as aulas agendadas no periodo, notificando os professores responsaveis |
| RN08 | Checklist Obrigatorio | A liberacao do laboratorio e dos kits para a aula fica condicionada ao preenchimento e conclusao do checklist de verificacao pela Equipe do Laboratorio |
| RN09 | Impedimento Retroativo | O sistema bloqueia qualquer tentativa de reserva de salas ou solicitacao de kits para datas e horarios ja passados |
| RN10 | Confirmacao de Entrega | O professor deve registrar no sistema o recebimento dos materiais e a verificacao da condicao da sala no inicio da aula, validando a entrega feita pela Equipe do Laboratorio |

---

## Requisitos Funcionais

| Codigo | Descricao |
|--------|-----------|
| RF01 | O sistema devera permitir a escolha de salas, kits e horarios |
| RF02 | O sistema devera permitir modo de cor do sistema (modo claro e modo escuro) |
| RF03 | O sistema devera permitir o cadastro de usuario e login |
| RF04 | O sistema devera permitir cancelar a reserva de salas, kits e horarios |
| RF05 | O sistema devera permitir deletar usuarios |
| RF06 | O sistema devera permitir registrar checklist |
| RF07 | O sistema devera permitir consultar historico de reservas de salas e horarios utilizados |
| RF08 | O sistema devera permitir consultar historico de checklists |
| RF09 | O sistema devera permitir consultar agenda das salas |
| RF10 | O sistema devera permitir gerenciar estoque de insumos |

---

## Requisitos Nao Funcionais

| Codigo | Descricao |
|--------|-----------|
| RNF01 | O sistema deve ter um bom desempenho |
| RNF02 | O sistema deve ser seguro |
| RNF03 | O sistema deve ser compativel com celulares e computadores |
| RNF04 | Todos os dados do sistema serao armazenados em nuvem e terao backups em tempo real |
| RNF05 | O sistema devera registrar toda alteracao feita no sistema |
| RNF06 | A consulta devera ser realizada em no maximo 1 segundo |
| RNF07 | Todos que tiverem cadastro aprovado terao acesso ao sistema |
| RNF08 | Os usuarios serao identificados no sistema pelo ID e CPF |

---

## Estrutura do Projeto
sistema-laboratorio/
├── model/
│ ├── PerfilUsuario.java
│ ├── StatusAgendamento.java
│ ├── StatusSolicitacao.java
│ ├── CondicaoSala.java
│ ├── Usuario.java
│ ├── Professor.java
│ ├── Aluno.java
│ ├── EquipeLaboratorio.java
│ ├── Coordenacao.java
│ ├── Laboratorio.java
│ ├── Agendamento.java
│ ├── Kit.java
│ ├── Material.java
│ ├── Equipamento.java
│ ├── SolicitacaoKit.java
│ └── Checklist.java
├── repository/
│ └── Repositorio.java
├── service/
│ └── Sistema.java
└── Main.java


### Arquitetura

O sistema segue o padrao **MVC (Model-View-Controller)** adaptado para aplicacao console:

- **Model** (`model/`): Classes de dominio que representam as entidades do sistema
- **Repository** (`repository/`): Camada de persistencia e acesso aos dados
- **Service** (`service/`): Logica de negocio e orquestracao das operacoes
- **View** (`Main.java` + menus no `Sistema.java`): Interface com o usuario via console

---

## Classes e Relacionamentos

### Principais Classes

| Classe | Responsabilidade |
|--------|------------------|
| Usuario | Centraliza dados comuns de acesso (nome, CPF, login, senha, perfil) |
| Professor | Registra quem esta realizando o agendamento da aula e solicitando os kits |
| Aluno | Representa os alunos que consultam a agenda |
| EquipeLaboratorio | Prepara kits, realiza checklists e controla estoque |
| Coordenacao | Administra o sistema e aprova cadastros |
| Laboratorio | Armazena informacoes fisicas da sala e valida regras de agendamento |
| Agendamento | Coracao do sistema - guarda data, horario, disciplina e vincula Professor ao Laboratorio |
| SolicitacaoKit | Registra o pedido antecipado de kits feito pelo professor |
| Kit | Agrupa um conjunto padrao de materiais e equipamentos |
| Material | Representa insumos individuais e permite controle de quantidade |
| Equipamento | Representa equipamentos reutilizaveis que podem entrar em manutencao |
| Checklist | Registra a conferencia feita pela Equipe do Laboratorio antes da aula |

### Relacionamentos

| Relacao | Cardinalidade | Descricao |
|---------|---------------|-----------|
| Professor → Agendamento | 1:N | Um professor pode realizar varios agendamentos, mas cada agendamento pertence a apenas um professor |
| Laboratorio → Agendamento | 1:N | Um laboratorio pode possuir varios agendamentos, mas cada agendamento utiliza apenas um laboratorio |
| Kit → Material | N:M | Um kit pode possuir varios materiais e um material pode estar em varios kits |
| Agendamento → SolicitacaoKit | 1:N | Uma aula pode possuir varias solicitacoes, mas cada solicitacao pertence a apenas uma aula |
| EquipeLaboratorio → Checklist | 1:N | Um funcionario pode realizar varios checklists, mas cada checklist e preenchido por apenas um funcionario |
| Laboratorio → Checklist | 1:N | Um laboratorio pode receber varios checklists, mas cada checklist e feito para apenas um laboratorio por vez |

---

## Tecnologias Utilizadas

- **Linguagem**: Java 17+
- **Paradigma**: Programacao Orientada a Objetos (POO)
- **Conceitos Aplicados**:
  - Heranca e polimorfismo (classe `Usuario` e suas subclasses)
  - Encapsulamento
  - Colecoes (`ArrayList`, `List`)
  - Enums para tipagem forte
  - API `java.time` para manipulacao de datas
  - Padrao Repository para acesso a dados

---

## Como Executar

### Pre-requisitos

- Java Development Kit (JDK) 17 ou superior
- Terminal ou IDE (Eclipse, IntelliJ, VS Code)

### Compilacao via Terminal

bash
 Compile todos os arquivos
javac -d out Main.java model/*.java repository/*.java service/*.java

 Execute o sistema
java -cp out Main

Perfil: Coordenacao
CPF: 000.000.000-00
Senha: coord123

1. Usuario se cadastra no sistema
            |
            v
2. Coordenacao aprova o cadastro
            |
            v
3. Professor faz login e agenda uma aula (UC01)
   - Seleciona laboratorio, data e horario
   - Sistema verifica disponibilidade (RN02)
   - Sistema valida antecedencia minima (RN01)
   - Sistema valida capacidade da sala (RN06)
   - Reserva e confirmada ou fica pendente
            |
            v
4. Professor solicita kit de insumos (UC02)
   - Sistema verifica estoque disponivel
   - Se fora do prazo, fica pendente (RN04)
            |
            v
5. Equipe de Laboratorio recebe notificacao
   - Prepara os kits solicitados
   - Realiza checklist de preparacao (UC03)
   - Testa equipamentos
   - Libera a sala para aula
            |
            v
6. Equipe controla estoque (UC04)
   - Registra entradas e saidas
   - Sistema dispara alertas de estoque minimo (RN05)
            |
            v
7. Professor confirma recebimento (RN10)
   - Registra condicao inicial da sala
   - Aula e realizada com sucesso
            |
            v
8. Professor registra condicao final da sala
   - Sistema atualiza status do laboratorio

