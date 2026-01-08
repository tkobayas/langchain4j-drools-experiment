package org.example.langchain4j.drools.domain;

public class LoanApplication {

    private Person applicant;

    private int amount;

    private boolean approved = false;

    public LoanApplication() {
    }

    public LoanApplication(Person applicant, int amount) {
        this.applicant = applicant;
        this.amount = amount;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Person getApplicant() {
        return applicant;
    }

    public void setApplicant(Person applicant) {
        this.applicant = applicant;
    }

    @Override
    public String toString() {
        return "LoanApplication{" +
                "applicant=" + applicant +
                ", amount=" + amount +
                ", approved=" + approved +
                '}';
    }
}