package fedex.plugins.createreactcomponent;

import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.pty4j.util.Pair;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс диалогового окна для создания файлов и структуры директорий React-компонента с настройками создаваемых элементов.
 * <br/>
 * Диалоговое окно отображает текстовое поле, в которое вводится название компонента, а также отмечаются чекбоксы с
 * указанием настроек дополнительных создаваемых элементов для компонента.
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
 * @see com.intellij.openapi.ui.DialogWrapper
 * @see Messages.InputDialog
 * @see com.intellij.openapi.ui.messages.MessageDialog
 * @see <a href="https://react.dev/learn/keeping-components-pure#purity-components-as-formulas">React Functional Components</a>
 */
class CreateReactComponentDialog extends Messages.InputDialog {

    /**
     * Чекбокс для указания создания функционального компонента.
     */
    private JCheckBox checkBoxFc;

    /**
     * Чекбокс для указания создания файла с типами компонента. По умолчанию - true.
     */
    private JCheckBox checkBoxTypes;

    /**
     * Чекбокс для указания создания файла с константами компонента.
     */
    private JCheckBox checkBoxConstants;

    /**
     * Чекбокс для указания создания файла с дополнительными утилитами компонента.
     */
    private JCheckBox checkBoxUtils;

    /**
     * Чекбокс для указания создания дополнительной структуры директорий для компонента, который создаётся в качестве модуля.
     */
    private JCheckBox checkBoxModule;

    /**
     * Чекбокс для указания создания меморизированного компонента.
     *
     * @see <a href="https://react.dev/reference/react/memo">React Memo</a>
     */
    private JCheckBox checkBoxMemo;

    /**
     * Свойство создания функционального компонента.
     */
    public static String OPTION_CREATE_FC = "OPTION_CREATE_FC";

    /**
     * Свойство создания файла с типами.
     */
    public static String OPTION_CREATE_TYPE = "OPTION_CREATE_TYPE";

    /**
     * Свойство создания файла с константами.
     */
    public static String OPTION_CREATE_CONSTANTS = "OPTION_CREATE_CONSTANTS";

    /**
     * Свойство создания файла с утилитами.
     */
    public static String OPTION_CREATE_UTILS = "OPTION_CREATE_UTILS";

    /**
     * Свойство создания дополнительной структуры директорий для компонента.
     */
    public static String OPTION_CREATE_MODULE = "OPTION_CREATE_MODULE";

    /**
     * Свойство создания меморизинованного компонента.
     */
    public static String OPTION_CREATE_MEMO = "OPTION_CREATE_MEMO";

    CreateReactComponentDialog() {
        super("Component name:", "Create Component", null, null, new InputValidator() {
            public boolean checkInput(String inputString) {
                return inputString.length() > 3 && inputString.matches("^[A-Z]+[a-zA-Z0-9]*$");
            }

            public boolean canClose(String inputString) {
                return true;
            }
        });
    }

    /**
     *
     * Создание панели с тектовым полем для указания названия компонента, а также панели с чекбоксами.
     *
     * @return JPanel
     */
    @NotNull
    @Override
    protected JPanel createMessagePanel() {
        JPanel messagePanel = new JPanel(new BorderLayout());
        JComponent textComponent = createTextComponent();
        myField = createTextFieldComponent();

        messagePanel.add(textComponent, BorderLayout.NORTH);
        messagePanel.add(createScrollableTextComponent(), BorderLayout.CENTER);
        messagePanel.add(this.createCheckboxPanel(), BorderLayout.SOUTH);

        return messagePanel;
    }

    /**
     * Создание панели с чекбоксами.
     *
     * @return JPanel
     */
    private JPanel createCheckboxPanel() {
        JPanel checkboxPanel = new JPanel(new GridLayout(0, 2, 16, 0));
        checkboxPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        checkBoxFc = new JCheckBox();
        checkBoxFc.setText("Create Functional Component");

        checkBoxTypes = new JCheckBox();
        checkBoxTypes.setText("Create Types");
        checkBoxTypes.setSelected(true);

        checkBoxConstants = new JCheckBox();
        checkBoxConstants.setText("Create Constants");

        checkBoxUtils = new JCheckBox();
        checkBoxUtils.setText("Create Utils");

        checkBoxModule = new JCheckBox();
        checkBoxModule.setText("Create Module Structure");

        checkBoxMemo = new JCheckBox();
        checkBoxMemo.setText("Create as Memo");

        checkboxPanel.add(checkBoxFc);
        checkboxPanel.add(checkBoxMemo);
        checkboxPanel.add(checkBoxTypes);
        checkboxPanel.add(checkBoxConstants);
        checkboxPanel.add(checkBoxUtils);
        checkboxPanel.add(checkBoxModule);

        return checkboxPanel;
    }

    /**
     * Вернуть значения формы создания компонента.
     *
     * @return Pair
     */
    public Pair < String, Map < String, Boolean >> getValues() {
        Map < String, Boolean > values = new HashMap < String, Boolean > ();

        values.put(OPTION_CREATE_FC, checkBoxFc.isSelected());
        values.put(OPTION_CREATE_TYPE, checkBoxTypes.isSelected());
        values.put(OPTION_CREATE_CONSTANTS, checkBoxConstants.isSelected());
        values.put(OPTION_CREATE_UTILS, checkBoxUtils.isSelected());
        values.put(OPTION_CREATE_MODULE, checkBoxModule.isSelected());
        values.put(OPTION_CREATE_MEMO, checkBoxMemo.isSelected());

        return Pair.create(myField.getText(), values);
    }

}