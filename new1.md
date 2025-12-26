Great question! To make this component configurable via YAML in Spring Boot, you'll need to create a configuration structure that can be parsed from YAML and then converted into the runtime objects. Let me create a complete solution for you.Perfect! Now let me create the YAML configuration examples and usage guide:

**application.yml Configuration:**

```yaml
table-configs:
  tables:
    # Example 1: Simple employee table with equality filter
    employee-engineering:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Employee Name
        - fieldName: department
          label: Department
        - fieldName: salary
          label: Salary
      filter:
        type: equals
        field: department
        value: Engineering

    # Example 2: Salary range filter
    high-earners:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Name
        - fieldName: salary
          label: Annual Salary
      filter:
        type: range
        field: salary
        minValue: 70000
        maxValue: 100000

    # Example 3: Multiple departments using "in" filter
    tech-sales-employees:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Employee Name
        - fieldName: department
          label: Department
      filter:
        type: in
        field: department
        values:
          - Engineering
          - Sales
          - IT

    # Example 4: Complex AND filter
    senior-high-earners:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Name
        - fieldName: age
          label: Age
        - fieldName: salary
          label: Salary
      filter:
        type: and
        conditions:
          - type: range
            field: age
            minValue: 30
          - type: range
            field: salary
            minValue: 70000

    # Example 5: Complex OR filter
    tech-or-high-salary:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Name
        - fieldName: department
          label: Department
        - fieldName: salary
          label: Salary
      filter:
        type: or
        conditions:
          - type: equals
            field: department
            value: Engineering
          - type: range
            field: salary
            minValue: 80000

    # Example 6: String contains filter
    smith-employees:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Employee Name
        - fieldName: department
          label: Department
      filter:
        type: contains
        field: name
        value: Smith

    # Example 7: Custom filter from a bean
    custom-senior-filter:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Name
        - fieldName: age
          label: Age
        - fieldName: salary
          label: Salary
      filter:
        type: custom
        customBean: customFilters
        customMethod: getSeniorEmployeeFilter

    # Example 8: No filter (all data)
    all-employees:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Employee Name
        - fieldName: department
          label: Department
        - fieldName: age
          label: Age
        - fieldName: salary
          label: Salary
```

**Spring Boot Configuration Class:**

```java
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TableConfigProperties.class)
public class TableConfiguration {
    // Configuration is automatically loaded from application.yml
}
```

**Usage in Your Controller or Service:**

```java
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ReportController {
    
    private final GenericTableDataComponent tableComponent;
    
    public ReportController(GenericTableDataComponent tableComponent) {
        this.tableComponent = tableComponent;
    }
    
    @GetMapping("/report/{configName}")
    public String generateReport(@PathVariable String configName, Model model) {
        // Process table using YAML config name
        TableData tableData = tableComponent.processTableByConfig(configName);
        
        // Add to model for FreeMarker template
        model.addAttribute("tableData", tableData);
        
        return "report-template";  // FreeMarker template name
    }
}
```

**FreeMarker Template (report-template.ftl):**

```ftl
<!DOCTYPE html>
<html>
<head>
    <title>Report</title>
    <style>
        table { border-collapse: collapse; width: 100%; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
        th { background-color: #4CAF50; color: white; }
    </style>
</head>
<body>
    <h1>Report</h1>
    <table>
        <thead>
            <tr>
                <#list tableData.columns as column>
                    <th>${column.label}</th>
                </#list>
            </tr>
        </thead>
        <tbody>
            <#list tableData.rows as row>
                <tr>
                    <#list tableData.columns as column>
                        <td>${row[column.fieldName]!""}</td>
                    </#list>
                </tr>
            </#list>
        </tbody>
    </table>
</body>
</html>
```

**Key Benefits:**

1. **Reusable**: Define multiple table configurations in YAML
2. **No Code Changes**: Add new reports by just adding YAML config
3. **Flexible Filters**: Support for equals, range, in, contains, and, or, and custom filters
4. **Dynamic Data Sources**: Call any Spring bean method as data source
5. **Type-Safe**: All configurations are type-checked at startup

**Filter Types Summary:**

- `equals`: Field equals a specific value
- `range`: Field within min/max range
- `in`: Field value in a list
- `contains`: String field contains substring
- `and`: All conditions must be true
- `or`: At least one condition must be true
- `custom`: Custom predicate from a Spring bean method

Would you like me to add more filter types or explain any specific part?