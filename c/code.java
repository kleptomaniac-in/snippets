import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Column definition for table rendering
 */
class ColumnDefinition {
    private String fieldName;
    private String label;
    
    public ColumnDefinition(String fieldName, String label) {
        this.fieldName = fieldName;
        this.label = label;
    }
    
    public String getFieldName() { return fieldName; }
    public String getLabel() { return label; }
}

/**
 * Table data model for FreeMarker template rendering
 */
class TableData {
    private List<ColumnDefinition> columns;
    private List<Map<String, Object>> rows;
    
    public TableData(List<ColumnDefinition> columns, List<Map<String, Object>> rows) {
        this.columns = columns;
        this.rows = rows;
    }
    
    public List<ColumnDefinition> getColumns() { return columns; }
    public List<Map<String, Object>> getRows() { return rows; }
}

/**
 * Data source service interface
 */
interface DataSourceService {
    List<Map<String, Object>> fetchData();
}

/**
 * YAML Configuration Classes
 */
@ConfigurationProperties(prefix = "table-configs")
class TableConfigProperties {
    private Map<String, TableConfig> tables = new HashMap<>();
    
    public Map<String, TableConfig> getTables() { return tables; }
    public void setTables(Map<String, TableConfig> tables) { this.tables = tables; }
}

class TableConfig {
    private DataSourceConfig dataSource;
    private List<ColumnConfig> columns;
    private FilterConfig filter;
    
    public DataSourceConfig getDataSource() { return dataSource; }
    public void setDataSource(DataSourceConfig dataSource) { this.dataSource = dataSource; }
    
    public List<ColumnConfig> getColumns() { return columns; }
    public void setColumns(List<ColumnConfig> columns) { this.columns = columns; }
    
    public FilterConfig getFilter() { return filter; }
    public void setFilter(FilterConfig filter) { this.filter = filter; }
}

class DataSourceConfig {
    private String beanName;
    private String methodName;
    
    public String getBeanName() { return beanName; }
    public void setBeanName(String beanName) { this.beanName = beanName; }
    
    public String getMethodName() { return methodName; }
    public void setMethodName(String methodName) { this.methodName = methodName; }
}

class ColumnConfig {
    private String fieldName;
    private String label;
    
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
}

class FilterConfig {
    private String type;
    private String field;
    private Object value;
    private Object minValue;
    private Object maxValue;
    private List<Object> values;
    private List<FilterConfig> conditions;
    private String customBean;
    private String customMethod;
    private Boolean caseInsensitive;
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getField() { return field; }
    public void setField(String field) { this.field = field; }
    
    public Object getValue() { return value; }
    public void setValue(Object value) { this.value = value; }
    
    public Object getMinValue() { return minValue; }
    public void setMinValue(Object minValue) { this.minValue = minValue; }
    
    public Object getMaxValue() { return maxValue; }
    public void setMaxValue(Object maxValue) { this.maxValue = maxValue; }
    
    public List<Object> getValues() { return values; }
    public void setValues(List<Object> values) { this.values = values; }
    
    public List<FilterConfig> getConditions() { return conditions; }
    public void setConditions(List<FilterConfig> conditions) { this.conditions = conditions; }
    
    public String getCustomBean() { return customBean; }
    public void setCustomBean(String customBean) { this.customBean = customBean; }
    
    public String getCustomMethod() { return customMethod; }
    public void setCustomMethod(String customMethod) { this.customMethod = customMethod; }
    
    public Boolean getCaseInsensitive() { return caseInsensitive; }
    public void setCaseInsensitive(Boolean caseInsensitive) { this.caseInsensitive = caseInsensitive; }
}

/**
 * Service to build predicates from filter configuration
 */
@Service
class FilterBuilder {
    private final ApplicationContext context;
    
    public FilterBuilder(ApplicationContext context) {
        this.context = context;
    }
    
    public Predicate<Map<String, Object>> buildPredicate(FilterConfig config) {
        if (config == null) {
            return record -> true;
        }
        
        String type = config.getType() != null ? config.getType().toLowerCase() : "none";
        
        switch (type) {
            case "equals":
                return buildEqualsPredicate(config);
            case "not-equals":
                return buildNotEqualsPredicate(config);
            case "range":
                return buildRangePredicate(config);
            case "in":
                return buildInPredicate(config);
            case "not-in":
                return buildNotInPredicate(config);
            case "contains":
                return buildContainsPredicate(config);
            case "starts-with":
                return buildStartsWithPredicate(config);
            case "ends-with":
                return buildEndsWithPredicate(config);
            case "is-null":
                return buildIsNullPredicate(config);
            case "is-not-null":
                return buildIsNotNullPredicate(config);
            case "greater-than":
                return buildGreaterThanPredicate(config);
            case "less-than":
                return buildLessThanPredicate(config);
            case "and":
                return buildAndPredicate(config);
            case "or":
                return buildOrPredicate(config);
            case "not":
                return buildNotPredicate(config);
            case "custom":
                return buildCustomPredicate(config);
            default:
                return record -> true;
        }
    }
    
