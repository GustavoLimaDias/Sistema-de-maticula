# Sistema de Matrículas

Projeto da disciplina **Projeto de Software** (PUC Minas — Curso de Engenharia de Software), referente ao **Laboratório 2 — Sprint 1 (Lab01S01)**: Diagrama de Casos de Uso e Histórias de Usuário.

## Sumário

- [Contexto do domínio](#contexto-do-domínio)
- [Regras de negócio](#regras-de-negócio)
- [Diagrama de casos de uso](#diagrama-de-casos-de-uso)
- [Descrição dos casos de uso](#descrição-dos-casos-de-uso)
- [Histórias de usuário](#histórias-de-usuário)
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

## Diagrama de casos de uso

![Diagrama de Casos de Uso — Sistema de Matrículas](docs/diagrama-caso-de-uso.svg)

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
| Gerenciar Currículo do Semestre | Secretaria | — | Cadastra cursos, disciplinas, professores e alunos, e define o currículo do semestre. |
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

### Regra de sistema (transversal)

- **US14** — Como sistema, quero notificar o sistema de cobrança sempre que uma matrícula for concluída, para garantir que o aluno seja cobrado corretamente (RN05).
- **US15** — Como sistema, quero encerrar automaticamente as inscrições de uma disciplina ao atingir 60 alunos, para respeitar o limite de vagas (RN04).