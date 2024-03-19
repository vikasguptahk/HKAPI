package com.example.hkProxyServer.Controller;

import com.example.hkProxyServer.model.Apicalls;
import com.example.hkProxyServer.repository.ApicallRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController//apipath
public class MainController {
    @Autowired
    ApicallRepo apicallRepo;

    @PostMapping("/addApicall")
    public void addApicall(@RequestBody Apicalls apicalls){
             apicallRepo.save(apicalls);
    }
}
