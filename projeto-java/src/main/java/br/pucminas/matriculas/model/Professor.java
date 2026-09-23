package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Ator "Professor": consulta os alunos matriculados nas disciplinas que leciona.
 */
public class Professor extends Usuario {

    private String registroFuncional;
    private List<Disciplina> disciplinas = new ArrayList<>();

    public Professor(String id, String nome, String login, String senha, String registroFuncional) {
        super(id, nome, login, senha);
        this.registroFuncional = registroFuncional;
    }

    /**
     * Caso de uso "Consultar Alunos Matriculados".
     */
    public List<Aluno> consultarAlunosMatriculados(Disciplina disciplina) {
        if (disciplina == null) {
            throw new IllegalArgumentException("Disciplina obrigatoria");
        }
        return disciplina.getMatriculas().stream()
                .filter(matricula -> matricula.getStatus() == br.pucminas.matriculas.model.enums.StatusMatricula.ATIVA)
                .map(Matricula::getAluno)
                .distinct()
                .toList();
    }

    public void adicionarDisciplina(Disciplina disciplina) {
        if (disciplina != null && !disciplinas.contains(disciplina)) {
            disciplinas.add(disciplina);
        }
    }

    public String getRegistroFuncional() {
        return registroFuncional;
    }

    public List<Disciplina> getDisciplinas() {
        return disciplinas;
    }
}
