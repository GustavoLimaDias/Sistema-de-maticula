package br.pucminas.matriculas.model;

import java.time.LocalDate;

import br.pucminas.matriculas.model.enums.StatusMatricula;
import br.pucminas.matriculas.model.enums.TipoDisciplina;

/**
 * Classe de associação entre {@link Aluno} e {@link Disciplina}: representa uma inscrição
 * concreta, com o tipo (obrigatória/optativa - RN01), a data e o status.
 * Toda matrícula concluída deve notificar o {@link br.pucminas.matriculas.servico.ServicoCobranca}
 * (RN05).
 */
public class Matricula {

    private Aluno aluno;
    private Disciplina disciplina;
    private TipoDisciplina tipo;
    private LocalDate dataMatricula;
    private StatusMatricula status;

    public Matricula(Aluno aluno, Disciplina disciplina, TipoDisciplina tipo, LocalDate dataMatricula) {
        this.aluno = aluno;
        this.disciplina = disciplina;
        this.tipo = tipo;
        this.dataMatricula = dataMatricula;
        this.status = StatusMatricula.ATIVA;
    }

    /**
     * Caso de uso "Cancelar Matrícula".
     */
    public void cancelar() {
        status = StatusMatricula.CANCELADA;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public TipoDisciplina getTipo() {
        return tipo;
    }

    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    public StatusMatricula getStatus() {
        return status;
    }

    public void setStatus(StatusMatricula status) {
        this.status = status;
    }
}
