package fedexru.plugins.createreactcomponent;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

/**
 * Класс для создания директорий и файлов React-компонента.
 *
 * @since 1.0.0
 *
 * @author Uryadyshev P.A
 */
public class ComponentStructureCreator {

    /**
     * Директория создаваемого компонента.
     */
    private final VirtualFile d;

    public ComponentStructureCreator(VirtualFile directory) {
        d = directory;
    }

    /**
     * Создать записываемый файл.
     *
     * @param name Название компонента
     *
     * @return VirtualFile
     */
    public VirtualFile createFile(String name) throws IOException {
        VirtualFile file = d.createChildData(this, name);

        file.setWritable(true);

        return file;
    }

    /**
     * Создать директории компонентов и контейнеров для компонента.
     */
    public void createModuleStructure() throws IOException {
        VirtualFile containersDirectory = d.createChildDirectory(this, "containers");
        containersDirectory.setWritable(true);

        VirtualFile containersIndex = containersDirectory.createChildData(this, "index.ts");
        containersIndex.setWritable(true);
        containersIndex.setBinaryContent("\n".getBytes());

        VirtualFile componentsDirectory = d.createChildDirectory(this, "components");
        componentsDirectory.setWritable(true);

        VirtualFile componentsIndex = componentsDirectory.createChildData(this, "index.ts");
        componentsIndex.setWritable(true);
        componentsIndex.setBinaryContent("\n".getBytes());
    }
}
