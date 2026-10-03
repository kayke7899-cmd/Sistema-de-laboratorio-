package model;

public class Aluno extends Usuario {
    private String matricula;

    public Aluno(int id, String nome, String cpf, String email, String senha, String matricula) {
        super(id, nome, cpf, email, senha, PerfilUsuario.ALUNO);
        this.matricula = matricula;
    }

    public String getMatricula() { return matricula; }
}