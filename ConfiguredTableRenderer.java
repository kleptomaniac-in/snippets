@Service
public class ConfiguredTableRenderer {
    private final TableRenderer renderer;
    private final TableRenderConfig config;
    private final ApplicationContext context;  // For dynamic bean invocation

    public String renderTable(String tableKey) throws Exception {
        TableSpec spec = config.getSpecs().get(tableKey);
        List<Map<String, Object>> source = fetchData(spec.getDataSource(), context);
        Predicate<Map<String, Object>> predicate = buildCriteria(spec.getCriteria());
        return renderer.renderTable(source, spec.getColumns(), predicate);
    }
    
    private List<Map<String, Object>> fetchData(String dataSourceExpr, ApplicationContext ctx) {
        // Parse "service.method()" -> ctx.getBean(service).method()
        // Implementation using ScriptEngine or reflection
        return (List<Map<String, Object>>) evaluateExpression(dataSourceExpr, ctx);
    }
    
    private Predicate<Map<String, Object>> buildCriteria(Map<String, Object> criteria) {
        return row -> criteria.entrySet().stream()
            .allMatch(entry -> Objects.equals(row.get(entry.getKey()), entry.getValue()));
    }
}