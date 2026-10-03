package model;

public class Coordenacao extends Usuario {
    public Coordenacao(int id, String nome, String cpf, String email, String senha) {
        super(id, nome, cpf, email, senha, PerfilUsuario.COORDENACAO);
        this.aprovado = true;
    }
}
