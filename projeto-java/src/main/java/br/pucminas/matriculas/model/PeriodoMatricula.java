package br.pucminas.matriculas.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.pucminas.matriculas.model.enums.StatusPeriodo;

/**
 * Período de matrículas de um semestre: define a janela em que alunos podem se
 * matricular ou cancelar matrículas (RN02), e as disciplinas ofertadas nele.
 */
public class PeriodoMatricula {

    private String semestre;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private StatusPeriodo status = StatusPeriodo.ABERTO;
    private List<Disciplina> disciplinasOfertadas = new ArrayList<>();

    public PeriodoMatricula(String semestre, LocalDate dataInicio, LocalDate dataFim) {
        if (semestre == null || semestre.isBlank() || dataInicio == null || dataFim == null
                || dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("Semestre e datas validos sao obrigatorios");
        }
        this.semestre = semestre;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    /**
     * RN02: indica se matrículas/cancelamentos podem ser feitos agora.
     */
    public boolean estaAberto() {
        LocalDate hoje = LocalDate.now();
        return status == StatusPeriodo.ABERTO
            && !hoje.isBefore(dataInicio)
            && !hoje.isAfter(dataFim);
    }

    /**
     * Caso de uso "Processar Encerramento do Período de Matrículas" (RN03):
     * fecha o período e aciona {@code Disciplina.avaliarAtivacao()} para cada disciplina ofertada.
     */
    public void encerrar() {
        if (status == StatusPeriodo.ENCERRADO) {
            return;
        }
        status = StatusPeriodo.ENCERRADO;
        disciplinasOfertadas.forEach(Disciplina::avaliarAtivacao);
    }

    public void adicionarDisciplina(Disciplina disciplina) {
        if (disciplina == null) {
            throw new IllegalArgumentException("Disciplina obrigatoria");
        }
        if (!disciplinasOfertadas.contains(disciplina)) {
            disciplinasOfertadas.add(disciplina);
        }
    }

    public String getSemestre() {
        return semestre;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public StatusPeriodo getStatus() {
        return status;
    }

    public List<Disciplina> getDisciplinasOfertadas() {
        return java.util.Collections.unmodifiableList(disciplinasOfertadas);
    }
}
