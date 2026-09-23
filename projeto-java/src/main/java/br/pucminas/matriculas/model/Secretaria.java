package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Ator "Secretaria Acadêmica": monta o currículo do semestre, cadastra cursos, disciplinas,
 * professores e alunos, e conduz o encerramento do período de matrículas (RN03).
 */
public class Secretaria extends Usuario {

    private final List<Curso> cursos = new ArrayList<>();
    private final List<Disciplina> disciplinas = new ArrayList<>();
    private final List<Professor> professores = new ArrayList<>();
    private final List<Aluno> alunos = new ArrayList<>();

    public Secretaria(String id, String nome, String login, String senha) {
        super(id, nome, login, senha);
    }

    /**
     * Caso de uso "Gerenciar Currículo do Semestre".
     */
    public void cadastrarCurso(Curso curso) {
        if (curso != null && !cursos.contains(curso)) cursos.add(curso);
    }

    /**
     * Caso de uso "Gerenciar Currículo do Semestre".
     */
    public void cadastrarDisciplina(Disciplina disciplina) {
        if (disciplina != null && !disciplinas.contains(disciplina)) disciplinas.add(disciplina);
    }

    public void cadastrarProfessor(Professor professor) {
        if (professor != null && !professores.contains(professor)) professores.add(professor);
    }

    public void cadastrarAluno(Aluno aluno) {
        if (aluno != null && !alunos.contains(aluno)) alunos.add(aluno);
    }

    /**
     * Caso de uso "Processar Encerramento do Período de Matrículas" (RN03):
     * avalia cada disciplina ofertada e ativa (>= 3 matriculados) ou cancela (&lt; 3) cada uma.
     */
    public void processarEncerramento(PeriodoMatricula periodo) {
        if (periodo == null) throw new IllegalArgumentException("Periodo obrigatorio");
        periodo.encerrar();
    }
}
