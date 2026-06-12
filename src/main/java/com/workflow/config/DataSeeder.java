package com.workflow.config;

import com.workflow.domain.enums.EstadoWorkflow;
import com.workflow.domain.enums.Prioridad;
import com.workflow.domain.enums.RolUsuario;
import com.workflow.domain.model.Departamento;
import com.workflow.domain.model.SolicitudWorkflow;
import com.workflow.domain.model.Usuario;
import com.workflow.domain.model.Documento;
import com.workflow.domain.model.WorkflowDefinition;
import com.workflow.repository.DepartamentoRepository;
import com.workflow.repository.SolicitudWorkflowRepository;
import com.workflow.repository.UsuarioRepository;
import com.workflow.repository.DocumentoRepository;
import com.workflow.repository.WorkflowDefinitionRepository;
import com.workflow.repository.DiagramaBpmnRepository;
import com.workflow.repository.ReporteRepository;
import com.workflow.repository.UserDeviceTokenRepository;
import com.workflow.repository.WorkflowCoreEventRepository;
import com.workflow.repository.WorkspaceGraphStateRepository;
import com.workflow.service.CodigoSeguimientoGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final SolicitudWorkflowRepository solicitudRepository;
    private final DepartamentoRepository departamentoRepository;
    private final DocumentoRepository documentoRepository;
    private final WorkflowDefinitionRepository workflowDefinitionRepository;
    private final DiagramaBpmnRepository diagramaBpmnRepository;
    private final ReporteRepository reporteRepository;
    private final UserDeviceTokenRepository userDeviceTokenRepository;
    private final WorkflowCoreEventRepository workflowCoreEventRepository;
    private final WorkspaceGraphStateRepository workspaceGraphStateRepository;
    private final CodigoSeguimientoGenerator codigoGenerator;

    // --- 1. PROCESO DE AMPLIACIÓN DE INTERNET DE FIBRA ÓPTICA (3 swimlanes: Ventas Corporativas, Ingeniería de Red, Facturación y Legal) ---
    private static final String INTERNET_EXPANSION_XML = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<bpmn:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:wf=\"http://workflow.com/schema\" id=\"Definitions_Exp\" targetNamespace=\"http://bpmn.io/schema/bpmn\">\n" +
            "  <bpmn:collaboration id=\"Collaboration_Exp\">\n" +
            "    <bpmn:participant id=\"Participant_Exp\" name=\"Ampliación de Contrato de Internet (Fibra Corporativa)\" processRef=\"internet-expansion-workflow\" />\n" +
            "  </bpmn:collaboration>\n" +
            "  <bpmn:process id=\"internet-expansion-workflow\" name=\"Ampliación de Contrato de Internet\" isExecutable=\"true\">\n" +
            "    <bpmn:laneSet id=\"LaneSet_Exp\">\n" +
            "      <bpmn:lane id=\"Lane_Exp_Ventas\" name=\"Ventas Corporativas\" wf:departamento=\"Ventas Corporativas\">\n" +
            "        <bpmn:flowNodeRef>StartEvent_Exp</bpmn:flowNodeRef>\n" +
            "        <bpmn:flowNodeRef>Activity_Vta_Comercial</bpmn:flowNodeRef>\n" +
            "      </bpmn:lane>\n" +
            "      <bpmn:lane id=\"Lane_Exp_Noc\" name=\"Ingeniería de Red\" wf:departamento=\"Ingeniería de Red\">\n" +
            "        <bpmn:flowNodeRef>Activity_Noc_Estudio</bpmn:flowNodeRef>\n" +
            "      </bpmn:lane>\n" +
            "      <bpmn:lane id=\"Lane_Exp_Billing\" name=\"Facturación y Legal\" wf:departamento=\"Facturación y Legal\">\n" +
            "        <bpmn:flowNodeRef>Activity_Fact_Adenda</bpmn:flowNodeRef>\n" +
            "        <bpmn:flowNodeRef>EndEvent_Exp</bpmn:flowNodeRef>\n" +
            "      </bpmn:lane>\n" +
            "    </bpmn:laneSet>\n" +
            "    <bpmn:startEvent id=\"StartEvent_Exp\" name=\"Inicio\">\n" +
            "      <bpmn:outgoing>Flow_Exp_1</bpmn:outgoing>\n" +
            "    </bpmn:startEvent>\n" +
            "    <bpmn:userTask id=\"Activity_Vta_Comercial\" name=\"Validar Viabilidad Comercial\" wf:departamento=\"Ventas Corporativas\" wf:form='[{\"name\":\"nuevoAnchoBanda\",\"label\":\"Ancho de Banda Solicitado (Gbps)\",\"type\":\"number\",\"required\":true},{\"name\":\"tarifaMensualAdicional\",\"label\":\"Incremento Mensual Estimado (USD)\",\"type\":\"number\",\"required\":true}]'>\n" +
            "      <bpmn:incoming>Flow_Exp_1</bpmn:incoming>\n" +
            "      <bpmn:outgoing>Flow_Exp_2</bpmn:outgoing>\n" +
            "    </bpmn:userTask>\n" +
            "    <bpmn:userTask id=\"Activity_Noc_Estudio\" name=\"Estudio Técnico e Incremento de Capacidad\" wf:departamento=\"Ingeniería de Red\" wf:form='[{\"name\":\"nodoOrigen\",\"label\":\"Nodo Principal de Distribución\",\"type\":\"text\",\"required\":true},{\"name\":\"intervencionFisicaRequerida\",\"label\":\"Requiere Modificación de Fibra en Campo\",\"type\":\"checkbox\",\"required\":false}]'>\n" +
            "      <bpmn:incoming>Flow_Exp_2</bpmn:incoming>\n" +
            "      <bpmn:outgoing>Flow_Exp_3</bpmn:outgoing>\n" +
            "    </bpmn:userTask>\n" +
            "    <bpmn:userTask id=\"Activity_Fact_Adenda\" name=\"Firma de Adenda y Activación Comercial\" wf:departamento=\"Facturación y Legal\">\n" +
            "      <bpmn:incoming>Flow_Exp_3</bpmn:incoming>\n" +
            "      <bpmn:outgoing>Flow_Exp_4</bpmn:outgoing>\n" +
            "    </bpmn:userTask>\n" +
            "    <bpmn:endEvent id=\"EndEvent_Exp\" name=\"Fin\">\n" +
            "      <bpmn:incoming>Flow_Exp_4</bpmn:incoming>\n" +
            "    </bpmn:endEvent>\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Exp_1\" sourceRef=\"StartEvent_Exp\" targetRef=\"Activity_Vta_Comercial\" />\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Exp_2\" sourceRef=\"Activity_Vta_Comercial\" targetRef=\"Activity_Noc_Estudio\" />\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Exp_3\" sourceRef=\"Activity_Noc_Estudio\" targetRef=\"Activity_Fact_Adenda\" />\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Exp_4\" sourceRef=\"Activity_Fact_Adenda\" targetRef=\"EndEvent_Exp\" />\n" +
            "  </bpmn:process>\n" +
            "  <bpmndi:BPMNDiagram id=\"BPMNDiagram_Exp\">\n" +
            "    <bpmndi:BPMNPlane id=\"BPMNPlane_Exp\" bpmnElement=\"Collaboration_Exp\">\n" +
            "      <bpmndi:BPMNShape id=\"Participant_Exp_di\" bpmnElement=\"Participant_Exp\" isHorizontal=\"true\">\n" +
            "        <dc:Bounds x=\"120\" y=\"80\" width=\"900\" height=\"480\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Lane_Exp_Ventas_di\" bpmnElement=\"Lane_Exp_Ventas\" isHorizontal=\"true\">\n" +
            "        <dc:Bounds x=\"150\" y=\"80\" width=\"870\" height=\"160\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Lane_Exp_Noc_di\" bpmnElement=\"Lane_Exp_Noc\" isHorizontal=\"true\">\n" +
            "        <dc:Bounds x=\"150\" y=\"240\" width=\"870\" height=\"160\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Lane_Exp_Billing_di\" bpmnElement=\"Lane_Exp_Billing\" isHorizontal=\"true\">\n" +
            "        <dc:Bounds x=\"150\" y=\"400\" width=\"870\" height=\"160\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"StartEvent_Exp_di\" bpmnElement=\"StartEvent_Exp\">\n" +
            "        <dc:Bounds x=\"200\" y=\"140\" width=\"36\" height=\"36\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Activity_Vta_Comercial_di\" bpmnElement=\"Activity_Vta_Comercial\">\n" +
            "        <dc:Bounds x=\"280\" y=\"120\" width=\"100\" height=\"80\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Activity_Noc_Estudio_di\" bpmnElement=\"Activity_Noc_Estudio\">\n" +
            "        <dc:Bounds x=\"480\" y=\"280\" width=\"100\" height=\"80\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Activity_Fact_Adenda_di\" bpmnElement=\"Activity_Fact_Adenda\">\n" +
            "        <dc:Bounds x=\"680\" y=\"440\" width=\"100\" height=\"80\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"EndEvent_Exp_di\" bpmnElement=\"EndEvent_Exp\">\n" +
            "        <dc:Bounds x=\"880\" y=\"462\" width=\"36\" height=\"36\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Exp_1_di\" bpmnElement=\"Flow_Exp_1\">\n" +
            "        <di:waypoint x=\"236\" y=\"158\" />\n" +
            "        <di:waypoint x=\"280\" y=\"158\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Exp_2_di\" bpmnElement=\"Flow_Exp_2\">\n" +
            "        <di:waypoint x=\"380\" y=\"160\" />\n" +
            "        <di:waypoint x=\"430\" y=\"160\" />\n" +
            "        <di:waypoint x=\"430\" y=\"320\" />\n" +
            "        <di:waypoint x=\"480\" y=\"320\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Exp_3_di\" bpmnElement=\"Flow_Exp_3\">\n" +
            "        <di:waypoint x=\"580\" y=\"320\" />\n" +
            "        <di:waypoint x=\"630\" y=\"320\" />\n" +
            "        <di:waypoint x=\"630\" y=\"480\" />\n" +
            "        <di:waypoint x=\"680\" y=\"480\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Exp_4_di\" bpmnElement=\"Flow_Exp_4\">\n" +
            "        <di:waypoint x=\"780\" y=\"480\" />\n" +
            "        <di:waypoint x=\"880\" y=\"480\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "    </bpmndi:BPMNPlane>\n" +
            "  </bpmndi:BPMNDiagram>\n" +
            "</bpmn:definitions>";

    // --- 2. PROCESO DE SOPORTE TÉCNICO DE ENLACE DEDICADO (2 swimlanes: Soporte Técnico, Ingeniería de Red) ---
    private static final String TECH_SUPPORT_XML = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<bpmn:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:wf=\"http://workflow.com/schema\" id=\"Definitions_Sup\" targetNamespace=\"http://bpmn.io/schema/bpmn\">\n" +
            "  <bpmn:collaboration id=\"Collaboration_Sup\">\n" +
            "    <bpmn:participant id=\"Participant_Sup\" name=\"Soporte Técnico de Alta Prioridad\" processRef=\"tech-support-workflow\" />\n" +
            "  </bpmn:collaboration>\n" +
            "  <bpmn:process id=\"tech-support-workflow\" name=\"Soporte Técnico de Alta Prioridad\" isExecutable=\"true\">\n" +
            "    <bpmn:laneSet id=\"LaneSet_Sup\">\n" +
            "      <bpmn:lane id=\"Lane_Sup_Soporte\" name=\"Soporte Técnico\" wf:departamento=\"Soporte Técnico\">\n" +
            "        <bpmn:flowNodeRef>StartEvent_Sup</bpmn:flowNodeRef>\n" +
            "        <bpmn:flowNodeRef>Activity_Sop_Diag</bpmn:flowNodeRef>\n" +
            "        <bpmn:flowNodeRef>Activity_Sop_Calidad</bpmn:flowNodeRef>\n" +
            "        <bpmn:flowNodeRef>EndEvent_Sup</bpmn:flowNodeRef>\n" +
            "      </bpmn:lane>\n" +
            "      <bpmn:lane id=\"Lane_Sup_Noc\" name=\"Ingeniería de Red\" wf:departamento=\"Ingeniería de Red\">\n" +
            "        <bpmn:flowNodeRef>Activity_Noc_Intervencion</bpmn:flowNodeRef>\n" +
            "      </bpmn:lane>\n" +
            "    </bpmn:laneSet>\n" +
            "    <bpmn:startEvent id=\"StartEvent_Sup\" name=\"Inicio\">\n" +
            "      <bpmn:outgoing>Flow_Sup_1</bpmn:outgoing>\n" +
            "    </bpmn:startEvent>\n" +
            "    <bpmn:userTask id=\"Activity_Sop_Diag\" name=\"Diagnóstico y Triaje Técnico\" wf:departamento=\"Soporte Técnico\" wf:form='[{\"name\":\"atenuacionFibra\",\"label\":\"Atenuación Medida (dB)\",\"type\":\"number\",\"required\":true},{\"name\":\"causaProbable\",\"label\":\"Diagnóstico Preliminar\",\"type\":\"text\",\"required\":false}]'>\n" +
            "      <bpmn:incoming>Flow_Sup_1</bpmn:incoming>\n" +
            "      <bpmn:outgoing>Flow_Sup_2</bpmn:outgoing>\n" +
            "    </bpmn:userTask>\n" +
            "    <bpmn:userTask id=\"Activity_Noc_Intervencion\" name=\"Resolución e Intervención de Red\" wf:departamento=\"Ingeniería de Red\">\n" +
            "      <bpmn:incoming>Flow_Sup_2</bpmn:incoming>\n" +
            "      <bpmn:outgoing>Flow_Sup_3</bpmn:outgoing>\n" +
            "    </bpmn:userTask>\n" +
            "    <bpmn:userTask id=\"Activity_Sop_Calidad\" name=\"Control de Calidad y Cierre de Incidente\" wf:departamento=\"Soporte Técnico\">\n" +
            "      <bpmn:incoming>Flow_Sup_3</bpmn:incoming>\n" +
            "      <bpmn:outgoing>Flow_Sup_4</bpmn:outgoing>\n" +
            "    </bpmn:userTask>\n" +
            "    <bpmn:endEvent id=\"EndEvent_Sup\" name=\"Fin\">\n" +
            "      <bpmn:incoming>Flow_Sup_4</bpmn:incoming>\n" +
            "    </bpmn:endEvent>\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Sup_1\" sourceRef=\"StartEvent_Sup\" targetRef=\"Activity_Sop_Diag\" />\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Sup_2\" sourceRef=\"Activity_Sop_Diag\" targetRef=\"Activity_Noc_Intervencion\" />\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Sup_3\" sourceRef=\"Activity_Noc_Intervencion\" targetRef=\"Activity_Sop_Calidad\" />\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Sup_4\" sourceRef=\"Activity_Sop_Calidad\" targetRef=\"EndEvent_Sup\" />\n" +
            "  </bpmn:process>\n" +
            "  <bpmndi:BPMNDiagram id=\"BPMNDiagram_Sup\">\n" +
            "    <bpmndi:BPMNPlane id=\"BPMNPlane_Sup\" bpmnElement=\"Collaboration_Sup\">\n" +
            "      <bpmndi:BPMNShape id=\"Participant_Sup_di\" bpmnElement=\"Participant_Sup\" isHorizontal=\"true\">\n" +
            "        <dc:Bounds x=\"120\" y=\"80\" width=\"800\" height=\"320\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Lane_Sup_Soporte_di\" bpmnElement=\"Lane_Sup_Soporte\" isHorizontal=\"true\">\n" +
            "        <dc:Bounds x=\"150\" y=\"80\" width=\"770\" height=\"160\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Lane_Sup_Noc_di\" bpmnElement=\"Lane_Sup_Noc\" isHorizontal=\"true\">\n" +
            "        <dc:Bounds x=\"150\" y=\"240\" width=\"770\" height=\"160\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"StartEvent_Sup_di\" bpmnElement=\"StartEvent_Sup\">\n" +
            "        <dc:Bounds x=\"200\" y=\"140\" width=\"36\" height=\"36\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Activity_Sop_Diag_di\" bpmnElement=\"Activity_Sop_Diag\">\n" +
            "        <dc:Bounds x=\"280\" y=\"120\" width=\"100\" height=\"80\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Activity_Noc_Intervencion_di\" bpmnElement=\"Activity_Noc_Intervencion\">\n" +
            "        <dc:Bounds x=\"480\" y=\"280\" width=\"100\" height=\"80\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Activity_Sop_Calidad_di\" bpmnElement=\"Activity_Sop_Calidad\">\n" +
            "        <dc:Bounds x=\"680\" y=\"120\" width=\"100\" height=\"80\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"EndEvent_Sup_di\" bpmnElement=\"EndEvent_Sup\">\n" +
            "        <dc:Bounds x=\"830\" y=\"142\" width=\"36\" height=\"36\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Sup_1_di\" bpmnElement=\"Flow_Sup_1\">\n" +
            "        <di:waypoint x=\"236\" y=\"158\" />\n" +
            "        <di:waypoint x=\"280\" y=\"158\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Sup_2_di\" bpmnElement=\"Flow_Sup_2\">\n" +
            "        <di:waypoint x=\"380\" y=\"160\" />\n" +
            "        <di:waypoint x=\"430\" y=\"160\" />\n" +
            "        <di:waypoint x=\"430\" y=\"320\" />\n" +
            "        <di:waypoint x=\"480\" y=\"320\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Sup_3_di\" bpmnElement=\"Flow_Sup_3\">\n" +
            "        <di:waypoint x=\"580\" y=\"320\" />\n" +
            "        <di:waypoint x=\"630\" y=\"320\" />\n" +
            "        <di:waypoint x=\"630\" y=\"160\" />\n" +
            "        <di:waypoint x=\"680\" y=\"160\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Sup_4_di\" bpmnElement=\"Flow_Sup_4\">\n" +
            "        <di:waypoint x=\"780\" y=\"160\" />\n" +
            "        <di:waypoint x=\"830\" y=\"160\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "    </bpmndi:BPMNPlane>\n" +
            "  </bpmndi:BPMNDiagram>\n" +
            "</bpmn:definitions>";

    // --- 3. PROCESO DE APROBACIÓN DE TARIFAS Y DESCUENTOS ESPECIALES (2 swimlanes: Ventas Corporativas, Facturación y Legal) ---
    private static final String DATA_DISCOUNT_XML = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<bpmn:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:wf=\"http://workflow.com/schema\" id=\"Definitions_Disc\" targetNamespace=\"http://bpmn.io/schema/bpmn\">\n" +
            "  <bpmn:collaboration id=\"Collaboration_Disc\">\n" +
            "    <bpmn:participant id=\"Participant_Disc\" name=\"Aprobación de Descuentos Especiales\" processRef=\"data-discount-workflow\" />\n" +
            "  </bpmn:collaboration>\n" +
            "  <bpmn:process id=\"data-discount-workflow\" name=\"Aprobación de Descuentos Especiales\" isExecutable=\"true\">\n" +
            "    <bpmn:laneSet id=\"LaneSet_Disc\">\n" +
            "      <bpmn:lane id=\"Lane_Disc_Ventas\" name=\"Ventas\" wf:departamento=\"Ventas Corporativas\">\n" +
            "        <bpmn:flowNodeRef>StartEvent_Disc</bpmn:flowNodeRef>\n" +
            "        <bpmn:flowNodeRef>Activity_Vta_Propuesta</bpmn:flowNodeRef>\n" +
            "      </bpmn:lane>\n" +
            "      <bpmn:lane id=\"Lane_Disc_Billing\" name=\"Facturación\" wf:departamento=\"Facturación y Legal\">\n" +
            "        <bpmn:flowNodeRef>Activity_Fact_Aprobacion</bpmn:flowNodeRef>\n" +
            "        <bpmn:flowNodeRef>EndEvent_Disc</bpmn:flowNodeRef>\n" +
            "      </bpmn:lane>\n" +
            "    </bpmn:laneSet>\n" +
            "    <bpmn:startEvent id=\"StartEvent_Disc\" name=\"Inicio\">\n" +
            "      <bpmn:outgoing>Flow_Disc_1</bpmn:outgoing>\n" +
            "    </bpmn:startEvent>\n" +
            "    <bpmn:userTask id=\"Activity_Vta_Propuesta\" name=\"Formular Propuesta Comercial\" wf:departamento=\"Ventas Corporativas\">\n" +
            "      <bpmn:incoming>Flow_Disc_1</bpmn:incoming>\n" +
            "      <bpmn:outgoing>Flow_Disc_2</bpmn:outgoing>\n" +
            "    </bpmn:userTask>\n" +
            "    <bpmn:userTask id=\"Activity_Fact_Aprobacion\" name=\"Aprobación Financiera y Tarifaria\" wf:departamento=\"Facturación y Legal\" wf:form='[{\"name\":\"margenNetoFinal\",\"label\":\"Margen Bruto Proyectado (%)\",\"type\":\"number\",\"required\":true},{\"name\":\"aprobadoComiteTarifas\",\"label\":\"Aprobado por el Comité de Tarifas\",\"type\":\"checkbox\",\"required\":true}]'>\n" +
            "      <bpmn:incoming>Flow_Disc_2</bpmn:incoming>\n" +
            "      <bpmn:outgoing>Flow_Disc_3</bpmn:outgoing>\n" +
            "    </bpmn:userTask>\n" +
            "    <bpmn:endEvent id=\"EndEvent_Disc\" name=\"Fin\">\n" +
            "      <bpmn:incoming>Flow_Disc_3</bpmn:incoming>\n" +
            "    </bpmn:endEvent>\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Disc_1\" sourceRef=\"StartEvent_Disc\" targetRef=\"Activity_Vta_Propuesta\" />\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Disc_2\" sourceRef=\"Activity_Vta_Propuesta\" targetRef=\"Activity_Fact_Aprobacion\" />\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Disc_3\" sourceRef=\"Activity_Fact_Aprobacion\" targetRef=\"EndEvent_Disc\" />\n" +
            "  </bpmn:process>\n" +
            "  <bpmndi:BPMNDiagram id=\"BPMNDiagram_Disc\">\n" +
            "    <bpmndi:BPMNPlane id=\"BPMNPlane_Disc\" bpmnElement=\"Collaboration_Disc\">\n" +
            "      <bpmndi:BPMNShape id=\"Participant_Disc_di\" bpmnElement=\"Participant_Disc\" isHorizontal=\"true\">\n" +
            "        <dc:Bounds x=\"120\" y=\"80\" width=\"800\" height=\"320\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Lane_Disc_Ventas_di\" bpmnElement=\"Lane_Disc_Ventas\" isHorizontal=\"true\">\n" +
            "        <dc:Bounds x=\"150\" y=\"80\" width=\"770\" height=\"160\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Lane_Disc_Billing_di\" bpmnElement=\"Lane_Disc_Billing\" isHorizontal=\"true\">\n" +
            "        <dc:Bounds x=\"150\" y=\"240\" width=\"770\" height=\"160\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"StartEvent_Disc_di\" bpmnElement=\"StartEvent_Disc\">\n" +
            "        <dc:Bounds x=\"200\" y=\"140\" width=\"36\" height=\"36\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Activity_Vta_Propuesta_di\" bpmnElement=\"Activity_Vta_Propuesta\">\n" +
            "        <dc:Bounds x=\"280\" y=\"120\" width=\"100\" height=\"80\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Activity_Fact_Aprobacion_di\" bpmnElement=\"Activity_Fact_Aprobacion\">\n" +
            "        <dc:Bounds x=\"480\" y=\"280\" width=\"100\" height=\"80\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"EndEvent_Disc_di\" bpmnElement=\"EndEvent_Disc\">\n" +
            "        <dc:Bounds x=\"740\" y=\"300\" width=\"36\" height=\"36\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Disc_1_di\" bpmnElement=\"Flow_Disc_1\">\n" +
            "        <di:waypoint x=\"236\" y=\"158\" />\n" +
            "        <di:waypoint x=\"280\" y=\"158\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Disc_2_di\" bpmnElement=\"Flow_Disc_2\">\n" +
            "        <di:waypoint x=\"380\" y=\"160\" />\n" +
            "        <di:waypoint x=\"430\" y=\"160\" />\n" +
            "        <di:waypoint x=\"430\" y=\"320\" />\n" +
            "        <di:waypoint x=\"480\" y=\"320\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Disc_3_di\" bpmnElement=\"Flow_Disc_3\">\n" +
            "        <di:waypoint x=\"580\" y=\"320\" />\n" +
            "        <di:waypoint x=\"740\" y=\"320\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "    </bpmndi:BPMNPlane>\n" +
            "  </bpmndi:BPMNDiagram>\n" +
            "</bpmn:definitions>";

    // --- 4. PROCESO DE INSTALACIÓN Y APROVISIONAMIENTO DE FIBRA ÓPTICA (4 swimlanes: Ventas, Ingeniería, Soporte, Facturación) ---
    private static final String FIBER_INSTALLATION_XML = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<bpmn:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:wf=\"http://workflow.com/schema\" id=\"Definitions_FibInst\" targetNamespace=\"http://bpmn.io/schema/bpmn\">\n" +
            "  <bpmn:collaboration id=\"Collaboration_FibInst\">\n" +
            "    <bpmn:participant id=\"Participant_FibInst\" name=\"Instalación y Aprovisionamiento de Fibra Óptica\" processRef=\"fiber-installation-workflow\" />\n" +
            "  </bpmn:collaboration>\n" +
            "  <bpmn:process id=\"fiber-installation-workflow\" name=\"Aprovisionamiento e Instalación de Fibra\" isExecutable=\"true\">\n" +
            "    <bpmn:laneSet id=\"LaneSet_FibInst\">\n" +
            "      <bpmn:lane id=\"Lane_Fib_Ventas\" name=\"Ventas\" wf:departamento=\"Ventas Corporativas\">\n" +
            "        <bpmn:flowNodeRef>StartEvent_Fib</bpmn:flowNodeRef>\n" +
            "        <bpmn:flowNodeRef>Activity_Vta_Factibilidad</bpmn:flowNodeRef>\n" +
            "      </bpmn:lane>\n" +
            "      <bpmn:lane id=\"Lane_Fib_Noc\" name=\"Ingeniería\" wf:departamento=\"Ingeniería de Red\">\n" +
            "        <bpmn:flowNodeRef>Activity_Noc_Diseno</bpmn:flowNodeRef>\n" +
            "      </bpmn:lane>\n" +
            "      <bpmn:lane id=\"Lane_Fib_Soporte\" name=\"Soporte\" wf:departamento=\"Soporte Técnico\">\n" +
            "        <bpmn:flowNodeRef>Activity_Sop_Instalacion</bpmn:flowNodeRef>\n" +
            "      </bpmn:lane>\n" +
            "      <bpmn:lane id=\"Lane_Fib_Fact\" name=\"Facturación\" wf:departamento=\"Facturación y Legal\">\n" +
            "        <bpmn:flowNodeRef>Activity_Fact_Alta</bpmn:flowNodeRef>\n" +
            "        <bpmn:flowNodeRef>EndEvent_Fib</bpmn:flowNodeRef>\n" +
            "      </bpmn:lane>\n" +
            "    </bpmn:laneSet>\n" +
            "    <bpmn:startEvent id=\"StartEvent_Fib\" name=\"Inicio\">\n" +
            "      <bpmn:outgoing>Flow_Fib_1</bpmn:outgoing>\n" +
            "    </bpmn:startEvent>\n" +
            "    <bpmn:userTask id=\"Activity_Vta_Factibilidad\" name=\"Validar Viabilidad y Cobertura\" wf:departamento=\"Ventas Corporativas\" wf:form='[{\"name\":\"distanciaCajaCercana\",\"label\":\"Distancia a la caja de distribución (m)\",\"type\":\"number\",\"required\":true},{\"name\":\"coberturaConfirmada\",\"label\":\"Confirmar Cobertura en Zona\",\"type\":\"checkbox\",\"required\":true}]'>\n" +
            "      <bpmn:incoming>Flow_Fib_1</bpmn:incoming>\n" +
            "      <bpmn:outgoing>Flow_Fib_2</bpmn:outgoing>\n" +
            "    </bpmn:userTask>\n" +
            "    <bpmn:userTask id=\"Activity_Noc_Diseno\" name=\"Diseño de Red y Puerto GPON\" wf:departamento=\"Ingeniería de Red\" wf:form='[{\"name\":\"cajaNapoId\",\"label\":\"Código de Caja NAP Asignada\",\"type\":\"text\",\"required\":true},{\"name\":\"puertoGponAsignado\",\"label\":\"Número de Puerto en OLT\",\"type\":\"number\",\"required\":true}]'>\n" +
            "      <bpmn:incoming>Flow_Fib_2</bpmn:incoming>\n" +
            "      <bpmn:outgoing>Flow_Fib_3</bpmn:outgoing>\n" +
            "    </bpmn:userTask>\n" +
            "    <bpmn:userTask id=\"Activity_Sop_Instalacion\" name=\"Instalación Física y Fusión en Domicilio\" wf:departamento=\"Soporte Técnico\" wf:form='[{\"name\":\"potenciaOpticaDomicilio\",\"label\":\"Potencia óptica medida en ONU (dBm)\",\"type\":\"number\",\"required\":true},{\"name\":\"instalacionCompletada\",\"label\":\"Fusión e Instalación Exitosa\",\"type\":\"checkbox\",\"required\":true}]'>\n" +
            "      <bpmn:incoming>Flow_Fib_3</bpmn:incoming>\n" +
            "      <bpmn:outgoing>Flow_Fib_4</bpmn:outgoing>\n" +
            "    </bpmn:userTask>\n" +
            "    <bpmn:userTask id=\"Activity_Fact_Alta\" name=\"Alta de Suscripción y Activación de Cuenta\" wf:departamento=\"Facturación y Legal\">\n" +
            "      <bpmn:incoming>Flow_Fib_4</bpmn:incoming>\n" +
            "      <bpmn:outgoing>Flow_Fib_5</bpmn:outgoing>\n" +
            "    </bpmn:userTask>\n" +
            "    <bpmn:endEvent id=\"EndEvent_Fib\" name=\"Fin\">\n" +
            "      <bpmn:incoming>Flow_Fib_5</bpmn:incoming>\n" +
            "    </bpmn:endEvent>\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Fib_1\" sourceRef=\"StartEvent_Fib\" targetRef=\"Activity_Vta_Factibilidad\" />\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Fib_2\" sourceRef=\"Activity_Vta_Factibilidad\" targetRef=\"Activity_Noc_Diseno\" />\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Fib_3\" sourceRef=\"Activity_Noc_Diseno\" targetRef=\"Activity_Sop_Instalacion\" />\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Fib_4\" sourceRef=\"Activity_Sop_Instalacion\" targetRef=\"Activity_Fact_Alta\" />\n" +
            "    <bpmn:sequenceFlow id=\"Flow_Fib_5\" sourceRef=\"Activity_Fact_Alta\" targetRef=\"EndEvent_Fib\" />\n" +
            "  </bpmn:process>\n" +
            "  <bpmndi:BPMNDiagram id=\"BPMNDiagram_Fib\">\n" +
            "    <bpmndi:BPMNPlane id=\"BPMNPlane_Fib\" bpmnElement=\"Collaboration_FibInst\">\n" +
            "      <bpmndi:BPMNShape id=\"Participant_Fib_di\" bpmnElement=\"Participant_FibInst\" isHorizontal=\"true\">\n" +
            "        <dc:Bounds x=\"120\" y=\"80\" width=\"1000\" height=\"640\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Lane_Fib_Ventas_di\" bpmnElement=\"Lane_Fib_Ventas\" isHorizontal=\"true\">\n" +
            "        <dc:Bounds x=\"150\" y=\"80\" width=\"970\" height=\"160\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Lane_Fib_Noc_di\" bpmnElement=\"Lane_Fib_Noc\" isHorizontal=\"true\">\n" +
            "        <dc:Bounds x=\"150\" y=\"240\" width=\"970\" height=\"160\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Lane_Fib_Soporte_di\" bpmnElement=\"Lane_Fib_Soporte\" isHorizontal=\"true\">\n" +
            "        <dc:Bounds x=\"150\" y=\"400\" width=\"970\" height=\"160\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Lane_Fib_Fact_di\" bpmnElement=\"Lane_Fib_Fact\" isHorizontal=\"true\">\n" +
            "        <dc:Bounds x=\"150\" y=\"560\" width=\"970\" height=\"160\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"StartEvent_Fib_di\" bpmnElement=\"StartEvent_Fib\">\n" +
            "        <dc:Bounds x=\"200\" y=\"140\" width=\"36\" height=\"36\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Activity_Vta_Factibilidad_di\" bpmnElement=\"Activity_Vta_Factibilidad\">\n" +
            "        <dc:Bounds x=\"280\" y=\"120\" width=\"100\" height=\"80\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Activity_Noc_Diseno_di\" bpmnElement=\"Activity_Noc_Diseno\">\n" +
            "        <dc:Bounds x=\"460\" y=\"280\" width=\"100\" height=\"80\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Activity_Sop_Instalacion_di\" bpmnElement=\"Activity_Sop_Instalacion\">\n" +
            "        <dc:Bounds x=\"640\" y=\"440\" width=\"100\" height=\"80\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"Activity_Fact_Alta_di\" bpmnElement=\"Activity_Fact_Alta\">\n" +
            "        <dc:Bounds x=\"820\" y=\"600\" width=\"100\" height=\"80\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNShape id=\"EndEvent_Fib_di\" bpmnElement=\"EndEvent_Fib\">\n" +
            "        <dc:Bounds x=\"1000\" y=\"622\" width=\"36\" height=\"36\" />\n" +
            "      </bpmndi:BPMNShape>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Fib_1_di\" bpmnElement=\"Flow_Fib_1\">\n" +
            "        <di:waypoint x=\"236\" y=\"158\" />\n" +
            "        <di:waypoint x=\"280\" y=\"158\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Fib_2_di\" bpmnElement=\"Flow_Fib_2\">\n" +
            "        <di:waypoint x=\"380\" y=\"160\" />\n" +
            "        <di:waypoint x=\"410\" y=\"160\" />\n" +
            "        <di:waypoint x=\"410\" y=\"320\" />\n" +
            "        <di:waypoint x=\"460\" y=\"320\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Fib_3_di\" bpmnElement=\"Flow_Fib_3\">\n" +
            "        <di:waypoint x=\"560\" y=\"320\" />\n" +
            "        <di:waypoint x=\"590\" y=\"320\" />\n" +
            "        <di:waypoint x=\"590\" y=\"480\" />\n" +
            "        <di:waypoint x=\"640\" y=\"480\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Fib_4_di\" bpmnElement=\"Flow_Fib_4\">\n" +
            "        <di:waypoint x=\"740\" y=\"480\" />\n" +
            "        <di:waypoint x=\"770\" y=\"480\" />\n" +
            "        <di:waypoint x=\"770\" y=\"640\" />\n" +
            "        <di:waypoint x=\"820\" y=\"640\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "      <bpmndi:BPMNEdge id=\"Flow_Fib_5_di\" bpmnElement=\"Flow_Fib_5\">\n" +
            "        <di:waypoint x=\"920\" y=\"640\" />\n" +
            "        <di:waypoint x=\"1000\" y=\"640\" />\n" +
            "      </bpmndi:BPMNEdge>\n" +
            "    </bpmndi:BPMNPlane>\n" +
            "  </bpmndi:BPMNDiagram>\n" +
            "</bpmn:definitions>";

    @Override
    public void run(String... args) throws Exception {
        log.info("Limpieza y recarga completa de base de datos solicitada por el usuario.");
        forceSeed();
    }

    public void forceSeed() {
        log.info("INICIANDO FRESH SEED: Limpiando base de datos...");
        solicitudRepository.deleteAll();
        usuarioRepository.deleteAll();
        departamentoRepository.deleteAll();
        documentoRepository.deleteAll();
        workflowDefinitionRepository.deleteAll();
        diagramaBpmnRepository.deleteAll();
        reporteRepository.deleteAll();
        userDeviceTokenRepository.deleteAll();
        workflowCoreEventRepository.deleteAll();
        workspaceGraphStateRepository.deleteAll();

        seedDepartamentos();
        seedUsuarios();
        seedWorkflowDefinitions();
        seedSolicitudes();

        log.info("FRESH SEED FINALIZADO. Entorno de telecomunicaciones listo con datos limpios de prueba.");
    }

    private void seedDepartamentos() {
        departamentoRepository.saveAll(List.of(
                buildDepto("Ventas Corporativas", "Área encargada de la atención comercial a empresas, licitaciones de conectividad y ampliaciones de contratos de internet dedicado."),
                buildDepto("Ingeniería de Red", "Ingenieros NOC, aprovisionamiento lógico, enrutamiento, asignación de IPs, IPsec VPN y administración de fibra óptica."),
                buildDepto("Soporte Técnico", "Atención post-venta de incidentes, triaje, despacho de cuadrillas técnicas a campo y control de calidad de enlaces."),
                buildDepto("Facturación y Legal", "Responsable de la redacción de adendas de contratos, ajuste de tarifas de facturación recurrente (MRR) y auditorías legales.")
        ));
    }

    private void seedUsuarios() {
        usuarioRepository.saveAll(List.of(
                buildDefaultUser("admin", "admin", "Súper Administrador Telecom", RolUsuario.ADMINISTRADOR, "Ingeniería de Red"),
                buildDefaultUser("ventas", "ventas", "Carlos Ventas (Comercial)", RolUsuario.REVISOR, "Ventas Corporativas"),
                buildDefaultUser("noc", "noc", "Elena NOC (Redes)", RolUsuario.REVISOR, "Ingeniería de Red"),
                buildDefaultUser("soporte", "soporte", "Mario Soporte (Helpdesk)", RolUsuario.REVISOR, "Soporte Técnico"),
                buildDefaultUser("facturacion", "facturacion", "Silvia Facturación (Legal)", RolUsuario.REVISOR, "Facturación y Legal"),
                buildDefaultUser("revisor", "revisor", "Andrés Revisor (Auditor Técnico)", RolUsuario.REVISOR, "Ingeniería de Red"),
                buildDefaultUser("solicitante", "solicitante", "Juan Solicitante (Cliente)", RolUsuario.SOLICITANTE, "Ventas Corporativas"),
                buildDefaultUser("cliente2", "cliente2", "María Cliente (Corp. Global)", RolUsuario.SOLICITANTE, "Ventas Corporativas"),
                buildDefaultUser("cliente3", "cliente3", "Pedro Cliente (Banco Santa Cruz)", RolUsuario.SOLICITANTE, "Ventas Corporativas"),
                buildDefaultUser("pepito", "pepito", "Pepito Cliente (Instalación de Fibra)", RolUsuario.SOLICITANTE, "Ventas Corporativas")
        ));
    }

    private void seedWorkflowDefinitions() {
        workflowDefinitionRepository.save(WorkflowDefinition.builder()
                .key("internet-expansion-workflow").name("Ampliación de Contrato de Internet").description("Gestión de ampliaciones de ancho de banda y firmas de adendas de fibra óptica").xml(INTERNET_EXPANSION_XML)
                .editadoPor("admin").departamentoEditor("Ingeniería de Red").version(1).build());

        workflowDefinitionRepository.save(WorkflowDefinition.builder()
                .key("tech-support-workflow").name("Soporte Técnico de Alta Prioridad").description("Flujo de diagnóstico, reparación y comisionamiento de enlaces de datos corporativos").xml(TECH_SUPPORT_XML)
                .editadoPor("admin").departamentoEditor("Soporte Técnico").version(1).build());

        workflowDefinitionRepository.save(WorkflowDefinition.builder()
                .key("data-discount-workflow").name("Aprobación de Descuentos Especiales").description("Aprobación de propuestas comerciales de tarifas especiales para planes de datos masivos").xml(DATA_DISCOUNT_XML)
                .editadoPor("admin").departamentoEditor("Ventas Corporativas").version(1).build());

        workflowDefinitionRepository.save(WorkflowDefinition.builder()
                .key("fiber-installation-workflow").name("Aprovisionamiento e Instalación de Fibra").description("Proceso de factibilidad, diseño de red, fusión de fibra y activación de servicio GPON").xml(FIBER_INSTALLATION_XML)
                .editadoPor("admin").departamentoEditor("Ventas Corporativas").version(1).build());
    }

    private void seedSolicitudes() {
        // --- 1. SOLICITUDES DE AMPLIACIÓN DE INTERNET ---
        SolicitudWorkflow s1 = crearSolicitudCompleta(
                "Ampliación de Ancho de Banda a 10 Gbps - Nexus Bank",
                "El cliente Nexus Bank solicita duplicar la velocidad contratada en su enlace principal dedicado, subiendo de 5 Gbps a 10 Gbps simétricos sobre fibra óptica redundante. Requiere la validación comercial preliminar, la cotización de los nuevos transceptores SFP+ de 10G por Ingeniería, y el posterior ajuste contractual por Facturación.",
                Prioridad.URGENTE, "Ventas Corporativas", "solicitante", EstadoWorkflow.EN_REVISION, "ventas",
                "internet-expansion-workflow", "Activity_Vta_Comercial", "Validar Viabilidad Comercial");

        SolicitudWorkflow s2 = crearSolicitudCompleta(
                "Upgrade de Enlace de Fibra Óptica - Corporación Global",
                "Solicitud de incremento de capacidad del enlace secundario de contingencia a 1 Gbps simétrico. Viabilidad comercial previamente aprobada por el área de Ventas Corporativas. Pasa a Ingeniería de Red para asignación del direccionamiento de red, estudio técnico de holgura en el splitter óptico y aprovisionamiento lógico en ruteadores perimetrales.",
                Prioridad.ALTA, "Ingeniería de Red", "solicitante", EstadoWorkflow.EN_REVISION, "noc",
                "internet-expansion-workflow", "Activity_Noc_Estudio", "Estudio Técnico e Incremento de Capacidad");

        // --- 2. SOLICITUD DE SOPORTE TÉCNICO ---
        SolicitudWorkflow s3 = crearSolicitudCompleta(
                "Inestabilidad y Caída de Paquetes en Enlace Dedicado - Planta Industrial",
                "El cliente reporta microcortes recurrentes e incremento crítico de la latencia a más de 120ms en su enlace de fibra óptica dedicado principal que conecta la Planta Industrial de Nexus Bank. Se requiere un diagnóstico inmediato de la potencia óptica (dBm) por parte de Soporte Técnico y posible asignación de cuadrilla de campo si se detecta atenuación severa en el tramo de última milla.",
                Prioridad.URGENTE, "Soporte Técnico", "solicitante", EstadoWorkflow.EN_REVISION, "soporte",
                "tech-support-workflow", "Activity_Sop_Diag", "Diagnóstico y Triaje Técnico");

        // --- 3. SOLICITUD DE DESCUENTO ---
        SolicitudWorkflow s4 = crearSolicitudCompleta(
                "Tarifa Especial y Descuento del 15% - Nexus Bank (300 Líneas Móviles)",
                "Propuesta comercial de descuento del 15% para el plan masivo corporativo de 300 líneas móviles postpago con datos ilimitados. Requiere análisis de facturación, cálculo del margen bruto operativo mínimo aceptable y validación por la dirección financiera antes de la presentación final.",
                Prioridad.MEDIA, "Facturación y Legal", "solicitante", EstadoWorkflow.EN_REVISION, "facturacion",
                "data-discount-workflow", "Activity_Fact_Aprobacion", "Aprobación Financiera y Tarifaria");

        // --- 4. NUEVAS SOLICITUDES PARA ENRIQUECER EL CONJUNTO DE DATOS ---
        
        // SLA_CRITICO (Ingeniería de Red - cliente2)
        SolicitudWorkflow s5 = crearSolicitudCompleta(
                "Aprovisionamiento de VLANs y Prefijos IPv6 - DataCenter El Alto",
                "Asignación urgente de direccionamiento IPv6 nativo de operador y configuración de ruteo dinámico BGP multi-homed para el nuevo DataCenter corporativo. Presenta retrasos por falta de confirmación de prefijo en el registro LACNIC.",
                Prioridad.ALTA, "Ingeniería de Red", "cliente2", EstadoWorkflow.SLA_CRITICO, "noc",
                "internet-expansion-workflow", "Activity_Noc_Estudio", "Estudio Técnico e Incremento de Capacidad");

        // BLOQUEADO (Soporte Técnico - cliente2)
        SolicitudWorkflow s6 = crearSolicitudCompleta(
                "Instalación Enlace Satelital VSAT - Campamento Minero San Cristóbal",
                "Soporte técnico para el despliegue e instalación en campo de antena satelital VSAT de contingencia para telemetría SCADA. Bloqueado temporalmente debido a malas condiciones meteorológicas en la zona andina que impiden el izaje seguro de la torre.",
                Prioridad.URGENTE, "Soporte Técnico", "cliente2", EstadoWorkflow.BLOQUEADO, "soporte",
                "tech-support-workflow", "Activity_Noc_Intervencion", "Resolución e Intervención de Red");

        // APROBADO (Facturación y Legal - cliente3)
        SolicitudWorkflow s7 = crearSolicitudCompleta(
                "Ampliación Red de Fibra para Sucursales - Supermercados Fidalga",
                "Proyecto comercial y técnico para conectar 5 sucursales nuevas a la red metropolitana de fibra a 500 Mbps. El estudio técnico fue favorable y el contrato de adenda fue debidamente firmado por el representante legal del cliente.",
                Prioridad.MEDIA, "Facturación y Legal", "cliente3", EstadoWorkflow.APROBADO, "facturacion",
                "internet-expansion-workflow", "Activity_Fact_Adenda", "Firma de Adenda y Activación Comercial");

        // RECHAZADO (Facturación y Legal - cliente3)
        SolicitudWorkflow s8 = crearSolicitudCompleta(
                "Propuesta de Descuento 35% por Volumen - Cooperativa Minera Litoral",
                "Solicitud de tarifa ultra-reducida de internet dedicado para campamento. Rechazada por el Comité de Tarifas debido a que el margen bruto resultante (22%) se encuentra muy por debajo de la rentabilidad mínima estipulada del 45% sobre inversión de tendido.",
                Prioridad.BAJA, "Facturación y Legal", "cliente3", EstadoWorkflow.RECHAZADO, "facturacion",
                "data-discount-workflow", "Activity_Fact_Aprobacion", "Aprobación Financiera y Tarifaria");

        // --- 5. SOLICITUDES PENDIENTES SIN ASIGNAR ---
        crearSolicitudCompleta(
                "Instalación de Enlace MPLS Secundario - Oficina Sucursal Norte",
                "Requerimiento de instalación física y cableado estructurado para configurar un enlace de contingencia MPLS de 100 Mbps en la sucursal norte del cliente. Pendiente de asignación inicial por soporte técnico.",
                Prioridad.BAJA, "Soporte Técnico", "solicitante", EstadoWorkflow.PENDIENTE, null,
                null, null, null);

        crearSolicitudCompleta(
                "Auditoría de Enrutamiento BGP - Bloque de IPs IPv4 Agotadas",
                "Revisión de tablas de ruteo global ante posible secuestro de prefijos IP del pool asignado al segmento corporativo. Pendiente de vinculación por supervisor.",
                Prioridad.ALTA, "Ingeniería de Red", "cliente3", EstadoWorkflow.PENDIENTE, null,
                null, null, null);

        // Documentos de prueba específicos de telecomunicaciones
        seedDocumentoColaborativo(s1, "Informe_Factibilidad_Comercial_NexusBank", "Análisis de rentabilidad y cotización de equipamiento SFP+ de 10G.", 
                "### INFORME DE FACTIBILIDAD COMERCIAL - NEXUS BANK\n\n" +
                "| Concepto | Detalle Técnico | Tarifa Mensual (USD) | Costo Instalación (Costo Único) |\n" +
                "| :--- | :--- | :---: | :---: |\n" +
                "| **Ancho de Banda Actual** | 5 Gbps Dedicado Simétrico | $2,500.00 | - |\n" +
                "| **Ancho de Banda Nuevo** | 10 Gbps Dedicado Simétrico | $4,200.00 | - |\n" +
                "| **Equipamiento SFP+ 10G** | Transceptores Cisco y Patchcore Monomodo | - | $350.00 |\n" +
                "| **SLA Garantizado** | 99.98% de disponibilidad anual | Incluido | - |\n\n" +
                "**Incremento Neto Recurrente Mensual (MRR):** +$1,700.00 USD\n" +
                "**Plazo Contractual Sugerido:** 24 meses renovable automáticamente.\n\n" +
                "**Justificación:** El cliente Nexus Bank se encuentra expandiendo su core bancario y la replicación Multi-AZ de su base de datos requiere mayor throughput nocturno para evitar encolamientos.", "solicitante");

        seedDocumentoColaborativo(s2, "Aprovisionamiento_Logico_Red_NOC", "Plano lógico de enrutamiento y puertos asignados para el upgrade.", 
                "### PLAN DE DIRECCIONAMIENTO E IP DE INGENIERÍA - NOC\n\n" +
                "- **Cliente:** Corporación Global\n" +
                "- **ID de Circuito:** L2-FIB-CORP-98831-SCZ\n" +
                "- **Ancho de Banda Configurado:** 1 Gbps (Simétrico)\n\n" +
                "#### Direccionamiento WAN Asignado:\n" +
                "- **IP Prefijo WAN:** 187.45.190.224/30\n" +
                "- **IP Gateway Operador:** 187.45.190.225 (Router PE-03-SCZ)\n" +
                "- **IP WAN Cliente:** 187.45.190.226 (Router CE-01-Global)\n" +
                "- **Prefijo LAN /29 Público:** 187.45.195.40/29\n\n" +
                "#### Configuración del Puerto en Switch PE:\n" +
                "```bash\n" +
                "interface TenGigabitEthernet0/1/2\n" +
                " description Upgrade_Enlace_Corp_Global_VLAN_891\n" +
                " switchport trunk allowed vlan 891\n" +
                " service-policy input 1GBPS_SHAPING\n" +
                " service-policy output 1GBPS_SHAPING\n" +
                "```", "noc");

        seedDocumentoColaborativo(s3, "Diagnostico_Potencia_Optica_OTDR", "Registro de mediciones de reflectometría (OTDR) del tramo de fibra.", 
                "### DIAGNÓSTICO TÉCNICO DE ENLACE DE FIBRA ÓPTICA\n\n" +
                "- **ID de Circuito:** L1-FIB-NEXUS-00192-CBBA\n" +
                "- **Medición con OTDR:** Realizado a las 19:15 local.\n\n" +
                "#### Resultados de Potencia Óptica:\n" +
                "1. **Potencia de Transmisión (TX) PE:** -3.2 dBm (Normal)\n" +
                "2. **Potencia de Recepción (RX) CE:** -29.8 dBm (**CRÍTICO - Alta Atenuación**)\n" +
                "3. **Pérdida Total del Enlace:** 26.6 dB (Límite aceptable es 18 dB)\n\n" +
                "#### Diagnóstico OTDR:\n" +
                "- Se detecta una anomalía de reflexión (posible microdoblez o empalme sucio) en la caja de paso #4 situada en la Av. Doble Vía, Km 3.5.\n\n" +
                "**Acción Recomendada:** Despachar cuadrilla nocturna para limpieza del conector o fusión de fibra.", "soporte");

        seedDocumentoColaborativo(s4, "Analisis_Margen_Tarifario_Postpago", "Cálculo de costo marginal y aprobación de descuento del 15% de telefonía corporativa.", 
                "### ANÁLISIS DE RENTABILIDAD Y MÁRGENES - COMITÉ DE TARIFAS\n\n" +
                "- **Servicio:** 300 Líneas Móviles Postpago Corporativas (Ilimitadas)\n" +
                "- **Tarifa Estándar Mensual:** $35.00 USD por línea\n" +
                "- **Tarifa Especial Solicitada (-15%):** $29.75 USD por línea\n\n" +
                "#### Estructura de Costos por Línea:\n" +
                "| Concepto Costo | Costo Marginal Mensual | Porcentaje sobre Venta ($29.75) |\n" +
                "| :--- | :--- | :--- |\n" +
                "| **Costo Interconexión** | $4.20 USD | 14.1% |\n" +
                "| **Soporte y Operación** | $3.00 USD | 10.1% |\n" +
                "| **Amortización Red** | $5.50 USD | 18.5% |\n" +
                "| **Margen Bruto Unitario** | **$17.05 USD** | **57.3%** |\n\n" +
                "**Conclusión:** El margen operativo del 57.3% supera con holgura el piso de rentabilidad mínima corporativa establecido en 45%. Se recomienda la aprobación del descuento.", "ventas");

        // --- 6. PROCESO DE INSTALACIÓN DE FIBRA ÓPTICA PARA PEPITO ---
        // Representa cada una de las 4 etapas del flujo de instalación GPON para que Pepito vea su progreso
        
        // Etapa 1: Ventas - "Validar Viabilidad y Cobertura"
        SolicitudWorkflow sPepito1 = crearSolicitudCompleta(
                "Instalación de Fibra Óptica Residencial - Pepito",
                "Solicitud de instalación de internet de fibra óptica de alta velocidad (300 Mbps simétricos) para la residencia de Pepito. Pendiente de validación de cobertura física y distancia del tramo hasta la caja de distribución NAP más cercana.",
                Prioridad.URGENTE, "Ventas Corporativas", "pepito", EstadoWorkflow.EN_REVISION, "ventas",
                "fiber-installation-workflow", "Activity_Vta_Factibilidad", "Validar Viabilidad y Cobertura");

        // Etapa 2: Ingeniería - "Diseño de Red y Puerto GPON"
        SolicitudWorkflow sPepito2 = crearSolicitudCompleta(
                "Instalación de Enlace Fibra Pyme - Pepito S.R.L.",
                "Solicitud de fibra simétrica comercial para la oficina de Pepito S.R.L. La factibilidad comercial y de cobertura física ya fue validada favorablemente por Ventas. Actualmente en manos de Ingeniería de Red para el diseño de ruta, asignación de puerto de splitter óptico y asignación del puerto GPON en la OLT.",
                Prioridad.ALTA, "Ingeniería de Red", "pepito", EstadoWorkflow.EN_REVISION, "noc",
                "fiber-installation-workflow", "Activity_Noc_Diseno", "Diseño de Red y Puerto GPON");

        // Etapa 3: Soporte - "Instalación Física y Fusión en Domicilio"
        SolicitudWorkflow sPepito3 = crearSolicitudCompleta(
                "Instalación de Fibra Simétrica - Oficina Central Pepito",
                "Fusión de fibra y tendido de última milla para la oficina central. La viabilidad técnica y el diseño lógico en OLT ya están configurados por Ingeniería. Actualmente asignado a la cuadrilla de Soporte Técnico para realizar el tendido del cable drop, fusionar en la caja NAP asignada, e instalar y calibrar la ONU en el domicilio.",
                Prioridad.URGENTE, "Soporte Técnico", "pepito", EstadoWorkflow.EN_REVISION, "soporte",
                "fiber-installation-workflow", "Activity_Sop_Instalacion", "Instalación Física y Fusión en Domicilio");

        // Etapa 4: Facturación - "Alta de Suscripción y Activación de Cuenta"
        SolicitudWorkflow sPepito4 = crearSolicitudCompleta(
                "Activación y Alta de Servicio Fibra - Pepito Corporativo",
                "La instalación de cableado y fusión de fibra en el domicilio de Pepito ha culminado exitosamente con un nivel de potencia de -19.5 dBm. El servicio está listo en capa física. Pasa al área de Facturación y Legal para registrar el plan recurrente, dar de alta la cuenta de facturación y habilitar las credenciales PPPoE de navegación.",
                Prioridad.MEDIA, "Facturación y Legal", "pepito", EstadoWorkflow.EN_REVISION, "facturacion",
                "fiber-installation-workflow", "Activity_Fact_Alta", "Alta de Suscripción y Activación de Cuenta");

        // Documentos de Pepito
        seedDocumentoColaborativo(sPepito1, "Formulario_Solicitud_Fibra_Pepito", "Formulario de registro inicial y validación de cobertura en calle.", 
                "### FORMULARIO DE SOLICITUD DE FIBRA ÓPTICA RESIDENCIAL\n\n" +
                "- **Cliente:** Pepito (José Pérez)\n" +
                "- **Dirección de Residencia:** Av. Busch, Calle 4, Nro 450\n" +
                "- **Plan Seleccionado:** Fibra GPON 300 Mbps Hogar\n" +
                "- **Costo Mensual Base:** $39.00 USD / mes\n\n" +
                "#### Estado de Validación de Cobertura:\n" +
                "- **Distancia estimada a la caja NAP más cercana:** En medición de campo por el agente de ventas.\n" +
                "- **Disponibilidad de puertos en splitter:** Confirmado preliminarmente por mapa GIS.\n\n" +
                "**Nota del agente:** El cliente solicita la instalación a la brevedad debido a que realiza teletrabajo continuo.", "pepito");

        seedDocumentoColaborativo(sPepito2, "Estudio_Diseno_Red_PepitoPyme", "Plano lógico e informe técnico de puerto GPON asignado en OLT.", 
                "### INFORME DE INGENIERÍA DE RED - DISEÑO GPON PYME\n\n" +
                "- **Cliente:** Pepito S.R.L. (Sucursal Equipetrol)\n" +
                "- **Velocidad Contratada:** 150 Mbps Simétrico Pyme\n\n" +
                "#### Parámetros Técnicos de Diseño:\n" +
                "1. **OLT Central:** OLT-SCZ-EQUIPETROL-02\n" +
                "2. **Tarjeta / Puerto GPON:** Board 0, Slot 3, Port 4\n" +
                "3. **Caja NAP de Distribución:** NAP-EQ-12 (Capacidad: 1:16, Puertos Libres: 3)\n" +
                "4. **Puerto Asignado en Caja NAP:** Puerto 6\n" +
                "5. **Dirección IP LAN CE:** 192.168.100.1/24 (Asignado por DHCP de la ONU)\n\n" +
                "**Aprobado por NOC:** Listo para envío de orden de trabajo física.", "noc");

        seedDocumentoColaborativo(sPepito3, "Orden_Trabajo_Instalacion_Fisica_Pepito", "Orden de despacho técnico en campo para tendido de acometida y fusión.", 
                "### ORDEN DE TRABAJO TÉCNICA DE INSTALACIÓN - SOPORTE TÉCNICO\n\n" +
                "- **Cliente:** Oficina Central Pepito (Calle Florida Nro 12)\n" +
                "- **Cuadrilla Técnica Despachada:** Cuadrilla #3 (Ing. José Choque)\n\n" +
                "#### Lista de Tareas en Campo:\n" +
                "- [x] Tendido de cable drop monomodo de exterior (110 metros utilizados).\n" +
                "- [x] Fusión de fibra óptica en puerto 4 de caja NAP-FL-08.\n" +
                "- [ ] Armado del conector rápido en el domicilio del cliente.\n" +
                "- [ ] Conexión y calibración de potencia óptica en la ONU (Modelo Huawei HG8245H).\n" +
                "- [ ] Verificación de potencia (Umbral óptimo: entre -15 y -23 dBm).\n\n" +
                "**Comentarios en campo:** El tendido aéreo se realizó sin incidentes. Pendiente de fusión final en domicilio.", "soporte");

        seedDocumentoColaborativo(sPepito4, "Contrato_Suscripcion_Digital_Pepito", "Contrato de adhesión para el aprovisionamiento y facturación del servicio.", 
                "### CONTRATO DE ADHESIÓN Y ALTA DE SUSCRIPCIÓN - GPON HOGAR\n\n" +
                "- **Contrato Nro:** CONT-FIB-PEPITO-10029\n" +
                "- **Suscriptor:** Pepito (José Pérez)\n" +
                "- **Plan Activo:** Plan Fibra Premium 500 Mbps Corporativo\n" +
                "- **Cargo Fijo Recurrente (MRR):** $79.00 USD / mes\n" +
                "- **Ciclo de Facturación:** Del 1 al 5 de cada mes calendario\n\n" +
                "#### Verificación Física de Entrega:\n" +
                "- **Potencia de Recepción (RX ONU):** -19.5 dBm (Excelente señal)\n" +
                "- **Dirección MAC de la ONU:** E0:24:7F:A1:C2:5E\n" +
                "- **Credenciales PPPoE Generadas:** `pepito_fibra@nexus` / `key98321`\n\n" +
                "**Aprobación del área de Facturación:** El contrato ha sido validado y archivado digitalmente. Cuenta activa en sistema de cobros.", "facturacion");

        // Sincronizar el XML con los códigos de los tickets creados
        actualizarXmlConSolicitudes(s1, s2, s3, s4);
        actualizarXmlFiberInstallation(sPepito1, sPepito2, sPepito3, sPepito4);
    }

    private void actualizarXmlConSolicitudes(SolicitudWorkflow sys, SolicitudWorkflow noc, SolicitudWorkflow sop, SolicitudWorkflow billing) {
        // 1. internet-expansion-workflow
        workflowDefinitionRepository.findByKey("internet-expansion-workflow").ifPresent(def -> {
            String xml = def.getXml();
            xml = xml.replace("id=\"Activity_Vta_Comercial\" name=\"Validar Viabilidad Comercial\"", "id=\"Activity_Vta_Comercial\" name=\"Validar Viabilidad Comercial\" wf:solicitudes=\"" + sys.getCodigoSeguimiento() + "\"");
            xml = xml.replace("id=\"Activity_Noc_Estudio\" name=\"Estudio Técnico e Incremento de Capacidad\"", "id=\"Activity_Noc_Estudio\" name=\"Estudio Técnico e Incremento de Capacidad\" wf:solicitudes=\"" + noc.getCodigoSeguimiento() + "\"");
            def.setXml(xml);
            workflowDefinitionRepository.save(def);
        });

        // 2. tech-support-workflow
        workflowDefinitionRepository.findByKey("tech-support-workflow").ifPresent(def -> {
            String xml = def.getXml();
            xml = xml.replace("id=\"Activity_Sop_Diag\" name=\"Diagnóstico y Triaje Técnico\"", "id=\"Activity_Sop_Diag\" name=\"Diagnóstico y Triaje Técnico\" wf:solicitudes=\"" + sop.getCodigoSeguimiento() + "\"");
            def.setXml(xml);
            workflowDefinitionRepository.save(def);
        });

        // 3. data-discount-workflow
        workflowDefinitionRepository.findByKey("data-discount-workflow").ifPresent(def -> {
            String xml = def.getXml();
            xml = xml.replace("id=\"Activity_Fact_Aprobacion\" name=\"Aprobación Financiera y Tarifaria\"", "id=\"Activity_Fact_Aprobacion\" name=\"Aprobación Financiera y Tarifaria\" wf:solicitudes=\"" + billing.getCodigoSeguimiento() + "\"");
            def.setXml(xml);
            workflowDefinitionRepository.save(def);
        });
    }

    private void actualizarXmlFiberInstallation(SolicitudWorkflow fibVta, SolicitudWorkflow fibNoc, SolicitudWorkflow fibSop, SolicitudWorkflow fibFact) {
        workflowDefinitionRepository.findByKey("fiber-installation-workflow").ifPresent(def -> {
            String xml = def.getXml();
            xml = xml.replace("id=\"Activity_Vta_Factibilidad\" name=\"Validar Viabilidad y Cobertura\"", "id=\"Activity_Vta_Factibilidad\" name=\"Validar Viabilidad y Cobertura\" wf:solicitudes=\"" + fibVta.getCodigoSeguimiento() + "\"");
            xml = xml.replace("id=\"Activity_Noc_Diseno\" name=\"Diseño de Red y Puerto GPON\"", "id=\"Activity_Noc_Diseno\" name=\"Diseño de Red y Puerto GPON\" wf:solicitudes=\"" + fibNoc.getCodigoSeguimiento() + "\"");
            xml = xml.replace("id=\"Activity_Sop_Instalacion\" name=\"Instalación Física y Fusión en Domicilio\"", "id=\"Activity_Sop_Instalacion\" name=\"Instalación Física y Fusión en Domicilio\" wf:solicitudes=\"" + fibSop.getCodigoSeguimiento() + "\"");
            xml = xml.replace("id=\"Activity_Fact_Alta\" name=\"Alta de Suscripción y Activación de Cuenta\"", "id=\"Activity_Fact_Alta\" name=\"Alta de Suscripción y Activación de Cuenta\" wf:solicitudes=\"" + fibFact.getCodigoSeguimiento() + "\"");
            def.setXml(xml);
            workflowDefinitionRepository.save(def);
        });
    }

    private SolicitudWorkflow crearSolicitudCompleta(String titulo, String desc, Prioridad prio, String depto, String creador, 
                                                   EstadoWorkflow estadoFinal, String revisor, String flowId, String tareaId, String tareaNombre) {
        String codigo = codigoGenerator.generarCodigo();
        SolicitudWorkflow s = SolicitudWorkflow.builder()
                .codigoSeguimiento(codigo).titulo(titulo).descripcion(desc).prioridad(prio).estado(EstadoWorkflow.PENDIENTE)
                .departamentoActual(depto).usuarioCreador(creador).workflowDefinitionId(flowId).tareaActualId(tareaId).tareaActualNombre(tareaNombre)
                .fechaCreacion(LocalDateTime.now()).fechaLimiteAtencion(LocalDateTime.now().plusHours(48)).build();

        s.registrarTransicion(null, EstadoWorkflow.PENDIENTE, creador, "SOLICITANTE", "Registro inicial de solicitud en el panel de control.");
        if (estadoFinal != EstadoWorkflow.PENDIENTE) {
            s.setUsuarioAsignado(revisor);
            s.registrarTransicion(EstadoWorkflow.PENDIENTE, EstadoWorkflow.EN_REVISION, revisor, "REVISOR", "Iniciada auditoría y análisis en etapa: " + (tareaNombre != null ? tareaNombre : "Revisión"));
            
            if (estadoFinal != EstadoWorkflow.EN_REVISION) {
                String descEstado = "Tránsito de estado completado hacia: " + estadoFinal;
                if (estadoFinal == EstadoWorkflow.APROBADO) {
                    descEstado = "La solicitud ha sido aprobada de manera conforme por todas las áreas responsables.";
                } else if (estadoFinal == EstadoWorkflow.RECHAZADO) {
                    descEstado = "La solicitud ha sido rechazada tras detectar discrepancias técnicas o de factibilidad.";
                } else if (estadoFinal == EstadoWorkflow.SLA_CRITICO) {
                    descEstado = "La solicitud presenta demora de tiempo. Alerta de SLA Crítico enviada.";
                } else if (estadoFinal == EstadoWorkflow.BLOQUEADO) {
                    descEstado = "La solicitud se encuentra bloqueada a la espera de documentación complementaria.";
                }
                s.registrarTransicion(EstadoWorkflow.EN_REVISION, estadoFinal, revisor != null ? revisor : "system", "REVISOR", descEstado);
            }
        }
        return solicitudRepository.save(s);
    }

    private Departamento buildDepto(String nombre, String desc) {
        return Departamento.builder().nombre(nombre).descripcion(desc).creadoPor("system").activo(true).fechaCreacion(LocalDateTime.now()).build();
    }

    private Usuario buildDefaultUser(String username, String password, String nombre, RolUsuario rol, String depto) {
        String avatarUrl = "https://ui-avatars.com/api/?name=" + nombre.replace(" ", "+") + "&background=random&color=fff&size=128";
        return Usuario.builder().username(username).password(password).nombreCompleto(nombre).rol(rol).departamento(depto).avatarUrl(avatarUrl).fechaCreacion(LocalDateTime.now()).build();
    }

    private void seedDocumentoColaborativo(SolicitudWorkflow solicitud, String nombre, String desc, String contenido, String creador) {
        Documento documento = Documento.builder()
                .solicitudId(solicitud.getId())
                .nombre(nombre)
                .descripcion(desc)
                .tipo("COLLABORATIVE")
                .versionActual(1)
                .creadoPor(creador)
                .fechaCreacion(LocalDateTime.now())
                .fechaActualizacion(LocalDateTime.now())
                .contenidoColaborativo(contenido)
                .build();
        
        documento.agregarVersion(Documento.VersionDocumento.builder()
                .version(1)
                .subidoPor(creador)
                .fechaSubida(LocalDateTime.now())
                .comentarioCambio("Versión inicial creada por el sistema")
                .contenidoColaborativoSnapshot(contenido)
                .build());
                
        documentoRepository.save(documento);
    }
}
