package com.atheltic.reportes.controllers.inyeccion;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

@RestController
@RequestMapping("/reportes-api/inyeccion")
public class InyeccionController {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @GetMapping("/avance-dia")
    public ResponseEntity<byte[]> reporteDiario(@RequestParam(required = false) String dia) throws Exception {
        try {
            // Ruta al archivo .jasper dentro de resources/reportes
            InputStream reporteStream = new ClassPathResource("reportes/inyeccion/AvanceDiario.jasper")
                    .getInputStream();

            // Parámetros al reporte
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("dia", dia);

            // Conexión a SQL Server sin cifrado (evitando problemas de TLS)
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // URL con configuración de seguridad directamente en la cadena
           /*  String url = "jdbc:sqlserver://192.168.95.1\\DATOS65;" +
                    "databaseName=Avances;" +
                    "encrypt=false;" +
                    "trustServerCertificate=true;" +
                    "integratedSecurity=false;" +
                    "loginTimeout=30";*/

            Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

            // Llenar el reporte con la conexión
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporteStream, parametros, conn);

            // Cerrar conexión
            conn.close();

            // Exportar a PDF en bytes
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "ReporteDiario.pdf");
            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            System.err.println("Error al generar reporte: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/avance-semana")
    public ResponseEntity<byte[]> reporteSemanal(@RequestParam(required = true) Integer anio,
            @RequestParam(required = true) Integer semana) throws Exception {
        try {
            int year = (anio != null) ? anio : java.time.Year.now().getValue();
            int weekNum = (semana != null) ? semana : 1;

            // Ruta al archivo .jasper dentro de resources/reportes
            InputStream reporteStream = new ClassPathResource("reportes/inyeccion/AvanceSemana.jasper")
                    .getInputStream();

            // Parámetros al reporte
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("year", year);
            parametros.put("semana", weekNum);

            Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);

            // Llenar el reporte con la conexión
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporteStream, parametros, conn);

            // Cerrar conexión
            conn.close();

            // Exportar a PDF en bytes
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "ReporteSemanal.pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            System.err.println("Error al generar reporte: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/inventario-proceso")
    public ResponseEntity<byte[]> inventarioProceso() throws Exception {
        try {
            InputStream reporteStream = new ClassPathResource("reportes/inyeccion/Inventario.jasper").getInputStream();
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporteStream, null, conn);
            conn.close();

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "ReporteInventario.pdf");

            return ResponseEntity.ok().headers(headers).body(pdfBytes);
        } catch (Exception e) {
            System.err.println("Error al generar reporte: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}
