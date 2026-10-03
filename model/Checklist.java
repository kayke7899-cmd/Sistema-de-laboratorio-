package model;

import java.time.LocalDateTime;

public class Checklist {
    private static int contadorId = 1;

    private int id;
    private Agendamento agendamento;
    private EquipeLaboratorio tecnicoResponsavel;
    private boolean kitsConferidos;
    private boolean equipamentosTestados;
    private boolean concluido;
    private String observacoes;
    private LocalDateTime dataFinalizacao;

    public Checklist(Agendamento agendamento, EquipeLaboratorio tecnico) {
        this.id = contadorId++;
        this.agendamento = agendamento;
        this.tecnicoResponsavel = tecnico;
        this.kitsConferidos = false;
        this.equipamentosTestados = false;
        this.concluido = false;
        this.observacoes = "";
    }

    public void iniciarConferencia() {
        System.out.println("Iniciando conferencia do checklist...");
    }

    public void registrarKitsConferidos() {
        this.kitsConferidos = true;
    }

    public void registrarEquipamentosTestados() {
        this.equipamentosTestados = true;
    }

    public void registrarAvaria(String descricao) {
        this.observacoes += "AVARIA: " + descricao + "\n";
    }

    public boolean finalizar() {
        if (kitsConferidos && equipamentosTestados) {
            this.concluido = true;
            this.dataFinalizacao = LocalDateTime.now();
            return true;
        }
        return false;
    }

    public int getId() { return id; }
    public Agendamento getAgendamento() { return agendamento; }
    public boolean isConcluido() { return concluido; }
    public String getObservacoes() { return observacoes; }

    @Override
    public String toString() {
        String status = concluido ? "CONCLUIDO" : "PENDENTE";
        return String.format("[CHK-%03d] AG-%d | Kits: %s | Eqps: %s | %s",
                id, agendamento.getId(),
                kitsConferidos ? "OK" : "X",
                equipamentosTestados ? "OK" : "X",
                status);
    }
}