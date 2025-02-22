package dev.coph.simplesql.database.functions.numeric;

import dev.coph.simplesql.database.functions.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

public class NumericFunction {
    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Abs implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class ACos implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class ASin implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class ATan implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class ATan2 implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Avg implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Ceiling implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Cos implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Cot implements Function {
        /*TODO*/
    }


    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Count implements Function {
        private String columnName = "*";

        @Override
        public String toString() {
            return " COUNT(" + columnName + ")";
        }
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Degrees implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Div implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Exp implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Floor implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Greatest implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Least implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Ln implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Log implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Log10 implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Log2 implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Max implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Min implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Mod implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Pi implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Power implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Radians implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Rand implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Round implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Sign implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Sin implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class SqRt implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Sum implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Tan implements Function {
        /*TODO*/
    }

    @Getter
    @Setter
    @Accessors(fluent = true, chain = true)
    @AllArgsConstructor
    public static class Turncate implements Function {
        /*TODO*/
    }
}
