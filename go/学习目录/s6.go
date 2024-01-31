package main

import "fmt"

func main() {
	var n [10]int
	var b = [5]float64{1, 2, 3, 4, 5}
	// 初始化其中一些元素
	var c = [5]int{1: 1, 3: 4}

	fmt.Println(n)
	fmt.Println(b)
	fmt.Println(c)
}
