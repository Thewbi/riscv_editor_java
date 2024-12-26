package de.template;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.LineNumberFactory;

import com.mycompany.assembler.RiscVAssembler;
import com.mycompany.cpu.CPU;
import com.mycompany.data.Register;
import com.mycompany.data.Section;
import com.mycompany.linkerscriptparser.LinkerScriptParser;
import com.mycompany.preprocessing.IncludePreprocessor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FXMLController {

    private RiscVAssembler riscVAssembler = new RiscVAssembler();

    private CPU cpu = new CPU();

    private int lineIndex = 0;

    @FXML
    private Label label;

    // @FXML 
    // private TextArea textArea;

    @FXML
    private CodeArea codeArea_1;

    @FXML 
    private VirtualizedScrollPane<InlineCssTextArea> sourceContainerTextScreenVirtualizedScrollPane;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        //label.setText("Hello World!");

        // var asmLines = riscVAssembler.asmLines;

        // // TODObuild line indexes

        // if (lineIndex == 0) {
        //     for (var asmLine : asmLines) {

        //         if (asmLine.mnemonic != null) {
        //             //long offset = asmLine.offset;
                    
        //             break;
        //         }

        //         // todo handle special case of pseudo instructions
        //         lineIndex++;
        //     }
        // } else {
        //     lineIndex++;
        // }

        // highlightRow(lineIndex);

        cpu.step();


        var asmLines = riscVAssembler.asmLines;

        // TODObuild line indexes

        for (var asmLine : asmLines) {

            if (asmLine.mnemonic != null) {
                if (asmLine.offset == cpu.pc) {
                    System.out.println(asmLine);

                    highlightRow(asmLine.sourceLine - 1);
                    break;
                }
            }
        }
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
    public void initialize() throws IOException {

        /**/
        codeArea_1.setParagraphGraphicFactory(LineNumberFactory.get(codeArea_1));

        // codeArea_1.replaceText("        .global add3\n" +
        //                 "        .text\n" +
        //                 "add3:   add a0, a0, a1      # a0 = a0 + a1\n" +
        //                 "        add a0, a0, a2      # a0 = a0 + a2\n" +
        //                 "        ret                 # return value in a0");

        // codeArea_1.replaceText(
        //                 "        .global add3\n" + //
        //                 "        .text\n" + //
        //                 "add3:   li t0, 0\n" + //
        //                 "        ret                 # return value in a0");

        codeArea_1.replaceText("    li t0, 0            # t0 = 0\n" + //
                        "    li t2, 10           # t2 = 10\n" + //
                        "\n" + //
                        "loop_head:\n" + //
                        "    bge t0, t2, loop_end\n" + //
                        "                    # Repeated code goes here\n" + //
                        "    addi t0, t0, 1\n" + //
                        "    j loop_head\n" + //
                        "\n" + //
                        "loop_end:\n" + //
                        "   ret");


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

        // create build folder
        Files.createDirectories(Paths.get("build"));

        //String inputFile = "src/test/resources/projects/snake/Main.asm";
        String inputFile = "src/test/resources/riscvasm/test.s";
        String outputFile = "build/preprocessed.s";

        //
        // preprocess
        //

        // the first step is always to let the preprocessor resolve .include
        // instructions. Let the compiler run on the combined file in a second step!

        preprocess(inputFile, outputFile);

        byte[] machineCode = assemble(inputFile, outputFile);

        //
        // CPU
        //
        
        cpu.pc = 0;
        cpu.memory = machineCode;

        cpu.registerFile[Register.REG_A1.ordinal()] = 1;
        cpu.registerFile[Register.REG_A2.ordinal()] = 5;

        // for (int i = 0; i < 100; i++) {
        //     cpu.step();
        // }

        var asmLines = riscVAssembler.asmLines;

        // TODObuild line indexes

        if (lineIndex == 0) {
            for (var asmLine : asmLines) {

                if (asmLine.mnemonic != null) {
                    if (asmLine.offset == cpu.pc) {
                        System.out.println(asmLine);

                        if (asmLine.pseudoInstructionAsmLine != null) {
                            highlightRow(asmLine.pseudoInstructionAsmLine.sourceLine - 1);
                        } else {
                            highlightRow(asmLine.sourceLine - 1);
                        }
                        break;
                    }
                }

                // todo handle special case of pseudo instructions
                lineIndex++;
            }

        } else {
            lineIndex++;
        }
         
    }
    
    private byte[] assemble(final String inputFile, final String outputFile) throws FileNotFoundException, IOException {

        //
        // linker script
        //

        Map<String, Section> sectionMap = new HashMap<>();

        LinkerScriptParser linkerScriptParser = new LinkerScriptParser();
        linkerScriptParser.parseLinkerScript(sectionMap);

        //
        // assemble
        //

        String asmInputFile = "build/preprocessed.s";

        
        byte[] machineCode = riscVAssembler.assemble(sectionMap, asmInputFile);
                
        return machineCode;
    }

    private static void preprocess(final String inputFile, final String outputFile) throws FileNotFoundException, IOException {
        
        System.out.println("Precprocessing input file ...");

        try (java.io.BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile))) {
            
            IncludePreprocessor includePreprocessor = new IncludePreprocessor();
            includePreprocessor.preprocess(inputFile, bufferedWriter);
            
            bufferedWriter.flush();
        }

        System.out.println("Precprocessing input done ...");
    }
}