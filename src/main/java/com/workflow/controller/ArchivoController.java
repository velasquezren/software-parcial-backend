package com.workflow.controller;

import com.workflow.domain.model.ArchivoAdjunto;
import com.workflow.dto.response.ApiResponse;
import com.workflow.dto.response.ArchivoAdjuntoResponse;
import com.workflow.dto.response.ArchivoDetalladoResponse;
import com.workflow.service.ArchivoStorageService;
import com.workflow.repository.SolicitudWorkflowRepository;
import com.workflow.repository.DocumentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for file upload and download operations.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/archivos")
@RequiredArgsConstructor
@Tag(name = "Archivos", description = "Endpoints para subir y descargar archivos adjuntos de solicitudes.")
public class ArchivoController {

    private final ArchivoStorageService storageService;
    private final SolicitudWorkflowRepository solicitudRepository;
    private final DocumentoRepository documentoRepository;

    /**
     * Sube uno o más archivos y retorna metadatos.
     * Los archivos se almacenan temporalmente; el frontend usa los IDs retornados
     * para vincularlos a la solicitud al momento de crearla.
     */
    @PostMapping(value = "/subir", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir archivos", description = "Sube uno o más archivos (max 10MB cada uno). Tipos: PDF, imágenes, Word, Excel, texto.")
    public ResponseEntity<ApiResponse<List<ArchivoAdjuntoResponse>>> subirArchivos(
            @RequestParam("archivos") MultipartFile[] archivos,
            @RequestHeader(value = "X-Usuario", required = false) String usuario) {

        String uploader = (usuario != null && !usuario.isBlank()) ? usuario : "anonimo";
        log.info("POST /api/v1/archivos/subir - {} archivos por {}", archivos.length, uploader);

        List<ArchivoAdjunto> almacenados = storageService.almacenarArchivos(archivos, uploader);

        List<ArchivoAdjuntoResponse> respuesta = almacenados.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.ok("Archivos subidos exitosamente", respuesta)
        );
    }

    /**
     * Retorna una lista consolidada de todos los archivos subidos al sistema (solicitudes y documentos).
     */
    @GetMapping("/todos")
    @Operation(summary = "Listar todos los archivos", description = "Retorna una lista consolidada de todos los archivos subidos al sistema (solicitudes y documentos).")
    public ResponseEntity<ApiResponse<List<ArchivoDetalladoResponse>>> listarTodosLosArchivos() {
        log.info("GET /api/v1/archivos/todos");
        List<ArchivoDetalladoResponse> archivos = new java.util.ArrayList<>();

        // 1. Obtener archivos de SolicitudWorkflow
        solicitudRepository.findAll().forEach(solicitud -> {
            if (solicitud.getArchivosAdjuntos() != null) {
                solicitud.getArchivosAdjuntos().forEach(adjunto -> {
                    archivos.add(ArchivoDetalladoResponse.builder()
                            .id(adjunto.getId())
                            .nombreOriginal(adjunto.getNombreOriginal())
                            .nombreAlmacenado(adjunto.getNombreAlmacenado())
                            .tipoContenido(adjunto.getTipoContenido())
                            .tamanoBytes(adjunto.getTamanoBytes())
                            .subidoPor(adjunto.getSubidoPor())
                            .fechaSubida(adjunto.getFechaSubida())
                            .origenTipo("SOLICITUD")
                            .origenNombre(solicitud.getCodigoSeguimiento() + ": " + solicitud.getTitulo())
                            .solicitudId(solicitud.getId())
                            .build());
                });
            }
        });

        // 2. Obtener archivos de Documentos (de tipo FILE)
        documentoRepository.findAll().forEach(documento -> {
            if ("FILE".equalsIgnoreCase(documento.getTipo()) && documento.getVersiones() != null) {
                documento.getVersiones().forEach(version -> {
                    archivos.add(ArchivoDetalladoResponse.builder()
                            .id(documento.getId() + "_" + version.getVersion())
                            .nombreOriginal(version.getNombreOriginal())
                            .nombreAlmacenado(version.getNombreAlmacenado())
                            .tipoContenido(version.getTipoContenido())
                            .tamanoBytes(version.getTamanoBytes())
                            .subidoPor(version.getSubidoPor())
                            .fechaSubida(version.getFechaSubida())
                            .origenTipo("DOCUMENTO")
                            .origenNombre(documento.getNombre())
                            .documentoId(documento.getId())
                            .solicitudId(documento.getSolicitudId())
                            .build());
                });
            }
        });

        // Ordenar por fecha de subida descendente (más recientes primero)
        archivos.sort((a, b) -> {
            if (a.getFechaSubida() == null && b.getFechaSubida() == null) return 0;
            if (a.getFechaSubida() == null) return 1;
            if (b.getFechaSubida() == null) return -1;
            return b.getFechaSubida().compareTo(a.getFechaSubida());
        });

        return ResponseEntity.ok(ApiResponse.ok("Listado de archivos consolidado", archivos));
    }

    /**
     * Descarga un archivo por su ID (nombre almacenado).
     */
    @GetMapping("/{archivoId}")
    @Operation(summary = "Descargar archivo", description = "Descarga un archivo adjunto por su ID.")
    public ResponseEntity<Resource> descargarArchivo(@PathVariable String archivoId) {
        log.info("GET /api/v1/archivos/{}", archivoId);

        // Search for file with any extension
        Resource resource = storageService.cargarArchivo(archivoId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    private ArchivoAdjuntoResponse toResponse(ArchivoAdjunto archivo) {
        return ArchivoAdjuntoResponse.builder()
                .id(archivo.getId())
                .nombreOriginal(archivo.getNombreOriginal())
                .tipoContenido(archivo.getTipoContenido())
                .tamanoBytes(archivo.getTamanoBytes())
                .subidoPor(archivo.getSubidoPor())
                .fechaSubida(archivo.getFechaSubida())
                .build();
    }
}
