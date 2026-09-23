package com.dashamail.sdk.internal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal, dependency-free JSON parser and serializer.
 *
 * <p>Java's standard library ships no JSON support, and the goal of this SDK
 * is zero third-party dependencies (no Jackson/Gson/org.json), so this class
 * exists purely to read and write the DashaMail API's request/response
 * bodies. It maps JSON to plain {@link Map}/{@link List}/{@link String}/
 * {@link Number}/{@link Boolean}/{@code null}, with no custom types.</p>
 */
public final class Json {

    private Json() {
    }

    /** Parses a JSON document into Map/List/String/Number/Boolean/null. */
    public static Object parse(String text) {
        Parser parser = new Parser(text);
        parser.skipWhitespace();
        Object value = parser.parseValue();
        parser.skipWhitespace();
        if (!parser.isAtEnd()) {
            throw new JsonException("Unexpected trailing content at position " + parser.pos);
        }
        return value;
    }

    /** Serializes a Map/List/String/Number/Boolean/null tree to JSON text. */
    public static String stringify(Object value) {
        StringBuilder sb = new StringBuilder();
        writeValue(sb, value);
        return sb.toString();
    }

    private static void writeValue(StringBuilder sb, Object value) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof String) {
            writeString(sb, (String) value);
        } else if (value instanceof Boolean) {
            sb.append(((Boolean) value) ? "true" : "false");
        } else if (value instanceof Number) {
            sb.append(formatNumber((Number) value));
        } else if (value instanceof Map<?, ?>) {
            sb.append('{');
            boolean first = true;
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) value).entrySet()) {
                if (!first) {
                    sb.append(',');
                }
                first = false;
                writeString(sb, String.valueOf(entry.getKey()));
                sb.append(':');
                writeValue(sb, entry.getValue());
            }
            sb.append('}');
        } else if (value instanceof Iterable<?>) {
            sb.append('[');
            boolean first = true;
            for (Object item : (Iterable<?>) value) {
                if (!first) {
                    sb.append(',');
                }
                first = false;
                writeValue(sb, item);
            }
            sb.append(']');
        } else if (value instanceof Object[]) {
            sb.append('[');
            Object[] arr = (Object[]) value;
            for (int i = 0; i < arr.length; i++) {
                if (i > 0) {
                    sb.append(',');
                }
                writeValue(sb, arr[i]);
            }
            sb.append(']');
        } else {
            writeString(sb, value.toString());
        }
    }

    private static String formatNumber(Number number) {
        if (number instanceof Double || number instanceof Float) {
            double d = number.doubleValue();
            if (d == Math.floor(d) && !Double.isInfinite(d)) {
                return Long.toString((long) d);
            }
            return String.valueOf(d);
        }
        return number.toString();
    }

    private static void writeString(StringBuilder sb, String s) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }

    /** Raised for malformed JSON input. */
    public static class JsonException extends RuntimeException {
        public JsonException(String message) {
            super(message);
        }
    }

    private static final class Parser {
        private final String text;
        private int pos;

        Parser(String text) {
            this.text = text;
            this.pos = 0;
        }

        boolean isAtEnd() {
            return pos >= text.length();
        }

        void skipWhitespace() {
            while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
                pos++;
            }
        }

        char peek() {
            if (pos >= text.length()) {
                throw new JsonException("Unexpected end of input");
            }
            return text.charAt(pos);
        }

        char next() {
            char c = peek();
            pos++;
            return c;
        }

        void expect(char expected) {
            char c = next();
            if (c != expected) {
                throw new JsonException("Expected '" + expected + "' but found '" + c + "' at position " + (pos - 1));
            }
        }

        Object parseValue() {
            skipWhitespace();
            char c = peek();
            switch (c) {
                case '{':
                    return parseObject();
                case '[':
                    return parseArray();
                case '"':
                    return parseString();
                case 't':
                    expectLiteral("true");
                    return Boolean.TRUE;
                case 'f':
                    expectLiteral("false");
                    return Boolean.FALSE;
                case 'n':
                    expectLiteral("null");
                    return null;
                default:
                    return parseNumber();
            }
        }

        void expectLiteral(String literal) {
            if (pos + literal.length() > text.length() || !text.regionMatches(pos, literal, 0, literal.length())) {
                throw new JsonException("Expected literal '" + literal + "' at position " + pos);
            }
            pos += literal.length();
        }

        Map<String, Object> parseObject() {
            Map<String, Object> map = new LinkedHashMap<>();
            expect('{');
            skipWhitespace();
            if (peek() == '}') {
                pos++;
                return map;
            }
            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                expect(':');
                Object value = parseValue();
                map.put(key, value);
                skipWhitespace();
                char c = next();
                if (c == ',') {
                    continue;
                } else if (c == '}') {
                    break;
                } else {
                    throw new JsonException("Expected ',' or '}' at position " + (pos - 1));
                }
            }
            return map;
        }

        List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            expect('[');
            skipWhitespace();
            if (peek() == ']') {
                pos++;
                return list;
            }
            while (true) {
                Object value = parseValue();
                list.add(value);
                skipWhitespace();
                char c = next();
                if (c == ',') {
                    continue;
                } else if (c == ']') {
                    break;
                } else {
                    throw new JsonException("Expected ',' or ']' at position " + (pos - 1));
                }
            }
            return list;
        }

        String parseString() {
            skipWhitespace();
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = next();
                if (c == '"') {
                    break;
                } else if (c == '\\') {
                    char escaped = next();
                    switch (escaped) {
                        case '"':
                            sb.append('"');
                            break;
                        case '\\':
                            sb.append('\\');
                            break;
                        case '/':
                            sb.append('/');
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'u':
                            String hex = text.substring(pos, pos + 4);
                            pos += 4;
                            sb.append((char) Integer.parseInt(hex, 16));
                            break;
                        default:
                            throw new JsonException("Invalid escape sequence '\\" + escaped + "' at position " + (pos - 1));
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        Object parseNumber() {
            int start = pos;
            if (peek() == '-') {
                pos++;
            }
            while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
                pos++;
            }
            boolean isDouble = false;
            if (pos < text.length() && text.charAt(pos) == '.') {
                isDouble = true;
                pos++;
                while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
                    pos++;
                }
            }
            if (pos < text.length() && (text.charAt(pos) == 'e' || text.charAt(pos) == 'E')) {
                isDouble = true;
                pos++;
                if (pos < text.length() && (text.charAt(pos) == '+' || text.charAt(pos) == '-')) {
                    pos++;
                }
                while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
                    pos++;
                }
            }
            String numberText = text.substring(start, pos);
            if (numberText.isEmpty() || "-".equals(numberText)) {
                throw new JsonException("Invalid number at position " + start);
            }
            if (isDouble) {
                return Double.parseDouble(numberText);
            }
            try {
                return Long.parseLong(numberText);
            } catch (NumberFormatException e) {
                return Double.parseDouble(numberText);
            }
        }
    }
}
