why do we need wrapUnHtmlDocument method can't we just add those to freemarker template directly

You **can** embed the full HTML document directly in FreeMarker templates, but the `wrapInHtmlDocument` method provides critical separation of concerns and PDF rendering reliability.

## Why Separate Wrapping is Better

**PDF Generation Requires Complete HTML Documents**
OpenHTMLToPDF expects a **full XHTML document** with `<!DOCTYPE>`, `<html>`, `<head>`, and `<body>` tags. Fragment-only HTML often fails or renders poorly:

```html
<!-- BAD: Table fragment only - PDF fails -->
<table>...</table>

<!-- GOOD: Complete document -->
<!DOCTYPE html><html><head><title>Report</title></head><body><table>...</table></body></html>
```

## Template vs Wrapper Benefits

| Approach | Pros | Cons |
|----------|------|------|
| **Full in Template** | Single file | PDF-specific, not reusable for HTML |
| **Wrapper Method** | Reusable table HTML, PDF-ready wrapper | Extra method |

## Better Template Design (Recommended)
Keep template **table-only** for maximum reusability:

**vertical-table.ftl** (just table):
```html
<table class="vertical-table">
  <#list data as row>
    <tr>
      <#items row as cell>
        <td>${cell}</td>
      </#items>
    </tr>
  </#list>
</table>
```

**Service handles document structure**:
```java
public String renderVerticalTable(List<List<String>> data) throws Exception {
    String tableHtml = processTemplate("vertical-table.ftl", data);
    return wrapInHtmlDocument(tableHtml);  // Adds PDF-ready structure
}

public byte[] renderVerticalTableAsPdf(List<List<String>> data) throws Exception {
    String tableHtml = processTemplate("vertical-table.ftl", data);
    String fullHtml = wrapInHtmlDocument(tableHtml);
    return pdfService.convertHtmlToPdf(fullHtml);
}
```

## Reusability Wins
- **Same template** → HTML email, web page, **or** PDF
- **Single template source** → multiple output formats
- **PDF-specific CSS/headers** isolated in wrapper
- **YAML configs** reference clean table templates only

The wrapper makes your component **truly reusable** across HTML, email, and PDF use cases without template duplication.[1]

Citations:
[1] FreeMarker Java Template Engine https://freemarker.apache.org/index.html
[2] Freemarker Java template engine - Key features and use ... https://messagegears.com/resources/blog/freemarker-java-template-engine/
[3] FreeMarker Manual - FAQ https://freemarker.sourceforge.io/docs/app_faq.html
[4] Freemarker Template in Liferay https://www.aspiresoftserv.com/blog/freemarker-template-in-liferay
[5] Freemarker best practices - Squiz Help Center https://docs.squiz.net/funnelback/docs/latest/build/results-pages/search-results-html/freemarker-best-practices.html
[6] The template at a glance - Apache FreeMarker Manual https://freemarker.apache.org/docs/dgui_quickstart_template.html
[7] 2.3.22 - Apache FreeMarker Manual https://freemarker.apache.org/docs/versions_2_3_22.html
[8] FreeMarker :: Spring Framework https://docs.spring.io/spring-framework/reference/web/webmvc-view/mvc-freemarker.html
[9] HTML and FreeMarker for Scriptable Templates https://docs.oracle.com/en/cloud/saas/netsuite/ns-online-help/section_1556902584.html
[10] Freemarker: access public field with no getter in template https://stackoverflow.com/questions/56640056/freemarker-access-public-field-with-no-getter-in-template
