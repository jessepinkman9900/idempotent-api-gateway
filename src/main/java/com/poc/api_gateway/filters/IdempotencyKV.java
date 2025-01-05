package com.poc.api_gateway.filters;

import org.springframework.http.HttpStatus;

public record IdempotencyKV(
    HttpStatus status, String requestPayloadHash, String region, String responseTimestamp) {}
