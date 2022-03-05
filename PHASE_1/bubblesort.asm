.data
array: .word 11,2,34,5,0,22,4,78,90,809
.text
.globl main
main:
li x0,0             #arr[0]
li x1,0             #i = 0
li x2,0             #j = 0
li x3,10             #n = 10
li x4,10             #n-i for inner loop
li x9,0
addi x3,x3,-1
outerloop:
li x2,0
addi x4,x4,-1       #decreasing size for inner_loop
addi x7,x0,0        #resetting addr itr j
innerloop:
lw x5,0(x7)         #arr[j]
addi x7,x7,1        #addr itr j += 4
lw x6,0(x7)         #arr[j+1]
addi x2,x2,1        #j++
slt x8,x5,x6        #set x8 = 1 if x5 < x6
bne x8,x9,leave
swap:
sw x5,0(x7)
sw x6,-1(x7)
lw x6,0(x7)
leave:
bne x2,x4,innerloop     #j != n-i
addi x1,x1,1            #i++
bne x1,x3,outerloop     #i != n