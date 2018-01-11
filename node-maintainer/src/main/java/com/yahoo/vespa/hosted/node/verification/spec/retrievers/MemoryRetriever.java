// Copyright 2017 Yahoo Holdings. Licensed under the terms of the Apache 2.0 license. See LICENSE in the project root.
package com.yahoo.vespa.hosted.node.verification.spec.retrievers;

import com.yahoo.vespa.hosted.node.verification.commons.CommandExecutor;
import com.yahoo.vespa.hosted.node.verification.commons.parser.OutputParser;
import com.yahoo.vespa.hosted.node.verification.commons.parser.ParseInstructions;
import com.yahoo.vespa.hosted.node.verification.commons.parser.ParseResult;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Retrieves memory size, and stores the result in a HardwareInfo instance
 *
 * @author olaaun
 * @author sgrostad
 */
public class MemoryRetriever implements HardwareRetriever {

    private static final String MEMORY_INFO_COMMAND = "cat /proc/meminfo";
    private static final String SEARCH_WORD = "MemTotal";
    private static final String REGEX_SPLIT = ":\\s";
    private static final int SEARCH_ELEMENT_INDEX = 0;
    private static final int RETURN_ELEMENT_INDEX = 1;
    private static final Logger logger = Logger.getLogger(MemoryRetriever.class.getName());

    private final CommandExecutor commandExecutor;

    MemoryRetriever(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    @Override
    public void updateInfo(HardwareInfo.Builder hardwareInfoBuilder) {
        try {
            List<String> commandOutput = commandExecutor.executeCommand(MEMORY_INFO_COMMAND);
            ParseResult parseResult = parseMemInfoFile(commandOutput);
            double memory = convertKBToGB(parseResult.getValue());
            hardwareInfoBuilder.withMinMainMemoryAvailableGb(memory);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Failed to retrieve memory info. ", e);
        }
    }

    ParseResult parseMemInfoFile(List<String> commandOutput) {
        List<String> searchWords = Collections.singletonList(SEARCH_WORD);
        ParseInstructions parseInstructions = new ParseInstructions(SEARCH_ELEMENT_INDEX, RETURN_ELEMENT_INDEX, REGEX_SPLIT, searchWords);
        ParseResult parseResult = OutputParser.parseSingleOutput(parseInstructions, commandOutput);
        if (!parseResult.getSearchWord().matches(SEARCH_WORD)) {
            throw new IllegalArgumentException("Failed to parse memory info file.");
        }
        return parseResult;
    }

    static double convertKBToGB(String totMem) {
        String[] split = totMem.split(" ");
        double value = Double.parseDouble(split[0]);
        double kiloToGiga = 1_000_000.0;
        return value / kiloToGiga;
    }
}
