package model;

public class Material {
    private static int contadorId = 1;

    private int id;
    private String nome;
    private String categoria;
    private int quantidadeEstoque;
    private int estoqueMinimo;

    public Material(String nome, String categoria, int quantidadeInicial, int estoqueMinimo) {
        this.id = contadorId++;
        this.nome = nome;
        this.categoria = categoria;
        this.quantidadeEstoque = quantidadeInicial;
        this.estoqueMinimo = estoqueMinimo;
    }

    public boolean verificarEstoqueMinimo() {
        return quantidadeEstoque <= estoqueMinimo;
    }

    public void darEntrada(int quantidade) {
        this.quantidadeEstoque += quantidade;
    }

    public boolean darBaixa(int quantidade) {
        if (quantidadeEstoque >= quantidade) {
            this.quantidadeEstoque -= quantidade;
            return true;
        }
        return false;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public int getEstoqueMinimo() { return estoqueMinimo; }

    @Override
    public String toString() {
        String alerta = verificarEstoqueMinimo() ? " [ESTOQUE BAIXO]" : "";
        return String.format("[MAT-%03d] %s - Estoque: %d (min: %d)%s",
                id, nome, quantidadeEstoque, estoqueMinimo, alerta);
    }
}
