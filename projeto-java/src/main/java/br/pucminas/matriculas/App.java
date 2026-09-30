package br.pucminas.matriculas;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import br.pucminas.matriculas.model.Aluno;
import br.pucminas.matriculas.model.Disciplina;
import br.pucminas.matriculas.model.Matricula;
import br.pucminas.matriculas.model.PeriodoMatricula;
import br.pucminas.matriculas.model.Professor;
import br.pucminas.matriculas.model.Secretaria;
import br.pucminas.matriculas.model.Usuario;
import br.pucminas.matriculas.model.enums.StatusMatricula;
import br.pucminas.matriculas.model.enums.TipoDisciplina;
import br.pucminas.matriculas.servico.ServicoCobranca;

public class App {

    public static void main(String[] args) {
        Aluno aluno = new Aluno("1", "Ana Silva", "ana", "123", "RA001");
        Professor professor = new Professor("2", "Carlos Souza", "carlos", "123", "RF001");
        Secretaria secretaria = new Secretaria("3", "Secretaria", "secretaria", "123");
        PeriodoMatricula periodo = criarPeriodoDemonstracao(secretaria);
        Disciplina primeiraDisciplina = periodo.getDisciplinasOfertadas().get(0);
        professor.adicionarDisciplina(primeiraDisciplina);
        secretaria.cadastrarAluno(aluno);
        secretaria.cadastrarProfessor(professor);
        ServicoCobranca cobranca = matricula -> System.out.println("Cobrança notificada para "
                + matricula.getAluno().getNome() + " - " + matricula.getDisciplina().getNome());
        Map<String, Usuario> usuarios = Map.of(
                aluno.getLogin(), aluno,
                professor.getLogin(), professor,
                secretaria.getLogin(), secretaria);

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== Sistema de Matrículas ===");
            System.out.println("Usuários de demonstração: ana / 123, carlos / 123, secretaria / 123");
            Usuario usuario = autenticar(scanner, usuarios);
            if (usuario == null) {
                System.out.println("Login ou senha inválidos.");
                return;
            }
            executarMenu(scanner, usuario, periodo, cobranca);
        }
    }

    private static PeriodoMatricula criarPeriodoDemonstracao(Secretaria secretaria) {
        PeriodoMatricula periodo = secretaria.definirPeriodo("2026.2",
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(30));
        periodo.adicionarDisciplina(new Disciplina("COMP101", "Algoritmos"));
        periodo.adicionarDisciplina(new Disciplina("COMP102", "Banco de Dados"));
        periodo.adicionarDisciplina(new Disciplina("COMP103", "Engenharia de Software"));
        periodo.adicionarDisciplina(new Disciplina("COMP104", "Redes de Computadores"));
        periodo.adicionarDisciplina(new Disciplina("COMP105", "Inteligência Artificial"));
        periodo.adicionarDisciplina(new Disciplina("COMP106", "Projeto de Software"));
        return periodo;
    }

    private static Usuario autenticar(Scanner scanner, Map<String, Usuario> usuarios) {
        System.out.print("Login: ");
        String login = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        Usuario usuario = usuarios.get(login);
        return usuario != null && usuario.autenticar(login, senha) ? usuario : null;
    }

    private static void executarMenu(Scanner scanner, Usuario usuario, PeriodoMatricula periodo,
            ServicoCobranca cobranca) {
        if (usuario instanceof Aluno aluno) {
            executarMenuAluno(scanner, aluno, periodo, cobranca);
        } else if (usuario instanceof Professor professor) {
            executarMenuProfessor(scanner, professor);
        } else if (usuario instanceof Secretaria secretaria) {
            executarMenuSecretaria(scanner, secretaria, periodo);
        }
    }

    private static void executarMenuAluno(Scanner scanner, Aluno aluno, PeriodoMatricula periodo,
            ServicoCobranca cobranca) {
        while (true) {
            System.out.println("\n--- Menu principal ---");
            System.out.println("1 - Ver disciplinas ofertadas");
            System.out.println("2 - Fazer matrícula");
            System.out.println("3 - Cancelar matrícula");
            System.out.println("4 - Ver minhas matrículas");
            System.out.println("5 - Encerrar período");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");

            switch (scanner.nextLine()) {
                case "1" -> listarDisciplinas(periodo);
                case "2" -> matricular(scanner, aluno, periodo, cobranca);
                case "3" -> cancelar(scanner, aluno, periodo);
                case "4" -> listarMatriculas(aluno);
                case "5" -> {
                    periodo.encerrar();
                    System.out.println("Período encerrado e disciplinas avaliadas.");
                }
                case "0" -> {
                    System.out.println("Até logo.");
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void executarMenuProfessor(Scanner scanner, Professor professor) {
        while (true) {
            System.out.println("\n--- Menu do professor ---");
            System.out.println("1 - Consultar alunos matriculados");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");
            String opcao = scanner.nextLine();
            if ("0".equals(opcao)) return;
            if (!"1".equals(opcao)) {
                System.out.println("Opção inválida.");
                continue;
            }
            for (int indice = 0; indice < professor.getDisciplinas().size(); indice++) {
                Disciplina disciplina = professor.getDisciplinas().get(indice);
                System.out.printf("%d - %s (%s)%n", indice + 1, disciplina.getNome(), disciplina.getCodigo());
            }
            int indice = lerIndice(scanner, professor.getDisciplinas().size());
            if (indice >= 0) {
                List<Aluno> alunos = professor.consultarAlunosMatriculados(
                        professor.getDisciplinas().get(indice));
                if (alunos.isEmpty()) {
                    System.out.println("Nenhum aluno matriculado.");
                } else {
                    alunos.forEach(aluno -> System.out.println("- " + aluno.getNome()));
                }
            }
        }
    }

    private static void executarMenuSecretaria(Scanner scanner, Secretaria secretaria,
            PeriodoMatricula periodo) {
        while (true) {
            System.out.println("\n--- Menu da secretaria ---");
            System.out.println("1 - Consultar cadastros");
            System.out.println("2 - Encerrar período");
            System.out.println("0 - Sair");
            System.out.print("Opção: ");
            switch (scanner.nextLine()) {
                case "1" -> System.out.printf("Alunos: %d | Professores: %d | Períodos: %d%n",
                        secretaria.getAlunos().size(), secretaria.getProfessores().size(),
                        secretaria.getPeriodos().size());
                case "2" -> {
                    secretaria.processarEncerramento(periodo);
                    System.out.println("Período encerrado e disciplinas avaliadas.");
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    private static void listarDisciplinas(PeriodoMatricula periodo) {
        System.out.println("\nDisciplinas do período " + periodo.getSemestre() + ":");
        for (int indice = 0; indice < periodo.getDisciplinasOfertadas().size(); indice++) {
            Disciplina disciplina = periodo.getDisciplinasOfertadas().get(indice);
            System.out.printf("%d - %s (%s)%n", indice + 1, disciplina.getNome(), disciplina.getCodigo());
        }
    }

    private static void matricular(Scanner scanner, Aluno aluno, PeriodoMatricula periodo,
            ServicoCobranca cobranca) {
        listarDisciplinas(periodo);
        System.out.print("Número da disciplina: ");
        int indice = lerIndice(scanner, periodo.getDisciplinasOfertadas().size());
        if (indice < 0) return;

        System.out.print("Tipo (1 - obrigatória, 2 - optativa): ");
        String tipoInformado = scanner.nextLine();
        TipoDisciplina tipo;
        if ("1".equals(tipoInformado)) {
            tipo = TipoDisciplina.OBRIGATORIA;
        } else if ("2".equals(tipoInformado)) {
            tipo = TipoDisciplina.OPTATIVA;
        } else {
            System.out.println("Tipo inválido.");
            return;
        }
        try {
            aluno.matricularEm(periodo.getDisciplinasOfertadas().get(indice), tipo, periodo, cobranca);
            System.out.println("Matrícula realizada com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            System.out.println("Não foi possível realizar a matrícula: " + exception.getMessage());
        }
    }

    private static void cancelar(Scanner scanner, Aluno aluno, PeriodoMatricula periodo) {
        List<Matricula> ativas = aluno.getMatriculas().stream()
                .filter(matricula -> matricula.getStatus() == StatusMatricula.ATIVA)
                .toList();
        if (ativas.isEmpty()) {
            System.out.println("Você não possui matrículas ativas.");
            return;
        }
        listarMatriculas(ativas);
        System.out.print("Número da matrícula: ");
        int indice = lerIndice(scanner, ativas.size());
        if (indice < 0) return;
        try {
            aluno.cancelarMatricula(ativas.get(indice), periodo);
            System.out.println("Matrícula cancelada.");
        } catch (IllegalArgumentException | IllegalStateException exception) {
            System.out.println("Não foi possível cancelar: " + exception.getMessage());
        }
    }

    private static void listarMatriculas(Aluno aluno) {
        listarMatriculas(aluno.getMatriculas().stream()
                .filter(matricula -> matricula.getStatus() == StatusMatricula.ATIVA)
                .toList());
    }

    private static void listarMatriculas(List<Matricula> matriculas) {
        if (matriculas.isEmpty()) {
            System.out.println("Nenhuma matrícula ativa.");
            return;
        }
        System.out.println("\nMinhas matrículas:");
        for (int indice = 0; indice < matriculas.size(); indice++) {
            Matricula matricula = matriculas.get(indice);
            System.out.printf("%d - %s (%s)%n", indice + 1,
                    matricula.getDisciplina().getNome(), matricula.getTipo());
        }
    }

    private static int lerIndice(Scanner scanner, int tamanho) {
        try {
            int indice = Integer.parseInt(scanner.nextLine()) - 1;
            if (indice >= 0 && indice < tamanho) return indice;
        } catch (NumberFormatException ignored) {
        }
        System.out.println("Número inválido.");
        return -1;
    }
}