    private Predicate<Map<String, Object>> buildEqualsPredicate(FilterConfig config) {
        return record -> {
            Object fieldValue = record.get(config.getField());
            if (fieldValue == null) return config.getValue() == null;
            
            if (isCaseInsensitive(config) && fieldValue instanceof String) {
                return fieldValue.toString().equalsIgnoreCase(config.getValue().toString());
            }
            return fieldValue.equals(config.getValue());
        };
    }
    
    private Predicate<Map<String, Object>> buildNotEqualsPredicate(FilterConfig config) {
        return buildEqualsPredicate(config).negate();
    }
    
    private Predicate<Map<String, Object>> buildRangePredicate(FilterConfig config) {
        return record -> {
            Object fieldValue = record.get(config.getField());
            if (fieldValue == null) return false;
            
            if (fieldValue instanceof Comparable) {
                @SuppressWarnings("unchecked")
                Comparable<Object> comp = (Comparable<Object>) fieldValue;
                
                boolean aboveMin = config.getMinValue() == null || 
                                  comp.compareTo(config.getMinValue()) >= 0;
                boolean belowMax = config.getMaxValue() == null || 
                                  comp.compareTo(config.getMaxValue()) <= 0;
                return aboveMin && belowMax;
            }
            return false;
        };
    }
    
    private Predicate<Map<String, Object>> buildInPredicate(FilterConfig config) {
        return record -> {
            Object fieldValue = record.get(config.getField());
            if (config.getValues() == null) return false;
            
            if (isCaseInsensitive(config) && fieldValue instanceof String) {
                String strValue = fieldValue.toString().toLowerCase();
                return config.getValues().stream()
                    .anyMatch(v -> v != null && v.toString().toLowerCase().equals(strValue));
            }
            return config.getValues().contains(fieldValue);
        };
    }
    
    private Predicate<Map<String, Object>> buildNotInPredicate(FilterConfig config) {
        return buildInPredicate(config).negate();
    }
    
    private Predicate<Map<String, Object>> buildContainsPredicate(FilterConfig config) {
        return record -> {
            Object fieldValue = record.get(config.getField());
            if (fieldValue == null || config.getValue() == null) return false;
            
            String strValue = fieldValue.toString();
            String searchValue = config.getValue().toString();
            
            if (isCaseInsensitive(config)) {
                return strValue.toLowerCase().contains(searchValue.toLowerCase());
            }
            return strValue.contains(searchValue);
        };
    }
    
    private Predicate<Map<String, Object>> buildStartsWithPredicate(FilterConfig config) {
        return record -> {
            Object fieldValue = record.get(config.getField());
            if (fieldValue == null || config.getValue() == null) return false;
            
            String strValue = fieldValue.toString();
            String searchValue = config.getValue().toString();
            
            if (isCaseInsensitive(config)) {
                return strValue.toLowerCase().startsWith(searchValue.toLowerCase());
            }
            return strValue.startsWith(searchValue);
        };
    }
    
    private Predicate<Map<String, Object>> buildEndsWithPredicate(FilterConfig config) {
        return record -> {
            Object fieldValue = record.get(config.getField());
            if (fieldValue == null || config.getValue() == null) return false;
            
            String strValue = fieldValue.toString();
            String searchValue = config.getValue().toString();
            
            if (isCaseInsensitive(config)) {
                return strValue.toLowerCase().endsWith(searchValue.toLowerCase());
            }
            return strValue.endsWith(searchValue);
        };
    }
    
    private Predicate<Map<String, Object>> buildIsNullPredicate(FilterConfig config) {
        return record -> record.get(config.getField()) == null;
    }
    
    private Predicate<Map<String, Object>> buildIsNotNullPredicate(FilterConfig config) {
        return record -> record.get(config.getField()) != null;
    }
    
    private Predicate<Map<String, Object>> buildGreaterThanPredicate(FilterConfig config) {
        return record -> {
            Object fieldValue = record.get(config.getField());
            if (fieldValue == null || config.getValue() == null) return false;
            
            if (fieldValue instanceof Comparable) {
                @SuppressWarnings("unchecked")
                Comparable<Object> comp = (Comparable<Object>) fieldValue;
                return comp.compareTo(config.getValue()) > 0;
            }
            return false;
        };
    }
    
    private Predicate<Map<String, Object>> buildLessThanPredicate(FilterConfig config) {
        return record -> {
            Object fieldValue = record.get(config.getField());
            if (fieldValue == null || config.getValue() == null) return false;
            
            if (fieldValue instanceof Comparable) {
                @SuppressWarnings("unchecked")
                Comparable<Object> comp = (Comparable<Object>) fieldValue;
                return comp.compareTo(config.getValue()) < 0;
            }
            return false;
        };
    }
    
    private Predicate<Map<String, Object>> buildAndPredicate(FilterConfig config) {
        if (config.getConditions() == null || config.getConditions().isEmpty()) {
            return record -> true;
        }
        
        List<Predicate<Map<String, Object>>> predicates = config.getConditions()
            .stream()
            .map(this::buildPredicate)
            .collect(Collectors.toList());
        
        return record -> predicates.stream().allMatch(p -> p.test(record));
    }
    
