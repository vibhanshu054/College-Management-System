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

    @DeleteMapping("/api/library/librarian/{universityId}")
    ResponseEntity<Void> deleteLibrarian(
            @PathVariable String universityId
    );

    @PutMapping("/api/library/librarian/{universityId}")
    ResponseEntity<LibrarianDTO> updateLibrarian(
            @PathVariable String universityId,
            @RequestBody LibrarianDTO dto
    );
}