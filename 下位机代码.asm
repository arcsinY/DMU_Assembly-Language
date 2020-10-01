con8279 equ 0492h
dat8279 equ 0490h 
port0809 equ 04a0h
port8259 equ 04c0h
port8255 equ 04d0h
port8253 equ 04e0h
port0832 equ 04f0h
    code segment       
    assume cs:code
    org 0100h
start:    
jmp start1
segcod db 3fh,06h,5bh,4fh,66h,6dh,7dh,07h,7fh,6fh,77h,7ch,39h,5eh,79h,71h 
analog1 db 0
analog2 db 0
digital db 0 
choose db 0    
;-----------8250初始化---------    
start1:
    cli     ;关中断
    mov bx,0480h
    mov dx,bx
    add dx,6        ;通信线路控制器（LCR）高位置1，用于选择除数寄存器
    mov ax,80h
    out dx,ax
   
    mov dx,bx      ;设置波特率因子为12,波特率设置为9600bps 
    mov ax,0ch      
    out dx,ax       
    add dx,2
    mov ax,0h
    out dx,ax

    add dx,4            ;设置LCR
    mov ax,0fh           ;奇校验，8位数据，1位停止位
    out dx,ax

    mov dx,bx
    add dx,2        
    mov ax,0
    out dx,ax
;---------8253初始化-------
    mov dx,port8253+6 ;控制寄存器
    mov ax,36h ;计数器0，方式3
    out dx,ax       
    mov ax,76h   ;计数器1，方式3
    out dx,ax
;--------8259初始化----------
    mov dx,port8259
    mov ax,13h			;ICW1, ICW4 NEEDED
    out dx,ax

    add dx,2
    mov ax,80h 	 	 	;ICW2 中断类型80h
	out dx,ax

	mov ax,07H
	out dx,ax   			;ICW4

	mov ax,00h    		;OCW1, 开放所有中断
	out dx,ax 

;安装中断向量
	mov ax,0
	mov ds,ax			;中断向量表位于内存最开始的1KB，段地址为0

	mov si,200h  			;初始化中断向量表，80H*4=200H
	mov ax,offset oneCollect
	mov ds:[si],ax
	add si,2    
	mov ds:[si],seg ontCollect    	
	sti   ;开中断
;--------8255初始化----------
	mov dx,port8255+6        ;b口输入，a口输入,c口输出
	mov al,92h
	out dx,al        
	
	mov al,0ffh       ;LED清零
	mov dx,port8255+4
	out dx,al	      ;向c口输出
;---------开始工作----------
  
    call recStart        ;接受开始符号
    call recChoose 
randt:
    call collectData   ;收到开始符号，开始采集数据
    
    jmp randt
 
 
 
recStart proc
;----------接受开始符号子程序--------        
    push bx
    push dx
    push ax
    mov bx,0480h
    mov dx,bx
    add dx,0ah  

wait2r1:
    in al,dx
    test al,01h          ;判断有无数据
    jnz recvok1
    jmp wait2r1
recvok1:    ;接受数据
    mov dx,bx
    in al,dx
    cmp al,73h      ;判断是否为s
    jne wait2r1      ;不为s，再次等待接收数据 
    pop ax
    pop dx
    pop bx
    ret 
recStart endp   


recChoose proc  
	push ax
	push bx
	push dx
;----------接受选择----------
    mov bx,0480h
    mov dx,bx
    add dx,0ah 

wait2r2:
    in al,dx
    test al,01h          ;判断有无数据
    jnz recvok2
    jmp wait2r2 
recvok2:    ;接受数据  
    mov dx,bx
    in al,dx
    mov choose,al
      
	pop dx
	pop bx
	pop ax
    ret        
recChoose endp


send1p proc 
;--------发送digital和analog1子程序--------
    push ax
    push bx
    push dx
    
    mov al,digital      ;准备发送digital
    push ax               ;保护ax
    mov bx,0480h
    mov dx,bx
    add dx,0ah           ;读LSR
wait2t_1p:
    in al,dx
    test al,20h          ;可以发送数据
    jnz sendok_1p
    jmp wait2t_1p
sendok_1p:
    pop ax
    mov dx,bx           ;发送digital数据
    out dx,ax     
    
    mov al,analog1      ;准备发送analog1
    push ax 
    mov bx,0480h
    mov dx,bx
    add dx,0ah           ;读LSR 
wait2t_1p2:
    in al,dx
    test al,20h          ;可以发送数据
    jnz sendok_1p2
    jmp wait2t_1p2
sendok_1p2:
    pop ax
    mov dx,bx           ;发送
    out dx,ax 
    
    pop dx
    pop bx
    pop ax    
    ret     
send1p endp
      

