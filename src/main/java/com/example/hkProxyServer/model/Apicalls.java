package com.example.hkProxyServer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Apicalls {
    @Id
    private Integer apicall_id;
    private String request_url;
    private String request_method;
    private String request_headers;
    private Integer status_code;
    private String remote_address;
    private String referrer_policy;
    private LocalDateTime time;
    private String response;


}
