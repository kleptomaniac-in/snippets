@Service
public class TableRenderer {
    private final Configuration freemarkerConfig;

    public TableRenderer() {
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        freemarkerConfig.setClassForTemplateLoading(getClass(), "/templates");
        freemarkerConfig.setDefaultEncoding("UTF-8");
    }

    public String renderTable(List<Map<String, Object>> source, 
                             List<ColumnDef> columns, 
                             Predicate<Map<String, Object>> criteria) throws Exception {
        List<Map<String, Object>> filteredData = source.stream()
            .filter(criteria)
            .map(row -> {
                Map<String, Object> projected = new HashMap<>();
                columns.forEach(col -> projected.put(col.getField(), row.get(col.getField())));
                return projected;
            })
            .collect(Collectors.toList());

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("filteredData", filteredData);
        dataModel.put("columns", columns.stream().map(ColumnDef::toMap).collect(Collectors.toList()));

        Template template = freemarkerConfig.getTemplate("table.ftl");
        StringWriter writer = new StringWriter();
        template.process(dataModel, writer);
        return writer.toString();
    }
}

@Data
public class ColumnDef {
    private String field;
    private String label;
    public Map<String, Object> toMap() {
        return Map.of("field", field, "label", label);
    }
}