package de.template;

import java.util.Collections;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.LineNumberFactory;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FXMLController {

    @FXML
    private Label label;

    // @FXML 
    // private TextArea textArea;

    @FXML
    private CodeArea codeArea_1;

    @FXML private VirtualizedScrollPane<InlineCssTextArea> sourceContainerTextScreenVirtualizedScrollPane;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }

    public void highlightRow(final int index) {

        int size = codeArea_1.getParagraphs().size();
        if (size <= index) {
            return;
        }

        for (int i = 0; i < codeArea_1.getParagraphs().size(); i++) {
            codeArea_1.clearParagraphStyle(i);
        }

        codeArea_1.setParagraphStyle(index, Collections.singleton("red"));
    }
    
    /*
        .global add3
        .text
add3:   add a0, a0, a1      # a0 = a0 + a1
        add a0, a0, a2      # a0 = a0 + a2
        ret                 # return value in a0
*/
    public void initialize() {

        codeArea_1.setParagraphGraphicFactory(LineNumberFactory.get(codeArea_1));

        codeArea_1.replaceText("        .global add3\n" +
                        "        .text\n" +
                        "add3:   add a0, a0, a1      # a0 = a0 + a1\n" +
                        "        add a0, a0, a2      # a0 = a0 + a2\n" +
                        "        ret                 # return value in a0");

        codeArea_1.textProperty().addListener(new ChangeListener() {

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                //System.out.println("textarea changed from " + oldValue + " to " + newValue);
            }
            
        });

        codeArea_1.plainTextChanges().subscribe(change -> {
            
            for (int i = 0; i < codeArea_1.getParagraphs().size(); i++) {
                codeArea_1.clearParagraphStyle(i);
            }

            // find new paragraph
            int para = codeArea_1.getCaretSelectionBind().getParagraphIndex();

            codeArea_1.setParagraphStyle(para, Collections.singleton("red"));

            System.out.println("{\n" + codeArea_1.getText() + "\n}");

            // highlightRow(2);
        });
    }    
}