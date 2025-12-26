how do I write a java component  to display data in a  vertical table with first column values serving as column headers and 2nd column values for the column.

Create a `VerticalTableRenderer` component that transforms row-based data into a key-value display where the first column becomes headers and the second column shows values, perfect for configuration summaries or profile displays.

## Vertical Table Concept
Instead of traditional horizontal tables, this renders data as:
```
┌─────────────────┬──────────┐
│ Employee Name   │ Ravi     │
│ Department      │ IT       │
│ Salary          │ 75000    │
│ Join Date       │ 2023-01  │
└─────────────────┴──────────┘
```

## FreeMarker Template (vertical-table.ftl)
```html
<table class="vertical-table" style="width: 100%; border-collapse: collapse;">
  <#list data as row>
    <#if row_index == 0>
      <tr>
        <#list row as keyValue>
          <th style="border: 1px solid #ddd; padding: 12px; background: #f8f9fa; font-weight: bold; width: 40%;">
            ${keyValue?string}
          </th>
          <th style="border: 1px solid #ddd; padding: 12px; background: #f8f9fa; width: 60%;"></th>
        </#list>
      </tr>
    </#if>
    <tr>
      <#list row as keyValue>
        <td style="border: 1px solid #ddd; padding: 12px; vertical-align: top;">
          ${keyValue?string}
        </td>
      </#list>
    </tr>
  </#list>
</table>
```
First row becomes headers, subsequent rows show key-value pairs side-by-side.

## Java Component
```java
@Service
public class VerticalTableRenderer {
    private final Configuration freemarkerConfig;

    public VerticalTableRenderer() {
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        freemarkerConfig.setClassForTemplateLoading(getClass(), "/templates");
        freemarkerConfig.setDefaultEncoding("UTF-8");
    }

    public String renderVerticalTable(List<List<String>> data) throws Exception {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("data", data);
        
        Template template = freemarkerConfig.getTemplate("vertical-table.ftl");
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return wrapInHtmlDocument(writer.toString());
    }
    
    private String wrapInHtmlDocument(String tableHtml) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <title>Vertical Table</title>
                <style>
                    body { font-family: Arial, sans-serif; margin: 20px; }
                    .vertical-table td, .vertical-table th { 
                        border: 1px solid #ddd; padding: 12px; 
                    }
                </style>
            </head>
            <body>
                %s
            </body>
            </html>
            """.formatted(tableHtml);
    }
}
```

## Data Preparation
Convert your `Map<String, Object>` source data:
```java
List<List<String>> verticalData = new ArrayList<>();
verticalData.add(Arrays.asList("Employee Name", "Department"));  // Headers row
verticalData.add(Arrays.asList("Ravi Kumar", "IT"));
verticalData.add(Arrays.asList("Monthly Salary", "₹75,000"));
verticalData.add(Arrays.asList("Join Date", "2023-01-15"));
verticalData.add(Arrays.asList("Status", "Active"));

String html = verticalRenderer.renderVerticalTable(verticalData);
```

## YAML Integration
Extend your `TableRenderConfig`:
```yaml
table-renders:
  profile-summary:
    type: vertical
    data:
      - ["Employee Name", "Department"]
      - ["Ravi Kumar", "IT"]
      - ["Salary", "₹75,000"]
    template: vertical-table.ftl
```

## PDF Generation
Use the same `HtmlToPdfService` - vertical tables render perfectly:
```java
@GetMapping(value = "/vertical-report/pdf", produces = "application/pdf")
public ResponseEntity<byte[]> verticalPdf() throws Exception {
    String html = verticalRenderer.renderVerticalTable(data);
    byte[] pdf = pdfService.convertHtmlToPdf(html);
    // ... headers and response
}
```

This creates compact, readable vertical displays ideal for reports, profiles, and configuration summaries that fit better on narrow pages or mobile views.

Citations:
[1] TableView (Java SE 25 & JDK 25 [ad-hoc build]) https://cr.openjdk.org/~prr/8357176/api/java.desktop/javax/swing/text/TableView.html
[2] Vertical Table Header Cell Renderer | Java Tips Weblog https://tips4java.wordpress.com/2009/03/06/vertical-table-header-cell-renderer/
[3] How to display vertical grid lines in a table with Java? https://www.tutorialspoint.com/how-to-display-vertical-grid-lines-in-a-table-with-java
[4] How to Use Tables (The Java™ Tutorials > Creating ... https://docs.oracle.com/javase/tutorial/uiswing/components/table.html
[5] Java JTable Ep.03 - Add Custom Vertical Row Headers https://www.youtube.com/watch?v=ph2Cxemh23w
[6] JTable (Java Platform SE 8 ) https://javadoc.scijava.org/Java8/javax/swing/JTable.html
[7] java - JavaFX - create vertical Table https://stackoverflow.com/questions/66662106/javafx-create-vertical-table
[8] tips4java/source/VerticalTableHeaderCellRenderer.java at ... https://github.com/tips4java/tips4java/blob/main/source/VerticalTableHeaderCellRenderer.java
[9] JTable: Making Table-Cell display text vertical https://forums.oracle.com/ords/apexds/post/jtable-making-table-cell-display-text-vertical-2719
[10] no vertical lines in JTableHeader https://coderanch.com/t/416084/java/vertical-lines-JTableHeader
