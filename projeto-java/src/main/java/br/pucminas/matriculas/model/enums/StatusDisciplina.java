package br.pucminas.matriculas.model.enums;

/**
 * Situação de uma disciplina em relação à oferta do próximo semestre (RN03, RN04).
 */
public enum StatusDisciplina {
    /** Aceitando matrículas durante o período vigente. */
    EM_OFERTA,
    /** Atingiu o mínimo de 3 inscritos ao final do período e ocorrerá no semestre seguinte. */
    ATIVA,
    /** Não atingiu o mínimo de 3 inscritos e foi cancelada. */
    CANCELADA
}
