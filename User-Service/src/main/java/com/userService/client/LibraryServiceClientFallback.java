package com.userService.client;


import com.userService.dto.LibrarianDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Fallback implementation for LibraryServiceClient
 * Used when Library-Service is unavailable (circuit breaker pattern)
 */
@Component
@Slf4j
public class LibraryServiceClientFallback implements LibraryServiceClient {

    @Override
    public ResponseEntity<LibrarianDTO> createLibrarianFromUser(LibrarianDTO dto) {
        log.error("Library-Service DOWN (CREATE)");
        throw new RuntimeException("Library service unavailable");
    }

    @Override
    public ResponseEntity<Void> deleteLibrarian(String universityId) {
        log.error("Library-Service DOWN (DELETE)");
        throw new RuntimeException("Library service unavailable");
    }

    @Override
    public ResponseEntity<LibrarianDTO> updateLibrarian(String universityId, LibrarianDTO dto) {
        log.error("Library-Service DOWN (UPDATE)");
        throw new RuntimeException("Library service unavailable");
    }
}