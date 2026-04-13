package com.dashboard.clients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;

@FeignClient(name = "LIBRARY-SERVICE")
public interface LibraryServiceClient {

    @GetMapping("/api/library/dashboard")
    Map<String, Object> getLibraryDashboard();
}