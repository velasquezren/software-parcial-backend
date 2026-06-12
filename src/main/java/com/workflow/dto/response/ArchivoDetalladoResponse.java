package com.workflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para la lista consolidada y detallada de archivos subidos en el sistema.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchivoDetalladoResponse {
    private String id;
    private String nombreOriginal;
    private String nombreAlmacenado;
    private String tipoContenido;
    private long tamanoBytes;
    private String subidoPor;
    private LocalDateTime fechaSubida;
    
    // Asociación e información de procedencia
    private String origenTipo;   // "SOLICITUD" o "DOCUMENTO"
    private String origenNombre; // Título o descripción del origen
    private String solicitudId;  // ID de la solicitud para navegación
    private String documentoId;  // ID del documento para navegación
}
