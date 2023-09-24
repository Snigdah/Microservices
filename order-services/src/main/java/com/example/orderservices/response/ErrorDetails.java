package com.example.orderservices.response;

import lombok.*;

import java.util.Date;

@Getter
@Data
@Builder
@Setter
@NoArgsConstructor
public class ErrorDetails {
    private Date timestamp;
    private String message;
    private String details;

    public ErrorDetails(Date timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

}
