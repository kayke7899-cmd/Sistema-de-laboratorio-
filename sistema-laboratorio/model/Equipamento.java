package model;

public class Equipamento {
    private static int contadorId = 1;

    private int id;
    private String nome;
    private boolean emManutencao;
    private Laboratorio laboratorioVinculado;

    public Equipamento(String nome) {
        this.id = contadorId++;
        this.nome = nome;
        this.emManutencao = false;
    }

    public void registrarEntradaManutencao() {
        this.emManutencao = true;
    }

    public void liberarParaUso() {
        this.emManutencao = false;
    }

    public boolean testarFuncionamento() {
        return !emManutencao;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public boolean isEmManutencao() { return emManutencao; }
    public Laboratorio getLaboratorioVinculado() { return laboratorioVinculado; }
    public void setLaboratorioVinculado(Laboratorio laboratorio) {
        this.laboratorioVinculado = laboratorio;
    }

    @Override
    public String toString() {
        String status = emManutencao ? "EM MANUTENCAO" : "OPERACIONAL";
        return String.format("[EQP-%03d] %s - %s", id, nome, status);
    }
}