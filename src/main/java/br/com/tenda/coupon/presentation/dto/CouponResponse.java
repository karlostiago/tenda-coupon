package br.com.tenda.coupon.presentation.dto;

import br.com.tenda.coupon.domain.vo.CouponStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Resposta contendo os dados do cupom")
public class CouponResponse {

    @Schema(description = "ID único do cupom", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Código do cupom (6 caracteres alfanuméricos)", example = "ABC123")
    private String code;

    @Schema(description = "Descrição do cupom", example = "Desconto de primavera")
    private String description;

    @Schema(description = "Valor do desconto", example = "10.50")
    private BigDecimal discountValue;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data de expiração do cupom", example = "2026-12-31T23:59:59")
    private LocalDateTime expirationDate;

    @Schema(description = "Indica se o cupom está publicado", example = "true")
    private boolean published;

    @Schema(description = "Indica se o cupom foi resgatado", example = "true")
    private boolean redeemed;

    @Schema(description = "Status do cupom", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "DELETED"})
    private CouponStatus status;
}

