package br.com.tenda.coupon.presentation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "Resposta de erro padrão")
public class ErrorResponse {

    @Schema(description = "Timestamp do erro", example = "2026-02-16T10:30:00")
    private LocalDateTime timestamp;

    @Schema(description = "Código de status HTTP", example = "400")
    private int status;

    @Schema(description = "Mensagem de erro", example = "Validation failed")
    private String error;

    @Schema(description = "Mensagem detalhada", example = "Discount value must be at least 0.5")
    private String message;

    @Schema(description = "Caminho da requisição", example = "/api/v1/coupons")
    private String path;
}

