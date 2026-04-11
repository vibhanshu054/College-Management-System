package com.collage.dashboard.clients;

import java.util.Map;

public interface FacultyServiceClient {
    <K> Map<K, Long> getTotalFacultyCount();
}
