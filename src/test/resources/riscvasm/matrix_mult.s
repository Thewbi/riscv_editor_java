; public Matrix mult(final Matrix matB) {
;     if (columns != matB.rows) {
;         throw new RuntimeException("Does not match!");
;     }
;     Matrix matC = new Matrix(rows, matB.columns);
;     // over row of matrix B
;     for (int i = 0; i < rows; i++) {
;         // over column of matrix A
;         for (int j = 0; j < matB.columns; j++) {
;             // fuse row and column together into a single cell of matrix C
;             for (int k = 0; k < columns; k++) {
;                 matC.data[i*rows + j] += data[i*columns + k] * matB.data[k*columns + j];
;             }
;         }
;     }
;     return matC;
; }    
    
    .global _start
    .section .text

_start:
    li a0, 3    # matrix dimensions

    li t0, 0    # i
rows_b:
    bge t0, a0, end_rows_b


    li t1, 0    # j
cols_a:
    bge t1, a0, end_cols_a

    # clear accumulation register
    li s10, 0

    li t2, 0    # k
fuse:
    bge t2, a0, end_fuse


    # i*columns + k
    mul t3, t0, a0          # mul, a0 == ROWS, t0 = i, t2 = k
    add t3, t3, t2          # add
    slli s11, t3, 2         # multiply by 4 for word size

    # offset by matrix ptr
    # la t4, matrix_a + t3  # pseudo instruction load address. Resolved: pc relative!
    la t4, matrix_a         # pseudo instruction load address. Resolved: pc relative!
    add t4, t4, s11         # advance pointer
    lw t5, (t4)             # load matrix A cell


    # k*columns + j
    mul s1, t2, a0          # mul, t2 = k
    add s1, s1, t1          # add, t1 = j
    slli s11, s1, 2         # multiply by 4 for word size

    # offset by matrix ptr
    la t4, matrix_b         # pseudo instruction load address. Resolved: pc relative!
    add t4, t4, s11         # advance pointer
    lw t6, (t4)             # load matrix B cell

    # perform multiplication of cells
    mul t3, t5, t6

    # accumulate
    add s10, s10, t3

    addi t2, t2, 1          # increment for loop counter
    j fuse                  # next for loop iteration
end_fuse:
    nop     # end of cols A

    # write result back to memory
    # i*rows + j
    mul t3, t0, a0
    add t3, t3, t1
    slli s11, t3, 2

    # offset by matrix ptr
    la t4, result_data      # pseudo instruction load address. Resolved: pc relative!
    add t4, t4, s11         # advance pointer
    sw s10, (t4)

    addi t1, t1, 1          # increment for loop counter (t1 = j)
    j cols_a                # next for loop iteration
end_cols_a:
    nop                     # end of cols A

    addi t0, t0, 1          # increment for loop counter (t0 = i)
    j rows_b                # next for loop iteration
end_rows_b:
    nop                     # end of rows B, end of matrix mult




    .section .data
result_data: .zero 36

matrix_a:   .word 0x00000001, 0x00000002, 0x00000003
            .word 0x00000004, 0x00000005, 0x00000006
            .word 0x00000007, 0x00000008, 0x00000009

matrix_b:   .word 0x00000001, 0x00000002, 0x00000003
            .word 0x00000004, 0x00000005, 0x00000006
            .word 0x00000007, 0x00000008, 0x00000009


# Sample:
#
# 1 2 3   1 2 3    30  36  42
# 4 5 6 x 4 5 6 =  66  81  96
# 7 8 9   7 8 9   102 126 150
#
# 7*1 + 4*8 + 9*7 = 102