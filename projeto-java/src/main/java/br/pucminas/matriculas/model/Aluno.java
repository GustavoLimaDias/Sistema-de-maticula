package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

import br.pucminas.matriculas.model.enums.TipoDisciplina;

/**
 * Ator "Aluno": matricula-se e cancela matrículas em disciplinas (RN01, RN02),
 * dentro dos limites de vagas de cada disciplina (RN04).
 */
public class Aluno extends Usuario {

    private String registroAcademico;
    private List<Matricula> matriculas = new ArrayList<>();

    public Aluno(String id, String nome, String login, String senha, String registroAcademico) {
        super(id, nome, login, senha);
        this.registroAcademico = registroAcademico;
    }

    /**
     * Caso de uso "Matricular-se em Disciplina".
     * Regras a validar na implementação (Sprint 3): RN01 (até 4 obrigatórias + 2 optativas),
     * RN02 (período aberto) e RN04 (disciplina não pode estar lotada).
     * Ao concluir, deve incluir "Notificar Sistema de Cobrança".
     */
    public Matricula matricularEm(Disciplina disciplina, TipoDisciplina tipo) {
        validarMatricula(disciplina, tipo);
        Matricula matricula = new Matricula(this, disciplina, tipo, LocalDate.now());
        matriculas.add(matricula);
        disciplina.getMatriculas().add(matricula);
        disciplina.encerrarPorLotacao();
        return matricula;
    }

    public Matricula matricularEm(Disciplina disciplina, TipoDisciplina tipo,
            PeriodoMatricula periodo, br.pucminas.matriculas.servico.ServicoCobranca servicoCobranca) {
        if (periodo == null || !periodo.estaAberto() || !periodo.getDisciplinasOfertadas().contains(disciplina)) {
            throw new IllegalStateException("Periodo de matriculas fechado ou disciplina nao ofertada");
        }
        Matricula matricula = matricularEm(disciplina, tipo);
        if (servicoCobranca != null) {
            servicoCobranca.notificarMatricula(matricula);
        }
        return matricula;
    }

    /**
     * Caso de uso "Cancelar Matrícula" (somente dentro do período de matrículas vigente - RN02).
     */
    public void cancelarMatricula(Matricula matricula) {
        if (matricula == null || matricula.getAluno() != this || !matriculas.contains(matricula)) {
            throw new IllegalArgumentException("Matricula nao pertence ao aluno");
        }
        matricula.cancelar();
    }

    public void cancelarMatricula(Matricula matricula, PeriodoMatricula periodo) {
        if (periodo == null || !periodo.estaAberto()) {
            throw new IllegalStateException("Periodo de matriculas fechado");
        }
        cancelarMatricula(matricula);
    }

    /**
     * Caso de uso "Consultar Disciplinas Ofertadas".
     */
    public List<Disciplina> consultarDisciplinasOfertadas(PeriodoMatricula periodo) {
        if (periodo == null) {
            throw new IllegalArgumentException("Periodo obrigatorio");
        }
        return new ArrayList<>(periodo.getDisciplinasOfertadas());
    }

    private void validarMatricula(Disciplina disciplina, TipoDisciplina tipo) {
        if (disciplina == null || tipo == null) {
            throw new IllegalArgumentException("Disciplina e tipo sao obrigatorios");
        }
        if (!disciplina.possuiVagas()) {
            throw new IllegalStateException("Disciplina sem vagas ou indisponivel");
        }
        if (matriculas.stream().anyMatch(matricula -> matricula.getDisciplina() == disciplina
                && matricula.getStatus() == br.pucminas.matriculas.model.enums.StatusMatricula.ATIVA)) {
            throw new IllegalStateException("Aluno ja esta matriculado nessa disciplina");
        }
        long quantidade = matriculas.stream().filter(matricula -> matricula.getTipo() == tipo
                && matricula.getStatus() == br.pucminas.matriculas.model.enums.StatusMatricula.ATIVA).count();
        long limite = tipo == TipoDisciplina.OBRIGATORIA ? 4 : 2;
        if (quantidade >= limite) {
            throw new IllegalStateException("Limite de disciplinas atingido para o tipo informado");
        }
    }

    public String getRegistroAcademico() {
        return registroAcademico;
    }

    public List<Matricula> getMatriculas() {
        return matriculas;
    }
}
