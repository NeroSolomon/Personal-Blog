public class HelloWorld {
    /* 第一个Java程序
     * 它将输出字符串 Hello World
     */
    public static void main(String[] args) {
        System.out.println("Hello World"); // 输出 Hello World
    }
}

// tanmingyang@oioioi day1 % javac HelloWorld.java
// tanmingyang@oioioi day1 % java HelloWorld

// Java 不是脚本语言，不能直接"运行一个文件"。
// 传统方式：需要先 javac HelloWorld.java 编译，再 java HelloWorld 显式指定要运行的类。JVM 只执行你指定的那个类的 main 方法。
// Java 11+ 单文件模式：可以用 java HelloWorld.java 一步完成编译+运行，但仍然是你明确指定了文件，JVM 在该文件中找第一个带 main 的类来执行——不是自动扫描发现。
// 如果 .java 文件里有多个带 main 的类，JVM 不会全执行，只看你指定了哪一个。

// 如果有多个带main的类，又没有指定某一个
// Java 11+ 单文件模式（java XXX.java）：JVM 只执行文件中第一个带 main 的类，其余带 main 的类被忽略。
// 编译后的多文件（java ClassName）：你必须明确指定类名，否则直接报错。