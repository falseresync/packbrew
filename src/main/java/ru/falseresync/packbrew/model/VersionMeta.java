package ru.falseresync.packbrew.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record VersionMeta(
    String id,
    VersionType type,
    String time,
    String releaseTime,
    int minimumLauncherVersion,
    int complianceLevel,
    String mainClass,
    String assets,
    Downloadable assetIndex,
    Map<DownloadType, Downloadable> downloads,
    List<Library> libraries,
    Map<LoggingType, Logging> logging,
    Map<ArgumentsType, List<Argument>> arguments
) {
    public enum DownloadType {
        @JsonProperty("client") CLIENT,
        @JsonProperty("client_mappings") CLIENT_MAPPINGS,
        @JsonProperty("server") SERVER,
        @JsonProperty("server_mappings") SERVER_MAPPINGS
    }

    public enum LoggingType {
        @JsonProperty("client") CLIENT
    }

    public enum ArgumentsType {
        @JsonProperty("game") GAME,
        @JsonProperty("jvm") JVM
    }

    public record Downloadable(
        @JsonAlias("id") String path,
        String sha1,
        int size,
        int totalSize,
        URI url
    ) {
    }

    public record Library(
        String name,
        Downloads downloads,
        Map<String, List<String>> extract,
        Map<String, String> natives,
        List<Rule> rules
    ) {
        public record Downloads(Downloadable artifact, Map<String, Downloadable> classifiers) {
        }
    }

    public record Rule(String action, OS os, Map<String, Boolean> features) {
    }

    public record OS(String name, String version, String arch) {
    }

    public record Logging(String argument, Downloadable file, String type) {
    }

    @JsonDeserialize(using = ArgumentDeserializer.class)
    public record Argument(List<String> value, List<Rule> rules) {
    }

    public static class ArgumentDeserializer extends JsonDeserializer<Argument> {
        @Override
        public Argument deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            var node = p.getCodec().readTree(p);

            if (node instanceof TextNode textNode) {
                return new Argument(List.of(textNode.textValue()), List.of());
            } else if (node instanceof ObjectNode objectNode) {
                var valueNode = objectNode.get("value");
                var value = new ArrayList<String>();
                if (valueNode instanceof ArrayNode valueArray) {
                    var valueIterator = valueArray.elements();
                    while (valueIterator.hasNext()) {
                        var singleValueNode = valueIterator.next();
                        if (singleValueNode instanceof TextNode singleValue) {
                            value.add(singleValue.textValue());
                        } else {
                            throw InvalidFormatException.from(p, Argument.class, "Argument value can only be a string or an array of strings");
                        }
                    }
                } else if (valueNode instanceof TextNode singleValue) {
                    value.add(singleValue.textValue());
                } else {
                    throw InvalidFormatException.from(p, Argument.class, "Argument value can only be a string or an array of strings");
                }

                var rulesNode = objectNode.get("rules");
                var rules = new ArrayList<Rule>();
                if (rulesNode instanceof ArrayNode rulesArray) {
                    var rulesIterator = rulesArray.elements();
                    while (rulesIterator.hasNext()) {
                        var ruleNode = rulesIterator.next();
                        rules.add(p.getCodec().treeToValue(ruleNode, Rule.class));
                    }
                } else {
                    throw InvalidFormatException.from(p, Argument.class, "Argument rules can only be an array of objects");
                }

                return new Argument(value, rules);
            }

            throw InvalidFormatException.from(p, Argument.class, "Argument can either be a string or an object");
        }
    }
}
