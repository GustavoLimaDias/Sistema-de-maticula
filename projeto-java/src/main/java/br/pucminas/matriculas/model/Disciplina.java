package br.pucminas.matriculas.model;

import java.util.ArrayList;
import java.util.List;

import br.pucminas.matriculas.model.enums.StatusDisciplina;

/**
 * Disciplina ofertada dentro de um curso, em um período de matrículas.
 * RN03: precisa de ao menos {@link #minimoParaAtivar} matriculados para ser ativada.
 * RN04: encerra inscrições automaticamente ao atingir {@link #capacidadeMaxima}.
 */
public class Disciplina {

    private String codigo;
    private String nome;
    private int capacidadeMaxima = 60;
    private int minimoParaAtivar = 3;
    private StatusDisciplina status = StatusDisciplina.EM_OFERTA;
    private List<Matricula> matriculas = new ArrayList<>();

    public Disciplina(String codigo, String nome) {
        this.codigo = codigo;
        this.nome = nome;
    }

    /**
     * RN04: verifica se ainda há vagas (matriculados &lt; capacidadeMaxima).
     */
    public boolean possuiVagas() {
        return status == StatusDisciplina.EM_OFERTA && matriculas.stream()
            .filter(matricula -> matricula.getStatus() == br.pucminas.matriculas.model.enums.StatusMatricula.ATIVA)
            .count() < capacidadeMaxima;
    }

    /**
     * RN03: chamada pelo caso de uso "Processar Encerramento do Período de Matrículas".
     * Ativa a disciplina se houver ao menos {@link #minimoParaAtivar} matriculados;
     * caso contrário, cancela a disciplina.
     */
    public void avaliarAtivacao() {
        long matriculasAtivas = matriculas.stream()
            .filter(matricula -> matricula.getStatus() == br.pucminas.matriculas.model.enums.StatusMatricula.ATIVA)
            .count();
        status = matriculasAtivas >= minimoParaAtivar ? StatusDisciplina.ATIVA : StatusDisciplina.CANCELADA;
    }

    /**
     * RN04: encerra as inscrições assim que a disciplina atinge a capacidade máxima
     * (extensão do caso de uso "Matricular-se em Disciplina").
     */
    public void encerrarPorLotacao() {
        if (matriculas.stream()
                .filter(matricula -> matricula.getStatus() == br.pucminas.matriculas.model.enums.StatusMatricula.ATIVA)
                .count() >= capacidadeMaxima) {
            status = StatusDisciplina.LOTADA;
        }
    }

    void adicionarMatricula(Matricula matricula) {
        matriculas.add(matricula);
    }

    void atualizarStatusAposCancelamento() {
        long matriculasAtivas = matriculas.stream()
                .filter(matricula -> matricula.getStatus() == br.pucminas.matriculas.model.enums.StatusMatricula.ATIVA)
                .count();
        if (status == StatusDisciplina.LOTADA && matriculasAtivas < capacidadeMaxima) {
            status = StatusDisciplina.EM_OFERTA;
        }
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public int getMinimoParaAtivar() {
        return minimoParaAtivar;
    }

    public StatusDisciplina getStatus() {
        return status;
    }

    public void setStatus(StatusDisciplina status) {
        this.status = status;
    }

    public List<Matricula> getMatriculas() {
        return java.util.Collections.unmodifiableList(matriculas);
    }
}
