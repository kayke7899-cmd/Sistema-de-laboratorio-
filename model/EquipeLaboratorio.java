package model;

public class EquipeLaboratorio extends Usuario {
    private String cargo;

    public EquipeLaboratorio(int id, String nome, String cpf, String email, String senha, String cargo) {
        super(id, nome, cpf, email, senha, PerfilUsuario.EQUIPE_LABORATORIO);
        this.cargo = cargo;
    }

    public String getCargo() { return cargo; }
}
