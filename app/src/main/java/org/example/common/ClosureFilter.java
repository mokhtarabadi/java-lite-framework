/*
 * Apache License 2.0
 * 
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Copyright [2024] [Mohammad Reza Mokhtarabadi <mmokhtarabadi@gmail.com>]
 */
package org.example.common;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import de.neuland.pug4j.filter.CachingFilter;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class ClosureFilter extends CachingFilter {

    @NonNull private Boolean prettyPrint;

    private static final String SCRIPT_START = "<script type=\"text/javascript\">";
    private static final String SCRIPT_END = "</script>";

    @Override
    public String convert(String source, Map<String, Object> options) {
        // options.forEach((s, o) -> System.out.println(s + " : " + o));
        log.info("ClosureFilter.convert()");

        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setLanguageOut(CompilerOptions.LanguageMode.ECMASCRIPT_2015);
        compilerOptions.setPrettyPrint(prettyPrint);

        Compiler compiler = new Compiler();

        SourceFile sourceFile = SourceFile.fromCode("input.js", source);
        SourceFile externalSourceFile = SourceFile.fromCode("externs.js", "");

        compiler.compile(externalSourceFile, sourceFile, compilerOptions);

        return SCRIPT_START + compiler.toSource() + SCRIPT_END;
    }
}
