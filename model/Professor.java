package model;

public class Professor extends Usuario {
    private String disciplina;

    public Professor(int id, String nome, String cpf, String email, String senha, String disciplina) {
        super(id, nome, cpf, email, senha, PerfilUsuario.PROFESSOR);
        this.disciplina = disciplina;
    }

    public String getDisciplina() { return disciplina; }
}