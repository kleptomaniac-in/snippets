if we have data in json format with nested objects and arrays could we have them converted to Map and apply filter criteria against it.

Yes, you can easily convert nested JSON (objects and arrays) to `List<Map<String, Object>>` and apply the existing filter criteria against flattened or nested paths.

## JSON to Map Conversion Service
```java
@Service
public class JsonToMapConverter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Map<String, Object>> convertJsonToMaps(String jsonString) throws JsonProcessingException {
        // Handles both JSON array and single object wrapped in array
        if (jsonString.trim().startsWith("[")) {
            return objectMapper.readValue(jsonString, 
                new TypeReference<List<Map<String, Object>>>() {});
        } else {
            Map<String, Object> single = objectMapper.readValue(jsonString, 
                new TypeReference<Map<String, Object>>(){});
            return List.of(single);
        }
    }
    
    public List<Map<String, Object>> convertJsonFile(String filePath) throws IOException {
        String json = Files.readString(Path.of(filePath));
        return convertJsonToMaps(json);
    }
}
```

## Nested Path Filtering
Extend `Predicate` to handle dot-notation paths like `address.city` or `tags[0]`:

```java
public Predicate<Map<String, Object>> buildNestedCriteria(Map<String, Object> criteria) {
    return row -> criteria.entrySet().stream().allMatch(entry -> {
        String key = entry.getKey();
        Object expectedValue = entry.getValue();
        
        if (key.contains(".")) {
            // Handle nested paths: "department.name" -> row.get("department").get("name")
            return Objects.equals(getNestedValue(row, key), expectedValue);
        } else if (key.contains("[")) {
            // Handle array access: "tags[0]" -> ((List)row.get("tags")).get(0)
            return Objects.equals(getArrayValue(row, key), expectedValue);
        }
        
        return Objects.equals(row.get(key), expectedValue);
    });
}

private Object getNestedValue(Map<String, Object> row, String path) {
    String[] parts = path.split("\\.");
    Object current = row;
    for (String part : parts) {
        if (current instanceof Map) {
            current = ((Map<?, ?>) current).get(part);
        } else {
            return null;
        }
    }
    return current;
}

private Object getArrayValue(Map<String, Object> row, String path) {
    Pattern arrayPattern = Pattern.compile("(.+)\\[(\\d+)\\]");
    Matcher matcher = arrayPattern.matcher(path);
    if (matcher.matches()) {
        String basePath = matcher.group(1);
        int index = Integer.parseInt(matcher.group(2));
        Object array = getNestedValue(row, basePath);
        if (array instanceof List && index < ((List<?>) array).size()) {
            return ((List<?>) array).get(index);
        }
    }
    return null;
}
```

## Updated YAML Config Example
```yaml
table-renders:
  employee-report:
    data-source: |
      [
        {
          "id": 1,
          "name": "Ravi",
          "department": {"name": "IT", "location": "Chennai"},
          "skills": ["Java", "Spring Boot", "YAML"],
          "salary": 75000
        },
        {
          "id": 2,
          "name": "Priya",
          "department": {"name": "HR", "location": "Bangalore"},
          "skills": ["Recruitment"],
          "salary": 65000
        }
      ]
    columns:
      - field: name
        label: "Employee"
      - field: "department.name"
        label: "Department"
      - field: salary
        label: "Salary"
    criteria:
      "department.name": "IT"
      "skills[0]": "Java"
      salary: { ">": 70000 }
```

## Integrated Usage
```java
@Service
public class ConfiguredTableRenderer {
    private final JsonToMapConverter jsonConverter;
    private final TableRenderer tableRenderer;

    public String renderTable(String tableKey) throws Exception {
        TableSpec spec = config.getSpecs().get(tableKey);
        
        // Convert JSON data-source to List<Map>
        List<Map<String, Object>> source = jsonConverter.convertJsonToMaps(spec.getDataSource());
        
        // Apply nested filtering
        Predicate<Map<String, Object>> criteria = buildNestedCriteria(spec.getCriteria());
        List<Map<String, Object>> filtered = source.stream()
            .filter(criteria)
            .collect(Collectors.toList());
        
        return tableRenderer.renderTable(filtered, spec.getColumns(), p -> true);
    }
}
```

## Jackson Dependency
```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

This handles **any** JSON structure - nested objects become nested Maps, arrays stay as Lists, and your existing `Predicate` filtering works with dot-notation paths seamlessly. Perfect for dynamic API responses or config-driven JSON processing.

Citations:
[1] Convert a List of JSON-Objects Input into a Nested Map in Java https://www.youtube.com/watch?v=pN8ee4e0xDM
[2] Nested Json to Map using Jackson - java https://stackoverflow.com/questions/19840818/nested-json-to-map-using-jackson
[3] 35. Create Nested JSON Object payload using Java Map https://www.youtube.com/watch?v=3USL0lolTtU
[4] Converting Complex JSON with Nested Objects and Lists into ... https://www.shariqsp.com/restAssured/pojo.html
[5] Mapping Nested Values with Jackson https://www.baeldung.com/jackson-nested-values
[6] nested JSON converting to java map problem https://groups.google.com/g/rest-assured/c/Eo5YYHYrNaM
[7] Converting JsonNode Object to Map https://www.baeldung.com/jackson-jsonnode-map
[8] Any way to convert unknown/dynamic json to generic ... https://www.reddit.com/r/golang/comments/11glk8w/any_way_to_convert_unknowndynamic_json_to_generic/
[9] 54. Convert Nested JSON Object Response To POJO ... https://www.youtube.com/watch?v=PwnVycLVkI0
