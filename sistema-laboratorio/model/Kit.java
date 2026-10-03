package model;

import java.util.ArrayList;
import java.util.List;

public class Kit {
    private static int contadorId = 1;

    private int id;
    private String nomeKit;
    private List<Material> materiais;
    private List<Equipamento> equipamentos;

    public Kit(String nomeKit) {
        this.id = contadorId++;
        this.nomeKit = nomeKit;
        this.materiais = new ArrayList<>();
        this.equipamentos = new ArrayList<>();
    }

    public void adicionarMaterial(Material material) {
        materiais.add(material);
    }

    public void adicionarEquipamento(Equipamento equipamento) {
        equipamentos.add(equipamento);
    }

    public void removerMaterial(Material material) {
        materiais.remove(material);
    }

    public boolean verificarDisponibilidade(int quantidadeKits) {
        for (Material m : materiais) {
            if (m.getQuantidadeEstoque() < quantidadeKits) {
                return false;
            }
        }

        for (Equipamento e : equipamentos) {
            if (e.isEmManutencao()) return false;
        }

        return true;
    }

    public int getId() { return id; }
    public String getNomeKit() { return nomeKit; }
    public List<Material> getMateriais() { return materiais; }
    public List<Equipamento> getEquipamentos() { return equipamentos; }

    @Override
    public String toString() {
        return String.format("[KIT-%03d] %s (%d materiais, %d equipamentos)",
                id, nomeKit, materiais.size(), equipamentos.size());
    }
}