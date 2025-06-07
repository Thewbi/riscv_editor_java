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
import com.mycompany.common.ByteArrayUtil;
import com.mycompany.cpu.SingleCycleCPU;
import com.mycompany.data.AsmLine;
import com.mycompany.data.RISCVRegister;
import com.mycompany.data.Section;
import com.mycompany.linkerscriptparser.LinkerScriptParser;
import com.mycompany.preprocessing.IncludePreprocessor;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class FXMLController {

    private static final int MEMORY_SIZE_IN_BYTE = 1024 * 2;

    private static final String MAIN_ENTRY_POINT_LABEL = "__main";

    private static final String INTERMEDIATE_FILE = "build/preprocessed.s";

    private RiscVAssembler assembler;

    private SingleCycleCPU cpu = new SingleCycleCPU();
    // private PipelinedCPU cpu = new PipelinedCPU();

    @FXML
    private Label label;

    @FXML
    private CodeArea codeArea_1;

    @FXML
    private TextField textField_x0;

    @FXML
    private TextField textField_x1;

    @FXML
    private TextField textField_x2;

    @FXML
    private TextField textField_x3;

    @FXML
    private TextField textField_x4;

    @FXML
    private TextField textField_x5;

    @FXML
    private TextField textField_x6;

    @FXML
    private TextField textField_x7;

    @FXML
    private TextField textField_x8;

    @FXML
    private TextField textField_x9;

    @FXML
    private TextField textField_x10;

    @FXML
    private TextField textField_x11;

    @FXML
    private TextField textField_x12;

    @FXML
    private TextField textField_x13;

    @FXML
    private TextField textField_x14;

    @FXML
    private TextField textField_x15;

    @FXML
    private TextField textField_x16;

    @FXML
    private TextField textField_x17;

    @FXML
    private TextField textField_x18;

    @FXML
    private TextField textField_x19;

    @FXML
    private TextField textField_x20;

    @FXML
    private TextField textField_x21;

    @FXML
    private TextField textField_x22;

    @FXML
    private TextField textField_x23;

    @FXML
    private TextField textField_x24;

    @FXML
    private TextField textField_x25;

    @FXML
    private TextField textField_x26;

    @FXML
    private TextField textField_x27;

    @FXML
    private TextField textField_x28;

    @FXML
    private TextField textField_x29;

    @FXML
    private TextField textField_x30;

    @FXML
    private TextField textField_x31;

    @FXML
    private VirtualizedScrollPane<InlineCssTextArea> sourceContainerTextScreenVirtualizedScrollPane;

    /**
     *
     * @throws IOException
     */
    public void initialize() throws IOException {

        updateRegisterView();

        codeArea_1.setParagraphGraphicFactory(LineNumberFactory.get(codeArea_1));

        codeArea_1.textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // System.out.println("textarea changed from " + oldValue + " to " + newValue);
            }

        });

        codeArea_1.plainTextChanges().subscribe(change -> {

            for (int i = 0; i < codeArea_1.getParagraphs().size(); i++) {
                codeArea_1.clearParagraphStyle(i);
            }

            // find new paragraph
            int para = codeArea_1.getCaretSelectionBind().getParagraphIndex();

            codeArea_1.setParagraphStyle(para, Collections.singleton("red"));

            // System.out.println("{\n" + codeArea_1.getText() + "\n}");
        });

        // //String inputFile = "src/test/resources/projects/snake/Main.asm";
        // String inputFile = "src/test/resources/riscvasm/test.s";
        // String inputFile = "src/test/resources/riscvasm/fibonacci_rvcc.s";
        // String inputFile = "src/test/resources/riscvasm/for_loop_2.s";
        // String inputFile = "src/test/resources/riscvasm/square_with_driver.s";
        // String inputFile = "src/test/resources/riscvasm/if.s";
        // String inputFile = "src/test/resources/riscvasm/quicksort.s";
        //String inputFile = "src/test/resources/riscvasm/quicksort_clang.s";
        //String inputFile = "src/test/resources/riscvasm/string_length.s";
        //String inputFile = "src/test/resources/riscvasm/bltu.s";
        //String inputFile = "src/test/resources/riscvasm/la.s";
        //String inputFile = "src/test/resources/riscvasm/fib.s";
        String inputFile = "src/test/resources/riscvasm/blinky_memory_mapped_LED.s";

        // load the source file contents into the codeArea
        codeArea_1.replaceText(Files.readString(Paths.get(inputFile), StandardCharsets.UTF_8));

        // String outputFile = "build/preprocessed.s";

        //
        // global variables
        //

        // the GCC compiler adds a funny line: .section .note.GNU-stack,"",@progbits
        // The section .note.GNU-stack is not defined
        // To not break the code, a dummy section is inserted which is used as a
        // catch-all
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

        String asmInputFile = INTERMEDIATE_FILE;

        cpu.memory = new byte[2048];
        // cpu.memory[80] = 1;
        // cpu.memory[81] = 2;
        // cpu.memory[82] = 3;
        // cpu.memory[83] = 4;

        assembler = new RiscVAssembler(sectionMap, dummySection);
        byte[] machineCode = assembler.assemble(sectionMap, asmInputFile);
        System.arraycopy(machineCode, 0, cpu.memory, 0, machineCode.length);

        // THIS IS AN ERROR!
        // THE PC SHOULD ONLY START AT ADDRESS 0 IF THIS APPLICATION
        // DOES NOT DEFINE A MAIN ENTRY POINT!
        // IF THE APPLICATION HAS A MAIN FUNCTION / MAIN ENTRY POINT,
        // EXECUTION HAS TO START AT THE MAIN ENTRY POINT!
        int startAddress = assembler.labelAddressMap.get(MAIN_ENTRY_POINT_LABEL).intValue();
        cpu.pc = startAddress;

        // stack-pointer (sp, x2) register:
        // should not point into the source code (menomics in memory!)
        // because using the stack will then override the application code!
        //
        // stack should grow down, so set it to the highest memory address possible
        cpu.registerFile[RISCVRegister.REG_SP.getIndex()] = MEMORY_SIZE_IN_BYTE - 4;

        // frame-pointer (s0/fp, x8 register)
        cpu.registerFile[RISCVRegister.REG_FP.getIndex()] = 0;

        // ra - the initial return address is retrieved from the application loader
        // so that the app can return to that address
        // Without loader, we set it to 0xCAFEBABE = 3405691582 dec
        cpu.registerFile[RISCVRegister.REG_RA.getIndex()] = 0xCAFEBABE;

        cpu.memory = new byte[MEMORY_SIZE_IN_BYTE];
        // cpu.memory[80] = 1;
        // cpu.memory[81] = 2;
        // cpu.memory[82] = 3;
        // cpu.memory[83] = 4;

        System.arraycopy(machineCode, 0, cpu.memory, 0, machineCode.length);

        cpu.memory[machineCode.length + 4] = (byte) 0xFF;
        cpu.memory[machineCode.length + 5] = (byte) 0xFF;
        cpu.memory[machineCode.length + 6] = (byte) 0xFF;
        cpu.memory[machineCode.length + 7] = (byte) 0xFF;

        // // DEBUG - output machine code
        // ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        // //ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
        // BaseAssembler.outputHexMachineCode(machineCode, byteOrder);

        // DEBUG
        System.out.println("\n\n\n");
        System.out.println("***************************");
        for (AsmLine<?> asmLine : assembler.asmLines) {
            try {
                System.out.print(asmLine);
                System.out.println(" SourceLine: " + asmLine.sourceLine);
            } catch (Throwable e) {
                System.out.println("error!");
            }
        }
        System.out.println("***************************");

        // // build line indexes
        // int sourceLine = 0;
        // for (var asmLine : assembler.asmLines) {
        // asmLine.sourceLine = sourceLine;
        // sourceLine++;
        // }

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
        // lineIndex++;
        // }
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {

        // System.out.println("You clicked me!");

        // var asmLines = assembler.asmLines;

        // if (lineIndex == 0) {
        // for (var asmLine : asmLines) {

        // if (asmLine.mnemonic != null) {
        // break;
        // }

        // // todo handle special case of pseudo instructions
        // lineIndex++;
        // }
        // } else {
        // lineIndex++;
        // }

        cpu.step();

        // MIPSAssembler assembler = new MIPSAssembler(sectionMap, dummySection);

        // DEBUG
        // System.out.println("cpu.executePC: " + cpu.executePC);

        // for (var asmLine : asmLines) {

        // if (asmLine.mnemonic != null) {

        // int pc = cpu.executePC;
        // if (pc < 0) {
        // pc = 0;
        // }
        // if (asmLine.offset == pc) {

        // System.out.println(asmLine);

        // if (asmLine.pseudoInstructionAsmLine != null) {
        // highlightRow(asmLine.pseudoInstructionAsmLine.sourceLine - 1);
        // } else {
        // highlightRow(asmLine.sourceLine - 1);
        // }

        // break;
        // }
        // }
        // }

        //
        // highlight row
        //

        // if (cpu.de_ex.getAsmLine() != null) {
        // System.out.println("Highlight: " + cpu.de_ex.getAsmLine());
        // highlightRow(cpu.de_ex.getAsmLine().sourceLine - 1);
        // }

        // var asmLines = assembler.asmLines;

        // int index = cpu.pc / 4;
        // AsmLine<?> asmLine = asmLines.get(index);

        // // DEBUG
        // System.out.print(asmLine);
        // System.out.println(" SourceLine: " + asmLine.sourceLine);

        // map address to asmLine

        // System.out.println("PC: " + cpu.pc);
        // System.out.println("PC/4: " + cpu.pc / 4L);

        // for (Map.Entry<Long, AsmLine<?>> entry :
        // assembler.addressSourceAsmLineMap.entrySet()) {
        // System.out.println(entry.getKey() + " -> " + entry.getValue());
        // }

        AsmLine<?> asmLine = assembler.addressSourceAsmLineMap.get((long) cpu.pc);

        // System.out.println("asmLine.sourceLine: " + asmLine.sourceLine);

        highlightRow(asmLine.sourceLine - 1);

        updateRegisterView();

        //int address = MEMORY_SIZE_IN_BYTE - 4;
        int address = 2028;
        byte a = cpu.memory[address + 0];
        byte b = cpu.memory[address + 1];
        byte c = cpu.memory[address + 2];
        byte d = cpu.memory[address + 3];
        int number = ByteArrayUtil.fourByteToInt(a, b, c, d, ByteOrder.LITTLE_ENDIAN);
        System.out.print(number + " ");

        address -= 4;
        a = cpu.memory[address + 0];
        b = cpu.memory[address + 1];
        c = cpu.memory[address + 2];
        d = cpu.memory[address + 3];
        number = ByteArrayUtil.fourByteToInt(a, b, c, d, ByteOrder.LITTLE_ENDIAN);
        System.out.print(number + " ");

        address -= 4;
        a = cpu.memory[address + 0];
        b = cpu.memory[address + 1];
        c = cpu.memory[address + 2];
        d = cpu.memory[address + 3];
        number = ByteArrayUtil.fourByteToInt(a, b, c, d, ByteOrder.LITTLE_ENDIAN);
        System.out.print(number + " ");

        address -= 4;
        a = cpu.memory[address + 0];
        b = cpu.memory[address + 1];
        c = cpu.memory[address + 2];
        d = cpu.memory[address + 3];
        number = ByteArrayUtil.fourByteToInt(a, b, c, d, ByteOrder.LITTLE_ENDIAN);
        System.out.print(number + " ");

        address -= 4;
        a = cpu.memory[address + 0];
        b = cpu.memory[address + 1];
        c = cpu.memory[address + 2];
        d = cpu.memory[address + 3];
        number = ByteArrayUtil.fourByteToInt(a, b, c, d, ByteOrder.LITTLE_ENDIAN);
        System.out.println(number);

        System.out.println("done");
    }

    /**
     * Hightlights a row in the debugger window given it's index in the
     *
     * @param index
     */
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

    private static void preprocess(final String inputFile, final String outputFile)
            throws FileNotFoundException, IOException {

        System.out.println("Precprocessing input file ...");

        try (java.io.BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputFile))) {

            IncludePreprocessor includePreprocessor = new IncludePreprocessor();
            includePreprocessor.preprocess(inputFile, bufferedWriter);

            bufferedWriter.flush();
        }

        System.out.println("Precprocessing input done ...");
    }

    private void updateRegisterView() {
        textField_x0.setText("" + cpu.registerFile[RISCVRegister.REG_X0.getIndex()]);
        textField_x1.setText("" + ByteArrayUtil.byteToHex(cpu.registerFile[RISCVRegister.REG_X1.getIndex()]));
        textField_x2.setText("" + cpu.registerFile[RISCVRegister.REG_X2.getIndex()]);
        textField_x3.setText("" + cpu.registerFile[RISCVRegister.REG_X3.getIndex()]);
        textField_x4.setText("" + cpu.registerFile[RISCVRegister.REG_X4.getIndex()]);
        textField_x5.setText("" + cpu.registerFile[RISCVRegister.REG_X5.getIndex()]);
        textField_x6.setText("" + cpu.registerFile[RISCVRegister.REG_X6.getIndex()]);
        textField_x7.setText("" + cpu.registerFile[RISCVRegister.REG_X7.getIndex()]);
        textField_x8.setText("" + cpu.registerFile[RISCVRegister.REG_X8.getIndex()]);
        textField_x9.setText("" + cpu.registerFile[RISCVRegister.REG_X9.getIndex()]);

        textField_x10.setText("" + cpu.registerFile[RISCVRegister.REG_X10.getIndex()]);
        textField_x11.setText("" + cpu.registerFile[RISCVRegister.REG_X11.getIndex()]);
        textField_x12.setText("" + cpu.registerFile[RISCVRegister.REG_X12.getIndex()]);
        textField_x13.setText("" + cpu.registerFile[RISCVRegister.REG_X13.getIndex()]);
        textField_x14.setText("" + cpu.registerFile[RISCVRegister.REG_X14.getIndex()]);
        textField_x15.setText("" + cpu.registerFile[RISCVRegister.REG_X15.getIndex()]);
        textField_x16.setText("" + cpu.registerFile[RISCVRegister.REG_X16.getIndex()]);
        textField_x17.setText("" + cpu.registerFile[RISCVRegister.REG_X17.getIndex()]);
        textField_x18.setText("" + cpu.registerFile[RISCVRegister.REG_X18.getIndex()]);
        textField_x19.setText("" + cpu.registerFile[RISCVRegister.REG_X19.getIndex()]);

        textField_x20.setText("" + cpu.registerFile[RISCVRegister.REG_X20.getIndex()]);
        textField_x21.setText("" + cpu.registerFile[RISCVRegister.REG_X21.getIndex()]);
        textField_x22.setText("" + cpu.registerFile[RISCVRegister.REG_X22.getIndex()]);
        textField_x23.setText("" + cpu.registerFile[RISCVRegister.REG_X23.getIndex()]);
        textField_x24.setText("" + cpu.registerFile[RISCVRegister.REG_X24.getIndex()]);
        textField_x25.setText("" + cpu.registerFile[RISCVRegister.REG_X25.getIndex()]);
        textField_x26.setText("" + cpu.registerFile[RISCVRegister.REG_X26.getIndex()]);
        textField_x27.setText("" + cpu.registerFile[RISCVRegister.REG_X27.getIndex()]);
        textField_x28.setText("" + cpu.registerFile[RISCVRegister.REG_X28.getIndex()]);
        textField_x29.setText("" + cpu.registerFile[RISCVRegister.REG_X29.getIndex()]);

        textField_x30.setText("" + cpu.registerFile[RISCVRegister.REG_X30.getIndex()]);
        textField_x31.setText("" + cpu.registerFile[RISCVRegister.REG_X31.getIndex()]);
    }

}