package service;

import model.*;
import repository.Repositorio;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Scanner;

public class Sistema {
    // STRICT + "uuuu": rejeita datas inexistentes como 31/02/2026
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter FORMATO_DATA_HORA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

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

        // Referencias diretas (nao depende mais da ordem dos IDs gerados pelo contador estatico)
        Material luvas = new Material("Luvas P", "Insumo", 200, 30);
        Material seringa5 = new Material("Seringas 5ml", "Insumo", 150, 30);
        Material seringa10 = new Material("Seringas 10ml", "Insumo", 100, 30);
        Material mascaras = new Material("Mascaras", "Insumo", 180, 40);
        repo.adicionarMaterial(luvas);
        repo.adicionarMaterial(seringa5);
        repo.adicionarMaterial(seringa10);
        repo.adicionarMaterial(mascaras);

        Equipamento eqp1 = new Equipamento("Manequim Neonatal");
        Equipamento eqp2 = new Equipamento("Manequim Adulto");
        Equipamento eqp3 = new Equipamento("Desfibrilador");
        repo.adicionarEquipamento(eqp1);
        repo.adicionarEquipamento(eqp2);
        repo.adicionarEquipamento(eqp3);

        Kit kit1 = new Kit("Kit Reanimacao Neonatal");
        kit1.adicionarMaterial(luvas);
        kit1.adicionarMaterial(seringa5);
        kit1.adicionarEquipamento(eqp1);
        repo.adicionarKit(kit1);

        Kit kit2 = new Kit("Kit Suporte Basico");
        kit2.adicionarMaterial(seringa10);
        kit2.adicionarMaterial(mascaras);
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

    // ===== METODOS AUXILIARES DE LEITURA (validam a entrada em vez de quebrar o programa) =====
    private String lerLinha(String prompt) {
        System.out.print(prompt);
        if (!sc.hasNextLine()) {
            System.out.println();
            imprimirMensagem("Entrada encerrada. Saindo...");
            System.exit(0);
        }
        return sc.nextLine();
    }

    private String lerTexto(String prompt) {
        return lerLinha(prompt).trim();
    }

    private String lerTextoObrigatorio(String prompt) {
        String texto = lerTexto(prompt);
        while (texto.isEmpty()) {
            imprimirErro("Este campo nao pode ficar vazio.");
            texto = lerTexto(prompt);
        }
        return texto;
    }

