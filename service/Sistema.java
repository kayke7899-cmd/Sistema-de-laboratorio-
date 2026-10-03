package service;

import model.*;
import repository.Repositorio;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Sistema {
    private Repositorio repo;
    private Usuario usuarioLogado;
    private Scanner sc;

    public Sistema() {
        this.repo = new Repositorio();
        this.sc = new Scanner(System.in);
        carregarDadosIniciais();
    }

    private void carregarDadosIniciais() {
        Coordenacao coord = new Coordenacao(1, "Coordenacao Geral", "000.000.000-00",
                "coord@lab.com", "coord123");
        repo.adicionarUsuario(coord);

        repo.adicionarLaboratorio(new Laboratorio(1, "Lab 01 - Praticas Gerais", 30));
        repo.adicionarLaboratorio(new Laboratorio(2, "Lab 02 - Praticas Neonatais", 25));
        repo.adicionarLaboratorio(new Laboratorio(3, "Lab 03 - Simulacao Avancada", 20));

        repo.adicionarMaterial(new Material("Luvas P", "Insumo", 200, 30));
        repo.adicionarMaterial(new Material("Seringas 5ml", "Insumo", 150, 30));
        repo.adicionarMaterial(new Material("Seringas 10ml", "Insumo", 100, 30));
        repo.adicionarMaterial(new Material("Mascaras", "Insumo", 180, 40));

        Equipamento eqp1 = new Equipamento("Manequim Neonatal");
        Equipamento eqp2 = new Equipamento("Manequim Adulto");
        Equipamento eqp3 = new Equipamento("Desfibrilador");

        repo.adicionarEquipamento(eqp1);
        repo.adicionarEquipamento(eqp2);
        repo.adicionarEquipamento(eqp3);

        Kit kit1 = new Kit("Kit Reanimacao Neonatal");
        kit1.adicionarMaterial(repo.buscarMaterial(1));
        kit1.adicionarMaterial(repo.buscarMaterial(2));
        kit1.adicionarEquipamento(eqp1);
        repo.adicionarKit(kit1);

        Kit kit2 = new Kit("Kit Suporte Basico");
        kit2.adicionarMaterial(repo.buscarMaterial(3));
        kit2.adicionarMaterial(repo.buscarMaterial(4));
        kit2.adicionarEquipamento(eqp2);
        kit2.adicionarEquipamento(eqp3);
        repo.adicionarKit(kit2);
    }

    // ===== METODOS AUXILIARES DE IMPRESSAO =====
    private void imprimirLinha() {
        System.out.println("========================================");
    }

    private void imprimirTitulo(String titulo) {
        System.out.println();
        imprimirLinha();
        System.out.println("   " + titulo);
        imprimirLinha();
    }

    private void imprimirSubtitulo(String titulo) {
        System.out.println();
        System.out.println("--- " + titulo + " ---");
    }

    private void imprimirMensagem(String msg) {
        System.out.println(msg);
    }

    private void imprimirErro(String msg) {
        System.out.println("[ERRO] " + msg);
    }

    private void imprimirSucesso(String msg) {
        System.out.println("[OK] " + msg);
    }

    private void imprimirAviso(String msg) {
        System.out.println("[AVISO] " + msg);
    }

    // ===== LOGIN =====
    public void menuLogin() {
        while (usuarioLogado == null) {
            imprimirTitulo("SISTEMA DO LABORATORIO DE ENFERMAGEM");
            System.out.println("1 - Fazer Login");
            System.out.println("2 - Cadastrar Novo Usuario");
            System.out.println("0 - Sair");
            System.out.print("Opcao: ");
            String op = sc.nextLine();

            switch (op) {
                case "1":
                    fazerLogin();
                    break;

                case "2":
                    cadastrarUsuario();
                    break;

                case "0":
                    imprimirMensagem("Saindo...");
                    System.exit(0);
                    break;

                default:
                    imprimirErro("Opcao invalida!");
            }
        }
    }

    private void fazerLogin() {
        System.out.print("CPF: ");
        String cpf = sc.nextLine();

        System.out.print("Senha: ");
        String senha = sc.nextLine();

        Usuario u = repo.buscarPorCpf(cpf);
        if (u == null) {
            imprimirErro("Usuario nao encontrado!");
            return;
        }

        if (!u.fazerLogin(senha)) {
            imprimirErro("Senha incorreta!");
            return;
        }

        if (!u.isAprovado()) {
            imprimirAviso("Seu cadastro ainda nao foi aprovado pela Coordenacao.");
            return;
        }

        usuarioLogado = u;
        imprimirSucesso("Bem-vindo(a), " + u.getNome() + "!");
    }

    private void cadastrarUsuario() {
        imprimirSubtitulo("CADASTRO DE USUARIO");
        System.out.println("1 - Professor");
        System.out.println("2 - Aluno");
        System.out.println("3 - Equipe de Laboratorio");
        System.out.print("Tipo: ");
        String tipo = sc.nextLine();

        System.out.print("Nome: ");
        String nome = sc.nextLine();

        System.out.print("CPF: ");
        String cpf = sc.nextLine();

        System.out.print("Email: ");
        String email = sc.nextLine();

        System.out.print("Senha: ");
        String senha = sc.nextLine();

        int novoId = repo.listarTodosUsuarios().size() + 1;
        Usuario novo = null;

        switch (tipo) {
            case "1":
                System.out.print("Disciplina: ");
                String disc = sc.nextLine();
                novo = new Professor(novoId, nome, cpf, email, senha, disc);
                break;

            case "2":
                System.out.print("Matricula: ");
                String mat = sc.nextLine();
                novo = new Aluno(novoId, nome, cpf, email, senha, mat);
                break;

            case "3":
                System.out.print("Cargo: ");
                String cargo = sc.nextLine();
                novo = new EquipeLaboratorio(novoId, nome, cpf, email, senha, cargo);
                break;

            default:
                imprimirErro("Tipo invalido!");
                return;
        }

        repo.adicionarUsuario(novo);
        imprimirSucesso("Cadastro realizado! Aguarde aprovacao da Coordenacao.");
    }

    // ===== MENU PRINCIPAL =====
    public void menuPrincipal() {
        while (true) {
            imprimirTitulo("Logado como: " + usuarioLogado.getNome() +
                    " (" + usuarioLogado.getPerfil() + ")");

            switch (usuarioLogado.getPerfil()) {
                case PROFESSOR:
                    menuProfessor();
                    break;

                case ALUNO:
                    menuAluno();
                    break;

                case EQUIPE_LABORATORIO:
                    menuEquipe();
                    break;

                case COORDENACAO:
                    menuCoordenacao();
                    break;
            }
        }
    }

    // ===== MENU PROFESSOR =====
    private void menuProfessor() {
        System.out.println("1 - Agendar Aula Pratica (UC01)");
        System.out.println("2 - Solicitar Kit de Insumos (UC02)");
        System.out.println("3 - Consultar Agenda");
        System.out.println("4 - Cancelar Agendamento");
        System.out.println("5 - Registrar Condicao da Sala");
        System.out.println("6 - Meus Dados");
        System.out.println("0 - Sair");
        System.out.print("Opcao: ");
        String op = sc.nextLine();

        switch (op) {
            case "1":
                agendarAula();
                break;

            case "2":
                solicitarKit();
                break;

            case "3":
                consultarAgenda();
                break;

            case "4":
                cancelarAgendamento();
                break;

            case "5":
                registrarCondicaoSala();
                break;

            case "6":
                imprimirMensagem(usuarioLogado.toString());
                break;

            case "0":
                usuarioLogado = null;
                break;

            default:
                imprimirErro("Opcao invalida!");
        }
    }

    private void agendarAula() {
        imprimirSubtitulo("AGENDAR AULA PRATICA (UC01)");
        imprimirMensagem("Laboratorios disponiveis:");

        for (Laboratorio l : repo.listarLaboratorios()) {
            System.out.println("  " + l);
        }

        System.out.print("ID do Laboratorio: ");
        int idLab = Integer.parseInt(sc.nextLine());
        Laboratorio lab = repo.buscarLaboratorio(idLab);

        if (lab == null) {
            imprimirErro("Laboratorio nao encontrado!");
            return;
        }

        if (lab.getCondicaoAtual() == CondicaoSala.EM_MANUTENCAO) {
            imprimirErro("Laboratorio em manutencao! Nao e possivel agendar.");
            return;
        }

        System.out.print("Data da aula (dd/MM/yyyy): ");
        LocalDate data = LocalDate.parse(sc.nextLine(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        System.out.print("Horario inicio (HH:mm): ");
        LocalTime inicio = LocalTime.parse(sc.nextLine());

        System.out.print("Horario fim (HH:mm): ");
        LocalTime fim = LocalTime.parse(sc.nextLine());

        if (data.isBefore(LocalDate.now())) {
            imprimirErro("RN09: Nao e possivel agendar em data passada!");
            return;
        }

        if (repo.verificarConflitoHorario(lab, data, inicio, fim, 0)) {
            imprimirErro("RN02: Horario ja reservado para este laboratorio!");
            return;
        }

        System.out.print("Disciplina: ");
        String disc = sc.nextLine();

        System.out.print("Area Tematica: ");
        String area = sc.nextLine();

        System.out.print("Turma: ");
        String turma = sc.nextLine();

        System.out.print("Quantidade de alunos: ");
        int qtdAlunos = Integer.parseInt(sc.nextLine());

        if (qtdAlunos > lab.getCapacidadeMaxima()) {
            imprimirErro("RN06: Quantidade de alunos excede a capacidade da sala (" +
                    lab.getCapacidadeMaxima() + ")! Converse com a coordenacao.");
            return;
        }

        System.out.print("Observacoes (opcional): ");
        String obs = sc.nextLine();

        Agendamento ag = new Agendamento((Professor) usuarioLogado, lab, disc, area,
                turma, data, inicio, fim, qtdAlunos, obs);

        if (!ag.verificarAntecedenciaMinima()) {
            imprimirAviso("RN01: Agendamento fora do prazo (minimo 7 dias).");
            imprimirMensagem("   Status definido como PENDENTE - aguardando aprovacao da Coordenacao.");
            ag.setStatus(StatusAgendamento.PENDENTE);
        } else {
            ag.confirmarReserva();
        }

        repo.adicionarAgendamento(ag);
        imprimirSucesso("Agendamento realizado! ID: AG-" + ag.getId());
        imprimirMensagem("Equipe do Laboratorio foi notificada.");
    }

    private void solicitarKit() {
        imprimirSubtitulo("SOLICITAR KIT DE INSUMOS (UC02)");
        List<Agendamento> meusAg = repo.listarAgendamentosPorProfessor(usuarioLogado.getId());

        if (meusAg.isEmpty()) {
            imprimirErro("Voce nao possui agendamentos!");
            return;
        }

        imprimirMensagem("Seus agendamentos:");
        for (Agendamento a : meusAg) {
            System.out.println("  " + a);
        }

        System.out.print("ID do Agendamento: ");
        int idAg = Integer.parseInt(sc.nextLine());

        Agendamento ag = null;
        for (Agendamento a : meusAg) {
            if (a.getId() == idAg) {
                ag = a;
                break;
            }
        }

        if (ag == null) {
            imprimirErro("Agendamento nao encontrado!");
            return;
        }

        imprimirMensagem("\nKits disponiveis:");
        for (Kit k : repo.listarKits()) {
            System.out.println("  " + k);
        }

        System.out.print("ID do Kit: ");
        int idKit = Integer.parseInt(sc.nextLine());
        Kit kit = repo.buscarKit(idKit);

        if (kit == null) {
            imprimirErro("Kit nao encontrado!");
            return;
        }

        System.out.print("Quantidade de kits: ");
        int qtd = Integer.parseInt(sc.nextLine());

        if (!kit.verificarDisponibilidade(qtd)) {
            imprimirErro("Estoque insuficiente ou equipamento em manutencao!");
            return;
        }

        System.out.print("Data/hora limite para preparacao (dd/MM/yyyy HH:mm): ");
        String[] partes = sc.nextLine().split(" ");
        LocalDateTime prazo = LocalDateTime.of(
                LocalDate.parse(partes[0], DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                LocalTime.parse(partes[1]));

        SolicitacaoKit sol = new SolicitacaoKit(ag, kit, qtd, prazo);

        if (!sol.verificarPrazo()) {
            imprimirAviso("RN04: Solicitacao fora do prazo! Pendente de aprovacao.");
            sol.atualizarStatus(StatusSolicitacao.PENDENTE);
        } else {
            sol.atualizarStatus(StatusSolicitacao.RESERVADO);
        }

        repo.adicionarSolicitacao(sol);
        imprimirSucesso("Solicitacao registrada! ID: SOL-" + sol.getId());
    }

    private void consultarAgenda() {
        imprimirSubtitulo("AGENDA DE AULAS PRATICAS");
        List<Agendamento> lista;

        if (usuarioLogado.getPerfil() == PerfilUsuario.PROFESSOR) {
            lista = repo.listarAgendamentosPorProfessor(usuarioLogado.getId());
        } else {
            lista = repo.listarAgendamentos();
        }

        if (lista.isEmpty()) {
            imprimirMensagem("Nenhum agendamento encontrado.");
            return;
        }

        for (Agendamento a : lista) {
            System.out.println(a);
        }
    }

    private void cancelarAgendamento() {
        imprimirSubtitulo("CANCELAR AGENDAMENTO");
        consultarAgenda();

        System.out.print("ID do Agendamento para cancelar: ");
        int id = Integer.parseInt(sc.nextLine());

        for (Agendamento a : repo.listarAgendamentos()) {
            if (a.getId() == id) {
                a.cancelarReserva();
                imprimirSucesso("Agendamento cancelado!");
                return;
            }
        }

        imprimirErro("Agendamento nao encontrado!");
    }

    private void registrarCondicaoSala() {
        imprimirSubtitulo("REGISTRAR CONDICAO DA SALA");
        consultarAgenda();

        System.out.print("ID do Agendamento: ");
        int id = Integer.parseInt(sc.nextLine());

        imprimirMensagem("Condicao: 1-Boa  2-Razoavel  3-Ruim");
        System.out.print("Opcao: ");
        String op = sc.nextLine();
        CondicaoSala cond;

        switch (op) {
            case "1":
                cond = CondicaoSala.BOA;
                break;

            case "2":
                cond = CondicaoSala.RAZOAVEL;
                break;

            case "3":
                cond = CondicaoSala.RUIM;
                break;

            default:
                imprimirErro("Opcao invalida!");
                return;
        }

        for (Agendamento a : repo.listarAgendamentos()) {
            if (a.getId() == id) {
                a.getLaboratorio().atualizarCondicao(cond);
                imprimirSucesso("Condicao registrada!");
                return;
            }
        }
    }

    // ===== MENU ALUNO =====
    private void menuAluno() {
        System.out.println("1 - Consultar Agenda de Aulas");
        System.out.println("2 - Meus Dados");
        System.out.println("0 - Sair");
        System.out.print("Opcao: ");
        String op = sc.nextLine();

        switch (op) {
            case "1":
                consultarAgenda();
                break;

            case "2":
                imprimirMensagem(usuarioLogado.toString());
                break;

            case "0":
                usuarioLogado = null;
                break;

            default:
                imprimirErro("Opcao invalida!");
        }
    }

    // ===== MENU EQUIPE =====
    private void menuEquipe() {
        System.out.println("1 - Realizar Checklist (UC03)");
        System.out.println("2 - Controlar Estoque (UC04)");
        System.out.println("3 - Consultar Agenda Geral");
        System.out.println("4 - Ver Solicitacoes de Kits");
        System.out.println("5 - Registrar Manutencao de Equipamento");
        System.out.println("0 - Sair");
        System.out.print("Opcao: ");
        String op = sc.nextLine();

        switch (op) {
            case "1":
                realizarChecklist();
                break;

            case "2":
                controlarEstoque();
                break;

            case "3":
                consultarAgenda();
                break;

            case "4":
                verSolicitacoes();
                break;

            case "5":
                registrarManutencao();
                break;

            case "0":
                usuarioLogado = null;
                break;

            default:
                imprimirErro("Opcao invalida!");
        }
    }

    private void realizarChecklist() {
        imprimirSubtitulo("REALIZAR CHECKLIST (UC03)");
        consultarAgenda();

        System.out.print("ID do Agendamento: ");
        int id = Integer.parseInt(sc.nextLine());
        Agendamento ag = null;

        for (Agendamento a : repo.listarAgendamentos()) {
            if (a.getId() == id) {
                ag = a;
                break;
            }
        }

        if (ag == null) {
            imprimirErro("Agendamento nao encontrado!");
            return;
        }

        Checklist chk = new Checklist(ag, (EquipeLaboratorio) usuarioLogado);
        chk.iniciarConferencia();

        System.out.print("Kits conferidos? (s/n): ");
        if (sc.nextLine().equalsIgnoreCase("s")) chk.registrarKitsConferidos();

        System.out.print("Equipamentos testados? (s/n): ");
        if (sc.nextLine().equalsIgnoreCase("s")) chk.registrarEquipamentosTestados();

        System.out.print("Registrar avaria? (descricao ou vazio): ");
        String avaria = sc.nextLine();
        if (!avaria.isEmpty()) chk.registrarAvaria(avaria);

        if (chk.finalizar()) {
            repo.adicionarChecklist(chk);
            imprimirSucesso("Checklist concluido! Sala liberada para aula.");
        } else {
            imprimirAviso("RN08: Checklist incompleto! Pendencia registrada.");
        }
    }

    private void controlarEstoque() {
        imprimirSubtitulo("CONTROLAR ESTOQUE (UC04)");
        System.out.println("1 - Dar Entrada");
        System.out.println("2 - Dar Baixa");
        System.out.println("3 - Listar Materiais");
        System.out.print("Opcao: ");
        String op = sc.nextLine();

        switch (op) {
            case "1":
                imprimirMensagem("Materiais:");
                for (Material m : repo.listarMateriais()) System.out.println("  " + m);

                System.out.print("ID do Material: ");
                int idE = Integer.parseInt(sc.nextLine());
                Material matE = repo.buscarMaterial(idE);

                if (matE == null) {
                    imprimirErro("Nao encontrado!");
                    return;
                }

                System.out.print("Quantidade: ");
                matE.darEntrada(Integer.parseInt(sc.nextLine()));
                imprimirSucesso("Entrada registrada!");
                verificarAlertaEstoque();
                break;

            case "2":
                imprimirMensagem("Materiais:");
                for (Material m : repo.listarMateriais()) System.out.println("  " + m);

                System.out.print("ID do Material: ");
                int idS = Integer.parseInt(sc.nextLine());
                Material matS = repo.buscarMaterial(idS);

                if (matS == null) {
                    imprimirErro("Nao encontrado!");
                    return;
                }

                System.out.print("Quantidade: ");
                int qtd = Integer.parseInt(sc.nextLine());

                if (matS.darBaixa(qtd)) {
                    imprimirSucesso("Baixa registrada!");
                } else {
                    imprimirErro("Estoque insuficiente!");
                }

                verificarAlertaEstoque();
                break;

            case "3":
                for (Material m : repo.listarMateriais()) System.out.println(m);
                break;

            default:
                imprimirErro("Opcao invalida!");
        }
    }

    private void verificarAlertaEstoque() {
        List<Material> baixo = repo.listarMateriaisEstoqueBaixo();

        if (!baixo.isEmpty()) {
            imprimirAviso("RN05: ALERTA DE ESTOQUE MINIMO!");
            for (Material m : baixo) System.out.println("  " + m);
            imprimirMensagem("Coordenacao e equipe notificadas.");
        }
    }

    private void verSolicitacoes() {
        imprimirSubtitulo("SOLICITACOES DE KITS");
        for (SolicitacaoKit s : repo.listarSolicitacoes()) {
            System.out.println(s);
        }
    }

    private void registrarManutencao() {
        imprimirSubtitulo("REGISTRAR MANUTENCAO");

        for (Equipamento e : repo.listarEquipamentos()) {
            System.out.println(e);
        }

        System.out.print("ID do Equipamento: ");
        int id = Integer.parseInt(sc.nextLine());
        Equipamento eqp = repo.buscarEquipamento(id);

        if (eqp == null) {
            imprimirErro("Nao encontrado!");
            return;
        }

        eqp.registrarEntradaManutencao();
        imprimirMensagem("Equipamento em manutencao!");
        imprimirMensagem("RN07: Aulas agendadas serao notificadas.");
    }

    // ===== MENU COORDENACAO =====
    private void menuCoordenacao() {
        System.out.println("1 - Aprovar Cadastros Pendentes (UC05)");
        System.out.println("2 - Gerenciar Laboratorios");
        System.out.println("3 - Gerenciar Kits");
        System.out.println("4 - Consultar Agenda Geral");
        System.out.println("5 - Aprovar Solicitacoes Extemporaneas");
        System.out.println("6 - Listar Usuarios");
        System.out.println("7 - Relatorios");
        System.out.println("0 - Sair");
        System.out.print("Opcao: ");
        String op = sc.nextLine();

        switch (op) {
            case "1":
                aprovarCadastros();
                break;

            case "2":
                gerenciarLaboratorios();
                break;

            case "3":
                gerenciarKits();
                break;

            case "4":
                consultarAgenda();
                break;

            case "5":
                aprovarSolicitacoesExtemporaneas();
                break;

            case "6":
                for (Usuario u : repo.listarTodosUsuarios()) System.out.println(u);
                break;

            case "7":
                gerarRelatorios();
                break;

            case "0":
                usuarioLogado = null;
                break;

            default:
                imprimirErro("Opcao invalida!");
        }
    }

    private void aprovarCadastros() {
        imprimirSubtitulo("APROVAR CADASTROS (UC05)");
        List<Usuario> pendentes = repo.listarUsuariosPendentes();

        if (pendentes.isEmpty()) {
            imprimirMensagem("Nao ha cadastros pendentes.");
            return;
        }

        for (Usuario u : pendentes) System.out.println(u);

        System.out.print("ID do usuario para aprovar (0 = cancelar): ");
        int id = Integer.parseInt(sc.nextLine());

        if (id == 0) return;

        Usuario u = repo.buscarPorId(id);
        if (u != null) {
            u.setAprovado(true);
            imprimirSucesso("Usuario aprovado!");
        }
    }

    private void gerenciarLaboratorios() {
        imprimirSubtitulo("GERENCIAR LABORATORIOS");
        System.out.println("1 - Cadastrar Laboratorio");
        System.out.println("2 - Listar Laboratorios");
        System.out.print("Opcao: ");
        String op = sc.nextLine();

        if (op.equals("1")) {
            System.out.print("Nome: ");
            String nome = sc.nextLine();

            System.out.print("Capacidade: ");
            int cap = Integer.parseInt(sc.nextLine());

            int novoId = repo.listarLaboratorios().size() + 1;
            repo.adicionarLaboratorio(new Laboratorio(novoId, nome, cap));
            imprimirSucesso("Laboratorio cadastrado!");
        } else {
            for (Laboratorio l : repo.listarLaboratorios()) System.out.println(l);
        }
    }

    private void gerenciarKits() {
        imprimirSubtitulo("GERENCIAR KITS");
        System.out.println("1 - Criar Novo Kit");
        System.out.println("2 - Listar Kits");
        System.out.print("Opcao: ");
        String op = sc.nextLine();

        if (op.equals("1")) {
            System.out.print("Nome do Kit: ");
            String nome = sc.nextLine();
            Kit kit = new Kit(nome);

            imprimirMensagem("Materiais disponiveis:");
            for (Material m : repo.listarMateriais()) System.out.println("  " + m);

            System.out.print("IDs dos materiais (separados por virgula): ");
            String[] ids = sc.nextLine().split(",");

            for (String idStr : ids) {
                Material m = repo.buscarMaterial(Integer.parseInt(idStr.trim()));
                if (m != null) kit.adicionarMaterial(m);
            }

            repo.adicionarKit(kit);
            imprimirSucesso("Kit criado!");
        } else {
            for (Kit k : repo.listarKits()) System.out.println(k);
        }
    }

    private void aprovarSolicitacoesExtemporaneas() {
        imprimirSubtitulo("APROVAR SOLICITACOES EXTEMPORANEAS (RN04)");

        for (SolicitacaoKit s : repo.listarSolicitacoes()) {
            if (s.getStatus() == StatusSolicitacao.PENDENTE) {
                System.out.println(s);
            }
        }

        System.out.print("ID da Solicitacao para aprovar (0 = cancelar): ");
        int id = Integer.parseInt(sc.nextLine());

        if (id == 0) return;

        for (SolicitacaoKit s : repo.listarSolicitacoes()) {
            if (s.getId() == id) {
                s.atualizarStatus(StatusSolicitacao.APROVADO);
                imprimirSucesso("Solicitacao aprovada!");
                return;
            }
        }

        imprimirErro("Nao encontrada!");
    }

    private void gerarRelatorios() {
        imprimirSubtitulo("RELATORIOS");
        System.out.println("Total de Usuarios: " + repo.listarTodosUsuarios().size());
        System.out.println("Total de Agendamentos: " + repo.listarAgendamentos().size());
        System.out.println("Total de Kits: " + repo.listarKits().size());
        System.out.println("Materiais com estoque baixo: " + repo.listarMateriaisEstoqueBaixo().size());
        System.out.println("Checklists realizados: " + repo.listarChecklists().size());
        System.out.println("================================");
    }
}