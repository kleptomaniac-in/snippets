Perfect! Now let me show you comprehensive YAML examples for AND/OR conditions:

## Complete YAML Configuration Examples

```yaml
table-configs:
  tables:
    # ============================================
    # EXAMPLE 1: Simple AND condition
    # Senior engineers with high salary
    # ============================================
    senior-high-paid-engineers:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Employee Name
        - fieldName: age
          label: Age
        - fieldName: department
          label: Department
        - fieldName: salary
          label: Salary
      filter:
        type: and
        conditions:
          - type: equals
            field: department
            value: Engineering
          - type: greater-than
            field: age
            value: 30
          - type: range
            field: salary
            minValue: 75000
            maxValue: 150000

    # ============================================
    # EXAMPLE 2: Simple OR condition
    # Tech departments OR high earners
    # ============================================
    tech-or-high-earners:
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
          - type: in
            field: department
            values:
              - Engineering
              - IT
              - Data Science
          - type: greater-than
            field: salary
            value: 90000

    # ============================================
    # EXAMPLE 3: Nested AND inside OR
    # (Engineering AND Senior) OR (Sales AND High Performer)
    # ============================================
    senior-engineers-or-top-sales:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Name
        - fieldName: department
          label: Department
        - fieldName: age
          label: Age
        - fieldName: performanceScore
          label: Performance
      filter:
        type: or
        conditions:
          # First OR condition: Senior Engineers
          - type: and
            conditions:
              - type: equals
                field: department
                value: Engineering
              - type: greater-than
                field: age
                value: 30
          # Second OR condition: Top performing sales
          - type: and
            conditions:
              - type: equals
                field: department
                value: Sales
              - type: greater-than
                field: performanceScore
                value: 4.5

    # ============================================
    # EXAMPLE 4: Nested OR inside AND
    # High salary AND (Engineering OR Marketing)
    # ============================================
    high-paid-tech-or-marketing:
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
        type: and
        conditions:
          # Must have high salary
          - type: greater-than
            field: salary
            value: 80000
          # AND must be in Engineering OR Marketing
          - type: or
            conditions:
              - type: equals
                field: department
                value: Engineering
              - type: equals
                field: department
                value: Marketing

    # ============================================
    # EXAMPLE 5: Complex multi-level nesting
    # ((Engineering OR IT) AND Senior AND HighSalary) OR 
    # (Sales AND TopPerformer)
    # ============================================
    complex-employee-filter:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Name
        - fieldName: department
          label: Department
        - fieldName: age
          label: Age
        - fieldName: salary
          label: Salary
      filter:
        type: or
        conditions:
          # First complex condition
          - type: and
            conditions:
              # Tech departments
              - type: or
                conditions:
                  - type: equals
                    field: department
                    value: Engineering
                  - type: equals
                    field: department
                    value: IT
              # Senior age
              - type: greater-than
                field: age
                value: 35
              # High salary
              - type: greater-than
                field: salary
                value: 85000
          # OR top sales performers
          - type: and
            conditions:
              - type: equals
                field: department
                value: Sales
              - type: greater-than
                field: performanceScore
                value: 4.8

    # ============================================
    # EXAMPLE 6: NOT condition
    # All employees except Marketing department
    # ============================================
    non-marketing-employees:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Name
        - fieldName: department
          label: Department
      filter:
        type: not
        conditions:
          - type: equals
            field: department
            value: Marketing

    # ============================================
    # EXAMPLE 7: Complex NOT with AND
    # NOT (Junior AND Low Salary)
    # ============================================
    experienced-or-well-paid:
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
        type: not
        conditions:
          - type: and
            conditions:
              - type: less-than
                field: age
                value: 25
              - type: less-than
                field: salary
                value: 50000

    # ============================================
    # EXAMPLE 8: String filters with case insensitivity
    # ============================================
    employees-with-smith-or-doe:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Name
        - fieldName: department
          label: Department
      filter:
        type: or
        conditions:
          - type: contains
            field: name
            value: smith
            caseInsensitive: true
          - type: contains
            field: name
            value: doe
            caseInsensitive: true

    # ============================================
    # EXAMPLE 9: Combining multiple filter types
    # Active employees in specific departments with good performance
    # ============================================
    active-high-performers:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Name
        - fieldName: department
          label: Department
        - fieldName: status
          label: Status
        - fieldName: performanceScore
          label: Performance
      filter:
        type: and
        conditions:
          # Must be active
          - type: equals
            field: status
            value: Active
          # Must be in specific departments
          - type: in
            field: department
            values:
              - Engineering
              - Sales
              - Marketing
          # Must have good performance
          - type: greater-than
            field: performanceScore
            value: 3.5
          # Must not be on probation
          - type: not-equals
            field: employmentType
            value: Probation

    # ============================================
    # EXAMPLE 10: Date range with other conditions
    # Recent hires with specific qualifications
    # ============================================
    qualified-recent-hires:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Name
        - fieldName: hireDate
          label: Hire Date
        - fieldName: education
          label: Education
      filter:
        type: and
        conditions:
          # Hired in last year
          - type: range
            field: hireDateTimestamp
            minValue: 1672531200000  # Jan 1, 2023
          # Has advanced degree
          - type: or
            conditions:
              - type: equals
                field: education
                value: Masters
              - type: equals
                field: education
                value: PhD
          # Not null check on critical field
          - type: is-not-null
            field: backgroundCheckStatus

    # ============================================
    # EXAMPLE 11: All available filter types demonstration
    # ============================================
    all-filter-types-demo:
      dataSource:
        beanName: employeeDataService
        methodName: getAllEmployees
      columns:
        - fieldName: name
          label: Name
      filter:
        type: and
        conditions:
          - type: equals              # Exact match
            field: status
            value: Active
          - type: not-equals          # Not equal
            field: department
            value: Intern
          - type: range               # Min/Max range
            field: age
            minValue: 25
            maxValue: 55
          - type: in                  # In list
            field: location
            values: [NY, CA, TX]
          - type: not-in              # Not in list
            field: title
            values: [Contractor, Temp]
          - type: contains            # Substring
            field: skills
            value: Java
          - type: starts-with         # Prefix
            field: employeeId
            value: EMP
          - type: ends-with           # Suffix
            field: email
            value: "@company.com"
          - type: is-not-null         # Not null check
            field: manager
          - type: greater-than        # Greater than
            field: yearsExperience
            value: 3
          - type: less-than           # Less than
            field: absenceDays
            value: 10
```

## Additional Filter Type Reference

Here's a complete list of all supported filter types:

**Comparison Filters:**
- `equals` - Exact match
- `not-equals` - Not equal to
- `greater-than` - Greater than value
- `less-than` - Less than value
- `range` - Between minValue and maxValue (inclusive)

**Collection Filters:**
- `in` - Value is in the list
- `not-in` - Value is not in the list

**String Filters:**
- `contains` - Contains substring
- `starts-with` - Starts with prefix
- `ends-with` - Ends with suffix
- Add `caseInsensitive: true` for case-insensitive matching

**Null Checks:**
- `is-null` - Field is null
- `is-not-null` - Field is not null

**Logical Operators:**
- `and` - All conditions must be true
- `or` - At least one condition must be true
- `not` - Negates the inner condition

**Custom Filter:**
- `custom` - Calls a method on a Spring bean

This gives you complete flexibility to express any filtering logic in YAML!