    private int lerInteiro(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(lerTexto(prompt));
            } catch (NumberFormatException e) {
                imprimirErro("Digite um numero inteiro valido.");
            }
        }
    }

    private LocalDate lerData(String prompt) {
        while (true) {
            try {
                return LocalDate.parse(lerTexto(prompt), FORMATO_DATA);
            } catch (DateTimeParseException e) {
                imprimirErro("Data invalida! Use o formato dd/MM/yyyy (ex: 25/12/2026).");
            }
        }
    }

    private LocalTime lerHora(String prompt) {
        while (true) {
            try {
                return LocalTime.parse(lerTexto(prompt));
            } catch (DateTimeParseException e) {
                imprimirErro("Horario invalido! Use o formato HH:mm (ex: 08:30).");
            }
        }
    }

    private LocalDateTime lerDataHora(String prompt) {
        while (true) {
            try {
                return LocalDateTime.parse(lerTexto(prompt), FORMATO_DATA_HORA);
            } catch (DateTimeParseException e) {
                imprimirErro("Data/hora invalida! Use o formato dd/MM/yyyy HH:mm (ex: 25/12/2026 08:00).");
            }
        }
    }

    // ===== LOGIN =====
    public void menuLogin() {
        while (usuarioLogado == null) {
            imprimirTitulo("SISTEMA DO LABORATORIO DE ENFERMAGEM");
            System.out.println("1 - Fazer Login");
            System.out.println("2 - Cadastrar Novo Usuario");
            System.out.println("0 - Sair");
            String op = lerTexto("Opcao: ");

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
        String cpf = lerTexto("CPF: ");
        String senha = lerLinha("Senha: ");

        Usuario u = repo.buscarPorCpf(cpf);

        // Mensagem unica: nao revela se o CPF existe ou nao
        if (u == null || !u.fazerLogin(senha)) {
            imprimirErro("CPF ou senha invalidos!");
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
        String tipo = lerTexto("Tipo: ");

        if (!tipo.equals("1") && !tipo.equals("2") && !tipo.equals("3")) {
            imprimirErro("Tipo invalido!");
            return;
        }

        String nome = lerTextoObrigatorio("Nome: ");
        String cpf = lerTextoObrigatorio("CPF: ");

        if (repo.buscarPorCpf(cpf) != null) {
            imprimirErro("Ja existe um usuario cadastrado com este CPF!");
            return;
        }

        String email = lerTextoObrigatorio("Email: ");
        if (!email.contains("@")) {
            imprimirErro("Email invalido!");
            return;
        }

        String senha = lerLinha("Senha: ");
        while (senha.isEmpty()) {
            imprimirErro("A senha nao pode ficar vazia.");
            senha = lerLinha("Senha: ");
        }

        int novoId = repo.listarTodosUsuarios().size() + 1;
        Usuario novo;

        switch (tipo) {
            case "1":
                novo = new Professor(novoId, nome, cpf, email, senha,
                        lerTextoObrigatorio("Disciplina: "));
                break;

            case "2":
                novo = new Aluno(novoId, nome, cpf, email, senha,
                        lerTextoObrigatorio("Matricula: "));
                break;

            default:
                novo = new EquipeLaboratorio(novoId, nome, cpf, email, senha,
                        lerTextoObrigatorio("Cargo: "));
                break;
        }

        repo.adicionarUsuario(novo);
        imprimirSucesso("Cadastro realizado! Aguarde aprovacao da Coordenacao.");
    }

    // ===== MENU PRINCIPAL =====
    public void menuPrincipal() {
        while (true) {
            // Depois de "Sair" o usuarioLogado fica null: volta para a tela de login
            // (antes disso causava NullPointerException).
            if (usuarioLogado == null) {
                menuLogin();
                continue;
            }

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
        String op = lerTexto("Opcao: ");

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

        int idLab = lerInteiro("ID do Laboratorio: ");
        Laboratorio lab = repo.buscarLaboratorio(idLab);

        if (lab == null) {
            imprimirErro("Laboratorio nao encontrado!");
            return;
        }

        if (lab.getCondicaoAtual() == CondicaoSala.EM_MANUTENCAO) {
            imprimirErro("Laboratorio em manutencao! Nao e possivel agendar.");
            return;
        }

        LocalDate data = lerData("Data da aula (dd/MM/yyyy): ");

        if (data.isBefore(LocalDate.now())) {
            imprimirErro("RN09: Nao e possivel agendar em data passada!");
            return;
        }

        LocalTime inicio = lerHora("Horario inicio (HH:mm): ");
        LocalTime fim = lerHora("Horario fim (HH:mm): ");

        if (!fim.isAfter(inicio)) {
            imprimirErro("O horario de fim deve ser posterior ao horario de inicio!");
            return;
        }

        if (repo.verificarConflitoHorario(lab, data, inicio, fim, 0)) {
            imprimirErro("RN02: Horario ja reservado para este laboratorio!");
            return;
        }

        String disc = lerTextoObrigatorio("Disciplina: ");
        String area = lerTextoObrigatorio("Area Tematica: ");
        String turma = lerTextoObrigatorio("Turma: ");

        int qtdAlunos = lerInteiro("Quantidade de alunos: ");

        if (qtdAlunos <= 0) {
            imprimirErro("A quantidade de alunos deve ser maior que zero!");
            return;
        }

        if (qtdAlunos > lab.getCapacidadeMaxima()) {
            imprimirErro("RN06: Quantidade de alunos excede a capacidade da sala (" +
                    lab.getCapacidadeMaxima() + ")! Converse com a coordenacao.");
            return;
        }

        String obs = lerTexto("Observacoes (opcional): ");

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

        int idAg = lerInteiro("ID do Agendamento: ");

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

        if (ag.getStatus() == StatusAgendamento.CANCELADO) {
            imprimirErro("Este agendamento foi cancelado. Nao e possivel solicitar kits.");
            return;
        }

        imprimirMensagem("\nKits disponiveis:");
        for (Kit k : repo.listarKits()) {
            System.out.println("  " + k);
        }

        int idKit = lerInteiro("ID do Kit: ");
        Kit kit = repo.buscarKit(idKit);

        if (kit == null) {
            imprimirErro("Kit nao encontrado!");
            return;
        }

        int qtd = lerInteiro("Quantidade de kits: ");

        if (qtd <= 0) {
            imprimirErro("A quantidade de kits deve ser maior que zero!");
            return;
        }

        if (!kit.verificarDisponibilidade(qtd)) {
            imprimirErro("Estoque insuficiente ou equipamento em manutencao!");
            return;
        }

        LocalDateTime prazo = lerDataHora("Data/hora limite para preparacao (dd/MM/yyyy HH:mm): ");

        LocalDateTime inicioAula = LocalDateTime.of(ag.getDataAula(), ag.getHorarioInicio());
        if (!prazo.isBefore(inicioAula)) {
            imprimirErro("O prazo de preparacao deve ser anterior ao inicio da aula (" + inicioAula + ")!");
            return;
        }

        SolicitacaoKit sol = new SolicitacaoKit(ag, kit, qtd, prazo);

        if (!sol.verificarPrazo()) {
            imprimirAviso("RN04: Solicitacao fora do prazo! Pendente de aprovacao.");
            sol.atualizarStatus(StatusSolicitacao.PENDENTE);
        } else {
            sol.atualizarStatus(StatusSolicitacao.RESERVADO);
        }

        repo.adicionarSolicitacao(sol);
        imprimirSucesso("Solicitacao registrada! ID: " + String.format("SOL-%03d", sol.getId()));
    }

    /** Mostra a agenda do usuario logado. Retorna false se nao houver nada para mostrar. */
    private boolean consultarAgenda() {
        imprimirSubtitulo("AGENDA DE AULAS PRATICAS");
        List<Agendamento> lista;

        if (usuarioLogado.getPerfil() == PerfilUsuario.PROFESSOR) {
            lista = repo.listarAgendamentosPorProfessor(usuarioLogado.getId());
        } else {
            lista = repo.listarAgendamentos();
        }

        if (lista.isEmpty()) {
            imprimirMensagem("Nenhum agendamento encontrado.");
            return false;
        }

        for (Agendamento a : lista) {
            System.out.println(a);
        }
        return true;
    }

    /** Busca um agendamento que o usuario logado tem permissao de operar. */
    private Agendamento buscarAgendamentoPermitido(int id) {
        Agendamento ag = repo.buscarAgendamento(id);
        if (ag == null) return null;

        boolean donoDoAgendamento = ag.getProfessor().getId() == usuarioLogado.getId();
        boolean professor = usuarioLogado.getPerfil() == PerfilUsuario.PROFESSOR;

        // Professor so mexe nos proprios agendamentos
        if (professor && !donoDoAgendamento) return null;
        return ag;
    }

    private void cancelarAgendamento() {
        imprimirSubtitulo("CANCELAR AGENDAMENTO");
        if (!consultarAgenda()) return;

        int id = lerInteiro("ID do Agendamento para cancelar: ");
        Agendamento ag = buscarAgendamentoPermitido(id);

        if (ag == null) {
            imprimirErro("Agendamento nao encontrado!");
            return;
        }

        if (ag.getStatus() == StatusAgendamento.CANCELADO) {
            imprimirAviso("Este agendamento ja esta cancelado.");
            return;
        }

        ag.cancelarReserva();
        imprimirSucesso("Agendamento cancelado!");
    }

    private void registrarCondicaoSala() {
        imprimirSubtitulo("REGISTRAR CONDICAO DA SALA");
        if (!consultarAgenda()) return;

        int id = lerInteiro("ID do Agendamento: ");
        Agendamento ag = buscarAgendamentoPermitido(id);

        if (ag == null) {
            imprimirErro("Agendamento nao encontrado!");
            return;
        }

        imprimirMensagem("Condicao: 1-Boa  2-Razoavel  3-Ruim");
        String op = lerTexto("Opcao: ");
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

        ag.getLaboratorio().atualizarCondicao(cond);
        imprimirSucesso("Condicao registrada!");
    }

    // ===== MENU ALUNO =====
    private void menuAluno() {
        System.out.println("1 - Consultar Agenda de Aulas");
        System.out.println("2 - Meus Dados");
        System.out.println("0 - Sair");
        String op = lerTexto("Opcao: ");

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
        System.out.println("6 - Liberar Equipamento da Manutencao");
        System.out.println("0 - Sair");
        String op = lerTexto("Opcao: ");

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

            case "6":
                liberarEquipamento();
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
        if (!consultarAgenda()) return;

        int id = lerInteiro("ID do Agendamento: ");
        Agendamento ag = repo.buscarAgendamento(id);

        if (ag == null) {
            imprimirErro("Agendamento nao encontrado!");
            return;
        }

        if (ag.getStatus() == StatusAgendamento.CANCELADO) {
            imprimirErro("Este agendamento foi cancelado. Nao ha checklist a realizar.");
            return;
        }

        for (Checklist existente : repo.listarChecklists()) {
            if (existente.getAgendamento().getId() == ag.getId() && existente.isConcluido()) {
                imprimirAviso("Este agendamento ja possui um checklist concluido.");
                return;
            }
        }

        Checklist chk = new Checklist(ag, (EquipeLaboratorio) usuarioLogado);
        chk.iniciarConferencia();

        if (lerTexto("Kits conferidos? (s/n): ").equalsIgnoreCase("s")) chk.registrarKitsConferidos();
        if (lerTexto("Equipamentos testados? (s/n): ").equalsIgnoreCase("s")) chk.registrarEquipamentosTestados();

        String avaria = lerTexto("Registrar avaria? (descricao ou vazio): ");
        if (!avaria.isEmpty()) chk.registrarAvaria(avaria);

        // O checklist e guardado sempre; se estiver incompleto, aparece como PENDENTE.
        // (antes, o aviso dizia "pendencia registrada" mas o objeto era descartado)
        boolean concluido = chk.finalizar();
        repo.adicionarChecklist(chk);

        if (concluido) {
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
        String op = lerTexto("Opcao: ");

        switch (op) {
            case "1": {
                imprimirMensagem("Materiais:");
                for (Material m : repo.listarMateriais()) System.out.println("  " + m);

                Material mat = repo.buscarMaterial(lerInteiro("ID do Material: "));
                if (mat == null) {
                    imprimirErro("Nao encontrado!");
                    return;
                }

                if (mat.darEntrada(lerInteiro("Quantidade: "))) {
                    imprimirSucesso("Entrada registrada!");
                } else {
                    imprimirErro("Quantidade invalida! Informe um valor maior que zero.");
                }
                verificarAlertaEstoque();
                break;
            }

            case "2": {
                imprimirMensagem("Materiais:");
                for (Material m : repo.listarMateriais()) System.out.println("  " + m);

                Material mat = repo.buscarMaterial(lerInteiro("ID do Material: "));
                if (mat == null) {
                    imprimirErro("Nao encontrado!");
                    return;
                }

                if (mat.darBaixa(lerInteiro("Quantidade: "))) {
                    imprimirSucesso("Baixa registrada!");
                } else {
                    imprimirErro("Quantidade invalida ou estoque insuficiente!");
                }
                verificarAlertaEstoque();
                break;
            }

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

        if (repo.listarSolicitacoes().isEmpty()) {
            imprimirMensagem("Nenhuma solicitacao registrada.");
            return;
        }

        for (SolicitacaoKit s : repo.listarSolicitacoes()) {
            System.out.println(s);
        }
    }

    private void registrarManutencao() {
        imprimirSubtitulo("REGISTRAR MANUTENCAO");

        for (Equipamento e : repo.listarEquipamentos()) {
            System.out.println(e);
        }

        Equipamento eqp = repo.buscarEquipamento(lerInteiro("ID do Equipamento: "));

        if (eqp == null) {
            imprimirErro("Nao encontrado!");
            return;
        }

        if (eqp.isEmManutencao()) {
            imprimirAviso("Este equipamento ja esta em manutencao.");
            return;
        }

        eqp.registrarEntradaManutencao();
        imprimirMensagem("Equipamento em manutencao!");
        notificarAulasAfetadas(eqp);
    }

    /** RN07: avisa os professores cujas aulas futuras dependem do equipamento em manutencao. */
    private void notificarAulasAfetadas(Equipamento eqp) {
        boolean algumaAfetada = false;

        for (SolicitacaoKit s : repo.listarSolicitacoes()) {
            Agendamento ag = s.getAgendamento();

            boolean usaEquipamento = s.getKit().getEquipamentos().contains(eqp);
            boolean aulaAtiva = ag.getStatus() != StatusAgendamento.CANCELADO
                    && !ag.verificarDataPassada();
            boolean solicitacaoAtiva = s.getStatus() != StatusSolicitacao.INDISPONIVEL;

            if (usaEquipamento && aulaAtiva && solicitacaoAtiva) {
                s.atualizarStatus(StatusSolicitacao.INDISPONIVEL);
                imprimirAviso("RN07: Prof. " + ag.getProfessor().getNome() + " notificado - AG-"
                        + ag.getId() + " (" + ag.getDataAula() + "): kit marcado como INDISPONIVEL.");
                algumaAfetada = true;
            }
        }

        if (!algumaAfetada) {
            imprimirMensagem("RN07: Nenhuma aula agendada utiliza este equipamento.");
        }
    }

    private void liberarEquipamento() {
        imprimirSubtitulo("LIBERAR EQUIPAMENTO DA MANUTENCAO");

        boolean encontrou = false;
        for (Equipamento e : repo.listarEquipamentos()) {
            if (e.isEmManutencao()) {
                System.out.println(e);
                encontrou = true;
            }
        }

        if (!encontrou) {
            imprimirMensagem("Nenhum equipamento em manutencao.");
            return;
        }

        Equipamento eqp = repo.buscarEquipamento(lerInteiro("ID do Equipamento: "));

        if (eqp == null || !eqp.isEmManutencao()) {
            imprimirErro("Equipamento nao encontrado ou nao esta em manutencao!");
            return;
        }

        eqp.liberarParaUso();
        imprimirSucesso("Equipamento liberado para uso!");
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
        System.out.println("8 - Aprovar Agendamentos Pendentes (RN01)");
        System.out.println("0 - Sair");
        String op = lerTexto("Opcao: ");

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

            case "8":
                aprovarAgendamentosPendentes();
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

        int id = lerInteiro("ID do usuario para aprovar (0 = cancelar): ");

        if (id == 0) return;

        Usuario u = repo.buscarPorId(id);
        if (u == null || u.isAprovado()) {
            imprimirErro("Usuario nao encontrado entre os pendentes!");
            return;
        }

        u.setAprovado(true);
        imprimirSucesso("Usuario aprovado!");
    }

    private void aprovarAgendamentosPendentes() {
        imprimirSubtitulo("APROVAR AGENDAMENTOS PENDENTES (RN01)");

        boolean encontrou = false;
        for (Agendamento a : repo.listarAgendamentos()) {
            if (a.getStatus() == StatusAgendamento.PENDENTE) {
                System.out.println(a);
                encontrou = true;
            }
        }

        if (!encontrou) {
            imprimirMensagem("Nao ha agendamentos pendentes.");
            return;
        }

        int id = lerInteiro("ID do agendamento (0 = cancelar): ");
        if (id == 0) return;

        Agendamento ag = repo.buscarAgendamento(id);
        if (ag == null || ag.getStatus() != StatusAgendamento.PENDENTE) {
            imprimirErro("Agendamento nao encontrado entre os pendentes!");
            return;
        }

        String acao = lerTexto("1 - Aprovar  2 - Rejeitar: ");
        if (acao.equals("1")) {
            ag.confirmarReserva();
            imprimirSucesso("Agendamento aprovado!");
        } else if (acao.equals("2")) {
            ag.cancelarReserva();
            imprimirSucesso("Agendamento rejeitado (cancelado).");
        } else {
            imprimirErro("Opcao invalida!");
        }
    }

    private void gerenciarLaboratorios() {
        imprimirSubtitulo("GERENCIAR LABORATORIOS");
        System.out.println("1 - Cadastrar Laboratorio");
        System.out.println("2 - Listar Laboratorios");
        System.out.println("3 - Atualizar Condicao de um Laboratorio");
        String op = lerTexto("Opcao: ");

        switch (op) {
            case "1": {
                String nome = lerTextoObrigatorio("Nome: ");
                int cap = lerInteiro("Capacidade: ");

                if (cap <= 0) {
                    imprimirErro("A capacidade deve ser maior que zero!");
                    return;
                }

                int novoId = repo.listarLaboratorios().size() + 1;
                repo.adicionarLaboratorio(new Laboratorio(novoId, nome, cap));
                imprimirSucesso("Laboratorio cadastrado!");
                break;
            }

            case "2":
                for (Laboratorio l : repo.listarLaboratorios()) System.out.println(l);
                break;

            case "3": {
                for (Laboratorio l : repo.listarLaboratorios()) System.out.println(l);

                Laboratorio lab = repo.buscarLaboratorio(lerInteiro("ID do Laboratorio: "));
                if (lab == null) {
                    imprimirErro("Laboratorio nao encontrado!");
                    return;
                }

                CondicaoSala[] condicoes = CondicaoSala.values();
                for (int i = 0; i < condicoes.length; i++) {
                    System.out.println("  " + (i + 1) + " - " + condicoes[i]);
                }

                int escolha = lerInteiro("Nova condicao: ");
                if (escolha < 1 || escolha > condicoes.length) {
                    imprimirErro("Opcao invalida!");
                    return;
                }

                lab.atualizarCondicao(condicoes[escolha - 1]);
                imprimirSucesso("Condicao atualizada!");
                break;
            }

            default:
                imprimirErro("Opcao invalida!");
        }
    }

    private void gerenciarKits() {
        imprimirSubtitulo("GERENCIAR KITS");
        System.out.println("1 - Criar Novo Kit");
        System.out.println("2 - Listar Kits");
        String op = lerTexto("Opcao: ");

        if (op.equals("1")) {
            String nome = lerTextoObrigatorio("Nome do Kit: ");
            Kit kit = new Kit(nome);

            imprimirMensagem("Materiais disponiveis:");
            for (Material m : repo.listarMateriais()) System.out.println("  " + m);

            for (String idStr : lerTexto("IDs dos materiais (separados por virgula): ").split(",")) {
                idStr = idStr.trim();
                if (idStr.isEmpty()) continue;

                try {
                    Material m = repo.buscarMaterial(Integer.parseInt(idStr));
                    if (m != null) kit.adicionarMaterial(m);
                    else imprimirAviso("Material " + idStr + " nao encontrado.");
                } catch (NumberFormatException e) {
                    imprimirAviso("ID invalido ignorado: " + idStr);
                }
            }

            imprimirMensagem("Equipamentos disponiveis:");
            for (Equipamento e : repo.listarEquipamentos()) System.out.println("  " + e);

            for (String idStr : lerTexto("IDs dos equipamentos (virgula, ou vazio para nenhum): ").split(",")) {
                idStr = idStr.trim();
                if (idStr.isEmpty()) continue;

                try {
                    Equipamento e = repo.buscarEquipamento(Integer.parseInt(idStr));
                    if (e != null) kit.adicionarEquipamento(e);
                    else imprimirAviso("Equipamento " + idStr + " nao encontrado.");
                } catch (NumberFormatException ex) {
                    imprimirAviso("ID invalido ignorado: " + idStr);
                }
            }

            if (kit.getMateriais().isEmpty() && kit.getEquipamentos().isEmpty()) {
                imprimirErro("Um kit precisa ter ao menos um material ou equipamento. Kit nao criado.");
                return;
            }

            repo.adicionarKit(kit);
            imprimirSucesso("Kit criado!");
        } else {
            for (Kit k : repo.listarKits()) System.out.println(k);
        }
    }

    private void aprovarSolicitacoesExtemporaneas() {
        imprimirSubtitulo("APROVAR SOLICITACOES EXTEMPORANEAS (RN04)");

        boolean encontrou = false;
        for (SolicitacaoKit s : repo.listarSolicitacoes()) {
            if (s.getStatus() == StatusSolicitacao.PENDENTE) {
                System.out.println(s);
                encontrou = true;
            }
        }

        if (!encontrou) {
            imprimirMensagem("Nao ha solicitacoes pendentes.");
            return;
        }

        int id = lerInteiro("ID da Solicitacao para aprovar (0 = cancelar): ");

        if (id == 0) return;

        SolicitacaoKit s = repo.buscarSolicitacao(id);

        // Antes qualquer ID era aprovado, mesmo de solicitacoes que nao estavam pendentes
        if (s == null || s.getStatus() != StatusSolicitacao.PENDENTE) {
            imprimirErro("Nao encontrada entre as pendentes!");
            return;
        }

        // Reconfere o estoque/equipamentos: o cenario pode ter mudado desde o pedido
        if (!s.getKit().verificarDisponibilidade(s.getQuantidadeKits())) {
            s.atualizarStatus(StatusSolicitacao.INDISPONIVEL);
            imprimirErro("Kit sem estoque ou com equipamento em manutencao. Marcada como INDISPONIVEL.");
            return;
        }

        s.atualizarStatus(StatusSolicitacao.APROVADO);
        imprimirSucesso("Solicitacao aprovada!");
    }

    private void gerarRelatorios() {
        long checklistsConcluidos = repo.listarChecklists().stream().filter(Checklist::isConcluido).count();
        long agendamentosPendentes = repo.listarAgendamentos().stream()
                .filter(a -> a.getStatus() == StatusAgendamento.PENDENTE).count();

        imprimirSubtitulo("RELATORIOS");
        System.out.println("Total de Usuarios: " + repo.listarTodosUsuarios().size());
        System.out.println("Total de Agendamentos: " + repo.listarAgendamentos().size()
                + " (" + agendamentosPendentes + " pendentes)");
        System.out.println("Total de Kits: " + repo.listarKits().size());
        System.out.println("Materiais com estoque baixo: " + repo.listarMateriaisEstoqueBaixo().size());
        System.out.println("Checklists concluidos: " + checklistsConcluidos
                + " de " + repo.listarChecklists().size());
        System.out.println("================================");
    }
}
