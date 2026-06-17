package com.cl.mdd.server.core.service.payment.impl.primerate.model;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "nm_response")
@XmlAccessorType(XmlAccessType.FIELD)
public class TxResponse {

    public static String STATUS_COMPLETE = "complete";
    public static String STATUS_PENDING = "pendingsettlement";
    public static String STATUS_FAILED = "failed";
    public static String STATUS_CANCELED = "canceled";

    @XmlElement(name = "transaction")
    private Transaction transaction;

    @XmlElement(name = "error_response")
    private String errorResponse;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public String getErrorResponse() {
        return errorResponse;
    }

    public void setErrorResponse(String errorResponse) {
        this.errorResponse = errorResponse;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Transaction {

        @XmlElement(name = "transaction_id")
        private String id;

        @XmlElement(name = "transaction_type")
        private TransactionType type;

        @XmlElement(name = "condition")
        private String condition;

        @XmlElement(name = "cc_number")
        private String ccNumber;

        @XmlElement(name = "check_account")
        private String accountNumber;

        @XmlElement(name = "action")
        private List<Action> actions;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public TransactionType getType() {
            return type;
        }

        public void setType(TransactionType type) {
            this.type = type;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getCcNumber() {
            return ccNumber;
        }

        public void setCcNumber(String ccNumber) {
            this.ccNumber = ccNumber;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public List<Action> getActions() {
            return actions;
        }

        public void setActions(List<Action> actions) {
            this.actions = actions;
        }
    }

    @XmlEnum
    public enum TransactionType {
        @XmlEnumValue("cc")
        CC,
        @XmlEnumValue("ck")
        ACH
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Action {

        @XmlElement(name = "amount")
        private String amount;

        @XmlElement(name = "action_type")
        private String type;

        @XmlElement(name = "response_text")
        private String responseText;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getResponseText() {
            return responseText;
        }

        public void setResponseText(String responseText) {
            this.responseText = responseText;
        }

    }

}
