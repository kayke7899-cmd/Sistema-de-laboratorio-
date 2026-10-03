package model;

public class Laboratorio {
    private int id;
    private String nomeSala;
    private int capacidadeMaxima;
    private CondicaoSala condicaoAtual;

    public Laboratorio(int id, String nomeSala, int capacidadeMaxima) {
        this.id = id;
        this.nomeSala = nomeSala;
        this.capacidadeMaxima = capacidadeMaxima;
        this.condicaoAtual = CondicaoSala.DISPONIVEL;
    }

    public boolean verificarDisponibilidade() {
        return condicaoAtual != CondicaoSala.EM_MANUTENCAO &&
               condicaoAtual != CondicaoSala.OCUPADA;
    }

    public void atualizarCondicao(CondicaoSala novaCondicao) {
        this.condicaoAtual = novaCondicao;
    }

    public int getId() { return id; }
    public String getNomeSala() { return nomeSala; }
    public int getCapacidadeMaxima() { return capacidadeMaxima; }
    public CondicaoSala getCondicaoAtual() { return condicaoAtual; }

    @Override
    public String toString() {
        return String.format("[Lab %d] %s - Capacidade: %d - Status: %s",
                id, nomeSala, capacidadeMaxima, condicaoAtual);
    }
}