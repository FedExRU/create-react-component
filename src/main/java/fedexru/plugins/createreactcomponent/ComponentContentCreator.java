package fedexru.plugins.createreactcomponent;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Класс для генерации контента создаваемых файлов.
 *
 * @since 1.0.1
 *
 * @author Uryadyshev P.A
 */
public class ComponentContentCreator
{
    /**
     * Название создаваемого компонента.
     */
    private final String componentName;

    /**
     * Конфигурация создаваемого компонента.
     */
    private final Map< String, Boolean > options;

    /**
     * Экспорт packages;
     */
    private final boolean usePackages = true;

    public ComponentContentCreator(String cn, Map< String, Boolean > o) {
        componentName = cn;
        options = o;
    }

    /**
     * Сгенерировать контент файла с компонентом.
     *
     * @return String
     */
    public @NotNull String getComponentContent() {
        boolean isFc = options.get(CreateReactComponentDialog.OPTION_CREATE_FC);
        boolean isMemo = options.get(CreateReactComponentDialog.OPTION_CREATE_MEMO);
        boolean isTypes = options.get(CreateReactComponentDialog.OPTION_CREATE_TYPE);
        boolean hasMemoOrFc = isFc || isMemo;
        boolean hasMemoAndFc = isFc && isMemo;

        String componentType = componentName+"Props";
        String componentSuffix = (isFc ? ": FC" : (usePackages ? ": CFC" : "")) + (isFc && isTypes || (!isFc && isTypes && usePackages) ? "<"+componentType+">" : "");

        String reactAdditionalImport = hasMemoOrFc ? "{ "+(isFc ? "FC" : "")+(hasMemoAndFc ? ", " : "") + (isMemo ? "memo" : "") + " }" : null;

        String memoPrefix = isMemo ? "memo(" : "";
        String memoSuffix = isMemo ? ")" : "";

        String propsSuffix = isFc ? "" : (usePackages ? "" : (isTypes ? ": " + componentType : ""));

        String componentReturnType = isFc ? "" : (usePackages ? "" : ": JSX.Element");

        String props = isTypes ? "props" : (!isTypes && isFc ? "{ children }" : "");

        return "import React"+(reactAdditionalImport != null ? ", " + reactAdditionalImport : "")+" from 'react'\n" +
                (usePackages && !isFc ? "import { CFC } from '@packages/common'\n" : "" ) +
                (isTypes ? "import { "+componentType+" } from './types'\n" : "") +
                "\n" +
                "export const "+componentName+componentSuffix+" = "+memoPrefix+"("+props+propsSuffix+")"+componentReturnType+" => {\n" +
                "    return (\n" +
                "        <></>\n" +
                "    )\n" +
                "}" + memoSuffix + "\n";
    }

    /**
     * Сгенерировать контент файла index.ts.
     *
     * @return String
     */
    public String getIndexContent() {
        return "export { " +
                componentName +
                " } from './" +
                componentName +
                "'" +
                (
                        options.get(CreateReactComponentDialog.OPTION_CREATE_TYPE) ?
                                "\nexport type { " + componentName + "Props } from './types'" :
                                ""
                ) + "\n";
    }

    /**
     * Сгенерировать контент файла types.ts.
     *
     * @return String
     */
    public String getTypesContent() {
        return "export type " + componentName + "Props = {}\n";
    }
}
