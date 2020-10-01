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
;-----------8250��ʼ��---------    
start1:
    cli     ;���ж�
    mov bx,0480h
    mov dx,bx
    add dx,6        ;ͨ����·��������LCR����λ��1������ѡ������Ĵ���
    mov ax,80h
    out dx,ax
   
    mov dx,bx      ;���ò���������Ϊ12,����������Ϊ9600bps 
    mov ax,0ch      
    out dx,ax       
    add dx,2
    mov ax,0h
    out dx,ax

    add dx,4            ;����LCR
    mov ax,0fh           ;��У�飬8λ���ݣ�1λֹͣλ
    out dx,ax

    mov dx,bx
    add dx,2        
    mov ax,0
    out dx,ax
;---------8253��ʼ��-------
    mov dx,port8253+6 ;���ƼĴ���
    mov ax,36h ;������0����ʽ3
    out dx,ax       
    mov ax,76h   ;������1����ʽ3
    out dx,ax
;--------8259��ʼ��----------
    mov dx,port8259
    mov ax,13h			;ICW1, ICW4 NEEDED
    out dx,ax

    add dx,2
    mov ax,80h 	 	 	;ICW2 �ж�����80h
	out dx,ax

	mov ax,07H
	out dx,ax   			;ICW4

	mov ax,00h    		;OCW1, ���������ж�
	out dx,ax 

;��װ�ж�����
	mov ax,0
	mov ds,ax			;�ж�������λ���ڴ��ʼ��1KB���ε�ַΪ0

	mov si,200h  			;��ʼ���ж�������80H*4=200H
	mov ax,offset oneCollect
	mov ds:[si],ax
	add si,2    
	mov ds:[si],seg ontCollect    	
	sti   ;���ж�
;--------8255��ʼ��----------
	mov dx,port8255+6        ;b�����룬a������,c�����
	mov al,92h
	out dx,al        
	
	mov al,0ffh       ;LED����
	mov dx,port8255+4
	out dx,al	      ;��c�����
;---------��ʼ����----------
  
    call recStart        ;���ܿ�ʼ����
    call recChoose 
randt:
    call collectData   ;�յ���ʼ���ţ���ʼ�ɼ�����
    
    jmp randt
 
 
 
recStart proc
;----------���ܿ�ʼ�����ӳ���--------        
    push bx
    push dx
    push ax
    mov bx,0480h
    mov dx,bx
    add dx,0ah  

wait2r1:
    in al,dx
    test al,01h          ;�ж���������
    jnz recvok1
    jmp wait2r1
recvok1:    ;��������
    mov dx,bx
    in al,dx
    cmp al,73h      ;�ж��Ƿ�Ϊs
    jne wait2r1      ;��Ϊs���ٴεȴ��������� 
    pop ax
    pop dx
    pop bx
    ret 
recStart endp   


recChoose proc  
	push ax
	push bx
	push dx
;----------����ѡ��----------
    mov bx,0480h
    mov dx,bx
    add dx,0ah 

wait2r2:
    in al,dx
    test al,01h          ;�ж���������
    jnz recvok2
    jmp wait2r2 
recvok2:    ;��������  
    mov dx,bx
    in al,dx
    mov choose,al
      
	pop dx
	pop bx
	pop ax
    ret        
recChoose endp


send1p proc 
;--------����digital��analog1�ӳ���--------
    push ax
    push bx
    push dx
    
    mov al,digital      ;׼������digital
    push ax               ;����ax
    mov bx,0480h
    mov dx,bx
    add dx,0ah           ;��LSR
wait2t_1p:
    in al,dx
    test al,20h          ;���Է�������
    jnz sendok_1p
    jmp wait2t_1p
sendok_1p:
    pop ax
    mov dx,bx           ;����digital����
    out dx,ax     
    
    mov al,analog1      ;׼������analog1
    push ax 
    mov bx,0480h
    mov dx,bx
    add dx,0ah           ;��LSR 
wait2t_1p2:
    in al,dx
    test al,20h          ;���Է�������
    jnz sendok_1p2
    jmp wait2t_1p2
sendok_1p2:
    pop ax
    mov dx,bx           ;����
    out dx,ax 
    
    pop dx
    pop bx
    pop ax    
    ret     
send1p endp
      

