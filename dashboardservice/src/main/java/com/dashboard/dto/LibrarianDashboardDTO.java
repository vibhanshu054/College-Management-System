package com.collage.dashboard.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibrarianDashboardDTO {
    private int totalBooks;
    private int availableBooks;
    private int issuedBooks;
    private Map<String, Object> todayActivity;
    private List<Map<String, Object>> overdueBooks;
    private int totalMembers;
}