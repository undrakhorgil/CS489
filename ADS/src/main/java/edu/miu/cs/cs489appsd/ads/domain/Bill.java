package edu.miu.cs.cs489appsd.ads.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Bill {
    private Long billId;
    private Long patientId;
    private BigDecimal amount;
    private LocalDate dueDate;
    private boolean paid;

    public Bill() {
    }

    public Bill(Long billId, Long patientId, BigDecimal amount, LocalDate dueDate, boolean paid) {
        this.billId = billId;
        this.patientId = patientId;
        this.amount = amount;
        this.dueDate = dueDate;
        this.paid = paid;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
}
