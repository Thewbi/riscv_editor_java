package de.template;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
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
import com.mycompany.cpu.PipelinedCPU;
import com.mycompany.cpu.SingleCycleCPU;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;
import com.mycompany.linkerscriptparser.LinkerScriptParser;
import com.mycompany.preprocessing.IncludePreprocessor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class FXMLController {

    private static final String INTERMEDIATE_FILE = "build/preprocessed.s";

    private RiscVAssembler assembler;

    //private SingleCycleCPU cpu = new SingleCycleCPU();
    private PipelinedCPU cpu = new PipelinedCPU();

    // private int lineIndex = 0;

    @FXML
    private Label label;

    @FXML
    private CodeArea codeArea_1;

    @FXML
    private VirtualizedScrollPane<InlineCssTextArea> sourceContainerTextScreenVirtualizedScrollPane;

    @FXML
    private void handleButtonAction(ActionEvent event) {

        // System.out.println("You clicked me!");

        var asmLines = assembler.asmLines;

        // if (lineIndex == 0) {
        //     for (var asmLine : asmLines) {

        //         if (asmLine.mnemonic != null) {
        //             break;
        //         }

        //         // todo handle special case of pseudo instructions
        //         lineIndex++;
        //     }
        // } else {
        //     lineIndex++;
        // }

        cpu.step();

        // MIPSAssembler assembler = new MIPSAssembler(sectionMap, dummySection);

        System.out.println("cpu.executePC: " + cpu.executePC);

        // for (var asmLine : asmLines) {

        //     if (asmLine.mnemonic != null) {

        //         int pc = cpu.executePC;
        //         if (pc < 0) {
        //             pc = 0;
        //         }
        //         if (asmLine.offset == pc) {

        //             System.out.println(asmLine);

        //             if (asmLine.pseudoInstructionAsmLine != null) {
        //                 highlightRow(asmLine.pseudoInstructionAsmLine.sourceLine - 1);
        //             } else {
        //                 highlightRow(asmLine.sourceLine - 1);
        //             }

        //             break;
        //         }
        //     }
        // }

        if (cpu.de_ex.getAsmLine() != null) {
            System.out.println("Highlight: " + cpu.de_ex.getAsmLine());
            highlightRow(cpu.de_ex.getAsmLine().sourceLine - 1);
        }
    }

    public void highlightRow(final int index) {

        int size = codeArea_1.getParagraphs().size();
        if ((size <= index) || (index < 0)) {
            return;
        }

        for (int i = 0; i < codeArea_1.getParagraphs().size(); i++) {
            codeArea_1.clearParagraphStyle(i);
        }

        codeArea_1.setParagraphStyle(index, Collections.singleton("red"));
    }

    public void initialize() throws IOException {

        codeArea_1.setParagraphGraphicFactory(LineNumberFactory.get(codeArea_1));

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

            //System.out.println("{\n" + codeArea_1.getText() + "\n}");
        });

        // //String inputFile = "src/test/resources/projects/snake/Main.asm";
        //String inputFile = "src/test/resources/riscvasm/test.s";
        //String inputFile = "src/test/resources/riscvasm/fibonacci_rvcc.s";
        //String inputFile = "src/test/resources/riscvasm/for_loop_2.s";
        //String inputFile = "src/test/resources/riscvasm/square_with_driver.s";
        String inputFile = "src/test/resources/riscvasm/if.s";

        // load the source file contents into the codeArea
        codeArea_1.replaceText(Files.readString(Paths.get(inputFile), StandardCharsets.UTF_8));

        // String outputFile = "build/preprocessed.s";

        //
        // global variables
        //

        // the GCC compiler adds a funny line: .section	.note.GNU-stack,"",@progbits
        // The section .note.GNU-stack is not defined
        // To not break the code, a dummy section is inserted which is used as a catch-all
        // for all sections that are not defined
        Section dummySection = new Section();
        dummySection.name = "dummy-section";

        //
        // preprocess
        //

        // create build folder
        Files.createDirectories(Paths.get("build"));

        // the first step is always to let the preprocessor resolve .include
        // instructions. Let the compiler run on the combined file in a second step!

        // String inputFile = args[0];
        String outputFile = INTERMEDIATE_FILE;
        preprocess(inputFile, outputFile);

        //
        // linker script
        //

        Map<String, Section> sectionMap = new HashMap<>();
        sectionMap.put(dummySection.name, dummySection);

        LinkerScriptParser linkerScriptParser = new LinkerScriptParser();
        linkerScriptParser.parseLinkerScript(sectionMap);

        //
        // assemble to machine code
        //

        assembler = new RiscVAssembler(sectionMap, dummySection);

        String asmInputFile = INTERMEDIATE_FILE;

        cpu.memory = new byte[2048];
        // cpu.memory[80] = 1;
        // cpu.memory[81] = 2;
        // cpu.memory[82] = 3;
        // cpu.memory[83] = 4;

        byte[] machineCode = assembler.assemble(sectionMap, asmInputFile);
        System.arraycopy(machineCode, 0, cpu.memory, 0, machineCode.length);

        // set up program counter and stack pointer
        cpu.pc = 0;
        cpu.registerFile[RISCVRegister.REG_SP.ordinal()] = 4 * 100;

        // DEBUG
        ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        //ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
        assembler.outputHexMachineCode(machineCode, byteOrder);

        // TODO build line indexes

        int sourceLine = 0;
        for (var asmLine : assembler.asmLines) {

            asmLine.sourceLine = sourceLine;
            sourceLine++;

        }

        // if (lineIndex == 0) {

            for (var asmLine : assembler.asmLines) {

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
                // lineIndex++;
            }

        // } else {
        //     lineIndex++;
        // }

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