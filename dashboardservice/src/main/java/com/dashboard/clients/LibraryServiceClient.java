package com.collage.dashboard.clients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.util.Map;

@FeignClient(name = "library-service", url = "http://localhost:8084")
public interface LibraryServiceClient {

    @GetMapping("/api/library/dashboard")
    Map<String, Object> getLibraryDashboard();
}