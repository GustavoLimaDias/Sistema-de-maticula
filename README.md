# Sistema de Matrículas

Projeto da disciplina **Projeto de Software** (PUC Minas — Curso de Engenharia de Software), Laboratório 1. Este README acumula as entregas de cada sprint.

## Sumário

- [Contexto do domínio](#contexto-do-domínio)
- [Regras de negócio](#regras-de-negócio)
- [Sprint 1 — Diagrama de Casos de Uso e Histórias de Usuário](#sprint-1--diagrama-de-casos-de-uso-e-histórias-de-usuário)
  - [Correções aplicadas na Sprint 2](#correções-aplicadas-na-sprint-2)
  - [Diagrama de casos de uso](#diagrama-de-casos-de-uso)
  - [Descrição dos casos de uso](#descrição-dos-casos-de-uso)
  - [Histórias de usuário](#histórias-de-usuário)
- [Sprint 2 — Diagrama de Classes e Projeto Java](#sprint-2--diagrama-de-classes-e-projeto-java)
  - [Diagrama de classes](#diagrama-de-classes)
  - [Descrição das classes](#descrição-das-classes)
  - [Estrutura do projeto Java](#estrutura-do-projeto-java)
  - [Como compilar e executar](#como-compilar-e-executar)
- [Próximas sprints](#próximas-sprints)

## Contexto do domínio

Uma universidade quer informatizar seu sistema de matrículas. A secretaria acadêmica monta o currículo de cada semestre e mantém os dados de cursos, disciplinas, professores e alunos. Cada **curso** tem nome, número de créditos e é composto por várias **disciplinas**. Durante o período de matrículas, o **aluno** se inscreve ou cancela inscrições em disciplinas; o **professor** consulta quem está matriculado nas disciplinas que leciona; e a **secretaria** administra o currículo e os períodos de matrícula. Ao final do período, o sistema decide automaticamente quais disciplinas serão oferecidas no semestre seguinte. Toda matrícula concluída dispara uma notificação a um sistema externo de cobrança.

## Regras de negócio

| # | Regra |
|---|-------|
| RN01 | Um aluno pode se matricular em até 4 disciplinas obrigatórias (1ª opção) e até 2 disciplinas optativas (alternativas). |
| RN02 | Matrículas e cancelamentos só podem ser feitos dentro do período de matrículas vigente. |
| RN03 | Uma disciplina só é ativada para o semestre seguinte se tiver, no mínimo, 3 alunos inscritos ao final do período de matrículas; caso contrário, é cancelada. |
| RN04 | Uma disciplina tem no máximo 60 vagas; ao atingir esse número, as inscrições para ela são encerradas automaticamente. |
| RN05 | Toda matrícula concluída notifica o sistema de cobrança, para que o aluno seja cobrado pelas disciplinas do semestre. |
| RN06 | Todo usuário (aluno, professor, secretaria) autentica-se por login e senha. |

## Sprint 1 — Diagrama de Casos de Uso e Histórias de Usuário

### Correções aplicadas na Sprint 2

Conforme pedido no enunciado da Sprint 2 ("Correção dos Diagramas desenvolvidos"), o diagrama e as histórias da Sprint 1 foram revisados:

- **Novo caso de uso "Definir Período de Matrículas"**, incluído (`«include»`) por *Gerenciar Currículo do Semestre*. Antes essa responsabilidade estava implícita dentro do caso de uso de currículo; agora está explícita, com sua própria história (US12).
- **Remoção das histórias US14 e US15** ("Como sistema, quero..."). Elas duplicavam informação que já existia como regra de negócio (RN04 e RN05) e como relações `«include»`/`«extend»` no próprio diagrama (*Notificar Sistema de Cobrança* e *Encerramento Automático por Lotação*). Manter histórias de usuário sem um ator humano real não é uma boa prática (foge do formato "Como `<ator>`, quero..."); a regra de negócio + a relação no diagrama já documentam esse comportamento sem redundância.

### Diagrama de casos de uso

![Diagrama de Casos de Uso — Sistema de Matrículas](diagrama-caso-de-uso.svg)

**Atores:**
- **Aluno** — matricula-se e cancela matrículas em disciplinas.
- **Professor** — consulta os alunos matriculados em suas disciplinas.
- **Secretaria Acadêmica** — monta o currículo, cadastra cursos/disciplinas/pessoas e conduz o encerramento do período de matrículas.
- **Sistema de Cobrança** *(ator secundário/externo)* — recebe a notificação de matrícula para gerar a cobrança.

## Descrição dos casos de uso

| Caso de uso | Ator principal | Tipo de relação | Descrição resumida |
|---|---|---|---|
| Efetuar Login | Aluno, Professor, Secretaria | — | Valida usuário e senha antes de liberar qualquer outra funcionalidade. |
| Matricular-se em Disciplina | Aluno | inclui *Efetuar Login* e *Notificar Sistema de Cobrança* | Inscreve o aluno em até 4 disciplinas obrigatórias e 2 optativas (RN01, RN02). |
| Encerramento Automático por Lotação | — (regra do sistema) | estende *Matricular-se em Disciplina* | Fecha as inscrições da disciplina assim que ela atinge 60 alunos (RN04). |
| Cancelar Matrícula | Aluno | inclui *Efetuar Login* | Remove uma matrícula feita anteriormente, dentro do período vigente. |
| Consultar Disciplinas Ofertadas | Aluno | — | Lista as disciplinas disponíveis no período de matrículas atual. |
| Consultar Alunos Matriculados | Professor | inclui *Efetuar Login* | Mostra os alunos inscritos em cada disciplina do professor. |
| Gerenciar Currículo do Semestre | Secretaria | inclui *Definir Período de Matrículas* | Cadastra cursos, disciplinas, professores e alunos, e define o currículo do semestre. |
| Definir Período de Matrículas | Secretaria (via *Gerenciar Currículo*) | incluído por *Gerenciar Currículo do Semestre* | Define início e fim do período em que alunos podem matricular-se ou cancelar (RN02). |
| Processar Encerramento do Período de Matrículas | Secretaria | dispara a avaliação de RN03 para cada disciplina | Ao fim do período, ativa disciplinas com 3+ inscritos e cancela as demais. |
| Notificar Sistema de Cobrança | Sistema de Cobrança *(ator secundário)* | incluído por *Matricular-se em Disciplina* | Envia ao sistema externo os dados necessários para cobrar o aluno (RN05). |

## Histórias de usuário

### Aluno

- **US01** — Como aluno, quero efetuar login com meu usuário e senha, para acessar as funcionalidades de matrícula.
- **US02** — Como aluno, quero visualizar as disciplinas ofertadas no período de matrículas vigente, para decidir em quais me inscrever.
- **US03** — Como aluno, quero me matricular em até 4 disciplinas obrigatórias e 2 optativas, para montar minha grade do semestre.
  - *Critério:* o sistema deve impedir matrícula em disciplina com 60 vagas já preenchidas (RN04).
- **US04** — Como aluno, quero cancelar uma matrícula feita anteriormente, para ajustar minha grade enquanto o período de matrículas estiver aberto.
- **US05** — Como aluno, quero que, ao concluir minha matrícula, eu seja automaticamente incluído na cobrança do semestre, para não precisar solicitar isso manualmente.

### Professor

- **US06** — Como professor, quero efetuar login no sistema, para acessar informações das minhas disciplinas.
- **US07** — Como professor, quero consultar a lista de alunos matriculados em cada disciplina que leciono, para saber quem frequentará minhas aulas.

### Secretaria Acadêmica

- **US08** — Como secretaria acadêmica, quero efetuar login no sistema, para gerenciar o currículo do semestre.
- **US09** — Como secretaria acadêmica, quero cadastrar cursos (nome e número de créditos), para estruturar a oferta acadêmica.
- **US10** — Como secretaria acadêmica, quero cadastrar disciplinas vinculadas a um curso, para compor o currículo de cada semestre.
- **US11** — Como secretaria acadêmica, quero cadastrar professores e alunos, para que possam efetuar login e usar o sistema.
- **US12** — Como secretaria acadêmica, quero definir o período de matrículas de cada semestre, para controlar quando alunos podem se inscrever ou cancelar disciplinas.
- **US13** — Como secretaria acadêmica, quero que, ao final do período de matrículas, o sistema avalie automaticamente cada disciplina, para ativar as que tiverem 3 ou mais alunos inscritos e cancelar as demais (RN03).

> As regras RN04 (encerrar inscrições ao atingir 60 vagas) e RN05 (notificar o sistema de cobrança) já estão documentadas na tabela de [regras de negócio](#regras-de-negócio) e representadas no diagrama pelas relações `«extend»`/`«include»` — por isso não repetimos como histórias de usuário separadas (ver [correções da Sprint 2](#correções-aplicadas-na-sprint-2)).

## Sprint 2 — Diagrama de Classes e Projeto Java

### Diagrama de classes

![Diagrama de Classes — Sistema de Matrículas](diagrama-classes.svg)

O modelo estrutural segue diretamente os casos de uso e regras de negócio da Sprint 1:

- **`Usuario`** (abstrata) concentra login/senha e a autenticação (RN06); **`Aluno`**, **`Professor`** e **`Secretaria`** herdam dela — cada um só expõe os métodos do seu próprio caso de uso.
- **`Curso`** *compõe* **`Disciplina`** (uma disciplina pertence a exatamente um curso).
- **`Matricula`** é uma **classe de associação** entre `Aluno` e `Disciplina`: carrega o tipo (obrigatória/optativa — RN01), a data e o status, em vez de ser só uma ligação simples. A conclusão da matrícula dispara o `ServicoCobranca` (RN05).
- **`PeriodoMatricula`** guarda a janela de matrículas (RN02) e as disciplinas ofertadas; seu método `encerrar()` é quem aciona a avaliação de RN03 em cada `Disciplina`.
- **`ServicoCobranca`** é uma **interface**, não uma classe concreta: representa a fronteira com o sistema de cobrança externo (o ator secundário do diagrama de casos de uso). O sistema aceita qualquer implementação dessa interface.
- Os quatro **enums** (`TipoDisciplina`, `StatusDisciplina`, `StatusMatricula`, `StatusPeriodo`) evitam strings soltas ("ativa", "cancelada", ...) espalhadas pelo código.
- A nota ligada a `Secretaria` resume que ela cadastra `Curso`, `Disciplina`, `Professor` e `Aluno` — não desenhei uma seta de dependência para cada um para não poluir o diagrama, já que essa relação de cadastro não tem multiplicidade nem comportamento próprio a modelar.

### Descrição das classes

| Classe | Tipo | Atributos principais | Responsabilidade |
|---|---|---|---|
| `Usuario` | abstrata | id, nome, login, senha | Autenticação comum a todo usuário. |
| `Aluno` | concreta | registroAcademico, matriculas | Matricular-se, cancelar matrícula, consultar disciplinas. |
| `Professor` | concreta | registroFuncional, disciplinas | Consultar alunos matriculados. |
| `Secretaria` | concreta | — | Cadastrar curso/disciplina/professor/aluno; processar encerramento do período. |
| `Curso` | concreta | nome, numeroCreditos, disciplinas | Agrupar as disciplinas de um curso. |
| `Disciplina` | concreta | codigo, nome, capacidadeMaxima=60, minimoParaAtivar=3, status | Controlar vagas (RN04) e ativação/cancelamento (RN03). |
| `Matricula` | concreta (classe de associação) | tipo, dataMatricula, status | Registrar a inscrição de um aluno em uma disciplina. |
| `PeriodoMatricula` | concreta | semestre, dataInicio, dataFim, status | Controlar a janela de matrículas e disparar o encerramento. |
| `ServicoCobranca` | interface | — | Fronteira com o sistema de cobrança externo. |

### Estrutura do projeto Java

```
projeto-java/
├── pom.xml
└── src/main/java/br/pucminas/matriculas/
    ├── App.java                     -> ponto de entrada
    ├── model/
    │   ├── Usuario.java
    │   ├── Aluno.java
    │   ├── Professor.java
    │   ├── Secretaria.java
    │   ├── Curso.java
    │   ├── Disciplina.java
    │   ├── Matricula.java
    │   ├── PeriodoMatricula.java
    │   └── enums/
    │       ├── TipoDisciplina.java
    │       ├── StatusDisciplina.java
    │       ├── StatusMatricula.java
    │       └── StatusPeriodo.java
    └── servico/
        └── ServicoCobranca.java     -> interface (integração com o sistema de cobrança)
```

Os testes automatizados ficam em:

```text
  src/test/java/br/pucminas/matriculas/model/MatriculaTest.java
```

As regras de negócio principais já estão implementadas: autenticação, período vigente,
limite de 4 disciplinas obrigatórias e 2 optativas, limite de 60 vagas, cancelamento,
ativação ou cancelamento de disciplinas no encerramento e notificação do sistema de cobrança.

### Como compilar e executar

Projeto Maven, Java 17. Na raiz de `projeto-java/`:

```bash
cd projeto-java
mvn clean test                                       # compila e executa os testes
mvn compile                                          # apenas compila
java -cp target/classes br.pucminas.matriculas.App  # executa o ponto de entrada
```

O projeto usa Java 17 e Maven. A suíte atual cobre autenticação, limites de matrícula,
cancelamento, cobrança e encerramento do período.

## Estado atual

O protótipo de domínio da Sprint 2 está funcional e testado. A interface em linha de
comando e a persistência em arquivo podem ser adicionadas nas próximas sprints.

Estrutura de repositório usada a partir desta sprint:

```
/diagrama-caso-de-uso.svg
/diagrama-classes.svg
/projeto-java/          -> projeto Maven (código-fonte Java)
README.md               -> este documento, atualizado a cada sprint
```
