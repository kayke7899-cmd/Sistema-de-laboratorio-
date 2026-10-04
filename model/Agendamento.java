package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Agendamento {
    private static int contadorId = 1000;

    private int id;
    private Professor professor;
    private Laboratorio laboratorio;
    private String disciplina;
    private String areaTematica;
    private String turma;
    private LocalDate dataAula;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private int quantidadeAlunos;
    private String observacoes;
    private StatusAgendamento status;
    private LocalDateTime dataCriacao;

    public Agendamento(Professor professor, Laboratorio laboratorio, String disciplina,
                       String areaTematica, String turma, LocalDate dataAula,
                       LocalTime horarioInicio, LocalTime horarioFim,
                       int quantidadeAlunos, String observacoes) {
        this.id = ++contadorId;
        this.professor = professor;
        this.laboratorio = laboratorio;
        this.disciplina = disciplina;
        this.areaTematica = areaTematica;
        this.turma = turma;
        this.dataAula = dataAula;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.quantidadeAlunos = quantidadeAlunos;
        this.observacoes = observacoes;
        this.status = StatusAgendamento.PENDENTE;
        this.dataCriacao = LocalDateTime.now();
    }

    public boolean verificarAntecedenciaMinima() {
        LocalDate dataMinima = LocalDate.now().plusDays(7);
        return !dataAula.isBefore(dataMinima);
    }

    public boolean verificarCapacidade() {
        return quantidadeAlunos <= laboratorio.getCapacidadeMaxima();
    }

    public boolean verificarDataPassada() {
        return dataAula.isBefore(LocalDate.now());
    }

    public void confirmarReserva() {
        this.status = StatusAgendamento.APROVADO;
    }

    public void cancelarReserva() {
        this.status = StatusAgendamento.CANCELADO;
    }

    public int getId() { return id; }
    public Professor getProfessor() { return professor; }
    public Laboratorio getLaboratorio() { return laboratorio; }
    public LocalDate getDataAula() { return dataAula; }
    public LocalTime getHorarioInicio() { return horarioInicio; }
    public LocalTime getHorarioFim() { return horarioFim; }
    public int getQuantidadeAlunos() { return quantidadeAlunos; }
    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("[AG-%d] %s | %s | %s %s-%s | %d alunos | %s",
                id, disciplina, laboratorio.getNomeSala(),
                dataAula, horarioInicio, horarioFim,
                quantidadeAlunos, status);
    }
}