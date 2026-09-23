package br.pucminas.matriculas.servico;

import br.pucminas.matriculas.model.Matricula;

/**
 * Fronteira de integração com o Sistema de Cobrança (ator externo do diagrama de casos de uso).
 * Caso de uso "Notificar Sistema de Cobrança" (RN05): toda matrícula concluída deve
 * chamar {@link #notificarMatricula(Matricula)} para que o aluno seja cobrado.
 *
 * A implementação concreta (ex.: chamada HTTP, fila de mensagens, arquivo) fica para a Sprint 3,
 * quando a integração real (ou um stub de persistência em arquivo) for definida.
 */
public interface ServicoCobranca {

    void notificarMatricula(Matricula matricula);
}