send2p proc   
;--------发送digital和analog2子程序--------
    push ax
    push bx
    push dx
    
    mov al,digital      ;准备发送digital
    push ax               ;保护ax
    mov bx,0480h
    mov dx,bx
    add dx,0ah           ;读LSR
wait2t_2p:
    in al,dx
    test al,20h          ;可以发送数据
    jnz sendok_2p
    jmp wait2t_2p
sendok_2p:
    pop ax
    mov dx,bx           ;发送接受到的数据
    out dx,ax     
    
    mov al,analog2      ;准备发送analog2
    push ax 
    mov bx,0480h
    mov dx,bx
    add dx,0ah           ;读LSR 
wait2t_2p2:
    in al,dx
    test al,20h          ;可以发送数据
    jnz sendok_2p2
    jmp wait2t_2p2
sendok_2p2:
    pop ax
    mov dx,bx           ;发送
    out dx,ax 
    
    pop dx
    pop bx
    pop ax    
    ret        
send2p endp
   
   
collectData proc  
;------ 采集数据，0.5s产生一个中断 ------- 
    push dx
    push ax
    
;-----8253计数初值-------
    mov dx,port8253
    mov ax,7Ch
    out dx,ax
    mov ax,92h
    out dx,ax ;计数值927Ch(十进制37500)
    
    mov dx,port8253+6
    mov dx,04e2h     ;计数值10
    mov ax,0ah
    out dx,ax
    mov ax,0 
    out dx,ax   
waitInterrupt:
	
	jmp waitInterrupt
	    
    pop ax
    pop dx 
    ret 
collectData endp
     
     
oneCollect proc   
;------ 中断服务程序：一次采集数据，存储两路模拟量和一路数字量
;                     接受上位机发来的选择,发送数据 ------------- 
	sti
    push ax
    push bx
    push cx
    push dx   
    mov dx,port0809   ;启动0809通道0
    mov ax,0 
    out dx,ax
    
    mov dx,port8255
query1:
    in ax,dx
    test ax,2    ;检查a口的第二位
    jz query1       ;查询转换结果 
    
    mov dx,port0809
    in al,dx     ;读取转换结果
    mov analog1,al   ;存在内存中
    mov bl,al      ;bx用于显示8279
;-------在数码管上显示-----
    disp:	    
    mov di,offset segcod
	mov ax,08h			;工作方式，16位，左入
	mov dx,con8279
	out	dx,ax
	mov ax,90h			
	mov dx,con8279
	out	dx,ax			;写显示RAM命令，地址自增

	mov dx,dat8279
	push bx
	and	bl,0f0h          ;取bl的高4位
	mov cl,4            ;把bl的高四位放入bl的低四位中
	shr	bx,cl

	add	di,bx
	mov al,cs:[di]
	mov ah,0
	out	dx,ax			 ;写RAM0
	nop
	nop
	mov di,offset segcod

	pop	bx
	and	bl,0fh            ;取bl低4位
	add	di,bx
	mov al,cs:[di]
	mov ah,0
	out	dx,ax			  ;写RAM1
     
;----------------------
    mov dx,port8255
query2:          
    in ax,dx
    test ax,2
    jz query2       ;查询转换结果 
    
    mov dx,port0809
    in al,dx     ;读取转换结果  
    mov analog2,al   ;存在内存中
    
;------------读数字量--------------
    mov dx,port8255+2     ;读数字量
    in al,dx
    mov digital,al  
    cmp choose,0fbh  
    je  send1      ;选择模拟量1
    call send2p   ;否则调用send2p
    jmp sendOver
send1:
    call send1p 
sendOver:  
    call recAgain
               
    pop dx
    pop cx
    pop bx
    pop ax
    iret
oneCollect endp
   
recAgain proc
;------------ 接收到减半的模拟量DA转换，或者是结束命令，或者是报警命令，或者是选择路数 -------
    push bx
    push dx
    push ax 
    
    mov bx,0480h
    mov dx,bx
    add dx,0ah  
wait2r5:
    in al,dx
    test al,01h          ;判断有无数据
    jnz recvok5
    jmp wait2r5
recvok5:    ;接受数据
    mov dx,bx
    in al,dx   
    cmp al,0fbh     ;这是一种选择
    je chooseChange
    cmp al,0fch     ;这是一种选择
    je chooseChange
    cmp al,0feh          ;停机命令
    je stop  
    jmp half
chooseChange:
	mov choose, al
	jmp continue2
stop:
	hlt
half:
    mov dx,port8255+4    ;各种命令都不是，那就是减半后的模拟量了
    out dx,al            ;输出给8255 c口
continue2:   
   
    pop ax
    pop dx
    pop bx
    ret
recAgain endp

code  ends               
end   start        