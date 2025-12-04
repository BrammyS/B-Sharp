.bytecode 49.0
.class public HelloWorld
.super java/lang/Object
.method public <init>()V
.limit stack 1
.limit locals 1
aload_0
invokespecial java/lang/Object/<init>()V
return
.end method
.method public static main([Ljava/lang/String;)V
.limit stack 20
.limit locals 20
new HelloWorld
dup
invokespecial HelloWorld.<init>()V
astore_1
aload_1
invokevirtual HelloWorld.Main()V
return
.end method
.method public Main()V
.limit stack 20
.limit locals 20
getstatic java/lang/System/out Ljava/io/PrintStream;
ldc "Hello World!"
invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
return
.end method
