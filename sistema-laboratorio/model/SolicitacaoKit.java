package model;

import java.time.LocalDateTime;

public class SolicitacaoKit {
    private static int contadorId = 1;

    private int id;
    private Agendamento agendamento;
    private Kit kit;
    private int quantidadeKits;
    private StatusSolicitacao status;
    private LocalDateTime dataLimitePreparacao;
    private LocalDateTime dataSolicitacao;

    public SolicitacaoKit(Agendamento agendamento, Kit kit, int quantidadeKits,
                          LocalDateTime dataLimitePreparacao) {
        this.id = contadorId++;
        this.agendamento = agendamento;
        this.kit = kit;
        this.quantidadeKits = quantidadeKits;
        this.dataLimitePreparacao = dataLimitePreparacao;
        this.dataSolicitacao = LocalDateTime.now();
        this.status = StatusSolicitacao.PENDENTE;
    }

    public boolean verificarPrazo() {
        return dataSolicitacao.isBefore(dataLimitePreparacao);
    }

    public void atualizarStatus(StatusSolicitacao novoStatus) {
        this.status = novoStatus;
    }

    public int getId() { return id; }
    public Agendamento getAgendamento() { return agendamento; }
    public Kit getKit() { return kit; }
    public int getQuantidadeKits() { return quantidadeKits; }
    public StatusSolicitacao getStatus() { return status; }
    public LocalDateTime getDataLimitePreparacao() { return dataLimitePreparacao; }

    @Override
    public String toString() {
        return String.format("[SOL-%03d] %s - Qtd: %d - Status: %s - Prazo: %s",
                id, kit.getNomeKit(), quantidadeKits, status, dataLimitePreparacao);
    }
}
