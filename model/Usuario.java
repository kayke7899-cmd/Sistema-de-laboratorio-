package model;

import java.time.LocalDateTime;

public abstract class Usuario {
    protected int id;
    protected String nome;
    protected String cpf;
    protected String email;
    protected String senha;
    protected PerfilUsuario perfil;
    protected boolean aprovado;
    protected LocalDateTime dataCadastro;

    public Usuario(int id, String nome, String cpf, String email, String senha, PerfilUsuario perfil) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.perfil = perfil;
        this.aprovado = false;
        this.dataCadastro = LocalDateTime.now();
    }

    public boolean fazerLogin(String senhaDigitada) {
        return this.senha.equals(senhaDigitada);
    }

    public void atualizarDados(String nome, String email) {
        this.nome = nome;
        this.email = email;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getEmail() { return email; }
    public PerfilUsuario getPerfil() { return perfil; }
    public boolean isAprovado() { return aprovado; }
    public void setAprovado(boolean aprovado) { this.aprovado = aprovado; }

    @Override
    public String toString() {
        String status = aprovado ? "APROVADO" : "PENDENTE";
        return String.format("[%d] %s (%s) - %s", id, nome, perfil, status);
    }
}