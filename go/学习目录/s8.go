package main

import "fmt"

func main() {
	// 切片
	var a = []int{1, 2, 3, 4, 5, 6}
	fmt.Println(a)
	fmt.Println(a[1:3])
	a = append(a, 7)

	b := make([]int, len(a), cap(a))
	copy(b, a)
	fmt.Println(b)
}
