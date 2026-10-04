package repository;

import model.*;
import java.util.ArrayList;
import java.util.List;

public class Repositorio {
    private List<Usuario> usuarios = new ArrayList<>();
    private List<Laboratorio> laboratorios = new ArrayList<>();
    private List<Agendamento> agendamentos = new ArrayList<>();
    private List<Kit> kits = new ArrayList<>();
    private List<Material> materiais = new ArrayList<>();
    private List<Equipamento> equipamentos = new ArrayList<>();
    private List<SolicitacaoKit> solicitacoes = new ArrayList<>();
    private List<Checklist> checklists = new ArrayList<>();

    // ===== USUARIOS =====
    public void adicionarUsuario(Usuario u) {
        usuarios.add(u);
    }

    public Usuario buscarPorCpf(String cpf) {
        return usuarios.stream()
                .filter(u -> u.getCpf().equals(cpf))
                .findFirst().orElse(null);
    }

    public Usuario buscarPorId(int id) {
        return usuarios.stream()
                .filter(u -> u.getId() == id)
                .findFirst().orElse(null);
    }

    public List<Usuario> listarUsuariosPendentes() {
        List<Usuario> pendentes = new ArrayList<>();
        for (Usuario u : usuarios) {
            if (!u.isAprovado()) pendentes.add(u);
        }
        return pendentes;
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarios;
    }

    // ===== LABORATORIOS =====
    public void adicionarLaboratorio(Laboratorio l) {
        laboratorios.add(l);
    }

    public Laboratorio buscarLaboratorio(int id) {
        return laboratorios.stream()
                .filter(l -> l.getId() == id)
                .findFirst().orElse(null);
    }

    public List<Laboratorio> listarLaboratorios() {
        return laboratorios;
    }

    // ===== AGENDAMENTOS =====
    public void adicionarAgendamento(Agendamento a) {
        agendamentos.add(a);
    }

    public boolean verificarConflitoHorario(Laboratorio lab, java.time.LocalDate data,
                                            java.time.LocalTime inicio, java.time.LocalTime fim,
                                            int ignorarId) {
        for (Agendamento a : agendamentos) {
            if (a.getLaboratorio().getId() == lab.getId() &&
                a.getDataAula().equals(data) &&
                a.getStatus() != StatusAgendamento.CANCELADO &&
                a.getId() != ignorarId) {

                // Intervalos [inicio, fim) - uma aula que termina as 10:00 nao
                // conflita com outra que comeca as 10:00.
                boolean sobrepoe = inicio.isBefore(a.getHorarioFim()) &&
                                   fim.isAfter(a.getHorarioInicio());

                if (sobrepoe) return true;
            }
        }
        return false;
    }

    public Agendamento buscarAgendamento(int id) {
        return agendamentos.stream()
                .filter(a -> a.getId() == id)
                .findFirst().orElse(null);
    }

    public List<Agendamento> listarAgendamentos() {
        return agendamentos;
    }

    public List<Agendamento> listarAgendamentosPorProfessor(int idProfessor) {
        List<Agendamento> lista = new ArrayList<>();
        for (Agendamento a : agendamentos) {
            if (a.getProfessor().getId() == idProfessor) lista.add(a);
        }
        return lista;
    }

    // ===== KITS =====
    public void adicionarKit(Kit k) {
        kits.add(k);
    }

    public Kit buscarKit(int id) {
        return kits.stream()
                .filter(k -> k.getId() == id)
                .findFirst().orElse(null);
    }

    public List<Kit> listarKits() {
        return kits;
    }

    // ===== MATERIAIS =====
    public void adicionarMaterial(Material m) {
        materiais.add(m);
    }

    public Material buscarMaterial(int id) {
        return materiais.stream()
                .filter(m -> m.getId() == id)
                .findFirst().orElse(null);
    }

    public List<Material> listarMateriais() {
        return materiais;
    }

    public List<Material> listarMateriaisEstoqueBaixo() {
        List<Material> lista = new ArrayList<>();
        for (Material m : materiais) {
            if (m.verificarEstoqueMinimo()) lista.add(m);
        }
        return lista;
    }

    // ===== EQUIPAMENTOS =====
    public void adicionarEquipamento(Equipamento e) {
        equipamentos.add(e);
    }

    public Equipamento buscarEquipamento(int id) {
        return equipamentos.stream()
                .filter(e -> e.getId() == id)
                .findFirst().orElse(null);
    }

    public List<Equipamento> listarEquipamentos() {
        return equipamentos;
    }

    // ===== SOLICITACOES =====
    public void adicionarSolicitacao(SolicitacaoKit s) {
        solicitacoes.add(s);
    }

    public SolicitacaoKit buscarSolicitacao(int id) {
        return solicitacoes.stream()
                .filter(s -> s.getId() == id)
                .findFirst().orElse(null);
    }

    public List<SolicitacaoKit> listarSolicitacoes() {
        return solicitacoes;
    }

    // ===== CHECKLISTS =====
    public void adicionarChecklist(Checklist c) {
        checklists.add(c);
    }

    public List<Checklist> listarChecklists() {
        return checklists;
    }
}