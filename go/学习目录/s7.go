package main

import "fmt"

func main() {
	a := 10

	fmt.Println("变量地址：%x", &a)

	// 指针类型
	var ptr *int
	ptr = &a

	fmt.Println("指针变量存储的地址：%x", ptr)

	// 空指针
	var ptr2 *int
	fmt.Println("空指针：%x", ptr2)
	if ptr2 == nil {
		fmt.Println("空指针")
	}
}
