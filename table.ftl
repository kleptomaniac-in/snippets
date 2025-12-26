<table border="1" class="data-table">
  <thead>
    <tr>
      <#list columns as col>
        <th>${col.label}</th>
      </#list>
    </tr>
  </thead>
  <tbody>
    <#list filteredData as row>
      <tr>
        <#list columns as col>
          <td>${row[col.field]?string}</td>
        </#list>
      </tr>
    </#list>
  </tbody>
</table>