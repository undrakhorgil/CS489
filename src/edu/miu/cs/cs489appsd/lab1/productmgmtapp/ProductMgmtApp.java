package edu.miu.cs.cs489appsd.lab1.productmgmtapp;

import edu.miu.cs.cs489appsd.lab1.productmgmtapp.model.Product;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;

public class ProductMgmtApp {

    public static void main(String[] args) {
        Product[] products = new Product[] {
                new Product(new BigInteger("31288741190182539912"), "Banana", LocalDate.parse("2026-01-24"), 124, new BigDecimal("0.55")),
                new Product(new BigInteger("29274582650152771644"), "Apple", LocalDate.parse("2025-12-09"), 18, new BigDecimal("1.09")),
                new Product(new BigInteger("91899274600128155167"), "Carrot", LocalDate.parse("2026-03-31"), 89, new BigDecimal("2.99")),
                new Product(new BigInteger("31288741190182539913"), "Banana", LocalDate.parse("2026-02-13"), 240, new BigDecimal("0.65"))
        };

        printProducts(products);
    }

    public static void printProducts(Product[] products) {
        Product[] sorted = Arrays.copyOf(products, products.length);
        Arrays.sort(sorted,
                Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Product::getUnitPrice, Comparator.nullsLast(Comparator.reverseOrder()))
        );

        System.out.println("JSON-formatted list of all Products:");
        System.out.println(toJson(sorted));
        System.out.println();

        System.out.println("XML-formatted list of all Products:");
        System.out.println(toXml(sorted));
        System.out.println();

        System.out.println("CSV-formatted list of all Products:");
        System.out.println(toCsv(sorted));
    }

    private static String toJson(Product[] products) {
        StringBuilder sb = new StringBuilder();
        sb.append("[\n");
        for (int i = 0; i < products.length; i++) {
            Product p = products[i];
            sb.append("  {\n");
            sb.append("    \"productId\": ").append(p.getProductId()).append(",\n");
            sb.append("    \"name\": ").append(jsonString(p.getName())).append(",\n");
            sb.append("    \"dateSupplied\": ").append(jsonString(p.getDateSupplied() != null ? p.getDateSupplied().toString() : null)).append(",\n");
            sb.append("    \"quantityInStock\": ").append(p.getQuantityInStock()).append(",\n");
            sb.append("    \"unitPrice\": ").append(p.getUnitPrice() != null ? p.getUnitPrice().toPlainString() : "null").append("\n");
            sb.append("  }");
            if (i < products.length - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    private static String toXml(Product[] products) {
        StringBuilder sb = new StringBuilder();
        sb.append("<Products>\n");
        for (Product p : products) {
            sb.append("  <Product>\n");
            sb.append("    <productId>").append(escapeXml(p.getProductId() != null ? p.getProductId().toString() : "")).append("</productId>\n");
            sb.append("    <name>").append(escapeXml(p.getName() != null ? p.getName() : "")).append("</name>\n");
            sb.append("    <dateSupplied>").append(escapeXml(p.getDateSupplied() != null ? p.getDateSupplied().toString() : "")).append("</dateSupplied>\n");
            sb.append("    <quantityInStock>").append(p.getQuantityInStock()).append("</quantityInStock>\n");
            sb.append("    <unitPrice>").append(escapeXml(p.getUnitPrice() != null ? p.getUnitPrice().toPlainString() : "")).append("</unitPrice>\n");
            sb.append("  </Product>\n");
        }
        sb.append("</Products>");
        return sb.toString();
    }

    private static String toCsv(Product[] products) {
        StringBuilder sb = new StringBuilder();
        sb.append("productId,name,dateSupplied,quantityInStock,unitPrice\n");
        for (Product p : products) {
            sb.append(csvField(p.getProductId() != null ? p.getProductId().toString() : ""));
            sb.append(",");
            sb.append(csvField(p.getName() != null ? p.getName() : ""));
            sb.append(",");
            sb.append(csvField(p.getDateSupplied() != null ? p.getDateSupplied().toString() : ""));
            sb.append(",");
            sb.append(p.getQuantityInStock());
            sb.append(",");
            sb.append(p.getUnitPrice() != null ? p.getUnitPrice().toPlainString() : "");
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String jsonString(String s) {
        if (s == null) return "null";
        String escaped = s
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
        return "\"" + escaped + "\"";
    }

    private static String escapeXml(String s) {
        return s
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static String csvField(String s) {
        boolean needsQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        if (!needsQuotes) return s;
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }
}