    private Predicate<Map<String, Object>> buildOrPredicate(FilterConfig config) {
        if (config.getConditions() == null || config.getConditions().isEmpty()) {
            return record -> false;
        }
        
        List<Predicate<Map<String, Object>>> predicates = config.getConditions()
            .stream()
            .map(this::buildPredicate)
            .collect(Collectors.toList());
        
        return record -> predicates.stream().anyMatch(p -> p.test(record));
    }
    
    private Predicate<Map<String, Object>> buildNotPredicate(FilterConfig config) {
        if (config.getConditions() == null || config.getConditions().isEmpty()) {
            return record -> true;
        }
        
        Predicate<Map<String, Object>> innerPredicate = buildPredicate(config.getConditions().get(0));
        return innerPredicate.negate();
    }
    
    private Predicate<Map<String, Object>> buildCustomPredicate(FilterConfig config) {
        try {
            Object bean = context.getBean(config.getCustomBean());
            java.lang.reflect.Method method = bean.getClass()
                .getMethod(config.getCustomMethod());
            
            @SuppressWarnings("unchecked")
            Predicate<Map<String, Object>> predicate = 
                (Predicate<Map<String, Object>>) method.invoke(bean);
            return predicate;
        } catch (Exception e) {
            throw new RuntimeException("Failed to build custom predicate: " + 
                config.getCustomBean() + "." + config.getCustomMethod(), e);
        }
    }
    
    private boolean isCaseInsensitive(FilterConfig config) {
        return config.getCaseInsensitive() != null && config.getCaseInsensitive();
    }
}

/**
 * Complete Generic Table Data Component with Spring Integration
 */
@Component
public class GenericTableDataComponent {
    private final ApplicationContext context;
    private final FilterBuilder filterBuilder;
    private final TableConfigProperties configProperties;
    
    public GenericTableDataComponent(
            ApplicationContext context,
            FilterBuilder filterBuilder,
            TableConfigProperties configProperties) {
        this.context = context;
        this.filterBuilder = filterBuilder;
        this.configProperties = configProperties;
    }
    
    /**
     * Process table by configuration name from YAML
     * 
     * @param configName The name of the table configuration in YAML
     * @return TableData ready for FreeMarker rendering
     */
    public TableData processTableByConfig(String configName) {
        TableConfig config = configProperties.getTables().get(configName);
        if (config == null) {
            throw new IllegalArgumentException("Table config not found: " + configName);
        }
        
        DataSourceService dataSource = buildDataSourceService(config.getDataSource());
        
        List<ColumnDefinition> columns = config.getColumns().stream()
            .map(c -> new ColumnDefinition(c.getFieldName(), c.getLabel()))
            .collect(Collectors.toList());
        
        Predicate<Map<String, Object>> predicate = filterBuilder.buildPredicate(config.getFilter());
        
        return processData(dataSource, predicate, columns);
    }
    
    /**
     * Get list of all available table configuration names
     */
    public Set<String> getAvailableConfigs() {
        return configProperties.getTables().keySet();
    }
    
    /**
     * Check if a configuration exists
     */
    public boolean configExists(String configName) {
        return configProperties.getTables().containsKey(configName);
    }
    
    private DataSourceService buildDataSourceService(DataSourceConfig config) {
        return () -> {
            try {
                Object bean = context.getBean(config.getBeanName());
                java.lang.reflect.Method method = bean.getClass()
                    .getMethod(config.getMethodName());
                
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> result = 
                    (List<Map<String, Object>>) method.invoke(bean);
                return result != null ? result : Collections.emptyList();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Method not found: " + config.getMethodName() + 
                    " on bean: " + config.getBeanName(), e);
            } catch (Exception e) {
                throw new RuntimeException("Failed to fetch data from " + 
                    config.getBeanName() + "." + config.getMethodName(), e);
            }
        };
    }
    
    private TableData processData(
            DataSourceService dataSourceService,
            Predicate<Map<String, Object>> criteria,
            List<ColumnDefinition> columnDefinitions) {
        
        List<Map<String, Object>> dataSource = dataSourceService.fetchData();
        
        List<Map<String, Object>> filteredData = dataSource.stream()
                .filter(criteria)
                .collect(Collectors.toList());
        
        List<Map<String, Object>> processedRows = filteredData.stream()
                .map(record -> extractColumns(record, columnDefinitions))
                .collect(Collectors.toList());
        
        return new TableData(columnDefinitions, processedRows);
    }
    
    private Map<String, Object> extractColumns(
            Map<String, Object> record, 
            List<ColumnDefinition> columnDefinitions) {
        
        Map<String, Object> extractedData = new LinkedHashMap<>();
        
        for (ColumnDefinition colDef : columnDefinitions) {
            Object value = record.get(colDef.getFieldName());
            extractedData.put(colDef.getFieldName(), value != null ? value : "");
        }
        
        return extractedData;
    }
}