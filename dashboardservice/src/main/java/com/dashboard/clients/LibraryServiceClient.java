package com.dashboard.clients;


import com.dashboard.dto.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "LIBRARY-SERVICE")
public interface LibraryServiceClient {

    @GetMapping("/api/library/dashboard")
    ResponseEntity<ApiResponse> getLibraryDashboard();

    @GetMapping("/api/library/user/{universityId}/books/count")
    ResponseEntity<ApiResponse> getBookCountByUser(@PathVariable String universityId);
}