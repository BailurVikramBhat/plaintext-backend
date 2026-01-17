package com.plaintext.auth.controller;

import com.plaintext.common.config.TncConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/tnc")
@RequiredArgsConstructor
public class TncController {

    @GetMapping
    public ResponseEntity<Map<String, String>> getTncContent() {
        return ResponseEntity.ok(Map.of(
                "version", TncConfig.CURRENT_TNC_VERSION,
                "content", TncConfig.TNC_CONTENT));
    }
}
