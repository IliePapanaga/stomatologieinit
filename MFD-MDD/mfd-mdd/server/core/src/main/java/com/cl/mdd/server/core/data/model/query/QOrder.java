package com.cl.mdd.server.core.data.model.query;

public class QOrder {

    public enum Direction {
        ASC, DESC
    }

    private String field;

    private Direction direction;

    private Boolean nullsFirst;

    public QOrder() {
    }

    public QOrder(String field, Direction direction) {
        this.field = field;
        this.direction = direction;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Boolean getNullsFirst() {
        return nullsFirst;
    }

    public void setNullsFirst(Boolean nullsFirst) {
        this.nullsFirst = nullsFirst;
    }
}
