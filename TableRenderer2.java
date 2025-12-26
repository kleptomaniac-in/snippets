@Service
public class ConfiguredTableRenderer {
    private final TableRenderer tableRenderer;
    private final HtmlToPdfService pdfService;
    private final TableRenderConfig config;

    public byte[] renderTableAsPdf(String tableKey) throws Exception {
        // Generate HTML first
        String htmlTable = renderTable(tableKey);
        
        // Wrap in full HTML document for better PDF rendering
        String fullHtml = wrapInHtmlDocument(htmlTable, tableKey);
        
        return pdfService.convertHtmlToPdf(fullHtml);
    }
    
    private String wrapInHtmlDocument(String tableHtml, String tableKey) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>%s Report</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .data-table { 
                        width: 100%; border-collapse: collapse; 
                        margin: 20px 0; font-size: 14px;
                    }
                    .data-table th, .data-table td { 
                        border: 1px solid #ddd; padding: 12px; 
                        text-align: left; 
                    }
                    .data-table th { background-color: #f2f2f2; font-weight: bold; }
                    .data-table tr:nth-child(even) { background-color: #f9f9f9; }
                </style>
            </head>
            <body>
                <h1>%s Report</h1>
                %s
            </body>
            </html>
            """.formatted(tableKey.replace("-", " ").toUpperCase(), 
                         tableKey.replace("-", " ").toUpperCase(), 
                         tableHtml);
    }
}