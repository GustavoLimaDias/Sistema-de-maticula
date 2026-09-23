package br.pucminas.matriculas.model;

import java.util.Objects;

/**
 * Superclasse de todo usuário do sistema (RN06: todo usuário autentica-se por login e senha).
 * Aluno, Professor e Secretaria herdam desta classe.
 */
public abstract class Usuario {

    private String id;
    private String nome;
    private String login;
    private String senha;

    protected Usuario(String id, String nome, String login, String senha) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
    }

    /**
     * Valida a senha informada contra a senha cadastrada (caso de uso "Efetuar Login").
     */
    public boolean autenticar(String senha) {
        return Objects.equals(this.senha, senha);
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLogin() {
        return login;
    }

    protected String getSenha() {
        return senha;
    }
}