send2p proc   
;--------����digital��analog2�ӳ���--------
    push ax
    push bx
    push dx
    
    mov al,digital      ;׼������digital
    push ax               ;����ax
    mov bx,0480h
    mov dx,bx
    add dx,0ah           ;��LSR
wait2t_2p:
    in al,dx
    test al,20h          ;���Է�������
    jnz sendok_2p
    jmp wait2t_2p
sendok_2p:
    pop ax
    mov dx,bx           ;���ͽ��ܵ�������
    out dx,ax     
    
    mov al,analog2      ;׼������analog2
    push ax 
    mov bx,0480h
    mov dx,bx
    add dx,0ah           ;��LSR 
wait2t_2p2:
    in al,dx
    test al,20h          ;���Է�������
    jnz sendok_2p2
    jmp wait2t_2p2
sendok_2p2:
    pop ax
    mov dx,bx           ;����
    out dx,ax 
    
    pop dx
    pop bx
    pop ax    
    ret        
send2p endp
   
   
collectData proc  
;------ �ɼ����ݣ�0.5s����һ���ж� ------- 
    push dx
    push ax
    
;-----8253������ֵ-------
    mov dx,port8253
    mov ax,7Ch
    out dx,ax
    mov ax,92h
    out dx,ax ;����ֵ927Ch(ʮ����37500)
    
    mov dx,port8253+6
    mov dx,04e2h     ;����ֵ10
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
;------ �жϷ������һ�βɼ����ݣ��洢��·ģ������һ·������
;                     ������λ��������ѡ��,�������� ------------- 
	sti
    push ax
    push bx
    push cx
    push dx   
    mov dx,port0809   ;����0809ͨ��0
    mov ax,0 
    out dx,ax
    
    mov dx,port8255
query1:
    in ax,dx
    test ax,2    ;���a�ڵĵڶ�λ
    jz query1       ;��ѯת����� 
    
    mov dx,port0809
    in al,dx     ;��ȡת�����
    mov analog1,al   ;�����ڴ���
    mov bl,al      ;bx������ʾ8279
;-------�����������ʾ-----
    disp:	    
    mov di,offset segcod
	mov ax,08h			;������ʽ��16λ������
	mov dx,con8279
	out	dx,ax
	mov ax,90h			
	mov dx,con8279
	out	dx,ax			;д��ʾRAM�����ַ����

	mov dx,dat8279
	push bx
	and	bl,0f0h          ;ȡbl�ĸ�4λ
	mov cl,4            ;��bl�ĸ���λ����bl�ĵ���λ��
	shr	bx,cl

	add	di,bx
	mov al,cs:[di]
	mov ah,0
	out	dx,ax			 ;дRAM0
	nop
	nop
	mov di,offset segcod

	pop	bx
	and	bl,0fh            ;ȡbl��4λ
	add	di,bx
	mov al,cs:[di]
	mov ah,0
	out	dx,ax			  ;дRAM1
     
;----------------------
    mov dx,port8255
query2:          
    in ax,dx
    test ax,2
    jz query2       ;��ѯת����� 
    
    mov dx,port0809
    in al,dx     ;��ȡת�����  
    mov analog2,al   ;�����ڴ���
    
;------------��������--------------
    mov dx,port8255+2     ;��������
    in al,dx
    mov digital,al  
    cmp choose,0fbh  
    je  send1      ;ѡ��ģ����1
    call send2p   ;�������send2p
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
;------------ ���յ������ģ����DAת���������ǽ�����������Ǳ������������ѡ��·�� -------
    push bx
    push dx
    push ax 
    
    mov bx,0480h
    mov dx,bx
    add dx,0ah  
wait2r5:
    in al,dx
    test al,01h          ;�ж���������
    jnz recvok5
    jmp wait2r5
recvok5:    ;��������
    mov dx,bx
    in al,dx   
    cmp al,0fbh     ;����һ��ѡ��
    je chooseChange
    cmp al,0fch     ;����һ��ѡ��
    je chooseChange
    cmp al,0feh          ;ͣ������
    je stop  
    jmp half
chooseChange:
	mov choose, al
	jmp continue2
stop:
	hlt
half:
    mov dx,port8255+4    ;����������ǣ��Ǿ��Ǽ�����ģ������
    out dx,al            ;�����8255 c��
continue2:   
   
    pop ax
    pop dx
    pop bx
    ret
recAgain endp

code  ends               
end   start        