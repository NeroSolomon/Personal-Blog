package main

import "fmt"

func main() {
	var a string = "runoob"
	fmt.Println(a)

	var b, c int = 1, 2
	fmt.Println(b, c)

	// 语法糖，自动推断类型
	// 这种不带声明格式的只能在函数体中出现
	f := "runoob"
	fmt.Println(f)

	var (
		g int
		h bool
	)
	fmt.Println(g, h)

	// 常量
	const j string = "runoob"
	const k = len(j)
	fmt.Println(j, k)
}
