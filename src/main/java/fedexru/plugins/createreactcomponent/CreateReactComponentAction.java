package fedexru.plugins.createreactcomponent;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.Map;

/**
 * Шорткат для создания React-компонента с необходимой структурой директорий.
 * <p/>
 *
 * При использовании сочетания клавиш "SHIFT+CTRL+C", вызывается диалоговое окно, в котором указывается
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

        Application app = ApplicationManager.getApplication();

        assert runtimeDirectory != null;

        VirtualFile currentDirectory = runtimeDirectory.getExtension() == null ? runtimeDirectory : runtimeDirectory.getParent();

        CreateReactComponentDialog dialog = new CreateReactComponentDialog();

        if (dialog.showAndGet() && dialog.getValues().first != null) {
            componentName = dialog.getValues().first;
            options = dialog.getValues().second;

            Runnable createComponentCommand = ()-> {
                try {
                    if (!currentDirectory.isDirectory()) {
                        throw new IOException(currentDirectory.getName() + "is not valid directory!");
                    }

                    VirtualFile directory = currentDirectory.createChildDirectory(this, componentName);

                ComponentStructureCreator creator = new ComponentStructureCreator(directory);
                ComponentContentCreator content = new ComponentContentCreator(componentName, options);

                if (!currentDirectory.isWritable()) {
                    throw new IOException(currentDirectory.getName() + "is not writable directory!");
                }

                creator.createFile("index.ts").setBinaryContent(content.getIndexContent().getBytes());

                if (options.get(CreateReactComponentDialog.OPTION_CREATE_TYPE)) {
                    creator.createFile("types.ts").setBinaryContent(content.getTypesContent().getBytes());
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
                reactComponent.setBinaryContent(content.getComponentContent().getBytes());
                } catch (IOException e) {
                    Messages.showErrorDialog(e.getMessage(), "An Error Occurred");
                }
            };


            app.runWriteAction(createComponentCommand);
        }
    }

    @Override
    public boolean isDumbAware() {
        return false;
    }
}