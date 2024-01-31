package main

import "fmt"

func main() {
	a := 10
	b := 20

	var c int

	c = max(a, b)

	fmt.Println(c)

	j, k := swap("hello", "world")
	fmt.Println(j, k)
}

func max(num1, num2 int) int {
	var r int
	if num1 > num2 {
		r = num1
	} else {
		r = num2
	}

	return r
}

func swap(x, y string) (string, string) {
	return y, x
}
