package br.com.tenda.coupon.presentation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para criar um novo cupom")
public class CreateCouponRequest {

    @NotBlank(message = "Code is required")
    @Schema(description = "Código do cupom (6 caracteres alfanuméricos, caracteres especiais serão removidos)",
            example = "ABC-123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotBlank(message = "Description is required")
    @Schema(description = "Descrição do cupom", example = "Desconto de primavera", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.5", message = "Discount value must be at least 0.5")
    @Schema(description = "Valor do desconto (mínimo 0.5)", example = "10.50", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal discountValue;

    @NotNull(message = "Expiration date is required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(
            description = "Data de expiração (não pode ser no passado)",
            example = "2026-02-16T20:32:17",
            type = "string",
            format = "date-time",
            pattern = "yyyy-MM-dd'T'HH:mm:ss",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private LocalDateTime expirationDate;

    @Schema(description = "Indica se o cupom já está publicado", example = "true", defaultValue = "false")
    private boolean published;

    @Schema(description = "Indica se o cupom foi recuperado", example = "true", defaultValue = "false")
    private boolean redeemed;
}


