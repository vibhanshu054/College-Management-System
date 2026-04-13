package com.userService.client;

import com.userService.dto.LibrarianDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "Library-Service",
        fallback = LibraryServiceClientFallback.class
)
public interface LibraryServiceClient {

    @PostMapping("/api/library/librarian")
    ResponseEntity<LibrarianDTO> createLibrarianFromUser(
            @RequestBody LibrarianDTO librarianDTO
    );

    @DeleteMapping("/api/library/librarian/{id}")
    ResponseEntity<Void> deleteLibrarian(
            @PathVariable Long id
    );

    // 🔥 NEW
    @PutMapping("/api/library/librarian/{id}")
    ResponseEntity<LibrarianDTO> updateLibrarian(
            @PathVariable Long id,
            @RequestBody LibrarianDTO dto
    );
}