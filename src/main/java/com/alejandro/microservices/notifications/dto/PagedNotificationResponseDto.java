package com.alejandro.microservices.notifications.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta paginada de notificaciones")
public class PagedNotificationResponseDto {

    @Schema(description = "Lista de notificaciones en la página actual")
    private List<NotificationResponseDto> notifications;

    @Schema(description = "Número de página actual (base 0)", example = "0")
    private int currentPage;

    @Schema(description = "Tamaño de la página", example = "10")
    private int pageSize;

    @Schema(description = "Total de elementos disponibles", example = "150")
    private long totalElements;

    @Schema(description = "Total de páginas disponibles", example = "15")
    private int totalPages;

    @Schema(description = "Indica si es la primera página", example = "true")
    private boolean first;

    @Schema(description = "Indica si es la última página", example = "false")
    private boolean last;

    // Campos adicionales calculados automáticamente
    @Schema(description = "Indica si hay una página siguiente", example = "true")
    public boolean isHasNext() {
        return !this.last;
    }

    @Schema(description = "Indica si hay una página anterior", example = "false")
    public boolean isHasPrevious() {
        return !this.first;
    }

    @Schema(description = "Número de elementos en la página actual", example = "10")
    public int getNumberOfElements() {
        return this.notifications != null ? this.notifications.size() : 0;
    }

    @Schema(description = "Indica si la página está vacía", example = "false")
    public boolean isEmpty() {
        return getNumberOfElements() == 0;
    }
}
