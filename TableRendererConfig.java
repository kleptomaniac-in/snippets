@ConfigurationProperties(prefix = "table-renders")
@Data
public class TableRenderConfig {
    private Map<String, TableSpec> specs = new HashMap<>();
    
    @Data
    public static class TableSpec {
        private String dataSource;
        private List<ColumnDef> columns = new ArrayList<>();
        private Map<String, Object> criteria;
        private String template = "table.ftl";
    }
}