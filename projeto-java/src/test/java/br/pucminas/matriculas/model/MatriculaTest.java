package br.pucminas.matriculas.model;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import br.pucminas.matriculas.model.enums.StatusDisciplina;
import br.pucminas.matriculas.model.enums.StatusMatricula;
import br.pucminas.matriculas.model.enums.TipoDisciplina;

class MatriculaTest {

    @Test
    void autenticaUsuarioComSenhaCorreta() {
        Aluno aluno = new Aluno("1", "Ana", "ana", "segredo", "A1");

        assertTrue(aluno.autenticar("segredo"));
        assertTrue(aluno.autenticar("ana", "segredo"));
        assertTrue(!aluno.autenticar("errada"));
        assertTrue(!aluno.autenticar("outra", "segredo"));
    }

    @Test
    void aplicaLimiteDeQuatroObrigatorias() {
        Aluno aluno = aluno("1");
        PeriodoMatricula periodo = periodoAberto();
        for (int indice = 0; indice < 4; indice++) {
            Disciplina disciplina = new Disciplina("D" + indice, "Disciplina " + indice);
            periodo.adicionarDisciplina(disciplina);
            aluno.matricularEm(disciplina, TipoDisciplina.OBRIGATORIA, periodo, matricula -> {
            });
        }

        Disciplina excedente = new Disciplina("D5", "Excedente");
        periodo.adicionarDisciplina(excedente);
        assertThrows(IllegalStateException.class,
            () -> aluno.matricularEm(excedente, TipoDisciplina.OBRIGATORIA, periodo, matricula -> {
            }));
    }

    @Test
    void aplicaLimiteDeDuasOptativas() {
        Aluno aluno = aluno("1");
        PeriodoMatricula periodo = periodoAberto();
        for (int indice = 0; indice < 2; indice++) {
            Disciplina disciplina = new Disciplina("O" + indice, "Optativa " + indice);
            periodo.adicionarDisciplina(disciplina);
            aluno.matricularEm(disciplina, TipoDisciplina.OPTATIVA, periodo, matricula -> {
            });
        }

        Disciplina excedente = new Disciplina("O3", "Excedente");
        periodo.adicionarDisciplina(excedente);
        assertThrows(IllegalStateException.class,
                () -> aluno.matricularEm(excedente, TipoDisciplina.OPTATIVA, periodo, matricula -> {
                }));
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
            periodo.adicionarDisciplina(disciplina);
            aluno(String.valueOf(indice)).matricularEm(disciplina, TipoDisciplina.OBRIGATORIA, periodo,
                    matricula -> {
                    });
        }

        periodo.encerrar();

        assertEquals(StatusDisciplina.ATIVA, disciplina.getStatus());
        assertTrue(!periodo.estaAberto());
    }

        @Test
        void impedeMatriculaForaDoPeriodoEDisciplinaNaoOfertada() {
        Aluno aluno = aluno("1");
        PeriodoMatricula encerrado = new PeriodoMatricula("2026.1", LocalDate.now().minusDays(2),
            LocalDate.now().minusDays(1));
        Disciplina disciplina = new Disciplina("D1", "Algoritmos");

        assertThrows(IllegalStateException.class,
            () -> aluno.matricularEm(disciplina, TipoDisciplina.OBRIGATORIA, encerrado, matricula -> {
            }));
        }

        @Test
        void exigeCobrancaEImpedeCancelamentoForaDoPeriodo() {
        Aluno aluno = aluno("1");
        Disciplina disciplina = new Disciplina("D1", "Algoritmos");
        PeriodoMatricula periodo = periodoAberto();
        periodo.adicionarDisciplina(disciplina);
        assertThrows(IllegalArgumentException.class,
            () -> aluno.matricularEm(disciplina, TipoDisciplina.OBRIGATORIA, periodo, null));

        AtomicReference<Matricula> notificada = new AtomicReference<>();
        Matricula matricula = aluno.matricularEm(disciplina, TipoDisciplina.OBRIGATORIA, periodo,
            notificada::set);
        periodo.encerrar();
        assertThrows(IllegalStateException.class, () -> aluno.cancelarMatricula(matricula, periodo));
        assertEquals(matricula, notificada.get());
        }

        @Test
        void bloqueiaInscricoesAoAtingirSessentaVagas() {
        PeriodoMatricula periodo = periodoAberto();
        Disciplina disciplina = new Disciplina("D1", "Algoritmos");
        periodo.adicionarDisciplina(disciplina);
        for (int indice = 0; indice < 60; indice++) {
            aluno(String.valueOf(indice)).matricularEm(disciplina, TipoDisciplina.OBRIGATORIA, periodo,
                matricula -> {
                });
        }

        assertEquals(StatusDisciplina.LOTADA, disciplina.getStatus());
        assertThrows(IllegalStateException.class, () -> aluno("61").matricularEm(disciplina,
            TipoDisciplina.OBRIGATORIA, periodo, matricula -> {
            }));
        }

    @Test
    void reabreVagaQuandoMatriculaDaDisciplinaLotadaECancelada() {
        PeriodoMatricula periodo = periodoAberto();
        Disciplina disciplina = new Disciplina("D1", "Algoritmos");
        periodo.adicionarDisciplina(disciplina);
        Aluno ultimoAluno = null;
        for (int indice = 0; indice < 60; indice++) {
            ultimoAluno = aluno(String.valueOf(indice));
            ultimoAluno.matricularEm(disciplina, TipoDisciplina.OBRIGATORIA, periodo, matricula -> {
            });
        }

        ultimoAluno.cancelarMatricula(ultimoAluno.getMatriculas().get(0), periodo);

        assertEquals(StatusDisciplina.EM_OFERTA, disciplina.getStatus());
    }

        @Test
        void professorConsultaApenasDisciplinaQueLeciona() {
        Professor professor = new Professor("P1", "Professor", "prof", "senha", "RF1");
        Disciplina lecionada = new Disciplina("D1", "Algoritmos");
        Disciplina desconhecida = new Disciplina("D2", "Redes");
        professor.adicionarDisciplina(lecionada);

        assertTrue(professor.consultarAlunosMatriculados(lecionada).isEmpty());
        assertThrows(IllegalStateException.class,
            () -> professor.consultarAlunosMatriculados(desconhecida));
        }

        @Test
        void secretariaDefinePeriodoEValidaDatas() {
        Secretaria secretaria = new Secretaria("S1", "Secretaria", "sec", "senha");
        PeriodoMatricula periodo = secretaria.definirPeriodo("2026.2", LocalDate.now().minusDays(1),
            LocalDate.now().plusDays(1));

        assertEquals(periodo, secretaria.getPeriodos().get(0));
        assertThrows(IllegalArgumentException.class,
            () -> secretaria.definirPeriodo("", LocalDate.now(), LocalDate.now()));
        }

    private static Aluno aluno(String id) {
        return new Aluno(id, "Aluno " + id, "aluno" + id, "senha", "RA" + id);
    }

    private static PeriodoMatricula periodoAberto() {
        return new PeriodoMatricula("2026.2", LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
    }
}