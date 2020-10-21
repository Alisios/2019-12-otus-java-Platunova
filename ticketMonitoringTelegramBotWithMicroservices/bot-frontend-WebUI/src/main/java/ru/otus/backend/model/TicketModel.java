package ru.otus.backend.model;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class TicketModel implements Serializable {

    private static final long serialVersionUID = 129348556L;

    private long id;
    private String type;
    private String cost;

    public TicketModel(String type, String cost) {
        this.type = type;
        this.cost = cost;
    }

    @Override
    public String toString() {
        return type + ": " +
                cost;
    }

}
