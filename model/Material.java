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

    public boolean darEntrada(int quantidade) {
        if (quantidade <= 0) return false;
        this.quantidadeEstoque += quantidade;
        return true;
    }

    public boolean darBaixa(int quantidade) {
        if (quantidade <= 0 || quantidadeEstoque < quantidade) return false;
        this.quantidadeEstoque -= quantidade;
        return true;
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public int getEstoqueMinimo() { return estoqueMinimo; }

    @Override
    public String toString() {
        String alerta = verificarEstoqueMinimo() ? " [ESTOQUE BAIXO]" : "";
        return String.format("[MAT-%03d] %s - Estoque: %d (min: %d)%s",
                id, nome, quantidadeEstoque, estoqueMinimo, alerta);
    }
}
