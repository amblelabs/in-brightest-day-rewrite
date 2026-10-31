package dev.amblelabs.lib.jna;

import com.sun.jna.FunctionMapper;
import com.sun.jna.Library;
import dev.amblelabs.lib.util.CaseUtil;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class JnaHelper {

    public static Map<String, ?> makeOptions(Class<? extends Library> clazz) {
        Map<String, String> symbols = makeSymbols(clazz);
        if (symbols == null) return Map.of();

        FunctionMapper mapper = (_, method) -> symbols.getOrDefault(method.getName(), method.getName());
        return Map.of(Library.OPTION_FUNCTION_MAPPER, mapper);
    }

    private static @Nullable Map<String, String> makeSymbols(Class<? extends Library> clazz) {
        Map<String, String> symbols = null;

        JnaClass global = clazz.getAnnotation(JnaClass.class);
        JnaClass.Mapper defMapper = global == null || global.mapper() == JnaClass.Mapper.NONE
                ? null : JnaClass.Mapper.SNAKE_CASE;

        for (Method method : clazz.getMethods()) {
            JnaFunction func = method.getAnnotation(JnaFunction.class);
            if (func == null) continue;

            String name;
            JnaFunction.Mapper mapper = func.mapper() == JnaFunction.Mapper.DEFAULT ? null : func.mapper();

            if (func.value().isEmpty()) {
                if (mapper == JnaFunction.Mapper.NONE) continue;

                mapper = mapper == null ? (defMapper != null ? defMapper.asFunc() : null) : mapper;
                if (mapper == null) continue;

                name = switch (mapper) {
                    case NONE, DEFAULT -> throw new IllegalStateException();
                    case SNAKE_CASE -> CaseUtil.camelToSnake(method.getName());
                };
            } else {
                name = func.value();
            }

            if (symbols == null)
                symbols = new HashMap<>();

            symbols.put(method.getName(), name);
        }

        return symbols;
    }
}
