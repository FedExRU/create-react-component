package fedex.plugins.createreactcomponent;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

/**
 * Шорткат для создания React-компонента с необходимой структурой директорий.
 * <p/>
 *
 * При использовании сочетания клавиш "SHIFT+C", вызывается диалоговое окно, в котором указывается
 * название компонента, а также, с помощью соответствующих чекбоксов, указываются настройки создания самого
 * компонента и необходимых ему дополнительных директорий и файлов.
 * <p/>
 * Название компонента должно быть не менее трёх символов, только латинские буквы и числа, компонент должен называться
 * с заглавной буквы, первым символом не должно являться число.
 * <p/>
 * Настройки позволяют указать следующие создаваемые элементы:
 * <li/> Создание компонента как FunctionalComponent (FC), или компонент без children;
 * <li/> Нужно ли создавать для компонента файл с типами;
 * <li/> Нужно ли создавать для компонента файл с константами;
 * <li/> Нужно ли создавать для компонента файл с дополнительными утилитами;
 * <li/> Является ли компонент модулем, и нужно ли создавать дополнительную структуру директорий для модуля.
 *
 * @since 1.0.0
 *
 * @author Uryadyshev P.A
 */
public class CreateReactComponentAction extends AnAction {

    /**
     * Экспорт packages;
     */
    private boolean usePackages = false;

    /**
     * Название создаваемого компонента.
     */
    private String componentName;

    /**
     * Конфигурация создаваемого компонента.
     */
    private Map < String, Boolean > options;

    @Override
    public void actionPerformed(AnActionEvent event) {
        VirtualFile runtimeDirectory = event.getData(PlatformDataKeys.VIRTUAL_FILE);

        assert runtimeDirectory != null;

        VirtualFile currentDirectory = runtimeDirectory.getExtension() == null ? runtimeDirectory : runtimeDirectory.getParent();

        CreateReactComponentDialog dialog = new CreateReactComponentDialog();

        if (dialog.showAndGet() && dialog.getValues().first != null) {
            componentName = dialog.getValues().first;
            options = dialog.getValues().second;

            try {
                if (!currentDirectory.isDirectory()) {
                    throw new IOException(currentDirectory.getName() + "is not valid directory!");
                }

                VirtualFile directory = currentDirectory.createChildDirectory(this, componentName);

                ComponentStructureCreator creator = new ComponentStructureCreator(directory);

                if (!currentDirectory.isWritable()) {
                    throw new IOException(currentDirectory.getName() + "is not writable directory!");
                }

                creator.createFile("index.ts").setBinaryContent(this.getIndexContent().getBytes());

                if (options.get(CreateReactComponentDialog.OPTION_CREATE_TYPE)) {
                    creator.createFile("types.ts").setBinaryContent(this.getTypesContent().getBytes());
                }

                if (options.get(CreateReactComponentDialog.OPTION_CREATE_CONSTANTS)) {
                    creator.createFile("constants.ts");
                }

                if (options.get(CreateReactComponentDialog.OPTION_CREATE_UTILS)) {
                    creator.createFile("utils.ts");
                }

                if (options.get(CreateReactComponentDialog.OPTION_CREATE_MODULE)) {
                    creator.createModuleStructure();
                }

                VirtualFile reactComponent = creator.createFile(componentName + ".tsx");
                reactComponent.setBinaryContent(this.getComponentContent().getBytes());
            } catch (IOException e) {
                Messages.showErrorDialog(e.getMessage(), "An Error Occurred");
            }
        }
    }

    /**
     * Записать контент файла с компонентом.
     *
     * @return String
     */
    private @NotNull String getComponentContent() {
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
     * Записать контент файла index.ts.
     *
     * @return String
     */
    private String getIndexContent() {
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
     * Записать контент файла types.ts.
     *
     * @return String
     */
    private String getTypesContent() {
        return "export type " + componentName + "Props = {}\n";
    }

    @Override
    public boolean isDumbAware() {
        return false;
    }
}