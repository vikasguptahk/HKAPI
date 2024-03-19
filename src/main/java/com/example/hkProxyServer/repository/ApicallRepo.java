package com.example.hkProxyServer.repository;

import com.example.hkProxyServer.model.Apicalls;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ApicallRepo extends MongoRepository<Apicalls,Integer> {
}
