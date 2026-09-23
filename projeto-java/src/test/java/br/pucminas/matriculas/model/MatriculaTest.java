package br.pucminas.matriculas.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import br.pucminas.matriculas.model.enums.StatusDisciplina;
import br.pucminas.matriculas.model.enums.StatusMatricula;
import br.pucminas.matriculas.model.enums.TipoDisciplina;

class MatriculaTest {

    @Test
    void autenticaUsuarioComSenhaCorreta() {
        Aluno aluno = new Aluno("1", "Ana", "ana", "segredo", "A1");

        assertTrue(aluno.autenticar("segredo"));
        assertTrue(!aluno.autenticar("errada"));
    }

    @Test
    void aplicaLimiteDeQuatroObrigatorias() {
        Aluno aluno = aluno("1");
        PeriodoMatricula periodo = periodoAberto();
        for (int indice = 0; indice < 4; indice++) {
            Disciplina disciplina = new Disciplina("D" + indice, "Disciplina " + indice);
            periodo.adicionarDisciplina(disciplina);
            aluno.matricularEm(disciplina, TipoDisciplina.OBRIGATORIA, periodo, null);
        }

        Disciplina excedente = new Disciplina("D5", "Excedente");
        periodo.adicionarDisciplina(excedente);
        assertThrows(IllegalStateException.class,
                () -> aluno.matricularEm(excedente, TipoDisciplina.OBRIGATORIA, periodo, null));
    }

    @Test
    void notificaCobrancaECancelaDentroDoPeriodo() {
        Aluno aluno = aluno("1");
        Disciplina disciplina = new Disciplina("D1", "Algoritmos");
        PeriodoMatricula periodo = periodoAberto();
        periodo.adicionarDisciplina(disciplina);
        AtomicReference<Matricula> notificada = new AtomicReference<>();

        Matricula matricula = aluno.matricularEm(disciplina, TipoDisciplina.OBRIGATORIA, periodo,
                notificada::set);
        aluno.cancelarMatricula(matricula, periodo);

        assertEquals(matricula, notificada.get());
        assertEquals(StatusMatricula.CANCELADA, matricula.getStatus());
    }

    @Test
    void encerraPeriodoEAtivaDisciplinaComTresAlunos() {
        PeriodoMatricula periodo = periodoAberto();
        Disciplina disciplina = new Disciplina("D1", "Algoritmos");
        periodo.adicionarDisciplina(disciplina);
        for (int indice = 0; indice < 3; indice++) {
            aluno(String.valueOf(indice)).matricularEm(disciplina, TipoDisciplina.OBRIGATORIA);
        }

        periodo.encerrar();

        assertEquals(StatusDisciplina.ATIVA, disciplina.getStatus());
        assertTrue(!periodo.estaAberto());
    }

    private static Aluno aluno(String id) {
        return new Aluno(id, "Aluno " + id, "aluno" + id, "senha", "RA" + id);
    }

    private static PeriodoMatricula periodoAberto() {
        return new PeriodoMatricula("2026.2", LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
    }
}