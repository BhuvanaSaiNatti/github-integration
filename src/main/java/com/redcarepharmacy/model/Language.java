package com.redcarepharmacy.model;

import java.util.Arrays;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Language {
    C("C"),
    CPLUSPLUS("C++"),
    CSHARP("C#"),
    GO("Go"),
    JAVA("Java"),
    JAVASCRIPT("JavaScript"),
    PHP("PHP"),
    PYTHON("Python"),
    RUBY("Ruby"),
    SCALA("Scala"),
    SWIFT("Swift"),
    TYPESCRIPT("TypeScript");

    @NonNull private final String name;

    public static Language getLanguageIfValid(String language) {
        if (EnumUtils.isValidEnumIgnoreCase(Language.class, language)) {
            return Arrays.stream(Language.values())
                    .filter(e -> e.name().equalsIgnoreCase(language))
                    .findAny()
                    .orElse(null);
        } else return null;
    }
}
