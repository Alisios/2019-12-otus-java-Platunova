package ru.otus.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Log {

    @Id
    private String id;

    @Field
    private String typeOfError;

    @Field
    private String errorMessage;

    @Field
    private long chatId = 0;

}
